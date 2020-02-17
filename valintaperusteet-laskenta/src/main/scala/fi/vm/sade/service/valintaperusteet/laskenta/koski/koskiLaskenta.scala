package fi.vm.sade.service.valintaperusteet.laskenta.koski

import fi.vm.sade.service.valintaperusteet.laskenta.AmmatillisenPerustutkinnonValitsija
import fi.vm.sade.service.valintaperusteet.laskenta.AmmatillisenTutkinnonOsanValitsija
import fi.vm.sade.service.valintaperusteet.laskenta.AmmatillisenTutkinnonYtoOsaAlueenValitsija
import fi.vm.sade.service.valintaperusteet.laskenta.api.Hakemus
import fi.vm.sade.service.valintaperusteet.laskenta.koski.Tutkinnot.TutkintoLinssit
import io.circe.Json
import io.circe.optics.JsonPath
import monocle.Optional
import org.joda.time.DateTime
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object KoskiLaskenta {
  val ammatillisenHuomioitavaOpiskeluoikeudenTyyppi: String = "ammatillinenkoulutus"
  val ammatillisenSuorituksenTyyppi: String = "ammatillinentutkinto"

  val ammatillisenHhuomioitavatKoulutustyypit: Set[AmmatillisenPerustutkinnonKoulutustyyppi] =
    Set(AmmatillinenPerustutkinto, AmmatillinenReforminMukainenPerustutkinto, AmmatillinenPerustutkintoErityisopetuksena)

  // Osasuorituksen rakennetta purkavat linssit
  private val _osasuorituksenTyypinKoodiarvo = JsonPath.root.tyyppi.koodiarvo.string
  private val _osasuorituksenKoulutusmoduuli = JsonPath.root.koulutusmoduuli
  val _osasuorituksenKoulutusmoduulinTunnisteenKoodiarvo: Optional[Json, String] = _osasuorituksenKoulutusmoduuli.tunniste.koodiarvo.string

  def sulkeutumisPaivamaara: DateTime = {
    DateTime.now()  // TODO: Tee konfiguroitavaksi
  }

  def etsiAmmatillisetTutkinnot(hakemus: Hakemus): Seq[Tutkinto] = {
    if (hakemus.koskiOpiskeluoikeudet == null) {
      Nil
    } else {
      val tutkintoJsonit = Tutkinnot.etsiValmiitTutkinnot(hakemus.koskiOpiskeluoikeudet, ammatillisenHuomioitavaOpiskeluoikeudenTyyppi, ammatillisenSuorituksenTyyppi, hakemus)
      tutkintoJsonit.zipWithIndex.map { case (tutkintoJson, indeksi) =>
        Tutkinto(
          indeksi,
          TutkintoLinssit.opiskeluoikeudenOid.getOption(tutkintoJson).getOrElse("-"),
          TutkintoLinssit.opiskeluoikeudenVersio.getOption(tutkintoJson).getOrElse(-1),
          TutkintoLinssit.opiskeluoikeudenAikaleima.getOption(tutkintoJson).getOrElse("-"),
          TutkintoLinssit.opiskeluoikeudenOppilaitoksenSuomenkielinenNimi.getOption(tutkintoJson).getOrElse("-"))
      }
    }
  }

  def etsiAmmatillisenTutkinnonOsat(tutkinnonValitsija: AmmatillisenPerustutkinnonValitsija, hakemus: Hakemus): Seq[Osasuoritus] = {
    haeAmmatillisenTutkinnonOsat(tutkinnonValitsija, hakemus)
  }

  def laskeAmmatillisenTutkinnonYtoOsaAlueet(tutkinnonValitsija: AmmatillisenPerustutkinnonValitsija, ytoKoodi: String, hakemus: Hakemus): Int = {
    2
    //haeAmmatillisenTutkinnonYtoOsaAlueet(tutkinnonValitsija, ytoKoodi, hakemus).size
  }

  def haeAmmatillisenTutkinnonOsanLaajuus(tutkinnonValitsija: AmmatillisenPerustutkinnonValitsija,
                                          osanValitsija: AmmatillisenTutkinnonOsanValitsija,
                                          hakemus: Hakemus,
                                          oletusarvo: Option[BigDecimal]
                                     ): Option[BigDecimal] = {
    haeAmmatillisenTutkinnonOsat(tutkinnonValitsija, hakemus)(osanValitsija.osanIndeksi).uusinLaajuus.orElse(oletusarvo)
  }

  def haeAmmatillisenTutkinnonOsanArvosana(tutkinnonValitsija: AmmatillisenPerustutkinnonValitsija,
                                           osanValitsija: AmmatillisenTutkinnonOsanValitsija,
                                           hakemus: Hakemus
                                      ): Option[BigDecimal] = {
    haeAmmatillisenTutkinnonOsat(tutkinnonValitsija, hakemus)(osanValitsija.osanIndeksi).uusinHyvaksyttyArvio.map(BigDecimal(_))
  }

  def haeAmmatillisenYtonOsaAlueenLaajuus(tutkinnonValitsija: AmmatillisenPerustutkinnonValitsija,
                                          osaAlueenValitsija: AmmatillisenTutkinnonYtoOsaAlueenValitsija,
                                          hakemus: Hakemus,
                                          oletusarvo: Option[BigDecimal]
                                         ): Option[BigDecimal] = {
    Some(BigDecimal(3.0)) // TODO implement
  }

  def haeAmmatillisenYtonOsaAlueenArvosana(tutkinnonValitsija: AmmatillisenPerustutkinnonValitsija,
                                           osaAlueenValitsija: AmmatillisenTutkinnonYtoOsaAlueenValitsija,
                                           hakemus: Hakemus,
                                           oletusarvo: Option[BigDecimal]
                                          ): Option[BigDecimal] = {
    Some(BigDecimal(4.7)) // TODO implement
  }

  def haeAmmatillisenTutkinnonKoskeenTallennettuKeskiarvo(tutkinnonValitsija: AmmatillisenPerustutkinnonValitsija, hakemus: Hakemus): Option[BigDecimal] = {
    haeArvoSuorituksista(
      tutkinnonValitsija,
      hakemus,
      suoritukset => suoritukset.headOption.flatMap(JsonPath.root.keskiarvo.bigDecimal.getOption(_)))
  }

  def haeAmmatillisenTutkinnonSuoritustapa(tutkinnonValitsija: AmmatillisenPerustutkinnonValitsija, hakemus: Hakemus): Option[String] = {
    val suoritustapa: Option[Json] = haeArvoSuorituksista(
      tutkinnonValitsija,
      hakemus,
      suoritukset => suoritukset.headOption.flatMap(JsonPath.root.suoritustapa.json.getOption(_)))

    suoritustapa.flatMap { s =>
      if (JsonPath.root.koodistoUri.string.getOption(s).contains("ammatillisentutkinnonsuoritustapa")) {
        JsonPath.root.koodiarvo.string.getOption(s)
      } else {
        None
      }
    }
  }

  private def haeArvoSuorituksista[T](tutkinnonValitsija: AmmatillisenPerustutkinnonValitsija,
                                   hakemus: Hakemus,
                                   arvonHakija: List[Json] => Option[T]
                                  ): Option[T] = {
    if (hakemus.koskiOpiskeluoikeudet == null) {
      None
    } else {
      val tutkinnot = Tutkinnot.etsiValmiitTutkinnot(hakemus.koskiOpiskeluoikeudet, ammatillisenHuomioitavaOpiskeluoikeudenTyyppi, ammatillisenSuorituksenTyyppi, hakemus)
      val suorituksenSallitutKoodit: Set[Int] = ammatillisenHhuomioitavatKoulutustyypit.map(_.koodiarvo)
      val suoritukset = Tutkinnot.etsiValiditSuoritukset(tutkinnot(tutkinnonValitsija.tutkinnonIndeksi), sulkeutumisPaivamaara, suorituksenSallitutKoodit)
      if (suoritukset.size > 1) {
        throw new IllegalStateException(s"Odotettiin täsmälleen yhtä suoritusta hakemuksen ${hakemus.oid} " +
          s"hakijan ammatillisella tutkinnolla ${tutkinnonValitsija.tutkinnonIndeksi} , mutta oli ${suoritukset.size}")
      }
      arvonHakija(suoritukset)
    }
  }

  private def haeAmmatillisenTutkinnonYtoOsaAlueet(tutkinnonValitsija: AmmatillisenPerustutkinnonValitsija, ytoKoodi: String, hakemus: Hakemus): List[Osasuoritus] = {
    if (hakemus.koskiOpiskeluoikeudet == null) {
      Nil
    } else {
      /*
      val osaAlueet = OsaSuoritukset.etsiOsasuoritukset(hakemus, tutkinnonValitsija, ytoKoodi)

      val osasuoritusPredikaatti: Json => Boolean = osasuoritus => {
        "ammatillisentutkinnonosa" == _osasuorituksenTyypinKoodiarvo.getOption(osasuoritus).orNull
      }

      osaAlueet.flatMap(OsaSuoritukset.etsiOsasuoritukset(_, sulkeutumisPaivamaara, osasuoritusPredikaatti))
       */
      Nil
    }
  }

  private def haeAmmatillisenTutkinnonOsat(tutkinnonValitsija: AmmatillisenPerustutkinnonValitsija, hakemus: Hakemus): List[Osasuoritus] = {
    if (hakemus.koskiOpiskeluoikeudet == null) {
      Nil
    } else {
      val oikeaOpiskeluoikeus: Json = Tutkinnot.etsiValmiitTutkinnot(
        json = hakemus.koskiOpiskeluoikeudet,
        opiskeluoikeudenHaluttuTyyppi = ammatillisenHuomioitavaOpiskeluoikeudenTyyppi,
        suorituksenHaluttuTyyppi = ammatillisenSuorituksenTyyppi, hakemus = hakemus)(tutkinnonValitsija.tutkinnonIndeksi)

      val suorituksenSallitutKoodit: Set[Int] = ammatillisenHhuomioitavatKoulutustyypit.map(_.koodiarvo)
      val suoritukset = Tutkinnot.etsiValiditSuoritukset(oikeaOpiskeluoikeus, sulkeutumisPaivamaara, suorituksenSallitutKoodit)

      val osasuoritusPredikaatti: Json => Boolean = osasuoritus => {
        "ammatillisentutkinnonosa" == _osasuorituksenTyypinKoodiarvo.getOption(osasuoritus).orNull
      }

      suoritukset.flatMap(OsaSuoritukset.etsiOsasuoritukset(_, sulkeutumisPaivamaara, osasuoritusPredikaatti))
    }
  }


}

sealed trait AmmatillisenPerustutkinnonKoulutustyyppi {
  val koodiarvo: Int
  val kuvaus: String
}

case object AmmatillinenPerustutkinto extends AmmatillisenPerustutkinnonKoulutustyyppi {
  val koodiarvo: Int = 1
  val kuvaus: String = "Ammatillinen perustutkinto"
}

case object AmmatillinenReforminMukainenPerustutkinto extends AmmatillisenPerustutkinnonKoulutustyyppi {
  val koodiarvo: Int = 26
  val kuvaus: String = "Ammatillinen perustutkinto (reformin mukainen)"
}

case object AmmatillinenPerustutkintoErityisopetuksena extends AmmatillisenPerustutkinnonKoulutustyyppi {
  val koodiarvo: Int = 4
  val kuvaus: String = "Ammatillinen perustutkinto erityisopetuksena"
}

case class Osasuoritus(koulutusmoduulinTunnisteenKoodiarvo: String,
                       koulutusmoduulinNimiFi: String,
                       uusinHyvaksyttyArvio: Option[Int],
                       uusinArviointiasteikko: String,
                       uusinLaajuus: Option[BigDecimal])

case class Tutkinto(indeksi: Int, opiskeluoikeudenOid: String, opiskeluoikeudenVersio: Int, opiskeluoikeudenAikaleima: String, opiskeluoikeudenOppilaitoksenSuomenkielinenNimi: String)
