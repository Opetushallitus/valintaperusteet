package fi.vm.sade.service.valintaperusteet.dao.impl;

import com.mysema.query.SearchResults;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.EntityPath;
import fi.vm.sade.service.valintaperusteet.dao.AbstractJpaDAOImpl;
import fi.vm.sade.service.valintaperusteet.dao.HakukohteenValintaperusteDAO;
import fi.vm.sade.service.valintaperusteet.model.HakukohdeViite;
import fi.vm.sade.service.valintaperusteet.model.HakukohteenValintaperuste;
import fi.vm.sade.service.valintaperusteet.model.QHakukohdeViite;
import fi.vm.sade.service.valintaperusteet.model.QHakukohteenValintaperuste;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class HakukohteenValintaperusteDAOImpl extends AbstractJpaDAOImpl<HakukohteenValintaperuste, Long> implements HakukohteenValintaperusteDAO {

    protected JPAQuery from(EntityPath<?>... o) {
        return new JPAQuery(getEntityManager()).from(o);
    }

    @Override
    public List<HakukohteenValintaperuste> haeHakukohteenValintaperusteet(String hakukohdeOid) {
        QHakukohdeViite hk = QHakukohdeViite.hakukohdeViite;
        QHakukohteenValintaperuste vp = QHakukohteenValintaperuste.hakukohteenValintaperuste;
        HakukohdeViite viite = from(hk).where(hk.oid.eq(hakukohdeOid)).singleResult(hk);
        if (viite == null) {
            return new ArrayList<>();
        }
        return from(vp)
                .where(vp.hakukohde.eq(viite))
                .list(vp);
    }

    @Override
    public List<HakukohteenValintaperuste> haeHakukohteidenValintaperusteet(List<String> hakukohdeOidit) {
        QHakukohdeViite hk = QHakukohdeViite.hakukohdeViite;
        QHakukohteenValintaperuste vp = QHakukohteenValintaperuste.hakukohteenValintaperuste;
        List<HakukohdeViite> viite = from(hk).where(hk.oid.in(hakukohdeOidit)).listResults(hk).getResults();
        if (viite == null || viite.isEmpty()) {
            return new ArrayList<>();
        }
        return from(vp)
                .where(vp.hakukohde.in(viite))
                .list(vp);
    }
}
