package fi.vm.sade.service.valintaperusteet.laskenta

/**
 * @author Jussi Jartamo
 *
 * Avaimiin voi esim laittaa oidit ja muut valivaiheeseen liittyvat arvot
 */
case class Historia(val funktio: String, val tulos: Option[Any], val historiat: Option[List[Historia]], val avaimet: Option[Map[String, Option[Any]]]) {

}