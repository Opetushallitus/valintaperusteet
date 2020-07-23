package fi.vm.sade.service.valintaperusteet.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "HakijaryhmaDTO", description = "Hakijaryhmä")
public class HakijaryhmaDTO extends HakijaryhmaCreateDTO implements Prioritized {
  @ApiModelProperty(value = "OID", required = true)
  private String oid;

  @ApiModelProperty(value = "Hakijaryhmän prioriteetti")
  private int prioriteetti;

  public String getOid() {
    return oid;
  }

  public void setOid(String oid) {
    this.oid = oid;
  }

  @Override
  public int getPrioriteetti() {
    return prioriteetti;
  }

  @Override
  public void setPrioriteetti(int prioriteetti) {
    this.prioriteetti = prioriteetti;
  }
}
