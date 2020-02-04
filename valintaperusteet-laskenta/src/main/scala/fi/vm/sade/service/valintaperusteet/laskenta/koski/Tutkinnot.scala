package fi.vm.sade.service.valintaperusteet.laskenta.koski

import fi.vm.sade.service.valintaperusteet.laskenta.api.Hakemus
import io.circe.Json
import io.circe.optics.JsonPath
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Tutkinnot {
  private val LOG: Logger = LoggerFactory.getLogger(Tutkinnot.getClass)

  private val sallitutSuoritusTavat: Set[String] = Set("ops", "reformi")

  def etsiValmiitTutkinnot(json: Json,
                           opiskeluoikeudenHaluttuTyyppi: String,
                           suorituksenHaluttuTyyppi: String,
                           hakemus: Hakemus
                          ): Seq[Json] = {
    // Opiskeluoikeudet etsivä linssi
    val _opiskeluoikeudet = JsonPath.root.each.json

    // Opiskeluoikeuden tyypin etsivä linssi
    val _opiskeluoikeudenTyyppi = JsonPath.root.tyyppi.koodiarvo.string
    val _valmistumisTila = JsonPath.root.tila.opiskeluoikeusjaksot.each.tila.koodiarvo.string
    val _suoritukset = JsonPath.root.suoritukset.each
    val _suorituksenTyyppi = JsonPath.root.tyyppi.koodiarvo.string
    val _suoritustavanKoodiarvo = JsonPath.root.suoritustapa.koodiarvo.string
    val _suoritustavanKoodistoUri = JsonPath.root.suoritustapa.koodistoUri.string

    _opiskeluoikeudet.getAll(json).filter(opiskeluoikeus => {
      val opiskeluoikeudenTyyppi = _opiskeluoikeudenTyyppi.getOption(opiskeluoikeus).orNull
      val valmistumisTila = _valmistumisTila.getAll(opiskeluoikeus)

      _suoritukset.json.getAll(opiskeluoikeus).exists { suoritus =>
        val suorituksenTyyppi = _suorituksenTyyppi.getOption(suoritus)
        val suoritustavanKoodiarvo = _suoritustavanKoodiarvo.getOption(suoritus)
        val suoritustavanKoodistoUri = _suoritustavanKoodistoUri.getOption(suoritus)

        if (suorituksenTyyppi.size > 1) { // Suoritukselta pitäisi löytyä vain yksi tyyppi; vaikuttaa bugilta.
          throw new IllegalStateException(s"Odotettiin täsmälleen yhtä tyyppiä suoritukselle, mutta oli ${suorituksenTyyppi.size} hakemuksen ${hakemus.oid} hakijalle.")
        }

        val onkoValmistunut: Boolean = valmistumisTila.contains("valmistunut")
        val onkoValidiSuoritusTapa = suoritustavanKoodiarvo.map(s => sallitutSuoritusTavat.contains(s)).exists(v => v) &&
          suoritustavanKoodistoUri.contains("ammatillisentutkinnonsuoritustapa")
        val onkoAmmatillinenOpiskeluOikeus =
          opiskeluoikeudenTyyppi == opiskeluoikeudenHaluttuTyyppi &&
            suorituksenTyyppi.contains(suorituksenHaluttuTyyppi) &&
            onkoValidiSuoritusTapa

        LOG.debug("Opiskeluoikeuden tyyppi: %s".format(opiskeluoikeudenTyyppi))
        LOG.debug("Valmistumistila: %s".format(valmistumisTila))
        LOG.debug("Suoritustapa: %s".format(suoritustavanKoodiarvo))
        LOG.debug("Onko validi suoritustapa: %s".format(onkoValidiSuoritusTapa))
        LOG.debug("Onko valmistunut: %s".format(onkoValmistunut))
        LOG.debug("Suorituksen tyyppi: %s".format(suorituksenTyyppi))
        LOG.debug("Onko ammatillinen opiskeluoikeus: %s".format(onkoAmmatillinenOpiskeluOikeus))

        onkoAmmatillinenOpiskeluOikeus && onkoValmistunut
      }
    })
  }

  def etsiValiditSuoritukset(tutkinto: Json, sulkeutumisPäivämäärä: DateTime, suorituksenSallitutKoodit: Set[Int]): List[Json] = {
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
    val _suoritusTapa = JsonPath.root.suoritustapa.koodiarvo.string

    _suoritukset.getAll(tutkinto).filter(suoritus => {
      val koodiarvo = _koodiarvo.getOption(suoritus).orNull
      val lyhytNimiFi = _lyhytNimiFi.getOption(suoritus).orNull
      val koulutusTyypinNimiFi = _koulutusTyypinNimiFi.getOption(suoritus).orNull
      val suoritusTapa = _suoritusTapa.getOption(suoritus).orNull

      val vahvistettuRajapäivänä = _vahvistusPvm.getOption(suoritus) match {
        case Some(dateString) => sulkeutumisPäivämäärä.isAfter(dateFormat.parseDateTime(dateString))
        case None => false
      }
      val koulutusTyypinKoodiarvo = _koulutusTyypinKoodiarvo.getOption(suoritus) match {
        case Some(s) => s.toInt
        case None => -1
      }
      val onkoSallitunTyyppinenSuoritus =
        suorituksenSallitutKoodit.contains(koulutusTyypinKoodiarvo) &&
        sallitutSuoritusTavat.contains(suoritusTapa)

      LOG.debug("Koodiarvo: %s".format(koodiarvo))
      LOG.debug("Suoritustapa: %s".format(suoritusTapa))
      LOG.debug("Onko sallitun tyyppinen suoritus: %s".format(onkoSallitunTyyppinenSuoritus))
      LOG.debug("Nimi: %s".format(lyhytNimiFi))
      LOG.debug("Koulutustyypin nimi: %s".format(koulutusTyypinNimiFi))
      LOG.debug("Koulutustyypin koodiarvo: %s".format(koulutusTyypinKoodiarvo))
      LOG.debug("On vahvistettu rajapäivänä pvm: %s".format(vahvistettuRajapäivänä))

      onkoSallitunTyyppinenSuoritus && vahvistettuRajapäivänä
    })
  }
}
