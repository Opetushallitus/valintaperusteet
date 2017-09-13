package fi.vm.sade.service.valintaperusteet.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import fi.vm.sade.service.valintaperusteet.annotation.DataSetLocation;
import fi.vm.sade.service.valintaperusteet.dao.ValinnanVaiheDAO;
import fi.vm.sade.service.valintaperusteet.dao.ValintatapajonoDAO;
import fi.vm.sade.service.valintaperusteet.dto.ValintatapajonoCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.dto.model.Tasapistesaanto;
import fi.vm.sade.service.valintaperusteet.dto.model.ValinnanVaiheTyyppi;
import fi.vm.sade.service.valintaperusteet.listeners.ValinnatJTACleanInsertTestExecutionListener;
import fi.vm.sade.service.valintaperusteet.model.ValinnanVaihe;
import fi.vm.sade.service.valintaperusteet.model.Valintatapajono;
import fi.vm.sade.service.valintaperusteet.service.exception.ValintatapajonoaEiVoiLisataException;
import fi.vm.sade.service.valintaperusteet.util.VtsRestClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import java.io.IOException;
import java.util.*;

/**
 * Created with IntelliJ IDEA. User: jukais Date: 17.1.2013 Time: 15.14 To
 * change this template use File | Settings | File Templates.
 */
@ContextConfiguration(classes = VtsRestClientConfig.class)
@TestExecutionListeners(listeners = { ValinnatJTACleanInsertTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        WithSecurityContextTestExecutionListener.class })
@RunWith(SpringJUnit4ClassRunner.class)
@DataSetLocation("classpath:test-data.xml")
public class ValintatapajonoServiceTest {

    @Autowired
    private ValintatapajonoService valintatapajonoService;

    @Autowired
    private ValinnanVaiheDAO valinnanVaiheDAO;

    @Autowired
    private ValintatapajonoDAO valintatapajonoDAO;

    @Mock
    SecurityContextHolder securityContextHolder;

    @Test
    public void testFindJonoByValinnanvaihe() throws Exception {
        List<Valintatapajono> valintatapajonoByValinnanvaihe = valintatapajonoService.findJonoByValinnanvaihe("1");

        assertEquals(5, valintatapajonoByValinnanvaihe.size());
    }

    private boolean valintatapajonotOvatKopioita(Valintatapajono jono1, Valintatapajono jono2) {
        return jono1.getAktiivinen().equals(jono2.getAktiivinen())
                && jono1.getSiirretaanSijoitteluun().equals(jono2.getSiirretaanSijoitteluun())
                && jono1.getAloituspaikat().equals(jono2.getAloituspaikat())
                && jono1.getKuvaus().equals(jono2.getKuvaus()) && jono1.getNimi().equals(jono2.getNimi())
                && jono1.getTasapistesaanto().equals(jono2.getTasapistesaanto());
    }

    @Test
    public void lisaaValintatapajonoValinnanVaiheelle() {
        final String masterValinnanVaiheOid = "45";
        final String kopioValinnanVaiheOid = "46";
        {
            // Alkutilanne:
            // --Valintaryhma (id 14)
            // | - valinnan vaihe 1 (id 45)
            // | - jono 1 (id 1050)
            // | - jono 2 (id 1051)
            // | - jono 3 (id 1052)
            // |
            // |--Valintaryhma (id 15)
            // - valinnan vaihe 1.1 (id 46)
            // - jono 1.1 (id 1053)
            // - jono 2.1 (id 1054)
            // - jono 4 (id 1055)
            // - jono 3.1 (id 1056)

            assertNotNull(valinnanVaiheDAO.readByOid(masterValinnanVaiheOid));
            List<Valintatapajono> vv45Ljonot = valintatapajonoService.findJonoByValinnanvaihe(masterValinnanVaiheOid);
            assertEquals(3, vv45Ljonot.size());
            assertTrue(vv45Ljonot.get(0).getId().longValue() == 1050L
                    && vv45Ljonot.get(0).getMasterValintatapajono() == null);
            assertTrue(vv45Ljonot.get(1).getId().longValue() == 1051L
                    && vv45Ljonot.get(1).getMasterValintatapajono() == null);
            assertTrue(vv45Ljonot.get(2).getId().longValue() == 1052L
                    && vv45Ljonot.get(1).getMasterValintatapajono() == null);

            ValinnanVaihe vv46L = valinnanVaiheDAO.readByOid(kopioValinnanVaiheOid);
            assertNotNull(vv46L);
            assertEquals(45L, vv46L.getMasterValinnanVaihe().getId().longValue());
            List<Valintatapajono> vv46Ljonot = valintatapajonoService.findJonoByValinnanvaihe(kopioValinnanVaiheOid);
            assertEquals(4, vv46Ljonot.size());

            assertTrue(vv46Ljonot.get(0).getId().longValue() == 1053L
                    && vv46Ljonot.get(0).getMasterValintatapajono().getId().longValue() == 1050L);
            assertTrue(vv46Ljonot.get(1).getId().longValue() == 1054L
                    && vv46Ljonot.get(1).getMasterValintatapajono().getId().longValue() == 1051L);
            assertTrue(vv46Ljonot.get(2).getId().longValue() == 1055L
                    && vv46Ljonot.get(2).getMasterValintatapajono() == null);
            assertTrue(vv46Ljonot.get(3).getId().longValue() == 1056L
                    && vv46Ljonot.get(3).getMasterValintatapajono().getId().longValue() == 1052L);
        }

        final String edellinenValintatapajonoOid = "1051";

        ValintatapajonoCreateDTO uusiJono = new ValintatapajonoCreateDTO();
        uusiJono.setAktiivinen(true);
        uusiJono.setAutomaattinenLaskentaanSiirto(true);
        uusiJono.setValisijoittelu(false);
        uusiJono.setAloituspaikat(15);
        uusiJono.setKuvaus("uusi kuvaus");
        uusiJono.setNimi("uusi nimi");
        uusiJono.setTasapistesaanto(fi.vm.sade.service.valintaperusteet.dto.model.Tasapistesaanto.ALITAYTTO);
        uusiJono.setSiirretaanSijoitteluun(true);

        Valintatapajono lisatty = valintatapajonoService.lisaaValintatapajonoValinnanVaiheelle(masterValinnanVaiheOid,
                uusiJono, edellinenValintatapajonoOid);

        {
            // Lopputilanne:
            // --Valintaryhma (id 14)
            // | - valinnan vaihe 1 (id 45)
            // | - jono 1 (id 1050)
            // | - jono 2 (id 1051)
            // | - uusi jono x
            // | - jono 3 (id 1052)
            // |
            // |--Valintaryhma (id 15)
            // - valinnan vaihe 1.1 (id 46)
            // - jono 1.1 (id 1053)
            // - jono 2.1 (id 1054)
            // - uusi jono x.1
            // - jono 4 (id 1055)
            // - jono 3.1 (id 1056)
            assertNotNull(valinnanVaiheDAO.readByOid(masterValinnanVaiheOid));
            List<Valintatapajono> vv45Ljonot = valintatapajonoService.findJonoByValinnanvaihe(masterValinnanVaiheOid);
            assertEquals(4, vv45Ljonot.size());
            assertTrue(vv45Ljonot.get(0).getId().longValue() == 1050L
                    && vv45Ljonot.get(0).getMasterValintatapajono() == null);
            assertTrue(vv45Ljonot.get(1).getId().longValue() == 1051L
                    && vv45Ljonot.get(1).getMasterValintatapajono() == null);
            assertTrue(vv45Ljonot.get(2).equals(lisatty) && vv45Ljonot.get(2).getMasterValintatapajono() == null);
            assertTrue(vv45Ljonot.get(3).getId().longValue() == 1052L
                    && vv45Ljonot.get(3).getMasterValintatapajono() == null);

            ValinnanVaihe vv46L = valinnanVaiheDAO.readByOid(kopioValinnanVaiheOid);
            assertNotNull(vv46L);
            assertEquals(45L, vv46L.getMasterValinnanVaihe().getId().longValue());
            List<Valintatapajono> vv46Ljonot = valintatapajonoService.findJonoByValinnanvaihe(kopioValinnanVaiheOid);
            assertEquals(5, vv46Ljonot.size());

            assertTrue(vv46Ljonot.get(0).getId().longValue() == 1053L
                    && vv46Ljonot.get(0).getMasterValintatapajono().getId().longValue() == 1050L);
            assertTrue(vv46Ljonot.get(1).getId().longValue() == 1054L
                    && vv46Ljonot.get(1).getMasterValintatapajono().getId().longValue() == 1051L);
            assertTrue(vv46Ljonot.get(2).getMasterValintatapajono().equals(lisatty)
                    && valintatapajonotOvatKopioita(vv46Ljonot.get(2), lisatty));
            assertTrue(vv46Ljonot.get(3).getId().longValue() == 1055L
                    && vv46Ljonot.get(3).getMasterValintatapajono() == null);
            assertTrue(vv46Ljonot.get(4).getId().longValue() == 1056L
                    && vv46Ljonot.get(4).getMasterValintatapajono().getId().longValue() == 1052L);
        }
    }

    @Test
    public void lisaaValintatapajonoTyhjalleValinnanVaiheelle() {
        final String valinnanVaiheOid = "47";
        {
            assertNotNull(valinnanVaiheDAO.readByOid(valinnanVaiheOid));
            assertEquals(0, valintatapajonoService.findJonoByValinnanvaihe(valinnanVaiheOid).size());
        }

        ValintatapajonoCreateDTO uusiJono = new ValintatapajonoCreateDTO();
        uusiJono.setAktiivinen(true);
        uusiJono.setAutomaattinenLaskentaanSiirto(true);
        uusiJono.setValisijoittelu(false);
        uusiJono.setAloituspaikat(15);
        uusiJono.setKuvaus("uusi kuvaus");
        uusiJono.setNimi("uusi nimi");
        uusiJono.setTasapistesaanto(fi.vm.sade.service.valintaperusteet.dto.model.Tasapistesaanto.ALITAYTTO);
        uusiJono.setSiirretaanSijoitteluun(true);
        Valintatapajono lisatty = valintatapajonoService.lisaaValintatapajonoValinnanVaiheelle(valinnanVaiheOid,
                uusiJono, null);

        {
            assertNotNull(valinnanVaiheDAO.readByOid(valinnanVaiheOid));
            List<Valintatapajono> jonot = valintatapajonoService.findJonoByValinnanvaihe(valinnanVaiheOid);
            assertEquals(1, jonot.size());
            assertTrue(jonot.get(0).getMasterValintatapajono() == null && jonot.get(0).equals(lisatty));
        }
    }

    @Test
    public void lisaaValintatapajonoValinnanVaiheelleIlmanEdellista() {
        final String valinnanVaiheOid = "48";
        {
            assertNotNull(valinnanVaiheDAO.readByOid(valinnanVaiheOid));
            List<Valintatapajono> jonot = valintatapajonoService.findJonoByValinnanvaihe(valinnanVaiheOid);
            assertEquals(3, jonot.size());
            assertTrue(jonot.get(0).getId().longValue() == 1057L);
            assertTrue(jonot.get(1).getId().longValue() == 1058L);
            assertTrue(jonot.get(2).getId().longValue() == 1059L);
        }

        ValintatapajonoCreateDTO uusiJono = new ValintatapajonoCreateDTO();
        uusiJono.setAktiivinen(true);
        uusiJono.setAutomaattinenLaskentaanSiirto(true);
        uusiJono.setValisijoittelu(false);
        uusiJono.setAloituspaikat(15);
        uusiJono.setKuvaus("uusi kuvaus");
        uusiJono.setNimi("uusi nimi");
        uusiJono.setTasapistesaanto(fi.vm.sade.service.valintaperusteet.dto.model.Tasapistesaanto.ALITAYTTO);
        uusiJono.setSiirretaanSijoitteluun(true);
        Valintatapajono lisatty = valintatapajonoService.lisaaValintatapajonoValinnanVaiheelle(valinnanVaiheOid,
                uusiJono, null);

        {
            assertNotNull(valinnanVaiheDAO.readByOid(valinnanVaiheOid));
            List<Valintatapajono> jonot = valintatapajonoService.findJonoByValinnanvaihe(valinnanVaiheOid);
            assertEquals(4, jonot.size());
            assertTrue(jonot.get(0).getId().longValue() == 1057L);
            assertTrue(jonot.get(1).getId().longValue() == 1058L);
            assertTrue(jonot.get(2).getId().longValue() == 1059L);
            assertTrue(jonot.get(3).equals(lisatty));
        }
    }

    @Test
    public void testJarjestaValintatapajonot() {
        final String valinnanVaiheOid = "68";
        final String kopioValinnanVaiheOid = "69";
        {
            // Alkutilanne:
            // --Valintaryhma (id 21)
            // | - valinnan vaihe 1 (id 68)
            // | - jono 1 (id 18)
            // | - jono 2 (id 19)
            // | - jono 3 (id 20)
            // |
            // |--Valintaryhma (id 22)
            // - valinnan vaihe 1.1 (id 69)
            // - jono 4 (id 21)
            // - jono 1.1 (id 22)
            // - jono 3.1 (id 23)
            // - jono 5 (id 24)
            // - jono 2.1 (id 25)

            assertNotNull(valinnanVaiheDAO.readByOid(valinnanVaiheOid));
            List<Valintatapajono> vv68Ljonot = valintatapajonoService.findJonoByValinnanvaihe(valinnanVaiheOid);
            assertEquals(3, vv68Ljonot.size());
            assertTrue(vv68Ljonot.get(0).getId().longValue() == 18L
                    && vv68Ljonot.get(0).getMasterValintatapajono() == null);
            assertTrue(vv68Ljonot.get(1).getId().longValue() == 19L
                    && vv68Ljonot.get(1).getMasterValintatapajono() == null);
            assertTrue(vv68Ljonot.get(2).getId().longValue() == 20L
                    && vv68Ljonot.get(2).getMasterValintatapajono() == null);

            assertNotNull(valinnanVaiheDAO.readByOid(kopioValinnanVaiheOid));
            List<Valintatapajono> vv69Ljonot = valintatapajonoService.findJonoByValinnanvaihe(kopioValinnanVaiheOid);
            assertEquals(5, vv69Ljonot.size());
            assertTrue(vv69Ljonot.get(0).getId().longValue() == 21L
                    && vv69Ljonot.get(0).getMasterValintatapajono() == null);
            assertTrue(vv69Ljonot.get(1).getId().longValue() == 22L
                    && vv69Ljonot.get(1).getMasterValintatapajono().getId().longValue() == 18L);
            assertTrue(vv69Ljonot.get(2).getId().longValue() == 23L
                    && vv69Ljonot.get(2).getMasterValintatapajono().getId().longValue() == 20L);
            assertTrue(vv69Ljonot.get(3).getId().longValue() == 24L
                    && vv69Ljonot.get(3).getMasterValintatapajono() == null);
            assertTrue(vv69Ljonot.get(4).getId().longValue() == 25L
                    && vv69Ljonot.get(4).getMasterValintatapajono().getId().longValue() == 19L);
        }

        String[] uusiJarjestys = new String[] { "20", "18", "19" };
        valintatapajonoService.jarjestaValintatapajonot(Arrays.asList(uusiJarjestys));

        {
            // Lopputilanne:
            // --Valintaryhma (id 21)
            // | - valinnan vaihe 1 (id 68)
            // | - jono 1 (id 18)
            // | - jono 2 (id 19)
            // | - jono 3 (id 20)
            // |
            // |--Valintaryhma (id 22)
            // - valinnan vaihe 1.1 (id 69)
            // - jono 4 (id 21)
            // - jono 3.1 (id 23)
            // - jono 5 (id 24)
            // - jono 1.1 (id 22)
            // - jono 2.1 (id 25)

            assertNotNull(valinnanVaiheDAO.readByOid(valinnanVaiheOid));
            List<Valintatapajono> vv68Ljonot = valintatapajonoService.findJonoByValinnanvaihe(valinnanVaiheOid);
            assertEquals(3, vv68Ljonot.size());
            assertTrue(vv68Ljonot.get(0).getId().longValue() == 20L
                    && vv68Ljonot.get(0).getMasterValintatapajono() == null);
            assertTrue(vv68Ljonot.get(1).getId().longValue() == 18L
                    && vv68Ljonot.get(1).getMasterValintatapajono() == null);
            assertTrue(vv68Ljonot.get(2).getId().longValue() == 19L
                    && vv68Ljonot.get(2).getMasterValintatapajono() == null);

            assertNotNull(valinnanVaiheDAO.readByOid(kopioValinnanVaiheOid));
            List<Valintatapajono> vv69Ljonot = valintatapajonoService.findJonoByValinnanvaihe(kopioValinnanVaiheOid);
            assertEquals(5, vv69Ljonot.size());
            assertTrue(vv69Ljonot.get(0).getId().longValue() == 21L
                    && vv69Ljonot.get(0).getMasterValintatapajono() == null);
            assertTrue(vv69Ljonot.get(1).getId().longValue() == 23L
                    && vv69Ljonot.get(1).getMasterValintatapajono().getId().longValue() == 20L);
            assertTrue(vv69Ljonot.get(2).getId().longValue() == 24L
                    && vv69Ljonot.get(2).getMasterValintatapajono() == null);
            assertTrue(vv69Ljonot.get(3).getId().longValue() == 22L
                    && vv69Ljonot.get(3).getMasterValintatapajono().getId().longValue() == 18L);
            assertTrue(vv69Ljonot.get(4).getId().longValue() == 25L
                    && vv69Ljonot.get(4).getMasterValintatapajono().getId().longValue() == 19L);
        }
    }

    private List<Valintatapajono> jarjestaJonotIdnMukaan(Collection<Valintatapajono> jonot) {
        List<Valintatapajono> jarjestetty = new ArrayList<Valintatapajono>(jonot);
        Collections.sort(jarjestetty, new Comparator<Valintatapajono>() {
            @Override
            public int compare(Valintatapajono o1, Valintatapajono o2) {
                return o1.getId().compareTo(o2.getId());
            }
        });

        return jarjestetty;
    }

    @Test
    public void testUpdate() {
        final String uusiNimi = "uusi nimi";
        final String uusiKuvaus = "uusi kuvaus";
        final fi.vm.sade.service.valintaperusteet.dto.model.Tasapistesaanto uusiTasapistesaanto = Tasapistesaanto.YLITAYTTO;
        final Integer uusiAloituspaikat = 1;

        final String valintatapajonoOid = "26";
        {
            Valintatapajono jono26L = valintatapajonoService.readByOid(valintatapajonoOid);
            assertNotNull(jono26L);
            assertNotSame(uusiNimi, jono26L.getNimi());
            assertNotSame(uusiKuvaus, jono26L.getKuvaus());
            assertTrue(!uusiTasapistesaanto.equals(jono26L.getTasapistesaanto()));
            assertTrue(!uusiAloituspaikat.equals(jono26L.getAloituspaikat()));

            List<Valintatapajono> jono26Lkopiot = jarjestaJonotIdnMukaan(valintatapajonoDAO.haeKopiot(jono26L.getOid()));
            assertEquals(1, jono26Lkopiot.size());

            Valintatapajono jono27L = jono26Lkopiot.get(0);
            assertNotSame(uusiNimi, jono27L.getNimi());
            assertNotSame(uusiKuvaus, jono27L.getKuvaus());
            assertTrue(!uusiTasapistesaanto.equals(jono27L.getTasapistesaanto()));
            assertTrue(!uusiAloituspaikat.equals(jono27L.getAloituspaikat()));

            List<Valintatapajono> jono27Lkopiot = jarjestaJonotIdnMukaan(valintatapajonoDAO.haeKopiot(jono27L.getOid()));
            assertEquals(2, jono27Lkopiot.size());

            Valintatapajono jono28L = jono27Lkopiot.get(0);

            assertNotSame(uusiNimi, jono28L.getNimi());
            assertNotSame(uusiKuvaus, jono28L.getKuvaus());
            assertTrue(!uusiTasapistesaanto.equals(jono28L.getTasapistesaanto()));
            assertTrue(!uusiAloituspaikat.equals(jono28L.getAloituspaikat()));

            List<Valintatapajono> jono28Lkopiot = jarjestaJonotIdnMukaan(valintatapajonoDAO.haeKopiot(jono28L.getOid()));
            assertEquals(1, jono28Lkopiot.size());

            Valintatapajono jono29L = jono28Lkopiot.get(0);
            assertNotSame(uusiNimi, jono29L.getNimi());
            assertNotSame(uusiKuvaus, jono29L.getKuvaus());
            assertTrue(!uusiTasapistesaanto.equals(jono29L.getTasapistesaanto()));
            assertTrue(!uusiAloituspaikat.equals(jono29L.getAloituspaikat()));
            assertEquals(0, valintatapajonoDAO.haeKopiot(jono29L.getOid()).size());

            Valintatapajono jono30L = jono27Lkopiot.get(1);
            assertNotSame(uusiNimi, jono30L.getNimi());
            assertNotSame(uusiKuvaus, jono30L.getKuvaus());
            assertTrue(!uusiTasapistesaanto.equals(jono30L.getTasapistesaanto()));
            assertTrue(!uusiAloituspaikat.equals(jono30L.getAloituspaikat()));
            assertEquals(0, valintatapajonoDAO.haeKopiot(jono30L.getOid()).size());
        }

        ValintatapajonoCreateDTO paivitys = new ValintatapajonoCreateDTO();
        paivitys.setAktiivinen(true);
        paivitys.setAutomaattinenLaskentaanSiirto(true);
        paivitys.setValisijoittelu(false);
        paivitys.setNimi(uusiNimi);
        paivitys.setKuvaus(uusiKuvaus);
        paivitys.setAloituspaikat(uusiAloituspaikat);
        paivitys.setTasapistesaanto(new ModelMapper().map(uusiTasapistesaanto,
                fi.vm.sade.service.valintaperusteet.dto.model.Tasapistesaanto.class));
        paivitys.setKaytetaanValintalaskentaa(true);

        Valintatapajono paivitetty = valintatapajonoService.update(valintatapajonoOid, paivitys);
        {
            Valintatapajono jono26L = valintatapajonoService.readByOid(valintatapajonoOid);
            assertNotNull(jono26L);
            assertEquals(uusiNimi, jono26L.getNimi());
            assertEquals(uusiKuvaus, jono26L.getKuvaus());
            assertTrue(uusiTasapistesaanto.equals(jono26L.getTasapistesaanto()));
            assertTrue(uusiAloituspaikat.equals(jono26L.getAloituspaikat()));

            List<Valintatapajono> jono26Lkopiot = jarjestaJonotIdnMukaan(valintatapajonoDAO.haeKopiot(jono26L.getOid()));
            assertEquals(1, jono26Lkopiot.size());

            Valintatapajono jono27L = jono26Lkopiot.get(0);
            assertEquals(uusiNimi, jono27L.getNimi());
            assertEquals(uusiKuvaus, jono27L.getKuvaus());
            assertTrue(uusiTasapistesaanto.equals(jono27L.getTasapistesaanto()));
            assertTrue(uusiAloituspaikat.equals(jono27L.getAloituspaikat()));

            List<Valintatapajono> jono27Lkopiot = jarjestaJonotIdnMukaan(valintatapajonoDAO.haeKopiot(jono27L.getOid()));
            assertEquals(2, jono27Lkopiot.size());

            Valintatapajono jono28L = jono27Lkopiot.get(0);

            assertEquals(uusiNimi, jono28L.getNimi());
            assertEquals(uusiKuvaus, jono28L.getKuvaus());
            assertTrue(!uusiTasapistesaanto.equals(jono28L.getTasapistesaanto()));
            assertTrue(!uusiAloituspaikat.equals(jono28L.getAloituspaikat()));

            List<Valintatapajono> jono28Lkopiot = jarjestaJonotIdnMukaan(valintatapajonoDAO.haeKopiot(jono28L.getOid()));
            assertEquals(1, jono28Lkopiot.size());

            Valintatapajono jono29L = jono28Lkopiot.get(0);
            assertEquals(uusiNimi, jono29L.getNimi());
            assertEquals(uusiKuvaus, jono29L.getKuvaus());
            assertTrue(!uusiTasapistesaanto.equals(jono29L.getTasapistesaanto()));
            assertTrue(!uusiAloituspaikat.equals(jono29L.getAloituspaikat()));
            assertEquals(0, valintatapajonoDAO.haeKopiot(jono29L.getOid()).size());

            Valintatapajono jono30L = jono27Lkopiot.get(1);
            assertEquals(uusiNimi, jono30L.getNimi());
            assertEquals(uusiKuvaus, jono30L.getKuvaus());
            assertTrue(!uusiTasapistesaanto.equals(jono30L.getTasapistesaanto()));
            assertTrue(uusiAloituspaikat.equals(jono30L.getAloituspaikat()));
            assertEquals(0, valintatapajonoDAO.haeKopiot(jono30L.getOid()).size());
        }
    }

    @Test
    public void testUpdateSijoiteltuJono() throws Exception {
        final String valintatapajonoOid = "26";
        Valintatapajono jono26L = valintatapajonoService.readByOid(valintatapajonoOid);
        //when(mockVtsRestClient.isJonoSijoiteltu(anyString())).thenReturn(true);

        assertEquals(true, jono26L.getSiirretaanSijoitteluun());

        ValintaperusteetModelMapper mapper = new ValintaperusteetModelMapper();
        ValintatapajonoCreateDTO dto = mapper.map(jono26L, ValintatapajonoCreateDTO.class);

        dto.setSiirretaanSijoitteluun(false);

        Valintatapajono update = valintatapajonoService.update(valintatapajonoOid, dto);
        assertEquals("Siirretaan sijoitteluun should remain true for jonos that have been ran through sijoittelu process",
                true, update.getSiirretaanSijoitteluun());
    }

    @Test
    @WithMockUser(username="admin",authorities = "${root.organisaatio.oid}")
    public void testUpdateSijoiteltuJonoWithOPHUser() throws Exception {
        final String valintatapajonoOid = "26";
        Valintatapajono jono26L = valintatapajonoService.readByOid(valintatapajonoOid);
        //when(mockVtsRestClient.isJonoSijoiteltu(anyString())).thenReturn(true);

        assertEquals(true, jono26L.getSiirretaanSijoitteluun());

        ValintaperusteetModelMapper mapper = new ValintaperusteetModelMapper();
        ValintatapajonoCreateDTO dto = mapper.map(jono26L, ValintatapajonoCreateDTO.class);

        dto.setSiirretaanSijoitteluun(false);

        Valintatapajono update = valintatapajonoService.update(valintatapajonoOid, dto);
        assertEquals("Siirretaan sijoitteluun should remain false for jonos that have been ran through sijoittelu process IF update is done with oph user account",
                false, update.getSiirretaanSijoitteluun());
    }

    @Test(expected = ValintatapajonoaEiVoiLisataException.class)
    public void testLisaaValintatapajonoValintakoeValinnanVaiheelle() {
        final String valinnanVaiheOid = "82";
        ValinnanVaihe valinnanVaihe = valinnanVaiheDAO.readByOid(valinnanVaiheOid);
        assertEquals(ValinnanVaiheTyyppi.VALINTAKOE, valinnanVaihe.getValinnanVaiheTyyppi());

        ValintatapajonoCreateDTO jono = new ValintatapajonoCreateDTO();
        jono.setAktiivinen(true);
        jono.setAutomaattinenLaskentaanSiirto(true);
        jono.setValisijoittelu(false);
        jono.setAloituspaikat(10);
        jono.setKuvaus("kuvaus");
        jono.setNimi("nimi");
        jono.setSiirretaanSijoitteluun(false);
        jono.setTasapistesaanto(fi.vm.sade.service.valintaperusteet.dto.model.Tasapistesaanto.ARVONTA);

        valintatapajonoService.lisaaValintatapajonoValinnanVaiheelle(valinnanVaiheOid, jono, null);
    }

}

@Configuration
@ImportResource(value = "classpath:test-context.xml")
class VtsRestClientConfig {

    @Bean
    @Primary
    VtsRestClient vtsRestClient() {
        VtsRestClient mock = mock(VtsRestClient.class);
        try {
            when(mock.isJonoSijoiteltu(eq("26"))).thenReturn(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mock;
    }
}
/*
final class WithUserDetailsSecurityContextFactory
        implements WithSecurityContextFactory<WithUserDetails> {

    private UserDetailsService userDetailsService;

    @Autowired
    public WithUserDetailsSecurityContextFactory(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    public SecurityContext createSecurityContext(WithUserDetails withUser) {
        String username = withUser.value();
        Assert.hasLength(username, "value() must be non-empty String");
        UserDetails principal = userDetailsService.loadUserByUsername(username);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        return context;
    }
}*/