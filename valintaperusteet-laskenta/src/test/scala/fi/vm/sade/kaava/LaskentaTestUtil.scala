package fi.vm.sade.kaava

import fi.vm.sade.service.valintaperusteet.model._
import collection.JavaConversions._
import fi.vm.sade.service.valintaperusteet.laskenta.api.Hakemus

/**
 * User: kwuoti
 * Date: 4.2.2013
 * Time: 14.16
 */
object LaskentaTestUtil {

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
              valintaperustetunniste: ValintaperusteViite = null) = {
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
      funktiokutsu.setArvovalikonvertteriparametrit(setAsJavaSet(arvovalikonvertterit.toSet))
      funktiokutsu.setValintaperuste(valintaperustetunniste)

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
    def apply(paluuarvo: String = "", min: Double, max: Double, palautaHaettuArvo: java.lang.Boolean = false, hylkaysperuste: java.lang.Boolean) = {
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
    def apply(onPaasykoe: java.lang.Boolean =false, onPakollinen: java.lang.Boolean, tunniste: String) = {
      val viite = new ValintaperusteViite
      viite.setKuvaus("")
      viite.setLahde(Valintaperustelahde.SYOTETTAVA_ARVO)
      viite.setOnPakollinen(onPakollinen)
      viite.setOnPaasykoe(onPaasykoe)
      viite.setTunniste(tunniste)

      viite
    }
  }

  object Hakemus {
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
