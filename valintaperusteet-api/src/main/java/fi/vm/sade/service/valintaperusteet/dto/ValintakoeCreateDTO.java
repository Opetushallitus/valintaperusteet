package fi.vm.sade.service.valintaperusteet.dto;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import fi.vm.sade.service.valintaperusteet.dto.model.Koekutsu;

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

    @ApiModelProperty(value = "Kutsutaanko kaikki kokeeseen", required = true)
    private Boolean kutsutaankoKaikki;

    @ApiModelProperty(value = "Kutsuttavien määrä")
    private Integer kutsuttavienMaara;

    @ApiModelProperty(value = "Minne koekutsu osoitetaan")
    private Koekutsu kutsunKohde;

    @ApiModelProperty(value = "Avain, josta kutsun kohde haetaan", required = false)
    private String kutsunKohdeAvain;

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

    public Boolean getKutsutaankoKaikki() {
        return kutsutaankoKaikki;
    }

    public void setKutsutaankoKaikki(Boolean kutsutaankoKaikki) {
        this.kutsutaankoKaikki = kutsutaankoKaikki;
    }

    public Integer getKutsuttavienMaara() {
        return kutsuttavienMaara;
    }

    public void setKutsuttavienMaara(final Integer kutsuttavienMaara) {
        this.kutsuttavienMaara = kutsuttavienMaara;
    }

    public Koekutsu getKutsunKohde() {
        return kutsunKohde;
    }

    public void setKutsunKohde(Koekutsu kutsunKohde) {
        this.kutsunKohde = kutsunKohde;
    }

    public String getKutsunKohdeAvain() {
        return kutsunKohdeAvain;
    }

    public void setKutsunKohdeAvain(String kutsunKohdeAvain) {
        this.kutsunKohdeAvain = kutsunKohdeAvain;
    }
}
