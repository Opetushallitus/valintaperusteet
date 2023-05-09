package fi.vm.sade.service.valintaperusteet.laskenta.koski

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import fi.vm.sade.kaava.LaskentaUtil
import fi.vm.sade.service.valintaperusteet.laskenta.api.Hakemus
import fi.vm.sade.service.valintaperusteet.laskenta.koski.Osasuoritus.OsaSuoritusLinssit
import io.circe.Json
import io.circe.optics.JsonPath
import io.circe.optics.JsonTraversalPath
import monocle.{Optional, Traversal}
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import scala.Option.option2Iterable
import scala.util.control.Exception.catching

object Tutkinnot {
  private val LOG: Logger = LoggerFactory.getLogger(Tutkinnot.getClass)

  private val sallitutSuoritusTavat: Set[String] = Set("ops", "reformi")
  val koskiDateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

  object TutkintoLinssit {
    // Tutkinnon opiskeluoikeuden tiedot
    val opiskeluoikeudet: Traversal[Json, Json] = JsonPath.root.each.json
    val opiskeluoikeudenOid: Optional[Json, String] = JsonPath.root.oid.string
    val opiskeluoikeudenVersio: Optional[Json, Int] = JsonPath.root.versionumero.int
    val opiskeluoikeudenAikaleima: Optional[Json, String] = JsonPath.root.aikaleima.string
    val opiskeluoikeudenOppilaitoksenSuomenkielinenNimi: Optional[Json, String] =
      JsonPath.root.oppilaitos.nimi.fi.string

    // Suoritukset etsivä linssi
    val suoritukset: JsonTraversalPath = JsonPath.root.suoritukset.each
    val vahvistusPvm: Optional[Json, String] = JsonPath.root.vahvistus.päivä.string

    val koulutusmoduuli: JsonPath = JsonPath.root.koulutusmoduuli
    val koodiarvo: Optional[Json, String] = koulutusmoduuli.tunniste.koodiarvo.string
    val lyhytNimiFi: Optional[Json, String] = koulutusmoduuli.tunniste.nimi.fi.string
    val koulutusTyypinKoodiarvo: Optional[Json, String] =
      koulutusmoduuli.koulutustyyppi.koodiarvo.string
    val koulutusTyypinNimiFi: Optional[Json, String] = koulutusmoduuli.koulutustyyppi.nimi.fi.string
    val suoritusTapa: Optional[Json, String] = JsonPath.root.suoritustapa.koodiarvo.string

    // Kenttiin katsovia linssejä
    val opiskeluoikeusOid: Optional[Json, String] = JsonPath.root.oid.string
    val opiskeluoikeudenTyyppi: Optional[Json, String] = JsonPath.root.tyyppi.koodiarvo.string
    val valmistumisTila: Traversal[Json, String] =
      JsonPath.root.tila.opiskeluoikeusjaksot.each.tila.koodiarvo.string
    val korotettuOpiskeluoikeusOid: Optional[Json, String] =
      JsonPath.root.korotettuOpiskeluoikeusOid.string
    val suorituksenTyyppi: Optional[Json, String] = JsonPath.root.tyyppi.koodiarvo.string
    val suoritustavanKoodiarvo: Optional[Json, String] = JsonPath.root.suoritustapa.koodiarvo.string
    val suoritustavanKoodistoUri: Optional[Json, String] = {
      JsonPath.root.suoritustapa.koodistoUri.string
    }
    val korotettuKeskiarvo: Optional[Json, BigDecimal] = JsonPath.root.korotettuKeskiarvo.bigDecimal
    val keskiarvo: Optional[Json, BigDecimal] = JsonPath.root.keskiarvo.bigDecimal
  }

  private def suoritusTäsmää(
    suorituksenHaluttuTyyppi: String,
    valmistumisTila: List[String],
    valmistumisenTakaraja: Option[LocalDate],
    opiskeluoikeudenTyyppi: String,
    opiskeluoikeudenHaluttuTyyppi: String,
    hakemus: Hakemus,
    suoritus: Json
  ): Boolean = {
    val suorituksenTyyppi = TutkintoLinssit.suorituksenTyyppi.getOption(suoritus)
    val suoritustavanKoodiarvo = TutkintoLinssit.suoritustavanKoodiarvo.getOption(suoritus)
    val suoritustavanKoodistoUri = TutkintoLinssit.suoritustavanKoodistoUri.getOption(suoritus)

    val onkoValmistunut: Boolean =
      valmistumisTila.contains("valmistunut") && (valmistumisenTakaraja
        .forall(vahvistettuRajapäiväänMennessä(_, suoritus, hakemus)))
    val onkoValidiSuoritusTapa =
      suoritustavanKoodiarvo.map(s => sallitutSuoritusTavat.contains(s)).exists(v => v) &&
        suoritustavanKoodistoUri.contains("ammatillisentutkinnonsuoritustapa")
    val onkoAmmatillinenOpiskeluOikeus =
      opiskeluoikeudenTyyppi == opiskeluoikeudenHaluttuTyyppi &&
        suorituksenTyyppi.contains(suorituksenHaluttuTyyppi) &&
        onkoValidiSuoritusTapa

    onkoAmmatillinenOpiskeluOikeus && onkoValmistunut
  }

  private def etsiTähänOpiskeluoikeuteenLiittyvätKorotussuoritukset(
    json: Json,
    opiskeluoikeusOid: Option[String],
    korotuksetSisältäväSuorituksenTyyppi: String,
    valmistumisenTakaraja: Option[LocalDate],
    opiskeluoikeudenHaluttuTyyppi: String,
    hakemus: Hakemus
  ): List[Json] =
    TutkintoLinssit.opiskeluoikeudet
      .getAll(json)
      .filter(korotusOpiskeluoikeus => {
        val opiskeluoikeudenTyyppi =
          TutkintoLinssit.opiskeluoikeudenTyyppi.getOption(korotusOpiskeluoikeus).orNull
        val valmistumisTila =
          TutkintoLinssit.valmistumisTila.getAll(korotusOpiskeluoikeus)
        TutkintoLinssit.suoritukset.json
          .getAll(korotusOpiskeluoikeus)
          .filter(
            TutkintoLinssit.suorituksenTyyppi
              .getOption(_)
              .contains(korotuksetSisältäväSuorituksenTyyppi)
          )
          .exists { suoritus =>
            val opiskeluoikeusOiditTäsmää =
              (
                opiskeluoikeusOid,
                TutkintoLinssit.korotettuOpiskeluoikeusOid.getOption(suoritus)
              ) match {
                case (Some(oid), Some(korotusOid)) => oid == korotusOid
                case (_, _)                        => false
              }

            suoritusTäsmää(
              korotuksetSisältäväSuorituksenTyyppi,
              valmistumisTila,
              valmistumisenTakaraja,
              opiskeluoikeudenTyyppi,
              opiskeluoikeudenHaluttuTyyppi,
              hakemus,
              suoritus
            ) && opiskeluoikeusOiditTäsmää
          }
      })
      .flatMap(TutkintoLinssit.suoritukset.json.getAll)
      .sortBy(JsonPath.root.vahvistus.päivä.string.getOption(_))(Ordering.Option[String].reverse)

  private def korotusPredikaatti(koulutusmoduulinTunniste: Option[String])(
    korotusOsasuoritus: Json
  ): Boolean = {
    (
      koulutusmoduulinTunniste,
      OsaSuoritusLinssit.koulutusmoduulinTunnisteenKoodiarvo.getOption(korotusOsasuoritus)
    ) match {
      case (Some(komo), Some(korotusKomo)) if korotusKomo == komo => true
      case (_, _)                                                 => false
    }
  }

  private def numeerinenArvosana(arvosana: String): Option[BigDecimal] = {
    catching(classOf[NumberFormatException]) opt BigDecimal(arvosana)
  }

  private def käytäParastaArviota(
    korotuksenArviot: List[Json]
  )(alkuperäisetArviot: Vector[Json]): Vector[Json] = {
    val uusinKorotus = OsaSuoritukset.etsiUusinArvio(korotuksenArviot)
    (
      OsaSuoritukset.etsiUusinArvio(alkuperäisetArviot.toList).flatMap(OsaSuoritukset.arvio),
      uusinKorotus.flatMap(OsaSuoritukset.arvio)
    ) match {
      // uusin alkuperäinen arvio hylätty, korotus hyväksytty -> käytä korotusta (sallii asteikon vaihtamisen)
      case (Some((OsaSuoritukset.hylätty, _)), Some((OsaSuoritukset.hyväksytty, _))) =>
        Vector(uusinKorotus.get)
      // korotus hylätty -> käytä alkuperäisiä arvioita (sallii asteikon vaihtamisen)
      case (_, Some((OsaSuoritukset.hylätty, _))) => alkuperäisetArviot
      // vertaile numeerisia arvosanoja vain samoilla asteikoilla
      case (Some((alkuperäinen, asteikko)), Some((korotus, korotuksenAsteikko)))
          if korotuksenAsteikko == asteikko &&
            numeerinenArvosana(korotus) > numeerinenArvosana(alkuperäinen) =>
        Vector(uusinKorotus.get)
      // oletuksena käytetään alkuperäisiä arvioita
      case (_, _) => alkuperäisetArviot
    }
  }

  private def korvaaArviot(korotus: Option[Json], osasuoritus: Json): Json =
    OsaSuoritusLinssit.arviointiKokoelma.modify(
      käytäParastaArviota(
        korotus
          .map(s => OsaSuoritusLinssit.arviointi.getAll(s))
          .getOrElse(List.empty)
      )(_)
    )(osasuoritus)

  private def muokkaaOsasuoritustenOsasuoritukset(
    osasuorituksenKorotus: Option[Json],
    osasuoritus: Json
  ): Json =
    OsaSuoritusLinssit.osasuoritukset.modify(osasuorituksenOsasuoritus => {
      val osasuorituksenOsasuorituksenKorotus = osasuorituksenKorotus
        .flatMap(osasuoritus =>
          OsaSuoritukset
            .etsiOsasuoritukset(
              osasuoritus,
              korotusPredikaatti(
                OsaSuoritusLinssit.koulutusmoduulinTunnisteenKoodiarvo
                  .getOption(osasuorituksenOsasuoritus)
              )
            )
            .headOption
        )
      korvaaArviot(
        osasuorituksenOsasuorituksenKorotus,
        osasuorituksenOsasuoritus
      )
    })(osasuoritus)

  private def muokkaaOsasuoritukset(suoritustenKorotukset: List[Json], opiskeluoikeus: Json): Json =
    TutkintoLinssit.suoritukset.osasuoritukset.each.json.modify(osasuoritus => {
      // käsitellään korotukset suoritusjärjestyksessä
      suoritustenKorotukset.reverse
        .flatMap(suoritus => {
          OsaSuoritukset.etsiOsasuoritukset(
            suoritus,
            korotusPredikaatti(
              OsaSuoritusLinssit.koulutusmoduulinTunnisteenKoodiarvo.getOption(osasuoritus)
            )
          )
        })
        .fold(osasuoritus)((iteroitavaOsasuoritus: Json, korotettuOsasuoritus: Json) =>
          muokkaaOsasuoritustenOsasuoritukset(
            Some(korotettuOsasuoritus),
            korvaaArviot(
              Some(korotettuOsasuoritus),
              iteroitavaOsasuoritus
            )
          )
        )
    })(opiskeluoikeus)

  private def muokkaaSuoritus(suoritustenKorotukset: List[Json], opiskeluoikeus: Json): Json =
    TutkintoLinssit.suoritukset.json.modify(suoritus => {
      // Korvataan alkuperäisen opiskeluoikeuden suorituksen keskiarvo
      // viimeisimpänä vahvistetun korotussuorituksen korotetulla keskiarvolla
      TutkintoLinssit.keskiarvo.modify(keskiarvo =>
        suoritustenKorotukset.headOption
          .flatMap(TutkintoLinssit.korotettuKeskiarvo.getOption)
          .getOrElse(keskiarvo)
      )(suoritus)
    })(opiskeluoikeus)

  private def yhdistäKorotuksetOpiskeluoikeuteen(
    json: Json,
    korotuksetSisältäväSuorituksenTyyppi: String,
    valmistumisenTakaraja: Option[LocalDate],
    opiskeluoikeudenHaluttuTyyppi: String,
    hakemus: Hakemus,
    opiskeluoikeus: Json
  ): Json = {
    val opiskeluoikeusOid = TutkintoLinssit.opiskeluoikeusOid.getOption(opiskeluoikeus)
    val suoritustenKorotukset = etsiTähänOpiskeluoikeuteenLiittyvätKorotussuoritukset(
      json,
      opiskeluoikeusOid,
      korotuksetSisältäväSuorituksenTyyppi,
      valmistumisenTakaraja,
      opiskeluoikeudenHaluttuTyyppi,
      hakemus
    )
    muokkaaOsasuoritukset(
      suoritustenKorotukset,
      muokkaaSuoritus(
        suoritustenKorotukset,
        opiskeluoikeus
      )
    )
  }

  def etsiValmiitTutkinnot(
    valmistumisenTakaraja: Option[LocalDate],
    json: Json,
    opiskeluoikeudenHaluttuTyyppi: String,
    suorituksenHaluttuTyyppi: String,
    korotuksetSisältäväSuorituksenTyyppi: String,
    hakemus: Hakemus
  ): Seq[Json] = {
    TutkintoLinssit.opiskeluoikeudet
      .getAll(json)
      .filter(opiskeluoikeus => {
        TutkintoLinssit.suoritukset.json
          .getAll(opiskeluoikeus)
          .filter(
            TutkintoLinssit.suorituksenTyyppi.getOption(_).contains(suorituksenHaluttuTyyppi)
          )
          .exists { suoritus =>
            suoritusTäsmää(
              suorituksenHaluttuTyyppi,
              TutkintoLinssit.valmistumisTila.getAll(opiskeluoikeus),
              valmistumisenTakaraja,
              TutkintoLinssit.opiskeluoikeudenTyyppi.getOption(opiskeluoikeus).orNull,
              opiskeluoikeudenHaluttuTyyppi,
              hakemus,
              suoritus
            )
          }
      })
      .map(opiskeluoikeus =>
        yhdistäKorotuksetOpiskeluoikeuteen(
          json,
          korotuksetSisältäväSuorituksenTyyppi,
          valmistumisenTakaraja,
          opiskeluoikeudenHaluttuTyyppi,
          hakemus,
          opiskeluoikeus
        )
      )
  }

  private def etsiSuoritukset(
    tutkinto: Json,
    valmistumisenTakarajaPvm: LocalDate,
    suodatusPredikaatti: Json => Boolean,
    hakemus: Hakemus
  ): List[Json] = {
    TutkintoLinssit.suoritukset.json
      .getAll(tutkinto)
      .filter(suoritus => {
        val onkoSallitunTyyppinenSuoritus = suodatusPredikaatti(suoritus)

        onkoSallitunTyyppinenSuoritus && vahvistettuRajapäiväänMennessä(
          valmistumisenTakarajaPvm,
          suoritus,
          hakemus
        )
      })
  }

  def vahvistettuRajapäiväänMennessä(
    valmistumisenTakaraja: LocalDate,
    päätasonSuoritus: Json,
    hakemus: Hakemus
  ): Boolean = {
    TutkintoLinssit.vahvistusPvm.getOption(päätasonSuoritus) match {
      case Some(dateString) =>
        val valmistunutAjoissa =
          !LocalDate.parse(dateString, koskiDateFormat).isAfter(valmistumisenTakaraja)
        if (!valmistunutAjoissa) {
          LOG.info(
            s"Suorituksen vahvistuspäivä $dateString on valmistumisen takarajan " +
              s"${LaskentaUtil.suomalainenPvmMuoto.format(valmistumisenTakaraja)} jälkeen, joten suoritusta ei huomioida. Hakemus: ${hakemus.oid}"
          )
        }
        valmistunutAjoissa
      case None => false
    }
  }

  def etsiValiditSuoritukset(
    tutkinto: Json,
    valmistumisenTakarajaPvm: LocalDate,
    suorituksenSallitutKoodit: Set[Int],
    hakemus: Hakemus
  ): List[Json] = {
    etsiSuoritukset(
      tutkinto,
      valmistumisenTakarajaPvm,
      suoritus => {
        val suoritusTapa = TutkintoLinssit.suoritusTapa.getOption(suoritus).orNull
        val koulutusTyypinKoodiarvo =
          TutkintoLinssit.koulutusTyypinKoodiarvo.getOption(suoritus) match {
            case Some(s) => s.toInt
            case None    => -1
          }
        suorituksenSallitutKoodit.contains(koulutusTyypinKoodiarvo) &&
        sallitutSuoritusTavat.contains(suoritusTapa)
      },
      hakemus
    )
  }
}
