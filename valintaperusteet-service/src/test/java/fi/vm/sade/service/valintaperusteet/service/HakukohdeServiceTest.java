package fi.vm.sade.service.valintaperusteet.service;

import fi.vm.sade.service.valintaperusteet.annotation.DataSetLocation;
import fi.vm.sade.service.valintaperusteet.dao.HakukohdekoodiDAO;
import fi.vm.sade.service.valintaperusteet.dao.ValinnanVaiheDAO;
import fi.vm.sade.service.valintaperusteet.dao.ValintatapajonoDAO;
import fi.vm.sade.service.valintaperusteet.dto.HakukohdeViiteDTO;
import fi.vm.sade.service.valintaperusteet.listeners.ValinnatJTACleanInsertTestExecutionListener;
import fi.vm.sade.service.valintaperusteet.model.HakukohdeViite;
import fi.vm.sade.service.valintaperusteet.model.ValinnanVaihe;
import fi.vm.sade.service.valintaperusteet.model.Valintatapajono;
import fi.vm.sade.service.valintaperusteet.service.exception.HakukohdeViiteEiOleOlemassaException;
import fi.vm.sade.service.valintaperusteet.util.LinkitettavaJaKopioitavaUtil;
import org.apache.commons.lang.StringUtils;
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
 * User: kwuoti
 * Date: 20.2.2013
 * Time: 9.05
 */
@ContextConfiguration(locations = "classpath:test-context.xml")
@TestExecutionListeners(listeners = {ValinnatJTACleanInsertTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class})
@RunWith(SpringJUnit4ClassRunner.class)
@DataSetLocation("classpath:test-data.xml")
public class HakukohdeServiceTest {

    @Autowired
    private ValintatapajonoDAO valintatapajonoDAO;

    @Autowired
    private ValinnanVaiheDAO valinnanVaiheDAO;

    @Autowired
    private HakukohdeService hakukohdeService;

    @Autowired
    private HakukohdekoodiDAO hakukohdekoodiDAO;

    @Autowired
    private ValintaryhmaService valintaryhmaService;

    @Test
    public void testInsert() {
        final String parentOid = "oid33";
        {
            assertNotNull(valintaryhmaService.readByOid(parentOid));
            List<ValinnanVaihe> vr33Lvaiheet = LinkitettavaJaKopioitavaUtil.jarjesta(valinnanVaiheDAO.findByValintaryhma(parentOid));

            assertEquals(2, vr33Lvaiheet.size());
            ValinnanVaihe vaihe80L = vr33Lvaiheet.get(0);
            ValinnanVaihe vaihe81L = vr33Lvaiheet.get(1);

            List<Valintatapajono> vaihe80Ljonot = LinkitettavaJaKopioitavaUtil.jarjesta(valintatapajonoDAO.findByValinnanVaihe(vaihe80L.getOid()));
            List<Valintatapajono> vaihe81Ljonot = LinkitettavaJaKopioitavaUtil.jarjesta(valintatapajonoDAO.findByValinnanVaihe(vaihe81L.getOid()));

            assertEquals(2, vaihe80Ljonot.size());
            assertEquals(1, vaihe81Ljonot.size());
        }

        HakukohdeViiteDTO uusiHakukohde = new HakukohdeViiteDTO();
        uusiHakukohde.setNimi("Uusi hakukohde");
        uusiHakukohde.setOid("oid1234567");
        uusiHakukohde.setHakuoid("uusihakuoid");

        HakukohdeViite lisatty = hakukohdeService.insert(uusiHakukohde, parentOid);
        assertTrue(StringUtils.isNotBlank(lisatty.getOid()));

        {
            assertNotNull(valintaryhmaService.readByOid(parentOid));
            List<ValinnanVaihe> vr33Lvaiheet = LinkitettavaJaKopioitavaUtil.jarjesta(valinnanVaiheDAO.findByValintaryhma(parentOid));

            assertEquals(2, vr33Lvaiheet.size());
            ValinnanVaihe vaihe80L = vr33Lvaiheet.get(0);
            ValinnanVaihe vaihe81L = vr33Lvaiheet.get(1);

            List<Valintatapajono> vaihe80Ljonot = LinkitettavaJaKopioitavaUtil.jarjesta(valintatapajonoDAO.findByValinnanVaihe(vaihe80L.getOid()));
            List<Valintatapajono> vaihe81Ljonot = LinkitettavaJaKopioitavaUtil.jarjesta(valintatapajonoDAO.findByValinnanVaihe(vaihe81L.getOid()));

            assertEquals(2, vaihe80Ljonot.size());
            assertEquals(1, vaihe81Ljonot.size());
        }
        {
            assertNotNull(hakukohdeService.readByOid(lisatty.getOid()));
            List<ValinnanVaihe> uusiVaiheet = LinkitettavaJaKopioitavaUtil.jarjesta(valinnanVaiheDAO.findByHakukohde(lisatty.getOid()));

            assertEquals(2, uusiVaiheet.size());
            ValinnanVaihe uusiVaihe1 = uusiVaiheet.get(0);
            ValinnanVaihe uusiVaihe2 = uusiVaiheet.get(1);

            List<Valintatapajono> uusiVaihe1jonot = LinkitettavaJaKopioitavaUtil.jarjesta(valintatapajonoDAO.findByValinnanVaihe(uusiVaihe1.getOid()));
            List<Valintatapajono> uusiVaihe2jonot = LinkitettavaJaKopioitavaUtil.jarjesta(valintatapajonoDAO.findByValinnanVaihe(uusiVaihe2.getOid()));

            assertEquals(2, uusiVaihe1jonot.size());
            assertEquals(1, uusiVaihe2jonot.size());
        }
    }

    @Test
    public void testInsertIlmanValintaryhmaa() {
        HakukohdeViiteDTO uusiHakukohde = new HakukohdeViiteDTO();
        uusiHakukohde.setNimi("Uusi hakukohde");
        uusiHakukohde.setOid("oid1234567");
        uusiHakukohde.setHakuoid("uusihakuoid");

        HakukohdeViite lisatty = hakukohdeService.insert(uusiHakukohde, null);
        assertNotNull(hakukohdeService.readByOid(lisatty.getOid()));

    }

    @Test(expected = HakukohdeViiteEiOleOlemassaException.class)
    public void testDeleteByOid() {
        final String hakukohdeOid = "oid12";
        hakukohdeService.readByOid(hakukohdeOid);

        assertEquals(2, valinnanVaiheDAO.findByHakukohde(hakukohdeOid).size());
        assertNotNull(hakukohdekoodiDAO.findByHakukohdeOid(hakukohdeOid));
        hakukohdeService.deleteByOid(hakukohdeOid);
        hakukohdeService.readByOid(hakukohdeOid);

    }

    @Test
    public void testSiirraHakukohdeToiseenValintaryhmaan() {
        final String hakukohdeOid = "oid18";
        final String valintaryhmaOidEnnen = "oid53";
        final String valintaryhmaOidLopuksi = "oid54";

        final String hakukohdekoodiUri = "hakukohdekoodiuri19";
        final String opetuskielikoodiUri = "kieli_fi";
        final String valintakoekoodiUri = "valintakoeuri1";
        {
            HakukohdeViite hakukohde = hakukohdeService.readByOid(hakukohdeOid);
            assertEquals(valintaryhmaOidEnnen, hakukohde.getValintaryhma().getOid());
            assertNull(hakukohde.getManuaalisestiSiirretty());

            List<ValinnanVaihe> vaiheet = LinkitettavaJaKopioitavaUtil.jarjesta(valinnanVaiheDAO.findByHakukohde(hakukohdeOid));
            assertEquals(2, vaiheet.size());
            assertEquals("104", vaiheet.get(0).getOid());
            assertEquals("105", vaiheet.get(1).getOid());
            assertNotNull(vaiheet.get(0).getMasterValinnanVaihe());
            assertNull(vaiheet.get(1).getMasterValinnanVaihe());

            assertEquals(hakukohdekoodiUri, hakukohde.getHakukohdekoodi().getUri());

            assertEquals(1, hakukohde.getValintakokeet().size());
            assertEquals(valintakoekoodiUri, hakukohde.getValintakokeet().iterator().next().getUri());
        }

        hakukohdeService.siirraHakukohdeValintaryhmaan(hakukohdeOid, valintaryhmaOidLopuksi, true);

        {
            HakukohdeViite hakukohde = hakukohdeService.readByOid(hakukohdeOid);
            assertEquals(valintaryhmaOidLopuksi, hakukohde.getValintaryhma().getOid());
            assertTrue(hakukohde.getManuaalisestiSiirretty());

            List<ValinnanVaihe> vaiheet = LinkitettavaJaKopioitavaUtil.jarjesta(valinnanVaiheDAO.findByHakukohde(hakukohdeOid));
            assertEquals(2, vaiheet.size());
            assertNotNull(vaiheet.get(0).getMasterValinnanVaihe());
            assertEquals("103", vaiheet.get(0).getMasterValinnanVaihe().getOid());
            assertEquals("105", vaiheet.get(1).getOid());
            assertNull(vaiheet.get(1).getMasterValinnanVaihe());

            assertEquals(hakukohdekoodiUri, hakukohde.getHakukohdekoodi().getUri());

            assertEquals(1, hakukohde.getValintakokeet().size());
            assertEquals(valintakoekoodiUri, hakukohde.getValintakokeet().iterator().next().getUri());
        }
    }

    @Test
    public void testSiirraHakukohdeValintaryhmaan() {
        final String hakukohdeOid = "oid19";
        final String valintaryhmaOidLopuksi = "oid54";

        final String hakukohdekoodiUri = "hakukohdekoodiuri20";
        final String opetuskielikoodiUri = "kieli_fi";
        final String valintakoekoodiUri = "valintakoeuri1";
        {
            HakukohdeViite hakukohde = hakukohdeService.readByOid(hakukohdeOid);
            assertNull(hakukohde.getValintaryhma());
            assertNull(hakukohde.getManuaalisestiSiirretty());

            List<ValinnanVaihe> vaiheet = LinkitettavaJaKopioitavaUtil.jarjesta(valinnanVaiheDAO.findByHakukohde(hakukohdeOid));
            assertEquals(1, vaiheet.size());
            assertEquals("106", vaiheet.get(0).getOid());
            assertNull(vaiheet.get(0).getMasterValinnanVaihe());

            assertEquals(hakukohdekoodiUri, hakukohde.getHakukohdekoodi().getUri());

            assertEquals(1, hakukohde.getValintakokeet().size());
            assertEquals(valintakoekoodiUri, hakukohde.getValintakokeet().iterator().next().getUri());
        }

        hakukohdeService.siirraHakukohdeValintaryhmaan(hakukohdeOid, valintaryhmaOidLopuksi, true);

        {
            HakukohdeViite hakukohde = hakukohdeService.readByOid(hakukohdeOid);
            assertEquals(valintaryhmaOidLopuksi, hakukohde.getValintaryhma().getOid());
            assertTrue(hakukohde.getManuaalisestiSiirretty());

            List<ValinnanVaihe> vaiheet = LinkitettavaJaKopioitavaUtil.jarjesta(valinnanVaiheDAO.findByHakukohde(hakukohdeOid));
            assertEquals(2, vaiheet.size());
            assertNotNull(vaiheet.get(0).getMasterValinnanVaihe());
            assertEquals("103", vaiheet.get(0).getMasterValinnanVaihe().getOid());
            assertEquals("106", vaiheet.get(1).getOid());
            assertNull(vaiheet.get(1).getMasterValinnanVaihe());

            assertEquals(hakukohdekoodiUri, hakukohde.getHakukohdekoodi().getUri());

            assertEquals(1, hakukohde.getValintakokeet().size());
            assertEquals(valintakoekoodiUri, hakukohde.getValintakokeet().iterator().next().getUri());
        }
    }

    @Test
    public void testSiirraHakukohdePoisValintaryhmasta() {
        final String hakukohdeOid = "oid18";
        final String valintaryhmaOidEnnen = "oid53";

        final String hakukohdekoodiUri = "hakukohdekoodiuri19";
        final String opetuskielikoodiUri = "kieli_fi";
        final String valintakoekoodiUri = "valintakoeuri1";
        {
            HakukohdeViite hakukohde = hakukohdeService.readByOid(hakukohdeOid);
            assertEquals(valintaryhmaOidEnnen, hakukohde.getValintaryhma().getOid());
            assertNull(hakukohde.getManuaalisestiSiirretty());

            List<ValinnanVaihe> vaiheet = LinkitettavaJaKopioitavaUtil.jarjesta(valinnanVaiheDAO.findByHakukohde(hakukohdeOid));
            assertEquals(2, vaiheet.size());
            assertEquals("104", vaiheet.get(0).getOid());
            assertEquals("105", vaiheet.get(1).getOid());
            assertNotNull(vaiheet.get(0).getMasterValinnanVaihe());
            assertNull(vaiheet.get(1).getMasterValinnanVaihe());

            assertEquals(hakukohdekoodiUri, hakukohde.getHakukohdekoodi().getUri());

            assertEquals(1, hakukohde.getValintakokeet().size());
            assertEquals(valintakoekoodiUri, hakukohde.getValintakokeet().iterator().next().getUri());
        }

        hakukohdeService.siirraHakukohdeValintaryhmaan(hakukohdeOid, null, true);

        {
            HakukohdeViite hakukohde = hakukohdeService.readByOid(hakukohdeOid);
            assertNull(hakukohde.getValintaryhma());
            assertTrue(hakukohde.getManuaalisestiSiirretty());

            List<ValinnanVaihe> vaiheet = LinkitettavaJaKopioitavaUtil.jarjesta(valinnanVaiheDAO.findByHakukohde(hakukohdeOid));
            assertEquals(1, vaiheet.size());
            assertEquals("105", vaiheet.get(0).getOid());
            assertNull(vaiheet.get(0).getMasterValinnanVaihe());

            assertEquals(hakukohdekoodiUri, hakukohde.getHakukohdekoodi().getUri());

            assertEquals(1, hakukohde.getValintakokeet().size());
            assertEquals(valintakoekoodiUri, hakukohde.getValintakokeet().iterator().next().getUri());
        }
    }

    @Test
    public void testSiirraHakukohdeSamaanValintaryhmaan() {
        final String hakukohdeOid = "oid18";
        final String valintaryhmaOid = "oid53";

        final String hakukohdekoodiUri = "hakukohdekoodiuri19";
        final String opetuskielikoodiUri = "kieli_fi";
        final String valintakoekoodiUri = "valintakoeuri1";
        {
            HakukohdeViite hakukohde = hakukohdeService.readByOid(hakukohdeOid);
            assertEquals(valintaryhmaOid, hakukohde.getValintaryhma().getOid());
            assertNull(hakukohde.getManuaalisestiSiirretty());

            List<ValinnanVaihe> vaiheet = LinkitettavaJaKopioitavaUtil.jarjesta(valinnanVaiheDAO.findByHakukohde(hakukohdeOid));
            assertEquals(2, vaiheet.size());
            assertEquals("104", vaiheet.get(0).getOid());
            assertEquals("105", vaiheet.get(1).getOid());
            assertNotNull(vaiheet.get(0).getMasterValinnanVaihe());
            assertNull(vaiheet.get(1).getMasterValinnanVaihe());

            assertEquals(hakukohdekoodiUri, hakukohde.getHakukohdekoodi().getUri());

            assertEquals(1, hakukohde.getValintakokeet().size());
            assertEquals(valintakoekoodiUri, hakukohde.getValintakokeet().iterator().next().getUri());
        }

        hakukohdeService.siirraHakukohdeValintaryhmaan(hakukohdeOid, valintaryhmaOid, true);

        {
            HakukohdeViite hakukohde = hakukohdeService.readByOid(hakukohdeOid);
            assertEquals(valintaryhmaOid, hakukohde.getValintaryhma().getOid());
            assertTrue(hakukohde.getManuaalisestiSiirretty());

            List<ValinnanVaihe> vaiheet = LinkitettavaJaKopioitavaUtil.jarjesta(valinnanVaiheDAO.findByHakukohde(hakukohdeOid));
            assertEquals(2, vaiheet.size());
            assertEquals("104", vaiheet.get(0).getOid());
            assertEquals("105", vaiheet.get(1).getOid());
            assertNotNull(vaiheet.get(0).getMasterValinnanVaihe());
            assertNull(vaiheet.get(1).getMasterValinnanVaihe());

            assertEquals(hakukohdekoodiUri, hakukohde.getHakukohdekoodi().getUri());

            assertEquals(1, hakukohde.getValintakokeet().size());
            assertEquals(valintakoekoodiUri, hakukohde.getValintakokeet().iterator().next().getUri());
        }
    }
}
