package fi.vm.sade.service.valintaperusteet.service;

import fi.vm.sade.dbunit.annotation.DataSetLocation;
import fi.vm.sade.dbunit.listener.JTACleanInsertTestExecutionListener;
import fi.vm.sade.service.valintaperusteet.dao.HakukohdeViiteDAO;
import fi.vm.sade.service.valintaperusteet.dao.HakukohdekoodiDAO;
import fi.vm.sade.service.valintaperusteet.model.*;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

    @Autowired
    private ValintaryhmaService valintaryhmaService;

    @Autowired
    private ValintatapajonoService valintatapajonoService;

    private static final String OLETUS_VALINTAKOEURI = "valintakoeuri1";

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
        koe.setTyyppiUri(OLETUS_VALINTAKOEURI);

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

        assertNull(hakukohdekoodiDAO.readByUri(koodiUri));
        assertNull(hakukohdeViiteDAO.readByOid(hakukohdeOid));

        HakukohdeImportTyyppi importData = luoHakukohdeImportTyyppi(hakukohdeOid, hakuOid, koodiUri);
        hakukohdeImportService.tuoHakukohde(importData);

        Hakukohdekoodi koodi = hakukohdekoodiDAO.readByUri(koodiUri);
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

        Hakukohdekoodi koodi = hakukohdekoodiDAO.readByUri(koodiUri);
        assertEquals(valintaryhmaOid, koodi.getValintaryhmat().iterator().next().getOid());
        assertEquals(0, koodi.getHakukohteet().size());
        assertNull(hakukohdeViiteDAO.readByOid(hakukohdeOid));

        assertEquals(0, hakukohdeViiteDAO.findByValintaryhmaOid(valintaryhmaOid).size());

        HakukohdeImportTyyppi importData = luoHakukohdeImportTyyppi(hakukohdeOid, hakuOid, koodiUri);
        hakukohdeImportService.tuoHakukohde(importData);

        koodi = hakukohdekoodiDAO.readByUri(koodiUri);
        assertEquals(1, koodi.getValintaryhmat().size());
        assertEquals(valintaryhmaOid, koodi.getValintaryhmat().iterator().next().getOid());
        assertEquals(1, koodi.getHakukohteet().size());

        HakukohdeViite koodiHakukohde = koodi.getHakukohteet().iterator().next();
        HakukohdeViite hakukohde = hakukohdeViiteDAO.readByOid(hakukohdeOid);
        assertFalse(hakukohde.getManuaalisestiSiirretty());
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
            Hakukohdekoodi koodi = hakukohdekoodiDAO.readByUri(hakukohdekoodiUri);
            assertEquals(valintaryhmaOidLopuksi, koodi.getValintaryhmat().iterator().next().getOid());
            assertEquals(0, koodi.getHakukohteet().size());

            List<HakukohdeViite> hakukohteet = hakukohdeService.findByValintaryhmaOid(valintaryhmaOidAluksi);
            assertEquals(1, hakukohteet.size());
            assertEquals(hakukohdeOid, hakukohteet.get(0).getOid());
            assertNull(hakukohteet.get(0).getManuaalisestiSiirretty());

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
            Hakukohdekoodi koodi = hakukohdekoodiDAO.readByUri(hakukohdekoodiUri);
            assertEquals(valintaryhmaOidLopuksi, koodi.getValintaryhmat().iterator().next().getOid());
            assertEquals(1, koodi.getHakukohteet().size());
            assertEquals(hakukohdeOid, koodi.getHakukohteet().iterator().next().getOid());

            assertEquals(0, hakukohdeService.findByValintaryhmaOid(valintaryhmaOidAluksi).size());
            List<HakukohdeViite> hakukohteet = hakukohdeService.findByValintaryhmaOid(valintaryhmaOidLopuksi);
            assertEquals(1, hakukohteet.size());
            assertEquals(hakukohdeOid, hakukohteet.get(0).getOid());
            assertFalse(hakukohteet.get(0).getManuaalisestiSiirretty());


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
            Hakukohdekoodi koodi = hakukohdekoodiDAO.readByUri(hakukohdekoodiUri);
            assertEquals(0, koodi.getValintaryhmat().size());
            assertEquals(0, koodi.getHakukohteet().size());

            List<HakukohdeViite> hakukohteet = hakukohdeService.findByValintaryhmaOid(valintaryhmaOidAluksi);
            assertEquals(1, hakukohteet.size());
            assertEquals(hakukohdeOid, hakukohteet.get(0).getOid());
            assertNull(hakukohteet.get(0).getManuaalisestiSiirretty());

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
            Hakukohdekoodi koodi = hakukohdekoodiDAO.readByUri(hakukohdekoodiUri);
            assertEquals(0, koodi.getValintaryhmat().size());
            assertEquals(hakukohdeOid, koodi.getHakukohteet().iterator().next().getOid());

            assertEquals(0, hakukohdeService.findByValintaryhmaOid(valintaryhmaOidAluksi).size());
            HakukohdeViite hakukohde = hakukohdeService.readByOid(hakukohdeOid);
            assertFalse(hakukohde.getManuaalisestiSiirretty());

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
            Hakukohdekoodi koodi = hakukohdekoodiDAO.readByUri(hakukohdekoodiUri);
            assertEquals(valintaryhmaOidAluksi, koodi.getValintaryhmat().iterator().next().getOid());
            assertEquals(0, koodi.getHakukohteet().size());

            List<HakukohdeViite> hakukohteet = hakukohdeService.findByValintaryhmaOid(valintaryhmaOidAluksi);
            assertEquals(1, hakukohteet.size());
            HakukohdeViite hakukohde = hakukohteet.get(0);
            assertEquals(hakukohdeOid, hakukohde.getOid());
            assertNull(hakukohde.getManuaalisestiSiirretty());

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
            Hakukohdekoodi koodi = hakukohdekoodiDAO.readByUri(hakukohdekoodiUri);
            assertEquals(valintaryhmaOidAluksi, koodi.getValintaryhmat().iterator().next().getOid());
            assertEquals(hakukohdeOid, koodi.getHakukohteet().iterator().next().getOid());

            List<HakukohdeViite> hakukohteet = hakukohdeService.findByValintaryhmaOid(valintaryhmaOidAluksi);
            assertEquals(1, hakukohteet.size());
            HakukohdeViite hakukohde = hakukohteet.get(0);
            assertEquals(hakukohdeOid, hakukohde.getOid());
            assertFalse(hakukohde.getManuaalisestiSiirretty());

            List<ValinnanVaihe> vaiheet = valinnanVaiheService.findByHakukohde(hakukohdeOid);
            assertEquals(4, vaiheet.size());
            assertTrue(vaiheet.get(0).getId().equals(95L) && vaiheet.get(0).getMasterValinnanVaihe().getId().equals(94L));
            assertTrue(vaiheet.get(1).getId().equals(96L) && vaiheet.get(1).getMasterValinnanVaihe() == null);
            assertTrue(vaiheet.get(2).getId().equals(97L) && vaiheet.get(2).getMasterValinnanVaihe().getId().equals(93L));
            assertTrue(vaiheet.get(3).getId().equals(98L) && vaiheet.get(3).getMasterValinnanVaihe() == null);
        }
    }

    @Test
    public void testKaksiValintaryhmaaSamoillaKoodeilla() {
        // Kannassa on kaksi valintaryhmää samoilla hakukohdekoodeilla, samoilla opetuskielikoodeilla ja samoilla
        // valintakoekooddeilla varustettuna. Uuden hakukohteen tulisi valua juureen, koska
        // valintaryhmää ei voida yksilöidä.

        final String valintaryhmaOid1 = "oid46";
        final String valintaryhmaOid2 = "oid47";

        final String hakuOid = "hakuoid1";
        final String hakukohdeOid = "uusihakukohdeoid";
        final String hakukohdekoodiUri = "hakukohdekoodiuri12";

        {
            assertNull(hakukohdeViiteDAO.readByOid(hakukohdeOid));
            Hakukohdekoodi koodi = hakukohdekoodiDAO.readByUri(hakukohdekoodiUri);
            assertNotNull(koodi);
            assertEquals(2, koodi.getValintaryhmat().size());
            List<Valintaryhma> valintaryhmat = new ArrayList<Valintaryhma>(koodi.getValintaryhmat());
            Collections.sort(valintaryhmat, new Comparator<Valintaryhma>() {
                @Override
                public int compare(Valintaryhma o1, Valintaryhma o2) {
                    return o1.getOid().compareTo(o2.getOid());
                }
            });

            assertEquals(valintaryhmaOid1, valintaryhmat.get(0).getOid());
            assertEquals(valintaryhmaOid2, valintaryhmat.get(1).getOid());

            Valintaryhma valintaryhma1 = valintaryhmaService.readByOid(valintaryhmaOid1);
            Valintaryhma valintaryhma2 = valintaryhmaService.readByOid(valintaryhmaOid2);
            assertTrue(valintaryhma1.getOpetuskielikoodit().size() == 1
                    && HakukohdeImportServiceImpl.Kieli.FI.getUri().equals(valintaryhma1.getOpetuskielikoodit().iterator().next().getUri()));
            assertTrue(valintaryhma2.getOpetuskielikoodit().size() == 1
                    && HakukohdeImportServiceImpl.Kieli.FI.getUri().equals(valintaryhma2.getOpetuskielikoodit().iterator().next().getUri()));

            assertTrue(valintaryhma1.getValintakoekoodit().size() == 1
                    && OLETUS_VALINTAKOEURI.equals(valintaryhma1.getValintakoekoodit().iterator().next().getUri()));
            assertTrue(valintaryhma2.getValintakoekoodit().size() == 1
                    && OLETUS_VALINTAKOEURI.equals(valintaryhma2.getValintakoekoodit().iterator().next().getUri()));
        }

        HakukohdeImportTyyppi importData = luoHakukohdeImportTyyppi(hakukohdeOid, hakuOid, hakukohdekoodiUri);
        hakukohdeImportService.tuoHakukohde(importData);

        {
            HakukohdeViite hakukohde = hakukohdeViiteDAO.readByOid(hakukohdeOid);
            assertFalse(hakukohde.getManuaalisestiSiirretty());
            assertNotNull(hakukohde);
            assertNull(hakukohde.getValintaryhma());
        }
    }

    @Test
    public void testPaivitaAloituspaikkojenLkm() {
        final String valintaryhmaOid = "oid48";

        final String hakuOid = "hakuoid1";
        final String hakukohdeOid = "uusihakukohdeoid";
        final String hakukohdekoodiUri = "hakukohdekoodiuri13";

        final int aloituspaikat = 100;
        {
            assertNull(hakukohdeViiteDAO.readByOid(hakukohdeOid));
            Valintaryhma valintaryhma = valintaryhmaService.readByOid(valintaryhmaOid);
            assertNotNull(valintaryhma);

            List<ValinnanVaihe> valinnanVaiheet = valinnanVaiheService.findByValintaryhma(valintaryhmaOid);
            assertEquals(1, valinnanVaiheet.size());

            ValinnanVaihe vaihe = valinnanVaiheet.get(0);
            assertEquals(ValinnanVaiheTyyppi.TAVALLINEN, vaihe.getValinnanVaiheTyyppi());
            assertNull(vaihe.getMasterValinnanVaihe());

            List<Valintatapajono> jonot = valintatapajonoService.findJonoByValinnanvaihe(vaihe.getOid());
            assertEquals(1, jonot.size());

            Valintatapajono jono = jonot.get(0);
            assertNull(jono.getMasterValintatapajono());
            assertFalse(aloituspaikat == jono.getAloituspaikat().intValue());

        }

        HakukohdeImportTyyppi importData = luoHakukohdeImportTyyppi(hakukohdeOid, hakuOid, hakukohdekoodiUri);
        importData.setValinnanAloituspaikat(aloituspaikat);

        hakukohdeImportService.tuoHakukohde(importData);
        {
            HakukohdeViite hakukohde = hakukohdeViiteDAO.readByOid(hakukohdeOid);
            assertNotNull(hakukohde);
            assertFalse(hakukohde.getManuaalisestiSiirretty());

            List<ValinnanVaihe> valinnanVaiheet = valinnanVaiheService.findByHakukohde(hakukohdeOid);
            assertEquals(1, valinnanVaiheet.size());

            ValinnanVaihe vaihe = valinnanVaiheet.get(0);
            assertEquals(ValinnanVaiheTyyppi.TAVALLINEN, vaihe.getValinnanVaiheTyyppi());
            assertNotNull(vaihe.getMasterValinnanVaihe());

            List<Valintatapajono> jonot = valintatapajonoService.findJonoByValinnanvaihe(vaihe.getOid());
            assertEquals(1, jonot.size());

            Valintatapajono jono = jonot.get(0);
            assertNotNull(jono.getMasterValintatapajono());
            assertEquals(aloituspaikat, jono.getAloituspaikat().intValue());
        }
    }

    @Test
    public void testMelkeinSopivaValintaryhma() {
        // Kannassa on valintaryhmä joka täsmää melkein importoitavaan hakukohteeseen. Ainoa ero on, että sama
        // valintakoekoodi on lisätty valintaryhmälle kaksi kertaa.

        final String valintaryhmaOid = "oid49";

        final String hakuOid = "hakuoid1";
        final String hakukohdeOid = "uusihakukohdeoid";
        final String hakukohdekoodiUri = "hakukohdekoodiuri14";

        {
            assertNull(hakukohdeViiteDAO.readByOid(hakukohdeOid));

            Hakukohdekoodi koodi = hakukohdekoodiDAO.readByUri(hakukohdekoodiUri);
            assertNotNull(koodi);
            assertEquals(1, koodi.getValintaryhmat().size());
            assertEquals(valintaryhmaOid, koodi.getValintaryhmat().iterator().next().getOid());

            Valintaryhma valintaryhma1 = valintaryhmaService.readByOid(valintaryhmaOid);
            assertTrue(valintaryhma1.getOpetuskielikoodit().size() == 1
                    && HakukohdeImportServiceImpl.Kieli.FI.getUri().equals(valintaryhma1.getOpetuskielikoodit().iterator().next().getUri()));

            List<Valintakoekoodi> valintakoekoodit = valintaryhma1.getValintakoekoodit();
            assertEquals(2, valintakoekoodit.size());
            assertEquals(valintakoekoodit.get(0).getUri(), OLETUS_VALINTAKOEURI);
            assertEquals(valintakoekoodit.get(1).getUri(), OLETUS_VALINTAKOEURI);
        }

        HakukohdeImportTyyppi importData = luoHakukohdeImportTyyppi(hakukohdeOid, hakuOid, hakukohdekoodiUri);
        HakukohteenValintakoeTyyppi koe = new HakukohteenValintakoeTyyppi();
        koe.setTyyppiUri("toinenkoodityyppiuri");
        koe.setOid("toinenoid");
        importData.getValintakoe().add(koe);

        hakukohdeImportService.tuoHakukohde(importData);

        {
            HakukohdeViite hakukohde = hakukohdeViiteDAO.readByOid(hakukohdeOid);
            assertFalse(hakukohde.getManuaalisestiSiirretty());
            assertNotNull(hakukohde);
            assertNull(hakukohde.getValintaryhma());
        }
    }

    @Test
    public void testManuaalisestiSiirrettyHakukohde() {
        // Testaa, että hakukohteelle ei tehdä mitään, jos se on siirretty manuaalisesti
        final String valintaryhmaOid = "oid55";

        final String hakuOid = "hakuoid1";
        final String hakukohdeOid = "oid20";
        final String hakukohdekoodiUri = "hakukohdekoodiuri21";
        {
            Hakukohdekoodi koodi = hakukohdekoodiDAO.readByUri(hakukohdekoodiUri);
            assertEquals(valintaryhmaOid, koodi.getValintaryhmat().iterator().next().getOid());
            assertEquals(1, koodi.getHakukohteet().size());
            assertEquals(koodi.getHakukohteet().iterator().next().getOid(), hakukohdeOid);

            HakukohdeViite hakukohde = hakukohdeService.readByOid(hakukohdeOid);
            assertEquals(hakukohdeOid, hakukohde.getOid());
            assertNull(hakukohde.getValintaryhma());
            assertTrue(hakukohde.getManuaalisestiSiirretty());

            List<HakukohdeViite> hakukohteet = hakukohdeService.findByValintaryhmaOid(valintaryhmaOid);
            assertEquals(0, hakukohteet.size());
        }

        HakukohdeImportTyyppi importData = luoHakukohdeImportTyyppi(hakukohdeOid, hakuOid, hakukohdekoodiUri);
        hakukohdeImportService.tuoHakukohde(importData);

        {
            Hakukohdekoodi koodi = hakukohdekoodiDAO.readByUri(hakukohdekoodiUri);
            assertEquals(valintaryhmaOid, koodi.getValintaryhmat().iterator().next().getOid());
            assertEquals(1, koodi.getHakukohteet().size());

            HakukohdeViite hakukohde = hakukohdeService.readByOid(hakukohdeOid);
            assertEquals(hakukohdeOid, hakukohde.getOid());
            assertNull(hakukohde.getValintaryhma());
            assertTrue(hakukohde.getManuaalisestiSiirretty());

            List<HakukohdeViite> hakukohteet = hakukohdeService.findByValintaryhmaOid(valintaryhmaOid);
            assertEquals(0, hakukohteet.size());
        }
    }
}
