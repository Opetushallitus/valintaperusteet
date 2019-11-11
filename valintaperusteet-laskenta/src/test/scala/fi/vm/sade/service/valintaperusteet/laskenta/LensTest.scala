import io.circe.Json, io.circe.parser
import io.circe.optics.JsonPath
import scala.io.Source

object LensTest {

  def loadJson(path: String): Json = {
    val jsonFile: String = Source.fromResource(path).getLines.reduce(_ + _)
    parser.parse(jsonFile).getOrElse(Json.Null)
  }

  def main(args: Array[String]): Unit = {
    val json = loadJson("koski-opiskeluoikeudet.json")

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

    // Osasuorituksen rakennetta purkavat linssit
    val _osasuorituksenKoulutusmoduuli = JsonPath.root.koulutusmoduuli
    val _osasuorituksenKoodiarvo = _osasuorituksenKoulutusmoduuli.tunniste.koodiarvo.string
    val _osasuorituksenNimiFi = _osasuorituksenKoulutusmoduuli.tunniste.nimi.fi.string

    _opiskeluoikeudet.getAll(json).foreach(opiskeluoikeus => {
      val opiskeluoikeudenTyyppi = _opiskeluoikeudenTyyppi.getOption(opiskeluoikeus).orNull
      val valmistumisTila = _valmistumisTila.getAll(opiskeluoikeus)
      val onkoValmistunut: Boolean = valmistumisTila.contains("valmistunut")
      println("Opiskeluoikeuden tyyppi: %s".format(opiskeluoikeudenTyyppi))
      println("Valmistumistila: %s".format(valmistumisTila))
      println("Onko valmistunut: %s".format(onkoValmistunut))

      _suoritukset.getAll(opiskeluoikeus).foreach(suoritus => {
        val koodiarvo = _koodiarvo.getOption(suoritus).orNull
        val lyhytNimiFi = _lyhytNimiFi.getOption(suoritus).orNull
        val koulutusTyypinNimiFi = _koulutusTyypinNimiFi.getOption(suoritus).orNull
        val koulutusTyypinKoodiarvo = _koulutusTyypinKoodiarvo.getOption(suoritus).orNull

        println("Koodiarvo: %s".format(koodiarvo))
        println("Nimi: %s".format(lyhytNimiFi))
        println("Koulutustyypin nimi: %s".format(koulutusTyypinNimiFi))
        println("Koulutustyypin koodiarvo: %s".format(koulutusTyypinKoodiarvo))

        println("Osasuoritukset")
        _osasuoritukset.getAll(suoritus).foreach(osasuoritus => {
          val osasuorituksenNimiFi = _osasuorituksenNimiFi.getOption(osasuoritus).orNull
          val osasuorituksenKoodiarvo = _osasuorituksenKoodiarvo.getOption(osasuoritus).orNull

          println("Osasuorituksen nimi: %s".format(osasuorituksenNimiFi))
          println("Osasuorituksen koodiarvo: %s".format(osasuorituksenKoodiarvo))
        })
      })

    })

    /*
    println("Oppilaitokset")
    val _foo = JsonPath.root.each.oppilaitos.nimi.fi.string
    val oppilaitokset: List[String] = _foo.getAll(json)

    println("Suoritukset")
    val _bar = JsonPath.root.each.suoritukset.each.koulutusmoduuli.tunniste.nimi.fi.string
    val koulutusmodulit: List[String] = _bar.getAll(json)

    println("Osasuoritukset")
    val _baz = JsonPath.root.each.suoritukset.each.osasuoritukset.each.koulutusmoduuli.tunniste.nimi.fi.string
    val osasuoritukset: List[String] = _baz.getAll(json)

    oppilaitokset.foreach(s => println(s))
    koulutusmodulit.foreach(s => println(s))
    osasuoritukset.foreach(s => println(s))
     */
  }
}
