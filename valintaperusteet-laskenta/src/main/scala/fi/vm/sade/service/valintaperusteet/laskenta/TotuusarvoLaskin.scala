package fi.vm.sade.service.valintaperusteet.laskenta

import java.math.RoundingMode

import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi.DEMOGRAFIA
import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi.EI
import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi.HAEMERKKIJONOJAKONVERTOITOTUUSARVOKSI
import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi.HAEMERKKIJONOJAVERTAAYHTASUURUUS
import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi.HAETOTUUSARVO
import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi.HAKUKELPOISUUS
import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi.HAKUTOIVE
import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi.HAKUTOIVERYHMASSA
import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi.JA
import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi.NIMETTYTOTUUSARVO
import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi.PIENEMPI
import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi.PIENEMPITAIYHTASUURI
import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi.SUUREMPI
import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi.SUUREMPITAIYHTASUURI
import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi.TAI
import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi.TOTUUSARVO
import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi.VALINTAPERUSTEYHTASUURUUS
import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi.YHTASUURI
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.Arvokonvertteri
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.Demografia
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.Ei
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.HaeMerkkijonoJaKonvertoiTotuusarvoksi
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.HaeMerkkijonoJaVertaaYhtasuuruus
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.HaeTotuusarvo
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.Hakukelpoisuus
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.Hakutoive
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.HakutoiveRyhmassa
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.Ja
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.NimettyTotuusarvo
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.Pienempi
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.PienempiTaiYhtasuuri
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.Suurempi
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.SuurempiTaiYhtasuuri
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.Tai
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.Totuusarvo
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.Valintaperusteyhtasuuruus
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.Yhtasuuri
import fi.vm.sade.service.valintaperusteet.laskenta.api.tila.Hyvaksyttavissatila
import fi.vm.sade.service.valintaperusteet.laskenta.api.tila.Tila

import scala.collection.mutable.ListBuffer

protected[laskenta] class TotuusarvoLaskin(private val laskin: Laskin) extends LaskinFunktiot {
  private val HUNDRED: BigDecimal = BigDecimal("100.0")

  protected[laskenta] def laskeTotuusarvo(laskettava: Totuusarvofunktio, iteraatioParametrit: LaskennanIteraatioParametrit): Tulos[Boolean] = {

    def muodostaKoostettuTulos(fs: Seq[Totuusarvofunktio], trans: Seq[Boolean] => Boolean) = {
      val (tulokset, tilat, historiat) = fs.reverse.foldLeft((Nil, Nil, ListBuffer()): (List[Option[Boolean]], List[Tila], ListBuffer[Historia]))((lst, f) => {
        laskeTotuusarvo(f, iteraatioParametrit) match {
          case Tulos(tulos, tila, historia) =>
            lst._3 += historia
            (tulos :: lst._1, tila :: lst._2, lst._3)
        }
      })

      val (tyhjat, eiTyhjat) = tulokset.partition(_.isEmpty)

      // jos yksikin laskennasta saaduista arvoista on tyhjä, koko funktion laskenta palauttaa tyhjän
      val totuusarvo = if (tyhjat.nonEmpty) None else Some(trans(eiTyhjat.map(_.get)))
      (totuusarvo, tilat, Historia("Koostettu tulos", totuusarvo, tilat, Some(historiat.toList), None))
    }

    def muodostaYksittainenTulos(f: Totuusarvofunktio, trans: Boolean => Boolean) = {
      laskeTotuusarvo(f, iteraatioParametrit) match {
        case Tulos(tulos, tila, historia) => (tulos.map(trans(_)), List(tila), historia)
      }
    }

    def muodostaVertailunTulos(f1: Lukuarvofunktio, f2: Lukuarvofunktio, trans: (BigDecimal, BigDecimal) => Boolean, iteraatioParametrit: LaskennanIteraatioParametrit) = {
      val tulos1 = new LukuarvoLaskin(laskin).laskeLukuarvo(f1, iteraatioParametrit)
      val tulos2 = new LukuarvoLaskin(laskin).laskeLukuarvo(f2, iteraatioParametrit)
      val tulos = for {
        t1 <- tulos1.tulos
        t2 <- tulos2.tulos
      } yield trans(t1, t2)
      val tilat = List(tulos1.tila, tulos2.tila)
      (tulos, tilat, Historia("Vertailuntulos", tulos, tilat, Some(List(tulos1.historia, tulos2.historia)), None))
    }

    val (laskettuTulos, tilat, hist): (Option[Boolean], List[Tila], Historia) = laskettava match {
      case Ja(fs, _, _,_,_,_,_) =>
        val (tulos, tilat, h) = muodostaKoostettuTulos(fs, lst => lst.forall(b => b))
        (tulos, tilat, Historia(JA, tulos, tilat, h.historiat, None))
      case Tai(fs, _, _,_,_,_,_) =>
        val (tulos, tilat, h) = muodostaKoostettuTulos(fs, lst => lst.exists(b => b))
        (tulos, tilat, Historia(TAI, tulos, tilat, h.historiat, None))
      case Ei(fk, _, _,_,_,_,_) =>
        val (tulos, tilat, h) = muodostaYksittainenTulos(fk, b => !b)
        (tulos, tilat, Historia(EI, tulos, tilat, Some(List(h)), None))
      case Totuusarvo(b, _, _,_,_,_,_) =>
        val tilat = List(new Hyvaksyttavissatila)
        (Some(b), tilat, Historia(TOTUUSARVO, Some(b), tilat, None, None))
      case Suurempi(f1, f2, _, _,_,_,_,_) =>
        val (tulos, tilat, h) = muodostaVertailunTulos(f1, f2, (d1, d2) => d1 > d2, iteraatioParametrit)
        (tulos, tilat, Historia(SUUREMPI, tulos, tilat, Some(List(h)), None))
      case SuurempiTaiYhtasuuri(f1, f2, _, _,_,_,_,_) =>
        val (tulos, tilat, h) = muodostaVertailunTulos(f1, f2, (d1, d2) => d1 >= d2, iteraatioParametrit)
        (tulos, tilat, Historia(SUUREMPITAIYHTASUURI, tulos, tilat, Some(List(h)), None))
      case Pienempi(f1, f2, _, _,_,_,_,_) =>
        val (tulos, tilat, h) = muodostaVertailunTulos(f1, f2, (d1, d2) => d1 < d2, iteraatioParametrit)
        (tulos, tilat, Historia(PIENEMPI, tulos, tilat, Some(List(h)), None))
      case PienempiTaiYhtasuuri(f1, f2, _, _,_,_,_,_) =>
        val (tulos, tilat, h) = muodostaVertailunTulos(f1, f2, (d1, d2) => d1 <= d2, iteraatioParametrit)
        (tulos, tilat, Historia(PIENEMPITAIYHTASUURI, tulos, tilat, Some(List(h)), None))
      case Yhtasuuri(f1, f2, _, _,_,_,_,_) =>
        val (tulos, tilat, h) = muodostaVertailunTulos(f1, f2, (d1, d2) => d1 == d2, iteraatioParametrit)
        (tulos, tilat, Historia(YHTASUURI, tulos, tilat, Some(List(h)), None))
      case HaeTotuusarvo(konvertteri, oletusarvo, valintaperusteviite, _, _,_,_,_,_) =>
        val (konv, _) = konvertteri match {
          case Some(a: Arvokonvertteri[_,_]) => konversioToArvokonversio[Boolean, Boolean](a.konversioMap, laskin.hakemus.kentat, laskin.hakukohde)
          case _ => (konvertteri, List())
        }
        val (tulos, tila) = laskin.haeValintaperuste[Boolean](valintaperusteviite, laskin.hakemus.kentat,
          s => suoritaOptionalKonvertointi[Boolean](string2boolean(s, valintaperusteviite.tunniste),
            konv), oletusarvo)
        (tulos, tila, Historia(HAETOTUUSARVO, tulos, tila, None, Some(Map("oletusarvo" -> oletusarvo))))
      case NimettyTotuusarvo(nimi, f, _, _,_,_,_,_) =>
        val (tulos, tilat, h) = muodostaYksittainenTulos(f, b => b)
        (tulos, tilat, Historia(NIMETTYTOTUUSARVO, tulos, tilat, Some(List(h)), Some(Map("nimi" -> Some(nimi)))))

      case Hakutoive(n, _, _, _, _, _,_) =>
        val onko = Some(laskin.hakemus.onkoHakutoivePrioriteetilla(laskin.hakukohde.hakukohdeOid, n))
        val tilat = List(new Hyvaksyttavissatila)
        (onko, tilat, Historia(HAKUTOIVE, onko, tilat, None, Some(Map("prioriteetti" -> Some(n)))))

      case HakutoiveRyhmassa(n, ryhmaOid, _, _, _, _, _,_) =>
        val onko = Some(laskin.hakemus.onkoHakutoivePrioriteetilla(laskin.hakukohde.hakukohdeOid, n, Some(ryhmaOid)))
        val tilat = List(new Hyvaksyttavissatila)
        (onko, tilat, Historia(HAKUTOIVERYHMASSA, onko, tilat, None, Some(Map("prioriteetti" -> Some(n)))))

      case Hakukelpoisuus(_, _,_,_,_,_) =>
        val onko = Some(laskin.hakemus.onkoHakukelpoinen(laskin.hakukohde.hakukohdeOid))
        val tilat = List(new Hyvaksyttavissatila)
        (onko, tilat, Historia(HAKUKELPOISUUS, onko, tilat, None, Some(Map("hakukohde" -> Some(laskin.hakukohde.hakukohdeOid)))))

      case Demografia(_, _,_,_,_,_, tunniste, prosenttiosuus) =>
        if (laskin.laskentamoodi != Laskentamoodi.VALINTALASKENTA) {
          val moodi = laskin.laskentamoodi.toString
          moodiVirhe(s"Demografia-funktiota ei voida suorittaa laskentamoodissa $moodi", "Demografia", moodi)
        } else {
          val ensisijaisetHakijat = laskin.kaikkiHakemukset.count(_.onkoHakutoivePrioriteetilla(laskin.hakukohde.hakukohdeOid, 1))

          val omaArvo = laskin.hakemus.kentat.toSeq.map(e => e._1.toLowerCase -> e._2).toMap.get(tunniste.toLowerCase)
          val tulos = Some(if (ensisijaisetHakijat == 0) false
          else {
            val samojenArvojenLkm = laskin.kaikkiHakemukset.count(h => h.onkoHakutoivePrioriteetilla(laskin.hakukohde.hakukohdeOid, 1) &&
              h.kentat.toSeq.map(e => e._1.toLowerCase -> e._2).toMap.get(tunniste.toLowerCase) == omaArvo)


            val vertailuarvo = BigDecimal(samojenArvojenLkm).underlying.divide(BigDecimal(ensisijaisetHakijat).underlying, 4, RoundingMode.HALF_UP)
            vertailuarvo.compareTo(prosenttiosuus.underlying.divide(HUNDRED.underlying, 4, RoundingMode.HALF_UP)) != 1
          })

          val tila = new Hyvaksyttavissatila
          (tulos, List(tila), Historia(DEMOGRAFIA, tulos, List(tila), None, Some(Map("avain" -> Some(tunniste), "valintaperuste" -> omaArvo))))
        }

      case HaeMerkkijonoJaKonvertoiTotuusarvoksi(konvertteri, oletusarvo, valintaperusteviite, _, _,_,_,_,_) =>
        konvertteri match {
          case a: Arvokonvertteri[_,_] =>
            val (konv, virheet) = konversioToArvokonversio(a.konversioMap, laskin.hakemus.kentat, laskin.hakukohde)
            if(konv.isEmpty) {
              (None, virheet, Historia(HAEMERKKIJONOJAKONVERTOITOTUUSARVOKSI, None, virheet, None, None))
            } else {
              val (tulos, tila) = laskin.haeValintaperuste[Boolean](valintaperusteviite, laskin.hakemus.kentat,
                s => suoritaKonvertointi[String, Boolean]((Some(s), new Hyvaksyttavissatila), konv.get.asInstanceOf[Arvokonvertteri[String,Boolean]]), oletusarvo)
              (tulos, tila, Historia(HAEMERKKIJONOJAKONVERTOITOTUUSARVOKSI, tulos, tila, None, Some(Map("oletusarvo" -> oletusarvo))))
            }
          case _ =>
            val (tulos, tila) = laskin.haeValintaperuste[Boolean](valintaperusteviite, laskin.hakemus.kentat,
              s => suoritaKonvertointi[String, Boolean]((Some(s), new Hyvaksyttavissatila), konvertteri), oletusarvo)
            (tulos, tila, Historia(HAEMERKKIJONOJAKONVERTOITOTUUSARVOKSI, tulos, tila, None, Some(Map("oletusarvo" -> oletusarvo))))
        }

      case HaeMerkkijonoJaVertaaYhtasuuruus(oletusarvo, valintaperusteviite, vertailtava, _, _,_,_,_,_) =>
        val (tulos, tila) = laskin.haeValintaperuste[Boolean](valintaperusteviite, laskin.hakemus.kentat,
          s => (Some(vertailtava.trim.equalsIgnoreCase(s.trim)), List(new Hyvaksyttavissatila)), oletusarvo)
        (tulos, tila, Historia(HAEMERKKIJONOJAVERTAAYHTASUURUUS, tulos, tila, None, Some(Map("oletusarvo" -> oletusarvo))))

      case Valintaperusteyhtasuuruus(_, _,_,_,_,_, (valintaperusteviite1, valintaperusteviite2)) =>
        val (arvo1, tilat1) = laskin.haeValintaperuste[String](valintaperusteviite1, laskin.hakemus.kentat, s => (Some(s.trim.toLowerCase), List(new Hyvaksyttavissatila)), None)
        val (arvo2, tilat2) = laskin.haeValintaperuste[String](valintaperusteviite2, laskin.hakemus.kentat, s => (Some(s.trim.toLowerCase), List(new Hyvaksyttavissatila)), None)

        val tulos = Some(arvo1 == arvo2)
        val tilat = tilat1 ::: tilat2
        (tulos, tilat, Historia(VALINTAPERUSTEYHTASUURUUS, tulos, tilat, None, Some(Map("tunniste1" -> arvo1, "tunniste2" -> arvo2))))
    }

    if (!laskettava.tulosTunniste.isEmpty) {
      val v = FunktioTulos(
        laskettava.tulosTunniste,
        laskettuTulos.getOrElse("").toString,
        laskettava.tulosTekstiFi,
        laskettava.tulosTekstiSv,
        laskettava.tulosTekstiEn,
        laskettava.omaopintopolku
      )
      laskin.funktioTulokset.update(laskettava.tulosTunniste, v)
    }
    Tulos(laskettuTulos, palautettavaTila(tilat), hist)
  }
}
