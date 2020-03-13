package fi.vm.sade.service.valintaperusteet.laskenta.koski

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import fi.vm.sade.kaava.LaskentaUtil
import fi.vm.sade.service.valintaperusteet.laskenta.AmmatillisenPerustutkinnonValitsija
import fi.vm.sade.service.valintaperusteet.laskenta.AmmatillisenTutkinnonOsanValitsija
import fi.vm.sade.service.valintaperusteet.laskenta.AmmatillisenTutkinnonYtoOsaAlueenValitsija
import fi.vm.sade.service.valintaperusteet.laskenta.api.Hakemus
import fi.vm.sade.service.valintaperusteet.laskenta.koski.Tutkinnot.TutkintoLinssit
import io.circe.Json
import io.circe.optics.JsonPath
import monocle.Optional
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import scala.util.control.Exception._

object KoskiLaskenta {
  private val LOG: Logger = LoggerFactory.getLogger(KoskiLaskenta.getClass)
  private val opiskeluoikeudenAikaleimaFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")

  val ammatillisenHuomioitavaOpiskeluoikeudenTyyppi: String = "ammatillinenkoulutus"
  val ammatillisenSuorituksenTyyppi: String = "ammatillinentutkinto"

  val ammatillisenHhuomioitavatKoulutustyypit: Set[AmmatillisenPerustutkinnonKoulutustyyppi] =
    Set(AmmatillinenPerustutkinto, AmmatillinenReforminMukainenPerustutkinto, AmmatillinenPerustutkintoErityisopetuksena)

  // Osasuorituksen rakennetta purkavat linssit
  private val _osasuorituksenTyypinKoodiarvo = JsonPath.root.tyyppi.koodiarvo.string
  private val _osasuorituksenKoulutusmoduuli = JsonPath.root.koulutusmoduuli
  val _osasuorituksenKoulutusmoduulinTunnisteenKoodiarvo: Optional[Json, String] = _osasuorituksenKoulutusmoduuli.tunniste.koodiarvo.string

  def etsiAmmatillisetTutkinnot(hakemus: Hakemus, datanAikaleimanLeikkuri: LocalDate, valmistumisenTakaraja: LocalDate): Seq[Tutkinto] = {
    if (hakemus.koskiOpiskeluoikeudet == null) {
      Nil
    } else {
      val tutkintoJsonitHuomioimattaValmistumisenTakarajaa = Tutkinnot.etsiValmiitTutkinnot(
        valmistumisenTakaraja = None,
        json = hakemus.koskiOpiskeluoikeudet,
        opiskeluoikeudenHaluttuTyyppi = ammatillisenHuomioitavaOpiskeluoikeudenTyyppi,
        suorituksenHaluttuTyyppi = ammatillisenSuorituksenTyyppi,
        hakemus = hakemus)
      tutkintoJsonitHuomioimattaValmistumisenTakarajaa.zipWithIndex.map { case (tutkintoJson, indeksi) =>
        val päätasonSuoritus = TutkintoLinssit.suoritukset.json.getAll(tutkintoJson).headOption
        val tutkinto = Tutkinto(
          indeksi,
          TutkintoLinssit.opiskeluoikeudenOid.getOption(tutkintoJson).getOrElse("-"),
          TutkintoLinssit.opiskeluoikeudenVersio.getOption(tutkintoJson).getOrElse(-1),
          TutkintoLinssit.opiskeluoikeudenAikaleima.getOption(tutkintoJson).getOrElse("-"),
          TutkintoLinssit.opiskeluoikeudenOppilaitoksenSuomenkielinenNimi.getOption(tutkintoJson).getOrElse("-"),
          päätasonSuoritus.flatMap(TutkintoLinssit.vahvistusPvm.getOption).getOrElse("-"),
          päätasonSuoritus.exists(Tutkinnot.vahvistettuRajapäiväänMennessä(valmistumisenTakaraja, _)))
        if (aikaleimaYlittaaLeikkuripaivan(datanAikaleimanLeikkuri, tutkinto)) {
          val message = s"Hakemuksen ${hakemus.oid} opiskeluoikeuden ${tutkinto.opiskeluoikeudenOid} " +
            s"version ${tutkinto.opiskeluoikeudenVersio} aikaleima ${tutkinto.opiskeluoikeudenAikaleima} " +
            s"on datan leikkuripäivän ${LaskentaUtil.suomalainenPvmMuoto.format(datanAikaleimanLeikkuri)} jälkeen. Sen ei olisi pitänyt tulla mukaan laskentaan."
          throw new IllegalArgumentException(message)
        }
        tutkinto
      }
    }
  }

  def etsiAmmatillisenTutkinnonOsat(tutkinnonValitsija: AmmatillisenPerustutkinnonValitsija, hakemus: Hakemus): Seq[Osasuoritus] = {
    haeAmmatillisenTutkinnonOsat(tutkinnonValitsija, hakemus)
  }

  def haeAmmatillisenTutkinnonOsanLaajuus(tutkinnonValitsija: AmmatillisenPerustutkinnonValitsija,
                                          osanValitsija: AmmatillisenTutkinnonOsanValitsija,
                                          hakemus: Hakemus,
                                          oletusarvo: Option[BigDecimal]
                                     ): Option[BigDecimal] = {
    val osasuoritus = haeAmmatillisenTutkinnonOsat(tutkinnonValitsija, hakemus)(osanValitsija.osanIndeksi)
    osasuoritus.uusinLaajuus.orElse {
      LOG.warn(s"Hakemuksen ${hakemus.oid} hakijan ammatillisen perustutkinnon ${tutkinnonValitsija.kuvaus} " +
        s"tutkinnon osan ${osanValitsija.kuvaus} (${osasuoritus.koulutusmoduulinNimiFi}) " +
        s"laajuus on tyhjä. Arvosana on ${osasuoritus.uusinHyvaksyttyArvio}")
      oletusarvo
    }
  }

  def haeAmmatillisenTutkinnonOsanArvosana(tutkinnonValitsija: AmmatillisenPerustutkinnonValitsija,
                                           osanValitsija: AmmatillisenTutkinnonOsanValitsija,
                                           hakemus: Hakemus
                                      ): Option[BigDecimal] = {
    haeAmmatillisenTutkinnonOsat(tutkinnonValitsija, hakemus)(osanValitsija.osanIndeksi).uusinHyvaksyttyArvio.map(BigDecimal(_))
  }

  def haeAmmatillisenYtonOsaAlueenLaajuus(tutkinnonValitsija: AmmatillisenPerustutkinnonValitsija,
                                          osaAlueenValitsija: AmmatillisenTutkinnonYtoOsaAlueenValitsija,
                                          hakemus: Hakemus,
                                          oletusarvo: Option[BigDecimal]
                                         ): Option[BigDecimal] = {
    val (_, uusinLaajuus, _) = haeOsaAlueenUusinArvosanaLaajuusJaArviointiAsteikkoValitsijoilla(tutkinnonValitsija, osaAlueenValitsija, hakemus)

    uusinLaajuus
  }

  def haeAmmatillisenYtonOsaAlueenArvosana(tutkinnonValitsija: AmmatillisenPerustutkinnonValitsija,
                                           osaAlueenValitsija: AmmatillisenTutkinnonYtoOsaAlueenValitsija,
                                           hakemus: Hakemus,
                                           oletusarvo: Option[BigDecimal]
                                          ): Option[BigDecimal] = {
    haeOsaAlueenUusinArvosanaLaajuusJaArviointiAsteikkoValitsijoilla(tutkinnonValitsija, osaAlueenValitsija, hakemus) match {
      case (uusinArvosana, _, _) => {
        catching(classOf[NumberFormatException]) opt BigDecimal(uusinArvosana)
      }
    }
  }

  def haeAmmatillisenTutkinnonKoskeenTallennettuKeskiarvo(tutkinnonValitsija: AmmatillisenPerustutkinnonValitsija, hakemus: Hakemus): Option[BigDecimal] = {
    haeArvoSuorituksista(
      tutkinnonValitsija,
      hakemus,
      suoritukset => suoritukset.headOption.flatMap(JsonPath.root.keskiarvo.bigDecimal.getOption(_)))
  }

  def haeAmmatillisenTutkinnonSuoritustapa(tutkinnonValitsija: AmmatillisenPerustutkinnonValitsija, hakemus: Hakemus): Option[String] = {
    val suoritustapa: Option[Json] = haeArvoSuorituksista(
      tutkinnonValitsija,
      hakemus,
      suoritukset => suoritukset.headOption.flatMap(JsonPath.root.suoritustapa.json.getOption(_)))

    suoritustapa.flatMap { s =>
      if (JsonPath.root.koodistoUri.string.getOption(s).contains("ammatillisentutkinnonsuoritustapa")) {
        JsonPath.root.koodiarvo.string.getOption(s)
      } else {
        None
      }
    }
  }

  private def aikaleimaYlittaaLeikkuripaivan(datanAikaleimanLeikkuri: LocalDate, tutkinto: Tutkinto): Boolean = {
    LocalDateTime.parse(tutkinto.opiskeluoikeudenAikaleima, opiskeluoikeudenAikaleimaFormat).isAfter(datanAikaleimanLeikkuri.plusDays(1).atStartOfDay())
  }

  private def haeOsaAlueenUusinArvosanaLaajuusJaArviointiAsteikkoValitsijoilla(tutkinnonValitsija: AmmatillisenPerustutkinnonValitsija,
                                                                      osaAlueenValitsija: AmmatillisenTutkinnonYtoOsaAlueenValitsija,
                                                                      hakemus: Hakemus): (String, Option[BigDecimal], String) = {
    val osaAlueet = YhteisetTutkinnonOsat.haeYtoOsaAlueet(tutkinnonValitsija, hakemus, osaAlueenValitsija.ytoKoodi)
    if (osaAlueenValitsija.osanIndeksi >= osaAlueet.size) {
      throw new IllegalStateException(s"Osa-alueen indeksointi yrittää käsitellä indeksiä ${osaAlueenValitsija.osanIndeksi} kun osa-alueita on vain ${osaAlueet.size} hakemuksella ${hakemus.oid}")
    }

    OsaSuoritukset.etsiUusinArvosanaLaajuusJaArviointiAsteikko(osaAlueet(osaAlueenValitsija.osanIndeksi))
  }

  private def haeArvoSuorituksista[T](tutkinnonValitsija: AmmatillisenPerustutkinnonValitsija,
                                   hakemus: Hakemus,
                                   arvonHakija: List[Json] => Option[T]
                                  ): Option[T] = {
    if (hakemus.koskiOpiskeluoikeudet == null) {
      None
    } else {
      val tutkinnot = Tutkinnot.etsiValmiitTutkinnot(Some(tutkinnonValitsija.valmistumisenTakarajaPvm), hakemus.koskiOpiskeluoikeudet, ammatillisenHuomioitavaOpiskeluoikeudenTyyppi, ammatillisenSuorituksenTyyppi, hakemus)
      val suorituksenSallitutKoodit: Set[Int] = ammatillisenHhuomioitavatKoulutustyypit.map(_.koodiarvo)
      val suoritukset = Tutkinnot.etsiValiditSuoritukset(tutkinnot(tutkinnonValitsija.tutkinnonIndeksi), tutkinnonValitsija.valmistumisenTakarajaPvm, suorituksenSallitutKoodit)
      if (suoritukset.size > 1) {
        throw new IllegalStateException(s"Odotettiin täsmälleen yhtä suoritusta hakemuksen ${hakemus.oid} " +
          s"hakijan ammatillisella tutkinnolla ${tutkinnonValitsija.tutkinnonIndeksi} , mutta oli ${suoritukset.size}")
      }
      arvonHakija(suoritukset)
    }
  }

  def haeAmmatillisenTutkinnonYtoOsaAlueet(tutkinnonValitsija: AmmatillisenPerustutkinnonValitsija, ytoKoodiArvo: String, hakemus: Hakemus): Seq[Osasuoritus] = {
    if (hakemus.koskiOpiskeluoikeudet == null) {
      Nil
    } else {
      YhteisetTutkinnonOsat.haeYtoOsaAlueet(tutkinnonValitsija, hakemus, ytoKoodiArvo)
        .map(Osasuoritus(_))
    }
  }

  private def haeAmmatillisenTutkinnonOsat(tutkinnonValitsija: AmmatillisenPerustutkinnonValitsija, hakemus: Hakemus): List[Osasuoritus] = {
    if (hakemus.koskiOpiskeluoikeudet == null) {
      Nil
    } else {
      val oikeaOpiskeluoikeus: Json = Tutkinnot.etsiValmiitTutkinnot(
        Some(tutkinnonValitsija.valmistumisenTakarajaPvm),
        json = hakemus.koskiOpiskeluoikeudet,
        opiskeluoikeudenHaluttuTyyppi = ammatillisenHuomioitavaOpiskeluoikeudenTyyppi,
        suorituksenHaluttuTyyppi = ammatillisenSuorituksenTyyppi, hakemus = hakemus)(tutkinnonValitsija.tutkinnonIndeksi)

      val suorituksenSallitutKoodit: Set[Int] = ammatillisenHhuomioitavatKoulutustyypit.map(_.koodiarvo)
      val suoritukset = Tutkinnot.etsiValiditSuoritukset(oikeaOpiskeluoikeus, tutkinnonValitsija.valmistumisenTakarajaPvm, suorituksenSallitutKoodit)

      val osasuoritusPredikaatti: Json => Boolean = osasuoritus => {
        "ammatillisentutkinnonosa" == _osasuorituksenTyypinKoodiarvo.getOption(osasuoritus).orNull
      }

      suoritukset.flatMap((suoritus: Json) => OsaSuoritukset.etsiOsasuoritukset(suoritus, osasuoritusPredikaatti))
        .map(Osasuoritus(_))
    }
  }
}

sealed trait AmmatillisenPerustutkinnonKoulutustyyppi {
  val koodiarvo: Int
  val kuvaus: String
}

case object AmmatillinenPerustutkinto extends AmmatillisenPerustutkinnonKoulutustyyppi {
  val koodiarvo: Int = 1
  val kuvaus: String = "Ammatillinen perustutkinto"
}

case object AmmatillinenReforminMukainenPerustutkinto extends AmmatillisenPerustutkinnonKoulutustyyppi {
  val koodiarvo: Int = 26
  val kuvaus: String = "Ammatillinen perustutkinto (reformin mukainen)"
}

case object AmmatillinenPerustutkintoErityisopetuksena extends AmmatillisenPerustutkinnonKoulutustyyppi {
  val koodiarvo: Int = 4
  val kuvaus: String = "Ammatillinen perustutkinto erityisopetuksena"
}


case class Tutkinto(indeksi: Int,
                    opiskeluoikeudenOid: String,
                    opiskeluoikeudenVersio: Int,
                    opiskeluoikeudenAikaleima: String,
                    opiskeluoikeudenOppilaitoksenSuomenkielinenNimi: String,
                    vahvistusPvm: String,
                    vahvistettuRajaPäiväänMennessä: Boolean)
