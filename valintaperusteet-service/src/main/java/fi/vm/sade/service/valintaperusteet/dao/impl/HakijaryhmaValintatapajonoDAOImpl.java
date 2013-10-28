package fi.vm.sade.service.valintaperusteet.dao.impl;

import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.jpa.impl.JPASubQuery;
import com.mysema.query.types.EntityPath;
import fi.vm.sade.generic.dao.AbstractJpaDAOImpl;
import fi.vm.sade.service.valintaperusteet.dao.HakijaryhmaValintatapajonoDAO;
import fi.vm.sade.service.valintaperusteet.model.HakijaryhmaValintatapajono;
import fi.vm.sade.service.valintaperusteet.model.QHakijaryhma;
import fi.vm.sade.service.valintaperusteet.model.QHakijaryhmaValintatapajono;
import org.springframework.stereotype.Repository;

import java.util.List;

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
                .leftJoin(hv.hakijaryhma).fetch()
                .leftJoin(hv.master).fetch()
                .leftJoin(hv.edellinen).fetch()
                .singleResult(hv);
    }

    @Override
    public List<HakijaryhmaValintatapajono> findByValintatapajono(String oid) {
        QHakijaryhmaValintatapajono hakijaryhmaValintatapajono = QHakijaryhmaValintatapajono.hakijaryhmaValintatapajono;

        return from(hakijaryhmaValintatapajono).where(hakijaryhmaValintatapajono.valintatapajono.oid.eq(oid))
                .leftJoin(hakijaryhmaValintatapajono.hakijaryhma).fetch()
                .leftJoin(hakijaryhmaValintatapajono.master).fetch()
                .leftJoin(hakijaryhmaValintatapajono.edellinen).fetch()
                .listDistinct(hakijaryhmaValintatapajono);
    }
}
