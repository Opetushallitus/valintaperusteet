package fi.vm.sade.service.valintaperusteet.dto;

import fi.vm.sade.service.valintaperusteet.dto.model.Funktiotyyppi;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "LaskentakaavaDTO", description = "Laskentakaava")
public abstract class AbstractLaskentakaavaDTO {

  @ApiModelProperty(value = "Onko laskentakaava luonnos vai valmis", required = true)
  private Boolean onLuonnos;

  @ApiModelProperty(value = "Nimi", required = true)
  private String nimi;

  @ApiModelProperty(value = "Kuvaus")
  private String kuvaus;

  @ApiModelProperty(value = "Laskentakaavan tyyppi", required = true)
  private fi.vm.sade.service.valintaperusteet.dto.model.Funktiotyyppi tyyppi;

  protected AbstractLaskentakaavaDTO() { }

  protected AbstractLaskentakaavaDTO(Boolean onLuonnos,
                                     String nimi,
                                     String kuvaus,
                                     Funktiotyyppi tyyppi) {
    this.onLuonnos = onLuonnos;
    this.nimi = nimi;
    this.kuvaus = kuvaus;
    this.tyyppi = tyyppi;
  }

  public Boolean getOnLuonnos() {
    return onLuonnos;
  }

  public void setOnLuonnos(Boolean onLuonnos) {
    this.onLuonnos = onLuonnos;
  }

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

  public Funktiotyyppi getTyyppi() {
    return tyyppi;
  }

  public void setTyyppi(Funktiotyyppi tyyppi) {
    this.tyyppi = tyyppi;
  }
}
