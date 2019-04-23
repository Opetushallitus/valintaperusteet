package fi.vm.sade.service.valintaperusteet.resource;

import static org.junit.Assert.assertEquals;

import fi.vm.sade.service.valintaperusteet.annotation.DataSetLocation;
import fi.vm.sade.service.valintaperusteet.dto.ValinnanVaiheJonoillaDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintaperusteetDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintaperusteetValinnanVaiheDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintatapajonoDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintatapajonoJarjestyskriteereillaDTO;
import fi.vm.sade.service.valintaperusteet.listeners.ValinnatJTACleanInsertTestExecutionListener;
import fi.vm.sade.service.valintaperusteet.resource.impl.HakukohdeResourceImpl;
import fi.vm.sade.service.valintaperusteet.resource.impl.ValinnanVaiheResourceImpl;
import fi.vm.sade.service.valintaperusteet.resource.impl.ValintalaskentakoostepalveluResourceImpl;
import fi.vm.sade.service.valintaperusteet.resource.impl.ValintatapajonoResourceImpl;
import fi.vm.sade.service.valintaperusteet.util.TestUtil;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ContextConfiguration(locations = "classpath:test-context.xml")
@TestExecutionListeners(listeners = {ValinnatJTACleanInsertTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class })
@RunWith(SpringJUnit4ClassRunner.class)
@DataSetLocation("classpath:test-data.xml")
public class ValintalaskentakoostepalveluResourceTest {

    private ValintatapajonoResourceImpl jonoResource = new ValintatapajonoResourceImpl();
    private ValinnanVaiheResourceImpl vaiheResource = new ValinnanVaiheResourceImpl();
    private ValintalaskentakoostepalveluResourceImpl valintalaskentakoostepalveluResource;
    private HakukohdeResourceImpl hakukohdeResource = new HakukohdeResourceImpl();

    @Autowired
    private ApplicationContext applicationContext;
    private TestUtil testUtil = new TestUtil(ValintatapajonoResourceImpl.class);

    @Before
    public void setUp() {
        applicationContext.getAutowireCapableBeanFactory().autowireBean(jonoResource);
        applicationContext.getAutowireCapableBeanFactory().autowireBean(vaiheResource);
        applicationContext.getAutowireCapableBeanFactory().autowireBean(hakukohdeResource);
        valintalaskentakoostepalveluResource = applicationContext.getAutowireCapableBeanFactory().getBean(ValintalaskentakoostepalveluResourceImpl.class);
    }

    @Test
    public void testHakukohdeJaValintakoeResurssi() {
        valintalaskentakoostepalveluResource.valintakoesForHakukohteet(Arrays.asList("oid1", "oid2", "oid3", "oid4", "oid5", "oid6", "oid7", "oid8", "oid9", "oid10", "oid11", "oid12", "oid13", "oid14", "oid15", "oid16", "oid17", "oid18", "oid19", "oid20", "oid21", "oid22", "oid23", "3001", "3101", "3201", "3301", "3401", "3501", "3601", "3701", "3801"));

    }

    @Test
    public void testJonojenPrioriteetitEndToEnd() {

        Map<String, Integer> oidToPrioriteetti = new HashMap<>();

        List<ValintaperusteetDTO> valintaperusteetDTOs = valintalaskentakoostepalveluResource.haeValintaperusteet("3801", null);
        for(ValintaperusteetDTO valintaperusteetDTO : valintaperusteetDTOs) {
            ValintaperusteetValinnanVaiheDTO valintaperusteetValinnanVaiheDTO = valintaperusteetDTO.getValinnanVaihe();
            List<ValintatapajonoJarjestyskriteereillaDTO> valintatapajonoJarjestyskriteereillaDTOs = valintaperusteetValinnanVaiheDTO.getValintatapajono();
            for(ValintatapajonoJarjestyskriteereillaDTO valintatapajonoJarjestyskriteereillaDTO : valintatapajonoJarjestyskriteereillaDTOs) {
                oidToPrioriteetti.put(valintatapajonoJarjestyskriteereillaDTO.getOid(), valintatapajonoJarjestyskriteereillaDTO.getPrioriteetti());
            }
        }

        List<ValinnanVaiheJonoillaDTO> valinnanVaiheJonoillaDTOs = valintalaskentakoostepalveluResource.ilmanLaskentaa("3801");
        for(ValinnanVaiheJonoillaDTO valinnanVaiheJonoillaDTO : valinnanVaiheJonoillaDTOs) {
            for(ValintatapajonoDTO valintatapajonoDTO : valinnanVaiheJonoillaDTO.getJonot()) {
                int prioriteetti = oidToPrioriteetti.get(valintatapajonoDTO.getOid());
                assertEquals(prioriteetti, valintatapajonoDTO.getPrioriteetti());
            }

        }

    }
}
