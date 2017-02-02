package fi.vm.sade.service.valintaperusteet.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import fi.vm.sade.service.valintaperusteet.annotation.DataSetLocation;
import fi.vm.sade.service.valintaperusteet.dao.HakijaryhmaDAO;
import fi.vm.sade.service.valintaperusteet.dao.HakukohdeViiteDAO;
import fi.vm.sade.service.valintaperusteet.dao.ValintaryhmaDAO;
import fi.vm.sade.service.valintaperusteet.dto.*;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.listeners.ValinnatJTACleanInsertTestExecutionListener;
import fi.vm.sade.service.valintaperusteet.model.Hakijaryhma;
import fi.vm.sade.service.valintaperusteet.model.HakijaryhmaValintatapajono;
import fi.vm.sade.service.valintaperusteet.model.ValinnanVaihe;
import fi.vm.sade.service.valintaperusteet.model.Valintatapajono;
import fi.vm.sade.service.valintaperusteet.service.exception.HakijaryhmaEiKuuluValintatapajonolleException;
import fi.vm.sade.service.valintaperusteet.service.exception.HakijaryhmaEiOleOlemassaException;
import fi.vm.sade.service.valintaperusteet.service.exception.HakijaryhmaValintatapajonoOnJoOlemassaException;
import fi.vm.sade.service.valintaperusteet.service.exception.ValintatapajonoEiOleOlemassaException;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created with IntelliJ IDEA.
 * User: jukais
 * Date: 16.1.2013
 * Time: 14.16
 * To change this template use File | Settings | File Templates.
 */
@ContextConfiguration(locations = "classpath:test-context.xml")
@TestExecutionListeners(listeners = {ValinnatJTACleanInsertTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class })
@RunWith(SpringJUnit4ClassRunner.class)
@DataSetLocation("classpath:test-data-hakijaryhma.xml")
public class HakijaryhmaServiceTest {

    @Autowired
    private HakukohdeViiteDAO hakukohdeViiteDAO;
    @Autowired
    private ValintaryhmaDAO valintaryhmaDAO;
    @Autowired
    private HakijaryhmaService hakijaryhmaService;
    @Autowired
    private HakijaryhmaValintatapajonoService hakijaryhmaValintatapajonoService;
    @Autowired
    private HakukohdeService hakukohdeService;
    @Autowired
    private ValinnanVaiheService valinnanvaiheService;
    @Autowired
    private ValintatapajonoService valintatapajonoService;
    @Autowired
    private ValintaryhmaService valintaryhmaService;
    @Autowired
    private HakijaryhmaDAO hakijaryhmaDAO;

    @Autowired
    private ValintaperusteetModelMapper modelMapper;

    @Test
    public void testReadByOid(){
        final String oid = "hr1";
        final String nimi = "hakijaryhma 1";

        Hakijaryhma hr = hakijaryhmaService.readByOid(oid);
        assertEquals(nimi, hr.getNimi());

        assertEquals("hakijaryhmantyypit_ensikertalaiset nimi", hr.getHakijaryhmatyyppikoodi().getNimiFi());
    }

    @Test
    public void testFindByValintaryhma() {
        // vr1:llä on vain yksi hakijaryhmä, vr4:lla kaksi joilla on tietty järjestys
        assertEquals(1, hakijaryhmaService.findByValintaryhma("vr1").size());
        assertEquals(2, hakijaryhmaService.findByValintaryhma("vr4").size());
    }

    @Test
    public void testDelete() {
        final String oid = "hr1";

        Hakijaryhma hakijaryhma = hakijaryhmaService.readByOid(oid);
        assertNotNull(hakijaryhma);

        hakijaryhmaService.deleteByOid(oid, true);

        try {
            hakijaryhmaService.readByOid(oid);
            assertTrue(false);
        } catch (HakijaryhmaEiOleOlemassaException e) {
            assertTrue(true);
        }

    }

    @Test
    public void testLisaaHakijaryhmaValintaryhmalle() {
        {
            /*

            vr1 ----------+---------------hr1---.
             |            |                |    |
             |            |     .---------hr2   |
             +--vv1       |     |          |    |
             |   |        |     |   .-----hr3   |
             |   +--vtj1--'     |   |      |    |
             |   |   |          |   |   .-hr4   |
             |   '––vtj3        |   |   |       |
             |                  |   |   |       |
             +--haku1-----------'   |   |       |
             |   |              |   |   |       |
             |   '--vv2         |   |   |       |
             |       |          |   |   |       |
             |       +--vtj2----'   |   |       |
             |       |   |          /   |       |
             |       +--vtj4------------'       |
             |       |   |          \           |
             |       '--vtj5--------'           |
             |                                  |
             +--vr2-----------------------hr5---'
                 |
                 '--vv3
                     |
                     +––vtj6 (vtj1)
                     |   |
                     '––vtj7
            */

            assertEquals(1, hakijaryhmaService.findByValintaryhma("vr1").size());
            assertEquals(1, hakijaryhmaService.findByValintaryhma("vr2").size());
            assertEquals(1, hakijaryhmaService.findByHakukohde("1").size());

        }

        HakijaryhmaCreateDTO hakijaryhma = new HakijaryhmaCreateDTO();
        hakijaryhma.setKiintio(20);
        hakijaryhma.setKuvaus("");
        hakijaryhma.setLaskentakaavaId(11L);
        hakijaryhma.setNimi("nimi");
        KoodiDTO hakijaryhmatyyppikoodi = new KoodiDTO();
        hakijaryhmatyyppikoodi.setUri("muu");
        hakijaryhma.setHakijaryhmatyyppikoodi(hakijaryhmatyyppikoodi);

        hakijaryhmaService.lisaaHakijaryhmaValintaryhmalle("vr1", hakijaryhma, null);

        assertEquals(2, hakijaryhmaService.findByValintaryhma("vr1").size());
        assertEquals(2, hakijaryhmaService.findByValintaryhma("vr2").size());
        assertEquals(2, hakijaryhmaService.findByHakukohde("1").size());

        hakijaryhma = new HakijaryhmaCreateDTO();
        hakijaryhma.setKiintio(20);
        hakijaryhma.setKuvaus("");
        hakijaryhma.setLaskentakaavaId(11L);
        hakijaryhma.setNimi("nimi");
        hakijaryhma.setHakijaryhmatyyppikoodi(hakijaryhmatyyppikoodi);

        hakijaryhmaService.lisaaHakijaryhmaValintaryhmalle("vr2", hakijaryhma, null);

        assertEquals(2, hakijaryhmaService.findByValintaryhma("vr1").size());
        assertEquals(3, hakijaryhmaService.findByValintaryhma("vr2").size());
        assertEquals(2, hakijaryhmaService.findByHakukohde("1").size());

        HakijaryhmaSiirraDTO siirrettava = modelMapper.map(hakijaryhma, HakijaryhmaSiirraDTO.class);
        siirrettava.setUusinimi("Uusi nimi");
        siirrettava.setValintaryhmaOid("vr3");

        Hakijaryhma siirretty = hakijaryhmaService.siirra(siirrettava).get();

        assertEquals(1, hakijaryhmaService.findByValintaryhma("vr3").size());
        assertEquals("Uusi nimi", siirretty.getNimi());
    }

    @Test
    @Ignore
    public void testLisaaHakijaryhmaHakukohteelle() {
        {
            /*

            vr1 ----------+---------------hr1---.
             |            |                |    |
             |            |     .---------hr2   |
             +--vv1       |     |          |    |
             |   |        |     |   .-----hr3   |
             |   +--vtj1--'     |   |      |    |
             |   |   |          |   |   .-hr4   |
             |   '––vtj3        |   |   |       |
                    (vtj1)      |   |   |       |
             |                  |   |   |       |
             +--haku1-----------'   |   |       |
             |   |              |   |   |       |
             |   '--vv2         |   |   |       |
             |       |          |   |   |       |
             |       +--vtj2----'   |   |       |
             |       |  (vtj1)      |   |       |
             |       |   |          /   |       |
             |       +--vtj4------------'       |
             |       |  (vtj3)      \           |
             |       |   |          |           |
             |       '--vtj5--------'           |
             |                                  |
             |                                  |
             +--vr2-----------------------hr5---'
                 |
                 '--vv3
                     |
                     +––vtj6
                     |  (vtj1)
                     |   |
                     '––vtj7
                        (vtj3)

            */
            assertEquals(1, hakijaryhmaService.findByValintaryhma("vr1").size());
            assertEquals(1, hakijaryhmaService.findByValintaryhma("vr2").size());
            assertEquals(1, hakijaryhmaService.findByHakukohde("1").size());

        }

        HakijaryhmaCreateDTO hakijaryhma = new HakijaryhmaCreateDTO();
        hakijaryhma.setKiintio(20);
        hakijaryhma.setKuvaus("");
        hakijaryhma.setKaytaKaikki(true);
        hakijaryhma.setTarkkaKiintio(true);
        hakijaryhma.setLaskentakaavaId(11L);
        hakijaryhma.setNimi("nimi");

        hakijaryhmaValintatapajonoService.lisaaHakijaryhmaHakukohteelle("1", hakijaryhma);

        {
            assertEquals(1, hakijaryhmaService.findByValintaryhma("vr1").size());
            assertEquals(1, hakijaryhmaService.findByValintaryhma("vr2").size());
            assertEquals(1, hakijaryhmaService.findByHakukohde("1").size());
        }

        hakijaryhma = new HakijaryhmaCreateDTO();
        hakijaryhma.setKiintio(20);
        hakijaryhma.setKaytaKaikki(true);
        hakijaryhma.setTarkkaKiintio(true);
        hakijaryhma.setKuvaus("");
        hakijaryhma.setLaskentakaavaId(11L);
        hakijaryhma.setNimi("nimi");

        hakijaryhmaService.lisaaHakijaryhmaValintaryhmalle("vr1", hakijaryhma, null);

        {
            assertEquals(2, hakijaryhmaService.findByValintaryhma("vr1").size());
            assertEquals(2, hakijaryhmaService.findByValintaryhma("vr2").size());
            assertEquals(5, hakijaryhmaService.findByHakukohde("1").size());
        }

        hakijaryhma = new HakijaryhmaCreateDTO();
        hakijaryhma.setKiintio(20);
        hakijaryhma.setKuvaus("");
        hakijaryhma.setKaytaKaikki(true);
        hakijaryhma.setTarkkaKiintio(true);
        hakijaryhma.setLaskentakaavaId(11L);
        hakijaryhma.setNimi("nimi");

        hakijaryhmaService.lisaaHakijaryhmaValintaryhmalle("vr2", hakijaryhma, null);

        {
            assertEquals(2, hakijaryhmaService.findByValintaryhma("vr1").size());
            assertEquals(3, hakijaryhmaService.findByValintaryhma("vr2").size());
            assertEquals(5, hakijaryhmaService.findByHakukohde("1").size());
        }

        HakukohdeViiteDTO hakukohde = new HakukohdeViiteDTO();
        hakukohde.setHakuoid("oid2");
        hakukohde.setNimi("");
        hakukohde.setOid("2");

        hakukohdeService.insert(hakukohde, "vr2");
        {
            assertEquals(3, hakijaryhmaService.findByHakukohde("2").size());
        }

        ValintaryhmaDTO valintaryhma = new ValintaryhmaDTO();
        valintaryhma.setNimi("");


        valintaryhma = modelMapper.map(valintaryhmaService.insert(valintaryhma, "vr2"), ValintaryhmaDTO.class);
        {
            assertEquals(3, hakijaryhmaService.findByValintaryhma(valintaryhma.getOid()).size());
        }
    }

    @Test
    @Ignore
    public void testLiitaHakijaryhmaValintatapajonolle() {
        try {
            hakijaryhmaService.liitaHakijaryhmaValintatapajonolle("vtj1", "asdasd");
            assertFalse(true);
        } catch (HakijaryhmaEiOleOlemassaException e) {

        }

        try {
            hakijaryhmaService.liitaHakijaryhmaValintatapajonolle("asdsas", "hr1");
            assertFalse(true);
        } catch (ValintatapajonoEiOleOlemassaException e) {

        }

        try {
            hakijaryhmaService.liitaHakijaryhmaValintatapajonolle("vtj1", "hr1");
            assertFalse(true);
        } catch (HakijaryhmaValintatapajonoOnJoOlemassaException e) {

        }

        assertEquals(1, hakijaryhmaValintatapajonoService.findHakijaryhmaByJono("vtj6").size());
        assertEquals(0, hakijaryhmaValintatapajonoService.findHakijaryhmaByJono("vtj4").size());
        assertEquals(1, hakijaryhmaDAO.readByOid("hr2").getJonot().size());

        hakijaryhmaService.liitaHakijaryhmaValintatapajonolle("vtj4", "hr2");
        hakijaryhmaService.liitaHakijaryhmaValintatapajonolle("vtj4", "hr3");
        hakijaryhmaService.liitaHakijaryhmaValintatapajonolle("vtj4", "hr4");

        List<HakijaryhmaValintatapajono> vtj4 = hakijaryhmaValintatapajonoService.findHakijaryhmaByJono("vtj4");
        assertEquals(3, vtj4.size());
        assertEquals(2, hakijaryhmaDAO.readByOid("hr2").getJonot().size());

        try {
            hakijaryhmaService.liitaHakijaryhmaValintatapajonolle("vtj1", "hr5");
            assertFalse(true);
        } catch (HakijaryhmaEiKuuluValintatapajonolleException e) {

        }
    }

    @Test
    @Ignore
    public void testHakijaryhmaValintatapajonoPeriytyminen() {
        // Poistetaan vanhat liitokset
        hakijaryhmaValintatapajonoService.deleteByOid("hr1_vtj1", true);
        hakijaryhmaValintatapajonoService.deleteByOid("hr3_vtj5", true);
        hakijaryhmaValintatapajonoService.deleteByOid("hr4_vtj2", true);
        hakijaryhmaValintatapajonoService.deleteByOid("hr5_vtj6", true);

        HakukohdeViiteDTO viite = new HakukohdeViiteDTO();
        viite.setHakuoid("temp");
        viite.setOid("temp");
        viite.setNimi("temp");
        hakukohdeService.insert(viite, "vr2");

        for (ValinnanVaihe valinnanVaihe : valinnanvaiheService.findByHakukohde("temp")) {
            List<Valintatapajono> jonoByValinnanvaihe = valintatapajonoService.findJonoByValinnanvaihe(valinnanVaihe.getOid());
            assertEquals(0, jonoByValinnanvaihe.get(0).getHakijaryhmat().size());
            assertEquals(0, jonoByValinnanvaihe.get(1).getHakijaryhmat().size());
        }

        assertEquals(0, hakijaryhmaValintatapajonoService.findHakijaryhmaByJono("vtj1").size());
        assertEquals(0, hakijaryhmaValintatapajonoService.findHakijaryhmaByJono("vtj2").size());
        assertEquals(0, hakijaryhmaValintatapajonoService.findHakijaryhmaByJono("vtj7").size());


        hakijaryhmaService.liitaHakijaryhmaValintatapajonolle("vtj1", "hr1");
        hakijaryhmaService.liitaHakijaryhmaValintatapajonolle("vtj7", "hr5");


        assertEquals(1, hakijaryhmaValintatapajonoService.findHakijaryhmaByJono("vtj1").size());
        assertEquals(1, hakijaryhmaValintatapajonoService.findHakijaryhmaByJono("vtj2").size());
        assertEquals(1, hakijaryhmaValintatapajonoService.findHakijaryhmaByJono("vtj7").size());

        for (ValinnanVaihe valinnanVaihe : valinnanvaiheService.findByHakukohde("temp")) {
            List<Valintatapajono> jonoByValinnanvaihe = valintatapajonoService.findJonoByValinnanvaihe(valinnanVaihe.getOid());
            assertEquals(1, jonoByValinnanvaihe.get(0).getHakijaryhmat().size());
            assertEquals(1, jonoByValinnanvaihe.get(1).getHakijaryhmat().size());
        }

    }

    @Test
    public void testJarjestaValintaryhmanHakijaryhmatAsettaaUudenJarjestyksenAnnettujenOidienPerusteella() throws Exception {
        List<String> original = Arrays.asList("hr6", "hr7");
        assertStream(original, hakijaryhmaService.findByValintaryhma("vr4").stream().map(Hakijaryhma::getOid));

        List<String> reversed = Arrays.asList("hr7", "hr6");
        hakijaryhmaService.jarjestaHakijaryhmat(reversed).stream().map(Hakijaryhma::getOid);

        assertStream(reversed, hakijaryhmaService.findByValintaryhma("vr4").stream().map(Hakijaryhma::getOid));
    }

    private <T> void assertStream(List<T> expected, Stream<T> stream) {
        assertEquals(expected, stream.collect(Collectors.toList()));
    }

}
