package fi.vm.sade.service.valintaperusteet.dto;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import org.codehaus.jackson.map.annotate.JsonView;

/**
 * User: wuoti
 * Date: 2.12.2013
 * Time: 14.08
 */
@ApiModel(value = "AbstractValintaryhmaDTO", description = "Valintaryhmä")
public abstract class AbstractValintaryhmaDTO {
    @JsonView({JsonViews.Basic.class, JsonViews.ParentHierarchy.class})
    @ApiModelProperty(value = "Nimi", required = true)
    private String nimi;

    public String getNimi() {
        return nimi;
    }

    public void setNimi(String nimi) {
        this.nimi = nimi;
    }
}
