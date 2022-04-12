package fi.vm.sade.service.valintaperusteet.service.impl;

import fi.vm.sade.service.valintaperusteet.dao.*;
import fi.vm.sade.service.valintaperusteet.dto.*;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class HakukohdeImportServiceImpl implements HakukohdeImportService {
  private static final Logger LOG = LoggerFactory.getLogger(HakukohdeImportServiceImpl.class);

  public static final String KIELI_FI_URI = "kieli_fi";
  public static final String KIELI_SV_URI = "kieli_sv";
  public static final String KIELI_EN_URI = "kieli_en";
  public final String KK_KOHDEJOUKKO = "haunkohdejoukko_12";

  public enum Kieli {
    FI(KIELI_FI_URI),
    SV(KIELI_SV_URI),
    EN(KIELI_EN_URI);

    Kieli(String uri) {
      this.uri = uri;
    }

    private String uri;

    public String getUri() {
      return uri;
    }
  }

  @Autowired private HakukohdeViiteDAO hakukohdeViiteDAO;

  @Autowired private HakukohdeService hakukohdeService;

  @Autowired private ValintaryhmaDAO valintaryhmaDAO;

  @Autowired private HakukohdekoodiDAO hakukohdekoodiDAO;

  @Autowired private ValinnanVaiheService valinnanVaiheService;

  @Autowired private ValinnanVaiheDAO valinnanVaiheDAO;

  @Autowired private ValintatapajonoService valintatapajonoService;

  @Autowired private ValintakoekoodiDAO valintakoekoodiDAO;

  @Autowired private HakukohteenValintaperusteDAO hakukohteenValintaperusteDAO;

  @Autowired private GenericDAO genericDAO;

  @Autowired private ValintaperusteetModelMapper modelMapper;

  protected void convertKoodi(HakukohdekoodiDTO from, Hakukohdekoodi to) {
    to.setArvo(from.getArvo());
    to.setUri(sanitizeKoodiUri(from.getKoodiUri()));
    to.setNimiFi(from.getNimiFi());
    to.setNimiSv(from.getNimiSv());
    to.setNimiEn(from.getNimiEn());
  }

  private String haeMonikielinenTekstiKielelle(
      Collection<MonikielinenTekstiDTO> tekstit, Kieli kieli) {
    String found = null;
    for (MonikielinenTekstiDTO t : tekstit) {
      if (kieli.uri.equals(t.getLang())) {
        found = t.getText();
        break;
      }
    }
    return found;
  }

  private String haeLahinMonikielinenTekstiKielelle(
      Collection<MonikielinenTekstiDTO> tekstit, Kieli kieli) {
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
    String tarjoajanimi =
        haeLahinMonikielinenTekstiKielelle(importData.getTarjoajaNimi(), Kieli.FI);
    String hakukohdeNimi =
        haeLahinMonikielinenTekstiKielelle(importData.getHakukohdeNimi(), Kieli.FI);
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

  private String createHakukohteetKoodiUri(String koodiUri) {
    if (koodiUri == null) return null;
    String sanitizedKoodiuri = sanitizeKoodiUri(koodiUri);
    if (!sanitizedKoodiuri.contains("_")) return sanitizedKoodiuri;
    String hakukohteetKoodiUri =
        "hakukohteet".concat(sanitizedKoodiuri.substring(sanitizedKoodiuri.indexOf("_")));
    return hakukohteetKoodiUri;
  }

  private Valintaryhma selvitaValintaryhma(HakukohdeImportDTO importData) {
    String hakukohdeOid = importData.getHakukohdeOid();
    String hakuOid = importData.getHakuOid();
    String hakukohdekoodi = createHakukohteetKoodiUri(importData.getHakukohdekoodi().getKoodiUri());
    Set<String> valintakoekoodit =
        importData.getValintakoe().stream()
            .map(valintakoe -> sanitizeKoodiUri(valintakoe.getTyyppiUri()))
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

    LOG.info(
        "Yritetään selvittää haun {} hakukohteen {} valintaryhmä hakukohdekoodilla {} ja valintakoekoodeilla {}",
        hakuOid,
        hakukohdeOid,
        hakukohdekoodi,
        valintakoekoodit);

    List<Valintaryhma> valintaryhmat =
        valintaryhmaDAO.haeHakukohdekoodinJaValintakoekoodienMukaan(
            hakuOid, hakukohdekoodi, valintakoekoodit);

    if (valintaryhmat.size() != 1) {
      LOG.info(
          "Haku {}, hakukohdekoodi {} ja valintakoekoodit {} eivät antaneet yksikäsitteistä valintaryhmää. Mahdolliset ryhmät {}.",
          hakuOid,
          hakukohdekoodi,
          String.join(", ", valintakoekoodit),
          valintaryhmat.stream().map(Valintaryhma::getOid).collect(Collectors.joining(", ")));
      valintaryhmat = valintaryhmaDAO.readByHakuoid(hakuOid);
    }

    if (valintaryhmat.size() != 1) {
      LOG.info(
          "Haku {} ei antanut yksikäsitteistä valintaryhmää. Mahdolliset ryhmät {}.",
          hakuOid,
          valintaryhmat.stream().map(Valintaryhma::getOid).collect(Collectors.joining(", ")));
      valintaryhmat = Collections.singletonList(null);
    }

    Valintaryhma valintaryhma = valintaryhmat.get(0);
    if (valintaryhma == null) {
      LOG.info("Hakukohteen {} tulisi olla juurivalintaryhmän alla.", hakukohdeOid);
    } else {
      LOG.info(
          "Hakukohteen {} tulisi olla valintaryhmän {} alla.", hakukohdeOid, valintaryhma.getOid());
    }

    return valintaryhma;
  }

  @Override
  public void tuoHakukohde(HakukohdeImportDTO importData) {
    LOG.info(
        "Aloitetaan import hakukohteelle. Hakukohde OID: {}, hakukohdekoodi URI: {}",
        importData.getHakukohdeOid(),
        importData.getHakukohdekoodi().getKoodiUri());
    HakukohdekoodiDTO hakukohdekoodiTyyppi = importData.getHakukohdekoodi();
    HakukohdeViite hakukohde = hakukohdeViiteDAO.readForImport(importData.getHakukohdeOid());
    Hakukohdekoodi koodi =
        hakukohdekoodiDAO.readByUri(sanitizeKoodiUri(hakukohdekoodiTyyppi.getKoodiUri()));
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
      hakukohde =
          hakukohdeService.insert(
              modelMapper.map(hakukohde, HakukohdeViiteDTO.class),
              valintaryhma != null ? valintaryhma.getOid() : null);
      hakukohde.setHakukohdekoodi(koodi);
    } else {
      LOG.info("Hakukohde löytyi.");
      Valintaryhma hakukohdeValintaryhma = hakukohde.getValintaryhma();
      kopioiTiedot(importData, hakukohde);
      if (valintaryhma == null && hakukohdeValintaryhma != null) {
        LOG.info(
            "Hakukohde on ollut valintaryhmässä ja nyt yritetään laittaa juureen. Säilytetään vanhassa ryhmässä");
        SynkronoiKoodiJanimi(importData, hakukohde, koodi);
      }
      // ^ on XOR-operaattori. Tsekataan, että sekä koodin että
      // hakukohteen kautta navigoidut valintaryhmät ovat
      // samat ja että hakukohdetta ei ole manuaalisesti siirretty
      // valintaryhmään.
      else if ((valintaryhma != null ^ hakukohdeValintaryhma != null)
          || (valintaryhma != null
              && hakukohdeValintaryhma != null
              && !valintaryhma.getOid().equals(hakukohdeValintaryhma.getOid()))) {
        if (hakukohde.getManuaalisestiSiirretty() != null
            && hakukohde.getManuaalisestiSiirretty()) {
          LOG.info(
              "Hakukohde on väärän valintaryhmän alla, mutta se on siirretty manuaalisesti. "
                  + "Synkronointia ei suoriteta");
        } else {
          LOG.info(
              "Hakukohde on väärän valintaryhmän alla. Synkronoidaan hakukohde oikean valintaryhmän alle");
          String valintaryhmaOid = valintaryhma != null ? valintaryhma.getOid() : null;
          hakukohde =
              hakukohdeService.siirraHakukohdeValintaryhmaan(
                  importData.getHakukohdeOid(), valintaryhmaOid, false);
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
      List<HakukohteenValintaperuste> perusteet =
          hakukohteenValintaperusteDAO.haeHakukohteenValintaperusteet(hakukohde.getOid());
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

  private void SynkronoiKoodiJanimi(
      HakukohdeImportDTO importData, HakukohdeViite hakukohde, Hakukohdekoodi koodi) {
    hakukohde.setNimi(generoiHakukohdeNimi(importData));
    hakukohde.setTarjoajaOid(importData.getTarjoajaOid());
    if (hakukohde.getManuaalisestiSiirretty() == null) {
      hakukohde.setManuaalisestiSiirretty(false);
    }
    hakukohde.setHakukohdekoodi(koodi);
  }

  private void paivitaAloituspaikkojenLkm(
      final HakukohdeViite hakukohde, final int valinnanAloituspaikat) {
    // Päivitetään viimeisen valinnanvaiheen ensimmäiselle jonolle tarjonnasta tulleet
    // alotuspaikkalukumäärät
    List<ValinnanVaihe> valinnanVaiheet = valinnanVaiheService.findByHakukohde(hakukohde.getOid());
    int vaiheidenMaara = valinnanVaiheet.size() - 1;
    if (vaiheidenMaara >= 0
        && fi.vm.sade.service.valintaperusteet.dto.model.ValinnanVaiheTyyppi.TAVALLINEN.equals(
            valinnanVaiheet.get(vaiheidenMaara).getValinnanVaiheTyyppi())
        && valinnanVaiheet.get(vaiheidenMaara).getMasterValinnanVaihe() != null) {
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
        .map(
            koe ->
                haeTaiLisaaKoodi(
                    Valintakoekoodi.class,
                    koe.getTyyppiUri(),
                    new KoodiFactory<Valintakoekoodi>() {
                      @Override
                      public Valintakoekoodi newInstance() {
                        return new Valintakoekoodi();
                      }
                    }))
        .filter(koodi -> koodi != null)
        .collect(Collectors.toList());
  }

  private Map<String, HakukohteenValintaperuste> lisaaValintaperusteet(
      HakukohdeImportDTO importData, HakukohdeViite hakukohde) {
    return importData.getValintaperuste().parallelStream()
        .map(
            a -> {
              HakukohteenValintaperuste peruste = new HakukohteenValintaperuste();
              peruste.setTunniste(a.getAvain());
              peruste.setArvo(a.getArvo());
              peruste.setKuvaus(a.getAvain());
              peruste.setHakukohde(hakukohde);
              return peruste;
            })
        .collect(Collectors.toMap(HakukohteenValintaperuste::getTunniste, p -> p, (s, a) -> a));
  }

  private abstract class KoodiFactory<T extends Koodi> {
    public abstract T newInstance();
  }

  private <T extends Koodi> T haeTaiLisaaKoodi(
      Class<T> clazz, String uri, KoodiFactory<T> factory) {
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
