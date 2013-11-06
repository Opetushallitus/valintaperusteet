package fi.vm.sade.service.valintaperusteet.laskenta

import fi.vm.sade.service.valintaperusteet.laskenta.api.tila.Tila

/**
 * @author Jussi Jartamo
 *
 * Avaimiin voi esim laittaa oidit ja muut valivaiheeseen liittyvat arvot
 */
case class Historia(funktio: String, tulos: Option[Any], tilat: List[Tila], historiat: Option[List[Historia]], avaimet: Option[Map[String, Option[Any]]]) {

}