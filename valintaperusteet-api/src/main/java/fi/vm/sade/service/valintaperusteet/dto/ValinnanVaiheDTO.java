package fi.vm.sade.service.valintaperusteet.dto;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@ApiModel(value = "ValinnanVaiheDTO", description = "Valinnan vaihe")
public class ValinnanVaiheDTO extends ValinnanVaiheCreateDTO {

    @ApiModelProperty(value = "OID", required = true)
    private String oid;

    @ApiModelProperty(value = "Onko valinnan vaihe peritty")
    private Boolean inheritance;

    @ApiModelProperty(value = "Onko valinnanvaiheella välisijoittelua")
    private Boolean hasValisijoittelu;

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public Boolean getInheritance() {
        return inheritance;
    }

    public void setInheritance(Boolean inheritance) {
        this.inheritance = inheritance;
    }

    public void setHasValisijoittelu(Boolean hasValisijoittelu) {
        this.hasValisijoittelu = hasValisijoittelu;
    }

    public Boolean getHasValisijoittelu() {
        return this.hasValisijoittelu;
    }
}
