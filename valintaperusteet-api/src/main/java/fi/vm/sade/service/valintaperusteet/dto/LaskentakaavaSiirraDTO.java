package fi.vm.sade.service.valintaperusteet.dto;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@ApiModel(value = "LaskentakaavaSiirraDTO", description = "Laskentakaavan siirto DTO")
public class LaskentakaavaSiirraDTO extends LaskentakaavaCreateDTO {

    public LaskentakaavaSiirraDTO() {
    }

    @ApiModelProperty(value = "Nimi")
    private String uusinimi;

    @ApiModelProperty(value = "Valintaryhmä OID, jolle laskentakaava lisätään")
    private String valintaryhmaOid;

    @ApiModelProperty(value = "Hakukohde OID, jolle laskentakaava lisätään")
    private String hakukohdeOid;

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

    public String getHakukohdeOid() {
        return hakukohdeOid;
    }

    public void setHakukohdeOid(String hakukohdeOid) {
        this.hakukohdeOid = hakukohdeOid;
    }
}
