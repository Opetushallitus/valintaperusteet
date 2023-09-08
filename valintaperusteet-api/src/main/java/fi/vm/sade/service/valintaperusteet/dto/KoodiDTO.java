package fi.vm.sade.service.valintaperusteet.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "KoodiDTO", description = "Koodiston koodi")
public class KoodiDTO {

  @Schema(description = "URI", required = true)
  private String uri;

  @Schema(description = "Suomenkielinen nimi")
  private String nimiFi;

  @Schema(description = "Ruotsinkielinen nimi")
  private String nimiSv;

  @Schema(description = "Englanninkielinen nimi")
  private String nimiEn;

  @Schema(description = "Koodin arvo")
  private String arvo;

  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  public String getNimiFi() {
    return nimiFi;
  }

  public void setNimiFi(String nimiFi) {
    this.nimiFi = nimiFi;
  }

  public String getNimiSv() {
    return nimiSv;
  }

  public void setNimiSv(String nimiSv) {
    this.nimiSv = nimiSv;
  }

  public String getNimiEn() {
    return nimiEn;
  }

  public void setNimiEn(String nimiEn) {
    this.nimiEn = nimiEn;
  }

  public String getArvo() {
    return arvo;
  }

  public void setArvo(String arvo) {
    this.arvo = arvo;
  }
}
