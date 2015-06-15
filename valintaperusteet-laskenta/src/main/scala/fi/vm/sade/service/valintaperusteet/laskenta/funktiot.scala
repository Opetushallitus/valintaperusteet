package fi.vm.sade.service.valintaperusteet.laskenta

import scala.math.BigDecimal

trait Funktio[T] {
  val oid: String
  val tulosTunniste: String
  val tulosTekstiFi: String
  val tulosTekstiSv: String
  val tulosTekstiEn: String
}

trait Lukuarvofunktio extends Funktio[BigDecimal]

trait Totuusarvofunktio extends Funktio[Boolean]
