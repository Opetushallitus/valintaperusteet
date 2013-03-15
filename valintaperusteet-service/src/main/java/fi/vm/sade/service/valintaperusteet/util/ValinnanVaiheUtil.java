package fi.vm.sade.service.valintaperusteet.util;

import fi.vm.sade.service.valintaperusteet.model.ValinnanVaihe;

/**
 * User: kwuoti
 * Date: 7.2.2013
 * Time: 17.10
 */
public abstract class ValinnanVaiheUtil {
    public static ValinnanVaihe teeKopioMasterista(ValinnanVaihe master) {
        ValinnanVaihe kopio = new ValinnanVaihe();
        kopio.setAktiivinen(master.getAktiivinen());
        kopio.setKuvaus(master.getKuvaus());
        kopio.setNimi(master.getNimi());
        kopio.setMasterValinnanVaihe(master);
        return kopio;
    }
}
