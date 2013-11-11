package fi.vm.sade.service.valintaperusteet.service;

import fi.vm.sade.dbunit.annotation.DataSetLocation;
import fi.vm.sade.dbunit.listener.JTACleanInsertTestExecutionListener;
import fi.vm.sade.service.valintaperusteet.ValintaperusteService;
import fi.vm.sade.service.valintaperusteet.messages.HakuparametritTyyppi;
import fi.vm.sade.service.valintaperusteet.schema.*;
import fi.vm.sade.service.valintaperusteet.service.exception.HakuparametritOnTyhjaException;
import fi.vm.sade.service.valintaperusteet.service.exception.ValinnanVaiheEpaaktiivinenException;
import fi.vm.sade.service.valintaperusteet.service.exception.ValinnanVaiheJarjestyslukuOutOfBoundsException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import java.util.*;

import static junit.framework.Assert.*;

/**
 * User: kwuoti Date: 22.1.2013 Time: 15.40
 */
@ContextConfiguration(locations = "classpath:test-context.xml")
@TestExecutionListeners(listeners = {JTACleanInsertTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class})
@RunWith(SpringJUnit4ClassRunner.class)
@DataSetLocation("classpath:test-data.xml")
public class ValintaperusteServiceTest {

    @Autowired
    private ValintaperusteService valintaperusteService;

    @Test
    public void testHaeValintatapajonotSijoittelulle() {
        final String hakukohdeoid = "oid6";

        assertEquals(9, valintaperusteService.haeValintatapajonotSijoittelulle(hakukohdeoid).size());
    }

    @Test
    public void testHaeValintaperusteet() {
        List<HakuparametritTyyppi> params = new ArrayList<HakuparametritTyyppi>();
        params.add(getHakuparametritTyyppi("oid6", null));

        Set<String> tunnisteet = new HashSet<String>(Arrays.asList(new String[]{"tunniste1", "tunniste2", "tunniste3", "tunniste4"}));

        List<ValintaperusteetTyyppi> valintaperusteetTyyppis = valintaperusteService.haeValintaperusteet(params);

        assertEquals(3, valintaperusteetTyyppis.size());
        for (ValintaperusteetTyyppi vp : valintaperusteetTyyppis) {
            assertEquals(tunnisteet.size(), vp.getHakukohteenValintaperuste().size());
            for (HakukohteenValintaperusteTyyppi hkvpt : vp.getHakukohteenValintaperuste()) {
                assertTrue(tunnisteet.contains(hkvpt.getTunniste()));
                assertNotNull(hkvpt.getArvo());
            }
        }

        assertEquals(1, valintaperusteetTyyppis.get(1).getValinnanVaihe().getValinnanVaiheJarjestysluku());
        assertEquals(3, ((TavallinenValinnanVaiheTyyppi) valintaperusteetTyyppis.get(0).getValinnanVaihe()).getValintatapajono().get(0).getJarjestyskriteerit().size());
        assertEquals(1, ((TavallinenValinnanVaiheTyyppi) valintaperusteetTyyppis.get(0).getValinnanVaihe()).getValintatapajono().get(1).getPrioriteetti());
        assertEquals(1, ((TavallinenValinnanVaiheTyyppi) valintaperusteetTyyppis.get(0).getValinnanVaihe()).getValintatapajono().get(0).getJarjestyskriteerit().get(1).getPrioriteetti());
    }

    @Test
    public void testHaeValintaperusteetJarjestysluvulla() {
        List<HakuparametritTyyppi> params = new ArrayList<HakuparametritTyyppi>();
        params.add(getHakuparametritTyyppi("oid6", 2));
        List<ValintaperusteetTyyppi> valintaperusteetTyyppis = valintaperusteService.haeValintaperusteet(params);

        assertEquals(1, valintaperusteetTyyppis.size());
    }

    @Test(expected = ValinnanVaiheJarjestyslukuOutOfBoundsException.class)
    public void testHaeValintaperusteetEpakelvollaJarjestysluvulla() {
        List<HakuparametritTyyppi> params = new ArrayList<HakuparametritTyyppi>();
        params.add(getHakuparametritTyyppi("oid6", -1));
        valintaperusteService.haeValintaperusteet(params);
    }

    @Test(expected = ValinnanVaiheJarjestyslukuOutOfBoundsException.class)
    public void testHaeValintaperusteetLiianIsollaJarjestysluvulla() {
        List<HakuparametritTyyppi> params = new ArrayList<HakuparametritTyyppi>();
        params.add(getHakuparametritTyyppi("oid6", 77));
        valintaperusteService.haeValintaperusteet(params);
    }

    @Test
    public void testHaeValintaperusteetJarjestysluvulla2() {
        List<HakuparametritTyyppi> params = new ArrayList<HakuparametritTyyppi>();
        params.add(getHakuparametritTyyppi("oid6", 2));
        params.add(getHakuparametritTyyppi("oid2", 2));
        List<ValintaperusteetTyyppi> valintaperusteetTyyppis = valintaperusteService.haeValintaperusteet(params);

        assertEquals(2, valintaperusteetTyyppis.size());
    }

    @Test(expected = HakuparametritOnTyhjaException.class)
    public void testHaeValintaperusteetNullilla() {
        valintaperusteService.haeValintaperusteet(null);
    }

    @Test
    public void testHaeValintaperusteet2() {
        List<HakuparametritTyyppi> params = new ArrayList<HakuparametritTyyppi>();

        params.add(getHakuparametritTyyppi("oid1", null));
        params.add(getHakuparametritTyyppi("oid2", null));
        params.add(getHakuparametritTyyppi("oid3", null));
        params.add(getHakuparametritTyyppi("oid4", null));
        params.add(getHakuparametritTyyppi("oid5", null));
        params.add(getHakuparametritTyyppi("oid6", null));

        List<ValintaperusteetTyyppi> valintaperusteetTyyppis = valintaperusteService.haeValintaperusteet(params);

        assertEquals(6, valintaperusteetTyyppis.size());
    }

    private HakuparametritTyyppi getHakuparametritTyyppi(String oid, Integer jl) {
        HakuparametritTyyppi hakuparametritTyyppi = new HakuparametritTyyppi();
        hakuparametritTyyppi.setHakukohdeOid(oid);
        hakuparametritTyyppi.setValinnanVaiheJarjestysluku(jl);
        return hakuparametritTyyppi;
    }

    @Test
    public void testHaeValintakoeValintaperusteet() {
        List<HakuparametritTyyppi> params = new ArrayList<HakuparametritTyyppi>();
        params.add(getHakuparametritTyyppi("oid10", null));

        List<ValintaperusteetTyyppi> valintaperusteetTyyppis = valintaperusteService.haeValintaperusteet(params);
        assertEquals(1, valintaperusteetTyyppis.size());

        ValintakoeValinnanVaiheTyyppi vv = (ValintakoeValinnanVaiheTyyppi)
                valintaperusteetTyyppis.get(0).getValinnanVaihe();

        assertNotNull(vv.getValinnanVaiheJarjestysluku());
        assertNotNull(vv.getValinnanVaiheOid());
        assertEquals(1, vv.getValintakoe().size());

        ValintakoeTyyppi vk = vv.getValintakoe().get(0);
        assertNotNull(vk.getFunktiokutsu());
        assertNotNull(vk.getKuvaus());
        assertNotNull(vk.getNimi());
        assertNotNull(vk.getOid());
        assertNotNull(vk.getTunniste());
    }

    @Test(expected = ValinnanVaiheEpaaktiivinenException.class)
    public void testHaeValintaperusteetMukanaEpaaktiivisiaVaiheita() {
        List<HakuparametritTyyppi> params = new ArrayList<HakuparametritTyyppi>();
        params.add(getHakuparametritTyyppi("oid21", 0));
        List<ValintaperusteetTyyppi> vps = valintaperusteService.haeValintaperusteet(params);
        assertEquals(1, vps.size());
        assertEquals("108", vps.get(0).getValinnanVaihe().getValinnanVaiheOid());

        params = new ArrayList<HakuparametritTyyppi>();
        params.add(getHakuparametritTyyppi("oid21", 2));
        vps = valintaperusteService.haeValintaperusteet(params);
        assertEquals(1, vps.size());
        assertEquals("110", vps.get(0).getValinnanVaihe().getValinnanVaiheOid());

        params = new ArrayList<HakuparametritTyyppi>();
        params.add(getHakuparametritTyyppi("oid21", 1));
        valintaperusteService.haeValintaperusteet(params);
    }

    @Test
    public void testHaeKaikkiValintaperusteetMukanaEpaaktiivisiaVaiheita() {
        List<HakuparametritTyyppi> params = new ArrayList<HakuparametritTyyppi>();
        params.add(getHakuparametritTyyppi("oid21", null));
        List<ValintaperusteetTyyppi> vps = valintaperusteService.haeValintaperusteet(params);
        assertEquals(2, vps.size());
        assertEquals("108", vps.get(0).getValinnanVaihe().getValinnanVaiheOid());
        assertEquals("110", vps.get(1).getValinnanVaihe().getValinnanVaiheOid());
    }

    @Test
    public void testHakukohdettaEiOleOlemassa() {
        List<HakuparametritTyyppi> params = new ArrayList<HakuparametritTyyppi>();
        params.add(getHakuparametritTyyppi("ei-ole-olemassa", 1));

        assertEquals(0, valintaperusteService.haeValintaperusteet(params).size());
    }
}
