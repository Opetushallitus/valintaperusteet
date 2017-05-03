package fi.vm.sade.kaava

import fi.vm.sade.service.valintaperusteet.dto.model.{Funktionimi, Valintaperustelahde}
import fi.vm.sade.service.valintaperusteet.laskenta.api.tila._
import fi.vm.sade.service.valintaperusteet.laskenta.api.{Hakemus, Hakutoive}
import fi.vm.sade.service.valintaperusteet.model._

import scala.collection.JavaConversions
import scala.collection.JavaConversions._

/**
 * User: kwuoti
 * Date: 4.2.2013
 * Time: 14.16
 */
object LaskentaTestUtil {

  def assertTulosTyhja(tulos: Option[_]) = {
    assert(tulos match {
      case None => true
      case _ => false
    })
  }

  def assertTilaHyvaksyttavissa(tila: Tila): Unit = {
    assert(tila match {
      case _: Hyvaksyttavissatila => true
      case _ => false
    })
  }

  def assertTilaHylatty(tila: Tila, hylattymeta: HylattyMetatieto.Hylattymetatietotyyppi, kuvaus: Option[String] = None): Unit = {
    assert(tila match {
      case h: Hylattytila => hylattymeta == h.getMetatieto.getMetatietotyyppi && (kuvaus.isEmpty || kuvaus.get == h.getKuvaus)
      case _ => false
    })
  }

  def assertTilaVirhe(tila: Tila, virhemeta: VirheMetatieto.VirheMetatietotyyppi): Unit = {
    assert(tila match {
      case v: Virhetila => virhemeta == v.getMetatieto.getMetatietotyyppi
      case _ => false
    })
  }

  object Funktioargumentti {
    def apply(parent: Funktiokutsu, child: FunktionArgumentti, indeksi: Int) = {
      val arg = new Funktioargumentti
      child match {
        case fk: Funktiokutsu => arg.setFunktiokutsuChild(fk)
        case lk: Laskentakaava => arg.setLaskentakaavaChild(lk)
      }
      arg.setIndeksi(indeksi)
      arg.setParent(parent)

      arg
    }
  }

  object Funktiokutsu {
    def apply(nimi: Funktionimi, funktioargumentit: Seq[FunktionArgumentti] = Nil, syoteparametrit: Seq[Syoteparametri] = Nil,
      arvokonvertterit: Seq[Arvokonvertteriparametri] = Nil,
      arvovalikonvertterit: Seq[Arvovalikonvertteriparametri] = Nil,
      valintaperustetunniste: Seq[ValintaperusteViite] = Nil,
      tulosTunniste: String = "", tallennaTulos: Boolean = false, tulosTekstiFi: String = "") = {
      val funktiokutsu = new Funktiokutsu
      funktiokutsu.setFunktionimi(nimi)
      funktiokutsu.setTallennaTulos(tallennaTulos)
      funktiokutsu.setTulosTunniste(tulosTunniste)
      funktiokutsu.setTulosTekstiFi(tulosTekstiFi)

      val fargs = for {
        i <- 1 to funktioargumentit.size
        child = funktioargumentit(i - 1)
        arg = Funktioargumentti(funktiokutsu, child, i)
      } yield arg

      funktiokutsu.setFunktioargumentit(setAsJavaSet(fargs.toSet))
      funktiokutsu.setSyoteparametrit(setAsJavaSet(syoteparametrit.toSet))
      funktiokutsu.setArvokonvertteriparametrit(setAsJavaSet(arvokonvertterit.toSet))
      funktiokutsu.setArvovalikonvertteriparametrit(setAsJavaSet(arvovalikonvertterit.toSet))
      funktiokutsu.setValintaperusteviitteet(setAsJavaSet(valintaperustetunniste.toSet))

      funktiokutsu
    }
  }

  object Syoteparametri {
    def apply(avain: String, arvo: String) = {
      val syoteparametri = new Syoteparametri
      syoteparametri.setArvo(arvo)
      syoteparametri.setAvain(avain)

      syoteparametri
    }
  }

  object Arvokonvertteriparametri {
    def apply(paluuarvo: String, arvo: String, hylkaysperuste: String, kuvaukset: TekstiRyhma) = {
      val konv = new Arvokonvertteriparametri
      konv.setArvo(arvo)
      konv.setHylkaysperuste(hylkaysperuste)
      konv.setPaluuarvo(paluuarvo)
      konv.setKuvaukset(kuvaukset)
      konv
    }
  }

  object Arvovalikonvertteriparametri {
    def apply(paluuarvo: String = "", min: String, max: String, palautaHaettuArvo: String = null, hylkaysperuste: String = "false", kuvaukset: TekstiRyhma) = {
      val konv = new Arvovalikonvertteriparametri
      konv.setMaxValue(max)
      konv.setMinValue(min)
      konv.setPaluuarvo(paluuarvo)
      konv.setPalautaHaettuArvo(palautaHaettuArvo)
      konv.setHylkaysperuste(hylkaysperuste)
      konv.setKuvaukset(kuvaukset)

      konv
    }
  }

  object ValintaperusteViite {
    def apply(onPakollinen: java.lang.Boolean,
      tunniste: String,
      lahde: Valintaperustelahde = Valintaperustelahde.HAETTAVA_ARVO,
      epasuoraViittaus: Boolean = false,
      indeksi: Int = 1) = {
      val viite = new ValintaperusteViite
      viite.setKuvaus("")
      viite.setLahde(lahde)
      viite.setOnPakollinen(onPakollinen)
      viite.setTunniste(tunniste)
      viite.setEpasuoraViittaus(epasuoraViittaus)
      viite.setIndeksi(indeksi)

      viite
    }
  }

  object TestHakemusWithRyhmaOids {
    def apply(oid: String, hakutoiveet: List[String], ryhmaoidit: List[List[String]], kentat: Map[String, String], suoritukset: java.util.Map[String, java.util.List[java.util.Map[String, String]]]) = {
      val hakutoiveetMap: Map[java.lang.Integer, Hakutoive] = (for {
        prio <- 1 to hakutoiveet.size
      } yield (new java.lang.Integer(prio), new Hakutoive(hakutoiveet(prio - 1), ryhmaoidit(prio - 1)))).toMap

      new Hakemus(oid, mapAsJavaMap(hakutoiveetMap), mapAsJavaMap(kentat), suoritukset)
    }
  }

  object TestHakemus {
    def apply(oid: String, hakutoiveet: List[String], kentat: Map[String, String], suoritukset: java.util.Map[String, java.util.List[java.util.Map[String, String]]] = mapAsJavaMap(Map())) = {
      val ryhmaoidit = hakutoiveet.map(_ => List())
      TestHakemusWithRyhmaOids(oid, hakutoiveet, ryhmaoidit, kentat, suoritukset)
    }
  }

  object Laskentakaava {
    def apply(funktiokutsu: Funktiokutsu, nimi: String, onLuonnos: java.lang.Boolean, kuvaus: String = "") = {
      val kaava = new Laskentakaava
      kaava.setFunktiokutsu(funktiokutsu)
      kaava.setTyyppi(funktiokutsu.getFunktionimi.getTyyppi)
      kaava.setKuvaus(kuvaus)
      kaava.setOnLuonnos(onLuonnos)

      kaava
    }
  }

  def suomenkielinenHylkaysperusteMap(teksti: String) = {
    JavaConversions.mapAsJavaMap(Map("FI" -> teksti))
  }

}
