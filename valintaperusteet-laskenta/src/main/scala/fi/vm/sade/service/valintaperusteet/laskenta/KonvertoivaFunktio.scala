package fi.vm.sade.service.valintaperusteet.laskenta

trait KonvertoivaFunktio[S, T] extends Funktio[T] {
    val f: Funktio[S]
    val konvertteri: Konvertteri[S, T]
  }