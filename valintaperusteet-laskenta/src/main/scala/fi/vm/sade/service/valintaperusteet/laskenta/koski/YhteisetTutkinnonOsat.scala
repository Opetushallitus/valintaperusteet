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
      haeAmmatillisenSuorituksenTiedot(hakemus, ammatillisenPerustutkinnonValitsija, valintaperusteviite.tunniste) match {
        case Nil => None
        case (osasuorituksenKoodiarvo, osasuorituksenNimiFi, osasuorituksenArvio, osasuorituksenArviointiAsteikko) :: Nil => Some(osasuorituksenArviointiAsteikko)
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
      haeYtoArvosana(ammatillisenPerustutkinnonValitsija, hakemus, valintaperusteviite.tunniste)
    } else {
      oletusarvo
    }
  }

  private def haeAmmatillisenSuorituksenTiedot(hakemus: Hakemus,
                                               ammatillisenPerustutkinnonValitsija: AmmatillisenPerustutkinnonValitsija,
                                               ytoKoodiArvo: String
                                              ): Seq[(String, String, Option[Int], String)] = {
    val oikeaOpiskeluoikeus: Json = etsiValmiitTutkinnot(
      hakemus.koskiOpiskeluoikeudet,
      opiskeluoikeudenHaluttuTyyppi = ammatillisenHuomioitavaOpiskeluoikeudenTyyppi,
      suorituksenHaluttuTyyppi = ammatillisenSuorituksenTyyppi,
      hakemus = hakemus)(ammatillisenPerustutkinnonValitsija.tutkinnonIndeksi)
    val ammatillisenSuorituksenTiedot = YhteisetTutkinnonOsat.haeAmmatillisenSuorituksenTiedot(
      hakemus,
      oikeaOpiskeluoikeus,
      ytoKoodiArvo)
    ammatillisenSuorituksenTiedot
  }

  private def etsiAmmatillistenTutkintojenSuoritukset(opiskeluoikeus: Json, hakemus: Hakemus) = {
    val suorituksenSallitutKoodit: Set[Int] = ammatillisenHhuomioitavatKoulutustyypit.map(_.koodiarvo)

    KoskiLaskenta.etsiValmiitTutkinnot(Json.arr(opiskeluoikeus), KoskiLaskenta.ammatillisenHuomioitavaOpiskeluoikeudenTyyppi, KoskiLaskenta.ammatillisenSuorituksenTyyppi, hakemus)
      .flatMap(tutkinto => KoskiLaskenta.etsiValiditSuoritukset(tutkinto, KoskiLaskenta.sulkeutumisPaivamaara, suorituksenSallitutKoodit))
  }

  private def etsiYhteisetTutkinnonOsat(suoritus: Json, sulkeutumisPäivämäärä: DateTime, osasuorituksenSallitutKoodit: Set[String]): List[(String, String, Option[Int], String)] = {
    KoskiLaskenta.etsiOsasuoritukset(suoritus, sulkeutumisPäivämäärä, osasuoritus => {
      osasuorituksenSallitutKoodit.contains(KoskiLaskenta._osasuorituksenKoulutusmoduulinTunnisteenKoodiarvo.getOption(osasuoritus).orNull)
    }).map(x => (x._1, x._2, x._3, x._4))
  }

  private def haeYtoArvosana(ammatillisenPerustutkinnonValitsija: AmmatillisenPerustutkinnonValitsija, hakemus: Hakemus, ytoKoodiArvo: String) = {

    haeAmmatillisenSuorituksenTiedot(hakemus, ammatillisenPerustutkinnonValitsija, ytoKoodiArvo) match {
      case Nil => None
      case (_, _, osasuorituksenArvio, _) :: Nil => osasuorituksenArvio match {
        case Some(x) => Some(BigDecimal(x))
        case None => None
      }
      case xs => throw new IllegalArgumentException(s"Piti löytyä vain yksi suoritus valitsijalla $ammatillisenPerustutkinnonValitsija , mutta löytyi ${xs.size} : $xs")
    }
  }

  private def haeAmmatillisenSuorituksenTiedot(hakemus: Hakemus, opiskeluoikeus: Json, ytoKoodiArvo: String): Seq[(String, String, Option[Int], String)] = {
    val hakemusOid = hakemus.oid
    try {
      val suoritukset = etsiAmmatillistenTutkintojenSuoritukset(opiskeluoikeus, hakemus)
      val yhteistenTutkinnonOsienArvosanat = suoritukset
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
}
