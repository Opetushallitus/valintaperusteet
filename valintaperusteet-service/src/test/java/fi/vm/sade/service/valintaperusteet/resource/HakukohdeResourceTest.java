package fi.vm.sade.service.valintaperusteet.resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import fi.vm.sade.service.valintaperusteet.ObjectMapperProvider;
import fi.vm.sade.service.valintaperusteet.WithSpringBoot;
import fi.vm.sade.service.valintaperusteet.annotation.DataSetLocation;
import fi.vm.sade.service.valintaperusteet.dao.ValinnanVaiheDAO;
import fi.vm.sade.service.valintaperusteet.dto.*;
import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import fi.vm.sade.service.valintaperusteet.util.TestUtil;
import fi.vm.sade.valinta.sharedutils.FakeAuthenticationInitialiser;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

/** User: jukais Date: 16.1.2013 Time: 14.15 */
@DataSetLocation("classpath:test-data.xml")
public class HakukohdeResourceTest extends WithSpringBoot {
  private HttpServletRequest request = mock(HttpServletRequest.class);
  private HttpSession session = Mockito.mock(HttpSession.class);

  private HakukohdeResource hakukohdeResource = new HakukohdeResource();
  private TestUtil testUtil = new TestUtil(HakukohdeResourceTest.class);

  private ObjectMapper mapper = new ObjectMapperProvider().getContext(HakukohdeResource.class);

  @Autowired private ApplicationContext applicationContext;

  @Autowired ValinnanVaiheDAO valinnanVaiheDao;

  @BeforeEach
  public void setUp() {
    applicationContext.getAutowireCapableBeanFactory().autowireBean(hakukohdeResource);
    FakeAuthenticationInitialiser.fakeAuthentication();
    when(request.getSession(false)).thenReturn(session);
  }

  @Test
  public void testFindByOid() throws Exception {
    HakukohdeViiteDTO vroid1 = hakukohdeResource.queryFull("oid1");
    assertEquals("haku1", vroid1.getNimi());
    testUtil.lazyCheck(JsonViews.Basic.class, vroid1);
  }

  @Test
  public void testFindAll() throws Exception {
    List<HakukohdeViiteDTO> hakukohdeViites = hakukohdeResource.query(false);
    assertEquals(32, hakukohdeViites.size());
    testUtil.lazyCheck(JsonViews.Basic.class, hakukohdeViites);
  }

  @Test
  public void testFindRoot() throws Exception {
    List<HakukohdeViiteDTO> hakukohdeViites = hakukohdeResource.query(true);
    assertEquals(17, hakukohdeViites.size());
    testUtil.lazyCheck(JsonViews.Basic.class, hakukohdeViites);
  }

  @Test
  public void testInsertNull() {
    HakukohdeViiteDTO valintaryhma = new HakukohdeViiteDTO();
    ResponseEntity<Object> insert =
        hakukohdeResource.insert(new HakukohdeInsertDTO(valintaryhma, null), request);
    assertEquals(500, insert.getStatusCode().value());
  }

  @Test
  public void testInsert() throws Exception {
    HakukohdeViiteCreateDTO hakukohdeDTO = new HakukohdeViiteDTO();
    hakukohdeDTO.setNimi("Uusi valintaryhmä");
    hakukohdeDTO.setOid("uusi oid");
    hakukohdeDTO.setHakuoid("hakuoid");

    ResponseEntity<Object> insert =
        hakukohdeResource.insert(new HakukohdeInsertDTO(hakukohdeDTO, "oid1"), request);
    assertEquals(201, insert.getStatusCode().value());

    testUtil.lazyCheck(JsonViews.Basic.class, insert.getBody());
  }

  @Test
  public void testInsertRoot() throws Exception {
    HakukohdeViiteDTO hakukohdeDto = new HakukohdeViiteDTO();
    hakukohdeDto.setNimi("Uusi valintaryhmä");
    hakukohdeDto.setOid("uusi oid");
    hakukohdeDto.setHakuoid("hakuoid");

    ResponseEntity<Object> insert =
        hakukohdeResource.insert(new HakukohdeInsertDTO(hakukohdeDto, null), request);
    assertEquals(201, insert.getStatusCode().value());

    testUtil.lazyCheck(JsonViews.Basic.class, insert.getBody());
  }

  @Test
  public void testInsertDuplicate() throws Exception {
    HakukohdeViiteDTO hakukohde = new HakukohdeViiteDTO();
    hakukohde.setNimi("Uusi valintaryhmä");
    hakukohde.setOid("oid1");
    ResponseEntity<Object> insert =
        hakukohdeResource.insert(new HakukohdeInsertDTO(hakukohde, null), request);
    assertEquals(500, insert.getStatusCode().value());
    testUtil.lazyCheck(JsonViews.Basic.class, insert.getBody());
  }

  @Test
  public void getValintakoesForHakukohdeReturns404IfViiteNotFound() {
    try {
      hakukohdeResource.valintakoesForHakukohde("IMAGINARY_HAKUKOHDE_OID");
      fail("Should not reach here. Expected to throw exception");
    } catch (ResponseStatusException e) {
      assertEquals("HakukohdeViite (IMAGINARY_HAKUKOHDE_OID) ei ole olemassa.", e.getReason());
      assertEquals(404, e.getStatus().value());
    }
  }

  @Test
  public void testUpdate() throws Exception {
    HakukohdeViiteDTO hkv = hakukohdeResource.queryFull("oid1");
    hkv.setNimi("muokattu");

    ObjectMapper mapper = testUtil.getObjectMapper();

    final String json = mapper.writerWithView(JsonViews.Basic.class).writeValueAsString(hkv);
    HakukohdeViiteCreateDTO fromJson = mapper.readValue(json, HakukohdeViiteCreateDTO.class);

    hakukohdeResource.update(hkv.getOid(), fromJson, request);

    hkv = hakukohdeResource.queryFull("oid1");
    assertEquals("muokattu", hkv.getNimi());
    testUtil.lazyCheck(JsonViews.Basic.class, hkv);
  }

  @Test
  public void testValinnanVaihesForHakukohde() throws Exception {
    List<ValinnanVaiheDTO> valinnanVaihes =
        hakukohdeResource.valinnanVaihesForHakukohde("oid6", "false");
    assertEquals(3, valinnanVaihes.size());
    testUtil.lazyCheck(JsonViews.Basic.class, valinnanVaihes);
  }

  @Test
  public void testValinnanVaihesForHakukohdeWithValisijoittelutieto() throws Exception {
    List<ValinnanVaiheDTO> valinnanVaihes =
        hakukohdeResource.valinnanVaihesForHakukohde("oid6", "true");
    assertEquals(3, valinnanVaihes.size());
    assertTrue(valinnanVaihes.get(0).getHasValisijoittelu());
    assertFalse(valinnanVaihes.get(1).getHasValisijoittelu());
    assertFalse(valinnanVaihes.get(2).getHasValisijoittelu());
    testUtil.lazyCheck(JsonViews.Basic.class, valinnanVaihes);
  }

  @Test
  public void testFindLaskentakaavatByHakukohde() throws Exception {
    List<JarjestyskriteeriDTO> laskentaKaavat = hakukohdeResource.findLaskentaKaavat("oid6");
    assertEquals(3, laskentaKaavat.size());
    testUtil.lazyCheck(JsonViews.Basic.class, laskentaKaavat);
  }

  @Test
  public void testFindAvaimet() throws Exception {
    List<ValintaperusteDTO> valintaperusteet = hakukohdeResource.findAvaimet("oid17");
    assertEquals(2, valintaperusteet.size());

    mapper.writerWithView(JsonViews.Basic.class).writeValueAsString(valintaperusteet);
  }

  @Test
  public void testInsertValinnanVaihe() {
    ValinnanVaiheCreateDTO valinnanVaihe = new ValinnanVaiheCreateDTO();

    valinnanVaihe.setNimi("uusi");
    valinnanVaihe.setAktiivinen(true);
    valinnanVaihe.setValinnanVaiheTyyppi(
        fi.vm.sade.service.valintaperusteet.dto.model.ValinnanVaiheTyyppi.TAVALLINEN);

    ResponseEntity<ValinnanVaiheDTO> response =
        hakukohdeResource.insertValinnanvaihe("oid1", null, valinnanVaihe, request);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    ValinnanVaiheDTO vv = response.getBody();

    valinnanVaihe = new ValinnanVaiheCreateDTO();
    valinnanVaihe.setNimi("uusi");
    valinnanVaihe.setAktiivinen(true);
    valinnanVaihe.setValinnanVaiheTyyppi(
        fi.vm.sade.service.valintaperusteet.dto.model.ValinnanVaiheTyyppi.TAVALLINEN);

    response = hakukohdeResource.insertValinnanvaihe("oid1", vv.getOid(), valinnanVaihe, request);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
  }

  @Test
  public void testKuuluuSijoitteluun() {
    boolean oid;
    oid = hakukohdeResource.kuuluuSijoitteluun("3401").get("sijoitteluun");
    assertEquals(false, oid);

    oid = hakukohdeResource.kuuluuSijoitteluun("3501").get("sijoitteluun");
    assertEquals(false, oid);

    oid = hakukohdeResource.kuuluuSijoitteluun("3601").get("sijoitteluun");
    assertEquals(true, oid);

    oid = hakukohdeResource.kuuluuSijoitteluun("3701").get("sijoitteluun");
    assertEquals(false, oid);
  }

  @Test
  public void testInsertAndUpdateHakukohdekoodi() {
    final String URI = "uri";
    final String ARVO = "arvo";
    KoodiDTO hakukohdekoodi = new KoodiDTO();
    hakukohdekoodi.setUri(URI);
    hakukohdekoodi.setArvo(ARVO);
    ResponseEntity<Object> response =
        hakukohdeResource.insertHakukohdekoodi("oid1", hakukohdekoodi, request);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());

    HakukohdeViiteDTO oid1 = hakukohdeResource.queryFull("oid1");
    KoodiDTO hakukohdekoodi1 = oid1.getHakukohdekoodi();
    assertEquals(ARVO, hakukohdekoodi1.getArvo());
    assertEquals(URI, hakukohdekoodi1.getUri());

    // update
    final String URI2 = "uri2";

    KoodiDTO uusikoodi = new KoodiDTO();
    uusikoodi.setUri(URI2);
    uusikoodi.setArvo(ARVO);

    response = hakukohdeResource.updateHakukohdekoodi("oid1", uusikoodi, request);
    assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());

    oid1 = hakukohdeResource.queryFull("oid1");
    assertEquals(ARVO, oid1.getHakukohdekoodi().getArvo());
    assertEquals(URI2, oid1.getHakukohdekoodi().getUri());
  }

  @Test
  public void testSiirraHakukohdeValintaryhmaan() {
    final String valintaryhmaOid = "oid54";
    final String hakukohdeOid = "oid18";

    ResponseEntity<Object> response =
        hakukohdeResource.siirraHakukohdeValintaryhmaan(hakukohdeOid, valintaryhmaOid, request);
    assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());

    HakukohdeViiteDTO hakukohde = (HakukohdeViiteDTO) response.getBody();
    assertEquals(valintaryhmaOid, hakukohde.getValintaryhmaOid());
  }
}
