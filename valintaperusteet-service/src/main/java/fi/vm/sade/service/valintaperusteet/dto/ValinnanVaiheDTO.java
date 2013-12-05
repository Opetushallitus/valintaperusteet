package fi.vm.sade.service.valintaperusteet.dto;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import org.codehaus.jackson.map.annotate.JsonView;

/**
 * User: wuoti
 * Date: 27.11.2013
 * Time: 18.00
 */
@ApiModel(value = "ValinnanVaiheDTO", description = "Valinnan vaihe")
public class ValinnanVaiheDTO extends ValinnanVaiheCreateDTO {

    @ApiModelProperty(value = "OID", required = true)
    @JsonView(JsonViews.Basic.class)
    private String oid;

    @JsonView(JsonViews.Basic.class)
    @ApiModelProperty(value = "Onko valinnan vaihe peritty")
    private Boolean inheritance;

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
}
