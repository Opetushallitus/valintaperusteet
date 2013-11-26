package fi.vm.sade.service.valintaperusteet.laskenta

import fi.vm.sade.service.valintaperusteet.laskenta.api.tila._
import fi.vm.sade.service.valintaperusteet.laskenta.api.{Hakukohde, Osallistuminen, Hakemus}
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta._
import scala.Some
import scala.Tuple2
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.Lukuarvovalikonversio
import scala.util.Try

/**
 * Created with IntelliJ IDEA.
 * User: kjsaila
 * Date: 06/11/13
 * Time: 16:26
 * To change this template use File | Settings | File Templates.
 */
trait LaskinFunktiot {

  val pattern = """\{\{([A-Za-z0–9\-_]+)\.([A-Za-z0–9\-_]+)\}\}""".r

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

  protected def haeValintaperusteHakemukselta(tunniste: String, hakemus: Hakemus): Option[Any] = {
    hakemus.kentat.get(tunniste) match {
      case Some(s) if (!s.trim.isEmpty) => {
        val result = Try(s.toBoolean).getOrElse(
          Try(BigDecimal(s)).getOrElse(s)
        )
        Some(result)
      }
      case _ =>  None
    }
  }

  protected def haeValintaperusteHakukohteelta(tunniste: String, hakukohde: Hakukohde): Option[Any] = {
    hakukohde.valintaperusteet.get(tunniste) match {
      case Some(s) if (!s.trim.isEmpty) => {
        val result = Try(s.toBoolean).getOrElse(
          Try(BigDecimal(s)).getOrElse(s)
        )
        Some(result)
      }
      case _ => None
    }
  }

  protected def haeValintaperuste(tunniste: String): Option[Any] = {
    val result = Try(tunniste.toBoolean).getOrElse(
      Try(BigDecimal(tunniste)).getOrElse(tunniste)
    )
    Some(result)
  }

  protected def haeArvovali(tunnisteet: (String,String), hakukohde: Hakukohde, hakemus: Hakemus): Option[(BigDecimal, BigDecimal)] = {
    val min = tunnisteet._1 match {
      case pattern(source, identifier) => {
        source match {
          case "hakemus" => haeValintaperusteHakemukselta(identifier, hakemus)
          case "hakukohde" => haeValintaperusteHakukohteelta(identifier, hakukohde)
          case _ => None
        }
      }
      case s: String => haeValintaperuste(s)
    }
    val max = tunnisteet._2 match {
      case pattern(source, identifier) => {
        source match {
          case "hakemus" => haeValintaperusteHakemukselta(identifier, hakemus)
          case "hakukohde" => haeValintaperusteHakukohteelta(identifier, hakukohde)
          case _ => None
        }
      }
      case s: String => haeValintaperuste(s)
    }

    (min, max) match {
      case (Some(minimi: BigDecimal), Some(maksimi: BigDecimal)) => Some((minimi, maksimi))
      case _ => None
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

  def konversioToLukuarvovalikonversio[S, T](konversiot: Seq[Konversio], hakemus: Hakemus, hakukohde: Hakukohde): (Option[Lukuarvovalikonvertteri], List[Tila]) = {

    def getLukuarvovaliKonversio(k: Konversio) = {
      val tilat = k match {
        case l: LukuarvovalikonversioMerkkijonoilla => {
          val min = l.min match {
            case pattern(source, identifier) => {
              source match {
                case "hakemus" => haeValintaperusteHakemukselta(identifier, hakemus)
                case "hakukohde" => haeValintaperusteHakukohteelta(identifier, hakukohde)
                case _ => None
              }
            }
            case s: String => haeValintaperuste(s)
          }
          val max = l.max match {
            case pattern(source, identifier) => {
              source match {
                case "hakemus" => haeValintaperusteHakemukselta(identifier, hakemus)
                case "hakukohde" => haeValintaperusteHakukohteelta(identifier, hakukohde)
                case _ => None
              }
            }
            case s: String => haeValintaperuste(s)
          }
          val paluuarvo = l.paluuarvo match {
            case pattern(source, identifier) => {
              source match {
                case "hakemus" => haeValintaperusteHakemukselta(identifier, hakemus)
                case "hakukohde" => haeValintaperusteHakukohteelta(identifier, hakukohde)
                case _ => None
              }
            }
            case s: String => haeValintaperuste(s)
          }
          val palautaHaettuArvo = l.palautaHaettuArvo match {
            case pattern(source, identifier) => {
              source match {
                case "hakemus" => haeValintaperusteHakemukselta(identifier, hakemus)
                case "hakukohde" => haeValintaperusteHakukohteelta(identifier, hakukohde)
                case _ => None
              }
            }
            case s: String => haeValintaperuste(s)
          }
          (min, max, paluuarvo, palautaHaettuArvo)
        }
        case lk: Lukuarvovalikonversio => {

         (Some(lk.min),Some(lk.max),Some(lk.paluuarvo),Some(lk.palautaHaettuArvo))
        }
        case _ => {
          (None,None,None,None,false)
        }
      }

      tilat match {
        case (Some(min: BigDecimal), Some(max: BigDecimal),Some(p: BigDecimal), Some(ph: Boolean)) => {
          Some(Lukuarvovalikonversio(min, max, p, ph))
        }
        case _ => None
      }

    }

    val konvertoidut = konversiot.map(konv => getLukuarvovaliKonversio(konv))

    if(konvertoidut.contains(None)) {
      (None, List(new Virhetila(s"Konversioita ei voitu muuttaa Arvovalikonversioiksi",new ArvokonvertointiVirhe())))
    } else {
      val konvertteri = Lukuarvovalikonvertteri(konvertoidut.map(k => k.get))
      (Some(konvertteri), List(new Hyvaksyttavissatila))
    }


  }

  def konversioToArvokonversio[S, T](konversiot: Seq[Konversio], hakemus: Hakemus, hakukohde: Hakukohde): (Option[Arvokonvertteri[S,T]], List[Tila]) = {

    def getArvoKonversio(k: Konversio) = {
      val tilat = k match {
        case a: ArvokonversioMerkkijonoilla[S,T] => {
          val arvo = a.arvo match {
            case pattern(source, identifier) => {
              source match {
                case "hakemus" => haeValintaperusteHakemukselta(identifier, hakemus)
                case "hakukohde" => haeValintaperusteHakukohteelta(identifier, hakukohde)
                case _ => None
              }
            }
            case s: String => haeValintaperuste(s)
          }
          val hylkaysperuste = a.hylkaysperuste match {
            case pattern(source, identifier) => {
              source match {
                case "hakemus" => haeValintaperusteHakemukselta(identifier, hakemus)
                case "hakukohde" => haeValintaperusteHakukohteelta(identifier, hakukohde)
              }
            }
            case s: String => haeValintaperuste(s)
          }

          (arvo, Some(a.paluuarvo), hylkaysperuste)
        }
        case ak: Arvokonversio[_,_] => {

          (Some(ak.arvo),Some(ak.paluuarvo),Some(ak.hylkaysperuste))
        }
        case _ => {
          (None,None,None)
        }
      }

      tilat match {
        case (Some(arvo: Any), Some(paluuarvo: Any), Some(h: Boolean)) => {
          Some(Arvokonversio(arvo, paluuarvo, h))
        }
        case _ => None
      }

    }

    val konvertoidut = konversiot.map(konv => getArvoKonversio(konv))

    if(konvertoidut.contains(None)) {
      (None, List(new Virhetila(s"Konversioita ei voitu muuttaa Arvokonversioiksi",new ArvokonvertointiVirhe())))
    } else {
      val konvertteri = Arvokonvertteri[S,T](konvertoidut.map(k => k.get))
      (Some(konvertteri), List(new Hyvaksyttavissatila))
    }


  }

}
