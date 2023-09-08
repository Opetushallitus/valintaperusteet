package fi.vm.sade.service.valintaperusteet.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "HakuViiteDTO", description = "Haku")
public class HakuViiteDTO {

  @Schema(description = "Haku OID", required = true)
  private String hakuoid;

  public String getHakuoid() {
    return hakuoid;
  }

  public void setHakuoid(String hakuoid) {
    this.hakuoid = hakuoid;
  }
}
