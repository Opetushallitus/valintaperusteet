package fi.vm.sade.kaava

import fi.vm.sade.service.valintaperusteet.model._
import Funktiokuvaaja._
import fi.vm.sade.service.valintaperusteet.service.validointi.virhe._
import org.apache.commons.lang.StringUtils
import fi.vm.sade.service.valintaperusteet.model.Funktiotyyppi
import scala.Some
import Funktiokuvaaja.Syoteparametrikuvaus

/**
 * User: kwuoti
 * Date: 31.1.2013
 * Time: 9.32
 */
object Laskentakaavavalidaattori {

  import scala.collection.JavaConversions._

  private def tryConvertString(s: String, f: (String => Unit)) = {
    try {
      f(s)
      true
    } catch {
      case e: NumberFormatException => false
    }
  }

  private def muutaMerkkijonoParametrityypiksi(s: String,
                                               tyyppi: Syoteparametrityyppi.Syoteparametrityyppi,
                                               virheviesti: String) = {
    val konv: (String => Unit) = tyyppi match {
      case Syoteparametrityyppi.DESIMAALILUKU => (mj: String) => mj.toDouble
      case Syoteparametrityyppi.KOKONAISLUKU => (mj: String) => mj.toInt
      case Syoteparametrityyppi.TOTUUSARVO => (mj: String) => mj.toBoolean
      case Syoteparametrityyppi.MERKKIJONO => (mj: String) => Unit
    }

    if (!tryConvertString(s, konv)) Some(virheviesti) else None
  }

  private def tarkistaParametriarvo(funktiokutsu: Funktiokutsu, annettuParametri: Syoteparametri,
                                    vaadittuParametri: Syoteparametrikuvaus,
                                    virheet: List[Validointivirhe]): List[Validointivirhe] = {
    if (StringUtils.isBlank(annettuParametri.getArvo)) {
      new TyhjaSyoteparametrinArvoVirhe("Parametrin (avain " + annettuParametri.getAvain
        + ") arvo on tyhjä", annettuParametri.getAvain) :: virheet
    } else {
      val nimi = funktiokutsu.getFunktionimi.name()

      val virheviesti = vaadittuParametri.tyyppi match {
        case Syoteparametrityyppi.DESIMAALILUKU => ("Parametrin (avain "
          + annettuParametri.getAvain + ") arvoa " + annettuParametri.getArvo + " ei voida konvertoida " +
          "Double-tyyppiseksi funktiolle " + nimi)
        case Syoteparametrityyppi.KOKONAISLUKU => ("Parametrin (avain "
          + annettuParametri.getAvain + ") arvoa " + annettuParametri.getArvo + " ei voida konvertoida " +
          "Integer-tyyppiseksi funktiolle " + nimi)
        case Syoteparametrityyppi.TOTUUSARVO => ("Parametrin (avain "
          + annettuParametri.getAvain + ") arvoa " + annettuParametri.getArvo + " ei voida konvertoida "
          + "Boolean-tyyppiseksi funktiolle " + nimi)
        case Syoteparametrityyppi.MERKKIJONO => ""
      }

      muutaMerkkijonoParametrityypiksi(annettuParametri.getArvo, vaadittuParametri.tyyppi, virheviesti) match {
        case Some(virheviesti) => new VirheellinenSyoteParametrinTyyppiVirhe(virheviesti, annettuParametri.getAvain,
          vaadittuParametri.tyyppi.toString) :: virheet
        case None => virheet
      }
    }
  }

  private def tarkistaParametrit(funktiokutsu: Funktiokutsu, virheet: List[Validointivirhe]): List[Validointivirhe] = {
    val funktiokuvaus = Funktiokuvaaja.annaFunktiokuvaus(funktiokutsu.getFunktionimi)._2

    val annetutParametrit = funktiokutsu.getSyoteparametrit.toList
    val vaaditutParametrit = funktiokuvaus.syoteparametrit

    val pakollisetVirheet = vaaditutParametrit.filter(_.pakollinen).foldLeft(virheet)((l, p) => {
      annetutParametrit.find(_.getAvain == p.avain) match {
        case None => new SyoteparametriPuuttuuVirhe("Avainta " + p.avain + " vastaavaa parametria ei ole " +
          "olemassa funktiolle " + funktiokutsu.getFunktionimi.name(), p.avain) :: l
        case Some(param) => tarkistaParametriarvo(funktiokutsu, param, p, l)
      }
    })

    vaaditutParametrit.filter(!_.pakollinen).foldLeft(pakollisetVirheet)((l, p) => {
      annetutParametrit.find(_.getAvain == p.avain) match {
        case None => l
        case Some(param) => tarkistaParametriarvo(funktiokutsu, param, p, l)
      }
    })
  }

  private def tarkistaFunktioargumentit(funktiokutsu: Funktiokutsu,
                                        validoiLaskettava: Boolean,
                                        virheet: List[Validointivirhe]): List[Validointivirhe] = {

    val funktiokuvaus = Funktiokuvaaja.annaFunktiokuvaus(funktiokutsu.getFunktionimi)._2
    val nimi = funktiokutsu.getFunktionimi.name()

    def validoiFunktioargumentti(vaadittuArgumentti: Funktiokuvaaja.Funktioargumenttikuvaus,
                                 argumentti: Funktioargumentti, accum: List[Validointivirhe]): List[Validointivirhe] = {

      if (validoiLaskettava && argumentti.getFunktiokutsuChild == null) {
        new FunktiokutsuaEiOleMaariteltyFunktioargumentilleVirhe("Funktiokutsua ei ole annettu funktiokutsun "
          + nimi + " funktioargumentille, indeksi " + argumentti.getIndeksi, argumentti.getIndeksi) :: accum
      } else if (!validoiLaskettava && argumentti.getFunktiokutsuChild == null && argumentti.getLaskentakaavaChild == null) {
        new FunktioargumenttiaEiMaariteltyVirhe("Funktiokutsua tai laskentakaavaa ei ole määritelty " +
          "funktiokutsun " + nimi + " funktioargumentille, indeksi " + argumentti.getIndeksi, argumentti.getIndeksi) :: accum
      } else {
        if (argumentti.getFunktiokutsuChild != null
          && vaadittuArgumentti.tyyppi.toString != argumentti.getFunktiokutsuChild.getFunktionimi.getTyyppi.name()) {
          new VirheellinenFunktioargumentinTyyppiVirhe(
            "Funktion " + nimi + " funktioargumentti on väärää tyyppiä. Vaadittu: "
              + vaadittuArgumentti.tyyppi.toString + ", annettu: "
              + argumentti.getFunktiokutsuChild.getFunktionimi.getTyyppi.name(), argumentti.getIndeksi,
            vaadittuArgumentti.tyyppi.toString, argumentti.getFunktiokutsuChild.getFunktionimi.getTyyppi.name()) :: accum
        } else if (argumentti.getLaskentakaavaChild != null) {
          val vv = if (vaadittuArgumentti.tyyppi.toString != argumentti.getLaskentakaavaChild.getTyyppi.name()) {
            new VirheellinenFunktioargumentinTyyppiVirhe(
              "Funktion " + nimi + " funktioargumentti on väärää tyyppiä. Vaadittu: "
                + vaadittuArgumentti.tyyppi.toString + ", annettu: "
                + argumentti.getLaskentakaavaChild.getTyyppi.name(), argumentti.getIndeksi,
              vaadittuArgumentti.tyyppi.toString, argumentti.getFunktiokutsuChild.getFunktionimi.getTyyppi.name()) :: accum
          } else accum

          if (argumentti.getLaskentakaavaChild.getOnLuonnos) {
            new FunktioargumentinLaskentakaavaOnLuonnosVirhe("Funktion " + nimi + " funktioargumentille määritelty " +
              "laskentakaava on luonnos-tilassa", argumentti.getIndeksi) :: vv
          } else vv
        } else accum
      }
    }

    val annetutArgumentit = LaskentaUtil.jarjestaFunktioargumentit(funktiokutsu.getFunktioargumentit)
    val vaaditutArgumentit = funktiokuvaus.funktioargumentit

    if (vaaditutArgumentit.isEmpty && !annetutArgumentit.isEmpty) {
      new Validointivirhe(Virhetyyppi.FUNKTIOKUTSU_EI_OTA_FUNKTIOARGUMENTTEJA,
        "Funktio " + nimi + " ei ota funktioargumentteja") :: virheet
    } else {
      if (vaaditutArgumentit.size == 1 && vaaditutArgumentit(0).kardinaliteetti == Kardinaliteetti.N) {
        val arg = vaaditutArgumentit(0)
        if (annetutArgumentit.isEmpty) {
          new VaaraMaaraFunktioargumenttejaVirhe(
            "Väärä määrä funktioargumentteja funktiolle " + nimi
              + ". Vaadittu: ainakin yksi, annettu: " + annetutArgumentit.size,
            ">=1", annetutArgumentit.size) :: virheet
        } else funktiokutsu.getFunktioargumentit.foldLeft(virheet)((l, a) => validoiFunktioargumentti(arg, a, l))
      } else {
        if (vaaditutArgumentit.size != annetutArgumentit.size) {
          new VaaraMaaraFunktioargumenttejaVirhe(
            "Väärä määrä funktioargumentteja funktiolle " + nimi +
              ". Vaadittu: " + vaaditutArgumentit.size + ", annettu: " + annetutArgumentit.size,
            vaaditutArgumentit.size.toString, annetutArgumentit.size) :: virheet
        } else {
          def tarkistaFunktioargumentit(annetutArgumentit: List[Funktioargumentti],
                                        vaaditutArgumentit: List[Funktiokuvaaja.Funktioargumenttikuvaus],
                                        accum: List[Validointivirhe]): List[Validointivirhe] = {
            if (!annetutArgumentit.isEmpty) {
              tarkistaFunktioargumentit(annetutArgumentit.tail, vaaditutArgumentit.tail,
                validoiFunktioargumentti(vaaditutArgumentit.head, annetutArgumentit.head, accum))
            } else accum
          }

          tarkistaFunktioargumentit(annetutArgumentit, vaaditutArgumentit.toList, virheet)
        }
      }
    }
  }

  private def tarkistaValintaperusteparametrit(funktiokutsu: Funktiokutsu,
                                               virheet: List[Validointivirhe]): List[Validointivirhe] = {
    val funktiokuvaus = Funktiokuvaaja.annaFunktiokuvaus(funktiokutsu.getFunktionimi)._2
    val nimi = funktiokutsu.getFunktionimi.name()

    funktiokuvaus.valintaperusteparametri match {
      case None => {
        if (funktiokutsu.getValintaperuste != null) {
          new Validointivirhe(Virhetyyppi.FUNKTIOKUTSU_EI_OTA_VALINTAPERUSTEPARAMETRIA,
            "Funktio " + nimi + " ei ota valintaperusteparametreja") :: virheet
        } else virheet
      }

      case Some(param) => {
        if (funktiokutsu.getValintaperuste == null) {
          new Validointivirhe(Virhetyyppi.VALINTAPERUSTEPARAMETRI_PUUTTUUU,
            "Valintaperusteparametri puuttuu funktiolle " + nimi) :: virheet
        } else if (StringUtils.isBlank(funktiokutsu.getValintaperuste.getTunniste)) {
          new Validointivirhe(Virhetyyppi.VALINTAPERUSTEPARAMETRIN_TUNNISTE_PUUTTUU, "Valintaperusteparametrin tunniste puuttuu " +
            "funktiolle " + nimi) :: virheet
        } else virheet
      }
    }
  }

  private def tarkistaKonvertteri(funktiokutsu: Funktiokutsu,
                                  virheet: List[Validointivirhe]): List[Validointivirhe] = {
    import Funktiokuvaaja.Konvertterinimi._

    val funktiokuvaus = Funktiokuvaaja.annaFunktiokuvaus(funktiokutsu.getFunktionimi)._2
    val nimi = funktiokutsu.getFunktionimi.name()


    funktiokuvaus.konvertteri match {
      case None => {
        if (!funktiokutsu.getArvokonvertteriparametrit.isEmpty
          || !funktiokutsu.getArvovalikonvertteriparametrit.isEmpty) {
          new Validointivirhe(Virhetyyppi.FUNKTIOKUTSU_EI_OTA_KONVERTTERIPARAMETREJA, "Funktio "
            + nimi + " ei ota konvertteriparametreja") :: virheet
        } else virheet
      }
      case Some(param) => {
        if (param.pakollinen &&
          ((param.konvertteriTyypit.containsKey(ARVOKONVERTTERI)
            && param.konvertteriTyypit.containsKey(ARVOVALIKONVERTTERI)
            && funktiokutsu.getArvokonvertteriparametrit.isEmpty
            && funktiokutsu.getArvovalikonvertteriparametrit.isEmpty) ||
            (param.konvertteriTyypit.containsKey(ARVOKONVERTTERI) &&
              !param.konvertteriTyypit.containsKey(ARVOVALIKONVERTTERI) &&
              funktiokutsu.getArvokonvertteriparametrit.isEmpty) ||
            (param.konvertteriTyypit.containsKey(ARVOVALIKONVERTTERI) &&
              !param.konvertteriTyypit.containsKey(ARVOKONVERTTERI) &&
              funktiokutsu.getArvovalikonvertteriparametrit.isEmpty))
        ) {
          new Validointivirhe(Virhetyyppi.EI_KONVERTTERIPARAMETREJA_MAARITELTY, "Vaadittuja " +
            "konvertteriparametreja ei ole määritelty funktiolle " + nimi) :: virheet
        } else {
          def validoiKonvertteriparametri(indeksi: Int, konv: Konvertteriparametri,
                                          accum: List[Validointivirhe]): List[Validointivirhe] = {
            val paluuarvoPuuttuu = konv match {
              case av: Arvovalikonvertteriparametri => {
                !av.getPalautaHaettuArvo && StringUtils.isBlank(av.getPaluuarvo)
              }
              case _ => StringUtils.isBlank(konv.getPaluuarvo)
            }

            if (paluuarvoPuuttuu) {
              new KonvertteriparametrinPaluuarvoPuuttuuVirhe("Konvertteriparametrin paluuarvo puuttuu funktiolle "
                + nimi, indeksi) :: accum
            } else {
              val tarkistaPaluuarvonTyyppi = konv match {
                case av: Arvovalikonvertteriparametri => !av.getPalautaHaettuArvo
                case _ => true
              }

              if (tarkistaPaluuarvonTyyppi) {
                val virheviesti = funktiokutsu.getFunktionimi.getTyyppi match {
                  case Funktiotyyppi.LUKUARVOFUNKTIO => {
                    if (!tryConvertString(konv.getPaluuarvo, _.toDouble)) {
                      Some("Konvertteriparametrin paluuarvoa " + konv.getPaluuarvo + " ei pystytty konvertoimaan " +
                        "Double-tyyppiseksi")
                    } else None
                  }

                  case Funktiotyyppi.TOTUUSARVOFUNKTIO => {
                    if (!tryConvertString(konv.getPaluuarvo, _.toBoolean)) {
                      Some("konvertterparametrin paluuarvoa " + konv.getPaluuarvo + " ei pystytty konvertoimaan " +
                        "Boolean-tyyppiseksi")
                    } else None
                  }
                  case _ => None
                }

                virheviesti match {
                  case Some(virheviesti) => new VirheellinenKonvertteriparametrinPaluuarvoTyyppiVirhe(
                    virheviesti, indeksi, funktiokutsu.getFunktionimi.name()) :: accum
                  case None => accum
                }
              } else accum
            }
          }

          val arvokonvertterikuvaus = param.konvertteriTyypit(ARVOKONVERTTERI).asInstanceOf[Arvokonvertterikuvaus]
          def validoiArvokonvertteriparametritRekursiivisesti(indeksi: Int, konvs: List[Arvokonvertteriparametri],
                                                              accum: List[Validointivirhe]): List[Validointivirhe] = {
            konvs match {
              case Nil => accum
              case head :: tail => {
                val virheet = validoiKonvertteriparametri(indeksi, head, accum)
                val arvovirhe = if (StringUtils.isBlank(head.getArvo)) {
                  Some(new ArvokonvertterinArvoPuuttuuVirhe(
                    "Konvertteriparametrin arvo puuttuu funktiolle " + nimi, indeksi))
                } else {
                  val virheviesti = arvokonvertterikuvaus.arvotyyppi match {
                    case Syoteparametrityyppi.DESIMAALILUKU =>
                      ("Arvokonvertterin arvoa " + head.getArvo + " ei pystytty konvertoimaan Double-tyyppiseksi")

                    case Syoteparametrityyppi.KOKONAISLUKU =>
                      ("Arvokonvertterin arvoa " + head.getArvo + " ei pystytty konvertoimaan Integer-tyyppiseksi")

                    case Syoteparametrityyppi.TOTUUSARVO =>
                      ("Arvokonvertterin arvoa " + head.getArvo + " ei pystytty konvertoimaan Boolean-tyyppiseksi")

                    case Syoteparametrityyppi.MERKKIJONO => ""
                  }

                  muutaMerkkijonoParametrityypiksi(head.getArvo, arvokonvertterikuvaus.arvotyyppi, virheviesti) match {
                    case Some(virhe) => Some(new VirheellinenArvokonvertterinArvoTyyppiVirhe(virhe, indeksi))
                    case None => None
                  }
                }

                val yhdistetytVirheet = arvovirhe match {
                  case Some(virhe) => virhe :: virheet
                  case None => virheet
                }

                validoiArvokonvertteriparametritRekursiivisesti(indeksi + 1, tail, yhdistetytVirheet)
              }
            }
          }

          def validoiArvovalikonvertteriparametrit(konvs: List[Arvovalikonvertteriparametri],
                                                   accum: List[Validointivirhe]): List[Validointivirhe] = {

            def validoiArvovalikonvertteriparametritRekursiivisesti(indeksi: Int,
                                                                    konvs: List[Arvovalikonvertteriparametri],
                                                                    accum: List[Validointivirhe]): List[Validointivirhe] = {
              konvs match {
                case Nil => accum
                case head :: tail => {
                  val virheet = validoiKonvertteriparametri(indeksi, head, accum)
                  val arvovirhe = if (head.getMaxValue == null || head.getMinValue == null) {
                    Some(new ArvovalikonvertterinMinMaxPuutteellinenVirhe(
                      "Funktion " + nimi + " arvovälikonvertteriparametrin min- ja max-välit ovat puutteelliset"
                      , indeksi))
                  } else if (head.getMaxValue < head.getMinValue) {
                    Some(new ArvovalikonvertterinMinimiSuurempiKuinMaksimiVirhe(
                      "Arvovälikonvertteriparametrin minimiarvo ei voi olla suurempi kuin maksimiarvo", indeksi))
                  } else None

                  val yhdistetytVirheet = arvovirhe match {
                    case Some(virhe) => virhe :: virheet
                    case None => virheet
                  }

                  validoiArvovalikonvertteriparametritRekursiivisesti(indeksi + 1, tail, yhdistetytVirheet)
                }
              }
            }

            def tarkistaMinimiJaMaksimi(edellinen: Option[Arvovalikonvertteriparametri],
                                        seuraavat: List[Arvovalikonvertteriparametri],
                                        accum: List[Validointivirhe]): List[Validointivirhe] = {
              seuraavat match {
                case head :: tail => {
                  edellinen match {
                    case Some(prev) => if (prev.getMaxValue != head.getMinValue) {
                      new Validointivirhe(Virhetyyppi.ARVOVALIKONVERTTERIN_ARVOVALI_PUUTTEELLINEN,
                        "Arvovälikonvertterien arvovälit ovat puutteelliset") :: accum
                    } else {
                      tarkistaMinimiJaMaksimi(Some(head), tail, accum)
                    }
                    case None => tarkistaMinimiJaMaksimi(Some(head), tail, accum)
                  }
                }
                case Nil => accum
              }
            }

            val vv = tarkistaMinimiJaMaksimi(None, konvs.sortWith(_.getMinValue < _.getMinValue), accum)
            validoiArvovalikonvertteriparametritRekursiivisesti(0, konvs, vv)
          }

          val annetutArvokonvertterit = funktiokutsu.getArvokonvertteriparametrit.toList
          val annetutArvovalikonvertterit = funktiokutsu.getArvovalikonvertteriparametrit.toList

          val vv = validoiArvokonvertteriparametritRekursiivisesti(0, annetutArvokonvertterit.toList, virheet)
          validoiArvovalikonvertteriparametrit(annetutArvovalikonvertterit, vv)
        }
      }
    }
  }

  def tarkistaFunktiokohtaisetRajoitteet(funktiokutsu: Funktiokutsu, virheet: List[Validointivirhe]) = {
    def tarkistaN(virheet: List[Validointivirhe]): List[Validointivirhe] = {
      funktiokutsu.getSyoteparametrit.filter(_.getAvain == "n").toList match {
        case head :: tail if (tryConvertString(head.getArvo, _.toInt)) => {
          val n = head.getArvo.toInt

          if (n < 1) {
            new NPienempiKuinYksiVirhe("Syöteparametri n ei voi olla pienempi kuin yksi. " +
              "Annettu arvo: " + n, n) :: virheet
          } else if (n > funktiokutsu.getFunktioargumentit.size()) {
            new NSuurempiKuinFunktioargumenttienLkmVirhe("Syöteparametri n ei voi olla " +
              "suurempi kuin annettujen funktioargumenttien lukumäärä. Annettu arvo: " + n, n) :: virheet
          } else virheet
        }
        case _ => virheet
      }
    }

    funktiokutsu.getFunktionimi match {
      case Funktionimi.KESKIARVONPARASTA |
           Funktionimi.SUMMANPARASTA |
           Funktionimi.NMAKSIMI |
           Funktionimi.NMINIMI => tarkistaN(virheet)
      case Funktionimi.DEMOGRAFIA => {
        funktiokutsu.getSyoteparametrit.filter(_.getAvain == "prosenttiosuus").toList match {
          case head :: tail if (tryConvertString(head.getArvo, _.toDouble)) => {
            val prosenttiosuus = head.getArvo.toDouble
            if (prosenttiosuus <= 0.0 || prosenttiosuus > 100.0) {
              new ProsenttiosuusEpavalidiVirhe("Prosenttiosuuden pitää olla välillä 0.0 - 100.0. Annettu " +
                "arvo: " + prosenttiosuus, prosenttiosuus) :: virheet
            } else virheet
          }
          case _ => virheet
        }
      }
      case _ => virheet
    }
  }

  def validoiMallinnettuKaava(funktiokutsu: Funktiokutsu): Funktiokutsu = {
    validoiKaava(funktiokutsu, false)
  }

  def validoiLaskettavaKaava(funktiokutsu: Funktiokutsu): Funktiokutsu = {
    validoiKaava(funktiokutsu, true)
  }

  private def validoiKaava(funktiokutsu: Funktiokutsu, validoiLaskettava: Boolean): Funktiokutsu = {
    val virheet = if (Funktiotyyppi.EI_VALIDI == funktiokutsu.getFunktionimi.getTyyppi) {
      List(new Validointivirhe(Virhetyyppi.FUNKTIONIMI_VIRHEELLINEN,
        "Funktionimi " + funktiokutsu.getFunktionimi.name() + " ei ole validi"));
    } else {
      val vv1 = tarkistaParametrit(funktiokutsu, Nil)
      val vv2 = tarkistaKonvertteri(funktiokutsu, vv1)
      val vv3 = tarkistaValintaperusteparametrit(funktiokutsu, vv2)
      val vv4 = tarkistaFunktioargumentit(funktiokutsu, validoiLaskettava, vv3)
      tarkistaFunktiokohtaisetRajoitteet(funktiokutsu, vv4)
    }

    funktiokutsu.getFunktioargumentit.filter(_.getFunktiokutsuChild != null).foreach(fa => {
      validoiKaava(fa.getFunktiokutsuChild, validoiLaskettava)
    })

    funktiokutsu.setValidointivirheet(virheet)

    funktiokutsu
  }

  def validoiLaskettavaKaava(laskentakaava: Laskentakaava): Laskentakaava = {
    validoiLaskettavaKaava(laskentakaava.getFunktiokutsu)
    laskentakaava
  }

  def validoiMallinnettuKaava(laskentakaava: Laskentakaava): Laskentakaava = {
    validoiMallinnettuKaava(laskentakaava.getFunktiokutsu)
    laskentakaava
  }

  def onkoMallinnettuKaavaValidi(laskentakaava: Laskentakaava): Boolean = {
    onkoMallinnettuKaavaValidi(laskentakaava.getFunktiokutsu)
  }

  def onkoLaskettavaKaavaValidi(laskentakaava: Laskentakaava): Boolean = {
    onkoLaskettavaKaavaValidi(laskentakaava.getFunktiokutsu)
  }

  def onkoMallinnettuKaavaValidi(funktiokutsu: Funktiokutsu): Boolean = {
    val validoitu = validoiMallinnettuKaava(funktiokutsu)
    !onkoValidointiVirheita(validoitu)
  }

  def onkoLaskettavaKaavaValidi(funktiokutsu: Funktiokutsu): Boolean = {
    val validoitu = validoiLaskettavaKaava(funktiokutsu)
    !onkoValidointiVirheita(validoitu)
  }

  def onkoValidointiVirheita(fk: Funktiokutsu): Boolean = {
    if (fk.getValidointivirheet.isEmpty) {
      fk.getFunktioargumentit.exists(fa => if (fa.getFunktiokutsuChild != null) {
        onkoValidointiVirheita(fa.getFunktiokutsuChild)
      } else false)
    } else {
      true
    }
  }
}
