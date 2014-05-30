package fi.vm.sade.service.valintaperusteet.dto;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import fi.vm.sade.service.valintaperusteet.dto.model.Valintaperustelahde;

/**
 * User: wuoti Date: 2.12.2013 Time: 10.06
 */
@ApiModel(value = "ValintaperusteViiteDTO", description = "Valintaperusteviite")
public class ValintaperusteViiteDTO  implements Comparable<ValintaperusteViiteDTO> {

    @ApiModelProperty(value = "tunniste", required = true)
    private String tunniste;

    @ApiModelProperty(value = "Kuvaus")
    private String kuvaus;

    @ApiModelProperty(value = "Valintaperusteen lähde", required = true)
    private Valintaperustelahde lahde;

    @ApiModelProperty(value = "Onko valintaperuste pakollinen", required = true)
    private Boolean onPakollinen;

    // Jos valintaperusteen lähde on hakukohde, voidaan epäsuoralla
    // viittauksella hakea
    // hakukohteelta tunniste, jolla viitataan hakemuksen arvoon
    @ApiModelProperty(value = "Viitataanko hakemuksen valintaperusteeseen epäsuorasti", required = true)
    private Boolean epasuoraViittaus;

    @ApiModelProperty(value = "Indeksi", required = true)
    private Integer indeksi;

    @ApiModelProperty(value = "Hylkäysperusteen kuvaukset")
    private TekstiRyhmaDTO kuvaukset = new TekstiRyhmaDTO();

    public String getTunniste() {
        return tunniste;
    }

    public void setTunniste(String tunniste) {
        this.tunniste = tunniste;
    }

    public String getKuvaus() {
        return kuvaus;
    }

    public void setKuvaus(String kuvaus) {
        this.kuvaus = kuvaus;
    }

    public Valintaperustelahde getLahde() {
        return lahde;
    }

    public void setLahde(Valintaperustelahde lahde) {
        this.lahde = lahde;
    }

    public Boolean getOnPakollinen() {
        return onPakollinen;
    }

    public void setOnPakollinen(Boolean onPakollinen) {
        this.onPakollinen = onPakollinen;
    }

    public Boolean getEpasuoraViittaus() {
        return epasuoraViittaus;
    }

    public void setEpasuoraViittaus(Boolean epasuoraViittaus) {
        this.epasuoraViittaus = epasuoraViittaus;
    }

    public Integer getIndeksi() {
        return indeksi;
    }

    public void setIndeksi(Integer indeksi) {
        this.indeksi = indeksi;
    }

    @Override
    public int compareTo(ValintaperusteViiteDTO o) {
        return indeksi - o.indeksi;
    }

    public TekstiRyhmaDTO getKuvaukset() {
        return kuvaukset;
    }

    public void setKuvaukset(TekstiRyhmaDTO kuvaukset) {
        this.kuvaukset = kuvaukset;
    }
}
