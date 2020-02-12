package fi.vm.sade.service.valintaperusteet.laskenta

import fi.vm.sade.kaava.LaskentaUtil
import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.IteroiAmmatillisenTutkinnonYtoOsaAlueet
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.IteroiAmmatillisetTutkinnonOsat
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.IteroiAmmatillisetTutkinnot
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.KloonattavaFunktio
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.Lukuarvo
import fi.vm.sade.service.valintaperusteet.laskenta.api.tila.Hyvaksyttavissatila
import fi.vm.sade.service.valintaperusteet.laskenta.api.tila.Tila
import fi.vm.sade.service.valintaperusteet.laskenta.koski.KoskiLaskenta

import scala.jdk.CollectionConverters._

trait AmmatillisetIterointiFunktiot {
  this: LukuarvoLaskin =>

  protected def iteroiAmmatillisetTutkinnot(iteraatioParametrit: Map[Class[_ <: IteraatioParametri], IteraatioParametri],
                                            f: Lukuarvofunktio with Laskenta.KoostavaFunktio[BigDecimal] with KloonattavaFunktio[BigDecimal, Lukuarvofunktio, Laskenta.KoostavaFunktio[BigDecimal]]
                                           ): (Option[BigDecimal], List[Tila], Historia) = {
    val ammatillisenPerustutkinnonValitsija = iteraatioParametrit.get(classOf[AmmatillisenPerustutkinnonValitsija])
    if (ammatillisenPerustutkinnonValitsija.exists(p => p.isInstanceOf[AmmatillisenPerustutkinnonValitsija])) {
      throw new IllegalStateException(s"Ei voi iteroida iteraatioparametrilla $ammatillisenPerustutkinnonValitsija uudestaan ammatillisten tutkintojen yli")
    } else {
      val tutkintojenMaara = KoskiLaskenta.laskeAmmatillisetTutkinnot(laskin.hakemus)
      Laskin.LOG.info(s"Hakemuksen ${laskin.hakemus.oid} hakijalle löytyi ${tutkintojenMaara} ammatillista perustutkintoa.")

      val uudetParametrit: Seq[AmmatillisenPerustutkinnonValitsija] = AmmatillisetPerustutkinnot(tutkintojenMaara).parametreiksi

      val kierrostenTulokset: Seq[(AmmatillisenPerustutkinnonValitsija, Tulos[BigDecimal])] = uudetParametrit.
        map(parametri => (parametri, laskeLukuarvo(f, iteraatioParametrit ++ Map(classOf[AmmatillisenPerustutkinnonValitsija] -> parametri))))
      val tuloksetLukuarvoina: Seq[Lukuarvo] = kierrostenTulokset.flatMap {
        case (parametri, Tulos(Some(lukuarvo), _, historia)) =>
          Laskin.LOG.info(s"Hakemuksen ${laskin.hakemus.oid} ${IteroiAmmatillisetTutkinnot.getClass.getSimpleName}-laskennan historia: ${LaskentaUtil.prettyPrint(historia)}")
          val ammatillisenHistorianTiivistelma: Seq[String] = tiivistelmaAmmatillisistaFunktioista(historia)

          Some(Lukuarvo(lukuarvo, tulosTekstiFi = s"Arvo parametrilla '$parametri' == $lukuarvo, historia: $ammatillisenHistorianTiivistelma"))
        case (parametri, tulos) =>
          Laskin.LOG.debug(s"Tyhjä tulos $tulos funktiosta $f parametrilla $parametri")
          None
      }

      val ammatillistenFunktioidenTulostenTiivistelmat: Map[String, Option[Any]] = kierrostenTulokset.flatMap {
        case (parametri, Tulos(Some(lukuarvo), _, historia)) =>
          val tiivistelmat: Map[String, Option[Any]] = historia.
            flatten.
            filter { h: Historia =>
              Funktionimi.ammatillistenArvosanojenFunktionimet.asScala.map(_.name()).contains(h.funktio)
            }.
            map { h =>
              s"${h.funktio} = ${h.tulos.getOrElse("-")}" -> Some(s"avaimet: ${h.avaimet.getOrElse(Map()).map(x => (x._1, x._2.getOrElse("-")))}")
            }.toMap
          Map(s"Arvo parametrilla '$parametri'" -> Some(lukuarvo)) ++ tiivistelmat
        case _ => Map()
      }.toMap

      val tulos: Tulos[BigDecimal] = if (tuloksetLukuarvoina.nonEmpty) {
        try {
          val iteroidutTuloksetKasittelevaKlooni = f.kloonaa(tuloksetLukuarvoina).asInstanceOf[Lukuarvofunktio]
          laskeLukuarvo(iteroidutTuloksetKasittelevaKlooni, Map())
        } catch {
          case e: ClassCastException => {
            Laskin.LOG.error(s"${classOf[IteroiAmmatillisetTutkinnot].getSimpleName} -funktion funktioargumenttina tulee olla " +
              s"kloonattava funktio, kuten maksimi, mutta oli $f", e)
            throw e
          }
        }
      } else {
        Tulos(None, new Hyvaksyttavissatila, Historia("Ei löytynyt tietoja ammatillisista tutkinnoista", None, Nil, None, None))
      }

      val tilalista = List(tulos.tila)
      val avaimet = Map(
        "Ammatillisten perustutkintojen määrä" -> Some(tutkintojenMaara)) ++
        ammatillistenFunktioidenTulostenTiivistelmat
      (tulos.tulos, tilalista, Historia("Iteroi ammatillisten perustutkintojen yli", tulos.tulos, tilalista, None, Some(avaimet)))
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
      val osienMaara = KoskiLaskenta.laskeAmmatillisenTutkinnonOsat(tutkinnonValitsija, laskin.hakemus)
      Laskin.LOG.info(s"Hakemuksen ${laskin.hakemus.oid} hakijan tutkinnolle ${tutkinnonValitsija} löytyi ${osienMaara} ammatillista perustutkinnon osaa.")

      val uudetParametrit: Seq[AmmatillisenTutkinnonOsanValitsija] = AmmatillisenTutkinnonOsat(osienMaara).parametreiksi

      val kierrostenTulokset: Seq[(AmmatillisenTutkinnonOsanValitsija, (Tulos[BigDecimal], Tulos[BigDecimal]))] = uudetParametrit.
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
          case e: ClassCastException => {
            Laskin.LOG.error(s"${classOf[IteroiAmmatillisetTutkinnonOsat].getSimpleName} -funktion funktioargumenttina tulee olla " +
              s"kloonattava funktio, kuten maksimi, mutta oli $lapsiFunktio", e)
            throw e
          }
        }
      } else {
        Tulos(None, new Hyvaksyttavissatila, Historia("Ei löytynyt tietoja ammatillisista tutkinnoista", None, Nil, None, None))
      }

      val tilalista = List(tulos.tila)
      val avaimet = Map(
        "ammatillisen perustutkinnon osien määrä" -> Some(osienMaara),
        "ammatillisen perustutkinnon osien pisteet" -> Some(tuloksetLukuarvoina.map(l => s"${l._1.tulosTekstiFi} = ${l._1.d};${l._2.tulosTekstiFi} = ${l._2.d}")))
      (tulos.tulos, tilalista, Historia("Iteroi ammatillisen perustutkinnon osien yli", tulos.tulos, tilalista, None, Some(avaimet)))
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
      val osienMaara = KoskiLaskenta.laskeAmmatillisenTutkinnonYtoOsaAlueet(tutkinnonValitsija, laskin.hakemus)
      Laskin.LOG.info(s"Hakemuksen ${laskin.hakemus.oid} hakijan tutkinnolle ${tutkinnonValitsija} löytyi ${osienMaara} ammatillista perustutkinnon osaa.")

      val uudetParametrit: Seq[AmmatillisenTutkinnonYtoOsaAlueenValitsija] = AmmatillisenTutkinnonYtoOsaAlueet(f.valintaperusteviite.tunniste, osienMaara).parametreiksi

      val kierrostenTulokset: Seq[(AmmatillisenTutkinnonYtoOsaAlueenValitsija, (Tulos[BigDecimal], Tulos[BigDecimal]))] = uudetParametrit.
        map(parametri => {
          val parametritLapsille = iteraatioParametrit ++ Map(classOf[AmmatillisenTutkinnonYtoOsaAlueenValitsija] -> parametri)
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
          case e: ClassCastException => {
            Laskin.LOG.error(s"${classOf[IteroiAmmatillisenTutkinnonYtoOsaAlueet].getSimpleName} -funktion funktioargumenttina tulee olla " +
              s"kloonattava funktio, kuten maksimi, mutta oli $lapsiFunktio", e)
            throw e
          }
        }
      } else {
        Tulos(None, new Hyvaksyttavissatila, Historia("Ei löytynyt tietoja ammatillisista tutkinnoista", None, Nil, None, None))
      }

      val tilalista = List(tulos.tila)
      val avaimet = Map(
        "ammatillisen perustutkinnon osien määrä" -> Some(osienMaara),
        "ammatillisen perustutkinnon osien pisteet" -> Some(tuloksetLukuarvoina.map(l => s"${l._1.tulosTekstiFi} = ${l._1.d};${l._2.tulosTekstiFi} = ${l._2.d}")))
      (tulos.tulos, tilalista, Historia("Iteroi ammatillisen perustutkinnon osien yli", tulos.tulos, tilalista, None, Some(avaimet)))
    }
  }
}
