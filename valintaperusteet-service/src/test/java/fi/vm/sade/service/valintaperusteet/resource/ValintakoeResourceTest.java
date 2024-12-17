package fi.vm.sade.service.valintaperusteet.resource;

import static org.junit.jupiter.api.Assertions.*;

import fi.vm.sade.service.valintaperusteet.WithSpringBoot;
import fi.vm.sade.service.valintaperusteet.annotation.DataSetLocation;
import fi.vm.sade.service.valintaperusteet.dto.ValintakoeDTO;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.dto.model.Koekutsu;
import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import fi.vm.sade.service.valintaperusteet.model.Valintakoe;
import fi.vm.sade.service.valintaperusteet.service.exception.ValintakoettaEiVoiLisataException;
import fi.vm.sade.service.valintaperusteet.util.TestUtil;
import fi.vm.sade.valinta.sharedutils.FakeAuthenticationInitialiser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

/** User: kwuoti Date: 15.4.2013 Time: 18.17 */
@DataSetLocation("classpath:test-data.xml")
public class ValintakoeResourceTest extends WithSpringBoot {

  private ValintakoeResource valintakoeResource = new ValintakoeResource();
  private TestUtil testUtil = new TestUtil(this.getClass());
  private ValintaperusteetModelMapper modelMapper = new ValintaperusteetModelMapper();
  private HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
  private HttpSession session = Mockito.mock(HttpSession.class);

  @Autowired private ApplicationContext applicationContext;

  @BeforeEach
  public void setUp() {
    applicationContext.getAutowireCapableBeanFactory().autowireBean(valintakoeResource);
    FakeAuthenticationInitialiser.fakeAuthentication();
    Mockito.when(request.getSession(false)).thenReturn(session);
  }

  @Test
  public void testReadByOid() throws Exception {
    final String oid = "oid1";
    ValintakoeDTO valintakoe = valintakoeResource.readByOid(oid);
    testUtil.lazyCheck(JsonViews.Basic.class, valintakoe);
  }

  @Test
  public void testUpdate() {
    final String oid = "oid1";
    final Long laskentakaavaId = 102L;

    ValintakoeDTO saved = valintakoeResource.readByOid(oid);

    ValintakoeDTO koe = new ValintakoeDTO();
    koe.setKuvaus("uusi kuvaus");
    koe.setNimi("uusi nimi");
    koe.setTunniste("uusi tunniste");
    koe.setLaskentakaavaId(laskentakaavaId);
    koe.setKutsunKohde(Koekutsu.YLIN_TOIVE);

    assertFalse(saved.getNimi().equals(koe.getNimi()));
    assertFalse(saved.getKuvaus().equals(koe.getKuvaus()));
    assertFalse(saved.getTunniste().equals(koe.getTunniste()));
    assertFalse(saved.getLaskentakaavaId().equals(koe.getLaskentakaavaId()));

    valintakoeResource.update(oid, koe, request);

    saved = valintakoeResource.readByOid(oid);

    assertEquals(koe.getKuvaus(), saved.getKuvaus());
    assertEquals(koe.getNimi(), saved.getNimi());
    assertEquals(koe.getTunniste(), saved.getTunniste());
    assertEquals(koe.getLaskentakaavaId(), saved.getLaskentakaavaId());
  }

  @Test
  public void testUpdateSetLaskentakaavaNull() {
    final String oid = "oid1";
    ValintakoeDTO saved = valintakoeResource.readByOid(oid);

    ValintakoeDTO koe = new ValintakoeDTO();
    koe.setKuvaus("uusi kuvaus");
    koe.setNimi("uusi nimi");
    koe.setTunniste("uusi tunniste");
    koe.setLaskentakaavaId(null);
    koe.setKutsunKohde(Koekutsu.YLIN_TOIVE);

    assertFalse(saved.getNimi().equals(koe.getNimi()));
    assertFalse(saved.getKuvaus().equals(koe.getKuvaus()));
    assertFalse(saved.getTunniste().equals(koe.getTunniste()));
    assertNotNull(saved.getLaskentakaavaId());

    valintakoeResource.update(oid, koe, request);

    saved = valintakoeResource.readByOid(oid);

    assertEquals(koe.getKuvaus(), saved.getKuvaus());
    assertEquals(koe.getNimi(), saved.getNimi());
    assertEquals(koe.getTunniste(), saved.getTunniste());
    assertNull(saved.getLaskentakaavaId());
  }

  @Test
  public void testMappings() {
    Valintakoe koe = new Valintakoe();
    koe.setAktiivinen(true);
    koe.setKutsunKohde(Koekutsu.YLIN_TOIVE);
    koe.setKutsunKohdeAvain("jeppis");
    koe.setKutsutaankoKaikki(true);
    koe.setLahetetaankoKoekutsut(false);
    koe.setNimi("Koe");
    koe.setOid("KoeOid");
    koe.setTunniste("KoeTunniste");

    ValintakoeDTO ylinToive = modelMapper.map(koe, ValintakoeDTO.class);
    assertTrue(ylinToive.getAktiivinen());
    assertEquals(ylinToive.getKutsunKohde(), Koekutsu.YLIN_TOIVE);
    assertEquals(ylinToive.getKutsunKohdeAvain(), "jeppis");
    assertTrue(ylinToive.getKutsutaankoKaikki());
    assertFalse(ylinToive.getLahetetaankoKoekutsut());
    assertEquals(ylinToive.getNimi(), "Koe");
    assertEquals(ylinToive.getOid(), "KoeOid");
    assertEquals(ylinToive.getTunniste(), "KoeTunniste");

    koe.setKutsunKohde(Koekutsu.HAKIJAN_VALINTA);

    ValintakoeDTO hakijanValinta = modelMapper.map(koe, ValintakoeDTO.class);
    assertTrue(hakijanValinta.getAktiivinen());
    assertEquals(hakijanValinta.getKutsunKohde(), Koekutsu.HAKIJAN_VALINTA);
    assertEquals(hakijanValinta.getKutsunKohdeAvain(), "jeppis");
    assertFalse(hakijanValinta.getKutsutaankoKaikki());
    assertFalse(hakijanValinta.getLahetetaankoKoekutsut());
    assertEquals(hakijanValinta.getNimi(), "Koe");
    assertEquals(hakijanValinta.getOid(), "KoeOid");
    assertEquals(hakijanValinta.getTunniste(), "KoeTunniste");
  }

  @Test
  public void testUpdateValintakoeWithExistingTunniste() {
    final String oid = "oid1";
    ValintakoeDTO valintakoe = valintakoeResource.readByOid(oid);

    valintakoe.setTunniste("valintakoetunniste2");

    assertThrows(
        ValintakoettaEiVoiLisataException.class,
        () -> valintakoeResource.update(oid, valintakoe, request));
  }
}
