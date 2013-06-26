package fi.vm.sade.service.valintaperusteet.laskenta

import api._
import org.apache.commons.lang.StringUtils
import tila._

/**
 *
 * User: tommiha
 * Date: 1/12/13
 * Time: 12:56 PM
 */
object Laskenta {

  case class Valintaperusteviite(tunniste: String, pakollinen: Boolean)

  case class Arvokonvertteri[S, T](konversioMap: Seq[Arvokonversio[S, T]]) extends Konvertteri[S, T] {
    def konvertoi(funktiokutsuOid: String, arvo: S): (T, Tila) = {
      konversioMap.filter(arvo == _.arvo) match {
        case Nil => throw new RuntimeException("Arvo " + arvo + " ei täsmää yhteenkään konvertterille " +
          "määritettyyn arvoon, funktiokutsu OID: " + funktiokutsuOid)
        case head :: tail => {
          val paluuarvo = head.paluuarvo
          val tila = if (head.hylkaysperuste) {
            new Hylattytila(funktiokutsuOid, "Arvo " + arvo + " on määritelty konvertterissa hylkäysperusteeksi",
              new Arvokonvertterihylkays(arvo.toString))
          } else new Hyvaksyttavissatila

          (paluuarvo, tila)
        }
      }
    }
  }

  case class Lukuarvovalikonvertteri(konversioMap: Seq[Lukuarvovalikonversio]) extends Konvertteri[Double, Double] {
    def konvertoi(funktiokutsuOid: String, arvo: Double): (Double, Tila) = {
      konversioMap.filter(konv => arvo >= konv.min && arvo < konv.max) match {
        case Nil => throw new RuntimeException("Arvo " + arvo + " ei täsmää yhteenkään konvertterille " +
          "määritettyyn arvoväliin, funktiokutsu OID: " + funktiokutsuOid)
        case head :: tail => {
          val paluuarvo = if (head.palautaHaettuArvo) arvo else head.paluuarvo
          val tila = if (head.hylkaysperuste) {
            new Hylattytila(funktiokutsuOid, "Arvoväli " + head.min + "-" + head.max + " on määritelty " +
              "konvertterissa hylkäysperusteeksi. Konvertoitava arvo " + arvo + ".",
              new Arvovalikonvertterihylkays(arvo, head.min, head.max))
          } else new Hyvaksyttavissatila

          (paluuarvo, tila)
        }
      }
    }
  }

  trait Konversio {
    val hylkaysperuste: Boolean
  }

  trait YksiParametrinenFunktio[T] extends Funktio[T] {
    val f: Funktio[_]
  }

  trait NollaParametrinenFunktio[T] extends Funktio[T]

  trait KaksiParametrinenFunktio[T] extends Funktio[T] {
    val f1: Funktio[_]
    val f2: Funktio[_]
  }

  case class Arvokonversio[S, T](arvo: S, paluuarvo: T, hylkaysperuste: Boolean) extends Konversio

  case class Lukuarvovalikonversio(min: Double, max: Double, paluuarvo: Double,
                                   palautaHaettuArvo: Boolean, hylkaysperuste: Boolean) extends Konversio

  case class KonvertoiLukuarvo(konvertteri: Konvertteri[Double, Double], f: Lukuarvofunktio, oid: String = "")
    extends KonvertoivaFunktio[Double, Double] with Lukuarvofunktio with YksiParametrinenFunktio[Double]

  abstract case class KoostavaFunktio[T](fs: Seq[Funktio[_]]) extends Funktio[T] {
    require(fs.size > 0, "Number of function arguments must be greater than zero")
  }

  abstract case class NParasta(n: Int, override val fs: Seq[Funktio[Double]])
    extends KoostavaFunktio[Double](fs) with Lukuarvofunktio {
    require(n <= fs.size, "Parameter n can't be greater than the number of function arguments")
  }

  abstract case class Ns(ns: Int, override val fs: Seq[Funktio[Double]])
    extends KoostavaFunktio[Double](fs) with Lukuarvofunktio {
    require(ns <= fs.size, "Parameter ns can't be greater than the number of function arguments")
  }

  case class Lukuarvo(d: Double, oid: String = "") extends Lukuarvofunktio with NollaParametrinenFunktio[Double]

  case class Negaatio(f: Lukuarvofunktio, oid: String = "") extends Lukuarvofunktio with YksiParametrinenFunktio[Double]

  case class Summa(override val fs: Seq[Lukuarvofunktio], oid: String = "")
    extends KoostavaFunktio[Double](fs) with Lukuarvofunktio

  object Summa {
    def apply(fs: Lukuarvofunktio*) = {
      new Summa(fs.toSeq)
    }
  }

  case class SummaNParasta(override val n: Int, override val fs: Seq[Lukuarvofunktio], oid: String = "")
    extends NParasta(n, fs) with Lukuarvofunktio

  object SummaNParasta {
    def apply(n: Int, fs: Lukuarvofunktio*) = {
      new SummaNParasta(n, fs.toSeq)
    }
  }

  case class KeskiarvoNParasta(override val n: Int, override val fs: Seq[Lukuarvofunktio], oid: String = "")
    extends NParasta(n, fs) with Lukuarvofunktio

  object KeskiarvoNParasta {
    def apply(n: Int, fs: Lukuarvofunktio*) = {
      new KeskiarvoNParasta(n, fs.toSeq)
    }
  }

  case class Keskiarvo(override val fs: Seq[Lukuarvofunktio], oid: String = "")
    extends KoostavaFunktio[Double](fs) with Lukuarvofunktio

  object Keskiarvo {
    def apply(fs: Lukuarvofunktio*) = {
      new Keskiarvo(fs.toSeq)
    }
  }

  case class Mediaani(override val fs: Seq[Lukuarvofunktio], oid: String = "")
    extends KoostavaFunktio[Double](fs) with Lukuarvofunktio

  object Mediaani {
    def apply(fs: Lukuarvofunktio*) = {
      new Mediaani(fs.toSeq)
    }
  }

  case class Osamaara(osoittaja: Lukuarvofunktio, nimittaja: Lukuarvofunktio, oid: String = "")
    extends Funktio[Double] with Lukuarvofunktio with KaksiParametrinenFunktio[Double] {
    val f1 = osoittaja
    val f2 = nimittaja
  }

  case class Minimi(override val fs: Seq[Lukuarvofunktio], oid: String = "")
    extends KoostavaFunktio[Double](fs) with Lukuarvofunktio

  object Minimi {
    def apply(fs: Lukuarvofunktio*) = {
      new Minimi(fs.toSeq)
    }
  }

  case class Maksimi(override val fs: Seq[Lukuarvofunktio], oid: String = "")
    extends KoostavaFunktio[Double](fs) with Lukuarvofunktio

  object Maksimi {
    def apply(fs: Lukuarvofunktio*) = {
      new Maksimi(fs.toSeq)
    }
  }

  case class NMinimi(override val ns: Int, override val fs: Seq[Lukuarvofunktio], oid: String = "")
    extends Ns(ns, fs) with Lukuarvofunktio

  object NMinimi {
    def apply(ns: Int, fs: Lukuarvofunktio*) = {
      new NMinimi(ns, fs.toSeq)
    }
  }

  case class NMaksimi(override val ns: Int, override val fs: Seq[Lukuarvofunktio], oid: String = "")
    extends Ns(ns, fs) with Lukuarvofunktio

  object NMaksimi {
    def apply(ns: Int, fs: Lukuarvofunktio*) = {
      new NMaksimi(ns, fs.toSeq)
    }
  }

  case class Tulo(override val fs: Seq[Lukuarvofunktio], oid: String = "")
    extends KoostavaFunktio[Double](fs) with Lukuarvofunktio

  object Tulo {
    def apply(fs: Lukuarvofunktio*) = {
      new Tulo(fs.toSeq)
    }
  }

  case class Jos(ehto: Totuusarvofunktio, ifHaara: Lukuarvofunktio, elseHaara: Lukuarvofunktio, oid: String = "")
    extends Lukuarvofunktio

  abstract case class HaeArvo[T](oletusarvo: Option[T], valintaperusteviite: Valintaperusteviite) extends Funktio[T]

  case class HaeMerkkijonoJaKonvertoiLukuarvoksi(konvertteri: Konvertteri[String, Double],
                                                 override val oletusarvo: Option[Double],
                                                 override val valintaperusteviite: Valintaperusteviite,
                                                 oid: String = "")
    extends HaeArvo[Double](oletusarvo, valintaperusteviite) with Lukuarvofunktio with NollaParametrinenFunktio[Double]

  case class HaeMerkkijonoJaKonvertoiTotuusarvoksi(konvertteri: Konvertteri[String, Boolean],
                                                   override val oletusarvo: Option[Boolean],
                                                   override val valintaperusteviite: Valintaperusteviite,
                                                   oid: String = "")
    extends HaeArvo[Boolean](oletusarvo, valintaperusteviite) with Totuusarvofunktio with NollaParametrinenFunktio[Boolean]

  case class HaeTotuusarvo(konvertteri: Option[Konvertteri[Boolean, Boolean]],
                           override val oletusarvo: Option[Boolean],
                           override val valintaperusteviite: Valintaperusteviite, oid: String = "")
    extends HaeArvo[Boolean](oletusarvo, valintaperusteviite) with Totuusarvofunktio with NollaParametrinenFunktio[Boolean]

  case class HaeLukuarvo(konvertteri: Option[Konvertteri[Double, Double]],
                         override val oletusarvo: Option[Double],
                         override val valintaperusteviite: Valintaperusteviite, oid: String = "")
    extends HaeArvo[Double](oletusarvo, valintaperusteviite) with Lukuarvofunktio with NollaParametrinenFunktio[Double]

  // Boolean-funktiot
  case class Ja(override val fs: Seq[Totuusarvofunktio], oid: String = "")
    extends KoostavaFunktio[Boolean](fs) with Totuusarvofunktio

  object Ja {
    def apply(fs: Totuusarvofunktio*) = {
      new Ja(fs.toSeq)
    }
  }

  case class Tai(override val fs: Seq[Totuusarvofunktio], oid: String = "")
    extends KoostavaFunktio[Boolean](fs) with Totuusarvofunktio

  object Tai {
    def apply(fs: Totuusarvofunktio*) = {
      new Tai(fs.toSeq)
    }
  }

  case class Ei(f: Totuusarvofunktio, oid: String = "") extends Totuusarvofunktio with YksiParametrinenFunktio[Boolean]

  case class Suurempi(f1: Lukuarvofunktio, f2: Lukuarvofunktio, oid: String = "")
    extends Totuusarvofunktio with KaksiParametrinenFunktio[Boolean]

  case class SuurempiTaiYhtasuuri(f1: Lukuarvofunktio, f2: Lukuarvofunktio, oid: String = "")
    extends Totuusarvofunktio with KaksiParametrinenFunktio[Boolean]

  case class Pienempi(f1: Lukuarvofunktio, f2: Lukuarvofunktio, oid: String = "")
    extends Totuusarvofunktio with KaksiParametrinenFunktio[Boolean]

  case class PienempiTaiYhtasuuri(f1: Lukuarvofunktio, f2: Lukuarvofunktio, oid: String = "")
    extends Totuusarvofunktio with KaksiParametrinenFunktio[Boolean]

  case class Yhtasuuri(f1: Lukuarvofunktio, f2: Lukuarvofunktio, oid: String = "")
    extends Totuusarvofunktio with KaksiParametrinenFunktio[Boolean]

  case class Totuusarvo(b: Boolean, oid: String = "") extends Totuusarvofunktio with NollaParametrinenFunktio[Boolean]

  abstract case class Nimetty[T](nimi: String, f: Funktio[T]) extends Funktio[T] {
    require(StringUtils.isNotBlank(nimi), "Nimi cannot be null or empty")
  }

  case class NimettyTotuusarvo(override val nimi: String, override val f: Totuusarvofunktio, oid: String = "")
    extends Nimetty(nimi, f) with Totuusarvofunktio with YksiParametrinenFunktio[Boolean]

  case class NimettyLukuarvo(override val nimi: String, override val f: Lukuarvofunktio, oid: String = "")
    extends Nimetty(nimi, f) with Lukuarvofunktio with YksiParametrinenFunktio[Double]

  case class Hakutoive(n: Int, oid: String = "") extends Totuusarvofunktio with NollaParametrinenFunktio[Boolean] {
    require(n > 0, "n must be greater than zero")
  }

  trait Esiprosessoiva extends Totuusarvofunktio {
    val tunniste: String
    val prosenttiosuus: Double
  }

  case class Demografia(oid: String, tunniste: String, prosenttiosuus: Double)
    extends Esiprosessoiva with NollaParametrinenFunktio[Boolean]

}
