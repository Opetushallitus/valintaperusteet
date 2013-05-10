package fi.vm.sade.service.valintaperusteet.dao;

import fi.vm.sade.dbunit.annotation.DataSetLocation;
import fi.vm.sade.dbunit.listener.JTACleanInsertTestExecutionListener;
import fi.vm.sade.service.valintaperusteet.model.ValinnanVaihe;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static junit.framework.Assert.assertEquals;


/**
 * User: kwuoti
 * Date: 18.1.2013
 * Time: 10.04
 */
@ContextConfiguration(locations = "classpath:test-context.xml")
@TestExecutionListeners(listeners = {JTACleanInsertTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@DataSetLocation("classpath:test-data.xml")
public class ValinnanVaiheDAOTest {

    @Autowired
    private ValinnanVaiheDAO valinnanVaiheDAO;


    @Test
    public void testHaeValintaryhmanViimeinenValinnanVaihe() {

        final String valintaryhmaOid = "oid6";
        ValinnanVaihe valinnanVaihe = valinnanVaiheDAO.haeValintaryhmanViimeinenValinnanVaihe(valintaryhmaOid);
        int i = 0;
        while(valinnanVaihe != null) {
            ++i;
            assertEquals(valintaryhmaOid, valinnanVaihe.getValintaryhma().getOid());
            valinnanVaihe = valinnanVaihe.getEdellinenValinnanVaihe();
        }

        assertEquals(5, i);
    }

    @Test
    public void testFindByValintaryhma() {
        List<ValinnanVaihe> oid1 = valinnanVaiheDAO.findByValintaryhma("oid2");
        assertEquals(1, oid1.size());
    }

    @Test
    public void testFindValinnanvaihe() {
        ValinnanVaihe valinnanVaihe = valinnanVaiheDAO.readByOid("32");
        assertEquals(valinnanVaihe.getMaster().getId().longValue(), 20L);
    }

    @Test
    public void testFindByHakukohde() {
        final String hakukohdeOid = "oid12";
        List<ValinnanVaihe> vaiheet = valinnanVaiheDAO.findByHakukohde(hakukohdeOid);
        assertEquals(2, vaiheet.size());
    }

}
