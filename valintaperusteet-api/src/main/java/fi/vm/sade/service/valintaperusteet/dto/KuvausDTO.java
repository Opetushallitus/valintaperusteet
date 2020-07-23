package fi.vm.sade.service.valintaperusteet.dto;

import fi.vm.sade.service.valintaperusteet.dto.model.Kieli;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "KuvausDTO", description = "Kuvaus")
public class KuvausDTO {

  @ApiModelProperty(value = "Kuvauksen kieli", required = true)
  private Kieli kieli;

  @ApiModelProperty(value = "Teksti", required = true)
  private String teksti;

  public Kieli getKieli() {
    return kieli;
  }

  public void setKieli(Kieli kieli) {
    this.kieli = kieli;
  }

  public String getTeksti() {
    return teksti;
  }

  public void setTeksti(String teksti) {
    this.teksti = teksti;
  }
}
