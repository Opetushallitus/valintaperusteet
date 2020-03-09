
package fi.vm.sade.service.valintaperusteet.laskenta

import java.util.{HashMap => JHashMap}
import java.util.{List => JList}
import java.util.{Map => JMap}

import fi.vm.sade.kaava.LaskentaTestUtil.TestHakemus
import fi.vm.sade.kaava.LaskentaTestUtil
import fi.vm.sade.kaava.Laskentadomainkonvertteri
import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi
import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi.JOS_LAISKA_PARAMETRI
import fi.vm.sade.service.valintaperusteet.laskenta.api.Hakukohde
import fi.vm.sade.service.valintaperusteet.model.Syoteparametri
import fi.vm.sade.service.valintaperusteet.model.Arvokonvertteriparametri
import fi.vm.sade.service.valintaperusteet.model.Arvovalikonvertteriparametri
import fi.vm.sade.service.valintaperusteet.model.Funktiokutsu
import fi.vm.sade.service.valintaperusteet.model.TekstiRyhma
import io.circe.Json
import io.circe.parser
import org.scalatest.funsuite.AnyFunSuite

import scala.io.Source

class AmmatillisetArvosanatLaskentaTest extends AnyFunSuite {

  private val hakukohde: Hakukohde = new Hakukohde("123", new JHashMap[String, String])

  private val suoritukset: JHashMap[String, JList[JMap[String, String]]] = new JHashMap[String, JList[JMap[String, String]]]

  private val hakemus = TestHakemus("", Nil, Map(), suoritukset, loadJson("koski-opiskeluoikeudet.json"))
  private val monenTutkinnonHakemus = TestHakemus("", Nil, Map(), suoritukset, loadJson("koski-monitutkinto.json"))
  private val reforminMukainenHakemus = TestHakemus("", Nil, Map(), suoritukset, loadJson("koski-reforminmukainen-keskiarvon_kanssa.json"))
  private val hakemusJossaOnVainSkipattaviaNayttoja = TestHakemus("", Nil, Map(), suoritukset, loadJson("koski-kaksiskipattavaatutkintoa.json"))

  test("Tutkinnon yhteisten tutkinnon osien arvosanat") {
    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(createHaeAmmatillinenYtoArvosanaKutsu("101054"))
    val (tulos, _) = Laskin.laske(hakukohde, hakemus, lasku)
    assert(BigDecimal(tulos.get) == BigDecimal("4.0"))
  }

  test("Tutkinnon yhteisten tutkinnon osien arvosanoja voi konvertoida") {
    val konvertteriParametrit = new Arvokonvertteriparametri
    konvertteriParametrit.setArvo("4")
    konvertteriParametrit.setPaluuarvo("15")
    konvertteriParametrit.setHylkaysperuste(Boolean.box(false).toString)

    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(createHaeAmmatillinenYtoArvosanaKutsu("101054", konvertteriparametrit = Set(konvertteriParametrit)))
    val (tulos, _) = Laskin.laske(hakukohde, hakemus, lasku)
    assert(BigDecimal(tulos.get) == BigDecimal("15"))
  }

  test("Tutkinnon yhteisten tutkinnon osien arvoasteikko lukuarvona") {
    val lasku1 = Laskentadomainkonvertteri.muodostaLukuarvolasku(
      createAmmatillinenYtoArviointiAsteikkoKutsu("101054")
    )
    val (tulos1, _) = Laskin.laske(hakukohde, hakemus, lasku1)
    assert(tulos1.contains(new java.math.BigDecimal(5)))
  }

  test("Tutkinnon yhteisten tutkinnon osien arvosanat, kun on useampi tutkinto") {
    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(createHaeAmmatillinenYtoArvosanaKutsu("101054"))
    val (tulos, _) = Laskin.laske(hakukohde, monenTutkinnonHakemus, lasku)
    assert(BigDecimal(tulos.get) == BigDecimal("2"))
  }

  test("Tutkinnon yhteisten tutkinnon osien arvoasteikko lukuarvona, kun on useampi tutkinto") {
    val lasku1 = Laskentadomainkonvertteri.muodostaLukuarvolasku(
      createAmmatillinenYtoArviointiAsteikkoKutsu("101054")
    )
    val (tulos1, _) = Laskin.laske(hakukohde, monenTutkinnonHakemus, lasku1)
    assert(tulos1.contains(new java.math.BigDecimal(3)))
  }

  test("Tutkinnon osien laskettu keskiarvo, kun on useampi tutkinto") {
    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(createLaskeAmmatillisenTutkinnonOsienKeskiarvoKutsu())
    val (tulos, _) = Laskin.laske(hakukohde, monenTutkinnonHakemus, lasku)
    assert(BigDecimal(tulos.get) == BigDecimal("4.5667"))
  }

  test("Tutkinnon osien laskettu keskiarvo reformin mukaisesta tutkinnosta") {
    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(createLaskeAmmatillisenTutkinnonOsienKeskiarvoKutsu())
    val (tulos, _) = Laskin.laske(hakukohde, reforminMukainenHakemus, lasku)
    assert(BigDecimal(tulos.get) == BigDecimal("4.5517"))
  }

  test("Tutkinnon yhteisten osien osa-alueiden (YTO) laskettu keskiarvo reformin mukaisesta tutkinnosta") {
    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(createLaskeAmmatillisenTutkinnonYtoOsaAlueidenKeskiarvo())
    val (tulos, _) = Laskin.laske(hakukohde, reforminMukainenHakemus, lasku)
    assert(BigDecimal(tulos.get) == BigDecimal("3.7857"))
  }

  test("Tutkinnon osien Koskeen tallennettu keskiarvo reformin mukaisesta tutkinnosta") {
    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(createHaeAmmatillisenTutkinnonOsienKeskiarvoKutsu())
    val (tulos, _) = Laskin.laske(hakukohde, reforminMukainenHakemus, lasku)
    assert(BigDecimal(tulos.get) == BigDecimal("4.40"))
  }

  test("Tutkinnon suoritustapa") {
    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(createHaeAmmatillisenTutkinnonSuoritustapaKutsu())

    val (tulos, _) = Laskin.laske(hakukohde, hakemus, lasku)
    assert(BigDecimal(tulos.get) == BigDecimal("2015"))

    val (reformitulos, _) = Laskin.laske(hakukohde, reforminMukainenHakemus, lasku)
    assert(BigDecimal(reformitulos.get) == BigDecimal("2017"))

    val (monenTutkinnonTulos, _) = Laskin.laske(hakukohde, monenTutkinnonHakemus, lasku)
    assert(BigDecimal(monenTutkinnonTulos.get) == BigDecimal("2017"))
  }

  test("Kaksi suoritusta sisältävä skipattava tutkinto toimii myös") {
    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(createHaeAmmatillisenTutkinnonSuoritustapaKutsu())

    val (tulos, _) = Laskin.laske(hakukohde, hakemusJossaOnVainSkipattaviaNayttoja, lasku)
    assert(tulos == None)
  }

  test("Testaa koko ammatillisten tutkintojen funktiohierarkia") {
    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(createAmmatillisenTutkintojenKokoHierarkia())

    val (tulos, _) = Laskin.laske(hakukohde, reforminMukainenHakemus, lasku)
    assert(BigDecimal(tulos.get) == BigDecimal("105"))
  }

  def createHaeAmmatillinenYtoArvosanaKutsu(ytoKoodi: String, konvertteriparametrit: Set[Arvokonvertteriparametri] = Set()): Funktiokutsu = {
    createAmmatillistenTutkintojenIteroija(
      LaskentaTestUtil.Funktiokutsu(
        nimi = Funktionimi.HAEAMMATILLINENYTOARVOSANA,
        arvokonvertterit = konvertteriparametrit.toSeq,
        valintaperustetunniste = List(
          LaskentaTestUtil.ValintaperusteViite(onPakollinen = false, tunniste = ytoKoodi))
      ))
  }

  def createLaskeAmmatillisenTutkinnonOsienKeskiarvoKutsu(): Funktiokutsu = {
    createAmmatillistenTutkintojenIteroija(
      LaskentaTestUtil.Funktiokutsu(
        nimi = Funktionimi.ITEROIAMMATILLISETOSAT,
        funktioargumentit = List(
          LaskentaTestUtil.Funktiokutsu(
            nimi = Funktionimi.PAINOTETTUKESKIARVO,
            funktioargumentit = List(
              LaskentaTestUtil.Funktiokutsu(nimi = Funktionimi.HAEAMMATILLISENOSANLAAJUUS),
              LaskentaTestUtil.Funktiokutsu(nimi = Funktionimi.HAEAMMATILLISENOSANARVOSANA))))))
  }

  def createLaskeAmmatillisenTutkinnonYtoOsaAlueidenKeskiarvo(): Funktiokutsu = {
    createAmmatillistenTutkintojenIteroija(
      LaskentaTestUtil.Funktiokutsu(
        nimi = Funktionimi.ITEROIAMMATILLISETYTOOSAALUEET,
        funktioargumentit = List(
          LaskentaTestUtil.Funktiokutsu(
            nimi = Funktionimi.PAINOTETTUKESKIARVO,
            funktioargumentit = List(
              LaskentaTestUtil.Funktiokutsu(nimi = Funktionimi.HAEAMMATILLISENYTOOSAALUEENLAAJUUS),
              LaskentaTestUtil.Funktiokutsu(nimi = Funktionimi.HAEAMMATILLISENYTOOSAALUEENARVOSANA)
            ))
        ),
        valintaperustetunniste = List(LaskentaTestUtil.ValintaperusteViite(onPakollinen = false, tunniste = "400012"))))
  }

  def createHaeAmmatillisenTutkinnonOsienKeskiarvoKutsu(): Funktiokutsu = {
    createAmmatillistenTutkintojenIteroija(
      LaskentaTestUtil.Funktiokutsu(nimi = Funktionimi.HAEAMMATILLISENTUTKINNONKESKIARVO)
    )
  }

  def createHaeAmmatillisenTutkinnonSuoritustapaKutsu(): Funktiokutsu = {
    createAmmatillistenTutkintojenIteroija(
      LaskentaTestUtil.Funktiokutsu(
        nimi = Funktionimi.HAEAMMATILLISENTUTKINNONSUORITUSTAPA,
        arvokonvertterit = List(
          LaskentaTestUtil.Arvokonvertteriparametri(paluuarvo = "2015", arvo = "ops", hylkaysperuste = "false", new TekstiRyhma()),
          LaskentaTestUtil.Arvokonvertteriparametri(paluuarvo = "2017", arvo = "reformi", hylkaysperuste = "false", new TekstiRyhma())
        )))
  }

  def createAmmatillinenYtoArviointiAsteikkoKutsu(parametri: String): Funktiokutsu = {
    createAmmatillistenTutkintojenIteroija(
      LaskentaTestUtil.Funktiokutsu(
        nimi = Funktionimi.HAEAMMATILLINENYTOARVIOINTIASTEIKKO,
        valintaperustetunniste = List(LaskentaTestUtil.ValintaperusteViite(onPakollinen = false, tunniste = parametri)),
        arvokonvertterit = List(
          LaskentaTestUtil.Arvokonvertteriparametri(paluuarvo = "5", arvo = "arviointiasteikkoammatillinen15", hylkaysperuste = "false", new TekstiRyhma()),
          LaskentaTestUtil.Arvokonvertteriparametri(paluuarvo = "3", arvo = "arviointiasteikkoammatillinent1k3", hylkaysperuste = "false", new TekstiRyhma())
        )))
  }

  def createAmmatillistenTutkintojenIteroija(lapsi: Funktiokutsu): Funktiokutsu = {
    LaskentaTestUtil.Funktiokutsu(
      nimi = Funktionimi.ITEROIAMMATILLISETTUTKINNOT,
      funktioargumentit = List(
        LaskentaTestUtil.Funktiokutsu(
          nimi = Funktionimi.MAKSIMI,
          funktioargumentit = List(lapsi))))
  }

  def createAmmatillisenTutkintojenKokoHierarkia(): Funktiokutsu = {
    val haeAmmatillisenTutkinnonSuoritustapa =
      LaskentaTestUtil.Funktiokutsu(
        nimi = Funktionimi.HAEAMMATILLISENTUTKINNONSUORITUSTAPA,
        arvokonvertterit = List(
          LaskentaTestUtil.Arvokonvertteriparametri(paluuarvo = "2015", arvo = "ops", hylkaysperuste = "false", new TekstiRyhma()),
          LaskentaTestUtil.Arvokonvertteriparametri(paluuarvo = "2017", arvo = "reformi", hylkaysperuste = "false", new TekstiRyhma())))

    def lukuarvo(lukuarvo: String): Funktiokutsu = {
      LaskentaTestUtil.Funktiokutsu(
        nimi = Funktionimi.LUKUARVO,
        syoteparametrit = List(LaskentaTestUtil.Syoteparametri("luku", lukuarvo))
      )
    }

    def summa(argumentit: Funktiokutsu*) = {
      LaskentaTestUtil.Funktiokutsu(
        nimi = Funktionimi.SUMMA,
        funktioargumentit = argumentit)
    }

    def maksimi(argumentit: Funktiokutsu*): Funktiokutsu = {
      LaskentaTestUtil.Funktiokutsu(
        nimi = Funktionimi.MAKSIMI,
        funktioargumentit = argumentit)
    }

    def jos(totuusarvo: Funktiokutsu, haara1: Funktiokutsu, haara2: Funktiokutsu): Funktiokutsu = {
      val josLaiskaksi: Syoteparametri = new Syoteparametri()
      josLaiskaksi.setAvain(JOS_LAISKA_PARAMETRI)
      josLaiskaksi.setArvo(true.toString)
      LaskentaTestUtil.Funktiokutsu(
        nimi = Funktionimi.JOS,
        funktioargumentit = List(
          totuusarvo, haara1, haara2),
        syoteparametrit = List(josLaiskaksi))
    }

    def yhtasuuri(haara1: Funktiokutsu, haara2: Funktiokutsu): Funktiokutsu = {
      LaskentaTestUtil.Funktiokutsu(
        nimi = Funktionimi.YHTASUURI,
        funktioargumentit = List(haara1, haara2))
    }

    def haeAmmatillisenTutkinnonTallennettuKeskiarvo(): Funktiokutsu = {
      LaskentaTestUtil.Funktiokutsu(
        nimi = Funktionimi.HAEAMMATILLISENTUTKINNONKESKIARVO,
      )
    }

    def arvovali(min: String, max: String, arvo: String) = {
      LaskentaTestUtil.Arvovalikonvertteriparametri(
        paluuarvo = arvo,
        min = min,
        max = max,
        palautaHaettuArvo = "false",
        kuvaukset = new TekstiRyhma())
    }

    val keskiarvotPisteiksi = List(
      arvovali("0", "1", "0"),
      arvovali("1", "1.1", "0"),
      arvovali("1.11", "1.17", "1"),
      arvovali("1.17", "1.23", "2"),
      arvovali("1.23", "1.30", "3"),
      arvovali("1.30", "1.36", "4"),
      arvovali("1.36", "1.42", "5"),
      arvovali("1.42", "1.49", "6"),
      arvovali("1.49", "1.55", "7"),
      arvovali("1.55", "1.61", "8"),
      arvovali("1.61", "1.67", "9"),
      arvovali("1.67", "1.74", "10"),
      arvovali("1.74", "1.80", "11"),
      arvovali("1.80", "1.86", "12"),
      arvovali("1.86", "1.93", "13"),
      arvovali("1.93", "1.99", "14"),
      arvovali("1.99", "2.05", "15"),
      arvovali("2.05", "2.12", "16"),
      arvovali("2.12", "2.18", "17"),
      arvovali("2.18", "2.24", "18"),
      arvovali("2.24", "2.30", "19"),
      arvovali("2.30", "2.37", "20"),
      arvovali("2.37", "2.43", "21"),
      arvovali("2.43", "2.49", "22"),
      arvovali("2.49", "2.54", "23"),
      arvovali("2.54", "2.58", "24"),
      arvovali("2.58", "2.63", "25"),
      arvovali("2.63", "2.67", "26"),
      arvovali("2.67", "2.71", "27"),
      arvovali("2.71", "2.76", "28"),
      arvovali("2.76", "2.80", "29"),
      arvovali("2.80", "2.85", "30"),
      arvovali("2.85", "2.89", "31"),
      arvovali("2.89", "2.93", "32"),
      arvovali("2.93", "2.98", "33"),
      arvovali("2.98", "3.02", "34"),
      arvovali("3.02", "3.07", "35"),
      arvovali("3.07", "3.11", "36"),
      arvovali("3.11", "3.15", "37"),
      arvovali("3.15", "3.20", "38"),
      arvovali("3.20", "3.24", "39"),
      arvovali("3.24", "3.29", "40"),
      arvovali("3.29", "3.33", "41"),
      arvovali("3.33", "3.37", "42"),
      arvovali("3.37", "3.42", "43"),
      arvovali("3.42", "3.46", "44"),
      arvovali("3.46", "3.50", "45"),
      arvovali("3.50", "3.54", "46"),
      arvovali("3.54", "3.57", "47"),
      arvovali("3.57", "3.61", "48"),
      arvovali("3.61", "3.65", "49"),
      arvovali("3.65", "3.68", "50"),
      arvovali("3.68", "3.72", "51"),
      arvovali("3.72", "3.76", "52"),
      arvovali("3.76", "3.79", "53"),
      arvovali("3.79", "3.83", "54"),
      arvovali("3.83", "3.87", "55"),
      arvovali("3.87", "3.91", "56"),
      arvovali("3.91", "3.94", "57"),
      arvovali("3.94", "3.98", "58"),
      arvovali("3.98", "4.02", "59"),
      arvovali("4.02", "4.05", "60"),
      arvovali("4.05", "4.09", "61"),
      arvovali("4.09", "4.13", "62"),
      arvovali("4.13", "4.16", "63"),
      arvovali("4.16", "4.20", "64"),
      arvovali("4.20", "4.24", "65"),
      arvovali("4.24", "4.28", "66"),
      arvovali("4.24", "4.28", "66"),
      arvovali("4.28", "4.31", "67"),
      arvovali("4.31", "4.35", "68"),
      arvovali("4.35", "4.39", "69"),
      arvovali("4.39", "4.42", "70"),
      arvovali("4.42", "4.46", "71"),
      arvovali("4.46", "4.49", "72"),
      arvovali("4.49", "4.52", "73"),
      arvovali("4.52", "4.55", "74"),
      arvovali("4.55", "4.58", "75"),
      arvovali("4.58", "4.61", "76"),
      arvovali("4.61", "4.64", "77"),
      arvovali("4.64", "4.67", "78"),
      arvovali("4.67", "4.70", "79"),
      arvovali("4.70", "4.73", "80"),
      arvovali("4.73", "4.76", "81"),
      arvovali("4.76", "4.79", "82"),
      arvovali("4.79", "4.82", "83"),
      arvovali("4.82", "4.85", "84"),
      arvovali("4.85", "4.88", "85"),
      arvovali("4.88", "4.91", "86"),
      arvovali("4.91", "4.94", "87"),
      arvovali("4.94", "4.97", "88"),
      arvovali("4.97", "5", "89"),
      arvovali("5", "5.1", "90")
    )

    val ytonKeskiarvotPisteiksi = List(
      arvovali("0", "1", "0"),
      arvovali("1", "2", "1"),
      arvovali("2", "3", "5"),
      arvovali("3", "4", "10"),
      arvovali("4", "5", "15"),
      arvovali("5", "5.1", "20")
    )


    def arvokonvertteriparametri(arvo: String, paluuarvo: String): Arvokonvertteriparametri = {
      LaskentaTestUtil.Arvokonvertteriparametri(paluuarvo = paluuarvo, arvo = arvo, hylkaysperuste = "false", new TekstiRyhma()),
    }

    def konvertoiLukuarvo(arvovalikonvertterit: List[Arvovalikonvertteriparametri], sisafunktio: Funktiokutsu): Funktiokutsu = {
      LaskentaTestUtil.Funktiokutsu(
        nimi = Funktionimi.KONVERTOILUKUARVO,
        funktioargumentit = List(sisafunktio),
        arvovalikonvertterit = arvovalikonvertterit
      )
    }

    def haeJaKonvertoiAmmatillinenYtoArvosana(parametri: String, arvovalikonvertterit: List[Arvokonvertteriparametri]) = {
      LaskentaTestUtil.Funktiokutsu(
        nimi = Funktionimi.HAEAMMATILLINENYTOARVOSANA,
        arvokonvertterit = arvovalikonvertterit,
        valintaperustetunniste = List(
          LaskentaTestUtil.ValintaperusteViite(onPakollinen = false, tunniste = parametri))
      )
    }

    def haeAmmatillinenYtoArviointiAsteikko(parametri: String): Funktiokutsu = {
      LaskentaTestUtil.Funktiokutsu(
        nimi = Funktionimi.HAEAMMATILLINENYTOARVIOINTIASTEIKKO,
        valintaperustetunniste = List(LaskentaTestUtil.ValintaperusteViite(onPakollinen = false, tunniste = parametri)),
        arvokonvertterit = List(
          arvokonvertteriparametri("arviointiasteikkoammatillinen15", "5"),
          arvokonvertteriparametri("arviointiasteikkoammatillinent1k3", "3"),
        ))
    }

    val opsYto1to3pisteytys = List(
      arvokonvertteriparametri("1", "2"),
      arvokonvertteriparametri("2", "13"),
      arvokonvertteriparametri("3", "20")
    )

    val opsYto1to5pisteytys = List(
      arvokonvertteriparametri("1", "2"),
      arvokonvertteriparametri("2", "5"),
      arvokonvertteriparametri("3", "10"),
      arvokonvertteriparametri("4", "15"),
      arvokonvertteriparametri("5", "20")
    )

    def haeYto(parametri1: String, parametri2: String): Funktiokutsu = {
      maksimi(
        jos(
          yhtasuuri(haeAmmatillinenYtoArviointiAsteikko(parametri1), lukuarvo("3")),
          haeJaKonvertoiAmmatillinenYtoArvosana(parametri1, opsYto1to3pisteytys),
          jos(
            yhtasuuri(haeAmmatillinenYtoArviointiAsteikko(parametri1), lukuarvo("5")),
            haeJaKonvertoiAmmatillinenYtoArvosana(parametri1, opsYto1to5pisteytys),
            lukuarvo("0")
          )),
        jos(
          yhtasuuri(haeAmmatillinenYtoArviointiAsteikko(parametri2), lukuarvo("3")),
          haeJaKonvertoiAmmatillinenYtoArvosana(parametri2, opsYto1to3pisteytys),
          jos(
            yhtasuuri(haeAmmatillinenYtoArviointiAsteikko(parametri2), lukuarvo("5")),
            haeJaKonvertoiAmmatillinenYtoArvosana(parametri2, opsYto1to5pisteytys),
            lukuarvo("0")
          )
        )
      )
    }

    def iteroiYtoOsaAlueidenKeskiarvo(ytoKoodi: String): Funktiokutsu= {
      LaskentaTestUtil.Funktiokutsu(
        nimi = Funktionimi.ITEROIAMMATILLISETYTOOSAALUEET,
        funktioargumentit = List(
          LaskentaTestUtil.Funktiokutsu(
            nimi = Funktionimi.PAINOTETTUKESKIARVO,
            funktioargumentit = List(
              LaskentaTestUtil.Funktiokutsu(nimi = Funktionimi.HAEAMMATILLISENYTOOSAALUEENLAAJUUS),
              LaskentaTestUtil.Funktiokutsu(nimi = Funktionimi.HAEAMMATILLISENYTOOSAALUEENARVOSANA)
            ))
        ),
        valintaperustetunniste = List(LaskentaTestUtil.ValintaperusteViite(onPakollinen = false, tunniste = ytoKoodi)))
    }

    val opsMallinenJuuri =
      summa(
        // Ammatillisen ops-mallisen tutkinnon yhteisten tutkinnon osien pisteet
        summa(
          // Ammatillinen yto viestintä- ja vuorovaikutusosaaminen
          haeYto("101053", "400012"),

          // Ammatillinen yto matemaattis-luonnontieteellinen osaaminen
          haeYto("101054", "400013"),

          // Ammatillinen yto yhteiskunta -ja työelämäosaaminen
          haeYto("101055", "400014")
        )
      )

    val reformiMallinenJuuri: Funktiokutsu = {
      summa(
        maksimi(
          summa(
            // Ammatillisen tutkinnon osien pisteet
            konvertoiLukuarvo(
              keskiarvotPisteiksi,
              haeAmmatillisenTutkinnonTallennettuKeskiarvo()
            ),
            maksimi(
              summa(
                // Ammatillinen yto viestintä- ja vuorovaikutusosaaminen
                haeYto("101053", "400012"),

                // Ammatillinen yto matemaattis-luonnontieteellinen osaaminen
                haeYto("101054", "400013"),

                // Ammatillinen yto yhteiskunta -ja työelämäosaaminen
                haeYto("101055", "400014")
              ),
              summa(
                konvertoiLukuarvo(
                  ytonKeskiarvotPisteiksi,
                  iteroiYtoOsaAlueidenKeskiarvo("400012")
                ),
                konvertoiLukuarvo(
                  ytonKeskiarvotPisteiksi,
                  iteroiYtoOsaAlueidenKeskiarvo("400013")
                ),
                konvertoiLukuarvo(
                  ytonKeskiarvotPisteiksi,
                  iteroiYtoOsaAlueidenKeskiarvo("400014")
                )
              )
            )
          )
        )
      )
    }

    createAmmatillistenTutkintojenIteroija(
      jos(
        yhtasuuri(haeAmmatillisenTutkinnonSuoritustapa, lukuarvo("2015")),
        opsMallinenJuuri,
        reformiMallinenJuuri))
  }

  private def loadJson(path: String): Json = {
    val jsonFile: String = Source.fromResource(path).getLines.reduce(_ + _)
    parser.parse(jsonFile).getOrElse(Json.Null)
  }
}
