package fi.vm.sade.service.valintaperusteet.laskenta

import scala.math.BigDecimal

/**
 * Created with IntelliJ IDEA.
 * User: kjsaila
 * Date: 06/11/13
 * Time: 08:51
 * To change this template use File | Settings | File Templates.
 */

trait Funktio[T] {
  val oid: String
  val tulosTunniste: String
}

trait Lukuarvofunktio extends Funktio[BigDecimal]

trait Totuusarvofunktio extends Funktio[Boolean]
