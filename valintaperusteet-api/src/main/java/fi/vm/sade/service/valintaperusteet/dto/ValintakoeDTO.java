package fi.vm.sade.service.valintaperusteet.dto;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * User: kwuoti Date: 16.4.2013 Time: 13.01
 */
@ApiModel(value = "ValintakoeDTO", description = "Valintakoe")
public class ValintakoeDTO extends ValintakoeCreateDTO {

    @ApiModelProperty(value = "OID", required = true)
    private String oid;

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }
}
