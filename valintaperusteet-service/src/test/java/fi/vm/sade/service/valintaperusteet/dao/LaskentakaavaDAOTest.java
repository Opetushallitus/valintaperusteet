package fi.vm.sade.service.valintaperusteet.dao;

import fi.vm.sade.kaava.Funktiokuvaaja;
import fi.vm.sade.service.valintaperusteet.annotation.DataSetLocation;
import fi.vm.sade.service.valintaperusteet.dto.FunktioargumentinLapsiDTO;
import fi.vm.sade.service.valintaperusteet.dto.FunktioargumenttiDTO;
import fi.vm.sade.service.valintaperusteet.dto.FunktiokutsuDTO;
import fi.vm.sade.service.valintaperusteet.dto.LaskentakaavaCreateDTO;
import fi.vm.sade.service.valintaperusteet.dto.SyoteparametriDTO;
import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi;
import fi.vm.sade.service.valintaperusteet.dto.model.Funktiotyyppi;
import fi.vm.sade.service.valintaperusteet.listeners.ValinnatJTACleanInsertTestExecutionListener;
import fi.vm.sade.service.valintaperusteet.model.Funktioargumentti;
import fi.vm.sade.service.valintaperusteet.model.Laskentakaava;
import fi.vm.sade.service.valintaperusteet.model.LaskentakaavaId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@ContextConfiguration(locations = "classpath:test-context.xml")
@TestExecutionListeners(
    listeners = {
      DependencyInjectionTestExecutionListener.class,
      DirtiesContextTestExecutionListener.class,
      ValinnatJTACleanInsertTestExecutionListener.class
    })
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@DataSetLocation("classpath:test-data.xml")
public class LaskentakaavaDAOTest {

  @Autowired private LaskentakaavaDAO laskentakaavaDAO;

  @Test
  public void testGetLaskentakaava() {
    LaskentakaavaId id = new LaskentakaavaId(204);

    Laskentakaava laskentakaava = laskentakaavaDAO.read(id);
    assertEquals(id, laskentakaava.getId());
    assertEquals(Funktionimi.MAKSIMI, laskentakaava.getFunktiokutsu().getFunktionimi());
  }

  @Test
  public void testInsert() {
    LaskentakaavaCreateDTO dto = new LaskentakaavaCreateDTO();
    dto.setNimi("kaava123");
    dto.setOnLuonnos(false);
    dto.setFunktiokutsu(createSumma(
            createLukuarvo(5.0),
            createLukuarvo(10.0),
            createLukuarvo(100.0)
    ));
    Laskentakaava tallennettu = laskentakaavaDAO.insert(dto, null, null, null);
    Laskentakaava haettu = laskentakaavaDAO.read(tallennettu.getId());
    assertFalse(tallennettu.getOnLuonnos());
    assertFalse(haettu.getOnLuonnos());
    assertEquals(Funktionimi.SUMMA, tallennettu.getFunktiokutsu().getFunktionimi());
    assertEquals(Funktionimi.SUMMA, haettu.getFunktiokutsu().getFunktionimi());
    assertEquals(3, tallennettu.getFunktiokutsu().getFunktioargumentit().size());
    assertEquals(3, haettu.getFunktiokutsu().getFunktioargumentit().size());

    for (Funktioargumentti fa : tallennettu.getFunktiokutsu().getFunktioargumentit()) {
      assertEquals(Funktionimi.LUKUARVO, fa.getFunktiokutsuChild().getFunktionimi());
    }
    for (Funktioargumentti fa : haettu.getFunktiokutsu().getFunktioargumentit()) {
      assertEquals(Funktionimi.LUKUARVO, fa.getFunktiokutsuChild().getFunktionimi());
    }
  }

  @Test
  public void testLaskentakaavatRootLevel() {
    List<Laskentakaava> kaavas = laskentakaavaDAO.findKaavas(true, null, null, null);
    assertEquals(27, kaavas.size());
  }

  @Test
  public void testLaskentakaavatForValintaryhma() {
    List<Laskentakaava> kaavas = laskentakaavaDAO.findKaavas(true, "oid1", null, null);
    assertEquals(3, kaavas.size());
  }

  @Test
  public void testLaskentakaavatForHakukohde() {
    List<Laskentakaava> kaavas = laskentakaavaDAO.findKaavas(true, null, "oid1", null);
    assertEquals(1, kaavas.size());
  }

  @Test
  public void testLaskentakaavatByTyyppi() {
    List<Laskentakaava> kaavas =
        laskentakaavaDAO.findKaavas(true, null, null, Funktiotyyppi.LUKUARVOFUNKTIO);
    assertEquals(23, kaavas.size());
    kaavas = laskentakaavaDAO.findKaavas(true, null, null, Funktiotyyppi.TOTUUSARVOFUNKTIO);
    assertEquals(4, kaavas.size());
  }

  private FunktiokutsuDTO createLukuarvo(double luku) {
    Funktiokuvaaja.Funktiokuvaus funktiokuvaus = Funktiokuvaaja.annaFunktiokuvaus(Funktionimi.LUKUARVO)._2();
    FunktiokutsuDTO funktiokutsu = new FunktiokutsuDTO();
    funktiokutsu.setFunktionimi(Funktionimi.LUKUARVO);
    SyoteparametriDTO syoteparametri = new SyoteparametriDTO();
    syoteparametri.setAvain(funktiokuvaus.syoteparametrit().head().avain());
    syoteparametri.setArvo(Double.toString(luku));

    funktiokutsu.getSyoteparametrit().add(syoteparametri);

    return funktiokutsu;
  }

  private FunktiokutsuDTO createSumma(FunktiokutsuDTO... args) {
    FunktiokutsuDTO funktiokutsu = new FunktiokutsuDTO();
    funktiokutsu.setFunktionimi(Funktionimi.SUMMA);

    for (int i = 0; i < args.length; ++i) {
      FunktioargumenttiDTO arg = new FunktioargumenttiDTO();
      FunktioargumentinLapsiDTO lapsi = new FunktioargumentinLapsiDTO(args[i]);
      arg.setLapsi(lapsi);
      arg.setIndeksi(i + 1);
      funktiokutsu.getFunktioargumentit().add(arg);
    }

    return funktiokutsu;
  }
}
