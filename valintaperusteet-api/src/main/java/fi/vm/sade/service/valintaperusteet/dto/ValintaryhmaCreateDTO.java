package fi.vm.sade.service.valintaperusteet.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Schema(name = "ValintaryhmaCreateDTO", description = "Valintaryhmä")
public class ValintaryhmaCreateDTO extends AbstractValintaryhmaDTO {
  @Schema(description = "Organisaatiot")
  private Set<OrganisaatioDTO> organisaatiot = new HashSet<OrganisaatioDTO>();

  @Schema(description = "VastuuorganisaatioOid")
  private String vastuuorganisaatioOid;

  @Schema(description = "Viimeinen päivämäärä, jolloin valinta-ajon voi käynnistää")
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
