package fi.vm.sade.service.valintaperusteet.dao.impl;

import com.mysema.query.jpa.JPASubQuery;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.EntityPath;
import fi.vm.sade.service.valintaperusteet.dao.AbstractJpaDAOImpl;
import fi.vm.sade.service.valintaperusteet.dao.ValinnanVaiheDAO;
import fi.vm.sade.service.valintaperusteet.model.*;
import fi.vm.sade.service.valintaperusteet.util.LinkitettavaJaKopioitavaUtil;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import org.springframework.stereotype.Repository;

@Repository
public class ValinnanVaiheDAOImpl extends AbstractJpaDAOImpl<ValinnanVaihe, Long>
    implements ValinnanVaiheDAO {

  protected JPAQuery from(EntityPath<?>... o) {
    return new JPAQuery(getEntityManager()).from(o);
  }

  protected JPASubQuery subQuery() {
    return new JPASubQuery();
  }

  @Override
  public ValinnanVaihe readByOid(String oid) {
    QValinnanVaihe valinnanVaihe = QValinnanVaihe.valinnanVaihe;
    QValintatapajono valintatapaJono = QValintatapajono.valintatapajono;
    return from(valinnanVaihe)
        .leftJoin(valinnanVaihe.jonot, valintatapaJono)
        .fetch()
        .leftJoin(valinnanVaihe.masterValinnanVaihe)
        .fetch()
        .leftJoin(valinnanVaihe.valintaryhma)
        .fetch()
        .leftJoin(valinnanVaihe.hakukohdeViite)
        .fetch()
        .where(valinnanVaihe.oid.eq(oid))
        .singleResult(valinnanVaihe);
  }

  @Override
  public List<ValinnanVaihe> haeKopiot(String oid) {
    QValinnanVaihe valinnanVaihe = QValinnanVaihe.valinnanVaihe;
    QValintatapajono valintatapaJono = QValintatapajono.valintatapajono;
    return from(valinnanVaihe)
        .leftJoin(valinnanVaihe.jonot, valintatapaJono)
        .fetch()
        .leftJoin(valinnanVaihe.masterValinnanVaihe)
        .fetch()
        .leftJoin(valinnanVaihe.valintaryhma)
        .fetch()
        .leftJoin(valinnanVaihe.hakukohdeViite)
        .fetch()
        .where(valinnanVaihe.masterValinnanVaihe.oid.eq(oid))
        .list(valinnanVaihe);
  }

  @Override
  public List<ValinnanVaihe> readByOids(Set<String> oids) {
    QValinnanVaihe valinnanVaihe = QValinnanVaihe.valinnanVaihe;
    QValintatapajono valintatapaJono = QValintatapajono.valintatapajono;
    return from(valinnanVaihe)
        .leftJoin(valinnanVaihe.jonot, valintatapaJono)
        .fetch()
        .where(valinnanVaihe.oid.in(oids))
        .list(valinnanVaihe);
  }

  @Override
  public ValinnanVaihe haeHakukohteenViimeinenValinnanVaihe(String hakukohdeOid) {
    QHakukohdeViite hakukohde = QHakukohdeViite.hakukohdeViite;
    QValinnanVaihe vv = QValinnanVaihe.valinnanVaihe;
    QValintatapajono valintatapaJono = QValintatapajono.valintatapajono;
    ValinnanVaihe lastValinnanVaihe =
        from(hakukohde)
            .leftJoin(hakukohde.valinnanvaiheet, vv)
            .leftJoin(vv.jonot, valintatapaJono)
            .fetch()
            .where(
                vv.id
                    .notIn(
                        subQuery()
                            .from(vv)
                            .where(vv.edellinenValinnanVaihe.isNotNull())
                            .list(vv.edellinenValinnanVaihe.id))
                    .and(hakukohde.oid.eq(hakukohdeOid)))
            .singleResult(vv);

    return lastValinnanVaihe;
  }

  @Override
  public Set<String> findValinnanVaiheOidsByValintaryhma(String valintaryhmaOid) {
    QValintaryhma valintaryhma = QValintaryhma.valintaryhma;
    QValinnanVaihe valinnanVaihe = QValinnanVaihe.valinnanVaihe;
    return new HashSet<>(
        from(valintaryhma)
            .leftJoin(valintaryhma.valinnanvaiheet, valinnanVaihe)
            .where(valintaryhma.oid.eq(valintaryhmaOid))
            .list(valinnanVaihe.oid));
  }

  @Override
  public Set<String> findValinnanVaiheOidsByHakukohde(String hakukohdeOid) {
    QHakukohdeViite hakukohde = QHakukohdeViite.hakukohdeViite;
    QValinnanVaihe valinnanVaihe = QValinnanVaihe.valinnanVaihe;
    return new HashSet<>(
        from(hakukohde)
            .leftJoin(hakukohde.valinnanvaiheet, valinnanVaihe)
            .where(hakukohde.oid.eq(hakukohdeOid))
            .list(valinnanVaihe.oid));
  }

  @Override
  public ValinnanVaihe haeValintaryhmanViimeinenValinnanVaihe(String oid) {
    QValintaryhma valintaryhma = QValintaryhma.valintaryhma;
    QValinnanVaihe vv = QValinnanVaihe.valinnanVaihe;
    QValintatapajono valintatapaJono = QValintatapajono.valintatapajono;
    ValinnanVaihe lastValinnanVaihe =
        from(valintaryhma)
            .leftJoin(valintaryhma.valinnanvaiheet, vv)
            .leftJoin(vv.jonot, valintatapaJono)
            .fetch()
            .where(
                vv.id
                    .notIn(
                        subQuery()
                            .from(vv)
                            .where(vv.edellinenValinnanVaihe.isNotNull())
                            .list(vv.edellinenValinnanVaihe.id))
                    .and(valintaryhma.oid.eq(oid)))
            .singleResult(vv);

    return lastValinnanVaihe;
  }

  @Override
  public List<ValinnanVaihe> findByValintaryhma(String oid) {
    QValintaryhma valintaryhma = QValintaryhma.valintaryhma;
    QValinnanVaihe vv = QValinnanVaihe.valinnanVaihe;
    QValintatapajono valintatapaJono = QValintatapajono.valintatapajono;
    return LinkitettavaJaKopioitavaUtil.jarjesta(
        from(vv)
            .join(vv.valintaryhma, valintaryhma)
            .fetch()
            .leftJoin(vv.jonot, valintatapaJono)
            .fetch()
            .leftJoin(vv.edellinenValinnanVaihe)
            .fetch()
            .leftJoin(vv.masterValinnanVaihe)
            .fetch()
            .where(valintaryhma.oid.eq(oid))
            .distinct()
            .list(vv));
  }

  @Override
  public List<ValinnanVaihe> findByHakukohde(String oid) {
    QHakukohdeViite hakukohde = QHakukohdeViite.hakukohdeViite;
    QValinnanVaihe vv = QValinnanVaihe.valinnanVaihe;
    QValintatapajono valintatapaJono = QValintatapajono.valintatapajono;
    return LinkitettavaJaKopioitavaUtil.jarjesta(
        from(vv)
            .join(vv.hakukohdeViite, hakukohde)
            .fetch()
            .leftJoin(vv.jonot, valintatapaJono)
            .fetch()
            .leftJoin(vv.edellinenValinnanVaihe)
            .fetch()
            .leftJoin(vv.masterValinnanVaihe)
            .fetch()
            .where(hakukohde.oid.eq(oid))
            .distinct()
            .list(vv));
  }

  @Override
  public boolean kuuluuSijoitteluun(String oid) {
    QValinnanVaihe vv = QValinnanVaihe.valinnanVaihe;
    QValintatapajono jono = QValintatapajono.valintatapajono;
    return from(jono)
            .leftJoin(jono.valinnanVaihe, vv)
            .where(
                vv.oid.eq(oid),
                vv.aktiivinen.eq(true),
                jono.aktiivinen.eq(true),
                jono.siirretaanSijoitteluun.eq(true))
            .count()
        > 0;
  }

  @Override
  public List<ValinnanVaihe> ilmanLaskentaaOlevatHakukohteelle(String hakukohdeOid) {
    QHakukohdeViite hakukohde = QHakukohdeViite.hakukohdeViite;
    QValinnanVaihe vv = QValinnanVaihe.valinnanVaihe;
    QValintatapajono jono = QValintatapajono.valintatapajono;
    return from(hakukohde)
        .leftJoin(hakukohde.valinnanvaiheet, vv)
        .leftJoin(vv.jonot, jono)
        .fetch()
        .where(hakukohde.oid.eq(hakukohdeOid).and(jono.kaytetaanValintalaskentaa.isFalse()))
        .distinct()
        .list(vv);
  }

  @Override
  public List<ValinnanVaihe> valinnanVaiheetJaJonot(String hakukohdeOid) {
    QHakukohdeViite hakukohde = QHakukohdeViite.hakukohdeViite;
    QValinnanVaihe vv = QValinnanVaihe.valinnanVaihe;
    QValintatapajono jono = QValintatapajono.valintatapajono;
    HakukohdeViite h =
        from(hakukohde).where(hakukohde.oid.eq(hakukohdeOid)).singleResult(hakukohde);
    if (h == null) {
      return new ArrayList<>();
    }
    return from(vv)
        .leftJoin(vv.jonot, jono)
        .fetch()
        .where(vv.hakukohdeViite.eq(h))
        .distinct()
        .list(vv);
  }

  private List<ValinnanVaihe> findByHakukohdeViite(HakukohdeViite hakukohdeViite) {
    QValinnanVaihe valinnanVaihe = QValinnanVaihe.valinnanVaihe;
    return from(valinnanVaihe)
        .leftJoin(valinnanVaihe.jonot)
        .fetch()
        .leftJoin(valinnanVaihe.edellinenValinnanVaihe)
        .fetch()
        .leftJoin(valinnanVaihe.masterValinnanVaihe)
        .fetch()
        .where(valinnanVaihe.hakukohdeViite.id.eq(hakukohdeViite.getId()))
        .distinct()
        .list(valinnanVaihe);
  }

  private List<ValinnanVaihe> findByValintaryhma(Valintaryhma valintaryhma) {
    QValinnanVaihe valinnanVaihe = QValinnanVaihe.valinnanVaihe;
    return from(valinnanVaihe)
        .leftJoin(valinnanVaihe.jonot)
        .fetch()
        .leftJoin(valinnanVaihe.edellinenValinnanVaihe)
        .fetch()
        .leftJoin(valinnanVaihe.masterValinnanVaihe)
        .fetch()
        .where(valinnanVaihe.valintaryhma.id.eq(valintaryhma.getId()))
        .distinct()
        .list(valinnanVaihe);
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
        from(valinnanVaihe)
            .where(
                (uusi.getValintaryhma() == null
                        ? valinnanVaihe.hakukohdeViite.id.eq(uusi.getHakukohdeViite().getId())
                        : valinnanVaihe.valintaryhma.id.eq(uusi.getValintaryhma().getId()))
                    .and(
                        uusi.getEdellinen() == null
                            ? valinnanVaihe.edellinenValinnanVaihe.isNull()
                            : valinnanVaihe.edellinenValinnanVaihe.id.eq(
                                uusi.getEdellinen().getId())))
            .singleResult(valinnanVaihe);
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
        from(seuraava)
            .where(seuraava.edellinenValinnanVaihe.id.eq(valinnanVaihe.getId()))
            .singleResult(seuraava);

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
