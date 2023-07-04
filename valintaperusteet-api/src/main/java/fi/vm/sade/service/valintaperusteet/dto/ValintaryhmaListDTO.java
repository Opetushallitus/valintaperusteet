package fi.vm.sade.service.valintaperusteet.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ValintaryhmaListDTO", description = "Valintaryhmä")
public class ValintaryhmaListDTO extends AbstractValintaryhmaDTO {
  @Schema(description = "OID", required = true)
  private String oid;

  public String getOid() {
    return oid;
  }

  public void setOid(String oid) {
    this.oid = oid;
  }
}
