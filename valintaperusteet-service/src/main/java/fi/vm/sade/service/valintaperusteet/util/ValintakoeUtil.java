package fi.vm.sade.service.valintaperusteet.util;

import fi.vm.sade.service.valintaperusteet.model.Valintakoe;

public class ValintakoeUtil {
    public static Valintakoe teeKopioMasterista(Valintakoe master, JuureenKopiointiCache kopiointiCache) {
        Valintakoe kopio = new Valintakoe();
        kopio.setAktiivinen(master.getAktiivinen());
        kopio.setKuvaus(master.getKuvaus());
        if(kopiointiCache == null) {
            kopio.setMaster(master);
            kopio.setLaskentakaava(master.getLaskentakaava());
        } else {
            if(master.getMaster() != null) {
                Valintakoe kopioituMaster = kopiointiCache.kopioidutValintakokeet.get(master.getMaster().getId());
                if (kopioituMaster == null) {
                    throw new IllegalStateException("Ei löydetty lähdekokeen " + master + " masterille " + master.getMaster() + " kopiota");
                }
                kopio.setMaster(kopioituMaster);
            }
            if (master.getLaskentakaavaId() != null) {
                kopio.setLaskentakaava(kopiointiCache.kopioidutLaskentakaavat.get(master.getLaskentakaavaId()));
            }
        }
        kopio.setNimi(master.getNimi());
        kopio.setTunniste(master.getTunniste());
        kopio.setKutsunKohdeAvain(master.getKutsunKohdeAvain());
        kopio.setLahetetaankoKoekutsut(master.getLahetetaankoKoekutsut());
        kopio.setKutsutaankoKaikki(master.getKutsutaankoKaikki());
        kopio.setKutsuttavienMaara(master.getKutsuttavienMaara());
        kopio.setKutsunKohde(master.getKutsunKohde());
        return kopio;
    }
}
