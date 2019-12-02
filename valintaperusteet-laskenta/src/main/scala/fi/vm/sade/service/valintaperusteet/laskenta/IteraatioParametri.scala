package fi.vm.sade.service.valintaperusteet.laskenta

sealed trait IteraatioParametri

case class AmmatillisenPerustutkinnonValitsija(tutkinnonIndeksi: Int) extends IteraatioParametri

case class AmmatillisetPerustutkinnot(tutkintojenMaara: Int) {
  def parametreiksi: Seq[AmmatillisenPerustutkinnonValitsija] = 0.until(tutkintojenMaara).map(AmmatillisenPerustutkinnonValitsija)
}
