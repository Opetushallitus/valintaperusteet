package fi.vm.sade.service.valintaperusteet.service.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import fi.vm.sade.service.valintaperusteet.dao.*;
import fi.vm.sade.service.valintaperusteet.dto.*;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.vm.sade.service.valintaperusteet.model.HakukohdeViite;
import fi.vm.sade.service.valintaperusteet.model.Hakukohdekoodi;
import fi.vm.sade.service.valintaperusteet.model.HakukohteenValintaperuste;
import fi.vm.sade.service.valintaperusteet.model.Koodi;
import fi.vm.sade.service.valintaperusteet.model.ValinnanVaihe;
import fi.vm.sade.service.valintaperusteet.model.Valintakoekoodi;
import fi.vm.sade.service.valintaperusteet.model.Valintaryhma;
import fi.vm.sade.service.valintaperusteet.model.Valintatapajono;
import fi.vm.sade.service.valintaperusteet.service.HakukohdeImportService;
import fi.vm.sade.service.valintaperusteet.service.HakukohdeService;
import fi.vm.sade.service.valintaperusteet.service.ValinnanVaiheService;
import fi.vm.sade.service.valintaperusteet.service.ValintatapajonoService;

@Service
@Transactional
public class HakukohdeImportServiceImpl implements HakukohdeImportService {
    private static final Logger LOG = LoggerFactory.getLogger(HakukohdeImportServiceImpl.class);

    public final static String KIELI_FI_URI = "kieli_fi";
    public final static String KIELI_SV_URI = "kieli_sv";
    public final static String KIELI_EN_URI = "kieli_en";
    public final String KK_KOHDEJOUKKO = "haunkohdejoukko_12";

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

    protected void convertKoodi(HakukohdekoodiDTO from, Hakukohdekoodi to) {
        to.setArvo(from.getArvo());
        to.setUri(sanitizeKoodiUri(from.getKoodiUri()));
        to.setNimiFi(from.getNimiFi());
        to.setNimiSv(from.getNimiSv());
        to.setNimiEn(from.getNimiEn());
    }

    private String haeMonikielinenTekstiKielelle(Collection<MonikielinenTekstiDTO> tekstit, Kieli kieli) {
        String found = null;
        for (MonikielinenTekstiDTO t : tekstit) {
            if (kieli.uri.equals(t.getLang())) {
                found = t.getText();
                break;
            }
        }
        return found;
    }

    private String haeLahinMonikielinenTekstiKielelle(Collection<MonikielinenTekstiDTO> tekstit, Kieli kieli) {
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


    private String generoiHakukohdeNimi(HakukohdeImportDTO importData) {
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

    private void kopioiTiedot(HakukohdeImportDTO from, HakukohdeViite to) {
        to.setNimi(generoiHakukohdeNimi(from));
        to.setHakuoid(from.getHakuOid());
        to.setOid(from.getHakukohdeOid());
        to.setTarjoajaOid(from.getTarjoajaOid());
        to.setTila(from.getTila());
    }

    public String sanitizeKoodiUri(String uri) {
        return uri != null ? uri.split("#")[0] : null;
    }

    public boolean isKKkohde(String kohdejoukkoUri) {
        if (kohdejoukkoUri == null) {
            return false;
        }
        String uri = sanitizeKoodiUri(kohdejoukkoUri);
        return uri.equals(KK_KOHDEJOUKKO);
    }

    private Valintaryhma selvitaValintaryhma(HakukohdeImportDTO importData) {
        LOG.info("Yritetään selvittää hakukohteen {} valintaryhmä", importData.getHakukohdeOid());
        // Lasketaan valintakokeiden esiintymiset importtidatalle
        Map<String, Integer> valintakoekoodiUrit = new HashMap<String, Integer>();
        for (HakukohteenValintakoeDTO valintakoe : importData.getValintakoe()) {
            final String valintakoeUri = sanitizeKoodiUri(valintakoe.getTyyppiUri());
            if (StringUtils.isNotBlank(valintakoeUri)) {
                if (!valintakoekoodiUrit.containsKey(valintakoeUri)) {
                    valintakoekoodiUrit.put(valintakoeUri, 0);
                }
                Integer esiintymat = valintakoekoodiUrit.get(valintakoeUri) + 1;
                valintakoekoodiUrit.put(valintakoeUri, esiintymat);
            }
        }
        // Haetaan potentiaaliset valintaryhmät hakukohdekoodin,
        // opetuskielikoodien ja valintakoekoodien mukaan
        List<Valintaryhma> valintaryhmat = valintaryhmaDAO.haeHakukohdekoodinJaValintakoekoodienMukaan(
                sanitizeKoodiUri(importData.getHakukohdekoodi().getKoodiUri()), valintakoekoodiUrit.keySet());
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
        // Filtteroinnin jälkeen pitäisi jäljellä olla toivottavasti enää yksi
        // valintaryhmä. Jos valintaryhmiä on
        // enemmän kuin yksi (eli valintaryhmien mallinnassa on ryssitty ja
        // esim. sama hakukohdekoodi on usealla
        // valintaryhmällä) tai jos valintaryhmiä on nolla kappaletta, lisätään
        // importoitava hakukohde juureen (eli
        // tämä metodi palauttaa nullin).
        Valintaryhma valintaryhma = null;
        if (valintaryhmat.size() == 1) {
            String ryhmanHakuoid = valintaryhmat.get(0).getHakuoid();
            if (!StringUtils.isBlank(ryhmanHakuoid) && !ryhmanHakuoid.equals(importData.getHakuOid())) {
                LOG.info("Hakukohteelle ei pystytty määrittämään valintaryhmää. Potentiaalinen ryhmä löytyi, mutta haun oid ei täsmää.");
            } else {
                valintaryhma = valintaryhmat.get(0);
                LOG.info("Hakukohteen tulisi olla valintaryhmän {} alla", valintaryhma.getOid());
            }
        } else if (valintaryhmat.size() > 1) {
            List<Valintaryhma> filtered = valintaryhmat.stream().filter(hakuoidFilter(importData.getHakuOid())).collect(Collectors.toList());
            if (filtered.size() == 1) {
                valintaryhma = filtered.get(0);
                LOG.info("Hakukohteen tulisi olla valintaryhmän {} alla", valintaryhma.getOid());
            } else {
                // Testataan vielä hakuvuodella
                final List<Valintaryhma> vuodenRyhmat = valintaryhmat.stream().filter(v -> !StringUtils.isBlank(v.getHakuvuosi()) && v.getHakuvuosi().equals(importData.getHakuVuosi())).collect(Collectors.toList());
                if (vuodenRyhmat.size() == 1) {
                    valintaryhma = vuodenRyhmat.get(0);
                    LOG.info("Hakukohteen tulisi olla valintaryhmän {} alla", valintaryhma.getOid());
                } else {
                    LOG.info("Hakukohteelle ei pystytty määrittämään valintaryhmää. Potentiaalisia valintaryhmiä on {} kpl. "
                            + "Hakukohde lisätään juureen tai säilytetään vanhassa ryhmässä.", valintaryhmat.size());
                }
            }
        } else {
            List<Valintaryhma> haunRyhmat = valintaryhmaDAO.readByHakuoid(importData.getHakuOid());
            if (haunRyhmat.size() == 1) {
                valintaryhma = haunRyhmat.get(0);
                LOG.info("Hakukohteen tulisi olla valintaryhmän {} alla", valintaryhma.getOid());
            } else {
                LOG.info("Yhtään potentiaalista valintaryhmää ei löytynyt. Hakukohde lisätään juureen.");
            }
        }
        return valintaryhma;
    }

    private Predicate<Valintaryhma> hakuoidFilter(String hakuoid) {
        return v -> !StringUtils.isBlank(v.getHakuoid()) && v.getHakuoid().equals(hakuoid);
    }

    @Override
    public void tuoHakukohde(HakukohdeImportDTO importData) {
        LOG.info("Aloitetaan import hakukohteelle. Hakukohde OID: {}, hakukohdekoodi URI: {}", importData.getHakukohdeOid(), importData.getHakukohdekoodi().getKoodiUri());
        HakukohdekoodiDTO hakukohdekoodiTyyppi = importData.getHakukohdekoodi();
        HakukohdeViite hakukohde = hakukohdeViiteDAO.readForImport(importData.getHakukohdeOid());
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
            hakukohde.setHakukohdekoodi(koodi);
        } else {
            LOG.info("Hakukohde löytyi.");
            Valintaryhma hakukohdeValintaryhma = hakukohde.getValintaryhma();
            kopioiTiedot(importData, hakukohde);
            if (valintaryhma == null && hakukohdeValintaryhma != null) {
                LOG.info("Hakukohde on ollut valintaryhmässä ja nyt yritetään laittaa juureen. Säilytetään vanhassa ryhmässä");
                SynkronoiKoodiJanimi(importData, hakukohde, koodi);
            }
            // ^ on XOR-operaattori. Tsekataan, että sekä koodin että
            // hakukohteen kautta navigoidut valintaryhmät ovat
            // samat ja että hakukohdetta ei ole manuaalisesti siirretty
            // valintaryhmään.
            else if ((valintaryhma != null ^ hakukohdeValintaryhma != null)
                    || (valintaryhma != null && hakukohdeValintaryhma != null && !valintaryhma.getOid().equals(
                    hakukohdeValintaryhma.getOid()))) {
                if (hakukohde.getManuaalisestiSiirretty() != null && hakukohde.getManuaalisestiSiirretty()) {
                    LOG.info("Hakukohde on väärän valintaryhmän alla, mutta se on siirretty manuaalisesti. "
                            + "Synkronointia ei suoriteta");
                } else {
                    LOG.info("Hakukohde on väärän valintaryhmän alla. Synkronoidaan hakukohde oikean valintaryhmän alle");
                    String valintaryhmaOid = valintaryhma != null ? valintaryhma.getOid() : null;
                    hakukohde = hakukohdeService.siirraHakukohdeValintaryhmaan(importData.getHakukohdeOid(), valintaryhmaOid, false);
                }
                hakukohde.setHakukohdekoodi(koodi);
            } else {
                LOG.info("Hakukohde on oikeassa valintaryhmässä. Synkronoidaan hakukohteen nimi ja koodi.");
                // Synkataan nimi ja koodi
                SynkronoiKoodiJanimi(importData, hakukohde, koodi);
            }
        }
        // Päivitetään valintakoekoodit
        hakukohde.setValintakokeet(haeTaiLisaaValintakoekoodit(importData));
        // Lisätään valinaperusteet
        if (hakukohde.getHakukohteenValintaperusteet() != null) {
            List<HakukohteenValintaperuste> perusteet = hakukohteenValintaperusteDAO.haeHakukohteenValintaperusteet(hakukohde.getOid());
            for (HakukohteenValintaperuste hv : perusteet) {
                hv.setHakukohde(null);
                hakukohteenValintaperusteDAO.remove(hv);
            }
            hakukohde.getHakukohteenValintaperusteet().clear();
        }
        genericDAO.flush();
        hakukohde = hakukohdeViiteDAO.readForImport(importData.getHakukohdeOid());
        hakukohde.setHakukohteenValintaperusteet(lisaaValintaperusteet(importData, hakukohde));
        // Päivitetään aloituspaikkojen lukumäärä jos mahdollista (KK-kohteille ei päivitetä)
        if (!isKKkohde(importData.getHaunkohdejoukkoUri())) {
            paivitaAloituspaikkojenLkm(hakukohde, importData.getValinnanAloituspaikat());
        }
    }

    private void SynkronoiKoodiJanimi(HakukohdeImportDTO importData, HakukohdeViite hakukohde, Hakukohdekoodi koodi) {
        hakukohde.setNimi(generoiHakukohdeNimi(importData));
        hakukohde.setTarjoajaOid(importData.getTarjoajaOid());
        if (hakukohde.getManuaalisestiSiirretty() == null) {
            hakukohde.setManuaalisestiSiirretty(false);
        }
        hakukohde.setHakukohdekoodi(koodi);
    }

    private void paivitaAloituspaikkojenLkm(final HakukohdeViite hakukohde, final int valinnanAloituspaikat) {
        // Päivitetään viimeisen valinnanvaiheen ensimmäiselle jonolle tarjonnasta tulleet alotuspaikkalukumäärät
        List<ValinnanVaihe> valinnanVaiheet = valinnanVaiheService.findByHakukohde(hakukohde.getOid());
        int vaiheidenMaara = valinnanVaiheet.size() - 1;
        if (vaiheidenMaara >= 0
                && fi.vm.sade.service.valintaperusteet.dto.model.ValinnanVaiheTyyppi.TAVALLINEN.equals(valinnanVaiheet
                .get(vaiheidenMaara).getValinnanVaiheTyyppi()) && valinnanVaiheet.get(vaiheidenMaara).getMasterValinnanVaihe() != null) {
            ValinnanVaihe vaihe = valinnanVaiheet.get(vaiheidenMaara);
            List<Valintatapajono> jonot = valintatapajonoService.findJonoByValinnanvaihe(vaihe.getOid());
            if (jonot.size() > 0 && jonot.get(0).getMasterValintatapajono() != null) {
                Valintatapajono jono = jonot.get(0);
                jono.setAloituspaikat(valinnanAloituspaikat);
            }
        }
    }

    private List<Valintakoekoodi> haeTaiLisaaValintakoekoodit(HakukohdeImportDTO importData) {
        return importData.getValintakoe().stream()
                .map(koe -> haeTaiLisaaKoodi(Valintakoekoodi.class, koe.getTyyppiUri(),
                        new KoodiFactory<Valintakoekoodi>() {
                            @Override
                            public Valintakoekoodi newInstance() {
                                return new Valintakoekoodi();
                            }
                        })).filter(koodi -> koodi != null).collect(Collectors.toList());
    }

    private Map<String, HakukohteenValintaperuste> lisaaValintaperusteet(HakukohdeImportDTO importData, HakukohdeViite hakukohde) {
        return importData.getValintaperuste().parallelStream().map(a -> {
            HakukohteenValintaperuste peruste = new HakukohteenValintaperuste();
            peruste.setTunniste(a.getAvain());
            peruste.setArvo(a.getArvo());
            peruste.setKuvaus(a.getAvain());
            peruste.setHakukohde(hakukohde);
            return peruste;
        }).collect(Collectors.toMap(HakukohteenValintaperuste::getTunniste, p -> p, (s, a) -> a));

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
