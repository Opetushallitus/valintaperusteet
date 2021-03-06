package fi.vm.sade.service.valintaperusteet.resource;

import static org.junit.Assert.assertEquals;

import fi.vm.sade.service.valintaperusteet.annotation.DataSetLocation;
import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaValintatapajonoDTO;
import fi.vm.sade.service.valintaperusteet.listeners.ValinnatJTACleanInsertTestExecutionListener;
import fi.vm.sade.service.valintaperusteet.resource.impl.HakukohdeResourceImpl;
import fi.vm.sade.service.valintaperusteet.resource.impl.ValinnanVaiheResourceImpl;
import fi.vm.sade.service.valintaperusteet.resource.impl.ValintalaskentakoostepalveluResourceImpl;
import fi.vm.sade.service.valintaperusteet.resource.impl.ValintatapajonoResourceImpl;
import fi.vm.sade.service.valintaperusteet.util.TestUtil;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

@ContextConfiguration(locations = "classpath:test-context.xml")
@TestExecutionListeners(
    listeners = {
      ValinnatJTACleanInsertTestExecutionListener.class,
      DependencyInjectionTestExecutionListener.class,
      DirtiesContextTestExecutionListener.class
    })
@RunWith(SpringJUnit4ClassRunner.class)
@DataSetLocation("classpath:test-data-hakijaryhma.xml")
public class ValintalaskentakoostepalveluResource2Test {

  private ValintatapajonoResourceImpl jonoResource = new ValintatapajonoResourceImpl();
  private ValinnanVaiheResourceImpl vaiheResource = new ValinnanVaiheResourceImpl();
  private ValintalaskentakoostepalveluResourceImpl valintalaskentakoostepalveluResource;
  private HakukohdeResourceImpl hakukohdeResource = new HakukohdeResourceImpl();

  @Autowired private ApplicationContext applicationContext;
  private TestUtil testUtil = new TestUtil(ValintatapajonoResourceImpl.class);

  @Before
  public void setUp() {
    applicationContext.getAutowireCapableBeanFactory().autowireBean(jonoResource);
    applicationContext.getAutowireCapableBeanFactory().autowireBean(vaiheResource);
    applicationContext.getAutowireCapableBeanFactory().autowireBean(hakukohdeResource);
    valintalaskentakoostepalveluResource =
        applicationContext
            .getAutowireCapableBeanFactory()
            .getBean(ValintalaskentakoostepalveluResourceImpl.class);
  }

  @Test
  public void testReadByHakukohdeOidsPrioriteetit() {
    final List<HakijaryhmaValintatapajonoDTO> hakijaryhmaValintatapajonoDTOs =
        valintalaskentakoostepalveluResource.readByHakukohdeOids(Arrays.asList("1"));
    assertEquals(3, hakijaryhmaValintatapajonoDTOs.size());
    assertEquals(0, hakijaryhmaValintatapajonoDTOs.get(0).getPrioriteetti());
    assertEquals("hr2_vtj2", hakijaryhmaValintatapajonoDTOs.get(0).getOid());
    assertEquals(2, hakijaryhmaValintatapajonoDTOs.get(2).getPrioriteetti());
    assertEquals("hr4_vtj2", hakijaryhmaValintatapajonoDTOs.get(2).getOid());
  }

  @Test
  public void testReadByValintatapajonoOids() {
    final List<HakijaryhmaValintatapajonoDTO> hakijaryhmaValintatapajonoDTOs =
        valintalaskentakoostepalveluResource.readByValintatapajonoOids(Arrays.asList("vtj6"));
    assertEquals(1, hakijaryhmaValintatapajonoDTOs.size());
    assertEquals(0, hakijaryhmaValintatapajonoDTOs.get(0).getPrioriteetti());
    assertEquals("hr5_vtj6", hakijaryhmaValintatapajonoDTOs.get(0).getOid());
  }
}
