package fi.vm.sade.service.valintaperusteet.laskenta.koski

import fi.vm.sade.service.valintaperusteet.laskenta.AmmatillisenPerustutkinnonValitsija
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta
import fi.vm.sade.service.valintaperusteet.laskenta.api.Hakemus
import fi.vm.sade.service.valintaperusteet.laskenta.koski.KoskiLaskenta.ammatillisenHhuomioitavatKoulutustyypit
import fi.vm.sade.service.valintaperusteet.laskenta.koski.KoskiLaskenta.ammatillisenHuomioitavaOpiskeluoikeudenTyyppi
import fi.vm.sade.service.valintaperusteet.laskenta.koski.KoskiLaskenta.ammatillisenSuorituksenTyyppi
import fi.vm.sade.service.valintaperusteet.laskenta.koski.KoskiLaskenta.etsiValmiitTutkinnot
import fi.vm.sade.service.valintaperusteet.laskenta.koski.KoskiLaskenta.sulkeutumisPaivamaara
import io.circe.Json
import org.joda.time.DateTime
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object YhteisetTutkinnonOsat {
  private val LOG: Logger = LoggerFactory.getLogger(YhteisetTutkinnonOsat.getClass)

  def haeYtoArviointiasteikko(ammatillisenPerustutkinnonValitsija: AmmatillisenPerustutkinnonValitsija,
                              hakemus: Hakemus,
                              valintaperusteviite: Laskenta.Valintaperuste
                             ): Option[String] = {
    if (hakemus.koskiOpiskeluoikeudet != null) {
      haeYhteisenTutkinnonOsanTiedot(hakemus, ammatillisenPerustutkinnonValitsija, valintaperusteviite.tunniste) match {
        case Nil => None
        case o :: Nil => Some(o.uusinArviointiasteikko)
        case xs => throw new IllegalArgumentException(s"Piti löytyä vain yksi suoritus valitsijalla $ammatillisenPerustutkinnonValitsija , mutta löytyi ${xs.size} : $xs")
      }
    } else {
      None
    }
  }

  def haeYtoArvosana(ammatillisenPerustutkinnonValitsija: AmmatillisenPerustutkinnonValitsija,
                     hakemus: Hakemus,
                     valintaperusteviite: Laskenta.Valintaperuste,
                     oletusarvo: Option[BigDecimal]
                    ): Option[BigDecimal] = {
    if (hakemus.koskiOpiskeluoikeudet != null) {
      haeYhteisenTutkinnonOsanTiedot(hakemus, ammatillisenPerustutkinnonValitsija, valintaperusteviite.tunniste) match {
        case Nil => None
        case o :: Nil => o.uusinHyvaksyttyArvio.map(BigDecimal(_))
        case xs => throw new IllegalArgumentException(s"Piti löytyä vain yksi suoritus valitsijalla $ammatillisenPerustutkinnonValitsija , mutta löytyi ${xs.size} : $xs")
      }
    } else {
      oletusarvo
    }
  }

  private def haeYhteisenTutkinnonOsanTiedot(hakemus: Hakemus,
                                             ammatillisenPerustutkinnonValitsija: AmmatillisenPerustutkinnonValitsija,
                                             ytoKoodiArvo: String
                                            ): Seq[Osasuoritus] = {
    val oikeaOpiskeluoikeus: Json = etsiValmiitTutkinnot(
      hakemus.koskiOpiskeluoikeudet,
      opiskeluoikeudenHaluttuTyyppi = ammatillisenHuomioitavaOpiskeluoikeudenTyyppi,
      suorituksenHaluttuTyyppi = ammatillisenSuorituksenTyyppi,
      hakemus = hakemus)(ammatillisenPerustutkinnonValitsija.tutkinnonIndeksi)
    val ammatillisenSuorituksenTiedot = YhteisetTutkinnonOsat.haeYhteisenTutkinnonOsanTiedot(
      hakemus,
      oikeaOpiskeluoikeus,
      ytoKoodiArvo)
    ammatillisenSuorituksenTiedot
  }

  private def haeYhteisenTutkinnonOsanTiedot(hakemus: Hakemus, opiskeluoikeus: Json, ytoKoodiArvo: String): Seq[Osasuoritus] = {
    val hakemusOid = hakemus.oid
    try {
      val suoritukset = etsiAmmatillistenTutkintojenSuoritukset(opiskeluoikeus, hakemus)
      val yhteistenTutkinnonOsienArvosanat: Seq[Osasuoritus] = suoritukset
        .flatMap(suoritus => etsiYhteisetTutkinnonOsat(suoritus, sulkeutumisPaivamaara, Set(ytoKoodiArvo)))

      if (yhteistenTutkinnonOsienArvosanat.size > 1) {
        throw new UnsupportedOperationException(
          s"Hakemukselle $hakemusOid löytyi useampi kuin yksi arvosana yhteiselle tutkinnon osalle '$ytoKoodiArvo': $yhteistenTutkinnonOsienArvosanat")
      }

      if (yhteistenTutkinnonOsienArvosanat.nonEmpty) {
        LOG.debug(s"Hakemukselle $hakemusOid löytyi yhteiselle tutkinnon osalle $ytoKoodiArvo arvosana $yhteistenTutkinnonOsienArvosanat")
      }
      yhteistenTutkinnonOsienArvosanat
    } catch {
      case e: Exception => {
        LOG.error(s"Virhe haettaessa ammatillisen suorituksen tietoja hakemukselle $hakemusOid", e)
        throw e
      }
    }
  }

  private def etsiYhteisetTutkinnonOsat(suoritus: Json, sulkeutumisPäivämäärä: DateTime, osasuorituksenSallitutKoodit: Set[String]): List[Osasuoritus] = {
    KoskiLaskenta.etsiOsasuoritukset(suoritus, sulkeutumisPäivämäärä, osasuoritus => {
      osasuorituksenSallitutKoodit.contains(KoskiLaskenta._osasuorituksenKoulutusmoduulinTunnisteenKoodiarvo.getOption(osasuoritus).orNull)
    })
  }

  private def etsiAmmatillistenTutkintojenSuoritukset(opiskeluoikeus: Json, hakemus: Hakemus) = {
    val suorituksenSallitutKoodit: Set[Int] = ammatillisenHhuomioitavatKoulutustyypit.map(_.koodiarvo)

    KoskiLaskenta.etsiValmiitTutkinnot(Json.arr(opiskeluoikeus), KoskiLaskenta.ammatillisenHuomioitavaOpiskeluoikeudenTyyppi, KoskiLaskenta.ammatillisenSuorituksenTyyppi, hakemus)
      .flatMap(tutkinto => KoskiLaskenta.etsiValiditSuoritukset(tutkinto, KoskiLaskenta.sulkeutumisPaivamaara, suorituksenSallitutKoodit))
  }
}
