package fi.vm.sade.service.valintaperusteet.laskenta

import scala.math.BigDecimal
import fi.vm.sade.service.valintaperusteet.laskenta.api.tila.Tila

/**
 * Created with IntelliJ IDEA.
 * User: kjsaila
 * Date: 06/11/13
 * Time: 08:51
 * To change this template use File | Settings | File Templates.
 */

trait Funktio[T] {
  val oid: String
}

trait Lukuarvofunktio extends Funktio[BigDecimal]

trait Totuusarvofunktio extends Funktio[Boolean]

trait Konvertteri[S, T] {
  def konvertoi(arvo: S): (Option[T], Tila)
}

trait KonvertoivaFunktio[S, T] extends Funktio[T] {
  val f: Funktio[S]
  val konvertteri: Konvertteri[S, T]
}
