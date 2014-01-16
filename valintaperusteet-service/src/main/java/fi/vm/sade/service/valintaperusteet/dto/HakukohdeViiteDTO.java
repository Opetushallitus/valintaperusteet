package fi.vm.sade.service.valintaperusteet.dto;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * User: jukais
 * Date: 8.2.2013
 * Time: 13.34
 */
@ApiModel(value = "HakukohdeViiteDTO", description = "Hakukohde")
public class HakukohdeViiteDTO extends HakukohdeViiteCreateDTO {

    @ApiModelProperty(value = "Valintaryhmä OID")
    @JsonProperty(value = "valintaryhma_id")
    private String valintaryhmaOid;

    @ApiModelProperty(value = "Hakukohdekoodi")
    private KoodiDTO hakukohdekoodi;


    public String getValintaryhmaOid() {
        return valintaryhmaOid;
    }

    public void setValintaryhmaOid(String valintaryhmaOid) {
        this.valintaryhmaOid = valintaryhmaOid;
    }

    public KoodiDTO getHakukohdekoodi() {
        return hakukohdekoodi;
    }

    public void setHakukohdekoodi(KoodiDTO hakukohdekoodi) {
        this.hakukohdekoodi = hakukohdekoodi;
    }
}
