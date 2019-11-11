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
    val cutoffDate = DateTime.now()
    val suorituksenSallitutKoodit = Set(1, 4, 26)

    onkoHaluttujaTutkintoja(json, cutoffDate, "ammatillinenkoulutus", suorituksenSallitutKoodit)
  }

  private def onkoHaluttujaTutkintoja(json: Json, cutoffDate: DateTime, opiskeluoikeudenHaluttuTyyppi: String, suorituksenSallitutKoodit: Set[Int]): Unit = {
    val dateFormat = DateTimeFormat.forPattern("yyyy-MM-dd")

    // Opiskeluoikeudet etsivä linssi
    val _opiskeluoikeudet = JsonPath.root.each.json

    // Suoritukset etsivä linssi
    val _suoritukset = JsonPath.root.suoritukset.each.json

    // Suoritusten alla olevien osasuoritusten tietoja etsivä linssi
    val _osasuoritukset = JsonPath.root.osasuoritukset.each.json

    // Opiskeluoikeuden tyypin etsivä linssi
    val _opiskeluoikeudenTyyppi = JsonPath.root.tyyppi.koodiarvo.string
    val _valmistumisTila = JsonPath.root.tila.opiskeluoikeusjaksot.each.tila.koodiarvo.string

    // Suorituksen rakennetta purkavat linssit
    val _koulutusmoduuli = JsonPath.root.koulutusmoduuli
    val _koodiarvo = _koulutusmoduuli.tunniste.koodiarvo.string
    val _lyhytNimiFi = _koulutusmoduuli.tunniste.nimi.fi.string
    val _koulutusTyypinKoodiarvo = _koulutusmoduuli.koulutustyyppi.koodiarvo.string
    val _koulutusTyypinNimiFi = _koulutusmoduuli.koulutustyyppi.nimi.fi.string

    val _vahvistusPvm = JsonPath.root.vahvistus.päivä.string

    // Osasuorituksen rakennetta purkavat linssit
    val _osasuorituksenKoulutusmoduuli = JsonPath.root.koulutusmoduuli
    val _osasuorituksenKoodiarvo = _osasuorituksenKoulutusmoduuli.tunniste.koodiarvo.string
    val _osasuorituksenNimiFi = _osasuorituksenKoulutusmoduuli.tunniste.nimi.fi.string

    _opiskeluoikeudet.getAll(json).foreach(opiskeluoikeus => {
      val opiskeluoikeudenTyyppi = _opiskeluoikeudenTyyppi.getOption(opiskeluoikeus).orNull
      val valmistumisTila = _valmistumisTila.getAll(opiskeluoikeus)

      val onkoValmistunut: Boolean = valmistumisTila.contains("valmistunut")
      val onkoAmmatillinenOpiskeluOikeus = opiskeluoikeudenTyyppi == opiskeluoikeudenHaluttuTyyppi

      println("Opiskeluoikeuden tyyppi: %s".format(opiskeluoikeudenTyyppi))
      println("Valmistumistila: %s".format(valmistumisTila))
      println("Onko valmistunut: %s".format(onkoValmistunut))
      println("Onko ammatillinen opiskeluoikeus: %s".format(onkoAmmatillinenOpiskeluOikeus))

      _suoritukset.getAll(opiskeluoikeus).foreach(suoritus => {
        val koodiarvo = _koodiarvo.getOption(suoritus).orNull
        val lyhytNimiFi = _lyhytNimiFi.getOption(suoritus).orNull
        val koulutusTyypinNimiFi = _koulutusTyypinNimiFi.getOption(suoritus).orNull
        val vahvistettuRajapäivänä = _vahvistusPvm.getOption(suoritus) match {
          case Some(dateString) => cutoffDate.isAfter(dateFormat.parseDateTime(dateString))
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

        println("Osasuoritukset")
        _osasuoritukset.getAll(suoritus).foreach(osasuoritus => {
          val osasuorituksenNimiFi = _osasuorituksenNimiFi.getOption(osasuoritus).orNull
          val osasuorituksenKoodiarvo = _osasuorituksenKoodiarvo.getOption(osasuoritus).orNull

          println("Osasuorituksen nimi: %s".format(osasuorituksenNimiFi))
          println("Osasuorituksen koodiarvo: %s".format(osasuorituksenKoodiarvo))
        })
      })

    })
  }
}
