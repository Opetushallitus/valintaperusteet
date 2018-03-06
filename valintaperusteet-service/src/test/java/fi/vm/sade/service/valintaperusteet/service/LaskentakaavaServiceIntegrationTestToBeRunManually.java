package fi.vm.sade.service.valintaperusteet.service;

import static org.junit.Assert.assertEquals;

import fi.vm.sade.service.valintaperusteet.dao.FunktiokutsuDAO;
import fi.vm.sade.service.valintaperusteet.dto.ValintaperusteDTO;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import java.util.Comparator;
import java.util.List;

@ContextConfiguration(locations = "classpath:fi/vm/sade/service/valintaperusteet/service/integration-test-context.xml")
@TestExecutionListeners(listeners = {
        DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class })
@RunWith(SpringJUnit4ClassRunner.class)
@Ignore
public class LaskentakaavaServiceIntegrationTestToBeRunManually {
    @Autowired
    private LaskentakaavaService laskentakaavaService;

    @Autowired
    private FunktiokutsuDAO funktiokutsuDAO;


    @Test
    public void testFindAvaimetForHakukohde() {
        for (int i = 0; i < 1; i++) {
            List<ValintaperusteDTO> valintaperusteet = laskentakaavaService.findAvaimetForHakukohde("1.2.246.562.20.39188224891");
            assertSyotettavaArvoHakukohde17(valintaperusteet);
        }
    }

    private void assertSyotettavaArvoHakukohde17(List<ValintaperusteDTO> valintaperusteet) {
        assertEquals(3, valintaperusteet.size());

        valintaperusteet.sort(Comparator.comparing(ValintaperusteDTO::getTunniste));

        assertEquals("Entrance examination: 0-70 points, min. 30 points.", valintaperusteet.get(0).getKuvaus());
        assertEquals("5454fd76-0e02-e868-4e45-f96061aa0cc0", valintaperusteet.get(0).getTunniste());

        assertEquals("Pre-assignment: 1 = delivered, 2 = not delivered.", valintaperusteet.get(1).getKuvaus());
        assertEquals("6b3c52e0-f9e7-7a75-8b83-05611208fa3f", valintaperusteet.get(1).getTunniste());

        assertEquals("Pre-assignment: 0-30 points, min. 10 points", valintaperusteet.get(2).getKuvaus());
        assertEquals("ced128ef-f288-fa6a-af47-3a0fa1c3a887", valintaperusteet.get(2).getTunniste());
    }
}
