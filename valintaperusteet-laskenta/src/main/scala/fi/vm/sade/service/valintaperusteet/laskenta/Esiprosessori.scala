package fi.vm.sade.service.valintaperusteet.laskenta

import api.Hakemus
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta._
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.Jos
import scala.collection.JavaConversions._
import org.slf4j.LoggerFactory
import java.math.{ BigDecimal => BigDec }
import java.math.RoundingMode
import java.util.Arrays

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

  def esiprosessoi(hakukohde: String, hakemukset: java.util.Collection[Hakemus], kasiteltavaHakemus: Hakemus,
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
        val ensisijaisetHakijat = hakemukset.count(_.onkoHakutoivePrioriteetilla(hakukohde, 1))
        val avain = prosessointiOid(hakukohde, prosessoituHakemus, f)
        if (ensisijaisetHakijat == 0) {
          // ei ensisijaisia hakijoita
          LOG.debug("Ei ensisijaisia hakijoita joten kellekään ei anneta bonusta!")
          new Hakemus(prosessoituHakemus.oid,
            prosessoituHakemus.hakutoiveet,
            prosessoituHakemus.kentat + (avain -> false.toString))
        } else {
          val ensisijaisistaHakijoistaErilaisetTunnisteet = hakemukset.filter(h => h.onkoHakutoivePrioriteetilla(hakukohde, 1)).map(h => h.kentat.get(f.tunniste).getOrElse(null))

          if (ensisijaisistaHakijoistaErilaisetTunnisteet.toSet.size > 2) {
            LOG.error("Hakemukselle {}: {} on liikaa! {}", Array[Object](prosessoituHakemus.oid, f.tunniste, ensisijaisistaHakijoistaErilaisetTunnisteet.toSet));
          }
          val omaTunniste = prosessoituHakemus.kentat.getOrElse(f.tunniste, null);
          val samatArvotLkm: Int = ensisijaisistaHakijoistaErilaisetTunnisteet.count(_.equals(omaTunniste))

          val vertailuarvo = BigDecimal(samatArvotLkm).underlying.divide(BigDecimal(ensisijaisetHakijat).underlying, 4, RoundingMode.HALF_UP) //hakemukset.size()
          val arvo = (vertailuarvo.compareTo(f.prosenttiosuus.underlying.divide(BigDecimal("100.0").underlying, 4, RoundingMode.HALF_UP)) != 1).toString

          LOG.debug("Samoja arvoja on {} ja vertailuarvo on {} ja arvo on {}", Array[Object](samatArvotLkm.toString(), vertailuarvo, arvo));
          LOG.debug("Hakemus {} {}bonuspisteet {}", Array[Object](prosessoituHakemus.oid, f.tunniste, arvo))
          new Hakemus(prosessoituHakemus.oid,
            prosessoituHakemus.hakutoiveet,
            prosessoituHakemus.kentat + (avain -> arvo))
        }
      }

      case _ => prosessoituHakemus
    }
  }
}
