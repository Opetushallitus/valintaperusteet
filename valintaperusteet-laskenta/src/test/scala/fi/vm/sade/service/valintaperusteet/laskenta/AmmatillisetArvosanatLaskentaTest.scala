
package fi.vm.sade.service.valintaperusteet.laskenta

import java.util.Collections
import java.util.{HashMap => JHashMap}
import java.util.{List => JList}
import java.util.{Map => JMap}

import fi.vm.sade.kaava.LaskentaTestUtil.TestHakemus
import fi.vm.sade.kaava.{LaskentaTestUtil, Laskentadomainkonvertteri}
import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi
import fi.vm.sade.service.valintaperusteet.dto.model.Valintaperustelahde
import fi.vm.sade.service.valintaperusteet.laskenta.api.Hakukohde
import fi.vm.sade.service.valintaperusteet.model.{Arvokonvertteriparametri, Funktioargumentti, Funktiokutsu, TekstiRyhma, ValintaperusteViite}
import io.circe.Json
import io.circe.parser
import org.scalatest.funsuite.AnyFunSuite

import scala.io.Source
import scala.jdk.CollectionConverters._

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
    assert(BigDecimal(tulos.get) == BigDecimal("2017"))
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

    val opsMallinenJuuri =
      LaskentaTestUtil.Funktiokutsu(
        nimi = Funktionimi.SUMMA,
        funktioargumentit = List(
          LaskentaTestUtil.Funktiokutsu(
            nimi = Funktionimi.SUMMA,
            funktioargumentit = List(
              LaskentaTestUtil.Funktiokutsu(
                nimi = Funktionimi.MAKSIMI,
                funktioargumentit = List(
                  LaskentaTestUtil.Funktiokutsu(
                    nimi = Funktionimi.MAKSIMI,
                    funktioargumentit = List(
                      LaskentaTestUtil.Funktiokutsu(
                        nimi = Funktionimi.JOS,
                        funktioargumentit = List(
                          LaskentaTestUtil.Funktiokutsu(
                            nimi = Funktionimi.YHTASUURI,
                            funktioargumentit = List(
                              createAmmatillinenYtoArviointiAsteikkoKutsu("101054"),
                              lukuarvo("3"))
                          ),
                          createHaeAmmatillinenYtoArvosanaKutsu("101054"),
                          LaskentaTestUtil.Funktiokutsu(
                            nimi = Funktionimi.JOS,
                            funktioargumentit = List(
                              LaskentaTestUtil.Funktiokutsu(
                                nimi = Funktionimi.YHTASUURI,
                                funktioargumentit = List(
                                  createAmmatillinenYtoArviointiAsteikkoKutsu("101054"),
                                  lukuarvo("3"))
                              ),
                              createAmmatillinenYtoArviointiAsteikkoKutsu("101054"),
                              lukuarvo("5"))
                          ),

                        ))
                    ))
                ))
            ))
        ))

    createAmmatillistenTutkintojenIteroija(
      LaskentaTestUtil.Funktiokutsu(nimi = Funktionimi.JOS,
        funktioargumentit = List(
          LaskentaTestUtil.Funktiokutsu(
            nimi = Funktionimi.YHTASUURI,
            funktioargumentit = List(haeAmmatillisenTutkinnonSuoritustapa, lukuarvo("2015"))
          ),
          opsMallinenJuuri,
          lukuarvo("2017")
        )
      )
    )
  }

  private def lukuarvo(lukuarvo: String): Funktiokutsu = {
    LaskentaTestUtil.Funktiokutsu(
      nimi = Funktionimi.LUKUARVO,
      syoteparametrit = List(LaskentaTestUtil.Syoteparametri("luku", lukuarvo))
    )
  }

  private def loadJson(path: String): Json = {
    val jsonFile: String = Source.fromResource(path).getLines.reduce(_ + _)
    parser.parse(jsonFile).getOrElse(Json.Null)
  }
}
