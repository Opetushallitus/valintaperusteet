package fi.vm.sade.service.valintaperusteet.laskenta

import fi.vm.sade.service.valintaperusteet.laskenta.api.tila.Tila

case class Historia(funktio: String, tulos: Option[Any], tilat: List[Tila], historiat: Option[List[Historia]], avaimet: Option[Map[String, Option[Any]]]) {

}