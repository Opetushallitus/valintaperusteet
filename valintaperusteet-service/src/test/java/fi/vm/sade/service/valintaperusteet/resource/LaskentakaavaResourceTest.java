package fi.vm.sade.service.valintaperusteet.resource;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import fi.vm.sade.kaava.Funktiokuvaaja;
import fi.vm.sade.service.valintaperusteet.ObjectMapperProvider;
import fi.vm.sade.service.valintaperusteet.WithSpringBoot;
import fi.vm.sade.service.valintaperusteet.annotation.DataSetLocation;
import fi.vm.sade.service.valintaperusteet.dto.*;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi;
import fi.vm.sade.service.valintaperusteet.dto.model.Kieli;
import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import fi.vm.sade.service.valintaperusteet.model.Laskentakaava;
import fi.vm.sade.valinta.sharedutils.FakeAuthenticationInitialiser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

// import fi.vm.sade.service.valintaperusteet.model.JsonViews;

/** User: kwuoti Date: 28.1.2013 Time: 13.04 */
@DataSetLocation("classpath:test-data.xml")
public class LaskentakaavaResourceTest extends WithSpringBoot {

  private LaskentakaavaResource laskentakaavaResource = new LaskentakaavaResource();
  private ObjectMapper mapper = new ObjectMapperProvider().getContext(LaskentakaavaResource.class);
  private HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
  private HttpSession session = Mockito.mock(HttpSession.class);

  @Autowired private ValintaperusteetModelMapper modelMapper;

  @Autowired private ApplicationContext applicationContext;

  @BeforeEach
  public void setUp() {
    applicationContext.getAutowireCapableBeanFactory().autowireBean(laskentakaavaResource);
    FakeAuthenticationInitialiser.fakeAuthentication();
    Mockito.when(request.getSession(false)).thenReturn(session);
  }

  @Test
  public void testInsert() throws Exception {
    ResponseEntity<LaskentakaavaDTO> response = insert(newLaskentakaava("jokuhienonimi"));
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    final LaskentakaavaDTO inserted = kaavaFromResponse(response);
    assertTrue(inserted.getFunktiokutsu().getTallennaTulos());
  }

  @Test
  public void testInsertInvalid() throws Exception {
    ResponseEntity<LaskentakaavaDTO> response = insert(newLaskentakaava(null));
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  private ResponseEntity<LaskentakaavaDTO> insert(final LaskentakaavaCreateDTO kaava) {
    return laskentakaavaResource.insert(new LaskentakaavaInsertDTO(kaava, null, null), request);
  }

  @Test
  public void testValidoi() throws Exception {
    LaskentakaavaDTO laskentakaava = new LaskentakaavaDTO();
    laskentakaava.setOnLuonnos(false);
    FunktiokutsuDTO kutsu =
        createSumma(createLukuarvo("viisi"), createLukuarvo("10.0"), createLukuarvo("100.0"));

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

    assertEquals(26, kaavat.size());

    for (LaskentakaavaListDTO lk : kaavat) {
      assertFalse(lk.getOnLuonnos());
    }
  }

  @Test
  public void testTallennaTulosUpdate() throws Exception {
    final LaskentakaavaCreateDTO laskentakaavaOriginal = newLaskentakaava("Test");
    laskentakaavaOriginal.setNimi("Test");
    laskentakaavaOriginal.setOnLuonnos(false);
    laskentakaavaOriginal.setFunktiokutsu(createSumma(createLukuarvo("5")));
    laskentakaavaOriginal.getFunktiokutsu().setTallennaTulos(false);
    laskentakaavaOriginal.getFunktiokutsu().setTulosTunniste(null);
    laskentakaavaOriginal.getFunktiokutsu().setTulosTekstiEn(null);
    laskentakaavaOriginal.getFunktiokutsu().setTulosTekstiSv(null);
    laskentakaavaOriginal.getFunktiokutsu().setTulosTekstiFi(null);

    ResponseEntity<LaskentakaavaDTO> response = insert(laskentakaavaOriginal);
    final LaskentakaavaDTO laskentakaavaInserted = kaavaFromResponse(response);
    assertFalse(laskentakaavaInserted.getFunktiokutsu().getTallennaTulos());
    assertNull(laskentakaavaInserted.getFunktiokutsu().getTulosTunniste());
    assertNull(laskentakaavaInserted.getFunktiokutsu().getTulosTekstiEn());
    assertNull(laskentakaavaInserted.getFunktiokutsu().getTulosTekstiFi());
    assertNull(laskentakaavaInserted.getFunktiokutsu().getTulosTekstiSv());

    laskentakaavaInserted.getFunktiokutsu().setTallennaTulos(true);
    laskentakaavaInserted.getFunktiokutsu().setTulosTunniste("t1");
    laskentakaavaInserted.getFunktiokutsu().setTulosTekstiEn("en");
    laskentakaavaInserted.getFunktiokutsu().setTulosTekstiFi("fi");
    laskentakaavaInserted.getFunktiokutsu().setTulosTekstiSv("sv");

    response =
        laskentakaavaResource.update(laskentakaavaInserted.getId(), laskentakaavaInserted, request);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    mapper.writerWithView(JsonViews.Basic.class).writeValueAsString(response.getBody());
    final LaskentakaavaDTO laskentakaavaUpdated = kaavaFromResponse(response);
    assertTrue(laskentakaavaUpdated.getFunktiokutsu().getTallennaTulos());
    assertEquals("t1", laskentakaavaUpdated.getFunktiokutsu().getTulosTunniste());
    assertEquals("en", laskentakaavaUpdated.getFunktiokutsu().getTulosTekstiEn());
    assertEquals("fi", laskentakaavaUpdated.getFunktiokutsu().getTulosTekstiFi());
    assertEquals("sv", laskentakaavaUpdated.getFunktiokutsu().getTulosTekstiSv());
  }

  @Test
  public void testUpdateName() throws Exception {
    final LaskentakaavaDTO inserted = kaavaFromResponse(insert(newLaskentakaava("kaava1")));
    inserted.setNimi("kaava2");
    final LaskentakaavaDTO updated =
        kaavaFromResponse(laskentakaavaResource.update(inserted.getId(), inserted, request));
    assertEquals("kaava2", updated.getNimi());

    tarkistaFunktiokutsunNimi(updated);
    tarkistaFunktiokutsunNimi(laskentakaavaResource.kaava(inserted.getId(), true));
  }

  @Test
  public void testSiirra() throws Exception {
    final LaskentakaavaDTO inserted = kaavaFromResponse(insert(newLaskentakaava("kaava1")));
    LaskentakaavaSiirraDTO siirrettava = modelMapper.map(inserted, LaskentakaavaSiirraDTO.class);
    siirrettava.setUusinimi("UusiNimi");
    siirrettava.setValintaryhmaOid("oid2");
    final LaskentakaavaDTO siirretty =
        kaavaFromResponse(laskentakaavaResource.siirra(siirrettava, request));
    tarkistaFunktiokutsunNimi(siirretty);
    tarkistaFunktiokutsunNimi(laskentakaavaResource.kaava(siirretty.getId(), true));
    final List<LaskentakaavaListDTO> kaavatUudellaValintaryhmalla =
        laskentakaavaResource.kaavat(false, "oid2", null, null);
    assertEquals(1, kaavatUudellaValintaryhmalla.size());
  }

  private void tarkistaFunktiokutsunNimi(final LaskentakaavaDTO kaava) {
    final SyoteparametriDTO parametri =
        kaava.getFunktiokutsu().getSyoteparametrit().iterator().next();
    assertEquals("nimi", parametri.getAvain());
    assertEquals(kaava.getNimi(), parametri.getArvo());
  }

  @Test
  public void testGetTotuusarvokaava() {
    List<LaskentakaavaListDTO> kaavat =
        laskentakaavaResource.kaavat(
            false,
            null,
            null,
            fi.vm.sade.service.valintaperusteet.dto.model.Funktiotyyppi.TOTUUSARVOFUNKTIO);
    assertEquals(3, kaavat.size());

    for (LaskentakaavaListDTO kaava : kaavat) {
      assertEquals(
          fi.vm.sade.service.valintaperusteet.dto.model.Funktiotyyppi.TOTUUSARVOFUNKTIO,
          kaava.getTyyppi());
      assertFalse(kaava.getOnLuonnos());
    }
  }

  private LaskentakaavaCreateDTO newLaskentakaava(final String name) throws IOException {
    LaskentakaavaCreateDTO laskentakaava = new LaskentakaavaCreateDTO();
    laskentakaava.setNimi(name);
    laskentakaava.setOnLuonnos(false);
    final FunktiokutsuDTO funktiokutsu =
        createSumma(createLukuarvo("5.0"), createLukuarvo("10.0"), createLukuarvo("100.0"));
    funktiokutsu.getSyoteparametrit().add(new SyoteparametriDTO("nimi", name));
    laskentakaava.setFunktiokutsu(funktiokutsu);
    return serializeAndDeserialize(laskentakaava);
  }
  ;

  private LaskentakaavaDTO kaavaFromResponse(
      final ResponseEntity<LaskentakaavaDTO> insertResponse) {
    return insertResponse.getBody();
  }

  private LaskentakaavaCreateDTO serializeAndDeserialize(final LaskentakaavaCreateDTO laskentakaava)
      throws java.io.IOException {
    final String json =
        mapper.writerWithView(JsonViews.Basic.class).writeValueAsString(laskentakaava);
    return mapper.readValue(json, LaskentakaavaCreateDTO.class);
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
}
