package fi.vm.sade.service.valintaperusteet.dto;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import org.codehaus.jackson.map.annotate.JsonView;

/**
 * User: wuoti
 * Date: 2.12.2013
 * Time: 9.52
 */
@ApiModel(value = "SyoteparametriDTO", description = "Syöteparametri")
public class SyoteparametriDTO {


    @ApiModelProperty(value = "Avain", required = true)
    private String avain;

    @ApiModelProperty(value = "Arvo", required = true)
    private String arvo;

    public String getAvain() {
        return avain;
    }

    public void setAvain(String avain) {
        this.avain = avain;
    }

    public String getArvo() {
        return arvo;
    }

    public void setArvo(String arvo) {
        this.arvo = arvo;
    }
}
