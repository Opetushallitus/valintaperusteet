package fi.vm.sade.service.valintaperusteet.dao.impl;

import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.EntityPath;
import fi.vm.sade.service.valintaperusteet.dao.AbstractJpaDAOImpl;
import fi.vm.sade.service.valintaperusteet.dao.HakukohteenValintaperusteDAO;
import fi.vm.sade.service.valintaperusteet.model.HakukohteenValintaperuste;
import fi.vm.sade.service.valintaperusteet.model.QHakukohdeViite;
import fi.vm.sade.service.valintaperusteet.model.QHakukohteenValintaperuste;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * User: wuoti
 * Date: 12.9.2013
 * Time: 13.45
 */
@Repository
public class HakukohteenValintaperusteDAOImpl extends AbstractJpaDAOImpl<HakukohteenValintaperuste, Long> implements HakukohteenValintaperusteDAO {

    protected JPAQuery from(EntityPath<?>... o) {
        return new JPAQuery(getEntityManager()).from(o);
    }

    @Override
    public List<HakukohteenValintaperuste> haeHakukohteenValintaperusteet(String hakukohdeOid) {
        QHakukohdeViite hk = QHakukohdeViite.hakukohdeViite;
        QHakukohteenValintaperuste vp = QHakukohteenValintaperuste.hakukohteenValintaperuste;

        return from(hk)
                .innerJoin(hk.hakukohteenValintaperusteet, vp)
                .where(hk.oid.eq(hakukohdeOid))
                .list(vp);
    }
}
