package fi.vm.sade.service.valintaperusteet.dao;

import fi.vm.sade.dbunit.annotation.DataSetLocation;
import fi.vm.sade.dbunit.listener.JTACleanInsertTestExecutionListener;
import fi.vm.sade.service.valintaperusteet.model.Valintakoekoodi;
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

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * User: wuoti
 * Date: 24.6.2013
 * Time: 12.58
 */
@ContextConfiguration(locations = "classpath:test-context.xml")
@TestExecutionListeners(listeners = {JTACleanInsertTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class})
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
