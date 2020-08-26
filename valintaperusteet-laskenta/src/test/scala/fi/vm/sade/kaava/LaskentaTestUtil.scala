package fi.vm.sade.kaava

import java.util
import java.util.Collections

import fi.vm.sade.service.valintaperusteet.dto.model.{Funktionimi, Valintaperustelahde}
import fi.vm.sade.service.valintaperusteet.laskenta.api.tila._
import fi.vm.sade.service.valintaperusteet.laskenta.api.{Hakemus, Hakutoive}
import fi.vm.sade.service.valintaperusteet.model._
import io.circe.Json
import io.circe.syntax.EncoderOps

import scala.jdk.CollectionConverters._

/**
  * User: kwuoti
  * Date: 4.2.2013
  * Time: 14.16
  */
object LaskentaTestUtil {

  def assertTulosTyhja(tulos: Option[_]) = {
    assert(tulos match {
      case None => true
      case _    => false
    })
  }

  def assertTilaHyvaksyttavissa(tila: Tila): Unit = {
    assert(tila match {
      case _: Hyvaksyttavissatila => true
      case _                      => false
    })
  }

  def assertTilaHylatty(
    tila: Tila,
    hylattymeta: HylattyMetatieto.Hylattymetatietotyyppi,
    kuvaus: Option[String] = None
  ): Unit = {
    assert(tila match {
      case h: Hylattytila =>
        hylattymeta == h.getMetatieto.getMetatietotyyppi && (kuvaus.isEmpty || kuvaus.get == h.getKuvaus)
      case _ => false
    })
  }

  def assertTilaVirhe(tila: Tila, virhemeta: VirheMetatieto.VirheMetatietotyyppi): Unit = {
    assert(tila match {
      case v: Virhetila => virhemeta == v.getMetatieto.getMetatietotyyppi
      case _            => false
    })
  }

  object Funktioargumentti {
    def apply(child: FunktionArgumentti, indeksi: Int) = child match {
      case fk: Funktiokutsu  => new Funktioargumentti(
        null,
        0,
        fk,
        null,
        indeksi
      )
      case lk: Laskentakaava => new Funktioargumentti(
        null,
        0,
        lk.getFunktiokutsu,
        lk,
        indeksi
      )
    }
  }

  object Funktiokutsu {
    def apply(
      nimi: Funktionimi,
      funktioargumentit: Seq[FunktionArgumentti] = Nil,
      syoteparametrit: Seq[Syoteparametri] = Nil,
      arvokonvertterit: Seq[Arvokonvertteriparametri] = Nil,
      arvovalikonvertterit: Seq[Arvovalikonvertteriparametri] = Nil,
      valintaperustetunniste: Seq[ValintaperusteViite] = Nil,
      tulosTunniste: String = "",
      tallennaTulos: Boolean = false,
      tulosTekstiFi: String = ""
    ) = {
      val fargs = for {
        i <- 1 to funktioargumentit.size
        child = funktioargumentit(i - 1)
        arg = Funktioargumentti(child, i)
      } yield arg
      new Funktiokutsu(
        null,
        0,
        nimi,
        tulosTunniste,
        tulosTekstiFi,
        null,
        null,
        tallennaTulos,
        false,
        arvokonvertterit.toSet.asJava,
        arvovalikonvertterit.toList.asJava,
        syoteparametrit.toSet.asJava,
        fargs.toList.asJava,
        valintaperustetunniste.toList.asJava
      )
    }
  }

  object Syoteparametri {
    def apply(avain: String, arvo: String) = {
      new Syoteparametri(
        null,
        0,
        avain,
        arvo
      )
    }
  }

  object Arvokonvertteriparametri {
    def apply(paluuarvo: String, arvo: String, hylkaysperuste: String, kuvaukset: TekstiRyhma) = {
      new Arvokonvertteriparametri(
        null,
        0,
        paluuarvo,
        arvo,
        hylkaysperuste,
        kuvaukset
      )
    }
  }

  object Arvovalikonvertteriparametri {
    def apply(
      paluuarvo: String = "",
      min: String,
      max: String,
      palautaHaettuArvo: String = null,
      hylkaysperuste: String = "false",
      kuvaukset: TekstiRyhma
    ) = {
      new Arvovalikonvertteriparametri(
        null,
        0,
        paluuarvo,
        min,
        max,
        palautaHaettuArvo,
        hylkaysperuste,
        kuvaukset
      )
    }
  }

  object ValintaperusteViite {
    def apply(
      onPakollinen: java.lang.Boolean,
      tunniste: String,
      lahde: Valintaperustelahde = Valintaperustelahde.HAETTAVA_ARVO,
      epasuoraViittaus: Boolean = false,
      indeksi: Int = 1
    ) = {
      new ValintaperusteViite(
        null,
        0,
        tunniste,
        "",
        lahde,
        onPakollinen,
        epasuoraViittaus,
        indeksi,
        new TekstiRyhma(null, 0, new util.HashSet[LokalisoituTeksti]()),
        true,
        true,
        null,
        false
      )
    }
  }

  object TestHakemusWithRyhmaOids {
    def apply(
      oid: String,
      hakutoiveet: List[String],
      ryhmaoidit: List[List[String]],
      kentat: Map[String, String],
      suoritukset: java.util.Map[String, java.util.List[java.util.Map[String, String]]],
      koskiopiskeluoikeudet: Json = List[Unit]().asJson
    ) = {
      val hakutoiveetMap: Map[java.lang.Integer, Hakutoive] = (for {
        prio <- 1 to hakutoiveet.size
      } yield (
        new java.lang.Integer(prio),
        new Hakutoive(hakutoiveet(prio - 1), ryhmaoidit(prio - 1).asJava)
      )).toMap

      new Hakemus(oid, hakutoiveetMap.asJava, kentat.asJava, suoritukset, koskiopiskeluoikeudet)
    }
  }

  object TestHakemus {
    def apply(
      oid: String,
      hakutoiveet: List[String],
      kentat: Map[String, String],
      suoritukset: java.util.Map[String, java.util.List[java.util.Map[String, String]]] =
        Collections.emptyMap(),
      koskiopiskeluoikeudet: Json = List[Unit]().asJson
    ): Hakemus = {
      val ryhmaoidit = hakutoiveet.map(_ => List())
      TestHakemusWithRyhmaOids(
        oid,
        hakutoiveet,
        ryhmaoidit,
        kentat,
        suoritukset,
        koskiopiskeluoikeudet
      )
    }
  }

  object Laskentakaava {
    def apply(
      funktiokutsu: Funktiokutsu,
      nimi: String,
      onLuonnos: java.lang.Boolean,
      kuvaus: String = ""
    ) = {
      new Laskentakaava(
        null,
        0,
        onLuonnos,
        nimi,
        kuvaus,
        null,
        null,
        null,
        funktiokutsu
      )
    }
  }

  def suomenkielinenHylkaysperusteMap(teksti: String) = {
    Map("FI" -> teksti).asJava
  }
}
