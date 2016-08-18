package fi.vm.sade.service.valintaperusteet.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import fi.vm.sade.service.valintaperusteet.dto.model.Kieli;

import java.util.HashSet;
import java.util.Set;

@ApiModel(value = "TekstiRyhmaDTO", description = "Tekstiryhmä")
public class TekstiRyhmaDTO {

    @ApiModelProperty(value = "Lokalisoidut tekstit", required = true)
    private Set<LokalisoituTekstiDTO> tekstit = new HashSet<LokalisoituTekstiDTO>();

    public Set<LokalisoituTekstiDTO> getTekstit() {
        return tekstit;
    }

    public void setTekstit(Set<LokalisoituTekstiDTO> tekstit) {
        this.tekstit = tekstit;
    }
}
