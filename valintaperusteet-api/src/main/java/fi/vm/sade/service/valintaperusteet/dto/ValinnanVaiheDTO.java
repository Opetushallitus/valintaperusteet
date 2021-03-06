package fi.vm.sade.service.valintaperusteet.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.HashSet;
import java.util.Set;

@ApiModel(value = "service.valintaperusteet.dto.ValinnanVaiheDTO", description = "Valinnan vaihe")
public class ValinnanVaiheDTO extends ValinnanVaiheCreateDTO {

  @ApiModelProperty(value = "OID", required = true)
  private String oid;

  @ApiModelProperty(value = "Onko valinnan vaihe peritty")
  private Boolean inheritance;

  @ApiModelProperty(value = "Onko valinnanvaiheella välisijoittelua")
  private Boolean hasValisijoittelu;

  @ApiModelProperty(value = "Valinnanvaiheen valintatapajonot")
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
