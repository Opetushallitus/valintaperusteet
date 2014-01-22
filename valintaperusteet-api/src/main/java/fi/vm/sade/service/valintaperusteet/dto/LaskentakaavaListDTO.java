package fi.vm.sade.service.valintaperusteet.dto;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * User: wuoti Date: 29.11.2013 Time: 9.18
 */
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
