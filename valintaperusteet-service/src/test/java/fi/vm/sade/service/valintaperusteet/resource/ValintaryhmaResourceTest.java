package fi.vm.sade.service.valintaperusteet.resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import fi.vm.sade.service.valintaperusteet.ObjectMapperProvider;
import fi.vm.sade.service.valintaperusteet.WithSpringBoot;
import fi.vm.sade.service.valintaperusteet.annotation.DataSetLocation;
import fi.vm.sade.service.valintaperusteet.dao.ValinnanVaiheDAO;
import fi.vm.sade.service.valintaperusteet.dto.*;
import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import fi.vm.sade.valinta.sharedutils.FakeAuthenticationInitialiser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/** User: tommiha Date: 1/21/13 Time: 4:05 PM */
@DataSetLocation("classpath:test-data.xml")
public class ValintaryhmaResourceTest extends WithSpringBoot {

  private ValintaryhmaResource valintaryhmaResource = new ValintaryhmaResource();
  private ObjectMapper mapper = new ObjectMapperProvider().getContext(ValintaryhmaResource.class);

  @Autowired private ApplicationContext applicationContext;

  @Autowired private ValinnanVaiheDAO valinnanVaiheDao;

  @BeforeEach
  public void setUp() {
    applicationContext.getAutowireCapableBeanFactory().autowireBean(valintaryhmaResource);
    FakeAuthenticationInitialiser.fakeAuthentication();
  }

  @Test
  public void testQueryFull() throws Exception {
    ValintaryhmaDTO valintaryhma = valintaryhmaResource.queryFull("oid1");
    assertEquals("oid1", valintaryhma.getOid());

    mapper.writerWithView(JsonViews.Basic.class).writeValueAsString(valintaryhma);
  }

  @Test
  public void testSearch() throws Exception {
    List<ValintaryhmaDTO> valintaryhmas = valintaryhmaResource.search(true, null);
    assertEquals(46, valintaryhmas.size());
    assertEquals("oid1", valintaryhmas.get(0).getOid());

    mapper.writerWithView(JsonViews.Basic.class).writeValueAsString(valintaryhmas);
  }

  @Test
  public void testQueryChildren() throws Exception {
    List<ValintaryhmaDTO> valintaryhmas = valintaryhmaResource.queryChildren("oid1");
    assertEquals(4, valintaryhmas.size());

    mapper.writerWithView(JsonViews.Basic.class).writeValueAsString(valintaryhmas);
  }

  @Test
  public void testInsert() throws Exception {
    ValintaryhmaCreateDTO valintaryhma = new ValintaryhmaCreateDTO();
    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    HttpSession session = Mockito.mock(HttpSession.class);
    Mockito.when(request.getSession(false)).thenReturn(session);

    valintaryhma.setNimi("Uusi valintaryhmä");
    System.out.println(
        mapper.writerWithView(JsonViews.Basic.class).writeValueAsString(valintaryhma));
    valintaryhmaResource.insert(valintaryhma, request);

    mapper.writerWithView(JsonViews.Basic.class).writeValueAsString(valintaryhma);
  }

  @Test
  public void testInsertParent() throws Exception {
    ValintaryhmaCreateDTO valintaryhma = new ValintaryhmaCreateDTO();
    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    HttpSession session = Mockito.mock(HttpSession.class);
    Mockito.when(request.getSession(false)).thenReturn(session);
    valintaryhma.setNimi("Uusi valintaryhmä");
    System.out.println(
        mapper.writerWithView(JsonViews.Basic.class).writeValueAsString(valintaryhma));
    valintaryhmaResource.insertChild("oid1", valintaryhma, request);

    mapper.writerWithView(JsonViews.Basic.class).writeValueAsString(valintaryhma);
  }

  @Test
  public void testUpdate() throws Exception {
    ValintaryhmaCreateDTO valintaryhma1 = valintaryhmaResource.queryFull("oid1");
    valintaryhma1.setNimi("Updated valintaryhmä");
    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    HttpSession session = Mockito.mock(HttpSession.class);
    Mockito.when(request.getSession(false)).thenReturn(session);

    valintaryhmaResource.update("oid1", valintaryhma1, request);

    mapper.writerWithView(JsonViews.Basic.class).writeValueAsString(valintaryhma1);
  }

  @Test
  public void testInsertValinnanVaihe() {
    ValinnanVaiheCreateDTO valinnanVaihe = new ValinnanVaiheCreateDTO();

    valinnanVaihe.setNimi("uusi");
    valinnanVaihe.setAktiivinen(true);
    valinnanVaihe.setValinnanVaiheTyyppi(
        fi.vm.sade.service.valintaperusteet.dto.model.ValinnanVaiheTyyppi.TAVALLINEN);
    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    HttpSession session = Mockito.mock(HttpSession.class);
    Mockito.when(request.getSession(false)).thenReturn(session);

    ResponseEntity<ValinnanVaiheDTO> response =
        valintaryhmaResource.insertValinnanvaihe("oid1", null, valinnanVaihe, request);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    ValinnanVaiheDTO vv = response.getBody();

    valinnanVaihe = new ValinnanVaiheCreateDTO();
    valinnanVaihe.setNimi("uusi");
    valinnanVaihe.setAktiivinen(true);
    valinnanVaihe.setValinnanVaiheTyyppi(
        fi.vm.sade.service.valintaperusteet.dto.model.ValinnanVaiheTyyppi.TAVALLINEN);

    response =
        valintaryhmaResource.insertValinnanvaihe("oid1", vv.getOid(), valinnanVaihe, request);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
  }

  @Test
  public void testInsertHakukohdekoodi() {
    final String URI = "uri";
    final String ARVO = "arvo";
    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    HttpSession session = Mockito.mock(HttpSession.class);
    Mockito.when(request.getSession(false)).thenReturn(session);

    KoodiDTO hakukohdekoodi = new KoodiDTO();
    hakukohdekoodi.setUri(URI);
    hakukohdekoodi.setArvo(ARVO);
    ResponseEntity<Object> response =
        valintaryhmaResource.insertHakukohdekoodi("oid1", hakukohdekoodi, request);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());

    ValintaryhmaDTO oid1 = valintaryhmaResource.queryFull("oid1");
    boolean found = false;
    for (KoodiDTO hkk : oid1.getHakukohdekoodit()) {
      if (hkk.getArvo().equals(ARVO) && hkk.getUri().equals(URI)) {
        found = true;
        break;
      }
    }
    assertTrue(found);
  }

  @Test
  public void testUpdateHakukohdekoodi() {
    final String URI = "uri";
    final String ARVO = "arvo";
    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    HttpSession session = Mockito.mock(HttpSession.class);
    Mockito.when(request.getSession(false)).thenReturn(session);

    KoodiDTO hakukohdekoodi = new KoodiDTO();
    hakukohdekoodi.setUri(URI);
    hakukohdekoodi.setArvo(ARVO);
    ResponseEntity<Object> response =
        valintaryhmaResource.insertHakukohdekoodi("oid1", hakukohdekoodi, request);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());

    Set<KoodiDTO> oid1 = valintaryhmaResource.queryFull("oid1").getHakukohdekoodit();
    for (int i = 0; i < 9; i++) {
      KoodiDTO koodi = new KoodiDTO();
      koodi.setUri("uri" + i);
      koodi.setArvo("arvo" + i);
      oid1.add(koodi);
    }

    valintaryhmaResource.updateHakukohdekoodi("oid1", oid1, request);

    oid1 = valintaryhmaResource.queryFull("oid1").getHakukohdekoodit();

    assertEquals(10, oid1.size());
  }

  @Test
  public void testRemoveAllHakukohdekoodi() {
    final String URI = "uri";
    final String ARVO = "arvo";
    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    HttpSession session = Mockito.mock(HttpSession.class);
    Mockito.when(request.getSession(false)).thenReturn(session);

    KoodiDTO hakukohdekoodi = new KoodiDTO();
    hakukohdekoodi.setUri(URI);
    hakukohdekoodi.setArvo(ARVO);
    ResponseEntity<Object> response =
        valintaryhmaResource.insertHakukohdekoodi("oid1", hakukohdekoodi, request);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());

    Set<KoodiDTO> oid1 = null;

    response = valintaryhmaResource.updateHakukohdekoodi("oid1", oid1, request);
    assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());

    oid1 = valintaryhmaResource.queryFull("oid1").getHakukohdekoodit();

    assertEquals(0, oid1.size());
  }

  @Test
  public void testLisaaValintakoekoodiValintaryhmalle() throws IOException {
    final String valintaryhmaOid = "oid52";
    final String valintakoekoodiUri = "uusivalintakoekoodiuri";
    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    HttpSession session = Mockito.mock(HttpSession.class);
    Mockito.when(request.getSession(false)).thenReturn(session);

    KoodiDTO koodi = new KoodiDTO();
    koodi.setUri(valintakoekoodiUri);

    String json = mapper.writerWithView(JsonViews.Basic.class).writeValueAsString(koodi);
    koodi = mapper.readValue(json, KoodiDTO.class);

    valintaryhmaResource.insertValintakoekoodi(valintaryhmaOid, koodi, request);
  }

  @Test
  public void testPaivitaValintaryhmanValintakoekoodit() throws IOException {
    final String[] koeUrit =
        new String[] {"uusivalintakoekoodi", "valintakoeuri1", "valintakoeuri2"};
    final String valintaryhmaOid = "oid52";
    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    HttpSession session = Mockito.mock(HttpSession.class);
    Mockito.when(request.getSession(false)).thenReturn(session);

    List<KoodiDTO> kokeet = new ArrayList<KoodiDTO>();
    for (String uri : koeUrit) {
      KoodiDTO koodi = new KoodiDTO();
      koodi.setUri(uri);
      String json = mapper.writerWithView(JsonViews.Basic.class).writeValueAsString(koodi);

      kokeet.add(mapper.readValue(json, KoodiDTO.class));
    }

    valintaryhmaResource.updateValintakoekoodi(valintaryhmaOid, kokeet, request);

    ValintaryhmaDTO paivitetty =
        mapper.readValue(
            mapper
                .writerWithView(JsonViews.Basic.class)
                .writeValueAsString(valintaryhmaResource.queryFull(valintaryhmaOid)),
            ValintaryhmaDTO.class);

    assertEquals(koeUrit.length, paivitetty.getValintakoekoodit().size());
  }

  @Test
  public void testDelete() throws Exception {
    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    HttpSession session = Mockito.mock(HttpSession.class);
    Mockito.when(request.getSession(false)).thenReturn(session);

    ValintaryhmaDTO valintaryhma = new ValintaryhmaDTO();
    valintaryhma.setOid("oid2");
    valintaryhma.setNimi("Uusi valintaryhmä");
    System.out.println(
        mapper.writerWithView(JsonViews.Basic.class).writeValueAsString(valintaryhma));
    valintaryhmaResource.insert(valintaryhma, request);

    mapper.writerWithView(JsonViews.Basic.class).writeValueAsString(valintaryhma);

    valintaryhmaResource.delete("oid2", request);
  }

  @Test
  public void testKopioiLapseksi() throws Exception {
    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    HttpSession session = Mockito.mock(HttpSession.class);
    Mockito.when(request.getSession(false)).thenReturn(session);

    ResponseEntity<Object> response =
        valintaryhmaResource.copyAsChild("oid_700", "oid_702", "Testi", request);
    ValintaryhmaDTO valintaryhmaDTO =
        mapper.readValue(
            mapper.writerWithView(JsonViews.Basic.class).writeValueAsString(response.getBody()),
            ValintaryhmaDTO.class);
    assertEquals(1, valintaryhmaDTO.getHakukohdekoodit().size());
    assertEquals(
        "hakukohdekoodi704", valintaryhmaDTO.getHakukohdekoodit().iterator().next().getUri());
    assertEquals(1, valintaryhmaDTO.getValintakoekoodit().size());
    assertEquals("koekoodi703", valintaryhmaDTO.getValintakoekoodit().get(0).getUri());
  }
}
