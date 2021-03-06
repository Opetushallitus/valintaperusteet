package fi.vm.sade.service.valintaperusteet.laskenta

import java.math.RoundingMode

import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi
import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi.HAELUKUARVO
import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi.HAELUKUARVOEHDOLLA
import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi.HAEMERKKIJONOJAKONVERTOILUKUARVOKSI
import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi.HAETOTUUSARVOJAKONVERTOILUKUARVOKSI
import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi.HYLKAA
import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi.HYLKAAARVOVALILLA
import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi.JOS
import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi.KESKIARVO
import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi.KESKIARVONPARASTA
import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi.KONVERTOILUKUARVO
import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi.LUKUARVO
import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi.MAKSIMI
import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi.MEDIAANI
import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi.MINIMI
import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi.NEGAATIO
import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi.NIMETTYLUKUARVO
import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi.NMAKSIMI
import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi.NMINIMI
import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi.OSAMAARA
import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi.PAINOTETTUKESKIARVO
import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi.PYORISTYS
import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi.SKAALAUS
import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi.SUMMA
import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi.SUMMANPARASTA
import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi.TULO
import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi.TULONPARASTA
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.Arvokonvertteri
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.HaeAmmatillinenYtoArviointiAsteikko
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.HaeAmmatillinenYtoArvosana
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.HaeAmmatillisenTutkinnonKeskiarvo
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.HaeAmmatillisenTutkinnonOsanArvosana
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.HaeAmmatillisenTutkinnonOsanLaajuus
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.HaeAmmatillisenTutkinnonSuoritustapa
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.HaeAmmatillisenTutkinnonYtoOsaAlueenArvosana
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.HaeAmmatillisenTutkinnonYtoOsaAlueenLaajuus
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.HaeLukuarvo
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.HaeLukuarvoEhdolla
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.HaeMerkkijonoJaKonvertoiLukuarvoksi
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.HaeTotuusarvoJaKonvertoiLukuarvoksi
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.HaeYoArvosana
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.HaeYoPisteet
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.HakemuksenValintaperuste
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.Hylkaa
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.HylkaaArvovalilla
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.IteroiAmmatillisenTutkinnonYtoOsaAlueet
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.IteroiAmmatillisetTutkinnonOsat
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.IteroiAmmatillisetTutkinnot
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.Jos
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.Keskiarvo
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.KeskiarvoNParasta
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.KonvertoiLukuarvo
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.Konvertteri
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.Lukuarvo
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.Lukuarvovalikonvertteri
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.Maksimi
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.Mediaani
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.Minimi
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.NMaksimi
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.NMinimi
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.Negaatio
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.NimettyLukuarvo
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.Osamaara
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.PainotettuKeskiarvo
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.Pyoristys
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.Skaalaus
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.Summa
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.SummaNParasta
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.Tulo
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.TuloNParasta
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.Valintaperuste
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.YoEhdot
import fi.vm.sade.service.valintaperusteet.laskenta.api.Hakemus.Kentat
import fi.vm.sade.service.valintaperusteet.laskenta.api.tila.Hylattytila
import fi.vm.sade.service.valintaperusteet.laskenta.api.tila.HylkaaFunktionSuorittamaHylkays
import fi.vm.sade.service.valintaperusteet.laskenta.api.tila.HylkaamistaEiVoidaTulkita
import fi.vm.sade.service.valintaperusteet.laskenta.api.tila.Hyvaksyttavissatila
import fi.vm.sade.service.valintaperusteet.laskenta.api.tila.JakoNollallaVirhe
import fi.vm.sade.service.valintaperusteet.laskenta.api.tila.SkaalattavaArvoEiOleLahdeskaalassaVirhe
import fi.vm.sade.service.valintaperusteet.laskenta.api.tila.Tila
import fi.vm.sade.service.valintaperusteet.laskenta.api.tila.Tila.Tilatyyppi
import fi.vm.sade.service.valintaperusteet.laskenta.api.tila.TuloksiaLiianVahanLahdeskaalanMaarittamiseenVirhe
import fi.vm.sade.service.valintaperusteet.laskenta.api.tila.Virhetila
import org.apache.commons.lang3.StringUtils

import scala.collection.immutable.ListMap
import scala.collection.mutable.ListBuffer
import scala.jdk.CollectionConverters._

protected[laskenta] class LukuarvoLaskin(protected val laskin: Laskin)
    extends LaskinFunktiot
    with IteraatioParametriFunktiot
    with AmmatillisetIterointiFunktiot
    with AmmatillisetArvonHakuFunktiot {

  private val ZERO: BigDecimal = BigDecimal("0.0")
  private val ONE: BigDecimal = BigDecimal("1.0")
  private val TWO: BigDecimal = BigDecimal("2.0")

  protected[laskenta] def laskeLukuarvo(
    laskettava: Lukuarvofunktio,
    iteraatioParametrit: LaskennanIteraatioParametrit
  ): Tulos[BigDecimal] = {

    def summa(vals: Seq[BigDecimal]): BigDecimal = vals.sum
    def tulo(vals: Seq[BigDecimal]): BigDecimal = vals.product

    def muodostaYksittainenTulos(
      f: Lukuarvofunktio,
      trans: BigDecimal => BigDecimal
    ): (Option[BigDecimal], List[Tila], Historia) = {
      laskeLukuarvo(f, iteraatioParametrit) match {
        case Tulos(tulos, tila, historia) => (tulos.map(trans(_)), List(tila), historia)
      }
    }

    def onArvovalilla(
      arvo: BigDecimal,
      arvovali: (BigDecimal, BigDecimal),
      alarajaMukaan: Boolean,
      ylarajaMukaan: Boolean
    ) =
      (alarajaMukaan, ylarajaMukaan) match {
        case (true, true)   => if (arvo >= arvovali._1 && arvo <= arvovali._2) true else false
        case (true, false)  => if (arvo >= arvovali._1 && arvo < arvovali._2) true else false
        case (false, true)  => if (arvo > arvovali._1 && arvo <= arvovali._2) true else false
        case (false, false) => if (arvo > arvovali._1 && arvo < arvovali._2) true else false
      }

    def muodostaKoostettuTulos(
      fs: Seq[Lukuarvofunktio],
      trans: Seq[BigDecimal] => BigDecimal,
      ns: Option[Int] = None
    ): (Option[BigDecimal], List[Tila], Historia) = {
      val tulokset = fs.reverse.foldLeft(
        (Nil, Nil, ListBuffer()): (List[BigDecimal], List[Tila], ListBuffer[Historia])
      )((lst, f) => {
        laskeLukuarvo(f, iteraatioParametrit) match {
          case Tulos(tulos, tila, historia) =>
            lst._3 += historia
            (if (tulos.isDefined) tulos.get :: lst._1 else lst._1, tila :: lst._2, lst._3)
        }
      })

      val lukuarvo =
        if (tulokset._1.isEmpty || (ns.isDefined && tulokset._1.size < ns.get)) None
        else Some(trans(tulokset._1))
      (
        lukuarvo,
        tulokset._2,
        Historia("Koostettu tulos", lukuarvo, tulokset._2, Some(tulokset._3.toList), None)
      )
    }

    def haeLukuarvo(
      konvertteri: Option[Konvertteri[BigDecimal, BigDecimal]],
      oletusarvo: Option[BigDecimal],
      valintaperusteviite: Valintaperuste,
      kentat: Kentat
    ): (Option[BigDecimal], Seq[Tila], Historia) = {
      val (konv, _) = konvertteri match {
        case Some(l: Lukuarvovalikonvertteri) =>
          konversioToLukuarvovalikonversio(l.konversioMap, kentat, laskin.hakukohde)
        case Some(a: Arvokonvertteri[_, _]) =>
          konversioToArvokonversio[BigDecimal, BigDecimal](a.konversioMap, kentat, laskin.hakukohde)
        case _ => (konvertteri, List())
      }
      val (tulos, tila) = laskin.haeValintaperuste[BigDecimal](
        valintaperusteviite,
        kentat,
        s =>
          suoritaOptionalKonvertointi[BigDecimal](
            string2bigDecimal(s, valintaperusteviite.tunniste),
            konv
          ),
        oletusarvo
      )
      (tulos, tila, Historia(HAELUKUARVO, tulos, tila, None, Some(Map("oletusarvo" -> oletusarvo))))

    }

    def haeMerkkijonoJaKonvertoiLukuarvoksi(
      konvertteri: Konvertteri[String, BigDecimal],
      oletusarvo: Option[BigDecimal],
      valintaperusteviite: Valintaperuste,
      kentat: Kentat
    ): (Option[BigDecimal], Seq[Tila], Historia) = {
      if (kentat.isEmpty && oletusarvo.isDefined) {
        val tila: Tila =
          if (valintaperusteviite.pakollinen) new Hylattytila else new Hyvaksyttavissatila
        (
          oletusarvo,
          List(tila),
          Historia(
            HAEMERKKIJONOJAKONVERTOILUKUARVOKSI,
            oletusarvo,
            List(tila),
            None,
            Some(Map("oletusarvo" -> oletusarvo))
          )
        )
      } else {
        konvertteri match {
          case a: Arvokonvertteri[_, _] =>
            val (konv, virheet) = konversioToArvokonversio(a.konversioMap, kentat, laskin.hakukohde)
            if (konv.isEmpty) {
              (
                None,
                virheet,
                Historia(HAEMERKKIJONOJAKONVERTOILUKUARVOKSI, None, virheet, None, None)
              )
            } else {
              val (tulos, tila) = laskin.haeValintaperuste[BigDecimal](
                valintaperusteviite,
                kentat,
                s =>
                  suoritaKonvertointi[String, BigDecimal](
                    (Some(s), new Hyvaksyttavissatila),
                    konv.get.asInstanceOf[Arvokonvertteri[String, BigDecimal]]
                  ),
                oletusarvo
              )

              (
                tulos,
                tila,
                Historia(
                  HAEMERKKIJONOJAKONVERTOILUKUARVOKSI,
                  tulos,
                  tila,
                  None,
                  Some(Map("oletusarvo" -> oletusarvo))
                )
              )
            }
          case _ =>
            val (tulos, tila) = laskin.haeValintaperuste[BigDecimal](
              valintaperusteviite,
              kentat,
              s =>
                suoritaKonvertointi[String, BigDecimal](
                  (Some(s), new Hyvaksyttavissatila),
                  konvertteri
                ),
              oletusarvo
            )
            (
              tulos,
              tila,
              Historia(
                HAEMERKKIJONOJAKONVERTOILUKUARVOKSI,
                tulos,
                tila,
                None,
                Some(Map("oletusarvo" -> oletusarvo))
              )
            )
        }
      }
    }

    def filteredSuoritusTiedot(
      valintaperusteviite: Valintaperuste,
      ehdot: YoEhdot
    ): List[Kentat] = {
      laskin.hakemus.metatiedot
        .getOrElse(valintaperusteviite.tunniste, List())
        .filter(kentat =>
          (ehdot.alkuvuosi.isEmpty || string2integer(
            kentat.get("SUORITUSVUOSI"),
            9999
          ) >= ehdot.alkuvuosi.get) &&
            (ehdot.loppuvuosi.isEmpty || string2integer(
              kentat.get("SUORITUSVUOSI"),
              0
            ) <= ehdot.loppuvuosi.get) &&
            (ehdot.alkulukukausi.isEmpty || (ehdot.alkuvuosi.isDefined &&
              (string2integer(kentat.get("SUORITUSVUOSI"), 0) > ehdot.alkuvuosi.get ||
                string2integer(kentat.get("SUORITUSLUKUKAUSI"), 0) >= ehdot.alkulukukausi.get))) &&
            (ehdot.loppulukukausi.isEmpty || (ehdot.loppuvuosi.isDefined &&
              (string2integer(kentat.get("SUORITUSVUOSI"), 9999) < ehdot.loppuvuosi.get ||
                string2integer(kentat.get("SUORITUSLUKUKAUSI"), 3) <= ehdot.loppulukukausi.get))) &&
            (ehdot.rooli.isEmpty || ehdot.rooli.contains(kentat.getOrElse("ROOLI", "")))
        )
    }

    val (laskettuTulos: Option[BigDecimal], tilat: Seq[Tila], historia: Historia) =
      laskettava match {
        case Lukuarvo(d, _, _, _, _, _, _) =>
          val tila = List(new Hyvaksyttavissatila)
          (Some(d), tila, Historia(LUKUARVO, Some(d), tila, None, None))
        case Negaatio(n, _, _, _, _, _, _) =>
          val (tulos, tilat, h) = muodostaYksittainenTulos(n, d => -d)
          (tulos, tilat, Historia(NEGAATIO, tulos, tilat, Some(List(h)), None))
        case Summa(fs, _, _, _, _, _, _) =>
          val (tulos, tilat, h) = muodostaKoostettuTulos(fs, summa)
          (tulos, tilat, Historia(SUMMA, tulos, tilat, h.historiat, None))

        case SummaNParasta(n, fs, _, _, _, _, _, _) =>
          val (tulos, tilat, h) =
            muodostaKoostettuTulos(fs, ds => summa(ds.sortWith(_ > _).take(n)))
          (
            tulos,
            tilat,
            Historia(SUMMANPARASTA, tulos, tilat, h.historiat, Some(Map("n" -> Some(n))))
          )

        case TuloNParasta(n, fs, _, _, _, _, _, _) =>
          val (tulos, tilat, h) = muodostaKoostettuTulos(fs, ds => tulo(ds.sortWith(_ > _).take(n)))
          (
            tulos,
            tilat,
            Historia(TULONPARASTA, tulos, tilat, h.historiat, Some(Map("n" -> Some(n))))
          )

        case Osamaara(osoittaja, nimittaja, _, _, _, _, _, _) =>
          val nimittajaTulos = laskeLukuarvo(nimittaja, iteraatioParametrit)
          val osoittajaTulos = laskeLukuarvo(osoittaja, iteraatioParametrit)

          val (arvo, laskentatilat) = (for {
            n <- nimittajaTulos.tulos
            o <- osoittajaTulos.tulos
          } yield {
            if (n.intValue == 0)
              (
                None,
                new Virhetila(
                  suomenkielinenHylkaysperusteMap("Jako nollalla"),
                  new JakoNollallaVirhe
                )
              )
            else {
              (
                Some(BigDecimal(o.underlying.divide(n.underlying, 4, RoundingMode.HALF_UP))),
                new Hyvaksyttavissatila
              )
            }
          }) match {
            case Some((arvo, tila)) => (arvo, List(tila))
            case None               => (None, List())
          }

          val tilat = osoittajaTulos.tila :: nimittajaTulos.tila :: laskentatilat
          (
            arvo,
            tilat,
            Historia(
              OSAMAARA,
              arvo,
              tilat,
              Some(List(osoittajaTulos.historia, nimittajaTulos.historia)),
              None
            )
          )

        case Tulo(fs, _, _, _, _, _, _) =>
          val (tulos, tilat, h) = muodostaKoostettuTulos(fs, ds => ds.product)
          (tulos, tilat, Historia(TULO, tulos, tilat, h.historiat, None))

        case Keskiarvo(fs, _, _, _, _, _, _) =>
          val (tulos, tilat, h) = muodostaKoostettuTulos(
            fs,
            ds =>
              BigDecimal(
                summa(ds).underlying.divide(BigDecimal(ds.size).underlying, 4, RoundingMode.HALF_UP)
              )
          )
          (tulos, tilat, Historia(KESKIARVO, tulos, tilat, h.historiat, None))

        case KeskiarvoNParasta(n, fs, _, _, _, _, _, _) =>
          val (tulos, tilat, h) = muodostaKoostettuTulos(
            fs,
            ds => {
              val kaytettavaN = scala.math.min(n, ds.size)
              BigDecimal(
                summa(ds.sortWith(_ > _).take(kaytettavaN)).underlying
                  .divide(BigDecimal(kaytettavaN).underlying, 4, RoundingMode.HALF_UP)
              )
            }
          )
          (
            tulos,
            tilat,
            Historia(KESKIARVONPARASTA, tulos, tilat, h.historiat, Some(Map("n" -> Some(n))))
          )
        case Minimi(fs, _, _, _, _, _, _) =>
          val (tulos, tilat, h) = muodostaKoostettuTulos(fs, ds => ds.min)
          (tulos, tilat, Historia(MINIMI, tulos, tilat, h.historiat, None))

        case f @ Maksimi(_, _, _, _, _, _, _)
            if iteraatioParametrit.sisaltaaAvoimenParametrilistan =>
          maksimiIteroiden(f, iteraatioParametrit)

        case Maksimi(fs, _, _, _, _, _, _) =>
          val (tulos, tilat, h) = muodostaKoostettuTulos(fs, ds => ds.max)
          (tulos, tilat, Historia(MAKSIMI, tulos, tilat, h.historiat, None))

        case NMinimi(ns, fs, _, _, _, _, _, _) =>
          val (tulos, tilat, h) = muodostaKoostettuTulos(
            fs,
            ds => ds.sortWith(_ < _)(scala.math.min(ns, ds.size) - 1),
            Some(ns)
          )
          (tulos, tilat, Historia(NMINIMI, tulos, tilat, h.historiat, Some(Map("ns" -> Some(ns)))))

        case NMaksimi(ns, fs, _, _, _, _, _, _) =>
          val (tulos, tilat, h) = muodostaKoostettuTulos(
            fs,
            ds => ds.sortWith(_ > _)(scala.math.min(ns, ds.size) - 1),
            Some(ns)
          )
          (tulos, tilat, Historia(NMAKSIMI, tulos, tilat, h.historiat, Some(Map("ns" -> Some(ns)))))

        case Mediaani(fs, _, _, _, _, _, _) =>
          val (tulos, tilat, h) = muodostaKoostettuTulos(
            fs,
            ds => {
              val sorted = ds.sortWith(_ < _)
              if (sorted.size % 2 == 1) sorted(sorted.size / 2)
              else
                BigDecimal(
                  (sorted(sorted.size / 2) + sorted(sorted.size / 2 - 1)).underlying
                    .divide(TWO.underlying, 4, RoundingMode.HALF_UP)
                )
            }
          )
          (tulos, tilat, Historia(MEDIAANI, tulos, tilat, h.historiat, None))

        case Jos(
              laskeHaaratLaiskasti,
              ehto,
              thenHaara,
              elseHaara,
              _,
              tulosTunniste,
              tulosTekstiFi,
              _,
              _,
              _
            ) =>
          val ehtoTulos =
            new TotuusarvoLaskin(this.laskin).laskeTotuusarvo(ehto, iteraatioParametrit)
          val (tulos, tilat, historia) =
            ehdollinenTulos[Boolean, (Option[BigDecimal], List[Tila], List[Historia])](
              (ehtoTulos.tulos, ehtoTulos.tila),
              (cond, tila) => {
                if (cond) {
                  laskeTarvittavatJosTulokset(
                    iteraatioParametrit,
                    laskeHaaratLaiskasti,
                    thenHaara,
                    elseHaara,
                    tila
                  )
                } else {
                  laskeTarvittavatJosTulokset(
                    iteraatioParametrit,
                    laskeHaaratLaiskasti,
                    elseHaara,
                    thenHaara,
                    tila
                  )
                }
              },
              (None, List(ehtoTulos.tila), Nil)
            )
          val avaimet: Option[Map[String, Option[Any]]] =
            if (StringUtils.isNotBlank(tulosTunniste)) {
              Some(
                Map(
                  tulosTekstiFi + ": laske vain tarvittavan haaran arvo" -> Option(
                    laskeHaaratLaiskasti
                  ),
                  tulosTekstiFi + ": ehtotulos" -> Option(ehtoTulos.tulos),
                  tulosTekstiFi + ": paluuarvo" -> Option(tulos)
                )
              )
            } else {
              None
            }

          (
            tulos,
            tilat,
            Historia(JOS, tulos, tilat, Some(List(ehtoTulos.historia) ++ historia), avaimet)
          )

        case KonvertoiLukuarvo(konvertteri, f, _, _, tulosTekstiFi, _, _, _) =>
          laskeLukuarvo(f, iteraatioParametrit) match {
            case Tulos(tulos, tila, historia) =>
              val (konv, virheet) = konvertteri match {
                case l: Lukuarvovalikonvertteri =>
                  konversioToLukuarvovalikonversio(
                    l.konversioMap,
                    laskin.hakemus.kentat,
                    laskin.hakukohde
                  )
                case a: Arvokonvertteri[_, _] =>
                  konversioToArvokonversio[BigDecimal, BigDecimal](
                    a.konversioMap,
                    laskin.hakemus.kentat,
                    laskin.hakukohde
                  )
                case _ => (Some(konvertteri), List())
              }
              if (konv.isEmpty) {
                (
                  None,
                  virheet,
                  Historia(KONVERTOILUKUARVO, None, virheet, Some(List(historia)), None)
                )
              } else {
                val (tulos2, tilat2) =
                  suoritaKonvertointi[BigDecimal, BigDecimal]((tulos, tila), konv.get)
                val avaimet: Map[String, Option[Any]] = Map(
                  "Kuvaus" -> Option(tulosTekstiFi),
                  "Lähdearvo" -> tulos,
                  "Konversion tulos" -> tulos2
                )
                (
                  tulos2,
                  tilat2,
                  Historia(KONVERTOILUKUARVO, tulos2, tilat2, Some(List(historia)), Some(avaimet))
                )
              }
          }

        case f: IteroiAmmatillisetTutkinnot => iteroiAmmatillisetTutkinnot(iteraatioParametrit, f)

        case f: IteroiAmmatillisetTutkinnonOsat =>
          iteroiAmmatillisetTutkinnonOsat(iteraatioParametrit, f)

        case f: IteroiAmmatillisenTutkinnonYtoOsaAlueet =>
          iteroiAmmatillisenTutkinnonYtoOsaAlueet(iteraatioParametrit, f)

        case f @ HaeAmmatillinenYtoArvosana(
              konvertteri,
              oletusarvo,
              valintaperusteviite,
              _,
              _,
              _,
              _,
              _,
              _
            ) =>
          haeAmmatillinenYtoArvosana(
            iteraatioParametrit,
            f,
            konvertteri,
            oletusarvo,
            valintaperusteviite
          )

        case f @ HaeAmmatillinenYtoArviointiAsteikko(
              konvertteri,
              oletusarvo,
              valintaperusteviite,
              _,
              _,
              _,
              _,
              _,
              _
            ) =>
          haeAmmatillinenYtoArviointiAsteikko(
            iteraatioParametrit,
            f,
            konvertteri,
            oletusarvo,
            valintaperusteviite
          )

        case f @ HaeAmmatillisenTutkinnonOsanLaajuus(konvertteri, oletusarvo, _, _, _, _, _, _) =>
          haeAmmatillisenTutkinnonOsanLaajuus(iteraatioParametrit, f, konvertteri, oletusarvo)

        case f @ HaeAmmatillisenTutkinnonOsanArvosana(konvertteri, _, _, _, _, _, _) =>
          haeAmmatillisenTutkinnonOsanArvosana(iteraatioParametrit, f, konvertteri)

        case f @ HaeAmmatillisenTutkinnonYtoOsaAlueenLaajuus(
              konvertteri,
              oletusarvo,
              _,
              _,
              _,
              _,
              _,
              _
            ) =>
          haeAmmatillisenTutkinnonYtoOsaAlueenLaajuus(
            iteraatioParametrit,
            f,
            konvertteri,
            oletusarvo
          )

        case f @ HaeAmmatillisenTutkinnonYtoOsaAlueenArvosana(
              konvertteri,
              oletusarvo,
              _,
              _,
              _,
              _,
              _,
              _
            ) =>
          haeAmmatillisenTutkinnonYtoOsaAlueenArvosana(
            iteraatioParametrit,
            f,
            konvertteri,
            oletusarvo
          )

        case f @ HaeAmmatillisenTutkinnonKeskiarvo(konvertteri, _, _, _, _, _, _) =>
          haeAmmatillisenTutkinnonKeskiarvo(iteraatioParametrit, f, konvertteri)

        case f @ HaeAmmatillisenTutkinnonSuoritustapa(konvertteri, oletusarvo, _, _, _, _, _, _) =>
          haeAmmatillisenTutkinnonSuoritustapa(iteraatioParametrit, f, konvertteri, oletusarvo)

        case HaeLukuarvo(konvertteri, oletusarvo, valintaperusteviite, _, _, _, _, _, _) =>
          haeLukuarvo(konvertteri, oletusarvo, valintaperusteviite, laskin.hakemus.kentat)

        case HaeYoPisteet(konvertteri, ehdot, oletusarvo, valintaperusteviite, _, _, _, _, _, _) =>
          val suoritukset = filteredSuoritusTiedot(valintaperusteviite, ehdot)
          val sortedValues = suoritukset.sortWith((kentat1, kentat2) =>
            string2integer(kentat1.get("PISTEET"), 0) > string2integer(kentat2.get("PISTEET"), 0)
          )
          haeLukuarvo(
            konvertteri,
            oletusarvo,
            HakemuksenValintaperuste("PISTEET", valintaperusteviite.pakollinen),
            sortedValues.headOption.getOrElse(Map())
          )

        case HaeLukuarvoEhdolla(
              konvertteri,
              oletusarvo,
              valintaperusteviite,
              ehto,
              _,
              _,
              _,
              _,
              _,
              _
            ) =>
          val tayttyy = ehtoTayttyy(ehto.tunniste, laskin.hakemus.kentat)

          val (konv, _) = konvertteri match {
            case Some(l: Lukuarvovalikonvertteri) =>
              konversioToLukuarvovalikonversio(
                l.konversioMap,
                laskin.hakemus.kentat,
                laskin.hakukohde
              )
            case Some(a: Arvokonvertteri[_, _]) =>
              konversioToArvokonversio[BigDecimal, BigDecimal](
                a.konversioMap,
                laskin.hakemus.kentat,
                laskin.hakukohde
              )
            case _ => (konvertteri, List())
          }

          val vp =
            if (tayttyy) valintaperusteviite else HakemuksenValintaperuste("", pakollinen = false)

          val (tulos, tila) = laskin.haeValintaperuste[BigDecimal](
            vp,
            laskin.hakemus.kentat,
            s => suoritaOptionalKonvertointi[BigDecimal](string2bigDecimal(s, vp.tunniste), konv),
            oletusarvo
          )
          (
            tulos,
            tila,
            Historia(HAELUKUARVOEHDOLLA, tulos, tila, None, Some(Map("oletusarvo" -> oletusarvo)))
          )

        case HaeMerkkijonoJaKonvertoiLukuarvoksi(
              konvertteri,
              oletusarvo,
              valintaperusteviite,
              _,
              _,
              _,
              _,
              _,
              _
            ) =>
          haeMerkkijonoJaKonvertoiLukuarvoksi(
            konvertteri,
            oletusarvo,
            valintaperusteviite,
            laskin.hakemus.kentat
          )

        case HaeYoArvosana(konvertteri, ehdot, oletusarvo, valintaperusteviite, _, _, _, _, _, _) =>
          val suoritukset = filteredSuoritusTiedot(valintaperusteviite, ehdot)
          val sortedValues = suoritukset.sortWith((kentat1, kentat2) =>
            ehdot.YO_ORDER.indexOf(kentat1.getOrElse("ARVO", "I")) < ehdot.YO_ORDER.indexOf(
              kentat2.getOrElse("ARVO", "I")
            )
          )
          haeMerkkijonoJaKonvertoiLukuarvoksi(
            konvertteri,
            oletusarvo,
            HakemuksenValintaperuste("ARVO", valintaperusteviite.pakollinen),
            sortedValues.headOption.getOrElse(Map())
          )

        case HaeTotuusarvoJaKonvertoiLukuarvoksi(
              konvertteri,
              oletusarvo,
              valintaperusteviite,
              _,
              _,
              _,
              _,
              _,
              _
            ) =>
          konvertteri match {
            case a: Arvokonvertteri[_, _] =>
              val (konv, virheet) =
                konversioToArvokonversio(a.konversioMap, laskin.hakemus.kentat, laskin.hakukohde)
              if (konv.isEmpty) {
                (
                  None,
                  virheet,
                  Historia(HAETOTUUSARVOJAKONVERTOILUKUARVOKSI, None, virheet, None, None)
                )
              } else {
                val (tulos, tila) = laskin.haeValintaperuste[BigDecimal](
                  valintaperusteviite,
                  laskin.hakemus.kentat,
                  s =>
                    suoritaKonvertointi[Boolean, BigDecimal](
                      string2boolean(s, valintaperusteviite.tunniste, new Hyvaksyttavissatila),
                      konv.get.asInstanceOf[Arvokonvertteri[Boolean, BigDecimal]]
                    ),
                  oletusarvo
                )

                (
                  tulos,
                  tila,
                  Historia(
                    HAETOTUUSARVOJAKONVERTOILUKUARVOKSI,
                    tulos,
                    tila,
                    None,
                    Some(Map("oletusarvo" -> oletusarvo))
                  )
                )
              }
            case _ =>
              val (tulos, tila) = laskin.haeValintaperuste[BigDecimal](
                valintaperusteviite,
                laskin.hakemus.kentat,
                s =>
                  suoritaKonvertointi[Boolean, BigDecimal](
                    string2boolean(s, valintaperusteviite.tunniste, new Hyvaksyttavissatila),
                    konvertteri
                  ),
                oletusarvo
              )
              (
                tulos,
                tila,
                Historia(
                  HAETOTUUSARVOJAKONVERTOILUKUARVOKSI,
                  tulos,
                  tila,
                  None,
                  Some(Map("oletusarvo" -> oletusarvo))
                )
              )
          }

        case NimettyLukuarvo(nimi, f, _, _, _, _, _, _) =>
          val (tulos, tilat, historia) = muodostaYksittainenTulos(f, d => d)
          val avaimetTallennettavienTulostenHistorioista: ListMap[String, Option[Any]] = ListMap(
            historianTiivistelma(
              historia,
              _ => StringUtils.isNotBlank(f.tulosTunniste),
              h => avaimetHistoriastaIteraatioparametrienKanssa(iteraatioParametrit, h)
            ).flatten: _*
          )
          val avaimet: ListMap[String, Option[Any]] =
            avaimetTallennettavienTulostenHistorioista ++ ListMap("nimi" -> Some(nimi))
          (
            tulos,
            tilat,
            Historia(NIMETTYLUKUARVO, tulos, tilat, Some(List(historia)), Some(avaimet))
          )

        case Pyoristys(tarkkuus, f, _, _, _, _, _, _) =>
          val (tulos, tilat, h) =
            muodostaYksittainenTulos(f, d => d.setScale(tarkkuus, BigDecimal.RoundingMode.HALF_UP))
          (
            tulos,
            tilat,
            Historia(
              PYORISTYS,
              tulos,
              tilat,
              Some(List(h)),
              Some(Map("tarkkuus" -> Some(tarkkuus)))
            )
          )
        case Hylkaa(f, hylkaysperustekuvaus, _, _, _, _, _, _) =>
          new TotuusarvoLaskin(laskin).laskeTotuusarvo(f, iteraatioParametrit) match {
            case Tulos(tulos, tila, historia) =>
              val tila2 = tulos
                .map(b =>
                  if (b)
                    new Hylattytila(
                      hylkaysperustekuvaus.getOrElse(Map.empty[String, String]).asJava,
                      new HylkaaFunktionSuorittamaHylkays
                    )
                  else new Hyvaksyttavissatila
                )
                .getOrElse(
                  new Virhetila(
                    suomenkielinenHylkaysperusteMap(
                      "Hylkäämisfunktion syöte on tyhjä. Hylkäystä ei voida tulkita."
                    ),
                    new HylkaamistaEiVoidaTulkita
                  )
                )
              val tilat = List(tila, tila2)
              (None, tilat, Historia(HYLKAA, None, tilat, Some(List(historia)), None))
          }
        case HylkaaArvovalilla(f, hylkaysperustekuvaus, _, _, _, _, _, _, (min, max)) =>
          laskeLukuarvo(f, iteraatioParametrit) match {
            case Tulos(tulos, tila, historia) =>
              val arvovali = haeArvovali((min, max), laskin.hakukohde, laskin.hakemus.kentat)
              if (tulos.isEmpty && tila.getTilatyyppi.equals(Tilatyyppi.HYVAKSYTTAVISSA)) {
                val virheTila = new Virhetila(
                  suomenkielinenHylkaysperusteMap(
                    "Hylkäämisfunktion syöte on tyhjä. Hylkäystä ei voida tulkita."
                  ),
                  new HylkaamistaEiVoidaTulkita
                )
                (
                  None,
                  List(virheTila),
                  Historia(HYLKAAARVOVALILLA, None, List(virheTila), Some(List(historia)), None)
                )
              } else if (arvovali.isEmpty) {
                val virheTila = new Virhetila(
                  suomenkielinenHylkaysperusteMap("Arvovalin arvoja ei voida muuntaa lukuarvoiksi"),
                  new HylkaamistaEiVoidaTulkita
                )
                (
                  None,
                  List(virheTila),
                  Historia(HYLKAAARVOVALILLA, None, List(virheTila), Some(List(historia)), None)
                )
              } else {
                val arvovaliTila = tulos
                  .map(arvo =>
                    if (
                      onArvovalilla(
                        arvo,
                        (arvovali.get._1, arvovali.get._2),
                        alarajaMukaan = true,
                        ylarajaMukaan = false
                      )
                    )
                      new Hylattytila(
                        hylkaysperustekuvaus.getOrElse(Map.empty[String, String]).asJava,
                        new HylkaaFunktionSuorittamaHylkays
                      )
                    else new Hyvaksyttavissatila
                  )
                  .getOrElse(new Hyvaksyttavissatila)

                val tilat = List(tila, arvovaliTila)
                (
                  tulos,
                  tilat,
                  Historia(HYLKAAARVOVALILLA, tulos, tilat, Some(List(historia)), None)
                )
              }
          }
        case Skaalaus(_, _, _, _, _, _, skaalattava, (kohdeMin, kohdeMax), lahdeskaala) =>
          if (laskin.laskentamoodi != Laskentamoodi.VALINTALASKENTA) {
            val moodi = laskin.laskentamoodi.toString
            moodiVirhe(
              s"Skaalaus-funktiota ei voida suorittaa laskentamoodissa $moodi",
              "Skaalaus",
              moodi
            )
          } else {
            val tulos = laskeLukuarvo(skaalattava, iteraatioParametrit)

            def skaalaa(
              skaalattavaArvo: BigDecimal,
              kohdeskaala: (BigDecimal, BigDecimal),
              lahdeskaala: (BigDecimal, BigDecimal)
            ) = {
              val lahdeRange = lahdeskaala._2 - lahdeskaala._1
              val kohdeRange = kohdeskaala._2 - kohdeskaala._1
              (((skaalattavaArvo - lahdeskaala._1) * kohdeRange) / lahdeRange) + kohdeskaala._1
            }

            val (skaalauksenTulos, tila) = tulos.tulos match {
              case Some(skaalattavaArvo) =>
                lahdeskaala match {
                  case Some((lahdeMin, lahdeMax)) =>
                    if (
                      !onArvovalilla(
                        skaalattavaArvo,
                        (lahdeMin, lahdeMax),
                        alarajaMukaan = true,
                        ylarajaMukaan = true
                      )
                    ) {
                      (
                        None,
                        new Virhetila(
                          suomenkielinenHylkaysperusteMap(
                            s"Arvo ${skaalattavaArvo.toString} ei ole arvovälillä ${lahdeMin.toString} - ${lahdeMax.toString}"
                          ),
                          new SkaalattavaArvoEiOleLahdeskaalassaVirhe(
                            skaalattavaArvo.underlying,
                            lahdeMin.underlying,
                            lahdeMax.underlying
                          )
                        )
                      )
                    } else {
                      val skaalattuArvo =
                        skaalaa(skaalattavaArvo, (kohdeMin, kohdeMax), (lahdeMin, lahdeMax))
                      (Some(skaalattuArvo), new Hyvaksyttavissatila)
                    }
                  case None =>
                    val tulokset = laskin.kaikkiHakemukset
                      .map(h => {
                        Option(
                          Laskin
                            .suoritaValintalaskenta(
                              laskin.hakukohde,
                              h,
                              laskin.kaikkiHakemukset.asJava,
                              skaalattava
                            )
                            .getTulos
                        )
                      })
                      .filter(_.isDefined)
                      .map(_.get)

                    tulokset match {
                      case _ if tulokset.size < 2 =>
                        (
                          None,
                          new Virhetila(
                            suomenkielinenHylkaysperusteMap(
                              "Skaalauksen lähdeskaalaa ei voida määrittää laskennallisesti. " +
                                "Tuloksia on vähemmän kuin 2 kpl tai kaikki tulokset ovat samoja."
                            ),
                            new TuloksiaLiianVahanLahdeskaalanMaarittamiseenVirhe
                          )
                        )
                      case _ =>
                        val lahdeSkaalaMin: BigDecimal = tulokset.min
                        val lahdeSkaalaMax: BigDecimal = tulokset.max

                        // Koska tulokset ovat setissä, tiedetään, että min ja max eivät koskaan voi olla yhtäsuuret
                        (
                          Some(
                            skaalaa(
                              skaalattavaArvo,
                              (kohdeMin, kohdeMax),
                              (lahdeSkaalaMin, lahdeSkaalaMax)
                            )
                          ),
                          new Hyvaksyttavissatila
                        )
                    }
                }
              case None => (None, new Hyvaksyttavissatila)
            }

            val tilat = List(tila, tulos.tila)
            (
              skaalauksenTulos,
              tilat,
              Historia(SKAALAUS, skaalauksenTulos, tilat, Some(List(tulos.historia)), None)
            )
          }

        case f @ PainotettuKeskiarvo(_, _, _, _, _, _, _)
            if iteraatioParametrit.sisaltaaAvoimenParametrilistan =>
          painotettuKeskiarvoIteroiden(f, iteraatioParametrit)

        case PainotettuKeskiarvo(_, _, _, _, _, _, fs) =>
          val tulokset = fs.map(p =>
            Tuple2(
              laskeLukuarvo(p._1, iteraatioParametrit),
              laskeLukuarvo(p._2, iteraatioParametrit)
            )
          )
          val (tilat, historiat) =
            tulokset.reverse.foldLeft(Tuple2(List[Tila](), List[Historia]()))((lst, t) =>
              Tuple2(t._1.tila :: t._2.tila :: lst._1, t._1.historia :: t._2.historia :: lst._2)
            )

          val painokertointenSumma = tulokset.foldLeft(ZERO) { (s, a) =>
            val painokerroin = a._1.tulos
            val painotettava = a._2.tulos

            s + (painotettava match {
              case Some(_) if painokerroin.isEmpty => ONE
              case Some(_)                         => painokerroin.get
              case None                            => ZERO
            })
          }

          val painotettuSumma = tulokset.foldLeft(ZERO)((s, a) =>
            s + a._1.tulos.getOrElse(ONE) * a._2.tulos.getOrElse(ZERO)
          )

          val painotettuKeskiarvo =
            if (painokertointenSumma.intValue == 0) None
            else
              Some(
                BigDecimal(
                  painotettuSumma.underlying
                    .divide(painokertointenSumma.underlying, 4, RoundingMode.HALF_UP)
                )
              )

          (
            painotettuKeskiarvo,
            tilat,
            Historia(PAINOTETTUKESKIARVO, painotettuKeskiarvo, tilat, Some(historiat), None)
          )
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
      val iteraatioParametriTiedot: String = iteraatioParametriTiedotMerkkijono(iteraatioParametrit)
      val tulosTunnisteIteraatioParametrienKanssa =
        s"${laskettava.tulosTunniste}$iteraatioParametriTiedot"
      laskin.funktioTulokset.update(tulosTunnisteIteraatioParametrienKanssa, v)
    }
    Tulos(laskettuTulos, palautettavaTila(tilat), historia)
  }

  private def maksimiIteroiden(
    maksimi: Maksimi,
    iteraatioParametrit: LaskennanIteraatioParametrit
  ): (Option[BigDecimal], List[Tila], Historia) = {
    val parametrit = iteraatioParametrit.avoinParametrilista

    val kierrostenTulokset: Seq[(IteraatioParametri, Tulos[BigDecimal])] =
      parametrit.flatMap(parametri => {
        maksimi.fs.map(lapsiFunktio =>
          (parametri, laskeLukuarvo(lapsiFunktio, iteraatioParametrit.sido(parametri)))
        )
      })
    val tuloksetLukuarvoina: Seq[Lukuarvo] = kierrostenTulokset.flatMap {
      case (parametri, Tulos(Some(lukuarvo), _, historia)) =>
        val ammatillisenHistorianTiivistelma: Seq[String] =
          tiivistelmaAmmatillisistaFunktioista(historia)
        Some(
          Lukuarvo(
            lukuarvo,
            tulosTekstiFi =
              s"Arvo parametrilla '${parametri.kuvaus}' == $lukuarvo, historia: $ammatillisenHistorianTiivistelma"
          )
        )
      case (parametri, tulos) =>
        Laskin.LOG.debug(s"Tyhjä tulos $tulos funktiosta $maksimi parametrilla $parametri")
        None
    }

    val tulos: Tulos[BigDecimal] = if (tuloksetLukuarvoina.nonEmpty) {
      val iteroidutTuloksetKasittelevaMaksimi = maksimi.copy(fs = tuloksetLukuarvoina)
      laskeLukuarvo(iteroidutTuloksetKasittelevaMaksimi, LaskennanIteraatioParametrit())
    } else {
      Tulos(None, new Hyvaksyttavissatila, Historia(MAKSIMI, None, Nil, None, None))
    }

    val kierrostenHistoriat = kierrostenTulokset.map { case (_, t) => t.historia }.toList

    val tilalista = List(tulos.tila)
    val avaimet = ListMap(s"Maksimi ${parametrit.size} iteraatioparametrilla" -> tulos.tulos)
    (
      tulos.tulos,
      tilalista,
      Historia(MAKSIMI, tulos.tulos, tilalista, Some(kierrostenHistoriat), Some(avaimet))
    )
  }

  private def laskeTarvittavatJosTulokset(
    iteraatioParametrit: LaskennanIteraatioParametrit,
    laskeHaaratLaiskasti: Boolean,
    haaraJostaPalautetaanTulos: Lukuarvofunktio,
    toinenHaara: Lukuarvofunktio,
    tila: Tila
  ): (Option[BigDecimal], List[Tila], List[Historia]) = {
    val palautettavaTulos = laskeLukuarvo(haaraJostaPalautetaanTulos, iteraatioParametrit)
    val palautettavatHistoriat: List[Historia] = if (laskeHaaratLaiskasti) {
      List(palautettavaTulos.historia)
    } else {
      val toisenHaaranTulos = laskeLukuarvo(toinenHaara, iteraatioParametrit)
      List(palautettavaTulos.historia, toisenHaaranTulos.historia)
    }
    (palautettavaTulos.tulos, List(tila, palautettavaTulos.tila), palautettavatHistoriat)
  }

  def painotettuKeskiarvoIteroiden(
    painotettuKeskiarvo: PainotettuKeskiarvo,
    iteraatioParametrit: LaskennanIteraatioParametrit
  ): (Option[BigDecimal], List[Tila], Historia) = {
    val parametrit = iteraatioParametrit.avoinParametrilista

    val kierrostenTulokset: Seq[(IteraatioParametri, Tulos[BigDecimal], Tulos[BigDecimal])] =
      parametrit.flatMap(parametri => {
        painotettuKeskiarvo.fs.map(funktioPari => {
          val (painokerroinFunktio, arvoFunktio) = funktioPari
          val tulos1 = laskeLukuarvo(painokerroinFunktio, iteraatioParametrit.sido(parametri))
          val tulos2 = laskeLukuarvo(arvoFunktio, iteraatioParametrit.sido(parametri))
          (parametri, tulos1, tulos2)
        })
      })
    val tuloksetLukuarvoina: Seq[(Lukuarvo, Lukuarvo)] = kierrostenTulokset.flatMap {
      case (
            parametri,
            Tulos(Some(painokerroin), _, painokerroinHistoria),
            Tulos(Some(arvo), _, arvohistoria)
          ) =>
        val ammatillisenHistorianTiivistelma: Seq[String] = tiivistelmaAmmatillisistaFunktioista(
          painokerroinHistoria
        ) ++ tiivistelmaAmmatillisistaFunktioista(arvohistoria)
        val painokerroinLukuarvona = Lukuarvo(
          painokerroin,
          tulosTekstiFi =
            s"Painokerroin parametrilla '${parametri.kuvaus}' == $painokerroin, historia: $ammatillisenHistorianTiivistelma"
        )
        val arvoLukuarvona = Lukuarvo(
          arvo,
          tulosTekstiFi =
            s"Arvo parametrilla '${parametri.kuvaus}' == $arvo, historia: $ammatillisenHistorianTiivistelma"
        )
        Some((painokerroinLukuarvona, arvoLukuarvona))
      case (parametri, Tulos(None, _, _), Tulos(Some(arvo), _, _)) =>
        Laskin.LOG.debug(
          s"Tyhjä painokerroin, arvo $arvo funktiosta $painotettuKeskiarvo parametrilla ${parametri.kuvaus}"
        )
        None
      case (parametri, Tulos(Some(painokerroin), _, _), Tulos(None, _, _)) =>
        Laskin.LOG.debug(
          s"Tyhjä arvo, painokerroin $painokerroin funktiosta $painotettuKeskiarvo parametrilla ${parametri.kuvaus}"
        )
        None
      case (parametri, Tulos(None, _, _), Tulos(None, _, _)) =>
        Laskin.LOG.debug(
          s"Tyhjä arvo ja painokerroin funktiosta $painotettuKeskiarvo parametrilla ${parametri.kuvaus}"
        )
        None
    }

    val tulos: Tulos[BigDecimal] = if (tuloksetLukuarvoina.nonEmpty) {
      val iteroidutTuloksetKasittelevaMaksimi = painotettuKeskiarvo.copy(fs = tuloksetLukuarvoina)
      laskeLukuarvo(iteroidutTuloksetKasittelevaMaksimi, LaskennanIteraatioParametrit())
    } else {
      Tulos(None, new Hyvaksyttavissatila, Historia(PAINOTETTUKESKIARVO, None, Nil, None, None))
    }

    val kierrostenHistoriat = kierrostenTulokset.flatMap {
      case (_, painokerroin, arvo) => List(painokerroin.historia, arvo.historia)
    }.toList

    val tilalista = List(tulos.tila)
    val avaimet = ListMap(
      s"Painotettu keskiarvo ${parametrit.size} iteraatioparametrilla" -> tulos.tulos
    )
    (
      tulos.tulos,
      tilalista,
      Historia(
        PAINOTETTUKESKIARVO,
        tulos.tulos,
        tilalista,
        Some(kierrostenHistoriat),
        Some(avaimet)
      )
    )
  }

  private def avaimetHistoriastaIteraatioparametrienKanssa(
    iteraatioParametrit: LaskennanIteraatioParametrit,
    h: Historia
  ): Map[String, Option[Any]] = {
    h.avaimet.getOrElse(
      Map[String, Option[Any]]().map(kv =>
        (kv._1 + iteraatioParametriTiedotMerkkijono(iteraatioParametrit), kv._2)
      )
    )
  }

  private def iteraatioParametriTiedotMerkkijono(
    iteraatioParametrit: LaskennanIteraatioParametrit
  ): String = {
    if (iteraatioParametrit.nonEmpty) {
      s" (${iteraatioParametrit.asList.map(_.lyhytKuvaus).mkString(" / ")})"
    } else {
      ""
    }
  }

  protected def tiivistelmaAmmatillisistaFunktioista(historia: Historia): Seq[String] = {
    historianTiivistelma(
      historia,
      h => Funktionimi.ammatillistenArvosanojenFunktionimet.asScala.map(_.name).contains(h.funktio)
    )
  }
}
