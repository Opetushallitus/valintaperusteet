package fi.vm.sade.service.valintaperusteet.service;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNotSame;
import static junit.framework.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import fi.vm.sade.service.valintaperusteet.listeners.ValinnatJTACleanInsertTestExecutionListener;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import fi.vm.sade.dbunit.annotation.DataSetLocation;
import fi.vm.sade.dbunit.listener.JTACleanInsertTestExecutionListener;
import fi.vm.sade.service.valintaperusteet.dao.HakukohdeViiteDAO;
import fi.vm.sade.service.valintaperusteet.dao.ValinnanVaiheDAO;
import fi.vm.sade.service.valintaperusteet.dao.ValintaryhmaDAO;
import fi.vm.sade.service.valintaperusteet.dto.ValinnanVaiheCreateDTO;
import fi.vm.sade.service.valintaperusteet.model.HakukohdeViite;
import fi.vm.sade.service.valintaperusteet.model.ValinnanVaihe;
import fi.vm.sade.service.valintaperusteet.model.Valintaryhma;

/**
 * Created with IntelliJ IDEA. User: jukais Date: 16.1.2013 Time: 14.16 To
 * change this template use File | Settings | File Templates.
 */
@ContextConfiguration(locations = "classpath:test-context.xml")
@TestExecutionListeners(listeners = { ValinnatJTACleanInsertTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class })
@RunWith(SpringJUnit4ClassRunner.class)
@DataSetLocation("classpath:test-data.xml")
public class ValinnanVaiheServiceTest {

    @Autowired
    private HakukohdeViiteDAO hakukohdeViiteDAO;

    @Autowired
    private ValintaryhmaDAO valintaryhmaDAO;

    @Autowired
    private ValintaryhmaService valintaryhmaService;

    @Autowired
    private ValinnanVaiheService valinnanVaiheService;

    @Autowired
    private ValinnanVaiheDAO valinnanVaiheDAO;

    @Test
    public void testFindByValintaryhma() {
        final String oid = "oid7";
        List<ValinnanVaihe> vaiheet = valinnanVaiheService.findByValintaryhma(oid);

        assertEquals(5, vaiheet.size());
        assertEquals("15", vaiheet.get(0).getOid());
        assertEquals("16", vaiheet.get(1).getOid());
        assertEquals("17", vaiheet.get(2).getOid());
        assertEquals("18", vaiheet.get(3).getOid());
        assertEquals("19", vaiheet.get(4).getOid());
    }

    private List<Valintaryhma> jarjestaValintaryhmatIdnMukaan(List<Valintaryhma> vr) {
        class ValintaryhmaComparator implements Comparator<Valintaryhma> {
            @Override
            public int compare(Valintaryhma o1, Valintaryhma o2) {
                return o1.getId().compareTo(o2.getId());
            }
        }

        Collections.sort(vr, new ValintaryhmaComparator());
        return vr;
    }

    private boolean valinnanVaiheetOvatKopioita(ValinnanVaihe vv1, ValinnanVaihe vv2) {
        return vv1.getNimi().equals(vv2.getNimi()) && vv1.getAktiivinen().equals(vv2.getAktiivinen())
                && vv1.getKuvaus().equals(vv2.getKuvaus());
    }

    @Test
    public void testDeleteValintakoeValinnanVaihe() {
        final String valinnanVaiheOid = "83";

        ValinnanVaihe valinnanVaihe = valinnanVaiheService.readByOid(valinnanVaiheOid);
        assertEquals(fi.vm.sade.service.valintaperusteet.dto.model.ValinnanVaiheTyyppi.VALINTAKOE,
                valinnanVaihe.getValinnanVaiheTyyppi());

        valinnanVaiheService.deleteByOid(valinnanVaiheOid);
    }

    @Test
    public void testLisaaValinnanVaiheValintaryhmalle() {
        final String parentOid = "oid8";

        {
            // Alkutilanne:
            // ----Valintaryhmä (id 8)
            // | - valinnan vaihe 1 (id 20)
            // | - valinnan vaihe 2 (id 21)
            // | - valinnan vaihe 3 (id 22)
            // |
            // |----Valintaryhmä (id 9)
            // | | - valinnan vaihe 1.1 (id 23)
            // | | - valinnan vaihe 2.1 (id 24)
            // | | - valinnan vaihe 4 (id 25)
            // | | - valinnan vaihe 3.1 (id 26)
            // | |
            // | |----Valintaryhmä (id 10)
            // | | - valinnan vaihe 3.1.1 (id 27)
            // | | - valinnan vaihe 4.1 (id 28)
            // | | - valinnan vaihe 1.1.1 (id 29)
            // | | - valinnan vaihe 5 (id 30)
            // | | - valinnan vaihe 2.1.1 (id 31)
            // | |
            // | |----Hakukohde (id 8)
            // | - valinnan vaihe 1.1.2 (id 38)
            // | - valinnan vaihe 2.1.2 (id 39)
            // | - valinnan vaihe 4.1 (id 40)
            // | - valinnan vaihe 3.1.2 (id 41)
            // |
            // |----Valintaryhmä (id 11)
            // | - valinnan vaihe 1.2 (id 32)
            // | - valinnan vaihe 2.2 (id 33)
            // | - valinnan vaihe 3.2 (id 34)
            // |
            // |----Hakukohde (id 7)
            // - valinnan vaihe 2.3 (id 35)
            // - valinnan vaihe 1.3 (id 36)
            // - valinnan vaihe 3.3 (id 37)
            Valintaryhma vr8L = valintaryhmaService.readByOid(parentOid);
            List<Valintaryhma> vr8Lalaryhmat = jarjestaValintaryhmatIdnMukaan(valintaryhmaDAO
                    .findChildrenByParentOid(parentOid));
            assertEquals(2, vr8Lalaryhmat.size());

            List<HakukohdeViite> vr8Lhakukohteet = hakukohdeViiteDAO.findByValintaryhmaOid(parentOid);
            assertEquals(1, vr8Lhakukohteet.size());

            List<ValinnanVaihe> vr8Lvaiheet = valinnanVaiheService.findByValintaryhma(vr8L.getOid());
            assertEquals(3, vr8Lvaiheet.size());
            assertTrue(vr8Lvaiheet.get(0).getId().longValue() == 20L
                    && vr8Lvaiheet.get(0).getMasterValinnanVaihe() == null);
            assertTrue(vr8Lvaiheet.get(1).getId().longValue() == 21L
                    && vr8Lvaiheet.get(1).getMasterValinnanVaihe() == null);
            assertTrue(vr8Lvaiheet.get(2).getId().longValue() == 22L
                    && vr8Lvaiheet.get(2).getMasterValinnanVaihe() == null);

            Valintaryhma vr9L = vr8Lalaryhmat.get(0);
            Valintaryhma vr11L = vr8Lalaryhmat.get(1);

            HakukohdeViite hk7L = vr8Lhakukohteet.get(0);
            List<ValinnanVaihe> hk7Lvaiheet = valinnanVaiheService.findByHakukohde(hk7L.getOid());
            assertEquals(3, hk7Lvaiheet.size());
            assertTrue(hk7Lvaiheet.get(0).getId().longValue() == 35L
                    && hk7Lvaiheet.get(0).getMasterValinnanVaihe().getId().longValue() == 21L);
            assertTrue(hk7Lvaiheet.get(1).getId().longValue() == 36L
                    && hk7Lvaiheet.get(1).getMasterValinnanVaihe().getId().longValue() == 20L);
            assertTrue(hk7Lvaiheet.get(2).getId().longValue() == 37L
                    && hk7Lvaiheet.get(2).getMasterValinnanVaihe().getId().longValue() == 22L);

            List<Valintaryhma> vr9Lalaryhmat = jarjestaValintaryhmatIdnMukaan(valintaryhmaDAO
                    .findChildrenByParentOid(vr9L.getOid()));
            assertEquals(1, vr9Lalaryhmat.size());

            List<Valintaryhma> vr11Lalaryhmat = jarjestaValintaryhmatIdnMukaan(valintaryhmaDAO
                    .findChildrenByParentOid(vr11L.getOid()));
            assertEquals(0, vr11Lalaryhmat.size());

            List<ValinnanVaihe> vr9Lvaiheet = valinnanVaiheService.findByValintaryhma(vr9L.getOid());
            assertEquals(4, vr9Lvaiheet.size());
            assertTrue(vr9Lvaiheet.get(0).getId().longValue() == 23L
                    && vr9Lvaiheet.get(0).getMasterValinnanVaihe().getId().longValue() == 20L);
            assertTrue(vr9Lvaiheet.get(1).getId().longValue() == 24L
                    && vr9Lvaiheet.get(1).getMasterValinnanVaihe().getId().longValue() == 21L);
            assertTrue(vr9Lvaiheet.get(2).getId().longValue() == 25L
                    && vr9Lvaiheet.get(2).getMasterValinnanVaihe() == null);
            assertTrue(vr9Lvaiheet.get(3).getId().longValue() == 26L
                    && vr9Lvaiheet.get(3).getMasterValinnanVaihe().getId().longValue() == 22L);

            List<HakukohdeViite> vr9Lhakukohteet = hakukohdeViiteDAO.findByValintaryhmaOid(vr9L.getOid());
            assertEquals(1, vr9Lhakukohteet.size());

            HakukohdeViite hk8L = vr9Lhakukohteet.get(0);
            List<ValinnanVaihe> hk8Lvaiheet = valinnanVaiheService.findByHakukohde(hk8L.getOid());
            assertEquals(4, hk8Lvaiheet.size());
            assertTrue(hk8Lvaiheet.get(0).getId().longValue() == 38L
                    && hk8Lvaiheet.get(0).getMasterValinnanVaihe().getId().longValue() == 23L);
            assertTrue(hk8Lvaiheet.get(1).getId().longValue() == 39L
                    && hk8Lvaiheet.get(1).getMasterValinnanVaihe().getId().longValue() == 24L);
            assertTrue(hk8Lvaiheet.get(2).getId().longValue() == 40L
                    && hk8Lvaiheet.get(2).getMasterValinnanVaihe().getId().longValue() == 25L);
            assertTrue(hk8Lvaiheet.get(3).getId().longValue() == 41L
                    && hk8Lvaiheet.get(3).getMasterValinnanVaihe().getId().longValue() == 26L);

            List<ValinnanVaihe> vr11Lvaiheet = valinnanVaiheService.findByValintaryhma(vr11L.getOid());
            assertEquals(3, vr11Lvaiheet.size());
            assertTrue(vr11Lvaiheet.get(0).getId().longValue() == 32L
                    && vr11Lvaiheet.get(0).getMasterValinnanVaihe().getId().longValue() == 20L);
            assertTrue(vr11Lvaiheet.get(1).getId().longValue() == 33L
                    && vr11Lvaiheet.get(1).getMasterValinnanVaihe().getId().longValue() == 21L);
            assertTrue(vr11Lvaiheet.get(2).getId().longValue() == 34L
                    && vr11Lvaiheet.get(2).getMasterValinnanVaihe().getId().longValue() == 22L);

            Valintaryhma vr10L = vr9Lalaryhmat.get(0);
            assertEquals(0, valintaryhmaDAO.findChildrenByParentOid(vr10L.getOid()).size());
            List<ValinnanVaihe> vr10Lvaiheet = valinnanVaiheService.findByValintaryhma(vr10L.getOid());
            assertEquals(5, vr10Lvaiheet.size());
            assertTrue(vr10Lvaiheet.get(0).getId().longValue() == 27L
                    && vr10Lvaiheet.get(0).getMasterValinnanVaihe().getId().longValue() == 26L);
            assertTrue(vr10Lvaiheet.get(1).getId().longValue() == 28L
                    && vr10Lvaiheet.get(1).getMasterValinnanVaihe().getId().longValue() == 25L);
            assertTrue(vr10Lvaiheet.get(2).getId().longValue() == 29L
                    && vr10Lvaiheet.get(2).getMasterValinnanVaihe().getId().longValue() == 23L);
            assertTrue(vr10Lvaiheet.get(3).getId().longValue() == 30L
                    && vr10Lvaiheet.get(3).getMasterValinnanVaihe() == null);
            assertTrue(vr10Lvaiheet.get(4).getId().longValue() == 31L
                    && vr10Lvaiheet.get(4).getMasterValinnanVaihe().getId().longValue() == 24L);
        }

        // Lisätään päätason valintaryhmään uusi valinnan vaihe vaiheen 1
        // jälkeen.
        final String edellinenValinnanVaiheOid = "20";
        ValinnanVaiheCreateDTO uusiValinnanVaihe = new ValinnanVaiheCreateDTO();
        uusiValinnanVaihe.setAktiivinen(true);
        uusiValinnanVaihe.setKuvaus("uusi kuvaus");
        uusiValinnanVaihe.setNimi("uusi nimi");
        uusiValinnanVaihe
                .setValinnanVaiheTyyppi(fi.vm.sade.service.valintaperusteet.dto.model.ValinnanVaiheTyyppi.TAVALLINEN);

        final ValinnanVaihe lisatty = valinnanVaiheService.lisaaValinnanVaiheValintaryhmalle(parentOid,
                uusiValinnanVaihe, edellinenValinnanVaiheOid);

        {
            // Lopputilanne:
            // ----Valintaryhmä (id 8)
            // | - valinnan vaihe 1 (id 20)
            // | - uusi vaihe x
            // | - valinnan vaihe 2 (id 21)
            // | - valinnan vaihe 3 (id 22)
            // |
            // |----Valintaryhmä (id 9)
            // | | - valinnan vaihe 1.1 (id 23)
            // | | - uusi vaihe x.1
            // | | - valinnan vaihe 2.1 (id 24)
            // | | - valinnan vaihe 4 (id 25)
            // | | - valinnan vaihe 3.1 (id 26)
            // | |
            // | |----Valintaryhmä (id 10)
            // | | - valinnan vaihe 3.1.1 (id 27)
            // | | - valinnan vaihe 4.1 (id 28)
            // | | - valinnan vaihe 1.1.1 (id 29)
            // | | - uusi vaihe x.1.1
            // | | - valinnan vaihe 5 (id 30)
            // | | - valinnan vaihe 2.1.1 (id 31)
            // | |
            // | |----Hakukohde (id 8)
            // | - valinnan vaihe 1.1.2 (id 38)
            // | - uusi vaihe x.1.2
            // | - valinnan vaihe 2.1.2 (id 39)
            // | - valinnan vaihe 4.1 (id 40)
            // | - valinnan vaihe 3.1.2 (id 41)
            // |
            // |----Valintaryhmä (id 11)
            // | - valinnan vaihe 1.2 (id 32)
            // | - uusi vaihe x.2
            // | - valinnan vaihe 2.2 (id 33)
            // | - valinnan vaihe 3.2 (id 34)
            // |
            // |----Hakukohde (id 7)
            // - valinnan vaihe 2.3 (id 35)
            // - valinnan vaihe 1.3 (id 36)
            // - uusi vaihe x.3
            // - valinnan vaihe 3.3 (id 37)

            Valintaryhma vr8L = valintaryhmaService.readByOid(parentOid);
            List<Valintaryhma> vr8Lalaryhmat = jarjestaValintaryhmatIdnMukaan(valintaryhmaDAO
                    .findChildrenByParentOid(parentOid));
            assertEquals(2, vr8Lalaryhmat.size());

            List<HakukohdeViite> vr8Lhakukohteet = hakukohdeViiteDAO.findByValintaryhmaOid(parentOid);
            assertEquals(1, vr8Lhakukohteet.size());

            List<ValinnanVaihe> vr8Lvaiheet = valinnanVaiheService.findByValintaryhma(vr8L.getOid());
            assertEquals(4, vr8Lvaiheet.size());
            assertTrue(vr8Lvaiheet.get(0).getId().longValue() == 20L
                    && vr8Lvaiheet.get(0).getMasterValinnanVaihe() == null);
            assertTrue(vr8Lvaiheet.get(1).equals(lisatty) && vr8Lvaiheet.get(1).getMasterValinnanVaihe() == null);
            assertTrue(vr8Lvaiheet.get(2).getId().longValue() == 21L
                    && vr8Lvaiheet.get(2).getMasterValinnanVaihe() == null);
            assertTrue(vr8Lvaiheet.get(3).getId().longValue() == 22L
                    && vr8Lvaiheet.get(3).getMasterValinnanVaihe() == null);

            Valintaryhma vr9L = vr8Lalaryhmat.get(0);
            Valintaryhma vr11L = vr8Lalaryhmat.get(1);

            HakukohdeViite hk7L = vr8Lhakukohteet.get(0);
            List<ValinnanVaihe> hk7Lvaiheet = valinnanVaiheService.findByHakukohde(hk7L.getOid());
            assertEquals(4, hk7Lvaiheet.size());
            assertTrue(hk7Lvaiheet.get(0).getId().longValue() == 35L
                    && hk7Lvaiheet.get(0).getMasterValinnanVaihe().getId().longValue() == 21L);
            assertTrue(hk7Lvaiheet.get(1).getId().longValue() == 36L
                    && hk7Lvaiheet.get(1).getMasterValinnanVaihe().getId().longValue() == 20L);
            assertTrue(hk7Lvaiheet.get(2).getMasterValinnanVaihe().equals(lisatty)
                    && valinnanVaiheetOvatKopioita(hk7Lvaiheet.get(2), lisatty));
            assertTrue(hk7Lvaiheet.get(3).getId().longValue() == 37L
                    && hk7Lvaiheet.get(3).getMasterValinnanVaihe().getId().longValue() == 22L);

            List<Valintaryhma> vr9Lalaryhmat = jarjestaValintaryhmatIdnMukaan(valintaryhmaDAO
                    .findChildrenByParentOid(vr9L.getOid()));
            assertEquals(1, vr9Lalaryhmat.size());

            List<ValinnanVaihe> vr9Lvaiheet = valinnanVaiheService.findByValintaryhma(vr9L.getOid());
            assertEquals(5, vr9Lvaiheet.size());
            assertTrue(vr9Lvaiheet.get(0).getId().longValue() == 23L
                    && vr9Lvaiheet.get(0).getMasterValinnanVaihe().getId().longValue() == 20L);
            assertTrue(vr9Lvaiheet.get(1).getMasterValinnanVaihe().equals(lisatty)
                    && valinnanVaiheetOvatKopioita(vr9Lvaiheet.get(1), lisatty));
            assertTrue(vr9Lvaiheet.get(2).getId().longValue() == 24L
                    && vr9Lvaiheet.get(2).getMasterValinnanVaihe().getId().longValue() == 21L);
            assertTrue(vr9Lvaiheet.get(3).getId().longValue() == 25L
                    && vr9Lvaiheet.get(3).getMasterValinnanVaihe() == null);
            assertTrue(vr9Lvaiheet.get(4).getId().longValue() == 26L
                    && vr9Lvaiheet.get(4).getMasterValinnanVaihe().getId().longValue() == 22L);

            final ValinnanVaihe lisatynKopio = vr9Lvaiheet.get(1);

            List<HakukohdeViite> vr9Lhakukohteet = hakukohdeViiteDAO.findByValintaryhmaOid(vr9L.getOid());
            assertEquals(1, vr9Lhakukohteet.size());

            HakukohdeViite hk8L = vr9Lhakukohteet.get(0);
            List<ValinnanVaihe> hk8Lvaiheet = valinnanVaiheService.findByHakukohde(hk8L.getOid());
            assertEquals(5, hk8Lvaiheet.size());
            assertTrue(hk8Lvaiheet.get(0).getId().longValue() == 38L
                    && hk8Lvaiheet.get(0).getMasterValinnanVaihe().getId().longValue() == 23L);
            assertTrue(hk8Lvaiheet.get(1).getMasterValinnanVaihe().equals(lisatynKopio)
                    && valinnanVaiheetOvatKopioita(hk8Lvaiheet.get(1), lisatty));
            assertTrue(hk8Lvaiheet.get(2).getId().longValue() == 39L
                    && hk8Lvaiheet.get(2).getMasterValinnanVaihe().getId().longValue() == 24L);
            assertTrue(hk8Lvaiheet.get(3).getId().longValue() == 40L
                    && hk8Lvaiheet.get(3).getMasterValinnanVaihe().getId().longValue() == 25L);
            assertTrue(hk8Lvaiheet.get(4).getId().longValue() == 41L
                    && hk8Lvaiheet.get(4).getMasterValinnanVaihe().getId().longValue() == 26L);

            List<Valintaryhma> vr11Lalaryhmat = jarjestaValintaryhmatIdnMukaan(valintaryhmaDAO
                    .findChildrenByParentOid(vr11L.getOid()));
            assertEquals(0, vr11Lalaryhmat.size());

            List<ValinnanVaihe> vr11Lvaiheet = valinnanVaiheService.findByValintaryhma(vr11L.getOid());
            assertEquals(4, vr11Lvaiheet.size());
            assertTrue(vr11Lvaiheet.get(0).getId().longValue() == 32L
                    && vr11Lvaiheet.get(0).getMasterValinnanVaihe().getId().longValue() == 20L);
            assertTrue(vr11Lvaiheet.get(1).getMasterValinnanVaihe().equals(lisatty)
                    && valinnanVaiheetOvatKopioita(vr11Lvaiheet.get(1), lisatty));
            assertTrue(vr11Lvaiheet.get(2).getId().longValue() == 33L
                    && vr11Lvaiheet.get(2).getMasterValinnanVaihe().getId().longValue() == 21L);
            assertTrue(vr11Lvaiheet.get(3).getId().longValue() == 34L
                    && vr11Lvaiheet.get(3).getMasterValinnanVaihe().getId().longValue() == 22L);

            Valintaryhma vr10L = vr9Lalaryhmat.get(0);
            assertEquals(0, valintaryhmaDAO.findChildrenByParentOid(vr10L.getOid()).size());
            List<ValinnanVaihe> vr10Lvaiheet = valinnanVaiheService.findByValintaryhma(vr10L.getOid());
            assertEquals(6, vr10Lvaiheet.size());
            assertTrue(vr10Lvaiheet.get(0).getId().longValue() == 27L
                    && vr10Lvaiheet.get(0).getMasterValinnanVaihe().getId().longValue() == 26L);
            assertTrue(vr10Lvaiheet.get(1).getId().longValue() == 28L
                    && vr10Lvaiheet.get(1).getMasterValinnanVaihe().getId().longValue() == 25L);
            assertTrue(vr10Lvaiheet.get(2).getId().longValue() == 29L
                    && vr10Lvaiheet.get(2).getMasterValinnanVaihe().getId().longValue() == 23L);
            assertTrue(vr10Lvaiheet.get(3).getMasterValinnanVaihe().equals(lisatynKopio)
                    && valinnanVaiheetOvatKopioita(vr10Lvaiheet.get(3), lisatty));
            assertTrue(vr10Lvaiheet.get(4).getId().longValue() == 30L
                    && vr10Lvaiheet.get(4).getMasterValinnanVaihe() == null);
            assertTrue(vr10Lvaiheet.get(5).getId().longValue() == 31L
                    && vr10Lvaiheet.get(5).getMasterValinnanVaihe().getId().longValue() == 24L);
        }
    }

    @Test
    public void testLisaaValinnanVaiheIlmanEdellistaValintaryhmalle() {
        final String valintaryhmaOid = "oid12";

        {
            assertNotNull(valintaryhmaService.readByOid(valintaryhmaOid));
            assertEquals(0, valintaryhmaDAO.findChildrenByParentOid(valintaryhmaOid).size());

            List<ValinnanVaihe> vaiheet = valinnanVaiheService.findByValintaryhma(valintaryhmaOid);
            assertEquals(3, vaiheet.size());

            assertEquals(42L, vaiheet.get(0).getId().longValue());
            assertEquals(43L, vaiheet.get(1).getId().longValue());
            assertEquals(44L, vaiheet.get(2).getId().longValue());
        }

        ValinnanVaiheCreateDTO uusiVaihe = new ValinnanVaiheCreateDTO();
        uusiVaihe.setAktiivinen(true);
        uusiVaihe.setKuvaus("uusi kuvaus");
        uusiVaihe.setNimi("uusi nimi");
        uusiVaihe.setValinnanVaiheTyyppi(fi.vm.sade.service.valintaperusteet.dto.model.ValinnanVaiheTyyppi.TAVALLINEN);

        // Uuden valinnan vaiheen pitäisi siirtyä listauksen viimeiseksi, jos
        // edellisen oidia ei ole annettu
        ValinnanVaihe lisatty = valinnanVaiheService
                .lisaaValinnanVaiheValintaryhmalle(valintaryhmaOid, uusiVaihe, null);

        {
            assertNotNull(valintaryhmaService.readByOid(valintaryhmaOid));
            assertEquals(0, valintaryhmaDAO.findChildrenByParentOid(valintaryhmaOid).size());

            List<ValinnanVaihe> vaiheet = valinnanVaiheService.findByValintaryhma(valintaryhmaOid);
            assertEquals(4, vaiheet.size());

            assertEquals(42L, vaiheet.get(0).getId().longValue());
            assertEquals(43L, vaiheet.get(1).getId().longValue());
            assertEquals(44L, vaiheet.get(2).getId().longValue());
            assertEquals(vaiheet.get(3), lisatty);
        }
    }

    @Test
    public void testLisaaValinnanVaiheTyhjaanValintaryhmaan() {
        final String valintaryhmaOid = "oid13";

        {
            assertNotNull(valintaryhmaService.readByOid(valintaryhmaOid));
            assertEquals(0, valintaryhmaDAO.findChildrenByParentOid(valintaryhmaOid).size());
            assertEquals(0, valinnanVaiheService.findByValintaryhma(valintaryhmaOid).size());
        }

        ValinnanVaiheCreateDTO uusiVaihe = new ValinnanVaiheCreateDTO();
        uusiVaihe.setAktiivinen(true);
        uusiVaihe.setKuvaus("uusi kuvaus");
        uusiVaihe.setNimi("uusi nimi");
        uusiVaihe.setValinnanVaiheTyyppi(fi.vm.sade.service.valintaperusteet.dto.model.ValinnanVaiheTyyppi.TAVALLINEN);

        ValinnanVaihe lisatty = valinnanVaiheService
                .lisaaValinnanVaiheValintaryhmalle(valintaryhmaOid, uusiVaihe, null);

        {
            assertNotNull(valintaryhmaService.readByOid(valintaryhmaOid));
            assertEquals(0, valintaryhmaDAO.findChildrenByParentOid(valintaryhmaOid).size());

            List<ValinnanVaihe> vaiheet = valinnanVaiheService.findByValintaryhma(valintaryhmaOid);
            assertEquals(1, vaiheet.size());

            assertEquals(vaiheet.get(0), lisatty);
        }
    }

    @Test
    public void testLisaaValinnanVaiheValintaryhmaanJonkaLapsellaOnYksiValinnavaihe() {
        {
            // VR1 3301
            // VR2 3302 -> VV2 3302
            // HK 3301 -> VV1 3301
            assertNotNull(valintaryhmaService.readByOid("3301"));
            assertNotNull(valintaryhmaService.readByOid("3302"));
            assertNotNull(hakukohdeViiteDAO.readByOid("3301"));

            assertEquals(1, valintaryhmaDAO.findChildrenByParentOid("3301").size());
            assertEquals("3302", valintaryhmaDAO.findChildrenByParentOid("3301").get(0).getOid());
            assertEquals(0, valintaryhmaDAO.findChildrenByParentOid("3302").size());

            assertEquals(0, valinnanVaiheService.findByValintaryhma("3301").size());
            assertEquals(1, valinnanVaiheService.findByValintaryhma("3302").size());
            assertEquals(1, valinnanVaiheService.findByHakukohde("3301").size());

        }

        ValinnanVaiheCreateDTO uusiVaihe = new ValinnanVaiheCreateDTO();
        uusiVaihe.setAktiivinen(true);
        uusiVaihe.setKuvaus("uusi kuvaus");
        uusiVaihe.setNimi("uusi nimi");
        uusiVaihe.setValinnanVaiheTyyppi(fi.vm.sade.service.valintaperusteet.dto.model.ValinnanVaiheTyyppi.TAVALLINEN);

        ValinnanVaihe lisatty = valinnanVaiheService.lisaaValinnanVaiheValintaryhmalle("3301", uusiVaihe, null);

        {
            // VR1 3301 -> VV3
            // VR2 3302 -> VV3(C)
            // VV2 3302
            // HK 3301 -> VV3(c)
            // VV1 3301

            assertNotNull(valintaryhmaService.readByOid("3301"));
            assertNotNull(valintaryhmaService.readByOid("3302"));
            assertNotNull(hakukohdeViiteDAO.readByOid("3301"));

            assertEquals(1, valintaryhmaDAO.findChildrenByParentOid("3301").size());
            assertEquals("3302", valintaryhmaDAO.findChildrenByParentOid("3301").get(0).getOid());
            assertEquals(0, valintaryhmaDAO.findChildrenByParentOid("3302").size());

            assertEquals(1, valinnanVaiheService.findByValintaryhma("3301").size());
            assertEquals(2, valinnanVaiheService.findByValintaryhma("3302").size());
            assertEquals(2, valinnanVaiheService.findByHakukohde("3301").size());
        }
    }

    @Test
    public void testJarjestaValintaryhmanValinnanVaiheet() {
        final String valintaryhmaOid = "oid18";

        {
            // Alkutilanne:
            // ----Valintaryhmä (id 18)
            // | - valinnan vaihe 1 (id 49)
            // | - valinnan vaihe 2 (id 50)
            // | - valinnan vaihe 3 (id 51)
            // |
            // |----Valintaryhmä (id 19)
            // | - valinnan vaihe 1.1 (id 52)
            // | - valinnan vaihe 2.1 (id 53)
            // | - valinnan vaihe 4 (id 54)
            // | - valinnan vaihe 3.1 (id 55)
            // |
            // |----Valintaryhmä (id 20)
            // | - valinnan vaihe 5 (id 56)
            // | - valinnan vaihe 6 (id 57)
            // | - valinnan vaihe 2.1.1 (id 58)
            // | - valinnan vaihe 4.1 (id 59)
            // | - valinnan vaihe 1.1.1 (id 60)
            // | - valinnan vaihe 3.1.1 (id 61)
            // |
            // |----Hakukohde (id 9)
            // - valinnan vaihe 5.1 (id 62)
            // - valinnan vaihe 6.1 (id 63)
            // - valinnan vaihe 2.1.1.1 (id 64)
            // - valinnan vaihe 4.1.1 (id 65)
            // - valinnan vaihe 1.1.1.1 (id 66)
            // - valinnan vaihe 3.1.1.1 (id 67)

            assertNotNull(valintaryhmaService.readByOid(valintaryhmaOid));
            List<ValinnanVaihe> vr18Lvaiheet = valinnanVaiheService.findByValintaryhma(valintaryhmaOid);
            assertEquals(3, vr18Lvaiheet.size());
            assertTrue(vr18Lvaiheet.get(0).getId().longValue() == 49L
                    && vr18Lvaiheet.get(0).getMasterValinnanVaihe() == null);
            assertTrue(vr18Lvaiheet.get(1).getId().longValue() == 50L
                    && vr18Lvaiheet.get(1).getMasterValinnanVaihe() == null);
            assertTrue(vr18Lvaiheet.get(2).getId().longValue() == 51L
                    && vr18Lvaiheet.get(2).getMasterValinnanVaihe() == null);

            List<Valintaryhma> vr18Lalavalintaryhmat = valintaryhmaDAO.findChildrenByParentOid(valintaryhmaOid);
            assertEquals(1, vr18Lalavalintaryhmat.size());

            Valintaryhma vr19L = vr18Lalavalintaryhmat.get(0);
            List<ValinnanVaihe> vr19Lvaiheet = valinnanVaiheService.findByValintaryhma(vr19L.getOid());
            assertEquals(4, vr19Lvaiheet.size());
            assertTrue(vr19Lvaiheet.get(0).getId().longValue() == 52L
                    && vr19Lvaiheet.get(0).getMasterValinnanVaihe().getId().longValue() == 49L);
            assertTrue(vr19Lvaiheet.get(1).getId().longValue() == 53L
                    && vr19Lvaiheet.get(1).getMasterValinnanVaihe().getId().longValue() == 50L);
            assertTrue(vr19Lvaiheet.get(2).getId().longValue() == 54L
                    && vr19Lvaiheet.get(2).getMasterValinnanVaihe() == null);
            assertTrue(vr19Lvaiheet.get(3).getId().longValue() == 55L
                    && vr19Lvaiheet.get(3).getMasterValinnanVaihe().getId().longValue() == 51L);

            List<Valintaryhma> vr19Lalavalintaryhmat = valintaryhmaDAO.findChildrenByParentOid(vr19L.getOid());
            assertEquals(1, vr19Lalavalintaryhmat.size());

            Valintaryhma vr20L = vr19Lalavalintaryhmat.get(0);
            List<ValinnanVaihe> vr20Lvaiheet = valinnanVaiheService.findByValintaryhma(vr20L.getOid());
            assertEquals(6, vr20Lvaiheet.size());
            assertTrue(vr20Lvaiheet.get(0).getId().longValue() == 56L
                    && vr20Lvaiheet.get(0).getMasterValinnanVaihe() == null);
            assertTrue(vr20Lvaiheet.get(1).getId().longValue() == 57L
                    && vr20Lvaiheet.get(1).getMasterValinnanVaihe() == null);
            assertTrue(vr20Lvaiheet.get(2).getId().longValue() == 58L
                    && vr20Lvaiheet.get(2).getMasterValinnanVaihe().getId().longValue() == 53L);
            assertTrue(vr20Lvaiheet.get(3).getId().longValue() == 59L
                    && vr20Lvaiheet.get(3).getMasterValinnanVaihe().getId().longValue() == 54L);
            assertTrue(vr20Lvaiheet.get(4).getId().longValue() == 60L
                    && vr20Lvaiheet.get(4).getMasterValinnanVaihe().getId().longValue() == 52L);
            assertTrue(vr20Lvaiheet.get(5).getId().longValue() == 61L
                    && vr20Lvaiheet.get(5).getMasterValinnanVaihe().getId().longValue() == 55L);

            List<HakukohdeViite> vr20Lhakukohteet = hakukohdeViiteDAO.findByValintaryhmaOid(vr20L.getOid());
            assertEquals(1, vr20Lhakukohteet.size());

            HakukohdeViite hk9L = vr20Lhakukohteet.get(0);
            List<ValinnanVaihe> hk9Lvaiheet = valinnanVaiheService.findByHakukohde(hk9L.getOid());
            assertEquals(6, hk9Lvaiheet.size());
            assertTrue(hk9Lvaiheet.get(0).getId().longValue() == 62L
                    && hk9Lvaiheet.get(0).getMasterValinnanVaihe().getId() == 56L);
            assertTrue(hk9Lvaiheet.get(1).getId().longValue() == 63L
                    && hk9Lvaiheet.get(1).getMasterValinnanVaihe().getId() == 57L);
            assertTrue(hk9Lvaiheet.get(2).getId().longValue() == 64L
                    && hk9Lvaiheet.get(2).getMasterValinnanVaihe().getId() == 58L);
            assertTrue(hk9Lvaiheet.get(3).getId().longValue() == 65L
                    && hk9Lvaiheet.get(3).getMasterValinnanVaihe().getId() == 59L);
            assertTrue(hk9Lvaiheet.get(4).getId().longValue() == 66L
                    && hk9Lvaiheet.get(4).getMasterValinnanVaihe().getId() == 60L);
            assertTrue(hk9Lvaiheet.get(5).getId().longValue() == 67L
                    && hk9Lvaiheet.get(5).getMasterValinnanVaihe().getId() == 61L);
        }

        String[] uusiJarjestys = { "51", "50", "49" };
        List<ValinnanVaihe> jarjestetty = valinnanVaiheService.jarjestaValinnanVaiheet(Arrays.asList(uusiJarjestys));

        {
            // Lopputilanne:
            // ----Valintaryhmä (id 18)
            // | - valinnan vaihe 3 (id 51)
            // | - valinnan vaihe 2 (id 50)
            // | - valinnan vaihe 1 (id 49)
            // |
            // |----Valintaryhmä (id 19)
            // | - valinnan vaihe 3.1 (id 55)
            // | - valinnan vaihe 2.1 (id 53)
            // | - valinnan vaihe 4 (id 54)
            // | - valinnan vaihe 1.1 (id 52)
            // |
            // |----Valintaryhmä (id 20)
            // | - valinnan vaihe 5 (id 56)
            // | - valinnan vaihe 6 (id 57)
            // | - valinnan vaihe 3.1.1 (id 61)
            // | - valinnan vaihe 2.1.1 (id 58)
            // | - valinnan vaihe 4.1 (id 59)
            // | - valinnan vaihe 1.1.1 (id 60)
            // |
            // |----Hakukohde (id 9)
            // - valinnan vaihe 5.1 (id 62)
            // - valinnan vaihe 6.1 (id 63)
            // - valinnan vaihe 3.1.1.1 (id 67)
            // - valinnan vaihe 2.1.1.1 (id 64)
            // - valinnan vaihe 4.1.1 (id 65)
            // - valinnan vaihe 1.1.1.1 (id 66)

            assertNotNull(valintaryhmaService.readByOid(valintaryhmaOid));
            List<ValinnanVaihe> vr18Lvaiheet = valinnanVaiheService.findByValintaryhma(valintaryhmaOid);
            assertEquals(3, vr18Lvaiheet.size());
            assertTrue(vr18Lvaiheet.get(0).getId().longValue() == 51L
                    && vr18Lvaiheet.get(0).getMasterValinnanVaihe() == null);
            assertTrue(vr18Lvaiheet.get(1).getId().longValue() == 50L
                    && vr18Lvaiheet.get(1).getMasterValinnanVaihe() == null);
            assertTrue(vr18Lvaiheet.get(2).getId().longValue() == 49L
                    && vr18Lvaiheet.get(2).getMasterValinnanVaihe() == null);

            List<Valintaryhma> vr18Lalavalintaryhmat = valintaryhmaDAO.findChildrenByParentOid(valintaryhmaOid);
            assertEquals(1, vr18Lalavalintaryhmat.size());

            Valintaryhma vr19L = vr18Lalavalintaryhmat.get(0);
            List<ValinnanVaihe> vr19Lvaiheet = valinnanVaiheService.findByValintaryhma(vr19L.getOid());
            assertEquals(4, vr19Lvaiheet.size());
            assertTrue(vr19Lvaiheet.get(0).getId().longValue() == 55L
                    && vr19Lvaiheet.get(0).getMasterValinnanVaihe().getId().longValue() == 51L);
            assertTrue(vr19Lvaiheet.get(1).getId().longValue() == 53L
                    && vr19Lvaiheet.get(1).getMasterValinnanVaihe().getId().longValue() == 50L);
            assertTrue(vr19Lvaiheet.get(2).getId().longValue() == 54L
                    && vr19Lvaiheet.get(2).getMasterValinnanVaihe() == null);
            assertTrue(vr19Lvaiheet.get(3).getId().longValue() == 52L
                    && vr19Lvaiheet.get(3).getMasterValinnanVaihe().getId().longValue() == 49L);

            List<Valintaryhma> vr19Lalavalintaryhmat = valintaryhmaDAO.findChildrenByParentOid(vr19L.getOid());
            assertEquals(1, vr19Lalavalintaryhmat.size());

            Valintaryhma vr20L = vr19Lalavalintaryhmat.get(0);
            List<ValinnanVaihe> vr20Lvaiheet = valinnanVaiheService.findByValintaryhma(vr20L.getOid());
            assertEquals(6, vr20Lvaiheet.size());
            assertTrue(vr20Lvaiheet.get(0).getId().longValue() == 56L
                    && vr20Lvaiheet.get(0).getMasterValinnanVaihe() == null);
            assertTrue(vr20Lvaiheet.get(1).getId().longValue() == 57L
                    && vr20Lvaiheet.get(1).getMasterValinnanVaihe() == null);
            assertTrue(vr20Lvaiheet.get(2).getId().longValue() == 61L
                    && vr20Lvaiheet.get(2).getMasterValinnanVaihe().getId().longValue() == 55L);
            assertTrue(vr20Lvaiheet.get(3).getId().longValue() == 58L
                    && vr20Lvaiheet.get(3).getMasterValinnanVaihe().getId().longValue() == 53L);
            assertTrue(vr20Lvaiheet.get(4).getId().longValue() == 59L
                    && vr20Lvaiheet.get(4).getMasterValinnanVaihe().getId().longValue() == 54L);
            assertTrue(vr20Lvaiheet.get(5).getId().longValue() == 60L
                    && vr20Lvaiheet.get(5).getMasterValinnanVaihe().getId().longValue() == 52L);

            List<HakukohdeViite> vr20Lhakukohteet = hakukohdeViiteDAO.findByValintaryhmaOid(vr20L.getOid());
            assertEquals(1, vr20Lhakukohteet.size());

            HakukohdeViite hk9L = vr20Lhakukohteet.get(0);
            List<ValinnanVaihe> hk9Lvaiheet = valinnanVaiheService.findByHakukohde(hk9L.getOid());
            assertEquals(6, hk9Lvaiheet.size());
            assertTrue(hk9Lvaiheet.get(0).getId().longValue() == 62L
                    && hk9Lvaiheet.get(0).getMasterValinnanVaihe().getId() == 56L);
            assertTrue(hk9Lvaiheet.get(1).getId().longValue() == 63L
                    && hk9Lvaiheet.get(1).getMasterValinnanVaihe().getId() == 57L);
            assertTrue(hk9Lvaiheet.get(2).getId().longValue() == 67L
                    && hk9Lvaiheet.get(2).getMasterValinnanVaihe().getId() == 61L);
            assertTrue(hk9Lvaiheet.get(3).getId().longValue() == 64L
                    && hk9Lvaiheet.get(3).getMasterValinnanVaihe().getId() == 58L);
            assertTrue(hk9Lvaiheet.get(4).getId().longValue() == 65L
                    && hk9Lvaiheet.get(4).getMasterValinnanVaihe().getId() == 59L);
            assertTrue(hk9Lvaiheet.get(5).getId().longValue() == 66L
                    && hk9Lvaiheet.get(5).getMasterValinnanVaihe().getId() == 60L);
        }
    }

    private static List<ValinnanVaihe> jarjestaVaiheetIdnMukaan(Collection<ValinnanVaihe> vaiheet) {
        List<ValinnanVaihe> jarjestetty = new ArrayList<ValinnanVaihe>(vaiheet);
        Collections.sort(jarjestetty, new Comparator<ValinnanVaihe>() {
            @Override
            public int compare(ValinnanVaihe o1, ValinnanVaihe o2) {
                return o1.getId().compareTo(o2.getId());
            }
        });

        return jarjestetty;
    }

    @Test
    public void testUpdate() {

        final String uusiNimi = "uusi nimi";
        final String uusiKuvaus = "uusi kuvaus";
        final Boolean uusiAktiivinen = false;

        final String valinnanVaiheOid = "70";
        {
            ValinnanVaihe vaihe70L = valinnanVaiheDAO.readByOid(valinnanVaiheOid);
            assertNotNull(vaihe70L);
            assertNotSame(uusiNimi, vaihe70L.getNimi());
            assertNotSame(uusiKuvaus, vaihe70L.getKuvaus());
            assertTrue(!uusiAktiivinen.equals(vaihe70L.getAktiivinen()));

            List<ValinnanVaihe> vaihe70Lkopiot = jarjestaVaiheetIdnMukaan(valinnanVaiheDAO.haeKopiot(vaihe70L.getOid()));
            assertEquals(1, vaihe70Lkopiot.size());

            ValinnanVaihe vaihe71L = vaihe70Lkopiot.get(0);
            assertNotSame(uusiNimi, vaihe71L.getNimi());
            assertNotSame(uusiKuvaus, vaihe71L.getKuvaus());
            assertTrue(!uusiAktiivinen.equals(vaihe71L.getAktiivinen()));

            List<ValinnanVaihe> vaihe71Lkopiot = jarjestaVaiheetIdnMukaan(valinnanVaiheDAO.haeKopiot(vaihe71L.getOid()));
            assertEquals(2, vaihe71Lkopiot.size());

            ValinnanVaihe vaihe72L = vaihe71Lkopiot.get(0);
            assertNotSame(uusiNimi, vaihe72L.getNimi());
            assertNotSame(uusiKuvaus, vaihe72L.getKuvaus());
            assertTrue(uusiAktiivinen.equals(vaihe72L.getAktiivinen()));

            ValinnanVaihe vaihe74L = vaihe71Lkopiot.get(1);
            assertNotSame(uusiNimi, vaihe74L.getNimi());
            assertNotSame(uusiKuvaus, vaihe74L.getKuvaus());
            assertTrue(!uusiAktiivinen.equals(vaihe74L.getAktiivinen()));

            List<ValinnanVaihe> vaihe72Lkopiot = jarjestaVaiheetIdnMukaan(valinnanVaiheDAO.haeKopiot(vaihe72L.getOid()));
            assertEquals(1, vaihe72Lkopiot.size());

            ValinnanVaihe vaihe73L = vaihe72Lkopiot.get(0);
            assertNotSame(uusiNimi, vaihe73L.getNimi());
            assertNotSame(uusiKuvaus, vaihe73L.getKuvaus());
            assertTrue(uusiAktiivinen.equals(vaihe73L.getAktiivinen()));

            assertEquals(0, valinnanVaiheDAO.haeKopiot(vaihe74L.getOid()).size());
        }

        ValinnanVaiheCreateDTO valinnanVaihe = new ValinnanVaiheCreateDTO();
        valinnanVaihe.setAktiivinen(uusiAktiivinen);
        valinnanVaihe.setNimi(uusiNimi);
        valinnanVaihe.setKuvaus(uusiKuvaus);
        valinnanVaihe
                .setValinnanVaiheTyyppi(fi.vm.sade.service.valintaperusteet.dto.model.ValinnanVaiheTyyppi.TAVALLINEN);

        ValinnanVaihe paivitetty = valinnanVaiheService.update(valinnanVaiheOid, valinnanVaihe);
        {
            ValinnanVaihe vaihe70L = valinnanVaiheDAO.readByOid(valinnanVaiheOid);
            assertNotNull(vaihe70L);
            assertEquals(uusiNimi, vaihe70L.getNimi());
            assertEquals(uusiKuvaus, vaihe70L.getKuvaus());
            assertTrue(uusiAktiivinen.equals(vaihe70L.getAktiivinen()));

            List<ValinnanVaihe> vaihe70Lkopiot = jarjestaVaiheetIdnMukaan(valinnanVaiheDAO.haeKopiot(vaihe70L.getOid()));
            assertEquals(1, vaihe70Lkopiot.size());

            ValinnanVaihe vaihe71L = vaihe70Lkopiot.get(0);
            assertEquals(uusiNimi, vaihe71L.getNimi());
            assertEquals(uusiKuvaus, vaihe71L.getKuvaus());
            assertTrue(uusiAktiivinen.equals(vaihe71L.getAktiivinen()));

            List<ValinnanVaihe> vaihe71Lkopiot = jarjestaVaiheetIdnMukaan(valinnanVaiheDAO.haeKopiot(vaihe71L.getOid()));
            assertEquals(2, vaihe71Lkopiot.size());

            ValinnanVaihe vaihe72L = vaihe71Lkopiot.get(0);
            assertEquals(uusiNimi, vaihe72L.getNimi());
            assertEquals(uusiKuvaus, vaihe72L.getKuvaus());
            assertTrue(uusiAktiivinen.equals(vaihe72L.getAktiivinen()));

            ValinnanVaihe vaihe74L = vaihe71Lkopiot.get(1);
            assertEquals(uusiNimi, vaihe74L.getNimi());
            assertEquals(uusiKuvaus, vaihe74L.getKuvaus());
            assertTrue(uusiAktiivinen.equals(vaihe74L.getAktiivinen()));

            List<ValinnanVaihe> vaihe72Lkopiot = jarjestaVaiheetIdnMukaan(valinnanVaiheDAO.haeKopiot(vaihe72L.getOid()));
            assertEquals(1, vaihe72Lkopiot.size());

            ValinnanVaihe vaihe73L = vaihe72Lkopiot.get(0);
            assertEquals(uusiNimi, vaihe73L.getNimi());
            assertEquals(uusiKuvaus, vaihe73L.getKuvaus());
            assertTrue(uusiAktiivinen.equals(vaihe73L.getAktiivinen()));

            assertEquals(0, valinnanVaiheDAO.haeKopiot(vaihe74L.getOid()).size());
        }
    }
}
