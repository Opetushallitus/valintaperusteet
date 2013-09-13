package fi.vm.sade.service.valintaperusteet.laskenta

import fi.vm.sade.service.valintaperusteet.laskenta.api.{Hakukohde, Hakemus}
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta._
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.Jos
import scala.collection.JavaConversions._
import org.slf4j.LoggerFactory
import java.math.{BigDecimal => BigDec}
import java.math.RoundingMode

/**
 * User: kwuoti
 * Date: 4.3.2013
 * Time: 15.07
 */
object Esiprosessori {
  val LOG = LoggerFactory.getLogger(Esiprosessori.getClass())

  def prosessointiOid(hakukohde: String, hakemus: Hakemus, prosessoiva: Esiprosessoiva) = {
    hakukohde + "-" + hakemus.oid + "-" + prosessoiva.oid + "-" + prosessoiva.tunniste + "-" + prosessoiva.prosenttiosuus
  }

  def esiprosessoi(hakukohde: Hakukohde, hakemukset: java.util.Collection[Hakemus], kasiteltavaHakemus: Hakemus,
                   prosessoitava: Funktio[_]): Hakemus = {
    val prosessoituHakemus = prosessoitava match {
      case _: NollaParametrinenFunktio[_] => kasiteltavaHakemus
      case f: YksiParametrinenFunktio[_] => esiprosessoi(hakukohde, hakemukset, kasiteltavaHakemus, f.f)
      case f: KaksiParametrinenFunktio[_] => {
        val f1prosessoitu = esiprosessoi(hakukohde, hakemukset, kasiteltavaHakemus, f.f1)
        esiprosessoi(hakukohde, hakemukset, f1prosessoitu, f.f2)
      }
      case KoostavaFunktio(fs) => fs.foldLeft(kasiteltavaHakemus)((h, f) => esiprosessoi(hakukohde, hakemukset, h, f))
      case Jos(ehto, thenHaara, elseHaara, oid) => {
        val ehtoProsessoitu = esiprosessoi(hakukohde, hakemukset, kasiteltavaHakemus, ehto)
        val thenHaaraProsessoitu = esiprosessoi(hakukohde, hakemukset, ehtoProsessoitu, thenHaara)
        esiprosessoi(hakukohde, hakemukset, thenHaaraProsessoitu, elseHaara)
      }
    }

    prosessoitava match {
      case f: Esiprosessoiva => {
        val ensisijaisetHakijat = hakemukset.count(_.onkoHakutoivePrioriteetilla(hakukohde.hakukohdeOid, 1))
        val avain = prosessointiOid(hakukohde.hakukohdeOid, prosessoituHakemus, f)
        if (ensisijaisetHakijat == 0) {
          // ei ensisijaisia hakijoita
          //LOG.debug("Ei ensisijaisia hakijoita joten kellekään ei anneta bonusta!")
          new Hakemus(prosessoituHakemus.oid,
            prosessoituHakemus.hakutoiveet,
            prosessoituHakemus.kentat + (avain -> false.toString))
        } else {
          val tunniste = f.tunniste.toLowerCase()
          val ensisijaisistaHakijoistaErilaisetTunnisteet =
            hakemukset.filter(h => h.onkoHakutoivePrioriteetilla(hakukohde.hakukohdeOid, 1))
              .map(h => h.kentat.map(h => h._1.toLowerCase() -> h._2).get(tunniste.toLowerCase()).getOrElse(null))

          if (ensisijaisistaHakijoistaErilaisetTunnisteet.toSet.size > 2) {
            LOG.error("Liikaa vaihtoehtoja tunnisteelle, {}: {}", Array[Object](tunniste, ensisijaisistaHakijoistaErilaisetTunnisteet.toSet));
          }
          val omaTunniste = prosessoituHakemus.kentat.map(h => h._1.toLowerCase() -> h._2).getOrElse(tunniste, null);
          if (omaTunniste == null) {
            LOG.error("Hakemuksella {}: Ei ole {}", Array[Object](prosessoituHakemus.oid, tunniste));
            new Hakemus(prosessoituHakemus.oid, prosessoituHakemus.hakutoiveet, prosessoituHakemus.kentat + (avain -> false.toString));
          } else {
            val samatArvotLkm: Int = ensisijaisistaHakijoistaErilaisetTunnisteet.count(omaTunniste.equals(_))

            val vertailuarvo = BigDecimal(samatArvotLkm).underlying.divide(BigDecimal(ensisijaisetHakijat).underlying, 4, RoundingMode.HALF_UP) //hakemukset.size()
            val arvo = (vertailuarvo.compareTo(f.prosenttiosuus.underlying.divide(BigDecimal("100.0").underlying, 4, RoundingMode.HALF_UP)) != 1).toString
            // LOG.debug("Samoja arvoja on {} ja vertailuarvo on {} ja arvo on {}", Array[Object](samatArvotLkm.toString(), vertailuarvo, arvo));
            // LOG.debug("Hakemus {} {}bonuspisteet {}", Array[Object](prosessoituHakemus.oid, tunniste, arvo))
            new Hakemus(prosessoituHakemus.oid,
              prosessoituHakemus.hakutoiveet,
              prosessoituHakemus.kentat + (avain -> arvo))
          }

        }
      }

      case _ => prosessoituHakemus
    }
  }
}
