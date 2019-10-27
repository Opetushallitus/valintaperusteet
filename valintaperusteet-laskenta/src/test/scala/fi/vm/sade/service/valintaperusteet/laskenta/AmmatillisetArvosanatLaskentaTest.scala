package fi.vm.sade.service.valintaperusteet.laskenta

import java.util

import fi.vm.sade.kaava.LaskentaTestUtil.TestHakemus
import fi.vm.sade.kaava.Laskentadomainkonvertteri
import fi.vm.sade.service.valintaperusteet.dto.model.{Funktionimi, Valintaperustelahde}
import fi.vm.sade.service.valintaperusteet.laskenta.api.Hakukohde
import fi.vm.sade.service.valintaperusteet.model.{Funktiokutsu, ValintaperusteViite, _}
import org.scalatest._

import scala.collection.JavaConversions._

class AmmatillisetArvosanatLaskentaTest extends FunSuite {

  val hakukohde = new Hakukohde("123", new util.HashMap[String, String])

  val kentat = new util.HashMap[String, String](){{
    put("ammatillinen-tutkinto.yhteiset-tutkinnon-osat", "3.0")
  }}
  val suoritukset = new util.HashMap[String, util.List[util.Map[String, String]]]
  val hakemus = TestHakemus("", Nil, kentat.toMap, suoritukset)

  test("Tutkinnon yhteisten tutkinnon osien arvosanat") {
    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(createHaeAmmatillinenArvosanaKutsu())
    val (tulos, _) = Laskin.laske(hakukohde, hakemus, lasku)
    assert(BigDecimal(tulos.get) == BigDecimal("3.0"))
  }

  def createHaeAmmatillinenArvosanaKutsu(): Funktiokutsu = {

    val kutsu: Funktiokutsu = new Funktiokutsu
    kutsu.setFunktionimi(Funktionimi.HAEAMMATILLINENARVOSANA)

    val viite: ValintaperusteViite = new ValintaperusteViite
    viite.setTunniste("ammatillinen-tutkinto.yhteiset-tutkinnon-osat")
    viite.setIndeksi(0)
    viite.setLahde(Valintaperustelahde.HAETTAVA_ARVO)
    viite.setEpasuoraViittaus(false) // TODO: pakollinen?
    viite.setOnPakollinen(false) // TODO: pakollinen?

    kutsu.getValintaperusteviitteet.add(viite)

    kutsu
  }

}
