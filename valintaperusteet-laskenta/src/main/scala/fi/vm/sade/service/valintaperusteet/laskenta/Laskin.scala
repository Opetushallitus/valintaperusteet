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
    val (tulos, tila) = new Laskin(hakukohde, hakemus).laske(laskettava)
    new Laskentatulos[java.lang.Double](tila, if (!tulos.isEmpty) Double.box(tulos.get) else null)
  }

  def suoritaLasku(hakukohde: String,
    hakemus: Hakemus,
    laskettava: Totuusarvofunktio): Laskentatulos[java.lang.Boolean] = {
    val (tulos, tila) = new Laskin(hakukohde, hakemus).laske(laskettava)
    new Laskentatulos[java.lang.Boolean](tila, if (!tulos.isEmpty) Boolean.box(tulos.get) else null)
  }

  def laske(hakukohde: String, hakemus: Hakemus, laskettava: Totuusarvofunktio): (Option[Boolean], Tila) = {
    new Laskin(hakukohde, hakemus).laske(laskettava)
  }

  def laske(hakukohde: String, hakemus: Hakemus, laskettava: Lukuarvofunktio): (Option[Double], Tila) = {
    new Laskin(hakukohde, hakemus).laske(laskettava)
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

  def laske(laskettava: Totuusarvofunktio): (Option[Boolean], Tila) = {

    def muodostaKoostettuTulos(fs: Seq[Totuusarvofunktio], trans: Seq[Boolean] => Boolean) = {
      val tulokset = fs.reverse.foldLeft((Nil, Nil): Tuple2[List[Boolean], List[Tila]])((lst, f) => {
        val (tulos, tila) = laske(f)
        (if (!tulos.isEmpty) tulos.get :: lst._1 else lst._1, tila :: lst._2)
      })

      val totuusarvo = if (tulokset._1.isEmpty) None else Some(trans(tulokset._1))
      (totuusarvo, tulokset._2)
    }

    def muodostaYksittainenTulos(f: Totuusarvofunktio, trans: Boolean => Boolean) = {
      val (tulos, tila) = laske(f)
      (tulos.map(trans(_)), List(tila))
    }

    def muodostaVertailunTulos(f1: Lukuarvofunktio, f2: Lukuarvofunktio,
      trans: (Double, Double) => Boolean) = {
      val (tulos1, tila1) = laske(f1)
      val (tulos2, tila2) = laske(f2)

      val tulos = for {
        t1 <- tulos1
        t2 <- tulos2
      } yield trans(t1, t2)

      (tulos, List(tila1, tila2))
    }

    val (laskettuTulos, tilat): Tuple2[Option[Boolean], List[Tila]] = laskettava match {
      case Ja(fs, oid) => muodostaKoostettuTulos(fs, lst => { val result = lst.forall(b => b); LOG.debug("JA {}", result); result })
      case Tai(fs, oid) => muodostaKoostettuTulos(fs, lst => { val result = lst.exists(b => b); LOG.debug("TAI {}", result); result; })
      case Ei(fk, oid) => muodostaYksittainenTulos(fk, b => { LOG.debug("EI {}", !b); !b })
      case Totuusarvo(b, oid) => { LOG.debug("TOTUUSARVO {}", b); (Some(b), List(new Hyvaksyttavissatila)) }
      case Suurempi(f1, f2, oid) => muodostaVertailunTulos(f1, f2, (d1, d2) => { LOG.debug("SUUREMPI {}", d1 > d2); d1 > d2 })
      case SuurempiTaiYhtasuuri(f1, f2, oid) => muodostaVertailunTulos(f1, f2, (d1, d2) => { LOG.debug("SUUREMPI TAI YHTASUURI {}", d1 >= d2); d1 >= d2 })
      case Pienempi(f1, f2, oid) => muodostaVertailunTulos(f1, f2, (d1, d2) => { LOG.debug("PIENEMPI {}", d1 < d2); d1 < d2 })
      case PienempiTaiYhtasuuri(f1, f2, oid) => muodostaVertailunTulos(f1, f2, (d1, d2) => { LOG.debug("PIENEMPI TAI YHTASUURI {}", d1 <= d2); d1 <= d2 })
      case Yhtasuuri(f1, f2, oid) => muodostaVertailunTulos(f1, f2, (d1, d2) => { LOG.debug("YHTASUURI {}", d1 == d2); d1 == d2 })
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
            LOG.debug("HAE TOTUUSARVO OLETUSARVO({})", oletusarvo)
            (oletusarvo, List(tila))
          }
          case Some(arvo) => {
            val tulos = (Some(arvo), new Hyvaksyttavissatila)
            val konvertoitu = suoritaOptionalKonvertointi[Boolean](oid, tulos, konvertteri)
            LOG.debug("HAE TOTUUSARVO {}", konvertoitu._1)
            konvertoitu
          }
        }
      }
      case NimettyTotuusarvo(nimi, f, oid) => muodostaYksittainenTulos(f, b => { LOG.debug("NIMETTY TOTUUSARVO {}", b); b })

      case Hakutoive(n, oid) => { val onko = Some(hakemus.onkoHakutoivePrioriteetilla(hakukohde, n)); LOG.debug("HAKUTOIVE {}", onko); (onko, List(new Hyvaksyttavissatila)) }

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
        LOG.debug("DEMOGRAFIA {}", arvoOption)
        (arvoOption, List(new Hyvaksyttavissatila))
      }

      case HaeMerkkijonoJaKonvertoiTotuusarvoksi(konvertteri, oletusarvo, valintaperusteviite, oid) => {
        val valintaperuste = hakemus.kentat.get(valintaperusteviite.tunniste)

        valintaperuste match {
          case Some(s) => {
            val tulos = (Some(s), new Hyvaksyttavissatila)
            val result = suoritaKonvertointi[String, Boolean](oid, tulos, konvertteri)
            LOG.debug("HAE MERKKIJONO JA KONVERTOI TOTUUSARVOKSI {}", result._1)
            result
          }
          case None => {
            val tila = if (valintaperusteviite.pakollinen) {
              new Hylattytila(oid, "Pakollista arvoa (tunniste " + valintaperusteviite.tunniste + ") ei " +
                "ole olemassa", new PakollinenValintaperusteHylkays(valintaperusteviite.tunniste))
            } else new Hyvaksyttavissatila
            LOG.debug("HAE MERKKIJONO JA KONVERTOI TOTUUSARVOKSI OLETUSARVO({})", oletusarvo)
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

    (laskettuTulos, palautettavaTila)
  }

  def laske(laskettava: Lukuarvofunktio): (Option[Double], Tila) = {
    def summa(vals: Seq[Double]): Double = {
      vals.reduceLeft(_ + _)
    }

    def muodostaYksittainenTulos(f: Lukuarvofunktio, trans: Double => Double) = {
      val (tulos, tila) = laske(f)
      (tulos.map(trans(_)), List(tila))
    }

    def muodostaKoostettuTulos(fs: Seq[Lukuarvofunktio], trans: Seq[Double] => Double) = {
      val tulokset = fs.reverse.foldLeft((Nil, Nil): Tuple2[List[Double], List[Tila]])((lst, f) => {
        val (tulos, tila) = laske(f)
        (if (!tulos.isEmpty) tulos.get :: lst._1 else lst._1, tila :: lst._2)
      })

      val lukuarvo = if (tulokset._1.isEmpty) None else Some(trans(tulokset._1))
      (lukuarvo, tulokset._2)
    }

    val (laskettuTulos: Option[Double], tilat: Seq[Tila]) = laskettava match {
      case Lukuarvo(d, oid) => { LOG.debug("LUKUARVO {}", d); (Some(d), List(new Hyvaksyttavissatila)) }
      case Negaatio(n, oid) => { val neg = muodostaYksittainenTulos(n, d => -d); LOG.debug("NEGAATIO {}", neg._1); neg }
      case Summa(fs, oid) => { val s = muodostaKoostettuTulos(fs, summa); LOG.debug("SUMMA {}", s._1); s }

      case SummaNParasta(n, fs, oid) => { val np = muodostaKoostettuTulos(fs, ds => summa(ds.sortWith(_ > _).take(n))); LOG.debug("SUMMA N PARASTA {}", np._1); np }

      case Osamaara(osoittaja, nimittaja, oid) => {
        val (nimittajaArvo, nimittajaTila) = laske(nimittaja)
        val (osoittajaArvo, osoittajaTila) = laske(osoittaja)

        val tulos = for {
          n <- nimittajaArvo
          o <- osoittajaArvo
        } yield {
          if (n == 0.0) throw new RuntimeException("Nimittäjä ei voi olla nolla")
          o / n
        }
        LOG.debug("OSAMAARA {}", tulos)
        (tulos, List(nimittajaTila, osoittajaTila))
      }

      case Tulo(fs, oid) => { val tulo = muodostaKoostettuTulos(fs, ds => ds.reduceLeft(_ * _)); LOG.debug("TULO {}", tulo._1); tulo }

      case Keskiarvo(fs, oid) => { val kes = muodostaKoostettuTulos(fs, ds => summa(ds) / ds.size); LOG.debug("KESKIARVO {}", kes._1); kes }

      case KeskiarvoNParasta(n, fs, oid) => {
        val kes = muodostaKoostettuTulos(fs, ds => {
          val kaytettavaN = scala.math.min(n, ds.size)
          summa(ds.sortWith(_ > _).take(kaytettavaN)) / kaytettavaN
        })
        LOG.debug("KESKIARVO N PARASTA {}", kes._1)
        kes
      }
      case Minimi(fs, oid) => { val m = muodostaKoostettuTulos(fs, ds => ds.min); LOG.debug("MINIMI {}", m._1); m }

      case Maksimi(fs, oid) => { val m = muodostaKoostettuTulos(fs, ds => ds.max); LOG.debug("MAKSIMI {}", m._1); m }

      case NMinimi(ns, fs, oid) => { val nm = muodostaKoostettuTulos(fs, ds => ds.sortWith(_ < _)(scala.math.min(ns, ds.size) - 1)); LOG.debug("N MINIMI {}", nm._1); nm }

      case NMaksimi(ns, fs, oid) => { val nm = muodostaKoostettuTulos(fs, ds => ds.sortWith(_ > _)(scala.math.min(ns, ds.size) - 1)); LOG.debug("N MAKSIMI {}", nm._1); nm }

      case Mediaani(fs, oid) => {
        val med = muodostaKoostettuTulos(fs, ds => {
          val sorted = ds.sortWith(_ < _)
          if (sorted.size % 2 == 1) sorted(sorted.size / 2)
          else (sorted(sorted.size / 2) + (sorted(sorted.size / 2 - 1))) / 2.0
        })
        LOG.debug("MEDIAANI {}", med._1)
        med
      }

      case Jos(ehto, thenHaara, elseHaara, oid) => {
        val laskettuEhto = laske(ehto)
        val (thenTulos, thenTila) = laske(thenHaara)
        val (elseTulos, elseTila) = laske(elseHaara)

        val j = ehdollinenTulos[Boolean, Double](laskettuEhto, (cond, tila) => {
          if (cond) (thenTulos, List(tila, thenTila)) else (elseTulos, List(tila, elseTila))
        })
        LOG.debug("JOS {}", j._1)
        j
      }

      case KonvertoiLukuarvo(konvertteri, f, oid) => {
        val laskettuTulos = laske(f)
        val konv = suoritaKonvertointi[Double, Double](oid, laskettuTulos, konvertteri)
        LOG.debug("KONVERTOITULUKUARVO {}", konv._1)
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
        LOG.debug("HAE LUKUARVO {}", a._1)
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
        LOG.debug("HAE MERKKIJONO JA KONVERTOI LUKUARVOKSI {}", a._1)
        a
      }
      case NimettyLukuarvo(nimi, f, oid) => { val t = muodostaYksittainenTulos(f, d => d); LOG.debug("NIMETTY LUKUARVO {}", t._1); t }
    }

    val palautettavaTila = tilat.filter(_ match {
      case _: Hylattytila => true
      case _ => false
    }) match {
      case head :: tail => head
      case Nil => new Hyvaksyttavissatila
    }

    (laskettuTulos, palautettavaTila)
  }
}

