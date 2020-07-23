package fi.vm.sade.service.valintaperusteet.util;

import fi.vm.sade.service.valintaperusteet.model.ValinnanVaihe;

public abstract class ValinnanVaiheUtil {
  public static ValinnanVaihe teeKopioMasterista(
      ValinnanVaihe master, JuureenKopiointiCache kopiointiCache) {
    ValinnanVaihe kopio = new ValinnanVaihe();
    kopio.setValinnanVaiheTyyppi(master.getValinnanVaiheTyyppi());
    kopio.setAktiivinen(master.getAktiivinen());
    kopio.setKuvaus(master.getKuvaus());
    kopio.setNimi(master.getNimi());
    if (kopiointiCache == null) {
      kopio.setMasterValinnanVaihe(master);
    } else {
      if (master.getMaster() != null) {
        ValinnanVaihe kopioituMaster =
            kopiointiCache.kopioidutValinnanVaiheet.get(master.getMaster().getId());
        if (kopioituMaster == null) {
          throw new IllegalStateException(
              "Ei löydetty lähdevaiheen "
                  + master
                  + " masterille "
                  + master.getMaster()
                  + " kopiota");
        }
        kopio.setMasterValinnanVaihe(kopioituMaster);
      }
    }
    return kopio;
  }
}
