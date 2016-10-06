package fi.vm.sade.service.valintaperusteet.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "HakijaryhmaValintatapajonoDTO", description = "Hakijaryhmän liittyminen valintatapajonoon")
public class HakijaryhmaValintatapajonoDTO implements Prioritized {

    @ApiModelProperty(value = "OID", required = true)
    private String oid;

    @ApiModelProperty(value = "Nimi")
    private String nimi;

    @ApiModelProperty(value = "Kuvaus")
    private String kuvaus;

    @ApiModelProperty(value = "Kiintio", required = true)
    private int kiintio;

    @ApiModelProperty(value = "Kayta kaikki", required = true)
    private boolean kaytaKaikki;

    @ApiModelProperty(value = "Tarkka kiintio", required = true)
    private boolean tarkkaKiintio;

    @ApiModelProperty(value = "Käytetäänkö hakijaryhmään kuuluvia", required = true)
    private boolean kaytetaanRyhmaanKuuluvia;

    @ApiModelProperty(value = "Aktiivinen", required = true)
    private Boolean aktiivinen;

    @ApiModelProperty(value = "Prioriteetti", required = true)
    private int prioriteetti;

    @ApiModelProperty(value = "Master haikjaryhmän OID", required = false)
    private String masterOid;

    public Boolean getAktiivinen() {
        return aktiivinen;
    }

    public void setAktiivinen(Boolean aktiivinen) {
        this.aktiivinen = aktiivinen;
    }

    public int getKiintio() {
        return kiintio;
    }

    public void setKiintio(int kiintio) {
        this.kiintio = kiintio;
    }

    public boolean isKaytaKaikki() {
        return kaytaKaikki;
    }

    public void setKaytaKaikki(boolean kaytaKaikki) {
        this.kaytaKaikki = kaytaKaikki;
    }

    public boolean isTarkkaKiintio() {
        return tarkkaKiintio;
    }

    public void setTarkkaKiintio(boolean tarkkaKiintio) {
        this.tarkkaKiintio = tarkkaKiintio;
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

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getMasterOid() {
        return masterOid;
    }

    public void setMasterOid(String masterOid) {
        this.masterOid = masterOid;
    }

    public boolean isKaytetaanRyhmaanKuuluvia() {
        return kaytetaanRyhmaanKuuluvia;
    }

    public void setKaytetaanRyhmaanKuuluvia(boolean kaytetaanRyhmaanKuuluvia) {
        this.kaytetaanRyhmaanKuuluvia = kaytetaanRyhmaanKuuluvia;
    }

    public int getPrioriteetti() {
        return prioriteetti;
    }

    public void setPrioriteetti(int prioriteetti) {
        this.prioriteetti = prioriteetti;
    }
}
