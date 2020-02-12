package fi.vm.sade.service.valintaperusteet.laskenta

import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.IteroiAmmatillisetTutkinnonOsat
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.IteroiAmmatillisetTutkinnot

trait IteraatioParametriFunktiot {
  protected def historianTiivistelma(historia: Historia, otetaanMukaan: Historia => Boolean): Seq[String] = {
    historia.
      flatten.
      filter(otetaanMukaan).
      map { h =>
        s"${h.funktio} = ${h.tulos.getOrElse("-")}; avaimet: ${h.avaimet.getOrElse(Map()).map(x => (x._1, x._2.getOrElse("-")))}"
      }
  }

  protected def ammatillisenTutkinnonValitsija(iteraatioParametrit: Map[Class[_ <: IteraatioParametri], IteraatioParametri], f: Funktio[_]): AmmatillisenPerustutkinnonValitsija = {
    haeIteraatioParametri(iteraatioParametrit, f, classOf[AmmatillisenPerustutkinnonValitsija], classOf[IteroiAmmatillisetTutkinnot])
  }

  protected def ammatillisenTutkinnonOsanValitsija(iteraatioParametrit: Map[Class[_ <: IteraatioParametri], IteraatioParametri], f: Funktio[_]): AmmatillisenTutkinnonOsanValitsija = {
    haeIteraatioParametri(iteraatioParametrit, f, classOf[AmmatillisenTutkinnonOsanValitsija], classOf[IteroiAmmatillisetTutkinnonOsat])
  }

  protected def ammatillisenYtonOsaAlueenValitsija(iteraatioParametrit: Map[Class[_ <: IteraatioParametri], IteraatioParametri], f: Funktio[_]): AmmatillisenTutkinnonYtoOsaAlueenValitsija = {
    haeIteraatioParametri(iteraatioParametrit, f, classOf[AmmatillisenTutkinnonYtoOsaAlueenValitsija], classOf[IteroiAmmatillisetTutkinnonOsat])
  }

  protected def haeIteraatioParametri[T <: IteraatioParametri](iteraatioParametrit: Map[Class[_ <: IteraatioParametri], IteraatioParametri],
                                                             f: Funktio[_],
                                                             parametrinTyppi: Class[T],
                                                             iterointifunktionTyyppi: Class[_ <: Funktio[_]]
                                                            ): T = {
    val iteraatioParametri = iteraatioParametrit.get(parametrinTyppi)
    iteraatioParametri match {
      case Some(p) if p.getClass == parametrinTyppi => p.asInstanceOf[T]
      case Some(x) => throw new IllegalArgumentException(s"Vääräntyyppinen iteraatioparametri $x ; piti olla $parametrinTyppi")
      case None => throw new IllegalArgumentException(s"${parametrinTyppi.getName} puuttuu. " +
        s"Onhan funktiokutsun $f yläpuolella puussa $iterointifunktionTyyppi -kutsu?")
    }
  }
}
