/**
 *
 */
package fi.vm.sade.service.valintaperusteet.service.impl;

import fi.vm.sade.generic.dao.JpaDAO;
import fi.vm.sade.service.valintaperusteet.service.CRUDService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author tommiha
 *
 */
@Transactional
public abstract class AbstractCRUDServiceImpl<E, ID, OID> implements CRUDService<E, ID, OID> {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private JpaDAO<E, ID> dao;

    public AbstractCRUDServiceImpl(JpaDAO<E, ID> dao) {
        this.dao = dao;
    }

    public JpaDAO<E, ID> getDao() {
        return dao;
    }

    /*
     * (non-Javadoc)
     * @see fi.vm.sade.generic.service.CRUDService#read(java.lang.Object)
     */
    @Transactional(readOnly=true)
    public E read(ID key) {
        log.debug("Reading record by primary key: " + key);
        return dao.read(key);
    }

    /*
     * (non-Javadoc)
     * @see fi.vm.sade.generic.service.CRUDService#update(java.lang.Object)
     */
    public abstract E update(OID oid, E entity);

    /*
     * (non-Javadoc)
     * @see fi.vm.sade.generic.service.CRUDService#insert(java.lang.Object)
     */
    public abstract E insert(E entity) ;


    /*
     * (non-Javadoc)
     * @see fi.vm.sade.generic.service.CRUDService#delete(java.lang.Object)
     */
    public void delete(E entity) {
        log.debug("Deleting record: " + entity);
        dao.remove(entity);
    }

    /*
     * (non-Javadoc)
     * @see fi.vm.sade.generic.service.CRUDService#deleteById(java.lang.Object)
     */
    public void deleteById(ID id) {
        E entity = dao.read(id);
        log.debug("Deleting record: " + entity);
        dao.remove(entity);
    }
}
