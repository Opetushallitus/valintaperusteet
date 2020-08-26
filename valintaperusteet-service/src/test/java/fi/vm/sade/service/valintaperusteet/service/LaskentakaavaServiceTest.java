package fi.vm.sade.service.valintaperusteet.service;

import fi.vm.sade.kaava.Funktiokuvaaja;
import fi.vm.sade.service.valintaperusteet.annotation.DataSetLocation;
import fi.vm.sade.service.valintaperusteet.dto.ArvokonvertteriparametriDTO;
import fi.vm.sade.service.valintaperusteet.dto.FunktioargumentinLapsiDTO;
import fi.vm.sade.service.valintaperusteet.dto.FunktioargumenttiDTO;
import fi.vm.sade.service.valintaperusteet.dto.FunktiokutsuDTO;
import fi.vm.sade.service.valintaperusteet.dto.HakukohteenValintaperusteAvaimetDTO;
import fi.vm.sade.service.valintaperusteet.dto.LaskentakaavaCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.LaskentakaavaInsertDTO;
import fi.vm.sade.service.valintaperusteet.dto.LaskentakaavaListDTO;
import fi.vm.sade.service.valintaperusteet.dto.LaskentakaavaSiirraDTO;
import fi.vm.sade.service.valintaperusteet.dto.SyoteparametriDTO;
import fi.vm.sade.service.valintaperusteet.dto.ValintaperusteDTO;
import fi.vm.sade.service.valintaperusteet.dto.mapping.ValintaperusteetModelMapper;
import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi;
import fi.vm.sade.service.valintaperusteet.dto.model.Funktiotyyppi;
import fi.vm.sade.service.valintaperusteet.dto.model.Laskentamoodi;
import fi.vm.sade.service.valintaperusteet.dto.model.Valintaperustelahde;
import fi.vm.sade.service.valintaperusteet.listeners.ValinnatJTACleanInsertTestExecutionListener;
import fi.vm.sade.service.valintaperusteet.model.Funktioargumentti;
import fi.vm.sade.service.valintaperusteet.model.Funktiokutsu;
import fi.vm.sade.service.valintaperusteet.model.HakukohdeViiteId;
import fi.vm.sade.service.valintaperusteet.model.Laskentakaava;
import fi.vm.sade.service.valintaperusteet.model.LaskentakaavaId;
import fi.vm.sade.service.valintaperusteet.model.Syoteparametri;
import fi.vm.sade.service.valintaperusteet.model.ValintaperusteViite;
import fi.vm.sade.service.valintaperusteet.service.exception.FunktiokutsuaEiVoidaKayttaaValintakoelaskennassaException;
import fi.vm.sade.service.valintaperusteet.service.exception.LaskentakaavaEiOleOlemassaException;
import fi.vm.sade.service.valintaperusteet.service.exception.LaskentakaavaMuodostaaSilmukanException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/** User: kwuoti Date: 21.1.2013 Time: 9.42 */
@ContextConfiguration(locations = "classpath:test-context.xml")
@TestExecutionListeners(
    listeners = {
      ValinnatJTACleanInsertTestExecutionListener.class,
      DependencyInjectionTestExecutionListener.class,
      DirtiesContextTestExecutionListener.class
    })
@RunWith(SpringJUnit4ClassRunner.class)
@DataSetLocation("classpath:test-data.xml")
public class LaskentakaavaServiceTest {
  @Autowired private LaskentakaavaService laskentakaavaService;

  @Autowired private ValintaperusteetModelMapper modelMapper;

  private static final double DELTA = 0.000000000000001d;

  @Test
  public void testHaeKaava() {
    LaskentakaavaId id = new LaskentakaavaId(204);
    Laskentakaava laskentakaava = laskentakaavaService.haeMallinnettuKaava(id);
    Funktiokutsu maksimi204L = laskentakaava.getFunktiokutsu();
    assertEquals(
        fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi.MAKSIMI,
        maksimi204L.getFunktionimi());
    assertEquals(2, maksimi204L.getFunktioargumentit().size());
    List<Funktioargumentti> maksimi204Largs = maksimi204L.getFunktioargumentit();

    Funktiokutsu summa203L = maksimi204Largs.get(0).getFunktiokutsuChild();
    Funktiokutsu luku6L = maksimi204Largs.get(1).getFunktiokutsuChild();
    assertEquals(Funktionimi.SUMMA, summa203L.getFunktionimi());
    assertEquals(Funktionimi.LUKUARVO, luku6L.getFunktionimi());

    assertEquals(3, summa203L.getFunktioargumentit().size());
    assertEquals(0, luku6L.getFunktioargumentit().size());

    List<Funktioargumentti> summa203Largs = summa203L.getFunktioargumentit();

    Funktiokutsu summa201L = summa203Largs.get(0).getFunktiokutsuChild();
    Funktiokutsu tulo202L = summa203Largs.get(1).getFunktiokutsuChild();
    Funktiokutsu luku5L = summa203Largs.get(2).getFunktiokutsuChild();

    assertEquals(Funktionimi.SUMMA, summa201L.getFunktionimi());
    assertEquals(Funktionimi.TULO, tulo202L.getFunktionimi());
    assertEquals(Funktionimi.LUKUARVO, luku5L.getFunktionimi());

    assertEquals(3, summa201L.getFunktioargumentit().size());
    assertEquals(2, tulo202L.getFunktioargumentit().size());
    assertEquals(0, luku5L.getFunktioargumentit().size());

    List<Funktioargumentti> summa201Largs = summa201L.getFunktioargumentit();
    List<Funktioargumentti> tulo202LLargs = tulo202L.getFunktioargumentit();

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
    LaskentakaavaId id = new LaskentakaavaId(420);
    Laskentakaava laskentakaava = laskentakaavaService.haeMallinnettuKaava(id);
    Funktiokutsu haelukuarvo204L = laskentakaava.getFunktiokutsu();
    ValintaperusteViite viite = haelukuarvo204L.getValintaperusteviitteet().iterator().next();
    assertTrue(viite.isTilastoidaan());
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
      funktiokutsu.getFunktioargumentit().add(new FunktioargumenttiDTO(
              new FunktioargumentinLapsiDTO(args[i]),
              i + 1
      ));
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

    Laskentakaava tallennettu = laskentakaavaService.insert(new LaskentakaavaInsertDTO(laskentakaava, null, "oid2"));

    Laskentakaava haettu = laskentakaavaService.haeMallinnettuKaava(tallennettu.getId());
    assertFalse(haettu.getOnLuonnos());
    assertEquals(Funktionimi.SUMMA, haettu.getFunktiokutsu().getFunktionimi());
    assertEquals(3, haettu.getFunktiokutsu().getFunktioargumentit().size());

    for (Funktioargumentti fa : haettu.getFunktiokutsu().getFunktioargumentit()) {
      assertEquals(Funktionimi.LUKUARVO, fa.getFunktiokutsuChild().getFunktionimi());
    }
  }

  private double luku(FunktioargumentinLapsiDTO lukufunktio) {
    if (!Funktionimi.LUKUARVO.equals(lukufunktio.getFunktionimi())
            || lukufunktio.getSyoteparametrit().size() != 1) {
      throw new RuntimeException("Illegal lukuarvo");
    }

    SyoteparametriDTO syoteparametri = lukufunktio.getSyoteparametrit().iterator().next();
    return Double.parseDouble(syoteparametri.getArvo());
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
    LaskentakaavaId id = new LaskentakaavaId(500);

    final double uusiLukuarvo = 8.0;
    Laskentakaava paivitetty = null;
    {
      LaskentakaavaCreateDTO laskentakaava = modelMapper.lkToCreateDto(laskentakaavaService.haeMallinnettuKaava(id));

      FunktiokutsuDTO summa500L = laskentakaava.getFunktiokutsu();
      assertEquals(Funktionimi.SUMMA, summa500L.getFunktionimi());
      assertEquals(4, summa500L.getFunktioargumentit().size());

      List<FunktioargumenttiDTO> summa500Largs = summa500L.getFunktioargumentit();
      FunktioargumentinLapsiDTO luku501L = summa500Largs.get(0).getLapsi();
      FunktioargumentinLapsiDTO luku502L = summa500Largs.get(1).getLapsi();
      FunktioargumentinLapsiDTO summa503L = summa500Largs.get(2).getLapsi();
      FunktioargumentinLapsiDTO summa506L = summa500Largs.get(3).getLapsi();

      assertEquals(Funktionimi.LUKUARVO, luku501L.getFunktionimi());
      assertEquals(10.0, luku(luku501L), DELTA);
      assertEquals(Funktionimi.LUKUARVO, luku502L.getFunktionimi());
      assertEquals(3.0, luku(luku502L), DELTA);
      assertEquals(Funktionimi.SUMMA, summa503L.getFunktionimi());
      assertEquals(2, summa503L.getFunktioargumentit().size());
      assertEquals(Funktionimi.SUMMA, summa506L.getFunktionimi());
      assertEquals(2, summa506L.getFunktioargumentit().size());

      List<FunktioargumenttiDTO> summa503Largs = summa503L.getFunktioargumentit();
      FunktioargumentinLapsiDTO haeMerkkijonoJaKonvertoiLukuarvoksi504L = summa503Largs.get(0).getLapsi();
      FunktioargumentinLapsiDTO luku505L = summa503Largs.get(1).getLapsi();

      assertEquals(
          Funktionimi.HAEMERKKIJONOJAKONVERTOILUKUARVOKSI,
          haeMerkkijonoJaKonvertoiLukuarvoksi504L.getFunktionimi());
      assertEquals(Funktionimi.LUKUARVO, luku505L.getFunktionimi());
      assertEquals(6.0, luku(luku505L), DELTA);
      assertEquals(
          5, haeMerkkijonoJaKonvertoiLukuarvoksi504L.getArvokonvertteriparametrit().size());

      List<FunktioargumenttiDTO> summa506Largs = summa506L.getFunktioargumentit();
      FunktioargumentinLapsiDTO luku7L = summa506Largs.get(0).getLapsi();
      FunktioargumentinLapsiDTO luku8L = summa506Largs.get(1).getLapsi();
      assertEquals(Funktionimi.LUKUARVO, luku7L.getFunktionimi());
      assertEquals(Funktionimi.LUKUARVO, luku8L.getFunktionimi());
      assertEquals(7.0, luku(luku7L), DELTA);
      assertEquals(8.0, luku(luku8L), DELTA);

      // Tehdään päivityksiä laskentakaavaan. Lisätään olemassa oleva luku
      // jälkimmäiseen summa-operaatioon.
      summa503L.getFunktioargumentit().add(new FunktioargumenttiDTO(luku501L, 3));

      // Vaihdetaan luvun arvo
      SyoteparametriDTO lukuparam = luku501L.getSyoteparametrit().iterator().next();
      lukuparam.setArvo(String.valueOf(uusiLukuarvo));

      // Rakennetaan ensimmäiselle summa-operaatiolle uudet argumentit
      // siten, että jälkimmäinen luku ja viimeinen
      // summa-operaatio poistetaan argumenttilistasta
      summa500L.getFunktioargumentit().clear();
      summa500L.getFunktioargumentit().add(new FunktioargumenttiDTO(luku501L, 1));
      summa500L.getFunktioargumentit().add(new FunktioargumenttiDTO(summa503L, 2));

      assertFalse(summa500L.getTallennaTulos());
      summa500L.setTallennaTulos(true);

      // Poistetaan vielä yksi konvertteriparametri konvertointifunktiosta
      ArvokonvertteriparametriDTO param =
          haeMerkkijonoJaKonvertoiLukuarvoksi504L.getArvokonvertteriparametrit().iterator().next();
      haeMerkkijonoJaKonvertoiLukuarvoksi504L.getArvokonvertteriparametrit().remove(param);

      paivitetty = laskentakaavaService.update(id, laskentakaava).getRight();
      assertTrue(paivitetty.getFunktiokutsu().isTallennaTulos());
    }

    Funktiokutsu summa500Lp = paivitetty.getFunktiokutsu();
    assertEquals(Funktionimi.SUMMA, summa500Lp.getFunktionimi());
    assertEquals(2, summa500Lp.getFunktioargumentit().size());

    List<Funktioargumentti> summa500Lpargs = summa500Lp.getFunktioargumentit();
    Funktiokutsu luku501Lp = summa500Lpargs.get(0).getFunktiokutsuChild();
    Funktiokutsu summa503Lp = summa500Lpargs.get(1).getFunktiokutsuChild();

    assertEquals(Funktionimi.LUKUARVO, luku501Lp.getFunktionimi());
    assertEquals(uusiLukuarvo, luku(luku501Lp), DELTA);
    assertEquals(Funktionimi.SUMMA, summa503Lp.getFunktionimi());
    assertEquals(3, summa503Lp.getFunktioargumentit().size());

    List<Funktioargumentti> summa503Lpargs = summa503Lp.getFunktioargumentit();
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

    FunktioargumenttiDTO arg = new FunktioargumenttiDTO(
            new FunktioargumentinLapsiDTO(child),
            1
    );
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
    Laskentakaava viitattuKaava = laskentakaavaService.haeMallinnettuKaava(new LaskentakaavaId(206));

    FunktiokutsuDTO funktiokutsu = createSumma(createLukuarvo(1.0), createLukuarvo(2.0));
    funktiokutsu.getFunktioargumentit().add(new FunktioargumenttiDTO(
            new FunktioargumentinLapsiDTO(modelMapper.lkToListDto(viitattuKaava)),
            funktiokutsu.getFunktioargumentit().size() + 1
    ));

    Laskentakaava tallennettu = laskentakaavaService.insert(new LaskentakaavaInsertDTO(new LaskentakaavaCreateDTO(
            false,
            "kaavasummaus",
            "",
            nimettyFunktiokutsu("kaavasummaus", funktiokutsu)
    ), null, "oid2"));

    Laskentakaava haettu = laskentakaavaService.haeMallinnettuKaava(tallennettu.getId());

    Funktiokutsu nimetty = haettu.getFunktiokutsu();
    assertEquals(Funktionimi.NIMETTYLUKUARVO, nimetty.getFunktionimi());
    assertEquals(1, nimetty.getFunktioargumentit().size());

    Funktioargumentti nimettyArg = nimetty.getFunktioargumentit().iterator().next();
    assertNotNull(nimettyArg.getFunktiokutsuChild());

    Funktiokutsu summa = nimettyArg.getFunktiokutsuChild();
    assertEquals(Funktionimi.SUMMA, summa.getFunktionimi());
    assertEquals(3, summa.getFunktioargumentit().size());

    List<Funktioargumentti> summaArgs = summa.getFunktioargumentit();
    assertNotNull(summaArgs.get(0).getFunktiokutsuChild());
    assertNotNull(summaArgs.get(1).getFunktiokutsuChild());
    assertNotNull(summaArgs.get(2).getLaskentakaavaChild());

    assertEquals(Funktionimi.LUKUARVO, summaArgs.get(0).getFunktiokutsuChild().getFunktionimi());
    assertEquals(Funktionimi.LUKUARVO, summaArgs.get(1).getFunktiokutsuChild().getFunktionimi());
    assertEquals(viitattuKaava, summaArgs.get(2).getLaskentakaavaChild());
  }

  @Test
  public void haeMallinnettuLaskentakaava() {
    LaskentakaavaId laskentakaavaId = new LaskentakaavaId(510);

    Laskentakaava laskentakaava =
        laskentakaavaService.haeLaskettavaKaava(laskentakaavaId, Laskentamoodi.VALINTALASKENTA);
    Funktiokutsu nimetty513L = laskentakaava.getFunktiokutsu();

    assertEquals(Funktionimi.NIMETTYLUKUARVO, nimetty513L.getFunktionimi());
    assertEquals(1, nimetty513L.getFunktioargumentit().size());

    Funktiokutsu tulo512L = nimetty513L.getFunktioargumentit().get(0).getFunktiokutsuChild();
    assertEquals(Funktionimi.TULO, tulo512L.getFunktionimi());
    assertEquals(2, tulo512L.getFunktioargumentit().size());

    List<Funktioargumentti> tulo512Largs = tulo512L.getFunktioargumentit();
    Laskentakaava laskentakaava509L = tulo512Largs.get(0).getLaskentakaavaChild();
    Funktiokutsu luku511L = tulo512Largs.get(1).getFunktiokutsuChild();

    assertEquals(Funktiotyyppi.LUKUARVOFUNKTIO, laskentakaava509L.getFunktiokutsu().getFunktionimi().getTyyppi());
    assertEquals(Funktionimi.LUKUARVO, luku511L.getFunktionimi());
  }

  @Test
  public void testFindAvaimetForHakukohde() {
    List<ValintaperusteDTO> valintaperusteet = laskentakaavaService.findAvaimetForHakukohde("oid17");
    assertEquals(2, valintaperusteet.size());

    valintaperusteet.sort(Comparator.comparing(ValintaperusteDTO::getTunniste));

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

  @Test(expected = LaskentakaavaMuodostaaSilmukanException.class)
  public void testItseensaViittaavaKaava() {
    // Kaava 415 viittaa kaavaan 414. Asetetaan kaava 414 viittaamaan
    // takaisin kaavaan 415, jolloin saadaan
    // silmukka muodostettua.

    LaskentakaavaId alakaavaId = new LaskentakaavaId(414);
    LaskentakaavaId ylakaavaId = new LaskentakaavaId(415);

    {
      Laskentakaava ylakaava =
          laskentakaavaService.haeLaskettavaKaava(ylakaavaId, Laskentamoodi.VALINTALASKENTA);
      assertEquals(Funktionimi.SUMMA, ylakaava.getFunktiokutsu().getFunktionimi());

      Funktiokutsu ylaFunktiokutsu = ylakaava.getFunktiokutsu();
      assertEquals(2, ylaFunktiokutsu.getFunktioargumentit().size());

      List<Funktioargumentti> ylafunktioArgs = ylaFunktiokutsu.getFunktioargumentit();
      assertNotNull(ylafunktioArgs.get(1).getLaskentakaavaChild());
      assertEquals(alakaavaId, ylafunktioArgs.get(1).getLaskentakaavaChild().getId());
    }
    {
      Laskentakaava alakaava =
          laskentakaavaService.haeLaskettavaKaava(alakaavaId, Laskentamoodi.VALINTALASKENTA);
      assertEquals(Funktionimi.SUMMA, alakaava.getFunktiokutsu().getFunktionimi());

      Funktiokutsu alaFunktiokutsu = alakaava.getFunktiokutsu();
      assertEquals(2, alaFunktiokutsu.getFunktioargumentit().size());

      List<Funktioargumentti> alafunktioArgs = alaFunktiokutsu.getFunktioargumentit();
      assertNull(alafunktioArgs.get(0).getLaskentakaavaChild());
      assertNull(alafunktioArgs.get(1).getLaskentakaavaChild());
    }

    LaskentakaavaCreateDTO alakaava = modelMapper.lkToCreateDto(
            laskentakaavaService.haeLaskettavaKaava(alakaavaId, Laskentamoodi.VALINTALASKENTA));

    FunktioargumenttiDTO arg = new FunktioargumenttiDTO();
    arg.setIndeksi(3);
    arg.setLapsi(new FunktioargumentinLapsiDTO(
            false,
            "nimi",
            "kuvaus",
            Funktiotyyppi.LUKUARVOFUNKTIO,
            ylakaavaId.id
    ));

    alakaava.getFunktiokutsu().getFunktioargumentit().add(arg);

    try {
      laskentakaavaService.update(alakaavaId, alakaava);
    } catch (LaskentakaavaMuodostaaSilmukanException e) {
      assertEquals(e.id, alakaavaId);
      assertEquals(e.takaisinViittaavanId, ylakaavaId);
      throw e;
    }
  }

  @Test(expected = FunktiokutsuaEiVoidaKayttaaValintakoelaskennassaException.class)
  public void testHaeLaskettavaKaavaVaarallaMoodilla() {
    LaskentakaavaId laskentakaavaId = new LaskentakaavaId(417);
    laskentakaavaService.haeLaskettavaKaava(laskentakaavaId, Laskentamoodi.VALINTAKOELASKENTA);
  }

  @Test
  public void testSiirra() {
    LaskentakaavaCreateDTO laskentakaava = new LaskentakaavaCreateDTO();
    laskentakaava.setNimi("kaava3342");
    laskentakaava.setOnLuonnos(false);
    laskentakaava.setFunktiokutsu(
        createSumma(createLukuarvo(5.0), createLukuarvo(10.0), createLukuarvo(100.0)));

    Laskentakaava tallennettu = laskentakaavaService.insert(new LaskentakaavaInsertDTO(laskentakaava, null, "oid1"));

    Laskentakaava haettu = laskentakaavaService.haeMallinnettuKaava(tallennettu.getId());
    assertFalse(haettu.getOnLuonnos());
    assertEquals(Funktionimi.SUMMA, haettu.getFunktiokutsu().getFunktionimi());
    assertEquals(3, haettu.getFunktiokutsu().getFunktioargumentit().size());

    for (Funktioargumentti fa : haettu.getFunktiokutsu().getFunktioargumentit()) {
      assertEquals(Funktionimi.LUKUARVO, fa.getFunktiokutsuChild().getFunktionimi());
    }

    LaskentakaavaSiirraDTO siirrettava = new LaskentakaavaSiirraDTO(
            tallennettu.getOnLuonnos(),
            tallennettu.getNimi(),
            tallennettu.getKuvaus(),
            modelMapper.fkToDto(tallennettu.getFunktiokutsu()),
            "UusiNimi",
            "oid2",
            null
    );

    Laskentakaava siirretty = laskentakaavaService.siirra(siirrettava);

    assertFalse(siirretty.getOnLuonnos());
    assertEquals(Funktionimi.SUMMA, siirretty.getFunktiokutsu().getFunktionimi());
    assertEquals(3, siirretty.getFunktiokutsu().getFunktioargumentit().size());

    for (Funktioargumentti fa : siirretty.getFunktiokutsu().getFunktioargumentit()) {
      assertEquals(Funktionimi.LUKUARVO, fa.getFunktiokutsuChild().getFunktionimi());
    }

    assertEquals("UusiNimi", siirretty.getNimi());
    assertEquals(2, siirretty.getValintaryhmaId().id);
    assertNotEquals(haettu.getId(), siirretty.getId());
  }

  @Test
  public void testSiirraAliKaavallinen() {

    LaskentakaavaId alakaavaId = new LaskentakaavaId(414);
    LaskentakaavaId ylakaavaId = new LaskentakaavaId(415);

    final Laskentakaava vanhaYlakaava = laskentakaavaService.haeMallinnettuKaava(ylakaavaId);
    assertEquals(2, vanhaYlakaava.getFunktiokutsu().getFunktioargumentit().size());
    Laskentakaava vanhaAlikaava =
        vanhaYlakaava.getFunktiokutsu().getFunktioargumentit().stream()
            .filter(kaava -> kaava.getLaskentakaavaChild() != null)
            .findFirst()
            .get()
            .getLaskentakaavaChild();
    assertEquals(alakaavaId, vanhaAlikaava.getId());

    LaskentakaavaSiirraDTO siirrettava = new LaskentakaavaSiirraDTO(
            vanhaYlakaava.getOnLuonnos(),
            vanhaYlakaava.getNimi(),
            vanhaYlakaava.getKuvaus(),
            modelMapper.fkToDto(vanhaYlakaava.getFunktiokutsu()),
            ylakaavaId.id + " kopio",
            "oid2",
            null
    );

    Laskentakaava siirretty = laskentakaavaService.siirra(siirrettava);

    assertFalse(siirretty.getOnLuonnos());
    assertEquals(Funktionimi.SUMMA, siirretty.getFunktiokutsu().getFunktionimi());
    assertEquals(2, siirretty.getFunktiokutsu().getFunktioargumentit().size());

    assertEquals("415 kopio", siirretty.getNimi());
    assertEquals(2, siirretty.getValintaryhmaId().id);
    assertNotEquals(vanhaYlakaava.getId(), siirretty.getId());

    Laskentakaava siirrettyAlikaava =
        siirretty.getFunktiokutsu().getFunktioargumentit().stream()
            .filter(kaava -> kaava.getLaskentakaavaChild() != null)
            .findFirst()
            .get()
            .getLaskentakaavaChild();
    assertNotEquals(vanhaAlikaava.getId(), siirrettyAlikaava.getId());
    assertEquals("summakaava414", siirrettyAlikaava.getNimi());
    assertEquals(2, siirrettyAlikaava.getValintaryhmaId().id);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSiirraValintaRyhmaaEiLoydy() {
    LaskentakaavaCreateDTO laskentakaava = new LaskentakaavaCreateDTO();
    laskentakaava.setNimi("kaava3342");
    laskentakaava.setOnLuonnos(false);
    laskentakaava.setFunktiokutsu(
        createSumma(createLukuarvo(5.0), createLukuarvo(10.0), createLukuarvo(100.0)));

    Laskentakaava tallennettu = laskentakaavaService.insert(new LaskentakaavaInsertDTO(laskentakaava, null, "oid1"));

    Laskentakaava haettu = laskentakaavaService.haeMallinnettuKaava(tallennettu.getId());
    assertFalse(haettu.getOnLuonnos());
    assertEquals(Funktionimi.SUMMA, haettu.getFunktiokutsu().getFunktionimi());
    assertEquals(3, haettu.getFunktiokutsu().getFunktioargumentit().size());

    for (Funktioargumentti fa : haettu.getFunktiokutsu().getFunktioargumentit()) {
      assertEquals(Funktionimi.LUKUARVO, fa.getFunktiokutsuChild().getFunktionimi());
    }

    LaskentakaavaSiirraDTO siirrettava = new LaskentakaavaSiirraDTO(
            tallennettu.getOnLuonnos(),
            tallennettu.getNimi(),
            tallennettu.getKuvaus(),
            modelMapper.fkToDto(tallennettu.getFunktiokutsu()),
            "UusiNimi",
            "jeppis",
            null
    );

    laskentakaavaService.siirra(siirrettava);
  }

  @Test(expected = LaskentakaavaEiOleOlemassaException.class)
  public void testPoistaKaava() {
    LaskentakaavaId id = new LaskentakaavaId(204);
    Laskentakaava laskentakaava = laskentakaavaService.haeMallinnettuKaava(id);
    Funktiokutsu maksimi204L = laskentakaava.getFunktiokutsu();
    assertEquals(Funktionimi.MAKSIMI, maksimi204L.getFunktionimi());
    assertEquals(2, maksimi204L.getFunktioargumentit().size());
    laskentakaavaService.poista(id);
    laskentakaavaService.haeMallinnettuKaava(id);
  }

  @Test
  public void poistaKaavaJohonToinenKaavaViittaa() {

    LaskentakaavaId tallennettuKaavaId = new LaskentakaavaId(206);
    Laskentakaava tallennettu = null;
    {
      LaskentakaavaListDTO tallennettuKaava =
              modelMapper.lkToListDto(laskentakaavaService.haeMallinnettuKaava(tallennettuKaavaId));

      FunktiokutsuDTO summa = createSumma(createLukuarvo(1.0), createLukuarvo(2.0));
      FunktioargumenttiDTO kaavaArg = new FunktioargumenttiDTO(
              new FunktioargumentinLapsiDTO(tallennettuKaava),
              summa.getFunktioargumentit().size() + 1
      );
      summa.getFunktioargumentit().add(kaavaArg);

      final String nimi = "kaavasummaus";

      FunktiokutsuDTO nimettyFunktiokutsu = nimettyFunktiokutsu(nimi, summa);

      LaskentakaavaCreateDTO laskentakaava = new LaskentakaavaCreateDTO();
      laskentakaava.setNimi(nimi);
      laskentakaava.setFunktiokutsu(nimettyFunktiokutsu);
      laskentakaava.setKuvaus("");
      laskentakaava.setOnLuonnos(false);

      tallennettu = laskentakaavaService.insert(new LaskentakaavaInsertDTO(laskentakaava, null, "oid2"));
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

    List<Funktioargumentti> summaArgs = summa.getFunktioargumentit();
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
    Set<SyoteparametriDTO> syoteparametrit = new HashSet<>();
    syoteparametrit.add(new SyoteparametriDTO(
            "totuusarvo",
            "true"
    ));
    LaskentakaavaId id = laskentakaavaService.insert(new LaskentakaavaInsertDTO(
            new LaskentakaavaCreateDTO(
                    false,
                    "nimi",
                    "kuvaus",
                    new FunktiokutsuDTO(
                            Funktionimi.TOTUUSARVO,
                            "tunniste",
                            "fi",
                            "sv",
                            "en",
                            false,
                            false,
                            new HashSet<>(),
                            new ArrayList<>(),
                            syoteparametrit,
                            new ArrayList<>(),
                            new ArrayList<>(),
                            new ArrayList<>()
                    )
            ),
            null,
            "oid8"
    )).getId();

    assertEquals(id, laskentakaavaService.kopioiJosEiJoKopioitu(id, new HakukohdeViiteId(8)));
  }
}
