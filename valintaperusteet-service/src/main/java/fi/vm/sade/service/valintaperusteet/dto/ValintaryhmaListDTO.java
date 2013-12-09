package fi.vm.sade.service.valintaperusteet.dto;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import org.codehaus.jackson.map.annotate.JsonView;

/**
 * User: wuoti
 * Date: 2.12.2013
 * Time: 14.08
 */
@ApiModel(value = "ValintaryhmaListDTO", description = "Valintaryhmä")
public class ValintaryhmaListDTO extends AbstractValintaryhmaDTO {

    @ApiModelProperty(value = "OID", required = true)
    private String oid;

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }
}
