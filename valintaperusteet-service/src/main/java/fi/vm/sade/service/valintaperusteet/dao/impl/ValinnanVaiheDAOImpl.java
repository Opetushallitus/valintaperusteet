package fi.vm.sade.service.valintaperusteet.dao.impl;

import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.jpa.impl.JPASubQuery;
import com.mysema.query.types.EntityPath;
import fi.vm.sade.service.valintaperusteet.dao.AbstractJpaDAOImpl;
import fi.vm.sade.service.valintaperusteet.dao.ValinnanVaiheDAO;
import fi.vm.sade.service.valintaperusteet.model.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    return from(valintaryhma)
        .leftJoin(valintaryhma.valinnanvaiheet, vv)
        .leftJoin(vv.jonot, valintatapaJono)
        .fetch()
        .leftJoin(vv.seuraavaValinnanVaihe)
        .fetch()
        .leftJoin(vv.masterValinnanVaihe)
        .fetch()
        .where(valintaryhma.oid.eq(oid))
        .list(vv);
  }

  @Override
  public List<ValinnanVaihe> findByHakukohde(String oid) {
    QHakukohdeViite hakukohde = QHakukohdeViite.hakukohdeViite;
    QValinnanVaihe vv = QValinnanVaihe.valinnanVaihe;
    QValintatapajono valintatapaJono = QValintatapajono.valintatapajono;
    return from(hakukohde)
        .leftJoin(hakukohde.valinnanvaiheet, vv)
        .leftJoin(vv.jonot, valintatapaJono)
        .fetch()
        .leftJoin(vv.seuraavaValinnanVaihe)
        .fetch()
        .leftJoin(vv.masterValinnanVaihe)
        .fetch()
        .where(hakukohde.oid.eq(oid))
        .list(vv);
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
}
