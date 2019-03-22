package fi.vm.sade.service.valintaperusteet.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.LinkedHashSet;

@ApiModel(value = "ValinnanVaiheDTO", description = "Valinnan vaihe")
public class ValinnanVaiheJonoillaDTO extends ValinnanVaiheCreateDTO {

    @ApiModelProperty(value = "OID", required = true)
    private String oid;

    @ApiModelProperty(value = "Onko valinnan vaihe peritty")
    private Boolean inheritance;

    @ApiModelProperty(value = "Valinnan vaiheen valintatapajonot prioriteettijärjestyksessä")
    private LinkedHashSet<ValintatapajonoDTO> jonot = new LinkedHashSet<>();

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

    public LinkedHashSet<ValintatapajonoDTO> getJonot() {
        return jonot;
    }

    public void setJonot(LinkedHashSet<ValintatapajonoDTO> jonot) {
        this.jonot = jonot;
    }
}
