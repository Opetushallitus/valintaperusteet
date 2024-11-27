package fi.vm.sade.service.valintaperusteet.dao.impl;

import fi.vm.sade.service.valintaperusteet.dao.PoistettuDAO;
import fi.vm.sade.service.valintaperusteet.model.Poistettu;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Repository;

@Repository
public class PoistettuDAOImpl implements PoistettuDAO {
  @PersistenceContext private EntityManager entityManager;
  private static final Logger LOG = LoggerFactory.getLogger(PoistettuDAOImpl.class);

  @Override
  public List<Poistettu> findPoistetutHakukohdeViitteet(LocalDateTime start, LocalDateTime end) {
    return doFindPoistetut("hakukohde_viite", "hakukohde_viite_history", "null", "oid", start, end);
  }

  @Override
  public List<Poistettu> findPoistetutValinnanvaiheet(LocalDateTime start, LocalDateTime end) {
    return doFindPoistetut(
        "valinnan_vaihe", "valinnan_vaihe_history", "hakukohde_viite_id", "oid", start, end);
  }

  @Override
  public List<Poistettu> findPoistetutValintatapajonot(LocalDateTime start, LocalDateTime end) {
    return doFindPoistetut(
        "valintatapajono", "valintatapajono_history", "valinnan_vaihe_id", "oid", start, end);
  }

  @Override
  public List<Poistettu> findPoistetutValintakokeet(LocalDateTime start, LocalDateTime end) {
    return doFindPoistetut(
        "valintakoe", "valintakoe_history", "valinnan_vaihe_id", "tunniste", start, end);
  }

  @Override
  public List<Poistettu> findPoistetutValintaperusteet(LocalDateTime start, LocalDateTime end) {
    return doFindPoistetut(
        "hakukohteen_valintaperuste",
        "hakukohteen_valintaperuste_history",
        "hakukohde_viite_id",
        "tunniste",
        start,
        end);
  }

  private List<Poistettu> doFindPoistetut(
      String tableName,
      String historyTableName,
      String parentIdFieldName,
      String tunnisteFieldName,
      LocalDateTime start,
      LocalDateTime end) {
    String sqlTemplate =
        """
        select distinct on (id) id, %s as parentId, %s from %s hist
          where upper(hist.system_time) is not null and
            upper(hist.system_time) >= :startDateTime and
            upper(hist.system_time) <  :endDateTime and
            not exists (select 1 from %s where id = hist.id)""";
    String tunnisteSelectPart =
        "tunniste".equals(tunnisteFieldName)
            ? "tunniste"
            : String.format("%s as tunniste", tunnisteFieldName);
    String sql =
        String.format(
            sqlTemplate, parentIdFieldName, tunnisteSelectPart, historyTableName, tableName);
    Query query =
        entityManager
            .createNativeQuery(sql)
            .setParameter("startDateTime", start)
            .setParameter("endDateTime", end);
    @SuppressWarnings("unchecked")
    List<Object[]> resultList = (List<Object[]>) query.getResultList();
    return resultList.stream().map(Poistettu::new).collect(Collectors.toList());
  }

  @Override
  public List<Poistettu> findParentHakukohdeviitteet(Collection<Long> ids) {
    return doFindParents("hakukohde_viite", "null", "hakuoid", ids);
  }

  @Override
  public List<Poistettu> findParentHakukohdeviitteetFromHistory(Collection<Long> ids) {
    return doFindParents("hakukohde_viite_history", "null", "hakuoid", ids);
  }

  @Override
  public List<Poistettu> findParentValinnanvaiheet(Collection<Long> ids) {
    return doFindParents("valinnan_vaihe", "hakukohde_viite_id", "oid", ids);
  }

  @Override
  public List<Poistettu> findParentValinnanvaiheetFromHistory(Collection<Long> ids) {
    return doFindParents("valinnan_vaihe_history", "hakukohde_viite_id", "oid", ids);
  }

  private List<Poistettu> doFindParents(
      String tableName, String parentIdFieldName, String tunnisteFieldName, Collection<Long> ids) {
    try {
      Collection<Long> nonNullIds = ids.stream().filter(Objects::nonNull).toList();
      String sqlTemplate =
          """
              select distinct on (id) id, %s as parentId, %s as tunniste from %s
              where id in (:ids)""";
      String sql = String.format(sqlTemplate, parentIdFieldName, tunnisteFieldName, tableName);
      Query query = entityManager.createNativeQuery(sql).setParameter("ids", nonNullIds);
      @SuppressWarnings("unchecked")
      List<Object[]> resultList = (List<Object[]>) query.getResultList();
      return resultList.stream()
          .map(Poistettu::new)
          .map(p -> p.setDeletedItself(false))
          .collect(Collectors.toList());
    } catch (Exception e) {
      LOG.error(
          "Virhe haettaessa tietoja parametreille {}, {}, {}, {}:",
          tableName,
          parentIdFieldName,
          tunnisteFieldName,
          ids,
          e);
      throw e;
    }
  }
}
