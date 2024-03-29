package fi.vm.sade.service.valintaperusteet.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import fi.vm.sade.service.valintaperusteet.WithSpringBoot;
import fi.vm.sade.service.valintaperusteet.annotation.DataSetLocation;
import fi.vm.sade.service.valintaperusteet.dao.HakukohdekoodiDAO;
import fi.vm.sade.service.valintaperusteet.dao.ValinnanVaiheDAO;
import fi.vm.sade.service.valintaperusteet.dao.ValintatapajonoDAO;
import fi.vm.sade.service.valintaperusteet.dto.HakukohdeViiteDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValinnanVaiheJonoillaDTO;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.model.Hakijaryhma;
import fi.vm.sade.service.valintaperusteet.model.HakukohdeViite;
import fi.vm.sade.service.valintaperusteet.model.ValinnanVaihe;
import fi.vm.sade.service.valintaperusteet.model.Valintatapajono;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/** User: kwuoti Date: 20.2.2013 Time: 9.05 */
@DataSetLocation("classpath:test-data.xml")
public class HakukohdeServiceTest extends WithSpringBoot {

  @Autowired private ValintatapajonoDAO valintatapajonoDAO;

  @Autowired private ValinnanVaiheDAO valinnanVaiheDAO;

  @Autowired private HakukohdeService hakukohdeService;

  @Autowired private HakukohdekoodiDAO hakukohdekoodiDAO;

  @Autowired private ValintaryhmaService valintaryhmaService;

  @Autowired private HakijaryhmaService hakijaryhmaService;

  @Autowired private ValintaperusteetModelMapper modelMapper;

  @Test
  public void testInsert() {
    final String parentOid = "oid33";
    {
      assertNotNull(valintaryhmaService.readByOid(parentOid));
      List<ValinnanVaihe> vr33Lvaiheet = valinnanVaiheDAO.findByValintaryhma(parentOid);

      assertEquals(2, vr33Lvaiheet.size());
      ValinnanVaihe vaihe80L = vr33Lvaiheet.get(0);
      ValinnanVaihe vaihe81L = vr33Lvaiheet.get(1);

      List<Valintatapajono> vaihe80Ljonot =
          valintatapajonoDAO.findByValinnanVaihe(vaihe80L.getOid());
      List<Valintatapajono> vaihe81Ljonot =
          valintatapajonoDAO.findByValinnanVaihe(vaihe81L.getOid());

      assertEquals(2, vaihe80Ljonot.size());
      assertEquals(1, vaihe81Ljonot.size());
    }

    HakukohdeViiteDTO uusiHakukohde = new HakukohdeViiteDTO();
    uusiHakukohde.setNimi("Uusi hakukohde");
    uusiHakukohde.setOid("oid1234567");
    uusiHakukohde.setHakuoid("uusihakuoid");

    HakukohdeViite lisatty = hakukohdeService.insert(uusiHakukohde, parentOid);
    assertTrue(StringUtils.isNotBlank(lisatty.getOid()));

    {
      assertNotNull(valintaryhmaService.readByOid(parentOid));
      List<ValinnanVaihe> vr33Lvaiheet = valinnanVaiheDAO.findByValintaryhma(parentOid);

      assertEquals(2, vr33Lvaiheet.size());
      ValinnanVaihe vaihe80L = vr33Lvaiheet.get(0);
      ValinnanVaihe vaihe81L = vr33Lvaiheet.get(1);

      List<Valintatapajono> vaihe80Ljonot =
          valintatapajonoDAO.findByValinnanVaihe(vaihe80L.getOid());
      List<Valintatapajono> vaihe81Ljonot =
          valintatapajonoDAO.findByValinnanVaihe(vaihe81L.getOid());

      assertEquals(2, vaihe80Ljonot.size());
      assertEquals(1, vaihe81Ljonot.size());
    }
    {
      assertNotNull(hakukohdeService.readByOid(lisatty.getOid()));
      List<ValinnanVaihe> uusiVaiheet = valinnanVaiheDAO.findByHakukohde(lisatty.getOid());

      assertEquals(2, uusiVaiheet.size());
      ValinnanVaihe uusiVaihe1 = uusiVaiheet.get(0);
      ValinnanVaihe uusiVaihe2 = uusiVaiheet.get(1);

      List<Valintatapajono> uusiVaihe1jonot =
          valintatapajonoDAO.findByValinnanVaihe(uusiVaihe1.getOid());
      List<Valintatapajono> uusiVaihe2jonot =
          valintatapajonoDAO.findByValinnanVaihe(uusiVaihe2.getOid());

      assertEquals(2, uusiVaihe1jonot.size());
      assertEquals(1, uusiVaihe2jonot.size());
    }
  }

  @Test
  public void testInsertIlmanValintaryhmaa() {
    HakukohdeViiteDTO uusiHakukohde = new HakukohdeViiteDTO();
    uusiHakukohde.setNimi("Uusi hakukohde");
    uusiHakukohde.setOid("oid1234567");
    uusiHakukohde.setHakuoid("uusihakuoid");

    HakukohdeViite lisatty = hakukohdeService.insert(uusiHakukohde, null);
    assertNotNull(hakukohdeService.readByOid(lisatty.getOid()));
  }

  @Test
  public void testSiirraHakukohdeToiseenValintaryhmaan() {
    final String hakukohdeOid = "oid18";
    final String valintaryhmaOidEnnen = "oid53";
    final String valintaryhmaOidLopuksi = "oid54";

    final String hakukohdekoodiUri = "hakukohdekoodiuri19";
    final String opetuskielikoodiUri = "kieli_fi";
    final String valintakoekoodiUri = "valintakoeuri1";
    {
      HakukohdeViite hakukohde = hakukohdeService.readByOid(hakukohdeOid);
      assertEquals(valintaryhmaOidEnnen, hakukohde.getValintaryhma().getOid());
      assertNull(hakukohde.getManuaalisestiSiirretty());

      List<ValinnanVaihe> vaiheet = valinnanVaiheDAO.findByHakukohde(hakukohdeOid);
      assertEquals(2, vaiheet.size());
      assertEquals("104", vaiheet.get(0).getOid());
      assertEquals("105", vaiheet.get(1).getOid());
      assertNotNull(vaiheet.get(0).getMasterValinnanVaihe());
      assertNull(vaiheet.get(1).getMasterValinnanVaihe());

      final List<Valintatapajono> vanhatValintatapajonot =
          valintatapajonoDAO.findByValinnanVaihe("104");
      assertEquals(2, vanhatValintatapajonot.size());
      assertEquals("100", vanhatValintatapajonot.get(0).getOid());
      assertNull(vanhatValintatapajonot.get(0).getMasterValintatapajono());
      assertEquals("102", vanhatValintatapajonot.get(1).getOid());
      assertEquals("101", vanhatValintatapajonot.get(1).getMasterValintatapajono().getOid());

      assertEquals(hakukohdekoodiUri, hakukohde.getHakukohdekoodi().getUri());

      assertEquals(1, hakukohde.getValintakokeet().size());
      assertEquals(valintakoekoodiUri, hakukohde.getValintakokeet().iterator().next().getUri());
    }

    hakukohdeService.siirraHakukohdeValintaryhmaan(hakukohdeOid, valintaryhmaOidLopuksi, true);

    {
      HakukohdeViite hakukohde = hakukohdeService.readByOid(hakukohdeOid);
      assertEquals(valintaryhmaOidLopuksi, hakukohde.getValintaryhma().getOid());
      assertTrue(hakukohde.getManuaalisestiSiirretty());

      List<ValinnanVaihe> vaiheet = valinnanVaiheDAO.findByHakukohde(hakukohdeOid);
      assertEquals(2, vaiheet.size());
      assertNotNull(vaiheet.get(0).getMasterValinnanVaihe());
      assertEquals("103", vaiheet.get(0).getMasterValinnanVaihe().getOid());
      assertEquals("105", vaiheet.get(1).getOid());
      assertNull(vaiheet.get(1).getMasterValinnanVaihe());

      final List<Valintatapajono> uudetValintatapajonot =
          valintatapajonoDAO.findByValinnanVaihe(vaiheet.get(0).getOid());
      assertEquals(1, uudetValintatapajonot.size());
      assertEquals("jono100", uudetValintatapajonot.get(0).getNimi());

      assertEquals(hakukohdekoodiUri, hakukohde.getHakukohdekoodi().getUri());

      assertEquals(1, hakukohde.getValintakokeet().size());
      assertEquals(valintakoekoodiUri, hakukohde.getValintakokeet().iterator().next().getUri());
    }
  }

  @Test
  public void testSiirraHakukohdeValintaryhmaan() {
    final String hakukohdeOid = "oid19";
    final String valintaryhmaOidLopuksi = "oid54";

    final String hakukohdekoodiUri = "hakukohdekoodiuri20";
    final String opetuskielikoodiUri = "kieli_fi";
    final String valintakoekoodiUri = "valintakoeuri1";
    {
      HakukohdeViite hakukohde = hakukohdeService.readByOid(hakukohdeOid);

      System.out.println(
          "1 - valintaryhma.getHakijaryhmat(): "
              + hakijaryhmaService.findByValintaryhma(valintaryhmaOidLopuksi));
      System.out.println(
          "1 - hakukohde.getHakijaryhmat(): " + hakijaryhmaService.findByHakukohde(hakukohdeOid));

      assertNull(hakukohde.getValintaryhma());
      assertNull(hakukohde.getManuaalisestiSiirretty());

      List<ValinnanVaihe> vaiheet = valinnanVaiheDAO.findByHakukohde(hakukohdeOid);
      assertEquals(1, vaiheet.size());
      assertEquals("106", vaiheet.get(0).getOid());
      assertNull(vaiheet.get(0).getMasterValinnanVaihe());

      assertEquals(hakukohdekoodiUri, hakukohde.getHakukohdekoodi().getUri());

      assertEquals(1, hakukohde.getValintakokeet().size());
      assertEquals(valintakoekoodiUri, hakukohde.getValintakokeet().iterator().next().getUri());
    }

    hakukohdeService.siirraHakukohdeValintaryhmaan(hakukohdeOid, valintaryhmaOidLopuksi, true);

    {
      HakukohdeViite hakukohde = hakukohdeService.readByOid(hakukohdeOid);
      hakijaryhmaService.findByHakukohde(hakukohdeOid);

      System.out.println(
          "2 - hakukohde.getHakijaryhmat(): " + hakijaryhmaService.findByHakukohde(hakukohdeOid));

      assertEquals(valintaryhmaOidLopuksi, hakukohde.getValintaryhma().getOid());
      assertTrue(hakukohde.getManuaalisestiSiirretty());

      List<ValinnanVaihe> vaiheet = valinnanVaiheDAO.findByHakukohde(hakukohdeOid);
      assertEquals(2, vaiheet.size());
      assertNotNull(vaiheet.get(0).getMasterValinnanVaihe());
      assertEquals("103", vaiheet.get(0).getMasterValinnanVaihe().getOid());
      assertEquals("106", vaiheet.get(1).getOid());
      assertNull(vaiheet.get(1).getMasterValinnanVaihe());

      assertEquals(hakukohdekoodiUri, hakukohde.getHakukohdekoodi().getUri());

      assertEquals(1, hakukohde.getValintakokeet().size());
      assertEquals(valintakoekoodiUri, hakukohde.getValintakokeet().iterator().next().getUri());

      List<Hakijaryhma> hakijaryhmat = hakijaryhmaService.findByHakukohde(hakukohdeOid);
      boolean hasHakijaryhma = hakijaryhmat.stream().anyMatch(hr -> hr.getOid().equals("hr2"));
      assertEquals(true, hasHakijaryhma);
      assertEquals(1, hakijaryhmat.size());
    }
  }

  @Test
  public void testSiirraHakukohdePoisValintaryhmasta() {
    final String hakukohdeOid = "oid18";
    final String valintaryhmaOidEnnen = "oid53";

    final String hakukohdekoodiUri = "hakukohdekoodiuri19";
    final String opetuskielikoodiUri = "kieli_fi";
    final String valintakoekoodiUri = "valintakoeuri1";
    {
      HakukohdeViite hakukohde = hakukohdeService.readByOid(hakukohdeOid);
      assertEquals(valintaryhmaOidEnnen, hakukohde.getValintaryhma().getOid());
      assertNull(hakukohde.getManuaalisestiSiirretty());

      List<ValinnanVaihe> vaiheet = valinnanVaiheDAO.findByHakukohde(hakukohdeOid);
      assertEquals(2, vaiheet.size());
      assertEquals("104", vaiheet.get(0).getOid());
      assertEquals("105", vaiheet.get(1).getOid());
      assertNotNull(vaiheet.get(0).getMasterValinnanVaihe());
      assertNull(vaiheet.get(1).getMasterValinnanVaihe());

      assertEquals(hakukohdekoodiUri, hakukohde.getHakukohdekoodi().getUri());

      assertEquals(1, hakukohde.getValintakokeet().size());
      assertEquals(valintakoekoodiUri, hakukohde.getValintakokeet().iterator().next().getUri());
    }

    hakukohdeService.siirraHakukohdeValintaryhmaan(hakukohdeOid, null, true);

    {
      HakukohdeViite hakukohde = hakukohdeService.readByOid(hakukohdeOid);
      assertNull(hakukohde.getValintaryhma());
      assertTrue(hakukohde.getManuaalisestiSiirretty());

      List<ValinnanVaihe> vaiheet = valinnanVaiheDAO.findByHakukohde(hakukohdeOid);
      assertEquals(1, vaiheet.size());
      assertEquals("105", vaiheet.get(0).getOid());
      assertNull(vaiheet.get(0).getMasterValinnanVaihe());

      assertEquals(hakukohdekoodiUri, hakukohde.getHakukohdekoodi().getUri());

      assertEquals(1, hakukohde.getValintakokeet().size());
      assertEquals(valintakoekoodiUri, hakukohde.getValintakokeet().iterator().next().getUri());
    }
  }

  @Test
  public void testSiirraHakukohdeSamaanValintaryhmaan() {
    final String hakukohdeOid = "oid18";
    final String valintaryhmaOid = "oid53";

    final String hakukohdekoodiUri = "hakukohdekoodiuri19";
    final String opetuskielikoodiUri = "kieli_fi";
    final String valintakoekoodiUri = "valintakoeuri1";
    {
      HakukohdeViite hakukohde = hakukohdeService.readByOid(hakukohdeOid);
      assertEquals(valintaryhmaOid, hakukohde.getValintaryhma().getOid());
      assertNull(hakukohde.getManuaalisestiSiirretty());

      List<ValinnanVaihe> vaiheet = valinnanVaiheDAO.findByHakukohde(hakukohdeOid);
      assertEquals(2, vaiheet.size());
      assertEquals("104", vaiheet.get(0).getOid());
      assertEquals("105", vaiheet.get(1).getOid());
      assertNotNull(vaiheet.get(0).getMasterValinnanVaihe());
      assertNull(vaiheet.get(1).getMasterValinnanVaihe());

      assertEquals(hakukohdekoodiUri, hakukohde.getHakukohdekoodi().getUri());

      assertEquals(1, hakukohde.getValintakokeet().size());
      assertEquals(valintakoekoodiUri, hakukohde.getValintakokeet().iterator().next().getUri());
    }

    hakukohdeService.siirraHakukohdeValintaryhmaan(hakukohdeOid, valintaryhmaOid, true);

    {
      HakukohdeViite hakukohde = hakukohdeService.readByOid(hakukohdeOid);
      assertEquals(valintaryhmaOid, hakukohde.getValintaryhma().getOid());
      assertTrue(hakukohde.getManuaalisestiSiirretty());

      List<ValinnanVaihe> vaiheet = valinnanVaiheDAO.findByHakukohde(hakukohdeOid);
      assertEquals(2, vaiheet.size());
      assertEquals("104", vaiheet.get(0).getOid());
      assertEquals("105", vaiheet.get(1).getOid());
      assertNotNull(vaiheet.get(0).getMasterValinnanVaihe());
      assertNull(vaiheet.get(1).getMasterValinnanVaihe());

      assertEquals(hakukohdekoodiUri, hakukohde.getHakukohdekoodi().getUri());

      assertEquals(1, hakukohde.getValintakokeet().size());
      assertEquals(valintakoekoodiUri, hakukohde.getValintakokeet().iterator().next().getUri());
    }
  }

  @Test
  public void testTyhjatVaiheet() {
    List<ValinnanVaihe> list = hakukohdeService.vaiheetJaJonot("nönönöö");
    assertEquals(0, list.size());
    List<ValinnanVaiheJonoillaDTO> dtos =
        modelMapper.mapList(
            hakukohdeService.vaiheetJaJonot("nönönöö"), ValinnanVaiheJonoillaDTO.class);
    assertEquals(0, dtos.size());
  }
}
