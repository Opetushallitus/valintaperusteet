package fi.vm.sade.kaava

import fi.vm.sade.service.valintaperusteet.model._
import Funktiokuvaaja._
import fi.vm.sade.service.valintaperusteet.service.validointi.virhe._
import org.apache.commons.lang.StringUtils
import fi.vm.sade.service.valintaperusteet.dto.model.{Funktionimi, Funktiotyyppi}
import java.math.BigDecimal

import scala.jdk.CollectionConverters._

object Laskentakaavavalidaattori {

  private def tryConvertString(s: String, f: (String => Unit)) = {
    try {
      f(s.replace(',', '.'))
      true
    } catch {
      case e: Throwable => false
    }
  }

  private def muutaMerkkijonoParametrityypiksi(s: String,
    tyyppi: Syoteparametrityyppi.Syoteparametrityyppi) = {
    val konv: (String => Unit) = tyyppi match {
      case Syoteparametrityyppi.DESIMAALILUKU => (mj: String) => new BigDecimal(mj.replace(',', '.'))
      case Syoteparametrityyppi.KOKONAISLUKU => (mj: String) => mj.toInt
      case Syoteparametrityyppi.TOTUUSARVO => (mj: String) => mj.toBoolean
      case Syoteparametrityyppi.CHECKBOX => (mj: String) => mj.toBoolean
      case Syoteparametrityyppi.MERKKIJONO => (_: String) => ()
      case Syoteparametrityyppi.ARVOJOUKKO => (_: String) => ()
    }

    tryConvertString(s, konv)
  }

  private def tarkistaParametriarvo(funktiokutsu: Funktiokutsu, annettuParametri: Syoteparametri,
    vaadittuParametri: Syoteparametrikuvaus): Option[Validointivirhe] = {
    if (StringUtils.isBlank(annettuParametri.getArvo)) {
      Some(new Validointivirhe(Virhetyyppi.TYHJA_SYOTEPARAMETRIN_ARVO, s"Parametrin (avain ${annettuParametri.getAvain}) arvo on tyhjä"))
    } else {
      val nimi = funktiokutsu.getFunktionimi.name()

      def viesti(tyyppi: String) = {
        (s"""Parametrin (avain ${annettuParametri.getAvain} arvoa ${annettuParametri.getArvo}
           ei voida konvertoida $tyyppi-tyyppiseksi funktiolle $nimi""")
      }

      val virheviesti = vaadittuParametri.tyyppi match {
        case Syoteparametrityyppi.DESIMAALILUKU => viesti("BigDecimal")
        case Syoteparametrityyppi.KOKONAISLUKU => viesti("Integer")
        case Syoteparametrityyppi.TOTUUSARVO => viesti("Boolean")
        case Syoteparametrityyppi.CHECKBOX => viesti("Boolean")
        case Syoteparametrityyppi.MERKKIJONO => ""
        case Syoteparametrityyppi.ARVOJOUKKO => viesti("Enum")
      }

      if (muutaMerkkijonoParametrityypiksi(annettuParametri.getArvo, vaadittuParametri.tyyppi)) {
        None
      } else {
        Some(new Validointivirhe(Virhetyyppi.VIRHEELLINEN_SYOTEPARAMETRIN_TYYPPI,virheviesti))
      }
    }
  }

  private def tarkistaParametrit(funktiokutsu: Funktiokutsu): List[Validointivirhe] = {
    val funktiokuvaus = Funktiokuvaaja.annaFunktiokuvaus(funktiokutsu.getFunktionimi)._2

    val annetutParametrit = funktiokutsu.getSyoteparametrit.asScala.filter(!_.getArvo.isEmpty).toList
    val vaaditutParametrit = funktiokuvaus.syoteparametrit

    val (pakolliset, eiPakolliset) = vaaditutParametrit.partition(_.pakollinen)

    pakolliset.foldLeft(List[Validointivirhe]())((l, p) => {
      annetutParametrit.find(p.avain == _.getAvain) match {
        case None => new Validointivirhe(Virhetyyppi.SYOTEPARAMETRI_PUUTTUU,
          s"Avainta '${p.avain}' vastaavaa parametria ei ole olemassa funktiolle '${funktiokutsu.getFunktionimi.name()}'") :: l
        case Some(param) => (tarkistaParametriarvo(funktiokutsu, param, p) ++ l).toList
      }
    }) ::: eiPakolliset.foldLeft(List[Validointivirhe]())((l, p) => {
      annetutParametrit.find(p.avain == _.getAvain) match {
        case None => l
        case Some(param) => (tarkistaParametriarvo(funktiokutsu, param, p) ++ l).toList
      }
    })
  }

  private def tarkistaFunktioargumentit(funktiokutsu: Funktiokutsu,
    validoiLaskettava: Boolean): List[Validointivirhe] = {

    val funktiokuvaus = Funktiokuvaaja.annaFunktiokuvaus(funktiokutsu.getFunktionimi)._2
    val nimi = funktiokutsu.getFunktionimi.name()

    def validoiFunktioargumentti(vaadittuArgumentti: Funktiokuvaaja.Funktioargumenttikuvaus,
      argumentti: Funktioargumentti, accum: List[Validointivirhe]): List[Validointivirhe] = {

      if (validoiLaskettava && argumentti.getFunktiokutsuChild == null) {
        new Validointivirhe(Virhetyyppi.FUNKTIOKUTSUA_EI_OLE_MAARITELTY_FUNKTIOARGUMENTILLE,
          s"Funktiokutsua ei ole annettu funktiokutsun $nimi funktioargumentille, indeksi ${argumentti.getIndeksi}, id ${argumentti.getId}, funktio id: ${funktiokutsu.getId}") :: accum
      } else if (!validoiLaskettava && argumentti.getFunktiokutsuChild == null && argumentti.getLaskentakaavaChild == null) {
        new Validointivirhe(Virhetyyppi.FUNKTIOARGUMENTTIA_EI_MAARITELTY,
          s"Funktiokutsua tai laskentakaavaa ei ole määritelty funktiokutsun $nimi funktioargumentille, indeksi ${argumentti.getIndeksi}") :: accum
      } else {
        if (argumentti.getFunktiokutsuChild != null
          && vaadittuArgumentti.tyyppi.toString != argumentti.getFunktiokutsuChild.getFunktionimi.getTyyppi.name()) {
          new Validointivirhe(Virhetyyppi.VIRHEELLINEN_FUNKTIOARGUMENTIN_TYYPPI,
            s"""Funktion $nimi funktioargumentti on väärää tyyppiä. Vaadittu: ${vaadittuArgumentti.tyyppi.toString},
            annettu: ${argumentti.getFunktiokutsuChild.getFunktionimi.getTyyppi.name()}""") :: accum
        } else if (argumentti.getLaskentakaavaChild != null) {
          val vv = if (vaadittuArgumentti.tyyppi.toString != argumentti.getLaskentakaavaChild.getTyyppi.name()) {
            new Validointivirhe(Virhetyyppi.VIRHEELLINEN_FUNKTIOARGUMENTIN_TYYPPI,
              s"""Funktion $nimi funktioargumentti on väärää tyyppiä.
              Vaadittu: ${vaadittuArgumentti.tyyppi.toString}, annettu: ${argumentti.getLaskentakaavaChild.getTyyppi.name()}""") :: accum
          } else accum

          // Laskentakaavaa ei voi tällä hetkellä tallentaa luonnoksena
//          if (argumentti.getLaskentakaavaChild.getOnLuonnos) {
//            new Validointivirhe(Virhetyyppi.FUNKTIOARGUMENTIN_LASKENTAKAAVA_ON_LUONNOS,
//              s"Funktion $nimi funktioargumentille määriteltylaskentakaava on luonnos-tilassa") :: vv
//          } else vv

          vv
        } else accum
      }
    }

    val annetutArgumentit = LaskentaUtil.jarjestaFunktioargumentit(funktiokutsu.getFunktioargumentit)
    val vaaditutArgumentit = funktiokuvaus.funktioargumentit

    if (vaaditutArgumentit.isEmpty && !annetutArgumentit.isEmpty) {
      List(new Validointivirhe(Virhetyyppi.FUNKTIOKUTSU_EI_OTA_FUNKTIOARGUMENTTEJA,
        s"Funktio $nimi ei ota funktioargumentteja"))
    } else {
      if (vaaditutArgumentit.size == 1 && vaaditutArgumentit(0).kardinaliteetti == Kardinaliteetti.N) {
        val arg = vaaditutArgumentit(0)
        if (annetutArgumentit.isEmpty) {
          List(new Validointivirhe(Virhetyyppi.VAARA_MAARA_FUNKTIOARGUMENTTEJA,
            s"Väärä määrä funktioargumentteja funktiolle $nimi. Vaadittu: ainakin yksi, annettu: ${annetutArgumentit.size}"))
        } else funktiokutsu.getFunktioargumentit.asScala.foldLeft(List[Validointivirhe]())((l, a) => validoiFunktioargumentti(arg, a, l))
      } else if (vaaditutArgumentit.size == 1 && vaaditutArgumentit(0).kardinaliteetti == Kardinaliteetti.LISTA_PAREJA) {
        val arg = vaaditutArgumentit(0)
        if (annetutArgumentit.size == 0 || annetutArgumentit.size % 2 != 0) {
          List(new Validointivirhe(Virhetyyppi.VAARA_MAARA_FUNKTIOARGUMENTTEJA,
            s"Väärä määrä funktioargumentteja funktiolle $nimi. Vaadittu: parillinen määrä, annettu: ${annetutArgumentit.size}"))
        } else funktiokutsu.getFunktioargumentit.asScala.foldLeft(List[Validointivirhe]())((l, a) => validoiFunktioargumentti(arg, a, l))
      } else {
        if (vaaditutArgumentit.size != annetutArgumentit.size) {
          List(new Validointivirhe(Virhetyyppi.VAARA_MAARA_FUNKTIOARGUMENTTEJA,
            s"Väärä määrä funktioargumentteja funktiolle $nimi. Vaadittu: ${vaaditutArgumentit.size}, annettu: ${annetutArgumentit.size}"))
        } else {
          def tarkistaFunktioargumentit(annetutArgumentit: List[Funktioargumentti],
            vaaditutArgumentit: List[Funktiokuvaaja.Funktioargumenttikuvaus],
            accum: List[Validointivirhe]): List[Validointivirhe] = {
            if (!annetutArgumentit.isEmpty) {
              tarkistaFunktioargumentit(annetutArgumentit.tail, vaaditutArgumentit.tail,
                validoiFunktioargumentti(vaaditutArgumentit.head, annetutArgumentit.head, accum))
            } else accum
          }

          tarkistaFunktioargumentit(annetutArgumentit, vaaditutArgumentit.toList, Nil)
        }
      }
    }
  }

  private def tarkistaValintaperusteparametrit(funktiokutsu: Funktiokutsu): List[Validointivirhe] = {
    val funktiokuvaus = Funktiokuvaaja.annaFunktiokuvaus(funktiokutsu.getFunktionimi)._2
    val nimi = funktiokutsu.getFunktionimi.name()

    funktiokuvaus.valintaperusteparametri match {
      case Nil => {
        if (funktiokutsu.getValintaperusteviitteet.size > 0) {
          List(new Validointivirhe(Virhetyyppi.FUNKTIOKUTSU_EI_OTA_VALINTAPERUSTEPARAMETRIA,
            s"Funktio $nimi ei ota valintaperusteparametreja"))
        } else Nil
      }

      case lista => {
        if (lista.size != funktiokutsu.getValintaperusteviitteet.size) {
          List(new Validointivirhe(Virhetyyppi.VALINTAPERUSTEPARAMETRI_PUUTTUUU,
            s"Valintaperusteparametri puuttuu funktiolle $nimi"))
        } else {
          funktiokutsu.getValintaperusteviitteet.asScala.foldLeft(List[Validointivirhe]())((l, vp) => {
            if (StringUtils.isBlank(vp.getTunniste))
              new Validointivirhe(Virhetyyppi.VALINTAPERUSTEPARAMETRI_PUUTTUUU, s"Valintaperusteparametrin tunniste puuttuu funktiolle $nimi") :: l
            else l
          })
        }
      }
    }
  }

  private def tarkistaKonvertteri(funktiokutsu: Funktiokutsu): List[Validointivirhe] = {
    import Funktiokuvaaja.Konvertterinimi._

    val funktiokuvaus = Funktiokuvaaja.annaFunktiokuvaus(funktiokutsu.getFunktionimi)._2
    val nimi = funktiokutsu.getFunktionimi.name()

    funktiokuvaus.konvertteri match {
      case None => {
        if (!funktiokutsu.getArvokonvertteriparametrit.isEmpty
          || !funktiokutsu.getArvovalikonvertteriparametrit.isEmpty) {
          List(new Validointivirhe(Virhetyyppi.FUNKTIOKUTSU_EI_OTA_KONVERTTERIPARAMETREJA,
            s"Funktio $nimi ei ota konvertteriparametreja"))
        } else Nil
      }
      case Some(param) => {
        if (param.pakollinen &&
          ((param.konvertteriTyypit.asJava.containsKey(ARVOKONVERTTERI)
            && param.konvertteriTyypit.asJava.containsKey(ARVOVALIKONVERTTERI)
            && funktiokutsu.getArvokonvertteriparametrit.isEmpty
            && funktiokutsu.getArvovalikonvertteriparametrit.isEmpty) ||
            (param.konvertteriTyypit.asJava.containsKey(ARVOKONVERTTERI) &&
              !param.konvertteriTyypit.asJava.containsKey(ARVOVALIKONVERTTERI) &&
              funktiokutsu.getArvokonvertteriparametrit.isEmpty) ||
              (param.konvertteriTyypit.asJava.containsKey(ARVOVALIKONVERTTERI) &&
                !param.konvertteriTyypit.asJava.containsKey(ARVOKONVERTTERI) &&
                funktiokutsu.getArvovalikonvertteriparametrit.isEmpty))) {
          List(new Validointivirhe(Virhetyyppi.EI_KONVERTTERIPARAMETREJA_MAARITELTY,
            s"Vaadittuja konvertteriparametreja ei ole määritelty funktiolle $nimi"))
        } else {

          def validoiKonvertteriparametri(indeksi: Int, konv: Konvertteriparametri): Option[Validointivirhe] = {
            val paluuarvoPuuttuu = konv match {
              case av: Arvokonvertteriparametri => {
                StringUtils.isBlank(av.getPaluuarvo)
              }
              case _ => false
            }

            if (paluuarvoPuuttuu) {
              Some(new Validointivirhe(Virhetyyppi.KONVERTTERIPARAMETRIN_PALUUARVO_PUUTTUU,
                s"Konvertteriparametrin paluuarvo puuttuu funktiolle $nimi"))
            } else {
              val tarkistaPaluuarvonTyyppi = konv match {
                case av: Arvovalikonvertteriparametri => false
                case _ => true
              }

              if (tarkistaPaluuarvonTyyppi) {
                def virhe(tyyppi: String) = {
                  s"Konvertteriparametrin paluuarvoa ${konv.getPaluuarvo} ei pystytty konvertoimaan $tyyppi-tyyppiseksi"
                }
                val virheviesti = funktiokutsu.getFunktionimi.getTyyppi match {
                  case fi.vm.sade.service.valintaperusteet.dto.model.Funktiotyyppi.LUKUARVOFUNKTIO => {
                    if (!tryConvertString(konv.getPaluuarvo, new BigDecimal(_))) {
                      Some(virhe("BigDecimal"))
                    } else None
                  }

                  case fi.vm.sade.service.valintaperusteet.dto.model.Funktiotyyppi.TOTUUSARVOFUNKTIO => {
                    if (!tryConvertString(konv.getPaluuarvo, _.toBoolean)) {
                      Some(virhe("Boolean"))
                    } else None
                  }
                  case _ => None
                }

                virheviesti match {
                  case Some(virheviesti) => Some(new Validointivirhe(
                    Virhetyyppi.VIRHEELLINEN_KONVERTTERIPARAMETRIN_PALUUARVOTYYPPI,
                    virheviesti))
                  case None => None
                }
              } else None
            }
          }

          val arvokonvertterikuvaus = param.konvertteriTyypit(ARVOKONVERTTERI).asInstanceOf[Arvokonvertterikuvaus]
          def validoiArvokonvertteriparametritRekursiivisesti(indeksi: Int, konvs: List[Arvokonvertteriparametri],
            accum: List[Validointivirhe]): List[Validointivirhe] = {
            konvs match {
              case Nil => accum
              case head :: tail => {
                val paluuarvovirhe = validoiKonvertteriparametri(indeksi, head)

                def virhe(tyyppi: String) = {
                  (s"Arvokonvertterin arvoa ${head.getArvo} ei pystytty konvertoimaan ${tyyppi}-tyyppiseksi")
                }

                val arvovirhe = if (StringUtils.isBlank(head.getArvo)) {
                  Some(new Validointivirhe(Virhetyyppi.ARVOKONVERTTERIN_ARVO_PUUTTUU,
                    "Konvertteriparametrin arvo puuttuu funktiolle " + nimi))
                } else {
                  val virheviesti = arvokonvertterikuvaus.arvotyyppi match {
                    case Syoteparametrityyppi.DESIMAALILUKU => virhe("BigDecimal")
                    case Syoteparametrityyppi.KOKONAISLUKU => virhe("Integer")
                    case Syoteparametrityyppi.TOTUUSARVO => virhe("Boolean")
                    case Syoteparametrityyppi.CHECKBOX => virhe("Boolean")
                    case Syoteparametrityyppi.MERKKIJONO => ""
                    case Syoteparametrityyppi.ARVOJOUKKO => virhe("Enum")
                  }

                  if (muutaMerkkijonoParametrityypiksi(head.getArvo, arvokonvertterikuvaus.arvotyyppi)) {
                    None
                  } else {
                    Some(new Validointivirhe(Virhetyyppi.VIRHEELLINEN_ARVOKONVERTTERIN_ARVOTYYPPI, virheviesti))
                  }
                }

                validoiArvokonvertteriparametritRekursiivisesti(indeksi + 1, tail, (arvovirhe ++ paluuarvovirhe ++ accum).toList)
              }
            }
          }

          val annetutArvokonvertterit = funktiokutsu.getArvokonvertteriparametrit.asScala.toList
          val annetutArvovalikonvertterit = funktiokutsu.getArvovalikonvertteriparametrit.asScala.toList

          validoiArvokonvertteriparametritRekursiivisesti(0, annetutArvokonvertterit.toList, Nil)
        }
      }
    }
  }

  def tarkistaFunktiokohtaisetRajoitteet(funktiokutsu: Funktiokutsu): List[Validointivirhe] = {
    def tarkistaN: List[Validointivirhe] = {
      funktiokutsu.getSyoteparametrit.asScala.filter(_.getAvain == "n").toList match {
        case head :: tail if (tryConvertString(head.getArvo, _.toInt)) => {
          val n = head.getArvo.toInt

          if (n < 1) {
            List(new Validointivirhe(Virhetyyppi.N_PIENEMPI_KUIN_YKSI,
              s"Syöteparametri n ei voi olla pienempi kuin yksi. Annettu arvo: $n"))
          } else if (n > funktiokutsu.getFunktioargumentit.size()) {
            List(new Validointivirhe(Virhetyyppi.N_SUUREMPI_KUIN_FUNKTIOARGUMENTTIEN_LKM,
              s"Syöteparametri n ei voi olla suurempi kuin annettujen funktioargumenttien lukumäärä. Annettu arvo: $n"))
          } else Nil
        }
        case _ => Nil
      }
    }

    funktiokutsu.getFunktionimi match {
      case Funktionimi.KESKIARVONPARASTA |
        Funktionimi.SUMMANPARASTA |
        Funktionimi.TULONPARASTA |
        Funktionimi.NMAKSIMI |
        Funktionimi.NMINIMI => tarkistaN
      case Funktionimi.DEMOGRAFIA => {
        funktiokutsu.getSyoteparametrit.asScala.filter(_.getAvain == "prosenttiosuus").toList match {
          case head :: tail if (tryConvertString(head.getArvo, new BigDecimal(_))) => {
            val prosenttiosuus = new BigDecimal(head.getArvo)
            if (prosenttiosuus.compareTo(BigDecimal.ZERO) != 1 || prosenttiosuus.compareTo(new BigDecimal("100.0")) == 1) {
              List(new Validointivirhe(Virhetyyppi.PROSENTTIOSUUS_EPAVALIDI,
                s"Prosenttiosuuden pitää olla välillä 0.0 - 100.0. Annettu arvo: $prosenttiosuus"))
            } else Nil
          }
          case _ => Nil
        }
      }
      case Funktionimi.PYORISTYS => {
        funktiokutsu.getSyoteparametrit.asScala.find(_.getAvain == "tarkkuus") match {
          case Some(p) if (tryConvertString(p.getArvo, _.toInt)) => {
            val tarkkuus = p.getArvo.toInt
            if (tarkkuus < 0) {
              List(new Validointivirhe(Virhetyyppi.TARKKUUS_PIENEMPI_KUIN_NOLLA,
                s"Tarkkuuden pitää olla suurempi kuin nolla. Annettu arvo: $tarkkuus"))
            } else Nil
          }
          case _ => Nil
        }
      }

      case Funktionimi.SKAALAUS => {
        funktiokutsu.getSyoteparametrit.asScala.filter(_.getAvain == "kaytaLaskennallistaLahdeskaalaa").toList match {
          case head :: tail if (tryConvertString(head.getArvo, _.toBoolean)) => {
            val kaytaLaskennallistaLahdeskaalaa = head.getArvo.toBoolean

            val kohdeskaalaMin = funktiokutsu.getSyoteparametrit.asScala.find(_.getAvain == "kohdeskaalaMin").map(sa => {
              if (tryConvertString(sa.getArvo, new BigDecimal(_))) Some(new BigDecimal(sa.getArvo)) else None
            })

            val kohdeskaalaMax = funktiokutsu.getSyoteparametrit.asScala.find(_.getAvain == "kohdeskaalaMax").map(sa => {
              if (tryConvertString(sa.getArvo, new BigDecimal(_))) Some(new BigDecimal(sa.getArvo)) else None
            })

            val virheet = List[Validointivirhe]()
            ((if (!kohdeskaalaMin.isEmpty && !kohdeskaalaMin.get.isEmpty
              && !kohdeskaalaMax.isEmpty && !kohdeskaalaMax.get.isEmpty
              && kohdeskaalaMin.get.get.compareTo(kohdeskaalaMax.get.get) > 0) {
              Some(new Validointivirhe(Virhetyyppi.KOHDESKAALA_VIRHEELLINEN, "Kohdeskaalan minimin pitää olla pienempi kuin maksimi"))
            } else None) ++ (if (!kaytaLaskennallistaLahdeskaalaa) {
              val lahdeskaalaMin = funktiokutsu.getSyoteparametrit.asScala.find(_.getAvain == "lahdeskaalaMin").map(sa => {
                if (tryConvertString(sa.getArvo, new BigDecimal(_))) Some(new BigDecimal(sa.getArvo)) else None
              })

              val lahdeskaalaMax = funktiokutsu.getSyoteparametrit.asScala.find(_.getAvain == "lahdeskaalaMax").map(sa => {
                if (tryConvertString(sa.getArvo, new BigDecimal(_))) Some(new BigDecimal(sa.getArvo)) else None
              })

              if (lahdeskaalaMin.isEmpty || lahdeskaalaMax.isEmpty) {
                Some(new Validointivirhe(Virhetyyppi.LAHDESKAALAA_EI_OLE_MAARITELTY,
                  "Skaalauksen lähdeskaalaa ei ole määritelty"))
              } else if (!lahdeskaalaMin.isEmpty && !lahdeskaalaMin.get.isEmpty
                && !lahdeskaalaMax.isEmpty && !lahdeskaalaMax.get.isEmpty
                && lahdeskaalaMin.get.get.compareTo(lahdeskaalaMax.get.get) > 0) {
                Some(new Validointivirhe(Virhetyyppi.LAHDESKAALA_VIRHEELLINEN,
                  "Lähdeskaalan minimin pitää olla pienempi kuin maksimi"))
              } else None
            } else None) ++ virheet).toList
          }
          case _ => Nil
        }
      }

      case _ => Nil
    }
  }

  def validoiMallinnettuKaava(funktiokutsu: Funktiokutsu): Funktiokutsu = {
    validoiKaava(funktiokutsu, false)
  }

  def validoiLaskettavaKaava(funktiokutsu: Funktiokutsu): Funktiokutsu = {
    validoiKaava(funktiokutsu, true)
  }

  private def validoiKaava(funktiokutsu: Funktiokutsu, validoiLaskettava: Boolean): Funktiokutsu = {
    val virheet = if (fi.vm.sade.service.valintaperusteet.dto.model.Funktiotyyppi.EI_VALIDI == funktiokutsu.getFunktionimi.getTyyppi) {
      List(new Validointivirhe(Virhetyyppi.FUNKTIONIMI_VIRHEELLINEN,
        s"Funktionimi ${funktiokutsu.getFunktionimi.name()} ei ole validi"));
    } else {
      (tarkistaParametrit(funktiokutsu) ++
        tarkistaKonvertteri(funktiokutsu) ++
        tarkistaValintaperusteparametrit(funktiokutsu) ++
        tarkistaFunktioargumentit(funktiokutsu, validoiLaskettava) ++
        tarkistaFunktiokohtaisetRajoitteet(funktiokutsu)).toList
    }

    funktiokutsu.getFunktioargumentit.asScala.filter(_.getFunktiokutsuChild != null).foreach(fa => {
      validoiKaava(fa.getFunktiokutsuChild, validoiLaskettava)
    })

    funktiokutsu.setValidointivirheet(virheet.asInstanceOf[List[Abstraktivalidointivirhe]].asJava)

    virheet.foreach(v => println(s"Validointivirhe: ${v.getVirheviesti}"))

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

  def onkoMallinnettuKaavaValidi(laskentakaava: Laskentakaava): List[Abstraktivalidointivirhe] = {
    onkoMallinnettuKaavaValidi(laskentakaava.getFunktiokutsu)
  }

  def onkoLaskettavaKaavaValidi(laskentakaava: Laskentakaava): List[Abstraktivalidointivirhe] = {
    onkoLaskettavaKaavaValidi(laskentakaava.getFunktiokutsu)
  }

  def onkoMallinnettuKaavaValidi(funktiokutsu: Funktiokutsu): List[Abstraktivalidointivirhe] = {
    val validoitu = validoiMallinnettuKaava(funktiokutsu)
    onkoValidointiVirheita(validoitu)
  }

  def onkoLaskettavaKaavaValidi(funktiokutsu: Funktiokutsu): List[Abstraktivalidointivirhe] = {
    val validoitu = validoiLaskettavaKaava(funktiokutsu)
    onkoValidointiVirheita(validoitu)
  }

  def onkoValidointiVirheita(fk: Funktiokutsu): List[Abstraktivalidointivirhe] = {
      fk.getValidointivirheet.asScala.toList ++ fk.getFunktioargumentit.asScala.flatMap(fa => if (fa.getFunktiokutsuChild != null) {
        onkoValidointiVirheita(fa.getFunktiokutsuChild)
      } else {List()})
  }
}
