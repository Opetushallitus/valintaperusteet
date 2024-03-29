package fi.vm.sade.service.valintaperusteet.service;

import static org.junit.jupiter.api.Assertions.*;

import fi.vm.sade.kaava.Funktiokuvaaja;
import fi.vm.sade.service.valintaperusteet.WithSpringBoot;
import fi.vm.sade.service.valintaperusteet.annotation.DataSetLocation;
import fi.vm.sade.service.valintaperusteet.dao.FunktiokutsuDAO;
import fi.vm.sade.service.valintaperusteet.dto.FunktioargumentinLapsiDTO;
import fi.vm.sade.service.valintaperusteet.dto.FunktioargumenttiDTO;
import fi.vm.sade.service.valintaperusteet.dto.FunktiokutsuDTO;
import fi.vm.sade.service.valintaperusteet.dto.HakukohteenValintaperusteAvaimetDTO;
import fi.vm.sade.service.valintaperusteet.dto.LaskentakaavaCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.LaskentakaavaListDTO;
import fi.vm.sade.service.valintaperusteet.dto.LaskentakaavaSiirraDTO;
import fi.vm.sade.service.valintaperusteet.dto.SyoteparametriDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintaperusteDTO;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi;
import fi.vm.sade.service.valintaperusteet.dto.model.Funktiotyyppi;
import fi.vm.sade.service.valintaperusteet.dto.model.Laskentamoodi;
import fi.vm.sade.service.valintaperusteet.dto.model.Valintaperustelahde;
import fi.vm.sade.service.valintaperusteet.model.Arvokonvertteriparametri;
import fi.vm.sade.service.valintaperusteet.model.Funktioargumentti;
import fi.vm.sade.service.valintaperusteet.model.Funktiokutsu;
import fi.vm.sade.service.valintaperusteet.model.HakukohdeViite;
import fi.vm.sade.service.valintaperusteet.model.Laskentakaava;
import fi.vm.sade.service.valintaperusteet.model.Syoteparametri;
import fi.vm.sade.service.valintaperusteet.model.ValintaperusteViite;
import fi.vm.sade.service.valintaperusteet.model.Valintaryhma;
import fi.vm.sade.service.valintaperusteet.service.exception.FunktiokutsuMuodostaaSilmukanException;
import fi.vm.sade.service.valintaperusteet.service.exception.FunktiokutsuaEiVoidaKayttaaValintakoelaskennassaException;
import fi.vm.sade.service.valintaperusteet.service.exception.LaskentakaavaEiOleOlemassaException;
import fi.vm.sade.service.valintaperusteet.service.exception.LaskentakaavaMuodostaaSilmukanException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/** User: kwuoti Date: 21.1.2013 Time: 9.42 */
@DataSetLocation("classpath:test-data.xml")
public class LaskentakaavaServiceTest extends WithSpringBoot {

  @Autowired private LaskentakaavaService laskentakaavaService;

  @Autowired private FunktiokutsuDAO funktiokutsuDAO;

  @Autowired private ValintaperusteetModelMapper modelMapper;

  private static final FunktioArgumenttiComparator comparator = new FunktioArgumenttiComparator();

  private static class FunktioArgumenttiComparator implements Comparator<Funktioargumentti> {

    @Override
    public int compare(Funktioargumentti o1, Funktioargumentti o2) {
      return o1.getIndeksi().compareTo(o2.getIndeksi());
    }
  }

  private static List<Funktioargumentti> argsSorted(Set<Funktioargumentti> set) {
    List<Funktioargumentti> args = new ArrayList<Funktioargumentti>(set);
    Collections.sort(args, comparator);

    return args;
  }

  private static final double DELTA = 0.000000000000001d;

  @Test
  public void testHaeKaava() {
    final Long id = 204L;
    Laskentakaava laskentakaava = laskentakaavaService.haeMallinnettuKaava(id);
    Funktiokutsu maksimi204L = laskentakaava.getFunktiokutsu();
    assertEquals(
        fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi.MAKSIMI,
        maksimi204L.getFunktionimi());
    assertEquals(2, maksimi204L.getFunktioargumentit().size());
    List<Funktioargumentti> maksimi204Largs = argsSorted(maksimi204L.getFunktioargumentit());

    Funktiokutsu summa203L = maksimi204Largs.get(0).getFunktiokutsuChild();
    Funktiokutsu luku6L = maksimi204Largs.get(1).getFunktiokutsuChild();
    assertEquals(Funktionimi.SUMMA, summa203L.getFunktionimi());
    assertEquals(Funktionimi.LUKUARVO, luku6L.getFunktionimi());

    assertEquals(3, summa203L.getFunktioargumentit().size());
    assertEquals(0, luku6L.getFunktioargumentit().size());

    List<Funktioargumentti> summa203Largs = argsSorted(summa203L.getFunktioargumentit());

    Funktiokutsu summa201L = summa203Largs.get(0).getFunktiokutsuChild();
    Funktiokutsu tulo202L = summa203Largs.get(1).getFunktiokutsuChild();
    Funktiokutsu luku5L = summa203Largs.get(2).getFunktiokutsuChild();

    assertEquals(Funktionimi.SUMMA, summa201L.getFunktionimi());
    assertEquals(Funktionimi.TULO, tulo202L.getFunktionimi());
    assertEquals(Funktionimi.LUKUARVO, luku5L.getFunktionimi());

    assertEquals(3, summa201L.getFunktioargumentit().size());
    assertEquals(2, tulo202L.getFunktioargumentit().size());
    assertEquals(0, luku5L.getFunktioargumentit().size());

    List<Funktioargumentti> summa201Largs = argsSorted(summa201L.getFunktioargumentit());
    List<Funktioargumentti> tulo202LLargs = argsSorted(tulo202L.getFunktioargumentit());

    Funktiokutsu luku1L = summa201Largs.get(0).getFunktiokutsuChild();
    Funktiokutsu luku2L = summa201Largs.get(1).getFunktiokutsuChild();
    Funktiokutsu haeLukuarvo405L = summa201Largs.get(2).getFunktiokutsuChild();

    Funktiokutsu luku3L = tulo202LLargs.get(0).getFunktiokutsuChild();
    Funktiokutsu luku4L = tulo202LLargs.get(1).getFunktiokutsuChild();

    assertEquals(Funktionimi.LUKUARVO, luku1L.getFunktionimi());
    assertEquals(Funktionimi.LUKUARVO, luku2L.getFunktionimi());
    assertEquals(Funktionimi.HAELUKUARVO, haeLukuarvo405L.getFunktionimi());

    assertEquals(Funktionimi.LUKUARVO, luku3L.getFunktionimi());
    assertEquals(Funktionimi.LUKUARVO, luku4L.getFunktionimi());

    assertEquals(0, luku1L.getFunktioargumentit().size());
    assertEquals(0, luku2L.getFunktioargumentit().size());
    assertEquals(0, haeLukuarvo405L.getFunktioargumentit().size());
    assertEquals(1, haeLukuarvo405L.getValintaperusteviitteet().size());
    ValintaperusteViite haeLukuarvo405LValintaperuste =
        haeLukuarvo405L.getValintaperusteviitteet().iterator().next();
    assertEquals("aidinkieli", haeLukuarvo405LValintaperuste.getTunniste());

    assertEquals(0, luku3L.getFunktioargumentit().size());
    assertEquals(0, luku4L.getFunktioargumentit().size());
  }

  @Test
  public void testHaeSyotettavanarvontyyppi() {
    final Long id = 420L;
    Laskentakaava laskentakaava = laskentakaavaService.haeMallinnettuKaava(id);
    Funktiokutsu haelukuarvo204L = laskentakaava.getFunktiokutsu();
    ValintaperusteViite viite = haelukuarvo204L.getValintaperusteviitteet().iterator().next();
    assertTrue(viite.getTilastoidaan());
    assertEquals("syotettavanarvontyypit_valintakoe", viite.getSyotettavanarvontyyppi().getUri());
  }

  private FunktiokutsuDTO createLukuarvo(Double luku) {
    final Funktionimi nimi = Funktionimi.LUKUARVO;

    final Funktiokuvaaja.Funktiokuvaus funktiokuvaus = Funktiokuvaaja.annaFunktiokuvaus(nimi)._2();

    FunktiokutsuDTO funktiokutsu = new FunktiokutsuDTO();
    funktiokutsu.setFunktionimi(fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi.LUKUARVO);
    funktiokutsu.setTallennaTulos(false);

    SyoteparametriDTO syoteparametri = new SyoteparametriDTO();
    syoteparametri.setAvain(funktiokuvaus.syoteparametrit().head().avain());
    syoteparametri.setArvo(luku.toString());

    funktiokutsu.getSyoteparametrit().add(syoteparametri);

    return funktiokutsu;
  }

  private FunktiokutsuDTO createSumma(FunktiokutsuDTO... args) {
    FunktiokutsuDTO funktiokutsu = new FunktiokutsuDTO();
    funktiokutsu.setFunktionimi(fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi.SUMMA);
    funktiokutsu.setTallennaTulos(false);

    for (int i = 0; i < args.length; ++i) {
      FunktioargumentinLapsiDTO f = modelMapper.map(args[i], FunktioargumentinLapsiDTO.class);
      FunktioargumenttiDTO arg = new FunktioargumenttiDTO();
      arg.setLapsi(f);
      arg.setIndeksi(i + 1);
      funktiokutsu.getFunktioargumentit().add(arg);
      f.setLapsityyppi(FunktioargumentinLapsiDTO.FUNKTIOKUTSUTYYPPI);
    }

    return funktiokutsu;
  }

  @Test
  public void testInsertNew() {
    LaskentakaavaCreateDTO laskentakaava = new LaskentakaavaCreateDTO();
    laskentakaava.setNimi("kaava3342");
    laskentakaava.setOnLuonnos(false);
    laskentakaava.setFunktiokutsu(
        createSumma(createLukuarvo(5.0), createLukuarvo(10.0), createLukuarvo(100.0)));

    Laskentakaava tallennettu = laskentakaavaService.insert(laskentakaava, null, null);

    Laskentakaava haettu = laskentakaavaService.haeMallinnettuKaava(tallennettu.getId());
    assertFalse(haettu.getOnLuonnos());
    assertEquals(Funktionimi.SUMMA, haettu.getFunktiokutsu().getFunktionimi());
    assertEquals(3, haettu.getFunktiokutsu().getFunktioargumentit().size());

    for (Funktioargumentti fa : haettu.getFunktiokutsu().getFunktioargumentit()) {
      assertEquals(Funktionimi.LUKUARVO, fa.getFunktiokutsuChild().getFunktionimi());
    }
  }

  private double luku(Funktiokutsu lukufunktio) {
    if (!Funktionimi.LUKUARVO.equals(lukufunktio.getFunktionimi())
        || lukufunktio.getSyoteparametrit().size() != 1) {
      throw new RuntimeException("Illegal lukuarvo");
    }

    Syoteparametri syoteparametri = lukufunktio.getSyoteparametrit().iterator().next();
    return Double.parseDouble(syoteparametri.getArvo());
  }

  @Test
  public void testUpdate() {
    final Long id = 500L;

    final double uusiLukuarvo = 8.0;
    Laskentakaava paivitetty = null;
    {
      Laskentakaava laskentakaava = laskentakaavaService.haeMallinnettuKaava(id);

      assertTrue(laskentakaavaService.onkoKaavaValidi(laskentakaava));

      Funktiokutsu summa500L = laskentakaava.getFunktiokutsu();
      assertEquals(Funktionimi.SUMMA, summa500L.getFunktionimi());
      assertEquals(4, summa500L.getFunktioargumentit().size());

      List<Funktioargumentti> summa500Largs = argsSorted(summa500L.getFunktioargumentit());
      Funktiokutsu luku501L = summa500Largs.get(0).getFunktiokutsuChild();
      Funktiokutsu luku502L = summa500Largs.get(1).getFunktiokutsuChild();
      Funktiokutsu summa503L = summa500Largs.get(2).getFunktiokutsuChild();
      Funktiokutsu summa506L = summa500Largs.get(3).getFunktiokutsuChild();

      assertEquals(Funktionimi.LUKUARVO, luku501L.getFunktionimi());
      assertEquals(10.0, luku(luku501L), DELTA);
      assertEquals(Funktionimi.LUKUARVO, luku502L.getFunktionimi());
      assertEquals(3.0, luku(luku502L), DELTA);
      assertEquals(Funktionimi.SUMMA, summa503L.getFunktionimi());
      assertEquals(2, summa503L.getFunktioargumentit().size());
      assertEquals(Funktionimi.SUMMA, summa506L.getFunktionimi());
      assertEquals(2, summa506L.getFunktioargumentit().size());

      List<Funktioargumentti> summa503Largs = argsSorted(summa503L.getFunktioargumentit());
      Funktiokutsu haeMerkkijonoJaKonvertoiLukuarvoksi504L =
          summa503Largs.get(0).getFunktiokutsuChild();
      Funktiokutsu luku505L = summa503Largs.get(1).getFunktiokutsuChild();

      assertEquals(
          Funktionimi.HAEMERKKIJONOJAKONVERTOILUKUARVOKSI,
          haeMerkkijonoJaKonvertoiLukuarvoksi504L.getFunktionimi());
      assertEquals(Funktionimi.LUKUARVO, luku505L.getFunktionimi());
      assertEquals(6.0, luku(luku505L), DELTA);
      assertEquals(
          5, haeMerkkijonoJaKonvertoiLukuarvoksi504L.getArvokonvertteriparametrit().size());

      List<Funktioargumentti> summa506Largs = argsSorted(summa506L.getFunktioargumentit());
      Funktiokutsu luku7L = summa506Largs.get(0).getFunktiokutsuChild();
      Funktiokutsu luku8L = summa506Largs.get(1).getFunktiokutsuChild();
      assertEquals(Funktionimi.LUKUARVO, luku7L.getFunktionimi());
      assertEquals(Funktionimi.LUKUARVO, luku8L.getFunktionimi());
      assertEquals(7.0, luku(luku7L), DELTA);
      assertEquals(8.0, luku(luku8L), DELTA);

      // Tehdään päivityksiä laskentakaavaan. Lisätään olemassa oleva luku
      // jälkimmäiseen summa-operaatioon.
      Funktioargumentti uusiFunktioargumentti = new Funktioargumentti();
      uusiFunktioargumentti.setFunktiokutsuChild(luku501L);
      uusiFunktioargumentti.setIndeksi(3);
      summa503L.getFunktioargumentit().add(uusiFunktioargumentti);

      // Vaihdetaan luvun arvo
      Syoteparametri lukuparam = luku501L.getSyoteparametrit().iterator().next();
      lukuparam.setArvo(String.valueOf(uusiLukuarvo));

      // Rakennetaan ensimmäiselle summa-operaatiolle uudet argumentit
      // siten, että jälkimmäinen luku ja viimeinen
      // summa-operaatio poistetaan argumenttilistasta
      summa500L.getFunktioargumentit().clear();

      Funktioargumentti summa500L1arg = new Funktioargumentti();
      summa500L1arg.setFunktiokutsuChild(luku501L);
      summa500L1arg.setIndeksi(1);
      Funktioargumentti summa500L2arg = new Funktioargumentti();
      summa500L2arg.setFunktiokutsuChild(summa503L);
      summa500L2arg.setIndeksi(2);

      summa500L.getFunktioargumentit().add(summa500L1arg);
      summa500L.getFunktioargumentit().add(summa500L2arg);

      assertFalse(summa500L.getTallennaTulos());
      summa500L.setTallennaTulos(true);

      // Poistetaan vielä yksi konvertteriparametri konvertointifunktiosta
      Arvokonvertteriparametri param =
          haeMerkkijonoJaKonvertoiLukuarvoksi504L.getArvokonvertteriparametrit().iterator().next();
      haeMerkkijonoJaKonvertoiLukuarvoksi504L.getArvokonvertteriparametrit().remove(param);

      assertNotNull(funktiokutsuDAO.getFunktiokutsu(502L));
      assertNotNull(funktiokutsuDAO.getFunktiokutsu(506L));
      assertNotNull(funktiokutsuDAO.getFunktiokutsu(7L));
      assertNotNull(funktiokutsuDAO.getFunktiokutsu(8L));
      LaskentakaavaCreateDTO dto = modelMapper.map(laskentakaava, LaskentakaavaCreateDTO.class);
      paivitetty = laskentakaavaService.update(laskentakaava.getId(), dto);
      // Akka hoitaa poiston
      //            assertNull(funktiokutsuDAO.getFunktiokutsu(502L));
      //            assertNull(funktiokutsuDAO.getFunktiokutsu(506L));
      assertNotNull(funktiokutsuDAO.getFunktiokutsu(7L));
      assertNotNull(funktiokutsuDAO.getFunktiokutsu(8L));
      assertTrue(paivitetty.getFunktiokutsu().getTallennaTulos());
    }

    Funktiokutsu summa500Lp = paivitetty.getFunktiokutsu();
    assertEquals(Funktionimi.SUMMA, summa500Lp.getFunktionimi());
    assertEquals(2, summa500Lp.getFunktioargumentit().size());

    List<Funktioargumentti> summa500Lpargs = argsSorted(summa500Lp.getFunktioargumentit());
    Funktiokutsu luku501Lp = summa500Lpargs.get(0).getFunktiokutsuChild();
    Funktiokutsu summa503Lp = summa500Lpargs.get(1).getFunktiokutsuChild();

    assertEquals(Funktionimi.LUKUARVO, luku501Lp.getFunktionimi());
    assertEquals(uusiLukuarvo, luku(luku501Lp), DELTA);
    assertEquals(Funktionimi.SUMMA, summa503Lp.getFunktionimi());
    assertEquals(3, summa503Lp.getFunktioargumentit().size());

    List<Funktioargumentti> summa503Lpargs = argsSorted(summa503Lp.getFunktioargumentit());
    Funktiokutsu haeMerkkijonoJaKonvertoiLukuarvoksi504Lp =
        summa503Lpargs.get(0).getFunktiokutsuChild();
    Funktiokutsu luku505Lp = summa503Lpargs.get(1).getFunktiokutsuChild();
    Funktiokutsu luku501Lp2 = summa503Lpargs.get(2).getFunktiokutsuChild();

    assertEquals(
        Funktionimi.HAEMERKKIJONOJAKONVERTOILUKUARVOKSI,
        haeMerkkijonoJaKonvertoiLukuarvoksi504Lp.getFunktionimi());
    assertEquals(Funktionimi.LUKUARVO, luku505Lp.getFunktionimi());
    assertEquals(6.0, luku(luku505Lp), DELTA);
    assertEquals(Funktionimi.LUKUARVO, luku501Lp2.getFunktionimi());
    assertEquals(uusiLukuarvo, luku(luku501Lp2), DELTA);
  }

  private FunktiokutsuDTO nimettyFunktiokutsu(String nimi, FunktiokutsuDTO child) {
    FunktiokutsuDTO nimetty = new FunktiokutsuDTO();
    nimetty.setTallennaTulos(false);

    FunktioargumenttiDTO arg = new FunktioargumenttiDTO();
    FunktioargumentinLapsiDTO lapsiDTO = modelMapper.map(child, FunktioargumentinLapsiDTO.class);
    lapsiDTO.setLapsityyppi(FunktioargumentinLapsiDTO.FUNKTIOKUTSUTYYPPI);
    arg.setLapsi(lapsiDTO);
    arg.setIndeksi(1);
    nimetty.getFunktioargumentit().add(arg);

    if (Funktiotyyppi.LUKUARVOFUNKTIO.equals(child.getFunktionimi().getTyyppi())) {
      nimetty.setFunktionimi(
          fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi.NIMETTYLUKUARVO);
    } else {
      nimetty.setFunktionimi(
          fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi.NIMETTYTOTUUSARVO);
    }

    SyoteparametriDTO syoteparametri = new SyoteparametriDTO();
    syoteparametri.setAvain("nimi");
    syoteparametri.setArvo(nimi);
    nimetty.getSyoteparametrit().add(syoteparametri);

    return nimetty;
  }

  @Test
  public void lisaaKaavaJokaViittaaToiseenKaavaan() {

    final Long tallennettuKaavaId = 206L;
    Laskentakaava tallennettu = null;
    {
      LaskentakaavaListDTO tallennettuKaava =
          modelMapper.map(
              laskentakaavaService.haeMallinnettuKaava(tallennettuKaavaId),
              LaskentakaavaListDTO.class);

      FunktiokutsuDTO summa = createSumma(createLukuarvo(1.0), createLukuarvo(2.0));
      FunktioargumenttiDTO kaavaArg = new FunktioargumenttiDTO();
      FunktioargumentinLapsiDTO lapsiDTO =
          modelMapper.map(tallennettuKaava, FunktioargumentinLapsiDTO.class);
      lapsiDTO.setLapsityyppi(FunktioargumentinLapsiDTO.LASKENTAKAAVATYYPPI);
      kaavaArg.setLapsi(lapsiDTO);
      kaavaArg.setIndeksi(summa.getFunktioargumentit().size() + 1);
      summa.getFunktioargumentit().add(kaavaArg);

      final String nimi = "kaavasummaus";

      FunktiokutsuDTO nimettyFunktiokutsu = nimettyFunktiokutsu(nimi, summa);

      LaskentakaavaCreateDTO laskentakaava = new LaskentakaavaCreateDTO();
      laskentakaava.setNimi(nimi);
      laskentakaava.setFunktiokutsu(nimettyFunktiokutsu);
      laskentakaava.setKuvaus("");
      laskentakaava.setOnLuonnos(false);

      tallennettu = laskentakaavaService.insert(laskentakaava, null, null);
    }

    Laskentakaava haettu = laskentakaavaService.haeMallinnettuKaava(tallennettu.getId());

    Funktiokutsu nimetty = haettu.getFunktiokutsu();
    assertEquals(Funktionimi.NIMETTYLUKUARVO, nimetty.getFunktionimi());
    assertEquals(1, nimetty.getFunktioargumentit().size());

    Funktioargumentti nimettyArg = nimetty.getFunktioargumentit().iterator().next();
    assertNotNull(nimettyArg.getFunktiokutsuChild());

    Funktiokutsu summa = nimettyArg.getFunktiokutsuChild();
    assertEquals(Funktionimi.SUMMA, summa.getFunktionimi());
    assertEquals(3, summa.getFunktioargumentit().size());

    List<Funktioargumentti> summaArgs = argsSorted(summa.getFunktioargumentit());
    assertNotNull(summaArgs.get(0).getFunktiokutsuChild());
    assertNotNull(summaArgs.get(1).getFunktiokutsuChild());
    assertNotNull(summaArgs.get(2).getLaskentakaavaChild());

    assertEquals(Funktionimi.LUKUARVO, summaArgs.get(0).getFunktiokutsuChild().getFunktionimi());
    assertEquals(Funktionimi.LUKUARVO, summaArgs.get(1).getFunktiokutsuChild().getFunktionimi());
    assertEquals(
        laskentakaavaService.haeMallinnettuKaava(tallennettuKaavaId),
        summaArgs.get(2).getLaskentakaavaChild());
  }

  @Test
  public void testHaeLaskettavaKaava() throws FunktiokutsuMuodostaaSilmukanException {

    final Long tulo = 512L;
    {
      Funktiokutsu funktiokutsu = laskentakaavaService.haeMallinnettuFunktiokutsu(tulo);
      assertEquals(tulo, funktiokutsu.getId());
      assertEquals(Funktionimi.TULO, funktiokutsu.getFunktionimi());
      assertEquals(2, funktiokutsu.getFunktioargumentit().size());

      List<Funktioargumentti> args = argsSorted(funktiokutsu.getFunktioargumentit());
      assertNotNull(args.get(0).getLaskentakaavaChild());
      assertNull(args.get(0).getFunktiokutsuChild());

      assertNotNull(args.get(1).getFunktiokutsuChild());
      assertNull(args.get(1).getLaskentakaavaChild());
    }

    {
      final Long laskentakaavaId = 510L;

      Laskentakaava laskentakaava =
          laskentakaavaService.haeLaskettavaKaava(laskentakaavaId, Laskentamoodi.VALINTALASKENTA);
      Funktiokutsu nimetty513L = laskentakaava.getFunktiokutsu();

      assertEquals(Funktionimi.NIMETTYLUKUARVO, nimetty513L.getFunktionimi());
      assertEquals(1, nimetty513L.getFunktioargumentit().size());

      Funktiokutsu tulo512L =
          argsSorted(nimetty513L.getFunktioargumentit()).get(0).getFunktiokutsuChild();
      assertEquals(tulo, tulo512L.getId());
      assertEquals(Funktionimi.TULO, tulo512L.getFunktionimi());
      assertEquals(2, tulo512L.getFunktioargumentit().size());

      List<Funktioargumentti> tulo512Largs = argsSorted(tulo512L.getFunktioargumentit());
      Funktiokutsu nimetty510L = tulo512Largs.get(0).getLaajennettuKaava();
      Funktiokutsu luku511L = tulo512Largs.get(1).getFunktiokutsuChild();

      assertEquals(Funktionimi.NIMETTYLUKUARVO, nimetty510L.getFunktionimi());
      assertEquals(Funktionimi.LUKUARVO, luku511L.getFunktionimi());

      assertEquals(1, nimetty510L.getFunktioargumentit().size());
      Funktiokutsu summa507L =
          argsSorted(nimetty510L.getFunktioargumentit()).get(0).getFunktiokutsuChild();
      assertEquals(Funktionimi.SUMMA, summa507L.getFunktionimi());

      assertEquals(2, summa507L.getFunktioargumentit().size());
      List<Funktioargumentti> summa507Largs = argsSorted(summa507L.getFunktioargumentit());
      Funktiokutsu luku508L = summa507Largs.get(0).getFunktiokutsuChild();
      Funktiokutsu luku509L = summa507Largs.get(1).getFunktiokutsuChild();

      assertEquals(Funktionimi.LUKUARVO, luku508L.getFunktionimi());
      assertEquals(Funktionimi.LUKUARVO, luku509L.getFunktionimi());
    }
  }

  @Test
  public void haeMallinnettuLaskentakaava() {
    final Long laskentakaavaId = 510L;

    Laskentakaava laskentakaava =
        laskentakaavaService.haeLaskettavaKaava(laskentakaavaId, Laskentamoodi.VALINTALASKENTA);
    Funktiokutsu nimetty513L = laskentakaava.getFunktiokutsu();

    assertEquals(Funktionimi.NIMETTYLUKUARVO, nimetty513L.getFunktionimi());
    assertEquals(1, nimetty513L.getFunktioargumentit().size());

    Funktiokutsu tulo512L =
        argsSorted(nimetty513L.getFunktioargumentit()).get(0).getFunktiokutsuChild();
    assertEquals(Funktionimi.TULO, tulo512L.getFunktionimi());
    assertEquals(2, tulo512L.getFunktioargumentit().size());

    List<Funktioargumentti> tulo512Largs = argsSorted(tulo512L.getFunktioargumentit());
    Laskentakaava laskentakaava509L = tulo512Largs.get(0).getLaskentakaavaChild();
    Funktiokutsu luku511L = tulo512Largs.get(1).getFunktiokutsuChild();

    assertEquals(Funktiotyyppi.LUKUARVOFUNKTIO, laskentakaava509L.getTyyppi());
    assertEquals(Funktionimi.LUKUARVO, luku511L.getFunktionimi());
  }

  @Test
  public void testFindAvaimetForHakukohde() {
    for (int i = 0; i < 10; i++) {
      List<ValintaperusteDTO> valintaperusteet =
          laskentakaavaService.findAvaimetForHakukohde("oid17");
      assertSyotettavaArvoHakukohde17(valintaperusteet);
    }
  }

  @Test
  public void testFindAvaimetForHakukohteet() {
    Map<String, List<ValintaperusteDTO>> valintaperusteet =
        laskentakaavaService.findAvaimetForHakukohteet(Arrays.asList("oid17"));
    assertEquals(1, valintaperusteet.size());
    assertSyotettavaArvoHakukohde17(valintaperusteet.get("oid17"));
  }

  private void assertSyotettavaArvoHakukohde17(List<ValintaperusteDTO> valintaperusteet) {
    assertEquals(2, valintaperusteet.size());

    Collections.sort(
        valintaperusteet,
        new Comparator<ValintaperusteDTO>() {
          @Override
          public int compare(ValintaperusteDTO o1, ValintaperusteDTO o2) {
            return o1.getTunniste().compareTo(o2.getTunniste());
          }
        });

    assertEquals("valintaperuste1", valintaperusteet.get(0).getTunniste());
    assertEquals("valintaperuste2", valintaperusteet.get(1).getTunniste());

    assertEquals(5, valintaperusteet.get(0).getArvot().size());
    assertNull(valintaperusteet.get(0).getMax());
    assertNull(valintaperusteet.get(0).getMin());
    assertEquals(new BigDecimal("0.0"), new BigDecimal(valintaperusteet.get(1).getMin()));
    assertEquals(new BigDecimal("30.0"), new BigDecimal(valintaperusteet.get(1).getMax()));
    assertNull(valintaperusteet.get(1).getArvot());
    assertEquals(Valintaperustelahde.SYOTETTAVA_ARVO, valintaperusteet.get(1).getLahde());
    assertTrue(valintaperusteet.get(0).getTilastoidaan());
    assertNotNull(valintaperusteet.get(0).getSyötettavanArvonTyyppi());
    assertEquals(
        "syotettavanarvontyypit_valintakoe",
        valintaperusteet.get(0).getSyötettavanArvonTyyppi().getUri());
  }

  @Test
  public void testFindHakukohdeValintaperusteAvaimet() {
    HakukohteenValintaperusteAvaimetDTO valintaperusteet =
        laskentakaavaService.findHakukohteenAvaimet("oid23");
    assertEquals(2, valintaperusteet.getTunnisteet().size());
    assertEquals(2, valintaperusteet.getArvot().size());
    assertEquals(1, valintaperusteet.getHylkaysperusteet().size());
    assertEquals(3, valintaperusteet.getMinimit().size());
    assertEquals(2, valintaperusteet.getMaksimit().size());
    assertEquals(1, valintaperusteet.getPalautaHaettutArvot().size());
  }

  @Test
  public void testItseensaViittaavaKaava() {
    // Kaava 415 viittaa kaavaan 414. Asetetaan kaava 414 viittaamaan
    // takaisin kaavaan 415, jolloin saadaan
    // silmukka muodostettua.

    final Long alakaavaId = 414L;
    final Long ylakaavaId = 415L;

    {
      Laskentakaava ylakaava =
          laskentakaavaService.haeLaskettavaKaava(ylakaavaId, Laskentamoodi.VALINTALASKENTA);
      assertEquals(Funktionimi.SUMMA, ylakaava.getFunktiokutsu().getFunktionimi());

      Funktiokutsu ylaFunktiokutsu = ylakaava.getFunktiokutsu();
      assertEquals(2, ylaFunktiokutsu.getFunktioargumentit().size());

      List<Funktioargumentti> ylafunktioArgs = argsSorted(ylaFunktiokutsu.getFunktioargumentit());
      assertNotNull(ylafunktioArgs.get(1).getLaskentakaavaChild());
      assertEquals(alakaavaId, ylafunktioArgs.get(1).getLaskentakaavaChild().getId());
    }
    {
      Laskentakaava alakaava =
          laskentakaavaService.haeLaskettavaKaava(alakaavaId, Laskentamoodi.VALINTALASKENTA);
      assertEquals(Funktionimi.SUMMA, alakaava.getFunktiokutsu().getFunktionimi());

      Funktiokutsu alaFunktiokutsu = alakaava.getFunktiokutsu();
      assertEquals(2, alaFunktiokutsu.getFunktioargumentit().size());

      List<Funktioargumentti> alafunktioArgs = argsSorted(alaFunktiokutsu.getFunktioargumentit());
      assertNull(alafunktioArgs.get(0).getLaskentakaavaChild());
      assertNull(alafunktioArgs.get(1).getLaskentakaavaChild());
    }

    Laskentakaava alakaava =
        laskentakaavaService.haeLaskettavaKaava(alakaavaId, Laskentamoodi.VALINTALASKENTA);

    Laskentakaava laskentakaavaViite = new Laskentakaava();
    laskentakaavaViite.setId(ylakaavaId);
    laskentakaavaViite.setTyyppi(Funktiotyyppi.LUKUARVOFUNKTIO);
    laskentakaavaViite.setOnLuonnos(false);

    Funktioargumentti arg = new Funktioargumentti();
    arg.setIndeksi(3);
    arg.setLaskentakaavaChild(laskentakaavaViite);

    alakaava.getFunktiokutsu().getFunktioargumentit().add(arg);

    boolean caught = false;
    try {
      laskentakaavaService.update(
          alakaavaId, modelMapper.map(alakaava, LaskentakaavaCreateDTO.class));
    } catch (LaskentakaavaMuodostaaSilmukanException e) {
      caught = true;

      assertEquals(e.getFunktiokutsuId().longValue(), 701L);
      assertEquals(e.getParentLaskentakaavaId(), alakaavaId);
      assertEquals(e.getViitattuLaskentakaavaId(), alakaavaId);
    }

    assertTrue(caught);
  }

  @Test
  public void testHaeLaskettavaKaavaVaarallaMoodilla() {
    final Long laskentakaavaId = 417L;
    assertThrows(
        FunktiokutsuaEiVoidaKayttaaValintakoelaskennassaException.class,
        () ->
            laskentakaavaService.haeLaskettavaKaava(
                laskentakaavaId, Laskentamoodi.VALINTAKOELASKENTA));
  }

  @Test
  public void testSiirra() {
    LaskentakaavaCreateDTO laskentakaava = new LaskentakaavaCreateDTO();
    laskentakaava.setNimi("kaava3342");
    laskentakaava.setOnLuonnos(false);
    laskentakaava.setFunktiokutsu(
        createSumma(createLukuarvo(5.0), createLukuarvo(10.0), createLukuarvo(100.0)));

    Laskentakaava tallennettu = laskentakaavaService.insert(laskentakaava, null, "oid1");

    Laskentakaava haettu = laskentakaavaService.haeMallinnettuKaava(tallennettu.getId());
    assertFalse(haettu.getOnLuonnos());
    assertEquals(Funktionimi.SUMMA, haettu.getFunktiokutsu().getFunktionimi());
    assertEquals(3, haettu.getFunktiokutsu().getFunktioargumentit().size());

    for (Funktioargumentti fa : haettu.getFunktiokutsu().getFunktioargumentit()) {
      assertEquals(Funktionimi.LUKUARVO, fa.getFunktiokutsuChild().getFunktionimi());
    }

    LaskentakaavaSiirraDTO siirrettava = modelMapper.map(tallennettu, LaskentakaavaSiirraDTO.class);
    siirrettava.setUusinimi("UusiNimi");
    siirrettava.setValintaryhmaOid("oid2");

    Laskentakaava siirretty = laskentakaavaService.siirra(siirrettava).get();

    assertFalse(siirretty.getOnLuonnos());
    assertEquals(Funktionimi.SUMMA, siirretty.getFunktiokutsu().getFunktionimi());
    assertEquals(3, siirretty.getFunktiokutsu().getFunktioargumentit().size());

    for (Funktioargumentti fa : siirretty.getFunktiokutsu().getFunktioargumentit()) {
      assertEquals(Funktionimi.LUKUARVO, fa.getFunktiokutsuChild().getFunktionimi());
    }

    assertEquals("UusiNimi", siirretty.getNimi());
    assertEquals("oid2", siirretty.getValintaryhma().getOid());
    assertNotEquals(haettu.getId(), siirretty.getId());
  }

  @Test
  public void testSiirraAliKaavallinen() {

    final Long alakaavaId = 414L;
    final Long ylakaavaId = 415L;

    final Laskentakaava vanhaYlakaava = laskentakaavaService.haeMallinnettuKaava(ylakaavaId);
    assertEquals(2, vanhaYlakaava.getFunktiokutsu().getFunktioargumentit().size());
    Laskentakaava vanhaAlikaava =
        vanhaYlakaava.getFunktiokutsu().getFunktioargumentit().stream()
            .filter(kaava -> kaava.getLaskentakaavaChild() != null)
            .findFirst()
            .get()
            .getLaskentakaavaChild();
    assertEquals(alakaavaId, vanhaAlikaava.getId());

    LaskentakaavaSiirraDTO siirrettava =
        modelMapper.map(vanhaYlakaava, LaskentakaavaSiirraDTO.class);

    siirrettava.setUusinimi(ylakaavaId + " kopio");
    siirrettava.setValintaryhmaOid("oid2");

    Laskentakaava siirretty = laskentakaavaService.siirra(siirrettava).get();

    assertFalse(siirretty.getOnLuonnos());
    assertEquals(Funktionimi.SUMMA, siirretty.getFunktiokutsu().getFunktionimi());
    assertEquals(2, siirretty.getFunktiokutsu().getFunktioargumentit().size());

    assertEquals("415 kopio", siirretty.getNimi());
    assertEquals("oid2", siirretty.getValintaryhma().getOid());
    assertNotEquals(vanhaYlakaava.getId(), siirretty.getId());

    Laskentakaava siirrettyAlikaava =
        siirretty.getFunktiokutsu().getFunktioargumentit().stream()
            .filter(kaava -> kaava.getLaskentakaavaChild() != null)
            .findFirst()
            .get()
            .getLaskentakaavaChild();
    assertNotEquals(vanhaAlikaava.getId(), siirrettyAlikaava.getId());
    assertEquals("summakaava414", siirrettyAlikaava.getNimi());
    assertEquals("oid2", siirrettyAlikaava.getValintaryhma().getOid());
  }

  @Test
  public void testSiirraValintaRyhmaaEiLoydy() {
    LaskentakaavaCreateDTO laskentakaava = new LaskentakaavaCreateDTO();
    laskentakaava.setNimi("kaava3342");
    laskentakaava.setOnLuonnos(false);
    laskentakaava.setFunktiokutsu(
        createSumma(createLukuarvo(5.0), createLukuarvo(10.0), createLukuarvo(100.0)));

    Laskentakaava tallennettu = laskentakaavaService.insert(laskentakaava, null, "oid1");

    Laskentakaava haettu = laskentakaavaService.haeMallinnettuKaava(tallennettu.getId());
    assertFalse(haettu.getOnLuonnos());
    assertEquals(Funktionimi.SUMMA, haettu.getFunktiokutsu().getFunktionimi());
    assertEquals(3, haettu.getFunktiokutsu().getFunktioargumentit().size());

    for (Funktioargumentti fa : haettu.getFunktiokutsu().getFunktioargumentit()) {
      assertEquals(Funktionimi.LUKUARVO, fa.getFunktiokutsuChild().getFunktionimi());
    }

    LaskentakaavaSiirraDTO siirrettava = modelMapper.map(tallennettu, LaskentakaavaSiirraDTO.class);
    siirrettava.setUusinimi("UusiNimi");
    siirrettava.setValintaryhmaOid("jeppis");

    Optional<Laskentakaava> siirretty = laskentakaavaService.siirra(siirrettava);

    assertFalse(siirretty.isPresent());
  }

  @Test
  public void testPoistaKaava() {
    final Long id = 204L;
    Laskentakaava laskentakaava = laskentakaavaService.haeMallinnettuKaava(id);
    Funktiokutsu maksimi204L = laskentakaava.getFunktiokutsu();
    assertEquals(
        fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi.MAKSIMI,
        maksimi204L.getFunktionimi());
    assertEquals(2, maksimi204L.getFunktioargumentit().size());
    laskentakaavaService.poista(id);
    assertThrows(
        LaskentakaavaEiOleOlemassaException.class,
        () -> laskentakaavaService.haeMallinnettuKaava(id));
  }

  @Test
  public void poistaKaavaJohonToinenKaavaViittaa() {

    final Long tallennettuKaavaId = 206L;
    Laskentakaava tallennettu = null;
    {
      LaskentakaavaListDTO tallennettuKaava =
          modelMapper.map(
              laskentakaavaService.haeMallinnettuKaava(tallennettuKaavaId),
              LaskentakaavaListDTO.class);

      FunktiokutsuDTO summa = createSumma(createLukuarvo(1.0), createLukuarvo(2.0));
      FunktioargumenttiDTO kaavaArg = new FunktioargumenttiDTO();
      FunktioargumentinLapsiDTO lapsiDTO =
          modelMapper.map(tallennettuKaava, FunktioargumentinLapsiDTO.class);
      lapsiDTO.setLapsityyppi(FunktioargumentinLapsiDTO.LASKENTAKAAVATYYPPI);
      kaavaArg.setLapsi(lapsiDTO);
      kaavaArg.setIndeksi(summa.getFunktioargumentit().size() + 1);
      summa.getFunktioargumentit().add(kaavaArg);

      final String nimi = "kaavasummaus";

      FunktiokutsuDTO nimettyFunktiokutsu = nimettyFunktiokutsu(nimi, summa);

      LaskentakaavaCreateDTO laskentakaava = new LaskentakaavaCreateDTO();
      laskentakaava.setNimi(nimi);
      laskentakaava.setFunktiokutsu(nimettyFunktiokutsu);
      laskentakaava.setKuvaus("");
      laskentakaava.setOnLuonnos(false);

      tallennettu = laskentakaavaService.insert(laskentakaava, null, null);
    }

    Laskentakaava haettu = laskentakaavaService.haeMallinnettuKaava(tallennettu.getId());

    Funktiokutsu nimetty = haettu.getFunktiokutsu();
    assertEquals(Funktionimi.NIMETTYLUKUARVO, nimetty.getFunktionimi());
    assertEquals(1, nimetty.getFunktioargumentit().size());

    Funktioargumentti nimettyArg = nimetty.getFunktioargumentit().iterator().next();
    assertNotNull(nimettyArg.getFunktiokutsuChild());

    Funktiokutsu summa = nimettyArg.getFunktiokutsuChild();
    assertEquals(Funktionimi.SUMMA, summa.getFunktionimi());
    assertEquals(3, summa.getFunktioargumentit().size());

    List<Funktioargumentti> summaArgs = argsSorted(summa.getFunktioargumentit());
    assertNotNull(summaArgs.get(0).getFunktiokutsuChild());
    assertNotNull(summaArgs.get(1).getFunktiokutsuChild());
    assertNotNull(summaArgs.get(2).getLaskentakaavaChild());

    assertEquals(Funktionimi.LUKUARVO, summaArgs.get(0).getFunktiokutsuChild().getFunktionimi());
    assertEquals(Funktionimi.LUKUARVO, summaArgs.get(1).getFunktiokutsuChild().getFunktionimi());
    assertEquals(
        laskentakaavaService.haeMallinnettuKaava(tallennettuKaavaId),
        summaArgs.get(2).getLaskentakaavaChild());

    boolean poistettu = laskentakaavaService.poista(tallennettuKaavaId);
    assertFalse(poistettu);
  }

  // BUG-1313
  @Test
  public void laskentakaavaPeriytyyOikein() {
    final Long id = 204L;
    Laskentakaava l = laskentakaavaService.haeMallinnettuKaava(id);
    Valintaryhma v1 = new Valintaryhma();
    Valintaryhma v2 = new Valintaryhma();
    Valintaryhma v3 = new Valintaryhma();
    HakukohdeViite h = new HakukohdeViite();
    h.setValintaryhma(v3);
    v1.getLaskentakaava().add(l);
    v2.setYlavalintaryhma(v1);
    v3.setYlavalintaryhma(v2);
    Optional<Laskentakaava> res =
        laskentakaavaService.haeLaskentakaavaTaiSenKopioVanhemmilta(l.getId(), h);
    assertTrue(res.isPresent());
    assertEquals(l, res.get());
  }
}
