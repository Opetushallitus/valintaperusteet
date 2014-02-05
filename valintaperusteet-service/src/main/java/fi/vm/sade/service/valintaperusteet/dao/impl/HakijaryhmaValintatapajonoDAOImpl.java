package fi.vm.sade.service.valintaperusteet.dao.impl;

import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.jpa.impl.JPASubQuery;
import com.mysema.query.types.EntityPath;
import fi.vm.sade.service.valintaperusteet.dao.AbstractJpaDAOImpl;
import fi.vm.sade.service.valintaperusteet.dao.HakijaryhmaValintatapajonoDAO;
import fi.vm.sade.service.valintaperusteet.model.*;
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

        QHakijaryhma h = QHakijaryhma.hakijaryhma;
        QValintatapajono v = QValintatapajono.valintatapajono;

        return from(hv)
                .where(hv.oid.eq(oid))
                .leftJoin(hv.hakijaryhma, h).fetch()
                .leftJoin(h.jonot).fetch()
                .leftJoin(hv.valintatapajono, v).fetch()
                .leftJoin(v.hakijaryhmat).fetch()
                .leftJoin(v.valinnanVaihe).fetch()
                .leftJoin(hv.master).fetch()
                .leftJoin(hv.edellinen).fetch()
                .singleResult(hv);
    }

    @Override
    public List<HakijaryhmaValintatapajono> findByValintatapajono(String oid) {
        QHakijaryhmaValintatapajono hv = QHakijaryhmaValintatapajono.hakijaryhmaValintatapajono;

        QHakijaryhma h = QHakijaryhma.hakijaryhma;
        QValintatapajono v = QValintatapajono.valintatapajono;

        return from(hv).where(hv.valintatapajono.oid.eq(oid))
                .leftJoin(hv.hakijaryhma, h).fetch()
                .leftJoin(h.jonot).fetch()
                .leftJoin(hv.valintatapajono, v).fetch()
                .leftJoin(v.hakijaryhmat).fetch()
                .leftJoin(v.valinnanVaihe).fetch()
                .leftJoin(hv.master).fetch()
                .leftJoin(hv.edellinen).fetch()
                .listDistinct(hv);
    }

    @Override
    public List<HakijaryhmaValintatapajono> findByHakijaryhma(String hakijaryhmaOid) {
        QHakijaryhmaValintatapajono hv = QHakijaryhmaValintatapajono.hakijaryhmaValintatapajono;

        QHakijaryhma h = QHakijaryhma.hakijaryhma;
        QValintatapajono v = QValintatapajono.valintatapajono;

        return from(hv).where(hv.hakijaryhma.oid.eq(hakijaryhmaOid))
                .leftJoin(hv.hakijaryhma, h).fetch()
                .leftJoin(h.jonot).fetch()
                .leftJoin(hv.valintatapajono, v).fetch()
                .leftJoin(v.hakijaryhmat).fetch()
                .leftJoin(v.valinnanVaihe).fetch()
                .leftJoin(hv.master).fetch()
                .leftJoin(hv.edellinen).fetch()
                .listDistinct(hv);
    }
}
