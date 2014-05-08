package fi.vm.sade.service.valintaperusteet.util;

import fi.vm.sade.service.valintaperusteet.model.Valintakoe;

/**
 * User: wuoti
 * Date: 10.5.2013
 * Time: 9.33
 */
public class ValintakoeUtil {
    public static Valintakoe teeKopioMasterista(Valintakoe master) {
        Valintakoe kopio = new Valintakoe();
        kopio.setAktiivinen(master.getAktiivinen());
        kopio.setKuvaus(master.getKuvaus());
        kopio.setLaskentakaava(master.getLaskentakaava());
        kopio.setMaster(master);
        kopio.setNimi(master.getNimi());
        kopio.setTunniste(master.getTunniste());
        kopio.setLahetetaankoKoekutsut(master.getLahetetaankoKoekutsut());
        kopio.setKutsutaankoKaikki(master.getKutsutaankoKaikki());

        return kopio;
    }
}
