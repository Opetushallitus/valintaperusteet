package fi.vm.sade.service.valintaperusteet.dto;

import io.swagger.annotations.ApiModel;

@ApiModel(value = "AvainArvoDTO", description = "")
public class AvainArvoDTO {
  private String avain;
  private String arvo;

  public String getAvain() {
    return avain;
  }

  public void setAvain(String avain) {
    this.avain = avain;
  }

  public String getArvo() {
    return arvo;
  }

  public void setArvo(String arvo) {
    this.arvo = arvo;
  }
}
