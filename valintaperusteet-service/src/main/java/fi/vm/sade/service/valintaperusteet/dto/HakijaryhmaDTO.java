package fi.vm.sade.service.valintaperusteet.dto;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import org.codehaus.jackson.map.annotate.JsonView;

/**
 * User: wuoti
 * Date: 27.11.2013
 * Time: 12.47
 */
@ApiModel(value = "HakijaryhmaDTO")
public class HakijaryhmaDTO {
    @JsonView(JsonViews.Basic.class)
    @ApiModelProperty(value = "OID", required = true)
    private String oid;

    @JsonView(JsonViews.Basic.class)
    @ApiModelProperty(value = "Nimi", required = true)
    private String nimi;

    @JsonView(JsonViews.Basic.class)
    @ApiModelProperty(value = "Kuvaus")
    private String kuvaus;

    @JsonView(JsonViews.Basic.class)
    @ApiModelProperty(value = "Kiintio", required = true)
    private int kiintio;

    @JsonView(JsonViews.Basic.class)
    @ApiModelProperty(value = "Laskentakaavan ID", required = true)
    private Long laskentakaavaId;

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getNimi() {
        return nimi;
    }

    public void setNimi(String nimi) {
        this.nimi = nimi;
    }

    public String getKuvaus() {
        return kuvaus;
    }

    public void setKuvaus(String kuvaus) {
        this.kuvaus = kuvaus;
    }

    public int getKiintio() {
        return kiintio;
    }

    public void setKiintio(int kiintio) {
        this.kiintio = kiintio;
    }

    public Long getLaskentakaavaId() {
        return laskentakaavaId;
    }

    public void setLaskentakaavaId(Long laskentakaavaId) {
        this.laskentakaavaId = laskentakaavaId;
    }
}
