package fi.vm.sade.service.valintaperusteet.dto;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * User: wuoti Date: 29.11.2013 Time: 9.18
 */
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
