package fi.vm.sade.kaava

import fi.vm.sade.service.valintaperusteet.model._
import collection.JavaConversions._
import fi.vm.sade.service.valintaperusteet.laskenta.api.Hakemus
import java.util.TreeSet
import java.math.BigDecimal
import fi.vm.sade.service.valintaperusteet.laskenta.api.tila._

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
              valintaperustetunniste: Seq[ValintaperusteViite] = Nil) = {
      val funktiokutsu = new Funktiokutsu
      funktiokutsu.setFunktionimi(nimi)

      val fargs = for {
        i <- 1 to funktioargumentit.size
        child = funktioargumentit(i - 1)
        arg = Funktioargumentti(funktiokutsu, child, i)
      } yield arg

      funktiokutsu.setFunktioargumentit(setAsJavaSet(fargs.toSet))
      funktiokutsu.setSyoteparametrit(setAsJavaSet(syoteparametrit.toSet))
      funktiokutsu.setArvokonvertteriparametrit(setAsJavaSet(arvokonvertterit.toSet))
      funktiokutsu.setArvovalikonvertteriparametrit(new TreeSet[Arvovalikonvertteriparametri](arvovalikonvertterit.toSet))
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
    def apply(paluuarvo: String, arvo: String, hylkaysperuste: java.lang.Boolean) = {
      val konv = new Arvokonvertteriparametri
      konv.setArvo(arvo)
      konv.setHylkaysperuste(hylkaysperuste)
      konv.setPaluuarvo(paluuarvo)

      konv
    }
  }

  object Arvovalikonvertteriparametri {
    def apply(paluuarvo: String = "", min: BigDecimal, max: BigDecimal, palautaHaettuArvo: java.lang.Boolean = false, hylkaysperuste: java.lang.Boolean) = {
      val konv = new Arvovalikonvertteriparametri
      konv.setMaxValue(max)
      konv.setMinValue(min)
      konv.setPaluuarvo(paluuarvo)
      konv.setHylkaysperuste(hylkaysperuste)
      konv.setPalautaHaettuArvo(palautaHaettuArvo)

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

  object TestHakemus {
    def apply(oid: String, hakutoiveet: List[String], kentat: Map[String, String]) = {
      val hakutoiveetMap: Map[java.lang.Integer, String] = (for {
        prio <- 1 to hakutoiveet.size
      } yield (new java.lang.Integer(prio), hakutoiveet(prio - 1))).toMap

      new Hakemus(oid, mapAsJavaMap(hakutoiveetMap), mapAsJavaMap(kentat))
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

}
