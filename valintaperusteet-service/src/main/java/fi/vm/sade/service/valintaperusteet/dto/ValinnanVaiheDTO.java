package fi.vm.sade.service.valintaperusteet.dto;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import fi.vm.sade.service.valintaperusteet.model.ValinnanVaiheTyyppi;
import org.codehaus.jackson.map.annotate.JsonView;

/**
 * User: wuoti
 * Date: 27.11.2013
 * Time: 18.00
 */
@ApiModel(value = "ValinnanVaiheDTO", description = "Valinnan vaihe")
public class ValinnanVaiheDTO {

    @ApiModelProperty(value = "OID", required = true)
    @JsonView(JsonViews.Basic.class)
    private String oid;

    @ApiModelProperty(value = "Nimi", required = true)
    @JsonView(JsonViews.Basic.class)
    private String nimi;

    @ApiModelProperty(value = "Kuvaus")
    @JsonView(JsonViews.Basic.class)
    private String kuvaus;

    @ApiModelProperty(value = "Aktiivinen", required = true)
    @JsonView(JsonViews.Basic.class)
    private Boolean aktiivinen;

    @JsonView(JsonViews.Basic.class)
    @ApiModelProperty(value = "Valinnan vaiheen tyyppi", required = true)
    private ValinnanVaiheTyyppi valinnanVaiheTyyppi;

    @JsonView(JsonViews.Basic.class)
    @ApiModelProperty(value = "Onko valinnan vaihe peritty")
    private Boolean inheritance;

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

    public Boolean getAktiivinen() {
        return aktiivinen;
    }

    public void setAktiivinen(Boolean aktiivinen) {
        this.aktiivinen = aktiivinen;
    }

    public ValinnanVaiheTyyppi getValinnanVaiheTyyppi() {
        return valinnanVaiheTyyppi;
    }

    public void setValinnanVaiheTyyppi(ValinnanVaiheTyyppi valinnanVaiheTyyppi) {
        this.valinnanVaiheTyyppi = valinnanVaiheTyyppi;
    }

    public Boolean getInheritance() {
        return inheritance;
    }

    public void setInheritance(Boolean inheritance) {
        this.inheritance = inheritance;
    }
}
