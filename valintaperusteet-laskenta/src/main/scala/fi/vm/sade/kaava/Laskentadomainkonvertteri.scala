package fi.vm.sade.kaava

import java.util.{Set => JSet}

import fi.vm.sade.service.valintaperusteet.dto.model.{Funktionimi, Valintaperustelahde}
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.{Arvokonvertteri, Demografia, Ei, HaeLukuarvo, HaeMerkkijonoJaKonvertoiLukuarvoksi, HaeMerkkijonoJaKonvertoiTotuusarvoksi, HaeMerkkijonoJaVertaaYhtasuuruus, HaeTotuusarvo, HakemuksenValintaperuste, Hakutoive, Hylkaa, HylkaaArvovalilla, Jos, KonvertoiLukuarvo, Lukuarvo, Lukuarvovalikonvertteri, Negaatio, NimettyLukuarvo, NimettyTotuusarvo, Osamaara, PainotettuKeskiarvo, Pienempi, PienempiTaiYhtasuuri, Pyoristys, Skaalaus, Suurempi, SuurempiTaiYhtasuuri, SyotettavaValintaperuste, Totuusarvo, Valintaperusteyhtasuuruus, Yhtasuuri, HakukohteenSyotettavaValintaperuste => HksValintaperuste, HakukohteenValintaperuste => HkValintaperuste, _}
import fi.vm.sade.service.valintaperusteet.laskenta._
import fi.vm.sade.service.valintaperusteet.model._
import fi.vm.sade.service.valintaperusteet.service.validointi.virhe.LaskentakaavaEiOleValidiException
import org.apache.commons.lang.StringUtils
import scala.jdk.CollectionConverters._

object Laskentadomainkonvertteri {

  private val YO = "YO"
  private val PK = "PK"
  private val LK = "LK"
  private val KYMPPI = "10"
  private val TILA_SUFFIX = "_TILA"
  private val VUOSI_SUFFIX = "_SUORITUSVUOSI"
  private val KAUSI_SUFFIX = "_SUORITUSLUKUKAUSI"
  private val ROOLI_SUFFIX = "_ROOLI"

  private def getParametri(avain: String, params: JSet[Syoteparametri]): Syoteparametri = {
    params.asScala.filter(_.getAvain == avain).toList match {
      case Nil => sys.error(s"Could not find parameter matching the key $avain")
      case head :: tail => head
    }
  }

  private def parametriToBigDecimal(param: Syoteparametri) = {
    try {
      BigDecimal(param.getArvo.replace(',','.'))
    } catch {
      case e: Throwable =>
        sys.error(s"Could not interpret parameter ${param.getAvain} value ${param.getArvo} as big decimal")
    }
  }

  private def parametriToInteger(param: Syoteparametri) = {
    try {
      param.getArvo.toInt
    } catch {
      case e: Throwable =>
        sys.error(s"Could not interpret parameter ${param.getAvain} value ${param.getArvo} as integer")
    }
  }

  private def parametriToBoolean(param: Syoteparametri) = {
    try {
      param.getArvo.toBoolean
    } catch {
      case e: Throwable =>
        sys.error(s"Could not interpret parameter ${param.getAvain} value ${param.getArvo} as boolean")
    }
  }

  private def stringToBigDecimal(arvo: String) = {
    try {
      BigDecimal(arvo.replace(',','.'))
    } catch {
      case e: Throwable =>
        sys.error(s"Could not interpret string $arvo value as big decimal")
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
        HksValintaperuste(valintaperuste.getTunniste, valintaperuste.getOnPakollinen, Option(valintaperuste.getEpasuoraViittaus).map(Boolean2boolean(_)).getOrElse(false),
          ValintaperusteViite.OSALLISTUMINEN_POSTFIX, valintaperuste.getKuvaus, valintaperuste.getKuvaukset,
          valintaperuste.getVaatiiOsallistumisen, valintaperuste.getSyotettavissaKaikille,
          Option(valintaperuste.getSyotettavanarvontyyppi).map(_.getUri), valintaperuste.getTilastoidaan)
      case Valintaperustelahde.SYOTETTAVA_ARVO => SyotettavaValintaperuste(valintaperuste.getTunniste,
        valintaperuste.getOnPakollinen, valintaperuste.getOsallistuminenTunniste, valintaperuste.getKuvaus, valintaperuste.getKuvaukset,
        valintaperuste.getVaatiiOsallistumisen, valintaperuste.getSyotettavissaKaikille, Option(valintaperuste.getSyotettavanarvontyyppi).map(_.getUri), valintaperuste.getTilastoidaan)
    }
  }

  private def muodostaLasku(funktiokutsu: Funktiokutsu): Funktio[_] = {
    val virheet = Laskentakaavavalidaattori.onkoLaskettavaKaavaValidi(funktiokutsu)
      if (!virheet.isEmpty) {
      throw new LaskentakaavaEiOleValidiException("Funktiokutsu ei ole validi: " + virheet.mkString(", ") + " | " + funktiokutsu.toString())
    }

    val jarjestetytArgumentit: List[Funktioargumentti] = LaskentaUtil.jarjestaFunktioargumentit(funktiokutsu.getFunktioargumentit)
    val lasketutArgumentit: List[Funktio[_]] = jarjestetytArgumentit.map((arg: Funktioargumentti) => muodostaLasku(arg.getFunktiokutsuChild))

    val funktiokuvaus = Funktiokuvaaja.annaFunktiokuvaus(funktiokutsu.getFunktionimi)._2

    val oid = if (funktiokutsu.getId != null) funktiokutsu.getId.toString else ""
    val funktionimi = funktiokutsu.getFunktionimi
    val tulosTunniste = if (java.lang.Boolean.TRUE.equals(funktiokutsu.getTallennaTulos)) funktiokutsu.getTulosTunniste() else ""
    val tulosTekstiFi = if(funktiokutsu.getTulosTekstiFi != null) funktiokutsu.getTulosTekstiFi else ""
    val tulosTekstiSv = if(funktiokutsu.getTulosTekstiSv != null) funktiokutsu.getTulosTekstiSv else ""
    val tulosTekstiEn = if(funktiokutsu.getTulosTekstiEn != null) funktiokutsu.getTulosTekstiEn else ""
    val omaopintopolku: Boolean = if(java.lang.Boolean.TRUE.equals(funktiokutsu.getOmaopintopolku)) funktiokutsu.getOmaopintopolku else false

    val valintaperusteviitteet = funktiokutsu.getValintaperusteviitteet.asScala.toList.sortWith(_.getIndeksi < _.getIndeksi).map(luoValintaperusteviite(_))

    val arvokonvertteriparametrit = funktiokutsu.getArvokonvertteriparametrit
    val arvovalikonvertteriparametrit = funktiokutsu.getArvovalikonvertteriparametrit
    val syoteparametrit = funktiokutsu.getSyoteparametrit

    funktionimi match {
      case Funktionimi.EI =>
        Ei(muunnaTotuusarvofunktioksi(lasketutArgumentit.head), omaopintopolku = omaopintopolku)

      case Funktionimi.HAELUKUARVO =>
        val konvertteri = if (!arvokonvertteriparametrit.isEmpty) {
          val konversioMap = arvokonvertteriparametrit.asScala.map(konv =>
            ArvokonversioMerkkijonoilla[BigDecimal, BigDecimal](konv.getArvo, stringToBigDecimal(konv.getPaluuarvo),
              konv.getHylkaysperuste, konv.getKuvaukset)).toList

          Some(Arvokonvertteri[BigDecimal, BigDecimal](konversioMap))
        } else if (!arvovalikonvertteriparametrit.isEmpty) {
          val konversioMap = arvovalikonvertteriparametrit.asScala.map(konv => {
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

        val oletusarvo = syoteparametrit.asScala.find(_.getAvain == "oletusarvo").filter(!_.getArvo.isEmpty)
          .map(p => parametriToBigDecimal(p))

        HaeLukuarvo(konvertteri, oletusarvo, valintaperusteviitteet.head, oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn, omaopintopolku)

      case Funktionimi.HAELUKUARVOEHDOLLA =>
        val konvertteri = if (!arvokonvertteriparametrit.isEmpty) {
          val konversioMap = arvokonvertteriparametrit.asScala.map(konv =>
            ArvokonversioMerkkijonoilla[BigDecimal, BigDecimal](konv.getArvo, stringToBigDecimal(konv.getPaluuarvo),
              konv.getHylkaysperuste, konv.getKuvaukset)).toList

          Some(Arvokonvertteri[BigDecimal, BigDecimal](konversioMap))
        } else if (!arvovalikonvertteriparametrit.isEmpty) {
          val konversioMap = arvovalikonvertteriparametrit.asScala.map(konv => {
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


        val oletusarvo = syoteparametrit.asScala.find(_.getAvain == "oletusarvo").filter(!_.getArvo.isEmpty)
          .map(p => parametriToBigDecimal(p))

        HaeLukuarvoEhdolla(konvertteri, oletusarvo, valintaperusteviitteet(0), valintaperusteviitteet(1), oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn, omaopintopolku)

      case Funktionimi.HAEMERKKIJONOJAKONVERTOILUKUARVOKSI =>
        val konversioMap = arvokonvertteriparametrit.asScala.map(konv =>
          ArvokonversioMerkkijonoilla[String, BigDecimal](konv.getArvo, stringToBigDecimal(konv.getPaluuarvo), konv.getHylkaysperuste, konv.getKuvaukset)).toList

        val oletusarvo = syoteparametrit.asScala.find(_.getAvain == "oletusarvo").filter(!_.getArvo.isEmpty)
          .map(p => parametriToBigDecimal(p))

        HaeMerkkijonoJaKonvertoiLukuarvoksi(
          Arvokonvertteri[String, BigDecimal](konversioMap),
          oletusarvo,
          valintaperusteviitteet.head,
          oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn, omaopintopolku)

      case Funktionimi.HAETOTUUSARVOJAKONVERTOILUKUARVOKSI =>
        val konversioMap = arvokonvertteriparametrit.asScala.map(konv =>
          ArvokonversioMerkkijonoilla[Boolean, BigDecimal](konv.getArvo, stringToBigDecimal(konv.getPaluuarvo), konv.getHylkaysperuste, konv.getKuvaukset)).toList

        val oletusarvo = syoteparametrit.asScala.find(_.getAvain == "oletusarvo").filter(!_.getArvo.isEmpty)
          .map(p => parametriToBigDecimal(p))

        HaeTotuusarvoJaKonvertoiLukuarvoksi(
          Arvokonvertteri[Boolean, BigDecimal](konversioMap),
          oletusarvo,
          valintaperusteviitteet.head,
          oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn, omaopintopolku)

      case Funktionimi.HAEMERKKIJONOJAKONVERTOITOTUUSARVOKSI =>
        val konversioMap = arvokonvertteriparametrit.asScala.map(konv =>
          ArvokonversioMerkkijonoilla[String, Boolean](konv.getArvo, konv.getPaluuarvo.toBoolean, konv.getHylkaysperuste, konv.getKuvaukset)).toList

        val oletusarvo = syoteparametrit.asScala.filter(!_.getArvo.isEmpty).find(_.getAvain == "oletusarvo")
          .map(p => parametriToBoolean(p))

        HaeMerkkijonoJaKonvertoiTotuusarvoksi(
          Arvokonvertteri[String, Boolean](konversioMap),
          oletusarvo,
          valintaperusteviitteet.head,
          oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn, omaopintopolku)

      case Funktionimi.HAEMERKKIJONOJAVERTAAYHTASUURUUS =>
        val oletusarvo = syoteparametrit.asScala.filter(!_.getArvo.isEmpty).find(_.getAvain == "oletusarvo")
          .map(p => parametriToBoolean(p))

        val vertailtava = getParametri("vertailtava", syoteparametrit).getArvo

        HaeMerkkijonoJaVertaaYhtasuuruus(oletusarvo, valintaperusteviitteet.head, vertailtava, oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn, omaopintopolku)

      case Funktionimi.HAETOTUUSARVO =>
        val konvertteri = if (!arvokonvertteriparametrit.isEmpty) {
          val konversioMap = arvokonvertteriparametrit.asScala.map(konv => {
            ArvokonversioMerkkijonoilla[Boolean, Boolean](konv.getArvo, konv.getPaluuarvo.toBoolean,
              konv.getHylkaysperuste, konv.getKuvaukset)
          }).toList

          Some(Arvokonvertteri[Boolean, Boolean](konversioMap))
        } else None

        val oletusarvo = syoteparametrit.asScala.filter(!_.getArvo.isEmpty).find(_.getAvain == "oletusarvo")
          .map(p => parametriToBoolean(p))

        HaeTotuusarvo(konvertteri, oletusarvo, valintaperusteviitteet.head, oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn, omaopintopolku)

      case Funktionimi.HYLKAA =>
        val hylkaysperustekuvaus = syoteparametrit.asScala.filter(_.getAvain.startsWith("hylkaysperustekuvaus_")).foldLeft(Map.empty[String, String]) {
          (result, kuvaus) => result + (kuvaus.getAvain.split("_")(1) -> kuvaus.getArvo)
        }
        Hylkaa(muunnaTotuusarvofunktioksi(lasketutArgumentit(0)), Some(hylkaysperustekuvaus), oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn, omaopintopolku)

      case Funktionimi.HYLKAAARVOVALILLA =>
        val hylkaysperustekuvaus = syoteparametrit.asScala.filter(_.getAvain.startsWith("hylkaysperustekuvaus_")).foldLeft(Map.empty[String, String]) {
          (result, kuvaus) => result + (kuvaus.getAvain.split("_")(1) -> kuvaus.getArvo)
        }

        val min = getParametri("arvovaliMin", syoteparametrit).getArvo
        val max = getParametri("arvovaliMax", syoteparametrit).getArvo

        HylkaaArvovalilla(muunnaLukuarvofunktioksi(lasketutArgumentit(0)), Some(hylkaysperustekuvaus), oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn, omaopintopolku, Tuple2(min, max))

      case Funktionimi.JA =>
        Ja(lasketutArgumentit.map(muunnaTotuusarvofunktioksi(_)), oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn, omaopintopolku)

      case Funktionimi.JOS => Jos(
        muunnaTotuusarvofunktioksi(lasketutArgumentit(0)),
        muunnaLukuarvofunktioksi(lasketutArgumentit(1)),
        muunnaLukuarvofunktioksi(lasketutArgumentit(2)),
        oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn, omaopintopolku = omaopintopolku)

      case Funktionimi.KESKIARVO =>
        Keskiarvo(lasketutArgumentit.map(muunnaLukuarvofunktioksi(_)), oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn, omaopintopolku)

      case Funktionimi.KESKIARVONPARASTA =>
        val nParam = getParametri(funktiokuvaus.syoteparametrit.head.avain, syoteparametrit)
        KeskiarvoNParasta(parametriToInteger(nParam), lasketutArgumentit.map(muunnaLukuarvofunktioksi(_)), oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn, omaopintopolku)

      case Funktionimi.LUKUARVO =>
        val lukuParam = getParametri(funktiokuvaus.syoteparametrit.head.avain, syoteparametrit)
        Lukuarvo(parametriToBigDecimal(lukuParam), oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn, omaopintopolku)

      case Funktionimi.KONVERTOILUKUARVO =>
        val konvertteri = if (!arvokonvertteriparametrit.isEmpty) {
          val konversioMap = arvokonvertteriparametrit.asScala.map(konv =>
            ArvokonversioMerkkijonoilla[BigDecimal, BigDecimal](konv.getArvo, stringToBigDecimal(konv.getPaluuarvo),
              konv.getHylkaysperuste, konv.getKuvaukset)).toList

          Arvokonvertteri[BigDecimal, BigDecimal](konversioMap)
        } else {
          val konversioMap = arvovalikonvertteriparametrit.asScala.map(konv =>
            LukuarvovalikonversioMerkkijonoilla(konv.getMinValue, konv.getMaxValue, konv.getPaluuarvo,
              konv.getPalautaHaettuArvo, konv.getHylkaysperuste, konv.getKuvaukset)).toList

          Lukuarvovalikonvertteri(konversioMap)
        }

        KonvertoiLukuarvo(konvertteri, muunnaLukuarvofunktioksi(lasketutArgumentit.head), oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn, omaopintopolku)

      case Funktionimi.MAKSIMI =>
        Maksimi(lasketutArgumentit.map(muunnaLukuarvofunktioksi(_)), oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn, omaopintopolku)

      case Funktionimi.MEDIAANI =>
        Mediaani(lasketutArgumentit.map(muunnaLukuarvofunktioksi(_)), oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn, omaopintopolku)

      case Funktionimi.MINIMI =>
        Minimi(lasketutArgumentit.map(muunnaLukuarvofunktioksi(_)), oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn, omaopintopolku)

      case Funktionimi.NEGAATIO =>
        Negaatio(muunnaLukuarvofunktioksi(lasketutArgumentit.head), oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn, omaopintopolku)

      case Funktionimi.NIMETTYLUKUARVO =>
        val nimiParam = getParametri(funktiokuvaus.syoteparametrit.head.avain, syoteparametrit)
        NimettyLukuarvo(nimiParam.getArvo, muunnaLukuarvofunktioksi(lasketutArgumentit.head), oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn, omaopintopolku)

      case Funktionimi.NIMETTYTOTUUSARVO =>
        val nimiParam = getParametri(funktiokuvaus.syoteparametrit.head.avain, syoteparametrit)
        NimettyTotuusarvo(nimiParam.getArvo, muunnaTotuusarvofunktioksi(lasketutArgumentit.head), oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn, omaopintopolku)

      case Funktionimi.NMAKSIMI =>
        val nParam = getParametri(funktiokuvaus.syoteparametrit.head.avain, syoteparametrit)
        NMaksimi(parametriToInteger(nParam), lasketutArgumentit.map(muunnaLukuarvofunktioksi(_)), oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn, omaopintopolku)

      case Funktionimi.NMINIMI =>
        val nParam = getParametri(funktiokuvaus.syoteparametrit.head.avain, syoteparametrit)
        NMinimi(parametriToInteger(nParam), lasketutArgumentit.map(muunnaLukuarvofunktioksi(_)), oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn, omaopintopolku)

      case Funktionimi.OSAMAARA => Osamaara(
        muunnaLukuarvofunktioksi(lasketutArgumentit(0)),
        muunnaLukuarvofunktioksi(lasketutArgumentit(1)),
        oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn, omaopintopolku)

      case Funktionimi.PAINOTETTUKESKIARVO =>
        def muodostaParit(fs: Seq[Funktio[_]], accu: List[Tuple2[Lukuarvofunktio, Lukuarvofunktio]]): List[Tuple2[Lukuarvofunktio, Lukuarvofunktio]] = {
          fs match {
            case first :: second :: rest => muodostaParit(rest, Tuple2(muunnaLukuarvofunktioksi(first), muunnaLukuarvofunktioksi(second)) :: accu)
            case _ => accu
          }
        }

        PainotettuKeskiarvo(oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn, omaopintopolku, muodostaParit(lasketutArgumentit, Nil))

      case Funktionimi.PIENEMPI => Pienempi(
        muunnaLukuarvofunktioksi(lasketutArgumentit(0)),
        muunnaLukuarvofunktioksi(lasketutArgumentit(1)), oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn, omaopintopolku)

      case Funktionimi.PIENEMPITAIYHTASUURI => PienempiTaiYhtasuuri(
        muunnaLukuarvofunktioksi(lasketutArgumentit(0)),
        muunnaLukuarvofunktioksi(lasketutArgumentit(1)),
        oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn, omaopintopolku)

      case Funktionimi.PYORISTYS =>
        val tarkkuus = getParametri("tarkkuus", syoteparametrit)
        Pyoristys(
          parametriToInteger(tarkkuus),
          muunnaLukuarvofunktioksi(lasketutArgumentit(0)),
          oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn, omaopintopolku)

      case Funktionimi.SKAALAUS => {
        val kohdeskaalaMin = parametriToBigDecimal(getParametri("kohdeskaalaMin", syoteparametrit))
        val kohdeskaalaMax = parametriToBigDecimal(getParametri("kohdeskaalaMax", syoteparametrit))

        val kaytaLaskennallistaLahdeskaalaa = parametriToBoolean(getParametri("kaytaLaskennallistaLahdeskaalaa",
          syoteparametrit))

        val lahdeskaala = if (!kaytaLaskennallistaLahdeskaalaa) {
          val lahdeskaalaMin = parametriToBigDecimal(getParametri("lahdeskaalaMin", syoteparametrit))
          val lahdeskaalaMax = parametriToBigDecimal(getParametri("lahdeskaalaMax", syoteparametrit))
          Some(Tuple2(lahdeskaalaMin, lahdeskaalaMax))
        } else None

        Skaalaus(oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn, omaopintopolku, muunnaLukuarvofunktioksi(lasketutArgumentit.head), Tuple2(kohdeskaalaMin, kohdeskaalaMax), lahdeskaala)
      }

      case Funktionimi.SUMMA =>
        Summa(lasketutArgumentit.map(muunnaLukuarvofunktioksi(_)), oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn, omaopintopolku)

      case Funktionimi.SUMMANPARASTA =>
        val nParam = getParametri(funktiokuvaus.syoteparametrit.head.avain, syoteparametrit)
        SummaNParasta(parametriToInteger(nParam), lasketutArgumentit.map(muunnaLukuarvofunktioksi(_)), oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn, omaopintopolku)

      case Funktionimi.TULONPARASTA =>
        val nParam = getParametri(funktiokuvaus.syoteparametrit.head.avain, syoteparametrit)
        TuloNParasta(parametriToInteger(nParam), lasketutArgumentit.map(muunnaLukuarvofunktioksi(_)), oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn, omaopintopolku)

      case Funktionimi.SUUREMPI => Suurempi(
        muunnaLukuarvofunktioksi(lasketutArgumentit(0)),
        muunnaLukuarvofunktioksi(lasketutArgumentit(1)),
        oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn, omaopintopolku)

      case Funktionimi.SUUREMPITAIYHTASUURI => SuurempiTaiYhtasuuri(
        muunnaLukuarvofunktioksi(lasketutArgumentit(0)),
        muunnaLukuarvofunktioksi(lasketutArgumentit(1)),
        oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn, omaopintopolku)

      case Funktionimi.TAI =>
        Tai(lasketutArgumentit.map(muunnaTotuusarvofunktioksi(_)), oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn, omaopintopolku)

      case Funktionimi.TOTUUSARVO => {
        val totuusarvoParam = getParametri(funktiokuvaus.syoteparametrit.head.avain, syoteparametrit)
        Totuusarvo(parametriToBoolean(totuusarvoParam), oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn, omaopintopolku)
      }

      case Funktionimi.TULO =>
        Tulo(lasketutArgumentit.map(muunnaLukuarvofunktioksi(_)), oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn, omaopintopolku)

      case Funktionimi.YHTASUURI => Yhtasuuri(
        muunnaLukuarvofunktioksi(lasketutArgumentit(0)),
        muunnaLukuarvofunktioksi(lasketutArgumentit(1)),
        oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn, omaopintopolku)

      case Funktionimi.HAKUTOIVE => {
        val nParam = getParametri(funktiokuvaus.syoteparametrit.head.avain, syoteparametrit)
        Hakutoive(parametriToInteger(nParam), oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn, omaopintopolku)
      }

      case Funktionimi.HAKUTOIVERYHMASSA => {
        val nParam = getParametri("n", syoteparametrit)
        val ryhmaOid = getParametri("ryhmaoid", syoteparametrit).getArvo
        HakutoiveRyhmassa(parametriToInteger(nParam), ryhmaOid, oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn, omaopintopolku)
      }

      case Funktionimi.HAKUKELPOISUUS => {
        Hakukelpoisuus(oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn, omaopintopolku)
      }

      case Funktionimi.DEMOGRAFIA => {
        val tunniste = getParametri("tunniste", syoteparametrit).getArvo
        val prosenttiosuus = getParametri("prosenttiosuus", syoteparametrit)

        Demografia(oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn, omaopintopolku, tunniste, parametriToBigDecimal(prosenttiosuus))
      }

      case Funktionimi.VALINTAPERUSTEYHTASUURUUS => {
        Valintaperusteyhtasuuruus(oid, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn, omaopintopolku, Tuple2(valintaperusteviitteet(0), valintaperusteviitteet(1)))
      }

      case Funktionimi.HAEYOARVOSANA =>
        List("A", "B", "C", "M", "E", "L", "I").foreach(
          arvosana => {
            if (syoteparametrit.asScala.count(s => s.getAvain == arvosana) == 0) {
              val target = new Syoteparametri
              target.setArvo("0.0")
              target.setAvain(arvosana)
              syoteparametrit.add(target)
            }
          }

        )

        val arvosanaKonvertterit = syoteparametrit.asScala.filter(s => s.getAvain.length == 1).map(
          param => ArvokonversioMerkkijonoilla[String, BigDecimal](param.getAvain, stringToBigDecimal(param.getArvo), "false", new TekstiRyhma)
        ).toList

        val ehdot = yoehdot(syoteparametrit)

        val arvosanaFunktio = HaeYoArvosana(
          Arvokonvertteri[String, BigDecimal](arvosanaKonvertterit),
          ehdot,
          Some(BigDecimal("0.0")),
          valintaperusteviitteet.head,
          omaopintopolku = omaopintopolku)

        val arvosana = if (ehdot.vainValmistuneet) {
          Jos(HaeTotuusarvo(None, Some(false), HakemuksenValintaperuste(YO + TILA_SUFFIX, false), omaopintopolku = omaopintopolku),
            arvosanaFunktio,
            Lukuarvo(BigDecimal("0.0"), omaopintopolku = omaopintopolku),
            omaopintopolku = omaopintopolku)
        } else {
          arvosanaFunktio
        }

        NimettyLukuarvo(s"YO-arvosana (${valintaperusteviitteet.head.tunniste})", arvosana, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn, omaopintopolku = omaopintopolku)


      case Funktionimi.HAEOSAKOEARVOSANA =>
        val konvertteri = if (!arvokonvertteriparametrit.isEmpty) {
          val konversioMap = arvokonvertteriparametrit.asScala.map(konv =>
            ArvokonversioMerkkijonoilla[BigDecimal, BigDecimal](konv.getArvo, stringToBigDecimal(konv.getPaluuarvo),
              konv.getHylkaysperuste, konv.getKuvaukset)).toList

          Some(Arvokonvertteri[BigDecimal, BigDecimal](konversioMap))
        } else if (!arvovalikonvertteriparametrit.isEmpty) {
          val konversioMap = arvovalikonvertteriparametrit.asScala.map(konv => {
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

        val ehdot = yoehdot(syoteparametrit)

        val arvosanaFunktio = HaeYoPisteet(
          konvertteri,
          ehdot,
          Some(BigDecimal("0.0")),
          valintaperusteviitteet.head,
          omaopintopolku = omaopintopolku)

        val arvosana = if (ehdot.vainValmistuneet) {
          Jos(HaeTotuusarvo(None, Some(false), HakemuksenValintaperuste(YO + TILA_SUFFIX, false), omaopintopolku = omaopintopolku),
            arvosanaFunktio, Lukuarvo(BigDecimal("0.0"), omaopintopolku = omaopintopolku),
            omaopintopolku = omaopintopolku)
        } else {
          arvosanaFunktio
        }

        NimettyLukuarvo(s"YO-kokeen pisteet (${valintaperusteviitteet.head.tunniste})", arvosana, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn, omaopintopolku = omaopintopolku)

      case Funktionimi.HAEAMMATILLINENARVOSANA => {
        val arvosana = HaeAmmatillinenArvosana(
          Some(BigDecimal("0.0")),
          valintaperusteviitteet.head,
          omaopintopolku = omaopintopolku)

        NimettyLukuarvo("Ammatillinen arvosana", arvosana, tulosTunniste, tulosTekstiFi, tulosTekstiSv, tulosTekstiEn, omaopintopolku = omaopintopolku)
      }

      case _ => sys.error(s"Could not calculate funktio ${funktionimi.name()}")
    }
  }

  def yoehdot(syoteparametrit: JSet[Syoteparametri]): YoEhdot = {
    val alkuvuosi = syoteparametrit.asScala.find(s => s.getAvain.equals("alkuvuosi") && !s.getArvo.isEmpty).map(value => stringToBigDecimal(value.getArvo).intValue)

    val loppuvuosi = syoteparametrit.asScala.find(s => s.getAvain.equals("loppuvuosi") && !s.getArvo.isEmpty).map(value => stringToBigDecimal(value.getArvo).intValue)

    val alkulukukausi = syoteparametrit.asScala.find(s => s.getAvain.equals("alkulukukausi") && !s.getArvo.isEmpty).map(value => stringToBigDecimal(value.getArvo).intValue)

    val loppulukukausi = syoteparametrit.asScala.find(s => s.getAvain.equals("loppulukukausi") && !s.getArvo.isEmpty).map(value => stringToBigDecimal(value.getArvo).intValue)

    val vainValmistuneet = syoteparametrit.asScala.find(s => s.getAvain.equals("valmistuneet") && !s.getArvo.isEmpty) match {
      case Some(sp: Syoteparametri) => parametriToBoolean(sp)
      case _ => false
    }

    val rooli = syoteparametrit.asScala.find(s => s.getAvain.equals("rooli") && !s.getArvo.isEmpty) match {
      case Some(sp: Syoteparametri) => Some(sp.getArvo)
      case _ => None
    }
    YoEhdot(alkuvuosi, loppuvuosi, alkulukukausi, loppulukukausi, vainValmistuneet, rooli)
  }
}
