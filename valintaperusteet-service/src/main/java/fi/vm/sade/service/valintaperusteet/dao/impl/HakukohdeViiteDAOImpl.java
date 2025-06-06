package fi.vm.sade.service.valintaperusteet.dao.impl;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import fi.vm.sade.service.valintaperusteet.dao.AbstractJpaDAOImpl;
import fi.vm.sade.service.valintaperusteet.dao.HakukohdeViiteDAO;
import fi.vm.sade.service.valintaperusteet.dto.HakukohdeKoosteTietoDTO;
import fi.vm.sade.service.valintaperusteet.model.*;
import java.time.LocalDateTime;
import java.util.*;
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
  public List<HakukohdeViite> haunHakukohteet(String hakuOid, Boolean vainValintakokeelliset) {
    QHakukohdeViite hakukohdeViite = QHakukohdeViite.hakukohdeViite;
    QValintaryhma valintaryhma = QValintaryhma.valintaryhma;

    return queryFactory()
        .selectFrom(hakukohdeViite)
        .leftJoin(hakukohdeViite.valintaryhma, valintaryhma)
        .fetchJoin()
        .leftJoin(hakukohdeViite.hakukohdekoodi)
        .fetchJoin()
        .where(
            hakukohdeViite.hakuoid.eq(hakuOid),
            vainValintakokeelliset ? hakukohdeViite.valintakokeet.isNotEmpty() : null)
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
  public List<HakukohdeKoosteTietoDTO> haunHakukohdeTiedot(String hakuOid) {
    QHakukohdeViite hakukohdeViite = QHakukohdeViite.hakukohdeViite;
    QValinnanVaihe vv = QValinnanVaihe.valinnanVaihe;
    QValintatapajono jono = QValintatapajono.valintatapajono;
    DateTimeExpression<Date> varasijatayttoPaattyy = jono.varasijojaTaytetaanAsti.max();
    BooleanExpression hasValintakoe = hakukohdeViite.valintakokeet.size().max().gt(0);
    List<Tuple> result =
        queryFactory()
            .query()
            .select(hakukohdeViite.oid, varasijatayttoPaattyy, hasValintakoe)
            .from(hakukohdeViite)
            .leftJoin(hakukohdeViite.valinnanvaiheet, vv)
            .on(vv.aktiivinen.eq(true))
            .leftJoin(vv.jonot, jono)
            .on(jono.aktiivinen.eq(true))
            .where(hakukohdeViite.hakuoid.eq(hakuOid))
            .groupBy(hakukohdeViite.oid)
            .fetch();

    return result.stream()
        .map(
            t ->
                new HakukohdeKoosteTietoDTO(
                    t.get(hakukohdeViite.oid), t.get(hasValintakoe), t.get(varasijatayttoPaattyy)))
        .toList();
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

  private String lastModified(String tablePrefix) {
    String lastModifiedField =
        tablePrefix.isEmpty() ? "last_modified" : String.format("%s.last_modified", tablePrefix);
    return String.format(
        " %s >= :startDatetime and %s < :endDatetime", lastModifiedField, lastModifiedField);
  }

  private String lastModifiedHistory(String tablePrefix) {
    String lastModifiedField =
        tablePrefix.isEmpty() ? "update_time" : String.format("%s.update_time", tablePrefix);
    return String.format(
        " %s >= :startDatetime and %s < :endDatetime", lastModifiedField, lastModifiedField);
  }

  @Override
  public List<String> findNewOrChangedHakukohdeOids(LocalDateTime start, LocalDateTime end) {
    // JPA does not support unions :(
    String sql =
        """
      select hakukohde_oid from (
        select oid as hakukohde_oid from hakukohde_viite
        where %s
                  union
        select hv.oid as hakukohde_oid from hakukohde_viite hv
          left join valinnan_vaihe vv on vv.hakukohde_viite_id = hv.id
        where %s
                  union
        select hv.oid as hakukohde_oid from hakukohde_viite hv
          left join valinnan_vaihe vv on vv.hakukohde_viite_id = hv.id
          left join valintatapajono vtj on vtj.valinnan_vaihe_id = vv.id
        where %s
                  union
        select hv.oid as hakukohde_oid from hakukohde_viite hv
          left join valinnan_vaihe vv on vv.hakukohde_viite_id = hv.id
          left join valintatapajono vtj on vtj.valinnan_vaihe_id = vv.id
          left join jarjestyskriteeri jk on jk.valintatapajono_id = vtj.id
        where %s
                  union
        select hv.oid as hakukohde_oid from hakukohde_viite hv
          left join valinnan_vaihe vv on vv.hakukohde_viite_id = hv.id
          left join valintakoe vk on vk.valinnan_vaihe_id = vv.id
        where %s
                  union
        select hv.oid as hakukohde_oid from hakukohde_viite hv
          left join hakukohteen_valintaperuste hva on hva.hakukohde_viite_id = hv.id
        where %s
                  union
        select oid as hakukohde_oid from hakukohde_viite_history
        where %s
                  union
        select hv.oid as hakukohde_oid from hakukohde_viite hv
          left join valinnan_vaihe_history vvh on vvh.hakukohde_viite_id = hv.id
        where %s
                  union
        select hv.oid as hakukohde_oid from hakukohde_viite hv
          left join valinnan_vaihe vv on vv.hakukohde_viite_id = hv.id
          left join valintatapajono_history vtjh on vtjh.valinnan_vaihe_id = vv.id
        where %s
                  union
        select hv.oid as hakukohde_oid from hakukohde_viite hv
          left join valinnan_vaihe vv on vv.hakukohde_viite_id = hv.id
          left join valintatapajono vtj on vtj.valinnan_vaihe_id = vv.id
          left join jarjestyskriteeri_history jkh on jkh.valintatapajono_id = vtj.id
        where %s
                  union
        select hv.oid as hakukohde_oid from hakukohde_viite hv
          left join valinnan_vaihe vv on vv.hakukohde_viite_id = hv.id
          left join valintakoe_history vkh on vkh.valinnan_vaihe_id = vv.id
        where %s
                  union
        select hv.oid as hakukohde_oid from hakukohde_viite hv
          left join hakukohteen_valintaperuste_history hvah on hvah.hakukohde_viite_id = hv.id
        where %s
      ) hvs
      """;
    sql =
        String.format(
            sql,
            lastModified(""),
            lastModified("vv"),
            lastModified("vtj"),
            lastModified("jk"),
            lastModified("vk"),
            lastModified("hva"),
            lastModifiedHistory(""),
            lastModifiedHistory("vvh"),
            lastModifiedHistory("vtjh"),
            lastModifiedHistory("jkh"),
            lastModifiedHistory("vkh"),
            lastModifiedHistory("hvah"));

    jakarta.persistence.Query query = getEntityManager().createNativeQuery(sql);
    query = query.setParameter("startDatetime", start).setParameter("endDatetime", end);
    return (List<String>) query.getResultList();
  }
}
