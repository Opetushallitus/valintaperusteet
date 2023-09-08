package fi.vm.sade.service.valintaperusteet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "HakukohdeViiteDTO", description = "Hakukohde")
public class HakukohdeViiteDTO extends HakukohdeViiteCreateDTO {

  @Schema(description = "Valintaryhmä OID")
  @JsonProperty(value = "valintaryhma_id")
  private String valintaryhmaOid;

  @Schema(description = "Hakukohdekoodi")
  private KoodiDTO hakukohdekoodi;

  public String getValintaryhmaOid() {
    return valintaryhmaOid;
  }

  public void setValintaryhmaOid(String valintaryhmaOid) {
    this.valintaryhmaOid = valintaryhmaOid;
  }

  public KoodiDTO getHakukohdekoodi() {
    return hakukohdekoodi;
  }

  public void setHakukohdekoodi(KoodiDTO hakukohdekoodi) {
    this.hakukohdekoodi = hakukohdekoodi;
  }
}
