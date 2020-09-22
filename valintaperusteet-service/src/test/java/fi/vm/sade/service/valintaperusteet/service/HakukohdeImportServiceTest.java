package fi.vm.sade.service.valintaperusteet.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import fi.vm.sade.service.valintaperusteet.annotation.DataSetLocation;
import fi.vm.sade.service.valintaperusteet.dao.HakukohdeViiteDAO;
import fi.vm.sade.service.valintaperusteet.dao.HakukohdekoodiDAO;
import fi.vm.sade.service.valintaperusteet.dao.ValintaryhmaDAO;
import fi.vm.sade.service.valintaperusteet.dto.AvainArvoDTO;
import fi.vm.sade.service.valintaperusteet.dto.HakukohdeImportDTO;
import fi.vm.sade.service.valintaperusteet.dto.HakukohdekoodiDTO;
import fi.vm.sade.service.valintaperusteet.dto.HakukohteenValintakoeDTO;
import fi.vm.sade.service.valintaperusteet.dto.MonikielinenTekstiDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintaryhmaCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.listeners.ValinnatJTACleanInsertTestExecutionListener;
import fi.vm.sade.service.valintaperusteet.model.HakukohdeViite;
import fi.vm.sade.service.valintaperusteet.model.Hakukohdekoodi;
import fi.vm.sade.service.valintaperusteet.model.ValinnanVaihe;
import fi.vm.sade.service.valintaperusteet.model.Valintakoekoodi;
import fi.vm.sade.service.valintaperusteet.model.Valintaryhma;
import fi.vm.sade.service.valintaperusteet.model.Valintatapajono;
import fi.vm.sade.service.valintaperusteet.service.impl.HakukohdeImportServiceImpl;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

/** User: wuoti Date: 8.5.2013 Time: 15.06 */
@ContextConfiguration(locations = "classpath:test-context.xml")
@TestExecutionListeners(
    listeners = {
      ValinnatJTACleanInsertTestExecutionListener.class,
      DependencyInjectionTestExecutionListener.class,
      DirtiesContextTestExecutionListener.class
    })
@RunWith(SpringJUnit4ClassRunner.class)
@DataSetLocation("classpath:test-data.xml")
public class HakukohdeImportServiceTest {

  @Autowired private HakukohdekoodiDAO hakukohdekoodiDAO;

  @Autowired private HakukohdeViiteDAO hakukohdeViiteDAO;

  @Autowired private ValintaryhmaDAO valintaryhmaDAO;

  @Autowired private HakukohdeImportService hakukohdeImportService;

  @Autowired private ValinnanVaiheService valinnanVaiheService;

  @Autowired private HakukohdeService hakukohdeService;

  @Autowired private ValintaryhmaService valintaryhmaService;

  @Autowired private ValintatapajonoService valintatapajonoService;

  private static final String OLETUS_VALINTAKOEURI = "valintakoeuri1";

  private MonikielinenTekstiDTO luoMonikielinenTeksti(
      String teksti, HakukohdeImportServiceImpl.Kieli kieli) {
    MonikielinenTekstiDTO t = new MonikielinenTekstiDTO();
    t.setLang(kieli.getUri());
    t.setText(teksti);
    return t;
  }

  private HakukohdeImportDTO luoHakukohdeImportDTO(
      String hakukohdeOid, String hakuOid, String koodiUri) {
    HakukohdeImportDTO imp = new HakukohdeImportDTO();
    imp.setHakukohdeOid(hakukohdeOid);
    imp.setHakuOid(hakuOid);
    imp.getHakukohdeNimi()
        .add(luoMonikielinenTeksti(hakukohdeOid + "-nimi", HakukohdeImportServiceImpl.Kieli.FI));
    imp.getTarjoajaNimi()
        .add(
            luoMonikielinenTeksti(hakukohdeOid + "-tarjoaja", HakukohdeImportServiceImpl.Kieli.FI));
    imp.getHakuKausi().add(luoMonikielinenTeksti("Syksy", HakukohdeImportServiceImpl.Kieli.FI));
    imp.setHakuVuosi("2013");

    HakukohteenValintakoeDTO koe = new HakukohteenValintakoeDTO();
    koe.setOid("oid123");
    koe.setTyyppiUri(OLETUS_VALINTAKOEURI);

    imp.getValintakoe().add(koe);
    HakukohdekoodiDTO koodi = new HakukohdekoodiDTO();
    koodi.setKoodiUri(koodiUri);
    koodi.setArvo(koodiUri);
    koodi.setNimiFi(koodiUri);
    koodi.setNimiSv(koodiUri);
    koodi.setNimiEn(koodiUri);

    imp.setHakukohdekoodi(koodi);
    return imp;
  }

  @Test
  public void testSanitizeKoodi() {
    HakukohdeImportServiceImpl serviceImpl = new HakukohdeImportServiceImpl();

    final String uri1 = "http://koodinuri";
    Assert.assertEquals(uri1, serviceImpl.sanitizeKoodiUri(uri1));

    final String uri2 = "http://koodinur2#1";
    final String uri2Expected = "http://koodinur2";
    Assert.assertEquals(uri2Expected, serviceImpl.sanitizeKoodiUri(uri2));

    final String uri3 = "http://koodinur3#1#5";
    final String uri3Expected = "http://koodinur3";
    Assert.assertEquals(uri3Expected, serviceImpl.sanitizeKoodiUri(uri3));
    Assert.assertNull(null);
  }

  @Test
  public void testImportNewHakukohde() {
    final String hakukohdeOid = "uusiHakukohdeOid";
    final String hakuOid = "uusiHakuOid";
    final String koodiUri = "uusihakukohdekoodiuri";

    assertNull(hakukohdekoodiDAO.readByUri(koodiUri));
    assertNull(hakukohdeViiteDAO.readByOid(hakukohdeOid));

    HakukohdeImportDTO importData = luoHakukohdeImportDTO(hakukohdeOid, hakuOid, koodiUri);
    hakukohdeImportService.tuoHakukohde(importData);

    Hakukohdekoodi koodi = hakukohdekoodiDAO.readByUri(koodiUri);
    assertEquals(koodiUri, koodi.getUri());

    List<HakukohdeViite> hakukohteet = hakukohdeViiteDAO.readByHakukohdekoodiUri(koodiUri);
    assertEquals(1, hakukohteet.size());
    HakukohdeViite koodiHakukohde = hakukohteet.get(0);
    HakukohdeViite hakukohde = hakukohdeViiteDAO.readByOid(hakukohdeOid);
    assertEquals(koodiHakukohde, hakukohde);
    assertEquals(hakukohdeOid, hakukohde.getOid());
  }

  @Test
  public void testImportHakukohdeWithExistingValintaryhma() {
    final String hakukohdeOid = "uusiHakukohdeOid";
    final String hakuOid = "uusiHakuOid";
    final String koodiUri = "hakukohdekoodiuri2";

    final String valintaryhmaOid = "oid37";

    List<Valintaryhma> valintaryhmas = valintaryhmaDAO.readByHakukohdekoodiUri(koodiUri);
    assertEquals(1, valintaryhmas.size());
    assertEquals(valintaryhmaOid, valintaryhmas.get(0).getOid());

    List<HakukohdeViite> hakukohdeViites = hakukohdeViiteDAO.readByHakukohdekoodiUri(koodiUri);
    assertEquals(0, hakukohdeViites.size());
    assertNull(hakukohdeViiteDAO.readByOid(hakukohdeOid));

    assertEquals(0, hakukohdeViiteDAO.findByValintaryhmaOid(valintaryhmaOid).size());

    HakukohdeImportDTO importData = luoHakukohdeImportDTO(hakukohdeOid, hakuOid, koodiUri);
    hakukohdeImportService.tuoHakukohde(importData);

    valintaryhmas = valintaryhmaDAO.readByHakukohdekoodiUri(koodiUri);
    hakukohdeViites = hakukohdeViiteDAO.readByHakukohdekoodiUri(koodiUri);
    assertEquals(1, valintaryhmas.size());
    assertEquals(valintaryhmaOid, valintaryhmas.get(0).getOid());
    assertEquals(1, hakukohdeViites.size());

    HakukohdeViite koodiHakukohde = hakukohdeViites.get(0);
    HakukohdeViite hakukohde = hakukohdeViiteDAO.readByOid(hakukohdeOid);
    assertFalse(hakukohde.getManuaalisestiSiirretty());
    assertEquals(koodiHakukohde, hakukohde);

    List<HakukohdeViite> hakukohteetByValintaryhma =
        hakukohdeViiteDAO.findByValintaryhmaOid(valintaryhmaOid);
    assertEquals(1, hakukohteetByValintaryhma.size());
    assertEquals(hakukohde, hakukohteetByValintaryhma.get(0));
  }

  @Test
  public void testImportHakukohdeUnderNewValintaryhma() {
    // Oletetaan että kannassa on hakukohde, joka on valintaryhmän alla
    // mutta hakukohde pitäisi synkata toisen
    // valintaryhmän alle. Toisin sanoen hakukohteen määrittävä
    // hakukohdekoodi viittaa eri valintaryhmään kuin mihin
    // hakukohde on tällä hetkellä määritelty.

    final String valintaryhmaOidAluksi = "oid40";
    final String valintaryhmaOidLopuksi = "oid41";

    final String hakuOid = "hakuoid1";
    final String hakukohdeOid = "oid14";
    final String hakukohdekoodiUri = "hakukohdekoodiuri4";
    {
      List<Valintaryhma> valintaryhmas = valintaryhmaDAO.readByHakukohdekoodiUri(hakukohdekoodiUri);
      List<HakukohdeViite> hakukohdeViites =
          hakukohdeViiteDAO.readByHakukohdekoodiUri(hakukohdekoodiUri);
      assertEquals(1, valintaryhmas.size());
      assertEquals(valintaryhmaOidLopuksi, valintaryhmas.get(0).getOid());
      assertEquals(0, hakukohdeViites.size());

      List<HakukohdeViite> hakukohteet =
          hakukohdeService.findByValintaryhmaOid(valintaryhmaOidAluksi);
      assertEquals(1, hakukohteet.size());
      assertEquals(hakukohdeOid, hakukohteet.get(0).getOid());
      assertNull(hakukohteet.get(0).getManuaalisestiSiirretty());

      assertEquals(0, hakukohdeService.findByValintaryhmaOid(valintaryhmaOidLopuksi).size());

      List<ValinnanVaihe> vaiheet = valinnanVaiheService.findByHakukohde(hakukohdeOid);
      assertEquals(4, vaiheet.size());
      assertTrue(
          vaiheet.get(0).getId().equals(95L)
              && vaiheet.get(0).getMasterValinnanVaihe().getId().equals(94L));
      assertTrue(
          vaiheet.get(1).getId().equals(96L) && vaiheet.get(1).getMasterValinnanVaihe() == null);
      assertTrue(
          vaiheet.get(2).getId().equals(97L)
              && vaiheet.get(2).getMasterValinnanVaihe().getId().equals(93L));
      assertTrue(
          vaiheet.get(3).getId().equals(98L) && vaiheet.get(3).getMasterValinnanVaihe() == null);
    }

    HakukohdeImportDTO importData = luoHakukohdeImportDTO(hakukohdeOid, hakuOid, hakukohdekoodiUri);
    hakukohdeImportService.tuoHakukohde(importData);

    {
      List<Valintaryhma> valintaryhmas = valintaryhmaDAO.readByHakukohdekoodiUri(hakukohdekoodiUri);
      List<HakukohdeViite> hakukohdeViites =
          hakukohdeViiteDAO.readByHakukohdekoodiUri(hakukohdekoodiUri);
      assertEquals(1, valintaryhmas.size());
      assertEquals(valintaryhmaOidLopuksi, valintaryhmas.get(0).getOid());
      assertEquals(1, hakukohdeViites.size());
      assertEquals(hakukohdeOid, hakukohdeViites.get(0).getOid());

      assertEquals(0, hakukohdeService.findByValintaryhmaOid(valintaryhmaOidAluksi).size());
      List<HakukohdeViite> hakukohteet =
          hakukohdeService.findByValintaryhmaOid(valintaryhmaOidLopuksi);
      assertEquals(1, hakukohteet.size());
      assertEquals(hakukohdeOid, hakukohteet.get(0).getOid());
      assertFalse(hakukohteet.get(0).getManuaalisestiSiirretty());

      // Hakukohteelle suoraan määriteltyjen valinnanvaiheiden tulisi
      // tulla periytyvien valinnan vaiheiden
      // jälkeen.
      List<ValinnanVaihe> vaiheet = valinnanVaiheService.findByHakukohde(hakukohdeOid);
      assertEquals(3, vaiheet.size());
      assertTrue(vaiheet.get(0).getMasterValinnanVaihe().getId().equals(99L));
      assertTrue(
          vaiheet.get(1).getId().equals(96L) && vaiheet.get(1).getMasterValinnanVaihe() == null);
      assertTrue(
          vaiheet.get(2).getId().equals(98L) && vaiheet.get(2).getMasterValinnanVaihe() == null);
    }
  }

  @Test
  public void testImportHakukohdeOutsideValintaryhma() {
    // Oletetaan että kannassa on hakukohde, joka on valintaryhmän alla
    // mutta synkkaus yrittää siirtää hakukohteen juureen
    // säilytetään vanha valintaryhmä

    final String valintaryhmaOidAluksi = "oid40";

    final String hakuOid = "hakuoid5";
    final String hakukohdeOid = "oid14";
    final String hakukohdekoodiUri = "hakukohdekoodiuri5";
    {
      List<Valintaryhma> valintaryhmas = valintaryhmaDAO.readByHakukohdekoodiUri(hakukohdekoodiUri);
      List<HakukohdeViite> hakukohdeViites =
          hakukohdeViiteDAO.readByHakukohdekoodiUri(hakukohdekoodiUri);
      assertEquals(0, valintaryhmas.size());
      assertEquals(0, hakukohdeViites.size());

      List<HakukohdeViite> hakukohteet =
          hakukohdeService.findByValintaryhmaOid(valintaryhmaOidAluksi);
      assertEquals(1, hakukohteet.size());
      assertEquals(hakukohdeOid, hakukohteet.get(0).getOid());
      assertNull(hakukohteet.get(0).getManuaalisestiSiirretty());

      List<ValinnanVaihe> vaiheet = valinnanVaiheService.findByHakukohde(hakukohdeOid);
      assertEquals(4, vaiheet.size());
      assertTrue(
          vaiheet.get(0).getId().equals(95L)
              && vaiheet.get(0).getMasterValinnanVaihe().getId().equals(94L));
      assertTrue(
          vaiheet.get(1).getId().equals(96L) && vaiheet.get(1).getMasterValinnanVaihe() == null);
      assertTrue(
          vaiheet.get(2).getId().equals(97L)
              && vaiheet.get(2).getMasterValinnanVaihe().getId().equals(93L));
      assertTrue(
          vaiheet.get(3).getId().equals(98L) && vaiheet.get(3).getMasterValinnanVaihe() == null);
    }

    HakukohdeImportDTO importData = luoHakukohdeImportDTO(hakukohdeOid, hakuOid, hakukohdekoodiUri);
    hakukohdeImportService.tuoHakukohde(importData);

    {
      List<Valintaryhma> valintaryhmas = valintaryhmaDAO.readByHakukohdekoodiUri(hakukohdekoodiUri);
      List<HakukohdeViite> hakukohdeViites =
          hakukohdeViiteDAO.readByHakukohdekoodiUri(hakukohdekoodiUri);
      assertEquals(0, valintaryhmas.size());
      assertEquals(1, hakukohdeViites.size());
      assertEquals(hakukohdeOid, hakukohdeViites.get(0).getOid());

      assertEquals(1, hakukohdeService.findByValintaryhmaOid(valintaryhmaOidAluksi).size());
      HakukohdeViite hakukohde = hakukohdeService.readByOid(hakukohdeOid);
      assertFalse(hakukohde.getManuaalisestiSiirretty());

      assertEquals(hakukohde.getValintaryhma().getOid(), valintaryhmaOidAluksi);

      List<ValinnanVaihe> vaiheet = valinnanVaiheService.findByHakukohde(hakukohdeOid);
      assertEquals(4, vaiheet.size());
      assertTrue(
          vaiheet.get(0).getId().equals(95L)
              && vaiheet.get(0).getMasterValinnanVaihe().getId().equals(94L));
      assertTrue(
          vaiheet.get(1).getId().equals(96L) && vaiheet.get(1).getMasterValinnanVaihe() == null);
      assertTrue(
          vaiheet.get(2).getId().equals(97L)
              && vaiheet.get(2).getMasterValinnanVaihe().getId().equals(93L));
      assertTrue(
          vaiheet.get(3).getId().equals(98L) && vaiheet.get(3).getMasterValinnanVaihe() == null);
    }
  }

  @Test
  public void testHakukohdeSynkassa() {
    // Testaa, että hakukohteelle ei tehdä mitään, jos se on jo valmiiksi
    // oikean valintaryhmän alla
    final String valintaryhmaOidAluksi = "oid40";

    final String hakuOid = "hakuoid4";
    final String hakukohdeOid = "oid14";
    final String hakukohdekoodiUri = "hakukohdekoodiuri6";
    {
      List<Valintaryhma> valintaryhmas = valintaryhmaDAO.readByHakukohdekoodiUri(hakukohdekoodiUri);
      List<HakukohdeViite> hakukohdeViites =
          hakukohdeViiteDAO.readByHakukohdekoodiUri(hakukohdekoodiUri);
      assertEquals(1, valintaryhmas.size());
      assertEquals(valintaryhmaOidAluksi, valintaryhmas.get(0).getOid());
      assertEquals(0, hakukohdeViites.size());

      List<HakukohdeViite> hakukohteet =
          hakukohdeService.findByValintaryhmaOid(valintaryhmaOidAluksi);
      assertEquals(1, hakukohteet.size());
      HakukohdeViite hakukohde = hakukohteet.get(0);
      assertEquals(hakukohdeOid, hakukohde.getOid());
      assertNull(hakukohde.getManuaalisestiSiirretty());

      List<ValinnanVaihe> vaiheet = valinnanVaiheService.findByHakukohde(hakukohdeOid);
      assertEquals(4, vaiheet.size());
      assertTrue(
          vaiheet.get(0).getId().equals(95L)
              && vaiheet.get(0).getMasterValinnanVaihe().getId().equals(94L));
      assertTrue(
          vaiheet.get(1).getId().equals(96L) && vaiheet.get(1).getMasterValinnanVaihe() == null);
      assertTrue(
          vaiheet.get(2).getId().equals(97L)
              && vaiheet.get(2).getMasterValinnanVaihe().getId().equals(93L));
      assertTrue(
          vaiheet.get(3).getId().equals(98L) && vaiheet.get(3).getMasterValinnanVaihe() == null);
    }

    HakukohdeImportDTO importData = luoHakukohdeImportDTO(hakukohdeOid, hakuOid, hakukohdekoodiUri);
    hakukohdeImportService.tuoHakukohde(importData);

    {
      List<Valintaryhma> valintaryhmas = valintaryhmaDAO.readByHakukohdekoodiUri(hakukohdekoodiUri);
      List<HakukohdeViite> hakukohdeViites =
          hakukohdeViiteDAO.readByHakukohdekoodiUri(hakukohdekoodiUri);
      assertEquals(1, valintaryhmas.size());
      assertEquals(valintaryhmaOidAluksi, valintaryhmas.get(0).getOid());
      assertEquals(1, hakukohdeViites.size());
      assertEquals(hakukohdeOid, hakukohdeViites.get(0).getOid());

      List<HakukohdeViite> hakukohteet =
          hakukohdeService.findByValintaryhmaOid(valintaryhmaOidAluksi);
      assertEquals(1, hakukohteet.size());
      HakukohdeViite hakukohde = hakukohteet.get(0);
      assertEquals(hakukohdeOid, hakukohde.getOid());
      assertFalse(hakukohde.getManuaalisestiSiirretty());

      List<ValinnanVaihe> vaiheet = valinnanVaiheService.findByHakukohde(hakukohdeOid);
      assertEquals(4, vaiheet.size());
      assertTrue(
          vaiheet.get(0).getId().equals(95L)
              && vaiheet.get(0).getMasterValinnanVaihe().getId().equals(94L));
      assertTrue(
          vaiheet.get(1).getId().equals(96L) && vaiheet.get(1).getMasterValinnanVaihe() == null);
      assertTrue(
          vaiheet.get(2).getId().equals(97L)
              && vaiheet.get(2).getMasterValinnanVaihe().getId().equals(93L));
      assertTrue(
          vaiheet.get(3).getId().equals(98L) && vaiheet.get(3).getMasterValinnanVaihe() == null);
    }
  }

  @Test
  public void testKaksiValintaryhmaaSamoillaKoodeilla() {
    // Kannassa on kaksi valintaryhmää samoilla hakukohdekoodeilla, samoilla
    // opetuskielikoodeilla, samoilla
    // valintakoekooddeilla ja samoilla hakuoideilla. Uuden hakukohteen tulisi valua
    // juureen, koska
    // valintaryhmää ei voida yksilöidä.

    final String valintaryhmaOid1 = "oid46";
    final String valintaryhmaOid2 = "oid47";

    final String hakuOid = "hakuoid2";
    final String hakukohdeOid = "uusihakukohdeoid";
    final String hakukohdekoodiUri = "hakukohdekoodiuri12";

    {
      assertNull(hakukohdeViiteDAO.readByOid(hakukohdeOid));
      Hakukohdekoodi koodi = hakukohdekoodiDAO.readByUri(hakukohdekoodiUri);
      assertNotNull(koodi);
      List<Valintaryhma> valintaryhmas = valintaryhmaDAO.readByHakukohdekoodiUri(hakukohdekoodiUri);

      assertEquals(2, valintaryhmas.size());
      List<Valintaryhma> valintaryhmat = new ArrayList<Valintaryhma>(valintaryhmas);
      Collections.sort(
          valintaryhmat,
          new Comparator<Valintaryhma>() {
            @Override
            public int compare(Valintaryhma o1, Valintaryhma o2) {
              return o1.getOid().compareTo(o2.getOid());
            }
          });

      assertEquals(valintaryhmaOid1, valintaryhmat.get(0).getOid());
      assertEquals(valintaryhmaOid2, valintaryhmat.get(1).getOid());

      Valintaryhma valintaryhma1 = valintaryhmaService.readByOid(valintaryhmaOid1);
      Valintaryhma valintaryhma2 = valintaryhmaService.readByOid(valintaryhmaOid2);
      // assertTrue(valintaryhma1.getOpetuskielikoodit().size() == 1
      // &&
      // HakukohdeImportServiceImpl.Kieli.FI.getUri().equals(valintaryhma1.getOpetuskielikoodit().iterator().next().getUri()));
      // assertTrue(valintaryhma2.getOpetuskielikoodit().size() == 1
      // &&
      // HakukohdeImportServiceImpl.Kieli.FI.getUri().equals(valintaryhma2.getOpetuskielikoodit().iterator().next().getUri()));

      assertTrue(
          valintaryhma1.getValintakoekoodit().size() == 1
              && OLETUS_VALINTAKOEURI.equals(
                  valintaryhma1.getValintakoekoodit().iterator().next().getUri()));
      assertTrue(
          valintaryhma2.getValintakoekoodit().size() == 1
              && OLETUS_VALINTAKOEURI.equals(
                  valintaryhma2.getValintakoekoodit().iterator().next().getUri()));
    }

    HakukohdeImportDTO importData = luoHakukohdeImportDTO(hakukohdeOid, hakuOid, hakukohdekoodiUri);
    hakukohdeImportService.tuoHakukohde(importData);

    {
      HakukohdeViite hakukohde = hakukohdeViiteDAO.readByOid(hakukohdeOid);
      assertFalse(hakukohde.getManuaalisestiSiirretty());
      assertNotNull(hakukohde);
      assertNull(hakukohde.getValintaryhma());
    }

    // Poistetaan toiselta valintaryhmältä hakuoidi ja katsotaan että hakukohde putoaa oikeaan
    // ryhmään
    {
      Valintaryhma valintaryhma2 = valintaryhmaService.readByOid(valintaryhmaOid2);
      ValintaperusteetModelMapper mapper = new ValintaperusteetModelMapper();
      ValintaryhmaCreateDTO dto = mapper.map(valintaryhma2, ValintaryhmaCreateDTO.class);
      dto.setHakuoid(null);
      valintaryhmaService.update(valintaryhmaOid2, dto);
    }

    hakukohdeImportService.tuoHakukohde(importData);

    {
      HakukohdeViite hakukohde = hakukohdeViiteDAO.readByOid(hakukohdeOid);
      assertFalse(hakukohde.getManuaalisestiSiirretty());
      assertNotNull(hakukohde);
      assertEquals(valintaryhmaOid1, hakukohde.getValintaryhma().getOid());
    }
  }

  @Test
  public void testPaivitaAloituspaikkojenLkm() {
    final String valintaryhmaOid = "oid48";

    final String hakuOid = "hakuoid3";
    final String hakukohdeOid = "uusihakukohdeoid";
    final String hakukohdekoodiUri = "hakukohdekoodiuri13";

    final int aloituspaikat = 100;
    {
      assertNull(hakukohdeViiteDAO.readByOid(hakukohdeOid));
      Valintaryhma valintaryhma = valintaryhmaService.readByOid(valintaryhmaOid);
      assertNotNull(valintaryhma);

      List<ValinnanVaihe> valinnanVaiheet =
          valinnanVaiheService.findByValintaryhma(valintaryhmaOid);
      assertEquals(3, valinnanVaiheet.size());

      ValinnanVaihe vaihe = valinnanVaiheet.get(2);
      assertEquals(
          fi.vm.sade.service.valintaperusteet.dto.model.ValinnanVaiheTyyppi.TAVALLINEN,
          vaihe.getValinnanVaiheTyyppi());
      assertNull(vaihe.getMasterValinnanVaihe());

      List<Valintatapajono> jonot = valintatapajonoService.findJonoByValinnanvaihe(vaihe.getOid());
      assertEquals(1, jonot.size());

      Valintatapajono jono = jonot.get(0);
      assertNull(jono.getMasterValintatapajono());
      assertFalse(aloituspaikat == jono.getAloituspaikat().intValue());
    }

    HakukohdeImportDTO importData = luoHakukohdeImportDTO(hakukohdeOid, hakuOid, hakukohdekoodiUri);
    importData.setValinnanAloituspaikat(aloituspaikat);
    importData.setHaunkohdejoukkoUri("haunkohdejoukko_11#1");

    hakukohdeImportService.tuoHakukohde(importData);
    {
      HakukohdeViite hakukohde = hakukohdeViiteDAO.readByOid(hakukohdeOid);
      assertNotNull(hakukohde);
      assertFalse(hakukohde.getManuaalisestiSiirretty());

      List<ValinnanVaihe> valinnanVaiheet = valinnanVaiheService.findByHakukohde(hakukohdeOid);
      assertEquals(3, valinnanVaiheet.size());

      ValinnanVaihe vaihe = valinnanVaiheet.get(2);
      assertEquals(
          fi.vm.sade.service.valintaperusteet.dto.model.ValinnanVaiheTyyppi.TAVALLINEN,
          vaihe.getValinnanVaiheTyyppi());
      assertNotNull(vaihe.getMasterValinnanVaihe());

      List<Valintatapajono> jonot = valintatapajonoService.findJonoByValinnanvaihe(vaihe.getOid());
      assertEquals(1, jonot.size());

      Valintatapajono jono = jonot.get(0);
      assertNotNull(jono.getMasterValintatapajono());
      assertEquals(aloituspaikat, jono.getAloituspaikat().intValue());
    }
  }

  @Test
  public void testKKAlaPaivitaAloituspaikkojenLkm() {
    final String valintaryhmaOid = "oid48";

    final String hakuOid = "hakuoid3";
    final String hakukohdeOid = "uusihakukohdeoid";
    final String hakukohdekoodiUri = "hakukohdekoodiuri13";

    final int aloituspaikat = 100;
    {
      assertNull(hakukohdeViiteDAO.readByOid(hakukohdeOid));
      Valintaryhma valintaryhma = valintaryhmaService.readByOid(valintaryhmaOid);
      assertNotNull(valintaryhma);

      List<ValinnanVaihe> valinnanVaiheet =
          valinnanVaiheService.findByValintaryhma(valintaryhmaOid);
      assertEquals(3, valinnanVaiheet.size());

      ValinnanVaihe vaihe = valinnanVaiheet.get(2);
      assertEquals(
          fi.vm.sade.service.valintaperusteet.dto.model.ValinnanVaiheTyyppi.TAVALLINEN,
          vaihe.getValinnanVaiheTyyppi());
      assertNull(vaihe.getMasterValinnanVaihe());

      List<Valintatapajono> jonot = valintatapajonoService.findJonoByValinnanvaihe(vaihe.getOid());
      assertEquals(1, jonot.size());

      Valintatapajono jono = jonot.get(0);
      assertNull(jono.getMasterValintatapajono());
      assertFalse(aloituspaikat == jono.getAloituspaikat().intValue());
    }

    HakukohdeImportDTO importData = luoHakukohdeImportDTO(hakukohdeOid, hakuOid, hakukohdekoodiUri);
    importData.setValinnanAloituspaikat(aloituspaikat);
    importData.setHaunkohdejoukkoUri("haunkohdejoukko_12#1");

    hakukohdeImportService.tuoHakukohde(importData);
    {
      HakukohdeViite hakukohde = hakukohdeViiteDAO.readByOid(hakukohdeOid);
      assertNotNull(hakukohde);
      assertFalse(hakukohde.getManuaalisestiSiirretty());

      List<ValinnanVaihe> valinnanVaiheet = valinnanVaiheService.findByHakukohde(hakukohdeOid);
      assertEquals(3, valinnanVaiheet.size());

      ValinnanVaihe vaihe = valinnanVaiheet.get(2);
      assertEquals(
          fi.vm.sade.service.valintaperusteet.dto.model.ValinnanVaiheTyyppi.TAVALLINEN,
          vaihe.getValinnanVaiheTyyppi());
      assertNotNull(vaihe.getMasterValinnanVaihe());

      List<Valintatapajono> jonot = valintatapajonoService.findJonoByValinnanvaihe(vaihe.getOid());
      assertEquals(1, jonot.size());

      Valintatapajono jono = jonot.get(0);
      assertNotNull(jono.getMasterValintatapajono());
      assertFalse(aloituspaikat == jono.getAloituspaikat().intValue());
    }
  }

  @Test
  public void testManuaalisestiSiirrettyHakukohde() {
    // Testaa, että hakukohteelle ei tehdä mitään, jos se on siirretty
    // manuaalisesti
    final String valintaryhmaOid = "oid55";

    final String hakuOid = "hakuoid1";
    final String hakukohdeOid = "oid20";
    final String hakukohdekoodiUri = "hakukohdekoodiuri21";
    {
      List<Valintaryhma> valintaryhmas = valintaryhmaDAO.readByHakukohdekoodiUri(hakukohdekoodiUri);
      assertEquals(1, valintaryhmas.size());
      assertEquals(valintaryhmaOid, valintaryhmas.get(0).getOid());

      List<HakukohdeViite> hakukohdeViites =
          hakukohdeViiteDAO.readByHakukohdekoodiUri(hakukohdekoodiUri);
      assertEquals(1, hakukohdeViites.size());
      assertEquals(hakukohdeOid, hakukohdeViites.get(0).getOid());

      HakukohdeViite hakukohde = hakukohdeService.readByOid(hakukohdeOid);
      assertEquals(hakukohdeOid, hakukohde.getOid());
      assertNull(hakukohde.getValintaryhma());
      assertTrue(hakukohde.getManuaalisestiSiirretty());

      List<HakukohdeViite> hakukohteet = hakukohdeService.findByValintaryhmaOid(valintaryhmaOid);
      assertEquals(0, hakukohteet.size());
    }

    HakukohdeImportDTO importData = luoHakukohdeImportDTO(hakukohdeOid, hakuOid, hakukohdekoodiUri);
    hakukohdeImportService.tuoHakukohde(importData);

    {
      List<Valintaryhma> valintaryhmas = valintaryhmaDAO.readByHakukohdekoodiUri(hakukohdekoodiUri);
      assertEquals(1, valintaryhmas.size());
      assertEquals(valintaryhmaOid, valintaryhmas.get(0).getOid());

      List<HakukohdeViite> hakukohdeViites =
          hakukohdeViiteDAO.readByHakukohdekoodiUri(hakukohdekoodiUri);
      assertEquals(1, hakukohdeViites.size());
      assertEquals(hakukohdeOid, hakukohdeViites.get(0).getOid());

      HakukohdeViite hakukohde = hakukohdeService.readByOid(hakukohdeOid);
      assertEquals(hakukohdeOid, hakukohde.getOid());
      assertNull(hakukohde.getValintaryhma());
      assertTrue(hakukohde.getManuaalisestiSiirretty());

      List<HakukohdeViite> hakukohteet = hakukohdeService.findByValintaryhmaOid(valintaryhmaOid);
      assertEquals(0, hakukohteet.size());
    }
  }

  @Test
  public void testLisaaValintaPerusteet() {
    final String valintaryhmaOid = "oid48";

    final String hakuOid = "hakuoid1";
    final String hakukohdeOid = "uusihakukohdeoid";
    final String hakukohdekoodiUri = "hakukohdekoodiuri13";

    HakukohdeImportDTO importData = luoHakukohdeImportDTO(hakukohdeOid, hakuOid, hakukohdekoodiUri);
    AvainArvoDTO avainArvo = new AvainArvoDTO();

    avainArvo.setAvain("paasykoe_min");
    avainArvo.setArvo("12.0");
    importData.getValintaperuste().add(avainArvo);

    avainArvo = new AvainArvoDTO();
    avainArvo.setAvain("kielikoe_tunniste");
    avainArvo.setArvo("tämä on kielikokeen tunniste");
    importData.getValintaperuste().add(avainArvo);

    avainArvo = new AvainArvoDTO();
    avainArvo.setAvain("MU_painokerroin");
    avainArvo.setArvo("15.0");
    importData.getValintaperuste().add(avainArvo);

    avainArvo = new AvainArvoDTO();
    avainArvo.setAvain("A1_EN_painokerroin");
    avainArvo.setArvo("35.0");
    importData.getValintaperuste().add(avainArvo);

    avainArvo = new AvainArvoDTO();
    avainArvo.setAvain("B3_FR_painokerroin");
    avainArvo.setArvo("43.0");
    importData.getValintaperuste().add(avainArvo);

    hakukohdeImportService.tuoHakukohde(importData);
    {
      HakukohdeViite hakukohde = hakukohdeViiteDAO.readByOid(hakukohdeOid);
      assertNotNull(hakukohde);

      assertEquals(
          "12.0", hakukohde.getHakukohteenValintaperusteet().get("paasykoe_min").getArvo());
      assertEquals(
          "tämä on kielikokeen tunniste",
          hakukohde.getHakukohteenValintaperusteet().get("kielikoe_tunniste").getArvo());
      assertEquals(
          "15.0", hakukohde.getHakukohteenValintaperusteet().get("MU_painokerroin").getArvo());
      assertEquals(
          "35.0", hakukohde.getHakukohteenValintaperusteet().get("A1_EN_painokerroin").getArvo());
      assertEquals(
          "43.0", hakukohde.getHakukohteenValintaperusteet().get("B3_FR_painokerroin").getArvo());
    }

    avainArvo = new AvainArvoDTO();
    avainArvo.setAvain("A1_EN_painokerroin");
    avainArvo.setArvo("3.0");
    importData.getValintaperuste().add(avainArvo);

    avainArvo = new AvainArvoDTO();
    avainArvo.setAvain("B3_FR_painokerroin");
    avainArvo.setArvo("1.0");
    importData.getValintaperuste().add(avainArvo);

    hakukohdeImportService.tuoHakukohde(importData);

    {
      HakukohdeViite hakukohde = hakukohdeViiteDAO.readByOid(hakukohdeOid);
      assertNotNull(hakukohde);

      assertEquals(
          "3.0", hakukohde.getHakukohteenValintaperusteet().get("A1_EN_painokerroin").getArvo());
      assertEquals(
          "1.0", hakukohde.getHakukohteenValintaperusteet().get("B3_FR_painokerroin").getArvo());
    }
  }
}
