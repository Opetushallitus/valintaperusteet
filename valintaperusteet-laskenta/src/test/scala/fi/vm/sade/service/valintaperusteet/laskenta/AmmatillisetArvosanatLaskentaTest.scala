
package fi.vm.sade.service.valintaperusteet.laskenta

import java.util.Collections
import java.util.{HashMap => JHashMap}
import java.util.{List => JList}
import java.util.{Map => JMap}

import fi.vm.sade.kaava.LaskentaTestUtil.TestHakemus
import fi.vm.sade.kaava.Laskentadomainkonvertteri
import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi
import fi.vm.sade.service.valintaperusteet.dto.model.Valintaperustelahde
import fi.vm.sade.service.valintaperusteet.laskenta.api.Hakukohde
import fi.vm.sade.service.valintaperusteet.model.Arvokonvertteriparametri
import fi.vm.sade.service.valintaperusteet.model.Funktioargumentti
import fi.vm.sade.service.valintaperusteet.model.Funktiokutsu
import fi.vm.sade.service.valintaperusteet.model.ValintaperusteViite
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
    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(createHaeAmmatillinenYtoArvosanaKutsu())
    val (tulos, _) = Laskin.laske(hakukohde, hakemus, lasku)
    assert(BigDecimal(tulos.get) == BigDecimal("4.0"))
  }

  test("Tutkinnon yhteisten tutkinnon osien arvosanoja voi konvertoida") {
    val konvetteriParameteri = new Arvokonvertteriparametri
    konvetteriParameteri.setArvo("4")
    konvetteriParameteri.setPaluuarvo("15")
    konvetteriParameteri.setHylkaysperuste(Boolean.box(false).toString)

    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(createHaeAmmatillinenYtoArvosanaKutsu(Set(konvetteriParameteri)))
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
    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(createHaeAmmatillinenYtoArvosanaKutsu())
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
    assert(BigDecimal(tulos.get) == BigDecimal("4.7000"))
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

  def createHaeAmmatillinenYtoArvosanaKutsu(konvertteriparametrit: Set[Arvokonvertteriparametri] = Set()): Funktiokutsu = {

    val kutsu: Funktiokutsu = new Funktiokutsu
    kutsu.setFunktionimi(Funktionimi.HAEAMMATILLINENYTOARVOSANA)
    kutsu.setArvokonvertteriparametrit(konvertteriparametrit.asJava)

    val viite: ValintaperusteViite = new ValintaperusteViite
    viite.setTunniste("101054")
    viite.setIndeksi(0)
    viite.setLahde(Valintaperustelahde.HAETTAVA_ARVO)

    kutsu.getValintaperusteviitteet.add(viite)

    createAmmatillistenTutkintojenIteroija(kutsu)
  }

  def createLaskeAmmatillisenTutkinnonOsienKeskiarvoKutsu(konvertteriparametrit: Set[Arvokonvertteriparametri] = Set()): Funktiokutsu = {
    val juurikutsu: Funktiokutsu =  new Funktiokutsu
    juurikutsu.setFunktionimi(Funktionimi.ITEROIAMMATILLISETOSAT)

    val keskiarvokutsu: Funktiokutsu = new Funktiokutsu
    keskiarvokutsu.setFunktionimi(Funktionimi.PAINOTETTUKESKIARVO)

    val laajuuskutsu: Funktiokutsu = new Funktiokutsu
    laajuuskutsu.setFunktionimi(Funktionimi.HAEAMMATILLISENOSANLAAJUUS)
    laajuuskutsu.setArvokonvertteriparametrit(konvertteriparametrit.asJava)

    val arvosanakutsu: Funktiokutsu = new Funktiokutsu
    arvosanakutsu.setFunktionimi(Funktionimi.HAEAMMATILLISENOSANARVOSANA)
    arvosanakutsu.setArvokonvertteriparametrit(konvertteriparametrit.asJava)

    juurikutsu.setFunktioargumentit(Collections.singleton(luoFunktioargumentti(keskiarvokutsu, 0)))
    keskiarvokutsu.setFunktioargumentit(Set(
      luoFunktioargumentti(laajuuskutsu, 0),
      luoFunktioargumentti(arvosanakutsu, 1)
    ).asJava)

    createAmmatillistenTutkintojenIteroija(juurikutsu)
  }

  def createLaskeAmmatillisenTutkinnonYtoOsaAlueidenKeskiarvo(konvertteriparametrit: Set[Arvokonvertteriparametri] = Set()): Funktiokutsu = {
    val viite: ValintaperusteViite = new ValintaperusteViite
    viite.setTunniste("400012")
    viite.setIndeksi(0)
    viite.setLahde(Valintaperustelahde.HAETTAVA_ARVO)

    val juurikutsu: Funktiokutsu =  new Funktiokutsu
    juurikutsu.setFunktionimi(Funktionimi.ITEROIAMMATILLISETYTOOSAALUEET)
    juurikutsu.getValintaperusteviitteet.add(viite)

    val keskiarvokutsu: Funktiokutsu = new Funktiokutsu
    keskiarvokutsu.setFunktionimi(Funktionimi.PAINOTETTUKESKIARVO)

    val laajuuskutsu: Funktiokutsu = new Funktiokutsu
    laajuuskutsu.setFunktionimi(Funktionimi.HAEAMMATILLISENYTOOSAALUEENLAAJUUS)
    laajuuskutsu.setArvokonvertteriparametrit(konvertteriparametrit.asJava)

    val arvosanakutsu: Funktiokutsu = new Funktiokutsu
    arvosanakutsu.setFunktionimi(Funktionimi.HAEAMMATILLISENYTOOSAALUEENARVOSANA)
    arvosanakutsu.setArvokonvertteriparametrit(konvertteriparametrit.asJava)

    juurikutsu.setFunktioargumentit(Collections.singleton(luoFunktioargumentti(keskiarvokutsu, 0)))
    keskiarvokutsu.setFunktioargumentit(Set(
      luoFunktioargumentti(laajuuskutsu, 0),
      luoFunktioargumentti(arvosanakutsu, 1)
    ).asJava)

    createAmmatillistenTutkintojenIteroija(juurikutsu)

  }

  def createHaeAmmatillisenTutkinnonOsienKeskiarvoKutsu(konvertteriparametrit: Set[Arvokonvertteriparametri] = Set()): Funktiokutsu = {
    val kutsu: Funktiokutsu =  new Funktiokutsu
    kutsu.setFunktionimi(Funktionimi.HAEAMMATILLISENTUTKINNONKESKIARVO)
    createAmmatillistenTutkintojenIteroija(kutsu)
  }

  def createHaeAmmatillisenTutkinnonSuoritustapaKutsu(): Funktiokutsu = {
    val konvetteriParameteriOps = new Arvokonvertteriparametri
    konvetteriParameteriOps.setArvo("ops")
    konvetteriParameteriOps.setPaluuarvo("2015")
    konvetteriParameteriOps.setHylkaysperuste(Boolean.box(false).toString)

    val konvetteriParameteriReformi = new Arvokonvertteriparametri
    konvetteriParameteriReformi.setArvo("reformi")
    konvetteriParameteriReformi.setPaluuarvo("2017")
    konvetteriParameteriReformi.setHylkaysperuste(Boolean.box(false).toString)

    val konvertteriparametrit = Set(konvetteriParameteriOps, konvetteriParameteriReformi)

    val kutsu: Funktiokutsu = new Funktiokutsu
    kutsu.setArvokonvertteriparametrit(konvertteriparametrit.asJava)
    kutsu.setFunktionimi(Funktionimi.HAEAMMATILLISENTUTKINNONSUORITUSTAPA)
    createAmmatillistenTutkintojenIteroija(kutsu)
  }

  def createAmmatillinenYtoArviointiAsteikkoKutsu(parametri: String): Funktiokutsu = {
    val kutsu: Funktiokutsu = new Funktiokutsu
    kutsu.setFunktionimi(Funktionimi.HAEAMMATILLINENYTOARVIOINTIASTEIKKO)

    val viite: ValintaperusteViite = new ValintaperusteViite
    viite.setTunniste(parametri)
    viite.setIndeksi(0)
    viite.setLahde(Valintaperustelahde.HAETTAVA_ARVO)

    kutsu.getValintaperusteviitteet.add(viite)

    val konvetteriParameteri1_5 = new Arvokonvertteriparametri
    konvetteriParameteri1_5.setArvo("arviointiasteikkoammatillinen15")
    konvetteriParameteri1_5.setPaluuarvo("5")
    konvetteriParameteri1_5.setHylkaysperuste(Boolean.box(false).toString)

    val konvetteriParameteri1_3 = new Arvokonvertteriparametri
    konvetteriParameteri1_3.setArvo("arviointiasteikkoammatillinent1k3")
    konvetteriParameteri1_3.setPaluuarvo("3")
    konvetteriParameteri1_3.setHylkaysperuste(Boolean.box(false).toString)

    kutsu.setArvokonvertteriparametrit(Set(konvetteriParameteri1_5, konvetteriParameteri1_3).asJava)

    createAmmatillistenTutkintojenIteroija(kutsu)
  }

  private def createAmmatillistenTutkintojenIteroija(lapsi: Funktiokutsu): Funktiokutsu = {
    val juurikutsu: Funktiokutsu =  new Funktiokutsu
    juurikutsu.setFunktionimi(Funktionimi.ITEROIAMMATILLISETTUTKINNOT)

    val maksimi: Funktiokutsu = new Funktiokutsu
    maksimi.setFunktionimi(Funktionimi.MAKSIMI)

    maksimi.setFunktioargumentit(Collections.singleton(luoFunktioargumentti(lapsi, 0)))

    juurikutsu.setFunktioargumentit(Collections.singleton(luoFunktioargumentti(maksimi, 0)))
    juurikutsu
  }

  private def luoFunktioargumentti(kutsu: Funktiokutsu, argumentinIndeksi: Int): Funktioargumentti = {
    val a = new Funktioargumentti
    a.setIndeksi(argumentinIndeksi)
    a.setFunktiokutsuChild(kutsu)
    a
  }

  private def loadJson(path: String): Json = {
    val jsonFile: String = Source.fromResource(path).getLines.reduce(_ + _)
    parser.parse(jsonFile).getOrElse(Json.Null)
  }
}
