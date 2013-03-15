package fi.vm.sade.service.valintaperusteet.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import fi.vm.sade.dbunit.annotation.DataSetLocation;
import fi.vm.sade.dbunit.listener.JTACleanInsertTestExecutionListener;
import fi.vm.sade.service.valintaperusteet.ValintaperusteService;

/**
 * 
 * @author Jussi Jartamo
 * 
 */
@ContextConfiguration(locations = "classpath:test-context.xml")
@TestExecutionListeners(listeners = { JTACleanInsertTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class })
@RunWith(SpringJUnit4ClassRunner.class)
@DataSetLocation("classpath:test-data.xml")
public class ValintaperusteServicePaasykokeetTest {

    // Testidatan käyttämä hakukohdeoid
    private static final String HAKUKOHDEOID = "oid2";

    @Autowired
    private ValintaperusteService valintaperusteService;

    @Test
    public void testHaePaasykokeellisiaHakukohdeOidilla() {

        // int onceOnly = 0;
        // for (PaasykoeHakukohdeTyyppi paasykoe :
        // valintaperusteService.haePaasykokeet(HAKUKOHDEOID)) {
        // ++onceOnly;
        // }
        // Assert.assertTrue("Valitulla hakukohdeoid:lla ja testidatan tuntien oletettiin vastauksena yhtätietuetta!",
        // onceOnly == 1);
    }

}