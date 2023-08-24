package fi.vm.sade.service.valintaperusteet.resource;

import static org.junit.jupiter.api.Assertions.assertEquals;

import fi.vm.sade.service.valintaperusteet.WithSpringBoot;
import fi.vm.sade.service.valintaperusteet.annotation.DataSetLocation;
import fi.vm.sade.service.valintaperusteet.dao.JarjestyskriteeriDAO;
import fi.vm.sade.service.valintaperusteet.dto.JarjestyskriteeriCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.JarjestyskriteeriDTO;
import fi.vm.sade.service.valintaperusteet.dto.JarjestyskriteeriInsertDTO;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.model.Jarjestyskriteeri;
import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import fi.vm.sade.service.valintaperusteet.util.TestUtil;
import fi.vm.sade.valinta.sharedutils.FakeAuthenticationInitialiser;
import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

/**
 * Created with IntelliJ IDEA. User: jukais Date: 4.2.2013 Time: 15.20 To change this template use
 * File | Settings | File Templates.
 */
@DataSetLocation("classpath:test-data.xml")
public class JarjestyskriteeriResourceTest extends WithSpringBoot {

  private JarjestyskriteeriResource resource = new JarjestyskriteeriResource();
  private TestUtil testUtil = new TestUtil(this.getClass());

  @Autowired private ApplicationContext applicationContext;

  @Autowired private JarjestyskriteeriDAO jarjestyskriteeriDAO;

  @Autowired private ValintaperusteetModelMapper modelMapper;

  @BeforeEach
  public void setUp() {
    FakeAuthenticationInitialiser.fakeAuthentication();
    applicationContext.getAutowireCapableBeanFactory().autowireBean(resource);
  }

  @Test
  public void testUpdate() throws Exception {
    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    HttpSession session = Mockito.mock(HttpSession.class);
    Mockito.when(request.getSession(false)).thenReturn(session);

    JarjestyskriteeriDTO jk = resource.readByOid("1");

    //        assertEquals(1, (int)jk.getPrioriteetti());
    //        jk.setPrioriteetti(100);
    JarjestyskriteeriInsertDTO comb = new JarjestyskriteeriInsertDTO();
    JarjestyskriteeriCreateDTO update = new JarjestyskriteeriCreateDTO();
    update.setMetatiedot("metatiedot");
    comb.setJarjestyskriteeri(update);
    comb.setLaskentakaavaId(jk.getLaskentakaavaId());

    resource.update("1", comb, request);

    jk = resource.readByOid("1");
    //        assertEquals(100, (int)jk.getPrioriteetti());

    testUtil.lazyCheck(JsonViews.Basic.class, jk);
  }

  @Test
  public void testRemove() throws Exception {
    Jarjestyskriteeri jk = jarjestyskriteeriDAO.readByOid("1");
    //        assertEquals(1, (int)jk.getPrioriteetti());

    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    HttpSession session = Mockito.mock(HttpSession.class);
    Mockito.when(request.getSession(false)).thenReturn(session);

    resource.delete("1", request);
    jk = jarjestyskriteeriDAO.readByOid("1");
    assertEquals(null, jk);
  }

  @Test
  public void testJarjesta() throws Exception {
    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
    HttpSession session = Mockito.mock(HttpSession.class);
    Mockito.when(request.getSession(false)).thenReturn(session);

    String[] uusiJarjestys = {"3203", "3202", "3201"};
    List<JarjestyskriteeriDTO> jarjestetty =
        resource.jarjesta(Arrays.asList(uusiJarjestys), request);
    testUtil.lazyCheck(JsonViews.Basic.class, jarjestetty);
  }
}
