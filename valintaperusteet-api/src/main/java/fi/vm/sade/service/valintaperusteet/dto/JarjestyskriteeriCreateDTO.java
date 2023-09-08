package fi.vm.sade.service.valintaperusteet.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "JarjestyskriteeriCreateDTO", description = "Järjestyskriteeri")
public class JarjestyskriteeriCreateDTO {

  @Schema(description = "Metatiedot")
  private String metatiedot;

  @Schema(description = "Aktiivinen", required = true)
  private Boolean aktiivinen;

  public String getMetatiedot() {
    return metatiedot;
  }

  public void setMetatiedot(String metatiedot) {
    this.metatiedot = metatiedot;
  }

  public Boolean getAktiivinen() {
    return aktiivinen;
  }

  public void setAktiivinen(Boolean aktiivinen) {
    this.aktiivinen = aktiivinen;
  }
}
