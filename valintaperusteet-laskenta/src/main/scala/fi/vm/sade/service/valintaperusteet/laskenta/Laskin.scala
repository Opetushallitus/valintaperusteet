package fi.vm.sade.service.valintaperusteet.laskenta

import api._
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta._
import java.util.{Map => JMap}
import fi.vm.sade.service.valintaperusteet.laskenta.api.tila._
import org.slf4j.LoggerFactory
import scala.collection.mutable.ListBuffer
import com.codahale.jerkson.Json
import java.math.{BigDecimal => BigDec}
import java.math.RoundingMode
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.KonvertoiLukuarvo
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.Negaatio
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.Totuusarvo
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.Osamaara
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.Pyoristys
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.Suurempi
import scala.Some
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.Yhtasuuri
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.Jos
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.PienempiTaiYhtasuuri
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.HaeMerkkijonoJaKonvertoiTotuusarvoksi
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.Demografia
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.HaeMerkkijonoJaKonvertoiLukuarvoksi
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.Lukuarvo
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.Pienempi
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.NimettyTotuusarvo
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.HaeLukuarvo
import scala.Tuple3
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.NimettyLukuarvo
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.SyotettavaValintaperuste
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.SuurempiTaiYhtasuuri
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.HaeTotuusarvo
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.HaeMerkkijonoJaVertaaYhtasuuruus
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.HakukohteenValintaperuste
import scala.Tuple2
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.Ei
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.Hakutoive

object Laskin {
  val LOG = LoggerFactory.getLogger(classOf[Laskin])

  def suoritaLasku(hakukohde: Hakukohde,
                   hakemus: Hakemus,
                   laskettava: Lukuarvofunktio, historiaBuffer: StringBuffer): Laskentatulos[BigDec] = {

    new Laskin(hakukohde, hakemus).laske(laskettava) match {
      case Tulos(tulos, tila, historia) => {
        historiaBuffer.append(Json.generate(wrap(hakemus, historia)))
        if (tulos.isEmpty) new Laskentatulos[BigDec](tila, null, historiaBuffer) else new Laskentatulos[BigDec](tila, tulos.get.underlying, historiaBuffer)
      }
    }
  }

  def suoritaLasku(hakukohde: Hakukohde,
                   hakemus: Hakemus,
                   laskettava: Totuusarvofunktio, historiaBuffer: StringBuffer): Laskentatulos[java.lang.Boolean] = {
    new Laskin(hakukohde, hakemus).laske(laskettava) match {
      case Tulos(tulos, tila, historia) => {
        historiaBuffer.append(Json.generate(wrap(hakemus, historia)))
        new Laskentatulos[java.lang.Boolean](tila, if (!tulos.isEmpty) Boolean.box(tulos.get) else null, historiaBuffer)
      }
    }
  }

  def laske(hakukohde: Hakukohde, hakemus: Hakemus, laskettava: Totuusarvofunktio): (Option[Boolean], Tila) = {
    new Laskin(hakukohde, hakemus).laske(laskettava) match {
      case Tulos(tulos, tila, historia) => {
        LOG.debug("{}", Json.generate(wrap(hakemus, historia)))
        (tulos, tila)
      }
    }
  }

  def laske(hakukohde: Hakukohde, hakemus: Hakemus, laskettava: Lukuarvofunktio): (Option[BigDec], Tila) = {
    new Laskin(hakukohde, hakemus).laske(laskettava) match {
      case Tulos(tulos, tila, historia) => {
        LOG.debug("{}", Json.generate(wrap(hakemus, historia)))
        (if (tulos.isEmpty) None else Some(tulos.get.underlying), tila)
      }
    }
  }

  def wrap(hakemus: Hakemus, historia: Historia) = {
    val v: Map[String, Option[Any]] = hakemus.kentat.map(f => (f._1 -> Some(f._2)))

    val name = new StringBuffer().append("Laskenta hakemukselle (").append(hakemus.oid).append(")").toString
    Historia(name, historia.tulos, historia.tilat, Some(List(historia)), Some(v))
  }
}

case class Tulos[T](tulos: Option[T], tila: Tila, historia: Historia)

class Laskin(hakukohde: Hakukohde, hakemus: Hakemus) {

  private def ehdollinenTulos[A, B](tulos: (Option[A], Tila), f: (A, Tila) => Tuple2[Option[B], List[Tila]]): Tuple2[Option[B], List[Tila]] = {
    val (alkupTulos, alkupTila) = tulos
    alkupTulos match {
      case Some(t) => f(t, alkupTila)
      case None => (None, List(alkupTila))
    }
  }

  private def suoritaKonvertointi[S, T](tulos: Tuple2[Option[S], Tila],
                                        konvertteri: Konvertteri[S, T]) = {
    ehdollinenTulos[S, T](tulos, (t, tila) => {
      val (konvertoituTulos, konvertoituTila) = konvertteri.konvertoi(t)
      (konvertoituTulos, List(tila, konvertoituTila))
    })
  }

  private def suoritaOptionalKonvertointi[T](tulos: Tuple2[Option[T], Tila],
                                             konvertteri: Option[Konvertteri[T, T]]) = {
    ehdollinenTulos[T, T](tulos, (t, tila) => {
      konvertteri match {
        case Some(konv) => {
          val (konvertoituTulos, konvertoituTila) = konv.konvertoi(t)
          (konvertoituTulos, List(tila, konvertoituTila))
        }
        case None => (Some(t), List(tila))
      }
    })
  }

  private def haeValintaperuste(tunniste: String, pakollinen: Boolean, hakemus: Hakemus): (Option[String], Tila) = {
    hakemus.kentat.get(tunniste) match {
      case Some(s) if (!s.trim.isEmpty) => (Some(s), new Hyvaksyttavissatila)
      case _ => {
        val tila = if (pakollinen) {
          new Hylattytila("Pakollista arvoa (tunniste " + tunniste + ") ei " +
            "ole olemassa", new PakollinenValintaperusteHylkays(tunniste))
        } else new Hyvaksyttavissatila

        (None, tila)
      }
    }
  }

  private def haeValintaperuste[T](valintaperusteviite: Valintaperuste, hakemus: Hakemus,
                                   konv: (String => Tuple2[Option[T], List[Tila]]),
                                   oletusarvo: Option[T]): Tuple2[Option[T], List[Tila]] = {
    def haeValintaperusteenArvoHakemukselta(tunniste: String, pakollinen: Boolean) = {
      val (valintaperuste, tila) = haeValintaperuste(tunniste, pakollinen, hakemus)

      valintaperuste match {
        case Some(s) => konv(s)
        case None => (oletusarvo, List(tila))
      }
    }

    // Jos kyseessä on syötettävä valintaperuste, pitää ensin tsekata osallistumistieto
    valintaperusteviite match {
      case SyotettavaValintaperuste(tunniste, pakollinen, osallistuminenTunniste) => {
        val (osallistuminen, osallistumistila) = hakemus.kentat.get(osallistuminenTunniste) match {
          case Some(osallistuiArvo) => {
            try {
              (Osallistuminen.valueOf(osallistuiArvo), new Hyvaksyttavissatila)
            } catch {
              case e: IllegalArgumentException => (Osallistuminen.MERKITSEMATTA, new Virhetila("Osallistumistietoa "
                + osallistuiArvo + " ei pystytty tulkitsemaan (tunniste " + osallistuminenTunniste + ")",
                new OsallistumistietoaEiVoidaTulkitaVirhe(osallistuminenTunniste)))
            }
          }
          case None => (Osallistuminen.MERKITSEMATTA, new Hyvaksyttavissatila)
        }

        // Jos valintaperusteelle on merkitty arvo "ei osallistunut" tai sitä ei ole merkitty, palautetaan hylätty-tila,
        // jos kyseessä on pakollinen tieto

        if (pakollinen && Osallistuminen.EI_OSALLISTUNUT == osallistuminen) {
          (None, List(osallistumistila, new Hylattytila("Pakollisen syötettävän kentän arvo on '" + osallistuminen.name() + "' (tunniste "
            + tunniste + ")", new EiOsallistunutHylkays(tunniste))))
        } else if (pakollinen && Osallistuminen.MERKITSEMATTA == osallistuminen) {
          (None, List(osallistumistila, new Virhetila("Pakollisen syötettävän kentän arvo on merkitsemättä (tunniste "
            + tunniste + ")", new SyotettavaArvoMerkitsemattaVirhe(tunniste))))
        } else {
          val (arvo, tilat) = haeValintaperusteenArvoHakemukselta(tunniste, pakollinen)
          (arvo, osallistumistila :: tilat)
        }
      }
      case HakemuksenValintaperuste(tunniste, pakollinen) => haeValintaperusteenArvoHakemukselta(tunniste, pakollinen)
      case HakukohteenValintaperuste(tunniste) => {
        hakukohde.valintaperusteet.get(tunniste).filter(!_.trim.isEmpty).map(konv(_)).getOrElse(
          if (!oletusarvo.isEmpty) (oletusarvo, List(new Hyvaksyttavissatila))
          else {
            (None, List(new Virhetila("Hakukohteen valintaperustetta " + tunniste + " ei ole määritelty",
              new HakukohteenValintaperusteMaarittelemattaVirhe(tunniste))))
          })
      }
    }
  }


  private def string2boolean(s: String, tunniste: String, oletustila: Tila = new Hyvaksyttavissatila): (Option[Boolean], Tila) = {
    try {
      (Some(s.toBoolean), oletustila)
    } catch {
      case e: NumberFormatException => (None, new Virhetila("Arvoa " + s + " ei voida muuttaa "
        + "Boolean-tyyppiseksi (tunniste " + tunniste + ")",
        new ValintaperustettaEiVoidaTulkitaTotuusarvoksiVirhe(tunniste)))
    }
  }

  private def string2bigDecimal(s: String, tunniste: String, oletustila: Tila = new Hyvaksyttavissatila): (Option[BigDecimal], Tila) = {
    try {
      (Some(BigDecimal(s)), oletustila)
    } catch {
      case e: NumberFormatException => (None, new Virhetila("Arvoa " + s + " ei voida muuttaa "
        + "BigDecimal-tyyppiseksi (tunniste " + tunniste + ")",
        new ValintaperustettaEiVoidaTulkitaLukuarvoksiVirhe(tunniste)))
    }
  }


  def laske(laskettava: Totuusarvofunktio): Tulos[Boolean] = {

    def muodostaKoostettuTulos(fs: Seq[Totuusarvofunktio], trans: Seq[Boolean] => Boolean) = {
      val (tulokset, tilat, historiat) = fs.reverse.foldLeft((Nil, Nil, ListBuffer()): Tuple3[List[Option[Boolean]], List[Tila], ListBuffer[Historia]])((lst, f) => {
        laske(f) match {
          case Tulos(tulos, tila, historia) => {
            lst._3 += historia
            (tulos :: lst._1, tila :: lst._2, lst._3)
          }
        }
      })

      val (tyhjat, eiTyhjat) = tulokset.partition(_.isEmpty)

      // jos yksikin laskennasta saaduista arvoista on tyhjä, koko funktion laskenta palauttaa tyhjän
      val totuusarvo = if (!tyhjat.isEmpty) None else Some(trans(eiTyhjat.map(_.get)))
      (totuusarvo, tilat, Historia("Koostettu tulos", totuusarvo, tilat, Some(historiat.toList), None))
    }

    def muodostaYksittainenTulos(f: Totuusarvofunktio, trans: Boolean => Boolean) = {
      laske(f) match {
        case Tulos(tulos, tila, historia) => (tulos.map(trans(_)), List(tila), historia)
      }
    }

    def muodostaVertailunTulos(f1: Lukuarvofunktio, f2: Lukuarvofunktio,
                               trans: (BigDecimal, BigDecimal) => Boolean) = {
      val tulos1 = laske(f1)
      val tulos2 = laske(f2)
      val tulos = for {
        t1 <- tulos1.tulos
        t2 <- tulos2.tulos
      } yield trans(t1, t2)
      val tilat = List(tulos1.tila, tulos2.tila)
      (tulos, tilat, Historia("Vertailuntulos", tulos, tilat, Some(List(tulos1.historia, tulos2.historia)), None))
    }

    val (laskettuTulos, tilat, hist): Tuple3[Option[Boolean], List[Tila], Historia] = laskettava match {
      case Ja(fs, oid) => {
        val (tulos, tilat, h) = muodostaKoostettuTulos(fs, lst => lst.forall(b => b));
        (tulos, tilat, Historia("Ja", tulos, tilat, h.historiat, None))
      }
      case Tai(fs, oid) => {
        val (tulos, tilat, h) = muodostaKoostettuTulos(fs, lst => lst.exists(b => b));
        (tulos, tilat, Historia("Tai", tulos, tilat, h.historiat, None))
      }
      case Ei(fk, oid) => {
        val (tulos, tilat, h) = muodostaYksittainenTulos(fk, b => !b)
        (tulos, tilat, Historia("Ei", tulos, tilat, Some(List(h)), None))
      }
      case Totuusarvo(b, oid) => {
        val tilat = List(new Hyvaksyttavissatila)
        (Some(b), tilat, Historia("Totuusarvo", Some(b), tilat, None, None))
      }
      case Suurempi(f1, f2, oid) => {
        val (tulos, tilat, h) = muodostaVertailunTulos(f1, f2, (d1, d2) => d1 > d2)
        (tulos, tilat, Historia("Suurempi", tulos, tilat, Some(List(h)), None))
      }
      case SuurempiTaiYhtasuuri(f1, f2, oid) => {
        val (tulos, tilat, h) = muodostaVertailunTulos(f1, f2, (d1, d2) => d1 >= d2);
        (tulos, tilat, Historia("Suurempi tai yhtäsuuri", tulos, tilat, Some(List(h)), None))
      }
      case Pienempi(f1, f2, oid) => {
        val (tulos, tilat, h) = muodostaVertailunTulos(f1, f2, (d1, d2) => d1 < d2)
        (tulos, tilat, Historia("Pienempi", tulos, tilat, Some(List(h)), None))
      }
      case PienempiTaiYhtasuuri(f1, f2, oid) => {
        val (tulos, tilat, h) = muodostaVertailunTulos(f1, f2, (d1, d2) => d1 <= d2)
        (tulos, tilat, Historia("Pienempi tai yhtäsuuri", tulos, tilat, Some(List(h)), None))
      }
      case Yhtasuuri(f1, f2, oid) => {
        val (tulos, tilat, h) = muodostaVertailunTulos(f1, f2, (d1, d2) => d1 == d2)
        (tulos, tilat, Historia("Yhtäsuuri", tulos, tilat, Some(List(h)), None))
      }
      case HaeTotuusarvo(konvertteri, oletusarvo, valintaperusteviite, oid) => {
        val (tulos, tila) = haeValintaperuste[Boolean](valintaperusteviite, hakemus,
          (s => suoritaOptionalKonvertointi[Boolean](string2boolean(s, valintaperusteviite.tunniste),
            konvertteri)), oletusarvo)
        (tulos, tila, Historia("Hae totuusarvo", tulos, tila, None, Some(Map("oletusarvo" -> oletusarvo))))
      }
      case NimettyTotuusarvo(nimi, f, oid) => {
        val (tulos, tilat, h) = muodostaYksittainenTulos(f, b => b)
        (tulos, tilat, Historia("Nimetty totuusarvo", tulos, tilat, Some(List(h)), Some(Map("nimi" -> Some(nimi)))))
      }

      case Hakutoive(n, oid) => {
        val onko = Some(hakemus.onkoHakutoivePrioriteetilla(hakukohde.hakukohdeOid, n));
        val tilat = List(new Hyvaksyttavissatila)
        (onko, tilat, Historia("Hakutoive", onko, tilat, None, Some(Map("prioriteetti" -> Some(n)))))
      }

      case d: Demografia => {
        val avain = Esiprosessori.prosessointiOid(hakukohde.hakukohdeOid, hakemus, d)
        val (arvo, tila) = haeValintaperuste(avain, true, hakemus)

        val (demografia, t) = arvo match {
          case Some(s) => string2boolean(s, avain, tila)
          case None => (None, tila)
        }

        (demografia, List(t), Historia("Demografia", demografia, List(t), None, Some(Map("avain" -> Some(avain), "valintaperuste" -> arvo))))
      }

      case HaeMerkkijonoJaKonvertoiTotuusarvoksi(konvertteri, oletusarvo, valintaperusteviite, oid) => {
        val (tulos, tila) = haeValintaperuste[Boolean](valintaperusteviite, hakemus,
          (s => suoritaKonvertointi[String, Boolean]((Some(s), new Hyvaksyttavissatila), konvertteri)), oletusarvo)
        (tulos, tila, Historia("Hae merkkijono ja konvertoi totuusarvoksi", tulos, tila, None, Some(Map("oletusarvo" -> oletusarvo))))
      }

      case HaeMerkkijonoJaVertaaYhtasuuruus(oletusarvo, valintaperusteviite, vertailtava, oid) => {
        val (tulos, tila) = haeValintaperuste[Boolean](valintaperusteviite, hakemus,
          (s => (Some(s.trim.equalsIgnoreCase(vertailtava.trim)), List(new Hyvaksyttavissatila))), oletusarvo)
        (tulos, tila, Historia("Hae merkkijono ja vertaa yhtasuuruus", tulos, tila, None, Some(Map("oletusarvo" -> oletusarvo))))
      }
    }

    val palautettavaTila = tilat.filter(_ match {
      case _: Virhetila => true
      case _ => false
    }) match {
      case head :: tail => head
      case Nil => tilat.filter(_ match {
        case _: Hylattytila => true
        case _ => false
      }) match {
        case head :: tail => head
        case Nil => new Hyvaksyttavissatila
      }
    }

    Tulos(laskettuTulos, palautettavaTila, hist)
  }

  def laske(laskettava: Lukuarvofunktio): Tulos[BigDecimal] = {

    def summa(vals: Seq[BigDecimal]): BigDecimal = vals.reduceLeft(_ + _)

    def muodostaYksittainenTulos(f: Lukuarvofunktio, trans: BigDecimal => BigDecimal): (Option[BigDecimal], List[Tila], Historia) = {
      laske(f) match {
        case Tulos(tulos, tila, historia) => (tulos.map(trans(_)), List(tila), historia)
      }
    }

    def muodostaKoostettuTulos(fs: Seq[Lukuarvofunktio], trans: Seq[BigDecimal] => BigDecimal): (Option[BigDecimal], List[Tila], Historia) = {
      val tulokset = fs.reverse.foldLeft((Nil, Nil, ListBuffer()): Tuple3[List[BigDecimal], List[Tila], ListBuffer[Historia]])((lst, f) => {
        laske(f) match {
          case Tulos(tulos, tila, historia) => {
            lst._3 += historia
            (if (!tulos.isEmpty) tulos.get :: lst._1 else lst._1, tila :: lst._2, lst._3)
          }
        }
      })

      val lukuarvo = if (tulokset._1.isEmpty) None else Some(trans(tulokset._1))
      (lukuarvo, tulokset._2, Historia("Koostettu tulos", lukuarvo, tulokset._2, Some(tulokset._3.toList), None))
    }

    val (laskettuTulos: Option[BigDecimal], tilat: Seq[Tila], historia: Historia) = laskettava match {
      case Lukuarvo(d, oid) => {
        val tila = List(new Hyvaksyttavissatila)
        (Some(d), tila, Historia("Lukuarvo", Some(d), tila, None, None))
      }
      case Negaatio(n, oid) => {
        val (tulos, tilat, h) = muodostaYksittainenTulos(n, d => -d)
        (tulos, tilat, Historia("Negaaatio", tulos, tilat, Some(List(h)), None))
      }
      case Summa(fs, oid) => {
        val (tulos, tilat, h) = muodostaKoostettuTulos(fs, summa)
        (tulos, tilat, Historia("Summa", tulos, tilat, h.historiat, None))
      }

      case SummaNParasta(n, fs, oid) => {
        val (tulos, tilat, h) = muodostaKoostettuTulos(fs, ds => summa(ds.sortWith(_ > _).take(n)))
        (tulos, tilat, Historia("Summa N-parasta", tulos, tilat, h.historiat, Some(Map("n" -> Some(n)))))
      }

      case Osamaara(osoittaja, nimittaja, oid) => {
        val nimittajaTulos = laske(nimittaja)
        val osoittajaTulos = laske(osoittaja)

        val (arvo, laskentatilat) = (for {
          n <- nimittajaTulos.tulos
          o <- osoittajaTulos.tulos
        } yield {
          if (n == 0.0) (None, new Virhetila("Jako nollalla", new JakoNollallaVirhe))
          else {
            (Some(BigDecimal(o.underlying.divide(n.underlying, 4, RoundingMode.HALF_UP))), new Hyvaksyttavissatila)
          }
        }) match {
          case Some((arvo, tila)) => (arvo, List(tila))
          case None => (None, List())
        }

        val tilat = osoittajaTulos.tila :: nimittajaTulos.tila :: laskentatilat

        (arvo, tilat, Historia("Osamäärä", arvo, tilat, Some(List(osoittajaTulos.historia, nimittajaTulos.historia)), None))
      }

      case Tulo(fs, oid) => {
        val (tulos, tilat, h) = muodostaKoostettuTulos(fs, ds => ds.reduceLeft(_ * _))
        (tulos, tilat, Historia("Tulo", tulos, tilat, h.historiat, None))
      }

      case Keskiarvo(fs, oid) => {
        val (tulos, tilat, h) = muodostaKoostettuTulos(fs, ds => BigDecimal(summa(ds).underlying.divide(BigDecimal(ds.size).underlying, 4, RoundingMode.HALF_UP)))
        (tulos, tilat, Historia("Keskiarvo", tulos, tilat, h.historiat, None))
      }

      case KeskiarvoNParasta(n, fs, oid) => {
        val (tulos, tilat, h) = muodostaKoostettuTulos(fs, ds => {
          val kaytettavaN = scala.math.min(n, ds.size)
          BigDecimal(summa(ds.sortWith(_ > _).take(kaytettavaN)).underlying.divide(BigDecimal(kaytettavaN).underlying, 4, RoundingMode.HALF_UP))
        })
        (tulos, tilat, Historia("Keskiarvo N-parasta", tulos, tilat, h.historiat, Some(Map("n" -> Some(n)))))
      }
      case Minimi(fs, oid) => {
        val (tulos, tilat, h) = muodostaKoostettuTulos(fs, ds => ds.min)
        (tulos, tilat, Historia("Minimi", tulos, tilat, h.historiat, None))
      }

      case Maksimi(fs, oid) => {
        val (tulos, tilat, h) = muodostaKoostettuTulos(fs, ds => ds.max)
        (tulos, tilat, Historia("Maksimi", tulos, tilat, h.historiat, None))
      }

      case NMinimi(ns, fs, oid) => {
        val (tulos, tilat, h) = muodostaKoostettuTulos(fs, ds => ds.sortWith(_ < _)(scala.math.min(ns, ds.size) - 1))
        (tulos, tilat, Historia("N-minimi", tulos, tilat, h.historiat, Some(Map("ns" -> Some(ns)))))
      }

      case NMaksimi(ns, fs, oid) => {
        val (tulos, tilat, h) = muodostaKoostettuTulos(fs, ds => ds.sortWith(_ > _)(scala.math.min(ns, ds.size) - 1))
        (tulos, tilat, Historia("N-maksimi", tulos, tilat, h.historiat, Some(Map("ns" -> Some(ns)))))
      }

      case Mediaani(fs, oid) => {
        val (tulos, tilat, h) = muodostaKoostettuTulos(fs, ds => {
          val sorted = ds.sortWith(_ < _)
          if (sorted.size % 2 == 1) sorted(sorted.size / 2)
          else BigDecimal((sorted(sorted.size / 2) + sorted(sorted.size / 2 - 1)).underlying.divide(BigDecimal("2.0").underlying, 4, RoundingMode.HALF_UP))
        })
        (tulos, tilat, Historia("Mediaani", tulos, tilat, h.historiat, None))
      }

      case Jos(ehto, thenHaara, elseHaara, oid) => {
        val ehtoTulos = laske(ehto)
        val thenTulos = laske(thenHaara)
        val elseTulos = laske(elseHaara)
        //historiat :+ historia1 :+ historia2
        val (tulos, tilat) = ehdollinenTulos[Boolean, BigDecimal]((ehtoTulos.tulos, ehtoTulos.tila), (cond, tila) => {
          if (cond) (thenTulos.tulos, List(tila, thenTulos.tila)) else (elseTulos.tulos, List(tila, elseTulos.tila))
        })
        (tulos, tilat, Historia("Jos", tulos, tilat, Some(List(ehtoTulos.historia, thenTulos.historia, elseTulos.historia)), None))
      }

      case KonvertoiLukuarvo(konvertteri, f, oid) => {
        laske(f) match {
          case Tulos(tulos, tila, historia) => {
            val (tulos2, tilat2) = suoritaKonvertointi[BigDecimal, BigDecimal]((tulos, tila), konvertteri)
            (tulos2, tilat2, Historia("Konvertoitulukuarvo", tulos2, tilat2, Some(List(historia)), None))
          }
        }

      }

      case HaeLukuarvo(konvertteri, oletusarvo, valintaperusteviite, oid) => {
        val (tulos, tila) = haeValintaperuste[BigDecimal](valintaperusteviite, hakemus,
          (s => suoritaOptionalKonvertointi[BigDecimal](string2bigDecimal(s, valintaperusteviite.tunniste),
            konvertteri)), oletusarvo)
        (tulos, tila, Historia("Hae Lukuarvo", tulos, tila, None, Some(Map("oletusarvo" -> oletusarvo))))
      }
      case HaeMerkkijonoJaKonvertoiLukuarvoksi(konvertteri, oletusarvo, valintaperusteviite, oid) => {
        val (tulos, tila) = haeValintaperuste[BigDecimal](valintaperusteviite, hakemus,
          (s => suoritaKonvertointi[String, BigDecimal]((Some(s), new Hyvaksyttavissatila), konvertteri)), oletusarvo)
        (tulos, tila, Historia("Hae merkkijono ja konvertoi lukuarvoksi", tulos, tila, None, Some(Map("oletusarvo" -> oletusarvo))))
      }
      case NimettyLukuarvo(nimi, f, oid) => {
        val (tulos, tilat, h) = muodostaYksittainenTulos(f, d => d)
        (tulos, tilat, Historia("Nimetty lukuarvo", tulos, tilat, Some(List(h)), Some(Map("nimi" -> Some(nimi)))))
      }

      case Pyoristys(tarkkuus, f, oid) => {
        val (tulos, tilat, h) = muodostaYksittainenTulos(f, d => d.setScale(tarkkuus, BigDecimal.RoundingMode.HALF_UP))
        (tulos, tilat, Historia("Pyöristys", tulos, tilat, Some(List(h)), Some(Map("tarkkuus" -> Some(tarkkuus)))))
      }
      case Hylkaa(f, hylkaysperustekuvaus, oid) => {
        laske(f) match {
          case Tulos(tulos, tila, historia) => {
            val tila2 = tulos.map(b => if (b) new Hylattytila(hylkaysperustekuvaus.getOrElse("Hylätty hylkäämisfunktiolla"),
              new HylkaaFunktionSuorittamaHylkays)
            else new Hyvaksyttavissatila)
              .getOrElse(new Virhetila("Hylkäämisfunktion syöte on tyhjä. Hylkäystä ei voida tulkita.", new HylkaamistaEiVoidaTulkita))
            val tilat = List(tila, tila2)
            (None, tilat, Historia("Hylkää", None, tilat, Some(List(historia)), None))
          }
        }
      }
    }


    val palautettavaTila = tilat.filter(_ match {
      case _: Virhetila => true
      case _ => false
    }) match {
      case head :: tail => head
      case Nil => tilat.filter(_ match {
        case _: Hylattytila => true
        case _ => false
      }) match {
        case head :: tail => head
        case Nil => new Hyvaksyttavissatila
      }
    }

    Tulos(laskettuTulos, palautettavaTila, historia)
  }
}

