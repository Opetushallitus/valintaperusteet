package fi.vm.sade.service.valintaperusteet.laskenta

import api.tila.Tila

trait Konvertteri[S, T] {
  def konvertoi(arvo: S): (Option[T], Tila)
}