package fi.vm.sade.service.valintaperusteet.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "LaskentakaavaListDTO", description = "Laskentakaava")
public class LaskentakaavaListDTO extends AbstractLaskentakaavaDTO {

    @ApiModelProperty(value = "ID", required = true)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
