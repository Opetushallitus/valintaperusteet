package fi.vm.sade.service.valintaperusteet.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "FunktioargumenttiDTO", description = "Funktioargumentti")
public class ValintaperusteetFunktioargumenttiDTO
    implements Comparable<ValintaperusteetFunktioargumenttiDTO> {

  @Schema(description = "Funktioargumentin lapsi (funktiokutsu tai laskentakaava)")
  private ValintaperusteetFunktiokutsuDTO funktiokutsu;

  @Schema(description = "Indeksi", required = true)
  private Integer indeksi;

  private Long id;

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
