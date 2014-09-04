package fi.vm.sade.service.valintaperusteet.dto;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * User: wuoti Date: 27.11.2013 Time: 12.47
 */
@ApiModel(value = "ValintaperusteetHakijaryhmaDTO", description = "Hakijaryhmä")
public class ValintaperusteetHakijaryhmaDTO {
    @ApiModelProperty(value = "OID", required = true)
    private String oid;

    @ApiModelProperty(value = "Nimi", required = true)
    private String nimi;

    @ApiModelProperty(value = "Kuvaus")
    private String kuvaus;

    @ApiModelProperty(value = "Kiintio", required = true)
    private int kiintio;

    @ApiModelProperty(value = "Hakijaryhmän prioriteetti", required = false)
    private int prioriteetti;

    @ApiModelProperty(value = "Funktiokutsu", required = false)
    private ValintaperusteetFunktiokutsuDTO funktiokutsu;

    @ApiModelProperty(value = "Kayta kaikki")
    private boolean kaytaKaikki;

    @ApiModelProperty(value = "Tarkka kiintio")
    private boolean tarkkaKiintio;

    @ApiModelProperty(value = "Käytetäänkö hakijaryhmään kuuluvia", required = true)
    private boolean kaytetaanRyhmaanKuuluvia;

    @ApiModelProperty(value = "valintatapajonon OID", required = true)
    private String valintatapajonoOid;

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

    public int getKiintio() {
        return kiintio;
    }

    public void setKiintio(int kiintio) {
        this.kiintio = kiintio;
    }


    public boolean isTarkkaKiintio() { return tarkkaKiintio; }

    public void setTarkkaKiintio(boolean tarkkaKiintio) { this.tarkkaKiintio = tarkkaKiintio; }

    public boolean isKaytaKaikki() { return kaytaKaikki; }

    public void setKaytaKaikki(boolean kaytaKaikki) { this.kaytaKaikki = kaytaKaikki; }

    public String getOid() {
        return oid;
    }


    public void setOid(String oid) {
        this.oid = oid;
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

    public boolean isKaytetaanRyhmaanKuuluvia() {
        return kaytetaanRyhmaanKuuluvia;
    }

    public void setKaytetaanRyhmaanKuuluvia(boolean kaytetaanRyhmaanKuuluvia) {
        this.kaytetaanRyhmaanKuuluvia = kaytetaanRyhmaanKuuluvia;
    }

    public String getValintatapajonoOid() {
        return valintatapajonoOid;
    }

    public void setValintatapajonoOid(String valintatapajonoOid) {
        this.valintatapajonoOid = valintatapajonoOid;
    }
}
