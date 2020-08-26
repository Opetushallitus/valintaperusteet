package fi.vm.sade.service.valintaperusteet.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "ValintaperusteetJarjestyskriteeriDTO", description = "Järjestyskriteeri")
public class ValintaperusteetJarjestyskriteeriDTO implements Prioritized {
  @ApiModelProperty(value = "nimi", required = true)
  private String nimi;

  @ApiModelProperty(value = "Järjestyskriteerin prioriteetti", required = false)
  private int prioriteetti;

  @ApiModelProperty(value = "Funktiokutsu", required = false)
  private ValintaperusteetFunktiokutsuDTO funktiokutsu;

  public ValintaperusteetJarjestyskriteeriDTO() { }

  public ValintaperusteetJarjestyskriteeriDTO(String nimi,
                                              int prioriteetti,
                                              ValintaperusteetFunktiokutsuDTO funktiokutsu) {
    this.nimi = nimi;
    this.prioriteetti = prioriteetti;
    this.funktiokutsu = funktiokutsu;
  }

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
