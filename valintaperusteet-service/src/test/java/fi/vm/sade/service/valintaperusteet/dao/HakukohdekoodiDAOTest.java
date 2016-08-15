package fi.vm.sade.service.valintaperusteet.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import fi.vm.sade.service.valintaperusteet.annotation.DataSetLocation;
import fi.vm.sade.service.valintaperusteet.listeners.ValinnatJTACleanInsertTestExecutionListener;
import fi.vm.sade.service.valintaperusteet.model.Hakukohdekoodi;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

/**
 * User: wuoti
 * Date: 8.5.2013
 * Time: 14.14
 */
@ContextConfiguration(locations = "classpath:test-context.xml")
@TestExecutionListeners(listeners = {
        DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
        ValinnatJTACleanInsertTestExecutionListener.class})
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
