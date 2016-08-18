package fi.vm.sade.service.valintaperusteet.dto;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "ValintaryhmaCreateDTO", description = "Valintaryhmä")
public class ValintaryhmaCreateDTO extends AbstractValintaryhmaDTO {
    @ApiModelProperty(value = "Organisaatiot")
    private Set<OrganisaatioDTO> organisaatiot = new HashSet<OrganisaatioDTO>();

    @ApiModelProperty(value = "VastuuorganisaatioOid")
    private String vastuuorganisaatioOid;

    @ApiModelProperty(value = "Viimeinen päivämäärä, jolloin valinta-ajon voi käynnistää")
    private Date viimeinenKaynnistyspaiva;

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

    public Date getViimeinenKaynnistyspaiva() {
        return viimeinenKaynnistyspaiva;
    }

    public void setViimeinenKaynnistyspaiva(Date viimeinenKaynnistyspaiva) {
        this.viimeinenKaynnistyspaiva = viimeinenKaynnistyspaiva;
    }
}
