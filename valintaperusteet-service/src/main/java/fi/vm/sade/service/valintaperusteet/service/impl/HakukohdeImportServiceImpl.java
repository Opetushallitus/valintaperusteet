package fi.vm.sade.service.valintaperusteet.service.impl;

import fi.vm.sade.service.valintaperusteet.dao.HakukohdeViiteDAO;
import fi.vm.sade.service.valintaperusteet.dao.HakukohdekoodiDAO;
import fi.vm.sade.service.valintaperusteet.dao.ValintaryhmaDAO;
import fi.vm.sade.service.valintaperusteet.model.HakukohdeViite;
import fi.vm.sade.service.valintaperusteet.model.Hakukohdekoodi;
import fi.vm.sade.service.valintaperusteet.model.ValinnanVaihe;
import fi.vm.sade.service.valintaperusteet.model.Valintaryhma;
import fi.vm.sade.service.valintaperusteet.schema.HakukohdeImportTyyppi;
import fi.vm.sade.service.valintaperusteet.schema.HakukohdekoodiTyyppi;
import fi.vm.sade.service.valintaperusteet.service.HakukohdeImportService;
import fi.vm.sade.service.valintaperusteet.service.HakukohdeService;
import fi.vm.sade.service.valintaperusteet.service.ValinnanVaiheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * User: wuoti
 * Date: 8.5.2013
 * Time: 13.38
 */
@Service
@Transactional
public class HakukohdeImportServiceImpl implements HakukohdeImportService {

    @Autowired
    private HakukohdeViiteDAO hakukohdeViiteDAO;

    @Autowired
    private HakukohdeService hakukohdeService;

    @Autowired
    private ValintaryhmaDAO valintaryhmaDAO;

    @Autowired
    private HakukohdekoodiDAO hakukohdekoodiDAO;

    @Autowired
    private ValinnanVaiheService valinnanVaiheService;

    protected void convertKoodi(HakukohdekoodiTyyppi from, Hakukohdekoodi to) {
        to.setArvo(from.getArvo());
        to.setUri(from.getKoodiUri());
        to.setNimiFi(from.getNimiFi());
        to.setNimiSv(from.getNimiSv());
        to.setNimiEn(from.getNimiEn());
    }

    @Override
    public void tuoHakukohde(HakukohdeImportTyyppi importData) {
        HakukohdekoodiTyyppi koodiTyyppi = importData.getHakukohdekoodi();

        HakukohdeViite hakukohde = hakukohdeViiteDAO.readByOid(importData.getHakukohdeOid());
        Hakukohdekoodi koodi = hakukohdekoodiDAO.findByKoodiUri(koodiTyyppi.getKoodiUri());

        if (koodi == null) {
            koodi = new Hakukohdekoodi();
            convertKoodi(koodiTyyppi, koodi);
        }

        if (hakukohde == null) {
            hakukohde = new HakukohdeViite();
            hakukohde.setNimi(importData.getNimi());
            hakukohde.setHakuoid(importData.getHakuOid());
            hakukohde.setOid(importData.getHakukohdeOid());
            hakukohde.setHakukohdekoodi(koodi);

            final String valintaryhmaOid = koodi.getValintaryhma() != null ? koodi.getValintaryhma().getOid() : null;
            hakukohde = hakukohdeService.insert(hakukohde, valintaryhmaOid);
            koodi.setHakukohde(hakukohde);
        } else {

            Valintaryhma koodiValintaryhma = koodi.getValintaryhma();
            Valintaryhma hakukohdeValintaryhma = hakukohde.getValintaryhma();

            // ^ on XOR-operaattori. Tsekataan, että sekä koodin että hakukohteen kautta navigoidut valintaryhmät ovat
            // samat.
            if((koodiValintaryhma != null ^ hakukohdeValintaryhma != null) ||
                    (koodiValintaryhma != null && hakukohdeValintaryhma != null
                            && !koodiValintaryhma.getOid().equals(hakukohdeValintaryhma.getOid()))) {

                Set<ValinnanVaihe> valinnanvaiheet = hakukohde.getValinnanvaiheet();
                for(ValinnanVaihe vv : valinnanvaiheet) {
//                    valinnanVaiheService.deleteByOid(vv.getOid())
//                    vv.get;
                }
            }
        }
    }
}
