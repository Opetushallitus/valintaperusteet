package fi.vm.sade.service.valintaperusteet.dao;

import fi.vm.sade.service.valintaperusteet.model.HakukohteenValintaperuste;

import java.util.List;

public interface HakukohteenValintaperusteDAO extends JpaDAO<HakukohteenValintaperuste, Long> {

    List<HakukohteenValintaperuste> haeHakukohteenValintaperusteet(String hakukohdeOid);

    List<HakukohteenValintaperuste> haeHakukohteidenValintaperusteet(List<String> hakukohdeOidit);
}
