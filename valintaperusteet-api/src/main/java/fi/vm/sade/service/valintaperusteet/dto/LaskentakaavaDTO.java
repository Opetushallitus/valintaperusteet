package fi.vm.sade.service.valintaperusteet.dto;

import fi.vm.sade.service.valintaperusteet.dto.model.Funktiotyyppi;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "LaskentakaavaDTO", description = "Laskentakaava")
public class LaskentakaavaDTO extends LaskentakaavaCreateDTO {

  @ApiModelProperty(value = "ID", required = true)
  private Long id;

  public LaskentakaavaDTO() { }

  public LaskentakaavaDTO(Long id,
                          Boolean onLuonnos,
                          String nimi,
                          String kuvaus,
                          FunktiokutsuDTO funktiokutsu) {
    super(onLuonnos, nimi, kuvaus, funktiokutsu);
    this.id = id;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }
}
