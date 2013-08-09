package fi.vm.sade.service.valintaperusteet.laskenta

import java.math.{BigDecimal => BigDec}
import scala.math.BigDecimal._

import fi.vm.sade.service.valintaperusteet.laskenta.api.{Osallistuminen, Hakemus}
import org.scalatest._
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta._
import scala.collection.JavaConversions._
import fi.vm.sade.kaava.LaskentaTestUtil.Hakemus
import fi.vm.sade.service.valintaperusteet.laskenta.api.tila.HylattyMetatieto
import scala._
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.KonvertoiLukuarvo
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.Totuusarvo
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.HaeLukuarvo
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.Arvokonvertteri
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.Suurempi
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.SyotettavaValintaperuste
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.Lukuarvovalikonvertteri
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.Arvokonversio
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.HaeTotuusarvo
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.Jos
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.Demografia
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.HaeMerkkijonoJaKonvertoiLukuarvoksi
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.Lukuarvo
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.Valintaperusteviite
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.Lukuarvovalikonversio
import fi.vm.sade.kaava.LaskentaTestUtil.assertTilaHylatty
import fi.vm.sade.kaava.LaskentaTestUtil.assertTilaHyvaksyttavissa
import fi.vm.sade.kaava.LaskentaTestUtil.assertTulosTyhja

/**
 *
 * User: bleed
 * Date: 1/12/13
 * Time: 6:48 PM
 */
class LaskentaTest extends FunSuite {

  val hakukohde = "123"
  val tyhjaHakemus = Hakemus("", Nil, Map[String, String]())

  test("Lukuarvo function returns its value") {
    val (tulos, _) = Laskin.laske(hakukohde, tyhjaHakemus, Lukuarvo(BigDecimal("5.0")))
    assert(BigDecimal(tulos.get) == BigDecimal("5.0"))
  }

  test("Summa function sums two function values") {
    val (tulos, _) = Laskin.laske(hakukohde, tyhjaHakemus, Summa(Lukuarvo(BigDecimal("5.0")), Lukuarvo(BigDecimal("6.0"))))
    assert(BigDecimal(tulos.get) == BigDecimal("11.0"))
  }

  test("KonvertoiLukuarvolukuarvoksi") {
    val arvokonvertteri = Arvokonvertteri[BigDecimal, BigDecimal](List(Arvokonversio(1, 10, false),
      Arvokonversio(BigDecimal("2.0"), BigDecimal("20.0"), false), Arvokonversio(3, 30, false)))
    val konvertoiLukuarvoLukuarvoksi = KonvertoiLukuarvo(arvokonvertteri, Lukuarvo(2))

    val (tulos, _) = Laskin.laske(hakukohde, tyhjaHakemus, konvertoiLukuarvoLukuarvoksi)
    assert(BigDecimal(tulos.get) == BigDecimal("20.0"))
  }

  test("KonvertoiLukuarvovaliLukuarvoksi") {
    val konv = Lukuarvovalikonvertteri(List(Lukuarvovalikonversio(1, 10, 1, false, false),
      Lukuarvovalikonversio(10, 20, 2, false, false),
      Lukuarvovalikonversio(20, 30, 3, false, false)))
    val l = KonvertoiLukuarvo(konv, Lukuarvo(15))
    val (tulos, _) = Laskin.laske(hakukohde, tyhjaHakemus, l)
    assert(BigDecimal(tulos.get) == BigDecimal(2.0))
  }

  test("SummaNParasta selects the n greatest values and calculates their sum") {
    val luvut = List(Lukuarvo(5.0), Lukuarvo(6.0), Lukuarvo(2.0), Lukuarvo(1.0))
    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, SummaNParasta(3, luvut: _*))
    assert(BigDecimal(tulos.get) == BigDecimal(13.0))
  }

  test("NMinimi returns the nth lowest value") {
    val luvut = List(Lukuarvo(5.0), Lukuarvo(6.0), Lukuarvo(2.0), Lukuarvo(1.0))
    val (tulos, _) = Laskin.laske(hakukohde, tyhjaHakemus, NMinimi(2, luvut: _*))
    assert(BigDecimal(tulos.get) == BigDecimal(2.0))
  }

  test("NMaksimi returns the nth greatest value") {
    val luvut = List(Lukuarvo(5.0), Lukuarvo(6.0), Lukuarvo(2.0), Lukuarvo(1.0))
    val (tulos, _) = Laskin.laske(hakukohde, tyhjaHakemus, NMaksimi(2, luvut: _*))
    assert(BigDecimal(tulos.get) == BigDecimal(5.0))
  }

  test("Mediaani returns the middle value of a sequence") {

    val luvut = List(Lukuarvo(5.0), Lukuarvo(6.0), Lukuarvo(2.0), Lukuarvo(1.0))
    val (tulos, _) = Laskin.laske(hakukohde, tyhjaHakemus, Mediaani(luvut))
    // Parillisesta listasta lasketaan keskimmäisten lukujen keskiarvo
    assert(BigDecimal(tulos.get) == BigDecimal(3.5))

    val luvut2 = Lukuarvo(10.0) :: luvut
    // Parittomasta listasta palautetaan keskimmäinen luku

    val (tulos2, _) = Laskin.laske(hakukohde, tyhjaHakemus, Mediaani(luvut2))
    assert(BigDecimal(tulos2.get) == BigDecimal(5.0))
  }

  test("Jos") {
    val ehto = Suurempi(Lukuarvo(5.0), Lukuarvo(10.0))
    val ifHaara = Lukuarvo(100.0)
    val elseHaara = Lukuarvo(500.0)

    val ifLause = Jos(ehto, ifHaara, elseHaara)

    val (tulos, _) = Laskin.laske(hakukohde, tyhjaHakemus, ifLause)
    assert(BigDecimal(tulos.get) == BigDecimal(500.0))

  }

  test("Totuusarvo") {
    val arvo = Totuusarvo(true)
    val (tulos, _) = Laskin.laske(hakukohde, tyhjaHakemus, arvo)
    assert(tulos.get)

    val arvo2 = Totuusarvo(false)
    val (tulos2, _) = Laskin.laske(hakukohde, tyhjaHakemus, arvo2)
    assert(!tulos2.get)
  }

  test("HaeLukuarvo") {
    val hakemus = Hakemus("", Nil, Map("paattotodistus_ka" -> "8.7"))

    val (tulos, _) = Laskin.laske(hakukohde, hakemus,
      HaeLukuarvo(None, None, Valintaperusteviite("paattotodistus_ka", false)))
    assert(BigDecimal(tulos.get) == BigDecimal("8.7"))
  }

  test("HaeTotuusarvo") {
    val hakemus = Hakemus("", Nil, Map("onYlioppilas" -> "true"))

    val (tulos, _) = Laskin.laske(hakukohde, hakemus,
      HaeTotuusarvo(None, None, Valintaperusteviite("onYlioppilas", false)))
    assert(tulos.get)
  }

  test("HaeMerkkijonoJaKonvertoiLukuarvoksi") {
    val hakemus = Hakemus("", Nil, Map("AI_yo" -> "L"))

    val konv = Arvokonvertteri[String, BigDecimal](List(Arvokonversio("puuppa", BigDecimal("20.0"), false),
      Arvokonversio("L", BigDecimal(10.0), false)))
    val f = HaeMerkkijonoJaKonvertoiLukuarvoksi(konv, None, Valintaperusteviite("AI_yo", false))
    val (tulos, tila) = Laskin.laske(hakukohde, hakemus, f)
    assert(BigDecimal(tulos.get) == BigDecimal("10.0"))
  }

  test("Demografia") {
    val hakemukset = List(
      Hakemus("hakemusoid1",
        List("hakutoiveoid1", "hakutoiveoid2", "hakutoiveoid3"),
        Map("sukupuoli" -> "mies")),
      Hakemus("hakemusoid2",
        List("hakutoiveoid1", "hakutoiveoid2", "hakutoiveoid3"),
        Map("sukupuoli" -> "mies")),
      Hakemus("hakemusoid3",
        List("hakutoiveoid1", "hakutoiveoid2", "hakutoiveoid3"),
        Map("sukupuoli" -> "mies")),
      Hakemus("hakemusoid4",
        List("hakutoiveoid1", "hakutoiveoid2", "hakutoiveoid3"),
        Map("sukupuoli" -> "nainen")))

    val hakukohde = "hakutoiveoid1"

    val funktio = Demografia("funktioid1", "sukupuoli", BigDecimal("33.0"))
    val prosessoidutHakemukset = hakemukset.map(h => Esiprosessori.esiprosessoi(hakukohde, seqAsJavaList(hakemukset), h, funktio))

    def check(hakemus: Hakemus) = {
      val avain = Esiprosessori.prosessointiOid(hakukohde, hakemus, funktio)
      hakemus.kentat(avain).toBoolean
    }

    assert(!check(prosessoidutHakemukset(0)))
    assert(!check(prosessoidutHakemukset(1)))
    assert(!check(prosessoidutHakemukset(2)))
    assert(check(prosessoidutHakemukset(3)))
  }

  test("Konvertoilukuarvovalilukuarvoksi aarirajat") {

    def luoFunktio(f: Lukuarvo): KonvertoiLukuarvo = {
      KonvertoiLukuarvo(
        f = f,
        konvertteri = Lukuarvovalikonvertteri(
          konversioMap = List(
            Lukuarvovalikonversio(
              min = BigDecimal("0.0"),
              max = BigDecimal("5.0"),
              palautaHaettuArvo = false,
              paluuarvo = BigDecimal("1.0"),
              hylkaysperuste = false
            ),
            Lukuarvovalikonversio(
              min = BigDecimal("5.0"),
              max = BigDecimal("8.0"),
              palautaHaettuArvo = false,
              paluuarvo = BigDecimal("2.0"),
              hylkaysperuste = false
            ),
            Lukuarvovalikonversio(
              min = BigDecimal("8.0"),
              max = BigDecimal("10.0"),
              palautaHaettuArvo = false,
              paluuarvo = BigDecimal("3.0"),
              hylkaysperuste = false
            )
          )
        ))
    }

    val konvertoiNolla = luoFunktio(Lukuarvo(BigDecimal("0.0")))
    val konvertoiViisi = luoFunktio(Lukuarvo(BigDecimal("5.0")))
    val konvertoiKahdeksan = luoFunktio(Lukuarvo(BigDecimal("8.0")))
    val konvertoiKymmenen = luoFunktio(Lukuarvo(BigDecimal("10.0")))

    val (nollaTulos, _) = Laskin.laske(hakukohde, tyhjaHakemus, konvertoiNolla)
    val (viisiTulos, _) = Laskin.laske(hakukohde, tyhjaHakemus, konvertoiViisi)
    val (kahdeksanTulos, _) = Laskin.laske(hakukohde, tyhjaHakemus, konvertoiKahdeksan)
    val (kymmenenTulos, _) = Laskin.laske(hakukohde, tyhjaHakemus, konvertoiKymmenen)

    assert(BigDecimal(nollaTulos.get) == BigDecimal("1.0"))
    assert(BigDecimal(viisiTulos.get) == BigDecimal("2.0"))
    assert(BigDecimal(kahdeksanTulos.get) == BigDecimal("3.0"))
    assert(BigDecimal(kymmenenTulos.get) == BigDecimal("3.0"))
  }

  test("Konvertoilukuarvovali, ei maaritetty") {
    val f = KonvertoiLukuarvo(
      f = Lukuarvo(BigDecimal("11.0")),
      konvertteri = Lukuarvovalikonvertteri(
        konversioMap = List(
          Lukuarvovalikonversio(
            min = BigDecimal("0.0"),
            max = BigDecimal("5.0"),
            palautaHaettuArvo = false,
            paluuarvo = BigDecimal("1.0"),
            hylkaysperuste = false
          ),
          Lukuarvovalikonversio(
            min = BigDecimal("5.0"),
            max = BigDecimal("8.0"),
            palautaHaettuArvo = false,
            paluuarvo = BigDecimal("2.0"),
            hylkaysperuste = false
          ),
          Lukuarvovalikonversio(
            min = BigDecimal("8.0"),
            max = BigDecimal("10.0"),
            palautaHaettuArvo = false,
            paluuarvo = BigDecimal("3.0"),
            hylkaysperuste = false
          )
        )
      ))
    intercept[RuntimeException] {
      Laskin.laske(hakukohde, tyhjaHakemus, f)
    }
  }

  test("Syotettava valintaperuste, osallistumistieto puuttuu") {
    val hakemus = Hakemus("", Nil, Map("valintakoe" -> "8.7"))

    val (tulos, tila) = Laskin.laske(hakukohde, hakemus,
      HaeLukuarvo(None, None, SyotettavaValintaperuste("valintakoe", true, "valintakoe-OSALLISTUMINEN")))

    assertTulosTyhja(tulos)
    assertTilaHylatty(tila, HylattyMetatieto.Hylattymetatietotyyppi.SYOTETTAVA_ARVO_MERKITSEMATTA)
  }

  test("Syotettava valintaperuste, osallistumistieto EI_OSALLISTUNUT") {
    val hakemus = Hakemus("", Nil, Map("valintakoe" -> "8.7",
      "valintakoe-OSALLISTUMINEN" -> Osallistuminen.EI_OSALLISTUNUT.name))

    val (tulos, tila) = Laskin.laske(hakukohde, hakemus,
      HaeLukuarvo(None, None, SyotettavaValintaperuste("valintakoe", true, "valintakoe-OSALLISTUMINEN")))

    assertTulosTyhja(tulos)
    assertTilaHylatty(tila, HylattyMetatieto.Hylattymetatietotyyppi.EI_OSALLISTUNUT_HYLKAYS)
  }

  test("Syotettava valintaperuste, osallistumistieto OSALLISTUI") {
    val hakemus = Hakemus("", Nil, Map("valintakoe" -> "8.7",
      "valintakoe-OSALLISTUMINEN" -> Osallistuminen.OSALLISTUI.name))

    val (tulos, tila) = Laskin.laske(hakukohde, hakemus,
      HaeLukuarvo(None, None, SyotettavaValintaperuste("valintakoe", true, "valintakoe-OSALLISTUMINEN")))

    assert(BigDecimal(tulos.get) == BigDecimal("8.7"))
    assertTilaHyvaksyttavissa(tila)
  }

  test("Syotettava valintaperuste, osallistumistietoa ei voida tulkita") {
    val hakemus = Hakemus("", Nil, Map("valintakoe" -> "8.7",
      "valintakoe-OSALLISTUMINEN" -> "ehkaosallistui"))

    intercept[RuntimeException] {
      Laskin.laske(hakukohde, hakemus,
        HaeLukuarvo(None, None, SyotettavaValintaperuste("valintakoe", true, "valintakoe-OSALLISTUMINEN")))
    }
  }

}
