package fi.vm.sade.service.valintaperusteet.dto;

import fi.vm.sade.service.valintaperusteet.dto.model.Kieli;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "KuvausDTO", description = "Kuvaus")
public class KuvausDTO {

  @Schema(description = "Kuvauksen kieli", required = true)
  private Kieli kieli;

  @Schema(description = "Teksti", required = true)
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
