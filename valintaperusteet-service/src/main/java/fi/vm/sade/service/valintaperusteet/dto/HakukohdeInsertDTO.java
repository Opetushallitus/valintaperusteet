package fi.vm.sade.service.valintaperusteet.dto;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import org.codehaus.jackson.map.annotate.JsonView;

/**
 * User: wuoti
 * Date: 27.11.2013
 * Time: 17.47
 */
@ApiModel(value = "HakukohdeInsertDTO", description = "Hakukohteen lisääminen")
public class HakukohdeInsertDTO {

    public HakukohdeInsertDTO() {
    }

    public HakukohdeInsertDTO(HakukohdeViiteCreateDTO hakukohde, String valintaryhmaOid) {
        this.hakukohde = hakukohde;
        this.valintaryhmaOid = valintaryhmaOid;
    }

    @JsonView(JsonViews.Basic.class)
    @ApiModelProperty(value = "Lisättävä hakukohde", required = true)
    private HakukohdeViiteCreateDTO hakukohde;

    @JsonView(JsonViews.Basic.class)
    @ApiModelProperty(value = "Valintaryhmä OID, johon hakukohde lisätään", required = true)
    private String valintaryhmaOid;

    public HakukohdeViiteCreateDTO getHakukohde() {
        return hakukohde;
    }

    public void setHakukohde(HakukohdeViiteCreateDTO hakukohde) {
        this.hakukohde = hakukohde;
    }

    public String getValintaryhmaOid() {
        return valintaryhmaOid;
    }

    public void setValintaryhmaOid(String valintaryhmaOid) {
        this.valintaryhmaOid = valintaryhmaOid;
    }
}
