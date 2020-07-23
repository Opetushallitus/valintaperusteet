package fi.vm.sade.service.valintaperusteet.util;

import fi.vm.sade.service.valintaperusteet.model.Valintatapajono;

public abstract class ValintatapajonoUtil {
  public static Valintatapajono teeKopioMasterista(
      Valintatapajono master, JuureenKopiointiCache kopiointiCache) {
    Valintatapajono kopio = new Valintatapajono();
    kopio.setAktiivinen(master.getAktiivinen());
    kopio.setautomaattinenSijoitteluunSiirto(master.getautomaattinenSijoitteluunSiirto());
    kopio.setValisijoittelu(master.getValisijoittelu());
    kopio.setAloituspaikat(master.getAloituspaikat());
    kopio.setKuvaus(master.getKuvaus());
    kopio.setTyyppi(master.getTyyppi());
    kopio.setNimi(master.getNimi());
    kopio.setSiirretaanSijoitteluun(master.getSiirretaanSijoitteluun());
    kopio.setTasapistesaanto(master.getTasapistesaanto());
    kopio.setEiVarasijatayttoa(master.getEiVarasijatayttoa());
    kopio.setKaikkiEhdonTayttavatHyvaksytaan(master.getKaikkiEhdonTayttavatHyvaksytaan());
    kopio.setPoistetaankoHylatyt(master.isPoistetaankoHylatyt());
    // VT-657
    kopio.setPoissaOlevaTaytto(master.getPoissaOlevaTaytto());
    kopio.setVarasijat(master.getVarasijat());
    kopio.setVarasijojaKaytetaanAlkaen(master.getVarasijojaKaytetaanAlkaen());
    kopio.setVarasijojaTaytetaanAsti(master.getVarasijojaTaytetaanAsti());
    kopio.setKaytetaanValintalaskentaa(master.getKaytetaanValintalaskentaa());
    if (kopiointiCache == null) {
      kopio.setMasterValintatapajono(master);
    } else {
      if (master.getMaster() != null) {
        Valintatapajono kopioituMaster =
            kopiointiCache.kopioidutValintapajonot.get(master.getMaster().getId());
        if (kopioituMaster == null) {
          throw new IllegalStateException(
              "Ei löydetty lähdejonon "
                  + master
                  + " masterille "
                  + master.getMaster()
                  + " kopiota");
        }
        kopio.setMasterValintatapajono(kopioituMaster);
      }
    }
    return kopio;
  }
}
