package fi.vm.sade.kaava

import fi.vm.sade.service.valintaperusteet.model._
import fi.vm.sade.service.valintaperusteet.dto.model._
import fi.vm.sade.service.valintaperusteet.laskenta._
import Laskenta._
import Laskenta.{HakukohteenValintaperuste => HkValintaperuste}
import Laskenta.{HakukohteenSyotettavaValintaperuste => HksValintaperuste}
import org.apache.commons.lang.StringUtils
import java.math.{BigDecimal => JBigDecimal}
import java.util.{Set => JSet}
import scala.math.BigDecimal._
import scala._
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.KonvertoiLukuarvo
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.Negaatio
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.Totuusarvo
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.Osamaara
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.Pyoristys
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.Arvokonvertteri
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.Suurempi
import scala.Some
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.Yhtasuuri
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.Jos
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.PienempiTaiYhtasuuri
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.HaeMerkkijonoJaKonvertoiTotuusarvoksi
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.Demografia
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.HaeMerkkijonoJaKonvertoiLukuarvoksi
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.PainotettuKeskiarvo
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.Lukuarvo
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.Pienempi
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.Valintaperusteyhtasuuruus
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.HylkaaArvovalilla
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.HakemuksenValintaperuste
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.NimettyTotuusarvo
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.HaeLukuarvo
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.NimettyLukuarvo
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.Skaalaus
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.SyotettavaValintaperuste
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.Lukuarvovalikonvertteri
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.HaeTotuusarvo
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.SuurempiTaiYhtasuuri
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.Hylkaa
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.HaeMerkkijonoJaVertaaYhtasuuruus
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.Ei
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.Hakutoive
import fi.vm.sade.service.valintaperusteet.service.validointi.virhe.LaskentakaavaEiOleValidiException
import fi.vm.sade.service.valintaperusteet.dto.model.{Funktionimi, Valintaperustelahde}
import scala.collection.JavaConversions

/**
 * User: kwuoti
 * Date: 21.1.2013
 * Time: 12.12
 */
object Laskentadomainkonvertteri {

  import scala.collection.JavaConversions._

  private def getParametri(avain: String, params: JSet[Syoteparametri]): Syoteparametri = {
    params.filter(_.getAvain == avain).toList match {
      case Nil => sys.error(s"Could not find parameter matching the key $avain")
      case head :: tail => head
    }
  }

  private def parametriToBigDecimal(param: Syoteparametri) = {
    try {
      BigDecimal(param.getArvo)
    } catch {
      case e: Throwable => sys.error(s"Could not interpret parameter ${param.getAvain} value ${param.getArvo} as big decimal")
    }
  }

  private def parametriToInteger(param: Syoteparametri) = {
    try {
      param.getArvo.toInt
    } catch {
      case e: Throwable => sys.error(s"Could not interpret parameter ${param.getAvain} value ${param.getArvo} as integer")
    }
  }

  private def parametriToBoolean(param: Syoteparametri) = {
    try {
      param.getArvo.toBoolean
    } catch {
      case e: Throwable => sys.error(s"Could not interpret parameter ${param.getAvain} value ${param.getArvo} as boolean")
    }
  }

  private def muunnaTotuusarvofunktioksi(f: Funktio[_]): Totuusarvofunktio = {
    f match {
      case tf: Totuusarvofunktio => tf
      case _ => sys.error("Cannot cast funktio to Totuusarvofunktio")
    }
  }

  private def muunnaLukuarvofunktioksi(f: Funktio[_]): Lukuarvofunktio = {
    f match {
      case lf: Lukuarvofunktio => lf
      case _ => sys.error("Cannot cast funktio to Lukuarvofunktio")
    }
  }

  def muodostaLukuarvolasku(funktiokutsu: Funktiokutsu): Lukuarvofunktio = {
    val lasku: Funktio[_] = muodostaLasku(funktiokutsu)
    muunnaLukuarvofunktioksi(lasku)
  }

  def muodostaTotuusarvolasku(funktiokutsu: Funktiokutsu): Totuusarvofunktio = {
    val lasku: Funktio[_] = muodostaLasku(funktiokutsu)
    muunnaTotuusarvofunktioksi(lasku)
  }

  private def luoValintaperusteviite(valintaperuste: ValintaperusteViite): Valintaperuste = {
    valintaperuste.getLahde match {
      case Valintaperustelahde.HAETTAVA_ARVO => HakemuksenValintaperuste(valintaperuste.getTunniste,
        valintaperuste.getOnPakollinen)
      case Valintaperustelahde.HAKUKOHTEEN_ARVO =>
        HkValintaperuste(valintaperuste.getTunniste, valintaperuste.getOnPakollinen, Option(valintaperuste.getEpasuoraViittaus).map(Boolean2boolean(_)).getOrElse(false))
      case Valintaperustelahde.HAKUKOHTEEN_SYOTETTAVA_ARVO =>
        HksValintaperuste(valintaperuste.getTunniste, valintaperuste.getOnPakollinen, Option(valintaperuste.getEpasuoraViittaus).map(Boolean2boolean(_)).getOrElse(false), ValintaperusteViite.OSALLISTUMINEN_POSTFIX)
      case Valintaperustelahde.SYOTETTAVA_ARVO => SyotettavaValintaperuste(valintaperuste.getTunniste,
        valintaperuste.getOnPakollinen, valintaperuste.getOsallistuminenTunniste, valintaperuste.getKuvaus)
    }
  }

  private def muodostaLasku(funktiokutsu: Funktiokutsu): Funktio[_] = {
    if (!Laskentakaavavalidaattori.onkoLaskettavaKaavaValidi(funktiokutsu)) {
      throw new LaskentakaavaEiOleValidiException("Funktiokutsu ei ole validi")
    }

    val jarjestetytArgumentit: List[Funktioargumentti] = LaskentaUtil.jarjestaFunktioargumentit(funktiokutsu.getFunktioargumentit)
    val lasketutArgumentit: List[Funktio[_]] = jarjestetytArgumentit.map((arg: Funktioargumentti) => muodostaLasku(arg.getFunktiokutsuChild))

    val funktiokuvaus = Funktiokuvaaja.annaFunktiokuvaus(funktiokutsu.getFunktionimi)._2

    val oid = if (funktiokutsu.getId != null) funktiokutsu.getId.toString else ""
    val tulosTunniste = if (funktiokutsu.getTallennaTulos()) funktiokutsu.getTulosTunniste() else ""
    val tulosTekstiFi = if(funktiokutsu.getTulosTekstiFi != null) funktiokutsu.getTulosTekstiFi else ""
    val tulosTekstiSv = if(funktiokutsu.getTulosTekstiSv != null) funktiokutsu.getTulosTekstiSv else ""
    val tulosTekstiEn = if(funktiokutsu.getTulosTekstiEn != null) funktiokutsu.getTulosTekstiEn else ""

    val valintaperusteviitteet = funktiokutsu.getValintaperusteviitteet.toList.sortWith(_.getIndeksi < _.getIndeksi).map(luoValintaperusteviite(_))

    funktiokutsu.getFunktionimi match {
      case Funktionimi.EI => Ei(muunnaTotuusarvofunktioksi(lasketutArgumentit.head))
      case Funktionimi.HAELUKUARVO => {
        val konvertteri = if (!funktiokutsu.getArvokonvertteriparametrit.isEmpty) {
          val konversioMap = funktiokutsu.getArvokonvertteriparametrit.map(konv =>
            ArvokonversioMerkkijonoilla[BigDecimal, BigDecimal](konv.getArvo, BigDecimal(konv.getPaluuarvo),
              konv.getHylkaysperuste, konv.getKuvaukset)).toList

          Some(Arvokonvertteri[BigDecimal, BigDecimal](konversioMap))
        } else if (!funktiokutsu.getArvovalikonvertteriparametrit.isEmpty) {
          val konversioMap = funktiokutsu.getArvovalikonvertteriparametrit.map(konv => {
            val paluuarvo = if (StringUtils.isNotBlank(konv.getPaluuarvo)) {
              konv.getPaluuarvo
            } else {
              "0.0"
            }
            LukuarvovalikonversioMerkkijonoilla(konv.getMinValue(), konv.getMaxValue(), paluuarvo,
              konv.getPalautaHaettuArvo, konv.getHylkaysperuste, konv.getKuvaukset)
          }).toList

          Some(Lukuarvovalikonvertteri(konversioMap))
        } else None

        val oletusarvo = funktiokutsu.getSyoteparametrit.find(_.getAvain == "oletusarvo")
          .map(p => parametriToBigDecimal(p))

        HaeLukuarvo(konvertteri, oletusarvo, valintaperusteviitteet.head, oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn)
      }
      case Funktionimi.HAELUKUARVOEHDOLLA => {
        val konvertteri = if (!funktiokutsu.getArvokonvertteriparametrit.isEmpty) {
          val konversioMap = funktiokutsu.getArvokonvertteriparametrit.map(konv =>
            ArvokonversioMerkkijonoilla[BigDecimal, BigDecimal](konv.getArvo, BigDecimal(konv.getPaluuarvo),
              konv.getHylkaysperuste, konv.getKuvaukset)).toList

          Some(Arvokonvertteri[BigDecimal, BigDecimal](konversioMap))
        } else if (!funktiokutsu.getArvovalikonvertteriparametrit.isEmpty) {
          val konversioMap = funktiokutsu.getArvovalikonvertteriparametrit.map(konv => {
            val paluuarvo = if (StringUtils.isNotBlank(konv.getPaluuarvo)) {
              konv.getPaluuarvo
            } else {
              "0.0"
            }
            LukuarvovalikonversioMerkkijonoilla(konv.getMinValue(), konv.getMaxValue(), paluuarvo,
              konv.getPalautaHaettuArvo, konv.getHylkaysperuste, konv.getKuvaukset)
          }).toList

          Some(Lukuarvovalikonvertteri(konversioMap))
        } else None

        val oletusarvo = funktiokutsu.getSyoteparametrit.find(_.getAvain == "oletusarvo")
          .map(p => parametriToBigDecimal(p))

        HaeLukuarvoEhdolla(konvertteri, oletusarvo, valintaperusteviitteet(0), valintaperusteviitteet(1), oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn)
      }
      case Funktionimi.HAEMERKKIJONOJAKONVERTOILUKUARVOKSI => {
        val konversioMap = funktiokutsu.getArvokonvertteriparametrit.map(konv =>
          ArvokonversioMerkkijonoilla[String, BigDecimal](konv.getArvo, BigDecimal(konv.getPaluuarvo), konv.getHylkaysperuste, konv.getKuvaukset)).toList

        val oletusarvo = funktiokutsu.getSyoteparametrit.find(_.getAvain == "oletusarvo")
          .map(p => parametriToBigDecimal(p))

        HaeMerkkijonoJaKonvertoiLukuarvoksi(
          Arvokonvertteri[String, BigDecimal](konversioMap),
          oletusarvo,
          valintaperusteviitteet.head,
          oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn)
      }

      case Funktionimi.HAEMERKKIJONOJAKONVERTOITOTUUSARVOKSI => {
        val konversioMap = funktiokutsu.getArvokonvertteriparametrit.map(konv =>
          ArvokonversioMerkkijonoilla[String, Boolean](konv.getArvo, konv.getPaluuarvo.toBoolean, konv.getHylkaysperuste, konv.getKuvaukset)).toList

        val oletusarvo = funktiokutsu.getSyoteparametrit.find(_.getAvain == "oletusarvo")
          .map(p => parametriToBoolean(p))

        HaeMerkkijonoJaKonvertoiTotuusarvoksi(
          Arvokonvertteri[String, Boolean](konversioMap),
          oletusarvo,
          valintaperusteviitteet.head,
          oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn)
      }

      case Funktionimi.HAEMERKKIJONOJAVERTAAYHTASUURUUS => {
        val oletusarvo = funktiokutsu.getSyoteparametrit.find(_.getAvain == "oletusarvo")
          .map(p => parametriToBoolean(p))

        val vertailtava = getParametri("vertailtava", funktiokutsu.getSyoteparametrit).getArvo

        HaeMerkkijonoJaVertaaYhtasuuruus(oletusarvo, valintaperusteviitteet.head, vertailtava, oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn)
      }

      case Funktionimi.HAETOTUUSARVO => {
        val konvertteri = if (!funktiokutsu.getArvokonvertteriparametrit.isEmpty) {
          val konversioMap = funktiokutsu.getArvokonvertteriparametrit.map(konv => {
            ArvokonversioMerkkijonoilla[Boolean, Boolean](konv.getArvo, konv.getPaluuarvo.toBoolean,
              konv.getHylkaysperuste, konv.getKuvaukset)
          }).toList

          Some(Arvokonvertteri[Boolean, Boolean](konversioMap))
        } else None

        val oletusarvo = funktiokutsu.getSyoteparametrit.find(_.getAvain == "oletusarvo")
          .map(p => parametriToBoolean(p))

        HaeTotuusarvo(konvertteri, oletusarvo, valintaperusteviitteet.head, oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn)
      }
      case Funktionimi.HYLKAA => {
        //val hylkaysperustekuvaus = funktiokutsu.getSyoteparametrit.find(_.getAvain == "hylkaysperustekuvaus").map(_.getArvo)
        val hylkaysperustekuvaus = funktiokutsu.getSyoteparametrit.filter(_.getAvain.startsWith("hylkaysperustekuvaus_")).foldLeft(Map.empty[String,String]) {
          (result, kuvaus) => result + (kuvaus.getAvain.split("_")(1) -> kuvaus.getArvo)
        }

        Hylkaa(muunnaTotuusarvofunktioksi(lasketutArgumentit(0)), Some(hylkaysperustekuvaus), oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn)
      }
      case Funktionimi.HYLKAAARVOVALILLA => {
        val hylkaysperustekuvaus = funktiokutsu.getSyoteparametrit.filter(_.getAvain.startsWith("hylkaysperustekuvaus_")).foldLeft(Map.empty[String,String]) {
          (result, kuvaus) => result + (kuvaus.getAvain.split("_")(1) -> kuvaus.getArvo)
        }

        val min = getParametri("arvovaliMin", funktiokutsu.getSyoteparametrit).getArvo
        val max = getParametri("arvovaliMax", funktiokutsu.getSyoteparametrit).getArvo

        HylkaaArvovalilla(muunnaLukuarvofunktioksi(lasketutArgumentit(0)), Some(hylkaysperustekuvaus), oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn, Pair(min, max))

      }
      case Funktionimi.JA => Ja(lasketutArgumentit.map(muunnaTotuusarvofunktioksi(_)), oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn)
      case Funktionimi.JOS => Jos(
        muunnaTotuusarvofunktioksi(lasketutArgumentit(0)),
        muunnaLukuarvofunktioksi(lasketutArgumentit(1)),
        muunnaLukuarvofunktioksi(lasketutArgumentit(2)),
        oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn)
      case Funktionimi.KESKIARVO => Keskiarvo(lasketutArgumentit.map(muunnaLukuarvofunktioksi(_)), oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn)
      case Funktionimi.KESKIARVONPARASTA => {
        val nParam = getParametri(funktiokuvaus.syoteparametrit.head.avain, funktiokutsu.getSyoteparametrit)
        KeskiarvoNParasta(parametriToInteger(nParam), lasketutArgumentit.map(muunnaLukuarvofunktioksi(_)), oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn)
      }
      case Funktionimi.LUKUARVO => {
        val lukuParam = getParametri(funktiokuvaus.syoteparametrit.head.avain, funktiokutsu.getSyoteparametrit)
        Lukuarvo(parametriToBigDecimal(lukuParam), oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn)
      }
      case Funktionimi.KONVERTOILUKUARVO => {
        val konvertteri = if (!funktiokutsu.getArvokonvertteriparametrit.isEmpty) {
          val konversioMap = funktiokutsu.getArvokonvertteriparametrit.map(konv =>
            ArvokonversioMerkkijonoilla[BigDecimal, BigDecimal](konv.getArvo, BigDecimal(konv.getPaluuarvo),
              konv.getHylkaysperuste, konv.getKuvaukset)).toList

          Arvokonvertteri[BigDecimal, BigDecimal](konversioMap)
        } else {
          val konversioMap = funktiokutsu.getArvovalikonvertteriparametrit.map(konv =>
            LukuarvovalikonversioMerkkijonoilla(konv.getMinValue, konv.getMaxValue, konv.getPaluuarvo,
              konv.getPalautaHaettuArvo, konv.getHylkaysperuste, konv.getKuvaukset)).toList

          Lukuarvovalikonvertteri(konversioMap)
        }

        KonvertoiLukuarvo(konvertteri, muunnaLukuarvofunktioksi(lasketutArgumentit.head), oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn)
      }
      case Funktionimi.MAKSIMI => Maksimi(lasketutArgumentit.map(muunnaLukuarvofunktioksi(_)), oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn)
      case Funktionimi.MEDIAANI => Mediaani(lasketutArgumentit.map(muunnaLukuarvofunktioksi(_)), oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn)
      case Funktionimi.MINIMI => Minimi(lasketutArgumentit.map(muunnaLukuarvofunktioksi(_)), oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn)
      case Funktionimi.NEGAATIO => Negaatio(muunnaLukuarvofunktioksi(lasketutArgumentit.head), oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn)
      case Funktionimi.NIMETTYLUKUARVO => {
        val nimiParam = getParametri(funktiokuvaus.syoteparametrit.head.avain, funktiokutsu.getSyoteparametrit)
        NimettyLukuarvo(nimiParam.getArvo, muunnaLukuarvofunktioksi(lasketutArgumentit.head), oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn)
      }
      case Funktionimi.NIMETTYTOTUUSARVO => {
        val nimiParam = getParametri(funktiokuvaus.syoteparametrit.head.avain, funktiokutsu.getSyoteparametrit)
        NimettyTotuusarvo(nimiParam.getArvo, muunnaTotuusarvofunktioksi(lasketutArgumentit.head), oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn)
      }
      case Funktionimi.NMAKSIMI => {
        val nParam = getParametri(funktiokuvaus.syoteparametrit.head.avain, funktiokutsu.getSyoteparametrit)
        NMaksimi(parametriToInteger(nParam), lasketutArgumentit.map(muunnaLukuarvofunktioksi(_)), oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn)
      }
      case Funktionimi.NMINIMI => {
        val nParam = getParametri(funktiokuvaus.syoteparametrit.head.avain, funktiokutsu.getSyoteparametrit)
        NMinimi(parametriToInteger(nParam), lasketutArgumentit.map(muunnaLukuarvofunktioksi(_)), oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn)
      }
      case Funktionimi.OSAMAARA => Osamaara(
        muunnaLukuarvofunktioksi(lasketutArgumentit(0)),
        muunnaLukuarvofunktioksi(lasketutArgumentit(1)),
        oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn)
      case Funktionimi.PAINOTETTUKESKIARVO => {
        def muodostaParit(fs: Seq[Funktio[_]], accu: List[Pair[Lukuarvofunktio, Lukuarvofunktio]]): List[Pair[Lukuarvofunktio, Lukuarvofunktio]] = {
          fs match {
            case first :: second :: rest => muodostaParit(rest, Pair(muunnaLukuarvofunktioksi(first), muunnaLukuarvofunktioksi(second)) :: accu)
            case _ => accu
          }
        }

        PainotettuKeskiarvo(oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn, muodostaParit(lasketutArgumentit, Nil))
      }
      case Funktionimi.PIENEMPI => Pienempi(
        muunnaLukuarvofunktioksi(lasketutArgumentit(0)),
        muunnaLukuarvofunktioksi(lasketutArgumentit(1)), oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn)
      case Funktionimi.PIENEMPITAIYHTASUURI => PienempiTaiYhtasuuri(
        muunnaLukuarvofunktioksi(lasketutArgumentit(0)),
        muunnaLukuarvofunktioksi(lasketutArgumentit(1)),
        oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn)
      case Funktionimi.PYORISTYS =>
        val tarkkuus = getParametri("tarkkuus", funktiokutsu.getSyoteparametrit)
        Pyoristys(
          parametriToInteger(tarkkuus),
          muunnaLukuarvofunktioksi(lasketutArgumentit(0)),
          oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn)
      case Funktionimi.SKAALAUS => {
        val kohdeskaalaMin = parametriToBigDecimal(getParametri("kohdeskaalaMin", funktiokutsu.getSyoteparametrit))
        val kohdeskaalaMax = parametriToBigDecimal(getParametri("kohdeskaalaMax", funktiokutsu.getSyoteparametrit))

        val kaytaLaskennallistaLahdeskaalaa = parametriToBoolean(getParametri("kaytaLaskennallistaLahdeskaalaa",
          funktiokutsu.getSyoteparametrit))

        val lahdeskaala = if (!kaytaLaskennallistaLahdeskaalaa) {
          val lahdeskaalaMin = parametriToBigDecimal(getParametri("lahdeskaalaMin", funktiokutsu.getSyoteparametrit))
          val lahdeskaalaMax = parametriToBigDecimal(getParametri("lahdeskaalaMax", funktiokutsu.getSyoteparametrit))
          Some(Pair(lahdeskaalaMin, lahdeskaalaMax))
        } else None

        Skaalaus(oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn, muunnaLukuarvofunktioksi(lasketutArgumentit.head), Pair(kohdeskaalaMin, kohdeskaalaMax), lahdeskaala)
      }
      case Funktionimi.SUMMA => Summa(lasketutArgumentit.map(muunnaLukuarvofunktioksi(_)), oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn)
      case Funktionimi.SUMMANPARASTA => {
        val nParam = getParametri(funktiokuvaus.syoteparametrit.head.avain, funktiokutsu.getSyoteparametrit)
        SummaNParasta(parametriToInteger(nParam), lasketutArgumentit.map(muunnaLukuarvofunktioksi(_)), oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn)
      }
      case Funktionimi.SUUREMPI => Suurempi(
        muunnaLukuarvofunktioksi(lasketutArgumentit(0)),
        muunnaLukuarvofunktioksi(lasketutArgumentit(1)),
        oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn)
      case Funktionimi.SUUREMPITAIYHTASUURI => SuurempiTaiYhtasuuri(
        muunnaLukuarvofunktioksi(lasketutArgumentit(0)),
        muunnaLukuarvofunktioksi(lasketutArgumentit(1)),
        oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn)
      case Funktionimi.TAI => Tai(lasketutArgumentit.map(muunnaTotuusarvofunktioksi(_)), oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn)
      case Funktionimi.TOTUUSARVO => {
        val totuusarvoParam = getParametri(funktiokuvaus.syoteparametrit.head.avain, funktiokutsu.getSyoteparametrit)
        Totuusarvo(parametriToBoolean(totuusarvoParam), oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn)
      }
      case Funktionimi.TULO => Tulo(lasketutArgumentit.map(muunnaLukuarvofunktioksi(_)), oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn)
      case Funktionimi.YHTASUURI => Yhtasuuri(
        muunnaLukuarvofunktioksi(lasketutArgumentit(0)),
        muunnaLukuarvofunktioksi(lasketutArgumentit(1)),
        oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn)

      case Funktionimi.HAKUTOIVE => {
        val nParam = getParametri(funktiokuvaus.syoteparametrit.head.avain, funktiokutsu.getSyoteparametrit)
        Hakutoive(parametriToInteger(nParam), oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn)
      }

      case Funktionimi.DEMOGRAFIA => {
        val tunniste = getParametri("tunniste", funktiokutsu.getSyoteparametrit).getArvo
        val prosenttiosuus = getParametri("prosenttiosuus", funktiokutsu.getSyoteparametrit)

        Demografia(oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn, tunniste, parametriToBigDecimal(prosenttiosuus))
      }

      case Funktionimi.VALINTAPERUSTEYHTASUURUUS => {
        Valintaperusteyhtasuuruus(oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn, Pair(valintaperusteviitteet(0), valintaperusteviitteet(1)))
      }

      case Funktionimi.HAEYOARVOSANA => {

        List("A", "B", "C", "M", "E", "L", "I").foreach(
          arvosana => {
            if (funktiokutsu.getSyoteparametrit.count(s => s.getAvain == arvosana) == 0) {
              val target = new Syoteparametri
              target.setArvo("0.0")
              target.setAvain(arvosana)
              funktiokutsu.getSyoteparametrit.add(target)
            }
          }

        )

        val arvosanaKonvertterit = funktiokutsu.getSyoteparametrit.filter(s => s.getAvain.length == 1).map(
          param => ArvokonversioMerkkijonoilla[String, BigDecimal](param.getAvain, BigDecimal(param.getArvo), "false", new TekstiRyhma)
        ).toList

        val alkuvuosi = funktiokutsu.getSyoteparametrit.find(s => s.getAvain.equals("alkuvuosi")) match {
          case Some(sp: Syoteparametri) => BigDecimal(sp.getArvo)
          case _ => BigDecimal("0.0")
        }

        val loppuvuosi = funktiokutsu.getSyoteparametrit.find(s => s.getAvain.equals("loppuvuosi")) match {
          case Some(sp: Syoteparametri) => BigDecimal(sp.getArvo)
          case _ => BigDecimal("9999")
        }

        val alkulukukausi = funktiokutsu.getSyoteparametrit.find(s => s.getAvain.equals("alkulukukausi")) match {
          case Some(sp: Syoteparametri) => BigDecimal(sp.getArvo)
          case _ => BigDecimal("1")
        }

        val loppulukukausi = funktiokutsu.getSyoteparametrit.find(s => s.getAvain.equals("loppulukukausi")) match {
          case Some(sp: Syoteparametri) => BigDecimal(sp.getArvo)
          case _ => BigDecimal("2")
        }

        val arvosana = HaeMerkkijonoJaKonvertoiLukuarvoksi(
          Arvokonvertteri[String, BigDecimal](arvosanaKonvertterit),
          None,
          valintaperusteviitteet.head)

        val vuosiperuste = HakemuksenValintaperuste(s"${valintaperusteviitteet.head.tunniste}_suoritusvuosi", pakollinen = false)
        val lukukausiperuste = HakemuksenValintaperuste(s"${valintaperusteviitteet.head.tunniste}_suorituslukukausi", pakollinen = false)

        val ehdot = Ja(
          Seq(
            SuurempiTaiYhtasuuri(HaeLukuarvo(None, None, vuosiperuste), Lukuarvo(alkuvuosi)),
            SuurempiTaiYhtasuuri(HaeLukuarvo(None, None, lukukausiperuste), Lukuarvo(alkulukukausi)),
            PienempiTaiYhtasuuri(HaeLukuarvo(None, None, vuosiperuste), Lukuarvo(loppuvuosi)),
            PienempiTaiYhtasuuri(HaeLukuarvo(None, None, lukukausiperuste), Lukuarvo(loppulukukausi))
          )
        )

        Jos(ehdot, arvosana, Lukuarvo(BigDecimal("0.0")),tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn)

      }

      case Funktionimi.HAEOSAKOEARVOSANA => {

        val konvertteri = if (!funktiokutsu.getArvokonvertteriparametrit.isEmpty) {
          val konversioMap = funktiokutsu.getArvokonvertteriparametrit.map(konv =>
            ArvokonversioMerkkijonoilla[BigDecimal, BigDecimal](konv.getArvo, BigDecimal(konv.getPaluuarvo),
              konv.getHylkaysperuste, konv.getKuvaukset)).toList

          Some(Arvokonvertteri[BigDecimal, BigDecimal](konversioMap))
        } else if (!funktiokutsu.getArvovalikonvertteriparametrit.isEmpty) {
          val konversioMap = funktiokutsu.getArvovalikonvertteriparametrit.map(konv => {
            val paluuarvo = if (StringUtils.isNotBlank(konv.getPaluuarvo)) {
              konv.getPaluuarvo
            } else {
              "0.0"
            }
            LukuarvovalikonversioMerkkijonoilla(konv.getMinValue(), konv.getMaxValue(), paluuarvo,
              konv.getPalautaHaettuArvo, konv.getHylkaysperuste, konv.getKuvaukset)
          }).toList

          Some(Lukuarvovalikonvertteri(konversioMap))
        } else None

        val alkuvuosi = funktiokutsu.getSyoteparametrit.find(s => s.getAvain.equals("alkuvuosi")) match {
          case Some(sp: Syoteparametri) => BigDecimal(sp.getArvo)
          case _ => BigDecimal("0.0")
        }

        val loppuvuosi = funktiokutsu.getSyoteparametrit.find(s => s.getAvain.equals("loppuvuosi")) match {
          case Some(sp: Syoteparametri) => BigDecimal(sp.getArvo)
          case _ => BigDecimal("9999")
        }

        val alkulukukausi = funktiokutsu.getSyoteparametrit.find(s => s.getAvain.equals("alkulukukausi")) match {
          case Some(sp: Syoteparametri) => BigDecimal(sp.getArvo)
          case _ => BigDecimal("1")
        }

        val loppulukukausi = funktiokutsu.getSyoteparametrit.find(s => s.getAvain.equals("loppulukukausi")) match {
          case Some(sp: Syoteparametri) => BigDecimal(sp.getArvo)
          case _ => BigDecimal("2")
        }

        val arvosana = HaeLukuarvo(
          konvertteri,
          None,
          valintaperusteviitteet.head)

        val vuosiperuste = HakemuksenValintaperuste(s"${valintaperusteviitteet.head.tunniste}_suoritusvuosi", pakollinen = false)
        val lukukausiperuste = HakemuksenValintaperuste(s"${valintaperusteviitteet.head.tunniste}_suorituslukukausi", pakollinen = false)

        val ehdot = Ja(
          Seq(
            SuurempiTaiYhtasuuri(HaeLukuarvo(None, None, vuosiperuste), Lukuarvo(alkuvuosi)),
            SuurempiTaiYhtasuuri(HaeLukuarvo(None, None, lukukausiperuste), Lukuarvo(alkulukukausi)),
            PienempiTaiYhtasuuri(HaeLukuarvo(None, None, vuosiperuste), Lukuarvo(loppuvuosi)),
            PienempiTaiYhtasuuri(HaeLukuarvo(None, None, lukukausiperuste), Lukuarvo(loppulukukausi))
          )
        )

        Jos(ehdot, arvosana, Lukuarvo(BigDecimal("0.0")),tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn)

      }

      case _ => sys.error(s"Could not calculate funktio ${funktiokutsu.getFunktionimi.name()}")
    }
  }

}
