package fi.vm.sade.service.valintaperusteet.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.querydsl.core.Tuple;
import fi.vm.sade.kaava.Funktiokuvaaja;
import fi.vm.sade.service.valintaperusteet.WithSpringBoot;
import fi.vm.sade.service.valintaperusteet.annotation.DataSetLocation;
import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi;
import fi.vm.sade.service.valintaperusteet.dto.model.Funktiotyyppi;
import fi.vm.sade.service.valintaperusteet.model.Funktioargumentti;
import fi.vm.sade.service.valintaperusteet.model.Funktiokutsu;
import fi.vm.sade.service.valintaperusteet.model.Laskentakaava;
import fi.vm.sade.service.valintaperusteet.model.QHakukohdeViite;
import fi.vm.sade.service.valintaperusteet.model.QLaskentakaava;
import fi.vm.sade.service.valintaperusteet.model.QValinnanVaihe;
import fi.vm.sade.service.valintaperusteet.model.Syoteparametri;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/** User: kwuoti Date: 28.1.2013 Time: 10.17 */
@Transactional
@DataSetLocation("classpath:test-data.xml")
public class LaskentakaavaDAOTest extends WithSpringBoot {

  @Autowired private LaskentakaavaDAO laskentakaavaDAO;

  @Test
  public void testGetLaskentakaava() {
    final Long id = 204L;

    Laskentakaava laskentakaava = laskentakaavaDAO.getLaskentakaava(id);
    assertEquals(id, laskentakaava.getId());
    assertEquals(Funktionimi.MAKSIMI, laskentakaava.getFunktiokutsu().getFunktionimi());
  }

  private Funktiokutsu createLukuarvo(Double luku) {
    final Funktionimi nimi = Funktionimi.LUKUARVO;

    final Funktiokuvaaja.Funktiokuvaus funktiokuvaus = Funktiokuvaaja.annaFunktiokuvaus(nimi)._2();

    Funktiokutsu funktiokutsu = new Funktiokutsu();
    funktiokutsu.setFunktionimi(Funktionimi.LUKUARVO);

    Syoteparametri syoteparametri = new Syoteparametri();
    syoteparametri.setAvain(funktiokuvaus.syoteparametrit().head().avain());
    syoteparametri.setArvo(luku.toString());
    syoteparametri.setFunktiokutsu(funktiokutsu);

    funktiokutsu.getSyoteparametrit().add(syoteparametri);

    return funktiokutsu;
  }

  private Funktiokutsu createSumma(Funktiokutsu... args) {
    Funktiokutsu funktiokutsu = new Funktiokutsu();
    funktiokutsu.setFunktionimi(Funktionimi.SUMMA);

    for (int i = 0; i < args.length; ++i) {
      Funktiokutsu f = args[i];
      Funktioargumentti arg = new Funktioargumentti();
      arg.setFunktiokutsuChild(f);
      arg.setParent(funktiokutsu);
      arg.setIndeksi(i + 1);
      funktiokutsu.getFunktioargumentit().add(arg);
    }

    return funktiokutsu;
  }

  @Test
  public void testInsert() {
    Laskentakaava laskentakaava = new Laskentakaava();
    laskentakaava.setNimi("kaava123");
    laskentakaava.setOnLuonnos(false);
    laskentakaava.setFunktiokutsu(
        createSumma(createLukuarvo(5.0), createLukuarvo(10.0), createLukuarvo(100.0)));

    Laskentakaava tallennettu = laskentakaavaDAO.insert(laskentakaava);

    Laskentakaava haettu = laskentakaavaDAO.getLaskentakaava(tallennettu.getId());
    assertFalse(haettu.getOnLuonnos());
    assertEquals(Funktionimi.SUMMA, haettu.getFunktiokutsu().getFunktionimi());
    assertEquals(3, haettu.getFunktiokutsu().getFunktioargumentit().size());

    for (Funktioargumentti fa : haettu.getFunktiokutsu().getFunktioargumentit()) {
      assertEquals(Funktionimi.LUKUARVO, fa.getFunktiokutsuChild().getFunktionimi());
    }
  }

  @Test
  public void testFindAvaimet() throws Exception {
    ArrayList<String> oids = new ArrayList<String>();
    oids.add("oid1");
    oids.add("3201");
    oids.add("oid6");
    List<Tuple> avaimet = laskentakaavaDAO.findLaskentakaavatByHakukohde(oids);
    for (Tuple tuple : avaimet) {
      System.out.println(
          tuple.get(QHakukohdeViite.hakukohdeViite.oid)
              + ":"
              + tuple.get(QValinnanVaihe.valinnanVaihe.oid));
      System.out.println(tuple.get(QLaskentakaava.laskentakaava));
    }

    assertEquals(7, avaimet.size());
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
}
