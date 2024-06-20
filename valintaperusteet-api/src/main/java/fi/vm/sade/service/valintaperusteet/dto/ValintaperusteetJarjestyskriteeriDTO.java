package fi.vm.sade.service.valintaperusteet.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ValintaperusteetJarjestyskriteeriDTO", description = "Järjestyskriteeri")
public class ValintaperusteetJarjestyskriteeriDTO extends AbstractWithModifyTimestamp
    implements Prioritized {
  @Schema(description = "nimi", required = true)
  private String nimi;

  @Schema(description = "Järjestyskriteerin prioriteetti")
  private int prioriteetti;

  @Schema(description = "Funktiokutsu")
  private ValintaperusteetFunktiokutsuDTO funktiokutsu;

  public String getNimi() {
    return nimi;
  }

  public void setNimi(String nimi) {
    this.nimi = nimi;
  }

  public int getPrioriteetti() {
    return prioriteetti;
  }

  public void setPrioriteetti(int prioriteetti) {
    this.prioriteetti = prioriteetti;
  }

  public ValintaperusteetFunktiokutsuDTO getFunktiokutsu() {
    return funktiokutsu;
  }

  public void setFunktiokutsu(ValintaperusteetFunktiokutsuDTO funktiokutsu) {
    this.funktiokutsu = funktiokutsu;
  }
}
