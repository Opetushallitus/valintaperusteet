package fi.vm.sade.service.valintaperusteet.laskenta

import org.apache.commons.lang.StringUtils
import fi.vm.sade.service.valintaperusteet.laskenta.api.tila._
import java.math.{BigDecimal => BigDec}

/**
 *
 * User: tommiha
 * Date: 1/12/13
 * Time: 12:56 PM
 */
object Laskenta {

  trait Valintaperuste {
    val tunniste: String
  }

  case class HakemuksenValintaperuste(override val tunniste: String, val pakollinen: Boolean) extends Valintaperuste

  case class SyotettavaValintaperuste(override val tunniste: String, val pakollinen: Boolean,
                                      osallistuminenTunniste: String) extends Valintaperuste

  case class HakukohteenValintaperuste(override val tunniste: String, val pakollinen: Boolean, val epasuoraViittaus: Boolean) extends Valintaperuste

  case class Arvokonvertteri[S, T](konversioMap: Seq[Arvokonversio[S, T]]) extends Konvertteri[S, T] {
    def konvertoi(arvo: S): (Option[T], Tila) = {
      konversioMap.filter(arvo == _.arvo) match {
        case Nil => (None, new Virhetila("Arvo " + arvo + " ei täsmää yhteenkään konvertterille "
          + "määritettyyn arvoon", new ArvokonvertointiVirhe(arvo.toString)))
        case head :: tail => {
          val paluuarvo = head.paluuarvo
          val tila = if (head.hylkaysperuste) {
            new Hylattytila("Arvo " + arvo + " on määritelty konvertterissa hylkäysperusteeksi",
              new Arvokonvertterihylkays(arvo.toString))
          } else new Hyvaksyttavissatila

          (Some(paluuarvo), tila)
        }
      }
    }
  }

  case class Lukuarvovalikonvertteri(konversioMap: Seq[Lukuarvovalikonversio]) extends Konvertteri[BigDecimal, BigDecimal] {
    def konvertoi(arvo: BigDecimal): (Option[BigDecimal], Tila) = {
      konversioMap.sortWith((a, b) => a.max > b.max).filter(konv => arvo >= konv.min && arvo <= konv.max) match {
        case Nil => (None, new Virhetila("Arvo " + arvo + " ei täsmää yhteenkään konvertterille " +
          "määritettyyn arvoväliin", new ArvovalikonvertointiVirhe(arvo.underlying)))
        case head :: tail => {
          val paluuarvo = if (head.palautaHaettuArvo) arvo else head.paluuarvo
          val tila = if (head.hylkaysperuste) {
            new Hylattytila("Arvoväli " + head.min + "-" + head.max + " on määritelty " +
              "konvertterissa hylkäysperusteeksi. Konvertoitava arvo " + arvo + ".",
              new Arvovalikonvertterihylkays(arvo.underlying, head.min.underlying, head.max.underlying))
          } else new Hyvaksyttavissatila

          (Some(paluuarvo), tila)
        }
      }
    }
  }

  trait Konversio {
    val hylkaysperuste: Boolean
  }

  case class Arvokonversio[S, T](arvo: S, paluuarvo: T, hylkaysperuste: Boolean) extends Konversio

  case class Lukuarvovalikonversio(min: BigDecimal, max: BigDecimal, paluuarvo: BigDecimal,
                                   palautaHaettuArvo: Boolean, hylkaysperuste: Boolean) extends Konversio

  case class KonvertoiLukuarvo(konvertteri: Konvertteri[BigDecimal, BigDecimal], f: Lukuarvofunktio, oid: String = "")
    extends KonvertoivaFunktio[BigDecimal, BigDecimal] with Lukuarvofunktio

  abstract case class KoostavaFunktio[T](fs: Seq[Funktio[_]]) extends Funktio[T] {
    require(fs.size > 0, "Number of function arguments must be greater than zero")
  }

  abstract case class NParasta(n: Int, override val fs: Seq[Funktio[BigDecimal]])
    extends KoostavaFunktio[BigDecimal](fs) with Lukuarvofunktio {
    require(n <= fs.size, "Parameter n can't be greater than the number of function arguments")
  }

  abstract case class Ns(ns: Int, override val fs: Seq[Funktio[BigDecimal]])
    extends KoostavaFunktio[BigDecimal](fs) with Lukuarvofunktio {
    require(ns <= fs.size, "Parameter ns can't be greater than the number of function arguments")
  }

  case class Lukuarvo(d: BigDecimal, oid: String = "") extends Lukuarvofunktio

  case class Negaatio(f: Lukuarvofunktio, oid: String = "") extends Lukuarvofunktio

  case class Pyoristys(tarkkuus: Int, f: Lukuarvofunktio, oid: String = "") extends Lukuarvofunktio {
    require(tarkkuus >= 0, "Parameter tarkkuus must be zero or greater")
  }

  case class Summa(override val fs: Seq[Lukuarvofunktio], oid: String = "")
    extends KoostavaFunktio[BigDecimal](fs) with Lukuarvofunktio

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
    extends KoostavaFunktio[BigDecimal](fs) with Lukuarvofunktio

  object Keskiarvo {
    def apply(fs: Lukuarvofunktio*) = {
      new Keskiarvo(fs.toSeq)
    }
  }

  case class Mediaani(override val fs: Seq[Lukuarvofunktio], oid: String = "")
    extends KoostavaFunktio[BigDecimal](fs) with Lukuarvofunktio

  object Mediaani {
    def apply(fs: Lukuarvofunktio*) = {
      new Mediaani(fs.toSeq)
    }
  }

  case class Osamaara(osoittaja: Lukuarvofunktio, nimittaja: Lukuarvofunktio, oid: String = "")
    extends Funktio[BigDecimal] with Lukuarvofunktio {
    val f1 = osoittaja
    val f2 = nimittaja
  }

  case class Minimi(override val fs: Seq[Lukuarvofunktio], oid: String = "")
    extends KoostavaFunktio[BigDecimal](fs) with Lukuarvofunktio

  object Minimi {
    def apply(fs: Lukuarvofunktio*) = {
      new Minimi(fs.toSeq)
    }
  }

  case class Maksimi(override val fs: Seq[Lukuarvofunktio], oid: String = "")
    extends KoostavaFunktio[BigDecimal](fs) with Lukuarvofunktio

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
    extends KoostavaFunktio[BigDecimal](fs) with Lukuarvofunktio

  object Tulo {
    def apply(fs: Lukuarvofunktio*) = {
      new Tulo(fs.toSeq)
    }
  }

  case class Jos(ehto: Totuusarvofunktio, ifHaara: Lukuarvofunktio, elseHaara: Lukuarvofunktio, oid: String = "")
    extends Lukuarvofunktio

  abstract case class HaeArvo[T](oletusarvo: Option[T], valintaperusteviite: Valintaperuste) extends Funktio[T]

  case class HaeMerkkijonoJaKonvertoiLukuarvoksi(konvertteri: Konvertteri[String, BigDecimal],
                                                 override val oletusarvo: Option[BigDecimal],
                                                 override val valintaperusteviite: Valintaperuste,
                                                 oid: String = "")
    extends HaeArvo[BigDecimal](oletusarvo, valintaperusteviite) with Lukuarvofunktio

  case class HaeMerkkijonoJaKonvertoiTotuusarvoksi(konvertteri: Konvertteri[String, Boolean],
                                                   override val oletusarvo: Option[Boolean],
                                                   override val valintaperusteviite: Valintaperuste,
                                                   oid: String = "")
    extends HaeArvo[Boolean](oletusarvo, valintaperusteviite) with Totuusarvofunktio

  case class HaeTotuusarvo(konvertteri: Option[Konvertteri[Boolean, Boolean]],
                           override val oletusarvo: Option[Boolean],
                           override val valintaperusteviite: Valintaperuste, oid: String = "")
    extends HaeArvo[Boolean](oletusarvo, valintaperusteviite) with Totuusarvofunktio

  case class HaeLukuarvo(konvertteri: Option[Konvertteri[BigDecimal, BigDecimal]],
                         override val oletusarvo: Option[BigDecimal],
                         override val valintaperusteviite: Valintaperuste, oid: String = "")
    extends HaeArvo[BigDecimal](oletusarvo, valintaperusteviite) with Lukuarvofunktio

  case class HaeMerkkijonoJaVertaaYhtasuuruus(override val oletusarvo: Option[Boolean],
                                              override val valintaperusteviite: Valintaperuste,
                                              vertailtava: String, oid: String = "")
    extends HaeArvo[Boolean](oletusarvo, valintaperusteviite) with Totuusarvofunktio

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

  case class Ei(f: Totuusarvofunktio, oid: String = "") extends Totuusarvofunktio

  case class Suurempi(f1: Lukuarvofunktio, f2: Lukuarvofunktio, oid: String = "")
    extends Totuusarvofunktio

  case class SuurempiTaiYhtasuuri(f1: Lukuarvofunktio, f2: Lukuarvofunktio, oid: String = "")
    extends Totuusarvofunktio

  case class Pienempi(f1: Lukuarvofunktio, f2: Lukuarvofunktio, oid: String = "")
    extends Totuusarvofunktio

  case class PienempiTaiYhtasuuri(f1: Lukuarvofunktio, f2: Lukuarvofunktio, oid: String = "")
    extends Totuusarvofunktio

  case class Yhtasuuri(f1: Lukuarvofunktio, f2: Lukuarvofunktio, oid: String = "")
    extends Totuusarvofunktio

  case class Totuusarvo(b: Boolean, oid: String = "") extends Totuusarvofunktio

  abstract case class Nimetty[T](nimi: String, f: Funktio[T]) extends Funktio[T] {
    require(StringUtils.isNotBlank(nimi), "Nimi cannot be null or empty")
  }

  case class NimettyTotuusarvo(override val nimi: String, override val f: Totuusarvofunktio, oid: String = "")
    extends Nimetty(nimi, f) with Totuusarvofunktio

  case class NimettyLukuarvo(override val nimi: String, override val f: Lukuarvofunktio, oid: String = "")
    extends Nimetty(nimi, f) with Lukuarvofunktio

  case class Hakutoive(n: Int, oid: String = "") extends Totuusarvofunktio {
    require(n > 0, "n must be greater than zero")
  }

  case class Hylkaa(f: Totuusarvofunktio, hylkaysperustekuvaus: Option[String] = None, oid: String = "")
    extends Lukuarvofunktio


  case class Demografia(oid: String = "", tunniste: String, prosenttiosuus: BigDecimal) extends Totuusarvofunktio

  case class Skaalaus(oid: String = "", skaalattava: Lukuarvofunktio, kohdeskaala: Pair[BigDecimal, BigDecimal],
                      lahdeskaala: Option[Pair[BigDecimal, BigDecimal]]) extends Lukuarvofunktio {
    require(kohdeskaala._1 < kohdeskaala._2, "Kohdeskaalan minimin pitää olla pienempi kuin maksimi")
    require(lahdeskaala.isEmpty || lahdeskaala.get._1 < lahdeskaala.get._2, "Lähdeskaalan minimin pitää olla " +
      "pienempi kuin maksimi")
  }

  case class PainotettuKeskiarvo(oid: String = "", fs: Seq[Pair[Lukuarvofunktio, Lukuarvofunktio]]) extends Lukuarvofunktio {
    require(fs.size > 0, "Parametreja pitää olla vähintään yksi")
  }

  case class Valintaperusteyhtasuuruus(oid: String = "",
                                       valintaperusteet: Pair[Valintaperuste, Valintaperuste]) extends Totuusarvofunktio

}
