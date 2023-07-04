package fi.vm.sade.service.valintaperusteet.resource;

import static org.junit.Assert.*;

import fi.vm.sade.service.valintaperusteet.WithSpringBoot;
import fi.vm.sade.service.valintaperusteet.annotation.DataSetLocation;
import fi.vm.sade.service.valintaperusteet.dto.*;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import fi.vm.sade.service.valintaperusteet.service.exception.ValintatapajonoEiOleOlemassaException;
import fi.vm.sade.service.valintaperusteet.util.TestUtil;
import fi.vm.sade.valinta.sharedutils.FakeAuthenticationInitialiser;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

/**
 * Created with IntelliJ IDEA. User: jukais Date: 23.1.2013 Time: 10.58 To change this template use
 * File | Settings | File Templates.
 */
@DataSetLocation("classpath:test-data.xml")
@ActiveProfiles({"dev", "vtsConfig"})
public class ValintatapajonoResourceTest extends WithSpringBoot {
  private ValintatapajonoResource resource = new ValintatapajonoResource();
  private ValinnanVaiheResource vaiheResource = new ValinnanVaiheResource();

  @Autowired private ApplicationContext applicationContext;
  private TestUtil testUtil = new TestUtil(ValintatapajonoResource.class);
  private HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
  private HttpSession session = Mockito.mock(HttpSession.class);

  @Before
  public void setUp() {
    applicationContext.getAutowireCapableBeanFactory().autowireBean(resource);
    applicationContext.getAutowireCapableBeanFactory().autowireBean(vaiheResource);
    FakeAuthenticationInitialiser.fakeAuthentication();
    Mockito.when(request.getSession(false)).thenReturn(session);
  }

  @Test
  public void testUpdate() throws Exception {
    ValintatapajonoDTO jono = resource.readByOid("1");
    jono.setNimi("muokattu");
    jono.setTyyppi("valintatapajono_m");
    resource.update(jono.getOid(), jono, request);

    jono = resource.readByOid("1");
    assertEquals("muokattu", jono.getNimi());
    testUtil.lazyCheck(JsonViews.Basic.class, jono);
  }

  @Test
  public void testFindAll() throws Exception {
    List<ValintatapajonoDTO> jonos = resource.findAll();

    assertEquals(76, jonos.size());
    testUtil.lazyCheck(JsonViews.Basic.class, jonos);
  }

  @Test
  public void testJarjestykriteeri() throws Exception {
    List<JarjestyskriteeriDTO> jarjestyskriteeri = resource.findJarjestyskriteeri("6");

    testUtil.lazyCheck(JsonViews.Basic.class, jarjestyskriteeri, true);
    assertEquals(3, jarjestyskriteeri.size());
  }

  @Test(expected = RuntimeException.class)
  public void testDeleteOidNotFound() {
    resource.delete("", request);
  }

  @Test
  public void testDeleteInherited() {
    ValintatapajonoDTO valintatapajono = resource.readByOid("27");
    assertNotNull(valintatapajono);
    ResponseEntity<Object> delete = resource.delete("27", request);
    assertEquals(HttpStatus.ACCEPTED, delete.getStatusCode());
    assertThrows(ValintatapajonoEiOleOlemassaException.class, () -> resource.readByOid("27"));
  }

  @Test
  public void testChildrenAreDeleted() {
    ValintatapajonoDTO valintatapajono = resource.readByOid("30");
    assertNotNull(valintatapajono);
    // objekti on peritty
    resource.delete("26", request);
    try {
      valintatapajono = resource.readByOid("30");
      assertNull(valintatapajono);
    } catch (ValintatapajonoEiOleOlemassaException e) {

    }
  }

  @Test
  public void testDelete() {
    ResponseEntity<Object> delete = resource.delete("12", request);
    assertEquals(HttpStatus.ACCEPTED, delete.getStatusCode());
  }

  @Test
  public void testInsertJK() throws Exception {
    JarjestyskriteeriCreateDTO jk = new JarjestyskriteeriCreateDTO();
    jk.setMetatiedot("mt1");
    jk.setAktiivinen(true);

    JarjestyskriteeriInsertDTO comb = new JarjestyskriteeriInsertDTO();
    comb.setJarjestyskriteeri(jk);
    comb.setLaskentakaavaId(1L);
    ResponseEntity<JarjestyskriteeriDTO> insert =
        resource.insertJarjestyskriteeri("1", comb, request);
    assertEquals(HttpStatus.ACCEPTED, insert.getStatusCode());

    JarjestyskriteeriDTO entity = insert.getBody();

    testUtil.lazyCheck(JsonViews.Basic.class, entity, true);
  }

  @Test
  public void testJarjesta() {
    List<ValintatapajonoDTO> valintatapajonoList = vaiheResource.listJonos("1");
    List<String> oids = new ArrayList<String>();

    for (ValintatapajonoDTO valintatapajono : valintatapajonoList) {
      oids.add(valintatapajono.getOid());
    }

    assertEquals("1", oids.get(0));
    assertEquals("5", oids.get(4));
    Collections.reverse(oids);

    List<ValintatapajonoDTO> jarjesta = resource.jarjesta(oids, request);
    assertEquals("5", jarjesta.get(0).getOid());
    assertEquals("1", jarjesta.get(4).getOid());

    jarjesta = vaiheResource.listJonos("1");
    assertEquals("5", jarjesta.get(0).getOid());
    assertEquals("1", jarjesta.get(4).getOid());
  }

  @Test
  public void testPrioriteettiOfSingleJonoHasCustomDefault() throws Exception {
    ValintaperusteetModelMapper modelMapper = new ValintaperusteetModelMapper();

    List<String> allOids =
        resource.findAll().stream().map(ValintatapajonoDTO::getOid).collect(Collectors.toList());
    allOids.stream()
        .forEach(
            oid -> {
              ValintatapajonoDTO dto =
                  modelMapper.map(resource.readByOid(oid), ValintatapajonoDTO.class);
              assertEquals(-1, dto.getPrioriteetti());
            });
  }

  @Test
  public void testPrioriteettiOfSeveralJonosInOrder() throws Exception {
    ValintaperusteetModelMapper modelMapper = new ValintaperusteetModelMapper();

    List<ValintatapajonoDTO> all = resource.findAll();

    for (int i = 0; i < all.size(); i++) {
      ValintatapajonoDTO dto = modelMapper.map(all.get(i), ValintatapajonoDTO.class);
      assertEquals(i, dto.getPrioriteetti());
    }
  }
}
