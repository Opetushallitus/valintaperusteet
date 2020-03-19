package fi.vm.sade.service.valintaperusteet.laskenta.koski

import fi.vm.sade.service.valintaperusteet.laskenta.koski.Osasuoritus.OsaSuoritusLinssit
import io.circe.Json
import io.circe.optics.JsonPath
import monocle.Optional

case class Osasuoritus(koulutusmoduulinTunnisteenKoodiarvo: String,
                       koulutusmoduulinNimiFi: String,
                       uusinHyvaksyttyArvio: Option[Int],
                       uusinArviointiasteikko: String,
                       uusinLaajuus: Option[BigDecimal])

object Osasuoritus {
  val tutkinnonOsanTyypinKoodiarvo: String = "ammatillisentutkinnonosa"

  // Osasuorituksen rakennetta purkavat linssit
  object OsaSuoritusLinssit {
    // Suoritusten alla olevien osasuoritusten tietoja etsivä linssi
    val osasuoritukset = JsonPath.root.osasuoritukset.each.json

    // Osasuorituksen rakennetta purkavat linssit
    val arviointi = JsonPath.root.arviointi.each.json
    val koulutusmoduuli = JsonPath.root.koulutusmoduuli
    val koulutusmoduulinLaajuudenArvo = koulutusmoduuli.laajuus.arvo.bigDecimal
    val koulutusmoduulinTunnisteenKoodiarvo: Optional[Json, String] = koulutusmoduuli.tunniste.koodiarvo.string
    val koulutusmoduulinNimiFi = koulutusmoduuli.tunniste.nimi.fi.string
    val osasuorituksenTyypinKoodiarvo = JsonPath.root.tyyppi.koodiarvo.string
    val osasuorituksenKoulutusmoduuli = JsonPath.root.koulutusmoduuli
    val osasuorituksenKoulutusmoduulinTunnisteenKoodiarvo: Optional[Json, String] = osasuorituksenKoulutusmoduuli.tunniste.koodiarvo.string
  }

  def apply(json: Json): Osasuoritus = {
    val (osasuorituksenKoodiarvo, osasuorituksenNimiFi, uusinHyvaksyttyArvio, uusinLaajuus, uusinArviointiAsteikko) = Osasuoritus.haePerustiedot(json)
    Osasuoritus(osasuorituksenKoodiarvo, osasuorituksenNimiFi, uusinHyvaksyttyArvio.toIntOption, uusinArviointiAsteikko, uusinLaajuus)
  }

  def haePerustiedot(json: Json): (String, String, String, Option[BigDecimal], String) = {
    val osasuorituksenKoodiarvo = OsaSuoritusLinssit.koulutusmoduulinTunnisteenKoodiarvo.getOption(json).orNull
    val osasuorituksenNimiFi = OsaSuoritusLinssit.koulutusmoduulinNimiFi.getOption(json).orNull
    val (uusinHyvaksyttyArvio: String, uusinLaajuus: Option[BigDecimal], uusinArviointiAsteikko: String) =
      OsaSuoritukset.etsiUusinArvosanaLaajuusJaArviointiAsteikko(json).getOrElse(("-", None, "-"))
    (osasuorituksenKoodiarvo, osasuorituksenNimiFi, uusinHyvaksyttyArvio, uusinLaajuus, uusinArviointiAsteikko)
  }
}

object OsaSuoritukset {
  def etsiOsasuoritukset(suoritus: Json, osasuoritusPredikaatti: Json => Boolean): List[Json] = {
    OsaSuoritusLinssit.osasuoritukset.getAll(suoritus).filter(osasuoritusPredikaatti)
  }

  def etsiUusinArvosanaLaajuusJaArviointiAsteikko(osasuoritus: Json): Option[(String, Option[BigDecimal], String)] = {
      OsaSuoritusLinssit.arviointi.getAll(osasuoritus)
        .filter(arvio => JsonPath.root.hyväksytty.boolean.getOption(arvio).getOrElse(false))
        .map(arvio => (
          JsonPath.root.päivä.string.getOption(arvio).orNull,
          JsonPath.root.arvosana.koodiarvo.string.getOption(arvio).orNull,
          OsaSuoritusLinssit.koulutusmoduulinLaajuudenArvo.getOption(osasuoritus), // TODO lisää laajuuden yksikkö / estä muut kuin osp
          JsonPath.root.arvosana.koodistoUri.string.getOption(arvio).orNull
        )).sorted(Ordering.Tuple4(Ordering.String.reverse, Ordering.String, Ordering.Option[BigDecimal], Ordering.String))
        .headOption.map(t => (t._2, t._3, t._4))
  }
}
