package fi.vm.sade.service.valintaperusteet.service;

import fi.vm.sade.dbunit.annotation.DataSetLocation;
import fi.vm.sade.dbunit.listener.JTACleanInsertTestExecutionListener;
import fi.vm.sade.service.valintaperusteet.dao.HakijaryhmaDAO;
import fi.vm.sade.service.valintaperusteet.dao.HakukohdeViiteDAO;
import fi.vm.sade.service.valintaperusteet.dao.ValintaryhmaDAO;
import fi.vm.sade.service.valintaperusteet.dto.HakukohdeViiteDTO;
import fi.vm.sade.service.valintaperusteet.model.Hakijaryhma;
import fi.vm.sade.service.valintaperusteet.model.Valintaryhma;
import fi.vm.sade.service.valintaperusteet.service.exception.HakijaryhmaEiKuuluValintatapajonolleException;
import fi.vm.sade.service.valintaperusteet.service.exception.HakijaryhmaEiOleOlemassaException;
import fi.vm.sade.service.valintaperusteet.service.exception.HakijaryhmaValintatapajonoOnJoOlemassaException;
import fi.vm.sade.service.valintaperusteet.service.exception.ValintatapajonoEiOleOlemassaException;
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

import static junit.framework.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: jukais
 * Date: 16.1.2013
 * Time: 14.16
 * To change this template use File | Settings | File Templates.
 */
@ContextConfiguration(locations = "classpath:test-context.xml")
@TestExecutionListeners(listeners = {JTACleanInsertTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class})
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
    private HakukohdeService hakukohdeService;
    @Autowired
    private ValintaryhmaService valintaryhmaService;
    @Autowired
    private HakijaryhmaDAO hakijaryhmaDAO;

    @Test
    public void testFindByValintaryhma() {
        final String oid = "vr1";

        List<Hakijaryhma> hakijaryhmas = hakijaryhmaService.findByValintaryhma(oid);

        assertEquals(1, hakijaryhmas.size());
    }

//    private List<Valintaryhma> jarjestaValintaryhmatIdnMukaan(List<Valintaryhma> vr) {
//        class ValintaryhmaComparator implements Comparator<Valintaryhma> {
//            @Override
//            public int compare(Valintaryhma o1, Valintaryhma o2) {
//                return o1.getId().compareTo(o2.getId());
//            }
//        }
//
//        Collections.sort(vr, new ValintaryhmaComparator());
//        return vr;
//    }
//
//    private boolean valinnanVaiheetOvatKopioita(ValinnanVaihe vv1, ValinnanVaihe vv2) {
//        return vv1.getNimi().equals(vv2.getNimi()) && vv1.getAktiivinen().equals(vv2.getAktiivinen())
//                && vv1.getKuvaus().equals(vv2.getKuvaus());
//    }

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
            assertEquals(3, hakijaryhmaService.findByHakukohde("1").size());

        }

        Hakijaryhma hakijaryhma = new Hakijaryhma();
        hakijaryhma.setKiintio(20);
        hakijaryhma.setKuvaus("");
        hakijaryhma.setLaskentakaavaId(11L);
        hakijaryhma.setNimi("nimi");

        hakijaryhmaService.lisaaHakijaryhmaValintaryhmalle("vr1", hakijaryhma);

        assertEquals(2, hakijaryhmaService.findByValintaryhma("vr1").size());
        assertEquals(2, hakijaryhmaService.findByValintaryhma("vr2").size());
        assertEquals(4, hakijaryhmaService.findByHakukohde("1").size());

        hakijaryhma = new Hakijaryhma();
        hakijaryhma.setKiintio(20);
        hakijaryhma.setKuvaus("");
        hakijaryhma.setLaskentakaavaId(11L);
        hakijaryhma.setNimi("nimi");

        hakijaryhmaService.lisaaHakijaryhmaValintaryhmalle("vr2", hakijaryhma);

        assertEquals(2, hakijaryhmaService.findByValintaryhma("vr1").size());
        assertEquals(3, hakijaryhmaService.findByValintaryhma("vr2").size());
        assertEquals(4, hakijaryhmaService.findByHakukohde("1").size());
    }

    @Test
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
            assertEquals(3, hakijaryhmaService.findByHakukohde("1").size());

        }

        Hakijaryhma hakijaryhma = new Hakijaryhma();
        hakijaryhma.setKiintio(20);
        hakijaryhma.setKuvaus("");
        hakijaryhma.setLaskentakaavaId(11L);
        hakijaryhma.setNimi("nimi");

        hakijaryhmaService.lisaaHakijaryhmaHakukohteelle("1", hakijaryhma);

        {
            assertEquals(1, hakijaryhmaService.findByValintaryhma("vr1").size());
            assertEquals(1, hakijaryhmaService.findByValintaryhma("vr2").size());
            assertEquals(4, hakijaryhmaService.findByHakukohde("1").size());
        }

        hakijaryhma = new Hakijaryhma();
        hakijaryhma.setKiintio(20);
        hakijaryhma.setKuvaus("");
        hakijaryhma.setLaskentakaavaId(11L);
        hakijaryhma.setNimi("nimi");

        hakijaryhmaService.lisaaHakijaryhmaValintaryhmalle("vr1", hakijaryhma);

        {
            assertEquals(2, hakijaryhmaService.findByValintaryhma("vr1").size());
            assertEquals(2, hakijaryhmaService.findByValintaryhma("vr2").size());
            assertEquals(5, hakijaryhmaService.findByHakukohde("1").size());
        }

        hakijaryhma = new Hakijaryhma();
        hakijaryhma.setKiintio(20);
        hakijaryhma.setKuvaus("");
        hakijaryhma.setLaskentakaavaId(11L);
        hakijaryhma.setNimi("nimi");

        hakijaryhmaService.lisaaHakijaryhmaValintaryhmalle("vr2", hakijaryhma);

        {
            assertEquals(2, hakijaryhmaService.findByValintaryhma("vr1").size());
            assertEquals(3, hakijaryhmaService.findByValintaryhma("vr2").size());
            assertEquals(5, hakijaryhmaService.findByHakukohde("1").size());
        }

        HakukohdeViiteDTO hakukohde = new HakukohdeViiteDTO();
        hakukohde.setHakuoid("oid2");
        hakukohde.setNimi("");
        hakukohde.setOid("2");
        hakukohde.setValintaryhmaOid("vr2");

        hakukohdeService.insert(hakukohde);
        {
            assertEquals(3, hakijaryhmaService.findByHakukohde("2").size());
        }

        Valintaryhma valintaryhma = new Valintaryhma();
        valintaryhma.setNimi("");
        valintaryhma.setHakuOid("hakuoid");

        valintaryhma = valintaryhmaService.insert(valintaryhma, "vr2");
        {
            assertEquals(3, hakijaryhmaService.findByValintaryhma(valintaryhma.getOid()).size() );
        }
    }

    @Test
    public void testLiitaHakijaryhmaValintatapajonolle() {
        try {
            hakijaryhmaService.liitaHakijaryhmaValintatapajonolle("vtj1", "asdasd");
            assertFalse(true);
        } catch(HakijaryhmaEiOleOlemassaException e){

        }

        try {
            hakijaryhmaService.liitaHakijaryhmaValintatapajonolle("asdsas", "hr1");
            assertFalse(true);
        } catch(ValintatapajonoEiOleOlemassaException e){

        }

        try {
            hakijaryhmaService.liitaHakijaryhmaValintatapajonolle("vtj1", "hr1");
            assertFalse(true);
        } catch(HakijaryhmaValintatapajonoOnJoOlemassaException e){

        }

        assertEquals(0, hakijaryhmaService.findHakijaryhmaByJono("vtj6").size());
        hakijaryhmaService.liitaHakijaryhmaValintatapajonolle("vtj6", "hr5");
        assertEquals(1, hakijaryhmaService.findHakijaryhmaByJono("vtj6").size());

        assertEquals(0, hakijaryhmaService.findHakijaryhmaByJono("vtj4").size());
        assertEquals(1, hakijaryhmaDAO.readByOid("hr2").getJonot().size());

        hakijaryhmaService.liitaHakijaryhmaValintatapajonolle("vtj4", "hr2");
        hakijaryhmaService.liitaHakijaryhmaValintatapajonolle("vtj4", "hr3");
        hakijaryhmaService.liitaHakijaryhmaValintatapajonolle("vtj4", "hr4");

        List<Hakijaryhma> vtj4 = hakijaryhmaService.findHakijaryhmaByJono("vtj4");
        assertEquals(3, vtj4.size());
        assertEquals(2, hakijaryhmaDAO.readByOid("hr2").getJonot().size());

        try {
            hakijaryhmaService.liitaHakijaryhmaValintatapajonolle("vtj1", "hr5");
            assertFalse(true);
        } catch (HakijaryhmaEiKuuluValintatapajonolleException e) {

        }
    }
}
