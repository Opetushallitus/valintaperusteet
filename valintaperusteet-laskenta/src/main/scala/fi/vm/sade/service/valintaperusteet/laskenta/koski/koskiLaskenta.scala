package fi.vm.sade.service.valintaperusteet.laskenta.koski

import fi.vm.sade.service.valintaperusteet.laskenta.AmmatillisenPerustutkinnonValitsija
import fi.vm.sade.service.valintaperusteet.laskenta.AmmatillisenTutkinnonOsanValitsija
import fi.vm.sade.service.valintaperusteet.laskenta.AmmatillisenTutkinnonYtoOsaAlueenValitsija
import fi.vm.sade.service.valintaperusteet.laskenta.api.Hakemus
import io.circe.Json
import io.circe.optics.JsonPath
import monocle.Optional
import org.joda.time.DateTime
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object KoskiLaskenta {
  private val LOG: Logger = LoggerFactory.getLogger(KoskiLaskenta.getClass)
  val ammatillisenHuomioitavaOpiskeluoikeudenTyyppi: String = "ammatillinenkoulutus"
  val ammatillisenSuorituksenTyyppi: String = "ammatillinentutkinto"

  val ammatillisenHhuomioitavatKoulutustyypit: Set[AmmatillisenPerustutkinnonKoulutustyyppi] =
    Set(AmmatillinenPerustutkinto, AmmatillinenReforminMukainenPerustutkinto, AmmatillinenPerustutkintoErityisopetuksena)

  // Suoritusten alla olevien osasuoritusten tietoja etsivä linssi
  private val _osasuoritukset = JsonPath.root.osasuoritukset.each.json

  // Osasuorituksen rakennetta purkavat linssit
  private val _osasuorituksenArviointi = JsonPath.root.arviointi.each.json
  private val _osasuorituksenTyypinKoodiarvo = JsonPath.root.tyyppi.koodiarvo.string
  private val _osasuorituksenKoulutusmoduuli = JsonPath.root.koulutusmoduuli
  private val _osasuorituksenKoulutusmoduulinLaajuudenArvo = _osasuorituksenKoulutusmoduuli.laajuus.arvo.bigDecimal
  val _osasuorituksenKoulutusmoduulinTunnisteenKoodiarvo: Optional[Json, String] = _osasuorituksenKoulutusmoduuli.tunniste.koodiarvo.string
  private val _osasuorituksenKoulutusmoduulinNimiFi = _osasuorituksenKoulutusmoduuli.tunniste.nimi.fi.string

  def sulkeutumisPaivamaara: DateTime = {
    DateTime.now()  // TODO: Tee konfiguroitavaksi
  }

  def laskeAmmatillisetTutkinnot(hakemus: Hakemus): Int = {
    if (hakemus.koskiOpiskeluoikeudet == null) {
      0
    } else {
      Tutkinnot.etsiValmiitTutkinnot(hakemus.koskiOpiskeluoikeudet, ammatillisenHuomioitavaOpiskeluoikeudenTyyppi, ammatillisenSuorituksenTyyppi, hakemus).size
    }
  }

  def laskeAmmatillisenTutkinnonOsat(tutkinnonValitsija: AmmatillisenPerustutkinnonValitsija, hakemus: Hakemus): Int = {
    haeAmmatillisenTutkinnonOsat(tutkinnonValitsija, hakemus).size
  }

  def laskeAmmatillisenTutkinnonYtoOsaAlueet(tutkinnonValitsija: AmmatillisenPerustutkinnonValitsija, ytoKoodi: String, hakemus: Hakemus): Int = {
    2
    //haeAmmatillisenTutkinnonYtoOsaalueet(tutkinnonValitsija, ytoKoodi, hakemus).size
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

  def etsiOsasuoritukset(suoritus: Json,
                         sulkeutumisPäivämäärä: DateTime,
                         osasuoritusPredikaatti: Json => Boolean,
                        ): List[Osasuoritus] = {
    _osasuoritukset.getAll(suoritus).filter(osasuoritusPredikaatti).map(osasuoritus => {
      val osasuorituksenKoodiarvo = _osasuorituksenKoulutusmoduulinTunnisteenKoodiarvo.getOption(osasuoritus).orNull
      val osasuorituksenNimiFi = _osasuorituksenKoulutusmoduulinNimiFi.getOption(osasuoritus).orNull
      val (uusinHyvaksyttyArvio: String, uusinLaajuus: BigDecimal, uusinArviointiAsteikko: String) = etsiUusinArvosanaLaajuusJaArviointiAsteikko(osasuoritus)

      LOG.debug("Osasuorituksen arvio: %s".format(uusinHyvaksyttyArvio))
      LOG.debug("Osasuorituksen laajuus: %s".format(uusinLaajuus))
      LOG.debug("Osasuorituksen nimi: %s".format(osasuorituksenNimiFi))
      LOG.debug("Osasuorituksen koodiarvo: %s".format(osasuorituksenKoodiarvo))

      Osasuoritus(osasuorituksenKoodiarvo, osasuorituksenNimiFi, uusinHyvaksyttyArvio.toIntOption, uusinArviointiAsteikko, Option(uusinLaajuus))
    })
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

  private def haeAmmatillisenTutkinnonYtoOsaalueet(tutkinnonValitsija: AmmatillisenPerustutkinnonValitsija, ytoKoodi: String, hakemus: Hakemus): List[Osasuoritus] = {
    if (hakemus.koskiOpiskeluoikeudet == null) {
      Nil
    } else {
      val oikeaOpiskeluoikeus: Json = Tutkinnot.etsiValmiitTutkinnot(
        json = hakemus.koskiOpiskeluoikeudet,
        opiskeluoikeudenHaluttuTyyppi = ammatillisenHuomioitavaOpiskeluoikeudenTyyppi,
        suorituksenHaluttuTyyppi = ammatillisenSuorituksenTyyppi, hakemus = hakemus)(tutkinnonValitsija.tutkinnonIndeksi)

      val suoritukset = Tutkinnot.etsiValiditYtoOsaAlueet(oikeaOpiskeluoikeus, sulkeutumisPaivamaara, ytoKoodi)

      val osasuoritusPredikaatti: Json => Boolean = osasuoritus => {
        "ammatillisentutkinnonosa" == _osasuorituksenTyypinKoodiarvo.getOption(osasuoritus).orNull
      }

      suoritukset.flatMap(etsiOsasuoritukset(_, sulkeutumisPaivamaara, osasuoritusPredikaatti))
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

      suoritukset.flatMap(etsiOsasuoritukset(_, sulkeutumisPaivamaara, osasuoritusPredikaatti))
    }
  }


  private def etsiUusinArvosanaLaajuusJaArviointiAsteikko(osasuoritus: Json): (String, BigDecimal, String) = {
    val (_, uusinHyvaksyttyArvio, uusinLaajuus, uusinArviointiAsteikko): (String, String, BigDecimal, String) =
      _osasuorituksenArviointi.getAll(osasuoritus)
        .filter(arvio => JsonPath.root.hyväksytty.boolean.getOption(arvio).getOrElse(false))
        .map(arvio => (
          JsonPath.root.päivä.string.getOption(arvio).orNull,
          JsonPath.root.arvosana.koodiarvo.string.getOption(arvio).orNull,
          _osasuorituksenKoulutusmoduulinLaajuudenArvo.getOption(osasuoritus).orNull, // TODO lisää laajuuden yksikkö / estä muut kuin osp
          JsonPath.root.arvosana.koodistoUri.string.getOption(arvio).orNull
        )).sorted(Ordering.Tuple4(Ordering.String.reverse, Ordering.String, Ordering.BigDecimal, Ordering.String))
        .head
    (uusinHyvaksyttyArvio, uusinLaajuus, uusinArviointiAsteikko)
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
