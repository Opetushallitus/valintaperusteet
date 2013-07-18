package fi.vm.sade.service.valintaperusteet.laskenta

import fi.vm.sade.service.valintaperusteet.laskenta.api.tila.Tila

/**
 * @author Jussi Jartamo
 *
 * Avaimiin voi esim laittaa oidit ja muut valivaiheeseen liittyvat arvot
 */
case class Historia(val funktio: String, val tulos: Option[Any], val tilat: List[Tila], val historiat: Option[List[Historia]], val avaimet: Option[Map[String, Option[Any]]]) {

}