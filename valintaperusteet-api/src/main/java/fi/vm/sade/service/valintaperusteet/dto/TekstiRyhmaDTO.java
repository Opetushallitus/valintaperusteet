package fi.vm.sade.service.valintaperusteet.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.HashSet;
import java.util.Set;

@Schema(name = "TekstiRyhmaDTO", description = "Tekstiryhmä")
public class TekstiRyhmaDTO {

  @Schema(description = "Lokalisoidut tekstit", required = true)
  private Set<LokalisoituTekstiDTO> tekstit = new HashSet<LokalisoituTekstiDTO>();

  public Set<LokalisoituTekstiDTO> getTekstit() {
    return tekstit;
  }

  public void setTekstit(Set<LokalisoituTekstiDTO> tekstit) {
    this.tekstit = tekstit;
  }
}
