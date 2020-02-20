package fi.vm.sade.service.valintaperusteet.laskenta

import fi.vm.sade.kaava.LaskentaUtil
import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi.ITEROIAMMATILLISETOSAT
import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi.ITEROIAMMATILLISETTUTKINNOT
import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi.ITEROIAMMATILLISETYTOOSAALUEET
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.IteroiAmmatillisenTutkinnonYtoOsaAlueet
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.IteroiAmmatillisetTutkinnonOsat
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.KloonattavaFunktio
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.Lukuarvo
import fi.vm.sade.service.valintaperusteet.laskenta.api.tila.Hyvaksyttavissatila
import fi.vm.sade.service.valintaperusteet.laskenta.api.tila.Tila
import fi.vm.sade.service.valintaperusteet.laskenta.koski.KoskiLaskenta
import fi.vm.sade.service.valintaperusteet.laskenta.koski.Osasuoritus
import fi.vm.sade.service.valintaperusteet.laskenta.koski.Tutkinto

import scala.collection.immutable.ListMap

trait AmmatillisetIterointiFunktiot {
  this: LukuarvoLaskin =>

  protected def iteroiAmmatillisetTutkinnot(iteraatioParametrit: LaskennanIteraatioParametrit,
                                            f: Lukuarvofunktio with Laskenta.KoostavaFunktio[BigDecimal] with KloonattavaFunktio[BigDecimal, Lukuarvofunktio, Laskenta.KoostavaFunktio[BigDecimal]]
                                           ): (Option[BigDecimal], List[Tila], Historia) = {
    if (iteraatioParametrit.ammatillisenPerustutkinnonValitsija.isDefined) {
      throw new IllegalStateException(s"Ei voi iteroida iteraatioparametrilla ${iteraatioParametrit.ammatillisenPerustutkinnonValitsija} uudestaan ammatillisten tutkintojen yli")
    } else {
      val tutkinnot: Seq[Tutkinto] = KoskiLaskenta.etsiAmmatillisetTutkinnot(laskin.hakemus)
      val tutkintojenMaara = tutkinnot.size
      Laskin.LOG.info(s"Hakemuksen ${laskin.hakemus.oid} hakijalle löytyi $tutkintojenMaara ammatillista perustutkintoa.")

      val tutkintojenIterointiParametrit: Seq[AmmatillisenPerustutkinnonValitsija] = AmmatillisetPerustutkinnot(tutkinnot).parametreiksi
      val uudetIteraatioParametrit = iteraatioParametrit.asetaAvoinParametrilista(classOf[AmmatillisenPerustutkinnonValitsija], tutkintojenIterointiParametrit)

      val tulos: Tulos[BigDecimal] = if (tutkintojenIterointiParametrit.nonEmpty) {
        laskeLukuarvo(f, uudetIteraatioParametrit)
      } else {
        Tulos(None, new Hyvaksyttavissatila, Historia(ITEROIAMMATILLISETTUTKINNOT, None, Nil, None, None))
      }

      val ammatillistenFunktioidenTulostenTiivistelmat: ListMap[String, Option[Any]] = tallennetutTuloksetHistoriaaVarten

      val tilalista = List(tulos.tila)
      val avaimet = ListMap(
        "Ammatillisten perustutkintojen määrä" -> Some(tutkintojenMaara),
        "Ammatilliset perustutkinnot" -> Some(tutkintojenIterointiParametrit.map(_.kuvaus).mkString("; "))) ++
        ammatillistenFunktioidenTulostenTiivistelmat
      (tulos.tulos, tilalista, Historia(ITEROIAMMATILLISETTUTKINNOT, tulos.tulos, tilalista, Some(List(tulos.historia)), Some(avaimet)))
    }
  }

  protected def iteroiAmmatillisetTutkinnonOsat(iteraatioParametrit: LaskennanIteraatioParametrit,
                                                f: IteroiAmmatillisetTutkinnonOsat,
                                                lapsiF: Lukuarvofunktio with KloonattavaFunktio[BigDecimal, _, Funktio[BigDecimal]]
                                               ): (Option[BigDecimal], List[Tila], Historia) = {
    val lapsiFunktio = lapsiF.asInstanceOf[KloonattavaFunktio[BigDecimal, (Lukuarvofunktio, Lukuarvofunktio), _]]
    if (lapsiFunktio.argumentit.length != 1) {
      throw new IllegalStateException(s"Odotettiin täsmälleen yhtä paria argumentiksi ${classOf[IteroiAmmatillisetTutkinnonOsat].getSimpleName}-funktion lapselle," +
        s"mutta löytyi ${lapsiFunktio.argumentit.length} . Funktio == $f , Lapsifunktio == $lapsiFunktio")
    }
    val tutkinnonValitsija: AmmatillisenPerustutkinnonValitsija = ammatillisenTutkinnonValitsija(iteraatioParametrit, f)

    if (iteraatioParametrit.ammatillisenTutkinnonOsanValitsija.isDefined) {
      throw new IllegalStateException(s"Ei voi iteroida iteraatioparametrilla ${iteraatioParametrit.ammatillisenTutkinnonOsanValitsija} uudestaan ammatillisen tutkinnon osien yli")
    } else {
      val tutkinnonOsat: Seq[Osasuoritus] = KoskiLaskenta.etsiAmmatillisenTutkinnonOsat(tutkinnonValitsija, laskin.hakemus)
      val osienMaara = tutkinnonOsat.size
      Laskin.LOG.info(s"Hakemuksen ${laskin.hakemus.oid} hakijan tutkinnolle $tutkinnonValitsija löytyi $osienMaara ammatillista perustutkinnon osaa.")

      val tutkinnonOsienIterointiParametrit: Seq[AmmatillisenTutkinnonOsanValitsija] = AmmatillisenTutkinnonOsat(tutkinnonOsat).parametreiksi

      val uudetIteraatioParametrit = iteraatioParametrit.asetaAvoinParametrilista(classOf[AmmatillisenTutkinnonOsanValitsija], tutkinnonOsienIterointiParametrit)

      val tulos: Tulos[BigDecimal] = if (tutkinnonOsienIterointiParametrit.nonEmpty) {
        laskeLukuarvo(lapsiF, uudetIteraatioParametrit)
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
                                                      f: IteroiAmmatillisenTutkinnonYtoOsaAlueet,
                                                      lapsiF: Lukuarvofunktio with KloonattavaFunktio[BigDecimal, _, Funktio[BigDecimal]]
                                                     ): (Option[BigDecimal], List[Tila], Historia) = {
    val lapsiFunktio = lapsiF.asInstanceOf[KloonattavaFunktio[BigDecimal, (Lukuarvofunktio, Lukuarvofunktio), _]]
    if (lapsiFunktio.argumentit.length != 1) {
      throw new IllegalStateException(s"Odotettiin täsmälleen yhtä paria argumentiksi ${classOf[IteroiAmmatillisenTutkinnonYtoOsaAlueet].getSimpleName}-funktion lapselle," +
        s"mutta löytyi ${lapsiFunktio.argumentit.length} . Funktio == $f , Lapsifunktio == $lapsiFunktio")
    }
    val tutkinnonValitsija: AmmatillisenPerustutkinnonValitsija = ammatillisenTutkinnonValitsija(iteraatioParametrit, f)

    if (iteraatioParametrit.ammatillisenTutkinnonYtoOsaAlueenValitsija.isDefined) {
      throw new IllegalStateException(s"Ei voi iteroida iteraatioparametrilla ${iteraatioParametrit.ammatillisenTutkinnonYtoOsaAlueenValitsija} uudestaan ammatillisen tutkinnon osien yli")
    } else {
      val ytoKoodi = f.valintaperusteviite.tunniste
      val ytoOsaAlueet = KoskiLaskenta.haeAmmatillisenTutkinnonYtoOsaAlueet(tutkinnonValitsija, ytoKoodi, laskin.hakemus)

      Laskin.LOG.info(s"Hakemuksen ${laskin.hakemus.oid} hakijan tutkinnon $tutkinnonValitsija YTOlle $ytoKoodi löytyi ${ytoOsaAlueet.size} YTOn osa-aluetta.")

      val uudetParametrit: Seq[AmmatillisenTutkinnonYtoOsaAlueenValitsija] = AmmatillisenTutkinnonYtoOsaAlueet(ytoKoodi, ytoOsaAlueet).parametreiksi

      val kierrostenTulokset: Seq[(AmmatillisenTutkinnonYtoOsaAlueenValitsija, (Tulos[BigDecimal], Tulos[BigDecimal]))] = uudetParametrit.
        map(parametri => {
          val parametritLapsille = iteraatioParametrit.copy(ammatillisenTutkinnonYtoOsaAlueenValitsija = Some(parametri))
          val tulos1 = laskeLukuarvo(lapsiFunktio.argumentit.head._1, parametritLapsille)
          val tulos2 = laskeLukuarvo(lapsiFunktio.argumentit.head._2, parametritLapsille)

          (parametri, (tulos1, tulos2))
        })
      val tuloksetLukuarvoina: Seq[(Lukuarvo, Lukuarvo)] = kierrostenTulokset.flatMap {
        case (parametri, (Tulos(Some(lukuarvo1), _, historia1), Tulos(Some(lukuarvo2), _, historia2))) =>
          Laskin.LOG.info(s"Hakemuksen ${laskin.hakemus.oid} ${IteroiAmmatillisenTutkinnonYtoOsaAlueet.getClass.getSimpleName}-laskennan historia1 YTOlle ${ytoKoodi}: ${LaskentaUtil.prettyPrint(historia1)}")
          Laskin.LOG.info(s"Hakemuksen ${laskin.hakemus.oid} ${IteroiAmmatillisenTutkinnonYtoOsaAlueet.getClass.getSimpleName}-laskennan historia2 YTOlle ${ytoKoodi}: ${LaskentaUtil.prettyPrint(historia2)}")

          Some(
            Lukuarvo(lukuarvo1, tulosTekstiFi = s"Arvo 1 parametrilla '$parametri' == $lukuarvo1, historia: ${tiivistelmaAmmatillisistaFunktioista(historia1)}"),
            Lukuarvo(lukuarvo2, tulosTekstiFi = s"Arvo 2 parametrilla '$parametri' == $lukuarvo2, historia: ${tiivistelmaAmmatillisistaFunktioista(historia2)}"),
          )
        case (parametri, tulokset) =>
          Laskin.LOG.debug(s"Tyhjiä tuloksia hakemukselle ${laskin.hakemus.oid} joukossa $tulokset funktiosta $lapsiFunktio parametrilla $parametri")
          None
      }

      val tulos: Tulos[BigDecimal] = if (tuloksetLukuarvoina.nonEmpty) {
        try {
          val iteroidutTuloksetKasittelevaKlooni = lapsiFunktio.kloonaa(tuloksetLukuarvoina).asInstanceOf[Lukuarvofunktio]
          laskeLukuarvo(iteroidutTuloksetKasittelevaKlooni, LaskennanIteraatioParametrit())
        } catch {
          case e: ClassCastException =>
            Laskin.LOG.error(s"${classOf[IteroiAmmatillisenTutkinnonYtoOsaAlueet].getSimpleName} -funktion funktioargumenttina tulee olla " +
              s"kloonattava funktio, kuten maksimi, mutta oli $lapsiFunktio", e)
            throw e
        }
      } else {
        Tulos(None, new Hyvaksyttavissatila, Historia(ITEROIAMMATILLISETYTOOSAALUEET, None, Nil, None, None))
      }

      val tilalista = List(tulos.tila)
      val avaimet = Map(
        s"ammatillisen perustutkinnon ${tutkinnonValitsija.lyhytKuvaus} YTOn $ytoKoodi osa-alueiden määrä" -> Some(ytoOsaAlueet.size),
        s"ammatillisen perustutkinnon ${tutkinnonValitsija.lyhytKuvaus} YTOn $ytoKoodi osa-alueet" -> Some(uudetParametrit.map(_.kuvaus).mkString("; ")))
      (tulos.tulos, tilalista, Historia(ITEROIAMMATILLISETYTOOSAALUEET, tulos.tulos, tilalista, Some(kierrostenHistoriatKahdelleParametrille(kierrostenTulokset)), Some(avaimet)))
    }
  }

  private def tallennetutTuloksetHistoriaaVarten: ListMap[String, Option[String]] = {
    val tuloksetSuomenkielistenNimienKanssa: List[(String, Option[String])] = laskin.funktioTulokset.toList.map {
      case (avain, ft) => (s"$avain : ${ft.nimiFi}", Some(ft.arvo))
    }
    ListMap(tuloksetSuomenkielistenNimienKanssa : _*)
  }

  private def kierrostenHistoriat(kierrostenTulokset: Seq[(IteraatioParametri, Tulos[_])]): List[Historia] = {
    kierrostenTulokset.map(_._2.historia).toList
  }
  private def kierrostenHistoriatKahdelleParametrille(kierrostenTulokset: Seq[(IteraatioParametri, (Tulos[_], Tulos[_]))]): List[Historia] = {
    kierrostenTulokset.flatMap(x => List(x._2._1.historia, x._2._2.historia)).toList
  }
}
