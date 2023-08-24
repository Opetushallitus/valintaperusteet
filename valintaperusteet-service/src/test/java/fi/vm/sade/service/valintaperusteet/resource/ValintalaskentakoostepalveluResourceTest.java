package fi.vm.sade.service.valintaperusteet.resource;

import static org.junit.jupiter.api.Assertions.assertEquals;

import fi.vm.sade.service.valintaperusteet.WithSpringBoot;
import fi.vm.sade.service.valintaperusteet.annotation.DataSetLocation;
import fi.vm.sade.service.valintaperusteet.dto.ValinnanVaiheJonoillaDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintaperusteetDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintaperusteetValinnanVaiheDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintatapajonoDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintatapajonoJarjestyskriteereillaDTO;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

@DataSetLocation("classpath:test-data.xml")
public class ValintalaskentakoostepalveluResourceTest extends WithSpringBoot {

  private ValintatapajonoResource jonoResource = new ValintatapajonoResource();
  private ValinnanVaiheResource vaiheResource = new ValinnanVaiheResource();
  private ValintalaskentakoostepalveluResource valintalaskentakoostepalveluResource;
  private HakukohdeResource hakukohdeResource = new HakukohdeResource();

  @Autowired private ApplicationContext applicationContext;

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
  public void testHakukohdeJaValintakoeResurssi() {
    valintalaskentakoostepalveluResource.valintakoesForHakukohteet(
        Arrays.asList(
            "oid1", "oid2", "oid3", "oid4", "oid5", "oid6", "oid7", "oid8", "oid9", "oid10",
            "oid11", "oid12", "oid13", "oid14", "oid15", "oid16", "oid17", "oid18", "oid19",
            "oid20", "oid21", "oid22", "oid23", "3001", "3101", "3201", "3301", "3401", "3501",
            "3601", "3701", "3801"));
  }

  @Test
  public void testJonojenPrioriteetitEndToEnd() {

    Map<String, Integer> oidToPrioriteetti = new HashMap<>();

    List<ValintaperusteetDTO> valintaperusteetDTOs =
        valintalaskentakoostepalveluResource.haeValintaperusteet("3801", null);
    for (ValintaperusteetDTO valintaperusteetDTO : valintaperusteetDTOs) {
      ValintaperusteetValinnanVaiheDTO valintaperusteetValinnanVaiheDTO =
          valintaperusteetDTO.getValinnanVaihe();
      List<ValintatapajonoJarjestyskriteereillaDTO> valintatapajonoJarjestyskriteereillaDTOs =
          valintaperusteetValinnanVaiheDTO.getValintatapajono();
      for (ValintatapajonoJarjestyskriteereillaDTO valintatapajonoJarjestyskriteereillaDTO :
          valintatapajonoJarjestyskriteereillaDTOs) {
        oidToPrioriteetti.put(
            valintatapajonoJarjestyskriteereillaDTO.getOid(),
            valintatapajonoJarjestyskriteereillaDTO.getPrioriteetti());
      }
    }

    List<ValinnanVaiheJonoillaDTO> valinnanVaiheJonoillaDTOs =
        valintalaskentakoostepalveluResource.ilmanLaskentaa("3801");
    for (ValinnanVaiheJonoillaDTO valinnanVaiheJonoillaDTO : valinnanVaiheJonoillaDTOs) {
      for (ValintatapajonoDTO valintatapajonoDTO : valinnanVaiheJonoillaDTO.getJonot()) {
        int prioriteetti = oidToPrioriteetti.get(valintatapajonoDTO.getOid());
        assertEquals(prioriteetti, valintatapajonoDTO.getPrioriteetti());
      }
    }
  }
}
