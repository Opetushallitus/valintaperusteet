package fi.vm.sade.service.valintaperusteet.service;

import fi.vm.sade.service.valintaperusteet.annotation.DataSetLocation;
import fi.vm.sade.service.valintaperusteet.dao.LaskentakaavaDAO;
import fi.vm.sade.service.valintaperusteet.dao.ValintatapajonoDAO;
import fi.vm.sade.service.valintaperusteet.dto.HakukohdeViiteDTO;
import fi.vm.sade.service.valintaperusteet.dto.JarjestyskriteeriCreateDTO;
import fi.vm.sade.service.valintaperusteet.listeners.ValinnatJTACleanInsertTestExecutionListener;
import fi.vm.sade.service.valintaperusteet.model.HakukohdeViite;
import fi.vm.sade.service.valintaperusteet.model.Jarjestyskriteeri;
import fi.vm.sade.service.valintaperusteet.service.exception.JarjestyskriteeriEiOleOlemassaException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: jukais
 * Date: 17.1.2013
 * Time: 15.14
 * To change this template use File | Settings | File Templates.
 */
@ContextConfiguration(locations = "classpath:test-context.xml")
@TestExecutionListeners(listeners = {ValinnatJTACleanInsertTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class})
@RunWith(SpringJUnit4ClassRunner.class)
@DataSetLocation("classpath:test-data.xml")
public class JarjestyskriteeriServiceTest {

    @Autowired
    private HakukohdeService hakukohdeService;

    @Autowired
    private JarjestyskriteeriService jarjestyskriteeriService;

    @Autowired
    private ValintatapajonoDAO valintatapajonoDAO;

    @Autowired
    private LaskentakaavaDAO laskentakaavaDAO;


    @Test(expected = RuntimeException.class)
    public void testLisaaJarjestyskriteeriLuonnosLaskentakaava() {
        final String valintatapajonoOid = "1059";
        final Long laskentakaavaId = 409L;

        JarjestyskriteeriCreateDTO jarjestyskriteeri = new JarjestyskriteeriCreateDTO();
        jarjestyskriteeri.setMetatiedot("jotain metaa");
        jarjestyskriteeriService.lisaaJarjestyskriteeriValintatapajonolle(valintatapajonoOid, jarjestyskriteeri, null, laskentakaavaId);
    }

    @Test
    public void testLisaaJarjestyskriteeri() {
        final String valintatapajonoOid = "1059";
        final Long laskentakaavaId = 408L;

        JarjestyskriteeriCreateDTO jarjestyskriteeri = new JarjestyskriteeriCreateDTO();
        jarjestyskriteeri.setMetatiedot("jotain metaa");
        jarjestyskriteeri.setAktiivinen(true);
        Jarjestyskriteeri lisatty = jarjestyskriteeriService.lisaaJarjestyskriteeriValintatapajonolle(valintatapajonoOid, jarjestyskriteeri, null, laskentakaavaId);

        assertNotNull(jarjestyskriteeriService.readByOid(lisatty.getOid()));
    }

    @Test
    public void testUpdate() {
        // Alkutilanne
        //  VR1 -> Vaihe -> Jono 3201
        //                    JK1 3201
        //                    JK2 3202
        //                    JK3 3203
        //    VR2  -> Vaihe(c) -> Jono(c) 3202
        //                          JK1(c) 3204
        //                          JK4    3210
        //                          JK2(c) 3205
        //                          JK3(c) 3206
        //
        //      Hakukohde -> Vaihe(c) -> Jono(c) 3203
        //                                  JK1(c) 3207
        //                                  JK5    3212
        //                                  JK4(c) 3211
        //                                  JK2(c) 3208
        //                                  JK3(c) 3209

        Jarjestyskriteeri jarjestyskriteeri = jarjestyskriteeriService.readByOid("3201");
        assertEquals(true, jarjestyskriteeri.getAktiivinen().booleanValue());
        assertEquals("jk1", jarjestyskriteeri.getMetatiedot());

        jarjestyskriteeri = jarjestyskriteeriService.readByOid("3204");
        assertEquals(true, jarjestyskriteeri.getAktiivinen().booleanValue());
        assertEquals("jk1(c)", jarjestyskriteeri.getMetatiedot());

        jarjestyskriteeri = jarjestyskriteeriService.readByOid("3207");
        assertEquals(true, jarjestyskriteeri.getAktiivinen().booleanValue());
        assertEquals("jk1(c)(c)", jarjestyskriteeri.getMetatiedot());

        JarjestyskriteeriCreateDTO jk = new JarjestyskriteeriCreateDTO();
        jk.setAktiivinen(false);
        jk.setMetatiedot("Update");
        jarjestyskriteeriService.update("3201", jk, 1L);

        jarjestyskriteeri = jarjestyskriteeriService.readByOid("3201");
        assertEquals(false, jarjestyskriteeri.getAktiivinen().booleanValue());
        assertEquals("Update", jarjestyskriteeri.getMetatiedot());

        jarjestyskriteeri = jarjestyskriteeriService.readByOid("3204");
        assertEquals(false, jarjestyskriteeri.getAktiivinen().booleanValue());
        assertEquals("Update", jarjestyskriteeri.getMetatiedot());

        jarjestyskriteeri = jarjestyskriteeriService.readByOid("3207");
        assertEquals(false, jarjestyskriteeri.getAktiivinen().booleanValue());
        assertEquals("Update", jarjestyskriteeri.getMetatiedot());
    }


    @Test
    public void lisaaJarjestyskriteeriTyhjalleValintatapajonolle() {
        // Alkutilanne
        //  VR1 -> Vaihe -> Jono
        //    VR2  -> Vaihe(c) -> Jono(c)
        //      Hakukohde -> Vaihe(c) -> Jono(c)
        final String valintatapajonoOid = "3001";
        {
            assertNotNull(valintatapajonoDAO.readByOid(valintatapajonoOid));
            assertEquals(0, jarjestyskriteeriService.findJarjestyskriteeriByJono(valintatapajonoOid).size());
        }

        JarjestyskriteeriCreateDTO uusiJK = new JarjestyskriteeriCreateDTO();
        uusiJK.setAktiivinen(true);
        uusiJK.setMetatiedot("uusi kuvaus");

        Jarjestyskriteeri lisatty = jarjestyskriteeriService.lisaaJarjestyskriteeriValintatapajonolle(valintatapajonoOid, uusiJK, null, 1L);

        {
            // Lopputilanne
            //  VR1 -> Vaihe -> Jono -> JK
            //    VR2  -> Vaihe(c) -> Jono(c) -> JK(c)
            //      Hakukohde -> Vaihe(c) -> Jono(c) -> JK(c)
            assertNotNull(valintatapajonoDAO.readByOid(valintatapajonoOid));
            List<Jarjestyskriteeri> jarjestyskriteeriList = jarjestyskriteeriService.findJarjestyskriteeriByJono(valintatapajonoOid);
            assertEquals(1, jarjestyskriteeriList.size());
            assertTrue(jarjestyskriteeriList.get(0).getMaster() == null && jarjestyskriteeriList.get(0).equals(lisatty));

            jarjestyskriteeriList = jarjestyskriteeriService.findJarjestyskriteeriByJono("3002");
            assertEquals(1, jarjestyskriteeriList.size());
            assertTrue(jarjestyskriteeriList.get(0).getMaster() != null);

            jarjestyskriteeriList = jarjestyskriteeriService.findJarjestyskriteeriByJono("3003");
            assertEquals(1, jarjestyskriteeriList.size());
            assertTrue(jarjestyskriteeriList.get(0).getMaster() != null);
        }
    }

    @Test
    public void lisaaJarjestyskriteeriValintatapajonolle() {
        // Alkutilanne
        //  VR1 -> Vaihe -> Jono 3101
        //                    JK1
        //                    JK2
        //                    JK3
        //    VR2  -> Vaihe(c) -> Jono(c) 3102
        //                          JK1(c)
        //                          JK4
        //                          JK2(c)
        //                          JK3(c)
        //
        //      Hakukohde -> Vaihe(c) -> Jono(c) 3103
        //                                  JK1(c)
        //                                  JK5
        //                                  JK4(c)
        //                                  JK2(c)
        //                                  JK3(c)
        String valintatapajonoOid = "3101";
        {
            assertNotNull(valintatapajonoDAO.readByOid(valintatapajonoOid));
            assertEquals(3, jarjestyskriteeriService.findJarjestyskriteeriByJono(valintatapajonoOid).size());
            assertNotNull(valintatapajonoDAO.readByOid("3102"));
            assertEquals(4, jarjestyskriteeriService.findJarjestyskriteeriByJono("3102").size());
            assertNotNull(valintatapajonoDAO.readByOid("3103"));
            assertEquals(5, jarjestyskriteeriService.findJarjestyskriteeriByJono("3103").size());
        }

        JarjestyskriteeriCreateDTO uusiJK = new JarjestyskriteeriCreateDTO();
        uusiJK.setAktiivinen(true);
        uusiJK.setMetatiedot("uusi kuvaus");

        Jarjestyskriteeri lisatty = jarjestyskriteeriService.lisaaJarjestyskriteeriValintatapajonolle(valintatapajonoOid, uusiJK, "3101", 1L);

        {
            // Lopputilanne
            //  VR1 -> Vaihe -> Jono
            //                    JK1
            //                    uusi
            //                    JK2
            //                    JK3
            //    VR2  -> Vaihe(c) -> Jono(c)
            //                          JK1(c)
            //                          uusi(c)
            //                          JK4
            //                          JK2(c)
            //                          JK3(c)
            //
            //      Hakukohde -> Vaihe(c) -> Jono(c)
            //                                  JK1(c)
            //                                  uusi(c)
            //                                  JK5
            //                                  JK4(c)
            //                                  JK2(c)
            //                                  JK3(c)

            assertNotNull(valintatapajonoDAO.readByOid(valintatapajonoOid));
            List<Jarjestyskriteeri> jarjestyskriteeriList = jarjestyskriteeriService.findJarjestyskriteeriByJono(valintatapajonoOid);
            assertEquals(4, jarjestyskriteeriList.size());
            assertTrue(jarjestyskriteeriList.get(1).getMaster() == null && jarjestyskriteeriList.get(1).equals(lisatty));

            jarjestyskriteeriList = jarjestyskriteeriService.findJarjestyskriteeriByJono("3102");
            assertEquals(5, jarjestyskriteeriList.size());
            assertTrue(jarjestyskriteeriList.get(1).getMaster() != null && jarjestyskriteeriList.get(1).getMaster().equals(lisatty));
            Jarjestyskriteeri kopio = jarjestyskriteeriList.get(1);

            jarjestyskriteeriList = jarjestyskriteeriService.findJarjestyskriteeriByJono("3103");
            assertEquals(6, jarjestyskriteeriList.size());
            assertTrue(jarjestyskriteeriList.get(1).getMaster() != null && jarjestyskriteeriList.get(1).getMaster().equals(kopio));
        }

        valintatapajonoOid = "3102";
        uusiJK = new JarjestyskriteeriCreateDTO();
        uusiJK.setAktiivinen(true);
        uusiJK.setMetatiedot("uusi kuvaus");
        lisatty = jarjestyskriteeriService.lisaaJarjestyskriteeriValintatapajonolle(valintatapajonoOid, uusiJK, "3110", 1L);

        {
            // Lopputilanne
            //  VR1 -> Vaihe -> Jono
            //                    JK1
            //                    uusi
            //                    JK2
            //                    JK3
            //    VR2  -> Vaihe(c) -> Jono(c)
            //                          JK1(c)
            //                          uusi(c)
            //                          JK4
            //                          uusi2
            //                          JK2(c)
            //                          JK3(c)
            //
            //      Hakukohde -> Vaihe(c) -> Jono(c)
            //                                  JK1(c)
            //                                  uusi(c)
            //                                  JK5
            //                                  JK4(c)
            //                                  uusi2(c)
            //                                  JK2(c)
            //                                  JK3(c)

            assertNotNull(valintatapajonoDAO.readByOid(valintatapajonoOid));
            List<Jarjestyskriteeri> jarjestyskriteeriList = jarjestyskriteeriService.findJarjestyskriteeriByJono("3101");
            assertEquals(4, jarjestyskriteeriList.size());

            jarjestyskriteeriList = jarjestyskriteeriService.findJarjestyskriteeriByJono("3102");
            assertEquals(6, jarjestyskriteeriList.size());
            assertTrue(jarjestyskriteeriList.get(3).getMaster() == null && jarjestyskriteeriList.get(3).equals(lisatty));

            jarjestyskriteeriList = jarjestyskriteeriService.findJarjestyskriteeriByJono("3103");
            assertEquals(7, jarjestyskriteeriList.size());
            assertTrue(jarjestyskriteeriList.get(4).getMaster() != null && jarjestyskriteeriList.get(4).getMaster().equals(lisatty));
        }

        valintatapajonoOid = "3103";
        uusiJK = new JarjestyskriteeriCreateDTO();
        uusiJK.setAktiivinen(true);
        uusiJK.setMetatiedot("uusi kuvaus");
        lisatty = jarjestyskriteeriService.lisaaJarjestyskriteeriValintatapajonolle(valintatapajonoOid, uusiJK, "3108", 1L);

        {
            // Lopputilanne
            //  VR1 -> Vaihe -> Jono
            //                    JK1
            //                    uusi
            //                    JK2
            //                    JK3
            //    VR2  -> Vaihe(c) -> Jono(c)
            //                          JK1(c)
            //                          uusi(c)
            //                          JK4
            //                          uusi2
            //                          JK2(c)
            //                          JK3(c)
            //
            //      Hakukohde -> Vaihe(c) -> Jono(c)
            //                                  JK1(c)
            //                                  uusi(c)
            //                                  JK5
            //                                  JK4(c)
            //                                  uusi2(c)
            //                                  JK2(c)       3108
            //                                  uusi
            //                                  JK3(c)

            assertNotNull(valintatapajonoDAO.readByOid(valintatapajonoOid));
            List<Jarjestyskriteeri> jarjestyskriteeriList = jarjestyskriteeriService.findJarjestyskriteeriByJono("3101");
            assertEquals(4, jarjestyskriteeriList.size());

            jarjestyskriteeriList = jarjestyskriteeriService.findJarjestyskriteeriByJono("3102");
            assertEquals(6, jarjestyskriteeriList.size());

            jarjestyskriteeriList = jarjestyskriteeriService.findJarjestyskriteeriByJono("3103");
            assertEquals(8, jarjestyskriteeriList.size());
            assertTrue(jarjestyskriteeriList.get(6).getMaster() == null && jarjestyskriteeriList.get(6).equals(lisatty));
        }
    }

    @Test
    public void testJarjesta() {
        {
            // Alkutilanne
            //  VR1 -> Vaihe -> Jono 3201
            //                    JK1 3201
            //                    JK2 3202
            //                    JK3 3203
            //    VR2  -> Vaihe(c) -> Jono(c) 3102
            //                          JK1(c) 3204
            //                          JK4    3210
            //                          JK2(c) 3205
            //                          JK3(c) 3206
            //
            //      Hakukohde -> Vaihe(c) -> Jono(c) 3103
            //                                  JK1(c) 3207
            //                                  JK5    3212
            //                                  JK4(c) 3211
            //                                  JK2(c) 3208
            //                                  JK3(c) 3209


            List<Jarjestyskriteeri> jono1 = jarjestyskriteeriService.findJarjestyskriteeriByJono("3201");
            assertEquals(3, jono1.size());
            assertTrue(jono1.get(0).getId().longValue() == 3201L && jono1.get(0).getMaster() == null);
            assertTrue(jono1.get(1).getId().longValue() == 3202L && jono1.get(1).getMaster() == null);
            assertTrue(jono1.get(2).getId().longValue() == 3203L && jono1.get(2).getMaster() == null);

            List<Jarjestyskriteeri> jono2 = jarjestyskriteeriService.findJarjestyskriteeriByJono("3202");
            assertEquals(4, jono2.size());
            assertTrue(jono2.get(0).getId().longValue() == 3204L && jono2.get(0).getMaster() != null);
            assertTrue(jono2.get(1).getId().longValue() == 3210L && jono2.get(1).getMaster() == null);
            assertTrue(jono2.get(2).getId().longValue() == 3205L && jono2.get(2).getMaster() != null);
            assertTrue(jono2.get(3).getId().longValue() == 3206L && jono2.get(3).getMaster() != null);

            List<Jarjestyskriteeri> jono3 = jarjestyskriteeriService.findJarjestyskriteeriByJono("3203");
            assertEquals(5, jono3.size());
            assertTrue(jono3.get(0).getId().longValue() == 3207L && jono3.get(0).getMaster() != null);
            assertTrue(jono3.get(1).getId().longValue() == 3212L && jono3.get(1).getMaster() == null);
            assertTrue(jono3.get(2).getId().longValue() == 3211L && jono3.get(2).getMaster() != null);
            assertTrue(jono3.get(3).getId().longValue() == 3208L && jono3.get(3).getMaster() != null);
            assertTrue(jono3.get(4).getId().longValue() == 3209L && jono3.get(4).getMaster() != null);


        }

        String[] uusiJarjestys = {"3203", "3202", "3201"};
        List<Jarjestyskriteeri> jarjestetty = jarjestyskriteeriService.jarjestaKriteerit(Arrays.asList(uusiJarjestys));

        {
            // Alkutilanne
            //  VR1 -> Vaihe -> Jono 3201
            //                    JK1 3201
            //                    JK2 3202
            //                    JK3 3203
            //    VR2  -> Vaihe(c) -> Jono(c) 3102
            //                          JK1(c) 3204
            //                          JK4    3210
            //                          JK2(c) 3205
            //                          JK3(c) 3206
            //
            //      Hakukohde -> Vaihe(c) -> Jono(c) 3103
            //                                  JK1(c) 3207
            //                                  JK5    3212
            //                                  JK4(c) 3211
            //                                  JK2(c) 3208
            //                                  JK3(c) 3209
            //
            // Lopputilanne:
            //  VR1 -> Vaihe -> Jono 3201
            //                    JK3 3203
            //                    JK2 3202
            //                    JK1 3201
            //    VR2  -> Vaihe(c) -> Jono(c) 3102
            //                          JK3(c) 3206
            //                          JK2(c) 3205
            //                          JK1(c) 3204
            //                          JK4    3210
            //
            //      Hakukohde -> Vaihe(c) -> Jono(c) 3103
            //                                  JK3(c) 3209
            //                                  JK2(c) 3208
            //                                  JK1(c) 3207
            //                                  JK5    3212
            //                                  JK4(c) 3211

            List<Jarjestyskriteeri> jono1 = jarjestyskriteeriService.findJarjestyskriteeriByJono("3201");
            assertEquals(3, jono1.size());
            assertTrue(jono1.get(0).getId().longValue() == 3203L && jono1.get(0).getMaster() == null);
            assertTrue(jono1.get(1).getId().longValue() == 3202L && jono1.get(1).getMaster() == null);
            assertTrue(jono1.get(2).getId().longValue() == 3201L && jono1.get(2).getMaster() == null);

            List<Jarjestyskriteeri> jono2 = jarjestyskriteeriService.findJarjestyskriteeriByJono("3202");
            assertEquals(4, jono2.size());
            assertTrue(jono2.get(0).getId().longValue() == 3206L && jono2.get(0).getMaster() != null);
            assertTrue(jono2.get(1).getId().longValue() == 3205L && jono2.get(1).getMaster() != null);
            assertTrue(jono2.get(2).getId().longValue() == 3204L && jono2.get(2).getMaster() != null);
            assertTrue(jono2.get(3).getId().longValue() == 3210L && jono2.get(3).getMaster() == null);

            List<Jarjestyskriteeri> jono3 = jarjestyskriteeriService.findJarjestyskriteeriByJono("3203");
            assertEquals(5, jono3.size());
            assertTrue(jono3.get(0).getId().longValue() == 3209L && jono3.get(0).getMaster() != null);
            assertTrue(jono3.get(1).getId().longValue() == 3208L && jono3.get(1).getMaster() != null);
            assertTrue(jono3.get(2).getId().longValue() == 3207L && jono3.get(2).getMaster() != null);
            assertTrue(jono3.get(3).getId().longValue() == 3212L && jono3.get(3).getMaster() == null);
            assertTrue(jono3.get(4).getId().longValue() == 3211L && jono3.get(4).getMaster() != null);
        }
    }

    @Test
    public void testDeleteInherited() {
        // Alkutilanne
        //  VR1 -> Vaihe -> Jono 3201
        //                    JK1 3201
        //                    JK2 3202
        //                    JK3 3203
        //    VR2  -> Vaihe(c) -> Jono(c) 3202
        //                          JK1(c) 3204
        //                          JK4    3210
        //                          JK2(c) 3205
        //                          JK3(c) 3206
        //
        //      Hakukohde -> Vaihe(c) -> Jono(c) 3203
        //                                  JK1(c) 3207
        //                                  JK5    3212
        //                                  JK4(c) 3211
        //                                  JK2(c) 3208
        //                                  JK3(c) 3209

        assertNotNull(jarjestyskriteeriService.readByOid("3201"));
        assertNotNull(jarjestyskriteeriService.readByOid("3212"));

        jarjestyskriteeriService.deleteByOid("3201");

        try {
            assertNull(jarjestyskriteeriService.readByOid("3201"));
        } catch (JarjestyskriteeriEiOleOlemassaException e) {
        }
        ;

        assertNotNull(jarjestyskriteeriService.readByOid("3212"));

        try {
            assertNull(jarjestyskriteeriService.readByOid("3201"));
        } catch (JarjestyskriteeriEiOleOlemassaException e) {
        }
        ;
    }

    @Test
    public void testHakukohdeInsert() {
        // Alkutilanne
        //  VR1 -> Vaihe -> Jono 3201
        //                    JK1 3201
        //                    JK2 3202
        //                    JK3 3203
        //    VR2  -> Vaihe(c) -> Jono(c) 3202
        //                          JK1(c) 3204
        //                          JK4    3210
        //                          JK2(c) 3205
        //                          JK3(c) 3206
        //
        //      Hakukohde -> Vaihe(c) -> Jono(c) 3203
        //                                  JK1(c) 3207
        //                                  JK5    3212
        //                                  JK4(c) 3211
        //                                  JK2(c) 3208
        //                                  JK3(c) 3209

        HakukohdeViiteDTO dto = new HakukohdeViiteDTO();
        dto.setOid("oidii");
        dto.setNimi("Nimi");
        dto.setHakuoid("uushakuoidi");

        HakukohdeViite insert = hakukohdeService.insert(dto, "3202");

        // Joo-o
        int size = insert.getValinnanvaiheet().iterator().next().getJonot().iterator().next().getJarjestyskriteerit().size();
        assertEquals(4, size);
    }
}
