package fi.vm.sade.service.valintaperusteet.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "HakukohdeInsertDTO", description = "Hakukohteen lisääminen")
public class HakukohdeInsertDTO {

  public HakukohdeInsertDTO() {}

  public HakukohdeInsertDTO(HakukohdeViiteCreateDTO hakukohde, String valintaryhmaOid) {
    this.hakukohde = hakukohde;
    this.valintaryhmaOid = valintaryhmaOid;
  }

  @Schema(description = "Lisättävä hakukohde", required = true)
  private HakukohdeViiteCreateDTO hakukohde;

  @Schema(description = "Valintaryhmä OID, johon hakukohde lisätään", required = true)
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
