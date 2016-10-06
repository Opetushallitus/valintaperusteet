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
    private KoodiDTO hakijaryhmatyyppikoodi = null;

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public KoodiDTO getHakijaryhmatyyppikoodi(){ return hakijaryhmatyyppikoodi; }

    public void setHakijaryhmatyyppikoodi(KoodiDTO hakijaryhmatyyppikoodi){
        this.hakijaryhmatyyppikoodi = hakijaryhmatyyppikoodi;
    }
}
