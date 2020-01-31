package fi.vm.sade.service.valintaperusteet.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.codehaus.jackson.annotate.JsonProperty;

@ApiModel(value = "HakuViiteDTO", description = "Haku")
public class HakuViiteDTO {

    @ApiModelProperty(value = "Haku OID", required = true)
    private String hakuoid;

    public String getHakuoid() {
        return hakuoid;
    }

    public void setHakuoid(String hakuoid) {
        this.hakuoid = hakuoid;
    }
}
