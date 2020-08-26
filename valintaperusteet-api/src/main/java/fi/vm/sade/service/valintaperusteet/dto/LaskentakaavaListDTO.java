package fi.vm.sade.service.valintaperusteet.dto;

import fi.vm.sade.service.valintaperusteet.dto.model.Funktiotyyppi;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "LaskentakaavaListDTO", description = "Laskentakaava")
public class LaskentakaavaListDTO extends AbstractLaskentakaavaDTO {

  @ApiModelProperty(value = "ID", required = true)
  private Long id;

  public LaskentakaavaListDTO(Long id,
                              Boolean onLuonnos,
                              String nimi,
                              String kuvaus,
                              Funktiotyyppi tyyppi) {
    super(onLuonnos, nimi, kuvaus, tyyppi);
    this.id = id;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }
}
