package fi.vm.sade.kaava

import org.scalatest.FunSuite
import fi.vm.sade.kaava.LaskentaTestUtil._
import fi.vm.sade.service.valintaperusteet.laskenta.Laskin
import fi.vm.sade.service.valintaperusteet.laskenta.api.tila._
import scala.collection.JavaConversions._
import java.math.BigDecimal
import fi.vm.sade.service.valintaperusteet.laskenta.api.{Hakemus, Laskentatulos, Hakukohde, Osallistuminen}
import fi.vm.sade.service.valintaperusteet.laskenta.api.tila.VirheMetatieto.VirheMetatietotyyppi
import java.util
import java.lang.Boolean
import fi.vm.sade.kaava.LaskentaTestUtil.TestHakemus
import fi.vm.sade.service.valintaperusteet.dto.model.{Valintaperustelahde, Funktionimi}

/**
 * User: kwuoti
 * Date: 4.2.2013
 * Time: 14.13
 */
class LaskentaIntegraatioTest extends FunSuite {

  val luku25 = Funktiokutsu(
    nimi = Funktionimi.LUKUARVO,
    syoteparametrit = List(
      Syoteparametri("luku", "25.0")))

  val luku50 = Funktiokutsu(
    nimi = Funktionimi.LUKUARVO,
    syoteparametrit = List(
      Syoteparametri("luku", "50.0")))

  val luku45 = Funktiokutsu(
    nimi = Funktionimi.LUKUARVO,
    syoteparametrit = List(
      Syoteparametri("luku", "45.0")))

  val luku100 = Funktiokutsu(
    nimi = Funktionimi.LUKUARVO,
    syoteparametrit = List(
      Syoteparametri("luku", "100.0")))

  val luku0 = Funktiokutsu(
    nimi = Funktionimi.LUKUARVO,
    syoteparametrit = List(
      Syoteparametri("luku", "0.0")))

  val luku3_335 = Funktiokutsu(
    nimi = Funktionimi.LUKUARVO,
    syoteparametrit = List(
      Syoteparametri("luku", "3.335")))

  val totuusarvoTrue = Funktiokutsu(
    nimi = Funktionimi.TOTUUSARVO,
    syoteparametrit = List(
      Syoteparametri("totuusarvo", "true")))

  val totuusarvoFalse = Funktiokutsu(
    nimi = Funktionimi.TOTUUSARVO,
    syoteparametrit = List(
      Syoteparametri("totuusarvo", "false")))

  val summa = Funktiokutsu(
    nimi = Funktionimi.SUMMA,
    funktioargumentit = List(
      luku25,
      luku50,
      luku45))

  val konvertoiLukuarvoLukuarvoksi = Funktiokutsu(
    nimi = Funktionimi.KONVERTOILUKUARVO,
    funktioargumentit = List(luku25),
    arvokonvertterit = List(
      Arvokonvertteriparametri(
        paluuarvo = "1.0",
        arvo = "10.0",
        hylkaysperuste = "false"),
      Arvokonvertteriparametri(
        paluuarvo = "2.0",
        arvo = "15.0",
        hylkaysperuste = "false"),
      Arvokonvertteriparametri(
        paluuarvo = "3.0",
        arvo = "20.0",
        hylkaysperuste = "false"),
      Arvokonvertteriparametri(
        paluuarvo = "4.0",
        arvo = "25.0",
        hylkaysperuste = "false"),
      Arvokonvertteriparametri(
        paluuarvo = "5.0",
        arvo = "30.0",
        hylkaysperuste = "false")))

  val maksimi = Funktiokutsu(
    nimi = Funktionimi.MAKSIMI,
    funktioargumentit = List(
      luku25,
      luku50,
      luku45))

  val minimi = Funktiokutsu(
    nimi = Funktionimi.MINIMI,
    funktioargumentit = List(
      luku25,
      luku50,
      luku45))

  val tulo = Funktiokutsu(
    nimi = Funktionimi.TULO,
    funktioargumentit = List(
      luku25,
      luku50,
      luku45))

  val konvertoiLukuarvovaliLukuarvoksi = Funktiokutsu(
    nimi = Funktionimi.KONVERTOILUKUARVO,
    funktioargumentit = List(luku25),
    arvovalikonvertterit = List(
      Arvovalikonvertteriparametri(
        paluuarvo = "1.0",
        min = "0.0",
        max = "10.0",
        palautaHaettuArvo = "false"),
      Arvovalikonvertteriparametri(
        paluuarvo = "3.0",
        min = "10.0",
        max = "20.0",
        palautaHaettuArvo = "false"),
      Arvovalikonvertteriparametri(
        paluuarvo = "3.0",
        min = "20.0",
        max = "30.0",
        palautaHaettuArvo = "false"),
      Arvovalikonvertteriparametri(
        paluuarvo = "4.0",
        min = "30.0",
        max = "40.0",
        palautaHaettuArvo = "false")))

  val keskiarvo = Funktiokutsu(
    nimi = Funktionimi.KESKIARVO,
    funktioargumentit = List(
      luku25,
      luku50,
      luku45))

  val mediaaniParillinenMaara = Funktiokutsu(
    nimi = Funktionimi.MEDIAANI,
    funktioargumentit = List(
      luku25,
      luku100,
      luku50,
      luku45))

  val mediaaniParitonMaara = Funktiokutsu(
    nimi = Funktionimi.MEDIAANI,
    funktioargumentit = List(
      luku25,
      luku100,
      luku45))

  val keskiarvoNParasta = Funktiokutsu(
    nimi = Funktionimi.KESKIARVONPARASTA,
    syoteparametrit = List(
      Syoteparametri(
        avain = "n",
        arvo = "2")),
    funktioargumentit = List(
      luku25,
      luku100,
      luku45,
      luku50))

  val summaNParasta = Funktiokutsu(
    nimi = Funktionimi.SUMMANPARASTA,
    syoteparametrit = List(
      Syoteparametri(
        avain = "n",
        arvo = "3")),
    funktioargumentit = List(
      luku25,
      luku100,
      luku45,
      luku50))

  val nMaksimi = Funktiokutsu(
    nimi = Funktionimi.NMAKSIMI,
    syoteparametrit = List(
      Syoteparametri(
        avain = "n",
        arvo = "3")),
    funktioargumentit = List(
      luku25,
      luku100,
      luku45,
      luku50))

  val nMinimi = Funktiokutsu(
    nimi = Funktionimi.NMINIMI,
    syoteparametrit = List(
      Syoteparametri(
        avain = "n",
        arvo = "3")),
    funktioargumentit = List(
      luku25,
      luku100,
      luku45,
      luku50))

  val osamaara = Funktiokutsu(
    nimi = Funktionimi.OSAMAARA,
    funktioargumentit = List(
      luku100,
      luku50))

  val osamaaraByZero = Funktiokutsu(
    nimi = Funktionimi.OSAMAARA,
    funktioargumentit = List(
      luku100,
      luku0))

  val suurempiTrue = Funktiokutsu(
    nimi = Funktionimi.SUUREMPI,
    funktioargumentit = List(
      luku100,
      luku50))

  val suurempiFalse = Funktiokutsu(
    nimi = Funktionimi.SUUREMPI,
    funktioargumentit = List(
      luku25,
      luku50))

  val suurempiTaiYhtasuuriTrue = Funktiokutsu(
    nimi = Funktionimi.SUUREMPITAIYHTASUURI,
    funktioargumentit = List(
      luku25,
      luku25))

  val suurempiTaiYhtasuuriFalse = Funktiokutsu(
    nimi = Funktionimi.SUUREMPITAIYHTASUURI,
    funktioargumentit = List(
      luku0,
      luku25))

  val pienempiTrue = Funktiokutsu(
    nimi = Funktionimi.PIENEMPI,
    funktioargumentit = List(
      luku0,
      luku25))

  val pienempiFalse = Funktiokutsu(
    nimi = Funktionimi.PIENEMPI,
    funktioargumentit = List(
      luku50,
      luku25))

  val pienempiTaiYhtasuuriTrue = Funktiokutsu(
    nimi = Funktionimi.PIENEMPITAIYHTASUURI,
    funktioargumentit = List(
      luku25,
      luku25))

  val pienempiTaiYhtasuuriFalse = Funktiokutsu(
    nimi = Funktionimi.PIENEMPITAIYHTASUURI,
    funktioargumentit = List(
      luku100,
      luku50))

  val yhtasuuriTrue = Funktiokutsu(
    nimi = Funktionimi.YHTASUURI,
    funktioargumentit = List(
      luku25,
      luku25))

  val yhtasuuriFalse = Funktiokutsu(
    nimi = Funktionimi.YHTASUURI,
    funktioargumentit = List(
      luku25,
      luku0))

  val ei = Funktiokutsu(
    nimi = Funktionimi.EI,
    funktioargumentit = List(
      totuusarvoFalse))

  val nimettyTotuusarvo = Funktiokutsu(
    nimi = Funktionimi.NIMETTYTOTUUSARVO,
    syoteparametrit = List(
      Syoteparametri(
        avain = "nimi",
        arvo = "tässä on true arvo")),
    funktioargumentit = List(
      totuusarvoTrue))

  val nimettyLukuarvo = Funktiokutsu(
    nimi = Funktionimi.NIMETTYLUKUARVO,
    syoteparametrit = List(
      Syoteparametri(
        avain = "nimi",
        arvo = "tässä on lukuarvo 25")),
    funktioargumentit = List(
      luku25))

  val haeLukuarvo = Funktiokutsu(
    nimi = Funktionimi.HAELUKUARVO,
    valintaperustetunniste = List(ValintaperusteViite(
      onPakollinen = false,
      tunniste = "tunniste")))

  val haeTotuusarvo = Funktiokutsu(
    nimi = Funktionimi.HAETOTUUSARVO,
    valintaperustetunniste = List(ValintaperusteViite(
      onPakollinen = false,
      tunniste = "tunniste")))

  val hakukohde = new Hakukohde("123", new util.HashMap[String, String])
  val tyhjaHakemus = TestHakemus("", Nil, Map[String, String]())

  test("lukuarvo") {
    val funktiokutsu = luku25

    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, lasku)

    Laskin.laske(hakukohde, tyhjaHakemus, lasku)
    assert(tulos.get.compareTo(new BigDecimal("25.0")) == 0)
    assertTilaHyvaksyttavissa(tila)

  }

  test("totuusarvo") {
    val funktiokutsu = totuusarvoTrue

    val lasku = Laskentadomainkonvertteri.muodostaTotuusarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, lasku)
    assert(tulos.get)
    assertTilaHyvaksyttavissa(tila)
  }

  test("summa") {
    val funktiokutsu = summa;
    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, lasku)
    assert(tulos.get.compareTo(new BigDecimal("120.0")) == 0)
    assertTilaHyvaksyttavissa(tila)
  }

  test("lukuarvolukuarvoksi") {
    val funktiokutsu = konvertoiLukuarvoLukuarvoksi
    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(funktiokutsu)
    val hakemus = tyhjaHakemus

    val (tulos, tila) = Laskin.laske(hakukohde, hakemus, lasku)
    assert(tulos.get.compareTo(new BigDecimal("4.0")) == 0)
    assertTilaHyvaksyttavissa(tila)
  }

  test("maksimi") {
    val funktiokutsu = maksimi
    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, lasku)
    assert(tulos.get.compareTo(new BigDecimal("50.0")) == 0)
    assertTilaHyvaksyttavissa(tila)
  }

  test("minimi") {
    val funktiokutsu = minimi
    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, lasku)
    assert(tulos.get.compareTo(new BigDecimal("25.0")) == 0)
    assertTilaHyvaksyttavissa(tila)
  }

  test("tulo") {
    val funktiokutsu = tulo
    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, lasku)
    assert(tulos.get.compareTo(new BigDecimal("56250.0")) == 0)
    assertTilaHyvaksyttavissa(tila)
  }

  test("konvertoiLukuarvovaliLukuarvoksi") {
    val funktiokutsu = konvertoiLukuarvovaliLukuarvoksi
    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(funktiokutsu)
    val hakemus = tyhjaHakemus

    val (tulos, tila) = Laskin.laske(hakukohde, hakemus, lasku)
    assert(tulos.get.compareTo(new BigDecimal("3.0")) == 0)
    assertTilaHyvaksyttavissa(tila)
  }

  test("keskiarvo") {
    val funktiokutsu = keskiarvo
    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, lasku)
    assert(tulos.get.compareTo(new BigDecimal("40.0")) == 0)
    assertTilaHyvaksyttavissa(tila)
  }

  test("mediaaniParillinen") {
    val funktiokutsu = mediaaniParillinenMaara
    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, lasku)
    assert(tulos.get.compareTo(new BigDecimal("47.5")) == 0)
    assertTilaHyvaksyttavissa(tila)
  }

  test("mediaaniPariton") {
    val funktiokutsu = mediaaniParitonMaara
    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, lasku)
    assert(tulos.get.compareTo(new BigDecimal("45.0")) == 0)
    assertTilaHyvaksyttavissa(tila)
  }

  test("keskiarvoNParasta") {
    val funktiokutsu = keskiarvoNParasta
    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, lasku)
    assert(tulos.get.compareTo(new BigDecimal("75.0")) == 0)
    assertTilaHyvaksyttavissa(tila)
  }

  test("summaNParasta") {
    val funktiokutsu = summaNParasta
    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, lasku)
    assert(tulos.get.compareTo(new BigDecimal("195.0")) == 0)
    assertTilaHyvaksyttavissa(tila)
  }

  test("nMaksimi") {
    val funktiokutsu = nMaksimi
    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, lasku)
    assert(tulos.get.compareTo(new BigDecimal("45.0")) == 0)
    assertTilaHyvaksyttavissa(tila)
  }

  test("nMinimi") {
    val funktiokutsu = nMinimi
    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, lasku)
    assert(tulos.get.compareTo(new BigDecimal("50.0")) == 0)
    assertTilaHyvaksyttavissa(tila)
  }

  test("ja") {
    val lista = List(
      Funktiokutsu(
        nimi = Funktionimi.JA,
        funktioargumentit = List(
          totuusarvoTrue,
          totuusarvoFalse)),
      Funktiokutsu(
        nimi = Funktionimi.JA,
        funktioargumentit = List(
          totuusarvoFalse,
          totuusarvoTrue)),
      Funktiokutsu(
        nimi = Funktionimi.JA,
        funktioargumentit = List(
          totuusarvoTrue,
          totuusarvoTrue)),
      Funktiokutsu(
        nimi = Funktionimi.JA,
        funktioargumentit = List(
          totuusarvoFalse,
          totuusarvoFalse)))

    val laskut = lista.map(Laskentadomainkonvertteri.muodostaTotuusarvolasku(_))
    val tulokset = laskut.map(Laskin.laske(hakukohde, tyhjaHakemus, _))
    assert(!tulokset(0)._1.get)
    assert(!tulokset(1)._1.get)
    assert(tulokset(2)._1.get)
    assert(!tulokset(3)._1.get)

    tulokset.foreach(t => assertTilaHyvaksyttavissa(t._2))
  }

  test("tai") {
    val lista = List(
      Funktiokutsu(
        nimi = Funktionimi.TAI,
        funktioargumentit = List(
          totuusarvoTrue,
          totuusarvoFalse)),
      Funktiokutsu(
        nimi = Funktionimi.TAI,
        funktioargumentit = List(
          totuusarvoFalse,
          totuusarvoTrue)),
      Funktiokutsu(
        nimi = Funktionimi.TAI,
        funktioargumentit = List(
          totuusarvoTrue,
          totuusarvoTrue)),
      Funktiokutsu(
        nimi = Funktionimi.TAI,
        funktioargumentit = List(
          totuusarvoFalse,
          totuusarvoFalse)))

    val laskut = lista.map(Laskentadomainkonvertteri.muodostaTotuusarvolasku(_))
    val tulokset = laskut.map(Laskin.laske(hakukohde, tyhjaHakemus, _))
    assert(tulokset(0)._1.get)
    assert(tulokset(1)._1.get)
    assert(tulokset(2)._1.get)
    assert(!tulokset(3)._1.get)

    tulokset.foreach(t => assertTilaHyvaksyttavissa(t._2))
  }

  test("negaatio") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.NEGAATIO,
      funktioargumentit = List(luku25))
    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, lasku)
    assert(tulos.get.compareTo(new BigDecimal("-25.0")) == 0)
    assertTilaHyvaksyttavissa(tila)
  }

  test("osamaara") {
    val funktiokutsu = osamaara
    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, lasku)
    assert(tulos.get.compareTo(new BigDecimal("2.0")) == 0)
    assertTilaHyvaksyttavissa(tila)
  }

  test("osamaara div by zero") {
    val funktiokutsu = osamaaraByZero
    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, lasku)

    assertTulosTyhja(tulos)
    assertTilaVirhe(tila, VirheMetatieto.VirheMetatietotyyppi.JAKO_NOLLALLA)
  }

  test("jos true") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.JOS,
      funktioargumentit = List(
        totuusarvoTrue,
        luku100,
        luku50))

    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, lasku)
    assert(tulos.get.compareTo(new BigDecimal("100.0")) == 0)
    assertTilaHyvaksyttavissa(tila)
  }

  test("jos false") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.JOS,
      funktioargumentit = List(
        totuusarvoFalse,
        luku100,
        luku50))

    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, lasku)
    assert(tulos.get.compareTo(new BigDecimal("50.0")) == 0)
    assertTilaHyvaksyttavissa(tila)
  }

  test("suurempi true") {
    val funktiokutsu = suurempiTrue
    val lasku = Laskentadomainkonvertteri.muodostaTotuusarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, lasku)
    assert(tulos.get)
    assertTilaHyvaksyttavissa(tila)
  }

  test("suurempi false") {
    val funktiokutsu = suurempiFalse
    val lasku = Laskentadomainkonvertteri.muodostaTotuusarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, lasku)
    assert(!tulos.get)
    assertTilaHyvaksyttavissa(tila)
  }

  test("suurempiTaiYhtasuuri true") {
    val funktiokutsu = suurempiTaiYhtasuuriTrue
    val lasku = Laskentadomainkonvertteri.muodostaTotuusarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, lasku)
    assert(tulos.get)
    assertTilaHyvaksyttavissa(tila)
  }

  test("suurempiTaiYhtasuuri false") {
    val funktiokutsu = suurempiTaiYhtasuuriFalse
    val lasku = Laskentadomainkonvertteri.muodostaTotuusarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, lasku)
    assert(!tulos.get)
    assertTilaHyvaksyttavissa(tila)
  }

  test("pienempi true") {
    val funktiokutsu = pienempiTrue
    val lasku = Laskentadomainkonvertteri.muodostaTotuusarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, lasku)
    assert(tulos.get)
    assertTilaHyvaksyttavissa(tila)
  }

  test("pienempi false") {
    val funktiokutsu = pienempiFalse
    val lasku = Laskentadomainkonvertteri.muodostaTotuusarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, lasku)
    assert(!tulos.get)
    assertTilaHyvaksyttavissa(tila)
  }

  test("pienempiTaiYhtasuuri true") {
    val funktiokutsu = pienempiTaiYhtasuuriTrue
    val lasku = Laskentadomainkonvertteri.muodostaTotuusarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, lasku)
    assert(tulos.get)
    assertTilaHyvaksyttavissa(tila)
  }

  test("pienempiTaiYhtasuuri false") {
    val funktiokutsu = pienempiTaiYhtasuuriFalse
    val lasku = Laskentadomainkonvertteri.muodostaTotuusarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, lasku)
    assert(!tulos.get)
    assertTilaHyvaksyttavissa(tila)
  }

  test("yhtasuuri true") {
    val funktiokutsu = yhtasuuriTrue
    val lasku = Laskentadomainkonvertteri.muodostaTotuusarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, lasku)
    assert(tulos.get)
    assertTilaHyvaksyttavissa(tila)
  }

  test("yhtasuuri false") {
    val funktiokutsu = yhtasuuriFalse
    val lasku = Laskentadomainkonvertteri.muodostaTotuusarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, lasku)
    assert(!tulos.get)
    assertTilaHyvaksyttavissa(tila)
  }

  test("ei") {
    val funktiokutsu = ei
    val lasku = Laskentadomainkonvertteri.muodostaTotuusarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, lasku)
    assert(tulos.get)
    assertTilaHyvaksyttavissa(tila)
  }

  test("tyhja") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.TYHJA)
    intercept[RuntimeException] {
      Laskentadomainkonvertteri.muodostaTotuusarvolasku(funktiokutsu)
    }
  }

  test("nimettyTotuusarvo") {
    val funktiokutsu = nimettyTotuusarvo
    val lasku = Laskentadomainkonvertteri.muodostaTotuusarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, lasku)
    assert(tulos.get)
    assertTilaHyvaksyttavissa(tila)
  }

  test("nimettyLukuarvo") {
    val funktiokutsu = nimettyLukuarvo
    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, lasku)
    assert(tulos.get.compareTo(new BigDecimal("25.0")) == 0)
    assertTilaHyvaksyttavissa(tila)
  }

  test("haeLukuarvo molemmilla konvertereilla") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.HAELUKUARVO,
      arvokonvertterit = List(
        Arvokonvertteriparametri(
          paluuarvo = "5.0",
          arvo = "25.0",
          hylkaysperuste = "false")),
      arvovalikonvertterit = List(
        Arvovalikonvertteriparametri(
          paluuarvo = "10.0",
          min = "20.0",
          max = "30.0",
          palautaHaettuArvo = "false")),
      valintaperustetunniste = List(ValintaperusteViite(
        onPakollinen = true,
        tunniste = "joku_tunniste")))

    val hakemus = TestHakemus("", Nil, Map("joku_tunniste" -> "25.0"))

    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, hakemus, lasku)
    assert(tulos.get.compareTo(new BigDecimal("5.0")) == 0)
    assertTilaHyvaksyttavissa(tila)
  }

  test("haeLukuarvo arvokonvertterilla") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.HAELUKUARVO,
      arvokonvertterit = List(
        Arvokonvertteriparametri(
          paluuarvo = "5.0",
          arvo = "25.0",
          hylkaysperuste = "false")),
      valintaperustetunniste = List(ValintaperusteViite(
        onPakollinen = true,
        tunniste = "joku_tunniste")))
    val hakemus = TestHakemus("", Nil, Map("joku_tunniste" -> "25.0"))

    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, hakemus, lasku)
    assert(tulos.get.compareTo(new BigDecimal("5.0")) == 0)
    assertTilaHyvaksyttavissa(tila)
  }

  test("haeLukuarvo arvovalikonvertterilla") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.HAELUKUARVO,
      arvovalikonvertterit = List(
        Arvovalikonvertteriparametri(
          paluuarvo = "10.0",
          min = "20.0",
          max = "30.0",
          palautaHaettuArvo = "false")),
      valintaperustetunniste = List(ValintaperusteViite(
        onPakollinen = true,
        tunniste = "joku_tunniste")))
    val hakemus = TestHakemus("", Nil, Map("joku_tunniste" -> "25.0"))

    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, hakemus, lasku)
    assert(tulos.get.compareTo(new BigDecimal("10.0")) == 0)
    assertTilaHyvaksyttavissa(tila)
  }

  test("haeTotuusarvo") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.HAETOTUUSARVO,
      valintaperustetunniste = List(ValintaperusteViite(
        onPakollinen = true,
        tunniste = "joku_tunniste")))

    val hakemus = TestHakemus("", Nil, Map("joku_tunniste" -> "true"))

    val lasku = Laskentadomainkonvertteri.muodostaTotuusarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, hakemus, lasku)
    assert(tulos.get)
    assertTilaHyvaksyttavissa(tila)
  }

  test("haeTotuusarvo konvertterilla") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.HAETOTUUSARVO,
      valintaperustetunniste = List(ValintaperusteViite(
        onPakollinen = true,
        tunniste = "joku_tunniste")),
      arvokonvertterit = List(
        Arvokonvertteriparametri(
          paluuarvo = "true",
          arvo = "false",
          hylkaysperuste = "false")))

    val hakemus = TestHakemus("", Nil, Map("joku_tunniste" -> "false"))

    val lasku = Laskentadomainkonvertteri.muodostaTotuusarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, hakemus, lasku)
    assert(tulos.get)
    assertTilaHyvaksyttavissa(tila)
  }

  test("haeTotuusarvo hylkaa konvertterilla") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.HAETOTUUSARVO,
      valintaperustetunniste = List(ValintaperusteViite(
        onPakollinen = true,
        tunniste = "joku_tunniste")),
      arvokonvertterit = List(
        Arvokonvertteriparametri(
          paluuarvo = "true",
          arvo = "false",
          hylkaysperuste = "true")))
    val hakemus = TestHakemus("", Nil, Map("joku_tunniste" -> "false"))

    val lasku = Laskentadomainkonvertteri.muodostaTotuusarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, hakemus, lasku)
    assert(tulos.get)
    assertTilaHylatty(tila, HylattyMetatieto.Hylattymetatietotyyppi.ARVOKONVERTTERIHYLKAYS)
  }

  test("haeMerkkijono") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.HAEMERKKIJONOJAKONVERTOILUKUARVOKSI,
      valintaperustetunniste = List(ValintaperusteViite(
        onPakollinen = true,
        tunniste = "joku_tunniste")),
      arvokonvertterit = List(
        Arvokonvertteriparametri(
          paluuarvo = "10.0",
          arvo = "puuppa",
          hylkaysperuste = "false")))

    val hakemus = TestHakemus("", Nil, Map("joku_tunniste" -> "puuppa"))

    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, hakemus, lasku)
    assert(tulos.get.compareTo(new BigDecimal("10.0")) == 0)
    assertTilaHyvaksyttavissa(tila)
  }

  test("konvertoilukuarvo hylkaa arvolla") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.KONVERTOILUKUARVO,
      funktioargumentit = List(
        luku25),
      arvokonvertterit = List(
        Arvokonvertteriparametri(
          paluuarvo = "5.0",
          arvo = "25.0",
          hylkaysperuste = "true"),
        Arvokonvertteriparametri(
          paluuarvo = "10.0",
          arvo = "50.0",
          hylkaysperuste = "false")))

    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, lasku)
    assert(tulos.get.equals(new BigDecimal("5.0")))
    assertTilaHylatty(tila, HylattyMetatieto.Hylattymetatietotyyppi.ARVOKONVERTTERIHYLKAYS)
  }

  test("haeLukuarvo hylkaa kun arvoa ei ole") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.HAELUKUARVO,
      valintaperustetunniste = List(ValintaperusteViite(
        onPakollinen = true,
        tunniste = "joku_tunniste")))

    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, lasku)
    assertTulosTyhja(tulos)
    assertTilaHylatty(tila, HylattyMetatieto.Hylattymetatietotyyppi.PAKOLLINEN_VALINTAPERUSTE_HYLKAYS)
  }

  test("haeLukuarvo hylkaa arvolla") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.HAELUKUARVO,
      valintaperustetunniste = List(ValintaperusteViite(
        onPakollinen = true,
        tunniste = "joku_tunniste")),
      arvokonvertterit = List(
        Arvokonvertteriparametri(
          paluuarvo = "5.0",
          arvo = "10.0",
          hylkaysperuste = "true"),
        Arvokonvertteriparametri(
          paluuarvo = "15.0",
          arvo = "5.0",
          hylkaysperuste = "false")))
    val hakemus = TestHakemus("", Nil, Map("joku_tunniste" -> "10.0"))

    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, hakemus, lasku)
    assert(tulos.get.equals(new BigDecimal("5.0")))
    assertTilaHylatty(tila, HylattyMetatieto.Hylattymetatietotyyppi.ARVOKONVERTTERIHYLKAYS)
  }

  test("haeLukuarvo ei konvertoi arvoa jota ei ole") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.HAELUKUARVO,
      valintaperustetunniste = List(ValintaperusteViite(
        onPakollinen = true,
        tunniste = "joku_tunniste")),
      arvokonvertterit = List(
        Arvokonvertteriparametri(
          paluuarvo = "5.0",
          arvo = "0.0",
          hylkaysperuste = "false")))

    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, lasku)
    assertTulosTyhja(tulos)
    assertTilaHylatty(tila, HylattyMetatieto.Hylattymetatietotyyppi.PAKOLLINEN_VALINTAPERUSTE_HYLKAYS)
  }

  test("haeMerkkijono hylkaa kun arvoa ei ole") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.HAEMERKKIJONOJAKONVERTOILUKUARVOKSI,
      valintaperustetunniste = List(ValintaperusteViite(
        onPakollinen = true,
        tunniste = "joku_tunniste")),
      arvokonvertterit = List(
        Arvokonvertteriparametri(
          paluuarvo = "10.0",
          arvo = "puuppa",
          hylkaysperuste = "false")))

    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, lasku)
    assertTulosTyhja(tulos)
    assertTilaHylatty(tila, HylattyMetatieto.Hylattymetatietotyyppi.PAKOLLINEN_VALINTAPERUSTE_HYLKAYS)
  }

  test("haeMerkkijono hylkaa arvolla") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.HAEMERKKIJONOJAKONVERTOILUKUARVOKSI,
      valintaperustetunniste = List(ValintaperusteViite(
        onPakollinen = true,
        tunniste = "joku_tunniste")),
      arvokonvertterit = List(
        Arvokonvertteriparametri(
          paluuarvo = "10.0",
          arvo = "puuppa",
          hylkaysperuste = "true")))
    val hakemus = TestHakemus("", Nil, Map("joku_tunniste" -> "puuppa"))

    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, hakemus, lasku)
    assert(tulos.get.equals(new BigDecimal("10.0")))
    assertTilaHylatty(tila, HylattyMetatieto.Hylattymetatietotyyppi.ARVOKONVERTTERIHYLKAYS)
  }

  test("haeTotuusarvo hylkaa kun arvoa ei ole") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.HAETOTUUSARVO,
      valintaperustetunniste = List(ValintaperusteViite(
        onPakollinen = true,
        tunniste = "joku_tunniste")))

    val lasku = Laskentadomainkonvertteri.muodostaTotuusarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, lasku)
    assertTulosTyhja(tulos)
    assertTilaHylatty(tila, HylattyMetatieto.Hylattymetatietotyyppi.PAKOLLINEN_VALINTAPERUSTE_HYLKAYS)
  }

  test("Keskiarvo, osa tyhjia arvoja") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.KESKIARVO,
      funktioargumentit = List(
        luku100,
        luku50,
        haeLukuarvo))

    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, lasku)
    assert(tulos.get.compareTo(new BigDecimal("75.0")) == 0)
    assertTilaHyvaksyttavissa(tila)
  }

  test("Keskiarvo, kaikki tyhjia arvoja") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.KESKIARVO,
      funktioargumentit = List(
        haeLukuarvo,
        haeLukuarvo,
        haeLukuarvo))

    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, lasku)
    assertTulosTyhja(tulos)
    assertTilaHyvaksyttavissa(tila)
  }

  test("Summa, osa tyhjia arvoja") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.SUMMA,
      funktioargumentit = List(
        luku100,
        luku50,
        haeLukuarvo))

    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, lasku)
    assert(tulos.get.compareTo(new BigDecimal("150.0")) == 0)
    assertTilaHyvaksyttavissa(tila)
  }

  test("Summa, kaikki tyhjia arvoja") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.SUMMA,
      funktioargumentit = List(
        haeLukuarvo,
        haeLukuarvo,
        haeLukuarvo))

    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, lasku)
    assertTulosTyhja(tulos)
    assertTilaHyvaksyttavissa(tila)
  }

  test("KeskiarvoNParasta, osa tyhjia arvoja") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.KESKIARVONPARASTA,
      funktioargumentit = List(
        luku100,
        luku50,
        haeLukuarvo,
        haeLukuarvo),
      syoteparametrit = List(
        Syoteparametri(
          avain = "n",
          arvo = "3")))

    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, lasku)
    assert(tulos.get.compareTo(new BigDecimal("75.0")) == 0)
    assertTilaHyvaksyttavissa(tila)
  }

  test("KeskiarvoNParasta, kaikki tyhjia arvoja") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.KESKIARVONPARASTA,
      funktioargumentit = List(
        haeLukuarvo,
        haeLukuarvo,
        haeLukuarvo,
        haeLukuarvo),
      syoteparametrit = List(
        Syoteparametri(
          avain = "n",
          arvo = "3")))

    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, lasku)
    assertTulosTyhja(tulos)
    assertTilaHyvaksyttavissa(tila)
  }

  test("NMinimi, osa tyhjia arvoja") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.NMINIMI,
      funktioargumentit = List(
        luku100,
        luku50,
        haeLukuarvo,
        haeLukuarvo),
      syoteparametrit = List(
        Syoteparametri(
          avain = "n",
          arvo = "3")))

    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, lasku)
    assert(tulos.get.compareTo(new BigDecimal("100.0")) == 0)
    assertTilaHyvaksyttavissa(tila)
  }

  test("NMinimi, kaikki tyhjia arvoja") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.NMINIMI,
      funktioargumentit = List(
        haeLukuarvo,
        haeLukuarvo,
        haeLukuarvo,
        haeLukuarvo),
      syoteparametrit = List(
        Syoteparametri(
          avain = "n",
          arvo = "3")))

    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, lasku)
    assertTulosTyhja(tulos)
    assertTilaHyvaksyttavissa(tila)
  }

  test("NMaksimi, osa tyhjia arvoja") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.NMAKSIMI,
      funktioargumentit = List(
        luku100,
        luku50,
        haeLukuarvo,
        haeLukuarvo),
      syoteparametrit = List(
        Syoteparametri(
          avain = "n",
          arvo = "3")))

    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, lasku)
    assert(tulos.get.equals(new BigDecimal("50.0")))
    assertTilaHyvaksyttavissa(tila)
  }

  test("NMaksimi, kaikki tyhjia arvoja") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.NMAKSIMI,
      funktioargumentit = List(
        haeLukuarvo,
        haeLukuarvo,
        haeLukuarvo,
        haeLukuarvo),
      syoteparametrit = List(
        Syoteparametri(
          avain = "n",
          arvo = "3")))

    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, lasku)
    assertTulosTyhja(tulos)
    assertTilaHyvaksyttavissa(tila)
  }

  test("SummaNParasta, osa tyhjia arvoja") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.SUMMANPARASTA,
      funktioargumentit = List(
        luku100,
        luku50,
        haeLukuarvo,
        haeLukuarvo),
      syoteparametrit = List(
        Syoteparametri(
          avain = "n",
          arvo = "3")))

    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, lasku)
    assert(tulos.get.equals(new BigDecimal("150.0")))
    assertTilaHyvaksyttavissa(tila)
  }

  test("SummaNParasta, kaikki tyhjia arvoja") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.SUMMANPARASTA,
      funktioargumentit = List(
        haeLukuarvo,
        haeLukuarvo,
        haeLukuarvo,
        haeLukuarvo),
      syoteparametrit = List(
        Syoteparametri(
          avain = "n",
          arvo = "3")))

    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, lasku)
    assertTulosTyhja(tulos)
    assertTilaHyvaksyttavissa(tila)
  }

  test("Jos, ehto tyhja") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.JOS,
      funktioargumentit = List(
        haeTotuusarvo,
        luku25,
        luku100))

    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, lasku)
    assertTulosTyhja(tulos)
    assertTilaHyvaksyttavissa(tila)
  }

  test("Konvertoilukuarvo, tyhja syote") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.KONVERTOILUKUARVO,
      funktioargumentit = List(
        haeLukuarvo),
      arvokonvertterit = List(
        Arvokonvertteriparametri(
          paluuarvo = "5.0",
          arvo = "25.0",
          hylkaysperuste = "true"),
        Arvokonvertteriparametri(
          paluuarvo = "10.0",
          arvo = "50.0",
          hylkaysperuste = "false")))

    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, lasku)
    assertTulosTyhja(tulos)
    assertTilaHyvaksyttavissa(tila)
  }

  test("Ja, osa tyhjia arvoja") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.JA,
      funktioargumentit = List(
        haeTotuusarvo,
        totuusarvoTrue))

    val lasku = Laskentadomainkonvertteri.muodostaTotuusarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, lasku)
    assertTulosTyhja(tulos)
    assertTilaHyvaksyttavissa(tila)
  }

  test("Tai, osa tyhjia arvoja") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.TAI,
      funktioargumentit = List(
        haeTotuusarvo,
        totuusarvoTrue))

    val lasku = Laskentadomainkonvertteri.muodostaTotuusarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, lasku)
    assertTulosTyhja(tulos)
    assertTilaHyvaksyttavissa(tila)
  }

  test("Ei, tyhja arvo") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.EI,
      funktioargumentit = List(
        haeTotuusarvo))

    val lasku = Laskentadomainkonvertteri.muodostaTotuusarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, lasku)
    assertTulosTyhja(tulos)
    assertTilaHyvaksyttavissa(tila)
  }

  test("Suurempi kuin, eka tyhja arvo") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.SUUREMPI,
      funktioargumentit = List(
        haeLukuarvo,
        luku50))

    val lasku = Laskentadomainkonvertteri.muodostaTotuusarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, lasku)
    assertTulosTyhja(tulos)
    assertTilaHyvaksyttavissa(tila)
  }

  test("Suurempi kuin, toka tyhja arvo") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.SUUREMPI,
      funktioargumentit = List(
        luku50,
        haeLukuarvo))

    val lasku = Laskentadomainkonvertteri.muodostaTotuusarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, lasku)
    assertTulosTyhja(tulos)
    assertTilaHyvaksyttavissa(tila)
  }

  test("Negaatio, tyhja arvo") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.NEGAATIO,
      funktioargumentit = List(
        haeLukuarvo))

    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, lasku)
    assertTulosTyhja(tulos)
    assertTilaHyvaksyttavissa(tila)
  }

  test("Hakutoive, true") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.HAKUTOIVE,
      syoteparametrit = List(
        Syoteparametri(
          avain = "n",
          arvo = "2")))

    val hakukohde = new Hakukohde("oid1", new util.HashMap[String, String])
    val hakemus = TestHakemus("", List("oid2", "oid1", "oid3"), Map[String, String]())

    val lasku = Laskentadomainkonvertteri.muodostaTotuusarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, hakemus, lasku)
    assert(tulos.get)
    assertTilaHyvaksyttavissa(tila)
  }

  test("Hakutoive, false") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.HAKUTOIVE,
      syoteparametrit = List(
        Syoteparametri(
          avain = "n",
          arvo = "3")))

    val hakukohde = new Hakukohde("oid1", new util.HashMap[String, String])
    val hakemus = TestHakemus("", List("oid2", "oid1", "oid3"), Map[String, String]())

    val lasku = Laskentadomainkonvertteri.muodostaTotuusarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, hakemus, lasku)
    assert(!tulos.get)
    assertTilaHyvaksyttavissa(tila)
  }

  test("Hakutoive, ei tarpeeksi hakutoiveita") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.HAKUTOIVE,
      syoteparametrit = List(
        Syoteparametri(
          avain = "n",
          arvo = "5")))

    val hakukohde = new Hakukohde("oid1", new util.HashMap[String, String])
    val hakemus = TestHakemus("", List("oid2", "oid1", "oid3"), Map[String, String]())

    val lasku = Laskentadomainkonvertteri.muodostaTotuusarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, hakemus, lasku)
    assert(!tulos.get)
    assertTilaHyvaksyttavissa(tila)
  }

  test("Iso kaava") {
    val haeArvo1 = Funktiokutsu(
      nimi = Funktionimi.HAELUKUARVO,
      valintaperustetunniste = List(ValintaperusteViite(
        onPakollinen = true,
        tunniste = "lukuarvotunniste1")))

    val haeArvo2 = Funktiokutsu(
      nimi = Funktionimi.HAELUKUARVO,
      valintaperustetunniste = List(ValintaperusteViite(
        onPakollinen = false,
        tunniste = "lukuarvotunniste2")),
      arvokonvertterit = List(
        Arvokonvertteriparametri(
          paluuarvo = "100.0",
          arvo = "10.0",
          hylkaysperuste = "true")))

    val lukuarvo = Funktiokutsu(
      nimi = Funktionimi.LUKUARVO,
      syoteparametrit = List(
        Syoteparametri(
          avain = "luku",
          arvo = "50.0")))

    val summa = Funktiokutsu(
      nimi = Funktionimi.SUMMA,
      funktioargumentit = List(
        haeArvo1,
        haeArvo2,
        lukuarvo))

    val hakemus = TestHakemus("", Nil, Map("lukuarvotunniste2" -> "10.0"))

    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(summa)
    val (tulos, tila) = Laskin.laske(hakukohde, hakemus, lasku)
    assert(tulos.get.equals(new BigDecimal("150.0")))
    assertTilaHylatty(tila, HylattyMetatieto.Hylattymetatietotyyppi.PAKOLLINEN_VALINTAPERUSTE_HYLKAYS)
  }

  test("haeLukuarvo, oletusarvo") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.HAELUKUARVO,
      valintaperustetunniste = List(ValintaperusteViite(
        tunniste = "joku_tunniste",
        onPakollinen = false)),
      syoteparametrit = List(
        Syoteparametri(avain = "oletusarvo", arvo = "100.0")))

    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, lasku)
    assert(tulos.get.equals(new BigDecimal("100.0")))
    assertTilaHyvaksyttavissa(tila)
  }

  test("haeTotuusarvo, oletusarvo") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.HAETOTUUSARVO,
      valintaperustetunniste = List(ValintaperusteViite(
        tunniste = "joku_tunniste",
        onPakollinen = true)),
      syoteparametrit = List(
        Syoteparametri(avain = "oletusarvo", arvo = "true")))

    val lasku = Laskentadomainkonvertteri.muodostaTotuusarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, lasku)
    assert(tulos.get == true)
    assertTilaHylatty(tila, HylattyMetatieto.Hylattymetatietotyyppi.PAKOLLINEN_VALINTAPERUSTE_HYLKAYS)
  }

  test("haeMerkkijonoJaKonvertoiLukuarvoksi, oletusarvo") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.HAEMERKKIJONOJAKONVERTOILUKUARVOKSI,
      valintaperustetunniste = List(ValintaperusteViite(
        tunniste = "joku_tunniste",
        onPakollinen = false)),
      syoteparametrit = List(
        Syoteparametri(avain = "oletusarvo", arvo = "50.0")),
      arvokonvertterit = List(
        Arvokonvertteriparametri(
          paluuarvo = "66.0",
          arvo = "L",
          hylkaysperuste = "false")))

    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, lasku)
    assert(tulos.get.equals(new BigDecimal("50.0")))
    assertTilaHyvaksyttavissa(tila)
  }

  test("haeMerkkijonoJaKonvertoiTotuusarvoksi") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.HAEMERKKIJONOJAKONVERTOITOTUUSARVOKSI,
      valintaperustetunniste = List(ValintaperusteViite(
        tunniste = "jokin_tunniste",
        onPakollinen = false)),
      arvokonvertterit = List(
        Arvokonvertteriparametri(
          paluuarvo = "false",
          arvo = "helsinki",
          hylkaysperuste = "false"),
        Arvokonvertteriparametri(
          paluuarvo = "true",
          arvo = "turku",
          hylkaysperuste = "true"),
        Arvokonvertteriparametri(
          paluuarvo = "false",
          arvo = "tampere",
          hylkaysperuste = "false")))

    val hakemus = TestHakemus("", Nil, Map("jokin_tunniste" -> "turku"))
    val lasku = Laskentadomainkonvertteri.muodostaTotuusarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, hakemus, lasku)
    assert(tulos.get)
    assertTilaHylatty(tila, HylattyMetatieto.Hylattymetatietotyyppi.ARVOKONVERTTERIHYLKAYS)

    val hakemus2 = TestHakemus("", Nil, Map("jokin_tunniste" -> "tampere"))
    val (tulos2, tila2) = Laskin.laske(hakukohde, hakemus2, lasku)
    assert(!tulos2.get)
    assertTilaHyvaksyttavissa(tila2)
  }

  test("demografia") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.DEMOGRAFIA,
      syoteparametrit = List(
        Syoteparametri("tunniste", "sukupuoli"),
        Syoteparametri("prosenttiosuus", "33.0")))

    val hakukohde = new Hakukohde("1", new util.HashMap[String, String])

    val hakemukset = List(
      TestHakemus("1", List("1", "2", "3"), Map("sukupuoli" -> "mies")),
      TestHakemus("2", List("1", "2", "3"), Map("sukupuoli" -> "mies")),
      TestHakemus("3", List("1", "2", "3"), Map("sukupuoli" -> "mies")),
      TestHakemus("4", List("1", "2", "3"), Map("sukupuoli" -> "nainen")))

    val lasku = Laskentadomainkonvertteri.muodostaTotuusarvolasku(funktiokutsu)

    val tulokset: List[Laskentatulos[Boolean]] = hakemukset.map(h => Laskin.suoritaValintalaskenta(hakukohde, h, hakemukset, lasku))

    for (i <- 0 until tulokset.size) {
      val tulos = tulokset(i)
      if (i < 3) assert(!tulos.getTulos) else assert(tulos.getTulos)
      assertTilaHyvaksyttavissa(tulos.getTila)
    }
  }

  test("demografia vaaralla moodilla") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.DEMOGRAFIA,
      syoteparametrit = List(
        Syoteparametri("tunniste", "sukupuoli"),
        Syoteparametri("prosenttiosuus", "33.0")))

    val hakukohde = new Hakukohde("1", new util.HashMap[String, String])

    val hakemus = TestHakemus("1", List("1", "2", "3"), Map("sukupuoli" -> "mies"))
    val lasku = Laskentadomainkonvertteri.muodostaTotuusarvolasku(funktiokutsu)

    val tulos = Laskin.suoritaValintakoelaskenta(hakukohde, hakemus, lasku)
    assertTulosTyhja(Option(tulos.getTulos))
    assertTilaVirhe(tulos.getTila, VirheMetatietotyyppi.VIRHEELLINEN_LASKENTAMOODI)
  }

  test("pyoristys up") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.PYORISTYS,
      funktioargumentit = List(luku3_335),
      syoteparametrit = List(
        Syoteparametri(avain = "tarkkuus", arvo = "2")))

    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, lasku)
    assert(tulos.get.equals(new BigDecimal("3.34")))
    assertTilaHyvaksyttavissa(tila)
  }

  test("pyoristys down") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.PYORISTYS,
      funktioargumentit = List(luku3_335),
      syoteparametrit = List(
        Syoteparametri(avain = "tarkkuus", arvo = "1")))

    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, lasku)
    assert(tulos.get.equals(new BigDecimal("3.3")))
    assertTilaHyvaksyttavissa(tila)
  }

  test("hae merkkijono ja vertaa yhtasuuruus") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.HAEMERKKIJONOJAVERTAAYHTASUURUUS,
      valintaperustetunniste = List(ValintaperusteViite(
        tunniste = "aidinkieli",
        onPakollinen = true)),
      syoteparametrit = List(
        Syoteparametri(
          avain = "vertailtava",
          arvo = "FI")))

    val hakemus = TestHakemus("", Nil, Map("aidinkieli" -> "FI"))
    val lasku = Laskentadomainkonvertteri.muodostaTotuusarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, hakemus, lasku)
    assert(tulos.get)
    assertTilaHyvaksyttavissa(tila)

    val hakemus2 = TestHakemus("", Nil, Map("aidinkieli" -> "SV"))
    val (tulos2, tila2) = Laskin.laske(hakukohde, hakemus2, lasku)
    assert(!tulos2.get)
    assertTilaHyvaksyttavissa(tila2)
  }

  test("hae merkkijono ja vertaa yhtasuuruus pakollinen") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.HAEMERKKIJONOJAVERTAAYHTASUURUUS,
      valintaperustetunniste = List(ValintaperusteViite(
        tunniste = "aidinkieli",
        onPakollinen = true)),
      syoteparametrit = List(
        Syoteparametri(
          avain = "vertailtava",
          arvo = "FI")))

    val hakemus = tyhjaHakemus
    val lasku = Laskentadomainkonvertteri.muodostaTotuusarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, hakemus, lasku)
    assertTulosTyhja(tulos)
    assertTilaHylatty(tila, HylattyMetatieto.Hylattymetatietotyyppi.PAKOLLINEN_VALINTAPERUSTE_HYLKAYS)
  }

  test("hae merkkijono ja vertaa yhtasuuruus oletusarvo") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.HAEMERKKIJONOJAVERTAAYHTASUURUUS,
      valintaperustetunniste = List(ValintaperusteViite(
        tunniste = "aidinkieli",
        onPakollinen = true)),
      syoteparametrit = List(
        Syoteparametri(
          avain = "vertailtava",
          arvo = "FI"),
        Syoteparametri(
          avain = "oletusarvo",
          arvo = "false")))

    val hakemus = tyhjaHakemus
    val lasku = Laskentadomainkonvertteri.muodostaTotuusarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, hakemus, lasku)
    assert(!tulos.get)
    assertTilaHylatty(tila, HylattyMetatieto.Hylattymetatietotyyppi.PAKOLLINEN_VALINTAPERUSTE_HYLKAYS)
  }

  test("syotettava arvo, osallistuminen puuttuu") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.HAELUKUARVO,
      valintaperustetunniste = List(ValintaperusteViite(
        tunniste = "tunniste",
        onPakollinen = true,
        lahde = Valintaperustelahde.SYOTETTAVA_ARVO)))

    val hakemus = TestHakemus("", Nil, Map("tunniste" -> "10.0"))
    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(funktiokutsu)

    val (tulos, tila) = Laskin.laske(hakukohde, hakemus, lasku)
    assertTulosTyhja(tulos)
    assertTilaVirhe(tila, VirheMetatietotyyppi.SYOTETTAVA_ARVO_MERKITSEMATTA)
  }

  test("syotettava arvo, osallistuminen false") {
    val valintaperuste = ValintaperusteViite(
      tunniste = "tunniste",
      onPakollinen = true,
      lahde = Valintaperustelahde.SYOTETTAVA_ARVO)

    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.HAELUKUARVO,
      valintaperustetunniste = List(valintaperuste))

    val hakemus = TestHakemus("", Nil, Map("tunniste" -> "10.0",
      valintaperuste.getOsallistuminenTunniste -> Osallistuminen.EI_OSALLISTUNUT.name))
    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(funktiokutsu)

    val (tulos, tila) = Laskin.laske(hakukohde, hakemus, lasku)
    assertTulosTyhja(tulos)
    assertTilaHylatty(tila, HylattyMetatieto.Hylattymetatietotyyppi.EI_OSALLISTUNUT_HYLKAYS)
  }

  test("syotettava arvo, osallistuminen true") {
    val valintaperuste = ValintaperusteViite(
      tunniste = "tunniste",
      onPakollinen = true,
      lahde = Valintaperustelahde.SYOTETTAVA_ARVO)

    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.HAELUKUARVO,
      valintaperustetunniste = List(valintaperuste))

    val hakemus = TestHakemus("", Nil, Map("tunniste" -> "10.0",
      valintaperuste.getOsallistuminenTunniste -> Osallistuminen.OSALLISTUI.name))
    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(funktiokutsu)

    val (tulos, tila) = Laskin.laske(hakukohde, hakemus, lasku)
    assert(tulos.get.equals(new BigDecimal("10.0")))
    assertTilaHyvaksyttavissa(tila)
  }

  test("hae lukuarvo, hakemuksella tyhja arvo") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.HAELUKUARVO,
      valintaperustetunniste = List(ValintaperusteViite(
        tunniste = "tunniste",
        onPakollinen = true)))

    val hakemus = TestHakemus("", Nil, Map("tunniste" -> ""))
    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(funktiokutsu)

    val (tulos, tila) = Laskin.laske(hakukohde, hakemus, lasku)
    assertTulosTyhja(tulos)
    assertTilaHylatty(tila, HylattyMetatieto.Hylattymetatietotyyppi.PAKOLLINEN_VALINTAPERUSTE_HYLKAYS)
  }

  test("hae lukuarvo, oletusarvo, hakemuksella tyhja arvo") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.HAELUKUARVO,
      valintaperustetunniste = List(ValintaperusteViite(
        tunniste = "tunniste",
        onPakollinen = true)),
      syoteparametrit = List(
        Syoteparametri(
          avain = "oletusarvo",
          arvo = "5.0")))

    val hakemus = TestHakemus("", Nil, Map("tunniste" -> ""))
    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(funktiokutsu)

    val (tulos, tila) = Laskin.laske(hakukohde, hakemus, lasku)
    assert(tulos.get.equals(new BigDecimal("5.0")))
    assertTilaHylatty(tila, HylattyMetatieto.Hylattymetatietotyyppi.PAKOLLINEN_VALINTAPERUSTE_HYLKAYS)
  }

  test("hae totuusarvo, hakemuksella tyhja arvo") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.HAETOTUUSARVO,
      valintaperustetunniste = List(ValintaperusteViite(
        tunniste = "tunniste",
        onPakollinen = true)))

    val hakemus = TestHakemus("", Nil, Map("tunniste" -> ""))
    val lasku = Laskentadomainkonvertteri.muodostaTotuusarvolasku(funktiokutsu)

    val (tulos, tila) = Laskin.laske(hakukohde, hakemus, lasku)
    assertTulosTyhja(tulos)
    assertTilaHylatty(tila, HylattyMetatieto.Hylattymetatietotyyppi.PAKOLLINEN_VALINTAPERUSTE_HYLKAYS)
  }

  test("hae totuusarvo, oletusarvo, hakemuksella tyhja arvo") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.HAETOTUUSARVO,
      valintaperustetunniste = List(ValintaperusteViite(
        tunniste = "tunniste",
        onPakollinen = true)),
      syoteparametrit = List(
        Syoteparametri(
          avain = "oletusarvo",
          arvo = "false")))

    val hakemus = TestHakemus("", Nil, Map("tunniste" -> ""))
    val lasku = Laskentadomainkonvertteri.muodostaTotuusarvolasku(funktiokutsu)

    val (tulos, tila) = Laskin.laske(hakukohde, hakemus, lasku)
    assert(!tulos.get)
    assertTilaHylatty(tila, HylattyMetatieto.Hylattymetatietotyyppi.PAKOLLINEN_VALINTAPERUSTE_HYLKAYS)
  }

  test("hae totuusarvo, epavalidi arvo") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.HAETOTUUSARVO,
      valintaperustetunniste = List(ValintaperusteViite(
        tunniste = "tunniste",
        onPakollinen = true)))

    val hakemus = TestHakemus("", Nil, Map("tunniste" -> "puuppa"))
    val lasku = Laskentadomainkonvertteri.muodostaTotuusarvolasku(funktiokutsu)

    val (tulos, tila) = Laskin.laske(hakukohde, hakemus, lasku)
    assertTulosTyhja(tulos)
    assertTilaVirhe(tila, VirheMetatietotyyppi.VALINTAPERUSTETTA_EI_VOIDA_TULKITA_TOTUUSARVOKSI)
  }

  test("hae lukuarvo, epavalidi arvo") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.HAELUKUARVO,
      valintaperustetunniste = List(ValintaperusteViite(
        tunniste = "tunniste",
        onPakollinen = true)))

    val hakemus = TestHakemus("", Nil, Map("tunniste" -> "puuppa"))
    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(funktiokutsu)

    val (tulos, tila) = Laskin.laske(hakukohde, hakemus, lasku)
    assertTulosTyhja(tulos)
    assertTilaVirhe(tila, VirheMetatietotyyppi.VALINTAPERUSTETTA_EI_VOIDA_TULKITA_LUKUARVOKSI)
  }

  test("hylkaa, syote true") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.HYLKAA,
      funktioargumentit = List(totuusarvoTrue))

    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, lasku)
    assertTulosTyhja(tulos)
    assertTilaHylatty(tila, HylattyMetatieto.Hylattymetatietotyyppi.HYLKAA_FUNKTION_SUORITTAMA_HYLKAYS)
  }

  test("hylkaa, syote false") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.HYLKAA,
      funktioargumentit = List(totuusarvoFalse))

    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, lasku)
    assertTulosTyhja(tulos)
    assertTilaHyvaksyttavissa(tila)
  }

  test("hylkaa, syote tyhja") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.HYLKAA,
      funktioargumentit = List(haeTotuusarvo))

    val lasku1 = Laskentadomainkonvertteri.muodostaTotuusarvolasku(haeTotuusarvo)
    val (tulos1, tila1) = Laskin.laske(hakukohde, tyhjaHakemus, lasku1)
    assertTulosTyhja(tulos1)
    assertTilaHyvaksyttavissa(tila1)

    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, lasku)
    assertTulosTyhja(tulos)
    assertTilaVirhe(tila, VirheMetatietotyyppi.HYLKAAMISTA_EI_VOIDA_TULKITA)
  }

  test("hylkaa, kuvaus syotetty") {
    val hylkaysperustekuvaus = "huono juttu"

    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.HYLKAA,
      funktioargumentit = List(totuusarvoTrue),
      syoteparametrit = List(Syoteparametri(avain = "hylkaysperustekuvaus", arvo = hylkaysperustekuvaus)))

    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, lasku)
    assertTulosTyhja(tulos)
    assertTilaHylatty(tila, HylattyMetatieto.Hylattymetatietotyyppi.HYLKAA_FUNKTION_SUORITTAMA_HYLKAYS, Some(hylkaysperustekuvaus))
  }

  test("hae hakukohteen valintaperuste") {
    val tunniste = "valintaperustetunniste"

    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.HAELUKUARVO,
      valintaperustetunniste = List(ValintaperusteViite(false, tunniste, Valintaperustelahde.HAKUKOHTEEN_ARVO)))

    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(new Hakukohde("hakukohdeOid", Map(tunniste -> "100.0")), tyhjaHakemus, lasku)
    assert(tulos.get.equals(new BigDecimal("100.0")))
    assertTilaHyvaksyttavissa(tila)
  }

  test("hae hakukohteen valintaperuste, tunnistetta ei ole olemassa") {
    val tunniste = "valintaperustetunniste"

    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.HAELUKUARVO,
      valintaperustetunniste = List(ValintaperusteViite(false, tunniste, Valintaperustelahde.HAKUKOHTEEN_ARVO)))

    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(funktiokutsu)
    val (tulos, tila) = Laskin.laske(new Hakukohde("hakukohdeOid", Map("toinen tunniste" -> "100.0")), tyhjaHakemus, lasku)
    assertTulosTyhja(tulos)
    assertTilaHyvaksyttavissa(tila)
  }

  test("skaalaus, lahdeskaalaa ei annettu") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.SKAALAUS,
      funktioargumentit = List(
        Funktiokutsu(
          nimi = Funktionimi.HAELUKUARVO,
          valintaperustetunniste = List(ValintaperusteViite(
            tunniste = "tunniste1",
            onPakollinen = false,
            lahde = Valintaperustelahde.HAETTAVA_ARVO)))),
      syoteparametrit = List(
        Syoteparametri(avain = "kohdeskaalaMin", arvo = "-25.0"),
        Syoteparametri(avain = "kohdeskaalaMax", arvo = "50.0"),
        Syoteparametri(avain = "kaytaLaskennallistaLahdeskaalaa", arvo = "true")))

    val hakemukset = List(
      TestHakemus("hakemusOid1", List[String](), Map("tunniste1" -> "-1000.0")),
      TestHakemus("hakemusOid2", List[String](), Map("tunniste1" -> "-500.0")),
      TestHakemus("hakemusOid3", List[String](), Map("tunniste1" -> "0.0")),
      TestHakemus("hakemusOid4", List[String](), Map("tunniste1" -> "500.0")))

    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(funktiokutsu)
    val tulokset = hakemukset.map {
      h =>
        Laskin.suoritaValintalaskenta(hakukohde, h, asJavaCollection(hakemukset), lasku)
    }

    assert(tulokset.size == 4)
    assert(tulokset(0).getTulos.equals(new BigDecimal("-25.0")))
    assertTilaHyvaksyttavissa(tulokset(0).getTila)

    assert(tulokset(1).getTulos.equals(new BigDecimal("0.0")))
    assertTilaHyvaksyttavissa(tulokset(1).getTila)

    assert(tulokset(2).getTulos.equals(new BigDecimal("25.0")))
    assertTilaHyvaksyttavissa(tulokset(2).getTila)

    assert(tulokset(3).getTulos.equals(new BigDecimal("50.0")))
    assertTilaHyvaksyttavissa(tulokset(3).getTila)
  }

  test("skaalaus, lahdeskaalaa annettu") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.SKAALAUS,
      funktioargumentit = List(
        Funktiokutsu(
          nimi = Funktionimi.HAELUKUARVO,
          valintaperustetunniste = List(ValintaperusteViite(
            tunniste = "tunniste1",
            onPakollinen = false,
            lahde = Valintaperustelahde.HAETTAVA_ARVO)))),
      syoteparametrit = List(
        Syoteparametri(avain = "kohdeskaalaMin", arvo = "-100.0"),
        Syoteparametri(avain = "kohdeskaalaMax", arvo = "100.0"),
        Syoteparametri(avain = "lahdeskaalaMin", arvo = "-1000.0"),
        Syoteparametri(avain = "lahdeskaalaMax", arvo = "1000.0"),
        Syoteparametri(avain = "kaytaLaskennallistaLahdeskaalaa", arvo = "false")))

    val hakemukset = List(
      TestHakemus("hakemusOid1", List[String](), Map("tunniste1" -> "-750.0")),
      TestHakemus("hakemusOid2", List[String](), Map("tunniste1" -> "-250.0")),
      TestHakemus("hakemusOid3", List[String](), Map("tunniste1" -> "250.0")),
      TestHakemus("hakemusOid4", List[String](), Map("tunniste1" -> "750.0")))

    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(funktiokutsu)
    val tulokset = hakemukset.map {
      h =>
        Laskin.suoritaValintalaskenta(hakukohde, h, asJavaCollection(hakemukset), lasku)
    }

    assert(tulokset.size == 4)
    assert(tulokset(0).getTulos.equals(new BigDecimal("-75.0")))
    assertTilaHyvaksyttavissa(tulokset(0).getTila)

    assert(tulokset(1).getTulos.equals(new BigDecimal("-25.0")))
    assertTilaHyvaksyttavissa(tulokset(1).getTila)

    assert(tulokset(2).getTulos.equals(new BigDecimal("25.0")))
    assertTilaHyvaksyttavissa(tulokset(2).getTila)

    assert(tulokset(3).getTulos.equals(new BigDecimal("75.0")))
    assertTilaHyvaksyttavissa(tulokset(3).getTila)
  }

  test("painotettu keskiarvo") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.PAINOTETTUKESKIARVO,
      funktioargumentit = List(
        Funktiokutsu( // Painokerroin 1
          nimi = Funktionimi.LUKUARVO,
          syoteparametrit = List(
            Syoteparametri(
              avain = "luku",
              arvo = "2.0"))),
        Funktiokutsu( // Painotettava 1
          nimi = Funktionimi.LUKUARVO,
          syoteparametrit = List(
            Syoteparametri(
              avain = "luku",
              arvo = "10.0"))),
        Funktiokutsu( // Painokerroin 2
          nimi = Funktionimi.LUKUARVO,
          syoteparametrit = List(
            Syoteparametri(
              avain = "luku",
              arvo = "1.5"))),
        Funktiokutsu( // Painotettava 2
          nimi = Funktionimi.LUKUARVO,
          syoteparametrit = List(
            Syoteparametri(
              avain = "luku",
              arvo = "30.0")))))

    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(funktiokutsu)
    val tulos = Laskin.suoritaValintalaskenta(hakukohde, tyhjaHakemus, List(), lasku)
    assert(tulos.getTulos.compareTo(new BigDecimal("18.5714")) == 0)
    assertTilaHyvaksyttavissa(tulos.getTila)
  }

  test("hae hakukohteen valintaperuste, epasuora viittaus") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.HAELUKUARVO,
      valintaperustetunniste = List(ValintaperusteViite(false, "hakukohteenTunniste", Valintaperustelahde.HAKUKOHTEEN_ARVO, true)))

    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(funktiokutsu)
    val hakukohde = new Hakukohde("hakukohdeOid", Map("hakukohteenTunniste" -> "hakemuksenTunniste"))
    val hakemus = new Hakemus("oid", Map[java.lang.Integer, String](), Map("hakemuksenTunniste" -> "100.0"))

    val tulos = Laskin.suoritaValintalaskenta(hakukohde, hakemus, List(hakemus), lasku)
    assert(tulos.getTulos.compareTo(new BigDecimal("100.0")) == 0)
    assertTilaHyvaksyttavissa(tulos.getTila)
  }

  test("valintaperusteyhtasuuruus") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.VALINTAPERUSTEYHTASUURUUS,
      valintaperustetunniste = List(
        ValintaperusteViite(
          onPakollinen = false,
          tunniste = "tunniste1",
          lahde = Valintaperustelahde.HAETTAVA_ARVO,
          epasuoraViittaus = false)))

  }

  test("tallennaTulos") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.PAINOTETTUKESKIARVO,
      funktioargumentit = List(
        Funktiokutsu( // Painokerroin 1
          nimi = Funktionimi.LUKUARVO,
          syoteparametrit = List(
            Syoteparametri(
              avain = "luku",
              arvo = "2.0"))),
        Funktiokutsu( // Painotettava 1
          nimi = Funktionimi.LUKUARVO,
          syoteparametrit = List(
            Syoteparametri(
              avain = "luku",
              arvo = "10.0")),
          tulosTunniste = "tunniste1",
          tallennaTulos = true),
        Funktiokutsu( // Painokerroin 2
          nimi = Funktionimi.LUKUARVO,
          syoteparametrit = List(
            Syoteparametri(
              avain = "luku",
              arvo = "1.5"))),
        Funktiokutsu( // Painotettava 2
          nimi = Funktionimi.LUKUARVO,
          syoteparametrit = List(
            Syoteparametri(
              avain = "luku",
              arvo = "30.0")),
          tulosTunniste = "tunniste2",
          tallennaTulos = true)),
      tulosTunniste = "painotettu",
      tallennaTulos = true)

    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(funktiokutsu)
    val tulos = Laskin.suoritaValintalaskenta(hakukohde, tyhjaHakemus, List(), lasku)

    assert(tulos.getFunktioTulokset.contains("painotettu"))
    val painotettu = tulos.getFunktioTulokset.get("painotettu")
    assert(painotettu.getArvo.equals("18.5714"))

    assert(tulos.getFunktioTulokset.contains("tunniste1"))
    val tunniste1 = tulos.getFunktioTulokset.get("tunniste1")
    assert(tunniste1.getArvo.equals("10.0"))

    assert(tulos.getFunktioTulokset.contains("tunniste2"))
    val tunniste2 = tulos.getFunktioTulokset.get("tunniste2")
    assert(tunniste2.getArvo.equals("30.0"))

    val lista =
      Funktiokutsu(
        nimi = Funktionimi.JA,
        funktioargumentit = List(
          Funktiokutsu(
            nimi = Funktionimi.TOTUUSARVO,
            tulosTunniste = "totuusarvoFalse",
            tallennaTulos = true,
            syoteparametrit = List(
              Syoteparametri("totuusarvo", "false"))),
          Funktiokutsu(
            nimi = Funktionimi.TOTUUSARVO,
            tulosTunniste = "totuusarvoTrue",
            tallennaTulos = true,
            syoteparametrit = List(
              Syoteparametri("totuusarvo", "true")))))

    val laskut = Laskentadomainkonvertteri.muodostaTotuusarvolasku(lista)
    val tulokset = Laskin.suoritaValintalaskenta(hakukohde, tyhjaHakemus, List(), laskut)

    assert(tulokset.getFunktioTulokset.contains("totuusarvoTrue"))
    val totuusarvoTrue = tulokset.getFunktioTulokset.get("totuusarvoTrue")
    assert(totuusarvoTrue.getArvo.equals("true"))

    assert(tulokset.getFunktioTulokset.contains("totuusarvoFalse"))
    val totuusarvoFalse = tulokset.getFunktioTulokset.get("totuusarvoFalse")
    assert(totuusarvoFalse.getArvo.equals("false"))

  }

}
