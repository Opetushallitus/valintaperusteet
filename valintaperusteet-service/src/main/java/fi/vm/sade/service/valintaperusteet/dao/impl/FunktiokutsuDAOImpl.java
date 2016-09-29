package fi.vm.sade.service.valintaperusteet.dao.impl;

import com.google.common.collect.Sets;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.jpa.impl.JPASubQuery;
import com.mysema.query.types.EntityPath;

import fi.vm.sade.service.valintaperusteet.dao.AbstractJpaDAOImpl;
import fi.vm.sade.service.valintaperusteet.dao.FunktiokutsuDAO;
import fi.vm.sade.service.valintaperusteet.dao.GenericDAO;
import fi.vm.sade.service.valintaperusteet.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Repository
public class FunktiokutsuDAOImpl extends AbstractJpaDAOImpl<Funktiokutsu, Long> implements FunktiokutsuDAO {
    @Autowired
    private GenericDAO genericDAO;

    @Override
    public Funktiokutsu getFunktiokutsu(Long id) {
        QFunktiokutsu fk = QFunktiokutsu.funktiokutsu;
        QFunktioargumentti fa = QFunktioargumentti.funktioargumentti;
        QArvokonvertteriparametri ak = QArvokonvertteriparametri.arvokonvertteriparametri;
        QArvovalikonvertteriparametri avk = QArvovalikonvertteriparametri.arvovalikonvertteriparametri;
        QValintaperusteViite vpv = QValintaperusteViite.valintaperusteViite;
        QTekstiRyhma t = QTekstiRyhma.tekstiRyhma;
        QSyotettavanarvontyyppi sak = QSyotettavanarvontyyppi.syotettavanarvontyyppi;

        JPAQuery query = from(fk);
        Funktiokutsu kutsu = query
                .leftJoin(fk.syoteparametrit).fetch()
                .leftJoin(fk.funktioargumentit, fa).fetch()
                .leftJoin(fa.laskentakaavaChild).fetch()
                .leftJoin(fk.valintaperusteviitteet, vpv).fetch()
                .leftJoin(vpv.syotettavanarvontyyppi, sak).fetch()
                .leftJoin(vpv.kuvaukset, t).fetch()
                .leftJoin(t.tekstit).fetch()
                .where(fk.id.eq(id)).singleResult(fk);

        if (kutsu != null) {
            List<Arvokonvertteriparametri> arvokonvertteriparametris = from(ak).leftJoin(ak.kuvaukset, t).fetch()
                    .leftJoin(t.tekstit).fetch()
                    .where(ak.funktiokutsu.eq(kutsu)).distinct().list(ak);
            List<Arvovalikonvertteriparametri> arvovalikonvertteriparametris = from(avk).leftJoin(avk.kuvaukset, t).fetch()
                    .leftJoin(t.tekstit).fetch()
                    .where(avk.funktiokutsu.eq(kutsu)).distinct().list(avk);
            kutsu.setArvokonvertteriparametrit(Sets.newHashSet(arvokonvertteriparametris));
            kutsu.setArvovalikonvertteriparametrit(Sets.newHashSet(arvovalikonvertteriparametris));
        }
        return kutsu;
    }

    @Override
    public Funktiokutsu getFunktiokutsunValintaperusteet(Long id) {
        QFunktiokutsu fk = QFunktiokutsu.funktiokutsu;

        return from(fk)
                .leftJoin(fk.arvokonvertteriparametrit).fetch()
                .leftJoin(fk.arvovalikonvertteriparametrit).fetch()
                .leftJoin(fk.funktioargumentit).fetch()
                .leftJoin(fk.valintaperusteviitteet).fetch()
                .where(fk.id.eq(id)).distinct().singleResult(fk);
    }

    @Override
    public List<Long> getOrphans() {
        QFunktiokutsu fk = QFunktiokutsu.funktiokutsu;
        QLaskentakaava lk = QLaskentakaava.laskentakaava;
        QFunktioargumentti arg = QFunktioargumentti.funktioargumentti;

        List<Long> laskentaakaavat = from(lk)
                //.leftJoin(lk.funktiokutsu)
                .distinct()
                .list(lk.funktiokutsu.id);

        List<Long> argumentit = from(arg)
                .distinct()
                .list(arg.funktiokutsuChild.id);

        List<Long> orphans = from(fk).distinct().list(fk.id);
        return orphans.stream().filter(f -> !laskentaakaavat.contains(f) && !argumentit.contains(f)).collect(Collectors.toList());
    }

    private boolean isReferenced(Long id) {
        QFunktiokutsu fk = QFunktiokutsu.funktiokutsu;
        QLaskentakaava lk = QLaskentakaava.laskentakaava;
        QFunktioargumentti arg = QFunktioargumentti.funktioargumentti;

        return from(fk).where(
                fk.id.eq(id).and(
                        fk.id.in(subQuery().from(arg).leftJoin(arg.funktiokutsuChild).distinct().list(arg.funktiokutsuChild.id)).or(
                                fk.id.in(subQuery().from(lk).leftJoin(lk.funktiokutsu).distinct()
                                        .list(lk.funktiokutsu.id))))).exists();
    }

    public void deleteRecursively(Funktiokutsu funktiokutsu) {
        Set<Funktiokutsu> children = new HashSet<Funktiokutsu>();

        for (Funktioargumentti arg : funktiokutsu.getFunktioargumentit().stream().filter(a -> a.getFunktiokutsuChild() != null).collect(Collectors.toList())) {
            children.add(getFunktiokutsu(arg.getFunktiokutsuChild().getId()));
        }
        remove(funktiokutsu);
        for (Funktiokutsu child : children) {
            deleteRecursively(child);
        }
    }

    @Override
    public List<Funktiokutsu> findFunktiokutsuByHakukohdeOids(String hakukohdeOid) {
        QHakukohdeViite hakukohde = QHakukohdeViite.hakukohdeViite;
        QValinnanVaihe vaihe = QValinnanVaihe.valinnanVaihe;
        QValintatapajono jono = QValintatapajono.valintatapajono;
        QJarjestyskriteeri kriteeri = QJarjestyskriteeri.jarjestyskriteeri;
        QLaskentakaava kaava = QLaskentakaava.laskentakaava;
        QFunktiokutsu funktiokutsu = QFunktiokutsu.funktiokutsu;

        return from(hakukohde)
                .innerJoin(hakukohde.valinnanvaiheet, vaihe)
                .innerJoin(vaihe.jonot, jono)
                .innerJoin(jono.jarjestyskriteerit, kriteeri)
                .innerJoin(kriteeri.laskentakaava, kaava)
                .innerJoin(kaava.funktiokutsu, funktiokutsu)
                .leftJoin(funktiokutsu.arvokonvertteriparametrit).fetch()
                .leftJoin(funktiokutsu.arvovalikonvertteriparametrit).fetch()
                .leftJoin(funktiokutsu.funktioargumentit).fetch()
                .leftJoin(funktiokutsu.syoteparametrit).fetch()
                .leftJoin(funktiokutsu.valintaperusteviitteet).fetch()
                .where(hakukohde.oid.eq(hakukohdeOid)
                        .and(vaihe.aktiivinen.isTrue())
                        .and(jono.aktiivinen.isTrue())
                        .and(kriteeri.aktiivinen.isTrue()))
                .distinct()
                .list(funktiokutsu);
    }

    @Override
    public List<Funktioargumentti> findByLaskentakaavaChild(Long laskentakaavaId) {
        QFunktioargumentti fa = QFunktioargumentti.funktioargumentti;
        return from(fa).leftJoin(fa.laskentakaavaChild).where(fa.laskentakaavaChild.id.eq(laskentakaavaId)).list(fa);
    }

    @Override
    public void flush() {
        getEntityManager().flush();
    }

    @Override
    public void deleteOrphans() {
        List<Long> orphans = getOrphans();
        for (Long orphan : orphans) {
            Funktiokutsu funktiokutsu = read(orphan);
            remove(funktiokutsu);
        }
        if (orphans.size() > 0) {
            deleteOrphans();
        }
    }

    @Override
    public void deleteOrphan(Long id) {
        Funktiokutsu funktiokutsu = read(id);
        remove(funktiokutsu);
    }

    protected JPAQuery from(EntityPath<?>... o) {
        return new JPAQuery(getEntityManager()).from(o);
    }

    protected JPASubQuery subQuery() {
        return new JPASubQuery();
    }

}
