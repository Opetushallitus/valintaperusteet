package fi.vm.sade.kaava

import org.scalatest.FunSuite

import fi.vm.sade.service.valintaperusteet.laskenta.JsonFormats._
import fi.vm.sade.service.valintaperusteet.laskenta.api.tila._
import scala.Some
import fi.vm.sade.service.valintaperusteet.laskenta.Historia


class HistoriaTest extends FunSuite {

  test("historiaJsonMuunnos") {
    val FUNKTIO1 = "DEMOGRAFIA"
    val FUNKTIO2 = "SUMMA"

    val avaimet = Map(
       "oletusarvo" -> None,
       "luku" -> Some("10.0")
    )
    val historia = new Historia(FUNKTIO1, Some(16), List(new Hyvaksyttavissatila, new Virhetila(LaskentaTestUtil.suomenkielinenHylkaysperusteMap("virhe"), new OsallistumistietoaEiVoidaTulkitaVirhe("virhe")), new Hylattytila(LaskentaTestUtil.suomenkielinenHylkaysperusteMap("Hylky") , new Arvokonvertterihylkays("hylky"))), Some(List(new Historia(FUNKTIO2, Some(true), List(), None, None))), Some(avaimet))

    val historiaAsJson = historiaWrites.writes(historia)

    val historiaFromJson = historiaReads.reads(historiaAsJson)
    val muunnos = historiaFromJson.get

    assert(muunnos.funktio.equals(FUNKTIO1))
    assert(muunnos.historiat.nonEmpty)
    assert(muunnos.historiat.get(0).funktio.equals(FUNKTIO2))
  }

  test("historiaLaskentaRekursio") {

  }

}