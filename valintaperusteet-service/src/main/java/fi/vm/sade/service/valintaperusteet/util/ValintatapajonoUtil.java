package fi.vm.sade.service.valintaperusteet.util;

import fi.vm.sade.service.valintaperusteet.model.Valintatapajono;

/**
 * User: kwuoti
 * Date: 11.2.2013
 * Time: 13.42
 */
public abstract class ValintatapajonoUtil {
    public static Valintatapajono teeKopioMasterista(Valintatapajono master) {
        Valintatapajono kopio = new Valintatapajono();
        kopio.setAktiivinen(master.getAktiivinen());
        kopio.setAutomaattinenLaskentaanSiirto(master.getAutomaattinenLaskentaanSiirto());
        kopio.setValisijoittelu(master.getValisijoittelu());
        kopio.setAloituspaikat(master.getAloituspaikat());
        kopio.setKuvaus(master.getKuvaus());
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

        kopio.setMasterValintatapajono(master);
        return kopio;
    }
}
