package fi.vm.sade.service.valintaperusteet.laskenta

import fi.vm.sade.service.valintaperusteet.dto.model.Osallistuminen
import fi.vm.sade.service.valintaperusteet.laskenta.api.tila.Tila

case class Tulos[T](tulos: Option[T], tila: Tila, historia: Historia)

protected[laskenta] case class SyotettyArvo(tunniste: String,
                                arvo: Option[String],
                                laskennallinenArvo: Option[String],
                                osallistuminen: Osallistuminen,
                                syotettavanarvontyyppiKoodiUri: Option[String],
                                tilastoidaan: Boolean
                               )

protected[laskenta] case class FunktioTulos(tunniste: String,
                                arvo: String,
                                nimiFi: String,
                                nimiSv: String,
                                nimiEn: String,
                                omaopintopolku: Boolean
                               )

protected[laskenta] object Laskentamoodi extends Enumeration {
  type Laskentamoodi = Value

  val VALINTALASKENTA: Laskentamoodi = Value("VALINTALASKENTA")
  val VALINTAKOELASKENTA: Laskentamoodi = Value("VALINTAKOELASKENTA")
}
