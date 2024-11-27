package fi.vm.sade.service.valintaperusteet.resource;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import fi.vm.sade.service.valintaperusteet.ObjectMapperProvider;
import fi.vm.sade.service.valintaperusteet.WithSpringBoot;
import fi.vm.sade.service.valintaperusteet.annotation.DataSetLocation;
import fi.vm.sade.service.valintaperusteet.dto.ValinnanVaiheDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintakoeDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintatapajonoDTO;
import fi.vm.sade.service.valintaperusteet.dto.model.Koekutsu;
import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import fi.vm.sade.service.valintaperusteet.service.exception.ValinnanVaiheEiOleOlemassaException;
import fi.vm.sade.valinta.sharedutils.FakeAuthenticationInitialiser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

/** User: tommiha Date: 1/23/13 Time: 1:03 PM */
@DataSetLocation("classpath:test-data.xml")
public class ValinnanVaiheResourceTest extends WithSpringBoot {

  private ObjectMapper mapper = new ObjectMapperProvider().getContext(ValinnanVaiheResource.class);
  private ValinnanVaiheResource vaiheResource = new ValinnanVaiheResource();
  private HakukohdeResource hakuResource = new HakukohdeResource();

  @Autowired private ApplicationContext applicationContext;

  @BeforeEach
  public void setUp() {
    applicationContext.getAutowireCapableBeanFactory().autowireBean(vaiheResource);
    applicationContext.getAutowireCapableBeanFactory().autowireBean(hakuResource);
    FakeAuthenticationInitialiser.fakeAuthentication();
  }

  @AfterEach
  public void tearDown() {}

  @Test
  public void testRead() throws IOException {
    ValinnanVaiheDTO vaihe = vaiheResource.read("1");
    mapper.writerWithView(JsonViews.Basic.class).writeValueAsString(vaihe);
  }

  @Test
  public void testQuery() throws IOException {
    List<ValintatapajonoDTO> jonos = vaiheResource.listJonos("1");

    mapper.writerWithView(JsonViews.Basic.class).writeValueAsString(jonos);
  }

  @Test
  public void testUpdate() throws IOException {
    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    HttpSession session = Mockito.mock(HttpSession.class);
    Mockito.when(request.getSession(false)).thenReturn(session);

    ValinnanVaiheDTO vaihe = vaiheResource.read("1");

    ValinnanVaiheDTO vaihe1 = vaiheResource.update(vaihe.getOid(), vaihe, request);
    mapper.writerWithView(JsonViews.Basic.class).writeValueAsString(vaihe1);
  }

  @Test
  public void testInsertValintatapajono() {
    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    HttpSession session = Mockito.mock(HttpSession.class);
    Mockito.when(request.getSession(false)).thenReturn(session);

    ValintatapajonoDTO jono = new ValintatapajonoDTO();
    ResponseEntity<ValintatapajonoDTO> insert =
        vaiheResource.addJonoToValinnanVaihe("1", jono, request);
    assertEquals(500, insert.getStatusCode().value());

    jono = newJono();
    insert = vaiheResource.addJonoToValinnanVaihe("1", jono, request);
    assertEquals(201, insert.getStatusCode().value());
  }

  @Test
  public void testInsertValintakoe() {
    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    HttpSession session = Mockito.mock(HttpSession.class);
    Mockito.when(request.getSession(false)).thenReturn(session);

    final String valinnanVaiheOid = "83";
    final Long laskentakaavaId = 101L;

    ValintakoeDTO valintakoe = new ValintakoeDTO();
    valintakoe.setTunniste("tunniste");
    valintakoe.setNimi("nimi");
    valintakoe.setAktiivinen(true);
    valintakoe.setLaskentakaavaId(laskentakaavaId);
    valintakoe.setLahetetaankoKoekutsut(true);
    valintakoe.setKutsutaankoKaikki(false);
    valintakoe.setKutsunKohde(Koekutsu.YLIN_TOIVE);

    ResponseEntity<ValintakoeDTO> response =
        vaiheResource.addValintakoeToValinnanVaihe(valinnanVaiheOid, valintakoe, request);
    assertEquals(201, response.getStatusCode().value());
  }

  @Test
  public void testInsertValintakoeWithExistingTunniste() {
    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

    final String valinnanVaiheOid = "83";
    final Long laskentakaavaId = 101L;

    ValintakoeDTO valintakoe = new ValintakoeDTO();
    valintakoe.setTunniste("valintakoetunniste2");
    valintakoe.setNimi("nimi");
    valintakoe.setAktiivinen(true);
    valintakoe.setLaskentakaavaId(laskentakaavaId);
    valintakoe.setLahetetaankoKoekutsut(true);
    valintakoe.setKutsutaankoKaikki(false);
    valintakoe.setKutsunKohde(Koekutsu.YLIN_TOIVE);

    ResponseEntity<ValintakoeDTO> response =
        vaiheResource.addValintakoeToValinnanVaihe(valinnanVaiheOid, valintakoe, request);
    assertEquals(500, response.getStatusCode().value());
  }

  @Test
  public void testDelete() {
    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    HttpSession session = Mockito.mock(HttpSession.class);
    Mockito.when(request.getSession(false)).thenReturn(session);
    ResponseEntity<Object> delete = vaiheResource.delete("4", request);
    assertEquals(HttpStatus.ACCEPTED, delete.getStatusCode());
  }

  @Test
  public void testDeleteOidNotFound() {
    boolean caughtOne = false;
    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

    try {
      vaiheResource.delete("", request);
    } catch (ResponseStatusException e) {
      caughtOne = true;
      assertEquals(404, e.getStatusCode().value());
    }

    assertTrue(caughtOne);
  }

  @Test
  public void testDeleteInherited() {
    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    HttpSession session = Mockito.mock(HttpSession.class);
    Mockito.when(request.getSession(false)).thenReturn(session);

    ResponseEntity<Object> delete = vaiheResource.delete("32", request);
    assertEquals(HttpStatus.ACCEPTED, delete.getStatusCode());
    assertThrows(ValinnanVaiheEiOleOlemassaException.class, () -> vaiheResource.read("32"));
  }

  @Test
  public void testChildrenAreDeleted() {
    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    HttpSession session = Mockito.mock(HttpSession.class);
    Mockito.when(request.getSession(false)).thenReturn(session);

    ValinnanVaiheDTO read = vaiheResource.read("79");

    assertNotNull(read);
    // objekti on peritty
    ResponseEntity<Object> delete = vaiheResource.delete("75", request);
    assertEquals(HttpStatus.ACCEPTED, delete.getStatusCode());

    try {
      ValinnanVaiheDTO read1 = vaiheResource.read("79");
      assertNull(read1);
    } catch (ValinnanVaiheEiOleOlemassaException e) {

    }
  }

  @Test
  public void testJarjesta() {
    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    HttpSession session = Mockito.mock(HttpSession.class);
    Mockito.when(request.getSession(false)).thenReturn(session);

    List<ValinnanVaiheDTO> valinnanVaiheList =
        hakuResource.valinnanVaihesForHakukohde("oid6", "false");
    List<String> oids = new ArrayList<String>();

    for (ValinnanVaiheDTO valinnanVaihe : valinnanVaiheList) {
      oids.add(valinnanVaihe.getOid());
    }

    assertEquals("4", oids.get(0));
    assertEquals("6", oids.get(2));
    Collections.reverse(oids);
    List<ValinnanVaiheDTO> jarjesta = vaiheResource.jarjesta(oids, request);
    assertEquals("6", jarjesta.get(0).getOid());
    assertEquals("4", jarjesta.get(2).getOid());
    jarjesta = hakuResource.valinnanVaihesForHakukohde("oid6", "false");
    assertEquals("6", jarjesta.get(0).getOid());
    assertEquals("4", jarjesta.get(2).getOid());
  }

  @Test
  public void testJarjestaEriHakuvaiheita() {
    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

    List<String> oids = new ArrayList<String>();

    oids.add("4");
    oids.add("5");
    oids.add("6");

    oids.add("1");

    assertThrows(RuntimeException.class, () -> vaiheResource.jarjesta(oids, request));
  }

  private ValintatapajonoDTO newJono() {
    ValintatapajonoDTO jono = new ValintatapajonoDTO();
    jono.setNimi("Uusi valintaryhmä");
    jono.setOid("oid123");
    jono.setAloituspaikat(1);
    jono.setSiirretaanSijoitteluun(false);
    jono.setTasapistesaanto(fi.vm.sade.service.valintaperusteet.dto.model.Tasapistesaanto.ARVONTA);
    jono.setAktiivinen(true);
    jono.setautomaattinenSijoitteluunSiirto(true);
    jono.setValisijoittelu(false);
    return jono;
  }

  @Test
  public void testKuuluuSijoitteluun() {
    boolean oid;
    oid = vaiheResource.kuuluuSijoitteluun("3401").get("sijoitteluun");
    assertEquals(false, oid);

    oid = vaiheResource.kuuluuSijoitteluun("3501").get("sijoitteluun");
    assertEquals(false, oid);

    oid = vaiheResource.kuuluuSijoitteluun("3601").get("sijoitteluun");
    assertEquals(true, oid);

    oid = vaiheResource.kuuluuSijoitteluun("3701").get("sijoitteluun");
    assertEquals(false, oid);
  }

  @Test
  public void testListValintakokeet() throws IOException {
    final String valintaryhmaOid = "83";

    List<ValintakoeDTO> kokeet = vaiheResource.listValintakokeet(valintaryhmaOid);

    String json = mapper.writerWithView(JsonViews.Basic.class).writeValueAsString(kokeet);
    System.out.println("JSON: " + json);

    assertEquals(5, kokeet.size());
    assertEquals(4, kokeet.stream().filter(vk -> vk.getPeritty() == false).count());
    assertEquals(1, kokeet.stream().filter(vk -> vk.getPeritty() == true).count());
  }

  @Test
  public void testListValintakokeetShouldBeEmpty() {
    final String valinnanVaiheOid = "85";
    List<ValintakoeDTO> kokeet = vaiheResource.listValintakokeet(valinnanVaiheOid);
    assertEquals(0, kokeet.size());
  }
}
