package fi.vm.sade.service.valintaperusteet.laskenta

import fi.vm.sade.service.valintaperusteet.laskenta.api.tila._
import fi.vm.sade.service.valintaperusteet.laskenta.api.{Hakukohde, Osallistuminen, Hakemus}
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

  protected def haeValintaperusteHakemukselta(tunniste: String, oletusarvo:Option[String], hakemus: Hakemus): Option[String] = {
    hakemus.kentat.get(tunniste) match {
      case Some(s) if (!s.trim.isEmpty) => Some(s)
      case None => if (!oletusarvo.isEmpty) oletusarvo else None
    }
  }

  protected def haeValintaperusteHakukohteelta(tunniste: String, oletusarvo:Option[String], hakukohde: Hakukohde): Option[String] = {
    hakukohde.valintaperusteet.get(tunniste) match {
      case Some(s) if (!s.trim.isEmpty) => Some(s)
      case None => if (!oletusarvo.isEmpty) oletusarvo else None
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

  def konversioToLukuarvovalikonversio[S, T](tulos: Tuple2[Option[S], Tila], konversiot: Seq[Konversio], hakemus: Hakemus, hakukohde: Hakukohde): (Option[BigDecimal], List[Tila]) = {
    val pattern = """\{\{([A-Za-z]+)\.([A-Za-z]+)\}\}""".r

    def haeTilaHakemukselta(tunniste: String, oletusarvo: Option[String], lukuarvo: Boolean) = {
      val peruste = haeValintaperusteHakemukselta(tunniste, None, hakemus)
      if(peruste.isEmpty) {
        (None, new Hylattytila(s"Pakollista arvoa (tunniste $tunniste) ei ole olemassa",
          new PakollinenValintaperusteHylkays(tunniste)))
      } else {
        if(lukuarvo) string2bigDecimal(peruste.get, tunniste) else string2boolean(peruste.get, tunniste)

      }
    }

    def haeTilaHakukohteelta(tunniste: String, oletusarvo: Option[String], lukuarvo: Boolean) = {
      val peruste = haeValintaperusteHakukohteelta(tunniste, None, hakukohde)
      if(peruste.isEmpty) {
        (None, new Virhetila(s"Hakukohteen valintaperustetta $tunniste ei ole määritelty",
          new HakukohteenValintaperusteMaarittelemattaVirhe(tunniste)))
      } else {
        if(lukuarvo) string2bigDecimal(peruste.get, tunniste) else string2boolean(peruste.get, tunniste)
      }
    }

    def getLukuarvovaliKonversio(k: Konversio) = {
      val tilat = k match {
        case l: LukuarvovalikonversioMerkkijonoilla => {
          val min = l.min match {
            case pattern(source, identifier) => {
              source match {
                case "hakemus" => haeTilaHakemukselta(identifier, None, true)
                case "hakukohde" => haeTilaHakukohteelta(identifier, None, true)
                // Tarttis toteuttaa uus virhe
                //case _ => VIRHE
              }
            }
            case s: String => string2bigDecimal(s, "")
          }
          val max = l.max match {
            case pattern(source, identifier) => {
              source match {
                case "hakemus" => haeTilaHakemukselta(identifier, None,true)
                case "hakukohde" => haeTilaHakukohteelta(identifier, None, true)
              }
            }
            case s: String => string2bigDecimal(s, "")
          }
          val paluuarvo = l.paluuarvo match {
            case pattern(source, identifier) => {
              source match {
                case "hakemus" => haeTilaHakemukselta(identifier, None, true)
                case "hakukohde" => haeTilaHakukohteelta(identifier, None, true)
              }
            }
            case s: String => string2bigDecimal(s, "")
          }
          val palautaHaettuArvo = l.palautaHaettuArvo match {
            case pattern(source, identifier) => {
              source match {
                case "hakemus" => haeTilaHakemukselta(identifier, None, false)
                case "hakukohde" => haeTilaHakukohteelta(identifier, None, false)
              }
            }
            case s: String => string2boolean(s, "")
          }
          (min, max, paluuarvo, palautaHaettuArvo)
        }
        case lk: Lukuarvovalikonversio => {
         val tila = new Hyvaksyttavissatila

         ((Some(lk.min), tila),(Some(lk.max), tila),(Some(lk.paluuarvo), tila),(Some(lk.palautaHaettuArvo), tila))
        }
        case _ => {
          val tila = new Virhetila(s"Konversioita ei voitu muuttaa Arvovalikonversioiksi",new ArvokonvertointiVirhe())
          ((None,tila),(None,tila),(None,tila),(None,tila))
        }
      }

      tilat match {
        case ((Some(min: BigDecimal), t1: Hyvaksyttavissatila), (Some(max: BigDecimal), t2: Hyvaksyttavissatila),
        (Some(p: BigDecimal), t3: Hyvaksyttavissatila), (Some(ph: Boolean), t4: Hyvaksyttavissatila)) => {
          Some(Lukuarvovalikonversio(min, max, p, ph, false))
        }
        case _ => None
      }

    }

    val konvertoidut = konversiot.map(konv => getLukuarvovaliKonversio(konv))

    if(konvertoidut.contains(None)) {
      (None, List(new Virhetila(s"Konversioita ei voitu muuttaa Arvovalikonversioiksi",new ArvokonvertointiVirhe())))
    } else {
      val konvertteri = Lukuarvovalikonvertteri(konvertoidut.map(k => k.get))
      ehdollinenTulos[S, BigDecimal](tulos, (t, tila) => {
        val (konvertoituTulos, konvertoituTila) = konvertteri.konvertoi(t.asInstanceOf[BigDecimal])
        (konvertoituTulos, List(tila, konvertoituTila))
      })
    }


  }

}
