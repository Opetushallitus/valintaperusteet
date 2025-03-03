package fi.vm.sade.service.valintaperusteet.laskenta

import java.lang.{Boolean => JBoolean}
import java.math.{BigDecimal => JBigDecimal}
import java.util.{Collection => JCollection}

import fi.vm.sade.service.valintaperusteet.dto.model.Osallistuminen
import fi.vm.sade.service.valintaperusteet.laskenta.JsonFormats.historiaWrites
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.HakemuksenValintaperuste
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.HakukohteenSyotettavaValintaperuste
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.HakukohteenValintaperuste
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.SyotettavaValintaperuste
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.Valintaperuste
import fi.vm.sade.service.valintaperusteet.laskenta.LaskentaDomain.tekstiryhmaToMap
import fi.vm.sade.service.valintaperusteet.laskenta.api.Hakemus
import fi.vm.sade.service.valintaperusteet.laskenta.api.Hakemus.Kentat
import fi.vm.sade.service.valintaperusteet.laskenta.api.Hakukohde
import fi.vm.sade.service.valintaperusteet.laskenta.api.Laskentatulos
import fi.vm.sade.service.valintaperusteet.laskenta.api.tila.EiOsallistunutHylkays
import fi.vm.sade.service.valintaperusteet.laskenta.api.tila.HakukohteenValintaperusteMaarittelemattaVirhe
import fi.vm.sade.service.valintaperusteet.laskenta.api.tila.Hylattytila
import fi.vm.sade.service.valintaperusteet.laskenta.api.tila.Hyvaksyttavissatila
import fi.vm.sade.service.valintaperusteet.laskenta.api.tila.OsallistumistietoaEiVoidaTulkitaVirhe
import fi.vm.sade.service.valintaperusteet.laskenta.api.tila.SyotettavaArvoMerkitsemattaVirhe
import fi.vm.sade.service.valintaperusteet.laskenta.api.tila.Tila
import fi.vm.sade.service.valintaperusteet.laskenta.api.tila.Virhetila
import fi.vm.sade.service.valintaperusteet.laskenta.api.{FunktioTulos => FTulos}
import fi.vm.sade.service.valintaperusteet.laskenta.api.{SyotettyArvo => SArvo}
import org.slf4j.LoggerFactory
import play.api.libs.json._

import scala.jdk.CollectionConverters._

object Laskin {
  val LOG = LoggerFactory.getLogger(classOf[Laskin])

  private def wrapSyotetytArvot(sa: Map[String, SyotettyArvo]): Map[String, SArvo] = {
    sa.map(e =>
      e._1 -> new SArvo(
        e._1,
        if (e._2.arvo.isEmpty) null else e._2.arvo.get,
        if (e._2.laskennallinenArvo.isEmpty) null else e._2.laskennallinenArvo.get,
        e._2.osallistuminen,
        if (e._2.syotettavanarvontyyppiKoodiUri.isEmpty) null
        else e._2.syotettavanarvontyyppiKoodiUri.get,
        e._2.tilastoidaan
      )
    )
  }

  private def wrapFunktioTulokset(sa: Map[String, FunktioTulos]): Map[String, FTulos] = {
    sa.map(e =>
      e._1 -> new FTulos(
        e._1,
        e._2.arvo,
        e._2.nimiFi,
        e._2.nimiSv,
        e._2.nimiEn,
        e._2.omaopintopolku
      )
    )
  }

  protected def suoritaValintalaskentaLukuarvofunktiolla(
    hakukohde: Hakukohde,
    hakemus: Hakemus,
    kaikkiHakemukset: Option[JCollection[Hakemus]],
    laskettava: Lukuarvofunktio
  ): Laskentatulos[JBigDecimal] = {

    val laskin: Laskin = createLaskin(hakukohde, hakemus, kaikkiHakemukset)
    val lukuarvoLaskin: LukuarvoLaskin = new LukuarvoLaskin(laskin)

    lukuarvoLaskin.laskeLukuarvo(laskettava, LaskennanIteraatioParametrit()) match {
      case Tulos(tulos, tila, historia) =>
        new Laskentatulos[JBigDecimal](
          tila,
          if (tulos.isEmpty) null else tulos.get.underlying,
          String.valueOf(Json.toJson(wrapHistoria(hakemus, historia))),
          wrapSyotetytArvot(laskin.getSyotetytArvot).asJava,
          wrapFunktioTulokset(laskin.getFunktioTulokset).asJava
        )
    }
  }

  private def createLaskin(
    hakukohde: Hakukohde,
    hakemus: Hakemus,
    kaikkiHakemukset: Option[JCollection[Hakemus]]
  ): Laskin = {
    if (kaikkiHakemukset.isDefined) {
      new Laskin(hakukohde, hakemus, kaikkiHakemukset.get.asScala.toSet)
    } else {
      new Laskin(hakukohde, hakemus)
    }
  }

  protected def suoritaValintalaskentaTotuusarvofunktiolla(
    hakukohde: Hakukohde,
    hakemus: Hakemus,
    kaikkiHakemukset: Option[JCollection[Hakemus]],
    laskettava: Totuusarvofunktio
  ): Laskentatulos[JBoolean] = {

    val laskin: Laskin = createLaskin(hakukohde, hakemus, kaikkiHakemukset)
    val totuusarvoLaskin: TotuusarvoLaskin = new TotuusarvoLaskin(laskin)

    totuusarvoLaskin.laskeTotuusarvo(laskettava, LaskennanIteraatioParametrit()) match {
      case Tulos(tulos, tila, historia) =>
        new Laskentatulos[JBoolean](
          tila,
          if (tulos.isEmpty) null else Boolean.box(tulos.get),
          String.valueOf(Json.toJson(wrapHistoria(hakemus, historia))),
          wrapSyotetytArvot(laskin.getSyotetytArvot).asJava,
          wrapFunktioTulokset(laskin.getFunktioTulokset).asJava
        )
    }
  }

  def suoritaValintalaskenta(
    hakukohde: Hakukohde,
    hakemus: Hakemus,
    kaikkiHakemukset: JCollection[Hakemus],
    laskettava: Lukuarvofunktio
  ): Laskentatulos[JBigDecimal] = {

    suoritaValintalaskentaLukuarvofunktiolla(hakukohde, hakemus, Some(kaikkiHakemukset), laskettava)
  }

  def suoritaValintalaskenta(
    hakukohde: Hakukohde,
    hakemus: Hakemus,
    kaikkiHakemukset: java.util.Collection[Hakemus],
    laskettava: Totuusarvofunktio
  ): Laskentatulos[JBoolean] = {

    suoritaValintalaskentaTotuusarvofunktiolla(
      hakukohde,
      hakemus,
      Some(kaikkiHakemukset),
      laskettava
    )

  }

  def suoritaValintakoelaskenta(
    hakukohde: Hakukohde,
    hakemus: Hakemus,
    laskettava: Lukuarvofunktio
  ): Laskentatulos[JBigDecimal] = {

    suoritaValintalaskentaLukuarvofunktiolla(hakukohde, hakemus, None, laskettava)
  }

  def suoritaValintakoelaskenta(
    hakukohde: Hakukohde,
    hakemus: Hakemus,
    laskettava: Totuusarvofunktio
  ): Laskentatulos[JBoolean] = {
    suoritaValintalaskentaTotuusarvofunktiolla(hakukohde, hakemus, None, laskettava)
  }

  def laske(
    hakukohde: Hakukohde,
    hakemus: Hakemus,
    laskettava: Totuusarvofunktio
  ): (Option[Boolean], Tila) = {
    val laskin = new Laskin(hakukohde, hakemus)
    val totuusarvoLaskin = new TotuusarvoLaskin(laskin)
    val tulos = totuusarvoLaskin.laskeTotuusarvo(
      laskettava,
      LaskennanIteraatioParametrit(hylkaaMukautettujaArvosanojaSisaltavatTutkinnot =
        hakukohde.korkeakouluhaku
      )
    )
    (tulos.tulos, tulos.tila)
  }

  def laske(
    hakukohde: Hakukohde,
    hakemus: Hakemus,
    laskettava: Lukuarvofunktio
  ): (Option[JBigDecimal], Tila) = {
    val laskin = new Laskin(hakukohde, hakemus)
    val lukuarvoLaskin = new LukuarvoLaskin(laskin)
    val tulos = lukuarvoLaskin.laskeLukuarvo(
      laskettava,
      LaskennanIteraatioParametrit(hylkaaMukautettujaArvosanojaSisaltavatTutkinnot =
        hakukohde.korkeakouluhaku
      )
    )
    (tulos.tulos.map(_.underlying()), tulos.tila)
  }

  private def wrapHistoria(hakemus: Hakemus, historia: Historia) = {
    val hakemuksenKenttienArvot: Seq[(String, Option[Any])] =
      hakemus.kentat.toSeq.map(f => f._1 -> Some(f._2))
    val hakemuksenMetatietojenArvot: Map[String, Option[Any]] =
      hakemus.metatiedot.toSeq.map(f => f._1 -> Some(f._2)).toMap
    val v: Map[String, Option[Any]] = (hakemuksenKenttienArvot ++ hakemuksenMetatietojenArvot).toMap

    val name = s"Laskenta hakemukselle (${hakemus.oid})"
    Historia(name, historia.tulos, historia.tilat, Some(List(historia)), Some(v))
  }
}

protected[laskenta] class Laskin private (
  val hakukohde: Hakukohde,
  val hakemus: Hakemus,
  val kaikkiHakemukset: Set[Hakemus],
  val laskentamoodi: Laskentamoodi.Laskentamoodi
) extends LaskinFunktiot
    with IteraatioParametriFunktiot {

  def this(hakukohde: Hakukohde, hakemus: Hakemus) {
    this(hakukohde, hakemus, Set(), Laskentamoodi.VALINTAKOELASKENTA)
  }

  def this(hakukohde: Hakukohde, hakemus: Hakemus, kaikkiHakemukset: Set[Hakemus]) {
    this(hakukohde, hakemus, kaikkiHakemukset, Laskentamoodi.VALINTALASKENTA)
  }

  val syotetytArvot: scala.collection.mutable.Map[String, SyotettyArvo] =
    scala.collection.mutable.Map[String, SyotettyArvo]()
  val funktioTulokset: scala.collection.mutable.LinkedHashMap[String, FunktioTulos] =
    scala.collection.mutable.LinkedHashMap[String, FunktioTulos]()

  def getSyotetytArvot: Map[String, SyotettyArvo] =
    Map[String, SyotettyArvo](syotetytArvot.toList: _*)
  def getFunktioTulokset: Map[String, FunktioTulos] =
    Map[String, FunktioTulos](funktioTulokset.toList: _*)

  protected[laskenta] def haeValintaperuste[T](
    valintaperusteviite: Valintaperuste,
    kentat: Kentat,
    konv: String => (Option[T], List[Tila]),
    oletusarvo: Option[T]
  ): (Option[T], List[Tila]) = {
    def haeValintaperusteenArvoHakemukselta(tunniste: String, pakollinen: Boolean) = {
      val (valintaperuste, tila) = haeValintaperuste(tunniste, pakollinen, kentat)

      valintaperuste match {
        case Some(s) =>
          val (konvertoituArvo, tilat) = konv(s)
          (valintaperuste, konvertoituArvo, tilat)
        case None =>
          (valintaperuste, oletusarvo, List(tila))
      }
    }

    // Jos kyseessä on syötettävä valintaperuste, pitää ensin tsekata osallistumistieto
    valintaperusteviite match {
      case SyotettavaValintaperuste(
            tunniste,
            pakollinen,
            osallistuminenTunniste,
            _,
            kuvaukset,
            vaatiiOsallistumisen,
            _,
            tyypinKoodiUri,
            tilastoidaan,
            ammatillisenKielikoeSpecialHandling
          ) =>
        val (osallistuminen, osallistumistila: Tila) =
          hakemus.kentat.get(osallistuminenTunniste) match {
            case Some(osallistuiArvo) =>
              try {
                (Osallistuminen.valueOf(osallistuiArvo), new Hyvaksyttavissatila)
              } catch {
                case _: IllegalArgumentException =>
                  (
                    Osallistuminen.MERKITSEMATTA,
                    new Virhetila(
                      suomenkielinenHylkaysperusteMap(
                        s"Osallistumistietoa $osallistuiArvo ei pystytty tulkitsemaan (tunniste $osallistuminenTunniste)"
                      ),
                      new OsallistumistietoaEiVoidaTulkitaVirhe(osallistuminenTunniste)
                    )
                  )
              }
            case None =>
              if (vaatiiOsallistumisen) (Osallistuminen.MERKITSEMATTA, new Hyvaksyttavissatila)
              else (Osallistuminen.EI_VAADITA, new Hyvaksyttavissatila)
          }

        val checkingAmmatillisenKielikoeOsallistuminenFromHakemus =
          !hakukohde.korkeakouluhaku && ammatillisenKielikoeSpecialHandling &&
            (osallistuminenTunniste == "kielikoe_fi-OSALLISTUMINEN" || osallistuminenTunniste == "kielikoe_sv-OSALLISTUMINEN")
        val overrideAmmatillisenKielikoeOsallistuminenToShowCorrectOsallistuminenForExistingResultInSure =
          checkingAmmatillisenKielikoeOsallistuminenFromHakemus && osallistuminen == Osallistuminen.OSALLISTUI

        val (arvo: Option[String], konvertoitu: Option[T], tilat: List[Tila]) =
          if (
            pakollinen && vaatiiOsallistumisen && Osallistuminen.EI_OSALLISTUNUT == osallistuminen
          )
            (
              None,
              None,
              List[Tila](
                osallistumistila,
                new Hylattytila(tekstiryhmaToMap(kuvaukset), new EiOsallistunutHylkays(tunniste))
              )
            )
          else if (pakollinen && Osallistuminen.MERKITSEMATTA == osallistuminen)
            (
              None,
              None,
              List[Tila](
                osallistumistila,
                new Virhetila(
                  suomenkielinenHylkaysperusteMap(
                    s"Pakollisen syötettävän kentän arvo on merkitsemättä (tunniste $tunniste)"
                  ),
                  new SyotettavaArvoMerkitsemattaVirhe(tunniste)
                )
              )
            )
          else {
            val (arvo, konvertoitu, tilat: Seq[Tila]) =
              haeValintaperusteenArvoHakemukselta(tunniste, pakollinen)
            val (
              osallistumislaskennassaKaytettavaArvo,
              osallistumislaskennassaKaytettavaLaskennallinenArvo
            ) =
              if (
                overrideAmmatillisenKielikoeOsallistuminenToShowCorrectOsallistuminenForExistingResultInSure
              ) {
                (Some("false"), Some(false).asInstanceOf[Some[T]])
              } else {
                (arvo, konvertoitu)
              }
            (
              osallistumislaskennassaKaytettavaArvo,
              osallistumislaskennassaKaytettavaLaskennallinenArvo,
              tilat.prepended(osallistumistila)
            )
          }

        val osallistuminenValueToUse =
          if (
            overrideAmmatillisenKielikoeOsallistuminenToShowCorrectOsallistuminenForExistingResultInSure
          ) {
            Osallistuminen.MERKITSEMATTA
          } else {
            osallistuminen
          }

        syotetytArvot(tunniste) = SyotettyArvo(
          tunniste,
          arvo,
          konvertoitu.map(_.toString),
          osallistuminenValueToUse,
          tyypinKoodiUri,
          tilastoidaan
        )
        (konvertoitu, tilat)
      case HakemuksenValintaperuste(tunniste, pakollinen) =>
        val (_, konvertoitu, tilat: List[Tila]) =
          haeValintaperusteenArvoHakemukselta(tunniste, pakollinen)
        (konvertoitu, tilat)
      case HakukohteenValintaperuste(tunniste, pakollinen, epasuoraViittaus) =>
        hakukohde.valintaperusteet.get(tunniste).filter(!_.trim.isEmpty) match {
          case Some(arvo) =>
            if (epasuoraViittaus) {
              val (_, konvertoitu, tilat: List[Tila]) =
                haeValintaperusteenArvoHakemukselta(arvo, pakollinen)
              (konvertoitu, tilat)
            } else konv(arvo)
          case None =>
            päätteleTila(oletusarvo, tunniste, pakollinen, epasuoraViittaus)
        }
      case HakukohteenSyotettavaValintaperuste(
            tunniste,
            pakollinen,
            epasuoraViittaus,
            osallistumisenTunnistePostfix,
            kuvaus,
            kuvaukset,
            vaatiiOsallistumisen,
            syotettavissaKaikille,
            syotettavanarvontyyppiKoodiUri,
            tilastoidaan
          ) =>
        hakukohde.valintaperusteet.get(tunniste).filter(!_.trim.isEmpty) match {
          case Some(arvo) =>
            if (epasuoraViittaus) {
              val ammatillisenKielikoeOsallistuminenSpecialHandling =
                !hakukohde.korkeakouluhaku && laskentamoodi == Laskentamoodi.VALINTAKOELASKENTA && tunniste == "kielikoe_tunniste"
              haeValintaperuste(
                SyotettavaValintaperuste(
                  arvo,
                  pakollinen,
                  s"$arvo$osallistumisenTunnistePostfix",
                  kuvaus,
                  kuvaukset,
                  vaatiiOsallistumisen,
                  syotettavissaKaikille,
                  syotettavanarvontyyppiKoodiUri,
                  tilastoidaan,
                  ammatillisenKielikoeOsallistuminenSpecialHandling
                ),
                hakemus.kentat,
                konv,
                oletusarvo
              )
            } else konv(arvo)
          case None =>
            päätteleTila(oletusarvo, tunniste, pakollinen, epasuoraViittaus)
        }
    }
  }

  private def päätteleTila[T](
    oletusarvo: Option[T],
    tunniste: String,
    pakollinen: Boolean,
    epasuoraViittaus: Boolean
  ) = {
    val tila =
      if (epasuoraViittaus || pakollinen)
        new Virhetila(
          suomenkielinenHylkaysperusteMap(
            s"Hakukohteen valintaperustetta $tunniste ei ole määritelty"
          ),
          new HakukohteenValintaperusteMaarittelemattaVirhe(tunniste)
        )
      else new Hyvaksyttavissatila

    val arvo =
      if (oletusarvo.isDefined) oletusarvo
      else None

    (arvo, List(tila))
  }
}
