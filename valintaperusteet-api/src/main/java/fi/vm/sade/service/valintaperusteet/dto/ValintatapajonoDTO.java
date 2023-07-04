package fi.vm.sade.service.valintaperusteet.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "service.valintaperusteet.dto.ValintatapajonoDTO", description = "Valintatapajono")
public class ValintatapajonoDTO extends ValintatapajonoCreateDTO implements Prioritized {
  @Schema(description = "OID", required = true)
  private String oid;

  @Schema(description = "Onko valintapajono peritty")
  private Boolean inheritance;

  @Schema(description = "Valintapajonon prioriteetti")
  private int prioriteetti;

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

  public void setPrioriteetti(int prioriteetti) {
    this.prioriteetti = prioriteetti;
  }

  public int getPrioriteetti() {
    return prioriteetti;
  }

  @Override
  public String toString() {
    return "ValintatapajonoDTO{"
        + "oid='"
        + oid
        + '\''
        + ", inheritance="
        + inheritance
        + ", prioriteetti="
        + prioriteetti
        + super.toString()
        + '}';
  }
}
