package fi.vm.sade.service.valintaperusteet.dao.impl;

import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.jpa.impl.JPASubQuery;
import com.mysema.query.types.EntityPath;
import fi.vm.sade.generic.dao.AbstractJpaDAOImpl;
import fi.vm.sade.service.valintaperusteet.dao.ValintakoeDAO;
import fi.vm.sade.service.valintaperusteet.model.QValinnanVaihe;
import fi.vm.sade.service.valintaperusteet.model.QValintakoe;
import fi.vm.sade.service.valintaperusteet.model.Valintakoe;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * User: kwuoti
 * Date: 15.4.2013
 * Time: 16.20
 */
@Repository
public class ValintakoeDAOImpl extends AbstractJpaDAOImpl<Valintakoe, Long> implements ValintakoeDAO {
    @Override
    public List<Valintakoe> findByValinnanVaihe(String valinnanVaiheOid) {
        QValinnanVaihe valinnanVaihe = QValinnanVaihe.valinnanVaihe;
        QValintakoe valintakoe = QValintakoe.valintakoe;

        return from(valinnanVaihe)
                .leftJoin(valinnanVaihe.valintakokeet, valintakoe)
                .where(valinnanVaihe.oid.eq(valinnanVaiheOid))
                .distinct()
                .list(valintakoe);
    }

    protected JPAQuery from(EntityPath<?>... o) {
        return new JPAQuery(getEntityManager()).from(o);
    }

    protected JPASubQuery subQuery() {
        return new JPASubQuery();
    }


}
