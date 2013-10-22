package fi.vm.sade.service.valintaperusteet.dao.impl;

import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.jpa.impl.JPASubQuery;
import com.mysema.query.types.EntityPath;
import fi.vm.sade.generic.dao.AbstractJpaDAOImpl;
import fi.vm.sade.service.valintaperusteet.dao.HakijaryhmaValintatapajonoDAO;
import fi.vm.sade.service.valintaperusteet.model.HakijaryhmaValintatapajono;
import fi.vm.sade.service.valintaperusteet.model.QHakijaryhmaValintatapajono;
import org.springframework.stereotype.Repository;

/**
 * Created with IntelliJ IDEA.
 * User: jukais
 * Date: 21.10.2013
 * Time: 12.53
 * To change this template use File | Settings | File Templates.
 */
@Repository
public class HakijaryhmaValintatapajonoDAOImpl extends AbstractJpaDAOImpl<HakijaryhmaValintatapajono, Long> implements HakijaryhmaValintatapajonoDAO {
    protected JPAQuery from(EntityPath<?>... o) {
        return new JPAQuery(getEntityManager()).from(o);
    }

    protected JPASubQuery subQuery() {
        return new JPASubQuery();
    }

    @Override
    public HakijaryhmaValintatapajono readByOid(String oid) {
        QHakijaryhmaValintatapajono hv = QHakijaryhmaValintatapajono.hakijaryhmaValintatapajono;

        return from(hv)
                .where(hv.oid.eq(oid))
                .singleResult(hv);
    }
}
