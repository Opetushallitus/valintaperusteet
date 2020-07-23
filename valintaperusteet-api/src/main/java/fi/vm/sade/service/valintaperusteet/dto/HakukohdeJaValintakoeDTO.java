package fi.vm.sade.service.valintaperusteet.dto;

import java.util.List;

public class HakukohdeJaValintakoeDTO {

  private String hakukohdeOid;
  private List<ValintakoeDTO> valintakoeDTO;

  public HakukohdeJaValintakoeDTO() {}

  public HakukohdeJaValintakoeDTO(String hakukohdeOid, List<ValintakoeDTO> valintakoeDTO) {
    this.hakukohdeOid = hakukohdeOid;
    this.valintakoeDTO = valintakoeDTO;
  }

  public String getHakukohdeOid() {
    return hakukohdeOid;
  }

  public List<ValintakoeDTO> getValintakoeDTO() {
    return valintakoeDTO;
  }
}
