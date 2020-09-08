package fi.vm.sade.service.valintaperusteet.dao.impl;

import com.mysema.query.jpa.JPASubQuery;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.EntityPath;
import fi.vm.sade.service.valintaperusteet.dao.AbstractJpaDAOImpl;
import fi.vm.sade.service.valintaperusteet.dao.ValintatapajonoDAO;
import fi.vm.sade.service.valintaperusteet.model.QHakijaryhmaValintatapajono;
import fi.vm.sade.service.valintaperusteet.model.QHakukohdeViite;
import fi.vm.sade.service.valintaperusteet.model.QValinnanVaihe;
import fi.vm.sade.service.valintaperusteet.model.QValintatapajono;
import fi.vm.sade.service.valintaperusteet.model.ValinnanVaihe;
import fi.vm.sade.service.valintaperusteet.model.Valintatapajono;
import fi.vm.sade.service.valintaperusteet.util.LinkitettavaJaKopioitavaUtil;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class ValintatapajonoDAOImpl extends AbstractJpaDAOImpl<Valintatapajono, Long>
    implements ValintatapajonoDAO {

  private static final Logger LOG = LoggerFactory.getLogger(ValintatapajonoDAOImpl.class);

  protected JPAQuery from(EntityPath<?>... o) {
    return new JPAQuery(getEntityManager()).from(o);
  }

  protected JPASubQuery subQuery() {
    return new JPASubQuery();
  }

  public List<Valintatapajono> findByValinnanVaihe(String oid) {
    QValinnanVaihe valinnanVaihe = QValinnanVaihe.valinnanVaihe;
    QValintatapajono jono = QValintatapajono.valintatapajono;
    QHakijaryhmaValintatapajono hv = QHakijaryhmaValintatapajono.hakijaryhmaValintatapajono;
    return LinkitettavaJaKopioitavaUtil.jarjesta(
        from(jono)
            .join(jono.valinnanVaihe, valinnanVaihe)
            .fetch()
            .leftJoin(valinnanVaihe.valintaryhma)
            .leftJoin(jono.edellinenValintatapajono)
            .fetch()
            .leftJoin(jono.masterValintatapajono)
            .fetch()
            .leftJoin(jono.varasijanTayttojono)
            .fetch()
            .leftJoin(jono.hakijaryhmat, hv)
            .fetch()
            .leftJoin(hv.hakijaryhma)
            .fetch()
            .leftJoin(jono.valinnanVaihe)
            .fetch()
            .where(valinnanVaihe.oid.eq(oid))
            .distinct()
            .list(jono));
  }

  @Override
  public List<Valintatapajono> findAll() {
    QValintatapajono jono = QValintatapajono.valintatapajono;
    QValinnanVaihe valinnanVaihe = QValinnanVaihe.valinnanVaihe;
    QHakijaryhmaValintatapajono hakijaryhmaValintatapajono =
        QHakijaryhmaValintatapajono.hakijaryhmaValintatapajono;
    return from(jono)
        .leftJoin(jono.valinnanVaihe, valinnanVaihe)
        .fetch()
        .leftJoin(jono.hakijaryhmat, hakijaryhmaValintatapajono)
        .fetch()
        .leftJoin(jono.varasijanTayttojono)
        .fetch()
        .leftJoin(hakijaryhmaValintatapajono.hakijaryhma)
        .fetch()
        .leftJoin(valinnanVaihe.valintaryhma)
        .distinct()
        .list(jono);
  }

  @Override
  public Valintatapajono readByOid(String oid) {
    QValintatapajono jono = QValintatapajono.valintatapajono;
    QValinnanVaihe valinnanVaihe = QValinnanVaihe.valinnanVaihe;
    QHakijaryhmaValintatapajono hakijaryhmaValintatapajono =
        QHakijaryhmaValintatapajono.hakijaryhmaValintatapajono;
    return from(jono)
        .where(jono.oid.eq(oid))
        .leftJoin(jono.valinnanVaihe, valinnanVaihe)
        .fetch()
        .leftJoin(jono.masterValintatapajono)
        .fetch()
        .leftJoin(jono.edellinenValintatapajono)
        .fetch()
        .leftJoin(jono.varasijanTayttojono)
        .fetch()
        .leftJoin(jono.hakijaryhmat, hakijaryhmaValintatapajono)
        .fetch()
        .leftJoin(hakijaryhmaValintatapajono.hakijaryhma)
        .fetch()
        .leftJoin(valinnanVaihe.valintaryhma)
        .singleResult(jono);
  }

  @Override
  public List<Valintatapajono> readByOids(List<String> oids) {
    QValintatapajono jono = QValintatapajono.valintatapajono;
    QValinnanVaihe valinnanVaihe = QValinnanVaihe.valinnanVaihe;
    QHakijaryhmaValintatapajono hakijaryhmaValintatapajono =
        QHakijaryhmaValintatapajono.hakijaryhmaValintatapajono;
    return from(jono)
        .where(jono.oid.in(oids))
        .leftJoin(jono.valinnanVaihe, valinnanVaihe)
        .fetch()
        .leftJoin(jono.masterValintatapajono)
        .fetch()
        .leftJoin(jono.edellinenValintatapajono)
        .fetch()
        .leftJoin(jono.varasijanTayttojono)
        .fetch()
        .leftJoin(jono.hakijaryhmat, hakijaryhmaValintatapajono)
        .fetch()
        .leftJoin(hakijaryhmaValintatapajono.hakijaryhma)
        .fetch()
        .leftJoin(valinnanVaihe.valintaryhma)
        .list(jono);
  }

  @Override
  public List<Valintatapajono> haeKopiot(String oid) {
    QValintatapajono valintatapajono = QValintatapajono.valintatapajono;
    return from(valintatapajono)
        .leftJoin(valintatapajono.valinnanVaihe)
        .fetch()
        .leftJoin(valintatapajono.masterValintatapajono)
        .fetch()
        .where(valintatapajono.masterValintatapajono.oid.eq(oid))
        .list(valintatapajono);
  }

  @Override
  public List<Valintatapajono> haeKopiotValisijoittelulle(String oid) {
    QValintatapajono valintatapajono = QValintatapajono.valintatapajono;
    return from(valintatapajono)
        .leftJoin(valintatapajono.masterValintatapajono)
        .where(valintatapajono.masterValintatapajono.oid.eq(oid))
        .where(valintatapajono.aktiivinen.eq(Boolean.TRUE))
        .where(valintatapajono.valisijoittelu.eq(Boolean.TRUE))
        .distinct()
        .list(valintatapajono);
  }

  @Override
  public List<Valintatapajono> ilmanLaskentaaOlevatHakukohteelle(String hakukohdeOid) {
    QHakukohdeViite hakukohde = QHakukohdeViite.hakukohdeViite;
    QValinnanVaihe vv = QValinnanVaihe.valinnanVaihe;
    QValintatapajono jono = QValintatapajono.valintatapajono;
    return from(hakukohde)
        .leftJoin(hakukohde.valinnanvaiheet, vv)
        .leftJoin(vv.jonot, jono)
        .where(hakukohde.oid.eq(hakukohdeOid).and(jono.kaytetaanValintalaskentaa.isFalse()))
        .distinct()
        .list(jono);
  }

  private List<Valintatapajono> findByValinnanVaihe(ValinnanVaihe valinnanVaihe) {
    QValintatapajono valintatapajono = QValintatapajono.valintatapajono;
    return from(valintatapajono)
        .leftJoin(valintatapajono.edellinenValintatapajono)
        .fetch()
        .leftJoin(valintatapajono.masterValintatapajono)
        .fetch()
        .leftJoin(valintatapajono.varasijanTayttojono)
        .fetch()
        .where(valintatapajono.valinnanVaihe.id.eq(valinnanVaihe.getId()))
        .distinct()
        .list(valintatapajono);
  }

  @Override
  public List<Valintatapajono> jarjestaUudelleen(
      ValinnanVaihe valinnanVaihe, List<String> uusiJarjestys) {
    return LinkitettavaJaKopioitavaUtil.jarjestaUudelleen(
        getEntityManager(), findByValinnanVaihe(valinnanVaihe), uusiJarjestys);
  }

  @Override
  public List<Valintatapajono> jarjestaUudelleenMasterJarjestyksenMukaan(
      ValinnanVaihe valinnanVaihe, List<Valintatapajono> uusiMasterJarjestys) {
    return LinkitettavaJaKopioitavaUtil.jarjestaUudelleenMasterJarjestyksenMukaan(
        getEntityManager(), findByValinnanVaihe(valinnanVaihe), uusiMasterJarjestys);
  }

  @Override
  public List<Valintatapajono> haeValintatapajonotSijoittelulle(String hakukohdeOid) {

    QHakukohdeViite hakukohde = QHakukohdeViite.hakukohdeViite;
    QValinnanVaihe vv = QValinnanVaihe.valinnanVaihe;
    QValintatapajono jono = QValintatapajono.valintatapajono;

    // Etsitään hakukohteen viimeinen aktiivinen valinnan vaihe
    List<ValinnanVaihe> valinnanVaiheet =
        LinkitettavaJaKopioitavaUtil.jarjesta(
            from(vv)
                .join(vv.hakukohdeViite, hakukohde)
                .where((hakukohde.oid.eq(hakukohdeOid)))
                .list(vv));

    List<ValinnanVaihe> aktiivisetValinnanVaiheet =
        valinnanVaiheet.stream().filter(ValinnanVaihe::getAktiivinen).collect(Collectors.toList());

    ValinnanVaihe lastValinnanVaihe =
        aktiivisetValinnanVaiheet.get(aktiivisetValinnanVaiheet.size() - 1);

    // Haetaan löydetyn valinnan vaiheen kaikki jonot
    List<Valintatapajono> jonot =
        LinkitettavaJaKopioitavaUtil.jarjesta(
            from(jono)
                .join(jono.valinnanVaihe, vv)
                .leftJoin(jono.edellinenValintatapajono)
                .fetch()
                .leftJoin(jono.varasijanTayttojono)
                .fetch()
                .where(vv.oid.eq(lastValinnanVaihe.getOid()))
                .distinct()
                .list(jono));

    // BUG-255 poistetaan jonoista väärin tallentuneet täyttöjonot
    for (Valintatapajono j : jonot) {
      if (j.getVarasijanTayttojono() != null && !jonot.contains(j.getVarasijanTayttojono())) {
        j.setVarasijanTayttojono(null);
      }
    }

    return jonot;
  }

  @Override
  public List<Valintatapajono> haeValintatapajonotHakukohteelle(String hakukohdeOid) {
    QHakukohdeViite hakukohde = QHakukohdeViite.hakukohdeViite;
    QValinnanVaihe vv = QValinnanVaihe.valinnanVaihe;
    QValintatapajono jono = QValintatapajono.valintatapajono;
    return from(hakukohde)
        .leftJoin(hakukohde.valinnanvaiheet, vv)
        .leftJoin(vv.jonot, jono)
        .where(hakukohde.oid.eq(hakukohdeOid))
        .distinct()
        .list(jono);
  }

  @Override
  public Valintatapajono haeValinnanVaiheenViimeinenValintatapajono(String valinnanVaiheOid) {
    QValinnanVaihe valinnanVaihe = QValinnanVaihe.valinnanVaihe;
    QValintatapajono jono = QValintatapajono.valintatapajono;
    return from(valinnanVaihe)
        .leftJoin(valinnanVaihe.jonot, jono)
        .leftJoin(valinnanVaihe.valintaryhma)
        .where(
            jono.id
                .notIn(
                    subQuery()
                        .from(jono)
                        .where(jono.edellinenValintatapajono.isNotNull())
                        .list(jono.edellinenValintatapajono.id))
                .and(valinnanVaihe.oid.eq(valinnanVaiheOid)))
        .singleResult(jono);
  }

  @Override
  public Valintatapajono insert(Valintatapajono entity) {
    Valintatapajono insert = super.insert(entity);
    return insert;
  }
}
