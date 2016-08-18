package fi.vm.sade.service.valintaperusteet.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "LaskentakaavaDTO", description = "Laskentakaava")
public class LaskentakaavaDTO extends LaskentakaavaCreateDTO {

    @ApiModelProperty(value = "ID", required = true)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
