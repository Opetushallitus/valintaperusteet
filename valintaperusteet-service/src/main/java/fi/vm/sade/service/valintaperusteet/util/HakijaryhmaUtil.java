package fi.vm.sade.service.valintaperusteet.util;

import fi.vm.sade.service.valintaperusteet.model.Hakijaryhma;

/**
 * User: kwuoti
 * Date: 7.2.2013
 * Time: 17.10
 */
public abstract class HakijaryhmaUtil {
    public static Hakijaryhma teeKopioMasterista(Hakijaryhma master) {
        Hakijaryhma kopio = new Hakijaryhma();
        //kopio.setValinnanVaiheTyyppi(master.getValinnanVaiheTyyppi());
        kopio.setKiintio(master.getKiintio());
        kopio.setLaskentakaava(master.getLaskentakaava());

        kopio.setAktiivinen(master.getAktiivinen());
        kopio.setKuvaus(master.getKuvaus());
        kopio.setNimi(master.getNimi());
        kopio.setMaster(master);
        return kopio;
    }
}
