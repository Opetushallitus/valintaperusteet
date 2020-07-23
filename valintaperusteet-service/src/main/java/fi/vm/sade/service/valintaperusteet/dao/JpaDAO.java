package fi.vm.sade.service.valintaperusteet.dao;

import java.util.List;

public interface JpaDAO<E, ID> {

  /**
   * Reads single record from the database.
   *
   * @param key
   * @return
   */
  E read(ID key);

  /**
   * Updates an existing record.
   *
   * @param entity
   */
  void update(E entity);

  /**
   * Creates a new record to the database and flushes it.
   *
   * @param entity
   */
  E insert(E entity);

  /**
   * Creates a new record to the database.
   *
   * @param entity
   */
  E insert(E entity, boolean flush);

  /**
   * Removes existing record from the database.
   *
   * @param entity
   */
  void remove(E entity);

  /** finds all objects of dao's type from db */
  List<E> findAll();

  /**
   * Find.
   *
   * @param column
   * @param value
   * @return
   */
  List<E> findBy(String column, Object value);

  /**
   * Paged find.
   *
   * @param column
   * @param value
   * @param firstResultIndex
   * @param maxResults
   * @return
   */
  List<E> findBy(String column, Object value, int firstResultIndex, int maxResults);

  /**
   * Implement custom validation logic when needed.
   *
   * @param entity
   */
  void validate(E entity);

  /**
   * Deataches the given managed entity from session
   *
   * @param entity
   */
  void detach(E entity);
}
