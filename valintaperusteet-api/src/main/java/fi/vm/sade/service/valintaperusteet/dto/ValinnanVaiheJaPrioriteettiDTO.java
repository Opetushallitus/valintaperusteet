package fi.vm.sade.service.valintaperusteet.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class ValinnanVaiheJaPrioriteettiDTO extends ValinnanVaiheDTO {

  @Schema(description = "Prioriteetti", required = true)
  private int prioriteetti;

  public int getPrioriteetti() {
    return prioriteetti;
  }

  public void setPrioriteetti(int prioriteetti) {
    this.prioriteetti = prioriteetti;
  }
}
