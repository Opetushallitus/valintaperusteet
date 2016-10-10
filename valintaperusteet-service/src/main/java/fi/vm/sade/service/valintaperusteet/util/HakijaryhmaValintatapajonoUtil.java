package fi.vm.sade.service.valintaperusteet.util;

import fi.vm.sade.service.valintaperusteet.model.HakijaryhmaValintatapajono;

public abstract class HakijaryhmaValintatapajonoUtil {

    public static HakijaryhmaValintatapajono teeKopioMasterista(HakijaryhmaValintatapajono master) {
        HakijaryhmaValintatapajono kopio = new HakijaryhmaValintatapajono();
        kopio.setKiintio(master.getKiintio());
        kopio.setKaytaKaikki(master.isKaytaKaikki());
        kopio.setTarkkaKiintio(master.isKaytaKaikki());
        kopio.setKaytetaanRyhmaanKuuluvia(master.isKaytetaanRyhmaanKuuluvia());
        kopio.setAktiivinen(master.getAktiivinen());
        kopio.setHakijaryhma(master.getHakijaryhma());
        kopio.setHakijaryhmatyyppikoodi(master.getHakijaryhmatyyppikoodi());
        kopio.setMaster(master);
        return kopio;
    }
}
