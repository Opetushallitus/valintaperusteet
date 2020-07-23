package fi.vm.sade.service.valintaperusteet.dto;

import java.util.List;

public class HakukohdeJaValinnanVaiheDTO {

  private String hakukohdeOid;
  private List<ValinnanVaiheJaPrioriteettiDTO> valinnanvaiheet;

  public HakukohdeJaValinnanVaiheDTO(
      String hakukohdeOid, List<ValinnanVaiheJaPrioriteettiDTO> valinnanvaiheet) {
    this.hakukohdeOid = hakukohdeOid;
    this.valinnanvaiheet = valinnanvaiheet;
  }

  public String getHakukohdeOid() {
    return hakukohdeOid;
  }

  public List<ValinnanVaiheJaPrioriteettiDTO> getValinnanvaiheet() {
    return valinnanvaiheet;
  }
}
