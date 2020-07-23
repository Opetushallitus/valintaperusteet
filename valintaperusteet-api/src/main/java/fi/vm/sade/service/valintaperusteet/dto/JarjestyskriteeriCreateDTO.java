package fi.vm.sade.service.valintaperusteet.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "JarjestyskriteeriCreateDTO", description = "Järjestyskriteeri")
public class JarjestyskriteeriCreateDTO {

  @ApiModelProperty(value = "Metatiedot")
  private String metatiedot;

  @ApiModelProperty(value = "Aktiivinen", required = true)
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
