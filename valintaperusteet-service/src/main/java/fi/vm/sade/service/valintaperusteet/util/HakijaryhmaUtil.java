package fi.vm.sade.service.valintaperusteet.util;

import fi.vm.sade.service.valintaperusteet.model.Hakijaryhma;

public abstract class HakijaryhmaUtil {
    public static Hakijaryhma teeKopioMasterista(Hakijaryhma master) {
        Hakijaryhma kopio = new Hakijaryhma();
        kopio.setKiintio(master.getKiintio());
        kopio.setLaskentakaava(master.getLaskentakaava());

        kopio.setKuvaus(master.getKuvaus());
        kopio.setNimi(master.getNimi());
        kopio.setMaster(master);
        return kopio;
    }
}
