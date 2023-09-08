package fi.vm.sade.service.valintaperusteet.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ValintaryhmaPlainDTO", description = "Valintaryhmä")
public class ValintaryhmaPlainDTO {
  @Schema(description = "Nimi", required = true)
  private String nimi;

  @Schema(description = "Haun kohdejoukko")
  private String kohdejoukko;

  @Schema(description = "Haun oid")
  private String hakuoid;

  public String getNimi() {
    return nimi;
  }

  public void setNimi(String nimi) {
    this.nimi = nimi;
  }

  public String getKohdejoukko() {
    return kohdejoukko;
  }

  public void setKohdejoukko(String kohdejoukko) {
    this.kohdejoukko = kohdejoukko;
  }

  public String getHakuoid() {
    return hakuoid;
  }

  public void setHakuoid(String hakuoid) {
    this.hakuoid = hakuoid;
  }
}
