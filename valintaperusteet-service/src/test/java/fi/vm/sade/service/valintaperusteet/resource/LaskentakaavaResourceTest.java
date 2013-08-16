package fi.vm.sade.service.valintaperusteet.resource;

import fi.vm.sade.dbunit.annotation.DataSetLocation;
import fi.vm.sade.dbunit.listener.JTACleanInsertTestExecutionListener;
import fi.vm.sade.kaava.Funktiokuvaaja;
import fi.vm.sade.service.valintaperusteet.ObjectMapperProvider;
import fi.vm.sade.service.valintaperusteet.model.*;
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
    private ApplicationContext applicationContext;

    @Before
    public void setUp() {
        applicationContext.getAutowireCapableBeanFactory().autowireBean(laskentakaavaResource);
    }

    private Funktiokutsu createLukuarvo(String luku) {
        final Funktionimi nimi = Funktionimi.LUKUARVO;

        final Funktiokuvaaja.Funktiokuvaus funktiokuvaus = Funktiokuvaaja.annaFunktiokuvaus(nimi)._2();

        Funktiokutsu funktiokutsu = new Funktiokutsu();
        funktiokutsu.setFunktionimi(Funktionimi.LUKUARVO);

        Syoteparametri syoteparametri = new Syoteparametri();
        syoteparametri.setAvain(funktiokuvaus.syoteparametrit().head().avain());
        syoteparametri.setArvo(luku);

        funktiokutsu.getSyoteparametrit().add(syoteparametri);

        return funktiokutsu;
    }

    private Funktiokutsu createSumma(FunktionArgumentti... args) {
        Funktiokutsu funktiokutsu = new Funktiokutsu();
        funktiokutsu.setFunktionimi(Funktionimi.SUMMA);

        for (int i = 0; i < args.length; ++i) {
            FunktionArgumentti f = args[i];
            Funktioargumentti arg = new Funktioargumentti();
            if (f instanceof Funktiokutsu) {
                arg.setFunktiokutsuChild((Funktiokutsu) f);
            } else if (f instanceof Laskentakaava) {
                arg.setLaskentakaavaChild((Laskentakaava) f);
            }
            arg.setIndeksi(i + 1);
            funktiokutsu.getFunktioargumentit().add(arg);
        }

        return funktiokutsu;
    }

    @Test
    public void testInsert() throws Exception {

        Laskentakaava laskentakaava = new Laskentakaava();
        laskentakaava.setNimi("jokuhienonimi");
        laskentakaava.setOnLuonnos(false);
        laskentakaava.setFunktiokutsu(createSumma(createLukuarvo("5.0"), createLukuarvo("10.0"),
                createLukuarvo("100.0")));

        final String json = mapper.writerWithView(JsonViews.Basic.class).writeValueAsString(laskentakaava);
        Laskentakaava fromJson = mapper.readValue(json, Laskentakaava.class);
        assertEquals(Response.Status.CREATED.getStatusCode(), laskentakaavaResource.insert(fromJson).getStatus());
    }

    @Test
    public void testInsertInvalid() throws Exception {

        Laskentakaava laskentakaava = new Laskentakaava();
        laskentakaava.setOnLuonnos(false);
        laskentakaava.setFunktiokutsu(createSumma(createLukuarvo("viisi"), createLukuarvo("10.0"),
                createLukuarvo("100.0")));

        final String json = mapper.writerWithView(JsonViews.Basic.class).writeValueAsString(laskentakaava);
        Laskentakaava fromJson = mapper.readValue(json, Laskentakaava.class);

        Response response = laskentakaavaResource.insert(fromJson);

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void testValidoi() throws Exception {
        Laskentakaava laskentakaava = new Laskentakaava();
        laskentakaava.setOnLuonnos(false);
        laskentakaava.setFunktiokutsu(createSumma(createLukuarvo("viisi"), createLukuarvo("10.0"),
                createLukuarvo("100.0")));

        final String json = mapper.writerWithView(JsonViews.Basic.class).writeValueAsString(laskentakaava);
        Laskentakaava fromJson = mapper.readValue(json, Laskentakaava.class);

        Laskentakaava validoitu = laskentakaavaResource.validoi(fromJson);
        String validoituJson = mapper.writerWithView(JsonViews.Basic.class).writeValueAsString(validoitu);
    }

    @Test
    public void testFindAll() {
        List<Laskentakaava> kaavat = laskentakaavaResource.kaavat(false, null, null, null);

        assertEquals(21, kaavat.size());

        for (Laskentakaava lk : kaavat) {
            assertFalse(lk.getOnLuonnos());
        }
    }

    @Test
    public void testUpdateWithLaskentakaava() throws Exception {
        Laskentakaava kaava = laskentakaavaResource.kaava(206L);
        Laskentakaava laskentakaava = new Laskentakaava();
        laskentakaava.setNimi("Test");
        laskentakaava.setOnLuonnos(false);
        laskentakaava.setFunktiokutsu(createSumma(createLukuarvo("5")));

        Response response = laskentakaavaResource.insert(laskentakaava);
        System.out.println(response.getEntity());

        Funktioargumentti funktioargumentti = new Funktioargumentti();
        funktioargumentti.setLaskentakaavaChild(kaava);
        funktioargumentti.setIndeksi(2);
        funktioargumentti.setParent(laskentakaava
                .getFunktiokutsu()
                .getFunktioargumentit()
                .toArray(new Funktioargumentti[0])[0]
                .getFunktiokutsuChild());
        laskentakaava.getFunktiokutsu().getFunktioargumentit().add(funktioargumentti);

        Response response1 = laskentakaavaResource.update(laskentakaava.getId(), false, laskentakaava);

        mapper.writerWithView(JsonViews.Basic.class).writeValueAsString(response1.getEntity());
    }


    @Test
    public void testGetTotuusarvokaava() {
        List<Laskentakaava> kaavat = laskentakaavaResource.kaavat(false, null, null, Funktiotyyppi.TOTUUSARVOFUNKTIO);
        assertEquals(2, kaavat.size());

        for (Laskentakaava kaava : kaavat) {
            assertEquals(Funktiotyyppi.TOTUUSARVOFUNKTIO, kaava.getTyyppi());
            assertFalse(kaava.getOnLuonnos());
        }
    }
}
