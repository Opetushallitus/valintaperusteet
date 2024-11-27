package fi.vm.sade.service.valintaperusteet.dao.impl;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import fi.vm.sade.service.valintaperusteet.dao.AbstractJpaDAOImpl;
import fi.vm.sade.service.valintaperusteet.dao.ValintatapajonoDAO;
import fi.vm.sade.service.valintaperusteet.model.QHakijaryhmaValintatapajono;
import fi.vm.sade.service.valintaperusteet.model.QHakukohdeViite;
import fi.vm.sade.service.valintaperusteet.model.QValinnanVaihe;
import fi.vm.sade.service.valintaperusteet.model.QValintatapajono;
import fi.vm.sade.service.valintaperusteet.model.ValinnanVaihe;
import fi.vm.sade.service.valintaperusteet.model.Valintatapajono;
import fi.vm.sade.service.valintaperusteet.util.LinkitettavaJaKopioitavaUtil;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class ValintatapajonoDAOImpl extends AbstractJpaDAOImpl<Valintatapajono, Long>
    implements ValintatapajonoDAO {
  protected JPAQueryFactory queryFactory() {
    return new JPAQueryFactory(getEntityManager());
  }

  public List<Valintatapajono> findByValinnanVaihe(String oid) {
    QValinnanVaihe valinnanVaihe = QValinnanVaihe.valinnanVaihe;
    QValintatapajono jono = QValintatapajono.valintatapajono;
    QHakijaryhmaValintatapajono hv = QHakijaryhmaValintatapajono.hakijaryhmaValintatapajono;
    return LinkitettavaJaKopioitavaUtil.jarjesta(
        queryFactory()
            .selectFrom(jono)
            .join(jono.valinnanVaihe, valinnanVaihe)
            .fetchJoin()
            .leftJoin(valinnanVaihe.valintaryhma)
            .leftJoin(jono.edellinenValintatapajono)
            .fetchJoin()
            .leftJoin(jono.masterValintatapajono)
            .fetchJoin()
            .leftJoin(jono.varasijanTayttojono)
            .fetchJoin()
            .leftJoin(jono.hakijaryhmat, hv)
            .fetchJoin()
            .leftJoin(hv.hakijaryhma)
            .fetchJoin()
            .leftJoin(jono.valinnanVaihe)
            .fetchJoin()
            .where(valinnanVaihe.oid.eq(oid))
            .distinct()
            .fetch());
  }

  @Override
  public List<Valintatapajono> findAll() {
    QValintatapajono jono = QValintatapajono.valintatapajono;
    QValinnanVaihe valinnanVaihe = QValinnanVaihe.valinnanVaihe;
    QHakijaryhmaValintatapajono hakijaryhmaValintatapajono =
        QHakijaryhmaValintatapajono.hakijaryhmaValintatapajono;
    return queryFactory()
        .selectFrom(jono)
        .leftJoin(jono.valinnanVaihe, valinnanVaihe)
        .fetchJoin()
        .leftJoin(jono.hakijaryhmat, hakijaryhmaValintatapajono)
        .fetchJoin()
        .leftJoin(jono.varasijanTayttojono)
        .fetchJoin()
        .leftJoin(hakijaryhmaValintatapajono.hakijaryhma)
        .fetchJoin()
        .leftJoin(valinnanVaihe.valintaryhma)
        .distinct()
        .fetch();
  }

  @Override
  public Valintatapajono readByOid(String oid) {
    QValintatapajono jono = QValintatapajono.valintatapajono;
    QValinnanVaihe valinnanVaihe = QValinnanVaihe.valinnanVaihe;
    QHakijaryhmaValintatapajono hakijaryhmaValintatapajono =
        QHakijaryhmaValintatapajono.hakijaryhmaValintatapajono;
    return queryFactory()
        .selectFrom(jono)
        .where(jono.oid.eq(oid))
        .leftJoin(jono.valinnanVaihe, valinnanVaihe)
        .fetchJoin()
        .leftJoin(jono.masterValintatapajono)
        .fetchJoin()
        .leftJoin(jono.edellinenValintatapajono)
        .fetchJoin()
        .leftJoin(jono.varasijanTayttojono)
        .fetchJoin()
        .leftJoin(jono.hakijaryhmat, hakijaryhmaValintatapajono)
        .fetchJoin()
        .leftJoin(hakijaryhmaValintatapajono.hakijaryhma)
        .fetchJoin()
        .leftJoin(valinnanVaihe.valintaryhma)
        .fetchFirst();
  }

  @Override
  public List<Valintatapajono> readByOids(List<String> oids) {
    QValintatapajono jono = QValintatapajono.valintatapajono;
    QValinnanVaihe valinnanVaihe = QValinnanVaihe.valinnanVaihe;
    QHakijaryhmaValintatapajono hakijaryhmaValintatapajono =
        QHakijaryhmaValintatapajono.hakijaryhmaValintatapajono;
    return queryFactory()
        .selectFrom(jono)
        .where(jono.oid.in(oids))
        .leftJoin(jono.valinnanVaihe, valinnanVaihe)
        .fetchJoin()
        .leftJoin(jono.masterValintatapajono)
        .fetchJoin()
        .leftJoin(jono.edellinenValintatapajono)
        .fetchJoin()
        .leftJoin(jono.varasijanTayttojono)
        .fetchJoin()
        .leftJoin(jono.hakijaryhmat, hakijaryhmaValintatapajono)
        .fetchJoin()
        .leftJoin(hakijaryhmaValintatapajono.hakijaryhma)
        .fetchJoin()
        .leftJoin(valinnanVaihe.valintaryhma)
        .fetch();
  }

  @Override
  public List<Valintatapajono> haeKopiot(String oid) {
    QValintatapajono valintatapajono = QValintatapajono.valintatapajono;
    return queryFactory()
        .selectFrom(valintatapajono)
        .leftJoin(valintatapajono.valinnanVaihe)
        .fetchJoin()
        .leftJoin(valintatapajono.masterValintatapajono)
        .fetchJoin()
        .where(valintatapajono.masterValintatapajono.oid.eq(oid))
        .fetch();
  }

  @Override
  public List<Valintatapajono> haeKopiotValisijoittelulle(String oid) {
    QValintatapajono valintatapajono = QValintatapajono.valintatapajono;
    return queryFactory()
        .selectFrom(valintatapajono)
        .leftJoin(valintatapajono.masterValintatapajono)
        .where(valintatapajono.masterValintatapajono.oid.eq(oid))
        .where(valintatapajono.aktiivinen.eq(Boolean.TRUE))
        .where(valintatapajono.valisijoittelu.eq(Boolean.TRUE))
        .distinct()
        .fetch();
  }

  @Override
  public List<Valintatapajono> ilmanLaskentaaOlevatHakukohteelle(String hakukohdeOid) {
    QHakukohdeViite hakukohde = QHakukohdeViite.hakukohdeViite;
    QValinnanVaihe vv = QValinnanVaihe.valinnanVaihe;
    QValintatapajono jono = QValintatapajono.valintatapajono;
    return queryFactory()
        .select(jono)
        .from(hakukohde)
        .leftJoin(hakukohde.valinnanvaiheet, vv)
        .leftJoin(vv.jonot, jono)
        .where(hakukohde.oid.eq(hakukohdeOid).and(jono.kaytetaanValintalaskentaa.isFalse()))
        .distinct()
        .fetch();
  }

  private List<Valintatapajono> findByValinnanVaihe(ValinnanVaihe valinnanVaihe) {
    QValintatapajono valintatapajono = QValintatapajono.valintatapajono;
    return queryFactory()
        .selectFrom(valintatapajono)
        .leftJoin(valintatapajono.edellinenValintatapajono)
        .fetchJoin()
        .leftJoin(valintatapajono.masterValintatapajono)
        .fetchJoin()
        .leftJoin(valintatapajono.varasijanTayttojono)
        .fetchJoin()
        .where(valintatapajono.valinnanVaihe.id.eq(valinnanVaihe.getId()))
        .distinct()
        .fetch();
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
  public void delete(Valintatapajono valintatapajono) {
    for (Valintatapajono kopio : valintatapajono.getKopiot()) {
      delete(kopio);
    }

    EntityManager entityManager = getEntityManager();

    QValintatapajono seuraava = QValintatapajono.valintatapajono;
    Valintatapajono seuraavaValintatapajono =
        queryFactory()
            .selectFrom(seuraava)
            .where(seuraava.edellinenValintatapajono.id.eq(valintatapajono.getId()))
            .fetchFirst();

    if (seuraavaValintatapajono != null) {
      Valintatapajono edellinen = valintatapajono.getEdellinen();

      if (valintatapajono.getEdellinen() == null) {
        valintatapajono.setEdellinen(valintatapajono);
        entityManager.flush();
      }

      seuraavaValintatapajono.setEdellinen(edellinen);
    }

    entityManager.remove(valintatapajono);
  }

  @Override
  public List<Valintatapajono> haeValintatapajonotSijoittelulle(String hakukohdeOid) {

    QHakukohdeViite hakukohde = QHakukohdeViite.hakukohdeViite;
    QValinnanVaihe vv = QValinnanVaihe.valinnanVaihe;
    QValintatapajono jono = QValintatapajono.valintatapajono;

    // Etsitään hakukohteen viimeinen aktiivinen valinnan vaihe
    List<ValinnanVaihe> valinnanVaiheet =
        LinkitettavaJaKopioitavaUtil.jarjesta(
            queryFactory()
                .selectFrom(vv)
                .join(vv.hakukohdeViite, hakukohde)
                .where((hakukohde.oid.eq(hakukohdeOid)))
                .fetch());

    List<ValinnanVaihe> aktiivisetValinnanVaiheet =
        valinnanVaiheet.stream().filter(ValinnanVaihe::getAktiivinen).collect(Collectors.toList());

    ValinnanVaihe lastValinnanVaihe =
        aktiivisetValinnanVaiheet.get(aktiivisetValinnanVaiheet.size() - 1);

    // Haetaan löydetyn valinnan vaiheen kaikki jonot
    List<Valintatapajono> jonot =
        LinkitettavaJaKopioitavaUtil.jarjesta(
            queryFactory()
                .selectFrom(jono)
                .join(jono.valinnanVaihe, vv)
                .leftJoin(jono.edellinenValintatapajono)
                .fetchJoin()
                .leftJoin(jono.varasijanTayttojono)
                .fetchJoin()
                .where(vv.oid.eq(lastValinnanVaihe.getOid()))
                .distinct()
                .fetch());

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
    return queryFactory()
        .select(jono)
        .from(hakukohde)
        .leftJoin(hakukohde.valinnanvaiheet, vv)
        .leftJoin(vv.jonot, jono)
        .where(hakukohde.oid.eq(hakukohdeOid))
        .distinct()
        .fetch();
  }

  @Override
  public Valintatapajono haeValinnanVaiheenViimeinenValintatapajono(String valinnanVaiheOid) {
    QValinnanVaihe valinnanVaihe = QValinnanVaihe.valinnanVaihe;
    QValintatapajono jono = QValintatapajono.valintatapajono;
    return queryFactory()
        .select(jono)
        .from(valinnanVaihe)
        .leftJoin(valinnanVaihe.jonot, jono)
        .leftJoin(valinnanVaihe.valintaryhma)
        .where(
            jono.id
                .notIn(
                    JPAExpressions.select(jono.edellinenValintatapajono.id)
                        .from(jono)
                        .where(jono.edellinenValintatapajono.isNotNull()))
                .and(valinnanVaihe.oid.eq(valinnanVaiheOid)))
        .fetchFirst();
  }

  @Override
  public Valintatapajono insert(Valintatapajono uusi) {
    QValintatapajono valintatapajono = QValintatapajono.valintatapajono;
    Valintatapajono seuraava =
        queryFactory()
            .selectFrom(valintatapajono)
            .where(
                valintatapajono
                    .valinnanVaihe
                    .id
                    .eq(uusi.getValinnanVaihe().getId())
                    .and(
                        uusi.getEdellinen() == null
                            ? valintatapajono.edellinenValintatapajono.isNull()
                            : valintatapajono.edellinenValintatapajono.id.eq(
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
}
