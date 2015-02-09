package fi.vm.sade.service.valintaperusteet.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import fi.vm.sade.service.valintaperusteet.ObjectMapperProvider;
import fi.vm.sade.service.valintaperusteet.annotation.DataSetLocation;
import fi.vm.sade.service.valintaperusteet.dto.ValintakoeDTO;
import fi.vm.sade.service.valintaperusteet.dto.model.Koekutsu;
import fi.vm.sade.service.valintaperusteet.listeners.ValinnatJTACleanInsertTestExecutionListener;
import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import fi.vm.sade.service.valintaperusteet.resource.impl.ValintakoeResourceImpl;
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

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.*;

/**
 * User: kwuoti
 * Date: 15.4.2013
 * Time: 18.17
 */
@ContextConfiguration(locations = "classpath:test-context.xml")
@TestExecutionListeners(listeners = {ValinnatJTACleanInsertTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class})
@RunWith(SpringJUnit4ClassRunner.class)
@DataSetLocation("classpath:test-data.xml")
public class ValintakoeResourceTest {

    private ValintakoeResourceImpl valintakoeResource = new ValintakoeResourceImpl();
    private ObjectMapper mapper = new ObjectMapperProvider().getContext(ValintakoeResourceImpl.class);
    private TestUtil testUtil = new TestUtil(this.getClass());


    @Autowired
    private ApplicationContext applicationContext;

    @Before
    public void setUp() {
        applicationContext.getAutowireCapableBeanFactory().autowireBean(valintakoeResource);
    }

    @Test
    public void testReadByOid() throws Exception {
        final String oid = "oid1";
        ValintakoeDTO valintakoe = valintakoeResource.readByOid(oid);
        testUtil.lazyCheck(JsonViews.Basic.class, valintakoe);
    }

    @Test
    public void testUpdate() {
        final String oid = "oid1";
        final Long laskentakaavaId = 102L;

        ValintakoeDTO saved = valintakoeResource.readByOid(oid);

        ValintakoeDTO koe = new ValintakoeDTO();
        koe.setKuvaus("uusi kuvaus");
        koe.setNimi("uusi nimi");
        koe.setTunniste("uusi tunniste");
        koe.setLaskentakaavaId(laskentakaavaId);
        koe.setKutsunKohde(Koekutsu.YLIN_TOIVE);

        assertFalse(saved.getNimi().equals(koe.getNimi()));
        assertFalse(saved.getKuvaus().equals(koe.getKuvaus()));
        assertFalse(saved.getTunniste().equals(koe.getTunniste()));
        assertFalse(saved.getLaskentakaavaId().equals(koe.getLaskentakaavaId()));

        valintakoeResource.update(oid, koe);

        saved = valintakoeResource.readByOid(oid);

        assertEquals(koe.getKuvaus(), saved.getKuvaus());
        assertEquals(koe.getNimi(), saved.getNimi());
        assertEquals(koe.getTunniste(), saved.getTunniste());
        assertEquals(koe.getLaskentakaavaId(), saved.getLaskentakaavaId());

    }

    @Test
    public void testUpdateSetLaskentakaavaNull() {
        final String oid = "oid1";
        ValintakoeDTO saved = valintakoeResource.readByOid(oid);

        ValintakoeDTO koe = new ValintakoeDTO();
        koe.setKuvaus("uusi kuvaus");
        koe.setNimi("uusi nimi");
        koe.setTunniste("uusi tunniste");
        koe.setLaskentakaavaId(null);
        koe.setKutsunKohde(Koekutsu.YLIN_TOIVE);

        assertFalse(saved.getNimi().equals(koe.getNimi()));
        assertFalse(saved.getKuvaus().equals(koe.getKuvaus()));
        assertFalse(saved.getTunniste().equals(koe.getTunniste()));
        assertNotNull(saved.getLaskentakaavaId());

        valintakoeResource.update(oid, koe);

        saved = valintakoeResource.readByOid(oid);

        assertEquals(koe.getKuvaus(), saved.getKuvaus());
        assertEquals(koe.getNimi(), saved.getNimi());
        assertEquals(koe.getTunniste(), saved.getTunniste());
        assertNull(saved.getLaskentakaavaId());
    }

}
