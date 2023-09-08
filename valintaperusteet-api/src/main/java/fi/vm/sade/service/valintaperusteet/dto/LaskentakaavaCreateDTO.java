package fi.vm.sade.service.valintaperusteet.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "LaskentakaavaCreateDTO", description = "Laskentakaava")
public class LaskentakaavaCreateDTO extends AbstractLaskentakaavaDTO {

  @Schema(description = "Nimi", required = true)
  private FunktiokutsuDTO funktiokutsu;

  public FunktiokutsuDTO getFunktiokutsu() {
    return funktiokutsu;
  }

  public void setFunktiokutsu(FunktiokutsuDTO funktiokutsu) {
    this.funktiokutsu = funktiokutsu;
  }
}
