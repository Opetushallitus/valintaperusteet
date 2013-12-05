package fi.vm.sade.service.valintaperusteet.dto;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import org.codehaus.jackson.map.annotate.JsonView;

/**
 * User: kwuoti
 * Date: 16.4.2013
 * Time: 13.01
 */
@ApiModel(value = "ValintakoeCreateDTO", description = "Valintakoe")
public class ValintakoeCreateDTO {

    @JsonView(JsonViews.Basic.class)
    @ApiModelProperty(value = "Tunniste", required = true)
    private String tunniste;

    @JsonView(JsonViews.Basic.class)
    @ApiModelProperty(value = "Laskentakaava ID", required = true)
    private Long laskentakaavaId;

    @JsonView(JsonViews.Basic.class)
    @ApiModelProperty(value = "Nimi", required = true)
    private String nimi;

    @JsonView(JsonViews.Basic.class)
    @ApiModelProperty(value = "Kuvaus")
    private String kuvaus;

    @JsonView(JsonViews.Basic.class)
    @ApiModelProperty(value = "Onko valintakoe aktiivinen", required = true)
    private Boolean aktiivinen;

    public String getTunniste() {
        return tunniste;
    }

    public void setTunniste(String tunniste) {
        this.tunniste = tunniste;
    }

    public Long getLaskentakaavaId() {
        return laskentakaavaId;
    }

    public void setLaskentakaavaId(Long laskentakaavaId) {
        this.laskentakaavaId = laskentakaavaId;
    }

    public String getKuvaus() {
        return kuvaus;
    }

    public void setKuvaus(String kuvaus) {
        this.kuvaus = kuvaus;
    }

    public String getNimi() {
        return nimi;
    }

    public void setNimi(String nimi) {
        this.nimi = nimi;
    }

    public Boolean getAktiivinen() {
        return aktiivinen;
    }

    public void setAktiivinen(Boolean aktiivinen) {
        this.aktiivinen = aktiivinen;
    }
}
