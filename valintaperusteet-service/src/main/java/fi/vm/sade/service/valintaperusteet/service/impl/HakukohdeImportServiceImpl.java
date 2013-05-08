package fi.vm.sade.service.valintaperusteet.service.impl;

import fi.vm.sade.service.valintaperusteet.dao.HakukohdeViiteDAO;
import fi.vm.sade.service.valintaperusteet.dao.HakukohdekoodiDAO;
import fi.vm.sade.service.valintaperusteet.dao.ValintaryhmaDAO;
import fi.vm.sade.service.valintaperusteet.model.HakukohdeViite;
import fi.vm.sade.service.valintaperusteet.model.Hakukohdekoodi;
import fi.vm.sade.service.valintaperusteet.schema.HakukohdeImportTyyppi;
import fi.vm.sade.service.valintaperusteet.service.HakukohdeImportService;
import fi.vm.sade.service.valintaperusteet.service.HakukohdeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * User: wuoti
 * Date: 8.5.2013
 * Time: 13.38
 */
@Service
public class HakukohdeImportServiceImpl implements HakukohdeImportService {

    @Autowired
    private HakukohdeViiteDAO hakukohdeViiteDAO;

    @Autowired
    private HakukohdeService hakukohdeService;

    @Autowired
    private ValintaryhmaDAO valintaryhmaDAO;

    @Autowired
    private HakukohdekoodiDAO hakukohdekoodiDAO;

    @Override
    public void tuoHakukohde(HakukohdeImportTyyppi importData) {
        HakukohdeViite hakukohde = hakukohdeViiteDAO.readByOid(importData.getHakukohdeOid());
        if(hakukohde == null) {
            hakukohde = new HakukohdeViite();
            hakukohde.setHakuoid(importData.getHakuOid());
            hakukohde.setOid(importData.getHakukohdeOid());

            Hakukohdekoodi koodi = hakukohdekoodiDAO.findByKoodiUri(importData.getHakukohdekoodi().getKoodiUri());
            if(koodi == null) {

            }

        } else {
            // TODO:
        }
    }
}
