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

case class AmmatillisenTutkinnonYtoOsaAlueenValitsija(ytoKoodi: String, osasuoritus: Osasuoritus, osanIndeksi: Int) extends IteraatioParametri {
  override val kuvaus: String = s"YTO:n ${ytoKoodi} osa-alue ${osanIndeksi + 1}: ${osasuoritus.koulutusmoduulinNimiFi}"
  override val lyhytKuvaus: String = s"YTO:n ${ytoKoodi} osa-alue: ${osasuoritus.koulutusmoduulinNimiFi}"
}

case class AmmatillisenTutkinnonYtoOsaAlueet(ytoKoodi: String, tutkinnonOsaAlueet: Seq[Osasuoritus]) {
  def parametreiksi: Seq[AmmatillisenTutkinnonYtoOsaAlueenValitsija] = tutkinnonOsaAlueet.zipWithIndex.map(
    x => AmmatillisenTutkinnonYtoOsaAlueenValitsija(ytoKoodi, x._1, x._2)
  )
}

case class LaskennanIteraatioParametrit(parametriListat: Map[Class[_ <: IteraatioParametri], Seq[IteraatioParametri]] = Map(),
                                        ammatillisenPerustutkinnonValitsija: Option[AmmatillisenPerustutkinnonValitsija] = None,
                                        ammatillisenTutkinnonOsanValitsija: Option[AmmatillisenTutkinnonOsanValitsija] = None,
                                        ammatillisenTutkinnonYtoOsaAlueenValitsija: Option[AmmatillisenTutkinnonYtoOsaAlueenValitsija] = None) {
  val asList: Seq[IteraatioParametri] = ammatillisenPerustutkinnonValitsija.toList ++ ammatillisenTutkinnonOsanValitsija.toList ++ ammatillisenTutkinnonYtoOsaAlueenValitsija.toList
  val nonEmpty: Boolean = ammatillisenTutkinnonYtoOsaAlueenValitsija.isDefined || ammatillisenTutkinnonOsanValitsija.isDefined || ammatillisenTutkinnonYtoOsaAlueenValitsija.isDefined
}
