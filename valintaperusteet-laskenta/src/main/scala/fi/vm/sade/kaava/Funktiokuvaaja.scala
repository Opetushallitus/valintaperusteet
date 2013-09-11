package fi.vm.sade.kaava

import fi.vm.sade.service.valintaperusteet.model.Funktionimi
import com.codahale.jerkson.Json

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
                           valintaperusteparametri: Option[Valintaperusteparametrikuvaus] = None,
                           konvertteri: Option[Konvertterikuvaus] = None)

  def annaFunktiokuvauksetAsJson = {
    Json.generate(funktiokuvaukset.map(annaFunktiokuvausAsJson(_)))
  }

  def annaFunktiokuvaukset = {
    funktiokuvaukset
  }


  def annaFunktiokuvaus(nimi: String): (Funktionimi, Funktiokuvaus) = {
    Funktionimi.values().filter(_.name() == nimi.toUpperCase()).toList match {
      case Nil => throw new RuntimeException("No funktio with name " + nimi.toUpperCase())
      case head :: tail => {
        annaFunktiokuvaus(head)
      }
    }
  }

  def annaFunktiokuvaus(nimi: Funktionimi) = {
    if (funktiokuvaukset.contains(nimi)) {
      (nimi, funktiokuvaukset(nimi))
    } else {
      throw new RuntimeException("No funktiokuvaus defined for funktio " + nimi.name())
    }
  }

  def annaFunktiokuvausAsJson(nimi: String): String = {
    Json.generate(annaFunktiokuvausAsJson(annaFunktiokuvaus(nimi)))
  }

  private def annaFunktiokuvausAsJson(kutsu: (Funktionimi, Funktiokuvaus)) = {

    val fk = kutsu._2

    val argumentit = if (fk.funktioargumentit.isEmpty) Nil
    else Seq("funktioargumentit" ->
      fk.funktioargumentit.map(funktioargumenttiAsJson(_)))

    val parametrit = if (fk.syoteparametrit.isEmpty) Nil
    else Seq("syoteparametrit" ->
      fk.syoteparametrit.map(funktioparametriAsJson(_)))

    val valintaperuste = fk.valintaperusteparametri match {
      case Some(vp) => Seq("valintaperuste" -> valintaperusteparametriAsJson(vp))
      case None => Nil
    }

    val konvertteri = fk.konvertteri match {
      case Some(konv) => Seq("konvertteri" -> konvertterikuvausAsJson(konv))
      case None => Nil
    }


    val seq = Seq(
      "nimi" -> kutsu._1.name(),
      "tyyppi" -> fk.tyyppi.toString) ++ argumentit ++ parametrit ++ valintaperuste ++ konvertteri

    Map(seq: _*)
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
      valintaperusteparametri = Some(Valintaperusteparametrikuvaus("tunniste", Syoteparametrityyppi.DESIMAALILUKU)),
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
      valintaperusteparametri = Some(Valintaperusteparametrikuvaus("tunniste", Syoteparametrityyppi.MERKKIJONO)),
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
      valintaperusteparametri = Some(Valintaperusteparametrikuvaus("tunniste", Syoteparametrityyppi.MERKKIJONO)),
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
      valintaperusteparametri = Some(Valintaperusteparametrikuvaus("tunniste", Syoteparametrityyppi.MERKKIJONO))
    ),
    Funktionimi.HAETOTUUSARVO -> Funktiokuvaus(
      tyyppi = Funktiotyyppi.TOTUUSARVOFUNKTIO,
      syoteparametrit = List(
        Syoteparametrikuvaus(avain = "oletusarvo", tyyppi = Syoteparametrityyppi.TOTUUSARVO, pakollinen = false)
      ),
      valintaperusteparametri = Some(Valintaperusteparametrikuvaus("tunniste", Syoteparametrityyppi.TOTUUSARVO)),
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
    )
  )

  private def konvertteriTyyppiAsJson(tyyppi: KonvertteriTyyppi) = {
    val seq = tyyppi match {
      case Arvokonvertterikuvaus(arvotyyppi) => {
        Seq("tyyppi" -> tyyppi.nimi.toString, "arvotyyppi" -> arvotyyppi.toString)
      }
      case _ => Seq("tyyppi" -> tyyppi.nimi.toString)
    }

    Map(seq: _*)
  }

  private def konvertterikuvausAsJson(konvertterikuvaus: Konvertterikuvaus) = {

    val seq = Seq("pakollinen" -> konvertterikuvaus.pakollinen,
      "konvertteriTyypit" -> konvertterikuvaus.konvertteriTyypit.map(t => konvertteriTyyppiAsJson(t._2)))

    Map(seq: _*)
  }

  private def valintaperusteparametriAsJson(vp: Valintaperusteparametrikuvaus) = {
    val seq = Seq(
      "nimi" -> vp.nimi,
      "tyyppi" -> vp.tyyppi.toString)

    Map(seq: _*)
  }


  private def funktioparametriAsJson(par: Syoteparametrikuvaus) = {
    val seq = Seq(
      "avain" -> par.avain,
      "tyyppi" -> par.tyyppi.toString,
      "pakollinen" -> par.pakollinen.toString
    )

    Map(seq: _*)
  }

  private def kardinaliteettiAsJson(kardinaliteetti: Kardinaliteetti): String = {
    kardinaliteetti match {
      case Kardinaliteetti.N => "n"
      case Kardinaliteetti.YKSI => "1"
    }
  }

  private def funktioargumenttiAsJson(arg: Funktioargumenttikuvaus) = {
    val seq = Seq(
      "nimi" -> arg.nimi,
      "tyyppi" -> arg.tyyppi.toString,
      "kardinaliteetti" -> kardinaliteettiAsJson(arg.kardinaliteetti)
    )

    Map(seq: _*)
  }
}
