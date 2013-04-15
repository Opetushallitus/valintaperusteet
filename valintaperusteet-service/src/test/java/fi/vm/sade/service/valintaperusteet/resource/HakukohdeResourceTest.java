package fi.vm.sade.service.valintaperusteet.resource;

import fi.vm.sade.dbunit.annotation.DataSetLocation;
import fi.vm.sade.dbunit.listener.JTACleanInsertTestExecutionListener;
import fi.vm.sade.service.valintaperusteet.dao.ValinnanVaiheDAO;
import fi.vm.sade.service.valintaperusteet.dto.HakukohdeViiteDTO;
import fi.vm.sade.service.valintaperusteet.model.*;
import fi.vm.sade.service.valintaperusteet.util.TestUtil;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jettison.json.JSONObject;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: jukais
 * Date: 16.1.2013
 * Time: 14.15
 * To change this template use File | Settings | File Templates.
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
        HakukohdeViite vroid1 = hakukohdeResource.queryFull("oid1");
        assertEquals("haku1", vroid1.getNimi());
        testUtil.lazyCheck(JsonViews.Basic.class, vroid1);
    }

    @Test
    public void testFindAll() throws Exception {
        List<HakukohdeViite> hakukohdeViites = hakukohdeResource.query(false);
        assertEquals(17, hakukohdeViites.size());
        testUtil.lazyCheck(JsonViews.Basic.class, hakukohdeViites);
    }

    @Test
    public void testFindRoot() throws Exception {
        List<HakukohdeViite> hakukohdeViites = hakukohdeResource.query(true);
        assertEquals(8, hakukohdeViites.size());
        testUtil.lazyCheck(JsonViews.Basic.class, hakukohdeViites);
    }

    @Test
    public void testInsertNull() throws Exception {
        HakukohdeViiteDTO valintaryhma = new HakukohdeViiteDTO();
        Response insert = hakukohdeResource.insert(valintaryhma);
        assertEquals(500, insert.getStatus());

    }

    @Test
    public void testInsert() throws Exception {
        HakukohdeViiteDTO hakukohdeDTO = new HakukohdeViiteDTO();
        hakukohdeDTO.setNimi("Uusi valintaryhmä");
        hakukohdeDTO.setOid("uusi oid");
        hakukohdeDTO.setHakuoid("hakuoid");
        hakukohdeDTO.setValintaryhmaOid("oid1");

        Set<String> vvOids = new HashSet<String>();
        vvOids.add("1");
        hakukohdeDTO.setValinnanvaiheetOids(vvOids);
        Response insert = hakukohdeResource.insert(hakukohdeDTO);
        assertEquals(201, insert.getStatus());

        testUtil.lazyCheck(JsonViews.Basic.class, insert.getEntity());
    }

    @Test
    public void testInsertRoot() throws Exception {
        HakukohdeViiteDTO hakukohdeDto = new HakukohdeViiteDTO();
        hakukohdeDto.setNimi("Uusi valintaryhmä");
        hakukohdeDto.setOid("uusi oid");
        hakukohdeDto.setHakuoid("hakuoid");

        Response insert = hakukohdeResource.insert(hakukohdeDto);
        assertEquals(201, insert.getStatus());

        testUtil.lazyCheck(JsonViews.Basic.class, insert.getEntity());
    }

    @Test
    public void testInsertDuplicate() throws Exception {
        HakukohdeViiteDTO valintaryhma = new HakukohdeViiteDTO();
        valintaryhma.setNimi("Uusi valintaryhmä");
        valintaryhma.setOid("oid1");
        Response insert = hakukohdeResource.insert(valintaryhma);
        assertEquals(500, insert.getStatus());
        testUtil.lazyCheck(JsonViews.Basic.class, insert.getEntity());
    }

    @Test
    public void testUpdate() throws Exception {
        HakukohdeViite hkv = hakukohdeResource.queryFull("oid1");
        hkv.setNimi("muokattu");

        ObjectMapper mapper = testUtil.getObjectMapper();

        final String json = mapper.writerWithView(JsonViews.Basic.class).writeValueAsString(hkv);
        HakukohdeViite fromJson = mapper.readValue(json, HakukohdeViite.class);

        hakukohdeResource.update(hkv.getOid(), fromJson);

        hkv = hakukohdeResource.queryFull("oid1");
        assertEquals("muokattu" , hkv.getNimi());
        testUtil.lazyCheck(JsonViews.Basic.class, hkv);
    }

    @Test
    public void testValinnanVaihesForHakukohde() throws Exception {
        List<ValinnanVaihe> valinnanVaihes = hakukohdeResource.valinnanVaihesForHakukohde("oid6");
        assertEquals(3 , valinnanVaihes.size());
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
        ArrayList<String> oids = new ArrayList<String>();

        JSONObject avaimet = hakukohdeResource.findAvaimet(oids);
        oids.add("oid1");
        oids.add("oid2");
        oids.add("oid6");
        avaimet = hakukohdeResource.findAvaimet(oids);
        System.out.println(avaimet.toString());
        assertEquals(2, avaimet.length());
    }



    @Test
    public void testInsertValinnanVaihe() throws Exception {
        ValinnanVaihe valinnanVaihe = new ValinnanVaihe();

        valinnanVaihe.setNimi("uusi");
        valinnanVaihe.setAktiivinen(true);
        valinnanVaihe.setValinnanVaiheTyyppi(ValinnanVaiheTyyppi.TAVALLINEN);

        Response response = hakukohdeResource.insertValinnanvaihe("oid1", null, valinnanVaihe);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        ValinnanVaihe vv = (ValinnanVaihe)response.getEntity();

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
}
