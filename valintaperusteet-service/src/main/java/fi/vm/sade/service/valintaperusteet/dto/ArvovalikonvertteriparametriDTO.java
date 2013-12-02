package fi.vm.sade.service.valintaperusteet.dto;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import org.codehaus.jackson.map.annotate.JsonView;

/**
 * User: wuoti
 * Date: 2.12.2013
 * Time: 9.49
 */
@ApiModel(value = "ArvovalikonvertteriparametriDTO", description = "Arvovälikonvertteriparametri")
public class ArvovalikonvertteriparametriDTO {
    @JsonView(JsonViews.Basic.class)
    @ApiModelProperty(value = "Paluuarvo")
    private String paluuarvo;

    @JsonView(JsonViews.Basic.class)
    @ApiModelProperty(value = "Minimiarvo", required = true)
    private String minValue;

    @JsonView(JsonViews.Basic.class)
    @ApiModelProperty(value = "Maksimiarvo", required = true)
    private String maxValue;

    @JsonView(JsonViews.Basic.class)
    @ApiModelProperty(value = "Palautetaanko haettu arvo")
    private String palautaHaettuArvo;

    public String getPaluuarvo() {
        return paluuarvo;
    }

    public void setPaluuarvo(String paluuarvo) {
        this.paluuarvo = paluuarvo;
    }

    public String getMinValue() {
        return minValue;
    }

    public void setMinValue(String minValue) {
        this.minValue = minValue;
    }

    public String getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(String maxValue) {
        this.maxValue = maxValue;
    }

    public String getPalautaHaettuArvo() {
        return palautaHaettuArvo;
    }

    public void setPalautaHaettuArvo(String palautaHaettuArvo) {
        this.palautaHaettuArvo = palautaHaettuArvo;
    }
}
