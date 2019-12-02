package fi.vm.sade.kaava

import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi
import play.api.libs.json.JsArray
import play.api.libs.json.Json

object Funktiokuvaaja {

  object Funktiotyyppi extends Enumeration {
    type Funktiotyyppi = Value

    val TOTUUSARVOFUNKTIO = Value("TOTUUSARVOFUNKTIO")
    val LUKUARVOFUNKTIO = Value("LUKUARVOFUNKTIO")
  }

  object Syoteparametrityyppi extends Enumeration {
    type Syoteparametrityyppi = Value

    val KOKONAISLUKU = Value("KOKONAISLUKU")
    val DESIMAALILUKU = Value("DESIMAALILUKU")
    val TOTUUSARVO = Value("TOTUUSARVO")
    val CHECKBOX = Value("CHECKBOX")
    val MERKKIJONO = Value("MERKKIJONO")
    val ARVOJOUKKO = Value("ARVOJOUKKO")
  }

  object Konvertterinimi extends Enumeration {
    type Konvertterinimi = Value

    val ARVOKONVERTTERI = Value("ARVOKONVERTTERI")
    val ARVOVALIKONVERTTERI = Value("ARVOVALIKONVERTTERI")
  }

  object Kardinaliteetti extends Enumeration {
    type Kardinaliteetti = Value

    val YKSI = Value("YKSI")
    val N = Value("N")
    val LISTA_PAREJA = Value("LISTA PAREJA")
  }

  import Funktiotyyppi._
  import Kardinaliteetti._
  import Konvertterinimi._
  import Syoteparametrityyppi._
  import fi.vm.sade.service.valintaperusteet.laskenta.JsonFormats._

  trait KonvertteriTyyppi {
    val nimi: Konvertterinimi.Konvertterinimi
  }

  object Arvovalikonvertterikuvaus extends KonvertteriTyyppi {
    override val nimi = ARVOVALIKONVERTTERI
  }

  case class Arvokonvertterikuvaus(arvotyyppi: Syoteparametrityyppi,
                                   arvojoukko: Array[(String,String)] = Array.empty[(String,String)]) extends KonvertteriTyyppi {
    override val nimi = ARVOKONVERTTERI
  }

  case class Konvertterikuvaus(pakollinen: Boolean,
                               konvertteriTyypit: Map[Konvertterinimi.Konvertterinimi, KonvertteriTyyppi])

  case class Valintaperusteparametrikuvaus(nimi: String, tyyppi: Syoteparametrityyppi, arvojoukko: Array[(String,String)] = Array.empty[(String,String)], kuvaus: String = "")

  case class Syoteparametrikuvaus(avain: String,
                                  tyyppi: Syoteparametrityyppi,
                                  pakollinen: Boolean = true,
                                  arvojoukko: Array[(String,String)] = Array.empty[(String,String)],
                                  kuvaus: String = "")

  case class Funktioargumenttikuvaus(nimi: String, tyyppi: Funktiotyyppi,
                                     kardinaliteetti: Kardinaliteetti = Kardinaliteetti.YKSI)


  case class Funktiokuvaus(tyyppi: Funktiotyyppi,
                           funktioargumentit: Seq[Funktioargumenttikuvaus] = Nil,
                           syoteparametrit: Seq[Syoteparametrikuvaus] = Nil,
                           valintaperusteparametri: Seq[Valintaperusteparametrikuvaus] = Nil,
                           konvertteri: Option[Konvertterikuvaus] = None)

  def annaFunktiokuvauksetAsJson = {

    val jsonKuvaukset = funktiokuvaukset.map(annaFunktiokuvausAsJson(_))

    val jsonKuvauksetArray = jsonKuvaukset.foldLeft(JsArray())(_ ++ Json.arr(_))

    Json.stringify(jsonKuvauksetArray)
  }

  def annaFunktiokuvaukset = {
    funktiokuvaukset
  }


  def annaFunktiokuvaus(nimi: String): (Funktionimi, Funktiokuvaus) = {
    Funktionimi.values().filter(_.name() == nimi.toUpperCase()).toList match {
      case Nil => throw new RuntimeException(s"No funktio with name ${nimi.toUpperCase()}")
      case head :: tail => {
        annaFunktiokuvaus(head)
      }
    }
  }

  def annaFunktiokuvaus(nimi: Funktionimi) = {
    if (funktiokuvaukset.contains(nimi)) {
      (nimi, funktiokuvaukset(nimi))
    } else {
      throw new RuntimeException(s"No funktiokuvaus defined for funktio ${nimi.name()}")
    }
  }

  def annaFunktiokuvausAsJson(nimi: String): String = {
    Json.stringify(annaFunktiokuvausAsJson(annaFunktiokuvaus(nimi)))
  }

  private def annaFunktiokuvausAsJson(kutsu: (Funktionimi, Funktiokuvaus)) = {

    val fk = kutsu._2

    val argumentit = if (fk.funktioargumentit.isEmpty) Json.obj()
    else Json.obj("funktioargumentit" ->
      fk.funktioargumentit.map(Json.toJson(_)))

    val parametrit = if (fk.syoteparametrit.isEmpty) Json.obj()
    else Json.obj("syoteparametrit" ->
      fk.syoteparametrit.map(Json.toJson(_)))

    val valintaperuste = if (fk.valintaperusteparametri.isEmpty) Json.obj()
    else Json.obj("valintaperusteviitteet" -> fk.valintaperusteparametri.map(Json.toJson(_)))

    val konvertteri = fk.konvertteri match {
      case Some(konv) => Json.obj("konvertteri" -> Json.toJson(konv))
      //case Some(konv) => Json.obj("konvertteri" -> konvertterikuvausAsJson(konv))
      case None => Json.obj()
    }


    Json.obj(
      "nimi" -> kutsu._1.name(),
      "tyyppi" -> fk.tyyppi.toString) ++ argumentit ++ parametrit ++ valintaperuste ++ konvertteri

  }

  private val funktiokuvaukset = Map(
    Funktionimi.DEMOGRAFIA -> Funktiokuvaus(
      tyyppi = Funktiotyyppi.TOTUUSARVOFUNKTIO,
      syoteparametrit = List(
        Syoteparametrikuvaus(avain = "tunniste", tyyppi = Syoteparametrityyppi.MERKKIJONO),
        Syoteparametrikuvaus(avain = "prosenttiosuus", tyyppi = Syoteparametrityyppi.DESIMAALILUKU)
      )
    ),
    Funktionimi.EI -> Funktiokuvaus(
      tyyppi = Funktiotyyppi.TOTUUSARVOFUNKTIO,
      funktioargumentit = List(Funktioargumenttikuvaus("f", Funktiotyyppi.TOTUUSARVOFUNKTIO))
    ),
    Funktionimi.HAEAMMATILLINENYTOARVOSANA -> Funktiokuvaus(
      tyyppi = Funktiotyyppi.LUKUARVOFUNKTIO,
      valintaperusteparametri = List(Valintaperusteparametrikuvaus("tunniste", Syoteparametrityyppi.MERKKIJONO, kuvaus = "Tunniste")),
      konvertteri = Some(
        Konvertterikuvaus(
          pakollinen = false,
          konvertteriTyypit = Map(ARVOKONVERTTERI -> Arvokonvertterikuvaus(Syoteparametrityyppi.DESIMAALILUKU),
            ARVOVALIKONVERTTERI -> Arvovalikonvertterikuvaus)
        ))
    ),
    Funktionimi.HAEAMMATILLINENYTOARVIOINTIASTEIKKO -> Funktiokuvaus(
      tyyppi = Funktiotyyppi.LUKUARVOFUNKTIO,
      valintaperusteparametri = List(Valintaperusteparametrikuvaus("yto-koodi", Syoteparametrityyppi.MERKKIJONO, kuvaus = "Yhteisen tutkinnon osan koodi")),
      konvertteri = Some(
        Konvertterikuvaus(
          pakollinen = true,
          konvertteriTyypit = Map(ARVOKONVERTTERI -> Arvokonvertterikuvaus(Syoteparametrityyppi.MERKKIJONO))
        ))
    ),
    Funktionimi.HAEUSEANTUTKINNONAMMATILLINENYTOARVOSANA -> Funktiokuvaus(
      tyyppi = Funktiotyyppi.LUKUARVOFUNKTIO,
      valintaperusteparametri = List(Valintaperusteparametrikuvaus("yto-koodi", Syoteparametrityyppi.MERKKIJONO, kuvaus = "Yhteisen tutkinnon osan koodi")),
      konvertteri = Some(
        Konvertterikuvaus(
          pakollinen = false,
          konvertteriTyypit = Map(ARVOKONVERTTERI -> Arvokonvertterikuvaus(Syoteparametrityyppi.DESIMAALILUKU),
            ARVOVALIKONVERTTERI -> Arvovalikonvertterikuvaus)
        ))
    ),
    Funktionimi.HAEMAKSIMIAMMATILLISISTATUTKINNOISTA -> Funktiokuvaus(
      tyyppi = Funktiotyyppi.LUKUARVOFUNKTIO,
      funktioargumentit = List(Funktioargumenttikuvaus("args", Funktiotyyppi.LUKUARVOFUNKTIO, Kardinaliteetti.N))
    ),
    Funktionimi.HAELUKUARVO -> Funktiokuvaus(
      tyyppi = Funktiotyyppi.LUKUARVOFUNKTIO,
      syoteparametrit = List(
        Syoteparametrikuvaus(avain = "oletusarvo", tyyppi = Syoteparametrityyppi.DESIMAALILUKU, pakollinen = false)
      ),
      valintaperusteparametri = List(Valintaperusteparametrikuvaus("tunniste", Syoteparametrityyppi.DESIMAALILUKU, kuvaus = "Tunniste")),
      konvertteri = Some(
        Konvertterikuvaus(
          pakollinen = false,
          konvertteriTyypit = Map(ARVOKONVERTTERI -> Arvokonvertterikuvaus(Syoteparametrityyppi.DESIMAALILUKU),
            ARVOVALIKONVERTTERI -> Arvovalikonvertterikuvaus)
        ))
    ),
    Funktionimi.HAELUKUARVOEHDOLLA -> Funktiokuvaus(
      tyyppi = Funktiotyyppi.LUKUARVOFUNKTIO,
      syoteparametrit = List(
        Syoteparametrikuvaus(avain = "oletusarvo", tyyppi = Syoteparametrityyppi.DESIMAALILUKU, pakollinen = false)
      ),
      valintaperusteparametri = List(
        Valintaperusteparametrikuvaus("tunniste", Syoteparametrityyppi.DESIMAALILUKU, kuvaus = "Tunniste"),
        Valintaperusteparametrikuvaus("ehto", Syoteparametrityyppi.MERKKIJONO, kuvaus = "Ehto")
      ),
      konvertteri = Some(
        Konvertterikuvaus(
          pakollinen = false,
          konvertteriTyypit = Map(ARVOKONVERTTERI -> Arvokonvertterikuvaus(Syoteparametrityyppi.DESIMAALILUKU),
            ARVOVALIKONVERTTERI -> Arvovalikonvertterikuvaus)
        ))
    ),
    Funktionimi.HAEYOARVOSANA -> Funktiokuvaus(
      tyyppi = Funktiotyyppi.LUKUARVOFUNKTIO,
      syoteparametrit = List(
        Syoteparametrikuvaus(avain = "alkuvuosi", tyyppi = Syoteparametrityyppi.KOKONAISLUKU, pakollinen = false, kuvaus = "Alkaen (vuosi)"),
        Syoteparametrikuvaus(avain = "alkulukukausi", tyyppi = Syoteparametrityyppi.ARVOJOUKKO, pakollinen = false, arvojoukko = Arvojoukot.LUKUKAUDET, kuvaus = "Alkaen (lukukausi)"),
        Syoteparametrikuvaus(avain = "loppuvuosi", tyyppi = Syoteparametrityyppi.KOKONAISLUKU, pakollinen = false, kuvaus = "Päättyen (vuosi)"),
        Syoteparametrikuvaus(avain = "loppulukukausi", tyyppi = Syoteparametrityyppi.ARVOJOUKKO, pakollinen = false, arvojoukko = Arvojoukot.LUKUKAUDET, kuvaus = "Päättyen (lukukausi)"),
        Syoteparametrikuvaus(avain = "rooli", tyyppi = Syoteparametrityyppi.ARVOJOUKKO, pakollinen = false, arvojoukko = Arvojoukot.KOEROOLIT, kuvaus = "Rooli"),
        Syoteparametrikuvaus(avain = "I", tyyppi = Syoteparametrityyppi.DESIMAALILUKU, pakollinen = true, kuvaus = "Arvosana I"),
        Syoteparametrikuvaus(avain = "A", tyyppi = Syoteparametrityyppi.DESIMAALILUKU, pakollinen = true, kuvaus = "Arvosana A"),
        Syoteparametrikuvaus(avain = "B", tyyppi = Syoteparametrityyppi.DESIMAALILUKU, pakollinen = true, kuvaus = "Arvosana B"),
        Syoteparametrikuvaus(avain = "C", tyyppi = Syoteparametrityyppi.DESIMAALILUKU, pakollinen = true, kuvaus = "Arvosana C"),
        Syoteparametrikuvaus(avain = "M", tyyppi = Syoteparametrityyppi.DESIMAALILUKU, pakollinen = true, kuvaus = "Arvosana M"),
        Syoteparametrikuvaus(avain = "E", tyyppi = Syoteparametrityyppi.DESIMAALILUKU, pakollinen = true, kuvaus = "Arvosana E"),
        Syoteparametrikuvaus(avain = "L", tyyppi = Syoteparametrityyppi.DESIMAALILUKU, pakollinen = true, kuvaus = "Arvosana L"),
        Syoteparametrikuvaus(avain = "valmistuneet", tyyppi = Syoteparametrityyppi.CHECKBOX, pakollinen = false, kuvaus = "Vain valmistuneet huomioidaan")
      ),
      valintaperusteparametri = List(Valintaperusteparametrikuvaus("oppiaine", tyyppi = Syoteparametrityyppi.ARVOJOUKKO, arvojoukko = Arvojoukot.YO_OPPIAINEET, kuvaus = "Oppiaine"))
    ),
    Funktionimi.HAEOSAKOEARVOSANA -> Funktiokuvaus(
      tyyppi = Funktiotyyppi.LUKUARVOFUNKTIO,
      syoteparametrit = List(
        Syoteparametrikuvaus(avain = "alkuvuosi", tyyppi = Syoteparametrityyppi.KOKONAISLUKU, pakollinen = false, kuvaus = "Alkaen (vuosi)"),
        Syoteparametrikuvaus(avain = "alkulukukausi", tyyppi = Syoteparametrityyppi.ARVOJOUKKO, pakollinen = false, arvojoukko = Arvojoukot.LUKUKAUDET, kuvaus = "Alkaen (lukukausi)"),
        Syoteparametrikuvaus(avain = "loppuvuosi", tyyppi = Syoteparametrityyppi.KOKONAISLUKU, pakollinen = false, kuvaus = "Päättyen (vuosi)"),
        Syoteparametrikuvaus(avain = "loppulukukausi", tyyppi = Syoteparametrityyppi.ARVOJOUKKO, pakollinen = false, arvojoukko = Arvojoukot.LUKUKAUDET, kuvaus = "Päättyen (lukukausi)"),
        Syoteparametrikuvaus(avain = "rooli", tyyppi = Syoteparametrityyppi.ARVOJOUKKO, pakollinen = false, arvojoukko = Arvojoukot.KOEROOLIT, kuvaus = "Rooli"),
        Syoteparametrikuvaus(avain = "valmistuneet", tyyppi = Syoteparametrityyppi.CHECKBOX, pakollinen = false, kuvaus = "Vain valmistuneet huomioidaan")
      ),
      valintaperusteparametri = List(Valintaperusteparametrikuvaus("oppiaine", tyyppi = Syoteparametrityyppi.ARVOJOUKKO, arvojoukko = Arvojoukot.OSAKOKEET, kuvaus = "YO-koe")),
      konvertteri = Some(
        Konvertterikuvaus(
          pakollinen = false,
          konvertteriTyypit = Map(ARVOKONVERTTERI -> Arvokonvertterikuvaus(Syoteparametrityyppi.DESIMAALILUKU),
            ARVOVALIKONVERTTERI -> Arvovalikonvertterikuvaus)
        ))
    ),
    Funktionimi.HAEMERKKIJONOJAKONVERTOILUKUARVOKSI -> Funktiokuvaus(
      tyyppi = Funktiotyyppi.LUKUARVOFUNKTIO,
      syoteparametrit = List(
        Syoteparametrikuvaus(avain = "oletusarvo", tyyppi = Syoteparametrityyppi.DESIMAALILUKU, pakollinen = false)
      ),
      valintaperusteparametri = List(Valintaperusteparametrikuvaus("tunniste", Syoteparametrityyppi.MERKKIJONO, kuvaus = "Tunniste")),
      konvertteri = Some(
        Konvertterikuvaus(
          pakollinen = true,
          konvertteriTyypit = Map(ARVOKONVERTTERI -> Arvokonvertterikuvaus(Syoteparametrityyppi.MERKKIJONO))
        ))
    ),
    Funktionimi.HAETOTUUSARVOJAKONVERTOILUKUARVOKSI -> Funktiokuvaus(
      tyyppi = Funktiotyyppi.LUKUARVOFUNKTIO,
      syoteparametrit = List(
        Syoteparametrikuvaus(avain = "oletusarvo", tyyppi = Syoteparametrityyppi.DESIMAALILUKU, pakollinen = false)
      ),
      valintaperusteparametri = List(Valintaperusteparametrikuvaus("tunniste", Syoteparametrityyppi.MERKKIJONO, kuvaus = "Tunniste")),
      konvertteri = Some(
        Konvertterikuvaus(
          pakollinen = true,
          konvertteriTyypit = Map(ARVOKONVERTTERI -> Arvokonvertterikuvaus(Syoteparametrityyppi.ARVOJOUKKO, Arvojoukot.TOTUUSARVOT))
        ))
    ),
    Funktionimi.HAEMERKKIJONOJAKONVERTOITOTUUSARVOKSI -> Funktiokuvaus(
      tyyppi = Funktiotyyppi.TOTUUSARVOFUNKTIO,
      syoteparametrit = List(
        Syoteparametrikuvaus(avain = "oletusarvo", tyyppi = Syoteparametrityyppi.TOTUUSARVO, pakollinen = false)
      ),
      valintaperusteparametri = List(Valintaperusteparametrikuvaus("tunniste", Syoteparametrityyppi.MERKKIJONO, kuvaus = "Tunniste")),
      konvertteri = Some(
        Konvertterikuvaus(
          pakollinen = true,
          konvertteriTyypit = Map(ARVOKONVERTTERI -> Arvokonvertterikuvaus(Syoteparametrityyppi.MERKKIJONO))
        ))
    ),
    Funktionimi.HAEMERKKIJONOJAVERTAAYHTASUURUUS -> Funktiokuvaus(
      tyyppi = Funktiotyyppi.TOTUUSARVOFUNKTIO,
      syoteparametrit = List(
        Syoteparametrikuvaus(avain = "oletusarvo", tyyppi = Syoteparametrityyppi.TOTUUSARVO, pakollinen = false),
        Syoteparametrikuvaus(avain = "vertailtava", tyyppi = Syoteparametrityyppi.MERKKIJONO, pakollinen = true)
      ),
      valintaperusteparametri = List(Valintaperusteparametrikuvaus("tunniste", Syoteparametrityyppi.MERKKIJONO, kuvaus = "Tunniste"))
    ),
    Funktionimi.HAETOTUUSARVO -> Funktiokuvaus(
      tyyppi = Funktiotyyppi.TOTUUSARVOFUNKTIO,
      syoteparametrit = List(
        Syoteparametrikuvaus(avain = "oletusarvo", tyyppi = Syoteparametrityyppi.TOTUUSARVO, pakollinen = false)
      ),
      valintaperusteparametri = List(Valintaperusteparametrikuvaus("tunniste", Syoteparametrityyppi.TOTUUSARVO, kuvaus = "Tunniste")),
      konvertteri = Some(
        Konvertterikuvaus(
          pakollinen = false,
          konvertteriTyypit = Map(ARVOKONVERTTERI -> Arvokonvertterikuvaus(Syoteparametrityyppi.TOTUUSARVO))
        ))
    ),
    Funktionimi.HAKUTOIVE -> Funktiokuvaus(
      tyyppi = Funktiotyyppi.TOTUUSARVOFUNKTIO,
      syoteparametrit = List(
        Syoteparametrikuvaus("n", Syoteparametrityyppi.KOKONAISLUKU))
    ),
    Funktionimi.HAKUTOIVERYHMASSA -> Funktiokuvaus(
      tyyppi = Funktiotyyppi.TOTUUSARVOFUNKTIO,
      syoteparametrit = List(
        Syoteparametrikuvaus("n", Syoteparametrityyppi.KOKONAISLUKU),
        Syoteparametrikuvaus("ryhmaoid", Syoteparametrityyppi.MERKKIJONO))
    ),
    Funktionimi.HAKUKELPOISUUS -> Funktiokuvaus(
      tyyppi = Funktiotyyppi.TOTUUSARVOFUNKTIO
    ),
    Funktionimi.HYLKAA -> Funktiokuvaus(
      tyyppi = Funktiotyyppi.LUKUARVOFUNKTIO,
      funktioargumentit = List(
        Funktioargumenttikuvaus("f", Funktiotyyppi.TOTUUSARVOFUNKTIO, Kardinaliteetti.YKSI)
      ),
      syoteparametrit = List(Syoteparametrikuvaus(avain = "hylkaysperustekuvaus_FI", tyyppi = Syoteparametrityyppi.MERKKIJONO, pakollinen = false),
        Syoteparametrikuvaus(avain = "hylkaysperustekuvaus_SV", tyyppi = Syoteparametrityyppi.MERKKIJONO, pakollinen = false),
        Syoteparametrikuvaus(avain = "hylkaysperustekuvaus_EN", tyyppi = Syoteparametrityyppi.MERKKIJONO, pakollinen = false))
    ),
    Funktionimi.HYLKAAARVOVALILLA -> Funktiokuvaus(
      tyyppi = Funktiotyyppi.LUKUARVOFUNKTIO,
      funktioargumentit = List(
        Funktioargumenttikuvaus("f", Funktiotyyppi.LUKUARVOFUNKTIO, Kardinaliteetti.YKSI)
      ),
      syoteparametrit = List(
        Syoteparametrikuvaus(avain = "hylkaysperustekuvaus_FI", tyyppi = Syoteparametrityyppi.MERKKIJONO, pakollinen = false),
        Syoteparametrikuvaus(avain = "hylkaysperustekuvaus_SV", tyyppi = Syoteparametrityyppi.MERKKIJONO, pakollinen = false),
        Syoteparametrikuvaus(avain = "hylkaysperustekuvaus_EN", tyyppi = Syoteparametrityyppi.MERKKIJONO, pakollinen = false),
        Syoteparametrikuvaus("arvovaliMin", Syoteparametrityyppi.MERKKIJONO, pakollinen = true),
        Syoteparametrikuvaus("arvovaliMax", Syoteparametrityyppi.MERKKIJONO, pakollinen = true)
      )
    ),
    Funktionimi.JA -> Funktiokuvaus(
      tyyppi = Funktiotyyppi.TOTUUSARVOFUNKTIO,
      funktioargumentit = List(Funktioargumenttikuvaus("args", Funktiotyyppi.TOTUUSARVOFUNKTIO, Kardinaliteetti.N))
    ),
    Funktionimi.JOS -> Funktiokuvaus(
      tyyppi = Funktiotyyppi.LUKUARVOFUNKTIO,
      funktioargumentit = List(Funktioargumenttikuvaus("ehto", Funktiotyyppi.TOTUUSARVOFUNKTIO),
        Funktioargumenttikuvaus("sitten", Funktiotyyppi.LUKUARVOFUNKTIO),
        Funktioargumenttikuvaus("muuten", Funktiotyyppi.LUKUARVOFUNKTIO))
    ),
    Funktionimi.KESKIARVO -> Funktiokuvaus(
      tyyppi = Funktiotyyppi.LUKUARVOFUNKTIO,
      funktioargumentit = List(Funktioargumenttikuvaus("args", Funktiotyyppi.LUKUARVOFUNKTIO, Kardinaliteetti.N))
    ),
    Funktionimi.KESKIARVONPARASTA -> Funktiokuvaus(
      tyyppi = Funktiotyyppi.LUKUARVOFUNKTIO,
      funktioargumentit = List(Funktioargumenttikuvaus("args", Funktiotyyppi.LUKUARVOFUNKTIO, Kardinaliteetti.N)),
      syoteparametrit = List(Syoteparametrikuvaus("n", Syoteparametrityyppi.KOKONAISLUKU))
    ),
    Funktionimi.KONVERTOILUKUARVO -> Funktiokuvaus(
      tyyppi = Funktiotyyppi.LUKUARVOFUNKTIO,
      funktioargumentit = List(Funktioargumenttikuvaus("f", Funktiotyyppi.LUKUARVOFUNKTIO)),
      konvertteri = Some(
        Konvertterikuvaus(
          pakollinen = true,
          konvertteriTyypit = Map(ARVOKONVERTTERI -> Arvokonvertterikuvaus(Syoteparametrityyppi.DESIMAALILUKU),
            ARVOVALIKONVERTTERI -> Arvovalikonvertterikuvaus)
        ))
    ),
    Funktionimi.LUKUARVO -> Funktiokuvaus(
      tyyppi = Funktiotyyppi.LUKUARVOFUNKTIO,
      syoteparametrit = List(Syoteparametrikuvaus("luku", Syoteparametrityyppi.DESIMAALILUKU))
    )
    ,
    Funktionimi.MAKSIMI -> Funktiokuvaus(
      tyyppi = Funktiotyyppi.LUKUARVOFUNKTIO,
      funktioargumentit = List(Funktioargumenttikuvaus("args", Funktiotyyppi.LUKUARVOFUNKTIO, Kardinaliteetti.N))
    ),
    Funktionimi.MEDIAANI -> Funktiokuvaus(
      tyyppi = Funktiotyyppi.LUKUARVOFUNKTIO,
      funktioargumentit = List(Funktioargumenttikuvaus("args", Funktiotyyppi.LUKUARVOFUNKTIO, Kardinaliteetti.N))
    ),
    Funktionimi.MINIMI -> Funktiokuvaus(
      tyyppi = Funktiotyyppi.LUKUARVOFUNKTIO,
      funktioargumentit = List(Funktioargumenttikuvaus("args", Funktiotyyppi.LUKUARVOFUNKTIO, Kardinaliteetti.N))
    ),
    Funktionimi.NEGAATIO -> Funktiokuvaus(
      tyyppi = Funktiotyyppi.LUKUARVOFUNKTIO,
      funktioargumentit = List(Funktioargumenttikuvaus("f", Funktiotyyppi.LUKUARVOFUNKTIO))
    ),
    Funktionimi.NIMETTYLUKUARVO -> Funktiokuvaus(
      tyyppi = Funktiotyyppi.LUKUARVOFUNKTIO,
      funktioargumentit = List(Funktioargumenttikuvaus("f", Funktiotyyppi.LUKUARVOFUNKTIO)),
      syoteparametrit = List(Syoteparametrikuvaus("nimi", Syoteparametrityyppi.MERKKIJONO))
    ),
    Funktionimi.NIMETTYTOTUUSARVO -> Funktiokuvaus(
      tyyppi = Funktiotyyppi.TOTUUSARVOFUNKTIO,
      funktioargumentit = List(Funktioargumenttikuvaus("f", Funktiotyyppi.TOTUUSARVOFUNKTIO)),
      syoteparametrit = List(Syoteparametrikuvaus("nimi", Syoteparametrityyppi.MERKKIJONO))
    ),
    Funktionimi.NMAKSIMI -> Funktiokuvaus(
      tyyppi = Funktiotyyppi.LUKUARVOFUNKTIO,
      funktioargumentit = List(Funktioargumenttikuvaus("args", Funktiotyyppi.LUKUARVOFUNKTIO, Kardinaliteetti.N)),
      syoteparametrit = List(Syoteparametrikuvaus("n", Syoteparametrityyppi.KOKONAISLUKU))
    ),
    Funktionimi.NMINIMI -> Funktiokuvaus(
      tyyppi = Funktiotyyppi.LUKUARVOFUNKTIO,
      funktioargumentit = List(Funktioargumenttikuvaus("args", Funktiotyyppi.LUKUARVOFUNKTIO, Kardinaliteetti.N)),
      syoteparametrit = List(Syoteparametrikuvaus("n", Syoteparametrityyppi.KOKONAISLUKU))
    ),
    Funktionimi.OSAMAARA -> Funktiokuvaus(
      tyyppi = Funktiotyyppi.LUKUARVOFUNKTIO,
      funktioargumentit = List(Funktioargumenttikuvaus("osoittaja", Funktiotyyppi.LUKUARVOFUNKTIO),
        Funktioargumenttikuvaus("nimittaja", Funktiotyyppi.LUKUARVOFUNKTIO))
    ),
    Funktionimi.PAINOTETTUKESKIARVO -> Funktiokuvaus(
      tyyppi = Funktiotyyppi.LUKUARVOFUNKTIO,
      funktioargumentit = List(Funktioargumenttikuvaus("args", Funktiotyyppi.LUKUARVOFUNKTIO, Kardinaliteetti.LISTA_PAREJA))
    ),
    Funktionimi.PIENEMPI -> Funktiokuvaus(
      tyyppi = Funktiotyyppi.TOTUUSARVOFUNKTIO,
      funktioargumentit = List(Funktioargumenttikuvaus("vasenOperandi", Funktiotyyppi.LUKUARVOFUNKTIO),
        Funktioargumenttikuvaus("oikeaOperandi", Funktiotyyppi.LUKUARVOFUNKTIO))
    ),
    Funktionimi.PIENEMPITAIYHTASUURI -> Funktiokuvaus(
      tyyppi = Funktiotyyppi.TOTUUSARVOFUNKTIO,
      funktioargumentit = List(Funktioargumenttikuvaus("vasenOperandi", Funktiotyyppi.LUKUARVOFUNKTIO),
        Funktioargumenttikuvaus("oikeaOperandi", Funktiotyyppi.LUKUARVOFUNKTIO))
    ),

    Funktionimi.PYORISTYS -> Funktiokuvaus(
      tyyppi = Funktiotyyppi.LUKUARVOFUNKTIO,
      funktioargumentit = List(Funktioargumenttikuvaus("f", Funktiotyyppi.LUKUARVOFUNKTIO)),
      syoteparametrit = List(Syoteparametrikuvaus("tarkkuus", Syoteparametrityyppi.KOKONAISLUKU))
    ),
    Funktionimi.SKAALAUS -> Funktiokuvaus(
      tyyppi = Funktiotyyppi.LUKUARVOFUNKTIO,
      funktioargumentit = List(Funktioargumenttikuvaus("f", Funktiotyyppi.LUKUARVOFUNKTIO, Kardinaliteetti.YKSI)),
      syoteparametrit = List(
        Syoteparametrikuvaus("kohdeskaalaMin", Syoteparametrityyppi.DESIMAALILUKU, pakollinen = true),
        Syoteparametrikuvaus("kohdeskaalaMax", Syoteparametrityyppi.DESIMAALILUKU, pakollinen = true),
        Syoteparametrikuvaus("lahdeskaalaMin", Syoteparametrityyppi.DESIMAALILUKU, pakollinen = false),
        Syoteparametrikuvaus("lahdeskaalaMax", Syoteparametrityyppi.DESIMAALILUKU, pakollinen = false),
        Syoteparametrikuvaus("kaytaLaskennallistaLahdeskaalaa", Syoteparametrityyppi.TOTUUSARVO, pakollinen = true)
      )
    ),
    Funktionimi.SUMMA -> Funktiokuvaus(
      tyyppi = Funktiotyyppi.LUKUARVOFUNKTIO,
      funktioargumentit = List(Funktioargumenttikuvaus("args", Funktiotyyppi.LUKUARVOFUNKTIO, Kardinaliteetti.N))
    ),
    Funktionimi.SUMMANPARASTA -> Funktiokuvaus(
      tyyppi = Funktiotyyppi.LUKUARVOFUNKTIO,
      funktioargumentit = List(Funktioargumenttikuvaus("args", Funktiotyyppi.LUKUARVOFUNKTIO, Kardinaliteetti.N)),
      syoteparametrit = List(Syoteparametrikuvaus("n", Syoteparametrityyppi.KOKONAISLUKU))
    ),
    Funktionimi.TULONPARASTA -> Funktiokuvaus(
      tyyppi = Funktiotyyppi.LUKUARVOFUNKTIO,
      funktioargumentit = List(Funktioargumenttikuvaus("args", Funktiotyyppi.LUKUARVOFUNKTIO, Kardinaliteetti.N)),
      syoteparametrit = List(Syoteparametrikuvaus("n", Syoteparametrityyppi.KOKONAISLUKU))
    ),
    Funktionimi.SUUREMPI -> Funktiokuvaus(
      tyyppi = Funktiotyyppi.TOTUUSARVOFUNKTIO,
      funktioargumentit = List(Funktioargumenttikuvaus("vasenOperandi", Funktiotyyppi.LUKUARVOFUNKTIO),
        Funktioargumenttikuvaus("oikeaOperandi", Funktiotyyppi.LUKUARVOFUNKTIO))
    ),
    Funktionimi.SUUREMPITAIYHTASUURI -> Funktiokuvaus(
      tyyppi = Funktiotyyppi.TOTUUSARVOFUNKTIO,
      funktioargumentit = List(Funktioargumenttikuvaus("vasenOperandi", Funktiotyyppi.LUKUARVOFUNKTIO),
        Funktioargumenttikuvaus("oikeaOperandi", Funktiotyyppi.LUKUARVOFUNKTIO))
    ),
    Funktionimi.TAI -> Funktiokuvaus(
      tyyppi = Funktiotyyppi.TOTUUSARVOFUNKTIO,
      funktioargumentit = List(Funktioargumenttikuvaus("args", Funktiotyyppi.TOTUUSARVOFUNKTIO, Kardinaliteetti.N))
    ),
    Funktionimi.TOTUUSARVO -> Funktiokuvaus(
      tyyppi = Funktiotyyppi.TOTUUSARVOFUNKTIO,
      syoteparametrit = List(Syoteparametrikuvaus("totuusarvo", Syoteparametrityyppi.TOTUUSARVO))
    ),
    Funktionimi.TULO -> Funktiokuvaus(
      tyyppi = Funktiotyyppi.LUKUARVOFUNKTIO,
      funktioargumentit = List(Funktioargumenttikuvaus("args", Funktiotyyppi.LUKUARVOFUNKTIO, Kardinaliteetti.N))
    ),
    Funktionimi.YHTASUURI -> Funktiokuvaus(
      tyyppi = Funktiotyyppi.TOTUUSARVOFUNKTIO,
      funktioargumentit = List(Funktioargumenttikuvaus("vasenOperandi", Funktiotyyppi.LUKUARVOFUNKTIO),
        Funktioargumenttikuvaus("oikeaOperandi", Funktiotyyppi.LUKUARVOFUNKTIO))
    ),
    Funktionimi.VALINTAPERUSTEYHTASUURUUS -> Funktiokuvaus(
      tyyppi = Funktiotyyppi.TOTUUSARVOFUNKTIO,
      valintaperusteparametri = List(
        Valintaperusteparametrikuvaus(nimi = "tunniste1", tyyppi = MERKKIJONO, kuvaus = "Ensimmäinen valintaperuste"),
        Valintaperusteparametrikuvaus(nimi = "tunniste2", tyyppi = MERKKIJONO, kuvaus = "Toinen valintaperuste")
      )
    )
  )
}
