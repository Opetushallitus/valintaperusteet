package fi.vm.sade.service.valintaperusteet.dao.impl;

import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.EntityPath;
import fi.vm.sade.generic.dao.AbstractJpaDAOImpl;
import fi.vm.sade.service.valintaperusteet.dao.ValintakoekoodiDAO;
import fi.vm.sade.service.valintaperusteet.model.QValintakoekoodi;
import fi.vm.sade.service.valintaperusteet.model.QValintaryhma;
import fi.vm.sade.service.valintaperusteet.model.Valintakoekoodi;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * User: kwuoti
 * Date: 15.4.2013
 * Time: 16.20
 */
@Repository
public class ValintakoekoodiDAOImpl extends AbstractJpaDAOImpl<Valintakoekoodi, Long> implements ValintakoekoodiDAO {

    protected JPAQuery from(EntityPath<?>... o) {
        return new JPAQuery(getEntityManager()).from(o);
    }

    @Override
    public List<Valintakoekoodi> findByValintaryhma(String valintaryhmaOid) {
        QValintaryhma valintaryhma = QValintaryhma.valintaryhma;
        QValintakoekoodi valintakoekoodi = QValintakoekoodi.valintakoekoodi;

        return from(valintaryhma)
                .innerJoin(valintaryhma.valintakoekoodit, valintakoekoodi)
                .where(valintaryhma.oid.eq(valintaryhmaOid))
                .list(valintakoekoodi);
    }

    @Override
    public Valintakoekoodi readByUri(String uri) {
        QValintakoekoodi valintakoekoodi = QValintakoekoodi.valintakoekoodi;
        return from(valintakoekoodi)
                .where(valintakoekoodi.uri.eq(uri))
                .singleResult(valintakoekoodi);
    }

    @Override
    public List<Valintakoekoodi> findByUris(String[] koodiUris) {
        if (koodiUris == null || koodiUris.length == 0) {
            return new ArrayList<Valintakoekoodi>();
        }
        QValintakoekoodi koodi = QValintakoekoodi.valintakoekoodi;

        return from(koodi)
                .where(koodi.uri.in(koodiUris))
                .list(koodi);
    }
}
