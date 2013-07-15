package fi.vm.sade.service.valintaperusteet.laskenta

import api.Hakemus
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta._
import fi.vm.sade.service.valintaperusteet.laskenta.Laskenta.Jos
import scala.collection.JavaConversions._

/**
 * User: kwuoti
 * Date: 4.3.2013
 * Time: 15.07
 */
object Esiprosessori {
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
        val samatArvotLkm: Int = hakemukset.filter(h => {
          h.onkoHakutoivePrioriteetilla(hakukohde, 1) && h.kentat.get(f.tunniste) == prosessoituHakemus.kentat.get(f.tunniste)
        }).size

        val vertailuarvo = samatArvotLkm.toDouble / hakemukset.size()

        val arvo = vertailuarvo <= (f.prosenttiosuus / 100.0)
        val avain = prosessointiOid(hakukohde, prosessoituHakemus, f)

        new Hakemus(prosessoituHakemus.oid,
          prosessoituHakemus.hakutoiveet,
          prosessoituHakemus.kentat + (avain -> arvo.toString))
      }

      case _ => prosessoituHakemus
    }
  }
}
