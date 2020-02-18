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
    val tutkinnonValitsija: AmmatillisenPerustutkinnonValitsija = ammatillisenTutkinnonValitsija(iteraatioParametrit, f)
    val arvosanaKoskessa: Option[BigDecimal] = YhteisetTutkinnonOsat.haeYtoArvosana(tutkinnonValitsija, laskin.hakemus, valintaperusteviite, oletusarvo)

    val (tulos, tilalista) = konvertoi(konvertteri, arvosanaKoskessa, laskin.hakemus, laskin.hakukohde)

    val uusiHistoria = Historia(
      HAEAMMATILLINENYTOARVOSANA,
      tulos,
      tilalista,
      None,
      Some(Map(
        s"""Tutkinnon "${tutkinnonValitsija.lyhytKuvaus}" YTO:n ${valintaperusteviite.tunniste} arvosanan lähdearvo""" -> arvosanaKoskessa,
        s"""Tutkinnon "${tutkinnonValitsija.lyhytKuvaus}" YTO:n ${valintaperusteviite.tunniste} arvosanan tuottamat pisteet""" -> tulos
      )))
    (tulos, tilalista, uusiHistoria)
  }


  protected def haeAmmatillinenYtoArviointiAsteikko(iteraatioParametrit: Map[Class[_ <: IteraatioParametri], IteraatioParametri],
                                                    f: HaeAmmatillinenYtoArviointiAsteikko,
                                                    konvertteri: Konvertteri[String, BigDecimal],
                                                    oletusarvo: Option[BigDecimal],
                                                    valintaperusteviite: Valintaperuste
                                                   ): (Option[BigDecimal], List[Tila], Historia) = {
    val tutkinnonValitsija: AmmatillisenPerustutkinnonValitsija = ammatillisenTutkinnonValitsija(iteraatioParametrit, f)
    val asteikonKoodiKoskessa: Option[String] = YhteisetTutkinnonOsat.haeYtoArviointiasteikko(tutkinnonValitsija, laskin.hakemus, valintaperusteviite)
    val (tulos: Option[BigDecimal], tilalista: List[Tila]) = konvertoiMerkkijono(konvertteri, oletusarvo, asteikonKoodiKoskessa)

    val uusiHistoria = Historia(
      HAEAMMATILLINENYTOARVIOINTIASTEIKKO,
      tulos,
      tilalista,
      None,
      Some(Map(
        s"""Tutkinnon "${tutkinnonValitsija.lyhytKuvaus}" YTO:n ${valintaperusteviite.tunniste} arviointiasteikon koodi""" -> asteikonKoodiKoskessa
      )))
    (tulos, tilalista, uusiHistoria)
  }

  protected def haeAmmatillisenTutkinnonOsanLaajuus(iteraatioParametrit: Map[Class[_ <: IteraatioParametri], IteraatioParametri],
                                                    f: HaeAmmatillisenTutkinnonOsanLaajuus,
                                                    konvertteri: Option[Konvertteri[BigDecimal, BigDecimal]],
                                                    oletusarvo: Option[BigDecimal]
                                                   ): (Option[BigDecimal], List[Tila], Historia) = {
    val tutkinnonValitsija: AmmatillisenPerustutkinnonValitsija = ammatillisenTutkinnonValitsija(iteraatioParametrit, f)
    val tutkinnonOsanValitsija = ammatillisenTutkinnonOsanValitsija(iteraatioParametrit, f)
    val laajuusKoskessa: Option[BigDecimal] = KoskiLaskenta.haeAmmatillisenTutkinnonOsanLaajuus(
      tutkinnonValitsija,
      tutkinnonOsanValitsija,
      laskin.hakemus,
      oletusarvo)

    val (tulos: Option[BigDecimal], tilalista: List[Tila]) = konvertoi(konvertteri, laajuusKoskessa, laskin.hakemus, laskin.hakukohde)

    val uusiHistoria = Historia(
      Funktionimi.HAEAMMATILLISENOSANLAAJUUS,
      tulos,
      tilalista,
      None,
      Some(Map(
        s"""Ammatillisen perustutkinnon "${tutkinnonValitsija.lyhytKuvaus}" osan "${tutkinnonOsanValitsija.lyhytKuvaus}" laajuus""" -> laajuusKoskessa
      )))
    (tulos, tilalista, uusiHistoria)
  }

  protected def haeAmmatillisenTutkinnonOsanArvosana(iteraatioParametrit: Map[Class[_ <: IteraatioParametri], IteraatioParametri],
                                                     f: HaeAmmatillisenTutkinnonOsanArvosana,
                                                     konvertteri: Option[Konvertteri[BigDecimal, BigDecimal]]
                                                    ): (Option[BigDecimal], List[Tila], Historia) = {
    val tutkinnonValitsija = ammatillisenTutkinnonValitsija(iteraatioParametrit, f)
    val tutkinnonOsanValitsija = ammatillisenTutkinnonOsanValitsija(iteraatioParametrit, f)
    val arvosanaKoskessa: Option[BigDecimal] = KoskiLaskenta.haeAmmatillisenTutkinnonOsanArvosana(
      tutkinnonValitsija,
      tutkinnonOsanValitsija,
      laskin.hakemus)

    val (tulos: Option[BigDecimal], tilalista: List[Tila]) = konvertoi(konvertteri, arvosanaKoskessa, laskin.hakemus, laskin.hakukohde)

    val uusiHistoria = Historia(
      Funktionimi.HAEAMMATILLISENOSANARVOSANA,
      tulos,
      tilalista,
      None,
      Some(Map(
        s"""Ammatillisen perustutkinnon "${tutkinnonValitsija.lyhytKuvaus}" osan "${tutkinnonOsanValitsija.lyhytKuvaus}" arvosana""" -> arvosanaKoskessa,
      )))
    (tulos, tilalista, uusiHistoria)
  }

  protected def haeAmmatillisenTutkinnonYtoOsaAlueenLaajuus(iteraatioParametrit: Map[Class[_ <: IteraatioParametri], IteraatioParametri],
                                                            f: HaeAmmatillisenTutkinnonYtoOsaAlueenLaajuus,
                                                            konvertteri: Option[Konvertteri[BigDecimal, BigDecimal]],
                                                            oletusarvo: Option[BigDecimal]
                                                           ): (Option[BigDecimal], List[Tila], Historia) = {
    val tutkinnonValitsija = ammatillisenTutkinnonValitsija(iteraatioParametrit, f)
    val ytoOsaAlueenValitsija = ammatillisenYtonOsaAlueenValitsija(iteraatioParametrit, f)
    val laajuusKoskessa: Option[BigDecimal] = KoskiLaskenta.haeAmmatillisenYtonOsaAlueenLaajuus(
      tutkinnonValitsija,
      ytoOsaAlueenValitsija,
      laskin.hakemus,
      oletusarvo)

    val (tulos: Option[BigDecimal], tilalista: List[Tila]) = konvertoi(konvertteri, laajuusKoskessa, laskin.hakemus, laskin.hakukohde)

    val uusiHistoria = Historia(
      Funktionimi.HAEAMMATILLISENYTOOSAALUEENLAAJUUS,
      tulos,
      tilalista,
      None,
      Some(Map(
        s"""Ammatillisen perustutkinnon "${tutkinnonValitsija.lyhytKuvaus}" osan "${ytoOsaAlueenValitsija.lyhytKuvaus}" laajuus""" -> laajuusKoskessa,
      )))
    (tulos, tilalista, uusiHistoria)
  }

  protected def haeAmmatillisenTutkinnonYtoOsaAlueenArvosana(iteraatioParametrit: Map[Class[_ <: IteraatioParametri], IteraatioParametri],
                                                             f: HaeAmmatillisenTutkinnonYtoOsaAlueenArvosana,
                                                             konvertteri: Option[Konvertteri[BigDecimal, BigDecimal]],
                                                             oletusarvo: Option[BigDecimal]
                                                            ): (Option[BigDecimal], List[Tila], Historia) = {
    val tutkinnonValitsija = ammatillisenTutkinnonValitsija(iteraatioParametrit, f)
    val ytoOsaAlueenValitsija = ammatillisenYtonOsaAlueenValitsija(iteraatioParametrit, f)
    val arvosanaKoskessa: Option[BigDecimal] = KoskiLaskenta.haeAmmatillisenYtonOsaAlueenArvosana(
      tutkinnonValitsija,
      ytoOsaAlueenValitsija,
      laskin.hakemus,
      oletusarvo)

    val (tulos: Option[BigDecimal], tilalista: List[Tila]) = konvertoi(konvertteri, arvosanaKoskessa, laskin.hakemus, laskin.hakukohde)

    val uusiHistoria = Historia(
      Funktionimi.HAEAMMATILLISENYTOOSAALUEENARVOSANA,
      tulos,
      tilalista,
      None,
      Some(Map(
        s"""Ammatillisen perustutkinnon "${tutkinnonValitsija.lyhytKuvaus}" osan "${ytoOsaAlueenValitsija.lyhytKuvaus}" arvosana""" -> arvosanaKoskessa,
      )))
    (tulos, tilalista, uusiHistoria)
  }


  protected def haeAmmatillisenTutkinnonKeskiarvo(iteraatioParametrit: Map[Class[_ <: IteraatioParametri], IteraatioParametri],
                                                  f: HaeAmmatillisenTutkinnonKeskiarvo,
                                                  konvertteri: Option[Konvertteri[BigDecimal, BigDecimal]]
                                                 ): (Option[BigDecimal], List[Tila], Historia) = {
    val tutkinnonValitsija = ammatillisenTutkinnonValitsija(iteraatioParametrit, f)
    val keskiarvoKoskessa: Option[BigDecimal] = KoskiLaskenta.haeAmmatillisenTutkinnonKoskeenTallennettuKeskiarvo(tutkinnonValitsija, laskin.hakemus)

    val (tulos: Option[BigDecimal], tilalista: List[Tila]) = konvertoi(konvertteri, keskiarvoKoskessa, laskin.hakemus, laskin.hakukohde)

    val uusiHistoria = Historia(
      Funktionimi.HAEAMMATILLISENTUTKINNONKESKIARVO,
      tulos,
      tilalista,
      None,
      Some(Map(
        s"""Ammatillisen perustutkinnon "${tutkinnonValitsija.lyhytKuvaus}" tallennettu keskiarvo""" -> keskiarvoKoskessa,
      )))
    (tulos, tilalista, uusiHistoria)
  }


  protected def haeAmmatillisenTutkinnonSuoritustapa(iteraatioParametrit: Map[Class[_ <: IteraatioParametri], IteraatioParametri],
                                                     f: HaeAmmatillisenTutkinnonSuoritustapa,
                                                     konvertteri: Konvertteri[String, BigDecimal],
                                                     oletusarvo: Option[BigDecimal]
                                                    ): (Option[BigDecimal], List[Tila], Historia) = {
    val perustutkinnonValitsija = ammatillisenTutkinnonValitsija(iteraatioParametrit, f)
    val suoritustapaKoskessa: Option[String] = KoskiLaskenta.haeAmmatillisenTutkinnonSuoritustapa(perustutkinnonValitsija, laskin.hakemus)

    val (tulos: Option[BigDecimal], tilalista: List[Tila]) = konvertoiMerkkijono(konvertteri, oletusarvo, suoritustapaKoskessa)

    val uusiHistoria = Historia(
      Funktionimi.HAEAMMATILLISENTUTKINNONSUORITUSTAPA,
      tulos,
      tilalista,
      None,
      Some(Map(
        s"Ammatillinen perustutkinto ${perustutkinnonValitsija.lyhytKuvaus}" -> Some(perustutkinnonValitsija.kuvaus),
        s"${perustutkinnonValitsija.lyhytKuvaus} – suoritustavan oletusarvo" -> oletusarvo,
        s"${perustutkinnonValitsija.lyhytKuvaus} – suoritustavan asteikon koodi" -> suoritustapaKoskessa,
        s"${perustutkinnonValitsija.lyhytKuvaus} – suoritustavan tulos" -> tulos
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

    koostaKonvertoituTulos[String](lahdearvo, oletusarvo, konv, tilatKonvertterinHausta)
  }
}
