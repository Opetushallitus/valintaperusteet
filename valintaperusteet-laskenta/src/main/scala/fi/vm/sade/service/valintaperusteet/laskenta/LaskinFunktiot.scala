package fi.vm.sade.service.valintaperusteet.laskenta

import fi.vm.sade.service.valintaperusteet.laskenta.api.tila._
import fi.vm.sade.service.valintaperusteet.laskenta.api.Hakemus
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta._
import scala.Some
import scala.Tuple2
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.Lukuarvovalikonversio

/**
 * Created with IntelliJ IDEA.
 * User: kjsaila
 * Date: 06/11/13
 * Time: 16:26
 * To change this template use File | Settings | File Templates.
 */
trait LaskinFunktiot {

  protected def ehdollinenTulos[A, B](tulos: (Option[A], Tila), f: (A, Tila) => Tuple2[Option[B], List[Tila]]): Tuple2[Option[B], List[Tila]] = {
    val (alkupTulos, alkupTila) = tulos
    alkupTulos match {
      case Some(t) => f(t, alkupTila)
      case None => (None, List(alkupTila))
    }
  }

  protected def suoritaKonvertointi[S, T](tulos: Tuple2[Option[S], Tila],
                                        konvertteri: Konvertteri[S, T]) = {

    ehdollinenTulos[S, T](tulos, (t, tila) => {
      val (konvertoituTulos, konvertoituTila) = konvertteri.konvertoi(t)
      (konvertoituTulos, List(tila, konvertoituTila))
    })


  }

  protected def suoritaOptionalKonvertointi[T](tulos: Tuple2[Option[T], Tila],
                                             konvertteri: Option[Konvertteri[T, T]]) = {
    ehdollinenTulos[T, T](tulos, (t, tila) => {
      konvertteri match {
        case Some(konv) => {
          val (konvertoituTulos, konvertoituTila) = konv.konvertoi(t)
          (konvertoituTulos, List(tila, konvertoituTila))
        }
        case None => (Some(t), List(tila))
      }
    })
  }

  protected def haeValintaperuste(tunniste: String, pakollinen: Boolean, hakemus: Hakemus): (Option[String], Tila) = {
    hakemus.kentat.get(tunniste) match {
      case Some(s) if (!s.trim.isEmpty) => (Some(s), new Hyvaksyttavissatila)
      case _ => {
        val tila = if (pakollinen) {
          new Hylattytila(s"Pakollista arvoa (tunniste $tunniste) ei ole olemassa",
            new PakollinenValintaperusteHylkays(tunniste))
        } else new Hyvaksyttavissatila

        (None, tila)
      }
    }
  }

  protected def palautettavaTila(tilat: Seq[Tila]): Tila = {
    tilat.filter(_ match {
      case _: Virhetila => true
      case _ => false
    }) match {
      case head :: tail => head
      case Nil => tilat.filter(_ match {
        case _: Hylattytila => true
        case _ => false
      }) match {
        case head :: tail => head
        case Nil => new Hyvaksyttavissatila
      }
    }
  }

  protected def moodiVirhe(virheViesti: String, funktioNimi: String, moodi: String) = {
    (None, List(new Virhetila(virheViesti, new VirheellinenLaskentamoodiVirhe(funktioNimi, moodi))),
      Historia(funktioNimi, None, List(), None, None))
  }

  protected def string2boolean(s: String, tunniste: String, oletustila: Tila = new Hyvaksyttavissatila): (Option[Boolean], Tila) = {
    try {
      (Some(s.toBoolean), oletustila)
    } catch {
      case e: Throwable => (None, new Virhetila(s"Arvoa $s ei voida muuttaa Boolean-tyyppiseksi (tunniste $tunniste)",
        new ValintaperustettaEiVoidaTulkitaTotuusarvoksiVirhe(tunniste)))
    }
  }

  protected def string2bigDecimal(s: String, tunniste: String, oletustila: Tila = new Hyvaksyttavissatila): (Option[BigDecimal], Tila) = {
    try {
      (Some(BigDecimal(s)), oletustila)
    } catch {
      case e: Throwable => (None, new Virhetila(s"Arvoa $s ei voida muuttaa BigDecimal-tyyppiseksi (tunniste $tunniste)",
        new ValintaperustettaEiVoidaTulkitaLukuarvoksiVirhe(tunniste)))
    }
  }

  def haeBooleanHakukohteelta(tunniste: String, hakukohde: Map[String, String]) = {
    hakukohde.get(tunniste) match {
      case Some(arvo) => {
        try {
          arvo.toBoolean
        } catch {
          case e: Throwable => false
        }
      }
      case None => false
    }
  }

  def haeBigDecimalHakukohteelta(tunniste: String, hakukohde: Map[String, String]) = {
    hakukohde.get(tunniste) match {
      case Some(arvo) => {
        try {
          BigDecimal(arvo)
        } catch {
          case e: IllegalArgumentException => BigDecimal("0.0")
        }
      }
      case None => BigDecimal("0.0")
    }
  }

  def haeBooleanHakemukselta(tunniste: String, hakemus: Map[String, String]) = {
    hakemus.get(tunniste) match {
      case Some(arvo) => {
        try {
          arvo.toBoolean
        } catch {
          case e: Throwable => false
        }
      }
      case None => false
    }
  }

  def haeBigDecimalHakemukselta(tunniste: String, hakemus: Map[String, String]) = {
    hakemus.get(tunniste) match {
      case Some(arvo) => {
        try {
          BigDecimal(arvo)
        } catch {
          case e: IllegalArgumentException => BigDecimal("0.0")
        }
      }
      case None => BigDecimal("0.0")
    }
  }

  def konversioToLukuarvovalikonversio(konversiot: Seq[Konversio], hakemus: Map[String, String], hakukohde: Map[String, String]): Seq[Lukuarvovalikonversio] = {
    val pattern = """\{\{([A-Za-z]+)\.([A-Za-z]+)\}\}""".r
    def getLukuarvovaliKonversio(k: Konversio) = {
      k match {
        case l: LukuarvovalikonversioMerkkijonoilla => {
          val min = l.min match {
            case pattern(source, identifier) => {
              source match {
                case "hakemus" => haeBigDecimalHakemukselta(identifier, hakemus)
                case "hakukohde" => haeBigDecimalHakukohteelta(identifier, hakukohde)
                case _ => BigDecimal("0.0")
              }
            }
            case s: String => BigDecimal(s)
          }
          val max = l.max match {
            case pattern(source, identifier) => {
              source match {
                case "hakemus" => haeBigDecimalHakemukselta(identifier, hakemus)
                case "hakukohde" => haeBigDecimalHakukohteelta(identifier, hakukohde)
                case _ => BigDecimal("0.0")
              }
            }
            case s: String => BigDecimal(s)
          }
          val paluuarvo = l.paluuarvo match {
            case pattern(source, identifier) => {
              source match {
                case "hakemus" => haeBigDecimalHakemukselta(identifier, hakemus)
                case "hakukohde" => haeBigDecimalHakukohteelta(identifier, hakukohde)
                case _ => BigDecimal("0.0")
              }
            }
            case s: String => BigDecimal(s)
          }
          val palautaHaettuArvo = l.palautaHaettuArvo match {
            case pattern(source, identifier) => {
              source match {
                case "hakemus" => haeBooleanHakemukselta(identifier, hakemus)
                case "hakukohde" => haeBooleanHakukohteelta(identifier, hakukohde)
                case _ => false
              }
            }
            case s: String => s.toBoolean
          }
          Lukuarvovalikonversio(min, max, paluuarvo, palautaHaettuArvo, l.hylkaysperuste)
        }
        case lk => lk.asInstanceOf[Lukuarvovalikonversio]
      }
    }
    konversiot.map(konv => getLukuarvovaliKonversio(konv))

  }

}
