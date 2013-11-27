package fi.vm.sade.service.valintaperusteet.resource;

import fi.vm.sade.dbunit.annotation.DataSetLocation;
import fi.vm.sade.dbunit.listener.JTACleanInsertTestExecutionListener;
import fi.vm.sade.service.valintaperusteet.ObjectMapperProvider;
import fi.vm.sade.service.valintaperusteet.dao.ValinnanVaiheDAO;
import fi.vm.sade.service.valintaperusteet.dto.*;
import fi.vm.sade.service.valintaperusteet.model.*;
import fi.vm.sade.service.valintaperusteet.util.TestUtil;
import org.codehaus.jackson.map.ObjectMapper;
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
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * User: jukais
 * Date: 16.1.2013
 * Time: 14.15
 */
@ContextConfiguration(locations = "classpath:test-context.xml")
@TestExecutionListeners(listeners = {JTACleanInsertTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class})
@RunWith(SpringJUnit4ClassRunner.class)
@DataSetLocation("classpath:test-data.xml")
public class HakukohdeResourceTest {

    private HakukohdeResource hakukohdeResource = new HakukohdeResource();
    private TestUtil testUtil = new TestUtil(HakukohdeResourceTest.class);

    private ObjectMapper mapper = new ObjectMapperProvider().getContext(HakukohdeResource.class);


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
        assertEquals(30, hakukohdeViites.size());
        testUtil.lazyCheck(JsonViews.Basic.class, hakukohdeViites);
    }

    @Test
    public void testFindRoot() throws Exception {
        List<HakukohdeViiteDTO> hakukohdeViites = hakukohdeResource.query(true);
        assertEquals(16, hakukohdeViites.size());
        testUtil.lazyCheck(JsonViews.Basic.class, hakukohdeViites);
    }

    @Test
    public void testInsertNull() throws Exception {
        HakukohdeViiteDTO valintaryhma = new HakukohdeViiteDTO();
        Response insert = hakukohdeResource.insert(valintaryhma, null);
        assertEquals(500, insert.getStatus());

    }

    @Test
    public void testInsert() throws Exception {
        HakukohdeViiteCreateDTO hakukohdeDTO = new HakukohdeViiteDTO();
        hakukohdeDTO.setNimi("Uusi valintaryhmä");
        hakukohdeDTO.setOid("uusi oid");
        hakukohdeDTO.setHakuoid("hakuoid");

        Response insert = hakukohdeResource.insert(hakukohdeDTO, "oid1");
        assertEquals(201, insert.getStatus());

        testUtil.lazyCheck(JsonViews.Basic.class, insert.getEntity());
    }

    @Test
    public void testInsertRoot() throws Exception {
        HakukohdeViiteDTO hakukohdeDto = new HakukohdeViiteDTO();
        hakukohdeDto.setNimi("Uusi valintaryhmä");
        hakukohdeDto.setOid("uusi oid");
        hakukohdeDto.setHakuoid("hakuoid");

        Response insert = hakukohdeResource.insert(hakukohdeDto, null);
        assertEquals(201, insert.getStatus());

        testUtil.lazyCheck(JsonViews.Basic.class, insert.getEntity());
    }

    @Test
    public void testInsertDuplicate() throws Exception {
        HakukohdeViiteDTO hakukohde = new HakukohdeViiteDTO();
        hakukohde.setNimi("Uusi valintaryhmä");
        hakukohde.setOid("oid1");
        Response insert = hakukohdeResource.insert(hakukohde, null);
        assertEquals(500, insert.getStatus());
        testUtil.lazyCheck(JsonViews.Basic.class, insert.getEntity());
    }

    @Test
    public void testUpdate() throws Exception {
        HakukohdeViiteDTO hkv = hakukohdeResource.queryFull("oid1");
        hkv.setNimi("muokattu");

        ObjectMapper mapper = testUtil.getObjectMapper();

        final String json = mapper.writerWithView(JsonViews.Basic.class).writeValueAsString(hkv);
        HakukohdeViiteCreateDTO fromJson = mapper.readValue(json, HakukohdeViiteCreateDTO.class);

        hakukohdeResource.update(hkv.getOid(), fromJson);

        hkv = hakukohdeResource.queryFull("oid1");
        assertEquals("muokattu", hkv.getNimi());
        testUtil.lazyCheck(JsonViews.Basic.class, hkv);
    }

    @Test
    public void testValinnanVaihesForHakukohde() throws Exception {
        List<ValinnanVaiheDTO> valinnanVaihes = hakukohdeResource.valinnanVaihesForHakukohde("oid6");
        assertEquals(3, valinnanVaihes.size());
        testUtil.lazyCheck(JsonViews.Basic.class, valinnanVaihes);

    }

    @Test
    public void testFindLaskentakaavatByHakukohde() throws Exception {
        List<Jarjestyskriteeri> laskentaKaavat = hakukohdeResource.findLaskentaKaavat("oid6");
        assertEquals(3, laskentaKaavat.size());
        testUtil.lazyCheck(JsonViews.Basic.class, laskentaKaavat);
    }

    @Test
    public void testFindAvaimet() throws Exception {
        final List<String> oids = Arrays.asList("oid17");

        List<ValintaperusteDTO> valintaperusteet = hakukohdeResource.findAvaimet(oids);
        assertEquals(2, valintaperusteet.size());

        mapper.writerWithView(JsonViews.Basic.class).writeValueAsString(valintaperusteet);
    }


    @Test
    public void testInsertValinnanVaihe() throws Exception {
        ValinnanVaihe valinnanVaihe = new ValinnanVaihe();

        valinnanVaihe.setNimi("uusi");
        valinnanVaihe.setAktiivinen(true);
        valinnanVaihe.setValinnanVaiheTyyppi(ValinnanVaiheTyyppi.TAVALLINEN);

        Response response = hakukohdeResource.insertValinnanvaihe("oid1", null, valinnanVaihe);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        ValinnanVaihe vv = (ValinnanVaihe) response.getEntity();

        valinnanVaihe = new ValinnanVaihe();
        valinnanVaihe.setNimi("uusi");
        valinnanVaihe.setAktiivinen(true);
        valinnanVaihe.setValinnanVaiheTyyppi(ValinnanVaiheTyyppi.TAVALLINEN);

        response = hakukohdeResource.insertValinnanvaihe("oid1", vv.getOid(), valinnanVaihe);
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
        Hakukohdekoodi hakukohdekoodi = new Hakukohdekoodi();
        hakukohdekoodi.setUri(URI);
        hakukohdekoodi.setArvo(ARVO);
        Response response = hakukohdeResource.insertHakukohdekoodi("oid1", hakukohdekoodi);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());

        HakukohdeViiteDTO oid1 = hakukohdeResource.queryFull("oid1");
        KoodiDTO hakukohdekoodi1 = oid1.getHakukohdekoodi();
        assertEquals(ARVO, hakukohdekoodi1.getArvo());
        assertEquals(URI, hakukohdekoodi1.getUri());

        // update
        final String URI2 = "uri2";

        Hakukohdekoodi uusikoodi = new Hakukohdekoodi();
        uusikoodi.setUri(URI2);
        uusikoodi.setArvo(ARVO);

        response = hakukohdeResource.updateHakukohdekoodi("oid1", uusikoodi);
        assertEquals(Response.Status.ACCEPTED.getStatusCode(), response.getStatus());

        oid1 = hakukohdeResource.queryFull("oid1");
        assertEquals(ARVO, oid1.getHakukohdekoodi().getArvo());
        assertEquals(URI2, oid1.getHakukohdekoodi().getUri());
    }

    @Test
    public void testSiirraHakukohdeValintaryhmaan() {
        final String valintaryhmaOid = "oid54";
        final String hakukohdeOid = "oid18";

        Response response = hakukohdeResource.siirraHakukohdeValintaryhmaan(hakukohdeOid, valintaryhmaOid);
        assertEquals(Response.Status.ACCEPTED.getStatusCode(), response.getStatus());

        HakukohdeViite hakukohde = (HakukohdeViite) response.getEntity();
        assertEquals(hakukohde.getValintaryhma().getOid(), valintaryhmaOid);
    }
}
