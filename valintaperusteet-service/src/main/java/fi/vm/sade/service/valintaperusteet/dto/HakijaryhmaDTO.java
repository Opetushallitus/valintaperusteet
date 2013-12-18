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
@ApiModel(value = "HakijaryhmaDTO", description = "Hakijaryhmä")
public class HakijaryhmaDTO extends HakijaryhmaCreateDTO {
    @ApiModelProperty(value = "OID", required = true)
    private String oid;


    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }
}
