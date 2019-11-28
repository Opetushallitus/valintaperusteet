package fi.vm.sade.service.valintaperusteet.resource;

import fi.vm.sade.service.valintaperusteet.annotation.DataSetLocation;
import fi.vm.sade.service.valintaperusteet.dto.*;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.listeners.ValinnatJTACleanInsertTestExecutionListener;
import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import fi.vm.sade.service.valintaperusteet.resource.impl.ValinnanVaiheResourceImpl;
import fi.vm.sade.service.valintaperusteet.resource.impl.ValintatapajonoResourceImpl;
import fi.vm.sade.service.valintaperusteet.service.exception.ValintatapajonoEiOleOlemassaException;
import fi.vm.sade.service.valintaperusteet.util.TestUtil;
import fi.vm.sade.service.valintaperusteet.util.VtsRestClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created with IntelliJ IDEA.
 * User: jukais
 * Date: 23.1.2013
 * Time: 10.58
 * To change this template use File | Settings | File Templates.
 */
@ContextConfiguration(classes = VtsRestClientConfig.class)
@TestExecutionListeners(listeners = {ValinnatJTACleanInsertTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class })
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
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpSession session = Mockito.mock(HttpSession.class);
        Mockito.when(request.getSession(false)).thenReturn(session);

        ValintatapajonoDTO jono = resource.readByOid("1");
        jono.setNimi("muokattu");
        jono.setTyyppi("valintatapajono_m");
        resource.update(jono.getOid(), jono, request);

        jono = resource.readByOid("1");
        assertEquals("muokattu", jono.getNimi());
        testUtil.lazyCheck(JsonViews.Basic.class, jono);
    }

    @Test
    public void testFindAll() throws Exception {
        List<ValintatapajonoDTO> jonos = resource.findAll();

        assertEquals(73, jonos.size());
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

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        resource.delete("", request);
    }

    @Test
    public void testDeleteInherited() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpSession session = Mockito.mock(HttpSession.class);
        Mockito.when(request.getSession(false)).thenReturn(session);

        ValintatapajonoDTO valintatapajono = resource.readByOid("27");
        assertNotNull(valintatapajono);
        Response delete = resource.delete("27", request);
        assertEquals(Response.Status.ACCEPTED.getStatusCode(), delete.getStatus());
        try {
            valintatapajono = resource.readByOid("27");
            assertNull(valintatapajono);
        } catch (ValintatapajonoEiOleOlemassaException e) {

        }
    }

    @Test
    public void testChildrenAreDeleted() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpSession session = Mockito.mock(HttpSession.class);
        Mockito.when(request.getSession(false)).thenReturn(session);

        ValintatapajonoDTO valintatapajono = resource.readByOid("30");
        assertNotNull(valintatapajono);
        // objekti on peritty
        resource.delete("26", request);
        try {
            valintatapajono = resource.readByOid("30");
            assertNull(valintatapajono);
        } catch (ValintatapajonoEiOleOlemassaException e) {

        }

    }

    @Test
    public void testDelete() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpSession session = Mockito.mock(HttpSession.class);
        Mockito.when(request.getSession(false)).thenReturn(session);

        Response delete = resource.delete("12", request);
        assertEquals(Response.Status.ACCEPTED.getStatusCode(), delete.getStatus());
    }

    @Test
    public void testInsertJK() throws Exception {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpSession session = Mockito.mock(HttpSession.class);
        Mockito.when(request.getSession(false)).thenReturn(session);

        JarjestyskriteeriCreateDTO jk = new JarjestyskriteeriCreateDTO();
        jk.setMetatiedot("mt1");
        jk.setAktiivinen(true);

        JarjestyskriteeriInsertDTO comb = new JarjestyskriteeriInsertDTO();
        comb.setJarjestyskriteeri(jk);
        comb.setLaskentakaavaId(1L);
        Response insert = resource.insertJarjestyskriteeri("1", comb, request);
        assertEquals(Response.Status.ACCEPTED.getStatusCode(), insert.getStatus());

        JarjestyskriteeriDTO entity = (JarjestyskriteeriDTO) insert.getEntity();

        testUtil.lazyCheck(JsonViews.Basic.class, entity, true);

    }


    @Test
    public void testJarjesta() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpSession session = Mockito.mock(HttpSession.class);
        Mockito.when(request.getSession(false)).thenReturn(session);

        List<ValintatapajonoDTO> valintatapajonoList = vaiheResource.listJonos("1");
        List<String> oids = new ArrayList<String>();

        for (ValintatapajonoDTO valintatapajono : valintatapajonoList) {
            oids.add(valintatapajono.getOid());
        }

        assertEquals("1", oids.get(0));
        assertEquals("5", oids.get(4));
        Collections.reverse(oids);

        List<ValintatapajonoDTO> jarjesta = resource.jarjesta(oids, request);
        assertEquals("5", jarjesta.get(0).getOid());
        assertEquals("1", jarjesta.get(4).getOid());

        jarjesta = vaiheResource.listJonos("1");
        assertEquals("5", jarjesta.get(0).getOid());
        assertEquals("1", jarjesta.get(4).getOid());
    }


    @Test
    public void testPrioriteettiOfSingleJonoIsAlwaysZero() throws Exception {
        ValintaperusteetModelMapper modelMapper = new ValintaperusteetModelMapper();

        List<String> allOids = resource.findAll().stream().map(ValintatapajonoDTO::getOid).collect(Collectors.toList());
        allOids.stream().forEach(oid -> {
            ValintatapajonoDTO dto = modelMapper.map(resource.readByOid(oid), ValintatapajonoDTO.class);
            assertEquals(0, dto.getPrioriteetti());
        });
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
