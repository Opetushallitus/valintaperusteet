package fi.vm.sade.service.valintaperusteet.dto;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * User: kwuoti Date: 16.4.2013 Time: 13.01
 */
@ApiModel(value = "ValintakoeCreateDTO", description = "Valintakoe")
public class ValintakoeCreateDTO {

    @ApiModelProperty(value = "Tunniste", required = true)
    private String tunniste;

    @ApiModelProperty(value = "Laskentakaava ID", required = true)
    private Long laskentakaavaId;

    @ApiModelProperty(value = "Nimi", required = true)
    private String nimi;

    @ApiModelProperty(value = "Kuvaus")
    private String kuvaus;

    @ApiModelProperty(value = "Onko valintakoe aktiivinen", required = true)
    private Boolean aktiivinen;

    @ApiModelProperty(value = "Lähetetäänkö koekutsut", required = true)
    private Boolean lahetetaankoKoekutsut;

    public String getTunniste() {
        return tunniste;
    }

    public void setTunniste(String tunniste) {
        this.tunniste = tunniste;
    }

    public Long getLaskentakaavaId() {
        return laskentakaavaId;
    }

    public void setLaskentakaavaId(Long laskentakaavaId) {
        this.laskentakaavaId = laskentakaavaId;
    }

    public String getKuvaus() {
        return kuvaus;
    }

    public void setKuvaus(String kuvaus) {
        this.kuvaus = kuvaus;
    }

    public String getNimi() {
        return nimi;
    }

    public void setNimi(String nimi) {
        this.nimi = nimi;
    }

    public Boolean getAktiivinen() {
        return aktiivinen;
    }

    public void setAktiivinen(Boolean aktiivinen) {
        this.aktiivinen = aktiivinen;
    }

    public Boolean getLahetetaankoKoekutsut() {
        return lahetetaankoKoekutsut;
    }

    public void setLahetetaankoKoekutsut(Boolean lahetetaankoKoekutsut) {
        this.lahetetaankoKoekutsut = lahetetaankoKoekutsut;
    }
}
