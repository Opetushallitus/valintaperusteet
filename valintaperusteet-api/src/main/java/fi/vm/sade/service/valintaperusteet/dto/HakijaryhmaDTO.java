package fi.vm.sade.service.valintaperusteet.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;

@ApiModel(value = "HakijaryhmaDTO", description = "Hakijaryhmä")
public class HakijaryhmaDTO extends HakijaryhmaCreateDTO {
    @ApiModelProperty(value = "OID", required = true)
    private String oid;

    @ApiModelProperty(value = "Hakijaryhmatyyppikoodi")
    private List<KoodiDTO> hakijaryhmatyyppikoodit = new ArrayList<KoodiDTO>();

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

}
