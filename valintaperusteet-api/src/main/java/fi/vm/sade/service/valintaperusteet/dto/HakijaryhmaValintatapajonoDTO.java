package fi.vm.sade.service.valintaperusteet.dto;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@ApiModel(value = "HakijaryhmaValintatapajonoDTO", description = "Hakijaryhmän liittyminen valintatapajonoon")
public class HakijaryhmaValintatapajonoDTO extends HakijaryhmaValintatapajonoUpdateDTO {
    @ApiModelProperty(value = "OID", required = true)
    private String oid;

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }
}
