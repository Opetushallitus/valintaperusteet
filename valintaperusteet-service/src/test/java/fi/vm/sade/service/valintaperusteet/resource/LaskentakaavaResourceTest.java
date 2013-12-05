package fi.vm.sade.service.valintaperusteet.resource;

import fi.vm.sade.dbunit.annotation.DataSetLocation;
import fi.vm.sade.dbunit.listener.JTACleanInsertTestExecutionListener;
import fi.vm.sade.kaava.Funktiokuvaaja;
import fi.vm.sade.service.valintaperusteet.ObjectMapperProvider;
import fi.vm.sade.service.valintaperusteet.dto.*;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.model.Funktionimi;
import fi.vm.sade.service.valintaperusteet.model.Funktiotyyppi;
import fi.vm.sade.service.valintaperusteet.model.JsonViews;
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
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;

/**
 * User: kwuoti Date: 28.1.2013 Time: 13.04
 */
@ContextConfiguration(locations = "classpath:test-context.xml")
@TestExecutionListeners(listeners = {JTACleanInsertTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class})
@RunWith(SpringJUnit4ClassRunner.class)
@DataSetLocation("classpath:test-data.xml")
public class LaskentakaavaResourceTest {

    private LaskentakaavaResource laskentakaavaResource = new LaskentakaavaResource();
    private ObjectMapper mapper = new ObjectMapperProvider().getContext(LaskentakaavaResource.class);

    @Autowired
    private ValintaperusteetModelMapper modelMapper;

    @Autowired
    private ApplicationContext applicationContext;

    @Before
    public void setUp() {
        applicationContext.getAutowireCapableBeanFactory().autowireBean(laskentakaavaResource);
    }

    private FunktiokutsuDTO createLukuarvo(String luku) {
        final Funktionimi nimi = Funktionimi.LUKUARVO;

        final Funktiokuvaaja.Funktiokuvaus funktiokuvaus = Funktiokuvaaja.annaFunktiokuvaus(nimi)._2();

        FunktiokutsuDTO funktiokutsu = new FunktiokutsuDTO();
        funktiokutsu.setFunktionimi(Funktionimi.LUKUARVO);

        SyoteparametriDTO syoteparametri = new SyoteparametriDTO();
        syoteparametri.setAvain(funktiokuvaus.syoteparametrit().head().avain());
        syoteparametri.setArvo(luku);

        funktiokutsu.getSyoteparametrit().add(syoteparametri);

        return funktiokutsu;
    }

    private FunktiokutsuDTO createSumma(FunktiokutsuDTO... args) {
        FunktiokutsuDTO funktiokutsu = new FunktiokutsuDTO();
        funktiokutsu.setFunktionimi(Funktionimi.SUMMA);

        for (int i = 0; i < args.length; ++i) {
            FunktiokutsuDTO f = args[i];
            FunktioargumenttiDTO arg = new FunktioargumenttiDTO();
            arg.setFunktiokutsuChild(f);
            arg.setIndeksi(i + 1);
            funktiokutsu.getFunktioargumentit().add(arg);
        }

        return funktiokutsu;
    }

    @Test
    public void testInsert() throws Exception {

        LaskentakaavaCreateDTO laskentakaava = new LaskentakaavaCreateDTO();
        laskentakaava.setNimi("jokuhienonimi");
        laskentakaava.setOnLuonnos(false);
        laskentakaava.setFunktiokutsu(createSumma(createLukuarvo("5.0"), createLukuarvo("10.0"),
                createLukuarvo("100.0")));

        final String json = mapper.writerWithView(JsonViews.Basic.class).writeValueAsString(laskentakaava);
        LaskentakaavaCreateDTO fromJson = mapper.readValue(json, LaskentakaavaCreateDTO.class);
        assertEquals(Response.Status.CREATED.getStatusCode(), laskentakaavaResource.insert(new LaskentakaavaInsertDTO(fromJson, null, null)).getStatus());
    }

    @Test
    public void testInsertInvalid() throws Exception {

        LaskentakaavaCreateDTO laskentakaava = new LaskentakaavaCreateDTO();
        laskentakaava.setOnLuonnos(false);
        laskentakaava.setFunktiokutsu(createSumma(createLukuarvo("viisi"), createLukuarvo("10.0"),
                createLukuarvo("100.0")));

        final String json = mapper.writerWithView(JsonViews.Basic.class).writeValueAsString(laskentakaava);
        LaskentakaavaCreateDTO fromJson = mapper.readValue(json, LaskentakaavaCreateDTO.class);


        Response response = laskentakaavaResource.insert(new LaskentakaavaInsertDTO(fromJson, null, null));

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void testValidoi() throws Exception {
        LaskentakaavaDTO laskentakaava = new LaskentakaavaDTO();
        laskentakaava.setOnLuonnos(false);
        laskentakaava.setFunktiokutsu(createSumma(createLukuarvo("viisi"), createLukuarvo("10.0"),
                createLukuarvo("100.0")));

        final String json = mapper.writerWithView(JsonViews.Basic.class).writeValueAsString(laskentakaava);
        LaskentakaavaDTO fromJson = mapper.readValue(json, LaskentakaavaDTO.class);

        LaskentakaavaDTO validoitu = laskentakaavaResource.validoi(fromJson);
        String validoituJson = mapper.writerWithView(JsonViews.Basic.class).writeValueAsString(validoitu);
    }

    @Test
    public void testFindAll() {
        List<LaskentakaavaListDTO> kaavat = laskentakaavaResource.kaavat(false, null, null, null);

        assertEquals(25, kaavat.size());

        for (LaskentakaavaListDTO lk : kaavat) {
            assertFalse(lk.getOnLuonnos());
        }
    }

    @Test
    public void testUpdateWithLaskentakaava() throws Exception {
        LaskentakaavaDTO kaava = laskentakaavaResource.kaava(206L);
        LaskentakaavaDTO laskentakaava = new LaskentakaavaDTO();
        laskentakaava.setNimi("Test");
        laskentakaava.setOnLuonnos(false);
        laskentakaava.setFunktiokutsu(createSumma(createLukuarvo("5")));

        LaskentakaavaInsertDTO insert = new LaskentakaavaInsertDTO();
        insert.setLaskentakaava(laskentakaava);

        Response response = laskentakaavaResource.insert(insert);
        FunktioargumenttiDTO funktioargumentti = new FunktioargumenttiDTO();
        funktioargumentti.setLaskentakaavaChild(modelMapper.map(kaava, LaskentakaavaListDTO.class));
        funktioargumentti.setIndeksi(2);
        laskentakaava.getFunktiokutsu().getFunktioargumentit().add(funktioargumentti);

        Response response1 = laskentakaavaResource.update(laskentakaava.getId(), false, laskentakaava);

        mapper.writerWithView(JsonViews.Basic.class).writeValueAsString(response1.getEntity());
    }


    @Test
    public void testGetTotuusarvokaava() {
        List<LaskentakaavaListDTO> kaavat = laskentakaavaResource.kaavat(false, null, null, Funktiotyyppi.TOTUUSARVOFUNKTIO);
        assertEquals(4, kaavat.size());

        for (LaskentakaavaListDTO kaava : kaavat) {
            assertEquals(Funktiotyyppi.TOTUUSARVOFUNKTIO, kaava.getTyyppi());
            assertFalse(kaava.getOnLuonnos());
        }
    }
}
