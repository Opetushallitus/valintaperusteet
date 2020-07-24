package fi.vm.sade.service.valintaperusteet.laskenta

import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi
import fi.vm.sade.service.valintaperusteet.laskenta.api.tila.Tila

case class Historia(
  funktio: String,
  funktionimi: Option[Funktionimi] = None,
  tulos: Option[Any],
  tilat: List[Tila],
  historiat: Option[List[Historia]],
  avaimet: Option[Map[String, Option[Any]]]
) {
  def flatten: Seq[Historia] = {
    flatten(this)
  }

  private def flatten(historia: Historia): Seq[Historia] = {
    historia +: historia.historiat.toSeq.flatten.flatMap(flatten)
  }
}

object Historia {
  def apply(
    funktioNimi: Funktionimi,
    tulos: Option[Any],
    tilat: List[Tila],
    historiat: Option[List[Historia]],
    avaimet: Option[Map[String, Option[Any]]]
  ): Historia = {
    Historia(funktioNimi.getKuvaus, Some(funktioNimi), tulos, tilat, historiat, avaimet)
  }

  def apply(
    funktio: String,
    tulos: Option[Any],
    tilat: List[Tila],
    historiat: Option[List[Historia]],
    avaimet: Option[Map[String, Option[Any]]]
  ): Historia = {
    Historia(funktio, None, tulos, tilat, historiat, avaimet)
  }
}
