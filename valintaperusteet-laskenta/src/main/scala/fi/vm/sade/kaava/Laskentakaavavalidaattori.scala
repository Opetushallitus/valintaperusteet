package fi.vm.sade.kaava

import fi.vm.sade.service.valintaperusteet.model._
import Funktiokuvaaja._
import fi.vm.sade.service.valintaperusteet.service.validointi.virhe._
import org.apache.commons.lang.StringUtils
import fi.vm.sade.service.valintaperusteet.dto.model.Funktiotyyppi
import java.math.BigDecimal
import scala.Some

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
      case e: Throwable => false
    }
  }


  private def muutaMerkkijonoParametrityypiksi(s: String,
                                               tyyppi: Syoteparametrityyppi.Syoteparametrityyppi) = {
    val konv: (String => Unit) = tyyppi match {
      case Syoteparametrityyppi.DESIMAALILUKU => (mj: String) => new BigDecimal(mj)
      case Syoteparametrityyppi.KOKONAISLUKU => (mj: String) => mj.toInt
      case Syoteparametrityyppi.TOTUUSARVO => (mj: String) => mj.toBoolean
      case Syoteparametrityyppi.MERKKIJONO => (mj: String) => Unit
    }

    tryConvertString(s, konv)
  }


  private def tarkistaParametriarvo(funktiokutsu: Funktiokutsu, annettuParametri: Syoteparametri,
                                    vaadittuParametri: Syoteparametrikuvaus): Option[Validointivirhe] = {
    if (StringUtils.isBlank(annettuParametri.getArvo)) {
      Some(new TyhjaSyoteparametrinArvoVirhe(s"Parametrin (avain ${annettuParametri.getAvain}) arvo on tyhjä",
      annettuParametri.getAvain))
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
        case Syoteparametrityyppi.MERKKIJONO => ""
      }

      if (muutaMerkkijonoParametrityypiksi(annettuParametri.getArvo, vaadittuParametri.tyyppi)) {
        None
      } else {
        Some(new VirheellinenSyoteParametrinTyyppiVirhe(virheviesti, annettuParametri.getAvain,
          vaadittuParametri.tyyppi.toString))
      }
    }
  }

  private def tarkistaParametrit(funktiokutsu: Funktiokutsu): List[Validointivirhe] = {
    val funktiokuvaus = Funktiokuvaaja.annaFunktiokuvaus(funktiokutsu.getFunktionimi)._2

    val annetutParametrit = funktiokutsu.getSyoteparametrit.filter(!_.getArvo.isEmpty).toList
    val vaaditutParametrit = funktiokuvaus.syoteparametrit

    val (pakolliset, eiPakolliset) = vaaditutParametrit.partition(_.pakollinen)

    pakolliset.foldLeft(List[Validointivirhe]())((l, p) => {
      annetutParametrit.find(p.avain == _.getAvain) match {
        case None => new SyoteparametriPuuttuuVirhe(
          s"Avainta ${p.avain} vastaavaa parametria ei ole olemassa funktiolle ${funktiokutsu.getFunktionimi.name()}",
          p.avain) :: l
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
        new FunktiokutsuaEiOleMaariteltyFunktioargumentilleVirhe(
          s"Funktiokutsua ei ole annettu funktiokutsun $nimi funktioargumentille, indeksi ${argumentti.getIndeksi}",
          argumentti.getIndeksi) :: accum
      } else if (!validoiLaskettava && argumentti.getFunktiokutsuChild == null && argumentti.getLaskentakaavaChild == null) {
        new FunktioargumenttiaEiMaariteltyVirhe(
          s"Funktiokutsua tai laskentakaavaa ei ole määritelty funktiokutsun $nimi funktioargumentille, indeksi ${argumentti.getIndeksi}",
          argumentti.getIndeksi) :: accum
      } else {
        if (argumentti.getFunktiokutsuChild != null
          && vaadittuArgumentti.tyyppi.toString != argumentti.getFunktiokutsuChild.getFunktionimi.getTyyppi.name()) {
          new VirheellinenFunktioargumentinTyyppiVirhe(
            s"""Funktion $nimi funktioargumentti on väärää tyyppiä. Vaadittu: ${vaadittuArgumentti.tyyppi.toString},
            annettu: ${argumentti.getFunktiokutsuChild.getFunktionimi.getTyyppi.name()}""", argumentti.getIndeksi,
            vaadittuArgumentti.tyyppi.toString, argumentti.getFunktiokutsuChild.getFunktionimi.getTyyppi.name()) :: accum
        } else if (argumentti.getLaskentakaavaChild != null) {
          val vv = if (vaadittuArgumentti.tyyppi.toString != argumentti.getLaskentakaavaChild.getTyyppi.name()) {
            new VirheellinenFunktioargumentinTyyppiVirhe(
              s"""Funktion $nimi funktioargumentti on väärää tyyppiä.
              Vaadittu: ${vaadittuArgumentti.tyyppi.toString}, annettu: ${argumentti.getLaskentakaavaChild.getTyyppi.name()}""",
              argumentti.getIndeksi,
              vaadittuArgumentti.tyyppi.toString, argumentti.getLaskentakaavaChild.getTyyppi.name()) :: accum
          } else accum

          if (argumentti.getLaskentakaavaChild.getOnLuonnos) {
            new FunktioargumentinLaskentakaavaOnLuonnosVirhe(
              s"Funktion $nimi funktioargumentille määriteltylaskentakaava on luonnos-tilassa",
              argumentti.getIndeksi) :: vv
          } else vv
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
          List(new VaaraMaaraFunktioargumenttejaVirhe(
            s"Väärä määrä funktioargumentteja funktiolle $nimi. Vaadittu: ainakin yksi, annettu: ${annetutArgumentit.size}",
            ">=1", annetutArgumentit.size))
        } else funktiokutsu.getFunktioargumentit.foldLeft(List[Validointivirhe]())((l, a) => validoiFunktioargumentti(arg, a, l))
      } else if (vaaditutArgumentit.size == 1 && vaaditutArgumentit(0).kardinaliteetti == Kardinaliteetti.LISTA_PAREJA) {
        val arg = vaaditutArgumentit(0)
        if (annetutArgumentit.size == 0 || annetutArgumentit.size % 2 != 0) {
          List(new VaaraMaaraFunktioargumenttejaVirhe(
            s"Väärä määrä funktioargumentteja funktiolle $nimi. Vaadittu: parillinen määrä, annettu: ${annetutArgumentit.size}",
            "parillinen määrä", annetutArgumentit.size))
        } else funktiokutsu.getFunktioargumentit.foldLeft(List[Validointivirhe]())((l, a) => validoiFunktioargumentti(arg, a, l))
      } else {
        if (vaaditutArgumentit.size != annetutArgumentit.size) {
          List(new VaaraMaaraFunktioargumenttejaVirhe(
            s"Väärä määrä funktioargumentteja funktiolle $nimi. Vaadittu: ${vaaditutArgumentit.size}, annettu: ${annetutArgumentit.size}",
            vaaditutArgumentit.size.toString, annetutArgumentit.size))
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
          funktiokutsu.getValintaperusteviitteet.foldLeft(List[Validointivirhe]())((l, vp) => {
            if (StringUtils.isBlank(vp.getTunniste))
              new ValintaperusteparametrinTunnistePuuttuuVirhe(s"Valintaperusteparametrin tunniste puuttuu funktiolle $nimi", vp.getIndeksi) :: l
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
          ((param.konvertteriTyypit.containsKey(ARVOKONVERTTERI)
            && param.konvertteriTyypit.containsKey(ARVOVALIKONVERTTERI)
            && funktiokutsu.getArvokonvertteriparametrit.isEmpty
            && funktiokutsu.getArvovalikonvertteriparametrit.isEmpty) ||
            (param.konvertteriTyypit.containsKey(ARVOKONVERTTERI) &&
              !param.konvertteriTyypit.containsKey(ARVOVALIKONVERTTERI) &&
              funktiokutsu.getArvokonvertteriparametrit.isEmpty) ||
            (param.konvertteriTyypit.containsKey(ARVOVALIKONVERTTERI) &&
              !param.konvertteriTyypit.containsKey(ARVOKONVERTTERI) &&
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
              Some(new KonvertteriparametrinPaluuarvoPuuttuuVirhe(
                s"Konvertteriparametrin paluuarvo puuttuu funktiolle $nimi", indeksi))
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
                  case Funktiotyyppi.LUKUARVOFUNKTIO => {
                    if (!tryConvertString(konv.getPaluuarvo, new BigDecimal(_))) {
                      Some(virhe("BigDecimal"))
                    } else None
                  }

                  case Funktiotyyppi.TOTUUSARVOFUNKTIO => {
                    if (!tryConvertString(konv.getPaluuarvo, _.toBoolean)) {
                      Some(virhe("Boolean"))
                    } else None
                  }
                  case _ => None
                }

                virheviesti match {
                  case Some(virheviesti) => Some(new VirheellinenKonvertteriparametrinPaluuarvoTyyppiVirhe(
                    virheviesti, indeksi, funktiokutsu.getFunktionimi.name()))
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
                  Some(new ArvokonvertterinArvoPuuttuuVirhe(
                    "Konvertteriparametrin arvo puuttuu funktiolle " + nimi, indeksi))
                } else {
                  val virheviesti = arvokonvertterikuvaus.arvotyyppi match {
                    case Syoteparametrityyppi.DESIMAALILUKU => virhe("BigDecimal")
                    case Syoteparametrityyppi.KOKONAISLUKU => virhe("Integer")
                    case Syoteparametrityyppi.TOTUUSARVO => virhe("Boolean")
                    case Syoteparametrityyppi.MERKKIJONO => ""
                  }

                  if (muutaMerkkijonoParametrityypiksi(head.getArvo, arvokonvertterikuvaus.arvotyyppi)) {
                    None
                  } else {
                    Some(new VirheellinenArvokonvertterinArvoTyyppiVirhe(virheviesti, indeksi))
                  }
                }

                validoiArvokonvertteriparametritRekursiivisesti(indeksi + 1, tail, (arvovirhe ++ paluuarvovirhe ++ accum).toList)
              }
            }
          }

          val annetutArvokonvertterit = funktiokutsu.getArvokonvertteriparametrit.toList
          val annetutArvovalikonvertterit = funktiokutsu.getArvovalikonvertteriparametrit.toList

          validoiArvokonvertteriparametritRekursiivisesti(0, annetutArvokonvertterit.toList, Nil)
        }
      }
    }
  }

  def tarkistaFunktiokohtaisetRajoitteet(funktiokutsu: Funktiokutsu): List[Validointivirhe] = {
    def tarkistaN: List[Validointivirhe] = {
      funktiokutsu.getSyoteparametrit.filter(_.getAvain == "n").toList match {
        case head :: tail if (tryConvertString(head.getArvo, _.toInt)) => {
          val n = head.getArvo.toInt

          if (n < 1) {
            List(new NPienempiKuinYksiVirhe(
              s"Syöteparametri n ei voi olla pienempi kuin yksi. Annettu arvo: $n", n))
          } else if (n > funktiokutsu.getFunktioargumentit.size()) {
            List(new NSuurempiKuinFunktioargumenttienLkmVirhe(
              s"Syöteparametri n ei voi olla suurempi kuin annettujen funktioargumenttien lukumäärä. Annettu arvo: $n", n))
          } else Nil
        }
        case _ => Nil
      }
    }

    funktiokutsu.getFunktionimi match {
      case Funktionimi.KESKIARVONPARASTA |
           Funktionimi.SUMMANPARASTA |
           Funktionimi.NMAKSIMI |
           Funktionimi.NMINIMI => tarkistaN
      case Funktionimi.DEMOGRAFIA => {
        funktiokutsu.getSyoteparametrit.filter(_.getAvain == "prosenttiosuus").toList match {
          case head :: tail if (tryConvertString(head.getArvo, new BigDecimal(_))) => {
            val prosenttiosuus = new BigDecimal(head.getArvo)
            if (prosenttiosuus.compareTo(BigDecimal.ZERO) != 1 || prosenttiosuus.compareTo(new BigDecimal("100.0")) == 1) {
              List(new ProsenttiosuusEpavalidiVirhe(
                s"Prosenttiosuuden pitää olla välillä 0.0 - 100.0. Annettu arvo: $prosenttiosuus", prosenttiosuus))
            } else Nil
          }
          case _ => Nil
        }
      }
      case Funktionimi.PYORISTYS => {
        funktiokutsu.getSyoteparametrit.find(_.getAvain == "tarkkuus") match {
          case Some(p) if (tryConvertString(p.getArvo, _.toInt)) => {
            val tarkkuus = p.getArvo.toInt
            if (tarkkuus < 0) {
              List(new TarkkuusPienempiKuinNollaVirhe(
                s"Tarkkuuden pitää olla suurempi kuin nolla. Annettu arvo: $tarkkuus", tarkkuus))
            } else Nil
          }
          case _ => Nil
        }
      }

      case Funktionimi.SKAALAUS => {
        funktiokutsu.getSyoteparametrit.filter(_.getAvain == "kaytaLaskennallistaLahdeskaalaa").toList match {
          case head :: tail if (tryConvertString(head.getArvo, _.toBoolean)) => {
            val kaytaLaskennallistaLahdeskaalaa = head.getArvo.toBoolean


            val kohdeskaalaMin = funktiokutsu.getSyoteparametrit.find(_.getAvain == "kohdeskaalaMin").map(sa => {
              if (tryConvertString(sa.getArvo, new BigDecimal(_))) Some(new BigDecimal(sa.getArvo)) else None
            })

            val kohdeskaalaMax = funktiokutsu.getSyoteparametrit.find(_.getAvain == "kohdeskaalaMax").map(sa => {
              if (tryConvertString(sa.getArvo, new BigDecimal(_))) Some(new BigDecimal(sa.getArvo)) else None
            })

            val virheet = List[Validointivirhe]()
            ((if (!kohdeskaalaMin.isEmpty && !kohdeskaalaMin.get.isEmpty
              && !kohdeskaalaMax.isEmpty && !kohdeskaalaMax.get.isEmpty
              && kohdeskaalaMin.get.get.compareTo(kohdeskaalaMax.get.get) > 0) {
              Some(new KohdeskaalaVirheellinenVirhe("Kohdeskaalan minimin pitää olla pienempi kuin maksimi"))
            } else None) ++ (if (!kaytaLaskennallistaLahdeskaalaa) {
              val lahdeskaalaMin = funktiokutsu.getSyoteparametrit.find(_.getAvain == "lahdeskaalaMin").map(sa => {
                if (tryConvertString(sa.getArvo, new BigDecimal(_))) Some(new BigDecimal(sa.getArvo)) else None
              })

              val lahdeskaalaMax = funktiokutsu.getSyoteparametrit.find(_.getAvain == "lahdeskaalaMax").map(sa => {
                if (tryConvertString(sa.getArvo, new BigDecimal(_))) Some(new BigDecimal(sa.getArvo)) else None
              })

              if (lahdeskaalaMin.isEmpty || lahdeskaalaMax.isEmpty) {
                Some(new SkaalauksenLahdeskaalaaEiOleMaariteltyVirhe("Skaalauksen lähdeskaalaa ei ole määritelty"))
              } else if (!lahdeskaalaMin.isEmpty && !lahdeskaalaMin.get.isEmpty
                && !lahdeskaalaMax.isEmpty && !lahdeskaalaMax.get.isEmpty
                && lahdeskaalaMin.get.get.compareTo(lahdeskaalaMax.get.get) > 0) {
                Some(new LahdeskaalaVirheellinenVirhe("Lähdeskaalan minimin pitää olla pienempi kuin maksimi"))
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
    val virheet = if (Funktiotyyppi.EI_VALIDI == funktiokutsu.getFunktionimi.getTyyppi) {
      List(new Validointivirhe(Virhetyyppi.FUNKTIONIMI_VIRHEELLINEN,
        s"Funktionimi ${funktiokutsu.getFunktionimi.name()} ei ole validi"));
    } else {
      (tarkistaParametrit(funktiokutsu) ++
        tarkistaKonvertteri(funktiokutsu) ++
        tarkistaValintaperusteparametrit(funktiokutsu) ++
        tarkistaFunktioargumentit(funktiokutsu, validoiLaskettava) ++
        tarkistaFunktiokohtaisetRajoitteet(funktiokutsu)).toList
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
