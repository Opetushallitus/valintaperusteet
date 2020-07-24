package fi.vm.sade.service.valintaperusteet.laskenta

import java.util

import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.Arvokonversio
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.ArvokonversioMerkkijonoilla
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.Arvokonvertteri
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.Konversio
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.Konvertteri
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.Lukuarvovalikonversio
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.LukuarvovalikonversioMerkkijonoilla
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.Lukuarvovalikonvertteri
import fi.vm.sade.service.valintaperusteet.laskenta.api.Hakemus
import fi.vm.sade.service.valintaperusteet.laskenta.api.Hakemus.Kentat
import fi.vm.sade.service.valintaperusteet.laskenta.api.Hakukohde
import fi.vm.sade.service.valintaperusteet.laskenta.api.tila.ArvokonvertointiVirhe
import fi.vm.sade.service.valintaperusteet.laskenta.api.tila.Hylattytila
import fi.vm.sade.service.valintaperusteet.laskenta.api.tila.Hyvaksyttavissatila
import fi.vm.sade.service.valintaperusteet.laskenta.api.tila.PakollinenValintaperusteHylkays
import fi.vm.sade.service.valintaperusteet.laskenta.api.tila.Tila
import fi.vm.sade.service.valintaperusteet.laskenta.api.tila.ValintaperustettaEiVoidaTulkitaLukuarvoksiVirhe
import fi.vm.sade.service.valintaperusteet.laskenta.api.tila.ValintaperustettaEiVoidaTulkitaTotuusarvoksiVirhe
import fi.vm.sade.service.valintaperusteet.laskenta.api.tila.VirheellinenLaskentamoodiVirhe
import fi.vm.sade.service.valintaperusteet.laskenta.api.tila.Virhetila
import fi.vm.sade.service.valintaperusteet.model.TekstiRyhma

import scala.jdk.CollectionConverters._
import scala.util.Try
import scala.util.matching.Regex

trait LaskinFunktiot {

  val pattern: Regex = """\{\{([A-Za-z\d\-_]+)\.([A-Za-z\d\-_]+)\}\}""".r

  protected def ehdollinenTulos[A, B](
    tulos: (Option[A], Tila),
    f: (A, Tila) => (Option[B], List[Tila])
  ): (Option[B], List[Tila]) = {
    ehdollinenTulos[A, (Option[B], List[Tila])](tulos, f, (None, List(tulos._2)))
  }

  protected def ehdollinenTulos[A, R](
    tulos: (Option[A], Tila),
    f: (A, Tila) => R,
    oletusarvo: R
  ): R = {
    val (alkupTulos, alkupTila) = tulos
    alkupTulos match {
      case Some(t) => f(t, alkupTila)
      case None    => oletusarvo
    }
  }

  protected def suomenkielinenHylkaysperusteMap(teksti: String): util.Map[String, String] = {
    Map("FI" -> teksti).asJava
  }

  protected def suoritaKonvertointi[S, T](
    tulos: (Option[S], Tila),
    konvertteri: Konvertteri[S, T]
  ): (Option[T], List[Tila]) = {
    ehdollinenTulos[S, T](
      tulos,
      (t, tila) => {
        val (konvertoituTulos, konvertoituTila) = konvertteri.konvertoi(t)
        (konvertoituTulos, List(tila, konvertoituTila))
      }
    )
  }

  protected def konvertoi(
    konvertteri: Option[Konvertteri[BigDecimal, BigDecimal]],
    lahdearvo: Option[BigDecimal],
    hakemus: Hakemus,
    hakukohde: Hakukohde
  ): (Option[BigDecimal], List[Tila]) = {
    val (konv, tilatKonvertterinHausta) = konvertteri match {
      case Some(l: Lukuarvovalikonvertteri) =>
        konversioToLukuarvovalikonversio(l.konversioMap, hakemus.kentat, hakukohde)
      case Some(a: Arvokonvertteri[_, _]) =>
        konversioToArvokonversio[BigDecimal, BigDecimal](a.konversioMap, hakemus.kentat, hakukohde)
      case _ => (konvertteri, List())
    }

    koostaKonvertoituTulos[BigDecimal](lahdearvo, lahdearvo, konv, tilatKonvertterinHausta)
  }

  protected def koostaKonvertoituTulos[T](
    lahdearvo: Option[T],
    oletusarvo: Option[BigDecimal],
    konvertteri: Option[Konvertteri[T, BigDecimal]],
    tilatKonvertterinHausta: scala.List[Tila]
  ): (Option[BigDecimal], List[Tila]) = {
    val (tulos, tilaKonvertoinnista): (Option[BigDecimal], Tila) = (for {
      k <- konvertteri
      tulosArvo <- lahdearvo
    } yield k.konvertoi(tulosArvo)).getOrElse((oletusarvo, new Hyvaksyttavissatila))
    val tilalista: List[Tila] = tilaKonvertoinnista :: tilatKonvertterinHausta
    (tulos, tilalista)
  }

  protected def suoritaOptionalKonvertointi[T](
    tulos: (Option[T], Tila),
    konvertteri: Option[Konvertteri[T, T]]
  ): (Option[T], List[Tila]) = {
    ehdollinenTulos[T, T](
      tulos,
      (t, tila) => {
        konvertteri match {
          case Some(konv) =>
            val (konvertoituTulos, konvertoituTila) = konv.konvertoi(t)
            (konvertoituTulos, List(tila, konvertoituTila))
          case None => (Some(t), List(tila))
        }
      }
    )
  }

  protected def haeValintaperuste(
    tunniste: String,
    pakollinen: Boolean,
    kentat: Kentat
  ): (Option[String], Tila) = {
    kentat.get(tunniste) match {
      case Some(s) if !s.trim.isEmpty => (Some(s), new Hyvaksyttavissatila)
      case _ =>
        val tila = if (pakollinen) {
          new Hylattytila(
            suomenkielinenHylkaysperusteMap(
              s"Pakollista arvoa (tunniste $tunniste) ei ole olemassa"
            ),
            new PakollinenValintaperusteHylkays(tunniste)
          )
        } else new Hyvaksyttavissatila

        (None, tila)
    }
  }

  protected def haeValintaperusteHakemukselta(tunniste: String, kentat: Kentat): Option[Any] = {
    kentat.get(tunniste) match {
      case Some(s) if !s.trim.isEmpty =>
        val pilkuton = s.replace(',', '.')
        val result = Try(s.toBoolean).getOrElse(
          Try(BigDecimal(pilkuton)).getOrElse(s)
        )
        Some(result)
      case _ => None
    }
  }

  protected def haeValintaperusteHakukohteelta(
    tunniste: String,
    hakukohde: Hakukohde
  ): Option[Any] = {
    hakukohde.valintaperusteet.get(tunniste) match {
      case Some(s) if !s.trim.isEmpty =>
        val pilkuton = s.replace(',', '.')
        val result = Try(s.toBoolean).getOrElse(
          Try(BigDecimal(pilkuton)).getOrElse(s)
        )
        Some(result)
      case _ => None
    }
  }

  protected def haeValintaperuste(tunniste: String): Option[Any] = {
    val pilkuton = tunniste.replace(',', '.')
    if (tunniste.equalsIgnoreCase("true") || tunniste.equalsIgnoreCase("false")) {
      return Some(tunniste.toBoolean)
    }
    Some(Try(BigDecimal(pilkuton)).getOrElse(tunniste))
  }

  protected def ehtoTayttyy(ehto: String, kentat: Kentat): Boolean = {
    ehto match {
      case pattern(avain, oletus) =>
        haeValintaperusteHakemukselta(avain, kentat) match {
          case Some(arvo: String) => if (arvo.equals(oletus)) true else false
          case _                  => false
        }
      case _ => false
    }
  }

  protected def haeArvovali(
    tunnisteet: (String, String),
    hakukohde: Hakukohde,
    kentat: Kentat
  ): Option[(BigDecimal, BigDecimal)] = {
    val min = tunnisteet._1 match {
      case pattern(source, identifier) =>
        source match {
          case "hakemus"   => haeValintaperusteHakemukselta(identifier, kentat)
          case "hakukohde" => haeValintaperusteHakukohteelta(identifier, hakukohde)
          case _           => None
        }
      case s: String => haeValintaperuste(s)
    }
    val max = tunnisteet._2 match {
      case pattern(source, identifier) =>
        source match {
          case "hakemus"   => haeValintaperusteHakemukselta(identifier, kentat)
          case "hakukohde" => haeValintaperusteHakukohteelta(identifier, hakukohde)
          case _           => None
        }
      case s: String => haeValintaperuste(s)
    }

    (min, max) match {
      case (Some(minimi: BigDecimal), Some(maksimi: BigDecimal)) => Some((minimi, maksimi))
      case _                                                     => None
    }
  }

  protected def palautettavaTila(tilat: Seq[Tila]): Tila = {
    tilat.filter(_ match {
      case _: Virhetila => true
      case _            => false
    }) match {
      case head :: _ => head
      case Nil =>
        tilat.filter(_ match {
          case _: Hylattytila => true
          case _              => false
        }) match {
          case head :: _ => head
          case Nil       => new Hyvaksyttavissatila
        }
    }
  }

  protected def moodiVirhe(
    virheViesti: String,
    funktioNimi: String,
    moodi: String
  ): (None.type, List[Virhetila], Historia) = {
    (
      None,
      List(
        new Virhetila(
          suomenkielinenHylkaysperusteMap(virheViesti),
          new VirheellinenLaskentamoodiVirhe(funktioNimi, moodi)
        )
      ),
      Historia(funktioNimi, None, List(), None, None)
    )
  }

  protected def string2boolean(
    s: String,
    tunniste: String,
    oletustila: Tila = new Hyvaksyttavissatila
  ): (Option[Boolean], Tila) = {
    try {
      (Some(s.toBoolean), oletustila)
    } catch {
      case _: Throwable =>
        (
          None,
          new Virhetila(
            suomenkielinenHylkaysperusteMap(
              s"Arvoa $s ei voida muuttaa Boolean-tyyppiseksi (tunniste $tunniste)"
            ),
            new ValintaperustettaEiVoidaTulkitaTotuusarvoksiVirhe(tunniste)
          )
        )
    }
  }

  protected def string2bigDecimal(
    s: String,
    tunniste: String,
    oletustila: Tila = new Hyvaksyttavissatila
  ): (Option[BigDecimal], Tila) = {
    try {
      (Some(BigDecimal(s.replace(',', '.'))), oletustila)
    } catch {
      case _: Throwable =>
        (
          None,
          new Virhetila(
            suomenkielinenHylkaysperusteMap(
              s"Arvoa $s ei voida muuttaa BigDecimal-tyyppiseksi (tunniste $tunniste)"
            ),
            new ValintaperustettaEiVoidaTulkitaLukuarvoksiVirhe(tunniste)
          )
        )
    }
  }

  protected def string2integer(s: Option[String], default: Int): Int = {
    try {
      s.get.toInt
    } catch {
      case _: Exception => default
    }
  }

  def konversioToLukuarvovalikonversio[S, T](
    konversiot: Seq[Konversio],
    kentat: Kentat,
    hakukohde: Hakukohde
  ): (Option[Lukuarvovalikonvertteri], List[Tila]) = {

    def getLukuarvovaliKonversio(k: Konversio) = {
      val tilat = k match {
        case l: LukuarvovalikonversioMerkkijonoilla =>
          val min = l.min match {
            case pattern(source, identifier) =>
              source match {
                case "hakemus"   => haeValintaperusteHakemukselta(identifier, kentat)
                case "hakukohde" => haeValintaperusteHakukohteelta(identifier, hakukohde)
                case _           => None
              }
            case s: String => haeValintaperuste(s)
          }
          val max = l.max match {
            case pattern(source, identifier) =>
              source match {
                case "hakemus"   => haeValintaperusteHakemukselta(identifier, kentat)
                case "hakukohde" => haeValintaperusteHakukohteelta(identifier, hakukohde)
                case _           => None
              }
            case s: String => haeValintaperuste(s)
          }
          val paluuarvo = l.paluuarvo match {
            case pattern(source, identifier) =>
              source match {
                case "hakemus"   => haeValintaperusteHakemukselta(identifier, kentat)
                case "hakukohde" => haeValintaperusteHakukohteelta(identifier, hakukohde)
                case _           => None
              }
            case s: String => haeValintaperuste(s)
          }
          val palautaHaettuArvo = l.palautaHaettuArvo match {
            case pattern(source, identifier) =>
              source match {
                case "hakemus"   => haeValintaperusteHakemukselta(identifier, kentat)
                case "hakukohde" => haeValintaperusteHakukohteelta(identifier, hakukohde)
                case _           => None
              }
            case s: String => haeValintaperuste(s)
          }
          val hylkaysperuste = l.hylkaysperuste match {
            case pattern(source, identifier) =>
              source match {
                case "hakemus"   => haeValintaperusteHakemukselta(identifier, kentat)
                case "hakukohde" => haeValintaperusteHakukohteelta(identifier, hakukohde)
              }
            case s: String => haeValintaperuste(s)
          }
          (min, max, paluuarvo, palautaHaettuArvo, hylkaysperuste, Some(l.kuvaukset))
        case lk: Lukuarvovalikonversio =>
          (
            Some(lk.min),
            Some(lk.max),
            Some(lk.paluuarvo),
            Some(lk.palautaHaettuArvo),
            Some(lk.hylkaysperuste),
            Some(lk.kuvaukset)
          )
        case _ =>
          (None, None, None, None, false, None, None)
      }

      tilat match {
        case (
              Some(min: BigDecimal),
              Some(max: BigDecimal),
              Some(p: BigDecimal),
              Some(ph: Boolean),
              Some(h: Boolean),
              Some(k: TekstiRyhma)
            ) =>
          Some(Lukuarvovalikonversio(min, max, p, ph, h, k))
        case (
              Some(min: BigDecimal),
              Some(max: BigDecimal),
              Some(p: BigDecimal),
              Some(ph: Boolean),
              Some(h: Boolean),
              Some(null)
            ) =>
          Some(Lukuarvovalikonversio(min, max, p, ph, h, new TekstiRyhma()))
        case _ => None
      }
    }

    val konvertoidut = konversiot.map(konv => getLukuarvovaliKonversio(konv))

    if (konvertoidut.contains(None)) {
      (
        None,
        List(
          new Virhetila(
            suomenkielinenHylkaysperusteMap(s"Konversioita ei voitu muuttaa Arvovälikonversioiksi"),
            new ArvokonvertointiVirhe()
          )
        )
      )
    } else {
      val konvertteri = Lukuarvovalikonvertteri(konvertoidut.map(k => k.get))
      (Some(konvertteri), List(new Hyvaksyttavissatila))
    }
  }

  def konversioToArvokonversio[S, T](
    konversiot: Seq[Konversio],
    kentat: Kentat,
    hakukohde: Hakukohde
  ): (Option[Arvokonvertteri[S, T]], List[Tila]) = {

    def getArvoKonversio(k: Konversio) = {
      val tilat = k match {
        case a: ArvokonversioMerkkijonoilla[S, T] =>
          val arvo = a.arvo match {
            case pattern(source, identifier) =>
              source match {
                case "hakemus"   => haeValintaperusteHakemukselta(identifier, kentat)
                case "hakukohde" => haeValintaperusteHakukohteelta(identifier, hakukohde)
                case _           => None
              }
            case s: String => haeValintaperuste(s)
          }
          val hylkaysperuste = a.hylkaysperuste match {
            case pattern(source, identifier) =>
              source match {
                case "hakemus"   => haeValintaperusteHakemukselta(identifier, kentat)
                case "hakukohde" => haeValintaperusteHakukohteelta(identifier, hakukohde)
              }
            case s: String => haeValintaperuste(s)
          }

          (arvo, Some(a.paluuarvo), hylkaysperuste, Some(a.kuvaukset))
        case ak: Arvokonversio[_, _] =>
          (Some(ak.arvo), Some(ak.paluuarvo), Some(ak.hylkaysperuste), Some(ak.kuvaukset))
        case _ =>
          (None, None, None, None)
      }

      tilat match {
        case (Some(arvo: Any), Some(paluuarvo: Any), Some(h: Boolean), Some(k: TekstiRyhma)) =>
          Some(Arvokonversio(arvo, paluuarvo, h, k))
        case (Some(arvo: Any), Some(paluuarvo: Any), Some(h: Boolean), Some(null)) =>
          Some(Arvokonversio(arvo, paluuarvo, h, new TekstiRyhma()))
        case _ => None
      }
    }

    val konvertoidut = konversiot.map(konv => getArvoKonversio(konv))

    if (konvertoidut.contains(None)) {
      (
        None,
        List(
          new Virhetila(
            suomenkielinenHylkaysperusteMap(s"Konversioita ei voitu muuttaa Arvokonversioiksi"),
            new ArvokonvertointiVirhe()
          )
        )
      )
    } else {
      val konvertteri = Arvokonvertteri[S, T](konvertoidut.map(k => k.get))
      (Some(konvertteri), List(new Hyvaksyttavissatila))
    }
  }
}
