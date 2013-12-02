package fi.vm.sade.service.valintaperusteet.dto;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import org.codehaus.jackson.map.annotate.JsonView;

import java.util.HashSet;
import java.util.Set;

/**
 * User: wuoti
 * Date: 2.12.2013
 * Time: 14.08
 */
@ApiModel(value = "ValintaryhmaCreateDTO", description = "Valintaryhmä")
public class ValintaryhmaCreateDTO {
    @JsonView({JsonViews.Basic.class, JsonViews.ParentHierarchy.class})
    @ApiModelProperty(value = "Nimi", required = true)
    private String nimi;

    @JsonView({JsonViews.Basic.class})
    @ApiModelProperty(value = "Organisaatiot")
    private Set<String> organisaatiot = new HashSet<String>();

    public String getNimi() {
        return nimi;
    }

    public void setNimi(String nimi) {
        this.nimi = nimi;
    }

    public Set<String> getOrganisaatiot() {
        return organisaatiot;
    }

    public void setOrganisaatiot(Set<String> organisaatiot) {
        this.organisaatiot = organisaatiot;
    }
}
