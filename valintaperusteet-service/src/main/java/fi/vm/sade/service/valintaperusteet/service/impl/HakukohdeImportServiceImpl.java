package fi.vm.sade.service.valintaperusteet.service.impl;

import fi.vm.sade.generic.dao.GenericDAO;
import fi.vm.sade.service.valintaperusteet.dao.*;
import fi.vm.sade.service.valintaperusteet.dto.HakukohdeViiteDTO;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.model.*;
import fi.vm.sade.service.valintaperusteet.model.ValinnanVaiheTyyppi;
import fi.vm.sade.service.valintaperusteet.schema.*;
import fi.vm.sade.service.valintaperusteet.service.HakukohdeImportService;
import fi.vm.sade.service.valintaperusteet.service.HakukohdeService;
import fi.vm.sade.service.valintaperusteet.service.ValinnanVaiheService;
import fi.vm.sade.service.valintaperusteet.service.ValintatapajonoService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * User: wuoti
 * Date: 8.5.2013
 * Time: 13.38
 */
@Service
@Transactional
public class HakukohdeImportServiceImpl implements HakukohdeImportService {
    private static final Logger LOG = LoggerFactory.getLogger(HakukohdeImportServiceImpl.class);

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
    private ValintatapajonoService valintatapajonoService;

    @Autowired
    private ValintakoekoodiDAO valintakoekoodiDAO;

    @Autowired
    private HakukohteenValintaperusteDAO hakukohteenValintaperusteDAO;

    @Autowired
    private GenericDAO genericDAO;

    @Autowired
    private ValintaperusteetModelMapper modelMapper;

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

    private void kopioiTiedot(HakukohdeImportTyyppi from, HakukohdeViite to) {
        to.setNimi(generoiHakukohdeNimi(from));
        to.setHakuoid(from.getHakuOid());
        to.setOid(from.getHakukohdeOid());
        to.setTarjoajaOid(from.getTarjoajaOid());
        to.setTila(from.getTila());
    }

    public String sanitizeKoodiUri(String uri) {
        return uri != null ? uri.split("#")[0] : null;
    }

    private Valintaryhma selvitaValintaryhma(HakukohdeImportTyyppi importData) {
        LOG.info("Yritetään selvittää hakukohteen {} valintaryhmä", importData.getHakukohdeOid());

        // Lasketaan valintakokeiden esiintymiset importtidatalle
        Map<String, Integer> valintakoekoodiUrit = new HashMap<String, Integer>();
        for (HakukohteenValintakoeTyyppi valintakoe : importData.getValintakoe()) {
            final String valintakoeUri = sanitizeKoodiUri(valintakoe.getTyyppiUri());
            if (StringUtils.isNotBlank(valintakoeUri)) {
                if (!valintakoekoodiUrit.containsKey(valintakoeUri)) {
                    valintakoekoodiUrit.put(valintakoeUri, 0);
                }

                Integer esiintymat = valintakoekoodiUrit.get(valintakoeUri) + 1;
                valintakoekoodiUrit.put(valintakoeUri, esiintymat);
            }
        }

        // Haetaan potentiaaliset valintaryhmät hakukohdekoodin, opetuskielikoodien ja valintakoekoodien mukaan
        List<Valintaryhma> valintaryhmat = valintaryhmaDAO.haeHakukohdekoodinJaValintakoekoodienMukaan(
                sanitizeKoodiUri(importData.getHakukohdekoodi().getKoodiUri()),
                valintakoekoodiUrit.keySet());

        LOG.info("Potentiaalisia valintaryhmiä {} kpl", valintaryhmat.size());

        // Tarkistetaan valintaryhmät valintakoekoodien osalta.
        Iterator<Valintaryhma> iterator = valintaryhmat.iterator();

        while (iterator.hasNext()) {
            Valintaryhma r = iterator.next();

            Map<String, Integer> valintaryhmanValintakoekoodiUrit = new HashMap<String, Integer>();
            List<Valintakoekoodi> valintakoekoodit = valintakoekoodiDAO.findByValintaryhma(r.getOid());

            // Lasketaan valintakoekoodien esiintymät valintaryhmässä
            for (Valintakoekoodi k : valintakoekoodit) {
                if (!valintaryhmanValintakoekoodiUrit.containsKey(k.getUri())) {
                    valintaryhmanValintakoekoodiUrit.put(k.getUri(), 0);
                }

                Integer esiintymat = valintaryhmanValintakoekoodiUrit.get(k.getUri()) + 1;
                valintaryhmanValintakoekoodiUrit.put(k.getUri(), esiintymat);
            }

            if (!valintakoekoodiUrit.equals(valintaryhmanValintakoekoodiUrit)) {
                iterator.remove();
            }
        }

        LOG.info("Valintakoekoodifilterin jälkeen potentiaalisia valintaryhmiä {} kpl", valintaryhmat.size());

        // Filtteroinnin jälkeen pitäisi jäljellä olla toivottavasti enää yksi valintaryhmä. Jos valintaryhmiä on
        // enemmän kuin yksi (eli valintaryhmien mallinnassa on ryssitty ja esim. sama hakukohdekoodi on usealla
        // valintaryhmällä) tai jos valintaryhmiä on nolla kappaletta, lisätään importoitava hakukohde juureen (eli
        // tämä metodi palauttaa nullin).
        Valintaryhma valintaryhma = null;
        if (valintaryhmat.size() == 1) {
            valintaryhma = valintaryhmat.get(0);
            LOG.info("Hakukohteen tulisi olla valintaryhmän {} alla", valintaryhma.getOid());
        } else {
            LOG.info("Hakukohteelle ei pystytty määrittämään valintaryhmää. Potentiaalisia valintaryhmiä on {} kpl. " +
                    "Hakukohde lisätään juureen.", valintaryhmat.size());

        }

        return valintaryhma;
    }

    @Override
    public void tuoHakukohde(HakukohdeImportTyyppi importData) {
        LOG.info("Aloitetaan import hakukohteelle. Hakukohde OID: {}, hakukohdekoodi URI: {}",
                importData.getHakukohdeOid(), importData.getHakukohdekoodi().getKoodiUri());
        HakukohdekoodiTyyppi hakukohdekoodiTyyppi = importData.getHakukohdekoodi();

        HakukohdeViite hakukohde = hakukohdeViiteDAO.readByOid(importData.getHakukohdeOid());
        Hakukohdekoodi koodi = hakukohdekoodiDAO.readByUri(sanitizeKoodiUri(hakukohdekoodiTyyppi.getKoodiUri()));

        if (koodi == null) {
            koodi = new Hakukohdekoodi();
            convertKoodi(hakukohdekoodiTyyppi, koodi);
            koodi = hakukohdekoodiDAO.insert(koodi);
        } else {
            convertKoodi(hakukohdekoodiTyyppi, koodi);
        }

        final Valintaryhma valintaryhma = selvitaValintaryhma(importData);

        if (hakukohde == null) {
            LOG.info("Hakukohdetta ei ole olemassa. Luodaan uusi hakukohde.");
            hakukohde = new HakukohdeViite();
            kopioiTiedot(importData, hakukohde);
            hakukohde = hakukohdeService.insert(modelMapper.map(hakukohde, HakukohdeViiteDTO.class), valintaryhma != null ? valintaryhma.getOid() : null);
            koodi.addHakukohde(hakukohde);
        } else {
            LOG.info("Hakukohde löytyi.");
            Valintaryhma hakukohdeValintaryhma = hakukohde.getValintaryhma();
            kopioiTiedot(importData, hakukohde);
            // ^ on XOR-operaattori. Tsekataan, että sekä koodin että hakukohteen kautta navigoidut valintaryhmät ovat
            // samat ja että hakukohdetta ei ole manuaalisesti siirretty valintaryhmään.
            if ((valintaryhma != null ^ hakukohdeValintaryhma != null) ||
                    (valintaryhma != null && hakukohdeValintaryhma != null
                            && !valintaryhma.getOid().equals(hakukohdeValintaryhma.getOid()))) {

                if (hakukohde.getManuaalisestiSiirretty() != null && hakukohde.getManuaalisestiSiirretty()) {
                    LOG.info("Hakukohde on väärän valintaryhmän alla, mutta se on siirretty manuaalisesti. " +
                            "Synkronointia ei suoriteta");

                } else {
                    LOG.info("Hakukohde on väärän valintaryhmän alla. Synkronoidaan hakukohde oikean valintaryhmän alle");
                    String valintaryhmaOid = valintaryhma != null ? valintaryhma.getOid() : null;
                    hakukohde =
                            hakukohdeService.siirraHakukohdeValintaryhmaan(importData.getHakukohdeOid(), valintaryhmaOid, false);
                }

                hakukohde.setHakukohdekoodi(koodi);
                koodi.addHakukohde(hakukohde);

            } else {
                LOG.info("Hakukohde on oikeassa valintaryhmässä. Synkronoidaan hakukohteen nimi ja koodi.");
                // Synkataan nimi ja koodi
                hakukohde.setNimi(generoiHakukohdeNimi(importData));
                hakukohde.setTarjoajaOid(importData.getTarjoajaOid());
                if (hakukohde.getManuaalisestiSiirretty() == null) {
                    hakukohde.setManuaalisestiSiirretty(false);
                }

                koodi.addHakukohde(hakukohde);
            }
        }

        // Päivitetään valintakoekoodit
        hakukohde.setValintakokeet(haeTaiLisaaValintakoekoodit(importData));

        // Lisätään valinaperusteet
        if(hakukohde.getHakukohteenValintaperusteet() != null) {
              List <HakukohteenValintaperuste> perusteet = hakukohteenValintaperusteDAO.haeHakukohteenValintaperusteet(hakukohde.getOid());
              for(HakukohteenValintaperuste hv : perusteet) {
                  hv.setHakukohde(null);
                  hakukohteenValintaperusteDAO.remove(hv);
              }
              hakukohde.getHakukohteenValintaperusteet().clear();

        }
        genericDAO.flush();
        hakukohde = hakukohdeViiteDAO.readByOid(importData.getHakukohdeOid());

        hakukohde.setHakukohteenValintaperusteet(lisaaValintaperusteet(importData, hakukohde));

        // Päivitetään aloituspaikkojen lukumäärä jos mahdollista
        paivitaAloituspaikkojenLkm(hakukohde, importData.getValinnanAloituspaikat());
    }

    private void paivitaAloituspaikkojenLkm(final HakukohdeViite hakukohde, final int valinnanAloituspaikat) {
        // Jos hakukohteella on tasan kaksi valinnan vaihetta, joista jälkimmäinen on tavallinen, periytyvä valinnan
        // vaihe ja jossa on yksi periytyvä valintatapajono, päivitetään jonolle aloituspaikkojen lukumäärä

        List<ValinnanVaihe> valinnanVaiheet = valinnanVaiheService.findByHakukohde(hakukohde.getOid());
        if (valinnanVaiheet.size() == 2
                && ValinnanVaiheTyyppi.TAVALLINEN.equals(valinnanVaiheet.get(1).getValinnanVaiheTyyppi())
                && valinnanVaiheet.get(1).getMasterValinnanVaihe() != null) {
            ValinnanVaihe vaihe = valinnanVaiheet.get(1);
            List<Valintatapajono> jonot = valintatapajonoService.findJonoByValinnanvaihe(vaihe.getOid());
            if (jonot.size() == 1 && jonot.get(0).getMasterValintatapajono() != null) {
                Valintatapajono jono = jonot.get(0);
                jono.setAloituspaikat(valinnanAloituspaikat);
            }
        }
    }

    private List<Valintakoekoodi> haeTaiLisaaValintakoekoodit(HakukohdeImportTyyppi importData) {
        List<Valintakoekoodi> koekoodit = new ArrayList<Valintakoekoodi>();

        for (HakukohteenValintakoeTyyppi koe : importData.getValintakoe()) {
            Valintakoekoodi koodi = haeTaiLisaaKoodi(Valintakoekoodi.class, koe.getTyyppiUri(),
                    new KoodiFactory<Valintakoekoodi>() {
                        @Override
                        public Valintakoekoodi newInstance() {
                            return new Valintakoekoodi();
                        }
                    });

            if (koodi != null) {
                koekoodit.add(koodi);
            }
        }

        return koekoodit;
    }

    private Map<String,HakukohteenValintaperuste> lisaaValintaperusteet(HakukohdeImportTyyppi importData, HakukohdeViite hakukohde) {
        Map<String,HakukohteenValintaperuste> perusteet = new HashMap<String,HakukohteenValintaperuste>();
        for (AvainArvoTyyppi avainArvo : importData.getValintaperuste()) {
            HakukohteenValintaperuste peruste = new HakukohteenValintaperuste();
            peruste.setTunniste(avainArvo.getAvain());
            peruste.setArvo(avainArvo.getArvo());
            peruste.setKuvaus(avainArvo.getAvain());
            peruste.setHakukohde(hakukohde);
            perusteet.put(peruste.getTunniste(),peruste);
        }
        return perusteet;
    }

    private abstract class KoodiFactory<T extends Koodi> {
        public abstract T newInstance();

    }

    private <T extends Koodi> T haeTaiLisaaKoodi(Class<T> clazz, String uri, KoodiFactory<T> factory) {
        String sanitizedUri = sanitizeKoodiUri(uri);

        if (StringUtils.isNotBlank(sanitizedUri)) {
            List<T> result = genericDAO.findBy(clazz, "uri", sanitizedUri);
            if (result != null && result.size() > 0) {
                return result.get(0);
            } else {
                T t = factory.newInstance();
                t.setUri(sanitizedUri);
                return genericDAO.insert(t);
            }
        } else {
            return null;
        }
    }
}
