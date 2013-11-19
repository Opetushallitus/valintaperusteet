package fi.vm.sade.kaava

import fi.vm.sade.service.valintaperusteet.model.Funktionimi
import play.api.libs.json.{JsArray, Json}
import scala.Predef._
import scala.Some

/**
 * User: kwuoti
 * Date: 16.1.2013
 * Time: 15.25
 */
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
    val MERKKIJONO = Value("MERKKIJONO")
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
  import Syoteparametrityyppi._
  import Kardinaliteetti._
  import Konvertterinimi._

  trait KonvertteriTyyppi {
    val nimi: Konvertterinimi.Konvertterinimi
  }

  object Arvovalikonvertterikuvaus extends KonvertteriTyyppi {
    override val nimi = ARVOVALIKONVERTTERI
  }

  case class Arvokonvertterikuvaus(arvotyyppi: Syoteparametrityyppi) extends KonvertteriTyyppi {
    override val nimi = ARVOKONVERTTERI
  }

  case class Konvertterikuvaus(pakollinen: Boolean,
                               konvertteriTyypit: Map[Konvertterinimi.Konvertterinimi, KonvertteriTyyppi])

  case class Valintaperusteparametrikuvaus(nimi: String, tyyppi: Syoteparametrityyppi)

  case class Syoteparametrikuvaus(avain: String, tyyppi: Syoteparametrityyppi, pakollinen: Boolean = true)

  case class Funktioargumenttikuvaus(nimi: String, tyyppi: Funktiotyyppi,
                                     kardinaliteetti: Kardinaliteetti = Kardinaliteetti.YKSI)


  case class Funktiokuvaus(tyyppi: Funktiotyyppi,
                           funktioargumentit: Seq[Funktioargumenttikuvaus] = Nil,
                           syoteparametrit: Seq[Syoteparametrikuvaus] = Nil,
                           valintaperusteparametri: Seq[Valintaperusteparametrikuvaus] = Nil,
                           konvertteri: Option[Konvertterikuvaus] = None)

  def annaFunktiokuvauksetAsJson = {
    //funktiokuvaukset.map(annaFunktiokuvausAsJson(_))

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
      fk.funktioargumentit.map(funktioargumenttiAsJson(_)))

    val parametrit = if (fk.syoteparametrit.isEmpty) Json.obj()
    else Json.obj("syoteparametrit" ->
      fk.syoteparametrit.map(funktioparametriAsJson(_)))

    val valintaperuste = if (fk.valintaperusteparametri.isEmpty) Json.obj()
    else Json.obj("valintaperuste" -> fk.valintaperusteparametri.map(valintaperusteparametriAsJson(_)))

    val konvertteri = fk.konvertteri match {
      case Some(konv) => Json.obj("konvertteri" -> konvertterikuvausAsJson(konv))
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
    Funktionimi.HAELUKUARVO -> Funktiokuvaus(
      tyyppi = Funktiotyyppi.LUKUARVOFUNKTIO,
      syoteparametrit = List(
        Syoteparametrikuvaus(avain = "oletusarvo", tyyppi = Syoteparametrityyppi.DESIMAALILUKU, pakollinen = false)
      ),
      valintaperusteparametri = List(Valintaperusteparametrikuvaus("tunniste", Syoteparametrityyppi.DESIMAALILUKU)),
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
      valintaperusteparametri = List(Valintaperusteparametrikuvaus("tunniste", Syoteparametrityyppi.MERKKIJONO)),
      konvertteri = Some(
        Konvertterikuvaus(
          pakollinen = true,
          konvertteriTyypit = Map(ARVOKONVERTTERI -> Arvokonvertterikuvaus(Syoteparametrityyppi.MERKKIJONO))
        ))
    ),
    Funktionimi.HAEMERKKIJONOJAKONVERTOITOTUUSARVOKSI -> Funktiokuvaus(
      tyyppi = Funktiotyyppi.TOTUUSARVOFUNKTIO,
      syoteparametrit = List(
        Syoteparametrikuvaus(avain = "oletusarvo", tyyppi = Syoteparametrityyppi.TOTUUSARVO, pakollinen = false)
      ),
      valintaperusteparametri = List(Valintaperusteparametrikuvaus("tunniste", Syoteparametrityyppi.MERKKIJONO)),
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
      valintaperusteparametri = List(Valintaperusteparametrikuvaus("tunniste", Syoteparametrityyppi.MERKKIJONO))
    ),
    Funktionimi.HAETOTUUSARVO -> Funktiokuvaus(
      tyyppi = Funktiotyyppi.TOTUUSARVOFUNKTIO,
      syoteparametrit = List(
        Syoteparametrikuvaus(avain = "oletusarvo", tyyppi = Syoteparametrityyppi.TOTUUSARVO, pakollinen = false)
      ),
      valintaperusteparametri = List(Valintaperusteparametrikuvaus("tunniste", Syoteparametrityyppi.TOTUUSARVO)),
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
    Funktionimi.HYLKAA -> Funktiokuvaus(
      tyyppi = Funktiotyyppi.LUKUARVOFUNKTIO,
      funktioargumentit = List(
        Funktioargumenttikuvaus("f", Funktiotyyppi.TOTUUSARVOFUNKTIO, Kardinaliteetti.YKSI)
      ),
      syoteparametrit = List(Syoteparametrikuvaus(avain = "hylkaysperustekuvaus", tyyppi = Syoteparametrityyppi.MERKKIJONO, pakollinen = false))
    ),
    Funktionimi.HYLKAAARVOVALILLA -> Funktiokuvaus(
      tyyppi = Funktiotyyppi.LUKUARVOFUNKTIO,
      funktioargumentit = List(
        Funktioargumenttikuvaus("f", Funktiotyyppi.LUKUARVOFUNKTIO, Kardinaliteetti.YKSI)
      ),
      syoteparametrit = List(
        Syoteparametrikuvaus(avain = "hylkaysperustekuvaus", tyyppi = Syoteparametrityyppi.MERKKIJONO, pakollinen = false),
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
        Valintaperusteparametrikuvaus(nimi = "tunniste1", tyyppi = MERKKIJONO),
        Valintaperusteparametrikuvaus(nimi = "tunniste2", tyyppi = MERKKIJONO)
      )
    )
  )

  private def konvertteriTyyppiAsJson(tyyppi: KonvertteriTyyppi) = {
    tyyppi match {
      case Arvokonvertterikuvaus(arvotyyppi) => {
        Json.obj("tyyppi" -> tyyppi.nimi.toString, "arvotyyppi" -> arvotyyppi.toString)
      }
      case _ => Json.obj("tyyppi" -> tyyppi.nimi.toString)
    }
  }

  private def konvertterikuvausAsJson(konvertterikuvaus: Konvertterikuvaus) = {

    Json.obj("pakollinen" -> konvertterikuvaus.pakollinen,
      "konvertteriTyypit" -> konvertterikuvaus.konvertteriTyypit.map(t => konvertteriTyyppiAsJson(t._2)))
  }

  private def valintaperusteparametriAsJson(vp: Valintaperusteparametrikuvaus) = {
    Json.obj(
      "nimi" -> vp.nimi,
      "tyyppi" -> vp.tyyppi.toString)
  }


  private def funktioparametriAsJson(par: Syoteparametrikuvaus) = {
    Json.obj(
      "avain" -> par.avain,
      "tyyppi" -> par.tyyppi.toString,
      "pakollinen" -> par.pakollinen.toString
    )
  }

  private def kardinaliteettiAsJson(kardinaliteetti: Kardinaliteetti): String = {
    kardinaliteetti match {
      case Kardinaliteetti.N => "n"
      case Kardinaliteetti.YKSI => "1"
      case Kardinaliteetti.LISTA_PAREJA => "lista_pareja"
    }
  }

  private def funktioargumenttiAsJson(arg: Funktioargumenttikuvaus) = {
    Json.obj(
      "nimi" -> arg.nimi,
      "tyyppi" -> arg.tyyppi.toString,
      "kardinaliteetti" -> kardinaliteettiAsJson(arg.kardinaliteetti)
    )
  }
}
