package fi.vm.sade.kaava

import org.scalatest.FunSuite
import fi.vm.sade.kaava.LaskentaTestUtil._
import fi.vm.sade.service.valintaperusteet.model.{Valintaperustelahde, Funktioargumentti, Funktionimi}
import fi.vm.sade.kaava.LaskentaTestUtil.Funktiokutsu
import java.math.BigDecimal
import fi.vm.sade.service.valintaperusteet.service.validointi.virhe.{Validointivirhe, Virhetyyppi}
import java.util

/**
 * User: kwuoti
 * Date: 25.1.2013
 * Time: 14.57
 */
class LaskentakaavavalidaattoriTest extends FunSuite {

  val validiLukuarvo1 = Funktiokutsu(
    nimi = Funktionimi.LUKUARVO,
    syoteparametrit = List(
      Syoteparametri(
        avain = "luku",
        arvo = "-123.0")))

  val validiLukuarvo2 = Funktiokutsu(
    nimi = Funktionimi.LUKUARVO,
    syoteparametrit = List(
      Syoteparametri(
        avain = "luku",
        arvo = "100.0")))

  val validiLukuarvo3 = Funktiokutsu(
    nimi = Funktionimi.LUKUARVO,
    syoteparametrit = List(
      Syoteparametri(
        avain = "luku",
        arvo = "3.335")))

  val validiTotuusarvo1 = Funktiokutsu(
    nimi = Funktionimi.TOTUUSARVO,
    syoteparametrit = List(
      Syoteparametri(
        avain = "totuusarvo",
        arvo = "false")))

  val validiTotuusarvo2 = Funktiokutsu(
    nimi = Funktionimi.TOTUUSARVO,
    syoteparametrit = List(
      Syoteparametri(
        avain = "totuusarvo",
        arvo = "true")))

  val validiSumma = Funktiokutsu(
    nimi = Funktionimi.SUMMA,
    funktioargumentit = List(validiLukuarvo1, validiLukuarvo2))

  val validiJa = Funktiokutsu(
    nimi = Funktionimi.JA,
    funktioargumentit = List(validiTotuusarvo1, validiTotuusarvo2))

  val validiJos = Funktiokutsu(
    nimi = Funktionimi.JOS,
    funktioargumentit = List(validiTotuusarvo1, validiLukuarvo1, validiLukuarvo2))

  val validKonvertoiLukuarvovaliLukuarvoksi = Funktiokutsu(
    nimi = Funktionimi.KONVERTOILUKUARVO,
    funktioargumentit = List(validiLukuarvo2),
    arvovalikonvertterit = List(
      Arvovalikonvertteriparametri(
        paluuarvo = "40",
        min = "20.0",
        max = "101.0",
        palautaHaettuArvo = "false"),
      Arvovalikonvertteriparametri(
        paluuarvo = "50.0",
        min = "101.0",
        max = "350.0",
        palautaHaettuArvo = "false")))

  val validKonvertoiLukuarvovaliLukuarvoksi2 = Funktiokutsu(
    nimi = Funktionimi.KONVERTOILUKUARVO,
    funktioargumentit = List(validiLukuarvo2),
    arvovalikonvertterit = List(
      Arvovalikonvertteriparametri(
        min = "20.0",
        max = "101.0",
        palautaHaettuArvo = "true"),
      Arvovalikonvertteriparametri(
        min = "101.0",
        max = "350.0",
        palautaHaettuArvo = "true")))

  val validKonvertoiLukuarvoLukuarvoksi = Funktiokutsu(
    nimi = Funktionimi.KONVERTOILUKUARVO,
    funktioargumentit = List(validiLukuarvo2),
    arvokonvertterit = List(
      Arvokonvertteriparametri(
        paluuarvo = "5.0",
        arvo = "100.0",
        hylkaysperuste = "false"),
      Arvokonvertteriparametri(
        paluuarvo = "10.0",
        arvo = "200.0",
        hylkaysperuste = "false")))

  test("Lukuarvo without input parameters") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.LUKUARVO)

    val validationMessages = Laskentakaavavalidaattori.validoiLaskettavaKaava(funktiokutsu).getValidointivirheet
    assert(1 == validationMessages.size)
    assert(Virhetyyppi.SYOTEPARAMETRI_PUUTTUU ==
      validationMessages.get(0).asInstanceOf[Validointivirhe].getVirhetyyppi)
  }

  test("Lukuarvo with incorrect input parameters") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.LUKUARVO,
      syoteparametrit = List(
        Syoteparametri(
          avain = "puuppa",
          arvo = "123.0")))

    val validationMessages = Laskentakaavavalidaattori.validoiLaskettavaKaava(funktiokutsu).getValidointivirheet
    assert(1 == validationMessages.size)
    assert(Virhetyyppi.SYOTEPARAMETRI_PUUTTUU ==
      validationMessages.get(0).asInstanceOf[Validointivirhe].getVirhetyyppi)
  }

  test("Lukuarvo with invalid input parameters") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.LUKUARVO,
      syoteparametrit = List(
        Syoteparametri(
          avain = "luku",
          arvo = "merkkijono")))

    val validationMessages = Laskentakaavavalidaattori.validoiLaskettavaKaava(funktiokutsu).getValidointivirheet
    assert(1 == validationMessages.size)
    assert(Virhetyyppi.VIRHEELLINEN_SYOTEPARAMETRIN_TYYPPI ==
      validationMessages.get(0).asInstanceOf[Validointivirhe].getVirhetyyppi)
  }

  test("Valid lukuarvo") {
    val validationMessages = Laskentakaavavalidaattori.validoiLaskettavaKaava(validiLukuarvo1).getValidointivirheet
    assert(0 == validationMessages.size)
  }

  test("Summa with zero arguments") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.SUMMA)

    val validationMessages = Laskentakaavavalidaattori.validoiLaskettavaKaava(funktiokutsu).getValidointivirheet
    assert(1 == validationMessages.size)
    assert(Virhetyyppi.VAARA_MAARA_FUNKTIOARGUMENTTEJA ==
      validationMessages.get(0).asInstanceOf[Validointivirhe].getVirhetyyppi)
  }

  test("Summa with incompatible arguments") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.SUMMA,
      funktioargumentit = List(validiLukuarvo1, validiTotuusarvo1))

    val validationMessages = Laskentakaavavalidaattori.validoiLaskettavaKaava(funktiokutsu).getValidointivirheet
    assert(1 == validationMessages.size)
    assert(Virhetyyppi.VIRHEELLINEN_FUNKTIOARGUMENTIN_TYYPPI ==
      validationMessages.get(0).asInstanceOf[Validointivirhe].getVirhetyyppi)
  }

  test("Valid summa") {
    val funktiokutsu = validiSumma

    val validationMessages = Laskentakaavavalidaattori.validoiLaskettavaKaava(funktiokutsu).getValidointivirheet
    assert(0 == validationMessages.size)
  }

  test("Totuusarvo with incorrect input parameters") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.TOTUUSARVO,
      syoteparametrit = List(
        Syoteparametri(
          avain = "plop",
          arvo = "false")))

    val validationMessages = Laskentakaavavalidaattori.validoiLaskettavaKaava(funktiokutsu).getValidointivirheet
    assert(1 == validationMessages.size)
    assert(Virhetyyppi.SYOTEPARAMETRI_PUUTTUU ==
      validationMessages.get(0).asInstanceOf[Validointivirhe].getVirhetyyppi)
  }

  test("Totuusarvo with invalid input parameters") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.TOTUUSARVO,
      syoteparametrit = List(
        Syoteparametri(
          avain = "totuusarvo",
          arvo = "tosi")))

    val validationMessages = Laskentakaavavalidaattori.validoiLaskettavaKaava(funktiokutsu).getValidointivirheet
    assert(1 == validationMessages.size)
    assert(Virhetyyppi.VIRHEELLINEN_SYOTEPARAMETRIN_TYYPPI ==
      validationMessages.get(0).asInstanceOf[Validointivirhe].getVirhetyyppi)
  }

  test("Valid totuusarvo") {
    val funktiokutsu = validiTotuusarvo1
    val validationMessages = Laskentakaavavalidaattori.validoiLaskettavaKaava(funktiokutsu).getValidointivirheet
    assert(0 == validationMessages.size)
  }

  test("Ja with incompatible arguments") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.JA,
      funktioargumentit = List(validiTotuusarvo1, validiLukuarvo2))
    val validationMessages = Laskentakaavavalidaattori.validoiLaskettavaKaava(funktiokutsu).getValidointivirheet
    assert(1 == validationMessages.size)
    assert(Virhetyyppi.VIRHEELLINEN_FUNKTIOARGUMENTIN_TYYPPI ==
      validationMessages.get(0).asInstanceOf[Validointivirhe].getVirhetyyppi)
  }

  test("Valid ja") {
    val funktiokutsu = validiJa

    val validationMessages = Laskentakaavavalidaattori.validoiLaskettavaKaava(funktiokutsu).getValidointivirheet
    assert(0 == validationMessages.size)
  }

  test("Jos without required arguments") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.JOS,
      funktioargumentit = List(validiTotuusarvo1, validiLukuarvo1))

    val validationMessages = Laskentakaavavalidaattori.validoiLaskettavaKaava(funktiokutsu).getValidointivirheet
    assert(1 == validationMessages.size)
    assert(Virhetyyppi.VAARA_MAARA_FUNKTIOARGUMENTTEJA ==
      validationMessages.get(0).asInstanceOf[Validointivirhe].getVirhetyyppi)
  }

  test("Jos with incompatible arguments") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.JOS,
      funktioargumentit = List(validiTotuusarvo1, validiLukuarvo1, validiTotuusarvo2))

    val validationMessages = Laskentakaavavalidaattori.validoiLaskettavaKaava(funktiokutsu).getValidointivirheet
    assert(1 == validationMessages.size)
    assert(Virhetyyppi.VIRHEELLINEN_FUNKTIOARGUMENTIN_TYYPPI ==
      validationMessages.get(0).asInstanceOf[Validointivirhe].getVirhetyyppi)
  }

  test("Valid jos") {
    val funktiokutsu = validiJos

    val validationMessages = Laskentakaavavalidaattori.validoiLaskettavaKaava(funktiokutsu).getValidointivirheet
    assert(0 == validationMessages.size)
  }


//  test("KonvertoiLukuarvovaliLukuarvoksi with invalid converter parameters") {
//    val funktiokutsu = Funktiokutsu(
//      nimi = Funktionimi.KONVERTOILUKUARVO,
//      funktioargumentit = List(validiLukuarvo2),
//      arvovalikonvertterit = List(
//        Arvovalikonvertteriparametri(
//          paluuarvo = "kolmesataa",
//          min = "20.0",
//          max = "300.0",
//          palautaHaettuArvo = "false"),
//        Arvovalikonvertteriparametri(
//          paluuarvo = "50.0",
//          min = "300.0",
//          max = "350.0",
//          palautaHaettuArvo = "false")))
//
//    val validationMessages = Laskentakaavavalidaattori.validoiLaskettavaKaava(funktiokutsu).getValidointivirheet
//    assert(1 == validationMessages.size)
//    assert(Virhetyyppi.VIRHEELLINEN_KONVERTTERIPARAMETRIN_PALUUARVOTYYPPI ==
//      validationMessages.get(0).asInstanceOf[Validointivirhe].getVirhetyyppi)
//  }

//  test("KonvertoiLukuarvovaliLukuarvoksi with invalid min/max") {
//    val funktiokutsu = Funktiokutsu(
//      nimi = Funktionimi.KONVERTOILUKUARVO,
//      funktioargumentit = List(validiLukuarvo2),
//      arvovalikonvertterit = List(
//        Arvovalikonvertteriparametri(
//          paluuarvo = "40",
//          min = "5.0",
//          max = "19.9",
//          palautaHaettuArvo = "false"),
//        Arvovalikonvertteriparametri(
//          paluuarvo = "50.0",
//          min = "19.9",
//          max = "15.0",
//          palautaHaettuArvo = "false")))
//
//    val validationMessages = Laskentakaavavalidaattori.validoiLaskettavaKaava(funktiokutsu).getValidointivirheet
//    assert(1 == validationMessages.size)
//    assert(Virhetyyppi.ARVOVALIKONVERTTERIN_MINIMI_SUUREMPI_KUIN_MAKSIMI ==
//      validationMessages.get(0).asInstanceOf[Validointivirhe].getVirhetyyppi)
//  }

  test("KonvertoiLukuarvo without konvertteri") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.KONVERTOILUKUARVO,
      funktioargumentit = List(validiLukuarvo2))

    val validationMessages = Laskentakaavavalidaattori.validoiLaskettavaKaava(funktiokutsu).getValidointivirheet
    assert(1 == validationMessages.size)
    assert(Virhetyyppi.EI_KONVERTTERIPARAMETREJA_MAARITELTY ==
      validationMessages.get(0).asInstanceOf[Validointivirhe].getVirhetyyppi)
  }

  test("KonvertoiLukuarvovaliLukuarvoksi with palautaHaettuArvo") {
    val funktiokutsu = validKonvertoiLukuarvovaliLukuarvoksi2

    val validationMessages = Laskentakaavavalidaattori.validoiLaskettavaKaava(funktiokutsu).getValidointivirheet
    assert(0 == validationMessages.size)
  }

  test("Valid konvertoiLukuarvovaliLukuarvoksi") {
    val funktiokutsu = validKonvertoiLukuarvovaliLukuarvoksi

    val validationMessages = Laskentakaavavalidaattori.validoiLaskettavaKaava(funktiokutsu).getValidointivirheet
    assert(0 == validationMessages.size)
  }

  test("KonvertoiLukuarvoLukuarvoksi with invalid konvertteriarvo") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.KONVERTOILUKUARVO,
      funktioargumentit = List(validiLukuarvo2),
      arvokonvertterit = List(
        Arvokonvertteriparametri(
          paluuarvo = "40",
          arvo = "kymmenen",
          hylkaysperuste = "false"),
        Arvokonvertteriparametri(
          paluuarvo = "50.0",
          arvo = "100.0",
          hylkaysperuste = "false")))

    val validationMessages = Laskentakaavavalidaattori.validoiLaskettavaKaava(funktiokutsu).getValidointivirheet
    assert(1 == validationMessages.size)
    assert(Virhetyyppi.VIRHEELLINEN_ARVOKONVERTTERIN_ARVOTYYPPI ==
      validationMessages.get(0).asInstanceOf[Validointivirhe].getVirhetyyppi)
  }

  test("KonvertoiLukuarvoLukuarvoksi with missing konvertteriparameters") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.KONVERTOILUKUARVO,
      funktioargumentit = List(validiLukuarvo2),
      arvokonvertterit = List(
        Arvokonvertteriparametri(
          paluuarvo = "",
          arvo = "",
          hylkaysperuste = "false"),
        Arvokonvertteriparametri(
          paluuarvo = "50",
          arvo = "",
          hylkaysperuste = "false")))

    val validationMessages = Laskentakaavavalidaattori.validoiLaskettavaKaava(funktiokutsu).getValidointivirheet
    assert(3 == validationMessages.size)
  }

  test("Valid konvertoiLukuarvoLukuarvoksi") {
    val funktiokutsu = validKonvertoiLukuarvoLukuarvoksi

    val validationMessages = Laskentakaavavalidaattori.validoiLaskettavaKaava(funktiokutsu).getValidointivirheet
    assert(0 == validationMessages.size)
  }

  test("Valid haeTotuusarvo") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.HAETOTUUSARVO,
      valintaperustetunniste = List(ValintaperusteViite(
        onPakollinen = true,
        tunniste = "onYlioppilas")))

    val validationMessages = Laskentakaavavalidaattori.validoiLaskettavaKaava(funktiokutsu).getValidointivirheet
    assert(0 == validationMessages.size)
  }

  test("Valid haeLukuarvo without konvertteri") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.HAELUKUARVO,
      valintaperustetunniste = List(ValintaperusteViite(
        onPakollinen = true,
        tunniste = "paasykoepisteet")))

    val validationMessages = Laskentakaavavalidaattori.validoiLaskettavaKaava(funktiokutsu).getValidointivirheet
    assert(0 == validationMessages.size)
  }

  test("Valid haeLukuarvo with arvokonvertteri") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.HAELUKUARVO,
      valintaperustetunniste = List(ValintaperusteViite(
        onPakollinen = true,
        tunniste = "paasykoepisteet")),
      arvokonvertterit = List(
        Arvokonvertteriparametri("10.0", "23.0", "false"),
        Arvokonvertteriparametri("5.0", "13.0", "false")))

    val validationMessages = Laskentakaavavalidaattori.validoiLaskettavaKaava(funktiokutsu).getValidointivirheet
    assert(0 == validationMessages.size)
  }

  test("Valid haeLukuarvo with arvovalikonvertteri") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.HAELUKUARVO,
      valintaperustetunniste = List(ValintaperusteViite(
        onPakollinen = true,
        tunniste = "paasykoepisteet")),
      arvovalikonvertterit = List(
        Arvovalikonvertteriparametri(
          min = "0.0",
          max = "10.0",
          palautaHaettuArvo = "true"),
        Arvovalikonvertteriparametri(
          min = "10.0",
          max = "20.0",
          paluuarvo = "100.0",
          palautaHaettuArvo = "false")))

    val validationMessages = Laskentakaavavalidaattori.validoiLaskettavaKaava(funktiokutsu).getValidointivirheet
    assert(0 == validationMessages.size)
  }

  test("HaeLukuarvo with invalid oletusarvo") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.HAELUKUARVO,
      valintaperustetunniste = List(ValintaperusteViite(
        onPakollinen = true,
        tunniste = "paasykoepisteet")),
      syoteparametrit = List(
        Syoteparametri(avain = "oletusarvo", arvo = "kuuppa")))

    val validationMessages = Laskentakaavavalidaattori.validoiLaskettavaKaava(funktiokutsu).getValidointivirheet
    assert(1 == validationMessages.size)
    assert(Virhetyyppi.VIRHEELLINEN_SYOTEPARAMETRIN_TYYPPI ==
      validationMessages.get(0).asInstanceOf[Validointivirhe].getVirhetyyppi)
  }

  test("HaeLukuarvo with valid oletusarvo") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.HAELUKUARVO,
      valintaperustetunniste = List(ValintaperusteViite(
        onPakollinen = true,
        tunniste = "paasykoepisteet")),
      syoteparametrit = List(
        Syoteparametri(avain = "oletusarvo", arvo = "10.0")))

    val validationMessages = Laskentakaavavalidaattori.validoiLaskettavaKaava(funktiokutsu).getValidointivirheet
    assert(0 == validationMessages.size)
  }

  test("HaeTotuusarvo with invalid oletusarvo") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.HAETOTUUSARVO,
      valintaperustetunniste = List(ValintaperusteViite(
        onPakollinen = true,
        tunniste = "onYlioppilas")),
      syoteparametrit = List(
        Syoteparametri(avain = "oletusarvo", arvo = "tosi")))

    val validationMessages = Laskentakaavavalidaattori.validoiLaskettavaKaava(funktiokutsu).getValidointivirheet
    assert(1 == validationMessages.size)
    assert(Virhetyyppi.VIRHEELLINEN_SYOTEPARAMETRIN_TYYPPI ==
      validationMessages.get(0).asInstanceOf[Validointivirhe].getVirhetyyppi)
  }

  test("HaeTotuusarvo with valid oletusarvo") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.HAETOTUUSARVO,
      valintaperustetunniste = List(ValintaperusteViite(
        onPakollinen = true,
        tunniste = "onYlioppilas")),
      syoteparametrit = List(
        Syoteparametri(avain = "oletusarvo", arvo = "true")))

    val validationMessages = Laskentakaavavalidaattori.validoiLaskettavaKaava(funktiokutsu).getValidointivirheet
    assert(0 == validationMessages.size)
  }

  test("HaeMerkkijonoJaKonvertoiLukuarvoksi with invalid oletusarvo") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.HAEMERKKIJONOJAKONVERTOILUKUARVOKSI,
      valintaperustetunniste = List(ValintaperusteViite(
        onPakollinen = true,
        tunniste = "onYlioppilas")),
      syoteparametrit = List(
        Syoteparametri(avain = "oletusarvo", arvo = "kymmenen")),
      arvokonvertterit = List(
        Arvokonvertteriparametri(
          paluuarvo = "10.0",
          arvo = "L",
          hylkaysperuste = "false")))

    val validationMessages = Laskentakaavavalidaattori.validoiLaskettavaKaava(funktiokutsu).getValidointivirheet
    assert(1 == validationMessages.size)
    assert(Virhetyyppi.VIRHEELLINEN_SYOTEPARAMETRIN_TYYPPI ==
      validationMessages.get(0).asInstanceOf[Validointivirhe].getVirhetyyppi)
  }

  test("HaeMerkkijonoJaKonvertoiLukuarvoksi with valid oletusarvo") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.HAEMERKKIJONOJAKONVERTOILUKUARVOKSI,
      valintaperustetunniste = List(ValintaperusteViite(
        onPakollinen = true,
        tunniste = "onYlioppilas")),
      syoteparametrit = List(
        Syoteparametri(avain = "oletusarvo", arvo = "5.0")),
      arvokonvertterit = List(
        Arvokonvertteriparametri(
          paluuarvo = "10.0",
          arvo = "L",
          hylkaysperuste = "false")))

    val validationMessages = Laskentakaavavalidaattori.validoiLaskettavaKaava(funktiokutsu).getValidointivirheet
    assert(0 == validationMessages.size)
  }

  test("HaeMerkkijonoJaKonvertoiLukuarvoksi without konvertteri") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.HAEMERKKIJONOJAKONVERTOILUKUARVOKSI,
      valintaperustetunniste = List(ValintaperusteViite(
        onPakollinen = true,
        tunniste = "onYlioppilas")),
      syoteparametrit = List(
        Syoteparametri(avain = "oletusarvo", arvo = "5.0")))

    val validationMessages = Laskentakaavavalidaattori.validoiLaskettavaKaava(funktiokutsu).getValidointivirheet
    assert(1 == validationMessages.size)
    assert(Virhetyyppi.EI_KONVERTTERIPARAMETREJA_MAARITELTY ==
      validationMessages.get(0).asInstanceOf[Validointivirhe].getVirhetyyppi)
  }

  test("HaeMerkkijonoJaKonvertoiTotuusarvoksi with invalid oletusarvo") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.HAEMERKKIJONOJAKONVERTOITOTUUSARVOKSI,
      valintaperustetunniste = List(ValintaperusteViite(
        onPakollinen = true,
        tunniste = "joku_tunniste")),
      syoteparametrit = List(
        Syoteparametri(avain = "oletusarvo", arvo = "puuppa")),
      arvokonvertterit = List(
        Arvokonvertteriparametri(
          paluuarvo = "true",
          arvo = "L",
          hylkaysperuste = "false")))

    val validationMessages = Laskentakaavavalidaattori.validoiLaskettavaKaava(funktiokutsu).getValidointivirheet
    assert(1 == validationMessages.size)
    assert(Virhetyyppi.VIRHEELLINEN_SYOTEPARAMETRIN_TYYPPI ==
      validationMessages.get(0).asInstanceOf[Validointivirhe].getVirhetyyppi)
  }

  test("HaeMerkkijonoJaKonvertoiTotuusarvoksi with valid oletusarvo") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.HAEMERKKIJONOJAKONVERTOITOTUUSARVOKSI,
      valintaperustetunniste = List(ValintaperusteViite(
        onPakollinen = true,
        tunniste = "joku_tunniste")),
      syoteparametrit = List(
        Syoteparametri(avain = "oletusarvo", arvo = "true")),
      arvokonvertterit = List(
        Arvokonvertteriparametri(
          paluuarvo = "true",
          arvo = "L",
          hylkaysperuste = "false")))

    val validationMessages = Laskentakaavavalidaattori.validoiLaskettavaKaava(funktiokutsu).getValidointivirheet
    assert(0 == validationMessages.size)
  }

  test("HaeMerkkijonoJaKonvertoiTotuusarvoksi without konvertteri") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.HAEMERKKIJONOJAKONVERTOITOTUUSARVOKSI,
      valintaperustetunniste = List(ValintaperusteViite(
        onPakollinen = true,
        tunniste = "joku_tunniste")),
      syoteparametrit = List(
        Syoteparametri(avain = "oletusarvo", arvo = "true")))

    val validationMessages = Laskentakaavavalidaattori.validoiLaskettavaKaava(funktiokutsu).getValidointivirheet
    assert(1 == validationMessages.size)
    assert(Virhetyyppi.EI_KONVERTTERIPARAMETREJA_MAARITELTY ==
      validationMessages.get(0).asInstanceOf[Validointivirhe].getVirhetyyppi)
  }

  test("Summa with invalid lukuarvo") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.SUMMA,
      funktioargumentit = List(
        Funktiokutsu(
          nimi = Funktionimi.LUKUARVO,
          syoteparametrit = List(
            Syoteparametri(
              avain = "luku",
              arvo = "100.0"))),
        Funktiokutsu(
          nimi = Funktionimi.LUKUARVO,
          syoteparametrit = List(
            Syoteparametri(
              avain = "luku",
              arvo = "kolmekymmenta")))))

    val validoitu = Laskentakaavavalidaattori.validoiLaskettavaKaava(funktiokutsu)
    val isValid = Laskentakaavavalidaattori.onkoLaskettavaKaavaValidi(funktiokutsu)
    assert(!isValid)
  }

  test("HaeLukuarvo with both types of converters") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.HAELUKUARVO,
      arvokonvertterit = List(
        Arvokonvertteriparametri(
          paluuarvo = "5.0",
          arvo = "10.0",
          hylkaysperuste = "true")),
      arvovalikonvertterit = List(
        Arvovalikonvertteriparametri(
          paluuarvo = "20.0",
          min = "0.0",
          max = "100.0",
          palautaHaettuArvo = "false")),
      valintaperustetunniste = List(ValintaperusteViite(
        onPakollinen = true,
        tunniste = "joku_tunniste")))

    val validationMessages = Laskentakaavavalidaattori.validoiLaskettavaKaava(funktiokutsu).getValidointivirheet
    assert(0 == validationMessages.size)
  }

  test("Funktiokutsu with laskentakaava argument") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.SUMMA,
      funktioargumentit = List(
        validiLukuarvo1,
        validiLukuarvo2,
        Laskentakaava(
          funktiokutsu = Funktiokutsu(
            nimi = Funktionimi.LUKUARVO,
            syoteparametrit = List(
              Syoteparametri(
                avain = "luku",
                arvo = "5.0"))),
          nimi = "laskentakaava",
          onLuonnos = false)))

    val validationMessages1 = Laskentakaavavalidaattori.validoiLaskettavaKaava(funktiokutsu).getValidointivirheet
    assert(1 == validationMessages1.size())
    assert(Virhetyyppi.FUNKTIOKUTSUA_EI_OLE_MAARITELTY_FUNKTIOARGUMENTILLE ==
      validationMessages1.get(0).asInstanceOf[Validointivirhe].getVirhetyyppi)

    val validationMessages2 = Laskentakaavavalidaattori.validoiMallinnettuKaava(funktiokutsu).getValidointivirheet
    assert(0 == validationMessages2.size())
  }

  test("Funktiokutsu with draft laskentakaava argument") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.SUMMA,
      funktioargumentit = List(
        validiLukuarvo1,
        validiLukuarvo2,
        Laskentakaava(
          funktiokutsu = Funktiokutsu(
            nimi = Funktionimi.LUKUARVO,
            syoteparametrit = List(
              Syoteparametri(
                avain = "luku",
                arvo = "5.0"))),
          nimi = "laskentakaava",
          onLuonnos = true)))

    val validationMessages1 = Laskentakaavavalidaattori.validoiLaskettavaKaava(funktiokutsu).getValidointivirheet
    assert(1 == validationMessages1.size())
    assert(Virhetyyppi.FUNKTIOKUTSUA_EI_OLE_MAARITELTY_FUNKTIOARGUMENTILLE ==
      validationMessages1.get(0).asInstanceOf[Validointivirhe].getVirhetyyppi)

    val validationMessages2 = Laskentakaavavalidaattori.validoiMallinnettuKaava(funktiokutsu).getValidointivirheet
    assert(1 == validationMessages2.size())
    assert(Virhetyyppi.FUNKTIOARGUMENTIN_LASKENTAKAAVA_ON_LUONNOS ==
      validationMessages2.get(0).asInstanceOf[Validointivirhe].getVirhetyyppi)
  }

  test("Funktiokutsu with argument null values") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.SUMMA)

    val fargs = new util.HashSet[Funktioargumentti]()
    val fa1 = new Funktioargumentti
    fa1.setFunktiokutsuChild(validiLukuarvo1)
    fa1.setIndeksi(1)

    val fa2 = new Funktioargumentti
    fa2.setFunktiokutsuChild(validiLukuarvo1)
    fa2.setIndeksi(2)

    val fa3 = new Funktioargumentti
    fa3.setIndeksi(3)

    fargs.add(fa1)
    fargs.add(fa2)
    fargs.add(fa3)

    funktiokutsu.setFunktioargumentit(fargs)

    val validationMessages1 = Laskentakaavavalidaattori.validoiLaskettavaKaava(funktiokutsu).getValidointivirheet
    assert(1 == validationMessages1.size())

    assert(Virhetyyppi.FUNKTIOKUTSUA_EI_OLE_MAARITELTY_FUNKTIOARGUMENTILLE ==
      validationMessages1.get(0).asInstanceOf[Validointivirhe].getVirhetyyppi)

    val validationMessages2 = Laskentakaavavalidaattori.validoiMallinnettuKaava(funktiokutsu).getValidointivirheet
    assert(1 == validationMessages2.size())

    assert(Virhetyyppi.FUNKTIOARGUMENTTIA_EI_MAARITELTY ==
      validationMessages2.get(0).asInstanceOf[Validointivirhe].getVirhetyyppi)
  }

  test("KeskiarvoNParasta with n less than 1") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.KESKIARVONPARASTA,
      funktioargumentit = List(
        validiLukuarvo1,
        validiLukuarvo2),
      syoteparametrit = List(
        Syoteparametri(avain = "n", arvo = "0")))

    val validationMessages = Laskentakaavavalidaattori.validoiLaskettavaKaava(funktiokutsu).getValidointivirheet
    assert(1 == validationMessages.size())
    assert(Virhetyyppi.N_PIENEMPI_KUIN_YKSI == validationMessages.get(0).asInstanceOf[Validointivirhe].getVirhetyyppi)
  }

  test("KeskiarvoNParasta with n greater than number of funktioarguments") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.KESKIARVONPARASTA,
      funktioargumentit = List(
        validiLukuarvo1,
        validiLukuarvo2),
      syoteparametrit = List(
        Syoteparametri(avain = "n", arvo = "3")))

    val validationMessages = Laskentakaavavalidaattori.validoiLaskettavaKaava(funktiokutsu).getValidointivirheet
    assert(1 == validationMessages.size())
    assert(Virhetyyppi.N_SUUREMPI_KUIN_FUNKTIOARGUMENTTIEN_LKM ==
      validationMessages.get(0).asInstanceOf[Validointivirhe].getVirhetyyppi)
  }

  test("Demografia with invalid percent value") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.DEMOGRAFIA,
      syoteparametrit = List(
        Syoteparametri(avain = "prosenttiosuus", arvo = "110.0"),
        Syoteparametri(avain = "tunniste", arvo = "sukupuoli")))

    val validationMessages = Laskentakaavavalidaattori.validoiLaskettavaKaava(funktiokutsu).getValidointivirheet
    assert(1 == validationMessages.size())
    assert(Virhetyyppi.PROSENTTIOSUUS_EPAVALIDI ==
      validationMessages.get(0).asInstanceOf[Validointivirhe].getVirhetyyppi)
  }

  test("Pyoristys with invalid precision value") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.PYORISTYS,
      funktioargumentit = List(validiLukuarvo1),
      syoteparametrit = List(
        Syoteparametri(avain = "tarkkuus", arvo = "-1"))
    )

    val validationMessages = Laskentakaavavalidaattori.validoiLaskettavaKaava(funktiokutsu).getValidointivirheet
    assert(1 == validationMessages.size())
    assert(Virhetyyppi.TARKKUUS_PIENEMPI_KUIN_NOLLA ==
      validationMessages.get(0).asInstanceOf[Validointivirhe].getVirhetyyppi)
  }

  test("Hae merkkijono ja vertaa yhtasuuruus, vertailtava puuttuu") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.HAEMERKKIJONOJAVERTAAYHTASUURUUS,
      valintaperustetunniste = List(ValintaperusteViite(
        onPakollinen = true,
        tunniste = "aidinkieli"
      )),
      syoteparametrit = List(
        Syoteparametri(avain = "oletusarvo", arvo = "false")
      )
    )

    val validationMessages = Laskentakaavavalidaattori.validoiLaskettavaKaava(funktiokutsu).getValidointivirheet
    assert(1 == validationMessages.size())
    assert(Virhetyyppi.SYOTEPARAMETRI_PUUTTUU ==
      validationMessages.get(0).asInstanceOf[Validointivirhe].getVirhetyyppi)
  }

  test("Hae merkkijono ja vertaa yhtasuuruus, validi") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.HAEMERKKIJONOJAVERTAAYHTASUURUUS,
      valintaperustetunniste = List(ValintaperusteViite(
        onPakollinen = true,
        tunniste = "aidinkieli"
      )),
      syoteparametrit = List(
        Syoteparametri(avain = "oletusarvo", arvo = "false"),
        Syoteparametri(avain = "vertailtava", arvo = "FI")
      )
    )

    val validationMessages = Laskentakaavavalidaattori.validoiLaskettavaKaava(funktiokutsu).getValidointivirheet
    assert(0 == validationMessages.size())
  }

  test("Hae merkkijono ja vertaa yhtasuuruus, virheellinen oletusarvo") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.HAEMERKKIJONOJAVERTAAYHTASUURUUS,
      valintaperustetunniste = List(
        ValintaperusteViite(
          onPakollinen = true,
          tunniste = "aidinkieli"
        )),
      syoteparametrit = List(
        Syoteparametri(avain = "oletusarvo", arvo = "puuppa"),
        Syoteparametri(avain = "vertailtava", arvo = "FI")
      )
    )

    val validationMessages = Laskentakaavavalidaattori.validoiLaskettavaKaava(funktiokutsu).getValidointivirheet
    assert(1 == validationMessages.size())
    assert(Virhetyyppi.VIRHEELLINEN_SYOTEPARAMETRIN_TYYPPI ==
      validationMessages.get(0).asInstanceOf[Validointivirhe].getVirhetyyppi)
  }

  test("Skaalaus") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.SKAALAUS,
      funktioargumentit = List(validiLukuarvo1),
      syoteparametrit = List(
        Syoteparametri(avain = "kohdeskaalaMin", arvo = "0.0"),
        Syoteparametri(avain = "kohdeskaalaMax", arvo = "75.0"),
        Syoteparametri(avain = "kaytaLaskennallistaLahdeskaalaa", arvo = "true")
      )
    )

    val validationMessages = Laskentakaavavalidaattori.validoiLaskettavaKaava(funktiokutsu).getValidointivirheet
    assert(validationMessages.isEmpty)
  }

  test("Skaalaus, kohdeskaala virheellinen") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.SKAALAUS,
      funktioargumentit = List(validiLukuarvo1),
      syoteparametrit = List(
        Syoteparametri(avain = "kohdeskaalaMin", arvo = "100.0"),
        Syoteparametri(avain = "kohdeskaalaMax", arvo = "75.0"),
        Syoteparametri(avain = "kaytaLaskennallistaLahdeskaalaa", arvo = "true")
      )
    )

    val validationMessages = Laskentakaavavalidaattori.validoiLaskettavaKaava(funktiokutsu).getValidointivirheet
    assert(validationMessages.size == 1)
    assert(Virhetyyppi.KOHDESKAALA_VIRHEELLINEN ==
      validationMessages.get(0).asInstanceOf[Validointivirhe].getVirhetyyppi)
  }

  test("Skaalaus, lahdeskaalaa ei annettu") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.SKAALAUS,
      funktioargumentit = List(validiLukuarvo1),
      syoteparametrit = List(
        Syoteparametri(avain = "kohdeskaalaMin", arvo = "0.0"),
        Syoteparametri(avain = "kohdeskaalaMax", arvo = "75.0"),
        Syoteparametri(avain = "kaytaLaskennallistaLahdeskaalaa", arvo = "false")
      )
    )

    val validationMessages = Laskentakaavavalidaattori.validoiLaskettavaKaava(funktiokutsu).getValidointivirheet
    assert(validationMessages.size == 1)
    assert(Virhetyyppi.LAHDESKAALAA_EI_OLE_MAARITELTY ==
      validationMessages.get(0).asInstanceOf[Validointivirhe].getVirhetyyppi)
  }

  test("Skaalaus, lahdeskaala annettu") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.SKAALAUS,
      funktioargumentit = List(validiLukuarvo1),
      syoteparametrit = List(
        Syoteparametri(avain = "kohdeskaalaMin", arvo = "0.0"),
        Syoteparametri(avain = "kohdeskaalaMax", arvo = "75.0"),
        Syoteparametri(avain = "lahdeskaalaMin", arvo = "-120.0"),
        Syoteparametri(avain = "lahdeskaalaMax", arvo = "300.0"),
        Syoteparametri(avain = "kaytaLaskennallistaLahdeskaalaa", arvo = "false")
      )
    )

    val validationMessages = Laskentakaavavalidaattori.validoiLaskettavaKaava(funktiokutsu).getValidointivirheet
    assert(validationMessages.isEmpty)
  }

  test("Skaalaus, lahdeskaala virheellinen") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.SKAALAUS,
      funktioargumentit = List(validiLukuarvo1),
      syoteparametrit = List(
        Syoteparametri(avain = "kohdeskaalaMin", arvo = "0.0"),
        Syoteparametri(avain = "kohdeskaalaMax", arvo = "75.0"),
        Syoteparametri(avain = "lahdeskaalaMin", arvo = "590.0"),
        Syoteparametri(avain = "lahdeskaalaMax", arvo = "300.0"),
        Syoteparametri(avain = "kaytaLaskennallistaLahdeskaalaa", arvo = "false")
      )
    )

    val validationMessages = Laskentakaavavalidaattori.validoiLaskettavaKaava(funktiokutsu).getValidointivirheet
    assert(validationMessages.size == 1)
    assert(Virhetyyppi.LAHDESKAALA_VIRHEELLINEN ==
      validationMessages.get(0).asInstanceOf[Validointivirhe].getVirhetyyppi)
  }

  test("painotettu keskiarvo, liian vahan argumentteja") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.PAINOTETTUKESKIARVO,
      funktioargumentit = List(
        Funktiokutsu(// Painokerroin 1
          nimi = Funktionimi.LUKUARVO,
          syoteparametrit = List(
            Syoteparametri(
              avain = "luku",
              arvo = "2.0"
            )
          )
        ),
        Funktiokutsu(// Painotettava 1
          nimi = Funktionimi.LUKUARVO,
          syoteparametrit = List(
            Syoteparametri(
              avain = "luku",
              arvo = "10.0"
            )
          )
        ),
        Funktiokutsu(// Painokerroin 2
          nimi = Funktionimi.LUKUARVO,
          syoteparametrit = List(
            Syoteparametri(
              avain = "luku",
              arvo = "1.5"
            )
          )
        )
      )
    )

    val validationMessages = Laskentakaavavalidaattori.validoiLaskettavaKaava(funktiokutsu).getValidointivirheet
    assert(validationMessages.size == 1)
    assert(Virhetyyppi.VAARA_MAARA_FUNKTIOARGUMENTTEJA == validationMessages.get(0).asInstanceOf[Validointivirhe].getVirhetyyppi)
  }

  test("painotettu keskiarvo, ei argumentteja") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.PAINOTETTUKESKIARVO
    )

    val validationMessages = Laskentakaavavalidaattori.validoiLaskettavaKaava(funktiokutsu).getValidointivirheet
    assert(validationMessages.size == 1)
    assert(Virhetyyppi.VAARA_MAARA_FUNKTIOARGUMENTTEJA == validationMessages.get(0).asInstanceOf[Validointivirhe].getVirhetyyppi)
  }

  test("syoteparametrin arvo tyhja") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.HAELUKUARVO,
      valintaperustetunniste = List(
        ValintaperusteViite(
          onPakollinen = true,
          tunniste = "tunniste1",
          lahde = Valintaperustelahde.HAETTAVA_ARVO)),
      syoteparametrit = List(Syoteparametri(avain = "oletusarvo", ""))
    )

    val validationMessages = Laskentakaavavalidaattori.validoiLaskettavaKaava(funktiokutsu).getValidointivirheet
    assert(validationMessages.size == 0)
  }

  test("valintaperusteyhtasuuruus, yksi valintaperusteviite") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.VALINTAPERUSTEYHTASUURUUS,
      valintaperustetunniste = List(
        ValintaperusteViite(
          onPakollinen = true,
          tunniste = "tunniste1",
          lahde = Valintaperustelahde.HAETTAVA_ARVO)
      )
    )

    val validationMessages = Laskentakaavavalidaattori.validoiLaskettavaKaava(funktiokutsu).getValidointivirheet
    assert(validationMessages.size == 1)
    assert(Virhetyyppi.VALINTAPERUSTEPARAMETRI_PUUTTUUU == validationMessages.get(0).asInstanceOf[Validointivirhe].getVirhetyyppi)
  }

  test("validi valintaperusteyhtasuuruus") {
    val funktiokutsu = Funktiokutsu(
      nimi = Funktionimi.VALINTAPERUSTEYHTASUURUUS,
      valintaperustetunniste = List(
        ValintaperusteViite(
          onPakollinen = true,
          tunniste = "tunniste1",
          lahde = Valintaperustelahde.HAETTAVA_ARVO),
        ValintaperusteViite(
          onPakollinen = true,
          tunniste = "tunniste2",
          lahde = Valintaperustelahde.HAETTAVA_ARVO)
      )
    )

    val validationMessages = Laskentakaavavalidaattori.validoiLaskettavaKaava(funktiokutsu).getValidointivirheet
    assert(validationMessages.size == 0)
  }
}
