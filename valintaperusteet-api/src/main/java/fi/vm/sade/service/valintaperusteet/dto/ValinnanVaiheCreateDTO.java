package fi.vm.sade.service.valintaperusteet.dto;

import fi.vm.sade.service.valintaperusteet.dto.model.ValinnanVaiheTyyppi;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ValinnanVaiheCreateDTO", description = "Valinnan vaihe")
public class ValinnanVaiheCreateDTO extends AbstractWithModifyTimestamp {

  @Schema(description = "Nimi", required = true)
  private String nimi;

  @Schema(description = "Kuvaus")
  private String kuvaus;

  @Schema(description = "Aktiivinen", required = true)
  private Boolean aktiivinen;

  @Schema(description = "Valinnan vaiheen tyyppi", required = true)
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
