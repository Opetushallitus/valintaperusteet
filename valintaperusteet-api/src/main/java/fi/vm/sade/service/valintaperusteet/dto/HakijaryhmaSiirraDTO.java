package fi.vm.sade.service.valintaperusteet.dto;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@ApiModel(value = "HakijaryhmaDTO", description = "Hakijaryhmä")
public class HakijaryhmaSiirraDTO extends HakijaryhmaCreateDTO {

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
