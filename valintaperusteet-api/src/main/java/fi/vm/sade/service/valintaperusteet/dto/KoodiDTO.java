package fi.vm.sade.service.valintaperusteet.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "KoodiDTO", description = "Koodiston koodi")
public class KoodiDTO {

  @ApiModelProperty(value = "URI", required = true)
  private String uri;

  @ApiModelProperty(value = "Suomenkielinen nimi")
  private String nimiFi;

  @ApiModelProperty(value = "Ruotsinkielinen nimi")
  private String nimiSv;

  @ApiModelProperty(value = "Englanninkielinen nimi")
  private String nimiEn;

  @ApiModelProperty(value = "Koodin arvo")
  private String arvo;

  public KoodiDTO() { }

  public KoodiDTO(String uri,
                  String nimiFi,
                  String nimiSv,
                  String nimiEn,
                  String arvo) {
    this.uri = uri;
    this.nimiFi = nimiFi;
    this.nimiSv = nimiSv;
    this.nimiEn = nimiEn;
    this.arvo = arvo;
  }

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
