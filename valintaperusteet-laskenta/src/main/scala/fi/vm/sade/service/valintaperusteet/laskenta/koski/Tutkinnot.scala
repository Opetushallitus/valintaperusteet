package fi.vm.sade.service.valintaperusteet.laskenta.koski

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import fi.vm.sade.kaava.LaskentaUtil
import fi.vm.sade.service.valintaperusteet.laskenta.api.Hakemus
import io.circe.Json
import io.circe.optics.JsonPath
import io.circe.optics.JsonTraversalPath
import monocle.Optional
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Tutkinnot {
  private val LOG: Logger = LoggerFactory.getLogger(Tutkinnot.getClass)

  private val sallitutSuoritusTavat: Set[String] = Set("ops", "reformi")
  val koskiDateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

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

  def etsiValmiitTutkinnot(valmistumisenTakaraja: Option[LocalDate],
                           json: Json,
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

        val onkoValmistunut: Boolean = valmistumisTila.contains("valmistunut") && (valmistumisenTakaraja.forall(vahvistettuRajapäiväänMennessä(_, suoritus)))
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

  private def etsiSuoritukset(tutkinto: Json, valmistumisenTakarajaPvm: LocalDate, suodatusPredikaatti: Json => Boolean): List[Json] = {
    TutkintoLinssit.suoritukset.json.getAll(tutkinto).filter(suoritus => {
      val onkoSallitunTyyppinenSuoritus = suodatusPredikaatti(suoritus)

      onkoSallitunTyyppinenSuoritus && vahvistettuRajapäiväänMennessä(valmistumisenTakarajaPvm, suoritus)
    })
  }

  def vahvistettuRajapäiväänMennessä(valmistumisenTakaraja: LocalDate, päätasonSuoritus: Json): Boolean = {
    TutkintoLinssit.vahvistusPvm.getOption(päätasonSuoritus) match {
      case Some(dateString) =>
        val valmistunutAjoissa = !LocalDate.parse(dateString, koskiDateFormat).isAfter(valmistumisenTakaraja)
        if (!valmistunutAjoissa) {
          LOG.info(s"Suorituksen valmistumispäivämäärä $dateString on valmistumisen takarajan " +
            s"${LaskentaUtil.suomalainenPvmMuoto.format(valmistumisenTakaraja)} jälkeen, joten suoritusta ei huomioida.") // TODO tähän voisi kaivaa jotain lisätietoa suorituksesta
        }
        valmistunutAjoissa
      case None => false
    }
  }

  def etsiValiditSuoritukset(tutkinto: Json, valmistumisenTakarajaPvm: LocalDate, suorituksenSallitutKoodit: Set[Int]): List[Json] = {
    etsiSuoritukset(tutkinto, valmistumisenTakarajaPvm, suoritus => {
      val suoritusTapa = TutkintoLinssit.suoritusTapa.getOption(suoritus).orNull
      val koulutusTyypinKoodiarvo = TutkintoLinssit.koulutusTyypinKoodiarvo.getOption(suoritus) match {
        case Some(s) => s.toInt
        case None => -1
      }
      suorituksenSallitutKoodit.contains(koulutusTyypinKoodiarvo) &&
        sallitutSuoritusTavat.contains(suoritusTapa)
    })
  }
}
