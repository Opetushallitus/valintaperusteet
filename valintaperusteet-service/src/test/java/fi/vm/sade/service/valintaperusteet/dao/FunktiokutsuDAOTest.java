package fi.vm.sade.service.valintaperusteet.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import fi.vm.sade.service.valintaperusteet.annotation.DataSetLocation;
import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi;
import fi.vm.sade.service.valintaperusteet.listeners.ValinnatJTACleanInsertTestExecutionListener;
import fi.vm.sade.service.valintaperusteet.model.Arvokonvertteriparametri;
import fi.vm.sade.service.valintaperusteet.model.Funktioargumentti;
import fi.vm.sade.service.valintaperusteet.model.Funktiokutsu;
import fi.vm.sade.service.valintaperusteet.model.TekstiRyhma;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * User: kwuoti Date: 18.1.2013 Time: 10.04
 */
@ContextConfiguration(locations = "classpath:test-context.xml")
@TestExecutionListeners(listeners = { ValinnatJTACleanInsertTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class })
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@DataSetLocation("classpath:test-data.xml")
public class FunktiokutsuDAOTest {

    @Autowired
    private FunktiokutsuDAO funktiokutsuDAO;

    @Autowired
    private GenericDAO dao;

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
        assertEquals(3, funktiokutsuDAO.deleteOrphans());
        assertNull(funktiokutsuDAO.getFunktiokutsu(101L));
        assertNull(funktiokutsuDAO.getFunktiokutsu(301L));
        assertNull(funktiokutsuDAO.getFunktiokutsu(302L));
    }

    @Test
    public void testFindFunktiokutsuByHakukohdeOids() {
        List<Funktiokutsu> kaavat = funktiokutsuDAO.findFunktiokutsuByHakukohdeOid("oid17");
        assertEquals(2, kaavat.size());
    }

    @Test
    public void testGetHylkaysperuste() {
        final Long id = 708L;

        Funktiokutsu funktiokutsu = funktiokutsuDAO.getFunktiokutsu(id);

        for (Arvokonvertteriparametri ak : funktiokutsu.getArvokonvertteriparametrit()) {
            TekstiRyhma ryhma = ak.getKuvaukset();
            if(ryhma.getId().equals(1L)) {
                assertEquals(3, ryhma.getTekstit().size());
            } else {
                assertEquals(1, ryhma.getTekstit().size());
            }
        }

    }
}
