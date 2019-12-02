package fi.vm.sade.service.valintaperusteet.laskenta

import scala.math.BigDecimal

trait Funktio[T] {
  val oid: String
  val tulosTunniste: String
  val tulosTekstiFi: String
  val tulosTekstiSv: String
  val tulosTekstiEn: String
  val omaopintopolku: Boolean
}

trait Lukuarvofunktio extends Funktio[BigDecimal]

trait IteroitavaLukuarvofunktio[T <: IteraatioParametri] extends Lukuarvofunktio {
  val iteraatioparametrinTyppi: Class[T]
  def sovellaParametri(parametri: T): Lukuarvofunktio
}

trait Totuusarvofunktio extends Funktio[Boolean]
