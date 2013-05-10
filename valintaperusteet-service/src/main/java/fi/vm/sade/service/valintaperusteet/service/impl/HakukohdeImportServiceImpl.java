package fi.vm.sade.service.valintaperusteet.service.impl;

import fi.vm.sade.service.valintaperusteet.dao.HakukohdeViiteDAO;
import fi.vm.sade.service.valintaperusteet.dao.HakukohdekoodiDAO;
import fi.vm.sade.service.valintaperusteet.dao.ValinnanVaiheDAO;
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

import java.util.List;

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

    @Autowired
    private ValinnanVaiheDAO valinnanVaiheDAO;

    protected void convertKoodi(HakukohdekoodiTyyppi from, Hakukohdekoodi to) {
        to.setArvo(from.getArvo());
        to.setUri(from.getKoodiUri());
        to.setNimiFi(from.getNimiFi());
        to.setNimiSv(from.getNimiSv());
        to.setNimiEn(from.getNimiEn());
    }

    private HakukohdeViite luoUusiHakukohde(HakukohdeImportTyyppi importData) {
        HakukohdeViite hakukohde = new HakukohdeViite();
        hakukohde.setNimi(importData.getNimi());
        hakukohde.setHakuoid(importData.getHakuOid());
        hakukohde.setOid(importData.getHakukohdeOid());
        return hakukohde;
    }

    @Override
    public void tuoHakukohde(HakukohdeImportTyyppi importData) {
        HakukohdekoodiTyyppi koodiTyyppi = importData.getHakukohdekoodi();

        HakukohdeViite hakukohde = hakukohdeViiteDAO.readByOid(importData.getHakukohdeOid());
        Hakukohdekoodi koodi = hakukohdekoodiDAO.findByKoodiUri(koodiTyyppi.getKoodiUri());

        if (koodi == null) {
            koodi = new Hakukohdekoodi();
            convertKoodi(koodiTyyppi, koodi);
            koodi = hakukohdekoodiDAO.insert(koodi);
        }

        if (hakukohde == null) {
            hakukohde = luoUusiHakukohde(importData);

            final String valintaryhmaOid = koodi.getValintaryhma() != null ? koodi.getValintaryhma().getOid() : null;
            hakukohde = hakukohdeService.insert(hakukohde, valintaryhmaOid);
            koodi.setHakukohde(hakukohde);
        } else {

            Valintaryhma koodiValintaryhma = koodi.getValintaryhma();
            Valintaryhma hakukohdeValintaryhma = hakukohde.getValintaryhma();

            // ^ on XOR-operaattori. Tsekataan, että sekä koodin että hakukohteen kautta navigoidut valintaryhmät ovat
            // samat.
            if ((koodiValintaryhma != null ^ hakukohdeValintaryhma != null) ||
                    (koodiValintaryhma != null && hakukohdeValintaryhma != null
                            && !koodiValintaryhma.getOid().equals(hakukohdeValintaryhma.getOid()))) {

                poistaHakukohteenEiPeriytyvatValinnanVaiheet(importData.getHakukohdeOid());
                List<ValinnanVaihe> valinnanVaiheet = valinnanVaiheService.findByHakukohde(importData.getHakukohdeOid());

                // Käydään läpi kaikki ei-periytyvät valinnan vaiheet ja asetetaan hakukohdeviittaus tilapäisesti
                // nulliksi
                for (ValinnanVaihe vv : valinnanVaiheet) {
                    vv.setHakukohdeViite(null);
                }

                // Poistetaan vanha hakukohde
                hakukohdeService.deleteByOid(importData.getHakukohdeOid());

                // Luodaan uusi hakukohde
                HakukohdeViite uusiHakukohde = luoUusiHakukohde(importData);

                HakukohdeViite lisatty = hakukohdeService.insert(uusiHakukohde,
                        koodiValintaryhma != null ? koodiValintaryhma.getOid() : null);
                lisatty.setHakukohdekoodi(koodi);
                koodi.setHakukohde(uusiHakukohde);

                ValinnanVaihe viimeinenValinnanVaihe =
                        valinnanVaiheDAO.haeHakukohteenViimeinenValinnanVaihe(importData.getHakukohdeOid());

                if(!valinnanVaiheet.isEmpty()) {
                    valinnanVaiheet.get(0).setEdellinen(viimeinenValinnanVaihe);
                    if(viimeinenValinnanVaihe != null) {
                        viimeinenValinnanVaihe.setSeuraava(valinnanVaiheet.get(0));
                    }

                    // Asetetaan hakukohteen omat valinnan vaiheet viittaamaan taas uuteen hakukohteeseen
                    for(ValinnanVaihe vv : valinnanVaiheet) {
                        vv.setHakukohdeViite(uusiHakukohde);
                    }
                }
            }
        }
    }

    private void poistaHakukohteenEiPeriytyvatValinnanVaiheet(String hakukohdeOid) {
        List<ValinnanVaihe> valinnanVaiheet = valinnanVaiheService.findByHakukohde(hakukohdeOid);
        // Poistetaan kaikki periytyvät valinnan vaiheet
        for (ValinnanVaihe vv : valinnanVaiheet) {
            if (vv.getMasterValinnanVaihe() != null) {
                valinnanVaiheService.deleteByOid(vv.getOid(), true);
            }
        }
    }
}
