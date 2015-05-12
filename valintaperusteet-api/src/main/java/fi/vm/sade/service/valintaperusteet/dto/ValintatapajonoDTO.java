package fi.vm.sade.service.valintaperusteet.dto;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * User: wuoti Date: 27.11.2013 Time: 14.06
 */
@ApiModel(value = "ValintatapajonoDTO", description = "Valintatapajono")
public class ValintatapajonoDTO extends ValintatapajonoCreateDTO {

    @ApiModelProperty(value = "OID", required = true)
    private String oid;

    @ApiModelProperty(value = "Onko valintapajono peritty")
    private Boolean inheritance;

    @ApiModelProperty(value = "Valintapajonon prioriteetti")
    private int prioriteetti;

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

    public void setPrioriteetti(int prioriteetti) {
        this.prioriteetti = prioriteetti;
    }

    public int getPrioriteetti() {
        return prioriteetti;
    }
}
