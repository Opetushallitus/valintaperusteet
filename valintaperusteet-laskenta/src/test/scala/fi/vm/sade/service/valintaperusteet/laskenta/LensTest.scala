import io.circe.Json, io.circe.parser
import io.circe.optics.JsonPath
import scala.io.Source

object LensTest {

  def loadJson(path: String): Json = {
    val jsonFile: String = Source.fromResource(path).getLines.reduce(_ + _)
    parser.parse(jsonFile).getOrElse(Json.Null)
  }

  def main(args: Array[String]): Unit = {
    val json = loadJson("opintotiedot-json.json")
    val _foo = JsonPath.root.opiskeluoikeudet.each.oppilaitos.nimi.fi.string
    val _bar = JsonPath.root.opiskeluoikeudet.each.suoritukset.each.koulutusmoduuli.tunniste.nimi.fi.string
    val _baz = JsonPath.root.opiskeluoikeudet.each.suoritukset.each.osasuoritukset.each.koulutusmoduuli.tunniste.nimi.fi.string

    val oppilaitokset: List[String] = _foo.getAll(json)
    val koulutusmodulit: List[String] = _bar.getAll(json)
    val osasuoritukset: List[String] = _baz.getAll(json)

    oppilaitokset.foreach(s => println(s))
    koulutusmodulit.foreach(s => println(s))
    osasuoritukset.foreach(s => println(s))
  }
}