package fi.vm.sade.service.valintaperusteet.dto;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import fi.vm.sade.service.valintaperusteet.model.Valintaperustelahde;
import org.codehaus.jackson.map.annotate.JsonView;

/**
 * User: wuoti
 * Date: 2.12.2013
 * Time: 10.06
 */
@ApiModel(value = "ValintaperusteViiteDTO", description = "Valintaperusteviite")
public class ValintaperusteViiteDTO {


    @JsonView(JsonViews.Basic.class)
    @ApiModelProperty(value = "tunniste", required = true)
    private String tunniste;

    @JsonView(JsonViews.Basic.class)
    @ApiModelProperty(value = "Kuvaus")
    private String kuvaus;

    @JsonView(JsonViews.Basic.class)
    @ApiModelProperty(value = "Valintaperusteen lähde", required = true)
    private Valintaperustelahde lahde;

    @JsonView(JsonViews.Basic.class)
    @ApiModelProperty(value = "Onko valintaperuste pakollinen", required = true)
    private Boolean onPakollinen;

    // Jos valintaperusteen lähde on hakukohde, voidaan epäsuoralla viittauksella hakea
    // hakukohteelta tunniste, jolla viitataan hakemuksen arvoon
    @JsonView(JsonViews.Basic.class)
    @ApiModelProperty(value = "Viitataanko hakemuksen valintaperusteeseen epäsuorasti", required = true)
    private Boolean epasuoraViittaus;

    @JsonView(JsonViews.Basic.class)
    @ApiModelProperty(value = "Indeksi", required = true)
    private Integer indeksi;

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
}
