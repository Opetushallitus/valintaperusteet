package fi.vm.sade.service.valintaperusteet.dto;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import org.codehaus.jackson.map.annotate.JsonView;

/**
 * User: wuoti
 * Date: 4.12.2013
 * Time: 14.19
 */
@ApiModel(value = "LaskentakaavaInsertDTO", description = "Laskentakaava")
public class LaskentakaavaInsertDTO {

    public LaskentakaavaInsertDTO() {
    }

    public LaskentakaavaInsertDTO(LaskentakaavaCreateDTO laskentakaava, String hakukohdeOid, String valintaryhmaOid) {
        this.laskentakaava = laskentakaava;
        this.hakukohdeOid = hakukohdeOid;
        this.valintaryhmaOid = valintaryhmaOid;
    }

    @JsonView(JsonViews.Basic.class)
    @ApiModelProperty(value = "Laskentakaava", required = true)
    private LaskentakaavaCreateDTO laskentakaava;

    @JsonView(JsonViews.Basic.class)
    @ApiModelProperty(value = "Hakukohde OID, jolle laskentakaava lisätään")
    private String hakukohdeOid;

    @JsonView(JsonViews.Basic.class)
    @ApiModelProperty(value = "Valintaryhmä OID, jolle laskentakaava lisätään")
    private String valintaryhmaOid;

    public LaskentakaavaCreateDTO getLaskentakaava() {
        return laskentakaava;
    }

    public void setLaskentakaava(LaskentakaavaCreateDTO laskentakaava) {
        this.laskentakaava = laskentakaava;
    }

    public String getHakukohdeOid() {
        return hakukohdeOid;
    }

    public void setHakukohdeOid(String hakukohdeOid) {
        this.hakukohdeOid = hakukohdeOid;
    }

    public String getValintaryhmaOid() {
        return valintaryhmaOid;
    }

    public void setValintaryhmaOid(String valintaryhmaOid) {
        this.valintaryhmaOid = valintaryhmaOid;
    }
}
