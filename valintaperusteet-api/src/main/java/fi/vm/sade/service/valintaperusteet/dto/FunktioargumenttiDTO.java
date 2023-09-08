package fi.vm.sade.service.valintaperusteet.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "FunktioargumenttiDTO", description = "Funktioargumentti")
public class FunktioargumenttiDTO implements Comparable<FunktioargumenttiDTO> {

  @Schema(description = "Funktioargumentin lapsi (funktiokutsu tai laskentakaava)")
  private FunktioargumentinLapsiDTO lapsi;

  @Schema(description = "Indeksi", required = true)
  private Integer indeksi;

  public Integer getIndeksi() {
    return indeksi;
  }

  public void setIndeksi(Integer indeksi) {
    this.indeksi = indeksi;
  }

  @Override
  public int compareTo(FunktioargumenttiDTO o) {
    return indeksi - o.indeksi;
  }

  public FunktioargumentinLapsiDTO getLapsi() {
    return lapsi;
  }

  public void setLapsi(FunktioargumentinLapsiDTO lapsi) {
    this.lapsi = lapsi;
  }
}
