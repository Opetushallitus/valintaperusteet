package fi.vm.sade.service.valintaperusteet.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import com.fasterxml.jackson.databind.ObjectMapper;

import fi.vm.sade.service.valintaperusteet.ObjectMapperProvider;
import fi.vm.sade.service.valintaperusteet.annotation.DataSetLocation;
import fi.vm.sade.service.valintaperusteet.dao.ValinnanVaiheDAO;
import fi.vm.sade.service.valintaperusteet.dto.KoodiDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValinnanVaiheCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValinnanVaiheDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintaryhmaCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintaryhmaDTO;
import fi.vm.sade.service.valintaperusteet.listeners.ValinnatJTACleanInsertTestExecutionListener;
import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import fi.vm.sade.service.valintaperusteet.resource.impl.ValintaryhmaResourceImpl;
import junit.framework.Assert;
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

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * User: tommiha Date: 1/21/13 Time: 4:05 PM
 */
@ContextConfiguration(locations = "classpath:test-context.xml")
@TestExecutionListeners(listeners = { ValinnatJTACleanInsertTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class })
@RunWith(SpringJUnit4ClassRunner.class)
@DataSetLocation("classpath:test-data.xml")
public class ValintaryhmaResourceTest {

    private ValintaryhmaResourceImpl valintaryhmaResource = new ValintaryhmaResourceImpl();
    private ObjectMapper mapper = new ObjectMapperProvider().getContext(ValintaryhmaResourceImpl.class);

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
        ValintaryhmaDTO valintaryhma = valintaryhmaResource.queryFull("oid1");
        Assert.assertEquals("oid1", valintaryhma.getOid());

        mapper.writerWithView(JsonViews.Basic.class).writeValueAsString(valintaryhma);
    }

    @Test
    public void testSearch() throws Exception {
        List<ValintaryhmaDTO> valintaryhmas = valintaryhmaResource.search(true, null);
        Assert.assertEquals(46, valintaryhmas.size());
        Assert.assertEquals("oid1", valintaryhmas.get(0).getOid());

        mapper.writerWithView(JsonViews.Basic.class).writeValueAsString(valintaryhmas);
    }

    @Test
    public void testQueryChildren() throws Exception {
        List<ValintaryhmaDTO> valintaryhmas = valintaryhmaResource.queryChildren("oid1");
        Assert.assertEquals(4, valintaryhmas.size());

        mapper.writerWithView(JsonViews.Basic.class).writeValueAsString(valintaryhmas);
    }

    @Test
    public void testInsert() throws Exception {
        ValintaryhmaCreateDTO valintaryhma = new ValintaryhmaCreateDTO();

        valintaryhma.setNimi("Uusi valintaryhmä");
        System.out.println(mapper.writerWithView(JsonViews.Basic.class).writeValueAsString(valintaryhma));
        valintaryhmaResource.insert(valintaryhma);

        mapper.writerWithView(JsonViews.Basic.class).writeValueAsString(valintaryhma);
    }


    @Test
    public void testInsertParent() throws Exception {
        ValintaryhmaCreateDTO valintaryhma = new ValintaryhmaCreateDTO();

        valintaryhma.setNimi("Uusi valintaryhmä");
        System.out.println(mapper.writerWithView(JsonViews.Basic.class).writeValueAsString(valintaryhma));
        valintaryhmaResource.insertChild("oid1", valintaryhma);

        mapper.writerWithView(JsonViews.Basic.class).writeValueAsString(valintaryhma);
    }

    @Test
    public void testUpdate() throws Exception {
        ValintaryhmaCreateDTO valintaryhma1 = valintaryhmaResource.queryFull("oid1");
        valintaryhma1.setNimi("Updated valintaryhmä");
        valintaryhmaResource.update("oid1", valintaryhma1);

        mapper.writerWithView(JsonViews.Basic.class).writeValueAsString(valintaryhma1);
    }

    @Test
    public void testInsertValinnanVaihe() throws IOException {
        ValinnanVaiheCreateDTO valinnanVaihe = new ValinnanVaiheCreateDTO();

        valinnanVaihe.setNimi("uusi");
        valinnanVaihe.setAktiivinen(true);
        valinnanVaihe
                .setValinnanVaiheTyyppi(fi.vm.sade.service.valintaperusteet.dto.model.ValinnanVaiheTyyppi.TAVALLINEN);

        Response response = valintaryhmaResource.insertValinnanvaihe("oid1", null, valinnanVaihe);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        ValinnanVaiheDTO vv = (ValinnanVaiheDTO) response.getEntity();

        valinnanVaihe = new ValinnanVaiheCreateDTO();
        valinnanVaihe.setNimi("uusi");
        valinnanVaihe.setAktiivinen(true);
        valinnanVaihe
                .setValinnanVaiheTyyppi(fi.vm.sade.service.valintaperusteet.dto.model.ValinnanVaiheTyyppi.TAVALLINEN);

        response = valintaryhmaResource.insertValinnanvaihe("oid1", vv.getOid(), valinnanVaihe);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());

    }

    @Test
    public void testInsertHakukohdekoodi() {
        final String URI = "uri";
        final String ARVO = "arvo";
        KoodiDTO hakukohdekoodi = new KoodiDTO();
        hakukohdekoodi.setUri(URI);
        hakukohdekoodi.setArvo(ARVO);
        Response response = valintaryhmaResource.insertHakukohdekoodi("oid1", hakukohdekoodi);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());

        ValintaryhmaDTO oid1 = valintaryhmaResource.queryFull("oid1");
        boolean found = false;
        for (KoodiDTO hkk : oid1.getHakukohdekoodit()) {
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
        KoodiDTO hakukohdekoodi = new KoodiDTO();
        hakukohdekoodi.setUri(URI);
        hakukohdekoodi.setArvo(ARVO);
        Response response = valintaryhmaResource.insertHakukohdekoodi("oid1", hakukohdekoodi);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());

        Set<KoodiDTO> oid1 = valintaryhmaResource.queryFull("oid1").getHakukohdekoodit();
        for (int i = 0; i < 9; i++) {
            KoodiDTO koodi = new KoodiDTO();
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
        KoodiDTO hakukohdekoodi = new KoodiDTO();
        hakukohdekoodi.setUri(URI);
        hakukohdekoodi.setArvo(ARVO);
        Response response = valintaryhmaResource.insertHakukohdekoodi("oid1", hakukohdekoodi);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());

        Set<KoodiDTO> oid1 = null;

        response = valintaryhmaResource.updateHakukohdekoodi("oid1", oid1);
        assertEquals(Response.Status.ACCEPTED.getStatusCode(), response.getStatus());

        oid1 = valintaryhmaResource.queryFull("oid1").getHakukohdekoodit();

        assertEquals(0, oid1.size());

    }

    @Test
    public void testLisaaValintakoekoodiValintaryhmalle() throws IOException {
        final String valintaryhmaOid = "oid52";
        final String valintakoekoodiUri = "uusivalintakoekoodiuri";

        KoodiDTO koodi = new KoodiDTO();
        koodi.setUri(valintakoekoodiUri);

        String json = mapper.writerWithView(JsonViews.Basic.class).writeValueAsString(koodi);
        koodi = mapper.readValue(json, KoodiDTO.class);

        valintaryhmaResource.insertValintakoekoodi(valintaryhmaOid, koodi);
    }

    @Test
    public void testPaivitaValintaryhmanValintakoekoodit() throws IOException {
        final String[] koeUrit = new String[] { "uusivalintakoekoodi", "uusivalintakoekoodi", "valintakoeuri1",
                "valintakoeuri1", "valintakoeuri2" };
        final String valintaryhmaOid = "oid52";

        List<KoodiDTO> kokeet = new ArrayList<KoodiDTO>();
        for (String uri : koeUrit) {
            KoodiDTO koodi = new KoodiDTO();
            koodi.setUri(uri);
            String json = mapper.writerWithView(JsonViews.Basic.class).writeValueAsString(koodi);

            kokeet.add(mapper.readValue(json, KoodiDTO.class));
        }

        valintaryhmaResource.updateValintakoekoodi(valintaryhmaOid, kokeet).getEntity();

        ValintaryhmaDTO paivitetty = mapper.readValue(
                mapper.writerWithView(JsonViews.Basic.class).writeValueAsString(
                        valintaryhmaResource.queryFull(valintaryhmaOid)), ValintaryhmaDTO.class);

        assertEquals(koeUrit.length, paivitetty.getValintakoekoodit().size());
    }

    @Test
    public void testDelete() throws Exception {
        ValintaryhmaDTO valintaryhma = new ValintaryhmaDTO();
        valintaryhma.setOid("oid2");
        valintaryhma.setNimi("Uusi valintaryhmä");
        System.out.println(mapper.writerWithView(JsonViews.Basic.class).writeValueAsString(valintaryhma));
        valintaryhmaResource.insert(valintaryhma);

        mapper.writerWithView(JsonViews.Basic.class).writeValueAsString(valintaryhma);

        valintaryhmaResource.delete("oid2");
    }

    @Test
    public void testKopioiLapseksi() throws Exception {
        Response response = valintaryhmaResource.copyAsChild("oid_700", "oid_702", "Testi");
        ValintaryhmaDTO valintaryhmaDTO = mapper.readValue(
                mapper.writerWithView(JsonViews.Basic.class).writeValueAsString(
                        response.getEntity()), ValintaryhmaDTO.class);
        assertEquals(1, valintaryhmaDTO.getHakukohdekoodit().size());
        assertEquals("hakukohdekoodi704", valintaryhmaDTO.getHakukohdekoodit().iterator().next().getUri());
        assertEquals(1, valintaryhmaDTO.getValintakoekoodit().size());
        assertEquals("koekoodi703", valintaryhmaDTO.getValintakoekoodit().get(0).getUri());
    }

}
