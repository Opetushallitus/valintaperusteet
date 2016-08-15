package fi.vm.sade.service.valintaperusteet.service;

import static junit.framework.Assert.assertEquals;

import fi.vm.sade.service.valintaperusteet.annotation.DataSetLocation;
import fi.vm.sade.service.valintaperusteet.dao.ValinnanVaiheDAO;
import fi.vm.sade.service.valintaperusteet.dao.ValintatapajonoDAO;
import fi.vm.sade.service.valintaperusteet.listeners.ValinnatJTACleanInsertTestExecutionListener;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA. User: jukais Date: 17.1.2013 Time: 15.14 To
 * change this template use File | Settings | File Templates.
 */
@ContextConfiguration(locations = "classpath:test-context.xml")
@TestExecutionListeners(listeners = { ValinnatJTACleanInsertTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class })
@RunWith(SpringJUnit4ClassRunner.class)
@DataSetLocation("classpath:test-data-valisijoittelu.xml")
public class ValintatapajonoValisijoitteluKopiotTest {
    @Autowired
    private ValintatapajonoService valintatapajonoService;

    @Autowired
    private ValinnanVaiheDAO valinnanVaiheDAO;

    @Autowired
    private ValintatapajonoDAO valintatapajonoDAO;

    @Test
    public void testValintaryhmanKopiot() throws Exception {
        Map<String, List<String>> kopiot = valintatapajonoService.findKopiot(Arrays.asList("3"));

        assertEquals(1, kopiot.get("oid2").size());
        assertEquals("3", kopiot.get("oid2").get(0));
    }

    @Test
    public void testHakukohteenKopiot() throws Exception {
        Map<String, List<String>> kopiot = valintatapajonoService.findKopiot(Arrays.asList("1"));

        assertEquals(1, kopiot.get("oid1").size());
        assertEquals("1", kopiot.get("oid1").get(0));
    }

}
