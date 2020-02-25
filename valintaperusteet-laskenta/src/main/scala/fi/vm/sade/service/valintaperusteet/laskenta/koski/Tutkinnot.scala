package fi.vm.sade.service.valintaperusteet.laskenta.koski

import fi.vm.sade.service.valintaperusteet.laskenta.api.Hakemus
import io.circe.Json
import io.circe.optics.JsonPath
import io.circe.optics.JsonTraversalPath
import monocle.Optional
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

object Tutkinnot {
  private val sallitutSuoritusTavat: Set[String] = Set("ops", "reformi")

  object TutkintoLinssit {
    // Tutkinnon opiskeluoikeuden tiedot
    val opiskeluoikeudenOid: Optional[Json, String] = JsonPath.root.oid.string
    val opiskeluoikeudenVersio: Optional[Json, Int] = JsonPath.root.versionumero.int
    val opiskeluoikeudenAikaleima: Optional[Json, String] = JsonPath.root.aikaleima.string
    val opiskeluoikeudenOppilaitoksenSuomenkielinenNimi: Optional[Json, String] = JsonPath.root.oppilaitos.nimi.fi.string

    // Suoritukset etsivä linssi
    val suoritukset: JsonTraversalPath = JsonPath.root.suoritukset.each
    val vahvistusPvm: Optional[Json, String] = JsonPath.root.vahvistus.päivä.string

    val koulutusmoduuli: JsonPath = JsonPath.root.koulutusmoduuli
    val koodiarvo: Optional[Json, String] = koulutusmoduuli.tunniste.koodiarvo.string
    val lyhytNimiFi: Optional[Json, String] = koulutusmoduuli.tunniste.nimi.fi.string
    val koulutusTyypinKoodiarvo: Optional[Json, String] = koulutusmoduuli.koulutustyyppi.koodiarvo.string
    val koulutusTyypinNimiFi: Optional[Json, String] = koulutusmoduuli.koulutustyyppi.nimi.fi.string
    val suoritusTapa: Optional[Json, String] = JsonPath.root.suoritustapa.koodiarvo.string
  }

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
    val _suorituksenTyyppi = JsonPath.root.tyyppi.koodiarvo.string
    val _suoritustavanKoodiarvo = JsonPath.root.suoritustapa.koodiarvo.string
    val _suoritustavanKoodistoUri = JsonPath.root.suoritustapa.koodistoUri.string

    _opiskeluoikeudet.getAll(json).filter(opiskeluoikeus => {
      val opiskeluoikeudenTyyppi = _opiskeluoikeudenTyyppi.getOption(opiskeluoikeus).orNull
      val valmistumisTila = _valmistumisTila.getAll(opiskeluoikeus)

      TutkintoLinssit.suoritukset.json.getAll(opiskeluoikeus).exists { suoritus =>
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

        onkoAmmatillinenOpiskeluOikeus && onkoValmistunut
      }
    })
  }

  private def etsiSuoritukset(tutkinto: Json, sulkeutumisPäivämäärä: DateTime, suodatusPredikaatti: Json => Boolean): List[Json] = {
    val dateFormat = DateTimeFormat.forPattern("yyyy-MM-dd")

    TutkintoLinssit.suoritukset.json.getAll(tutkinto).filter(suoritus => {
      val vahvistettuRajapäivänä = TutkintoLinssit.vahvistusPvm.getOption(suoritus) match {
        case Some(dateString) => sulkeutumisPäivämäärä.isAfter(dateFormat.parseDateTime(dateString))
        case None => false
      }
      val onkoSallitunTyyppinenSuoritus = suodatusPredikaatti(suoritus)

      onkoSallitunTyyppinenSuoritus && vahvistettuRajapäivänä
    })
  }

  def etsiValiditSuoritukset(tutkinto: Json, sulkeutumisPäivämäärä: DateTime, suorituksenSallitutKoodit: Set[Int]): List[Json] = {
    etsiSuoritukset(tutkinto, sulkeutumisPäivämäärä, suoritus => {
      val suoritusTapa = TutkintoLinssit.suoritusTapa.getOption(suoritus).orNull
      val koulutusTyypinKoodiarvo = TutkintoLinssit.koulutusTyypinKoodiarvo.getOption(suoritus) match {
        case Some(s) => s.toInt
        case None => -1
      }
      suorituksenSallitutKoodit.contains(koulutusTyypinKoodiarvo) &&
        sallitutSuoritusTavat.contains(suoritusTapa)
    })
  }

  private def tutkinnonPerusTiedot(suoritus: Json): (Option[String], Option[String], Option[String]) = {
    val koodiarvo = TutkintoLinssit.koodiarvo.getOption(suoritus)
    val lyhytNimiFi = TutkintoLinssit.lyhytNimiFi.getOption(suoritus)
    val koulutusTyypinNimiFi = TutkintoLinssit.koulutusTyypinNimiFi.getOption(suoritus)
    (koodiarvo, lyhytNimiFi, koulutusTyypinNimiFi)
  }
}
