package fi.vm.sade.service.valintaperusteet.dto;

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
}
