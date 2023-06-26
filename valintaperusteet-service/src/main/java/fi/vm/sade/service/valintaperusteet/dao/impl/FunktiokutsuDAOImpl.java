package fi.vm.sade.service.valintaperusteet.dao.impl;

import com.google.common.collect.Sets;
import com.querydsl.jpa.impl.JPADeleteClause;
import com.querydsl.jpa.impl.JPAQueryFactory;
import fi.vm.sade.service.valintaperusteet.dao.AbstractJpaDAOImpl;
import fi.vm.sade.service.valintaperusteet.dao.FunktiokutsuDAO;
import fi.vm.sade.service.valintaperusteet.model.Arvokonvertteriparametri;
import fi.vm.sade.service.valintaperusteet.model.Arvovalikonvertteriparametri;
import fi.vm.sade.service.valintaperusteet.model.Funktioargumentti;
import fi.vm.sade.service.valintaperusteet.model.Funktiokutsu;
import fi.vm.sade.service.valintaperusteet.model.QArvokonvertteriparametri;
import fi.vm.sade.service.valintaperusteet.model.QArvovalikonvertteriparametri;
import fi.vm.sade.service.valintaperusteet.model.QFunktioargumentti;
import fi.vm.sade.service.valintaperusteet.model.QFunktiokutsu;
import fi.vm.sade.service.valintaperusteet.model.QHakukohdeViite;
import fi.vm.sade.service.valintaperusteet.model.QJarjestyskriteeri;
import fi.vm.sade.service.valintaperusteet.model.QLaskentakaava;
import fi.vm.sade.service.valintaperusteet.model.QSyotettavanarvontyyppi;
import fi.vm.sade.service.valintaperusteet.model.QTekstiRyhma;
import fi.vm.sade.service.valintaperusteet.model.QValinnanVaihe;
import fi.vm.sade.service.valintaperusteet.model.QValintaperusteViite;
import fi.vm.sade.service.valintaperusteet.model.QValintatapajono;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class FunktiokutsuDAOImpl extends AbstractJpaDAOImpl<Funktiokutsu, Long>
    implements FunktiokutsuDAO {
  @Override
  public Funktiokutsu getFunktiokutsu(Long id) {
    QFunktiokutsu fk = QFunktiokutsu.funktiokutsu;
    QFunktioargumentti fa = QFunktioargumentti.funktioargumentti;
    QArvokonvertteriparametri ak = QArvokonvertteriparametri.arvokonvertteriparametri;
    QArvovalikonvertteriparametri avk = QArvovalikonvertteriparametri.arvovalikonvertteriparametri;
    QValintaperusteViite vpv = QValintaperusteViite.valintaperusteViite;
    QTekstiRyhma t = QTekstiRyhma.tekstiRyhma;
    QSyotettavanarvontyyppi sak = QSyotettavanarvontyyppi.syotettavanarvontyyppi;

    Funktiokutsu kutsu =
        queryFactory()
            .selectFrom(fk)
            .leftJoin(fk.syoteparametrit)
            .fetchJoin()
            .leftJoin(fk.funktioargumentit, fa)
            .fetchJoin()
            .leftJoin(fa.laskentakaavaChild)
            .fetchJoin()
            .leftJoin(fk.valintaperusteviitteet, vpv)
            .fetchJoin()
            .leftJoin(vpv.syotettavanarvontyyppi, sak)
            .fetchJoin()
            .leftJoin(vpv.kuvaukset, t)
            .fetchJoin()
            .leftJoin(t.tekstit)
            .fetchJoin()
            .where(fk.id.eq(id))
            .fetchFirst();

    if (kutsu != null) {
      List<Arvokonvertteriparametri> arvokonvertteriparametris =
          queryFactory()
              .selectFrom(ak)
              .leftJoin(ak.kuvaukset, t)
              .fetchJoin()
              .leftJoin(t.tekstit)
              .fetchJoin()
              .where(ak.funktiokutsu.eq(kutsu))
              .distinct()
              .fetch();
      List<Arvovalikonvertteriparametri> arvovalikonvertteriparametris =
          queryFactory()
              .selectFrom(avk)
              .leftJoin(avk.kuvaukset, t)
              .fetchJoin()
              .leftJoin(t.tekstit)
              .fetchJoin()
              .where(avk.funktiokutsu.eq(kutsu))
              .distinct()
              .fetch();
      kutsu.setArvokonvertteriparametrit(Sets.newHashSet(arvokonvertteriparametris));
      kutsu.setArvovalikonvertteriparametrit(Sets.newHashSet(arvovalikonvertteriparametris));
    }
    return kutsu;
  }

  @Override
  public Funktiokutsu getFunktiokutsunValintaperusteet(Long id) {
    QFunktiokutsu fk = QFunktiokutsu.funktiokutsu;

    return queryFactory()
        .selectFrom(fk)
        .leftJoin(fk.arvokonvertteriparametrit)
        .fetchJoin()
        .leftJoin(fk.arvovalikonvertteriparametrit)
        .fetchJoin()
        .leftJoin(fk.funktioargumentit)
        .fetchJoin()
        .leftJoin(fk.valintaperusteviitteet)
        .fetchJoin()
        .where(fk.id.eq(id))
        .distinct()
        .fetchFirst();
  }

  @Override
  public List<Funktiokutsu> findFunktiokutsuByHakukohdeOid(String hakukohdeOid) {
    QHakukohdeViite hakukohde = QHakukohdeViite.hakukohdeViite;
    QValinnanVaihe vaihe = QValinnanVaihe.valinnanVaihe;
    QValintatapajono jono = QValintatapajono.valintatapajono;
    QJarjestyskriteeri kriteeri = QJarjestyskriteeri.jarjestyskriteeri;
    QLaskentakaava kaava = QLaskentakaava.laskentakaava;
    QFunktiokutsu funktiokutsu = QFunktiokutsu.funktiokutsu;

    return queryFactory()
        .select(funktiokutsu)
        .from(hakukohde)
        .innerJoin(hakukohde.valinnanvaiheet, vaihe)
        .innerJoin(vaihe.jonot, jono)
        .innerJoin(jono.jarjestyskriteerit, kriteeri)
        .innerJoin(kriteeri.laskentakaava, kaava)
        .innerJoin(kaava.funktiokutsu, funktiokutsu)
        .leftJoin(funktiokutsu.arvokonvertteriparametrit)
        .fetchJoin()
        .leftJoin(funktiokutsu.arvovalikonvertteriparametrit)
        .fetchJoin()
        .leftJoin(funktiokutsu.funktioargumentit)
        .fetchJoin()
        .leftJoin(funktiokutsu.syoteparametrit)
        .fetchJoin()
        .leftJoin(funktiokutsu.valintaperusteviitteet)
        .fetchJoin()
        .where(
            hakukohde
                .oid
                .eq(hakukohdeOid)
                .and(vaihe.aktiivinen.isTrue())
                .and(jono.aktiivinen.isTrue())
                .and(kriteeri.aktiivinen.isTrue()))
        .distinct()
        .fetch();
  }

  @Override
  public Map<String, List<Funktiokutsu>> findFunktiokutsuByHakukohdeOids(
      List<String> hakukohdeOidit) {
    QHakukohdeViite hakukohde = QHakukohdeViite.hakukohdeViite;
    QValinnanVaihe vaihe = QValinnanVaihe.valinnanVaihe;
    QValintatapajono jono = QValintatapajono.valintatapajono;
    QJarjestyskriteeri kriteeri = QJarjestyskriteeri.jarjestyskriteeri;
    QLaskentakaava kaava = QLaskentakaava.laskentakaava;
    QFunktiokutsu funktiokutsu = QFunktiokutsu.funktiokutsu;

    Map<String, List<Funktiokutsu>> result = new HashMap<>();
    queryFactory()
        .select(hakukohde.oid, funktiokutsu)
        .from(hakukohde)
        .innerJoin(hakukohde.valinnanvaiheet, vaihe)
        .innerJoin(vaihe.jonot, jono)
        .innerJoin(jono.jarjestyskriteerit, kriteeri)
        .innerJoin(kriteeri.laskentakaava, kaava)
        .innerJoin(kaava.funktiokutsu, funktiokutsu)
        .leftJoin(funktiokutsu.arvokonvertteriparametrit)
        .fetchJoin()
        .leftJoin(funktiokutsu.arvovalikonvertteriparametrit)
        .fetchJoin()
        .leftJoin(funktiokutsu.funktioargumentit)
        .fetchJoin()
        .leftJoin(funktiokutsu.syoteparametrit)
        .fetchJoin()
        .leftJoin(funktiokutsu.valintaperusteviitteet)
        .fetchJoin()
        .where(
            hakukohde
                .oid
                .in(hakukohdeOidit)
                .and(vaihe.aktiivinen.isTrue())
                .and(jono.aktiivinen.isTrue())
                .and(kriteeri.aktiivinen.isTrue()))
        .distinct()
        .fetch()
        .forEach(
            r -> {
              String rHakukohdeOid = r.get(0, String.class);
              Funktiokutsu rFunktiokutsu = r.get(1, Funktiokutsu.class);
              if (!result.containsKey(rHakukohdeOid)) {
                result.put(rHakukohdeOid, new ArrayList<>());
              }
              result.get(rHakukohdeOid).add(rFunktiokutsu);
            });
    return result;
  }

  @Override
  public List<Funktioargumentti> findByLaskentakaavaChild(Long laskentakaavaId) {
    QFunktioargumentti fa = QFunktioargumentti.funktioargumentti;
    return queryFactory()
        .selectFrom(fa)
        .leftJoin(fa.laskentakaavaChild)
        .where(fa.laskentakaavaChild.id.eq(laskentakaavaId))
        .fetch();
  }

  @Override
  public void flush() {
    getEntityManager().flush();
  }

  @Override
  @Transactional
  public long deleteOrphans() {
    QFunktiokutsu funktiokutsu = QFunktiokutsu.funktiokutsu;
    QLaskentakaava laskentakaava = QLaskentakaava.laskentakaava;
    QFunktioargumentti funktioargumentti = QFunktioargumentti.funktioargumentti;
    JPADeleteClause deleteClause =
        new JPADeleteClause(getEntityManager(), funktiokutsu)
            .where(
                queryFactory()
                    .selectFrom(laskentakaava)
                    .where(laskentakaava.funktiokutsu.id.eq(funktiokutsu.id))
                    .notExists(),
                queryFactory()
                    .selectFrom(funktioargumentti)
                    .where(funktioargumentti.funktiokutsuChild.id.eq(funktiokutsu.id))
                    .notExists());
    long poistettiin = 0;
    for (long i = deleteClause.execute(); i > 0; i = deleteClause.execute()) {
      poistettiin += i;
    }
    return poistettiin;
  }

  protected JPAQueryFactory queryFactory() {
    return new JPAQueryFactory(getEntityManager());
  }
}
