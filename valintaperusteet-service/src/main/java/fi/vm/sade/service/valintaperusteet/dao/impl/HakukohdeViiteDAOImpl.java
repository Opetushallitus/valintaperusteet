package fi.vm.sade.service.valintaperusteet.dao.impl;

import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import fi.vm.sade.service.valintaperusteet.dao.AbstractJpaDAOImpl;
import fi.vm.sade.service.valintaperusteet.dao.HakukohdeViiteDAO;
import fi.vm.sade.service.valintaperusteet.model.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

@Repository
public class HakukohdeViiteDAOImpl extends AbstractJpaDAOImpl<HakukohdeViite, Long>
    implements HakukohdeViiteDAO {
  protected JPAQueryFactory queryFactory() {
    return new JPAQueryFactory(getEntityManager());
  }

  @Override
  public List<HakukohdeViite> findRoot() {
    QHakukohdeViite hakukohdeViite = QHakukohdeViite.hakukohdeViite;
    QValintaryhma valintaryhma = QValintaryhma.valintaryhma;
    return queryFactory()
        .selectFrom(hakukohdeViite)
        .leftJoin(hakukohdeViite.valintaryhma, valintaryhma)
        .fetchJoin()
        .leftJoin(hakukohdeViite.hakukohdekoodi)
        .fetchJoin()
        .where(valintaryhma.isNull())
        .fetch();
  }

  @Override
  public List<HakukohdeViite> haunHakukohteet(String hakuOid) {
    QHakukohdeViite hakukohdeViite = QHakukohdeViite.hakukohdeViite;
    QValintaryhma valintaryhma = QValintaryhma.valintaryhma;

    return queryFactory()
        .selectFrom(hakukohdeViite)
        .leftJoin(hakukohdeViite.valintaryhma, valintaryhma)
        .fetchJoin()
        .leftJoin(hakukohdeViite.hakukohdekoodi)
        .fetchJoin()
        .where(hakukohdeViite.hakuoid.eq(hakuOid))
        .fetch();
  }

  @Override
  public List<HakukohdeViite> findAll() {
    QHakukohdeViite hakukohdeViite = QHakukohdeViite.hakukohdeViite;
    QValintaryhma valintaryhma = QValintaryhma.valintaryhma;
    return queryFactory()
        .selectFrom(hakukohdeViite)
        .leftJoin(hakukohdeViite.valintaryhma, valintaryhma)
        .fetchJoin()
        .leftJoin(hakukohdeViite.hakukohdekoodi)
        .fetchJoin()
        .fetch();
  }

  @Override
  public HakukohdeViite readByOid(String oid) {
    QHakukohdeViite hakukohdeViite = QHakukohdeViite.hakukohdeViite;
    QValintaryhma valintaryhma = QValintaryhma.valintaryhma;
    return queryFactory()
        .selectFrom(hakukohdeViite)
        .leftJoin(hakukohdeViite.valintaryhma, valintaryhma)
        .fetchJoin()
        .leftJoin(hakukohdeViite.hakukohdekoodi)
        .fetchJoin()
        .leftJoin(hakukohdeViite.valintakokeet)
        .fetchJoin()
        .leftJoin(hakukohdeViite.hakukohteenValintaperusteet)
        .fetchJoin()
        .where(hakukohdeViite.oid.eq(oid))
        .fetchFirst();
  }

  @Override
  public List<HakukohdeViite> readByOids(List<String> oids) {
    QHakukohdeViite hakukohdeViite = QHakukohdeViite.hakukohdeViite;
    QValintaryhma valintaryhma = QValintaryhma.valintaryhma;
    return queryFactory()
        .selectFrom(hakukohdeViite)
        .leftJoin(hakukohdeViite.hakukohteenValintaperusteet)
        .fetchJoin()
        .where(hakukohdeViite.oid.in(oids))
        .distinct()
        .fetch();
  }

  @Override
  public HakukohdeViite readForImport(String oid) {
    QHakukohdeViite hakukohdeViite = QHakukohdeViite.hakukohdeViite;
    QValintaryhma valintaryhma = QValintaryhma.valintaryhma;
    return queryFactory()
        .selectFrom(hakukohdeViite)
        .leftJoin(hakukohdeViite.valintaryhma, valintaryhma)
        .fetchJoin()
        .where(hakukohdeViite.oid.eq(oid))
        .fetchFirst();
  }

  @Override
  public List<HakukohdeViite> findByValintaryhmaOid(String oid) {
    QHakukohdeViite hakukohdeViite = QHakukohdeViite.hakukohdeViite;
    QValintaryhma valintaryhma = QValintaryhma.valintaryhma;
    return queryFactory()
        .selectFrom(hakukohdeViite)
        .leftJoin(hakukohdeViite.valintaryhma, valintaryhma)
        .fetchJoin()
        .leftJoin(hakukohdeViite.hakukohdekoodi)
        .fetchJoin()
        .where(valintaryhma.oid.eq(oid))
        .fetch();
  }

  @Override
  public List<HakukohdeViite> findByValintaryhmaOidForValisijoittelu(String oid) {
    QHakukohdeViite hakukohdeViite = QHakukohdeViite.hakukohdeViite;
    return queryFactory()
        .selectFrom(hakukohdeViite)
        .leftJoin(hakukohdeViite.valintaryhma)
        .leftJoin(hakukohdeViite.hakukohdekoodi)
        .fetchJoin()
        .where(hakukohdeViite.valintaryhma.oid.eq(oid))
        .fetch();
  }

  @Override
  public boolean kuuluuSijoitteluun(String oid) {
    QHakukohdeViite hakukohdeViite = QHakukohdeViite.hakukohdeViite;
    QValinnanVaihe vv = QValinnanVaihe.valinnanVaihe;
    QValintatapajono jono = QValintatapajono.valintatapajono;
    Long count =
        queryFactory()
            .select(jono.id.count())
            .from(jono)
            .leftJoin(jono.valinnanVaihe, vv)
            .leftJoin(vv.hakukohdeViite, hakukohdeViite)
            .where(
                hakukohdeViite.oid.eq(oid),
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
  public void flush() {
    getEntityManager().flush();
  }

  @Override
  public List<HakukohdeViite> search(String hakuOid, List<String> tila, String searchString) {
    QHakukohdeViite hakukohdeViite = QHakukohdeViite.hakukohdeViite;
    JPAQuery<HakukohdeViite> a =
        queryFactory().selectFrom(hakukohdeViite).leftJoin(hakukohdeViite.valintaryhma).fetchJoin();
    if (StringUtils.isNotBlank(hakuOid)) {
      a.where(hakukohdeViite.hakuoid.eq(hakuOid));
    }
    if (tila != null && tila.size() > 0) {
      a.where(hakukohdeViite.tila.in(tila));
    }
    if (StringUtils.isNotBlank(searchString)) {
      a.where(hakukohdeViite.nimi.containsIgnoreCase(searchString));
    }
    return a.fetch();
  }

  @Override
  public List<HakukohdeViite> readByHakukohdekoodiUri(String koodiUri) {
    QHakukohdekoodi koodi = QHakukohdekoodi.hakukohdekoodi;
    QHakukohdeViite hk = QHakukohdeViite.hakukohdeViite;
    return queryFactory()
        .selectFrom(hk)
        .innerJoin(hk.hakukohdekoodi, koodi)
        .where(koodi.uri.eq(koodiUri))
        .distinct()
        .fetch();
  }

  @Override
  public Optional<Valintaryhma> findValintaryhmaByHakukohdeOid(String oid) {
    QHakukohdeViite hk = QHakukohdeViite.hakukohdeViite;
    final Optional<HakukohdeViite> hakukohdeViite =
        Optional.ofNullable(
            queryFactory()
                .selectFrom(hk)
                .leftJoin(hk.valintaryhma)
                .fetchJoin()
                .where(hk.oid.eq(oid))
                .fetchFirst());
    return Optional.ofNullable(hakukohdeViite.orElse(new HakukohdeViite()).getValintaryhma());
  }

  @Override
  public List<String> findNewOrChangedHakukohdeOids(
      LocalDateTime startDatetime, LocalDateTime endDatetime) {
    QHakukohdeViite hakukohde = QHakukohdeViite.hakukohdeViite;
    QValinnanVaihe vv = QValinnanVaihe.valinnanVaihe;
    QValintatapajono valintatapaJono = QValintatapajono.valintatapajono;
    QJarjestyskriteeri jk = QJarjestyskriteeri.jarjestyskriteeri;
    QValintakoe valintakoe = QValintakoe.valintakoe;
    QHakukohteenValintaperuste hkv = QHakukohteenValintaperuste.hakukohteenValintaperuste;
    Predicate whereCond =
        startDatetime != null
            ? hakukohde
                .lastModified
                .between(startDatetime, endDatetime)
                .or(vv.lastModified.between(startDatetime, endDatetime))
                .or(valintatapaJono.lastModified.between(startDatetime, endDatetime))
                .or(jk.lastModified.between(startDatetime, endDatetime))
                .or(valintakoe.lastModified.between(startDatetime, endDatetime))
                .or(hkv.lastModified.between(startDatetime, endDatetime))
            : hakukohde
                .lastModified
                .before(endDatetime)
                .or(vv.lastModified.before(endDatetime))
                .or(valintatapaJono.lastModified.before(endDatetime))
                .or(jk.lastModified.before(endDatetime))
                .or(valintakoe.lastModified.before(endDatetime))
                .or(hkv.lastModified.before(endDatetime));

    return queryFactory()
        .selectDistinct(hakukohde.oid)
        .from(hakukohde)
        .leftJoin(hakukohde.valinnanvaiheet, vv)
        .leftJoin(vv.jonot, valintatapaJono)
        .leftJoin(valintatapaJono.jarjestyskriteerit, jk)
        .leftJoin(vv.valintakokeet, valintakoe)
        .leftJoin(hakukohde.hakukohteenValintaperusteet, hkv)
        .where(whereCond)
        .fetch();
  }
}
