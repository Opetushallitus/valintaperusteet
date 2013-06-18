package fi.vm.sade.service.valintaperusteet.service.impl;

import fi.vm.sade.generic.dao.GenericDAO;
import fi.vm.sade.service.valintaperusteet.dao.HakukohdeViiteDAO;
import fi.vm.sade.service.valintaperusteet.dao.HakukohdekoodiDAO;
import fi.vm.sade.service.valintaperusteet.dao.ValinnanVaiheDAO;
import fi.vm.sade.service.valintaperusteet.dao.ValintaryhmaDAO;
import fi.vm.sade.service.valintaperusteet.model.*;
import fi.vm.sade.service.valintaperusteet.schema.HakukohdeImportTyyppi;
import fi.vm.sade.service.valintaperusteet.schema.HakukohdekoodiTyyppi;
import fi.vm.sade.service.valintaperusteet.schema.MonikielinenTekstiTyyppi;
import fi.vm.sade.service.valintaperusteet.service.HakukohdeImportService;
import fi.vm.sade.service.valintaperusteet.service.HakukohdeService;
import fi.vm.sade.service.valintaperusteet.service.ValinnanVaiheService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: wuoti
 * Date: 8.5.2013
 * Time: 13.38
 */
@Service
@Transactional
public class HakukohdeImportServiceImpl implements HakukohdeImportService {
    public final static String KIELI_FI_URI = "kieli_fi";
    public final static String KIELI_SV_URI = "kieli_sv";
    public final static String KIELI_EN_URI = "kieli_en";

    public enum Kieli {
        FI(KIELI_FI_URI), SV(KIELI_SV_URI), EN(KIELI_EN_URI);

        Kieli(String uri) {
            this.uri = uri;
        }

        private String uri;

        public String getUri() {
            return uri;
        }
    }

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

    @Autowired
    private GenericDAO genericDAO;

    protected void convertKoodi(HakukohdekoodiTyyppi from, Hakukohdekoodi to) {
        to.setArvo(from.getArvo());
        to.setUri(sanitizeKoodiUri(from.getKoodiUri()));
        to.setNimiFi(from.getNimiFi());
        to.setNimiSv(from.getNimiSv());
        to.setNimiEn(from.getNimiEn());
    }

    private String haeMonikielinenTekstiKielelle(Collection<MonikielinenTekstiTyyppi> tekstit, Kieli kieli) {
        String found = null;
        for (MonikielinenTekstiTyyppi t : tekstit) {
            if (kieli.uri.equals(t.getLang())) {
                found = t.getText();
                break;
            }
        }

        return found;
    }

    private String haeLahinMonikielinenTekstiKielelle(Collection<MonikielinenTekstiTyyppi> tekstit, Kieli kieli) {
        String found = haeMonikielinenTekstiKielelle(tekstit, kieli);

        Kieli alkuperainenKieli = kieli;
        int plus = 0;

        while ((found == null || "".equals(found)) && kieli.ordinal() < Kieli.values().length - 1) {
            kieli = Kieli.values()[plus];
            ++plus;
            if (kieli == alkuperainenKieli) {
                continue;
            }

            found = haeMonikielinenTekstiKielelle(tekstit, kieli);
        }

        return found;
    }

    private String generoiHakukohdeNimi(HakukohdeImportTyyppi importData) {
        String tarjoajanimi = haeLahinMonikielinenTekstiKielelle(importData.getTarjoajaNimi(), Kieli.FI);
        String hakukohdeNimi = haeLahinMonikielinenTekstiKielelle(importData.getHakukohdeNimi(), Kieli.FI);
        String hakukausi = haeLahinMonikielinenTekstiKielelle(importData.getHakuKausi(), Kieli.FI);
        String hakuvuosi = importData.getHakuVuosi();

        String nimi = "";

        if (StringUtils.isNotBlank(tarjoajanimi)) {
            nimi = tarjoajanimi + ", ";
        }
        if (StringUtils.isNotBlank(hakukohdeNimi)) {
            nimi += hakukohdeNimi + ", ";
        }
        if (StringUtils.isNotBlank(hakukausi)) {
            nimi += hakukausi + " ";
        }
        if (StringUtils.isNotBlank(hakuvuosi)) {
            nimi += hakuvuosi;
        }

        return nimi;
    }

    private HakukohdeViite luoUusiHakukohde(HakukohdeImportTyyppi importData) {
        HakukohdeViite hakukohde = new HakukohdeViite();

        hakukohde.setNimi(generoiHakukohdeNimi(importData));
        hakukohde.setHakuoid(importData.getHakuOid());
        hakukohde.setOid(importData.getHakukohdeOid());
        return hakukohde;
    }

    public String sanitizeKoodiUri(String uri) {
        return uri != null ? uri.split("#")[0] : null;
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
        } else {
            convertKoodi(koodiTyyppi, koodi);
        }


        if (hakukohde == null) {
            hakukohde = luoUusiHakukohde(importData);

            final String valintaryhmaOid = koodi.getValintaryhma() != null ? koodi.getValintaryhma().getOid() : null;
            hakukohde = hakukohdeService.insert(hakukohde, valintaryhmaOid);
            koodi.addHakukohde(hakukohde);
        } else {

            Valintaryhma koodiValintaryhma = koodi.getValintaryhma();
            Valintaryhma hakukohdeValintaryhma = hakukohde.getValintaryhma();

            // ^ on XOR-operaattori. Tsekataan, että sekä koodin että hakukohteen kautta navigoidut valintaryhmät ovat
            // samat.
            if ((koodiValintaryhma != null ^ hakukohdeValintaryhma != null) ||
                    (koodiValintaryhma != null && hakukohdeValintaryhma != null
                            && !koodiValintaryhma.getOid().equals(hakukohdeValintaryhma.getOid()))) {

                poistaHakukohteenPeriytyvatValinnanVaiheet(importData.getHakukohdeOid());
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
                koodi.addHakukohde(lisatty);

                ValinnanVaihe viimeinenValinnanVaihe =
                        valinnanVaiheDAO.haeHakukohteenViimeinenValinnanVaihe(importData.getHakukohdeOid());

                if (!valinnanVaiheet.isEmpty()) {
                    valinnanVaiheet.get(0).setEdellinen(viimeinenValinnanVaihe);
                    if (viimeinenValinnanVaihe != null) {
                        viimeinenValinnanVaihe.setSeuraava(valinnanVaiheet.get(0));
                    }

                    // Asetetaan hakukohteen omat valinnan vaiheet viittaamaan taas uuteen hakukohteeseen
                    for (ValinnanVaihe vv : valinnanVaiheet) {
                        vv.setHakukohdeViite(uusiHakukohde);
                    }
                }
            } else {
                // Synkataan nimi ja koodi
                hakukohde.setNimi(generoiHakukohdeNimi(importData));
                koodi.addHakukohde(hakukohde);
            }
        }

        hakukohde.getOpetuskielet().addAll(haeTaiLisaaOpetuskielet(importData));
    }

    private Set<Opetuskielikoodi> haeTaiLisaaOpetuskielet(HakukohdeImportTyyppi importData) {
        Set<Opetuskielikoodi> opetuskielet = new HashSet<Opetuskielikoodi>();

        for (String uri : importData.getOpetuskielet()) {
            List<Opetuskielikoodi> result = genericDAO.findBy(Opetuskielikoodi.class, "uri", sanitizeKoodiUri(uri));
            if (result != null && result.size() > 0) {
                opetuskielet.add(result.get(0));
            } else {
                Opetuskielikoodi opetuskielikoodi = new Opetuskielikoodi();
                opetuskielikoodi.setUri(uri);
                opetuskielet.add(genericDAO.insert(opetuskielikoodi));
            }
        }

        return opetuskielet;
    }

    private void poistaHakukohteenPeriytyvatValinnanVaiheet(String hakukohdeOid) {
        List<ValinnanVaihe> valinnanVaiheet = valinnanVaiheService.findByHakukohde(hakukohdeOid);
        // Poistetaan kaikki periytyvät valinnan vaiheet
        for (ValinnanVaihe vv : valinnanVaiheet) {
            if (vv.getMasterValinnanVaihe() != null) {
                valinnanVaiheService.deleteByOid(vv.getOid(), true);
            }
        }
    }
}
