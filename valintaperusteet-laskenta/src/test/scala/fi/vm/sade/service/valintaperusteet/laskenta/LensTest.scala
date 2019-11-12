import io.circe.Json
import io.circe.parser
import io.circe.optics.JsonPath
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

import scala.io.Source

object LensTest {

  def loadJson(path: String): Json = {
    val jsonFile: String = Source.fromResource(path).getLines.reduce(_ + _)
    parser.parse(jsonFile).getOrElse(Json.Null)
  }

  def main(args: Array[String]): Unit = {
    val json = loadJson("koski-opiskeluoikeudet.json")
    val sulkeutumisPäivämäärä = DateTime.now()
    val suorituksenSallitutKoodit = Set(1, 4, 26)
    val osasuorituksenSallitutKoodit = Set("101054")


    run(json, sulkeutumisPäivämäärä, "ammatillinenkoulutus", suorituksenSallitutKoodit, osasuorituksenSallitutKoodit)
  }

  private def run(json: Json, sulkeutumisPäivämäärä: DateTime, opiskeluoikeudenHaluttuTyyppi: String, suorituksenSallitutKoodit: Set[Int], osasuorituksenSallitutKoodit: Set[String]): Unit = {
    val yhteistenTutkinnonOsienArvosanat = etsiValmiitTutkinnot(json, opiskeluoikeudenHaluttuTyyppi)
      .flatMap(tutkinto => etsiValiditSuoritukset(tutkinto, sulkeutumisPäivämäärä, suorituksenSallitutKoodit))
      .map(suoritus => etsiYhteisetTutkinnonOsat(suoritus, sulkeutumisPäivämäärä, osasuorituksenSallitutKoodit))

    yhteistenTutkinnonOsienArvosanat.foreach(arvosana => println("Arvosana: %s".format(arvosana)))
  }

  private def etsiYhteisetTutkinnonOsat(suoritus: Json, sulkeutumisPäivämäärä: DateTime, osasuorituksenSallitutKoodit: Set[String]): List[(String, String, Int)] = {
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
      val osasuorituksenUusinHyväksyttyArvio: String = etsiUusinArvosana(osasuoritus)

      println("Osasuorituksen arvio: %s".format(osasuorituksenUusinHyväksyttyArvio))
      println("Osasuorituksen nimi: %s".format(osasuorituksenNimiFi))
      println("Osasuorituksen koodiarvo: %s".format(osasuorituksenKoodiarvo))

      (osasuorituksenKoodiarvo, osasuorituksenNimiFi, osasuorituksenUusinHyväksyttyArvio.toInt)
    })
  }

  private def etsiUusinArvosana(osasuoritus: Json) = {
    val _osasuorituksenArviointi = JsonPath.root.arviointi.each.json
    val (_, osasuorituksenUusinHyväksyttyArvio): (String, String) = _osasuorituksenArviointi.getAll(osasuoritus)
      .filter(arvio => JsonPath.root.hyväksytty.boolean.getOption(arvio).getOrElse(false))
      .map(arvio => (
        JsonPath.root.päivä.string.getOption(arvio).orNull,
        JsonPath.root.arvosana.koodiarvo.string.getOption(arvio).orNull
      )).sorted(Ordering.Tuple2(Ordering.String.reverse, Ordering.String))
      .head
    osasuorituksenUusinHyväksyttyArvio
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

      println("Koodiarvo: %s".format(koodiarvo))
      println("Onko sallitun tyyppinen suoritus: %s".format(onkoSallitunTyyppinenSuoritus))
      println("Nimi: %s".format(lyhytNimiFi))
      println("Koulutustyypin nimi: %s".format(koulutusTyypinNimiFi))
      println("Koulutustyypin koodiarvo: %s".format(koulutusTyypinKoodiarvo))
      println("On vahvistettu rajapäivänä pvm: %s".format(vahvistettuRajapäivänä))

      onkoSallitunTyyppinenSuoritus && vahvistettuRajapäivänä
    })
  }

  private def etsiValmiitTutkinnot(json: Json, opiskeluoikeudenHaluttuTyyppi: String): Seq[Json] = {
    // Opiskeluoikeudet etsivä linssi
    val _opiskeluoikeudet = JsonPath.root.each.json

    // Opiskeluoikeuden tyypin etsivä linssi
    val _opiskeluoikeudenTyyppi = JsonPath.root.tyyppi.koodiarvo.string
    val _valmistumisTila = JsonPath.root.tila.opiskeluoikeusjaksot.each.tila.koodiarvo.string

    _opiskeluoikeudet.getAll(json).filter(opiskeluoikeus => {
      val opiskeluoikeudenTyyppi = _opiskeluoikeudenTyyppi.getOption(opiskeluoikeus).orNull
      val valmistumisTila = _valmistumisTila.getAll(opiskeluoikeus)

      val onkoValmistunut: Boolean = valmistumisTila.contains("valmistunut")
      val onkoAmmatillinenOpiskeluOikeus = opiskeluoikeudenTyyppi == opiskeluoikeudenHaluttuTyyppi

      println("Opiskeluoikeuden tyyppi: %s".format(opiskeluoikeudenTyyppi))
      println("Valmistumistila: %s".format(valmistumisTila))
      println("Onko valmistunut: %s".format(onkoValmistunut))
      println("Onko ammatillinen opiskeluoikeus: %s".format(onkoAmmatillinenOpiskeluOikeus))

      onkoAmmatillinenOpiskeluOikeus && onkoValmistunut
    })
  }
}
