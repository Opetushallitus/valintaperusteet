package fi.vm.sade.service.valintaperusteet.dao.impl;

import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.jpa.impl.JPASubQuery;
import com.mysema.query.types.EntityPath;
import fi.vm.sade.generic.dao.AbstractJpaDAOImpl;
import fi.vm.sade.generic.dao.GenericDAO;
import fi.vm.sade.service.valintaperusteet.dao.FunktiokutsuDAO;
import fi.vm.sade.service.valintaperusteet.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: tommiha Date: 1/14/13 Time: 4:07 PM
 */
@Repository
public class FunktiokutsuDAOImpl extends AbstractJpaDAOImpl<Funktiokutsu, Long> implements FunktiokutsuDAO {

    @Autowired
    private GenericDAO genericDAO;

    @Override
    public Funktiokutsu getFunktiokutsu(Long id) {
        QFunktiokutsu fk = QFunktiokutsu.funktiokutsu;
        QFunktioargumentti fa = QFunktioargumentti.funktioargumentti;

        return from(fk)
                .leftJoin(fk.syoteparametrit).fetch()
                .leftJoin(fk.arvokonvertteriparametrit).fetch()
                .leftJoin(fk.arvovalikonvertteriparametrit).fetch()
                .leftJoin(fk.funktioargumentit, fa).fetch()
                .leftJoin(fa.laskentakaavaChild).fetch()
                .leftJoin(fk.valintaperuste).fetch()
                .where(fk.id.eq(id)).distinct().singleResult(fk);
    }

    @Override
    public List<Funktiokutsu> getOrphans() {
        QFunktiokutsu fk = QFunktiokutsu.funktiokutsu;
        QLaskentakaava lk = QLaskentakaava.laskentakaava;
        QFunktioargumentti arg = QFunktioargumentti.funktioargumentti;

        List<Funktiokutsu> orphans = from(fk)
                .leftJoin(fk.syoteparametrit)
                .fetch()
                .leftJoin(fk.arvokonvertteriparametrit)
                .fetch()
                .leftJoin(fk.arvovalikonvertteriparametrit)
                .fetch()
                .leftJoin(fk.funktioargumentit)
                .fetch()
                .leftJoin(fk.valintaperuste)
                .fetch()
                .where(
                        fk.id.notIn(
                                subQuery().from(lk)
                                        .leftJoin(lk.funktiokutsu)
                                        .distinct()
                                        .list(lk.funktiokutsu.id)
                        )
                                .and(
                                        fk.id.notIn(
                                                subQuery().from(arg)
                                                        .leftJoin(arg.funktiokutsuChild)
                                                        .distinct()
                                                        .list(arg.funktiokutsuChild.id)
                                        )
                                )
                )
                .distinct().list(fk);
        return orphans;
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
        if (isReferenced(funktiokutsu.getId())) {
            return;
        }

        Set<Funktiokutsu> children = new HashSet<Funktiokutsu>();

        for (Funktioargumentti arg : funktiokutsu.getFunktioargumentit()) {
            children.add(getFunktiokutsu(arg.getFunktiokutsuChild().getId()));
            genericDAO.remove(arg);
        }
        remove(funktiokutsu);
        for (Funktiokutsu child : children) {
            deleteRecursively(child);
        }
    }

    @Override
    public List<Funktiokutsu> findFunktiokutsuByHakukohdeOids(List<String> hakukohdeOids) {
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
                .leftJoin(funktiokutsu.valintaperuste).fetch()
                .where(hakukohde.oid.in(hakukohdeOids))
                .distinct()
                .list(funktiokutsu);
    }

    @Override
    public void deleteOrphans() {
        for (Funktiokutsu orphan : getOrphans()) {
            deleteRecursively(orphan);
        }
    }

    protected JPAQuery from(EntityPath<?>... o) {
        return new JPAQuery(getEntityManager()).from(o);
    }

    protected JPASubQuery subQuery() {
        return new JPASubQuery();
    }

}
