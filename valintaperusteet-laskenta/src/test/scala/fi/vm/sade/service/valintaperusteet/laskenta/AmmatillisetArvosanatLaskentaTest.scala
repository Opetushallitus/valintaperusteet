package fi.vm.sade.service.valintaperusteet.laskenta

import java.util

import fi.vm.sade.kaava.LaskentaTestUtil.TestHakemus
import fi.vm.sade.kaava.Laskentadomainkonvertteri
import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi
import fi.vm.sade.service.valintaperusteet.dto.model.Valintaperustelahde
import fi.vm.sade.service.valintaperusteet.laskenta.api.Hakukohde
import fi.vm.sade.service.valintaperusteet.model.Arvokonvertteriparametri
import fi.vm.sade.service.valintaperusteet.model.Funktiokutsu
import fi.vm.sade.service.valintaperusteet.model.ValintaperusteViite
import io.circe.Json
import io.circe.parser
import org.scalatest.funsuite.AnyFunSuite

import scala.io.Source
import scala.jdk.CollectionConverters._

class AmmatillisetArvosanatLaskentaTest extends AnyFunSuite {

  def loadJson(path: String): Json = {
    val jsonFile: String = Source.fromResource(path).getLines.reduce(_ + _)
    parser.parse(jsonFile).getOrElse(Json.Null)
  }

  val hakukohde = new Hakukohde("123", new util.HashMap[String, String])

  val suoritukset = new util.HashMap[String, util.List[util.Map[String, String]]]

  val koskiopiskeluoikeudet: Json = loadJson("koski-opiskeluoikeudet.json")

  val hakemus = TestHakemus("", Nil, Map(), suoritukset, koskiopiskeluoikeudet)

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

  def createHaeAmmatillinenYtoArvosanaKutsu(konvertteriparametrit: Set[Arvokonvertteriparametri] = Set()): Funktiokutsu = {

    val kutsu: Funktiokutsu = new Funktiokutsu
    kutsu.setFunktionimi(Funktionimi.HAEAMMATILLINENYTOARVOSANA)
    kutsu.setArvokonvertteriparametrit(konvertteriparametrit.asJava)

    val viite: ValintaperusteViite = new ValintaperusteViite
    viite.setTunniste("101054")
    viite.setIndeksi(0)
    viite.setLahde(Valintaperustelahde.HAETTAVA_ARVO)

    kutsu.getValintaperusteviitteet.add(viite)

    kutsu
  }

  def createAmmatillinenYtoArviointiAsteikkoKutsu(parametri: String): Funktiokutsu = {
    val kutsu: Funktiokutsu = new Funktiokutsu
    kutsu.setFunktionimi(Funktionimi.HAEAMMATILLINENYTOARVIOINTIASTEIKKO)

    val viite: ValintaperusteViite = new ValintaperusteViite
    viite.setTunniste(parametri)
    viite.setIndeksi(0)
    viite.setLahde(Valintaperustelahde.HAETTAVA_ARVO)

    kutsu.getValintaperusteviitteet.add(viite)

    val konvetteriParameteri = new Arvokonvertteriparametri
    konvetteriParameteri.setArvo("arviointiasteikkoammatillinen15")
    konvetteriParameteri.setPaluuarvo("5")
    konvetteriParameteri.setHylkaysperuste(Boolean.box(false).toString)

    kutsu.setArvokonvertteriparametrit(Set(konvetteriParameteri).asJava)
    kutsu
  }
}
