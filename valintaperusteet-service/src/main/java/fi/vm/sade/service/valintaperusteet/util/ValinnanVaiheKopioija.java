package fi.vm.sade.service.valintaperusteet.util;

import fi.vm.sade.service.valintaperusteet.model.ValinnanVaihe;
import org.apache.commons.lang.StringUtils;

public class ValinnanVaiheKopioija implements Kopioija<ValinnanVaihe> {
  @Override
  public ValinnanVaihe luoKlooni(ValinnanVaihe valinnanVaihe) {
    ValinnanVaihe klooni = new ValinnanVaihe();
    kopioiTiedot(valinnanVaihe, klooni);
    return klooni;
  }

  @Override
  public void kopioiTiedot(ValinnanVaihe from, ValinnanVaihe to) {
    to.setValinnanVaiheTyyppi(from.getValinnanVaiheTyyppi());
    if (from.getAktiivinen() != null) {
      to.setAktiivinen(from.getAktiivinen());
    }
    if (StringUtils.isNotBlank(from.getKuvaus())) {
      to.setKuvaus(from.getKuvaus());
    }
    if (StringUtils.isNotBlank(from.getNimi())) {
      to.setNimi(from.getNimi());
    }
  }

  @Override
  public void kopioiTiedotMasteriltaKopiolle(
      ValinnanVaihe alkuperainenMaster, ValinnanVaihe paivitettyMaster, ValinnanVaihe kopio) {
    if (kopio.getAktiivinen().equals(alkuperainenMaster.getAktiivinen())) {
      kopio.setAktiivinen(paivitettyMaster.getAktiivinen());
    }
    kopio.setNimi(paivitettyMaster.getNimi());
    kopio.setKuvaus(paivitettyMaster.getKuvaus());
  }
}
