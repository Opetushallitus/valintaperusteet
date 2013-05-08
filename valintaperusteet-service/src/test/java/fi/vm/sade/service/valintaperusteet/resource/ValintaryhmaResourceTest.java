package fi.vm.sade.service.valintaperusteet.resource;

import fi.vm.sade.dbunit.annotation.DataSetLocation;
import fi.vm.sade.dbunit.listener.JTACleanInsertTestExecutionListener;
import fi.vm.sade.service.valintaperusteet.ObjectMapperProvider;
import fi.vm.sade.service.valintaperusteet.dao.ValinnanVaiheDAO;
import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import fi.vm.sade.service.valintaperusteet.model.ValinnanVaihe;
import fi.vm.sade.service.valintaperusteet.model.ValinnanVaiheTyyppi;
import fi.vm.sade.service.valintaperusteet.model.Valintaryhma;
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
import java.util.List;

import static org.junit.Assert.assertEquals;

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
        Assert.assertEquals(22, valintaryhmas.size());
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
        ValinnanVaihe vv = (ValinnanVaihe)response.getEntity();

        valinnanVaihe = new ValinnanVaihe();
        valinnanVaihe.setNimi("uusi");
        valinnanVaihe.setAktiivinen(true);
        valinnanVaihe.setValinnanVaiheTyyppi(ValinnanVaiheTyyppi.TAVALLINEN);

        response = valintaryhmaResource.insertValinnanvaihe("oid1", vv.getOid(), valinnanVaihe);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());

    }
}
