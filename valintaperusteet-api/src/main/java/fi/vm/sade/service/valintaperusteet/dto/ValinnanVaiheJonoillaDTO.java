package fi.vm.sade.service.valintaperusteet.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.LinkedHashSet;

@Schema(name = "ValinnanVaiheDTO", description = "Valinnan vaihe")
public class ValinnanVaiheJonoillaDTO extends ValinnanVaiheCreateDTO {

  @Schema(description = "OID", required = true)
  private String oid;

  @Schema(description = "Onko valinnan vaihe peritty")
  private Boolean inheritance;

  @Schema(description = "Valinnan vaiheen valintatapajonot prioriteettijärjestyksessä")
  private LinkedHashSet<ValintatapajonoDTO> jonot = new LinkedHashSet<>();

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

  public LinkedHashSet<ValintatapajonoDTO> getJonot() {
    return jonot;
  }

  public void setJonot(LinkedHashSet<ValintatapajonoDTO> jonot) {
    this.jonot = jonot;
  }
}
