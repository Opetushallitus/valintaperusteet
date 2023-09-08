package fi.vm.sade.service.valintaperusteet.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "SyoteparametriDTO", description = "Syöteparametri")
public class SyoteparametriDTO {

  @Schema(description = "Avain", required = true)
  private String avain;

  @Schema(description = "Arvo", required = true)
  private String arvo;

  public SyoteparametriDTO() {}

  public SyoteparametriDTO(final String avain, final String arvo) {
    this.avain = avain;
    this.arvo = arvo;
  }

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
