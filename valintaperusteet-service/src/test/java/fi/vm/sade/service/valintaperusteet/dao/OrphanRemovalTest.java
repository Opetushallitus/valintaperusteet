package fi.vm.sade.service.valintaperusteet.dao;

import static junit.framework.Assert.assertEquals;

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
    private GenericDAO dao;


    @Test
    public void testDeleteOrphans() {
        List<Long> orphans = funktiokutsuDAO.getOrphans();
        assertEquals(1, orphans.size());

        assertEquals(9L, orphans.get(0).longValue());

        Funktiokutsu kutsu = funktiokutsuDAO.getFunktiokutsu(9L);
        Laskentakaava laskentakaava = dao.read(Laskentakaava.class, 1L);
        laskentakaava.setFunktiokutsu(kutsu);
        dao.update(laskentakaava);

        List<Funktioargumentti> funktioargumenttis = dao.findAll(Funktioargumentti.class);
        List<Funktiokutsu> funktiokutsus = dao.findAll(Funktiokutsu.class);

        assertEquals(9, funktioargumenttis.size());
        assertEquals(10, funktiokutsus.size());

        poistaOrvot();

        assertEquals(0, funktiokutsuDAO.getOrphans().size());

        funktioargumenttis = dao.findAll(Funktioargumentti.class);
        funktiokutsus = dao.findAll(Funktiokutsu.class);

        assertEquals(2, funktiokutsus.size());
        assertEquals(1, funktioargumenttis.size());
    }

    private void poistaOrvot() {
        List<Long> orphans = funktiokutsuDAO.getOrphans();
        orphans.forEach(funktiokutsuDAO::deleteOrphan);
        if(orphans.size() > 0) {
            poistaOrvot();
        }
    }

}
