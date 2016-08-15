package fi.vm.sade.service.valintaperusteet.dao;

import static org.junit.Assert.assertEquals;

import fi.vm.sade.service.valintaperusteet.annotation.DataSetLocation;
import fi.vm.sade.service.valintaperusteet.listeners.ValinnatJTACleanInsertTestExecutionListener;
import fi.vm.sade.service.valintaperusteet.model.Valintakoe;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * User: kwuoti
 * Date: 15.4.2013
 * Time: 16.33
 */
@ContextConfiguration(locations = "classpath:test-context.xml")
@TestExecutionListeners(listeners = {ValinnatJTACleanInsertTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class })
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@DataSetLocation("classpath:test-data.xml")
public class ValintakoeDAOTest {

    @Autowired
    private ValintakoeDAO valintakoeDAO;

    @Test
    public void testFindValintakoeByValinnanVaihe() {
        final String valinnanVaiheOid = "83";

        List<Valintakoe> valintakokeet = valintakoeDAO.findByValinnanVaihe(valinnanVaiheOid);
        assertEquals(4, valintakokeet.size());

        Collections.sort(valintakokeet, new Comparator<Valintakoe>() {
            @Override
            public int compare(Valintakoe o1, Valintakoe o2) {
                return o1.getOid().compareTo(o2.getOid());
            }
        });


        assertEquals("oid1", valintakokeet.get(0).getOid());
        assertEquals("oid2", valintakokeet.get(1).getOid());
        assertEquals("oid3", valintakokeet.get(2).getOid());
        assertEquals("oid4", valintakokeet.get(3).getOid());

    }
}
