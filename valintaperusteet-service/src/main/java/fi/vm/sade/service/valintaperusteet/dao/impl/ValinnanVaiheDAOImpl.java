package fi.vm.sade.service.valintaperusteet.dao.impl;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import fi.vm.sade.service.valintaperusteet.dao.AbstractJpaDAOImpl;
import fi.vm.sade.service.valintaperusteet.dao.ValinnanVaiheDAO;
import fi.vm.sade.service.valintaperusteet.model.*;
import fi.vm.sade.service.valintaperusteet.util.LinkitettavaJaKopioitavaUtil;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Repository;

@Repository
public class ValinnanVaiheDAOImpl extends AbstractJpaDAOImpl<ValinnanVaihe, Long>
    implements ValinnanVaiheDAO {

  protected JPAQueryFactory queryFactory() {
    return new JPAQueryFactory(getEntityManager());
  }

  @Override
  public ValinnanVaihe readByOid(String oid) {
    QValinnanVaihe valinnanVaihe = QValinnanVaihe.valinnanVaihe;
    QValintatapajono valintatapaJono = QValintatapajono.valintatapajono;
    return queryFactory()
        .selectFrom(valinnanVaihe)
        .leftJoin(valinnanVaihe.jonot, valintatapaJono)
        .fetchJoin()
        .leftJoin(valinnanVaihe.masterValinnanVaihe)
        .fetchJoin()
        .leftJoin(valinnanVaihe.valintaryhma)
        .fetchJoin()
        .leftJoin(valinnanVaihe.hakukohdeViite)
        .fetchJoin()
        .where(valinnanVaihe.oid.eq(oid))
        .fetchFirst();
  }

  @Override
  public List<ValinnanVaihe> haeKopiot(String oid) {
    QValinnanVaihe valinnanVaihe = QValinnanVaihe.valinnanVaihe;
    QValintatapajono valintatapaJono = QValintatapajono.valintatapajono;
    return queryFactory()
        .selectFrom(valinnanVaihe)
        .leftJoin(valinnanVaihe.jonot, valintatapaJono)
        .fetchJoin()
        .leftJoin(valinnanVaihe.masterValinnanVaihe)
        .fetchJoin()
        .leftJoin(valinnanVaihe.valintaryhma)
        .fetchJoin()
        .leftJoin(valinnanVaihe.hakukohdeViite)
        .fetchJoin()
        .where(valinnanVaihe.masterValinnanVaihe.oid.eq(oid))
        .fetch();
  }

  @Override
  public List<ValinnanVaihe> readByOids(Set<String> oids) {
    QValinnanVaihe valinnanVaihe = QValinnanVaihe.valinnanVaihe;
    QValintatapajono valintatapaJono = QValintatapajono.valintatapajono;
    return queryFactory()
        .selectFrom(valinnanVaihe)
        .leftJoin(valinnanVaihe.jonot, valintatapaJono)
        .fetchJoin()
        .where(valinnanVaihe.oid.in(oids))
        .fetch();
  }

  @Override
  public ValinnanVaihe haeHakukohteenViimeinenValinnanVaihe(String hakukohdeOid) {
    QHakukohdeViite hakukohde = QHakukohdeViite.hakukohdeViite;
    QValinnanVaihe vv = QValinnanVaihe.valinnanVaihe;
    QValintatapajono valintatapaJono = QValintatapajono.valintatapajono;
    return queryFactory()
        .select(vv)
        .from(hakukohde)
        .leftJoin(hakukohde.valinnanvaiheet, vv)
        .leftJoin(vv.jonot, valintatapaJono)
        .fetchJoin()
        .where(
            vv.id
                .notIn(
                    JPAExpressions.select(vv.edellinenValinnanVaihe.id)
                        .from(vv)
                        .where(vv.edellinenValinnanVaihe.isNotNull()))
                .and(hakukohde.oid.eq(hakukohdeOid)))
        .fetchFirst();
  }

  @Override
  public ValinnanVaihe haeValintaryhmanViimeinenValinnanVaihe(String oid) {
    QValintaryhma valintaryhma = QValintaryhma.valintaryhma;
    QValinnanVaihe vv = QValinnanVaihe.valinnanVaihe;
    QValintatapajono valintatapaJono = QValintatapajono.valintatapajono;
    return queryFactory()
        .select(vv)
        .from(valintaryhma)
        .leftJoin(valintaryhma.valinnanvaiheet, vv)
        .leftJoin(vv.jonot, valintatapaJono)
        .fetchJoin()
        .where(
            vv.id
                .notIn(
                    JPAExpressions.select(vv.edellinenValinnanVaihe.id)
                        .from(vv)
                        .where(vv.edellinenValinnanVaihe.isNotNull()))
                .and(valintaryhma.oid.eq(oid)))
        .fetchFirst();
  }

  @Override
  public List<ValinnanVaihe> findByValintaryhma(String oid) {
    QValintaryhma valintaryhma = QValintaryhma.valintaryhma;
    QValinnanVaihe vv = QValinnanVaihe.valinnanVaihe;
    QValintatapajono valintatapaJono = QValintatapajono.valintatapajono;
    return LinkitettavaJaKopioitavaUtil.jarjesta(
        queryFactory()
            .selectFrom(vv)
            .join(vv.valintaryhma, valintaryhma)
            .fetchJoin()
            .leftJoin(vv.jonot, valintatapaJono)
            .fetchJoin()
            .leftJoin(vv.edellinenValinnanVaihe)
            .fetchJoin()
            .leftJoin(vv.masterValinnanVaihe)
            .fetchJoin()
            .where(valintaryhma.oid.eq(oid))
            .distinct()
            .fetch());
  }

  @Override
  public List<ValinnanVaihe> findByHakukohde(String oid) {
    QHakukohdeViite hakukohde = QHakukohdeViite.hakukohdeViite;
    QValinnanVaihe vv = QValinnanVaihe.valinnanVaihe;
    QValintatapajono valintatapaJono = QValintatapajono.valintatapajono;
    return LinkitettavaJaKopioitavaUtil.jarjesta(
        queryFactory()
            .selectFrom(vv)
            .join(vv.hakukohdeViite, hakukohde)
            .fetchJoin()
            .leftJoin(vv.jonot, valintatapaJono)
            .fetchJoin()
            .leftJoin(vv.edellinenValinnanVaihe)
            .fetchJoin()
            .leftJoin(vv.masterValinnanVaihe)
            .fetchJoin()
            .where(hakukohde.oid.eq(oid))
            .distinct()
            .fetch());
  }

  @Override
  public boolean kuuluuSijoitteluun(String oid) {
    QValinnanVaihe vv = QValinnanVaihe.valinnanVaihe;
    QValintatapajono jono = QValintatapajono.valintatapajono;
    Long count =
        queryFactory()
            .select(jono.id.count())
            .from(jono)
            .leftJoin(jono.valinnanVaihe, vv)
            .where(
                vv.oid.eq(oid),
                vv.aktiivinen.eq(true),
                jono.aktiivinen.eq(true),
                jono.siirretaanSijoitteluun.eq(true))
            .fetchFirst();
    if (count == null) {
      return false;
    }
    return count > 0L;
  }

  @Override
  public List<ValinnanVaihe> valinnanVaiheetJaJonot(String hakukohdeOid) {
    QHakukohdeViite hakukohde = QHakukohdeViite.hakukohdeViite;
    QValinnanVaihe vv = QValinnanVaihe.valinnanVaihe;
    QValintatapajono jono = QValintatapajono.valintatapajono;
    HakukohdeViite h =
        queryFactory().selectFrom(hakukohde).where(hakukohde.oid.eq(hakukohdeOid)).fetchFirst();
    if (h == null) {
      return new ArrayList<>();
    }
    return queryFactory()
        .selectFrom(vv)
        .leftJoin(vv.jonot, jono)
        .fetchJoin()
        .where(vv.hakukohdeViite.eq(h))
        .distinct()
        .fetch();
  }

  private List<ValinnanVaihe> findByHakukohdeViite(HakukohdeViite hakukohdeViite) {
    QValinnanVaihe valinnanVaihe = QValinnanVaihe.valinnanVaihe;
    return queryFactory()
        .selectFrom(valinnanVaihe)
        .leftJoin(valinnanVaihe.jonot)
        .fetchJoin()
        .leftJoin(valinnanVaihe.edellinenValinnanVaihe)
        .fetchJoin()
        .leftJoin(valinnanVaihe.masterValinnanVaihe)
        .fetchJoin()
        .where(valinnanVaihe.hakukohdeViite.id.eq(hakukohdeViite.getId()))
        .distinct()
        .fetch();
  }

  private List<ValinnanVaihe> findByValintaryhma(Valintaryhma valintaryhma) {
    QValinnanVaihe valinnanVaihe = QValinnanVaihe.valinnanVaihe;
    return queryFactory()
        .selectFrom(valinnanVaihe)
        .leftJoin(valinnanVaihe.jonot)
        .fetchJoin()
        .leftJoin(valinnanVaihe.edellinenValinnanVaihe)
        .fetchJoin()
        .leftJoin(valinnanVaihe.masterValinnanVaihe)
        .fetchJoin()
        .where(valinnanVaihe.valintaryhma.id.eq(valintaryhma.getId()))
        .distinct()
        .fetch();
  }

  @Override
  public List<ValinnanVaihe> jarjestaUudelleen(
      HakukohdeViite hakukohdeViite, List<String> uusiJarjestys) {
    return LinkitettavaJaKopioitavaUtil.jarjestaUudelleen(
        getEntityManager(), findByHakukohdeViite(hakukohdeViite), uusiJarjestys);
  }

  @Override
  public List<ValinnanVaihe> jarjestaUudelleen(
      Valintaryhma valintaryhma, List<String> uusiJarjestys) {
    return LinkitettavaJaKopioitavaUtil.jarjestaUudelleen(
        getEntityManager(), findByValintaryhma(valintaryhma), uusiJarjestys);
  }

  @Override
  public List<ValinnanVaihe> jarjestaUudelleenMasterJarjestyksenMukaan(
      HakukohdeViite hakukohdeViite, List<ValinnanVaihe> uusiMasterJarjestys) {
    return LinkitettavaJaKopioitavaUtil.jarjestaUudelleenMasterJarjestyksenMukaan(
        getEntityManager(), findByHakukohdeViite(hakukohdeViite), uusiMasterJarjestys);
  }

  @Override
  public List<ValinnanVaihe> jarjestaUudelleenMasterJarjestyksenMukaan(
      Valintaryhma valintaryhma, List<ValinnanVaihe> uusiMasterJarjestys) {
    return LinkitettavaJaKopioitavaUtil.jarjestaUudelleenMasterJarjestyksenMukaan(
        getEntityManager(), findByValintaryhma(valintaryhma), uusiMasterJarjestys);
  }

  @Override
  public ValinnanVaihe insert(ValinnanVaihe uusi) {
    QValinnanVaihe valinnanVaihe = QValinnanVaihe.valinnanVaihe;
    ValinnanVaihe seuraava =
        queryFactory()
            .selectFrom(valinnanVaihe)
            .where(
                (uusi.getValintaryhma() == null
                        ? valinnanVaihe.hakukohdeViite.id.eq(uusi.getHakukohdeViite().getId())
                        : valinnanVaihe.valintaryhma.id.eq(uusi.getValintaryhma().getId()))
                    .and(
                        uusi.getEdellinen() == null
                            ? valinnanVaihe.edellinenValinnanVaihe.isNull()
                            : valinnanVaihe.edellinenValinnanVaihe.id.eq(
                                uusi.getEdellinen().getId())))
            .fetchFirst();
    if (seuraava != null && uusi.getEdellinen() == null) {
      seuraava.setEdellinen(seuraava);
      getEntityManager().flush();
    }

    getEntityManager().persist(uusi);

    if (seuraava != null) {
      seuraava.setEdellinen(uusi);
    }

    return uusi;
  }

  @Override
  public void delete(ValinnanVaihe valinnanVaihe) {
    for (ValinnanVaihe kopio : valinnanVaihe.getKopiot()) {
      delete(kopio);
    }

    EntityManager entityManager = getEntityManager();

    QValinnanVaihe seuraava = QValinnanVaihe.valinnanVaihe;
    ValinnanVaihe seuraavaValinnanVaihe =
        queryFactory()
            .selectFrom(seuraava)
            .where(seuraava.edellinenValinnanVaihe.id.eq(valinnanVaihe.getId()))
            .fetchFirst();

    if (seuraavaValinnanVaihe != null) {
      ValinnanVaihe edellinen = valinnanVaihe.getEdellinen();

      if (valinnanVaihe.getEdellinen() == null) {
        valinnanVaihe.setEdellinen(valinnanVaihe);
        entityManager.flush();
      }

      seuraavaValinnanVaihe.setEdellinen(edellinen);
    }

    entityManager.remove(valinnanVaihe);
  }
}
