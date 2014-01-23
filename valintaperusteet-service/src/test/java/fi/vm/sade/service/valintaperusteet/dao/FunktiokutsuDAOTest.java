package fi.vm.sade.service.valintaperusteet.dao;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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

import fi.vm.sade.dbunit.annotation.DataSetLocation;
import fi.vm.sade.dbunit.listener.JTACleanInsertTestExecutionListener;
import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi;
import fi.vm.sade.service.valintaperusteet.model.Funktioargumentti;
import fi.vm.sade.service.valintaperusteet.model.Funktiokutsu;

/**
 * User: kwuoti Date: 18.1.2013 Time: 10.04
 */
@ContextConfiguration(locations = "classpath:test-context.xml")
@TestExecutionListeners(listeners = { JTACleanInsertTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class })
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@DataSetLocation("classpath:test-data.xml")
public class FunktiokutsuDAOTest {

    @Autowired
    private FunktiokutsuDAO funktiokutsuDAO;

    @Test
    public void testGetLukuarvo() {
        final Long id = 1L;

        Funktiokutsu funktiokutsu = funktiokutsuDAO.getFunktiokutsu(id);
        assertEquals(fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi.LUKUARVO, funktiokutsu.getFunktionimi());
    }

    @Test
    public void testGetTotuusarvo() {
        final Long id = 101L;

        Funktiokutsu funktiokutsu = funktiokutsuDAO.getFunktiokutsu(id);
        assertEquals(Funktionimi.TOTUUSARVO, funktiokutsu.getFunktionimi());
    }

    @Test
    public void testGetSumma() {
        final Long id = 201L;

        Funktiokutsu funktiokutsu = funktiokutsuDAO.getFunktiokutsu(id);
        assertEquals(Funktionimi.SUMMA, funktiokutsu.getFunktionimi());
        assertEquals(3, funktiokutsu.getFunktioargumentit().size());

        List<Funktioargumentti> args = new ArrayList<Funktioargumentti>(funktiokutsu.getFunktioargumentit());
        Collections.sort(args, new Comparator<Funktioargumentti>() {
            @Override
            public int compare(Funktioargumentti o1, Funktioargumentti o2) {
                return o1.getIndeksi().compareTo(o2.getIndeksi());
            }
        });

        assertEquals(Funktionimi.LUKUARVO, args.get(0).getFunktiokutsuChild().getFunktionimi());
        assertEquals(Funktionimi.LUKUARVO, args.get(1).getFunktiokutsuChild().getFunktionimi());
        assertEquals(Funktionimi.HAELUKUARVO, args.get(2).getFunktiokutsuChild().getFunktionimi());
    }

    @Test
    public void testGetLukuarvoluvuksi() {
        final Long id = 301L;
        Funktiokutsu funktiokutsu = funktiokutsuDAO.getFunktiokutsu(id);
        assertEquals(Funktionimi.KONVERTOILUKUARVO, funktiokutsu.getFunktionimi());
        assertEquals(1, funktiokutsu.getFunktioargumentit().size());

        Funktiokutsu arg = funktiokutsu.getFunktioargumentit().iterator().next().getFunktiokutsuChild();
        assertEquals(Funktionimi.LUKUARVO, arg.getFunktionimi());

        assertEquals(3, funktiokutsu.getArvokonvertteriparametrit().size());
    }

    @Test
    public void testGetLukuarvovalilukuarvoksi() {
        final Long id = 302L;
        Funktiokutsu funktiokutsu = funktiokutsuDAO.getFunktiokutsu(id);
        assertEquals(Funktionimi.KONVERTOILUKUARVO, funktiokutsu.getFunktionimi());
        assertEquals(1, funktiokutsu.getFunktioargumentit().size());

        Funktiokutsu arg = funktiokutsu.getFunktioargumentit().iterator().next().getFunktiokutsuChild();
        assertEquals(Funktionimi.LUKUARVO, arg.getFunktionimi());

        assertEquals(3, funktiokutsu.getArvovalikonvertteriparametrit().size());

    }

    @Test
    public void testDeleteOrphans() {
        List<Funktiokutsu> orphans = funktiokutsuDAO.getOrphans();
        assertEquals(3, orphans.size());
        Collections.sort(orphans, new Comparator<Funktiokutsu>() {
            @Override
            public int compare(Funktiokutsu o1, Funktiokutsu o2) {
                return o1.getId().compareTo(o2.getId());
            }
        });

        assertEquals(101L, orphans.get(0).getId().longValue());
        assertEquals(301L, orphans.get(1).getId().longValue());
        assertEquals(302L, orphans.get(2).getId().longValue());

        funktiokutsuDAO.deleteOrphans();
        assertNull(funktiokutsuDAO.getFunktiokutsu(101L));
        assertNull(funktiokutsuDAO.getFunktiokutsu(301L));
        assertNull(funktiokutsuDAO.getFunktiokutsu(302L));

        assertEquals(0, funktiokutsuDAO.getOrphans().size());
    }

    @Test
    public void testFindFunktiokutsuByHakukohdeOids() {
        List<Funktiokutsu> kaavat = funktiokutsuDAO.findFunktiokutsuByHakukohdeOids("oid17");
        assertEquals(2, kaavat.size());
    }
}
