package fi.vm.sade.service.valintaperusteet.laskenta.koski

import fi.vm.sade.service.valintaperusteet.laskenta.koski.Osasuoritus.OsaSuoritusLinssit
import io.circe.Json
import io.circe.optics.JsonPath
import monocle.{Optional, Traversal}

case class Osasuoritus(
  koulutusmoduulinTunnisteenKoodiarvo: String,
  koulutusmoduulinNimiFi: String,
  uusinHyvaksyttyArvio: Option[Int],
  uusinArviointiasteikko: String,
  uusinLaajuus: Option[BigDecimal]
)

object Osasuoritus {
  val tutkinnonOsanTyypinKoodiarvo: String = "ammatillisentutkinnonosa"
  val ytojenKoulutusmoduulienTunnisteenKoodistoUri: String = "tutkinnonosat"

  // Osasuorituksen rakennetta purkavat linssit
  object OsaSuoritusLinssit {
    // Suoritusten alla olevien osasuoritusten tietoja etsivä linssi
    val osasuoritukset: Traversal[Json, Json] = JsonPath.root.osasuoritukset.each.json

    // Osasuorituksen rakennetta purkavat linssit
    val arviointi: Traversal[Json, Json] = JsonPath.root.arviointi.each.json
    val arviointiKokoelma: Optional[Json, Vector[Json]] = JsonPath.root.arviointi.arr
    val koulutusmoduuli: JsonPath = JsonPath.root.koulutusmoduuli
    val koulutusmoduulinLaajuudenArvo: Optional[Json, BigDecimal] =
      koulutusmoduuli.laajuus.bigDecimal
    val koulutusmoduulinTunnisteenKoodiarvo: Optional[Json, String] =
      koulutusmoduuli.tunniste.koodiarvo.string
    val koulutusmoduulinTunnisteenKoodistoUri: Optional[Json, String] =
      koulutusmoduuli.tunniste.koodistoUri.string
    val koulutusmoduulinNimiFi: Optional[Json, String] = koulutusmoduuli.tunniste.nimi.fi.string
    val osasuorituksenTyypinKoodiarvo: Optional[Json, String] =
      JsonPath.root.tyyppi.koodiarvo.string
  }

  def apply(json: Json): Osasuoritus = {
    val (
      osasuorituksenKoodiarvo,
      osasuorituksenNimiFi,
      uusinHyvaksyttyArvio,
      uusinLaajuus,
      uusinArviointiAsteikko
    ) = Osasuoritus.haePerustiedot(json)
    Osasuoritus(
      osasuorituksenKoodiarvo,
      osasuorituksenNimiFi,
      uusinHyvaksyttyArvio.toIntOption,
      uusinArviointiAsteikko,
      uusinLaajuus
    )
  }

  def haePerustiedot(json: Json): (String, String, String, Option[BigDecimal], String) = {
    val osasuorituksenKoodiarvo =
      OsaSuoritusLinssit.koulutusmoduulinTunnisteenKoodiarvo.getOption(json).orNull
    val osasuorituksenNimiFi = OsaSuoritusLinssit.koulutusmoduulinNimiFi.getOption(json).orNull
    val (
      uusinHyvaksyttyArvio: String,
      uusinLaajuus: Option[BigDecimal],
      uusinArviointiAsteikko: String
    ) =
      OsaSuoritukset.etsiUusinArvosanaLaajuusJaArviointiAsteikko(json).getOrElse(("-", None, "-"))
    (
      osasuorituksenKoodiarvo,
      osasuorituksenNimiFi,
      uusinHyvaksyttyArvio,
      uusinLaajuus,
      uusinArviointiAsteikko
    )
  }
}

object OsaSuoritukset {
  def etsiOsasuoritukset(suoritus: Json, osasuoritusPredikaatti: Json => Boolean): List[Json] = {
    OsaSuoritusLinssit.osasuoritukset.getAll(suoritus).filter(osasuoritusPredikaatti)
  }

  // Arvosanojen spessuarvot, esiintyy ainakin kolmella ammatillisen arvioinnin asteikolla
  val hylätty = "Hylätty"
  val hyväksytty = "Hyväksytty"

  def arvio(arvio: Json): Option[(String, String)] = {
    (
      JsonPath.root.arvosana.koodiarvo.string.getOption(arvio),
      JsonPath.root.arvosana.koodistoUri.string.getOption(arvio)
    ) match {
      case (Some(arvosana), Some(asteikko)) => Some((arvosana, asteikko))
      case (_, _)                           => None
    }
  }

  def etsiUusinArvio(arviot: List[Json]): Option[Json] = {
    arviot
      .filter(arvio => JsonPath.root.hyväksytty.boolean.getOption(arvio).getOrElse(false))
      .sortBy(JsonPath.root.päivä.string.getOption(_))(Ordering.Option[String].reverse)
      .headOption
  }

  private def etsiUusinArvio(osasuoritus: Json): Option[Json] = {
    etsiUusinArvio(OsaSuoritusLinssit.arviointi.getAll(osasuoritus))
  }

  def etsiUusinArvosanaLaajuusJaArviointiAsteikko(
    osasuoritus: Json
  ): Option[(String, Option[BigDecimal], String)] = {
    etsiUusinArvio(osasuoritus)
      .map(arvio =>
        (
          JsonPath.root.arvosana.koodiarvo.string.getOption(arvio).orNull,
          OsaSuoritusLinssit.koulutusmoduulinLaajuudenArvo.getOption(
            osasuoritus
          ), // TODO lisää laajuuden yksikkö / estä muut kuin osp
          JsonPath.root.arvosana.koodistoUri.string.getOption(arvio).orNull
        )
      )
  }
}
