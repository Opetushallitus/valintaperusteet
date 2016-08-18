package fi.vm.sade.service.valintaperusteet.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "FunktioargumenttiDTO", description = "Funktioargumentti")
public class FunktioargumenttiDTO implements Comparable<FunktioargumenttiDTO> {


    @ApiModelProperty(value = "Funktioargumentin lapsi (funktiokutsu tai laskentakaava)")
    private FunktioargumentinLapsiDTO lapsi;


    @ApiModelProperty(value = "Indeksi", required = true)
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
