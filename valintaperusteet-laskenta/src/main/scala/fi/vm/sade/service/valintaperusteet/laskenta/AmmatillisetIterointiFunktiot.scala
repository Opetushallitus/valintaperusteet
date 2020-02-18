package fi.vm.sade.service.valintaperusteet.laskenta

import fi.vm.sade.kaava.LaskentaUtil
import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi.ITEROIAMMATILLISETOSAT
import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi.ITEROIAMMATILLISETTUTKINNOT
import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi.ITEROIAMMATILLISETYTOOSAALUEET
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.IteroiAmmatillisenTutkinnonYtoOsaAlueet
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.IteroiAmmatillisetTutkinnonOsat
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.IteroiAmmatillisetTutkinnot
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

  protected def iteroiAmmatillisetTutkinnot(iteraatioParametrit: Map[Class[_ <: IteraatioParametri], IteraatioParametri],
                                            f: Lukuarvofunktio with Laskenta.KoostavaFunktio[BigDecimal] with KloonattavaFunktio[BigDecimal, Lukuarvofunktio, Laskenta.KoostavaFunktio[BigDecimal]]
                                           ): (Option[BigDecimal], List[Tila], Historia) = {
    val ammatillisenPerustutkinnonValitsija = iteraatioParametrit.get(classOf[AmmatillisenPerustutkinnonValitsija])
    if (ammatillisenPerustutkinnonValitsija.exists(p => p.isInstanceOf[AmmatillisenPerustutkinnonValitsija])) {
      throw new IllegalStateException(s"Ei voi iteroida iteraatioparametrilla $ammatillisenPerustutkinnonValitsija uudestaan ammatillisten tutkintojen yli")
    } else {
      val tutkinnot: Seq[Tutkinto] = KoskiLaskenta.etsiAmmatillisetTutkinnot(laskin.hakemus)
      val tutkintojenMaara = tutkinnot.size
      Laskin.LOG.info(s"Hakemuksen ${laskin.hakemus.oid} hakijalle löytyi $tutkintojenMaara ammatillista perustutkintoa.")

      val tutkintojenIterointiParametrit: Seq[AmmatillisenPerustutkinnonValitsija] = AmmatillisetPerustutkinnot(tutkinnot).parametreiksi

      val kierrostenTulokset: Seq[(AmmatillisenPerustutkinnonValitsija, Tulos[BigDecimal])] = tutkintojenIterointiParametrit.
        map(parametri => (parametri, laskeLukuarvo(f, iteraatioParametrit ++ Map(classOf[AmmatillisenPerustutkinnonValitsija] -> parametri))))
      val tuloksetLukuarvoina: Seq[Lukuarvo] = kierrostenTulokset.flatMap {
        case (parametri, Tulos(Some(lukuarvo), _, historia)) =>
          Laskin.LOG.info(s"Hakemuksen ${laskin.hakemus.oid} ${IteroiAmmatillisetTutkinnot.getClass.getSimpleName}-laskennan historia: ${LaskentaUtil.prettyPrint(historia)}")
          val ammatillisenHistorianTiivistelma: Seq[String] = tiivistelmaAmmatillisistaFunktioista(historia)

          Some(Lukuarvo(lukuarvo, tulosTekstiFi = s"Arvo parametrilla '${parametri.kuvaus}' == $lukuarvo, historia: $ammatillisenHistorianTiivistelma"))
        case (parametri, tulos) =>
          Laskin.LOG.debug(s"Tyhjä tulos $tulos funktiosta $f parametrilla $parametri")
          None
      }

      val tiivistelmaLista = kierrostenTulokset.flatMap {
        case (parametri, Tulos(Some(lukuarvo), _, _)) =>
          Map(s"${classOf[IteroiAmmatillisetTutkinnot].getSimpleName} parametrilla ${parametri.lyhytKuvaus}" -> Some(lukuarvo))
        case _ => Map()
      }
      val ammatillistenFunktioidenTulostenTiivistelmat: ListMap[String, Option[Any]] = tallennetutTuloksetHistoriaaVarten ++ ListMap(tiivistelmaLista : _*)

      val tulos: Tulos[BigDecimal] = if (tuloksetLukuarvoina.nonEmpty) {
        try {
          val iteroidutTuloksetKasittelevaKlooni = f.kloonaa(tuloksetLukuarvoina).asInstanceOf[Lukuarvofunktio]
          laskeLukuarvo(iteroidutTuloksetKasittelevaKlooni, Map())
        } catch {
          case e: ClassCastException =>
            Laskin.LOG.error(s"${classOf[IteroiAmmatillisetTutkinnot].getSimpleName} -funktion funktioargumenttina tulee olla " +
              s"kloonattava funktio, kuten maksimi, mutta oli $f", e)
            throw e
        }
      } else {
        Tulos(None, new Hyvaksyttavissatila, Historia(ITEROIAMMATILLISETTUTKINNOT, None, Nil, None, None))
      }

      val tilalista = List(tulos.tila)
      val avaimet = ListMap(
        "Ammatillisten perustutkintojen määrä" -> Some(tutkintojenMaara),
        "Ammatilliset perustutkinnot" -> Some(tutkintojenIterointiParametrit.map(_.kuvaus).mkString("; "))) ++
        ammatillistenFunktioidenTulostenTiivistelmat
      (tulos.tulos, tilalista, Historia(ITEROIAMMATILLISETTUTKINNOT, tulos.tulos, tilalista, Some(kierrostenHistoriat(kierrostenTulokset)), Some(avaimet)))
    }
  }

  protected def iteroiAmmatillisetTutkinnonOsat(iteraatioParametrit: Map[Class[_ <: IteraatioParametri], IteraatioParametri],
                                                f: IteroiAmmatillisetTutkinnonOsat,
                                                lapsiF: Lukuarvofunktio with KloonattavaFunktio[BigDecimal, _, Funktio[BigDecimal]]
                                               ): (Option[BigDecimal], List[Tila], Historia) = {
    val lapsiFunktio = lapsiF.asInstanceOf[KloonattavaFunktio[BigDecimal, (Lukuarvofunktio, Lukuarvofunktio), _]]
    if (lapsiFunktio.argumentit.length != 1) {
      throw new IllegalStateException(s"Odotettiin täsmälleen yhtä paria argumentiksi ${classOf[IteroiAmmatillisetTutkinnonOsat].getSimpleName}-funktion lapselle," +
        s"mutta löytyi ${lapsiFunktio.argumentit.length} . Funktio == $f , Lapsifunktio == $lapsiFunktio")
    }
    val tutkinnonValitsija: AmmatillisenPerustutkinnonValitsija = ammatillisenTutkinnonValitsija(iteraatioParametrit, f)

    val tutkinnonOsanValitsija = iteraatioParametrit.get(classOf[AmmatillisenTutkinnonOsanValitsija])
    if (tutkinnonOsanValitsija.exists(p => p.isInstanceOf[AmmatillisenTutkinnonOsanValitsija])) {
      throw new IllegalStateException(s"Ei voi iteroida iteraatioparametrilla $tutkinnonOsanValitsija uudestaan ammatillisen tutkinnon osien yli")
    } else {
      val tutkinnonOsat: Seq[Osasuoritus] = KoskiLaskenta.etsiAmmatillisenTutkinnonOsat(tutkinnonValitsija, laskin.hakemus)
      val osienMaara = tutkinnonOsat.size
      Laskin.LOG.info(s"Hakemuksen ${laskin.hakemus.oid} hakijan tutkinnolle $tutkinnonValitsija löytyi $osienMaara ammatillista perustutkinnon osaa.")

      val tutkinnonOsienIterointiParametrit: Seq[AmmatillisenTutkinnonOsanValitsija] = AmmatillisenTutkinnonOsat(tutkinnonOsat).parametreiksi

      val kierrostenTulokset: Seq[(AmmatillisenTutkinnonOsanValitsija, (Tulos[BigDecimal], Tulos[BigDecimal]))] = tutkinnonOsienIterointiParametrit.
        map(parametri => {
          val parametritLapsille = iteraatioParametrit ++ Map(classOf[AmmatillisenTutkinnonOsanValitsija] -> parametri)
          val tulos1 = laskeLukuarvo(lapsiFunktio.argumentit.head._1, parametritLapsille)
          val tulos2 = laskeLukuarvo(lapsiFunktio.argumentit.head._2, parametritLapsille)

          (parametri, (tulos1, tulos2))
        })
      val tuloksetLukuarvoina: Seq[(Lukuarvo, Lukuarvo)] = kierrostenTulokset.flatMap {
        case (parametri, (Tulos(Some(lukuarvo1), _, historia1), Tulos(Some(lukuarvo2), _, historia2))) =>
          Laskin.LOG.info(s"Hakemuksen ${laskin.hakemus.oid} ${IteroiAmmatillisetTutkinnonOsat.getClass.getSimpleName}-laskennan historia1: ${LaskentaUtil.prettyPrint(historia1)}")
          Laskin.LOG.info(s"Hakemuksen ${laskin.hakemus.oid} ${IteroiAmmatillisetTutkinnonOsat.getClass.getSimpleName}-laskennan historia2: ${LaskentaUtil.prettyPrint(historia2)}")

          Some(
            Lukuarvo(lukuarvo1, tulosTekstiFi = s"Arvo 1 parametrilla '$parametri' == $lukuarvo1, historia: ${tiivistelmaAmmatillisistaFunktioista(historia1)}"),
            Lukuarvo(lukuarvo2, tulosTekstiFi = s"Arvo 2 parametrilla '$parametri' == $lukuarvo2, historia: ${tiivistelmaAmmatillisistaFunktioista(historia2)}"),
          )
        case (parametri, tulokset) =>
          Laskin.LOG.debug(s"Tyhjiä tuloksia joukossa $tulokset funktiosta $lapsiFunktio parametrilla $parametri")
          None
      }

      val tulos: Tulos[BigDecimal] = if (tuloksetLukuarvoina.nonEmpty) {
        try {
          val iteroidutTuloksetKasittelevaKlooni = lapsiFunktio.kloonaa(tuloksetLukuarvoina).asInstanceOf[Lukuarvofunktio]
          laskeLukuarvo(iteroidutTuloksetKasittelevaKlooni, Map())
        } catch {
          case e: ClassCastException =>
            Laskin.LOG.error(s"${classOf[IteroiAmmatillisetTutkinnonOsat].getSimpleName} -funktion funktioargumenttina tulee olla " +
              s"kloonattava funktio, kuten maksimi, mutta oli $lapsiFunktio", e)
            throw e
        }
      } else {
        Tulos(None, new Hyvaksyttavissatila, Historia(ITEROIAMMATILLISETOSAT, None, Nil, None, None))
      }

      val tilalista = List(tulos.tila)
      val avaimet = Map(
        "ammatillisen perustutkinnon osien määrä" -> Some(osienMaara),
        "ammatillisen perustutkinnon osat" -> Some(tutkinnonOsienIterointiParametrit.map(_.kuvaus).mkString("; ")))
      (tulos.tulos, tilalista, Historia(ITEROIAMMATILLISETOSAT, tulos.tulos, tilalista, Some(kierrostenHistoriatKahdelleParametrille(kierrostenTulokset)), Some(avaimet)))
    }
  }

  protected def iteroiAmmatillisenTutkinnonYtoOsaAlueet(iteraatioParametrit: Map[Class[_ <: IteraatioParametri], IteraatioParametri],
                                                      f: IteroiAmmatillisenTutkinnonYtoOsaAlueet,
                                                      lapsiF: Lukuarvofunktio with KloonattavaFunktio[BigDecimal, _, Funktio[BigDecimal]]
                                                     ): (Option[BigDecimal], List[Tila], Historia) = {
    val lapsiFunktio = lapsiF.asInstanceOf[KloonattavaFunktio[BigDecimal, (Lukuarvofunktio, Lukuarvofunktio), _]]
    if (lapsiFunktio.argumentit.length != 1) {
      throw new IllegalStateException(s"Odotettiin täsmälleen yhtä paria argumentiksi ${classOf[IteroiAmmatillisenTutkinnonYtoOsaAlueet].getSimpleName}-funktion lapselle," +
        s"mutta löytyi ${lapsiFunktio.argumentit.length} . Funktio == $f , Lapsifunktio == $lapsiFunktio")
    }
    val tutkinnonValitsija: AmmatillisenPerustutkinnonValitsija = ammatillisenTutkinnonValitsija(iteraatioParametrit, f)

    val tutkinnonYtoOsaAlueenValitsija = iteraatioParametrit.get(classOf[AmmatillisenTutkinnonYtoOsaAlueenValitsija])
    if (tutkinnonYtoOsaAlueenValitsija.exists(p => p.isInstanceOf[AmmatillisenTutkinnonYtoOsaAlueenValitsija])) {
      throw new IllegalStateException(s"Ei voi iteroida iteraatioparametrilla $tutkinnonYtoOsaAlueenValitsija uudestaan ammatillisen tutkinnon osien yli")
    } else {
      val ytoKoodi = f.valintaperusteviite.tunniste
      val ytonOsaAlueidenMaara = KoskiLaskenta.laskeAmmatillisenTutkinnonYtoOsaAlueet(tutkinnonValitsija, ytoKoodi, laskin.hakemus)
      Laskin.LOG.info(s"Hakemuksen ${laskin.hakemus.oid} hakijan tutkinnon $tutkinnonValitsija YTOlle $ytoKoodi löytyi $ytonOsaAlueidenMaara YTOn osa-aluetta.")

      val uudetParametrit: Seq[AmmatillisenTutkinnonYtoOsaAlueenValitsija] = AmmatillisenTutkinnonYtoOsaAlueet(ytoKoodi, ytonOsaAlueidenMaara).parametreiksi

      val kierrostenTulokset: Seq[(AmmatillisenTutkinnonYtoOsaAlueenValitsija, (Tulos[BigDecimal], Tulos[BigDecimal]))] = uudetParametrit.
        map(parametri => {
          val parametritLapsille = iteraatioParametrit ++ Map(classOf[AmmatillisenTutkinnonYtoOsaAlueenValitsija] -> parametri)
          val tulos1 = laskeLukuarvo(lapsiFunktio.argumentit.head._1, parametritLapsille)
          val tulos2 = laskeLukuarvo(lapsiFunktio.argumentit.head._2, parametritLapsille)

          (parametri, (tulos1, tulos2))
        })
      val tuloksetLukuarvoina: Seq[(Lukuarvo, Lukuarvo)] = kierrostenTulokset.flatMap {
        case (parametri, (Tulos(Some(lukuarvo1), _, historia1), Tulos(Some(lukuarvo2), _, historia2))) =>
          Laskin.LOG.info(s"Hakemuksen ${laskin.hakemus.oid} ${IteroiAmmatillisenTutkinnonYtoOsaAlueet.getClass.getSimpleName}-laskennan historia1: ${LaskentaUtil.prettyPrint(historia1)}")
          Laskin.LOG.info(s"Hakemuksen ${laskin.hakemus.oid} ${IteroiAmmatillisenTutkinnonYtoOsaAlueet.getClass.getSimpleName}-laskennan historia2: ${LaskentaUtil.prettyPrint(historia2)}")

          Some(
            Lukuarvo(lukuarvo1, tulosTekstiFi = s"Arvo 1 parametrilla '$parametri' == $lukuarvo1, historia: ${tiivistelmaAmmatillisistaFunktioista(historia1)}"),
            Lukuarvo(lukuarvo2, tulosTekstiFi = s"Arvo 2 parametrilla '$parametri' == $lukuarvo2, historia: ${tiivistelmaAmmatillisistaFunktioista(historia2)}"),
          )
        case (parametri, tulokset) =>
          Laskin.LOG.debug(s"Tyhjiä tuloksia joukossa $tulokset funktiosta $lapsiFunktio parametrilla $parametri")
          None
      }

      val tulos: Tulos[BigDecimal] = if (tuloksetLukuarvoina.nonEmpty) {
        try {
          val iteroidutTuloksetKasittelevaKlooni = lapsiFunktio.kloonaa(tuloksetLukuarvoina).asInstanceOf[Lukuarvofunktio]
          laskeLukuarvo(iteroidutTuloksetKasittelevaKlooni, Map())
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
        s"ammatillisen perustutkinnon YTOn $ytoKoodi osa-alueiden määrä" -> Some(ytonOsaAlueidenMaara),
        s"ammatillisen perustutkinnon YTOn $ytoKoodi pisteet" -> Some(tuloksetLukuarvoina.map(l => s"${l._1.tulosTekstiFi} = ${l._1.d};${l._2.tulosTekstiFi} = ${l._2.d}")))
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
