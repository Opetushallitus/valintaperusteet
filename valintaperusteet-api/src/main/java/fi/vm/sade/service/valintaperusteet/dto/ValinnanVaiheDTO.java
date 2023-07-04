package fi.vm.sade.service.valintaperusteet.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.HashSet;
import java.util.Set;

@Schema(name = "service.valintaperusteet.dto.ValinnanVaiheDTO", description = "Valinnan vaihe")
public class ValinnanVaiheDTO extends ValinnanVaiheCreateDTO {

  @Schema(description = "OID", required = true)
  private String oid;

  @Schema(description = "Onko valinnan vaihe peritty")
  private Boolean inheritance;

  @Schema(description = "Onko valinnanvaiheella välisijoittelua")
  private Boolean hasValisijoittelu;

  @Schema(description = "Valinnanvaiheen valintatapajonot")
  private Set<ValintatapajonoDTO> jonot = new HashSet<>();

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

  public void setHasValisijoittelu(Boolean hasValisijoittelu) {
    this.hasValisijoittelu = hasValisijoittelu;
  }

  public Boolean getHasValisijoittelu() {
    return this.hasValisijoittelu;
  }

  public Set<ValintatapajonoDTO> getJonot() {
    return jonot;
  }

  public void setJonot(Set<ValintatapajonoDTO> jonot) {
    this.jonot = jonot;
  }
}
