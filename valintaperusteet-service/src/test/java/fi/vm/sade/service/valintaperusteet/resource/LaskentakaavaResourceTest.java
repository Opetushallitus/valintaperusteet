package fi.vm.sade.service.valintaperusteet.resource;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertNull;

import java.util.List;

import javax.ws.rs.core.Response;

import fi.vm.sade.service.valintaperusteet.annotation.DataSetLocation;
import fi.vm.sade.service.valintaperusteet.dto.*;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.dto.model.Kieli;
import fi.vm.sade.service.valintaperusteet.listeners.ValinnatJTACleanInsertTestExecutionListener;
import fi.vm.sade.service.valintaperusteet.model.Funktiokutsu;
import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import fi.vm.sade.service.valintaperusteet.model.Laskentakaava;
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

import fi.vm.sade.kaava.Funktiokuvaaja;
import fi.vm.sade.service.valintaperusteet.ObjectMapperProvider;
import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi;
//import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import fi.vm.sade.service.valintaperusteet.resource.impl.LaskentakaavaResourceImpl;

/**
 * User: kwuoti Date: 28.1.2013 Time: 13.04
 */
@ContextConfiguration(locations = "classpath:test-context.xml")
@TestExecutionListeners(listeners = { ValinnatJTACleanInsertTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class })
@RunWith(SpringJUnit4ClassRunner.class)
@DataSetLocation("classpath:test-data.xml")
public class LaskentakaavaResourceTest {

    private LaskentakaavaResourceImpl laskentakaavaResource = new LaskentakaavaResourceImpl();
    private ObjectMapper mapper = new ObjectMapperProvider().getContext(LaskentakaavaResourceImpl.class);

    @Autowired
    private ValintaperusteetModelMapper modelMapper;

    @Autowired
    private ApplicationContext applicationContext;

    @Before
    public void setUp() {
        applicationContext.getAutowireCapableBeanFactory().autowireBean(laskentakaavaResource);
    }

    private FunktiokutsuDTO createLukuarvo(String luku) {
        final fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi nimi = Funktionimi.LUKUARVO;

        final Funktiokuvaaja.Funktiokuvaus funktiokuvaus = Funktiokuvaaja.annaFunktiokuvaus(nimi)._2();

        FunktiokutsuDTO funktiokutsu = new FunktiokutsuDTO();
        funktiokutsu.setFunktionimi(fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi.LUKUARVO);
        funktiokutsu.setTallennaTulos(false);

        SyoteparametriDTO syoteparametri = new SyoteparametriDTO();
        syoteparametri.setAvain(funktiokuvaus.syoteparametrit().head().avain());
        syoteparametri.setArvo(luku);

        funktiokutsu.getSyoteparametrit().add(syoteparametri);

        return funktiokutsu;
    }

    private ValintaperusteetFunktiokutsuDTO createLukuarvoVP(String luku) {
        final fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi nimi = Funktionimi.LUKUARVO;

        final Funktiokuvaaja.Funktiokuvaus funktiokuvaus = Funktiokuvaaja.annaFunktiokuvaus(nimi)._2();

        ValintaperusteetFunktiokutsuDTO funktiokutsu = new ValintaperusteetFunktiokutsuDTO();
        funktiokutsu.setFunktionimi(fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi.LUKUARVO);
        funktiokutsu.setTallennaTulos(false);

        SyoteparametriDTO syoteparametri = new SyoteparametriDTO();
        syoteparametri.setAvain(funktiokuvaus.syoteparametrit().head().avain());
        syoteparametri.setArvo(luku);

        funktiokutsu.getSyoteparametrit().add(syoteparametri);

        return funktiokutsu;
    }

    private FunktiokutsuDTO createSumma(FunktiokutsuDTO... args) {
        FunktiokutsuDTO funktiokutsu = new FunktiokutsuDTO();
        funktiokutsu.setFunktionimi(fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi.SUMMA);

        funktiokutsu.setTallennaTulos(true);
        funktiokutsu.setTulosTekstiEn("en");
        funktiokutsu.setTulosTekstiSv("sv");
        funktiokutsu.setTulosTekstiFi("fi");
        funktiokutsu.setTulosTunniste("tunniste");

        for (int i = 0; i < args.length; ++i) {
            FunktioargumentinLapsiDTO f = modelMapper.map(args[i], FunktioargumentinLapsiDTO.class);
            FunktioargumenttiDTO arg = new FunktioargumenttiDTO();
            f.setLapsityyppi(FunktioargumentinLapsiDTO.FUNKTIOKUTSUTYYPPI);
            arg.setLapsi(f);
            arg.setIndeksi(i + 1);
            funktiokutsu.getFunktioargumentit().add(arg);
        }

        return funktiokutsu;
    }

    private ValintaperusteetFunktiokutsuDTO createSummaVP(ValintaperusteetFunktiokutsuDTO... args) {
        ValintaperusteetFunktiokutsuDTO funktiokutsu = new ValintaperusteetFunktiokutsuDTO();
        funktiokutsu.setFunktionimi(fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi.SUMMA);

        funktiokutsu.setTallennaTulos(true);
        funktiokutsu.setTulosTekstiEn("en");
        funktiokutsu.setTulosTekstiSv("sv");
        funktiokutsu.setTulosTekstiFi("fi");
        funktiokutsu.setTulosTunniste("tunniste");

        for (int i = 0; i < args.length; ++i) {
            ValintaperusteetFunktioargumenttiDTO f = new ValintaperusteetFunktioargumenttiDTO();
            f.setFunktiokutsu(args[i]);
            f.setIndeksi(i + 1);
            funktiokutsu.getFunktioargumentit().add(f);
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

        ValintaperusteetFunktiokutsuDTO dto1 = createSummaVP(createLukuarvoVP("5.0"),createLukuarvoVP("10.0"),createLukuarvoVP("100.0"));
        ValintaperusteetFunktiokutsuDTO dto2 = createSummaVP(createLukuarvoVP("5.0"),createLukuarvoVP("10.0"),createLukuarvoVP("100.0"));
        ValintaperusteetFunktiokutsuDTO dto3 = createSummaVP(dto1, dto2);
        Funktiokutsu kutsu = modelMapper.map(dto3, Funktiokutsu.class);

        final String json = mapper.writerWithView(JsonViews.Basic.class).writeValueAsString(laskentakaava);
        LaskentakaavaCreateDTO fromJson = mapper.readValue(json, LaskentakaavaCreateDTO.class);
        Response insert = laskentakaavaResource.insert(new LaskentakaavaInsertDTO(fromJson, null, null));
        assertEquals(Response.Status.CREATED.getStatusCode(), insert.getStatus());

        assertTrue(((LaskentakaavaDTO) insert.getEntity()).getFunktiokutsu().getTallennaTulos());

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
        FunktiokutsuDTO kutsu = createSumma(createLukuarvo("viisi"), createLukuarvo("10.0"),
                createLukuarvo("100.0"));

        LokalisoituTekstiDTO kuvaus = new LokalisoituTekstiDTO();
        kuvaus.setKieli(Kieli.FI);
        kuvaus.setTeksti("Teksti");

        TekstiRyhmaDTO ryhma = new TekstiRyhmaDTO();
        ryhma.getTekstit().add(kuvaus);

        ArvokonvertteriparametriDTO ak = new ArvokonvertteriparametriDTO();
        ak.setArvo("2");
        ak.setPaluuarvo("1");
        ak.setHylkaysperuste("true");
        ak.setKuvaukset(ryhma);

        kutsu.getArvokonvertteriparametrit().add(ak);

        laskentakaava.setFunktiokutsu(kutsu);

        final String json = mapper.writeValueAsString(laskentakaava);
        LaskentakaavaDTO fromJson = mapper.readValue(json, LaskentakaavaDTO.class);

        LaskentakaavaDTO validoitu = laskentakaavaResource.validoi(fromJson);

        String validoituJson = mapper.writeValueAsString(validoitu);

        LaskentakaavaDTO validoituFromJson = mapper.readValue(validoituJson, LaskentakaavaDTO.class);

        Laskentakaava validoituFromDTO = modelMapper.map(validoituFromJson, Laskentakaava.class);

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
    public void testTallennaTulosUpdate() throws Exception {
        LaskentakaavaDTO kaava = laskentakaavaResource.kaava(206L, true);
        LaskentakaavaDTO laskentakaava = new LaskentakaavaDTO();
        laskentakaava.setNimi("Test");
        laskentakaava.setOnLuonnos(false);
        laskentakaava.setFunktiokutsu(createSumma(createLukuarvo("5")));
        laskentakaava.getFunktiokutsu().setTallennaTulos(false);
        laskentakaava.getFunktiokutsu().setTulosTunniste(null);
        laskentakaava.getFunktiokutsu().setTulosTekstiEn(null);
        laskentakaava.getFunktiokutsu().setTulosTekstiSv(null);
        laskentakaava.getFunktiokutsu().setTulosTekstiFi(null);

        LaskentakaavaInsertDTO insert = new LaskentakaavaInsertDTO();
        insert.setLaskentakaava(laskentakaava);

        Response response = laskentakaavaResource.insert(insert);
        laskentakaava = (LaskentakaavaDTO) response.getEntity();

        assertFalse(laskentakaava.getFunktiokutsu().getTallennaTulos());
        assertNull(laskentakaava.getFunktiokutsu().getTulosTunniste());
        assertNull(laskentakaava.getFunktiokutsu().getTulosTekstiEn());
        assertNull(laskentakaava.getFunktiokutsu().getTulosTekstiFi());
        assertNull(laskentakaava.getFunktiokutsu().getTulosTekstiSv());

        laskentakaava.getFunktiokutsu().setTallennaTulos(true);
        laskentakaava.getFunktiokutsu().setTulosTunniste("t1");
        laskentakaava.getFunktiokutsu().setTulosTekstiEn("en");
        laskentakaava.getFunktiokutsu().setTulosTekstiFi("fi");
        laskentakaava.getFunktiokutsu().setTulosTekstiSv("sv");

        response = laskentakaavaResource.update(laskentakaava.getId(), false, laskentakaava);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        mapper.writerWithView(JsonViews.Basic.class).writeValueAsString(response.getEntity());

        laskentakaava = (LaskentakaavaDTO) response.getEntity();
        assertTrue(laskentakaava.getFunktiokutsu().getTallennaTulos());
        assertEquals("t1", laskentakaava.getFunktiokutsu().getTulosTunniste());
        assertEquals("en", laskentakaava.getFunktiokutsu().getTulosTekstiEn());
        assertEquals("fi", laskentakaava.getFunktiokutsu().getTulosTekstiFi());
        assertEquals("sv", laskentakaava.getFunktiokutsu().getTulosTekstiSv());
    }

    @Test
    public void testGetTotuusarvokaava() {
        List<LaskentakaavaListDTO> kaavat = laskentakaavaResource.kaavat(false, null, null,
                fi.vm.sade.service.valintaperusteet.dto.model.Funktiotyyppi.TOTUUSARVOFUNKTIO);
        assertEquals(4, kaavat.size());

        for (LaskentakaavaListDTO kaava : kaavat) {
            assertEquals(fi.vm.sade.service.valintaperusteet.dto.model.Funktiotyyppi.TOTUUSARVOFUNKTIO,
                    kaava.getTyyppi());
            assertFalse(kaava.getOnLuonnos());
        }
    }
}
