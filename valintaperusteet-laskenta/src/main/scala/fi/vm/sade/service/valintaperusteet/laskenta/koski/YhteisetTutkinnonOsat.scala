package fi.vm.sade.service.valintaperusteet.laskenta.koski

import java.time.LocalDate

import fi.vm.sade.service.valintaperusteet.laskenta.AmmatillisenPerustutkinnonValitsija
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta
import fi.vm.sade.service.valintaperusteet.laskenta.api.Hakemus
import fi.vm.sade.service.valintaperusteet.laskenta.koski.KoskiLaskenta.ammatillisenHhuomioitavatKoulutustyypit
import fi.vm.sade.service.valintaperusteet.laskenta.koski.KoskiLaskenta.ammatillisenHuomioitavaOpiskeluoikeudenTyyppi
import fi.vm.sade.service.valintaperusteet.laskenta.koski.KoskiLaskenta.ammatillisenSuorituksenTyyppi
import fi.vm.sade.service.valintaperusteet.laskenta.koski.Osasuoritus.OsaSuoritusLinssit
import fi.vm.sade.service.valintaperusteet.laskenta.koski.Tutkinnot.etsiValmiitTutkinnot
import io.circe.Json
import io.circe.optics.JsonPath
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object YhteisetTutkinnonOsat {
  private val LOG: Logger = LoggerFactory.getLogger(YhteisetTutkinnonOsat.getClass)

  def haeYtoArviointiasteikko(ammatillisenPerustutkinnonValitsija: AmmatillisenPerustutkinnonValitsija,
                              hakemus: Hakemus,
                              valintaperusteviite: Laskenta.Valintaperuste
                             ): Option[String] = {
    haeTietoYhteisestäTutkinnonosasta(
      ammatillisenPerustutkinnonValitsija,
      hakemus,
      valintaperusteviite,
      o => Some(o.uusinArviointiasteikko),
      None
    )
  }

  def haeYtoArvosana(ammatillisenPerustutkinnonValitsija: AmmatillisenPerustutkinnonValitsija,
                     hakemus: Hakemus,
                     valintaperusteviite: Laskenta.Valintaperuste,
                     oletusarvo: Option[BigDecimal]
                    ): Option[BigDecimal] = {
    haeTietoYhteisestäTutkinnonosasta[BigDecimal](
      ammatillisenPerustutkinnonValitsija,
      hakemus,
      valintaperusteviite,
      o => o.uusinHyvaksyttyArvio.map(BigDecimal(_)),
      oletusarvo
    )
  }

  def haeYtoOsaAlueet(ammatillisenPerustutkinnonValitsija: AmmatillisenPerustutkinnonValitsija,
                      hakemus: Hakemus,
                      ytoKoodiArvo: String): Seq[Json] = {
    if (hakemus.koskiOpiskeluoikeudet != null) {
      val oikeaOpiskeluoikeus: Json = etsiValmiitTutkinnot(
        ammatillisenPerustutkinnonValitsija.valmistumisenTakarajaPvm,
        hakemus.koskiOpiskeluoikeudet,
        opiskeluoikeudenHaluttuTyyppi = ammatillisenHuomioitavaOpiskeluoikeudenTyyppi,
        suorituksenHaluttuTyyppi = ammatillisenSuorituksenTyyppi,
        hakemus = hakemus)(ammatillisenPerustutkinnonValitsija.tutkinnonIndeksi)
      YhteisetTutkinnonOsat.haeYhteisenTutkinnonOsanTiedot(ammatillisenPerustutkinnonValitsija.valmistumisenTakarajaPvm, hakemus, oikeaOpiskeluoikeus, ytoKoodiArvo)
        .filter(osaAlue => {
          !JsonPath.root.tyyppi.koodiarvo.json.getOption(osaAlue).contains("ammatillisentutkinnonosanosaalue")
        })
        .flatMap(OsaSuoritusLinssit.osasuoritukset.getAll)
    } else {
      Seq[Json]()
    }
  }

  private def haeTietoYhteisestäTutkinnonosasta[T](ammatillisenPerustutkinnonValitsija: AmmatillisenPerustutkinnonValitsija,
                                                   hakemus: Hakemus,
                                                   valintaperusteviite: Laskenta.Valintaperuste,
                                                   tiedonHakija: Osasuoritus => Option[T],
                                                   oletusarvo: Option[T]
                                                  ): Option[T] = {
    if (hakemus.koskiOpiskeluoikeudet != null) {
      haeYhteisenTutkinnonOsanTiedotValitsijalla(hakemus, ammatillisenPerustutkinnonValitsija, valintaperusteviite.tunniste).map(Osasuoritus(_)) match {
        case Nil => None
        case o :: Nil => tiedonHakija.apply(o)
        case xs => throw new IllegalArgumentException(s"Piti löytyä vain yksi koodin ${valintaperusteviite.tunniste} yto " +
          s"valitsijalla $ammatillisenPerustutkinnonValitsija , mutta löytyi ${xs.size} : $xs")
      }
    } else {
      oletusarvo
    }
  }

  private def haeYhteisenTutkinnonOsanTiedotValitsijalla(hakemus: Hakemus,
                                             ammatillisenPerustutkinnonValitsija: AmmatillisenPerustutkinnonValitsija,
                                             ytoKoodiArvo: String
                                            ): Seq[Json] = {
    val oikeaOpiskeluoikeus: Json = etsiValmiitTutkinnot(
      ammatillisenPerustutkinnonValitsija.valmistumisenTakarajaPvm,
      hakemus.koskiOpiskeluoikeudet,
      opiskeluoikeudenHaluttuTyyppi = ammatillisenHuomioitavaOpiskeluoikeudenTyyppi,
      suorituksenHaluttuTyyppi = ammatillisenSuorituksenTyyppi,
      hakemus = hakemus)(ammatillisenPerustutkinnonValitsija.tutkinnonIndeksi)
    YhteisetTutkinnonOsat.haeYhteisenTutkinnonOsanTiedot(
      ammatillisenPerustutkinnonValitsija.valmistumisenTakarajaPvm,
      hakemus,
      oikeaOpiskeluoikeus,
      ytoKoodiArvo)
  }

  private def haeYhteisenTutkinnonOsanTiedot(valmistumisenTakarajaPvm: LocalDate, hakemus: Hakemus, opiskeluoikeus: Json, ytoKoodiArvo: String): Seq[Json] = {
    val hakemusOid = hakemus.oid
    try {
      val suoritukset = etsiAmmatillistenTutkintojenSuoritukset(valmistumisenTakarajaPvm, opiskeluoikeus, hakemus)
      val ytoJsonit: Seq[Json] = suoritukset
        .flatMap(suoritus => etsiYhteisetTutkinnonOsat(suoritus, Set(ytoKoodiArvo)))

      if (ytoJsonit.size > 1) {
        throw new UnsupportedOperationException(
          s"Hakemuksen $hakemusOid hakijan opiskeluoikeudelle löytyi useampi kuin yksi osasuoritus" +
            s" yhteiselle tutkinnon osalle '$ytoKoodiArvo': $ytoJsonit")
      }

      ytoJsonit
    } catch {
      case e: Exception => {
        LOG.error(s"Virhe haettaessa ammatillisen perustutkinnon yhteisen tutkinnon osan tietoja hakemukselle $hakemusOid", e)
        throw e
      }
    }
  }

  private def etsiYhteisetTutkinnonOsat(suoritus: Json, osasuorituksenSallitutKoodit: Set[String]) = {
    OsaSuoritukset.etsiOsasuoritukset(suoritus, osasuoritus => {
      osasuorituksenSallitutKoodit.contains(OsaSuoritusLinssit.koulutusmoduulinTunnisteenKoodiarvo.getOption(osasuoritus).orNull)
    })
  }

  private def etsiAmmatillistenTutkintojenSuoritukset(valmistumisenTakarajaPvm: LocalDate, opiskeluoikeus: Json, hakemus: Hakemus) = {
    val suorituksenSallitutKoodit: Set[Int] = ammatillisenHhuomioitavatKoulutustyypit.map(_.koodiarvo)

    Tutkinnot.etsiValmiitTutkinnot(valmistumisenTakarajaPvm, Json.arr(opiskeluoikeus), KoskiLaskenta.ammatillisenHuomioitavaOpiskeluoikeudenTyyppi, KoskiLaskenta.ammatillisenSuorituksenTyyppi, hakemus)
      .flatMap(tutkinto => Tutkinnot.etsiValiditSuoritukset(tutkinto, valmistumisenTakarajaPvm, suorituksenSallitutKoodit))
  }
}
