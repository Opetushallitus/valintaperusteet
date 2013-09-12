package fi.vm.sade.service.valintaperusteet.dao;

import fi.vm.sade.dbunit.annotation.DataSetLocation;
import fi.vm.sade.dbunit.listener.JTACleanInsertTestExecutionListener;
import fi.vm.sade.service.valintaperusteet.model.HakukohteenValintaperuste;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;
import scala.actors.threadpool.Arrays;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * User: wuoti
 * Date: 12.9.2013
 * Time: 13.53
 */
@ContextConfiguration(locations = "classpath:test-context.xml")
@TestExecutionListeners(listeners = {JTACleanInsertTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@DataSetLocation("classpath:test-data.xml")
public class HakukohteenValintaperusteDAOTest {

    @Autowired
    private HakukohteenValintaperusteDAO hakukohteenValintaperusteDAO;

    @Test
    public void test() {
        final String hakukohdeOid = "oid22";
        Set<String> tunnisteet = new HashSet<String>(Arrays.asList(new String[]{"tunniste1", "tunniste2", "tunniste3", "tunniste4"}));

        List<HakukohteenValintaperuste> valintaperusteet = hakukohteenValintaperusteDAO.haeHakukohteenValintaperusteet(hakukohdeOid);
        assertEquals(tunnisteet.size(), valintaperusteet.size());
        for (HakukohteenValintaperuste vp : valintaperusteet) {
            tunnisteet.contains(vp.getTunniste());
        }

    }

}
