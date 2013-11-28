package fi.vm.sade.service.valintaperusteet.dto;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import org.codehaus.jackson.map.annotate.JsonView;

/**
 * User: wuoti
 * Date: 27.11.2013
 * Time: 14.02
 */
@ApiModel(value = "HakijaryhmaValintatapajonoDTO", description = "Hakijaryhmän liittyminen valintatapajonoon")
public class HakijaryhmaValintatapajonoUpdateDTO {
    @ApiModelProperty(value = "Hakijaryhmä", required = true)
    @JsonView(JsonViews.Basic.class)
    private HakijaryhmaDTO hakijaryhma;

    @ApiModelProperty(value = "Valintatapajono", required = true)
    @JsonView(JsonViews.Basic.class)
    private ValintatapajonoDTO valintatapajono;

    @ApiModelProperty(value = "Aktiivinen", required = true)
    @JsonView(JsonViews.Basic.class)
    private Boolean aktiivinen;

    public HakijaryhmaDTO getHakijaryhma() {
        return hakijaryhma;
    }

    public void setHakijaryhma(HakijaryhmaDTO hakijaryhma) {
        this.hakijaryhma = hakijaryhma;
    }

    public ValintatapajonoDTO getValintatapajono() {
        return valintatapajono;
    }

    public void setValintatapajono(ValintatapajonoDTO valintatapajono) {
        this.valintatapajono = valintatapajono;
    }

    public Boolean getAktiivinen() {
        return aktiivinen;
    }

    public void setAktiivinen(Boolean aktiivinen) {
        this.aktiivinen = aktiivinen;
    }
}
