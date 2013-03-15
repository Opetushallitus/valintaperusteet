package fi.vm.sade.service.valintaperusteet.service;

import fi.vm.sade.dbunit.annotation.DataSetLocation;
import fi.vm.sade.dbunit.listener.JTACleanInsertTestExecutionListener;
import fi.vm.sade.service.valintaperusteet.dao.ValinnanVaiheDAO;
import fi.vm.sade.service.valintaperusteet.dao.ValintaryhmaDAO;
import fi.vm.sade.service.valintaperusteet.dao.ValintatapajonoDAO;
import fi.vm.sade.service.valintaperusteet.model.ValinnanVaihe;
import fi.vm.sade.service.valintaperusteet.model.Valintaryhma;
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
@DataSetLocation("classpath:test-data.xml")
public class ValintaryhmaServiceTest {

    @Autowired
    private ValintaryhmaService valintaryhmaService;

    @Autowired
    private ValintaryhmaDAO valintaryhmaDAO;

    @Autowired
    private ValinnanVaiheDAO valinnanVaiheDAO;

    @Autowired
    private ValintatapajonoDAO valintatapajonoDAO;

    @Test
    public void testInsertChild() {
        final String parentOid = "oid6";
        final int valinnanVaiheetLkm = 5;

        Valintaryhma uusiValintaryhma = new Valintaryhma();
        uusiValintaryhma.setOid("uusi oid");
        uusiValintaryhma.setNimi("uusi valintaryhma");
        uusiValintaryhma.setHakuOid("hakuoid");

        Valintaryhma lisatty = valintaryhmaService.insert(uusiValintaryhma, parentOid);
        List<ValinnanVaihe> valinnanVaiheet = LinkitettavaJaKopioitavaUtil.jarjesta(valinnanVaiheDAO.findByValintaryhma(lisatty.getOid()));
        assertEquals(valinnanVaiheetLkm, valinnanVaiheet.size());

        assertEquals(10L, valinnanVaiheet.get(0).getMasterValinnanVaihe().getId().longValue());
        assertEquals(11L, valinnanVaiheet.get(1).getMasterValinnanVaihe().getId().longValue());
        assertEquals(12L, valinnanVaiheet.get(2).getMasterValinnanVaihe().getId().longValue());
        assertEquals(13L, valinnanVaiheet.get(3).getMasterValinnanVaihe().getId().longValue());
        assertEquals(14L, valinnanVaiheet.get(4).getMasterValinnanVaihe().getId().longValue());
    }

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

        Valintaryhma uusiValintaryhma = new Valintaryhma();
        uusiValintaryhma.setHakuOid("hakuoid");
        uusiValintaryhma.setNimi("uusi nimi");

        Valintaryhma lisatty = valintaryhmaService.insert(uusiValintaryhma, parentOid);
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
            assertNotNull(valintaryhmaService.readByOid(lisatty.getOid()));
            List<ValinnanVaihe> uusiVaiheet = LinkitettavaJaKopioitavaUtil.jarjesta(valinnanVaiheDAO.findByValintaryhma(lisatty.getOid()));

            assertEquals(2, uusiVaiheet.size());
            ValinnanVaihe uusiVaihe1 = uusiVaiheet.get(0);
            ValinnanVaihe uusiVaihe2 = uusiVaiheet.get(1);

            List<Valintatapajono> uusiVaihe1jonot = LinkitettavaJaKopioitavaUtil.jarjesta(valintatapajonoDAO.findByValinnanVaihe(uusiVaihe1.getOid()));
            List<Valintatapajono> uusiVaihe2jonot = LinkitettavaJaKopioitavaUtil.jarjesta(valintatapajonoDAO.findByValinnanVaihe(uusiVaihe2.getOid()));

            assertEquals(2, uusiVaihe1jonot.size());
            assertEquals(1, uusiVaihe2jonot.size());
        }
    }
}
