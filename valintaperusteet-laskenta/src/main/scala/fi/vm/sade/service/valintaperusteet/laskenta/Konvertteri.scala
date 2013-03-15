package fi.vm.sade.service.valintaperusteet.laskenta

import api.tila.Tila

trait Konvertteri[S, T] {
    def konvertoi(funktiokutsuOid: String, arvo: S): (T, Tila)
  }