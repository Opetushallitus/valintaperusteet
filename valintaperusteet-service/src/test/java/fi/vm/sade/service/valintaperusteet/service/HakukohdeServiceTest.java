package fi.vm.sade.service.valintaperusteet.service;

import fi.vm.sade.dbunit.annotation.DataSetLocation;
import fi.vm.sade.dbunit.listener.JTACleanInsertTestExecutionListener;
import fi.vm.sade.service.valintaperusteet.dao.ValinnanVaiheDAO;
import fi.vm.sade.service.valintaperusteet.dao.ValintaryhmaDAO;
import fi.vm.sade.service.valintaperusteet.dao.ValintatapajonoDAO;
import fi.vm.sade.service.valintaperusteet.model.HakukohdeViite;
import fi.vm.sade.service.valintaperusteet.model.ValinnanVaihe;
import fi.vm.sade.service.valintaperusteet.model.Valintatapajono;
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
@TestExecutionListeners(listeners = {JTACleanInsertTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class})
@RunWith(SpringJUnit4ClassRunner.class)
@DataSetLocation("classpath:test-data.xml")
public class HakukohdeServiceTest {

    @Autowired
    private ValintaryhmaDAO valintaryhmaDAO;

    @Autowired
    private ValintatapajonoDAO valintatapajonoDAO;

    @Autowired
    private ValinnanVaiheDAO valinnanVaiheDAO;

    @Autowired
    private HakukohdeService hakukohdeService;

    @Test
    public void testInsert() {
        final String parentOid = "oid33";
        {
            assertNotNull(valintaryhmaDAO.readByOid(parentOid));
            List<ValinnanVaihe> vr33Lvaiheet = LinkitettavaJaKopioitavaUtil.jarjesta(valinnanVaiheDAO.findByValintaryhma(parentOid));

            assertEquals(2, vr33Lvaiheet.size());
            ValinnanVaihe vaihe80L = vr33Lvaiheet.get(0);
            ValinnanVaihe vaihe81L = vr33Lvaiheet.get(1);

            List<Valintatapajono> vaihe80Ljonot = LinkitettavaJaKopioitavaUtil.jarjesta(valintatapajonoDAO.findByValinnanVaihe(vaihe80L.getOid()));
            List<Valintatapajono> vaihe81Ljonot = LinkitettavaJaKopioitavaUtil.jarjesta(valintatapajonoDAO.findByValinnanVaihe(vaihe81L.getOid()));

            assertEquals(2, vaihe80Ljonot.size());
            assertEquals(1, vaihe81Ljonot.size());
        }

        HakukohdeViite uusiHakukohde = new HakukohdeViite();
        uusiHakukohde.setNimi("Uusi hakukohde");
        uusiHakukohde.setOid("oid1234567");
        uusiHakukohde.setHakuoid("uusihakuoid");

        HakukohdeViite lisatty = hakukohdeService.insert(uusiHakukohde, parentOid);
        assertTrue(StringUtils.isNotBlank(lisatty.getOid()));

        {
            assertNotNull(valintaryhmaDAO.readByOid(parentOid));
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
        HakukohdeViite uusiHakukohde = new HakukohdeViite();
        uusiHakukohde.setNimi("Uusi hakukohde");
        uusiHakukohde.setOid("oid1234567");
        uusiHakukohde.setHakuoid("uusihakuoid");

        HakukohdeViite lisatty = hakukohdeService.insert(uusiHakukohde, null);
        assertNotNull(hakukohdeService.readByOid(lisatty.getOid()));

    }
}
