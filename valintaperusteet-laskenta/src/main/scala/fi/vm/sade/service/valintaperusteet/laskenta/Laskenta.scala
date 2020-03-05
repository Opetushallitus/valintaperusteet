package fi.vm.sade.service.valintaperusteet.laskenta

import fi.vm.sade.service.valintaperusteet.laskenta.api.tila._
import fi.vm.sade.service.valintaperusteet.model.LokalisoituTeksti
import fi.vm.sade.service.valintaperusteet.model.TekstiRyhma
import org.apache.commons.lang.StringUtils

import scala.jdk.CollectionConverters._

object Laskenta {

  trait Valintaperuste {
    val tunniste: String
    val pakollinen: Boolean
  }

  case class HakemuksenValintaperuste(tunniste: String, pakollinen: Boolean) extends Valintaperuste

  case class SyotettavaValintaperuste(tunniste: String, pakollinen: Boolean,
                                      osallistuminenTunniste: String, kuvaus: String = "", kuvaukset: TekstiRyhma,
                                      vaatiiOsallistumisen: Boolean = true, syotettavissaKaikille: Boolean = true,
                                      tyypinKoodiUri: Option[String] = None, tilastoidaan: Boolean = false,
                                      ammatillisenKielikoeOsallistuminenSpecialHandling: Boolean = false) extends Valintaperuste

  case class HakukohteenValintaperuste(tunniste: String, pakollinen: Boolean, epasuoraViittaus: Boolean) extends Valintaperuste

  case class HakukohteenSyotettavaValintaperuste(tunniste: String, pakollinen: Boolean, epasuoraViittaus: Boolean,
                                                 osallistuminenTunniste: String, kuvaus: String = "", kuvaukset: TekstiRyhma,
                                                 vaatiiOsallistumisen: Boolean = true, syotettavissaKaikille: Boolean = true,
                                                 tyypinKoodiUri: Option[String] = None, tilastoidaan: Boolean = false) extends Valintaperuste

  trait Konvertteri[S, T] {
    def konvertoi(arvo: S): (Option[T], Tila)
  }

  trait KonvertoivaFunktio[S, T] extends Funktio[T] {
    val f: Funktio[S]
    val konvertteri: Konvertteri[S, T]
  }

  def tekstiryhmaToMap(ryhma: TekstiRyhma): java.util.Map[String,String] = {
    if(ryhma != null && ryhma.getTekstit != null) {
      val tekstit = ryhma.getTekstit.asScala.toSet
      val res = tekstit.foldLeft(Map.empty[String,String]){
        (result: Map[String,String],teksti: LokalisoituTeksti) => result + (teksti.getKieli.name -> teksti.getTeksti)
      }
      res.asJava
    } else {
      val res = Map("FI" -> "Valintaperusteelle ei oltu määritelty hylkäysperustekuvauksia")
      res.asJava
    }
  }

  def tekstiToMap(teksti: String): java.util.Map[String,String] = {
    val res = Map("FI" -> teksti)
    res.asJava
  }

  case class Arvokonvertteri[S, T](konversioMap: Seq[Konversio]) extends Konvertteri[S, T] {

    def konvertoi(arvo: S): (Option[T], Tila) = {
      val konversiot = konversioMap.map(konv => konv.asInstanceOf[Arvokonversio[S,T]])

      konversiot.filter(arvo == _.arvo) match {
        case Nil => (None, new Virhetila(tekstiToMap(s"Arvo $arvo ei täsmää yhteenkään konvertterille määritettyyn arvoon"),
          new ArvokonvertointiVirhe(arvo.toString)))
        case head :: _ =>
          val paluuarvo = head.paluuarvo
          val tila = if (head.hylkaysperuste) {
            new Hylattytila(tekstiryhmaToMap(head.kuvaukset),
              new Arvokonvertterihylkays(arvo.toString), "Arvokonvertterin konvertoi metodissa suoritettu hylkays.")
          } else new Hyvaksyttavissatila

          (Some(paluuarvo), tila)
      }
    }
  }

  case class Lukuarvovalikonvertteri(konversioMap: Seq[Konversio]) extends Konvertteri[BigDecimal, BigDecimal] {

    def konvertoi(arvo: BigDecimal): (Option[BigDecimal], Tila) = {

      val konversiot = konversioMap.map(konv => konv.asInstanceOf[Lukuarvovalikonversio])

      konversiot
        .filter(konv => arvo >= konv.min && arvo <= konv.max
        ).sortWith((a, b) => a.max > b.max) match {
        case Nil => (None, new Virhetila(tekstiToMap(s"Arvo $arvo ei täsmää yhteenkään konvertterille määritettyyn arvoväliin"),
          new ArvovalikonvertointiVirhe(arvo.underlying)))
        case head :: _ =>
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

  sealed trait Konversio

  case class Arvokonversio[S, T](arvo: S, paluuarvo: T, hylkaysperuste: Boolean, kuvaukset: TekstiRyhma) extends Konversio

  case class ArvokonversioMerkkijonoilla[S, T](arvo: String, paluuarvo: T, hylkaysperuste: String, kuvaukset: TekstiRyhma) extends Konversio

  case class Lukuarvovalikonversio(min: BigDecimal, max: BigDecimal, paluuarvo: BigDecimal,
                                   palautaHaettuArvo: Boolean, hylkaysperuste: Boolean, kuvaukset: TekstiRyhma) extends Konversio

  case class LukuarvovalikonversioMerkkijonoilla(min: String, max: String, paluuarvo: String,
                                   palautaHaettuArvo: String, hylkaysperuste: String, kuvaukset: TekstiRyhma) extends Konversio

  case class KonvertoiLukuarvo(konvertteri: Konvertteri[BigDecimal, BigDecimal], f: Lukuarvofunktio, oid: String = "", tulosTunniste: String = "",
                               tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "", omaopintopolku: Boolean = false)
    extends KonvertoivaFunktio[BigDecimal, BigDecimal] with Lukuarvofunktio

  sealed trait KoostavaFunktio[T] extends Funktio[T] {
    def fs: Seq[Funktio[_]]
    require(fs.nonEmpty, "Number of function arguments must be greater than zero")
  }

  object KoostavaFunktio {
    def unapply(k: KoostavaFunktio[_]): Option[Seq[Funktio[_]]] = Some(k.fs)
  }

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

  case class Lukuarvo(d: BigDecimal, oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "", omaopintopolku: Boolean = false) extends Lukuarvofunktio

  case class Negaatio(f: Lukuarvofunktio, oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "", omaopintopolku: Boolean = false) extends Lukuarvofunktio

  case class Pyoristys(tarkkuus: Int, f: Lukuarvofunktio, oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "", omaopintopolku: Boolean = false) extends Lukuarvofunktio {
    require(tarkkuus >= 0, "Parameter tarkkuus must be zero or greater")
  }

  case class Summa(fs: Seq[Lukuarvofunktio], oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "", omaopintopolku: Boolean = false)
    extends KoostavaFunktio[BigDecimal] with Lukuarvofunktio

  object Summa {
    def apply(fs: Lukuarvofunktio*): Summa = {
      new Summa(fs.toArray.toSeq)
    }
  }

  case class SummaNParasta(n: Int, fs: Seq[Lukuarvofunktio], oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "", omaopintopolku: Boolean = false)
    extends NParasta with Lukuarvofunktio

  object SummaNParasta {
    def apply(n: Int, fs: Lukuarvofunktio*): SummaNParasta = {
      new SummaNParasta(n, fs.toArray.toSeq)
    }
  }

  case class TuloNParasta(n: Int, fs: Seq[Lukuarvofunktio], oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "", omaopintopolku: Boolean = false)
    extends NParasta with Lukuarvofunktio

  object TuloNParasta {
    def apply(n: Int, fs: Lukuarvofunktio*): TuloNParasta = {
      new TuloNParasta(n, fs.toArray.toSeq)
    }
  }
  case class KeskiarvoNParasta(n: Int, fs: Seq[Lukuarvofunktio], oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "", omaopintopolku: Boolean = false)
    extends NParasta with Lukuarvofunktio

  object KeskiarvoNParasta {
    def apply(n: Int, fs: Lukuarvofunktio*): KeskiarvoNParasta = {
      new KeskiarvoNParasta(n, fs.toArray.toSeq)
    }
  }

  case class Keskiarvo(fs: Seq[Lukuarvofunktio], oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "", omaopintopolku: Boolean = false)
    extends KoostavaFunktio[BigDecimal] with Lukuarvofunktio

  object Keskiarvo {
    def apply(fs: Lukuarvofunktio*): Keskiarvo = {
      new Keskiarvo(fs.toArray.toSeq)
    }
  }

  case class Mediaani(fs: Seq[Lukuarvofunktio], oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "", omaopintopolku: Boolean = false)
    extends KoostavaFunktio[BigDecimal] with Lukuarvofunktio

  object Mediaani {
    def apply(fs: Lukuarvofunktio*): Mediaani = {
      new Mediaani(fs.toArray.toSeq)
    }
  }

  case class Osamaara(osoittaja: Lukuarvofunktio, nimittaja: Lukuarvofunktio, oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "", omaopintopolku: Boolean = false)
    extends Funktio[BigDecimal] with Lukuarvofunktio {
    val f1: Lukuarvofunktio = osoittaja
    val f2: Lukuarvofunktio = nimittaja
  }

  case class Minimi(fs: Seq[Lukuarvofunktio], oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "", omaopintopolku: Boolean = false)
    extends KoostavaFunktio[BigDecimal] with Lukuarvofunktio

  object Minimi {
    def apply(fs: Lukuarvofunktio*): Minimi = {
      new Minimi(fs.toArray.toSeq)
    }
  }

  case class Maksimi(fs: Seq[Lukuarvofunktio], oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "", omaopintopolku: Boolean = false)
    extends KoostavaFunktio[BigDecimal] with Lukuarvofunktio

  object Maksimi {
    def apply(fs: Lukuarvofunktio*): Maksimi = {
      new Maksimi(fs.toArray.toSeq)
    }
  }

  case class NMinimi(ns: Int, fs: Seq[Lukuarvofunktio], oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "", omaopintopolku: Boolean = false)
    extends Ns with Lukuarvofunktio

  object NMinimi {
    def apply(ns: Int, fs: Lukuarvofunktio*): NMinimi = {
      new NMinimi(ns, fs.toArray.toSeq)
    }
  }

  case class NMaksimi(ns: Int, fs: Seq[Lukuarvofunktio], oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "", omaopintopolku: Boolean = false)
    extends Ns with Lukuarvofunktio

  object NMaksimi {
    def apply(ns: Int, fs: Lukuarvofunktio*): NMaksimi = {
      new NMaksimi(ns, fs.toArray.toSeq)
    }
  }

  case class Tulo(fs: Seq[Lukuarvofunktio], oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "", omaopintopolku: Boolean = false)
    extends KoostavaFunktio[BigDecimal] with Lukuarvofunktio

  object Tulo {
    def apply(fs: Lukuarvofunktio*): Tulo = {
      new Tulo(fs.toArray.toSeq)
    }
  }

  case class Jos(ehto: Totuusarvofunktio, ifHaara: Lukuarvofunktio, elseHaara: Lukuarvofunktio, oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "", omaopintopolku: Boolean = false)
    extends Lukuarvofunktio

  sealed trait HaeArvo[T] extends Funktio[T] {
    def oletusarvo: Option[T]
    def valintaperusteviite: Valintaperuste
  }

  object HaeArvo {
    def unapply(ha: HaeArvo[_]): Option[(Option[_], Valintaperuste)] = Some((ha.oletusarvo, ha.valintaperusteviite))
  }

  case class YoEhdot(
    alkuvuosi: Option[Int],
    loppuvuosi: Option[Int],
    alkulukukausi: Option[Int],
    loppulukukausi: Option[Int],
    vainValmistuneet: Boolean,
    rooli: Option[String]
  ) {
    final val YO_ORDER = List("L", "E", "M", "C", "B", "A", "I")
  }

  case class HaeYoArvosana(konvertteri: Konvertteri[String, BigDecimal],
                           ehdot: YoEhdot,
                           oletusarvo: Option[BigDecimal],
                           valintaperusteviite: Valintaperuste,
                           oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "", omaopintopolku: Boolean = false)
    extends HaeArvo[BigDecimal] with Lukuarvofunktio

  case class HaeYoPisteet(konvertteri: Option[Konvertteri[BigDecimal, BigDecimal]],
                          ehdot: YoEhdot,
                          oletusarvo: Option[BigDecimal],
                          valintaperusteviite: Valintaperuste,oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "", omaopintopolku: Boolean = false)
    extends HaeArvo[BigDecimal] with Lukuarvofunktio

  case class IteroiAmmatillisetTutkinnot(f: Lukuarvofunktio,
                                         oid: String = "",
                                         tulosTunniste: String = "",
                                         tulosTekstiFi: String = "",
                                         tulosTekstiSv: String = "",
                                         tulosTekstiEn: String = "",
                                         omaopintopolku: Boolean = false
                                        ) extends Lukuarvofunktio

  case class IteroiAmmatillisetTutkinnonOsat(f: Lukuarvofunktio,
                                             oid: String = "",
                                             tulosTunniste: String = "",
                                             tulosTekstiFi: String = "",
                                             tulosTekstiSv: String = "",
                                             tulosTekstiEn: String = "",
                                             omaopintopolku: Boolean = false
                                        ) extends Lukuarvofunktio

  case class IteroiAmmatillisenTutkinnonYtoOsaAlueet(f: Lukuarvofunktio,
                                                     valintaperusteviite: Valintaperuste,
                                                     oid: String = "",
                                                     tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "", omaopintopolku: Boolean = false) extends Lukuarvofunktio

  case class HaeAmmatillisenTutkinnonYtoOsaAlueenArvosana(konvertteri: Option[Konvertteri[BigDecimal, BigDecimal]], oletusarvo: Option[BigDecimal], oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "", omaopintopolku: Boolean = false)
    extends Lukuarvofunktio {

    override val iteraatioParametrinTyyppi: Option[Class[_ <: IteraatioParametri]] = Some(classOf[AmmatillisenPerustutkinnonValitsija])
  }

  case class HaeAmmatillisenTutkinnonYtoOsaAlueenLaajuus(konvertteri: Option[Konvertteri[BigDecimal, BigDecimal]], oletusarvo: Option[BigDecimal], oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "", omaopintopolku: Boolean = false)
    extends Lukuarvofunktio {

    override val iteraatioParametrinTyyppi: Option[Class[_ <: IteraatioParametri]] = Some(classOf[AmmatillisenPerustutkinnonValitsija])
  }

  case class HaeAmmatillinenYtoArvosana(konvertteri: Option[Konvertteri[BigDecimal, BigDecimal]],
                                        oletusarvo: Option[BigDecimal],
                                        valintaperusteviite: Valintaperuste,
                                        oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "", omaopintopolku: Boolean = false)
    extends HaeArvo[BigDecimal] with Lukuarvofunktio {

    override val iteraatioParametrinTyyppi: Option[Class[_ <: IteraatioParametri]] = Some(classOf[AmmatillisenPerustutkinnonValitsija])
  }

  case class HaeAmmatillinenYtoArviointiAsteikko(konvertteri: Konvertteri[String, BigDecimal],
                                                 oletusarvo: Option[BigDecimal],
                                                 valintaperusteviite: Valintaperuste,
                                                 oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "", omaopintopolku: Boolean = false)
    extends HaeArvo[BigDecimal] with Lukuarvofunktio {

    override val iteraatioParametrinTyyppi: Option[Class[_ <: IteraatioParametri]] = Some(classOf[AmmatillisenPerustutkinnonValitsija])
  }

  case class HaeAmmatillisenTutkinnonOsanLaajuus(konvertteri: Option[Konvertteri[BigDecimal, BigDecimal]],
                                                 oletusarvo: Option[BigDecimal],
                                                 oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "", omaopintopolku: Boolean = false)
    extends Lukuarvofunktio {

    override val iteraatioParametrinTyyppi: Option[Class[_ <: IteraatioParametri]] = Some(classOf[AmmatillisenTutkinnonOsanValitsija])
  }

  case class HaeAmmatillisenTutkinnonOsanArvosana(konvertteri: Option[Konvertteri[BigDecimal, BigDecimal]],
                                                  oid: String = "",
                                                  tulosTunniste: String = "",
                                                  tulosTekstiFi: String = "",
                                                  tulosTekstiSv: String = "",
                                                  tulosTekstiEn: String = "",
                                                  omaopintopolku: Boolean = false)
    extends Lukuarvofunktio {

    override val iteraatioParametrinTyyppi: Option[Class[_ <: IteraatioParametri]] = Some(classOf[AmmatillisenTutkinnonOsanValitsija])
  }

  case class HaeAmmatillisenTutkinnonKeskiarvo(konvertteri: Option[Konvertteri[BigDecimal, BigDecimal]],
                                                    oid: String = "",
                                                    tulosTunniste: String = "",
                                                    tulosTekstiFi: String = "",
                                                    tulosTekstiSv: String = "",
                                                    tulosTekstiEn: String = "",
                                                    omaopintopolku: Boolean = false)
    extends Lukuarvofunktio {

    override val iteraatioParametrinTyyppi: Option[Class[_ <: IteraatioParametri]] = Some(classOf[AmmatillisenPerustutkinnonValitsija])
  }

  case class HaeAmmatillisenTutkinnonSuoritustapa(konvertteri: Konvertteri[String, BigDecimal],
                                                  oletusarvo: Option[BigDecimal],
                                                  oid: String = "",
                                                  tulosTunniste: String = "",
                                                  tulosTekstiFi: String = "",
                                                  tulosTekstiSv: String = "",
                                                  tulosTekstiEn: String = "",
                                                  omaopintopolku: Boolean = false)
    extends Lukuarvofunktio {

    override val iteraatioParametrinTyyppi: Option[Class[_ <: IteraatioParametri]] = Some(classOf[AmmatillisenPerustutkinnonValitsija])
  }

  case class HaeMerkkijonoJaKonvertoiLukuarvoksi(konvertteri: Konvertteri[String, BigDecimal],
                                                 oletusarvo: Option[BigDecimal],
                                                 valintaperusteviite: Valintaperuste,
                                                 oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "", omaopintopolku: Boolean = false)
    extends HaeArvo[BigDecimal] with Lukuarvofunktio

  case class HaeTotuusarvoJaKonvertoiLukuarvoksi(konvertteri: Konvertteri[Boolean, BigDecimal],
                                                 oletusarvo: Option[BigDecimal],
                                                 valintaperusteviite: Valintaperuste,
                                                 oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "", omaopintopolku: Boolean = false)
    extends HaeArvo[BigDecimal] with Lukuarvofunktio

  case class HaeMerkkijonoJaKonvertoiTotuusarvoksi(konvertteri: Konvertteri[String, Boolean],
                                                   oletusarvo: Option[Boolean],
                                                   valintaperusteviite: Valintaperuste,
                                                   oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "", omaopintopolku: Boolean = false)
    extends HaeArvo[Boolean] with Totuusarvofunktio

  case class HaeTotuusarvo(konvertteri: Option[Konvertteri[Boolean, Boolean]],
                           oletusarvo: Option[Boolean],
                           valintaperusteviite: Valintaperuste, oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "", omaopintopolku: Boolean = false)
    extends HaeArvo[Boolean] with Totuusarvofunktio

  case class HaeLukuarvo(konvertteri: Option[Konvertteri[BigDecimal, BigDecimal]],
                         oletusarvo: Option[BigDecimal],
                         valintaperusteviite: Valintaperuste, oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "", omaopintopolku: Boolean = false)
    extends HaeArvo[BigDecimal] with Lukuarvofunktio

  case class HaeLukuarvoEhdolla(konvertteri: Option[Konvertteri[BigDecimal, BigDecimal]],
                         oletusarvo: Option[BigDecimal],
                         valintaperusteviite: Valintaperuste, ehto: Valintaperuste, oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "", omaopintopolku: Boolean = false)
    extends HaeArvo[BigDecimal] with Lukuarvofunktio

  case class HaeMerkkijonoJaVertaaYhtasuuruus(oletusarvo: Option[Boolean],
                                              valintaperusteviite: Valintaperuste,
                                              vertailtava: String, oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "", omaopintopolku: Boolean = false)
    extends HaeArvo[Boolean] with Totuusarvofunktio

  // Boolean-funktiot
  case class Ja(fs: Seq[Totuusarvofunktio], oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "", omaopintopolku: Boolean = false)
    extends KoostavaFunktio[Boolean] with Totuusarvofunktio

  object Ja {
    def apply(fs: Totuusarvofunktio*): Ja = {
      new Ja(fs.toArray.toSeq)
    }
  }

  case class Tai(fs: Seq[Totuusarvofunktio], oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "", omaopintopolku: Boolean = false)
    extends KoostavaFunktio[Boolean] with Totuusarvofunktio

  object Tai {
    def apply(fs: Totuusarvofunktio*): Tai = {
      new Tai(fs.toArray.toSeq)
    }
  }

  case class Ei(f: Totuusarvofunktio, oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "", omaopintopolku: Boolean = false) extends Totuusarvofunktio

  case class Suurempi(f1: Lukuarvofunktio, f2: Lukuarvofunktio, oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "", omaopintopolku: Boolean = false)
    extends Totuusarvofunktio

  case class SuurempiTaiYhtasuuri(f1: Lukuarvofunktio, f2: Lukuarvofunktio, oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "", omaopintopolku: Boolean = false)
    extends Totuusarvofunktio

  case class Pienempi(f1: Lukuarvofunktio, f2: Lukuarvofunktio, oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "", omaopintopolku: Boolean = false)
    extends Totuusarvofunktio

  case class PienempiTaiYhtasuuri(f1: Lukuarvofunktio, f2: Lukuarvofunktio, oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "", omaopintopolku: Boolean = false)
    extends Totuusarvofunktio

  case class Yhtasuuri(f1: Lukuarvofunktio, f2: Lukuarvofunktio, oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "", omaopintopolku: Boolean = false)
    extends Totuusarvofunktio

  case class Totuusarvo(b: Boolean, oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "", omaopintopolku: Boolean = false) extends Totuusarvofunktio

  sealed trait Nimetty[T] extends Funktio[T] {
    def nimi: String
    def f: Funktio[T]
    require(StringUtils.isNotBlank(nimi), "Nimi cannot be null or empty")
  }

  object Nimetty {
    def unapply(n: Nimetty[_]): Option[(String, Funktio[_])] = Some((n.nimi, n.f))
  }

  case class NimettyTotuusarvo(nimi: String, f: Totuusarvofunktio, oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "", omaopintopolku: Boolean = false)
    extends Nimetty[Boolean] with Totuusarvofunktio

  case class NimettyLukuarvo(nimi: String, f: Lukuarvofunktio, oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "", omaopintopolku: Boolean = false)
    extends Nimetty[BigDecimal] with Lukuarvofunktio

  case class Hakutoive(n: Int, oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "", omaopintopolku: Boolean = false) extends Totuusarvofunktio {
    require(n > 0, "n must be greater than zero")
  }

  case class HakutoiveRyhmassa(n: Int, ryhmaOid: String, oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "", omaopintopolku: Boolean = false) extends Totuusarvofunktio {
    require(n > 0, "n must be greater than zero")
    require(StringUtils.isNotBlank(ryhmaOid), "Nimi cannot be null or empty")
  }

  case class Hakukelpoisuus(oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "", omaopintopolku: Boolean = false) extends Totuusarvofunktio {

  }

  case class Hylkaa(f: Totuusarvofunktio, hylkaysperustekuvaus: Option[Map[String,String]] = None, oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "", omaopintopolku: Boolean = false)
    extends Lukuarvofunktio

  case class Demografia(oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "", omaopintopolku: Boolean = false, tunniste: String, prosenttiosuus: BigDecimal) extends Totuusarvofunktio

  case class Skaalaus(oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "", omaopintopolku: Boolean = false, skaalattava: Lukuarvofunktio, kohdeskaala: Tuple2[BigDecimal, BigDecimal],
                      lahdeskaala: Option[Tuple2[BigDecimal, BigDecimal]]) extends Lukuarvofunktio {
    require(kohdeskaala._1 < kohdeskaala._2, "Kohdeskaalan minimin pitää olla pienempi kuin maksimi")
    require(lahdeskaala.isEmpty || lahdeskaala.get._1 < lahdeskaala.get._2,
      "Lähdeskaalan minimin pitää olla pienempi kuin maksimi")
  }

  case class PainotettuKeskiarvo(oid: String = "",
                                 tulosTunniste: String = "",
                                 tulosTekstiFi: String = "",
                                 tulosTekstiSv: String = "",
                                 tulosTekstiEn: String = "",
                                 omaopintopolku: Boolean = false,
                                 fs: Seq[Tuple2[Lukuarvofunktio, Lukuarvofunktio]]
                                ) extends Lukuarvofunktio {
    require(fs.nonEmpty, "Parametreja pitää olla vähintään yksi")
  }

  case class Valintaperusteyhtasuuruus(oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "", omaopintopolku: Boolean = false,
                                       valintaperusteet: Tuple2[Valintaperuste, Valintaperuste]) extends Totuusarvofunktio

  case class HylkaaArvovalilla(f: Lukuarvofunktio, hylkaysperustekuvaus: Option[Map[String,String]] = None, oid: String = "", tulosTunniste: String = "", tulosTekstiFi: String = "", tulosTekstiSv: String = "", tulosTekstiEn: String = "", omaopintopolku: Boolean = false, arvovali: Tuple2[String, String])
    extends Lukuarvofunktio
}
