package fi.vm.sade.service.valintaperusteet.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "HakukohdeInsertDTO", description = "Hakukohteen lisääminen")
public class HakukohdeInsertDTO {

  public HakukohdeInsertDTO() {}

  public HakukohdeInsertDTO(HakukohdeViiteCreateDTO hakukohde, String valintaryhmaOid) {
    this.hakukohde = hakukohde;
    this.valintaryhmaOid = valintaryhmaOid;
  }

  @ApiModelProperty(value = "Lisättävä hakukohde", required = true)
  private HakukohdeViiteCreateDTO hakukohde;

  @ApiModelProperty(value = "Valintaryhmä OID, johon hakukohde lisätään", required = true)
  private String valintaryhmaOid;

  public HakukohdeViiteCreateDTO getHakukohde() {
    return hakukohde;
  }

  public void setHakukohde(HakukohdeViiteCreateDTO hakukohde) {
    this.hakukohde = hakukohde;
  }

  public String getValintaryhmaOid() {
    return valintaryhmaOid;
  }

  public void setValintaryhmaOid(String valintaryhmaOid) {
    this.valintaryhmaOid = valintaryhmaOid;
  }
}
