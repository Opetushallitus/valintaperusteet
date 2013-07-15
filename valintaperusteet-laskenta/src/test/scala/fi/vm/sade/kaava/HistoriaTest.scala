package fi.vm.sade.kaava

import org.scalatest.FunSuite
import com.codahale.jerkson.Json
import fi.vm.sade.service.valintaperusteet.laskenta.Historia

class HistoriaTest extends FunSuite {

  test("historiaJsonMuunnos") {
    val FUNKTIO1 = "DEMOGRAFIA"
    val FUNKTIO2 = "SUMMA"

    val historia = new Historia(FUNKTIO1, Some(16), Some(List(new Historia(FUNKTIO2, Some(true), None, None))), None);

    val muunnos = Json.parse[Historia](Json.generate(historia))

    assert(muunnos.funktio.equals(FUNKTIO1))
    assert(muunnos.historiat.nonEmpty)
    assert(muunnos.historiat.get(0).funktio.equals(FUNKTIO2))
  }

  test("historiaLaskentaRekursio") {

  }

}