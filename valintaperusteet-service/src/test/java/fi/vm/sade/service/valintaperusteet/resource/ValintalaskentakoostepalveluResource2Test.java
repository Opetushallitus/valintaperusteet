package fi.vm.sade.service.valintaperusteet.resource;

import static org.junit.jupiter.api.Assertions.assertEquals;

import fi.vm.sade.service.valintaperusteet.WithSpringBoot;
import fi.vm.sade.service.valintaperusteet.annotation.DataSetLocation;
import fi.vm.sade.service.valintaperusteet.dto.HakijaryhmaValintatapajonoDTO;
import fi.vm.sade.service.valintaperusteet.util.TestUtil;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

@DataSetLocation("classpath:test-data-hakijaryhma.xml")
public class ValintalaskentakoostepalveluResource2Test extends WithSpringBoot {

  private ValintatapajonoResource jonoResource = new ValintatapajonoResource();
  private ValinnanVaiheResource vaiheResource = new ValinnanVaiheResource();
  private ValintalaskentakoostepalveluResource valintalaskentakoostepalveluResource;
  private HakukohdeResource hakukohdeResource = new HakukohdeResource();

  @Autowired private ApplicationContext applicationContext;
  private TestUtil testUtil = new TestUtil(ValintatapajonoResource.class);

  @BeforeEach
  public void setUp() {
    applicationContext.getAutowireCapableBeanFactory().autowireBean(jonoResource);
    applicationContext.getAutowireCapableBeanFactory().autowireBean(vaiheResource);
    applicationContext.getAutowireCapableBeanFactory().autowireBean(hakukohdeResource);
    valintalaskentakoostepalveluResource =
        applicationContext
            .getAutowireCapableBeanFactory()
            .getBean(ValintalaskentakoostepalveluResource.class);
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
