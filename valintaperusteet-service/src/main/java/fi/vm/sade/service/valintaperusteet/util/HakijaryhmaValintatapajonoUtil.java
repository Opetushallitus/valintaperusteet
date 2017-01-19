package fi.vm.sade.service.valintaperusteet.util;

import fi.vm.sade.service.valintaperusteet.model.HakijaryhmaValintatapajono;

public abstract class HakijaryhmaValintatapajonoUtil {

    public static HakijaryhmaValintatapajono teeKopioMasterista(HakijaryhmaValintatapajono master, JuureenKopiointiCache kopiointiCache) {
        HakijaryhmaValintatapajono kopio = new HakijaryhmaValintatapajono();
        kopio.setKiintio(master.getKiintio());
        kopio.setKaytaKaikki(master.isKaytaKaikki());
        kopio.setTarkkaKiintio(master.isKaytaKaikki());
        kopio.setKaytetaanRyhmaanKuuluvia(master.isKaytetaanRyhmaanKuuluvia());
        kopio.setAktiivinen(master.getAktiivinen());
        kopio.setHakijaryhma(master.getHakijaryhma());
        kopio.setHakijaryhmatyyppikoodi(master.getHakijaryhmatyyppikoodi());
        if (kopiointiCache == null) {
            kopio.setMaster(master);
        } else {
            if(master.getMaster() != null) {
                HakijaryhmaValintatapajono kopioituMaster = kopiointiCache.kopioidutHakijaryhmaValintapajonot.get(master.getMaster().getId());
                if (kopioituMaster == null) {
                    throw new IllegalStateException("Ei löydetty lähde HakijaryhmaValintatapajonon " + master + " masterille " + master.getMaster() + " kopiota");
                }
                kopio.setMaster(kopioituMaster);
            }
        }
        return kopio;
    }
}
