package fi.vm.sade.service.valintaperusteet.resource;

import fi.vm.sade.dbunit.annotation.DataSetLocation;
import fi.vm.sade.dbunit.listener.JTACleanInsertTestExecutionListener;
import fi.vm.sade.service.valintaperusteet.ObjectMapperProvider;
import fi.vm.sade.service.valintaperusteet.dao.ValinnanVaiheDAO;
import fi.vm.sade.service.valintaperusteet.model.*;
import junit.framework.Assert;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * User: tommiha
 * Date: 1/21/13
 * Time: 4:05 PM
 */
@ContextConfiguration(locations = "classpath:test-context.xml")
@TestExecutionListeners(listeners = {JTACleanInsertTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class})
@RunWith(SpringJUnit4ClassRunner.class)
@DataSetLocation("classpath:test-data.xml")
public class ValintaryhmaResourceTest {

    private ValintaryhmaResource valintaryhmaResource = new ValintaryhmaResource();
    private ObjectMapper mapper = new ObjectMapperProvider().getContext(ValintaryhmaResource.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ValinnanVaiheDAO valinnanVaiheDao;

    @Before
    public void setUp() {
        applicationContext.getAutowireCapableBeanFactory().autowireBean(valintaryhmaResource);
    }

    @Test
    public void testQueryFull() throws Exception {
        Valintaryhma valintaryhma = valintaryhmaResource.queryFull("oid1");
        Assert.assertEquals(new Long(1L), valintaryhma.getId());

        mapper.writerWithView(JsonViews.Basic.class).writeValueAsString(valintaryhma);
    }

    @Test
    public void testSearch() throws Exception {
        List<Valintaryhma> valintaryhmas = valintaryhmaResource.search(true, null);
        Assert.assertEquals(44, valintaryhmas.size());
        Assert.assertEquals(new Long(1L), valintaryhmas.get(0).getId());

        mapper.writerWithView(JsonViews.Basic.class).writeValueAsString(valintaryhmas);
    }

    @Test
    public void testQueryChildren() throws Exception {
        List<Valintaryhma> valintaryhmas = valintaryhmaResource.queryChildren("oid1");
        Assert.assertEquals(4, valintaryhmas.size());

        mapper.writerWithView(JsonViews.Basic.class).writeValueAsString(valintaryhmas);
    }

    @Test
    public void testInsert() throws Exception {
        Valintaryhma valintaryhma = new Valintaryhma();
        valintaryhma.setHakuOid("hakuOid");
        valintaryhma.setNimi("Uusi valintaryhmä");
        System.out.println(mapper.writerWithView(JsonViews.Basic.class).writeValueAsString(valintaryhma));
        valintaryhmaResource.insert(valintaryhma);

        mapper.writerWithView(JsonViews.Basic.class).writeValueAsString(valintaryhma);
    }

    @Test
    public void testInsertParent() throws Exception {
        Valintaryhma valintaryhma = new Valintaryhma();
        valintaryhma.setHakuOid("hakuOid");
        valintaryhma.setNimi("Uusi valintaryhmä");
        System.out.println(mapper.writerWithView(JsonViews.Basic.class).writeValueAsString(valintaryhma));
        valintaryhmaResource.insertChild("oid1", valintaryhma);

        mapper.writerWithView(JsonViews.Basic.class).writeValueAsString(valintaryhma);
    }

    @Test
    public void testUpdate() throws Exception {
        Valintaryhma valintaryhma1 = valintaryhmaResource.queryFull("oid1");
        valintaryhma1.setNimi("Updated valintaryhmä");
        valintaryhmaResource.update("oid1", valintaryhma1);

        mapper.writerWithView(JsonViews.Basic.class).writeValueAsString(valintaryhma1);
    }

    @Test
    public void testInsertValinnanVaihe() throws IOException {
        ValinnanVaihe valinnanVaihe = new ValinnanVaihe();

        valinnanVaihe.setNimi("uusi");
        valinnanVaihe.setAktiivinen(true);
        valinnanVaihe.setValinnanVaiheTyyppi(ValinnanVaiheTyyppi.TAVALLINEN);

        Response response = valintaryhmaResource.insertValinnanvaihe("oid1", null, valinnanVaihe);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        ValinnanVaihe vv = (ValinnanVaihe) response.getEntity();

        valinnanVaihe = new ValinnanVaihe();
        valinnanVaihe.setNimi("uusi");
        valinnanVaihe.setAktiivinen(true);
        valinnanVaihe.setValinnanVaiheTyyppi(ValinnanVaiheTyyppi.TAVALLINEN);

        response = valintaryhmaResource.insertValinnanvaihe("oid1", vv.getOid(), valinnanVaihe);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());

    }

    @Test
    public void testInsertHakukohdekoodi() {
        final String URI = "uri";
        final String ARVO = "arvo";
        Hakukohdekoodi hakukohdekoodi = new Hakukohdekoodi();
        hakukohdekoodi.setUri(URI);
        hakukohdekoodi.setArvo(ARVO);
        Response response = valintaryhmaResource.insertHakukohdekoodi("oid1", hakukohdekoodi);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());

        Valintaryhma oid1 = valintaryhmaResource.queryFull("oid1");
        boolean found = false;
        for (Hakukohdekoodi hkk : oid1.getHakukohdekoodit()) {
            if (hkk.getArvo().equals(ARVO) && hkk.getUri().equals(URI)) {
                found = true;
                break;
            }
        }
        assertTrue(found);

    }

    @Test
    public void testUpdateHakukohdekoodi() {
        final String URI = "uri";
        final String ARVO = "arvo";
        Hakukohdekoodi hakukohdekoodi = new Hakukohdekoodi();
        hakukohdekoodi.setUri(URI);
        hakukohdekoodi.setArvo(ARVO);
        Response response = valintaryhmaResource.insertHakukohdekoodi("oid1", hakukohdekoodi);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());

        Set<Hakukohdekoodi> oid1 = valintaryhmaResource.queryFull("oid1").getHakukohdekoodit();
        for (int i = 0; i < 9; i++) {
            Hakukohdekoodi koodi = new Hakukohdekoodi();
            koodi.setUri("uri" + i);
            koodi.setArvo("arvo" + i);
            oid1.add(koodi);
        }

        valintaryhmaResource.updateHakukohdekoodi("oid1", oid1);

        oid1 = valintaryhmaResource.queryFull("oid1").getHakukohdekoodit();

        assertEquals(10, oid1.size());

    }

    @Test
    public void testRemoveAllHakukohdekoodi() {
        final String URI = "uri";
        final String ARVO = "arvo";
        Hakukohdekoodi hakukohdekoodi = new Hakukohdekoodi();
        hakukohdekoodi.setUri(URI);
        hakukohdekoodi.setArvo(ARVO);
        Response response = valintaryhmaResource.insertHakukohdekoodi("oid1", hakukohdekoodi);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());

        Set<Hakukohdekoodi> oid1 = null;

        response = valintaryhmaResource.updateHakukohdekoodi("oid1", oid1);
        assertEquals(Response.Status.ACCEPTED.getStatusCode(), response.getStatus());

        oid1 = valintaryhmaResource.queryFull("oid1").getHakukohdekoodit();

        assertEquals(0, oid1.size());

    }


    @Test
    public void testLisaaValintakoekoodiValintaryhmalle() throws IOException {
        final String valintaryhmaOid = "oid52";
        final String valintakoekoodiUri = "uusivalintakoekoodiuri";

        Valintakoekoodi koodi = new Valintakoekoodi();
        koodi.setUri(valintakoekoodiUri);

        String json = mapper.writerWithView(JsonViews.Basic.class).writeValueAsString(koodi);
        koodi = mapper.readValue(json, Valintakoekoodi.class);

        valintaryhmaResource.insertValintakoekoodi(valintaryhmaOid, koodi);
    }

    @Test
    public void testPaivitaValintaryhmanValintakoekoodit() throws IOException {
        final String[] koeUrit = new String[]{"uusivalintakoekoodi", "uusivalintakoekoodi",
                "valintakoeuri1", "valintakoeuri1", "valintakoeuri2"};
        final String valintaryhmaOid = "oid52";

        List<Valintakoekoodi> kokeet = new ArrayList<Valintakoekoodi>();
        for (String uri : koeUrit) {
            Valintakoekoodi koodi = new Valintakoekoodi();
            koodi.setUri(uri);
            String json = mapper.writerWithView(JsonViews.Basic.class).writeValueAsString(koodi);

            kokeet.add(mapper.readValue(json, Valintakoekoodi.class));
        }

        valintaryhmaResource.updateValintakoekoodi(valintaryhmaOid, kokeet).getEntity();

        Valintaryhma paivitetty =
                mapper.readValue(mapper.writerWithView(JsonViews.Basic.class).writeValueAsString(
                        valintaryhmaResource.queryFull(valintaryhmaOid)), Valintaryhma.class);


        assertEquals(koeUrit.length, paivitetty.getValintakoekoodit().size());
    }
}
