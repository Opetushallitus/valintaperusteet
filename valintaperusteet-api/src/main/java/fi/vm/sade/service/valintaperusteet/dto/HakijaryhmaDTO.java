package fi.vm.sade.service.valintaperusteet.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "HakijaryhmaDTO", description = "Hakijaryhmä")
public class HakijaryhmaDTO extends HakijaryhmaCreateDTO implements Prioritized {
  @Schema(description = "OID", required = true)
  private String oid;

  @Schema(description = "Hakijaryhmän prioriteetti")
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
