package fi.vm.sade.kaava

import fi.vm.sade.service.valintaperusteet.laskenta.Historia
import fi.vm.sade.service.valintaperusteet.laskenta.JsonFormats._
import fi.vm.sade.service.valintaperusteet.laskenta.api.tila._
import org.scalatest.funsuite.AnyFunSuite


class HistoriaTest extends AnyFunSuite {

  test("historiaJsonMuunnos") {
    val FUNKTIO1 = "DEMOGRAFIA"
    val FUNKTIO2 = "SUMMA"

    val avaimet = Map(
       "oletusarvo" -> None,
       "luku" -> Some("10.0")
    )
    val historia = Historia(FUNKTIO1, Some(16), List(new Hyvaksyttavissatila, new Virhetila(LaskentaTestUtil.suomenkielinenHylkaysperusteMap("virhe"), new OsallistumistietoaEiVoidaTulkitaVirhe("virhe")), new Hylattytila(LaskentaTestUtil.suomenkielinenHylkaysperusteMap("Hylky"), new Arvokonvertterihylkays("hylky"))), Some(List(Historia(FUNKTIO2, Some(true), List(), None, None))), Some(avaimet))

    val historiaAsJson = historiaWrites.writes(historia)

    val historiaFromJson = historiaReads.reads(historiaAsJson)
    val muunnos = historiaFromJson.get

    assert(muunnos.funktio.equals(FUNKTIO1))
    assert(muunnos.historiat.nonEmpty)
    assert(muunnos.historiat.get.head.funktio.equals(FUNKTIO2))
  }
}
