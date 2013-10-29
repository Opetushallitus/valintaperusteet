package fi.vm.sade.kaava

import fi.vm.sade.service.valintaperusteet.model._
import fi.vm.sade.service.valintaperusteet.laskenta._
import Laskenta._
import Laskenta.{HakukohteenValintaperuste => HkValintaperuste}
import org.apache.commons.lang.StringUtils
import java.math.{BigDecimal => BigDec}
import scala.math.BigDecimal._
import fi.vm.sade.service.valintaperusteet.service.validointi.virhe.LaskentakaavaEiOleValidiException

/**
 * User: kwuoti
 * Date: 21.1.2013
 * Time: 12.12
 */
object Laskentadomainkonvertteri {

  import scala.collection.JavaConversions._

  private def getParametri(avain: String, params: java.util.Set[Syoteparametri]): Syoteparametri = {
    params.filter(_.getAvain == avain).toList match {
      case Nil => sys.error("Could not find parameter matching the key " + avain)
      case head :: tail => head
    }
  }

  private def parametriToBigDecimal(param: Syoteparametri) = {
    try {
      BigDecimal(param.getArvo)
    } catch {
      case e => sys.error("Could not interpret parameter " + param.getAvain + " value " + param.getArvo + " as big decimal")
    }
  }

  private def parametriToInteger(param: Syoteparametri) = {
    try {
      param.getArvo.toInt
    } catch {
      case e => sys.error("Could not interpret parameter " + param.getAvain + " value " + param.getArvo + " as integer")
    }
  }

  private def parametriToBoolean(param: Syoteparametri) = {
    try {
      param.getArvo.toBoolean
    } catch {
      case e => sys.error("Could not interpret parameter " + param.getAvain + " value " + param.getArvo + " as boolean")
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
      case Valintaperustelahde.SYOTETTAVA_ARVO => SyotettavaValintaperuste(valintaperuste.getTunniste,
        valintaperuste.getOnPakollinen, valintaperuste.getOsallistuminenTunniste)
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

    val valintaperusteviitteet = funktiokutsu.getValintaperusteviitteet.toList.sortWith(_.getIndeksi < _.getIndeksi).map(luoValintaperusteviite(_))

    funktiokutsu.getFunktionimi match {
      case Funktionimi.EI => Ei(muunnaTotuusarvofunktioksi(lasketutArgumentit.head))
      case Funktionimi.HAELUKUARVO => {
        val konvertteri = if (!funktiokutsu.getArvokonvertteriparametrit.isEmpty) {
          val konversioMap = funktiokutsu.getArvokonvertteriparametrit.map(konv =>
            Arvokonversio[BigDecimal, BigDecimal](BigDecimal(konv.getArvo), BigDecimal(konv.getPaluuarvo),
              konv.getHylkaysperuste)).toList

          Some(Arvokonvertteri[BigDecimal, BigDecimal](konversioMap))
        } else if (!funktiokutsu.getArvovalikonvertteriparametrit.isEmpty) {
          val konversioMap = funktiokutsu.getArvovalikonvertteriparametrit.map(konv => {
            val paluuarvo = if (StringUtils.isNotBlank(konv.getPaluuarvo)) {
              BigDecimal(konv.getPaluuarvo)
            } else {
              BigDecimal("0.0")
            }
            val min: BigDecimal = konv.getMinValue()
            val max: BigDecimal = konv.getMaxValue()
            Lukuarvovalikonversio(min, max, paluuarvo,
              konv.getPalautaHaettuArvo, konv.getHylkaysperuste)
          }).toList

          Some(Lukuarvovalikonvertteri(konversioMap))
        } else None

        val oletusarvo = funktiokutsu.getSyoteparametrit.find(_.getAvain == "oletusarvo")
          .map(p => parametriToBigDecimal(p))

        HaeLukuarvo(konvertteri, oletusarvo, valintaperusteviitteet.head, oid)
      }
      case Funktionimi.HAEMERKKIJONOJAKONVERTOILUKUARVOKSI => {
        val konversioMap = funktiokutsu.getArvokonvertteriparametrit.map(konv =>
          Arvokonversio[String, BigDecimal](konv.getArvo, BigDecimal(konv.getPaluuarvo), konv.getHylkaysperuste)).toList

        val oletusarvo = funktiokutsu.getSyoteparametrit.find(_.getAvain == "oletusarvo")
          .map(p => parametriToBigDecimal(p))

        HaeMerkkijonoJaKonvertoiLukuarvoksi(
          Arvokonvertteri[String, BigDecimal](konversioMap),
          oletusarvo,
          valintaperusteviitteet.head,
          oid)
      }

      case Funktionimi.HAEMERKKIJONOJAKONVERTOITOTUUSARVOKSI => {
        val konversioMap = funktiokutsu.getArvokonvertteriparametrit.map(konv =>
          Arvokonversio[String, Boolean](konv.getArvo, konv.getPaluuarvo.toBoolean, konv.getHylkaysperuste)).toList

        val oletusarvo = funktiokutsu.getSyoteparametrit.find(_.getAvain == "oletusarvo")
          .map(p => parametriToBoolean(p))

        HaeMerkkijonoJaKonvertoiTotuusarvoksi(
          Arvokonvertteri[String, Boolean](konversioMap),
          oletusarvo,
          valintaperusteviitteet.head,
          oid)
      }

      case Funktionimi.HAEMERKKIJONOJAVERTAAYHTASUURUUS => {
        val oletusarvo = funktiokutsu.getSyoteparametrit.find(_.getAvain == "oletusarvo")
          .map(p => parametriToBoolean(p))

        val vertailtava = getParametri("vertailtava", funktiokutsu.getSyoteparametrit).getArvo

        HaeMerkkijonoJaVertaaYhtasuuruus(oletusarvo, valintaperusteviitteet.head, vertailtava, oid)
      }

      case Funktionimi.HAETOTUUSARVO => {
        val konvertteri = if (!funktiokutsu.getArvokonvertteriparametrit.isEmpty) {
          val konversioMap = funktiokutsu.getArvokonvertteriparametrit.map(konv => {
            Arvokonversio[Boolean, Boolean](konv.getArvo.toBoolean, konv.getPaluuarvo.toBoolean,
              konv.getHylkaysperuste)
          }).toList

          Some(Arvokonvertteri[Boolean, Boolean](konversioMap))
        } else None

        val oletusarvo = funktiokutsu.getSyoteparametrit.find(_.getAvain == "oletusarvo")
          .map(p => parametriToBoolean(p))

        HaeTotuusarvo(konvertteri, oletusarvo, valintaperusteviitteet.head, oid)
      }
      case Funktionimi.HYLKAA => {
        val hylkaysperustekuvaus = funktiokutsu.getSyoteparametrit.find(_.getAvain == "hylkaysperustekuvaus").map(_.getArvo)
        Hylkaa(muunnaTotuusarvofunktioksi(lasketutArgumentit(0)), hylkaysperustekuvaus, oid)
      }
      case Funktionimi.JA => Ja(lasketutArgumentit.map(muunnaTotuusarvofunktioksi(_)), oid)
      case Funktionimi.JOS => Jos(
        muunnaTotuusarvofunktioksi(lasketutArgumentit(0)),
        muunnaLukuarvofunktioksi(lasketutArgumentit(1)),
        muunnaLukuarvofunktioksi(lasketutArgumentit(2)),
        oid)
      case Funktionimi.KESKIARVO => Keskiarvo(lasketutArgumentit.map(muunnaLukuarvofunktioksi(_)), oid)
      case Funktionimi.KESKIARVONPARASTA => {
        val nParam = getParametri(funktiokuvaus.syoteparametrit.head.avain, funktiokutsu.getSyoteparametrit)
        KeskiarvoNParasta(parametriToInteger(nParam), lasketutArgumentit.map(muunnaLukuarvofunktioksi(_)), oid)
      }
      case Funktionimi.LUKUARVO => {
        val lukuParam = getParametri(funktiokuvaus.syoteparametrit.head.avain, funktiokutsu.getSyoteparametrit)
        Lukuarvo(parametriToBigDecimal(lukuParam), oid)
      }
      case Funktionimi.KONVERTOILUKUARVO => {
        val konvertteri = if (!funktiokutsu.getArvokonvertteriparametrit.isEmpty) {
          val konversioMap = funktiokutsu.getArvokonvertteriparametrit.map(konv =>
            Arvokonversio[BigDecimal, BigDecimal](BigDecimal(konv.getArvo), BigDecimal(konv.getPaluuarvo),
              konv.getHylkaysperuste)).toList

          Arvokonvertteri[BigDecimal, BigDecimal](konversioMap)
        } else {
          val konversioMap = funktiokutsu.getArvovalikonvertteriparametrit.map(konv =>
            Lukuarvovalikonversio(konv.getMinValue, konv.getMaxValue, BigDecimal(konv.getPaluuarvo),
              konv.getPalautaHaettuArvo, konv.getHylkaysperuste)).toList

          Lukuarvovalikonvertteri(konversioMap)
        }

        KonvertoiLukuarvo(konvertteri, muunnaLukuarvofunktioksi(lasketutArgumentit.head), oid)
      }
      case Funktionimi.MAKSIMI => Maksimi(lasketutArgumentit.map(muunnaLukuarvofunktioksi(_)), oid)
      case Funktionimi.MEDIAANI => Mediaani(lasketutArgumentit.map(muunnaLukuarvofunktioksi(_)), oid)
      case Funktionimi.MINIMI => Minimi(lasketutArgumentit.map(muunnaLukuarvofunktioksi(_)), oid)
      case Funktionimi.NEGAATIO => Negaatio(muunnaLukuarvofunktioksi(lasketutArgumentit.head), oid)
      case Funktionimi.NIMETTYLUKUARVO => {
        val nimiParam = getParametri(funktiokuvaus.syoteparametrit.head.avain, funktiokutsu.getSyoteparametrit)
        NimettyLukuarvo(nimiParam.getArvo, muunnaLukuarvofunktioksi(lasketutArgumentit.head), oid)
      }
      case Funktionimi.NIMETTYTOTUUSARVO => {
        val nimiParam = getParametri(funktiokuvaus.syoteparametrit.head.avain, funktiokutsu.getSyoteparametrit)
        NimettyTotuusarvo(nimiParam.getArvo, muunnaTotuusarvofunktioksi(lasketutArgumentit.head), oid)
      }
      case Funktionimi.NMAKSIMI => {
        val nParam = getParametri(funktiokuvaus.syoteparametrit.head.avain, funktiokutsu.getSyoteparametrit)
        NMaksimi(parametriToInteger(nParam), lasketutArgumentit.map(muunnaLukuarvofunktioksi(_)), oid)
      }
      case Funktionimi.NMINIMI => {
        val nParam = getParametri(funktiokuvaus.syoteparametrit.head.avain, funktiokutsu.getSyoteparametrit)
        NMinimi(parametriToInteger(nParam), lasketutArgumentit.map(muunnaLukuarvofunktioksi(_)), oid)
      }
      case Funktionimi.OSAMAARA => Osamaara(
        muunnaLukuarvofunktioksi(lasketutArgumentit(0)),
        muunnaLukuarvofunktioksi(lasketutArgumentit(1)),
        oid)
      case Funktionimi.PAINOTETTUKESKIARVO => {
        def muodostaParit(fs: Seq[Funktio[_]], accu: List[Pair[Lukuarvofunktio, Lukuarvofunktio]]): List[Pair[Lukuarvofunktio, Lukuarvofunktio]] = {
          fs match {
            case first :: second :: rest => muodostaParit(rest, Pair(muunnaLukuarvofunktioksi(first), muunnaLukuarvofunktioksi(second)) :: accu)
            case _ => accu
          }
        }

        PainotettuKeskiarvo(oid, muodostaParit(lasketutArgumentit, Nil))
      }
      case Funktionimi.PIENEMPI => Pienempi(
        muunnaLukuarvofunktioksi(lasketutArgumentit(0)),
        muunnaLukuarvofunktioksi(lasketutArgumentit(1)), oid)
      case Funktionimi.PIENEMPITAIYHTASUURI => PienempiTaiYhtasuuri(
        muunnaLukuarvofunktioksi(lasketutArgumentit(0)),
        muunnaLukuarvofunktioksi(lasketutArgumentit(1)),
        oid)
      case Funktionimi.PYORISTYS =>
        val tarkkuus = getParametri("tarkkuus", funktiokutsu.getSyoteparametrit)
        Pyoristys(
          parametriToInteger(tarkkuus),
          muunnaLukuarvofunktioksi(lasketutArgumentit(0)),
          oid)
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

        Skaalaus(oid, muunnaLukuarvofunktioksi(lasketutArgumentit.head), Pair(kohdeskaalaMin, kohdeskaalaMax), lahdeskaala)
      }
      case Funktionimi.SUMMA => Summa(lasketutArgumentit.map(muunnaLukuarvofunktioksi(_)), oid)
      case Funktionimi.SUMMANPARASTA => {
        val nParam = getParametri(funktiokuvaus.syoteparametrit.head.avain, funktiokutsu.getSyoteparametrit)
        SummaNParasta(parametriToInteger(nParam), lasketutArgumentit.map(muunnaLukuarvofunktioksi(_)), oid)
      }
      case Funktionimi.SUUREMPI => Suurempi(
        muunnaLukuarvofunktioksi(lasketutArgumentit(0)),
        muunnaLukuarvofunktioksi(lasketutArgumentit(1)),
        oid)
      case Funktionimi.SUUREMPITAIYHTASUURI => SuurempiTaiYhtasuuri(
        muunnaLukuarvofunktioksi(lasketutArgumentit(0)),
        muunnaLukuarvofunktioksi(lasketutArgumentit(1)),
        oid)
      case Funktionimi.TAI => Tai(lasketutArgumentit.map(muunnaTotuusarvofunktioksi(_)), oid)
      case Funktionimi.TOTUUSARVO => {
        val totuusarvoParam = getParametri(funktiokuvaus.syoteparametrit.head.avain, funktiokutsu.getSyoteparametrit)
        Totuusarvo(parametriToBoolean(totuusarvoParam), oid)
      }
      case Funktionimi.TULO => Tulo(lasketutArgumentit.map(muunnaLukuarvofunktioksi(_)), oid)
      case Funktionimi.YHTASUURI => Yhtasuuri(
        muunnaLukuarvofunktioksi(lasketutArgumentit(0)),
        muunnaLukuarvofunktioksi(lasketutArgumentit(1)),
        oid)

      case Funktionimi.HAKUTOIVE => {
        val nParam = getParametri(funktiokuvaus.syoteparametrit.head.avain, funktiokutsu.getSyoteparametrit)
        Hakutoive(parametriToInteger(nParam), oid)
      }

      case Funktionimi.DEMOGRAFIA => {
        val tunniste = getParametri("tunniste", funktiokutsu.getSyoteparametrit).getArvo
        val prosenttiosuus = getParametri("prosenttiosuus", funktiokutsu.getSyoteparametrit)

        Demografia(oid, tunniste, parametriToBigDecimal(prosenttiosuus))
      }

      case Funktionimi.VALINTAPERUSTEYHTASUURUUS => {
        Valintaperusteyhtasuuruus(oid, Pair(valintaperusteviitteet(0), valintaperusteviitteet(1)))
      }

      case _ => sys.error("Could not calculate funktio " + funktiokutsu.getFunktionimi.name())
    }
  }

}
