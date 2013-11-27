package fi.vm.sade.service.valintaperusteet.dto;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import org.codehaus.jackson.map.annotate.JsonView;

/**
 * User: jukais
 * Date: 8.2.2013
 * Time: 13.34
 */
@ApiModel(value = "HakukohdeViiteDTO", description = "Hakukohde")
public class HakukohdeViiteDTO extends HakukohdeViiteCreateDTO {

    @ApiModelProperty(value = "Valintaryhmä OID")
    @JsonView(JsonViews.Basic.class)
    private String valintaryhmaOid;

    @ApiModelProperty(value = "Hakukohdekoodi")
    @JsonView(JsonViews.Basic.class)
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
