package fi.vm.sade.service.valintaperusteet.laskenta.api

import scala.collection.JavaConversions._
import java.util.{Map => JMap}

class Hakukohde(val hakukohdeOid: String, hakukohteenValintaperusteet: JMap[String, String]) {
  val valintaperusteet: Map[String, String] = hakukohteenValintaperusteet.toMap
}
