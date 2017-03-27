package fi.vm.sade.service.valintaperusteet.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import fi.vm.sade.service.valintaperusteet.annotation.DataSetLocation;
import fi.vm.sade.service.valintaperusteet.listeners.ValinnatJTACleanInsertTestExecutionListener;
import fi.vm.sade.service.valintaperusteet.model.Funktioargumentti;
import fi.vm.sade.service.valintaperusteet.model.Funktiokutsu;
import fi.vm.sade.service.valintaperusteet.model.Laskentakaava;
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
* User: kwuoti Date: 18.1.2013 Time: 10.04
*/
@ContextConfiguration(locations = "classpath:test-context.xml")
@TestExecutionListeners(listeners = { ValinnatJTACleanInsertTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class })
@RunWith(SpringJUnit4ClassRunner.class)
@DataSetLocation("classpath:orphan-test-data.xml")
@Transactional
public class OrphanRemovalTest {

    @Autowired
    private FunktiokutsuDAO funktiokutsuDAO;

    @Autowired
    private LaskentakaavaDAO laskentakaavaDAO;

    @Autowired
    private GenericDAO dao;

    @Test
    public void testDeleteOrphans() {
        assertNotNull(funktiokutsuDAO.getFunktiokutsu(9L));
        assertEquals(1, funktiokutsuDAO.deleteOrphans());
        assertNull(funktiokutsuDAO.getFunktiokutsu(9L));
        assertNotNull(laskentakaavaDAO.getLaskentakaava(1L));
        assertNotNull(laskentakaavaDAO.getLaskentakaava(2L));
        assertNotNull(funktiokutsuDAO.getFunktiokutsu(2L));
    }
}
