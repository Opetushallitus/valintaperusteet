package fi.vm.sade.service.valintaperusteet.laskenta

import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi
import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi.HAEAMMATILLINENYTOARVIOINTIASTEIKKO
import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi.HAEAMMATILLINENYTOARVOSANA
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.Arvokonvertteri
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.HaeAmmatillinenYtoArviointiAsteikko
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.HaeAmmatillinenYtoArvosana
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.HaeAmmatillisenTutkinnonKeskiarvo
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.HaeAmmatillisenTutkinnonOsanArvosana
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.HaeAmmatillisenTutkinnonOsanLaajuus
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.HaeAmmatillisenTutkinnonSuoritustapa
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.HaeAmmatillisenTutkinnonYtoOsaAlueenArvosana
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.HaeAmmatillisenTutkinnonYtoOsaAlueenLaajuus
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.Konvertteri
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.Valintaperuste
import fi.vm.sade.service.valintaperusteet.laskenta.api.tila.Hyvaksyttavissatila
import fi.vm.sade.service.valintaperusteet.laskenta.api.tila.Tila
import fi.vm.sade.service.valintaperusteet.laskenta.koski.KoskiLaskenta
import fi.vm.sade.service.valintaperusteet.laskenta.koski.YhteisetTutkinnonOsat

trait AmmatillisetArvonHakuFunktiot {
    this: LukuarvoLaskin =>

  protected def haeAmmatillinenYtoArvosana(iteraatioParametrit: Map[Class[_ <: IteraatioParametri], IteraatioParametri],
                                           f: HaeAmmatillinenYtoArvosana,
                                           konvertteri: Option[Konvertteri[BigDecimal, BigDecimal]],
                                           oletusarvo: Option[BigDecimal],
                                           valintaperusteviite: Valintaperuste
                                          ): (Option[BigDecimal], List[Tila], Historia) = {
    val arvosanaKoskessa: Option[BigDecimal] = YhteisetTutkinnonOsat.haeYtoArvosana(ammatillisenTutkinnonValitsija(iteraatioParametrit, f), laskin.hakemus, valintaperusteviite, oletusarvo)

    val (tulos, tilalista) = konvertoi(konvertteri, arvosanaKoskessa, laskin.hakemus, laskin.hakukohde)

    val uusiHistoria = Historia(
      HAEAMMATILLINENYTOARVOSANA.name(),
      tulos,
      tilalista,
      None,
      Some(Map(
        "oletusarvo" -> oletusarvo,
        "yto-koodiarvo" -> Some(valintaperusteviite.tunniste),
        "lähdearvo" -> arvosanaKoskessa
      )))
    (tulos, tilalista, uusiHistoria)
  }


  protected def haeAmmatillinenYtoArviointiAsteikko(iteraatioParametrit: Map[Class[_ <: IteraatioParametri], IteraatioParametri],
                                                    f: HaeAmmatillinenYtoArviointiAsteikko,
                                                    konvertteri: Konvertteri[String, BigDecimal],
                                                    oletusarvo: Option[BigDecimal],
                                                    valintaperusteviite: Valintaperuste
                                                   ): (Option[BigDecimal], List[Tila], Historia) = {
    val asteikonKoodiKoskessa: Option[String] = YhteisetTutkinnonOsat.haeYtoArviointiasteikko(ammatillisenTutkinnonValitsija(iteraatioParametrit, f), laskin.hakemus, valintaperusteviite)
    val (tulos: Option[BigDecimal], tilalista: List[Tila]) = konvertoiMerkkijono(konvertteri, oletusarvo, asteikonKoodiKoskessa)

    val uusiHistoria = Historia(
      HAEAMMATILLINENYTOARVIOINTIASTEIKKO.name(),
      tulos,
      tilalista,
      None,
      Some(Map(
        "oletusarvo" -> oletusarvo,
        "yto-koodiarvo" -> Some(valintaperusteviite.tunniste),
        "asteikon koodi" -> asteikonKoodiKoskessa
      )))
    (tulos, tilalista, uusiHistoria)
  }

  protected def haeAmmatillisenTutkinnonOsanLaajuus(iteraatioParametrit: Map[Class[_ <: IteraatioParametri], IteraatioParametri],
                                                    f: HaeAmmatillisenTutkinnonOsanLaajuus,
                                                    konvertteri: Option[Konvertteri[BigDecimal, BigDecimal]],
                                                    oletusarvo: Option[BigDecimal]
                                                   ): (Option[BigDecimal], List[Tila], Historia) = {
    val laajuusKoskessa: Option[BigDecimal] = KoskiLaskenta.haeAmmatillisenTutkinnonOsanLaajuus(
      ammatillisenTutkinnonValitsija(iteraatioParametrit, f),
      ammatillisenTutkinnonOsanValitsija(iteraatioParametrit, f),
      laskin.hakemus,
      oletusarvo)

    val (tulos: Option[BigDecimal], tilalista: List[Tila]) = konvertoi(konvertteri, laajuusKoskessa, laskin.hakemus, laskin.hakukohde)

    val uusiHistoria = Historia(
      Funktionimi.HAEAMMATILLISENOSANLAAJUUS.name(),
      tulos,
      tilalista,
      None,
      Some(Map(
        "oletusarvo" -> oletusarvo,
        "lähdearvo" -> laajuusKoskessa
      )))
    (tulos, tilalista, uusiHistoria)
  }

  protected def haeAmmatillisenTutkinnonOsanArvosana(iteraatioParametrit: Map[Class[_ <: IteraatioParametri], IteraatioParametri],
                                                     f: HaeAmmatillisenTutkinnonOsanArvosana,
                                                     konvertteri: Option[Konvertteri[BigDecimal, BigDecimal]]
                                                    ): (Option[BigDecimal], List[Tila], Historia) = {
    val arvosanaKoskessa: Option[BigDecimal] = KoskiLaskenta.haeAmmatillisenTutkinnonOsanArvosana(
      ammatillisenTutkinnonValitsija(iteraatioParametrit, f),
      ammatillisenTutkinnonOsanValitsija(iteraatioParametrit, f),
      laskin.hakemus)

    val (tulos: Option[BigDecimal], tilalista: List[Tila]) = konvertoi(konvertteri, arvosanaKoskessa, laskin.hakemus, laskin.hakukohde)

    val uusiHistoria = Historia(
      Funktionimi.HAEAMMATILLISENOSANARVOSANA.name(),
      tulos,
      tilalista,
      None,
      Some(Map(
        "lähdearvo" -> arvosanaKoskessa
      )))
    (tulos, tilalista, uusiHistoria)
  }

  protected def haeAmmatillisenTutkinnonYtoOsaAlueenLaajuus(iteraatioParametrit: Map[Class[_ <: IteraatioParametri], IteraatioParametri],
                                                            f: HaeAmmatillisenTutkinnonYtoOsaAlueenLaajuus,
                                                            konvertteri: Option[Konvertteri[BigDecimal, BigDecimal]],
                                                            oletusarvo: Option[BigDecimal]
                                                           ): (Option[BigDecimal], List[Tila], Historia) = {
    val laajuusKoskessa: Option[BigDecimal] = KoskiLaskenta.haeAmmatillisenYtonOsaAlueenLaajuus(
      ammatillisenTutkinnonValitsija(iteraatioParametrit, f),
      ammatillisenYtonOsaAlueenValitsija(iteraatioParametrit, f),
      laskin.hakemus,
      oletusarvo)

    val (tulos: Option[BigDecimal], tilalista: List[Tila]) = konvertoi(konvertteri, laajuusKoskessa, laskin.hakemus, laskin.hakukohde)

    val uusiHistoria = Historia(
      Funktionimi.HAEAMMATILLISENYTOOSAALUEENLAAJUUS.name(),
      tulos,
      tilalista,
      None,
      Some(Map(
        "oletusarvo" -> oletusarvo,
        "lähdearvo" -> laajuusKoskessa
      )))
    (tulos, tilalista, uusiHistoria)
  }

  protected def haeAmmatillisenTutkinnonYtoOsaAlueenArvosana(iteraatioParametrit: Map[Class[_ <: IteraatioParametri], IteraatioParametri],
                                                             f: HaeAmmatillisenTutkinnonYtoOsaAlueenArvosana,
                                                             konvertteri: Option[Konvertteri[BigDecimal, BigDecimal]],
                                                             oletusarvo: Option[BigDecimal]
                                                            ): (Option[BigDecimal], List[Tila], Historia) = {
    val arvosanaKoskessa: Option[BigDecimal] = KoskiLaskenta.haeAmmatillisenYtonOsaAlueenArvosana(
      ammatillisenTutkinnonValitsija(iteraatioParametrit, f),
      ammatillisenYtonOsaAlueenValitsija(iteraatioParametrit, f),
      laskin.hakemus,
      oletusarvo)

    val (tulos: Option[BigDecimal], tilalista: List[Tila]) = konvertoi(konvertteri, arvosanaKoskessa, laskin.hakemus, laskin.hakukohde)

    val uusiHistoria = Historia(
      Funktionimi.HAEAMMATILLISENYTOOSAALUEENARVOSANA.name(),
      tulos,
      tilalista,
      None,
      Some(Map(
        "lähdearvo" -> arvosanaKoskessa
      )))
    (tulos, tilalista, uusiHistoria)
  }


  protected def haeAmmatillisenTutkinnonKeskiarvo(iteraatioParametrit: Map[Class[_ <: IteraatioParametri], IteraatioParametri],
                                                  f: HaeAmmatillisenTutkinnonKeskiarvo,
                                                  konvertteri: Option[Konvertteri[BigDecimal, BigDecimal]]
                                                 ): (Option[BigDecimal], List[Tila], Historia) = {
    val keskiarvoKoskessa: Option[BigDecimal] = KoskiLaskenta.haeAmmatillisenTutkinnonKoskeenTallennettuKeskiarvo(ammatillisenTutkinnonValitsija(iteraatioParametrit, f), laskin.hakemus)

    val (tulos: Option[BigDecimal], tilalista: List[Tila]) = konvertoi(konvertteri, keskiarvoKoskessa, laskin.hakemus, laskin.hakukohde)

    val uusiHistoria = Historia(
      Funktionimi.HAEAMMATILLISENTUTKINNONKESKIARVO.name(),
      tulos,
      tilalista,
      None,
      Some(Map(
        "lähdearvo" -> keskiarvoKoskessa
      )))
    (tulos, tilalista, uusiHistoria)
  }


  protected def haeAmmatillisenTutkinnonSuoritustapa(iteraatioParametrit: Map[Class[_ <: IteraatioParametri], IteraatioParametri],
                                                     f: HaeAmmatillisenTutkinnonSuoritustapa,
                                                     konvertteri: Konvertteri[String, BigDecimal],
                                                     oletusarvo: Option[BigDecimal]
                                                    ): (Option[BigDecimal], List[Tila], Historia) = {
    val suoritustapaKoskessa: Option[String] = KoskiLaskenta.haeAmmatillisenTutkinnonSuoritustapa(ammatillisenTutkinnonValitsija(iteraatioParametrit, f), laskin.hakemus)

    val (tulos: Option[BigDecimal], tilalista: List[Tila]) = konvertoiMerkkijono(konvertteri, oletusarvo, suoritustapaKoskessa)

    val uusiHistoria = Historia(
      Funktionimi.HAEAMMATILLISENTUTKINNONSUORITUSTAPA.name(),
      tulos,
      tilalista,
      None,
      Some(Map(
        "oletusarvo" -> oletusarvo,
        "asteikon koodi" -> suoritustapaKoskessa
      )))
    (tulos, tilalista, uusiHistoria)
  }

  private def konvertoiMerkkijono(konvertteri: Konvertteri[String, BigDecimal],
                                  oletusarvo: Option[BigDecimal],
                                  lahdearvo: Option[String]
                                 ): (Option[BigDecimal], List[Tila]) = {
    val (konv: Option[Arvokonvertteri[String, BigDecimal]], tilatKonvertterinHausta) = konvertteri match {
      case a: Arvokonvertteri[_, _] => konversioToArvokonversio[String, BigDecimal](a.konversioMap, laskin.hakemus.kentat, laskin.hakukohde)
      case x => throw new IllegalArgumentException(s"Odotettiin arvokonvertteria mutta saatiin $x")
    }

    val (tulos, tilaKonvertoinnista): (Option[BigDecimal], Tila) = (for {
      k <- konv
      tulosArvo <- lahdearvo
    } yield k.konvertoi(tulosArvo)).getOrElse((oletusarvo, new Hyvaksyttavissatila))
    val tilalista: List[Tila] = tilaKonvertoinnista :: tilatKonvertterinHausta
    (tulos, tilalista)
  }
}
