package fi.vm.sade.service.valintaperusteet.laskenta.api

import scala.collection.JavaConversions._
import java.util.{Map => JMap}

/**
 * User: wuoti
 * Date: 13.9.2013
 * Time: 9.30
 */
class Hakukohde(val hakukohdeOid: String, hakukohteenValintaperusteet: JMap[String, String]) {
  val valintaperusteet: Map[String, String] = hakukohteenValintaperusteet.toMap
}
