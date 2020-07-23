package fi.vm.sade.service.valintaperusteet.dto;

import java.util.List;

public class HakukohdeJaValintaperusteDTO {

  private String hakukohdeOid;
  private List<ValintaperusteDTO> valintaperusteDTO;

  public HakukohdeJaValintaperusteDTO() {}

  public HakukohdeJaValintaperusteDTO(
      String hakukohdeOid, List<ValintaperusteDTO> valintaperusteDTO) {
    this.hakukohdeOid = hakukohdeOid;
    this.valintaperusteDTO = valintaperusteDTO;
  }

  public String getHakukohdeOid() {
    return hakukohdeOid;
  }

  public List<ValintaperusteDTO> getValintaperusteDTO() {
    return valintaperusteDTO;
  }
}
