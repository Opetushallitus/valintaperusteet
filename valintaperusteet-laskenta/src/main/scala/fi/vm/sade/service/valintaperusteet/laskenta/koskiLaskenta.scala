package fi.vm.sade.service.valintaperusteet.laskenta

import fi.vm.sade.service.valintaperusteet.laskenta.api.Hakemus
import io.circe.Json
import io.circe.optics.JsonPath
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object KoskiLaskenta {
  private val LOG: Logger = LoggerFactory.getLogger(KoskiLaskenta.getClass)
  private val ammatillisenHuomioitavaOpiskeluoikeudenTyyppi: String = "ammatillinenkoulutus"
  private val ammatillisenSuorituksenTyyppi: String = "ammatillinentutkinto"

  val ammatillisenHhuomioitavatKoulutustyypit: Set[AmmatillisenPerustutkinnonKoulutustyyppi] =
    Set(AmmatillinenPerustutkinto, AmmatillinenReforminMukainenPerustutkinto, AmmatillinenPerustutkintoErityisopetuksena)

  def haeYtoArvosana(hakemus: Hakemus, valintaperusteviite: Laskenta.Valintaperuste, oletusarvo: Option[BigDecimal]): Option[BigDecimal] = {
    if (hakemus.koskiOpiskeluoikeudet != null) {
      haeYtoArvosana(hakemus.oid, hakemus.koskiOpiskeluoikeudet, valintaperusteviite.tunniste)
    } else {
      oletusarvo
    }
  }

  def onkoYtoArviointiAsteikko(hakemus: Hakemus, valintaperusteviite: Laskenta.Valintaperuste, oletusarvo: Option[Boolean]): Option[Boolean] = {
    if (hakemus.koskiOpiskeluoikeudet != null) {
      val parametrit: Array[String] = valintaperusteviite.tunniste.split(":")
      assert(parametrit.length == 2)
      val ytoKoodiArvo: String = parametrit(0)
      val ammatillinenArviointiAsteikko: String = parametrit(1)
      onkoYtoArviointiAsteikko(hakemus.oid, hakemus.koskiOpiskeluoikeudet, ytoKoodiArvo, ammatillinenArviointiAsteikko)
    } else {
      oletusarvo
    }
  }

  def haeYtoArviointiasteikko(hakemus: Hakemus, valintaperusteviite: Laskenta.Valintaperuste): Option[String] = {
    haeAmmatillisenSuorituksenTiedot(hakemus.oid, hakemus.koskiOpiskeluoikeudet, valintaperusteviite.tunniste)
      .headOption.map(_._4)
  }

  private def haeYtoArvosana(hakemusOid: String, opiskeluoikeudet: Json, ytoKoodiArvo: String): Option[BigDecimal] = {
    haeAmmatillisenSuorituksenTiedot(hakemusOid, opiskeluoikeudet, ytoKoodiArvo)
      .headOption.map(t => BigDecimal(t._3))
  }

  private def onkoYtoArviointiAsteikko(hakemusOid: String, opiskeluoikeudet: Json, ytoKoodiArvo: String, ammatillinenArviointiAsteikko: String): Option[Boolean] = {
    haeAmmatillisenSuorituksenTiedot(hakemusOid, opiskeluoikeudet, ytoKoodiArvo)
      .headOption.map(t => t._4 == ammatillinenArviointiAsteikko)
  }

  private def haeAmmatillisenSuorituksenTiedot(hakemusOid: String, opiskeluoikeudet: Json, ytoKoodiArvo: String): Seq[(String, String, Int, String)] = {
    try {
      val sulkeutumisPaivamaara: DateTime = DateTime.now()
      val suorituksenSallitutKoodit: Set[Int] = ammatillisenHhuomioitavatKoulutustyypit.map(_.koodiarvo)

      val yhteistenTutkinnonOsienArvosanat = etsiValmiitTutkinnot(opiskeluoikeudet, ammatillisenHuomioitavaOpiskeluoikeudenTyyppi, ammatillisenSuorituksenTyyppi)
        .flatMap(tutkinto => etsiValiditSuoritukset(tutkinto, sulkeutumisPaivamaara, suorituksenSallitutKoodit))
        .flatMap(suoritus => etsiYhteisetTutkinnonOsat(suoritus, sulkeutumisPaivamaara, Set(ytoKoodiArvo)))

      if (yhteistenTutkinnonOsienArvosanat.size > 1) {
        throw new UnsupportedOperationException(
          s"Hakemukselle $hakemusOid löytyi useampi kuin yksi arvosana yhteiselle tutkinnon osalle '$ytoKoodiArvo': $yhteistenTutkinnonOsienArvosanat")
      }

      if (yhteistenTutkinnonOsienArvosanat.nonEmpty) {
        LOG.debug(s"Hakemukselle $hakemusOid löytyi yhteiselle tutkinnon osalle $ytoKoodiArvo arvosana $yhteistenTutkinnonOsienArvosanat")
      }
      yhteistenTutkinnonOsienArvosanat
    } catch {
      case e: Exception => {
        LOG.error(s"Virhe haettaessa ammatillisen suorituksen tietoja hakemukselle $hakemusOid", e)
        throw e
      }
    }
  }

  private def etsiYhteisetTutkinnonOsat(suoritus: Json, sulkeutumisPäivämäärä: DateTime, osasuorituksenSallitutKoodit: Set[String]): List[(String, String, Int, String)] = {
    // Suoritusten alla olevien osasuoritusten tietoja etsivä linssi
    val _osasuoritukset = JsonPath.root.osasuoritukset.each.json

    // Osasuorituksen rakennetta purkavat linssit
    val _osasuorituksenKoulutusmoduuli = JsonPath.root.koulutusmoduuli
    val _osasuorituksenKoodiarvo = _osasuorituksenKoulutusmoduuli.tunniste.koodiarvo.string
    val _osasuorituksenNimiFi = _osasuorituksenKoulutusmoduuli.tunniste.nimi.fi.string

    _osasuoritukset.getAll(suoritus).filter(osasuoritus => {
      osasuorituksenSallitutKoodit.contains(_osasuorituksenKoodiarvo.getOption(osasuoritus).orNull)
    }).map(osasuoritus => {
      val osasuorituksenKoodiarvo = _osasuorituksenKoodiarvo.getOption(osasuoritus).orNull
      val osasuorituksenNimiFi = _osasuorituksenNimiFi.getOption(osasuoritus).orNull
      val (osasuorituksenUusinHyväksyttyArvio: String, osasuorituksenUusinArviointiAsteikko: String) = etsiUusinArvosanaJaArviointiAsteikko(osasuoritus)

      LOG.debug("Osasuorituksen arvio: %s".format(osasuorituksenUusinHyväksyttyArvio))
      LOG.debug("Osasuorituksen nimi: %s".format(osasuorituksenNimiFi))
      LOG.debug("Osasuorituksen koodiarvo: %s".format(osasuorituksenKoodiarvo))

      (osasuorituksenKoodiarvo, osasuorituksenNimiFi, osasuorituksenUusinHyväksyttyArvio.toInt, osasuorituksenUusinArviointiAsteikko)
    })
  }

  private def etsiUusinArvosanaJaArviointiAsteikko(osasuoritus: Json) = {
    val _osasuorituksenArviointi = JsonPath.root.arviointi.each.json
    val (_, osasuorituksenUusinHyväksyttyArvio, osasuorituksenUusinArviointiAsteikko): (String, String, String) =
      _osasuorituksenArviointi.getAll(osasuoritus)
        .filter(arvio => JsonPath.root.hyväksytty.boolean.getOption(arvio).getOrElse(false))
        .map(arvio => (
          JsonPath.root.päivä.string.getOption(arvio).orNull,
          JsonPath.root.arvosana.koodiarvo.string.getOption(arvio).orNull,
          JsonPath.root.arvosana.koodistoUri.string.getOption(arvio).orNull
        )).sorted(Ordering.Tuple3(Ordering.String.reverse, Ordering.String, Ordering.String))
        .head
    (osasuorituksenUusinHyväksyttyArvio, osasuorituksenUusinArviointiAsteikko)
  }

  private def etsiValiditSuoritukset(tutkinto: Json, sulkeutumisPäivämäärä: DateTime, suorituksenSallitutKoodit: Set[Int]): List[Json] = {
    val dateFormat = DateTimeFormat.forPattern("yyyy-MM-dd")

    // Suoritukset etsivä linssi
    val _suoritukset = JsonPath.root.suoritukset.each.json

    val _vahvistusPvm = JsonPath.root.vahvistus.päivä.string

    // Suorituksen rakennetta purkavat linssit
    val _koulutusmoduuli = JsonPath.root.koulutusmoduuli
    val _koodiarvo = _koulutusmoduuli.tunniste.koodiarvo.string
    val _lyhytNimiFi = _koulutusmoduuli.tunniste.nimi.fi.string
    val _koulutusTyypinKoodiarvo = _koulutusmoduuli.koulutustyyppi.koodiarvo.string
    val _koulutusTyypinNimiFi = _koulutusmoduuli.koulutustyyppi.nimi.fi.string

    _suoritukset.getAll(tutkinto).filter(suoritus => {
      val koodiarvo = _koodiarvo.getOption(suoritus).orNull
      val lyhytNimiFi = _lyhytNimiFi.getOption(suoritus).orNull
      val koulutusTyypinNimiFi = _koulutusTyypinNimiFi.getOption(suoritus).orNull

      val vahvistettuRajapäivänä = _vahvistusPvm.getOption(suoritus) match {
        case Some(dateString) => sulkeutumisPäivämäärä.isAfter(dateFormat.parseDateTime(dateString))
        case None => false
      }
      val koulutusTyypinKoodiarvo = _koulutusTyypinKoodiarvo.getOption(suoritus) match {
        case Some(s) => s.toInt
        case None => -1
      }
      val onkoSallitunTyyppinenSuoritus = suorituksenSallitutKoodit.contains(koulutusTyypinKoodiarvo)

      LOG.debug("Koodiarvo: %s".format(koodiarvo))
      LOG.debug("Onko sallitun tyyppinen suoritus: %s".format(onkoSallitunTyyppinenSuoritus))
      LOG.debug("Nimi: %s".format(lyhytNimiFi))
      LOG.debug("Koulutustyypin nimi: %s".format(koulutusTyypinNimiFi))
      LOG.debug("Koulutustyypin koodiarvo: %s".format(koulutusTyypinKoodiarvo))
      LOG.debug("On vahvistettu rajapäivänä pvm: %s".format(vahvistettuRajapäivänä))

      onkoSallitunTyyppinenSuoritus && vahvistettuRajapäivänä
    })
  }

  private def etsiValmiitTutkinnot(json: Json, opiskeluoikeudenHaluttuTyyppi: String, suorituksenHaluttuTyyppi: String): Seq[Json] = {
    // Opiskeluoikeudet etsivä linssi
    val _opiskeluoikeudet = JsonPath.root.each.json

    // Opiskeluoikeuden tyypin etsivä linssi
    val _opiskeluoikeudenTyyppi = JsonPath.root.tyyppi.koodiarvo.string
    val _valmistumisTila = JsonPath.root.tila.opiskeluoikeusjaksot.each.tila.koodiarvo.string
    val _suorituksenTyyppi = JsonPath.root.suoritukset.each.tyyppi.koodiarvo.string

    _opiskeluoikeudet.getAll(json).filter(opiskeluoikeus => {
      val opiskeluoikeudenTyyppi = _opiskeluoikeudenTyyppi.getOption(opiskeluoikeus).orNull
      val valmistumisTila = _valmistumisTila.getAll(opiskeluoikeus)
      val suorituksenTyyppi = _suorituksenTyyppi.getAll(opiskeluoikeus)

      val onkoValmistunut: Boolean = valmistumisTila.contains("valmistunut")
      val onkoAmmatillinenOpiskeluOikeus = opiskeluoikeudenTyyppi == opiskeluoikeudenHaluttuTyyppi && suorituksenTyyppi.contains(suorituksenHaluttuTyyppi)

      LOG.debug("Opiskeluoikeuden tyyppi: %s".format(opiskeluoikeudenTyyppi))
      LOG.debug("Valmistumistila: %s".format(valmistumisTila))
      LOG.debug("Onko valmistunut: %s".format(onkoValmistunut))
      LOG.debug("Suorituksen tyyppi: %s".format(suorituksenTyyppi))
      LOG.debug("Onko ammatillinen opiskeluoikeus: %s".format(onkoAmmatillinenOpiskeluOikeus))

      onkoAmmatillinenOpiskeluOikeus && onkoValmistunut
    })
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
