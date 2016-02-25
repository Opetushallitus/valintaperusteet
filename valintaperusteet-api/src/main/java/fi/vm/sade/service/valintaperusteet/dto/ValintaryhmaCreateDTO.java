package fi.vm.sade.service.valintaperusteet.dto;

import java.util.HashSet;
import java.util.Set;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@ApiModel(value = "ValintaryhmaCreateDTO", description = "Valintaryhmä")
public class ValintaryhmaCreateDTO extends AbstractValintaryhmaDTO {
    @ApiModelProperty(value = "Organisaatiot")
    private Set<OrganisaatioDTO> organisaatiot = new HashSet<OrganisaatioDTO>();

    @ApiModelProperty(value = "VastuuorganisaatioOid")
    private String vastuuorganisaatioOid;

    public Set<OrganisaatioDTO> getOrganisaatiot() {
        return organisaatiot;
    }

    public void setOrganisaatiot(Set<OrganisaatioDTO> organisaatiot) {
        this.organisaatiot = organisaatiot;
    }

    public String getVastuuorganisaatioOid() {
        return vastuuorganisaatioOid;
    }

    public void setVastuuorganisaatioOid(String vastuuorganisaatioOid) {
        this.vastuuorganisaatioOid = vastuuorganisaatioOid;
    }
}
