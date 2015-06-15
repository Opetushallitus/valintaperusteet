package fi.vm.sade.service.valintaperusteet.util;

import fi.vm.sade.service.valintaperusteet.model.ValinnanVaihe;

public abstract class ValinnanVaiheUtil {
    public static ValinnanVaihe teeKopioMasterista(ValinnanVaihe master) {
        ValinnanVaihe kopio = new ValinnanVaihe();
        kopio.setValinnanVaiheTyyppi(master.getValinnanVaiheTyyppi());
        kopio.setAktiivinen(master.getAktiivinen());
        kopio.setKuvaus(master.getKuvaus());
        kopio.setNimi(master.getNimi());
        kopio.setMasterValinnanVaihe(master);
        return kopio;
    }
}
