package fi.vm.sade.service.valintaperusteet.laskenta.koski

import io.circe.Json
import io.circe.optics.JsonPath
import monocle.Optional
import org.joda.time.DateTime
import org.slf4j.{Logger, LoggerFactory}

object OsaSuoritukset {
  private val LOG: Logger = LoggerFactory.getLogger(KoskiLaskenta.getClass)

  // Suoritusten alla olevien osasuoritusten tietoja etsivä linssi
  private val _osasuoritukset = JsonPath.root.osasuoritukset.each.json

  // Osasuorituksen rakennetta purkavat linssit
  private val _osasuorituksenArviointi = JsonPath.root.arviointi.each.json
  private val _osasuorituksenKoulutusmoduuli = JsonPath.root.koulutusmoduuli
  private val _osasuorituksenKoulutusmoduulinLaajuudenArvo = _osasuorituksenKoulutusmoduuli.laajuus.arvo.bigDecimal
  private val _osasuorituksenKoulutusmoduulinTunnisteenKoodiarvo: Optional[Json, String] = _osasuorituksenKoulutusmoduuli.tunniste.koodiarvo.string
  private val _osasuorituksenKoulutusmoduulinNimiFi = _osasuorituksenKoulutusmoduuli.tunniste.nimi.fi.string

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

  def etsiUusinArvosanaLaajuusJaArviointiAsteikko(osasuoritus: Json): (String, BigDecimal, String) = {
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
