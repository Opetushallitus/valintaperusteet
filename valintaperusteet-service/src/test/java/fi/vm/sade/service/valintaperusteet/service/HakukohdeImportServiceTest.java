package fi.vm.sade.service.valintaperusteet.service;

import fi.vm.sade.dbunit.annotation.DataSetLocation;
import fi.vm.sade.dbunit.listener.JTACleanInsertTestExecutionListener;
import fi.vm.sade.service.valintaperusteet.dao.HakukohdeViiteDAO;
import fi.vm.sade.service.valintaperusteet.dao.HakukohdekoodiDAO;
import fi.vm.sade.service.valintaperusteet.model.HakukohdeViite;
import fi.vm.sade.service.valintaperusteet.model.Hakukohdekoodi;
import fi.vm.sade.service.valintaperusteet.model.ValinnanVaihe;
import fi.vm.sade.service.valintaperusteet.schema.HakukohdeImportTyyppi;
import fi.vm.sade.service.valintaperusteet.schema.HakukohdekoodiTyyppi;
import fi.vm.sade.service.valintaperusteet.schema.HakukohteenValintakoeTyyppi;
import fi.vm.sade.service.valintaperusteet.schema.MonikielinenTekstiTyyppi;
import fi.vm.sade.service.valintaperusteet.service.impl.HakukohdeImportServiceImpl;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import java.util.List;

import static org.junit.Assert.*;

/**
 * User: wuoti
 * Date: 8.5.2013
 * Time: 15.06
 */
@ContextConfiguration(locations = "classpath:test-context.xml")
@TestExecutionListeners(listeners = {JTACleanInsertTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class})
@RunWith(SpringJUnit4ClassRunner.class)
@DataSetLocation("classpath:test-data.xml")
public class HakukohdeImportServiceTest {

    @Autowired
    private HakukohdekoodiDAO hakukohdekoodiDAO;

    @Autowired
    private HakukohdeViiteDAO hakukohdeViiteDAO;

    @Autowired
    private HakukohdeImportService hakukohdeImportService;

    @Autowired
    private ValinnanVaiheService valinnanVaiheService;

    @Autowired
    private HakukohdeService hakukohdeService;

    private MonikielinenTekstiTyyppi luoMonikielinenTeksti(String teksti, HakukohdeImportServiceImpl.Kieli kieli) {
        MonikielinenTekstiTyyppi t = new MonikielinenTekstiTyyppi();
        t.setLang(kieli.getUri());
        t.setText(teksti);
        return t;
    }

    private HakukohdeImportTyyppi luoHakukohdeImportTyyppi(String hakukohdeOid, String hakuOid, String koodiUri) {
        HakukohdeImportTyyppi imp = new HakukohdeImportTyyppi();
        imp.setHakukohdeOid(hakukohdeOid);
        imp.setHakuOid(hakuOid);
        imp.getHakukohdeNimi().add(luoMonikielinenTeksti(hakukohdeOid + "-nimi", HakukohdeImportServiceImpl.Kieli.FI));
        imp.getTarjoajaNimi().add(luoMonikielinenTeksti(hakukohdeOid + "-tarjoaja", HakukohdeImportServiceImpl.Kieli.FI));
        imp.getHakuKausi().add(luoMonikielinenTeksti("Syksy", HakukohdeImportServiceImpl.Kieli.FI));
        imp.setHakuVuosi("2013");

        HakukohteenValintakoeTyyppi koe = new HakukohteenValintakoeTyyppi();
        koe.setOid("oid123");
        koe.setTyyppiUri("valintakoeuri1");

        imp.getValintakoe().add(koe);

        imp.getOpetuskielet().add(HakukohdeImportServiceImpl.Kieli.FI.getUri());

        HakukohdekoodiTyyppi koodi = new HakukohdekoodiTyyppi();
        koodi.setKoodiUri(koodiUri);
        koodi.setArvo(koodiUri);
        koodi.setNimiFi(koodiUri);
        koodi.setNimiSv(koodiUri);
        koodi.setNimiEn(koodiUri);

        imp.setHakukohdekoodi(koodi);
        return imp;
    }

    @Test
    public void testSanitizeKoodi() {
        HakukohdeImportServiceImpl serviceImpl = new HakukohdeImportServiceImpl();

        final String uri1 = "http://koodinuri";
        Assert.assertEquals(uri1, serviceImpl.sanitizeKoodiUri(uri1));

        final String uri2 = "http://koodinur2#1";
        final String uri2Expected = "http://koodinur2";
        Assert.assertEquals(uri2Expected, serviceImpl.sanitizeKoodiUri(uri2));

        final String uri3 = "http://koodinur3#1#5";
        final String uri3Expected = "http://koodinur3";
        Assert.assertEquals(uri3Expected, serviceImpl.sanitizeKoodiUri(uri3));
        Assert.assertNull(null);
    }

    @Test
    public void testImportNewHakukohde() {
        final String hakukohdeOid = "uusiHakukohdeOid";
        final String hakuOid = "uusiHakuOid";
        final String koodiUri = "uusihakukohdekoodiuri";

        assertNull(hakukohdekoodiDAO.findByKoodiUri(koodiUri));
        assertNull(hakukohdeViiteDAO.readByOid(hakukohdeOid));

        HakukohdeImportTyyppi importData = luoHakukohdeImportTyyppi(hakukohdeOid, hakuOid, koodiUri);
        hakukohdeImportService.tuoHakukohde(importData);

        Hakukohdekoodi koodi = hakukohdekoodiDAO.findByKoodiUri(koodiUri);
        assertEquals(koodiUri, koodi.getUri());

        assertEquals(1, koodi.getHakukohteet().size());
        HakukohdeViite koodiHakukohde = koodi.getHakukohteet().iterator().next();
        HakukohdeViite hakukohde = hakukohdeViiteDAO.readByOid(hakukohdeOid);
        assertEquals(koodiHakukohde, hakukohde);
        assertEquals(hakukohdeOid, hakukohde.getOid());
    }

    @Test
    public void testImportHakukohdeWithExistingValintaryhma() {
        final String hakukohdeOid = "uusiHakukohdeOid";
        final String hakuOid = "uusiHakuOid";
        final String koodiUri = "hakukohdekoodiuri2";

        final String valintaryhmaOid = "oid37";

        Hakukohdekoodi koodi = hakukohdekoodiDAO.findByKoodiUri(koodiUri);
        assertEquals(valintaryhmaOid, koodi.getValintaryhma().getOid());
        assertEquals(0, koodi.getHakukohteet().size());
        assertNull(hakukohdeViiteDAO.readByOid(hakukohdeOid));

        assertEquals(0, hakukohdeViiteDAO.findByValintaryhmaOid(valintaryhmaOid).size());

        HakukohdeImportTyyppi importData = luoHakukohdeImportTyyppi(hakukohdeOid, hakuOid, koodiUri);
        hakukohdeImportService.tuoHakukohde(importData);

        koodi = hakukohdekoodiDAO.findByKoodiUri(koodiUri);
        assertNotNull(koodi.getValintaryhma());
        assertEquals(valintaryhmaOid, koodi.getValintaryhma().getOid());
        assertEquals(1, koodi.getHakukohteet().size());

        HakukohdeViite koodiHakukohde = koodi.getHakukohteet().iterator().next();
        HakukohdeViite hakukohde = hakukohdeViiteDAO.readByOid(hakukohdeOid);
        assertEquals(koodiHakukohde, hakukohde);

        List<HakukohdeViite> hakukohteetByValintaryhma = hakukohdeViiteDAO.findByValintaryhmaOid(valintaryhmaOid);
        assertEquals(1, hakukohteetByValintaryhma.size());
        assertEquals(hakukohde, hakukohteetByValintaryhma.get(0));
    }

    @Test
    public void testImportHakukohdeUnderNewValintaryhma() {
        // Oletetaan että kannassa on hakukohde, joka on valintaryhmän alla mutta hakukohde pitäisi synkata toisen
        // valintaryhmän alle. Toisin sanoen hakukohteen määrittävä hakukohdekoodi viittaa eri valintaryhmään kuin mihin
        // hakukohde on tällä hetkellä määritelty.

        final String valintaryhmaOidAluksi = "oid40";
        final String valintaryhmaOidLopuksi = "oid41";

        final String hakuOid = "hakuoid1";
        final String hakukohdeOid = "oid14";
        final String hakukohdekoodiUri = "hakukohdekoodiuri4";
        {
            Hakukohdekoodi koodi = hakukohdekoodiDAO.findByKoodiUri(hakukohdekoodiUri);
            assertEquals(valintaryhmaOidLopuksi, koodi.getValintaryhma().getOid());
            assertEquals(0, koodi.getHakukohteet().size());

            List<HakukohdeViite> hakukohteet = hakukohdeService.findByValintaryhmaOid(valintaryhmaOidAluksi);
            assertEquals(1, hakukohteet.size());
            assertEquals(hakukohdeOid, hakukohteet.get(0).getOid());

            assertEquals(0, hakukohdeService.findByValintaryhmaOid(valintaryhmaOidLopuksi).size());

            List<ValinnanVaihe> vaiheet = valinnanVaiheService.findByHakukohde(hakukohdeOid);
            assertEquals(4, vaiheet.size());
            assertTrue(vaiheet.get(0).getId().equals(95L) && vaiheet.get(0).getMasterValinnanVaihe().getId().equals(94L));
            assertTrue(vaiheet.get(1).getId().equals(96L) && vaiheet.get(1).getMasterValinnanVaihe() == null);
            assertTrue(vaiheet.get(2).getId().equals(97L) && vaiheet.get(2).getMasterValinnanVaihe().getId().equals(93L));
            assertTrue(vaiheet.get(3).getId().equals(98L) && vaiheet.get(3).getMasterValinnanVaihe() == null);
        }

        HakukohdeImportTyyppi importData = luoHakukohdeImportTyyppi(hakukohdeOid, hakuOid, hakukohdekoodiUri);
        hakukohdeImportService.tuoHakukohde(importData);

        {
            Hakukohdekoodi koodi = hakukohdekoodiDAO.findByKoodiUri(hakukohdekoodiUri);
            assertEquals(valintaryhmaOidLopuksi, koodi.getValintaryhma().getOid());
            assertEquals(1, koodi.getHakukohteet().size());
            assertEquals(hakukohdeOid, koodi.getHakukohteet().iterator().next().getOid());

            assertEquals(0, hakukohdeService.findByValintaryhmaOid(valintaryhmaOidAluksi).size());
            List<HakukohdeViite> hakukohteet = hakukohdeService.findByValintaryhmaOid(valintaryhmaOidLopuksi);
            assertEquals(1, hakukohteet.size());
            assertEquals(hakukohdeOid, hakukohteet.get(0).getOid());

            // Hakukohteelle suoraan määriteltyjen valinnanvaiheiden tulisi tulla periytyvien valinnan vaiheiden
            // jälkeen.
            List<ValinnanVaihe> vaiheet = valinnanVaiheService.findByHakukohde(hakukohdeOid);
            assertEquals(3, vaiheet.size());
            assertTrue(vaiheet.get(0).getMasterValinnanVaihe().getId().equals(99L));
            assertTrue(vaiheet.get(1).getId().equals(96L) && vaiheet.get(1).getMasterValinnanVaihe() == null);
            assertTrue(vaiheet.get(2).getId().equals(98L) && vaiheet.get(2).getMasterValinnanVaihe() == null);
        }
    }


    @Test
    public void testImportHakukohdeOutsideValintaryhma() {
        // Oletetaan että kannassa on hakukohde, joka on valintaryhmän alla mutta synkkaus siirtää hakukohteen
        // pois valintaryhmästä

        final String valintaryhmaOidAluksi = "oid40";

        final String hakuOid = "hakuoid1";
        final String hakukohdeOid = "oid14";
        final String hakukohdekoodiUri = "hakukohdekoodiuri5";
        {
            Hakukohdekoodi koodi = hakukohdekoodiDAO.findByKoodiUri(hakukohdekoodiUri);
            assertNull(koodi.getValintaryhma());
            assertEquals(0, koodi.getHakukohteet().size());

            List<HakukohdeViite> hakukohteet = hakukohdeService.findByValintaryhmaOid(valintaryhmaOidAluksi);
            assertEquals(1, hakukohteet.size());
            assertEquals(hakukohdeOid, hakukohteet.get(0).getOid());

            List<ValinnanVaihe> vaiheet = valinnanVaiheService.findByHakukohde(hakukohdeOid);
            assertEquals(4, vaiheet.size());
            assertTrue(vaiheet.get(0).getId().equals(95L) && vaiheet.get(0).getMasterValinnanVaihe().getId().equals(94L));
            assertTrue(vaiheet.get(1).getId().equals(96L) && vaiheet.get(1).getMasterValinnanVaihe() == null);
            assertTrue(vaiheet.get(2).getId().equals(97L) && vaiheet.get(2).getMasterValinnanVaihe().getId().equals(93L));
            assertTrue(vaiheet.get(3).getId().equals(98L) && vaiheet.get(3).getMasterValinnanVaihe() == null);
        }

        HakukohdeImportTyyppi importData = luoHakukohdeImportTyyppi(hakukohdeOid, hakuOid, hakukohdekoodiUri);
        hakukohdeImportService.tuoHakukohde(importData);

        {
            Hakukohdekoodi koodi = hakukohdekoodiDAO.findByKoodiUri(hakukohdekoodiUri);
            assertNull(koodi.getValintaryhma());
            assertEquals(hakukohdeOid, koodi.getHakukohteet().iterator().next().getOid());

            assertEquals(0, hakukohdeService.findByValintaryhmaOid(valintaryhmaOidAluksi).size());
            HakukohdeViite hakukohde = hakukohdeService.readByOid(hakukohdeOid);

            assertNull(hakukohde.getValintaryhma());

            List<ValinnanVaihe> vaiheet = valinnanVaiheService.findByHakukohde(hakukohdeOid);
            assertEquals(2, vaiheet.size());
            assertTrue(vaiheet.get(0).getId().equals(96L) && vaiheet.get(0).getMasterValinnanVaihe() == null);
            assertTrue(vaiheet.get(1).getId().equals(98L) && vaiheet.get(1).getMasterValinnanVaihe() == null);
        }
    }

    @Test
    public void testHakukohdeSynkassa() {
        // Testaa, että hakukohteelle ei tehdä mitään, jos se on jo valmiiksi oikean valintaryhmän alla
        final String valintaryhmaOidAluksi = "oid40";

        final String hakuOid = "hakuoid1";
        final String hakukohdeOid = "oid14";
        final String hakukohdekoodiUri = "hakukohdekoodiuri6";
        {
            Hakukohdekoodi koodi = hakukohdekoodiDAO.findByKoodiUri(hakukohdekoodiUri);
            assertEquals(valintaryhmaOidAluksi, koodi.getValintaryhma().getOid());
            assertEquals(0, koodi.getHakukohteet().size());

            List<HakukohdeViite> hakukohteet = hakukohdeService.findByValintaryhmaOid(valintaryhmaOidAluksi);
            assertEquals(1, hakukohteet.size());
            HakukohdeViite hakukohde = hakukohteet.get(0);
            assertEquals(hakukohdeOid, hakukohde.getOid());
            //  assertFalse(hakukohdeOid.equals(hakukohde.getNimi()));

            List<ValinnanVaihe> vaiheet = valinnanVaiheService.findByHakukohde(hakukohdeOid);
            assertEquals(4, vaiheet.size());
            assertTrue(vaiheet.get(0).getId().equals(95L) && vaiheet.get(0).getMasterValinnanVaihe().getId().equals(94L));
            assertTrue(vaiheet.get(1).getId().equals(96L) && vaiheet.get(1).getMasterValinnanVaihe() == null);
            assertTrue(vaiheet.get(2).getId().equals(97L) && vaiheet.get(2).getMasterValinnanVaihe().getId().equals(93L));
            assertTrue(vaiheet.get(3).getId().equals(98L) && vaiheet.get(3).getMasterValinnanVaihe() == null);
        }

        HakukohdeImportTyyppi importData = luoHakukohdeImportTyyppi(hakukohdeOid, hakuOid, hakukohdekoodiUri);
        hakukohdeImportService.tuoHakukohde(importData);

        {
            Hakukohdekoodi koodi = hakukohdekoodiDAO.findByKoodiUri(hakukohdekoodiUri);
            assertEquals(valintaryhmaOidAluksi, koodi.getValintaryhma().getOid());
            assertEquals(hakukohdeOid, koodi.getHakukohteet().iterator().next().getOid());

            List<HakukohdeViite> hakukohteet = hakukohdeService.findByValintaryhmaOid(valintaryhmaOidAluksi);
            assertEquals(1, hakukohteet.size());
            HakukohdeViite hakukohde = hakukohteet.get(0);
            assertEquals(hakukohdeOid, hakukohde.getOid());
            //   assertEquals(hakukohdeOid, hakukohde.getNimi());

            List<ValinnanVaihe> vaiheet = valinnanVaiheService.findByHakukohde(hakukohdeOid);
            assertEquals(4, vaiheet.size());
            assertTrue(vaiheet.get(0).getId().equals(95L) && vaiheet.get(0).getMasterValinnanVaihe().getId().equals(94L));
            assertTrue(vaiheet.get(1).getId().equals(96L) && vaiheet.get(1).getMasterValinnanVaihe() == null);
            assertTrue(vaiheet.get(2).getId().equals(97L) && vaiheet.get(2).getMasterValinnanVaihe().getId().equals(93L));
            assertTrue(vaiheet.get(3).getId().equals(98L) && vaiheet.get(3).getMasterValinnanVaihe() == null);
        }
    }
}
