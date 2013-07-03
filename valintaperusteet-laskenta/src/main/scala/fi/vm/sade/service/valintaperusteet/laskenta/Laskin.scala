package fi.vm.sade.service.valintaperusteet.laskenta

import api._
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta._
import java.util.{ Map => JMap }
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.Totuusarvo
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.Suurempi
import scala.Some
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.Yhtasuuri
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.PienempiTaiYhtasuuri
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.Pienempi
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.NimettyTotuusarvo
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.SuurempiTaiYhtasuuri
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.HaeTotuusarvo
import scala.Tuple2
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.Ei
import tila.{ Tila, PakollinenValintaperusteHylkays, Hyvaksyttavissatila, Hylattytila }
import org.slf4j.LoggerFactory
import Laskin._

object Laskin {
  val LOG = LoggerFactory.getLogger(classOf[Laskin])

  def suoritaLasku(hakukohde: String,
    hakemus: Hakemus,
    laskettava: Lukuarvofunktio): Laskentatulos[java.lang.Double] = {
    val logBuffer = new StringBuffer
    logBuffer.append("LASKENTA HAKEMUKSELLE ").append(hakemus.oid).append("\r\n")
    val (tulos, tila) = new Laskin(hakukohde, hakemus).laske(laskettava, 0, logBuffer)
    new Laskentatulos[java.lang.Double](tila, if (!tulos.isEmpty) Double.box(tulos.get) else null)
  }

  def suoritaLasku(hakukohde: String,
    hakemus: Hakemus,
    laskettava: Totuusarvofunktio): Laskentatulos[java.lang.Boolean] = {
    val logBuffer = new StringBuffer
    logBuffer.append("LASKENTA HAKEMUKSELLE ").append(hakemus.oid).append("\r\n")
    val (tulos, tila) = new Laskin(hakukohde, hakemus).laske(laskettava, 0, logBuffer)
    new Laskentatulos[java.lang.Boolean](tila, if (!tulos.isEmpty) Boolean.box(tulos.get) else null)
  }

  def laske(hakukohde: String, hakemus: Hakemus, laskettava: Totuusarvofunktio): (Option[Boolean], Tila) = {
    val logBuffer = new StringBuffer
    logBuffer.append("LASKENTA HAKEMUKSELLE ").append(hakemus.oid).append("\r\n")
    new Laskin(hakukohde, hakemus).laske(laskettava, 0, logBuffer)
  }

  def laske(hakukohde: String, hakemus: Hakemus, laskettava: Lukuarvofunktio): (Option[Double], Tila) = {
    val logBuffer = new StringBuffer
    logBuffer.append("LASKENTA HAKEMUKSELLE ").append(hakemus.oid).append("\r\n")
    new Laskin(hakukohde, hakemus).laske(laskettava, 0, logBuffer)
  }

  private def log(b: StringBuffer, syvyys: Int, operaatio: String) = {
    0.to(syvyys).foreach(a => { b.append("    "); })
    b.append(operaatio).append(" ...\r\n")
    //LOG.debug("{} {} ...", Array[Object](b, operaatio))
  }
  private def log(b: StringBuffer, syvyys: Int, operaatio: String, value: Object) = {
    //val b = new StringBuffer
    0.to(syvyys).foreach(a => { b.append("    "); })
    b.append(operaatio).append(" == ").append(value).append("\r\n")
    //LOG.debug("{} {} == {}", Array[Object](b, operaatio, value))
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

  def laske(laskettava: Totuusarvofunktio, depth: Int, logBuffer: StringBuffer): (Option[Boolean], Tila) = {

    def muodostaKoostettuTulos(fs: Seq[Totuusarvofunktio], trans: Seq[Boolean] => Boolean) = {
      val tulokset = fs.reverse.foldLeft((Nil, Nil): Tuple2[List[Boolean], List[Tila]])((lst, f) => {
        val (tulos, tila) = laske(f, depth + 1, logBuffer)
        (if (!tulos.isEmpty) tulos.get :: lst._1 else lst._1, tila :: lst._2)
      })

      val totuusarvo = if (tulokset._1.isEmpty) None else Some(trans(tulokset._1))
      (totuusarvo, tulokset._2)
    }

    def muodostaYksittainenTulos(f: Totuusarvofunktio, trans: Boolean => Boolean) = {
      val (tulos, tila) = laske(f, depth + 1, logBuffer)
      (tulos.map(trans(_)), List(tila))
    }

    def muodostaVertailunTulos(f1: Lukuarvofunktio, f2: Lukuarvofunktio,
      trans: (Double, Double) => Boolean) = {
      val (tulos1, tila1) = laske(f1, depth + 1, logBuffer)
      val (tulos2, tila2) = laske(f2, depth + 1, logBuffer)

      val tulos = for {
        t1 <- tulos1
        t2 <- tulos2
      } yield trans(t1, t2)

      (tulos, List(tila1, tila2))
    }

    val (laskettuTulos, tilat): Tuple2[Option[Boolean], List[Tila]] = laskettava match {
      case Ja(fs, oid) => { log(logBuffer, depth, "JA"); val res = muodostaKoostettuTulos(fs, lst => lst.forall(b => b)); log(logBuffer, depth, "JA", res); res }
      case Tai(fs, oid) => { log(logBuffer, depth, "TAI"); val res = muodostaKoostettuTulos(fs, lst => lst.exists(b => b)); log(logBuffer, depth, "TAI", res); res; }
      case Ei(fk, oid) => { log(logBuffer, depth, "EI"); val res = muodostaYksittainenTulos(fk, b => !b); log(logBuffer, depth, "EI", res); res; }
      case Totuusarvo(b, oid) => { log(logBuffer, depth, "TOTUUSARVO", Some(b)); (Some(b), List(new Hyvaksyttavissatila)) }
      case Suurempi(f1, f2, oid) => { log(logBuffer, depth, "SUUREMPI"); val res = muodostaVertailunTulos(f1, f2, (d1, d2) => d1 > d2); log(logBuffer, depth, "SUUREMPI", res); res }
      case SuurempiTaiYhtasuuri(f1, f2, oid) => {
        log(logBuffer, depth, "SUUREMPI TAI YHTASUURI");
        val res = muodostaVertailunTulos(f1, f2, (d1, d2) => d1 >= d2);
        log(logBuffer, depth, "SUUREMPI TAI YHTASUURI", res);
        res
      }
      case Pienempi(f1, f2, oid) => {
        log(logBuffer, depth, "PIENEMPI");
        val res = muodostaVertailunTulos(f1, f2, (d1, d2) => d1 < d2)
        log(logBuffer, depth, "PIENEMPI", res);
        res
      }
      case PienempiTaiYhtasuuri(f1, f2, oid) => {
        log(logBuffer, depth, "PIENEMPI TAI YHTASUURI");
        val res = muodostaVertailunTulos(f1, f2, (d1, d2) => d1 <= d2)
        log(logBuffer, depth, "PIENEMPI TAI YHTASUURI");
        res
      }
      case Yhtasuuri(f1, f2, oid) => {
        log(logBuffer, depth, "YHTASUURI");
        val res = muodostaVertailunTulos(f1, f2, (d1, d2) => d1 == d2)
        log(logBuffer, depth, "YHTASUURI", res);
        res
      }
      case HaeTotuusarvo(konvertteri, oletusarvo, valintaperusteviite, oid) => {
        val valintaperuste = hakemus.kentat.get(valintaperusteviite.tunniste)

        val arvoOption = valintaperuste.map(arvo => {
          try {
            arvo.toBoolean
          } catch {
            case e: NumberFormatException => throw new RuntimeException("Arvoa " + arvo + " ei voida muuttaa " +
              "Boolean-tyypiksi")
          }
        })

        arvoOption match {
          case None => {
            val tila = if (valintaperusteviite.pakollinen) {
              new Hylattytila(oid, "Pakollista arvoa (tunniste " + valintaperusteviite.tunniste
                + ") ei " + "ole olemassa", new PakollinenValintaperusteHylkays(valintaperusteviite.tunniste))
            } else new Hyvaksyttavissatila
            log(logBuffer, depth, "HAE TOTUUSARVO (oletusarvo)", oletusarvo);
            (oletusarvo, List(tila))
          }
          case Some(arvo) => {
            val tulos = (Some(arvo), new Hyvaksyttavissatila)
            val konvertoitu = suoritaOptionalKonvertointi[Boolean](oid, tulos, konvertteri)
            log(logBuffer, depth, "HAE TOTUUSARVO", konvertoitu._1);
            konvertoitu
          }
        }
      }
      case NimettyTotuusarvo(nimi, f, oid) => {
        log(logBuffer, depth, "NIMETTY TOTUUSARVO");
        val res = muodostaYksittainenTulos(f, b => b)
        log(logBuffer, depth, "NIMETTY TOTUUSARVO", res);
        res
      }

      case Hakutoive(n, oid) => {
        log(logBuffer, depth, "HAKUTOIVE ... oid ", oid);
        val onko = Some(hakemus.onkoHakutoivePrioriteetilla(hakukohde, n)); LOG.debug("HAKUTOIVE {}", onko);
        log(logBuffer, depth, "HAKUTOIVE", onko);
        (onko, List(new Hyvaksyttavissatila))
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
        log(logBuffer, depth, "DEMOGRAFIA", arvoOption);
        (arvoOption, List(new Hyvaksyttavissatila))
      }

      case HaeMerkkijonoJaKonvertoiTotuusarvoksi(konvertteri, oletusarvo, valintaperusteviite, oid) => {
        val valintaperuste = hakemus.kentat.get(valintaperusteviite.tunniste)

        valintaperuste match {
          case Some(s) => {
            val tulos = (Some(s), new Hyvaksyttavissatila)
            val result = suoritaKonvertointi[String, Boolean](oid, tulos, konvertteri)
            log(logBuffer, depth, "HAE MERKKIJONO JA KONVERTOI TOTUUSARVOKSI", result);
            result
          }
          case None => {
            val tila = if (valintaperusteviite.pakollinen) {
              new Hylattytila(oid, "Pakollista arvoa (tunniste " + valintaperusteviite.tunniste + ") ei " +
                "ole olemassa", new PakollinenValintaperusteHylkays(valintaperusteviite.tunniste))
            } else new Hyvaksyttavissatila
            log(logBuffer, depth, "HAE MERKKIJONO JA KONVERTOI TOTUUSARVOKSI (oletusarvo)", oletusarvo);
            (oletusarvo, List(tila))
          }
        }
      }
    }

    val palautettavaTila = tilat.filter(_ match {
      case _: Hylattytila => true
      case _ => false
    }) match {
      case head :: tail => head
      case Nil => new Hyvaksyttavissatila
    }
    LOG.debug("{}", logBuffer)
    (laskettuTulos, palautettavaTila)
  }

  def laske(laskettava: Lukuarvofunktio, depth: Int, logBuffer: StringBuffer): (Option[Double], Tila) = {
    def summa(vals: Seq[Double]): Double = {
      vals.reduceLeft(_ + _)
    }

    def muodostaYksittainenTulos(f: Lukuarvofunktio, trans: Double => Double) = {
      val (tulos, tila) = laske(f, depth + 1, logBuffer)
      (tulos.map(trans(_)), List(tila))
    }

    def muodostaKoostettuTulos(fs: Seq[Lukuarvofunktio], trans: Seq[Double] => Double) = {
      val tulokset = fs.reverse.foldLeft((Nil, Nil): Tuple2[List[Double], List[Tila]])((lst, f) => {
        val (tulos, tila) = laske(f, depth + 1, logBuffer)
        (if (!tulos.isEmpty) tulos.get :: lst._1 else lst._1, tila :: lst._2)
      })

      val lukuarvo = if (tulokset._1.isEmpty) None else Some(trans(tulokset._1))
      (lukuarvo, tulokset._2)
    }

    val (laskettuTulos: Option[Double], tilat: Seq[Tila]) = laskettava match {
      case Lukuarvo(d, oid) => {
        log(logBuffer, depth, "LUKUARVO", Some(d));
        (Some(d), List(new Hyvaksyttavissatila))
      }
      case Negaatio(n, oid) => {
        log(logBuffer, depth, "NEGAATIO");
        val neg = muodostaYksittainenTulos(n, d => -d)
        log(logBuffer, depth, "NEGAATIO", neg);
        neg
      }
      case Summa(fs, oid) => {
        log(logBuffer, depth, "SUMMA");
        val s = muodostaKoostettuTulos(fs, summa)
        log(logBuffer, depth, "SUMMA", s);
        s
      }

      case SummaNParasta(n, fs, oid) => {
        log(logBuffer, depth, "SUMMA N PARASTA");
        val np = muodostaKoostettuTulos(fs, ds => summa(ds.sortWith(_ > _).take(n)))
        log(logBuffer, depth, "SUMMA N PARASTA", np);
        np
      }

      case Osamaara(osoittaja, nimittaja, oid) => {
        val (nimittajaArvo, nimittajaTila) = laske(nimittaja, depth + 1, logBuffer)
        val (osoittajaArvo, osoittajaTila) = laske(osoittaja, depth + 1, logBuffer)

        val tulos = for {
          n <- nimittajaArvo
          o <- osoittajaArvo
        } yield {
          if (n == 0.0) throw new RuntimeException("Nimittäjä ei voi olla nolla")
          o / n
        }
        log(logBuffer, depth, "OSAMAARA", tulos);
        (tulos, List(nimittajaTila, osoittajaTila))
      }

      case Tulo(fs, oid) => {
        log(logBuffer, depth, "TULO")
        val tulo = muodostaKoostettuTulos(fs, ds => ds.reduceLeft(_ * _))
        log(logBuffer, depth, "TULO", tulo)
        tulo
      }

      case Keskiarvo(fs, oid) => {
        log(logBuffer, depth, "KESKIARVO")
        val kes = muodostaKoostettuTulos(fs, ds => summa(ds) / ds.size)
        log(logBuffer, depth, "KESKIARVO", kes)
        kes
      }

      case KeskiarvoNParasta(n, fs, oid) => {
        log(logBuffer, depth, "KESKIARVO N PARASTA")
        val kes = muodostaKoostettuTulos(fs, ds => {
          val kaytettavaN = scala.math.min(n, ds.size)
          summa(ds.sortWith(_ > _).take(kaytettavaN)) / kaytettavaN
        })
        log(logBuffer, depth, "KESKIARVO N PARASTA", kes)
        kes
      }
      case Minimi(fs, oid) => {
        log(logBuffer, depth, "MINIMI")
        val m = muodostaKoostettuTulos(fs, ds => ds.min)
        log(logBuffer, depth, "MINIMI", m)
        m
      }

      case Maksimi(fs, oid) => {
        log(logBuffer, depth, "MAKSIMI")
        val m = muodostaKoostettuTulos(fs, ds => ds.max)
        log(logBuffer, depth, "MAKSIMI", m)
        m
      }

      case NMinimi(ns, fs, oid) => {
        log(logBuffer, depth, "N MINIMI")
        val nm = muodostaKoostettuTulos(fs, ds => ds.sortWith(_ < _)(scala.math.min(ns, ds.size) - 1))
        log(logBuffer, depth, "N MINIMI", nm)
        nm
      }

      case NMaksimi(ns, fs, oid) => {
        log(logBuffer, depth, "N MAKSIMI")
        val nm = muodostaKoostettuTulos(fs, ds => ds.sortWith(_ > _)(scala.math.min(ns, ds.size) - 1))
        log(logBuffer, depth, "N MAKSIMI")
        nm
      }

      case Mediaani(fs, oid) => {
        log(logBuffer, depth, "MEDIAANI")
        val med = muodostaKoostettuTulos(fs, ds => {
          val sorted = ds.sortWith(_ < _)
          if (sorted.size % 2 == 1) sorted(sorted.size / 2)
          else (sorted(sorted.size / 2) + (sorted(sorted.size / 2 - 1))) / 2.0
        })
        log(logBuffer, depth, "MEDIAANI", med)
        med
      }

      case Jos(ehto, thenHaara, elseHaara, oid) => {
        log(logBuffer, depth, "JOS")
        val laskettuEhto = laske(ehto, depth + 1, logBuffer)
        val (thenTulos, thenTila) = laske(thenHaara, depth + 1, logBuffer)
        val (elseTulos, elseTila) = laske(elseHaara, depth + 1, logBuffer)

        val j = ehdollinenTulos[Boolean, Double](laskettuEhto, (cond, tila) => {
          if (cond) (thenTulos, List(tila, thenTila)) else (elseTulos, List(tila, elseTila))
        })
        log(logBuffer, depth, "JOS", j)
        j
      }

      case KonvertoiLukuarvo(konvertteri, f, oid) => {
        val laskettuTulos = laske(f, depth + 1, logBuffer)
        val konv = suoritaKonvertointi[Double, Double](oid, laskettuTulos, konvertteri)
        log(logBuffer, depth, "KONVERTOITULUKUARVO", konv)
        konv
      }

      case HaeLukuarvo(konvertteri, oletusarvo, valintaperusteviite, oid) => {
        val valintaperuste = hakemus.kentat.get(valintaperusteviite.tunniste)

        val arvoOption = valintaperuste.map(arvo => {
          try {
            arvo.toDouble
          } catch {
            case e: NumberFormatException => None
            //throw new RuntimeException("Arvoa " + arvo + " ei voida muuttaa " +
            //  "Double-tyypiksi")
          }
        })

        def tyhjaarvo = {
          val tila = if (valintaperusteviite.pakollinen) {
            new Hylattytila(oid, "Pakollista arvoa (tunniste " + valintaperusteviite.tunniste + ") ei " +
              "ole olemassa", new PakollinenValintaperusteHylkays(valintaperusteviite.tunniste))
          } else new Hyvaksyttavissatila
          (oletusarvo, List(tila))
        }

        val a = arvoOption match {
          case None => tyhjaarvo
          case temp: Option[_] => {
            temp.get match {
              case arvo: Double =>
                val tulos = (temp.asInstanceOf[Option[Double]], new Hyvaksyttavissatila)
                suoritaOptionalKonvertointi[Double](oid, tulos, konvertteri)
              case _ =>
                tyhjaarvo
            }

          }
        }
        log(logBuffer, depth, "HAE LUKUARVO", a)
        a

      }
      case HaeMerkkijonoJaKonvertoiLukuarvoksi(konvertteri, oletusarvo, valintaperusteviite, oid) => {
        val valintaperuste = hakemus.kentat.get(valintaperusteviite.tunniste)

        val a = valintaperuste match {
          case Some(s) => {
            val tulos = (Some(s), new Hyvaksyttavissatila)
            suoritaKonvertointi[String, Double](oid, tulos, konvertteri)
          }
          case None => {
            val tila = if (valintaperusteviite.pakollinen) {
              new Hylattytila(oid, "Pakollista arvoa (tunniste " + valintaperusteviite.tunniste + ") ei " +
                "ole olemassa", new PakollinenValintaperusteHylkays(valintaperusteviite.tunniste))
            } else new Hyvaksyttavissatila
            (oletusarvo, List(tila))
          }
        }
        log(logBuffer, depth, "HAE MERKKIJONO JA KONVERTOI LUKUARVOKSI", a)
        a
      }
      case NimettyLukuarvo(nimi, f, oid) => {
        log(logBuffer, depth, "NIMETTY LUKUARVO")
        val t = muodostaYksittainenTulos(f, d => d)
        log(logBuffer, depth, "NIMETTY LUKUARVO", t)
        t
      }
    }

    val palautettavaTila = tilat.filter(_ match {
      case _: Hylattytila => true
      case _ => false
    }) match {
      case head :: tail => head
      case Nil => new Hyvaksyttavissatila
    }
    LOG.debug("{}", logBuffer)
    (laskettuTulos, palautettavaTila)
  }
}

