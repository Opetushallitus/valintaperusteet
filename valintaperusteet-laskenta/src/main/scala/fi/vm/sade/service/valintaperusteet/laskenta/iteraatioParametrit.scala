package fi.vm.sade.service.valintaperusteet.laskenta

import java.time.LocalDate

import fi.vm.sade.service.valintaperusteet.laskenta.koski.Osasuoritus
import fi.vm.sade.service.valintaperusteet.laskenta.koski.Tutkinto

sealed trait IteraatioParametri {
  val kuvaus: String = toString
  val lyhytKuvaus: String = toString
}

case class AmmatillisenPerustutkinnonValitsija(tutkinto: Tutkinto, valmistumisenTakarajaPvm: LocalDate = LocalDate.of(2020, 6, 1)) extends IteraatioParametri {
  val tutkinnonIndeksi: Int = tutkinto.indeksi
  override val kuvaus: String = s"Tutkinto ${tutkinto.indeksi + 1} " +
    s"(opiskeluoikeus ${tutkinto.opiskeluoikeudenOid}, versio ${tutkinto.opiskeluoikeudenVersio}, " +
    s"aikaleima ${tutkinto.opiskeluoikeudenAikaleima}, oppilaitos ${tutkinto.opiskeluoikeudenOppilaitoksenSuomenkielinenNimi}, " +
    s"vahvistusPvm ${tutkinto.vahvistusPvm})"
  override val lyhytKuvaus: String = s"Tutkinto ${tutkinto.indeksi + 1}"
}

case class AmmatillisetPerustutkinnot(tutkinnot: Seq[Tutkinto]) {
  def parametreiksi: Seq[AmmatillisenPerustutkinnonValitsija] = tutkinnot.map(AmmatillisenPerustutkinnonValitsija(_))
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
  val nonEmpty: Boolean = ammatillisenTutkinnonYtoOsaAlueenValitsija.isDefined || ammatillisenTutkinnonOsanValitsija.isDefined || ammatillisenTutkinnonYtoOsaAlueenValitsija.isDefined
  val asList: Seq[IteraatioParametri] = ammatillisenPerustutkinnonValitsija.toList ++ ammatillisenTutkinnonOsanValitsija.toList ++ ammatillisenTutkinnonYtoOsaAlueenValitsija.toList

  def asetaAvoinParametrilista[T <: IteraatioParametri](tyyppi: Class[T], parametrit: Seq[T]): LaskennanIteraatioParametrit = {
    copy(parametriListat = parametriListat.updated(tyyppi, parametrit))
  }

  def sisaltaaAvoimenParametrilistan: Boolean = parametriListat.exists(x => x._2.nonEmpty)

  def avoinParametrilista: Seq[IteraatioParametri] = parametriListat.toSeq.filter(x => x._2.nonEmpty) match {
    case (_, lista) :: Nil => lista
    case Nil => throw new IllegalArgumentException(s"Ei löytynyt avointa parametrilistaa iteraatioparametreista $this")
    case _ => throw new IllegalArgumentException(s"Löytyi useampi avoin parametrilista iteraatioparametreista $this")
  }

  def sido(parametri: IteraatioParametri): LaskennanIteraatioParametrit = {
    parametri match {
      case p: AmmatillisenPerustutkinnonValitsija =>
        copy(ammatillisenPerustutkinnonValitsija = Some(p), parametriListat = listatIlman(classOf[AmmatillisenPerustutkinnonValitsija]))
      case p: AmmatillisenTutkinnonOsanValitsija =>
        copy(ammatillisenTutkinnonOsanValitsija = Some(p), parametriListat = listatIlman(classOf[AmmatillisenTutkinnonOsanValitsija]))
      case p: AmmatillisenTutkinnonYtoOsaAlueenValitsija =>
        copy(ammatillisenTutkinnonYtoOsaAlueenValitsija = Some(p), parametriListat = listatIlman(classOf[AmmatillisenTutkinnonYtoOsaAlueenValitsija]))
    }
  }

  private def listatIlman[T <: IteraatioParametri](poistettavaTyyppi: Class[T]): Map[Class[_ <: IteraatioParametri], Seq[IteraatioParametri]] = {
    parametriListat.filter(kv => kv._1 != poistettavaTyyppi)
  }
}
