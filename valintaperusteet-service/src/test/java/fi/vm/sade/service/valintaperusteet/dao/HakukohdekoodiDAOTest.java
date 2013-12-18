package fi.vm.sade.service.valintaperusteet.dao;

import fi.vm.sade.dbunit.annotation.DataSetLocation;
import fi.vm.sade.dbunit.listener.JTACleanInsertTestExecutionListener;
import fi.vm.sade.service.valintaperusteet.model.Hakukohdekoodi;
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

import static org.junit.Assert.*;

/**
 * User: wuoti
 * Date: 8.5.2013
 * Time: 14.14
 */
@ContextConfiguration(locations = "classpath:test-context.xml")
@TestExecutionListeners(listeners = {
        DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class, JTACleanInsertTestExecutionListener.class})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@DataSetLocation("classpath:test-data.xml")
public class HakukohdekoodiDAOTest {

    @Autowired
    private HakukohdekoodiDAO hakukohdekoodiDAO;

    @Test
    public void testFindByKoodiUri() {
        final String koodiUri = "hakukohdekoodiuri1";
        Hakukohdekoodi koodi = hakukohdekoodiDAO.readByUri(koodiUri);

        assertEquals(koodiUri, koodi.getUri());
        assertNull(hakukohdekoodiDAO.readByUri("not-exists"));
    }

    @Test
    public void testFindByHakukohdeOid() {
        final String hakukohdeOid = "oid12";
        Hakukohdekoodi hakukohdekoodi = hakukohdekoodiDAO.findByHakukohdeOid(hakukohdeOid);
        assertNotNull(hakukohdekoodi);
        assertNull(hakukohdekoodiDAO.findByHakukohdeOid("not exists"));
    }
}
