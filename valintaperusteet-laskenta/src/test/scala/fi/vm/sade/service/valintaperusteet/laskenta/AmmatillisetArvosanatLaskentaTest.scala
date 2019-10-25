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

  test("Tutkinnon yhteisten tutkinnon osien arvosanat") {
    val lasku = Laskentadomainkonvertteri.muodostaLukuarvolasku(createHaeAmmatillinenArvosanaKutsu())
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
