package fi.vm.sade.service.valintaperusteet.laskenta

sealed trait IteraatioParametri

case class AmmatillisenPerustutkinnonValitsija(tutkinnonIndeksi: Int) extends IteraatioParametri

case class AmmatillisetPerustutkinnot(tutkintojenMaara: Int) {
  def parametreiksi: Seq[AmmatillisenPerustutkinnonValitsija] = 0.until(tutkintojenMaara).map(AmmatillisenPerustutkinnonValitsija)
}

case class AmmatillisenTutkinnonOsanValitsija(osanIndeksi: Int) extends IteraatioParametri

case class AmmatillisenTutkinnonOsat(tutkinnonOsienMaara: Int) {
  def parametreiksi: Seq[AmmatillisenTutkinnonOsanValitsija] = 0.until(tutkinnonOsienMaara).map(AmmatillisenTutkinnonOsanValitsija)
}

case class AmmatillisenTutkinnonYtoOsaAlueenValitsija(ytoKoodi: String, osanIndeksi: Int) extends IteraatioParametri

case class AmmatillisenTutkinnonYtoOsaAlueet(ytoKoodi: String, tutkinnonYtoOsaAlueidenMaara: Int) {
  def parametreiksi: Seq[AmmatillisenTutkinnonYtoOsaAlueenValitsija] = 0.until(tutkinnonYtoOsaAlueidenMaara).map(i => AmmatillisenTutkinnonYtoOsaAlueenValitsija(ytoKoodi, i))
}
