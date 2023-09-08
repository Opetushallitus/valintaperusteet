package fi.vm.sade.service.valintaperusteet.dao.impl;

import fi.vm.sade.service.valintaperusteet.dao.GenericDAO;
import fi.vm.sade.service.valintaperusteet.model.BaseEntity;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.springframework.stereotype.Repository;

@Repository
public class GenericDAOImpl implements GenericDAO {
  @PersistenceContext private EntityManager entityManager;

  public GenericDAOImpl() {}

  public <E extends BaseEntity> E read(Class<E> entity, Long key) {
    return entityManager.find(entity, key);
  }

  public <E extends BaseEntity> void update(E entity) {
    validate(entity);
    entityManager.merge(entity);
    entityManager.flush();
  }

  @Override
  public <E extends BaseEntity> E insert(E entity) {
    return insert(entity, true);
  }

  @Override
  public <E extends BaseEntity> E insert(E entity, boolean flush) {
    validate(entity);
    entityManager.persist(entity);
    if (flush) {
      entityManager.flush();
    }
    return entity;
  }

  public <E extends BaseEntity> void remove(E entity) {
    entityManager.remove(entity);
  }

  @SuppressWarnings("unchecked")
  public <E extends BaseEntity> List<E> findAll(Class<E> entity) {
    Query query = entityManager.createQuery("SELECT e FROM " + entity.getSimpleName() + " e");
    return query.getResultList();
  }

  @SuppressWarnings("unchecked")
  public <E extends BaseEntity> List<E> findBy(Class<E> entity, String column, Object value) {
    Query query =
        getEntityManager()
            .createQuery(
                "SELECT e FROM " + entity.getSimpleName() + " e WHERE e." + column + " = :value");
    query.setParameter("value", value);
    return query.getResultList();
  }

  public EntityManager getEntityManager() {
    return entityManager;
  }

  public void setEntityManager(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  // @Override
  public <E extends BaseEntity> void validate(E entity) {
    // empty, implemenet custom validation logic where needed
  }

  // @Override
  public <E extends BaseEntity> void detach(E entity) {
    entityManager.detach(entity);
  }

  // @Override
  public void flush() {
    entityManager.flush();
  }
}
