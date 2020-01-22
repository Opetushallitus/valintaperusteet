package fi.vm.sade.service.valintaperusteet.laskenta

import fi.vm.sade.service.valintaperusteet.laskenta.api.Hakemus
import io.circe.Json
import io.circe.optics.JsonPath
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object KoskiLaskenta {
  private val LOG: Logger = LoggerFactory.getLogger(KoskiLaskenta.getClass)
  private val ammatillisenHuomioitavaOpiskeluoikeudenTyyppi: String = "ammatillinenkoulutus"
  private val ammatillisenSuorituksenTyyppi: String = "ammatillinentutkinto"
  private val sallitutSuoritusTavat = Set("ops", "reformi")

  val ammatillisenHhuomioitavatKoulutustyypit: Set[AmmatillisenPerustutkinnonKoulutustyyppi] =
    Set(AmmatillinenPerustutkinto, AmmatillinenReforminMukainenPerustutkinto, AmmatillinenPerustutkintoErityisopetuksena)

  // Suoritusten alla olevien osasuoritusten tietoja etsivä linssi
  private val _osasuoritukset = JsonPath.root.osasuoritukset.each.json

  // Osasuorituksen rakennetta purkavat linssit
  private val _osasuorituksenArviointi = JsonPath.root.arviointi.each.json
  private val _osasuorituksenTyypinKoodiarvo = JsonPath.root.tyyppi.koodiarvo.string
  private val _osasuorituksenKoulutusmoduuli = JsonPath.root.koulutusmoduuli
  private val _osasuorituksenKoulutusmoduulinLaajuudenArvo = _osasuorituksenKoulutusmoduuli.laajuus.arvo.bigDecimal
  private val _osasuorituksenKoulutusmoduulinTunnisteenKoodiarvo = _osasuorituksenKoulutusmoduuli.tunniste.koodiarvo.string
  private val _osasuorituksenKoulutusmoduulinNimiFi = _osasuorituksenKoulutusmoduuli.tunniste.nimi.fi.string

  private def sulkeutumisPaivamaara: DateTime = {
    DateTime.now()  // TODO: Tee konfiguroitavaksi
  }

  def laskeAmmatillisetTutkinnot(hakemus: Hakemus): Int = {
    if (hakemus.koskiOpiskeluoikeudet == null) {
      0
    } else {
      etsiValmiitTutkinnot(hakemus.koskiOpiskeluoikeudet, ammatillisenHuomioitavaOpiskeluoikeudenTyyppi, ammatillisenSuorituksenTyyppi).size
    }
  }

  def laskeAmmatillisenTutkinnonOsat(tutkinnonValitsija: AmmatillisenPerustutkinnonValitsija, hakemus: Hakemus): Int = {
    haeAmmatillisenTutkinnonOsat(tutkinnonValitsija, hakemus).size
  }

  def haeYtoArvosana(ammatillisenPerustutkinnonValitsija: AmmatillisenPerustutkinnonValitsija,
                     hakemus: Hakemus,
                     valintaperusteviite: Laskenta.Valintaperuste,
                     oletusarvo: Option[BigDecimal]
                    ): Option[BigDecimal] = {
    if (hakemus.koskiOpiskeluoikeudet != null) {
      haeYtoArvosana(ammatillisenPerustutkinnonValitsija, hakemus.oid, hakemus.koskiOpiskeluoikeudet, valintaperusteviite.tunniste)
    } else {
      oletusarvo
    }
  }

  def haeYtoArviointiasteikko(ammatillisenPerustutkinnonValitsija: AmmatillisenPerustutkinnonValitsija,
                              hakemus: Hakemus,
                              valintaperusteviite: Laskenta.Valintaperuste
                             ): Option[String] = {
    if (hakemus.koskiOpiskeluoikeudet != null) {
      haeAmmatillisenSuorituksenTiedot(hakemus.oid, hakemus.koskiOpiskeluoikeudet, ammatillisenPerustutkinnonValitsija, valintaperusteviite.tunniste) match {
        case Nil => None
        case (osasuorituksenKoodiarvo, osasuorituksenNimiFi, osasuorituksenArvio, osasuorituksenArviointiAsteikko) :: Nil => Some(osasuorituksenArviointiAsteikko)
        case xs => throw new IllegalArgumentException(s"Piti löytyä vain yksi suoritus valitsijalla $ammatillisenPerustutkinnonValitsija , mutta löytyi ${xs.size} : $xs")
      }
    } else {
      None
    }
  }

  def haeAmmatillisenTutkinnonOsanLaajuus(tutkinnonValitsija: AmmatillisenPerustutkinnonValitsija,
                                          osanValitsija: AmmatillisenTutkinnonOsanValitsija,
                                          hakemus: Hakemus,
                                          oletusarvo: Option[BigDecimal]
                                     ): Option[BigDecimal] = {
    val osa: (String, String, Option[Int], String, Option[BigDecimal]) = haeAmmatillisenTutkinnonOsat(tutkinnonValitsija, hakemus)(osanValitsija.osanIndeksi)
    osa._5.orElse(oletusarvo)
  }

  def haeAmmatillisenTutkinnonOsanArvosana(tutkinnonValitsija: AmmatillisenPerustutkinnonValitsija,
                                           osanValitsija: AmmatillisenTutkinnonOsanValitsija,
                                           hakemus: Hakemus,
                                           oletusarvo: Option[BigDecimal]
                                      ): Option[BigDecimal] = {
    Some(BigDecimal(3.0))  // TODO implement
  }

  private def haeAmmatillisenTutkinnonOsat(tutkinnonValitsija: AmmatillisenPerustutkinnonValitsija, hakemus: Hakemus): List[(String, String, Option[Int], String, Option[BigDecimal])] = {
    if (hakemus.koskiOpiskeluoikeudet == null) {
      Nil
    } else {
      val oikeaOpiskeluoikeus: Json = etsiValmiitTutkinnot(
        hakemus.koskiOpiskeluoikeudet,
        ammatillisenHuomioitavaOpiskeluoikeudenTyyppi,
        ammatillisenSuorituksenTyyppi)(tutkinnonValitsija.tutkinnonIndeksi)

      val suorituksenSallitutKoodit: Set[Int] = ammatillisenHhuomioitavatKoulutustyypit.map(_.koodiarvo)
      val suoritukset = etsiValiditSuoritukset(oikeaOpiskeluoikeus, sulkeutumisPaivamaara, suorituksenSallitutKoodit)

      val osasuoritusPredikaatti: Json => Boolean = osasuoritus => {
        "ammatillisentutkinnonosa" == _osasuorituksenTyypinKoodiarvo.getOption(osasuoritus).orNull
      }

      suoritukset.flatMap(etsiOsasuoritukset(_, sulkeutumisPaivamaara, osasuoritusPredikaatti))
    }
  }

  private def haeYtoArvosana(ammatillisenPerustutkinnonValitsija: AmmatillisenPerustutkinnonValitsija,
                             hakemusOid: String,
                             opiskeluoikeudet: Json,
                             ytoKoodiArvo: String
                            ): Option[BigDecimal] = {

    haeAmmatillisenSuorituksenTiedot(hakemusOid, opiskeluoikeudet, ammatillisenPerustutkinnonValitsija, ytoKoodiArvo) match {
      case Nil => None
      case (_, _, osasuorituksenArvio, _) :: Nil => osasuorituksenArvio match {
        case Some(x) => Some(BigDecimal(x))
        case None => None
      }
      case xs => throw new IllegalArgumentException(s"Piti löytyä vain yksi suoritus valitsijalla $ammatillisenPerustutkinnonValitsija , mutta löytyi ${xs.size} : $xs")
    }
  }

  private def haeAmmatillisenSuorituksenTiedot(hakemusOid: String,
                                               kaikkiOpiskeluoikeudet: Json,
                                               ammatillisenPerustutkinnonValitsija: AmmatillisenPerustutkinnonValitsija,
                                               ytoKoodiArvo: String
                                              ): Seq[(String, String, Option[Int], String)] = {
    val oikeaOpiskeluoikeus: Json = etsiValmiitTutkinnot(kaikkiOpiskeluoikeudet, ammatillisenHuomioitavaOpiskeluoikeudenTyyppi, ammatillisenSuorituksenTyyppi)(ammatillisenPerustutkinnonValitsija.tutkinnonIndeksi)
    val ammatillisenSuorituksenTiedot = haeAmmatillisenSuorituksenTiedot(
      hakemusOid,
      Json.arr(oikeaOpiskeluoikeus),
      ytoKoodiArvo)
    ammatillisenSuorituksenTiedot
  }

  private def haeAmmatillisenSuorituksenTiedot(hakemusOid: String, opiskeluoikeudet: Json, ytoKoodiArvo: String): Seq[(String, String, Option[Int], String)] = {
    try {
      val suoritukset = etsiAmmatillistenTutkintojenSuoritukset(opiskeluoikeudet)
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

  private def etsiAmmatillistenTutkintojenSuoritukset(opiskeluoikeudet: Json): Seq[Json] = {
    val suorituksenSallitutKoodit: Set[Int] = ammatillisenHhuomioitavatKoulutustyypit.map(_.koodiarvo)

    etsiValmiitTutkinnot(opiskeluoikeudet, ammatillisenHuomioitavaOpiskeluoikeudenTyyppi, ammatillisenSuorituksenTyyppi)
      .flatMap(tutkinto => etsiValiditSuoritukset(tutkinto, sulkeutumisPaivamaara, suorituksenSallitutKoodit))
  }

  private def etsiYhteisetTutkinnonOsat(suoritus: Json, sulkeutumisPäivämäärä: DateTime, osasuorituksenSallitutKoodit: Set[String]): List[(String, String, Option[Int], String)] = {
    etsiOsasuoritukset(suoritus, sulkeutumisPäivämäärä, osasuoritus => {
      osasuorituksenSallitutKoodit.contains(_osasuorituksenKoulutusmoduulinTunnisteenKoodiarvo.getOption(osasuoritus).orNull)
    }).map(x => (x._1, x._2, x._3, x._4))
  }

  private def etsiOsasuoritukset(suoritus: Json,
                                 sulkeutumisPäivämäärä: DateTime,
                                 osasuoritusPredikaatti: Json => Boolean,
                                ): List[(String, String, Option[Int], String, Option[BigDecimal])] = {
    _osasuoritukset.getAll(suoritus).filter(osasuoritusPredikaatti).map(osasuoritus => {
      val osasuorituksenKoodiarvo = _osasuorituksenKoulutusmoduulinTunnisteenKoodiarvo.getOption(osasuoritus).orNull
      val osasuorituksenNimiFi = _osasuorituksenKoulutusmoduulinNimiFi.getOption(osasuoritus).orNull
      val (uusinHyvaksyttyArvio: String, uusinLaajuus: BigDecimal, uusinArviointiAsteikko: String) = etsiUusinArvosanaLaajuusJaArviointiAsteikko(osasuoritus)

      LOG.debug("Osasuorituksen arvio: %s".format(uusinHyvaksyttyArvio))
      LOG.debug("Osasuorituksen laajuus: %s".format(uusinLaajuus))
      LOG.debug("Osasuorituksen nimi: %s".format(osasuorituksenNimiFi))
      LOG.debug("Osasuorituksen koodiarvo: %s".format(osasuorituksenKoodiarvo))

      (osasuorituksenKoodiarvo, osasuorituksenNimiFi, uusinHyvaksyttyArvio.toIntOption, uusinArviointiAsteikko, Option(uusinLaajuus))
    })
  }

  private def etsiUusinArvosanaLaajuusJaArviointiAsteikko(osasuoritus: Json): (String, BigDecimal, String) = {
    val (_, uusinHyvaksyttyArvio, uusinLaajuus, uusinArviointiAsteikko): (String, String, BigDecimal, String) =
      _osasuorituksenArviointi.getAll(osasuoritus)
        .filter(arvio => JsonPath.root.hyväksytty.boolean.getOption(arvio).getOrElse(false))
        .map(arvio => (
          JsonPath.root.päivä.string.getOption(arvio).orNull,
          JsonPath.root.arvosana.koodiarvo.string.getOption(arvio).orNull,
          _osasuorituksenKoulutusmoduulinLaajuudenArvo.getOption(osasuoritus).orNull, // TODO lisää laajuuden yksikkö / estä muut kuin osp
          JsonPath.root.arvosana.koodistoUri.string.getOption(arvio).orNull
        )).sorted(Ordering.Tuple4(Ordering.String.reverse, Ordering.String, Ordering.BigDecimal, Ordering.String))
        .head
    (uusinHyvaksyttyArvio, uusinLaajuus, uusinArviointiAsteikko)
  }

  private def etsiValiditSuoritukset(tutkinto: Json, sulkeutumisPäivämäärä: DateTime, suorituksenSallitutKoodit: Set[Int]): List[Json] = {
    val dateFormat = DateTimeFormat.forPattern("yyyy-MM-dd")

    // Suoritukset etsivä linssi
    val _suoritukset = JsonPath.root.suoritukset.each.json

    val _vahvistusPvm = JsonPath.root.vahvistus.päivä.string

    // Suorituksen rakennetta purkavat linssit
    val _koulutusmoduuli = JsonPath.root.koulutusmoduuli
    val _koodiarvo = _koulutusmoduuli.tunniste.koodiarvo.string
    val _lyhytNimiFi = _koulutusmoduuli.tunniste.nimi.fi.string
    val _koulutusTyypinKoodiarvo = _koulutusmoduuli.koulutustyyppi.koodiarvo.string
    val _koulutusTyypinNimiFi = _koulutusmoduuli.koulutustyyppi.nimi.fi.string
    val _suoritusTapa = JsonPath.root.suoritustapa.koodiarvo.string

    _suoritukset.getAll(tutkinto).filter(suoritus => {
      val koodiarvo = _koodiarvo.getOption(suoritus).orNull
      val lyhytNimiFi = _lyhytNimiFi.getOption(suoritus).orNull
      val koulutusTyypinNimiFi = _koulutusTyypinNimiFi.getOption(suoritus).orNull
      val suoritusTapa = _suoritusTapa.getOption(suoritus).orNull

      val vahvistettuRajapäivänä = _vahvistusPvm.getOption(suoritus) match {
        case Some(dateString) => sulkeutumisPäivämäärä.isAfter(dateFormat.parseDateTime(dateString))
        case None => false
      }
      val koulutusTyypinKoodiarvo = _koulutusTyypinKoodiarvo.getOption(suoritus) match {
        case Some(s) => s.toInt
        case None => -1
      }
      val onkoSallitunTyyppinenSuoritus =
        suorituksenSallitutKoodit.contains(koulutusTyypinKoodiarvo) &&
        sallitutSuoritusTavat.contains(suoritusTapa)

      LOG.debug("Koodiarvo: %s".format(koodiarvo))
      LOG.debug("Suoritustapa: %s".format(suoritusTapa))
      LOG.debug("Onko sallitun tyyppinen suoritus: %s".format(onkoSallitunTyyppinenSuoritus))
      LOG.debug("Nimi: %s".format(lyhytNimiFi))
      LOG.debug("Koulutustyypin nimi: %s".format(koulutusTyypinNimiFi))
      LOG.debug("Koulutustyypin koodiarvo: %s".format(koulutusTyypinKoodiarvo))
      LOG.debug("On vahvistettu rajapäivänä pvm: %s".format(vahvistettuRajapäivänä))

      onkoSallitunTyyppinenSuoritus && vahvistettuRajapäivänä
    })
  }

  private def etsiValmiitTutkinnot(json: Json, opiskeluoikeudenHaluttuTyyppi: String, suorituksenHaluttuTyyppi: String): Seq[Json] = {
    // Opiskeluoikeudet etsivä linssi
    val _opiskeluoikeudet = JsonPath.root.each.json

    // Opiskeluoikeuden tyypin etsivä linssi
    val _opiskeluoikeudenTyyppi = JsonPath.root.tyyppi.koodiarvo.string
    val _valmistumisTila = JsonPath.root.tila.opiskeluoikeusjaksot.each.tila.koodiarvo.string
    val _suorituksenTyyppi = JsonPath.root.suoritukset.each.tyyppi.koodiarvo.string
    val _suoritusTapa = JsonPath.root.suoritukset.each.suoritustapa.koodiarvo.string

    _opiskeluoikeudet.getAll(json).filter(opiskeluoikeus => {
      val opiskeluoikeudenTyyppi = _opiskeluoikeudenTyyppi.getOption(opiskeluoikeus).orNull
      val valmistumisTila = _valmistumisTila.getAll(opiskeluoikeus)
      val suorituksenTyyppi = _suorituksenTyyppi.getAll(opiskeluoikeus)
      val suoritusTapa = _suoritusTapa.getAll(opiskeluoikeus)

      val onkoValmistunut: Boolean = valmistumisTila.contains("valmistunut")
      val onkoValidiSuoritusTapa = suoritusTapa.map(s => sallitutSuoritusTavat.contains(s)).exists(v => v)
      val onkoAmmatillinenOpiskeluOikeus =
        opiskeluoikeudenTyyppi == opiskeluoikeudenHaluttuTyyppi &&
        suorituksenTyyppi.contains(suorituksenHaluttuTyyppi) &&
        onkoValidiSuoritusTapa

      LOG.debug("Opiskeluoikeuden tyyppi: %s".format(opiskeluoikeudenTyyppi))
      LOG.debug("Valmistumistila: %s".format(valmistumisTila))
      LOG.debug("Suoritustapa: %s".format(suoritusTapa))
      LOG.debug("Onko validi suoritustapa: %s".format(onkoValidiSuoritusTapa))
      LOG.debug("Onko valmistunut: %s".format(onkoValmistunut))
      LOG.debug("Suorituksen tyyppi: %s".format(suorituksenTyyppi))
      LOG.debug("Onko ammatillinen opiskeluoikeus: %s".format(onkoAmmatillinenOpiskeluOikeus))

      onkoAmmatillinenOpiskeluOikeus && onkoValmistunut
    })
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
