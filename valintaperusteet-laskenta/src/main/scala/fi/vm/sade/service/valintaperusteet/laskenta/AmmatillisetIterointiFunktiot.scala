package fi.vm.sade.service.valintaperusteet.laskenta

import fi.vm.sade.kaava.LaskentaUtil.suomalainenPvmMuoto
import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi.ITEROIAMMATILLISETOSAT
import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi.ITEROIAMMATILLISETTUTKINNOT
import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi.ITEROIAMMATILLISETYTOOSAALUEET
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.IteroiAmmatillisenTutkinnonYtoOsaAlueet
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.IteroiAmmatillisetTutkinnonOsat
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.IteroiAmmatillisetTutkinnot
import fi.vm.sade.service.valintaperusteet.laskenta.api.tila.Hyvaksyttavissatila
import fi.vm.sade.service.valintaperusteet.laskenta.api.tila.Tila
import fi.vm.sade.service.valintaperusteet.laskenta.koski.KoskiLaskenta
import fi.vm.sade.service.valintaperusteet.laskenta.koski.Osasuoritus
import fi.vm.sade.service.valintaperusteet.laskenta.koski.Tutkinto

import scala.collection.immutable.ListMap

trait AmmatillisetIterointiFunktiot {
  this: LukuarvoLaskin =>

  protected def iteroiAmmatillisetTutkinnot(iteraatioParametrit: LaskennanIteraatioParametrit,
                                            iterointiFunktio: IteroiAmmatillisetTutkinnot,
                                           ): (Option[BigDecimal], List[Tila], Historia) = {
    if (iteraatioParametrit.ammatillisenPerustutkinnonValitsija.isDefined) {
      throw new IllegalStateException(s"Ei voi iteroida iteraatioparametrilla ${iteraatioParametrit.ammatillisenPerustutkinnonValitsija} uudestaan ammatillisten tutkintojen yli")
    } else {
      val tutkinnot: Seq[Tutkinto] = KoskiLaskenta.etsiAmmatillisetTutkinnot(laskin.hakemus, iterointiFunktio.datanAikaleimanLeikkuri, iterointiFunktio.valmistumisenTakaraja)
      val tutkintojenMaara = tutkinnot.size

      val tutkintojenIterointiParametrit: Seq[AmmatillisenPerustutkinnonValitsija] = AmmatillisetPerustutkinnot(tutkinnot).parametreiksi
      Laskin.LOG.info(s"Hakemuksen ${laskin.hakemus.oid} hakijalle löytyi $tutkintojenMaara ammatillista perustutkintoa: ${tutkintojenIterointiParametrit.map(_.kuvaus)}.")
      Laskin.LOG.info(s"${classOf[IteroiAmmatillisetTutkinnot].getSimpleName}-funktion parametrit: " +
        s"valmistumisenTakaraja = ${iterointiFunktio.valmistumisenTakaraja} , datanAikaleimanLeikkuri = ${iterointiFunktio.datanAikaleimanLeikkuri}")
      val uudetIteraatioParametrit = iteraatioParametrit.asetaAvoinParametrilista(classOf[AmmatillisenPerustutkinnonValitsija], tutkintojenIterointiParametrit)

      val tulos: Tulos[BigDecimal] = if (tutkintojenIterointiParametrit.nonEmpty) {
        laskeLukuarvo(iterointiFunktio.f, uudetIteraatioParametrit)
      } else {
        Tulos(None, new Hyvaksyttavissatila, Historia(ITEROIAMMATILLISETTUTKINNOT, None, Nil, None, None))
      }

      val ammatillistenFunktioidenTulostenTiivistelmat: ListMap[String, Option[Any]] = tallennetutTuloksetHistoriaaVarten

      val tilalista = List(tulos.tila)
      val avaimet = ListMap(
        "Valmistumisen takaraja" -> Some(suomalainenPvmMuoto.format(iterointiFunktio.valmistumisenTakaraja)),
        "Tiedot tallennettu viimeistään" -> Some(suomalainenPvmMuoto.format(iterointiFunktio.datanAikaleimanLeikkuri)),
        "Ammatillisten perustutkintojen määrä" -> Some(tutkintojenMaara),
        "Ammatilliset perustutkinnot" -> Some(tutkintojenIterointiParametrit.map(_.kuvaus).mkString("; "))) ++
        ammatillistenFunktioidenTulostenTiivistelmat
      (tulos.tulos, tilalista, Historia(ITEROIAMMATILLISETTUTKINNOT, tulos.tulos, tilalista, Some(List(tulos.historia)), Some(avaimet)))
    }
  }

  protected def iteroiAmmatillisetTutkinnonOsat(iteraatioParametrit: LaskennanIteraatioParametrit,
                                                iterointiFunktio: IteroiAmmatillisetTutkinnonOsat
                                               ): (Option[BigDecimal], List[Tila], Historia) = {
    val tutkinnonValitsija: AmmatillisenPerustutkinnonValitsija = ammatillisenTutkinnonValitsija(iteraatioParametrit, iterointiFunktio)

    if (iteraatioParametrit.ammatillisenTutkinnonOsanValitsija.isDefined) {
      throw new IllegalStateException(s"Ei voi iteroida iteraatioparametrilla ${iteraatioParametrit.ammatillisenTutkinnonOsanValitsija} uudestaan ammatillisen tutkinnon osien yli")
    } else {
      val tutkinnonOsat: Seq[Osasuoritus] = KoskiLaskenta.etsiAmmatillisenTutkinnonOsat(tutkinnonValitsija, laskin.hakemus)
      val osienMaara = tutkinnonOsat.size
      Laskin.LOG.info(s"Hakemuksen ${laskin.hakemus.oid} hakijan tutkinnolle ${tutkinnonValitsija.kuvaus} löytyi $osienMaara ammatillista perustutkinnon osaa.")

      val tutkinnonOsienIterointiParametrit: Seq[AmmatillisenTutkinnonOsanValitsija] = AmmatillisenTutkinnonOsat(tutkinnonOsat).parametreiksi

      val uudetIteraatioParametrit = iteraatioParametrit.asetaAvoinParametrilista(classOf[AmmatillisenTutkinnonOsanValitsija], tutkinnonOsienIterointiParametrit)

      val tulos: Tulos[BigDecimal] = if (tutkinnonOsienIterointiParametrit.nonEmpty) {
        laskeLukuarvo(iterointiFunktio.f, uudetIteraatioParametrit)
      } else {
        Tulos(None, new Hyvaksyttavissatila, Historia(ITEROIAMMATILLISETOSAT, None, Nil, None, None))
      }

      val tilalista = List(tulos.tila)
      val avaimet = Map(
        s"ammatillisen perustutkinnon ${tutkinnonValitsija.lyhytKuvaus} osien määrä" -> Some(osienMaara),
        s"ammatillisen perustutkinnon ${tutkinnonValitsija.lyhytKuvaus} osat" -> Some(tutkinnonOsienIterointiParametrit.map(_.kuvaus).mkString("; ")))
      (tulos.tulos, tilalista, Historia(ITEROIAMMATILLISETOSAT, tulos.tulos, tilalista, Some(List(tulos.historia)), Some(avaimet)))
    }
  }

  protected def iteroiAmmatillisenTutkinnonYtoOsaAlueet(iteraatioParametrit: LaskennanIteraatioParametrit,
                                                        iterointiFunktio: IteroiAmmatillisenTutkinnonYtoOsaAlueet
                                                     ): (Option[BigDecimal], List[Tila], Historia) = {
    val tutkinnonValitsija: AmmatillisenPerustutkinnonValitsija = ammatillisenTutkinnonValitsija(iteraatioParametrit, iterointiFunktio)

    if (iteraatioParametrit.ammatillisenTutkinnonYtoOsaAlueenValitsija.isDefined) {
      throw new IllegalStateException(s"Ei voi iteroida iteraatioparametrilla ${iteraatioParametrit.ammatillisenTutkinnonYtoOsaAlueenValitsija} uudestaan ammatillisen tutkinnon osien yli")
    } else {
      val ytoKoodi = iterointiFunktio.valintaperusteviite.tunniste
      val ytoOsaAlueet = KoskiLaskenta.haeAmmatillisenTutkinnonYtoOsaAlueet(tutkinnonValitsija, ytoKoodi, laskin.hakemus)

      Laskin.LOG.info(s"Hakemuksen ${laskin.hakemus.oid} hakijan tutkinnon ${tutkinnonValitsija.lyhytKuvaus} YTOlle $ytoKoodi löytyi ${ytoOsaAlueet.size} YTOn osa-aluetta.")

      val ytoOsaAlueidenIteraatioParametrit: Seq[AmmatillisenTutkinnonYtoOsaAlueenValitsija] = AmmatillisenTutkinnonYtoOsaAlueet(ytoKoodi, ytoOsaAlueet).parametreiksi

      val uudetIteraatioParametrit = iteraatioParametrit.asetaAvoinParametrilista(classOf[AmmatillisenTutkinnonYtoOsaAlueenValitsija], ytoOsaAlueidenIteraatioParametrit)

      val tulos: Tulos[BigDecimal] = if (ytoOsaAlueidenIteraatioParametrit.nonEmpty) {
        laskeLukuarvo(iterointiFunktio.f, uudetIteraatioParametrit)
      } else {
        Tulos(None, new Hyvaksyttavissatila, Historia(ITEROIAMMATILLISETYTOOSAALUEET, None, Nil, None, None))
      }

      val tilalista = List(tulos.tila)
      val avaimet = Map(
        s"ammatillisen perustutkinnon ${tutkinnonValitsija.lyhytKuvaus} YTOn $ytoKoodi osa-alueiden määrä" -> Some(ytoOsaAlueet.size),
        s"ammatillisen perustutkinnon ${tutkinnonValitsija.lyhytKuvaus} YTOn $ytoKoodi osa-alueet" -> Some(ytoOsaAlueidenIteraatioParametrit.map(_.kuvaus).mkString("; ")))
      (tulos.tulos, tilalista, Historia(ITEROIAMMATILLISETYTOOSAALUEET, tulos.tulos, tilalista, Some(List(tulos.historia)), Some(avaimet)))
    }
  }

  private def tallennetutTuloksetHistoriaaVarten: ListMap[String, Option[String]] = {
    val tuloksetSuomenkielistenNimienKanssa: List[(String, Option[String])] = laskin.funktioTulokset.toList.map {
      case (avain, ft) => (s"$avain : ${ft.nimiFi}", Some(ft.arvo))
    }
    ListMap(tuloksetSuomenkielistenNimienKanssa : _*)
  }
}
