package fi.vm.sade.service.valintaperusteet.laskenta

import java.math.RoundingMode

import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.Arvokonvertteri
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.HaeAmmatillinenYtoArviointiAsteikko
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.HaeAmmatillinenYtoArvosana
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.HaeAmmatillisenTutkinnonKeskiarvo
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.HaeAmmatillisenTutkinnonOsanArvosana
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.HaeAmmatillisenTutkinnonOsanLaajuus
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.HaeAmmatillisenTutkinnonSuoritustapa
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.HaeAmmatillisenTutkinnonYtoOsaAlueenArvosana
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.HaeAmmatillisenTutkinnonYtoOsaAlueenLaajuus
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.HaeLukuarvo
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.HaeLukuarvoEhdolla
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.HaeMerkkijonoJaKonvertoiLukuarvoksi
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.HaeTotuusarvoJaKonvertoiLukuarvoksi
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.HaeYoArvosana
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.HaeYoPisteet
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.HakemuksenValintaperuste
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.Hylkaa
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.HylkaaArvovalilla
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.IteroiAmmatillisenTutkinnonYtoOsaAlueet
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.IteroiAmmatillisetTutkinnonOsat
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.IteroiAmmatillisetTutkinnot
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.Jos
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.Keskiarvo
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.KeskiarvoNParasta
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.KonvertoiLukuarvo
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.Konvertteri
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.Lukuarvo
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.Lukuarvovalikonvertteri
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.Maksimi
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.Mediaani
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.Minimi
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.NMaksimi
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.NMinimi
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.Negaatio
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.NimettyLukuarvo
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.Osamaara
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.PainotettuKeskiarvo
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.Pyoristys
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.Skaalaus
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.Summa
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.SummaNParasta
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.Tulo
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.TuloNParasta
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.Valintaperuste
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.YoEhdot
import fi.vm.sade.service.valintaperusteet.laskenta.api.Hakemus.Kentat
import fi.vm.sade.service.valintaperusteet.laskenta.api.tila.Hylattytila
import fi.vm.sade.service.valintaperusteet.laskenta.api.tila.HylkaaFunktionSuorittamaHylkays
import fi.vm.sade.service.valintaperusteet.laskenta.api.tila.HylkaamistaEiVoidaTulkita
import fi.vm.sade.service.valintaperusteet.laskenta.api.tila.Hyvaksyttavissatila
import fi.vm.sade.service.valintaperusteet.laskenta.api.tila.JakoNollallaVirhe
import fi.vm.sade.service.valintaperusteet.laskenta.api.tila.SkaalattavaArvoEiOleLahdeskaalassaVirhe
import fi.vm.sade.service.valintaperusteet.laskenta.api.tila.Tila
import fi.vm.sade.service.valintaperusteet.laskenta.api.tila.Tila.Tilatyyppi
import fi.vm.sade.service.valintaperusteet.laskenta.api.tila.TuloksiaLiianVahanLahdeskaalanMaarittamiseenVirhe
import fi.vm.sade.service.valintaperusteet.laskenta.api.tila.Virhetila

import scala.collection.mutable.ListBuffer
import scala.jdk.CollectionConverters._

protected[laskenta] class LukuarvoLaskin(protected val laskin: Laskin)
  extends LaskinFunktiot
    with IteraatioParametriFunktiot
    with AmmatillisetIterointiFunktiot
    with AmmatillisetArvonHakuFunktiot {

  private val ZERO: BigDecimal = BigDecimal("0.0")
  private val ONE: BigDecimal = BigDecimal("1.0")
  private val TWO: BigDecimal = BigDecimal("2.0")

  protected[laskenta] def laskeLukuarvo(laskettava: Lukuarvofunktio, iteraatioParametrit: Map[Class[_ <: IteraatioParametri], IteraatioParametri]): Tulos[BigDecimal] = {

    def summa(vals: Seq[BigDecimal]): BigDecimal = vals.reduceLeft(_ + _)
    def tulo(vals: Seq[BigDecimal]): BigDecimal = vals.reduceLeft(_ * _)

    def muodostaYksittainenTulos(f: Lukuarvofunktio, trans: BigDecimal => BigDecimal): (Option[BigDecimal], List[Tila], Historia) = {
      laskeLukuarvo(f, iteraatioParametrit) match {
        case Tulos(tulos, tila, historia) => (tulos.map(trans(_)), List(tila), historia)
      }
    }

    def onArvovalilla(arvo: BigDecimal, arvovali : (BigDecimal, BigDecimal), alarajaMukaan: Boolean, ylarajaMukaan: Boolean) = (alarajaMukaan, ylarajaMukaan) match {
      case (true, true) => if(arvo >= arvovali._1 && arvo <= arvovali._2) true else false
      case (true, false) => if(arvo >= arvovali._1 && arvo < arvovali._2) true else false
      case (false, true) => if(arvo > arvovali._1 && arvo <= arvovali._2) true else false
      case (false, false) => if(arvo > arvovali._1 && arvo < arvovali._2) true else false
    }

    def muodostaKoostettuTulos(fs: Seq[Lukuarvofunktio], trans: Seq[BigDecimal] => BigDecimal, ns: Option[Int] = None): (Option[BigDecimal], List[Tila], Historia) = {
      val tulokset = fs.reverse.foldLeft((Nil, Nil, ListBuffer()): (List[BigDecimal], List[Tila], ListBuffer[Historia]))((lst, f) => {
        laskeLukuarvo(f, iteraatioParametrit) match {
          case Tulos(tulos, tila, historia) =>
            lst._3 += historia
            (if (tulos.isDefined) tulos.get :: lst._1 else lst._1, tila :: lst._2, lst._3)
        }
      })

      val lukuarvo = if (tulokset._1.isEmpty || (ns.isDefined && tulokset._1.size < ns.get)) None else Some(trans(tulokset._1))
      (lukuarvo, tulokset._2, Historia("Koostettu tulos", lukuarvo, tulokset._2, Some(tulokset._3.toList), None))
    }

    def haeLukuarvo(konvertteri: Option[Konvertteri[BigDecimal, BigDecimal]], oletusarvo: Option[BigDecimal], valintaperusteviite: Valintaperuste, kentat: Kentat): (Option[BigDecimal], Seq[Tila], Historia)  = {
      val (konv, _) = konvertteri match {
        case Some(l: Lukuarvovalikonvertteri) => konversioToLukuarvovalikonversio(l.konversioMap,kentat, laskin.hakukohde)
        case Some(a: Arvokonvertteri[_,_]) => konversioToArvokonversio[BigDecimal, BigDecimal](a.konversioMap,kentat, laskin.hakukohde)
        case _ => (konvertteri, List())
      }
      val (tulos, tila) = laskin.haeValintaperuste[BigDecimal](valintaperusteviite, kentat,
        s => suoritaOptionalKonvertointi[BigDecimal](string2bigDecimal(s, valintaperusteviite.tunniste), konv), oletusarvo)
      (tulos, tila, Historia("Hae Lukuarvo", tulos, tila, None, Some(Map("oletusarvo" -> oletusarvo))))

    }

    def haeMerkkijonoJaKonvertoiLukuarvoksi(konvertteri: Konvertteri[String, BigDecimal], oletusarvo: Option[BigDecimal], valintaperusteviite: Valintaperuste, kentat: Kentat): (Option[BigDecimal], Seq[Tila], Historia)  = {
      if (kentat.isEmpty && oletusarvo.isDefined) {
        val tila: Tila = if (valintaperusteviite.pakollinen) new Hylattytila else new Hyvaksyttavissatila
        (oletusarvo, List(tila), Historia("Hae merkkijono ja konvertoi lukuarvoksi", oletusarvo, List(tila), None, Some(Map("oletusarvo" -> oletusarvo))))
      } else {
        konvertteri match {
          case a: Arvokonvertteri[_, _] =>
            val (konv, virheet) = konversioToArvokonversio(a.konversioMap, kentat, laskin.hakukohde)
            if (konv.isEmpty) {
              (None, virheet, Historia("Hae merkkijono ja konvertoi lukuarvoksi", None, virheet, None, None))
            } else {
              val (tulos, tila) = laskin.haeValintaperuste[BigDecimal](valintaperusteviite, kentat,
                s => suoritaKonvertointi[String, BigDecimal]((Some(s), new Hyvaksyttavissatila), konv.get.asInstanceOf[Arvokonvertteri[String, BigDecimal]]), oletusarvo)

              (tulos, tila, Historia("Hae merkkijono ja konvertoi lukuarvoksi", tulos, tila, None, Some(Map("oletusarvo" -> oletusarvo))))
            }
          case _ =>
            val (tulos, tila) = laskin.haeValintaperuste[BigDecimal](valintaperusteviite, kentat,
              s => suoritaKonvertointi[String, BigDecimal]((Some(s), new Hyvaksyttavissatila), konvertteri), oletusarvo)
            (tulos, tila, Historia("Hae merkkijono ja konvertoi lukuarvoksi", tulos, tila, None, Some(Map("oletusarvo" -> oletusarvo))))
        }
      }
    }

    def filteredSuoritusTiedot(valintaperusteviite: Valintaperuste, ehdot: YoEhdot): List[Kentat] = {
      laskin.hakemus.metatiedot.getOrElse(valintaperusteviite.tunniste, List()).filter(kentat =>
          (ehdot.alkuvuosi.isEmpty ||  string2integer(kentat.get("SUORITUSVUOSI"), 9999) >= ehdot.alkuvuosi.get) &&
          (ehdot.loppuvuosi.isEmpty ||  string2integer(kentat.get("SUORITUSVUOSI"), 0) <= ehdot.loppuvuosi.get) &&
          (ehdot.alkulukukausi.isEmpty || (ehdot.alkuvuosi.isDefined &&
             (string2integer(kentat.get("SUORITUSVUOSI"), 0) > ehdot.alkuvuosi.get ||
              string2integer(kentat.get("SUORITUSLUKUKAUSI"), 0) >= ehdot.alkulukukausi.get))) &&
          (ehdot.loppulukukausi.isEmpty || (ehdot.loppuvuosi.isDefined &&
             (string2integer(kentat.get("SUORITUSVUOSI"), 9999) < ehdot.loppuvuosi.get ||
              string2integer(kentat.get("SUORITUSLUKUKAUSI"), 3) <= ehdot.loppulukukausi.get))) &&
          (ehdot.rooli.isEmpty || ehdot.rooli.contains(kentat.getOrElse("ROOLI", "")))
      )
    }

    val (laskettuTulos: Option[BigDecimal], tilat: Seq[Tila], historia: Historia) = laskettava match {
      case Lukuarvo(d, _, _,_,_,_,_) =>
        val tila = List(new Hyvaksyttavissatila)
        (Some(d), tila, Historia("Lukuarvo", Some(d), tila, None, None))
      case Negaatio(n, _, _,_,_,_,_) =>
        val (tulos, tilat, h) = muodostaYksittainenTulos(n, d => -d)
        (tulos, tilat, Historia("Negaaatio", tulos, tilat, Some(List(h)), None))
      case Summa(fs, _, _,_,_,_,_) =>
        val (tulos, tilat, h) = muodostaKoostettuTulos(fs, summa)
        (tulos, tilat, Historia("Summa", tulos, tilat, h.historiat, None))

      case SummaNParasta(n, fs, _, _,_,_,_,_) =>
        val (tulos, tilat, h) = muodostaKoostettuTulos(fs, ds => summa(ds.sortWith(_ > _).take(n)))
        (tulos, tilat, Historia("Summa N-parasta", tulos, tilat, h.historiat, Some(Map("n" -> Some(n)))))

      case TuloNParasta(n, fs, _, _,_,_,_,_) =>
        val (tulos, tilat, h) = muodostaKoostettuTulos(fs, ds => tulo(ds.sortWith(_ > _).take(n)))
        (tulos, tilat, Historia("Tulo N-parasta", tulos, tilat, h.historiat, Some(Map("n" -> Some(n)))))

      case Osamaara(osoittaja, nimittaja, _, _,_,_,_,_) =>
        val nimittajaTulos = laskeLukuarvo(nimittaja, iteraatioParametrit)
        val osoittajaTulos = laskeLukuarvo(osoittaja, iteraatioParametrit)

        val (arvo, laskentatilat) = (for {
          n <- nimittajaTulos.tulos
          o <- osoittajaTulos.tulos
        } yield {
          if (n.intValue == 0) (None, new Virhetila(suomenkielinenHylkaysperusteMap("Jako nollalla"), new JakoNollallaVirhe))
          else {
            (Some(BigDecimal(o.underlying.divide(n.underlying, 4, RoundingMode.HALF_UP))), new Hyvaksyttavissatila)
          }
        }) match {
          case Some((arvo, tila)) => (arvo, List(tila))
          case None => (None, List())
        }

        val tilat = osoittajaTulos.tila :: nimittajaTulos.tila :: laskentatilat
        (arvo, tilat, Historia("Osamäärä", arvo, tilat, Some(List(osoittajaTulos.historia, nimittajaTulos.historia)), None))

      case Tulo(fs, _, _,_,_,_,_) =>
        val (tulos, tilat, h) = muodostaKoostettuTulos(fs, ds => ds.product)
        (tulos, tilat, Historia("Tulo", tulos, tilat, h.historiat, None))

      case Keskiarvo(fs, _, _,_,_,_,_) =>
        val (tulos, tilat, h) = muodostaKoostettuTulos(fs, ds => BigDecimal(summa(ds).underlying.divide(BigDecimal(ds.size).underlying, 4, RoundingMode.HALF_UP)))
        (tulos, tilat, Historia("Keskiarvo", tulos, tilat, h.historiat, None))

      case KeskiarvoNParasta(n, fs, _, _,_,_,_,_) =>
        val (tulos, tilat, h) = muodostaKoostettuTulos(fs, ds => {
          val kaytettavaN = scala.math.min(n, ds.size)
          BigDecimal(summa(ds.sortWith(_ > _).take(kaytettavaN)).underlying.divide(BigDecimal(kaytettavaN).underlying, 4, RoundingMode.HALF_UP))
        })
        (tulos, tilat, Historia("Keskiarvo N-parasta", tulos, tilat, h.historiat, Some(Map("n" -> Some(n)))))
      case Minimi(fs, _, _,_,_,_,_) =>
        val (tulos, tilat, h) = muodostaKoostettuTulos(fs, ds => ds.min)
        (tulos, tilat, Historia("Minimi", tulos, tilat, h.historiat, None))

      case Maksimi(fs, _, _,_,_,_,_) =>
        val (tulos, tilat, h) = muodostaKoostettuTulos(fs, ds => ds.max)
        (tulos, tilat, Historia("Maksimi", tulos, tilat, h.historiat, None))

      case NMinimi(ns, fs, _, _,_,_,_,_) =>
        val (tulos, tilat, h) = muodostaKoostettuTulos(fs, ds => ds.sortWith(_ < _)(scala.math.min(ns, ds.size) - 1), Some(ns))
        (tulos, tilat, Historia("N-minimi", tulos, tilat, h.historiat, Some(Map("ns" -> Some(ns)))))

      case NMaksimi(ns, fs, _, _,_,_,_,_) =>
        val (tulos, tilat, h) = muodostaKoostettuTulos(fs, ds => ds.sortWith(_ > _)(scala.math.min(ns, ds.size) - 1), Some(ns))
        (tulos, tilat, Historia("N-maksimi", tulos, tilat, h.historiat, Some(Map("ns" -> Some(ns)))))

      case Mediaani(fs, _, _,_,_,_,_) =>
        val (tulos, tilat, h) = muodostaKoostettuTulos(fs, ds => {
          val sorted = ds.sortWith(_ < _)
          if (sorted.size % 2 == 1) sorted(sorted.size / 2)
          else BigDecimal((sorted(sorted.size / 2) + sorted(sorted.size / 2 - 1)).underlying.divide(TWO.underlying, 4, RoundingMode.HALF_UP))
        })
        (tulos, tilat, Historia("Mediaani", tulos, tilat, h.historiat, None))

      case Jos(ehto, thenHaara, elseHaara, _, _,_,_,_,_) =>
        val ehtoTulos = new TotuusarvoLaskin(this.laskin).laskeTotuusarvo(ehto, iteraatioParametrit)
        val thenTulos = laskeLukuarvo(thenHaara, iteraatioParametrit)
        val elseTulos = laskeLukuarvo(elseHaara, iteraatioParametrit)
        //historiat :+ historia1 :+ historia2
        val (tulos, tilat) = ehdollinenTulos[Boolean, BigDecimal]((ehtoTulos.tulos, ehtoTulos.tila), (cond, tila) => {
          if (cond) (thenTulos.tulos, List(tila, thenTulos.tila)) else (elseTulos.tulos, List(tila, elseTulos.tila))
        })
        (tulos, tilat, Historia("Jos", tulos, tilat, Some(List(ehtoTulos.historia, thenTulos.historia, elseTulos.historia)), None))

      case KonvertoiLukuarvo(konvertteri, f, _, _,_,_,_,_) =>
        laskeLukuarvo(f, iteraatioParametrit) match {
          case Tulos(tulos, tila, historia) =>
            val (konv, virheet) = konvertteri match {
              case l: Lukuarvovalikonvertteri => konversioToLukuarvovalikonversio(l.konversioMap, laskin.hakemus.kentat, laskin.hakukohde)
              case a: Arvokonvertteri[_,_] => konversioToArvokonversio[BigDecimal, BigDecimal](a.konversioMap, laskin.hakemus.kentat, laskin.hakukohde)
              case _ => (Some(konvertteri), List())
            }
            if(konv.isEmpty) {
              (None, virheet, Historia("Konvertoitulukuarvo", None, virheet, Some(List(historia)), None))
            } else {
              val (tulos2, tilat2) = suoritaKonvertointi[BigDecimal, BigDecimal]((tulos, tila), konv.get)
              (tulos2, tilat2, Historia("Konvertoitulukuarvo", tulos2, tilat2, Some(List(historia)), None))
            }
        }

      case IteroiAmmatillisetTutkinnot(f, _, _, _, _, _, _) => iteroiAmmatillisetTutkinnot(iteraatioParametrit, f)

      case f@IteroiAmmatillisetTutkinnonOsat(lapsiF, _, _, _, _, _, _) if lapsiF.argumentit.isInstanceOf[Seq[(Lukuarvofunktio, Lukuarvofunktio)]] =>
        iteroiAmmatillisetTutkinnonOsat(iteraatioParametrit, f, lapsiF)

      case f@IteroiAmmatillisetTutkinnonOsat(lapsiFunktio, _, _, _, _, _, _) =>
        val virheilmoitus = s"${classOf[IteroiAmmatillisetTutkinnonOsat].getSimpleName} -funktion $f funktioargumentin $lapsiFunktio ottaman argumentin tyyppi on " +
          s"${lapsiFunktio.argumentit.getClass} , jolle ei ole toteutettu käsittelyä"
        Laskin.LOG.error(virheilmoitus)
        throw new UnsupportedOperationException(virheilmoitus)

      case f@IteroiAmmatillisenTutkinnonYtoOsaAlueet(lapsiF, _, _, _, _, _, _, _) => iteroiAmmatillisenTutkinnonYtoOsaAlueet(iteraatioParametrit, f, lapsiF)

      case f@HaeAmmatillinenYtoArvosana(konvertteri, oletusarvo, valintaperusteviite, _, _,_,_,_,_) =>
        haeAmmatillinenYtoArvosana(iteraatioParametrit, f, konvertteri, oletusarvo, valintaperusteviite)

      case f@HaeAmmatillinenYtoArviointiAsteikko(konvertteri, oletusarvo, valintaperusteviite, _, _,_,_,_,_) =>
        haeAmmatillinenYtoArviointiAsteikko(iteraatioParametrit, f, konvertteri, oletusarvo, valintaperusteviite)

      case f@HaeAmmatillisenTutkinnonOsanLaajuus(konvertteri, oletusarvo, _, _,_,_,_,_) =>
        haeAmmatillisenTutkinnonOsanLaajuus(iteraatioParametrit, f, konvertteri, oletusarvo)

      case f@HaeAmmatillisenTutkinnonOsanArvosana(konvertteri, _, _,_,_,_,_) =>
        haeAmmatillisenTutkinnonOsanArvosana(iteraatioParametrit, f, konvertteri)

      case f@HaeAmmatillisenTutkinnonYtoOsaAlueenLaajuus(konvertteri, oletusarvo, _, _,_,_,_,_) =>
        haeAmmatillisenTutkinnonYtoOsaAlueenLaajuus(iteraatioParametrit, f, konvertteri, oletusarvo)

      case f@HaeAmmatillisenTutkinnonYtoOsaAlueenArvosana(konvertteri, oletusarvo, _, _,_,_,_,_) =>
        haeAmmatillisenTutkinnonYtoOsaAlueenArvosana(iteraatioParametrit, f, konvertteri, oletusarvo)

      case f@HaeAmmatillisenTutkinnonKeskiarvo(konvertteri, _, _,_,_,_,_) =>
        haeAmmatillisenTutkinnonKeskiarvo(iteraatioParametrit, f, konvertteri)

      case f@HaeAmmatillisenTutkinnonSuoritustapa(konvertteri, oletusarvo, _, _,_,_,_,_) =>
        haeAmmatillisenTutkinnonSuoritustapa(iteraatioParametrit, f, konvertteri, oletusarvo)

      case HaeLukuarvo(konvertteri, oletusarvo, valintaperusteviite, _, _,_,_,_,_) =>
        haeLukuarvo(konvertteri, oletusarvo, valintaperusteviite, laskin.hakemus.kentat)

      case HaeYoPisteet(konvertteri, ehdot, oletusarvo, valintaperusteviite, _, _,_,_,_,_) =>
        val suoritukset = filteredSuoritusTiedot(valintaperusteviite, ehdot)
        val sortedValues = suoritukset.sortWith((kentat1, kentat2) =>
          string2integer(kentat1.get("PISTEET"), 0) > string2integer(kentat2.get("PISTEET"), 0)
        )
        haeLukuarvo(konvertteri, oletusarvo, HakemuksenValintaperuste("PISTEET", valintaperusteviite.pakollinen), sortedValues.headOption.getOrElse(Map()))

      case HaeLukuarvoEhdolla(konvertteri, oletusarvo, valintaperusteviite, ehto, _, _,_,_,_,_) =>
        val tayttyy = ehtoTayttyy(ehto.tunniste, laskin.hakemus.kentat)

        val (konv, _) = konvertteri match {
          case Some(l: Lukuarvovalikonvertteri) => konversioToLukuarvovalikonversio(l.konversioMap, laskin.hakemus.kentat, laskin.hakukohde)
          case Some(a: Arvokonvertteri[_,_]) => konversioToArvokonversio[BigDecimal, BigDecimal](a.konversioMap, laskin.hakemus.kentat, laskin.hakukohde)
          case _ => (konvertteri, List())
        }

        val vp = if(tayttyy) valintaperusteviite else HakemuksenValintaperuste("", pakollinen = false)

        val (tulos, tila) = laskin.haeValintaperuste[BigDecimal](vp, laskin.hakemus.kentat,
          s => suoritaOptionalKonvertointi[BigDecimal](string2bigDecimal(s, vp.tunniste),
            konv), oletusarvo)
        (tulos, tila, Historia("Hae Lukuarvo Ehdolla", tulos, tila, None, Some(Map("oletusarvo" -> oletusarvo))))

      case HaeMerkkijonoJaKonvertoiLukuarvoksi(konvertteri, oletusarvo, valintaperusteviite, _, _,_,_,_,_) =>
        haeMerkkijonoJaKonvertoiLukuarvoksi(konvertteri, oletusarvo, valintaperusteviite, laskin.hakemus.kentat)

      case HaeYoArvosana(konvertteri, ehdot, oletusarvo, valintaperusteviite, _, _,_,_,_,_) =>
        val suoritukset = filteredSuoritusTiedot(valintaperusteviite, ehdot)
        val sortedValues = suoritukset.sortWith((kentat1, kentat2) =>
          ehdot.YO_ORDER.indexOf(kentat1.getOrElse("ARVO", "I")) < ehdot.YO_ORDER.indexOf(kentat2.getOrElse("ARVO", "I"))
        )
        haeMerkkijonoJaKonvertoiLukuarvoksi(konvertteri, oletusarvo, HakemuksenValintaperuste("ARVO", valintaperusteviite.pakollinen), sortedValues.headOption.getOrElse(Map()))

      case HaeTotuusarvoJaKonvertoiLukuarvoksi(konvertteri, oletusarvo, valintaperusteviite, _, _,_,_,_,_) =>
        konvertteri match {
          case a: Arvokonvertteri[_,_] =>
            val (konv, virheet) = konversioToArvokonversio(a.konversioMap, laskin.hakemus.kentat, laskin.hakukohde)
            if(konv.isEmpty) {
              (None, virheet, Historia("Hae totuusarvo ja konvertoi lukuarvoksi", None, virheet, None, None))
            } else {
              val (tulos, tila) = laskin.haeValintaperuste[BigDecimal](valintaperusteviite, laskin.hakemus.kentat,
                s => suoritaKonvertointi[Boolean, BigDecimal](string2boolean(s, valintaperusteviite.tunniste, new Hyvaksyttavissatila), konv.get.asInstanceOf[Arvokonvertteri[Boolean,BigDecimal]]), oletusarvo)

              (tulos, tila, Historia("Hae totuusarvo ja konvertoi lukuarvoksi", tulos, tila, None, Some(Map("oletusarvo" -> oletusarvo))))
            }
          case _ =>
            val (tulos, tila) = laskin.haeValintaperuste[BigDecimal](valintaperusteviite, laskin.hakemus.kentat,
              s => suoritaKonvertointi[Boolean, BigDecimal](string2boolean(s, valintaperusteviite.tunniste, new Hyvaksyttavissatila), konvertteri), oletusarvo)
            (tulos, tila, Historia("Hae totuusarvo ja konvertoi lukuarvoksi", tulos, tila, None, Some(Map("oletusarvo" -> oletusarvo))))
        }


      case NimettyLukuarvo(nimi, f, _, _,_,_,_,_) =>
        val (tulos, tilat, h) = muodostaYksittainenTulos(f, d => d)
        (tulos, tilat, Historia("Nimetty lukuarvo", tulos, tilat, Some(List(h)), Some(Map("nimi" -> Some(nimi)))))

      case Pyoristys(tarkkuus, f, _, _,_,_,_,_) =>
        val (tulos, tilat, h) = muodostaYksittainenTulos(f, d => d.setScale(tarkkuus, BigDecimal.RoundingMode.HALF_UP))
        (tulos, tilat, Historia("Pyöristys", tulos, tilat, Some(List(h)), Some(Map("tarkkuus" -> Some(tarkkuus)))))
      case Hylkaa(f, hylkaysperustekuvaus, _, _,_,_,_,_) =>
        new TotuusarvoLaskin(laskin).laskeTotuusarvo(f, iteraatioParametrit) match {
          case Tulos(tulos, tila, historia) =>
            val tila2 = tulos.map(b => if (b) new Hylattytila(hylkaysperustekuvaus.getOrElse(Map.empty[String,String]).asJava,
              new HylkaaFunktionSuorittamaHylkays)
            else new Hyvaksyttavissatila)
              .getOrElse(new Virhetila(suomenkielinenHylkaysperusteMap("Hylkäämisfunktion syöte on tyhjä. Hylkäystä ei voida tulkita."), new HylkaamistaEiVoidaTulkita))
            val tilat = List(tila, tila2)
            (None, tilat, Historia("Hylkää", None, tilat, Some(List(historia)), None))
        }
      case HylkaaArvovalilla(f, hylkaysperustekuvaus, _, _,_,_,_,_, (min,max)) =>
        laskeLukuarvo(f, iteraatioParametrit) match {
          case Tulos(tulos, tila, historia) =>
            val arvovali = haeArvovali((min, max), laskin.hakukohde, laskin.hakemus.kentat)
            if(tulos.isEmpty && tila.getTilatyyppi.equals(Tilatyyppi.HYVAKSYTTAVISSA)) {
              val virheTila = new Virhetila(suomenkielinenHylkaysperusteMap("Hylkäämisfunktion syöte on tyhjä. Hylkäystä ei voida tulkita."), new HylkaamistaEiVoidaTulkita)
              (None, List(virheTila), Historia("Hylkää Arvovälillä", None, List(virheTila), Some(List(historia)), None))
            } else if(arvovali.isEmpty) {
              val virheTila = new Virhetila(suomenkielinenHylkaysperusteMap("Arvovalin arvoja ei voida muuntaa lukuarvoiksi"), new HylkaamistaEiVoidaTulkita)
              (None, List(virheTila), Historia("Hylkää Arvovälillä", None, List(virheTila), Some(List(historia)), None))
            } else {
              val arvovaliTila = tulos.map(arvo => if(onArvovalilla(arvo, (arvovali.get._1,arvovali.get._2), alarajaMukaan = true, ylarajaMukaan = false)) new Hylattytila(hylkaysperustekuvaus.getOrElse(Map.empty[String,String]).asJava,
                new HylkaaFunktionSuorittamaHylkays) else new Hyvaksyttavissatila).getOrElse(new Hyvaksyttavissatila)

              val tilat = List(tila, arvovaliTila)
              (tulos, tilat, Historia("Hylkää Arvovälillä", tulos, tilat, Some(List(historia)), None))
            }
        }
      case Skaalaus(_, _,_,_,_,_, skaalattava, (kohdeMin, kohdeMax), lahdeskaala) =>
        if (laskin.laskentamoodi != Laskentamoodi.VALINTALASKENTA) {
          val moodi = laskin.laskentamoodi.toString
          moodiVirhe(s"Skaalaus-funktiota ei voida suorittaa laskentamoodissa $moodi", "Skaalaus", moodi)
        } else {
          val tulos = laskeLukuarvo(skaalattava, iteraatioParametrit)

          def skaalaa(skaalattavaArvo: BigDecimal,
                      kohdeskaala: (BigDecimal, BigDecimal),
                      lahdeskaala: (BigDecimal, BigDecimal)) = {
            val lahdeRange = lahdeskaala._2 - lahdeskaala._1
            val kohdeRange = kohdeskaala._2 - kohdeskaala._1
            (((skaalattavaArvo - lahdeskaala._1) * kohdeRange) / lahdeRange) + kohdeskaala._1
          }

          val (skaalauksenTulos, tila) = tulos.tulos match {
            case Some(skaalattavaArvo) =>
              lahdeskaala match {
                case Some((lahdeMin, lahdeMax)) =>
                  if (!onArvovalilla(skaalattavaArvo, (lahdeMin, lahdeMax), alarajaMukaan = true, ylarajaMukaan = true)) {
                    (None, new Virhetila(suomenkielinenHylkaysperusteMap(s"Arvo ${skaalattavaArvo.toString} ei ole arvovälillä ${lahdeMin.toString} - ${lahdeMax.toString}"),
                      new SkaalattavaArvoEiOleLahdeskaalassaVirhe(skaalattavaArvo.underlying, lahdeMin.underlying, lahdeMax.underlying)))
                  } else {
                    val skaalattuArvo = skaalaa(skaalattavaArvo, (kohdeMin, kohdeMax), (lahdeMin, lahdeMax))
                    (Some(skaalattuArvo), new Hyvaksyttavissatila)
                  }
                case None =>
                  val tulokset = laskin.kaikkiHakemukset.map(h => {
                    Option( Laskin.suoritaValintalaskenta(laskin.hakukohde, h, laskin.kaikkiHakemukset.asJava, skaalattava).getTulos)
                  }).filter(_.isDefined).map(_.get)

                  tulokset match {
                    case _ if tulokset.size < 2 => (None, new Virhetila(suomenkielinenHylkaysperusteMap("Skaalauksen lähdeskaalaa ei voida määrittää laskennallisesti. " +
                      "Tuloksia on vähemmän kuin 2 kpl tai kaikki tulokset ovat samoja."), new TuloksiaLiianVahanLahdeskaalanMaarittamiseenVirhe))
                    case _ =>
                      val lahdeSkaalaMin: BigDecimal = tulokset.min
                      val lahdeSkaalaMax: BigDecimal = tulokset.max

                      // Koska tulokset ovat setissä, tiedetään, että min ja max eivät koskaan voi olla yhtäsuuret
                      (Some(skaalaa(skaalattavaArvo, (kohdeMin, kohdeMax), (lahdeSkaalaMin, lahdeSkaalaMax))), new Hyvaksyttavissatila)
                  }
              }
            case None => (None, new Hyvaksyttavissatila)
          }

          val tilat = List(tila, tulos.tila)
          (skaalauksenTulos, tilat, Historia("Skaalaus", skaalauksenTulos, tilat, Some(List(tulos.historia)), None))
        }

      case PainotettuKeskiarvo(_, _,_,_,_,_, fs) =>
        val tulokset = fs.map(p => Tuple2(laskeLukuarvo(p._1, iteraatioParametrit), laskeLukuarvo(p._2, iteraatioParametrit)))
        val (tilat, historiat) = tulokset.reverse.foldLeft(Tuple2(List[Tila](), List[Historia]()))((lst, t) => Tuple2(t._1.tila :: t._2.tila :: lst._1, t._1.historia :: t._2.historia :: lst._2))

        val painokertointenSumma = tulokset.foldLeft(ZERO) {
          (s, a) =>
            val painokerroin = a._1.tulos
            val painotettava = a._2.tulos

            s + (painotettava match {
              case Some(_) if painokerroin.isEmpty => ONE
              case Some(_) => painokerroin.get
              case None => ZERO
            })
        }

        val painotettuSumma = tulokset.foldLeft(ZERO)((s, a) => s + a._1.tulos.getOrElse(ONE) * a._2.tulos.getOrElse(ZERO))

        val painotettuKeskiarvo = if(painokertointenSumma.intValue == 0) None else Some(BigDecimal(painotettuSumma.underlying.divide(painokertointenSumma.underlying, 4, RoundingMode.HALF_UP)))

        (painotettuKeskiarvo, tilat, Historia("Painotettu keskiarvo", painotettuKeskiarvo, tilat, Some(historiat), None))
    }
    if (!laskettava.tulosTunniste.isEmpty) {
      val v = FunktioTulos(
        laskettava.tulosTunniste,
        laskettuTulos.getOrElse("").toString,
        laskettava.tulosTekstiFi,
        laskettava.tulosTekstiSv,
        laskettava.tulosTekstiEn,
        laskettava.omaopintopolku
      )
      laskin.funktioTulokset.update(laskettava.tulosTunniste, v)
    }
    Tulos(laskettuTulos, palautettavaTila(tilat), historia)
  }

  protected def tiivistelmaAmmatillisistaFunktioista(historia: Historia): Seq[String] = {
    historianTiivistelma(historia, h => Funktionimi.ammatillistenArvosanojenFunktionimet.asScala.map(_.name).contains(h.funktio))
  }
}
