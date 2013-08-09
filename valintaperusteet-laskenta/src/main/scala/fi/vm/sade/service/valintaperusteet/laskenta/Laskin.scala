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
import scala.Tuple2
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.Ei
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.Hakutoive
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.Valintaperusteviite

object Laskin {
  val LOG = LoggerFactory.getLogger(classOf[Laskin])

  def suoritaLasku(hakukohde: String,
                   hakemus: Hakemus,
                   laskettava: Lukuarvofunktio, historiaBuffer: StringBuffer): Laskentatulos[BigDec] = {

    val (tulos, tila, historia) = new Laskin(hakukohde, hakemus).laske(laskettava)

    historiaBuffer.append(Json.generate(wrap(hakemus, historia)))
    if (tulos.isEmpty) new Laskentatulos[BigDec](tila, null) else new Laskentatulos[BigDec](tila, tulos.get.underlying)
  }

  def suoritaLasku(hakukohde: String,
                   hakemus: Hakemus,
                   laskettava: Totuusarvofunktio, historiaBuffer: StringBuffer): Laskentatulos[java.lang.Boolean] = {
    val (tulos, tila, historia) = new Laskin(hakukohde, hakemus).laske(laskettava)

    historiaBuffer.append(Json.generate(wrap(hakemus, historia)))
    new Laskentatulos[java.lang.Boolean](tila, if (!tulos.isEmpty) Boolean.box(tulos.get) else null)
  }

  def laske(hakukohde: String, hakemus: Hakemus, laskettava: Totuusarvofunktio): (Option[Boolean], Tila) = {
    val (tulos, tila, historia) = new Laskin(hakukohde, hakemus).laske(laskettava)
    LOG.debug("{}", Json.generate(wrap(hakemus, historia)))
    (tulos, tila)
  }

  def laske(hakukohde: String, hakemus: Hakemus, laskettava: Lukuarvofunktio): (Option[BigDec], Tila) = {
    val (tulos, tila, historia) = new Laskin(hakukohde, hakemus).laske(laskettava)
    LOG.debug("{}", Json.generate(wrap(hakemus, historia)))
    (if (tulos.isEmpty) None else Some(tulos.get.underlying), tila)
  }

  def wrap(hakemus: Hakemus, historia: Historia) = {
    val v: Map[String, Option[Any]] = hakemus.kentat.map(f => (f._1 -> Some(f._2)))

    val name = new StringBuffer().append("Laskenta hakemukselle (").append(hakemus.oid).append(")").toString
    Historia(name, historia.tulos, historia.tilat, Some(List(historia)), Some(v))
  }
}

class Laskin(hakukohde: String, hakemus: Hakemus) {

  private def ehdollinenTulos[A, B](tulos: (Option[A], Tila), f: (A, Tila) => Tuple2[Option[B], List[Tila]]): Tuple2[Option[B], List[Tila]] = {
    val (alkupTulos, alkupTila) = tulos
    alkupTulos match {
      case Some(t) => f(t, alkupTila)
      case None => (None, List(alkupTila))
    }
  }

  private def suoritaKonvertointi[S, T](oid: String,
                                        tulos: Tuple2[Option[S], Tila],
                                        konvertteri: Konvertteri[S, T]) = {
    ehdollinenTulos[S, T](tulos, (t, tila) => {
      val (konvertoituTulos, konvertoituTila) = konvertteri.konvertoi(oid, t)
      (Some(konvertoituTulos), List(tila, konvertoituTila))
    })
  }

  private def suoritaOptionalKonvertointi[T](oid: String,
                                             tulos: Tuple2[Option[T], Tila],
                                             konvertteri: Option[Konvertteri[T, T]]) = {
    ehdollinenTulos[T, T](tulos, (t, tila) => {
      konvertteri match {
        case Some(konv) => {
          val (konvertoituTulos, konvertoituTila) = konv.konvertoi(oid, t)
          (Some(konvertoituTulos), List(tila, konvertoituTila))
        }
        case None => (Some(t), List(tila))
      }
    })
  }

  private def haeValintaperuste[T](oid: String, valintaperusteviite: Valintaperusteviite, hakemus: Hakemus,
                                   konv: (String => Tuple2[Option[T], List[Tila]]),
                                   oletusarvo: Option[T]): Tuple2[Option[T], List[Tila]] = {
    def haeValintaperusteenArvoHakemukselta = {
      hakemus.kentat.get(valintaperusteviite.tunniste) match {
        case Some(s) if (!s.trim.isEmpty) => konv(s)
        case _ => {
          val tila = if (valintaperusteviite.pakollinen) {
            new Hylattytila(oid, "Pakollista arvoa (tunniste " + valintaperusteviite.tunniste + ") ei " +
              "ole olemassa", new PakollinenValintaperusteHylkays(valintaperusteviite.tunniste))
          } else new Hyvaksyttavissatila
          (oletusarvo, List(tila))
        }
      }
    }

    // Jos kyseessä on syötettävä valintaperuste, pitää ensin tsekata osallistumistieto
    valintaperusteviite match {
      case SyotettavaValintaperuste(tunniste, pakollinen, osallistuminenTunniste) => {
        val osallistui = hakemus.kentat.get(osallistuminenTunniste).map(osallistuiArvo => {
          try {
            osallistuiArvo.toBoolean
          } catch {
            case e: NumberFormatException => throw new RuntimeException("Arvoa " + osallistuiArvo + " ei voida muuttaa " +
              "Boolean tyypiksi (tunniste " + osallistuminenTunniste + ")")
          }
        })

        // Jos valintaperusteelle on merkitty arvo "ei osallistunut", palautetaan hylätty-tila, jos kyseessä on
        // pakollinen tieto
        if (!osallistui.getOrElse(throw new RuntimeException("Osallistumistietoa valintaperusteelle tunnisteella " +
          tunniste + " ei ole olemassa (tunniste " + osallistuminenTunniste + ")")) && pakollinen) {
          (None, List(new Hylattytila(oid, "Pakollisen syötettävän kentän arvo on 'Ei osallistunut' (tunniste "
            + tunniste + ")", new EiOsallistunutHylkays(tunniste))))
        } else haeValintaperusteenArvoHakemukselta
      }

      case _ => haeValintaperusteenArvoHakemukselta
    }
  }

  private def string2boolean(s: String, tunniste: String): Boolean = {
    try {
      s.toBoolean
    } catch {
      case e: NumberFormatException => throw new RuntimeException("Arvoa " + s + " ei voida muuttaa " +
        "Boolean-tyyppiseksi (tunniste " + tunniste + ")")
    }
  }

  private def string2bigDecimal(s: String, tunniste: String): BigDecimal = {
    try {
      BigDecimal(s)
    } catch {
      case e: NumberFormatException => throw new RuntimeException("Arvoa " + s + " ei voida muuttaa " +
        "BigDecimal-tyyppiseksi (tunniste " + tunniste + ")")
    }
  }


  def laske(laskettava: Totuusarvofunktio): (Option[Boolean], Tila, Historia) = {

    def muodostaKoostettuTulos(fs: Seq[Totuusarvofunktio], trans: Seq[Boolean] => Boolean) = {
      val (tulokset, tilat, historiat) = fs.reverse.foldLeft((Nil, Nil, ListBuffer()): Tuple3[List[Option[Boolean]], List[Tila], ListBuffer[Historia]])((lst, f) => {
        val (tulos, tila, historia) = laske(f)
        lst._3 += historia
        (tulos :: lst._1, tila :: lst._2, lst._3)
      })

      val (tyhjat, eiTyhjat) = tulokset.partition(_.isEmpty)

      // jos yksikin laskennasta saaduista arvoista on tyhjä, koko funktion laskenta palauttaa tyhjän
      val totuusarvo = if (!tyhjat.isEmpty) None else Some(trans(eiTyhjat.map(_.get)))
      (totuusarvo, tilat, Historia("Koostettu tulos", totuusarvo, tilat, Some(historiat.toList), None))
    }

    def muodostaYksittainenTulos(f: Totuusarvofunktio, trans: Boolean => Boolean) = {
      val (tulos, tila, historia) = laske(f)
      (tulos.map(trans(_)), List(tila), historia)
    }

    def muodostaVertailunTulos(f1: Lukuarvofunktio, f2: Lukuarvofunktio,
                               trans: (BigDecimal, BigDecimal) => Boolean) = {
      val (tulos1, tila1, historia1) = laske(f1)
      val (tulos2, tila2, historia2) = laske(f2)
      val tulos = for {
        t1 <- tulos1
        t2 <- tulos2
      } yield trans(t1, t2)
      val tilat = List(tila1, tila2)
      (tulos, tilat, Historia("Vertailuntulos", tulos, tilat, Some(List(historia1, historia2)), None))
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
        val (tulos, tila) = haeValintaperuste[Boolean](oid, valintaperusteviite, hakemus,
          (s => suoritaOptionalKonvertointi[Boolean](oid, (Some(string2boolean(s, valintaperusteviite.tunniste)), new Hyvaksyttavissatila),
            konvertteri)), oletusarvo)
        (tulos, tila, Historia("Hae totuusarvo", tulos, tila, None, Some(Map("oletusarvo" -> oletusarvo))))
      }
      case NimettyTotuusarvo(nimi, f, oid) => {
        val (tulos, tilat, h) = muodostaYksittainenTulos(f, b => b)
        (tulos, tilat, Historia("Nimetty totuusarvo", tulos, tilat, Some(List(h)), Some(Map("nimi" -> Some(nimi)))))
      }

      case Hakutoive(n, oid) => {
        val onko = Some(hakemus.onkoHakutoivePrioriteetilla(hakukohde, n));
        val tilat = List(new Hyvaksyttavissatila)
        (onko, tilat, Historia("Hakutoive", onko, tilat, None, Some(Map("prioriteetti" -> Some(n)))))
      }

      case d: Demografia => {
        val avain = Esiprosessori.prosessointiOid(hakukohde, hakemus, d)
        val valintaperuste = hakemus.kentat.get(avain)
        val arvoOption = valintaperuste.map(arvo => {
          try {
            arvo.toBoolean
          } catch {
            case e: NumberFormatException => throw new RuntimeException("Arvoa " + arvo + " ei voida muuttaa " +
              "Boolean-tyypiksi")

          }
        })
        val tilat = List(new Hyvaksyttavissatila)
        (arvoOption, tilat, Historia("Demografia", arvoOption, tilat, None, Some(Map("avain" -> Some(avain), "valintaperuste" -> valintaperuste))))
      }

      case HaeMerkkijonoJaKonvertoiTotuusarvoksi(konvertteri, oletusarvo, valintaperusteviite, oid) => {
        val (tulos, tila) = haeValintaperuste[Boolean](oid, valintaperusteviite, hakemus,
          (s => suoritaKonvertointi[String, Boolean](oid, (Some(s), new Hyvaksyttavissatila), konvertteri)), oletusarvo)
        (tulos, tila, Historia("Hae merkkijono ja konvertoi totuusarvoksi", tulos, tila, None, Some(Map("oletusarvo" -> oletusarvo))))
      }

      case HaeMerkkijonoJaVertaaYhtasuuruus(oletusarvo, valintaperusteviite, vertailtava, oid) => {
        val (tulos, tila) = haeValintaperuste[Boolean](oid, valintaperusteviite, hakemus,
          (s => (Some(s == vertailtava), List(new Hyvaksyttavissatila))), oletusarvo)
        (tulos, tila, Historia("Hae merkkijono ja vertaa yhtasuuruus", tulos, tila, None, Some(Map("oletusarvo" -> oletusarvo))))
      }
    }

    val palautettavaTila = tilat.filter(_ match {
      case _: Hylattytila => true
      case _ => false
    }) match {
      case head :: tail => head
      case Nil => new Hyvaksyttavissatila
    }

    (laskettuTulos, palautettavaTila, hist)
  }

  def laske(laskettava: Lukuarvofunktio): (Option[BigDecimal], Tila, Historia) = {

    def summa(vals: Seq[BigDecimal]): BigDecimal = {
      vals.reduceLeft(_ + _)
    }

    def muodostaYksittainenTulos(f: Lukuarvofunktio, trans: BigDecimal => BigDecimal): (Option[BigDecimal], List[Tila], Historia) = {
      val (tulos, tila, historia) = laske(f)
      (tulos.map(trans(_)), List(tila), historia)
    }

    def muodostaKoostettuTulos(fs: Seq[Lukuarvofunktio], trans: Seq[BigDecimal] => BigDecimal): (Option[BigDecimal], List[Tila], Historia) = {
      val tulokset = fs.reverse.foldLeft((Nil, Nil, ListBuffer()): Tuple3[List[BigDecimal], List[Tila], ListBuffer[Historia]])((lst, f) => {
        val (tulos, tila, historia) = laske(f)
        lst._3 += historia
        (if (!tulos.isEmpty) tulos.get :: lst._1 else lst._1, tila :: lst._2, lst._3)
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
        val (nimittajaArvo, nimittajaTila, historia1) = laske(nimittaja)
        val (osoittajaArvo, osoittajaTila, historia2) = laske(osoittaja)
        //historiat :+ historia1 :+ historia2
        val tulos = for {
          n <- nimittajaArvo
          o <- osoittajaArvo
        } yield {
          if (n == 0.0) throw new RuntimeException("Nimittäjä ei voi olla nolla")
          BigDecimal(o.underlying.divide(n.underlying, 4, RoundingMode.HALF_UP))
        }
        val tilat = List(nimittajaTila, osoittajaTila)
        (tulos, tilat, Historia("Osamäärä", tulos, tilat, Some(List(historia1, historia2)), None))
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
        val (ehtoTulos, ehtoTila, historia0) = laske(ehto)
        val (thenTulos, thenTila, historia1) = laske(thenHaara)
        val (elseTulos, elseTila, historia2) = laske(elseHaara)
        //historiat :+ historia1 :+ historia2
        val (tulos, tilat) = ehdollinenTulos[Boolean, BigDecimal]((ehtoTulos, ehtoTila), (cond, tila) => {
          if (cond) (thenTulos, List(tila, thenTila)) else (elseTulos, List(tila, elseTila))
        })
        (tulos, tilat, Historia("Jos", tulos, tilat, Some(List(historia0, historia1, historia2)), None))
      }

      case KonvertoiLukuarvo(konvertteri, f, oid) => {
        val (tulos, tila, h) = laske(f)
        val (tulos2, tilat2) = suoritaKonvertointi[BigDecimal, BigDecimal](oid, (tulos, tila), konvertteri)

        (tulos2, tilat2, Historia("Konvertoitulukuarvo", tulos2, tilat2, Some(List(h)), None))
      }

      case HaeLukuarvo(konvertteri, oletusarvo, valintaperusteviite, oid) => {
        val (tulos, tila) = haeValintaperuste[BigDecimal](oid, valintaperusteviite, hakemus,
          (s => suoritaOptionalKonvertointi[BigDecimal](oid, (Some(string2bigDecimal(s, valintaperusteviite.tunniste)), new Hyvaksyttavissatila),
            konvertteri)), oletusarvo)
        (tulos, tila, Historia("Hae Lukuarvo", tulos, tila, None, Some(Map("oletusarvo" -> oletusarvo))))
        //
        //        val valintaperuste = hakemus.kentat.get(valintaperusteviite.tunniste)
        //        val arvoOption = valintaperuste.map(arvo => {
        //          try {
        //            BigDecimal(arvo)
        //          } catch {
        //            case e: NumberFormatException => None
        //            //throw new RuntimeException("Arvoa " + arvo + " ei voida muuttaa " +
        //            //  "BigDecimal-tyypiksi")
        //          }
        //        })
        //
        //        def tyhjaarvo = {
        //          val tila = if (valintaperusteviite.pakollinen) {
        //            new Hylattytila(oid, "Pakollista arvoa (tunniste " + valintaperusteviite.tunniste + ") ei " +
        //              "ole olemassa", new PakollinenValintaperusteHylkays(valintaperusteviite.tunniste))
        //          } else new Hyvaksyttavissatila
        //          (oletusarvo, List(tila))
        //        }
        //
        //        arvoOption match {
        //          case None => {
        //            val (oletustulos, oletustila) = tyhjaarvo
        //            (oletustulos, oletustila, Historia("Hae lukuarvo (oletusarvo)", oletustulos, oletustila, None, Some(Map("oletusarvo" -> oletustulos))))
        //          }
        //          case temp: Option[_] => {
        //            temp.get match {
        //              case arvo: BigDecimal => {
        //                val (valitulos, valitila) = (temp.asInstanceOf[Option[BigDecimal]], new Hyvaksyttavissatila)
        //
        //                val (tulos, tila) = suoritaOptionalKonvertointi[BigDecimal](oid, (if (valitulos.isEmpty) None else Some(valitulos.get.underlying), valitila), konvertteri)
        //                val (oletustulos, oletustila) = tyhjaarvo
        //                (tulos, tila, Historia("Hae lukuarvo (oletusarvo)", tulos, tila, None, Some(Map("oletusarvo" -> oletustulos))))
        //              }
        //              case _ => {
        //                val (oletustulos, oletustila) = tyhjaarvo
        //                (oletustulos, oletustila, Historia("Hae lukuarvo (oletusarvo)", oletustulos, oletustila, None, Some(Map("oletusarvo" -> oletustulos))))
        //              }
        //            }
        //          }
        //        }
      }
      case HaeMerkkijonoJaKonvertoiLukuarvoksi(konvertteri, oletusarvo, valintaperusteviite, oid) => {
        val (tulos, tila) = haeValintaperuste[BigDecimal](oid, valintaperusteviite, hakemus,
          (s => suoritaKonvertointi[String, BigDecimal](oid, (Some(s), new Hyvaksyttavissatila), konvertteri)), oletusarvo)
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
    }

    val palautettavaTila = tilat.filter(_ match {
      case _: Hylattytila => true
      case _ => false
    }) match {
      case head :: tail => head
      case Nil => new Hyvaksyttavissatila
    }

    (laskettuTulos, palautettavaTila, historia) //new Historia("Laske", None, laskettuTulos.getOrElse("").toString, Some(historiat.toList), None))
  }
}

