package fi.vm.sade.service.valintaperusteet.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

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
