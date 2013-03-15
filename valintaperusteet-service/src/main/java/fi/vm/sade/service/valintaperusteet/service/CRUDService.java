package fi.vm.sade.service.valintaperusteet.service;

/**
 * User: tommiha
 * Date: 1/18/13
 * Time: 11:04 AM
 */
public interface CRUDService<E, ID, OID> {


    E read(ID key);

    /**
     * Updates an existing record.
     * @param entity
     */
    E update(OID oid, E entity);

    /**
     * Creates a new record to the database.
     * @param entity
     */
    E insert(E entity);

    /**
     * Removes existing record from the database.
     * @param entity
     */
    void delete(E entity);

    /**
     * Removes existing record from the database with ID.
     * @param id
     */
    void deleteById(ID id);
}
