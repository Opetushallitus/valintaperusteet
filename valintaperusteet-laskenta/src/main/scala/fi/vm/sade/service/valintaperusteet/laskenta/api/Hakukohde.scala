package fi.vm.sade.service.valintaperusteet.laskenta.api

import scala.jdk.CollectionConverters._
import java.util.{Map => JMap}

class Hakukohde(
  val hakukohdeOid: String,
  hakukohteenValintaperusteet: JMap[String, String],
  val korkeakouluhaku: Boolean = false
) {
  val valintaperusteet: Map[String, String] = hakukohteenValintaperusteet.asScala.toMap
}
