package fi.vm.sade.service.valintaperusteet.laskenta

import org.apache.commons.lang.StringUtils
import fi.vm.sade.service.valintaperusteet.laskenta.api.tila._
import java.math.{BigDecimal => BigDec}
import fi.vm.sade.service.valintaperusteet.laskenta.api.{Hakemus, Hakukohde}
import scala._
import scala.Some
import fi.vm.sade.service.valintaperusteet.model.{LokalisoituTeksti, TekstiRyhma}
import scala.collection.JavaConversions
import java.util

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

  case class HakemuksenValintaperuste(tunniste: String, pakollinen: Boolean) extends Valintaperuste

  case class SyotettavaValintaperuste(tunniste: String, pakollinen: Boolean,
                                      osallistuminenTunniste: String, kuvaus: String = "", kuvaukset: TekstiRyhma, vaatiiOsallistumisen: Boolean = true) extends Valintaperuste

  case class HakukohteenValintaperuste(tunniste: String, pakollinen: Boolean, epasuoraViittaus: Boolean) extends Valintaperuste

  case class HakukohteenSyotettavaValintaperuste(tunniste: String, pakollinen: Boolean, epasuoraViittaus: Boolean,
                                                 osallistuminenTunniste: String, kuvaus: String = "", kuvaukset: TekstiRyhma, vaatiiOsallistumisen: Boolean = true) extends Valintaperuste

  trait Konvertteri[S, T] {
    def konvertoi(arvo: S): (Option[T], Tila)
  }

  trait KonvertoivaFunktio[S, T] extends Funktio[T] {
    val f: Funktio[S]
    val konvertteri: Konvertteri[S, T]
  }

  def tekstiryhmaToMap(ryhma: TekstiRyhma): java.util.Map[String,String] = {
    if(ryhma != null && ryhma.getTekstit != null) {
      val tekstit = JavaConversions.asScalaSet(ryhma.getTekstit)
      val res = tekstit.foldLeft(Map.empty[String,String]){
        (result: Map[String,String],teksti: LokalisoituTeksti) => result + (teksti.getKieli.name -> teksti.getTeksti)
      }
      JavaConversions.mapAsJavaMap(res)
    } else {
      val res = Map("FI" -> "Valintaperusteelle ei oltu määritelty hylkäysperustekuvauksia")
      JavaConversions.mapAsJavaMap(res)
    }
  }

  def tekstiToMap(teksti: String): java.util.Map[String,String] = {
    val res = Map("FI" -> teksti)
    JavaConversions.mapAsJavaMap(res)
  }

  case class Arvokonvertteri[S, T](konversioMap: Seq[Konversio]) extends Konvertteri[S, T] {

    def konvertoi(arvo: S): (Option[T], Tila) = {
      val konversiot = konversioMap.map(konv => konv.asInstanceOf[Arvokonversio[S,T]])

      konversiot.filter(arvo == _.arvo) match {
        case Nil => (None, new Virhetila(tekstiToMap(s"Arvo $arvo ei täsmää yhteenkään konvertterille määritettyyn arvoon"),
          new ArvokonvertointiVirhe(arvo.toString)))
        case head :: tail => {
          val paluuarvo = head.paluuarvo
          val tila = if (head.hylkaysperuste) {
            new Hylattytila(tekstiryhmaToMap(head.kuvaukset),
              new Arvokonvertterihylkays(arvo.toString), "Arvokonvertterin konvertoi metodissa suoritettu hylkays.")
          } else new Hyvaksyttavissatila

          (Some(paluuarvo), tila)
        }
      }
    }
  }

  case class Lukuarvovalikonvertteri(konversioMap: Seq[Konversio]) extends Konvertteri[BigDecimal, BigDecimal] {

    def konvertoi(arvo: BigDecimal): (Option[BigDecimal], Tila) = {

      val konversiot = konversioMap.map(konv => konv.asInstanceOf[Lukuarvovalikonversio])

      konversiot.sortWith((a, b) => a.max > b.max)
      .filter(konv =>arvo >= konv.min && arvo <= konv.max
      ) match {
        case Nil => (None, new Virhetila(tekstiToMap(s"Arvo $arvo ei täsmää yhteenkään konvertterille määritettyyn arvoväliin"),
          new ArvovalikonvertointiVirhe(arvo.underlying)))
        case head :: tail => {
          val paluuarvo = if (head.palautaHaettuArvo) arvo else head.paluuarvo
          val tila = if (head.hylkaysperuste) {
            new Hylattytila(tekstiryhmaToMap(head.kuvaukset),
              new Arvovalikonvertterihylkays(arvo.underlying, head.min.underlying, head.max.underlying),
              "Arvovälikonvertterin konvertoi metodissa suoritettu hylkays.")
          } else new Hyvaksyttavissatila

          //val tila = new Hyvaksyttavissatila

          (Some(paluuarvo), tila)
        }
      }
    }
  }

  sealed trait Konversio

  case class Arvokonversio[S, T](arvo: S, paluuarvo: T, hylkaysperuste: Boolean, kuvaukset: TekstiRyhma) extends Konversio

  case class ArvokonversioMerkkijonoilla[S, T](arvo: String, paluuarvo: T, hylkaysperuste: String, kuvaukset: TekstiRyhma) extends Konversio

  case class Lukuarvovalikonversio(min: BigDecimal, max: BigDecimal, paluuarvo: BigDecimal,
                                   palautaHaettuArvo: Boolean, hylkaysperuste: Boolean, kuvaukset: TekstiRyhma) extends Konversio

  case class LukuarvovalikonversioMerkkijonoilla(min: String, max: String, paluuarvo: String,
                                   palautaHaettuArvo: String, hylkaysperuste: String, kuvaukset: TekstiRyhma) extends Konversio

  case class KonvertoiLukuarvo(konvertteri: Konvertteri[BigDecimal, BigDecimal], f: Lukuarvofunktio, oid: String = "", tulosTunniste: String = "",
                               tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "")
    extends KonvertoivaFunktio[BigDecimal, BigDecimal] with Lukuarvofunktio

  sealed trait KoostavaFunktio[T] extends Funktio[T] {
    def fs: Seq[Funktio[_]]
    require(fs.size > 0, "Number of function arguments must be greater than zero")
  }

  object KoostavaFunktio {
    def unapply(k: KoostavaFunktio[_]): Option[Seq[Funktio[_]]] = Some(k.fs)
  }
    // Scala 2.9 ratkasu
//  abstract case class NParasta(n: Int, override val fs: Seq[Funktio[BigDecimal]])
//    extends KoostavaFunktio[BigDecimal](fs) with Lukuarvofunktio {
//    require(n <= fs.size, "Parameter n can't be greater than the number of function arguments")
//  }

  sealed trait NParasta extends KoostavaFunktio[BigDecimal] with Lukuarvofunktio {
    def n: Int
    override def fs: Seq[Funktio[BigDecimal]]
    require(n <= fs.size, "Parameter n can't be greater than the number of function arguments")
  }

  object NParasta {
   def unapply(np: NParasta): Option[(Int, Seq[Funktio[BigDecimal]])] = Some((np.n, np.fs))
  }

  sealed trait Ns extends KoostavaFunktio[BigDecimal] with Lukuarvofunktio {
    def ns: Int
    override def fs: Seq[Funktio[BigDecimal]]
    require(ns <= fs.size, "Parameter ns can't be greater than the number of function arguments")
  }

  object Ns {
    def unapply(n: Ns): Option[(Int, Seq[Funktio[BigDecimal]])] = Some((n.ns, n.fs))
  }

  case class Lukuarvo(d: BigDecimal, oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "") extends Lukuarvofunktio

  case class Negaatio(f: Lukuarvofunktio, oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "") extends Lukuarvofunktio

  case class Pyoristys(tarkkuus: Int, f: Lukuarvofunktio, oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "") extends Lukuarvofunktio {
    require(tarkkuus >= 0, "Parameter tarkkuus must be zero or greater")
  }

  case class Summa(fs: Seq[Lukuarvofunktio], oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "")
    extends KoostavaFunktio[BigDecimal] with Lukuarvofunktio

  object Summa {
    def apply(fs: Lukuarvofunktio*) = {
      new Summa(fs.toSeq)
    }
  }

  case class SummaNParasta(n: Int, fs: Seq[Lukuarvofunktio], oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "")
    extends NParasta with Lukuarvofunktio

  object SummaNParasta {
    def apply(n: Int, fs: Lukuarvofunktio*) = {
      new SummaNParasta(n, fs.toSeq)
    }
  }

  case class KeskiarvoNParasta(n: Int, fs: Seq[Lukuarvofunktio], oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "")
    extends NParasta with Lukuarvofunktio

  object KeskiarvoNParasta {
    def apply(n: Int, fs: Lukuarvofunktio*) = {
      new KeskiarvoNParasta(n, fs.toSeq)
    }
  }

  case class Keskiarvo(fs: Seq[Lukuarvofunktio], oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "")
    extends KoostavaFunktio[BigDecimal] with Lukuarvofunktio

  object Keskiarvo {
    def apply(fs: Lukuarvofunktio*) = {
      new Keskiarvo(fs.toSeq)
    }
  }

  case class Mediaani(fs: Seq[Lukuarvofunktio], oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "")
    extends KoostavaFunktio[BigDecimal] with Lukuarvofunktio

  object Mediaani {
    def apply(fs: Lukuarvofunktio*) = {
      new Mediaani(fs.toSeq)
    }
  }

  case class Osamaara(osoittaja: Lukuarvofunktio, nimittaja: Lukuarvofunktio, oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "")
    extends Funktio[BigDecimal] with Lukuarvofunktio {
    val f1 = osoittaja
    val f2 = nimittaja
  }

  case class Minimi(fs: Seq[Lukuarvofunktio], oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "")
    extends KoostavaFunktio[BigDecimal] with Lukuarvofunktio

  object Minimi {
    def apply(fs: Lukuarvofunktio*) = {
      new Minimi(fs.toSeq)
    }
  }

  case class Maksimi(fs: Seq[Lukuarvofunktio], oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "")
    extends KoostavaFunktio[BigDecimal] with Lukuarvofunktio

  object Maksimi {
    def apply(fs: Lukuarvofunktio*) = {
      new Maksimi(fs.toSeq)
    }
  }

  case class NMinimi(ns: Int, fs: Seq[Lukuarvofunktio], oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "")
    extends Ns with Lukuarvofunktio

  object NMinimi {
    def apply(ns: Int, fs: Lukuarvofunktio*) = {
      new NMinimi(ns, fs.toSeq)
    }
  }

  case class NMaksimi(ns: Int, fs: Seq[Lukuarvofunktio], oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "")
    extends Ns with Lukuarvofunktio

  object NMaksimi {
    def apply(ns: Int, fs: Lukuarvofunktio*) = {
      new NMaksimi(ns, fs.toSeq)
    }
  }

  case class Tulo(fs: Seq[Lukuarvofunktio], oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "")
    extends KoostavaFunktio[BigDecimal] with Lukuarvofunktio

  object Tulo {
    def apply(fs: Lukuarvofunktio*) = {
      new Tulo(fs.toSeq)
    }
  }

  case class Jos(ehto: Totuusarvofunktio, ifHaara: Lukuarvofunktio, elseHaara: Lukuarvofunktio, oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "")
    extends Lukuarvofunktio

  sealed trait HaeArvo[T] extends Funktio[T] {
    def oletusarvo: Option[T]
    def valintaperusteviite: Valintaperuste
  }

  object HaeArvo {
    def unapply(ha: HaeArvo[_]): Option[(Option[_], Valintaperuste)] = Some((ha.oletusarvo, ha.valintaperusteviite))
  }

  case class HaeMerkkijonoJaKonvertoiLukuarvoksi(konvertteri: Konvertteri[String, BigDecimal],
                                                 oletusarvo: Option[BigDecimal],
                                                 valintaperusteviite: Valintaperuste,
                                                 oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "")
    extends HaeArvo[BigDecimal] with Lukuarvofunktio

  case class HaeMerkkijonoJaKonvertoiTotuusarvoksi(konvertteri: Konvertteri[String, Boolean],
                                                   oletusarvo: Option[Boolean],
                                                   valintaperusteviite: Valintaperuste,
                                                   oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "")
    extends HaeArvo[Boolean] with Totuusarvofunktio

  case class HaeTotuusarvo(konvertteri: Option[Konvertteri[Boolean, Boolean]],
                           oletusarvo: Option[Boolean],
                           valintaperusteviite: Valintaperuste, oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "")
    extends HaeArvo[Boolean] with Totuusarvofunktio

  case class HaeLukuarvo(konvertteri: Option[Konvertteri[BigDecimal, BigDecimal]],
                         oletusarvo: Option[BigDecimal],
                         valintaperusteviite: Valintaperuste, oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "")
    extends HaeArvo[BigDecimal] with Lukuarvofunktio

  case class HaeLukuarvoEhdolla(konvertteri: Option[Konvertteri[BigDecimal, BigDecimal]],
                         oletusarvo: Option[BigDecimal],
                         valintaperusteviite: Valintaperuste, ehto: Valintaperuste, oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "")
    extends HaeArvo[BigDecimal] with Lukuarvofunktio

  case class HaeMerkkijonoJaVertaaYhtasuuruus(oletusarvo: Option[Boolean],
                                              valintaperusteviite: Valintaperuste,
                                              vertailtava: String, oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "")
    extends HaeArvo[Boolean] with Totuusarvofunktio

  // Boolean-funktiot
  case class Ja(fs: Seq[Totuusarvofunktio], oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "")
    extends KoostavaFunktio[Boolean] with Totuusarvofunktio

  object Ja {
    def apply(fs: Totuusarvofunktio*) = {
      new Ja(fs.toSeq)
    }
  }

  case class Tai(fs: Seq[Totuusarvofunktio], oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "")
    extends KoostavaFunktio[Boolean] with Totuusarvofunktio

  object Tai {
    def apply(fs: Totuusarvofunktio*) = {
      new Tai(fs.toSeq)
    }
  }

  case class Ei(f: Totuusarvofunktio, oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "") extends Totuusarvofunktio

  case class Suurempi(f1: Lukuarvofunktio, f2: Lukuarvofunktio, oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "")
    extends Totuusarvofunktio

  case class SuurempiTaiYhtasuuri(f1: Lukuarvofunktio, f2: Lukuarvofunktio, oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "")
    extends Totuusarvofunktio

  case class Pienempi(f1: Lukuarvofunktio, f2: Lukuarvofunktio, oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "")
    extends Totuusarvofunktio

  case class PienempiTaiYhtasuuri(f1: Lukuarvofunktio, f2: Lukuarvofunktio, oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "")
    extends Totuusarvofunktio

  case class Yhtasuuri(f1: Lukuarvofunktio, f2: Lukuarvofunktio, oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "")
    extends Totuusarvofunktio

  case class Totuusarvo(b: Boolean, oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "") extends Totuusarvofunktio

  sealed trait Nimetty[T] extends Funktio[T] {
    def nimi: String
    def f: Funktio[T]
    require(StringUtils.isNotBlank(nimi), "Nimi cannot be null or empty")
  }

  object Nimetty {
    def unapply(n: Nimetty[_]): Option[(String, Funktio[_])] = Some((n.nimi, n.f))
  }

  case class NimettyTotuusarvo(nimi: String, f: Totuusarvofunktio, oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "")
    extends Nimetty[Boolean] with Totuusarvofunktio

  case class NimettyLukuarvo(nimi: String, f: Lukuarvofunktio, oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "")
    extends Nimetty[BigDecimal] with Lukuarvofunktio

  case class Hakutoive(n: Int, oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "") extends Totuusarvofunktio {
    require(n > 0, "n must be greater than zero")
  }

  case class Hylkaa(f: Totuusarvofunktio, hylkaysperustekuvaus: Option[Map[String,String]] = None, oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "")
    extends Lukuarvofunktio


  case class Demografia(oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "", tunniste: String, prosenttiosuus: BigDecimal) extends Totuusarvofunktio

  case class Skaalaus(oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "", skaalattava: Lukuarvofunktio, kohdeskaala: Pair[BigDecimal, BigDecimal],
                      lahdeskaala: Option[Pair[BigDecimal, BigDecimal]]) extends Lukuarvofunktio {
    require(kohdeskaala._1 < kohdeskaala._2, "Kohdeskaalan minimin pitää olla pienempi kuin maksimi")
    require(lahdeskaala.isEmpty || lahdeskaala.get._1 < lahdeskaala.get._2,
      "Lähdeskaalan minimin pitää olla pienempi kuin maksimi")
  }

  case class PainotettuKeskiarvo(oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "", fs: Seq[Pair[Lukuarvofunktio, Lukuarvofunktio]]) extends Lukuarvofunktio {
    require(fs.size > 0, "Parametreja pitää olla vähintään yksi")
  }

  case class Valintaperusteyhtasuuruus(oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "",
                                       valintaperusteet: Pair[Valintaperuste, Valintaperuste]) extends Totuusarvofunktio

  case class HylkaaArvovalilla(f: Lukuarvofunktio, hylkaysperustekuvaus: Option[Map[String,String]] = None, oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "", arvovali: Pair[String, String])
    extends Lukuarvofunktio {
    //require(arvovali._1 < arvovali._2, "Arvovälin minimin pitää olla pienempi kuin maksimi")
  }

}
