package fi.vm.sade.service.valintaperusteet.dto;

import fi.vm.sade.service.valintaperusteet.dto.model.ValinnanVaiheTyyppi;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "ValinnanVaiheCreateDTO", description = "Valinnan vaihe")
public class ValinnanVaiheCreateDTO {

  @ApiModelProperty(value = "Nimi", required = true)
  private String nimi;

  @ApiModelProperty(value = "Kuvaus")
  private String kuvaus;

  @ApiModelProperty(value = "Aktiivinen", required = true)
  private Boolean aktiivinen;

  @ApiModelProperty(value = "Valinnan vaiheen tyyppi", required = true)
  private ValinnanVaiheTyyppi valinnanVaiheTyyppi;

  public String getNimi() {
    return nimi;
  }

  public void setNimi(String nimi) {
    this.nimi = nimi;
  }

  public String getKuvaus() {
    return kuvaus;
  }

  public void setKuvaus(String kuvaus) {
    this.kuvaus = kuvaus;
  }

  public Boolean getAktiivinen() {
    return aktiivinen;
  }

  public void setAktiivinen(Boolean aktiivinen) {
    this.aktiivinen = aktiivinen;
  }

  public ValinnanVaiheTyyppi getValinnanVaiheTyyppi() {
    return valinnanVaiheTyyppi;
  }

  public void setValinnanVaiheTyyppi(ValinnanVaiheTyyppi valinnanVaiheTyyppi) {
    this.valinnanVaiheTyyppi = valinnanVaiheTyyppi;
  }
}
