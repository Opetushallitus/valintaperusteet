package fi.vm.sade.service.valintaperusteet.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "FunktioargumenttiDTO", description = "Funktioargumentti")
public class ValintaperusteetFunktioargumenttiDTO
    implements Comparable<ValintaperusteetFunktioargumenttiDTO> {

  @ApiModelProperty(value = "Funktioargumentin lapsi (funktiokutsu tai laskentakaava)")
  private ValintaperusteetFunktiokutsuDTO funktiokutsu;

  @ApiModelProperty(value = "Indeksi", required = true)
  private Integer indeksi;

  private Long id;

  public ValintaperusteetFunktioargumenttiDTO() { }

  public ValintaperusteetFunktioargumenttiDTO(Long id,
                                              ValintaperusteetFunktiokutsuDTO funktiokutsu,
                                              Integer indeksi) {
    this.id = id;
    this.funktiokutsu = funktiokutsu;
    this.indeksi = indeksi;
  }

  public Integer getIndeksi() {
    return indeksi;
  }

  public void setIndeksi(Integer indeksi) {
    this.indeksi = indeksi;
  }

  @Override
  public int compareTo(ValintaperusteetFunktioargumenttiDTO o) {
    return indeksi - o.indeksi;
  }

  public ValintaperusteetFunktiokutsuDTO getFunktiokutsu() {
    return funktiokutsu;
  }

  public void setFunktiokutsu(ValintaperusteetFunktiokutsuDTO funktiokutsu) {
    this.funktiokutsu = funktiokutsu;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }
}
