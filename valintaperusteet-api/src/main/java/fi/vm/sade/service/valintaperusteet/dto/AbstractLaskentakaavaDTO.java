package fi.vm.sade.service.valintaperusteet.dto;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import fi.vm.sade.service.valintaperusteet.dto.model.Funktiotyyppi;

/**
 * User: wuoti Date: 29.11.2013 Time: 9.54
 */
@ApiModel(value = "LaskentakaavaDTO", description = "Laskentakaava")
public abstract class AbstractLaskentakaavaDTO {

    @ApiModelProperty(value = "Onko laskentakaava luonnos vai valmis", required = true)
    private Boolean onLuonnos;

    @ApiModelProperty(value = "Nimi", required = true)
    private String nimi;

    @ApiModelProperty(value = "Kuvaus")
    private String kuvaus;

    @ApiModelProperty(value = "Laskentakaavan tyyppi", required = true)
    private fi.vm.sade.service.valintaperusteet.dto.model.Funktiotyyppi tyyppi;

    public Boolean getOnLuonnos() {
        return onLuonnos;
    }

    public void setOnLuonnos(Boolean onLuonnos) {
        this.onLuonnos = onLuonnos;
    }

    public String getNimi() {
        return nimi;
    }

    public void setNimi(String nimi) {
        this.nimi = nimi;
    }

    public String getKuvaus() {
        return kuvaus;
    }

    public void setKuvaus(String kuvaus) {
        this.kuvaus = kuvaus;
    }

    public Funktiotyyppi getTyyppi() {
        return tyyppi;
    }

    public void setTyyppi(Funktiotyyppi tyyppi) {
        this.tyyppi = tyyppi;
    }
}