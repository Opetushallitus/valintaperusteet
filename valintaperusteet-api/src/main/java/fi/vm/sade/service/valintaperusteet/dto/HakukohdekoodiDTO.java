package fi.vm.sade.service.valintaperusteet.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "HakukohdekoodiDTO", description = "")
public class HakukohdekoodiDTO {
  private String koodiUri;
  private String arvo;
  private String nimiFi;
  private String nimiSv;
  private String nimiEn;

  public String getKoodiUri() {
    return koodiUri;
  }

  public void setKoodiUri(String koodiUri) {
    this.koodiUri = koodiUri;
  }

  public String getArvo() {
    return arvo;
  }

  public void setArvo(String arvo) {
    this.arvo = arvo;
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
}
