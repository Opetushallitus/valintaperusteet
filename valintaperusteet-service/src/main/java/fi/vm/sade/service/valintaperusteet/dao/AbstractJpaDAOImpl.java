package fi.vm.sade.service.valintaperusteet.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.lang.reflect.ParameterizedType;
import java.util.List;

public abstract class AbstractJpaDAOImpl<E, ID> implements JpaDAO<E, ID> {

  private EntityManager entityManager;
  private Class<E> entityClass;

  @SuppressWarnings("unchecked")
  public AbstractJpaDAOImpl() {
    entityClass =
        (Class<E>)
            ((ParameterizedType) (getClass().getGenericSuperclass())).getActualTypeArguments()[0];
  }

  public EntityManager getEntityManager() {
    return entityManager;
  }

  @PersistenceContext
  public void setEntityManager(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  public E read(ID key) {
    return entityManager.find(entityClass, key);
  }

  public void update(E entity) {
    validate(entity);
    entityManager.merge(entity);
    entityManager.flush();
  }

  public E insert(E entity) {
    // Database must be synchronized at this point or the insert query does not get executed at all
    // (no id)
    return insert(entity, true);
  }

  public E insert(E entity, boolean flush) {
    validate(entity);
    entityManager.persist(entity);
    if (flush) {
      entityManager.flush();
    }
    return entity;
  }

  public void remove(E entity) {
    entityManager.remove(entity);
  }

  public List<E> findAll() {
    Query query =
        getEntityManager().createQuery("SELECT x FROM " + entityClass.getSimpleName() + " x");
    return query.getResultList();
  }

  @Override
  public List<E> findBy(String column, Object value) {
    return findBy(column, value, 0, Integer.MAX_VALUE);
  }

  @Override
  public List<E> findBy(String column, Object value, int startIndex, int maxResults) {
    Query query =
        getEntityManager()
            .createQuery(
                "SELECT x FROM "
                    + entityClass.getSimpleName()
                    + " x WHERE x."
                    + column
                    + " = :value");
    query.setParameter("value", value);
    query.setFirstResult(startIndex);
    query.setMaxResults(maxResults);

    return query.getResultList();
  }

  @Override
  public void validate(E entity) {
    // empty, override to implement custom validation logic
  }

  @Override
  public void detach(E entity) {
    getEntityManager().detach(entity);
  }
}
