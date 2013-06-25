package fi.vm.sade.service.valintaperusteet.dao.impl;

/**
 * User: wuoti
 * Date: 24.6.2013
 * Time: 14.18
 */

import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.EntityPath;
import fi.vm.sade.generic.dao.AbstractJpaDAOImpl;
import fi.vm.sade.service.valintaperusteet.dao.OpetuskielikoodiDAO;
import fi.vm.sade.service.valintaperusteet.model.Opetuskielikoodi;
import fi.vm.sade.service.valintaperusteet.model.QOpetuskielikoodi;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class OpetuskielikoodiDAOImpl extends AbstractJpaDAOImpl<Opetuskielikoodi, Long> implements OpetuskielikoodiDAO {
    protected JPAQuery from(EntityPath<?>... o) {
        return new JPAQuery(getEntityManager()).from(o);
    }

    @Override
    public Opetuskielikoodi readByUri(String uri) {
        QOpetuskielikoodi opetuskielikoodi = QOpetuskielikoodi.opetuskielikoodi;
        return from(opetuskielikoodi)
                .where(opetuskielikoodi.uri.eq(uri))
                .singleResult(opetuskielikoodi);
    }

    @Override
    public List<Opetuskielikoodi> findByUris(String[] koodiUris) {
        if (koodiUris == null || koodiUris.length == 0) {
            return new ArrayList<Opetuskielikoodi>();
        }
        QOpetuskielikoodi koodi = QOpetuskielikoodi.opetuskielikoodi;

        return from(koodi)
                .where(koodi.uri.in(koodiUris))
                .list(koodi);
    }
}
