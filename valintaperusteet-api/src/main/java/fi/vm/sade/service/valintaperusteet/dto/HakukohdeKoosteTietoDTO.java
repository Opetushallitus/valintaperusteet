package fi.vm.sade.service.valintaperusteet.dto;

import java.util.Date;

public class HakukohdeKoosteTietoDTO {
  public final String hakukohdeOid;
  public final Boolean hasValintakoe;
  public final Date varasijatayttoPaattyy;

  public HakukohdeKoosteTietoDTO(
      String hakukohdeOid, Boolean hasValintakoe, Date varasijatayttoPaattyy) {
    this.hakukohdeOid = hakukohdeOid;
    this.hasValintakoe = hasValintakoe;
    this.varasijatayttoPaattyy = varasijatayttoPaattyy;
  }
}
