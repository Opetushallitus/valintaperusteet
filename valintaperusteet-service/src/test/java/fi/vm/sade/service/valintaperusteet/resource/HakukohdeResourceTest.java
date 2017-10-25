package fi.vm.sade.service.valintaperusteet.resource;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.vm.sade.service.valintaperusteet.ObjectMapperProvider;
import fi.vm.sade.service.valintaperusteet.annotation.DataSetLocation;
import fi.vm.sade.service.valintaperusteet.dao.ValinnanVaiheDAO;
import fi.vm.sade.service.valintaperusteet.dto.*;
import fi.vm.sade.service.valintaperusteet.listeners.ValinnatJTACleanInsertTestExecutionListener;
import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import fi.vm.sade.service.valintaperusteet.resource.impl.HakukohdeResourceImpl;
import fi.vm.sade.service.valintaperusteet.util.TestUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import javax.ws.rs.WebApplicationException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.util.List;

import static org.junit.Assert.*;

/**
 * User: jukais Date: 16.1.2013 Time: 14.15
 */
@ContextConfiguration(locations = "classpath:test-context.xml")
@TestExecutionListeners(listeners = { ValinnatJTACleanInsertTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class })
@RunWith(SpringJUnit4ClassRunner.class)
@DataSetLocation("classpath:test-data.xml")
public class HakukohdeResourceTest {
    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);


    private HakukohdeResourceImpl hakukohdeResource = new HakukohdeResourceImpl();
    private TestUtil testUtil = new TestUtil(HakukohdeResourceTest.class);

    private ObjectMapper mapper = new ObjectMapperProvider().getContext(HakukohdeResourceImpl.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    ValinnanVaiheDAO valinnanVaiheDao;

    @Before
    public void setUp() {
        applicationContext.getAutowireCapableBeanFactory().autowireBean(hakukohdeResource);
    }

    @Test
    public void testFindByOid() throws Exception {
        HakukohdeViiteDTO vroid1 = hakukohdeResource.queryFull("oid1");
        assertEquals("haku1", vroid1.getNimi());
        testUtil.lazyCheck(JsonViews.Basic.class, vroid1);
    }

    @Test
    public void testFindAll() throws Exception {
        List<HakukohdeViiteDTO> hakukohdeViites = hakukohdeResource.query(false);
        assertEquals(32, hakukohdeViites.size());
        testUtil.lazyCheck(JsonViews.Basic.class, hakukohdeViites);
    }

    @Test
    public void testFindRoot() throws Exception {
        List<HakukohdeViiteDTO> hakukohdeViites = hakukohdeResource.query(true);
        assertEquals(17, hakukohdeViites.size());
        testUtil.lazyCheck(JsonViews.Basic.class, hakukohdeViites);
    }

    @Test
    public void testInsertNull() throws Exception {
        HakukohdeViiteDTO valintaryhma = new HakukohdeViiteDTO();
        Response insert = hakukohdeResource.insert(new HakukohdeInsertDTO(valintaryhma, null), request);
        assertEquals(500, insert.getStatus());

    }

    @Test
    public void testInsert() throws Exception {
        HakukohdeViiteCreateDTO hakukohdeDTO = new HakukohdeViiteDTO();
        hakukohdeDTO.setNimi("Uusi valintaryhmä");
        hakukohdeDTO.setOid("uusi oid");
        hakukohdeDTO.setHakuoid("hakuoid");

        Response insert = hakukohdeResource.insert(new HakukohdeInsertDTO(hakukohdeDTO, "oid1"), request);
        assertEquals(201, insert.getStatus());

        testUtil.lazyCheck(JsonViews.Basic.class, insert.getEntity());
    }

    @Test
    public void testInsertRoot() throws Exception {
        HakukohdeViiteDTO hakukohdeDto = new HakukohdeViiteDTO();
        hakukohdeDto.setNimi("Uusi valintaryhmä");
        hakukohdeDto.setOid("uusi oid");
        hakukohdeDto.setHakuoid("hakuoid");

        Response insert = hakukohdeResource.insert(new HakukohdeInsertDTO(hakukohdeDto, null), request);
        assertEquals(201, insert.getStatus());

        testUtil.lazyCheck(JsonViews.Basic.class, insert.getEntity());
    }

    @Test
    public void testInsertDuplicate() throws Exception {
        HakukohdeViiteDTO hakukohde = new HakukohdeViiteDTO();
        hakukohde.setNimi("Uusi valintaryhmä");
        hakukohde.setOid("oid1");
        Response insert = hakukohdeResource.insert(new HakukohdeInsertDTO(hakukohde, null), request);
        assertEquals(500, insert.getStatus());
        testUtil.lazyCheck(JsonViews.Basic.class, insert.getEntity());
    }

    @Test
    public void getValintakoesForHakukohdeReturns404IfViiteNotFound() {
        try {
            hakukohdeResource.valintakoesForHakukohde("IMAGINARY_HAKUKOHDE_OID");
            fail("Should not reach here. Expected to throw exception");
        } catch (WebApplicationException e) {
            assertEquals("HakukohdeViite (IMAGINARY_HAKUKOHDE_OID) ei ole olemassa.", e.getMessage());
            assertEquals(404, e.getResponse().getStatus());
        }
    }

    @Test
    public void testUpdate() throws Exception {
        HakukohdeViiteDTO hkv = hakukohdeResource.queryFull("oid1");
        hkv.setNimi("muokattu");

        ObjectMapper mapper = testUtil.getObjectMapper();

        final String json = mapper.writerWithView(JsonViews.Basic.class).writeValueAsString(hkv);
        HakukohdeViiteCreateDTO fromJson = mapper.readValue(json, HakukohdeViiteCreateDTO.class);

        hakukohdeResource.update(hkv.getOid(), fromJson, request);

        hkv = hakukohdeResource.queryFull("oid1");
        assertEquals("muokattu", hkv.getNimi());
        testUtil.lazyCheck(JsonViews.Basic.class, hkv);
    }

    @Test
    public void testValinnanVaihesForHakukohde() throws Exception {
        List<ValinnanVaiheDTO> valinnanVaihes = hakukohdeResource.valinnanVaihesForHakukohde("oid6", "false");
        assertEquals(3, valinnanVaihes.size());
        testUtil.lazyCheck(JsonViews.Basic.class, valinnanVaihes);

    }

    @Test
    public void testValinnanVaihesForHakukohdeWithValisijoittelutieto() throws Exception {
        List<ValinnanVaiheDTO> valinnanVaihes = hakukohdeResource.valinnanVaihesForHakukohde("oid6", "true");
        assertEquals(3, valinnanVaihes.size());
        assertTrue(valinnanVaihes.get(0).getHasValisijoittelu());
        assertFalse(valinnanVaihes.get(1).getHasValisijoittelu());
        assertFalse(valinnanVaihes.get(2).getHasValisijoittelu());
        testUtil.lazyCheck(JsonViews.Basic.class, valinnanVaihes);
    }

    @Test
    public void testFindLaskentakaavatByHakukohde() throws Exception {
        List<JarjestyskriteeriDTO> laskentaKaavat = hakukohdeResource.findLaskentaKaavat("oid6");
        assertEquals(3, laskentaKaavat.size());
        testUtil.lazyCheck(JsonViews.Basic.class, laskentaKaavat);
    }

    @Test
    public void testFindAvaimet() throws Exception {
        List<ValintaperusteDTO> valintaperusteet = hakukohdeResource.findAvaimet("oid17");
        assertEquals(2, valintaperusteet.size());

        mapper.writerWithView(JsonViews.Basic.class).writeValueAsString(valintaperusteet);
    }

    @Test
    public void testInsertValinnanVaihe() throws Exception {
        ValinnanVaiheCreateDTO valinnanVaihe = new ValinnanVaiheCreateDTO();

        valinnanVaihe.setNimi("uusi");
        valinnanVaihe.setAktiivinen(true);
        valinnanVaihe
                .setValinnanVaiheTyyppi(fi.vm.sade.service.valintaperusteet.dto.model.ValinnanVaiheTyyppi.TAVALLINEN);

        Response response = hakukohdeResource.insertValinnanvaihe("oid1", null, valinnanVaihe, request);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        ValinnanVaiheDTO vv = (ValinnanVaiheDTO) response.getEntity();

        valinnanVaihe = new ValinnanVaiheCreateDTO();
        valinnanVaihe.setNimi("uusi");
        valinnanVaihe.setAktiivinen(true);
        valinnanVaihe
                .setValinnanVaiheTyyppi(fi.vm.sade.service.valintaperusteet.dto.model.ValinnanVaiheTyyppi.TAVALLINEN);

        response = hakukohdeResource.insertValinnanvaihe("oid1", vv.getOid(), valinnanVaihe, request);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());

    }

    @Test
    public void testKuuluuSijoitteluun() {
        boolean oid;
        oid = hakukohdeResource.kuuluuSijoitteluun("3401").get("sijoitteluun");
        assertEquals(false, oid);

        oid = hakukohdeResource.kuuluuSijoitteluun("3501").get("sijoitteluun");
        assertEquals(false, oid);

        oid = hakukohdeResource.kuuluuSijoitteluun("3601").get("sijoitteluun");
        assertEquals(true, oid);

        oid = hakukohdeResource.kuuluuSijoitteluun("3701").get("sijoitteluun");
        assertEquals(false, oid);
    }

    @Test
    public void testInsertAndUpdateHakukohdekoodi() {
        final String URI = "uri";
        final String ARVO = "arvo";
        KoodiDTO hakukohdekoodi = new KoodiDTO();
        hakukohdekoodi.setUri(URI);
        hakukohdekoodi.setArvo(ARVO);
        Response response = hakukohdeResource.insertHakukohdekoodi("oid1", hakukohdekoodi, request);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());

        HakukohdeViiteDTO oid1 = hakukohdeResource.queryFull("oid1");
        KoodiDTO hakukohdekoodi1 = oid1.getHakukohdekoodi();
        assertEquals(ARVO, hakukohdekoodi1.getArvo());
        assertEquals(URI, hakukohdekoodi1.getUri());

        // update
        final String URI2 = "uri2";

        KoodiDTO uusikoodi = new KoodiDTO();
        uusikoodi.setUri(URI2);
        uusikoodi.setArvo(ARVO);

        response = hakukohdeResource.updateHakukohdekoodi("oid1", uusikoodi, request);
        assertEquals(Response.Status.ACCEPTED.getStatusCode(), response.getStatus());

        oid1 = hakukohdeResource.queryFull("oid1");
        assertEquals(ARVO, oid1.getHakukohdekoodi().getArvo());
        assertEquals(URI2, oid1.getHakukohdekoodi().getUri());
    }

    @Test
    public void testSiirraHakukohdeValintaryhmaan() {
        final String valintaryhmaOid = "oid54";
        final String hakukohdeOid = "oid18";

        Response response = hakukohdeResource.siirraHakukohdeValintaryhmaan(hakukohdeOid, valintaryhmaOid, request);
        assertEquals(Response.Status.ACCEPTED.getStatusCode(), response.getStatus());

        HakukohdeViiteDTO hakukohde = (HakukohdeViiteDTO) response.getEntity();
        assertEquals(valintaryhmaOid, hakukohde.getValintaryhmaOid());
    }
}
