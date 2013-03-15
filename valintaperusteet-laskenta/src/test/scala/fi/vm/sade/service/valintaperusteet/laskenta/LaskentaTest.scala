package fi.vm.sade.service.valintaperusteet.laskenta

import api.Hakemus
import org.scalatest._
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta._
import scala.collection.JavaConversions._
import fi.vm.sade.kaava.LaskentaTestUtil.Hakemus

/**
 *
 * User: bleed
 * Date: 1/12/13
 * Time: 6:48 PM
 */
class LaskentaTest extends FunSuite {

  val hakukohde = "123"
  val tyhjaHakemus = Hakemus("",Nil, Map[String, String]())

  test("Lukuarvo function returns its value") {
    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, Lukuarvo(5.0))
    assert(tulos.get == 5.0)
  }

  test("Summa function sums two function values") {
    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, Summa(Lukuarvo(5.0), Lukuarvo(6.0)))
    assert(tulos.get == 11.0)
  }

  test("KonvertoiLukuarvolukuarvoksi") {
    val arvokonvertteri = Arvokonvertteri[Double, Double](List(Arvokonversio(1.0, 10.0, false),
      Arvokonversio(2.0, 20.0, false), Arvokonversio(3.0, 30.0, false)))
    val konvertoiLukuarvoLukuarvoksi = KonvertoiLukuarvo(arvokonvertteri, Lukuarvo(2.0))

    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, konvertoiLukuarvoLukuarvoksi)
    assert(tulos.get == 20.0)
  }

  test("KonvertoiLukuarvovaliLukuarvoksi") {
    val konv = Lukuarvovalikonvertteri(List(Lukuarvovalikonversio(0.0, 10.0, 1.0, false, false),
      Lukuarvovalikonversio(10.0, 20.0, 2.0, false, false), Lukuarvovalikonversio(20.0, 30.0, 3.0, false, false)))
    val l = KonvertoiLukuarvo(konv, Lukuarvo(15.0))
    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, l)
    assert(tulos.get == 2.0)
  }

  test("SummaNParasta selects the n greatest values and calculates their sum") {
    val luvut = List(Lukuarvo(5.0), Lukuarvo(6.0), Lukuarvo(2.0), Lukuarvo(1.0))
    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, SummaNParasta(3, luvut: _*))
    assert(tulos.get == 13.0)
  }

  test("NMinimi returns the nth lowest value") {
    val luvut = List(Lukuarvo(5.0), Lukuarvo(6.0), Lukuarvo(2.0), Lukuarvo(1.0))
    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, NMinimi(2, luvut: _*))
    assert(tulos.get == 2.0)
  }

  test("NMaksimi returns the nth greatest value") {
    val luvut = List(Lukuarvo(5.0), Lukuarvo(6.0), Lukuarvo(2.0), Lukuarvo(1.0))
    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, NMaksimi(2, luvut: _*))
    assert(tulos.get == 5.0)
  }

  test("Mediaani returns the middle value of a sequence") {

    val luvut = List(Lukuarvo(5.0), Lukuarvo(6.0), Lukuarvo(2.0), Lukuarvo(1.0))
    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, Mediaani(luvut))
    // Parillisesta listasta lasketaan keskimmäisten lukujen keskiarvo
    assert(tulos.get == 3.5)

    val luvut2 = Lukuarvo(10.0) :: luvut
    // Parittomasta listasta palautetaan keskimmäinen luku

    val (tulos2, tila2) = Laskin.laske(hakukohde, tyhjaHakemus, Mediaani(luvut2))
    assert(tulos2.get == 5.0)
  }

  test("Jos") {
    val ehto = Suurempi(Lukuarvo(5.0), Lukuarvo(10.0))
    val ifHaara = Lukuarvo(100.0)
    val elseHaara = Lukuarvo(500.0)

    val ifLause = Jos(ehto, ifHaara, elseHaara)

    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, ifLause)
    assert(tulos.get == 500.0)

  }

  test("Totuusarvo") {
    val arvo = Totuusarvo(true)
    val (tulos, tila) = Laskin.laske(hakukohde, tyhjaHakemus, arvo)
    assert(tulos.get)

    val arvo2 = Totuusarvo(false)
    val (tulos2, tila2) = Laskin.laske(hakukohde, tyhjaHakemus, arvo2)
    assert(!tulos2.get)
  }

  test("HaeLukuarvo") {
    val hakemus = Hakemus("",Nil, Map("paattotodistus_ka" -> "8.7"))

    val (tulos, tila) = Laskin.laske(hakukohde, hakemus,
      HaeLukuarvo(None, None, Valintaperusteviite("paattotodistus_ka", false)))
    assert(tulos.get == 8.7)
  }

  test("HaeTotuusarvo") {
    val hakemus = Hakemus("",Nil, Map("onYlioppilas" -> "true"))

    val (tulos, tila) = Laskin.laske(hakukohde, hakemus,
      HaeTotuusarvo(None, None, Valintaperusteviite("onYlioppilas", false)))
    assert(tulos.get)
  }

  test("HaeMerkkijonoJaKonvertoiLukuarvoksi") {
    val hakemus = Hakemus("",Nil, Map("AI_yo" -> "L"))

    val konv = Arvokonvertteri[String, Double](List(Arvokonversio("puuppa", 20.0, false),
      Arvokonversio("L", 10.0, false)))
    val f = HaeMerkkijonoJaKonvertoiLukuarvoksi(konv, None, Valintaperusteviite("AI_yo", false))
    val (tulos, tila) = Laskin.laske(hakukohde, hakemus, f)
    assert(tulos.get == 10.0)
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
        Map("sukupuoli" -> "nainen"))
    )

    val hakukohde = "hakutoiveoid1"

    val funktio = Demografia("funktioid1", "sukupuoli", 33.0)
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
}
