package fi.vm.sade.service.valintaperusteet.dao;

import fi.vm.sade.dbunit.annotation.DataSetLocation;
import fi.vm.sade.dbunit.listener.JTACleanInsertTestExecutionListener;
import fi.vm.sade.service.valintaperusteet.model.Opetuskielikoodi;
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

import static org.junit.Assert.*;

/**
 * User: wuoti
 * Date: 25.6.2013
 * Time: 11.54
 */
@ContextConfiguration(locations = "classpath:test-context.xml")
@TestExecutionListeners(listeners = {JTACleanInsertTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@DataSetLocation("classpath:test-data.xml")
public class OpetuskielikoodiDAOTest {

    @Autowired
    private OpetuskielikoodiDAO opetuskielikoodiDAO;

    @Test
    public void testReadByUri() {
        final String koodiUri = "kieli_fi";
        Opetuskielikoodi opetuskielikoodi = opetuskielikoodiDAO.readByUri(koodiUri);
        assertNotNull(opetuskielikoodi);
        assertEquals(koodiUri, opetuskielikoodi.getUri());
    }

    @Test
    public void testFindByUris() {
        final String[] koodiUris = new String[]{"kieli_fi", "kieli_sv", "kieli_ru"};
        List<Opetuskielikoodi> koodis = opetuskielikoodiDAO.findByUris(koodiUris);
        assertEquals(3, koodis.size());

        for (String uri : koodiUris) {
            boolean found = false;
            for (Opetuskielikoodi k : koodis) {
                if (uri.equals(k.getUri())) {
                    found = true;
                    break;
                }
            }
            assertTrue(found);
        }
    }

}
