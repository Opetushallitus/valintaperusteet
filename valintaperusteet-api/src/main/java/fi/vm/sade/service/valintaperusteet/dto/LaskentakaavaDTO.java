package fi.vm.sade.service.valintaperusteet.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "LaskentakaavaDTO", description = "Laskentakaava")
public class LaskentakaavaDTO extends LaskentakaavaCreateDTO {

  @Schema(description = "ID", required = true)
  private Long id;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }
}
