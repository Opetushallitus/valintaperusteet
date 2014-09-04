package fi.vm.sade.service.valintaperusteet.resource;

import fi.vm.sade.service.valintaperusteet.annotation.DataSetLocation;
import fi.vm.sade.service.valintaperusteet.dto.JarjestyskriteeriCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.JarjestyskriteeriDTO;
import fi.vm.sade.service.valintaperusteet.dto.JarjestyskriteeriInsertDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintatapajonoDTO;
import fi.vm.sade.service.valintaperusteet.listeners.ValinnatJTACleanInsertTestExecutionListener;
import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import fi.vm.sade.service.valintaperusteet.resource.impl.ValinnanVaiheResourceImpl;
import fi.vm.sade.service.valintaperusteet.resource.impl.ValintatapajonoResourceImpl;
import fi.vm.sade.service.valintaperusteet.service.exception.ValintatapajonoEiOleOlemassaException;
import fi.vm.sade.service.valintaperusteet.util.TestUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: jukais
 * Date: 23.1.2013
 * Time: 10.58
 * To change this template use File | Settings | File Templates.
 */
@ContextConfiguration(locations = "classpath:test-context.xml")
@TestExecutionListeners(listeners = {ValinnatJTACleanInsertTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class})
@RunWith(SpringJUnit4ClassRunner.class)
@DataSetLocation("classpath:test-data.xml")
public class ValintatapajonoResourceTest {
    private ValintatapajonoResourceImpl resource = new ValintatapajonoResourceImpl();
    private ValinnanVaiheResourceImpl vaiheResource = new ValinnanVaiheResourceImpl();

    @Autowired
    private ApplicationContext applicationContext;
    private TestUtil testUtil = new TestUtil(ValintatapajonoResourceImpl.class);

    @Before
    public void setUp() {
        applicationContext.getAutowireCapableBeanFactory().autowireBean(resource);
        applicationContext.getAutowireCapableBeanFactory().autowireBean(vaiheResource);
    }

    @Test
    public void testUpdate() throws Exception {
        ValintatapajonoDTO jono = resource.readByOid("1");
        jono.setNimi("muokattu");
        resource.update(jono.getOid(), jono);

        jono = resource.readByOid("1");
        assertEquals("muokattu", jono.getNimi());
        testUtil.lazyCheck(JsonViews.Basic.class, jono);
    }

    @Test
    public void testFindAll() throws Exception {
        List<ValintatapajonoDTO> jonos = resource.findAll();

        assertEquals(70, jonos.size());
        testUtil.lazyCheck(JsonViews.Basic.class, jonos);
    }

    @Test
    public void testJarjestykriteeri() throws Exception {
        List<JarjestyskriteeriDTO> jarjestyskriteeri = resource.findJarjestyskriteeri("6");

        testUtil.lazyCheck(JsonViews.Basic.class, jarjestyskriteeri, true);
        assertEquals(3, jarjestyskriteeri.size());

    }

    @Test(expected = RuntimeException.class)
    public void testDeleteOidNotFound() {
        resource.delete("");
    }

    @Test(expected = RuntimeException.class)
    public void testDeleteInherited() {
        // objekti on peritty
        resource.delete("27");
    }

    @Test
    public void testChildrenAreDeleted() {
        ValintatapajonoDTO valintatapajono = resource.readByOid("30");
        assertNotNull(valintatapajono);
        // objekti on peritty
        resource.delete("26");
        try {
            valintatapajono = resource.readByOid("30");
            assertNull(valintatapajono);
        } catch (ValintatapajonoEiOleOlemassaException e) {

        }

    }

    @Test
    public void testDelete() {
        Response delete = resource.delete("12");
        assertEquals(Response.Status.ACCEPTED.getStatusCode(), delete.getStatus());
    }

    @Test
    public void testInsertJK() throws Exception {
        JarjestyskriteeriCreateDTO jk = new JarjestyskriteeriCreateDTO();
        jk.setMetatiedot("mt1");
        jk.setAktiivinen(true);

        JarjestyskriteeriInsertDTO comb = new JarjestyskriteeriInsertDTO();
        comb.setJarjestyskriteeri(jk);
        comb.setLaskentakaavaId(1L);
        Response insert = resource.insertJarjestyskriteeri("1", comb);
        assertEquals(Response.Status.ACCEPTED.getStatusCode(), insert.getStatus());

        JarjestyskriteeriDTO entity = (JarjestyskriteeriDTO) insert.getEntity();

        testUtil.lazyCheck(JsonViews.Basic.class, entity, true);

    }


    @Test
    public void testJarjesta() {
        List<ValintatapajonoDTO> valintatapajonoList = vaiheResource.listJonos("1");
        List<String> oids = new ArrayList<String>();

        for (ValintatapajonoDTO valintatapajono : valintatapajonoList) {
            oids.add(valintatapajono.getOid());
        }

        assertEquals("1", oids.get(0));
        assertEquals("5", oids.get(4));
        Collections.reverse(oids);

        List<ValintatapajonoDTO> jarjesta = resource.jarjesta(oids);
        assertEquals("5", jarjesta.get(0).getOid());
        assertEquals("1", jarjesta.get(4).getOid());

        jarjesta = vaiheResource.listJonos("1");
        assertEquals("5", jarjesta.get(0).getOid());
        assertEquals("1", jarjesta.get(4).getOid());
    }
}
