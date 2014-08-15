package fi.vm.sade.service.valintaperusteet.dto;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * User: wuoti Date: 4.12.2013 Time: 14.19
 */
@ApiModel(value = "LaskentakaavaSiirraDTO", description = "Laskentakaavan siirto DTO")
public class LaskentakaavaSiirraDTO extends LaskentakaavaCreateDTO{

    public LaskentakaavaSiirraDTO() {
    }

    @ApiModelProperty(value = "Nimi")
    private String uusinimi;

    @ApiModelProperty(value = "Valintaryhmä OID, jolle laskentakaava lisätään")
    private String valintaryhmaOid;

    public String getValintaryhmaOid() {
        return valintaryhmaOid;
    }

    public void setValintaryhmaOid(String valintaryhmaOid) {
        this.valintaryhmaOid = valintaryhmaOid;
    }

    public String getUusinimi() {
        return uusinimi;
    }

    public void setUusinimi(String uusinimi) {
        this.uusinimi = uusinimi;
    }
}
