package fi.vm.sade.service.valintaperusteet.dao;

import fi.vm.sade.generic.dao.JpaDAO;
import fi.vm.sade.service.valintaperusteet.model.HakukohteenValintaperuste;

import java.util.List;

/**
 * User: wuoti
 * Date: 12.9.2013
 * Time: 13.46
 */
public interface HakukohteenValintaperusteDAO extends JpaDAO<HakukohteenValintaperuste, Long> {
    List<HakukohteenValintaperuste> haeHakukohteenValintaperusteet(String hakukohdeOid);
}
