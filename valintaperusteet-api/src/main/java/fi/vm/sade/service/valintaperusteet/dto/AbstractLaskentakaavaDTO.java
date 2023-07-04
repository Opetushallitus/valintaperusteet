package fi.vm.sade.service.valintaperusteet.dto;

import fi.vm.sade.service.valintaperusteet.dto.model.Funktiotyyppi;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "LaskentakaavaDTO", description = "Laskentakaava")
public abstract class AbstractLaskentakaavaDTO {

  @Schema(description = "Onko laskentakaava luonnos vai valmis", required = true)
  private Boolean onLuonnos;

  @Schema(description = "Nimi", required = true)
  private String nimi;

  @Schema(description = "Kuvaus")
  private String kuvaus;

  @Schema(description = "Laskentakaavan tyyppi", required = true)
  private fi.vm.sade.service.valintaperusteet.dto.model.Funktiotyyppi tyyppi;

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
