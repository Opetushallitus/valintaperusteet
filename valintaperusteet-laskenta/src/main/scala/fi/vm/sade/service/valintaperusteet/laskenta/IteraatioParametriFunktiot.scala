package fi.vm.sade.service.valintaperusteet.laskenta

import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.IteroiAmmatillisenTutkinnonYtoOsaAlueet
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.IteroiAmmatillisetTutkinnonOsat
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.IteroiAmmatillisetTutkinnot

trait IteraatioParametriFunktiot {
  protected def historianTiivistelma(historia: Historia, otetaanMukaan: Historia => Boolean): Seq[String] = {
    historianTiivistelma(historia, otetaanMukaan, { h =>
      s"${h.funktio} = ${h.tulos.getOrElse("-")}; avaimet: ${h.avaimet.getOrElse(Map()).map(x => (x._1, x._2.getOrElse("-")))}"
    })
  }

  protected def historianTiivistelma[R](historia: Historia, otetaanMukaan: Historia => Boolean, formatoi: Historia => R): Seq[R] = {
    historia.
      flatten.
      filter(otetaanMukaan).
      map(formatoi)
  }

  protected def ammatillisenTutkinnonValitsija(iteraatioParametrit: LaskennanIteraatioParametrit, f: Funktio[_]): AmmatillisenPerustutkinnonValitsija = {
    palautaParametriTaiIlmoitaVirhe(f, classOf[AmmatillisenPerustutkinnonValitsija], classOf[IteroiAmmatillisetTutkinnot], iteraatioParametrit.ammatillisenPerustutkinnonValitsija)
  }

  protected def ammatillisenTutkinnonOsanValitsija(iteraatioParametrit: LaskennanIteraatioParametrit, f: Funktio[_]): AmmatillisenTutkinnonOsanValitsija = {
    palautaParametriTaiIlmoitaVirhe(f, classOf[AmmatillisenTutkinnonOsanValitsija], classOf[IteroiAmmatillisetTutkinnonOsat], iteraatioParametrit.ammatillisenTutkinnonOsanValitsija)
  }

  protected def ammatillisenYtonOsaAlueenValitsija(iteraatioParametrit: LaskennanIteraatioParametrit, f: Funktio[_]): AmmatillisenTutkinnonYtoOsaAlueenValitsija = {
    palautaParametriTaiIlmoitaVirhe(f, classOf[AmmatillisenTutkinnonYtoOsaAlueenValitsija], classOf[IteroiAmmatillisenTutkinnonYtoOsaAlueet], iteraatioParametrit.ammatillisenTutkinnonYtoOsaAlueenValitsija)
  }

  private def palautaParametriTaiIlmoitaVirhe[T <: IteraatioParametri](f: Funktio[_],
                                                                       parametrinTyppi: Class[T],
                                                                       iterointifunktionTyyppi: Class[_ <: Funktio[_]],
                                                                       iteraatioParametri: Option[IteraatioParametri]
                                                                      ): T = {
    iteraatioParametri match {
      case Some(p) if p.getClass == parametrinTyppi => p.asInstanceOf[T]
      case Some(x) => throw new IllegalArgumentException(s"Vääräntyyppinen iteraatioparametri $x ; piti olla $parametrinTyppi")
      case None => throw new IllegalArgumentException(s"${parametrinTyppi.getName} puuttuu. " +
        s"Onhan funktiokutsun $f yläpuolella puussa $iterointifunktionTyyppi -kutsu?")
    }
  }
}
