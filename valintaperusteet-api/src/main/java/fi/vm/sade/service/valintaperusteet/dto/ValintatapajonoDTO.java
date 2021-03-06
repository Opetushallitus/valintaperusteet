package fi.vm.sade.service.valintaperusteet.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(
    value = "service.valintaperusteet.dto.ValintatapajonoDTO",
    description = "Valintatapajono")
public class ValintatapajonoDTO extends ValintatapajonoCreateDTO implements Prioritized {
  @ApiModelProperty(value = "OID", required = true)
  private String oid;

  @ApiModelProperty(value = "Onko valintapajono peritty")
  private Boolean inheritance;

  @ApiModelProperty(value = "Valintapajonon prioriteetti")
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
