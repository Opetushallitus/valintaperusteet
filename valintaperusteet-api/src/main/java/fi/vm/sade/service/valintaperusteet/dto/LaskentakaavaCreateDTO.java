package fi.vm.sade.service.valintaperusteet.dto;

import fi.vm.sade.service.valintaperusteet.dto.model.Funktiotyyppi;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "LaskentakaavaCreateDTO", description = "Laskentakaava")
public class LaskentakaavaCreateDTO extends AbstractLaskentakaavaDTO {

  @ApiModelProperty(value = "Nimi", required = true)
  private FunktiokutsuDTO funktiokutsu;

  public FunktiokutsuDTO getFunktiokutsu() {
    return funktiokutsu;
  }

  public void setFunktiokutsu(FunktiokutsuDTO funktiokutsu) {
    this.funktiokutsu = funktiokutsu;
  }

  public LaskentakaavaCreateDTO() {
    super();
  }

  public LaskentakaavaCreateDTO(Boolean onLuonnos,
                                String nimi,
                                String kuvaus,
                                FunktiokutsuDTO funktiokutsu) {
    super(onLuonnos, nimi, kuvaus, funktiokutsu.getFunktionimi().getTyyppi());
    this.funktiokutsu = funktiokutsu;
  }
}
