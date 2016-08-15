package fi.vm.sade.service.valintaperusteet.dao;

import static org.junit.Assert.assertEquals;

import fi.vm.sade.service.valintaperusteet.annotation.DataSetLocation;
import fi.vm.sade.service.valintaperusteet.listeners.ValinnatJTACleanInsertTestExecutionListener;
import fi.vm.sade.service.valintaperusteet.model.Valintakoekoodi;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * User: wuoti
 * Date: 24.6.2013
 * Time: 12.58
 */
@ContextConfiguration(locations = "classpath:test-context.xml")
@TestExecutionListeners(listeners = {ValinnatJTACleanInsertTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class })
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@DataSetLocation("classpath:test-data.xml")
public class ValintakoekoodiDAOTest {

    @Autowired
    private ValintakoekoodiDAO valintakoekoodiDAO;

    @Test
    public void testFindByValintaryhma() {
        final String valintaryhmaOid = "oid49";
        final String valintakoekoodiUri = "valintakoeuri1";

        List<Valintakoekoodi> koodit = valintakoekoodiDAO.findByValintaryhma(valintaryhmaOid);
        assertEquals(2, koodit.size());
        for (Valintakoekoodi koodi : koodit) {
            assertEquals(valintakoekoodiUri, koodi.getUri());
        }

        assertEquals(0, valintakoekoodiDAO.findByValintaryhma("eioleolemassa").size());
    }
}
