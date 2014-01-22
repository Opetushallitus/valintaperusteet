package fi.vm.sade.service.valintaperusteet.dto;

import java.util.HashSet;
import java.util.Set;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * User: wuoti Date: 2.12.2013 Time: 14.08
 */
@ApiModel(value = "ValintaryhmaCreateDTO", description = "Valintaryhmä")
public class ValintaryhmaCreateDTO extends AbstractValintaryhmaDTO {

    @ApiModelProperty(value = "Organisaatiot")
    private Set<OrganisaatioDTO> organisaatiot = new HashSet<OrganisaatioDTO>();

    public Set<OrganisaatioDTO> getOrganisaatiot() {
        return organisaatiot;
    }

    public void setOrganisaatiot(Set<OrganisaatioDTO> organisaatiot) {
        this.organisaatiot = organisaatiot;
    }
}
