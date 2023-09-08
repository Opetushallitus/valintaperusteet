package fi.vm.sade.service.valintaperusteet.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "HakijaryhmaDTO", description = "Hakijaryhmä")
public class HakijaryhmaSiirraDTO extends HakijaryhmaCreateDTO {

  @Schema(description = "Nimi")
  private String uusinimi;

  @Schema(description = "Valintaryhmä OID, jolle laskentakaava lisätään")
  private String valintaryhmaOid;

  public String getValintaryhmaOid() {
    return valintaryhmaOid;
  }

  public void setValintaryhmaOid(String valintaryhmaOid) {
    this.valintaryhmaOid = valintaryhmaOid;
  }

  public String getUusinimi() {
    return uusinimi;
  }

  public void setUusinimi(String uusinimi) {
    this.uusinimi = uusinimi;
  }
}
