package fi.vm.sade.service.valintaperusteet.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import com.fasterxml.jackson.databind.ObjectMapper;

import fi.vm.sade.service.valintaperusteet.ObjectMapperProvider;
import fi.vm.sade.service.valintaperusteet.annotation.DataSetLocation;
import fi.vm.sade.service.valintaperusteet.dto.ValinnanVaiheDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintakoeDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintatapajonoDTO;
import fi.vm.sade.service.valintaperusteet.dto.model.Koekutsu;
import fi.vm.sade.service.valintaperusteet.listeners.ValinnatJTACleanInsertTestExecutionListener;
import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import fi.vm.sade.service.valintaperusteet.resource.impl.HakukohdeResourceImpl;
import fi.vm.sade.service.valintaperusteet.resource.impl.ValinnanVaiheResourceImpl;
import fi.vm.sade.service.valintaperusteet.service.exception.ValinnanVaiheEiOleOlemassaException;
import org.junit.After;
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

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: tommiha Date: 1/23/13 Time: 1:03 PM
 */
@ContextConfiguration(locations = "classpath:test-context.xml")
@TestExecutionListeners(listeners = { ValinnatJTACleanInsertTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class })
@RunWith(SpringJUnit4ClassRunner.class)
@DataSetLocation("classpath:test-data.xml")
public class ValinnanVaiheResourceTest {

    private ObjectMapper mapper = new ObjectMapperProvider().getContext(ValinnanVaiheResourceImpl.class);
    private ValinnanVaiheResourceImpl vaiheResource = new ValinnanVaiheResourceImpl();
    private HakukohdeResourceImpl hakuResource = new HakukohdeResourceImpl();

    @Autowired
    private ApplicationContext applicationContext;

    @Before
    public void setUp() {
        applicationContext.getAutowireCapableBeanFactory().autowireBean(vaiheResource);
        applicationContext.getAutowireCapableBeanFactory().autowireBean(hakuResource);
    }

    @After
    public void tearDown() {

    }

    @Test
    public void testRead() throws IOException {
        ValinnanVaiheDTO vaihe = vaiheResource.read("1");
        mapper.writerWithView(JsonViews.Basic.class).writeValueAsString(vaihe);
    }

    @Test
    public void testQuery() throws IOException {
        List<ValintatapajonoDTO> jonos = vaiheResource.listJonos("1");

        mapper.writerWithView(JsonViews.Basic.class).writeValueAsString(jonos);
    }

    @Test
    public void testUpdate() throws IOException {
        ValinnanVaiheDTO vaihe = vaiheResource.read("1");

        ValinnanVaiheDTO vaihe1 = vaiheResource.update(vaihe.getOid(), vaihe);
        mapper.writerWithView(JsonViews.Basic.class).writeValueAsString(vaihe1);
    }

    @Test
    public void testInsertValintatapajono() {
        ValintatapajonoDTO jono = new ValintatapajonoDTO();
        Response insert = vaiheResource.addJonoToValinnanVaihe("1", jono);
        assertEquals(500, insert.getStatus());

        jono = newJono();
        insert = vaiheResource.addJonoToValinnanVaihe("1", jono);
        assertEquals(201, insert.getStatus());
    }

    @Test
    public void testInsertValintakoe() {
        final String valinnanVaiheOid = "83";
        final Long laskentakaavaId = 101L;

        ValintakoeDTO valintakoe = new ValintakoeDTO();
        valintakoe.setTunniste("tunniste");
        valintakoe.setNimi("nimi");
        valintakoe.setAktiivinen(true);
        valintakoe.setLaskentakaavaId(laskentakaavaId);
        valintakoe.setLahetetaankoKoekutsut(true);
        valintakoe.setKutsutaankoKaikki(false);
        valintakoe.setKutsunKohde(Koekutsu.YLIN_TOIVE);

        Response response = vaiheResource.addValintakoeToValinnanVaihe(valinnanVaiheOid, valintakoe);
        assertEquals(201, response.getStatus());

    }

    @Test
    public void testInsertValintakoeWithExistingTunniste() {

        final String valinnanVaiheOid = "83";
        final Long laskentakaavaId = 101L;

        ValintakoeDTO valintakoe = new ValintakoeDTO();
        valintakoe.setTunniste("valintakoetunniste2");
        valintakoe.setNimi("nimi");
        valintakoe.setAktiivinen(true);
        valintakoe.setLaskentakaavaId(laskentakaavaId);
        valintakoe.setLahetetaankoKoekutsut(true);
        valintakoe.setKutsutaankoKaikki(false);
        valintakoe.setKutsunKohde(Koekutsu.YLIN_TOIVE);

        Response response = vaiheResource.addValintakoeToValinnanVaihe(valinnanVaiheOid, valintakoe);
        assertEquals(500, response.getStatus());

    }

    @Test
    public void testDelete() {
        Response delete = vaiheResource.delete("4");
        assertEquals(Response.Status.ACCEPTED.getStatusCode(), delete.getStatus());
    }

    @Test
    public void testDeleteOidNotFound() {
        boolean caughtOne = false;

        try {
            vaiheResource.delete("");
        } catch (WebApplicationException e) {
            caughtOne = true;
            assertEquals(404, e.getResponse().getStatus());
        }

        assertTrue(caughtOne);
    }

    @Test
    public void testDeleteInherited() {
        ValinnanVaiheDTO read = vaiheResource.read("32");

        assertNotNull(read);
        Response delete = vaiheResource.delete("32");
        assertEquals(Response.Status.ACCEPTED.getStatusCode(), delete.getStatus());

        try {
            ValinnanVaiheDTO read1 = vaiheResource.read("32");
            assertNull(read1);
        } catch (ValinnanVaiheEiOleOlemassaException e) {

        }
    }

    @Test
    public void testChildrenAreDeleted() {

        ValinnanVaiheDTO read = vaiheResource.read("79");

        assertNotNull(read);
        // objekti on peritty
        Response delete = vaiheResource.delete("75");
        assertEquals(Response.Status.ACCEPTED.getStatusCode(), delete.getStatus());

        try {
            ValinnanVaiheDTO read1 = vaiheResource.read("79");
            assertNull(read1);
        } catch (ValinnanVaiheEiOleOlemassaException e) {

        }
    }

    @Test
    public void testJarjesta() {
        List<ValinnanVaiheDTO> valinnanVaiheList = hakuResource.valinnanVaihesForHakukohde("oid6", "false");
        List<String> oids = new ArrayList<String>();

        for (ValinnanVaiheDTO valinnanVaihe : valinnanVaiheList) {
            oids.add(valinnanVaihe.getOid());
        }

        assertEquals("4", oids.get(0));
        assertEquals("6", oids.get(2));
        Collections.reverse(oids);
        List<ValinnanVaiheDTO> jarjesta = vaiheResource.jarjesta(oids);
        assertEquals("6", jarjesta.get(0).getOid());
        assertEquals("4", jarjesta.get(2).getOid());
        jarjesta = hakuResource.valinnanVaihesForHakukohde("oid6", "false");
        assertEquals("6", jarjesta.get(0).getOid());
        assertEquals("4", jarjesta.get(2).getOid());
    }

    @Test(expected = RuntimeException.class)
    public void testJarjestaEriHakuvaiheita() {
        List<String> oids = new ArrayList<String>();

        oids.add("4");
        oids.add("5");
        oids.add("6");

        oids.add("1");

        vaiheResource.jarjesta(oids);
    }

    private ValintatapajonoDTO newJono() {
        ValintatapajonoDTO jono = new ValintatapajonoDTO();
        jono.setNimi("Uusi valintaryhmä");
        jono.setOid("oid123");
        jono.setAloituspaikat(1);
        jono.setSiirretaanSijoitteluun(false);
        jono.setTasapistesaanto(fi.vm.sade.service.valintaperusteet.dto.model.Tasapistesaanto.ARVONTA);
        jono.setAktiivinen(true);
        jono.setautomaattinenSijoitteluunSiirto(true);
        jono.setValisijoittelu(false);
        return jono;
    }

    @Test
    public void testKuuluuSijoitteluun() {
        boolean oid;
        oid = vaiheResource.kuuluuSijoitteluun("3401").get("sijoitteluun");
        assertEquals(false, oid);

        oid = vaiheResource.kuuluuSijoitteluun("3501").get("sijoitteluun");
        assertEquals(false, oid);

        oid = vaiheResource.kuuluuSijoitteluun("3601").get("sijoitteluun");
        assertEquals(true, oid);

        oid = vaiheResource.kuuluuSijoitteluun("3701").get("sijoitteluun");
        assertEquals(false, oid);
    }

    @Test
    public void testListValintakokeet() throws IOException {
        final String valintaryhmaOid = "83";

        List<ValintakoeDTO> kokeet = vaiheResource.listValintakokeet(valintaryhmaOid);

        String json = mapper.writerWithView(JsonViews.Basic.class).writeValueAsString(kokeet);
        System.out.println("JSON: " + json);

        assertEquals(5, kokeet.size());
        assertEquals(4, kokeet.stream().filter(vk -> vk.getPeritty() == false).count());
        assertEquals(1, kokeet.stream().filter(vk -> vk.getPeritty() == true).count());

    }

    @Test
    public void testListValintakokeetShouldBeEmpty() {
        final String valinnanVaiheOid = "85";
        List<ValintakoeDTO> kokeet = vaiheResource.listValintakokeet(valinnanVaiheOid);
        assertEquals(0, kokeet.size());
    }
}
