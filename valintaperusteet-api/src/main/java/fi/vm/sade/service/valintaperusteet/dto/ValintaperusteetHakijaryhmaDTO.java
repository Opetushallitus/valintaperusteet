package fi.vm.sade.service.valintaperusteet.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "ValintaperusteetHakijaryhmaDTO", description = "Hakijaryhmä")
public class ValintaperusteetHakijaryhmaDTO implements Prioritized {
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

    @ApiModelProperty(value = "Vain hakijaryhmään kuuluvat voivat tulla hyväksytyksi")
    private boolean kaytaKaikki;

    @ApiModelProperty(value = "Vain kiintiön verran voi tulla hyväksytyksi tästä hakijaryhmästä")
    private boolean tarkkaKiintio;

    @ApiModelProperty(value = "Käytetäänkö vain hakijaryhmään kuuluvia", required = true)
    private boolean kaytetaanRyhmaanKuuluvia;

    @ApiModelProperty(value = "valintatapajonon OID", required = true)
    private String valintatapajonoOid;

    @ApiModelProperty(value = "hakukohteen OID", required = true)
    private String hakukohdeOid;

    @ApiModelProperty(value = "Hakijaryhmän tyyppi koodi", required = false)
    private KoodiDTO hakijaryhmatyyppikoodi;

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


    public boolean isTarkkaKiintio() {
        return tarkkaKiintio;
    }

    public void setTarkkaKiintio(boolean tarkkaKiintio) {
        this.tarkkaKiintio = tarkkaKiintio;
    }

    public boolean isKaytaKaikki() {
        return kaytaKaikki;
    }

    public void setKaytaKaikki(boolean kaytaKaikki) {
        this.kaytaKaikki = kaytaKaikki;
    }

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

    public String getHakukohdeOid() {
        return hakukohdeOid;
    }

    public void setHakukohdeOid(String hakukohdeOid) {
        this.hakukohdeOid = hakukohdeOid;
    }

    public KoodiDTO getHakijaryhmatyyppikoodi() {
        return hakijaryhmatyyppikoodi;
    }

    public void setHakijaryhmatyyppikoodi(KoodiDTO hakijaryhmatyyppikoodi) {
        this.hakijaryhmatyyppikoodi = hakijaryhmatyyppikoodi;
    }
}
