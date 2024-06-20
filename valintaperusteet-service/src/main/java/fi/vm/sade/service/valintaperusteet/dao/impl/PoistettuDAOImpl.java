package fi.vm.sade.service.valintaperusteet.dao.impl;

import fi.vm.sade.service.valintaperusteet.dao.PoistettuDAO;
import fi.vm.sade.service.valintaperusteet.model.Poistettu;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.*;
import org.springframework.stereotype.Repository;

@Repository
public class PoistettuDAOImpl implements PoistettuDAO {
  @PersistenceContext private EntityManager entityManager;

  @Override
  public List<Poistettu> findPoistetutHakukohdeViitteet(LocalDateTime start, LocalDateTime end) {
    Query query =
        entityManager
            .createNativeQuery(
                """
      select distinct on (id) id, null as parentId, oid as tunniste from hakukohde_viite_history hvh
        where upper(hvh.system_time) is not null and
          upper(hvh.system_time) >= :startDateTime and
          upper(hvh.system_time) <  :endDateTime and
          not exists (select 1 from hakukohde_viite where id = hvh.id)""")
            .setParameter("startDateTime", start)
            .setParameter("endDateTime", end);
    @SuppressWarnings("unchecked")
    List<Object[]> resultList = (List<Object[]>) query.getResultList();
    return resultList.stream().map(Poistettu::new).collect(Collectors.toList());
  }

  @Override
  public List<Poistettu> findPoistetutValinnanvaiheet(LocalDateTime start, LocalDateTime end) {
    Query query =
        entityManager
            .createNativeQuery(
                """
      select distinct on (id) id, hakukohde_viite_id as parentId, oid as tunniste from valinnan_vaihe_history vvh
        where upper(vvh.system_time) is not null and
          upper(vvh.system_time) >= :startDateTime and
          upper(vvh.system_time) <  :endDateTime and
          not exists (select 1 from valinnan_vaihe where id = vvh.id)""")
            .setParameter("startDateTime", start)
            .setParameter("endDateTime", end);
    @SuppressWarnings("unchecked")
    List<Object[]> resultList = (List<Object[]>) query.getResultList();
    return resultList.stream().map(Poistettu::new).collect(Collectors.toList());
  }

  @Override
  public List<Poistettu> findPoistetutValintatapajonot(LocalDateTime start, LocalDateTime end) {
    Query query =
        entityManager
            .createNativeQuery(
                """
      select distinct on (id) id, valinnan_vaihe_id as parentId, oid as tunniste from valintatapajono_history vtjh
        where upper(vtjh.system_time) is not null and
          upper(vtjh.system_time) >= :startDateTime and
          upper(vtjh.system_time) <  :endDateTime and
          not exists (select 1 from valintatapajono where id = vtjh.id)""")
            .setParameter("startDateTime", start)
            .setParameter("endDateTime", end);
    @SuppressWarnings("unchecked")
    List<Object[]> resultList = (List<Object[]>) query.getResultList();
    return resultList.stream().map(Poistettu::new).collect(Collectors.toList());
  }

  @Override
  public List<Poistettu> findPoistetutValintakokeet(LocalDateTime start, LocalDateTime end) {
    Query query =
        entityManager
            .createNativeQuery(
                """
      select distinct on (id) id, valinnan_vaihe_id as parentId, oid as tunniste from valintakoe_history vkh
        where upper(vkh.system_time) is not null and
          upper(vkh.system_time) >= :startDateTime and
          upper(vkh.system_time) <  :endDateTime and
          not exists (select 1 from valintakoe where id = vkh.id)""")
            .setParameter("startDateTime", start)
            .setParameter("endDateTime", end);
    @SuppressWarnings("unchecked")
    List<Object[]> resultList = (List<Object[]>) query.getResultList();
    return resultList.stream().map(Poistettu::new).collect(Collectors.toList());
  }

  @Override
  public List<Poistettu> findPoistetutValintaperusteet(LocalDateTime start, LocalDateTime end) {
    Query query =
        entityManager
            .createNativeQuery(
                """
      select distinct on (id) id, hakukohde_viite_id as parentId, tunniste from hakukohteen_valintaperuste_history hkvh
        where upper(hkvh.system_time) is not null and
          upper(hkvh.system_time) >= :startDateTime and
          upper(hkvh.system_time) <  :endDateTime and
          not exists (select 1 from hakukohteen_valintaperuste where id = hkvh.id)""")
            .setParameter("startDateTime", start)
            .setParameter("endDateTime", end);
    @SuppressWarnings("unchecked")
    List<Object[]> resultList = (List<Object[]>) query.getResultList();
    return resultList.stream().map(Poistettu::new).collect(Collectors.toList());
  }

  @Override
  public List<Poistettu> findHakukohdeviitteetFromHistory(Collection<Long> ids) {
    Query query =
        entityManager
            .createNativeQuery(
                """
        select distinct on (id) id, null as parentId, hakuoid as tunniste from hakukohde_viite_history
        where id in (:ids)
        """)
            .setParameter("ids", ids);
    @SuppressWarnings("unchecked")
    List<Object[]> resultList = (List<Object[]>) query.getResultList();
    return resultList.stream().map(Poistettu::new).collect(Collectors.toList());
  }

  @Override
  public List<Poistettu> findValinnanvaiheetFromHistory(Collection<Long> ids) {
    Query query =
        entityManager
            .createNativeQuery(
                """
        select distinct on (id) id, hakukohde_viite_id as parentId, hakuoid as tunniste from hakukohde_viite_history
        where id in (:ids)
        """)
            .setParameter("ids", ids);
    @SuppressWarnings("unchecked")
    List<Object[]> resultList = (List<Object[]>) query.getResultList();
    return resultList.stream().map(Poistettu::new).collect(Collectors.toList());
  }
}
