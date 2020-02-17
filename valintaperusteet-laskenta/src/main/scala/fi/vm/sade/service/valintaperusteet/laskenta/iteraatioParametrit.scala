package fi.vm.sade.service.valintaperusteet.laskenta

import fi.vm.sade.service.valintaperusteet.laskenta.koski.Osasuoritus
import fi.vm.sade.service.valintaperusteet.laskenta.koski.Tutkinto

sealed trait IteraatioParametri {
  val kuvaus: String = toString
  val lyhytKuvaus: String = toString
}

case class AmmatillisenPerustutkinnonValitsija(tutkinto: Tutkinto) extends IteraatioParametri {
  val tutkinnonIndeksi: Int = tutkinto.indeksi
  override val kuvaus: String = s"Tutkinto ${tutkinto.indeksi + 1} " +
    s"(opiskeluoikeus ${tutkinto.opiskeluoikeudenOid}, versio ${tutkinto.opiskeluoikeudenVersio}, " +
    s"aikaleima ${tutkinto.opiskeluoikeudenAikaleima}, oppilaitos ${tutkinto.opiskeluoikeudenOppilaitoksenSuomenkielinenNimi})"
  override val lyhytKuvaus: String = s"Tutkinto ${tutkinto.indeksi + 1}"
}

case class AmmatillisetPerustutkinnot(tutkinnot: Seq[Tutkinto]) {
  def parametreiksi: Seq[AmmatillisenPerustutkinnonValitsija] = tutkinnot.map(AmmatillisenPerustutkinnonValitsija)
}

case class AmmatillisenTutkinnonOsanValitsija(osasuoritus: Osasuoritus, indeksi: Int) extends IteraatioParametri {
  val osanIndeksi: Int = indeksi
  override val kuvaus: String = s"Osa ${indeksi + 1} " +
    s"""(koulutusmoduuli ${osasuoritus.koulutusmoduulinTunnisteenKoodiarvo} "${osasuoritus.koulutusmoduulinNimiFi}")"""
  override val lyhytKuvaus: String = s"${osasuoritus.koulutusmoduulinNimiFi}"
}

case class AmmatillisenTutkinnonOsat(tutkinnonOsat: Seq[Osasuoritus]) {
  def parametreiksi: Seq[AmmatillisenTutkinnonOsanValitsija] = tutkinnonOsat.zipWithIndex.map(AmmatillisenTutkinnonOsanValitsija.tupled)
}

case class AmmatillisenTutkinnonYtoOsaAlueenValitsija(ytoKoodi: String, osanIndeksi: Int) extends IteraatioParametri

case class AmmatillisenTutkinnonYtoOsaAlueet(ytoKoodi: String, tutkinnonYtoOsaAlueidenMaara: Int) {
  def parametreiksi: Seq[AmmatillisenTutkinnonYtoOsaAlueenValitsija] = 0.until(tutkinnonYtoOsaAlueidenMaara).map(i => AmmatillisenTutkinnonYtoOsaAlueenValitsija(ytoKoodi, i))
}
