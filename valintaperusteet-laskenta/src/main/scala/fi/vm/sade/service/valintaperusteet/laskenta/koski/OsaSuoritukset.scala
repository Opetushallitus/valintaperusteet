package fi.vm.sade.service.valintaperusteet.laskenta.koski

import io.circe.Json
import io.circe.optics.JsonPath
import monocle.Optional
import org.joda.time.DateTime
import org.slf4j.{Logger, LoggerFactory}

object OsaSuoritukset {
  private val LOG: Logger = LoggerFactory.getLogger(KoskiLaskenta.getClass)

  // Osasuorituksen rakennetta purkavat linssit
  object OsaSuoritusLinssit {
    // Suoritusten alla olevien osasuoritusten tietoja etsivä linssi
    val osasuoritukset = JsonPath.root.osasuoritukset.each.json

    val arviointi = JsonPath.root.arviointi.each.json
    val koulutusmoduuli = JsonPath.root.koulutusmoduuli
    val koulutusmoduulinLaajuudenArvo = koulutusmoduuli.laajuus.arvo.bigDecimal
    val koulutusmoduulinTunnisteenKoodiarvo: Optional[Json, String] = koulutusmoduuli.tunniste.koodiarvo.string
    val koulutusmoduulinNimiFi = koulutusmoduuli.tunniste.nimi.fi.string
  }

  def etsiOsasuorituksetJson(suoritus: Json, sulkeutumisPäivämäärä: DateTime, osasuoritusPredikaatti: Json => Boolean): List[Json] = {
    OsaSuoritusLinssit.osasuoritukset.getAll(suoritus).filter(osasuoritusPredikaatti).map(osasuoritus => {
      val (osasuorituksenKoodiarvo: String, osasuorituksenNimiFi: String, uusinHyvaksyttyArvio: String, uusinLaajuus: Option[BigDecimal], uusinArviointiAsteikko: String) = haeOsasuorituksenPerustiedot(osasuoritus)

      LOG.debug("Osasuorituksen arvio: %s".format(uusinHyvaksyttyArvio))
      LOG.debug("Osasuorituksen arviointiasteikko: %s".format(uusinArviointiAsteikko))
      LOG.debug("Osasuorituksen laajuus: %s".format(uusinLaajuus))
      LOG.debug("Osasuorituksen nimi: %s".format(osasuorituksenNimiFi))
      LOG.debug("Osasuorituksen koodiarvo: %s".format(osasuorituksenKoodiarvo))

      osasuoritus
    })
  }

  private def haeOsasuorituksenPerustiedot(osasuoritus: Json) = {
    val osasuorituksenKoodiarvo = OsaSuoritusLinssit.koulutusmoduulinTunnisteenKoodiarvo.getOption(osasuoritus).orNull
    val osasuorituksenNimiFi = OsaSuoritusLinssit.koulutusmoduulinNimiFi.getOption(osasuoritus).orNull
    val (uusinHyvaksyttyArvio: String, uusinLaajuus: Option[BigDecimal], uusinArviointiAsteikko: String) = etsiUusinArvosanaLaajuusJaArviointiAsteikko(osasuoritus)
    (osasuorituksenKoodiarvo, osasuorituksenNimiFi, uusinHyvaksyttyArvio, uusinLaajuus, uusinArviointiAsteikko)
  }

  def etsiOsasuoritukset(suoritus: Json,
                         sulkeutumisPäivämäärä: DateTime,
                         osasuoritusPredikaatti: Json => Boolean,
                        ): List[Osasuoritus] = {
    OsaSuoritukset.etsiOsasuorituksetJson(suoritus, sulkeutumisPäivämäärä, osasuoritusPredikaatti).map(osasuoritus => {
      val (osasuorituksenKoodiarvo: String, osasuorituksenNimiFi: String, uusinHyvaksyttyArvio: String, uusinLaajuus: Option[BigDecimal], uusinArviointiAsteikko: String) = haeOsasuorituksenPerustiedot(osasuoritus)

      Osasuoritus(osasuorituksenKoodiarvo, osasuorituksenNimiFi, uusinHyvaksyttyArvio.toIntOption, uusinArviointiAsteikko, uusinLaajuus)
    })
  }

  def etsiUusinArvosanaLaajuusJaArviointiAsteikko(osasuoritus: Json): (String, Option[BigDecimal], String) = {
    val (_, uusinHyvaksyttyArvio, uusinLaajuus, uusinArviointiAsteikko): (String, String, Option[BigDecimal], String) =
      OsaSuoritusLinssit.arviointi.getAll(osasuoritus)
        .filter(arvio => JsonPath.root.hyväksytty.boolean.getOption(arvio).getOrElse(false))
        .map(arvio => (
          JsonPath.root.päivä.string.getOption(arvio).orNull,
          JsonPath.root.arvosana.koodiarvo.string.getOption(arvio).orNull,
          OsaSuoritusLinssit.koulutusmoduulinLaajuudenArvo.getOption(osasuoritus), // TODO lisää laajuuden yksikkö / estä muut kuin osp
          JsonPath.root.arvosana.koodistoUri.string.getOption(arvio).orNull
        )).sorted(Ordering.Tuple4(Ordering.String.reverse, Ordering.String, Ordering.Option[BigDecimal], Ordering.String))
        .head
    (uusinHyvaksyttyArvio, uusinLaajuus, uusinArviointiAsteikko)
  }
}
