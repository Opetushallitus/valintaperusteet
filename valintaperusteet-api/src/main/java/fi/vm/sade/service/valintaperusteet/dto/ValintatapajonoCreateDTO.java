package fi.vm.sade.service.valintaperusteet.dto;

import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import fi.vm.sade.service.valintaperusteet.dto.model.Tasapistesaanto;

@ApiModel(value = "ValintatapajonoCreateDTO", description = "Valintatapajono")
public class ValintatapajonoCreateDTO {
    @ApiModelProperty(value = "Aloituspaikat", required = true)
    private Integer aloituspaikat;

    @ApiModelProperty(value = "Nimi", required = true)
    private String nimi;

    @ApiModelProperty(value = "Kuvaus")
    private String kuvaus;

    @ApiModelProperty(value = "Tyyppi")
    private String tyyppi;

    @ApiModelProperty(value = "Siirretään sijoitteluun", required = true)
    private Boolean siirretaanSijoitteluun = false;

    @ApiModelProperty(value = "Tasapistesääntö", required = true)
    private Tasapistesaanto tasapistesaanto;

    @ApiModelProperty(value = "Aktiivinen", required = true)
    private Boolean aktiivinen;

    @ApiModelProperty(value = "Suoritetaanko jonolle välisijoittelu", required = true)
    private Boolean valisijoittelu = false;

    @ApiModelProperty(value = "Siirretäänkö laskennan tulokset automaattisesti sijoitteluun", required = true)
    private Boolean automaattinenLaskentaanSiirto = false;

    @ApiModelProperty(value = "Ei varasijatäyttöä", required = true)
    private Boolean eiVarasijatayttoa = false;

    @ApiModelProperty(value = "Hyväksytäänkö kaikki hyväksyttävissä olevat aloituspaikoista riippumatta", required = true)
    private Boolean kaikkiEhdonTayttavatHyvaksytaan = false;

    @ApiModelProperty(value = "Varasijojen lkm. 0 == pois päältä", required = true)
    private Integer varasijat = 0;

    @ApiModelProperty(value = "Kuinka monta päivää varasijoja täytetään", required = true)
    private Integer varasijaTayttoPaivat = 0;

    @ApiModelProperty(value = "Täytetäänkö poissaolevia", required = true)
    private Boolean poissaOlevaTaytto = false;

    @ApiModelProperty(value = "Poistetaanko hylätyt laskentojen tuloksista", required = true)
    private Boolean poistetaankoHylatyt = false;

    @ApiModelProperty(value = "Varasijasääntöjä käytetään alkaen")
    private Date varasijojaKaytetaanAlkaen;

    @ApiModelProperty(value = "Varasijoja täytetään asti")
    private Date varasijojaTaytetaanAsti;

    @ApiModelProperty(value = "Käytetäänkö valintalaskentaa", required = true)
    private Boolean kaytetaanValintalaskentaa = true;

    @ApiModelProperty(value = "Valintatapajono, josta vapaaksi jääneet paikat täytetään", required = false)
    private String tayttojono;

    public Integer getAloituspaikat() {
        return aloituspaikat;
    }

    public void setAloituspaikat(Integer aloituspaikat) {
        this.aloituspaikat = aloituspaikat;
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

    public String getTyyppi() {
        return tyyppi;
    }

    public void setTyyppi(String tyyppi) {
        this.tyyppi = tyyppi;
    }

    public Boolean getSiirretaanSijoitteluun() {
        return siirretaanSijoitteluun;
    }

    public void setSiirretaanSijoitteluun(Boolean siirretaanSijoitteluun) {
        this.siirretaanSijoitteluun = siirretaanSijoitteluun;
    }

    public Tasapistesaanto getTasapistesaanto() {
        return tasapistesaanto;
    }

    public void setTasapistesaanto(Tasapistesaanto tasapistesaanto) {
        this.tasapistesaanto = tasapistesaanto;
    }

    public Boolean getAktiivinen() {
        return aktiivinen;
    }

    public void setAktiivinen(Boolean aktiivinen) {
        this.aktiivinen = aktiivinen;
    }

    public Boolean getEiVarasijatayttoa() {
        return eiVarasijatayttoa;
    }

    public void setEiVarasijatayttoa(Boolean eiVarasijatayttoa) {
        this.eiVarasijatayttoa = eiVarasijatayttoa;
    }

    public Integer getVarasijat() {
        return varasijat;
    }

    public void setVarasijat(Integer varasijat) {
        this.varasijat = varasijat;
    }

    public Integer getVarasijaTayttoPaivat() {
        return varasijaTayttoPaivat;
    }

    public void setVarasijaTayttoPaivat(Integer varasijaTayttoPaivat) {
        this.varasijaTayttoPaivat = varasijaTayttoPaivat;
    }

    public Boolean getPoissaOlevaTaytto() {
        return poissaOlevaTaytto;
    }

    public void setPoissaOlevaTaytto(Boolean poissaOlevaTaytto) {
        this.poissaOlevaTaytto = poissaOlevaTaytto;
    }

    public Date getVarasijojaKaytetaanAlkaen() {
        return varasijojaKaytetaanAlkaen;
    }

    public void setVarasijojaKaytetaanAlkaen(Date varasijojaKaytetaanAlkaen) {
        this.varasijojaKaytetaanAlkaen = varasijojaKaytetaanAlkaen;
    }

    public Date getVarasijojaTaytetaanAsti() {
        return varasijojaTaytetaanAsti;
    }

    public void setVarasijojaTaytetaanAsti(Date varasijojaTaytetaanAsti) {
        this.varasijojaTaytetaanAsti = varasijojaTaytetaanAsti;
    }

    public Boolean getKaytetaanValintalaskentaa() {
        return kaytetaanValintalaskentaa;
    }

    public void setKaytetaanValintalaskentaa(Boolean kaytetaanValintalaskentaa) {
        this.kaytetaanValintalaskentaa = kaytetaanValintalaskentaa;
    }

    public Boolean getKaikkiEhdonTayttavatHyvaksytaan() {
        return kaikkiEhdonTayttavatHyvaksytaan;
    }

    public void setKaikkiEhdonTayttavatHyvaksytaan(Boolean kaikkiEhdonTayttavatHyvaksytaan) {
        this.kaikkiEhdonTayttavatHyvaksytaan = kaikkiEhdonTayttavatHyvaksytaan;
    }

    public String getTayttojono() {
        return tayttojono;
    }

    public void setTayttojono(String tayttojono) {
        this.tayttojono = tayttojono;
    }

    public Boolean getValisijoittelu() {
        return valisijoittelu;
    }

    public void setValisijoittelu(Boolean valisijoittelu) {
        this.valisijoittelu = valisijoittelu;
    }

    public Boolean getAutomaattinenLaskentaanSiirto() {
        return automaattinenLaskentaanSiirto;
    }

    public void setAutomaattinenLaskentaanSiirto(Boolean automaattinenLaskentaanSiirto) {
        this.automaattinenLaskentaanSiirto = automaattinenLaskentaanSiirto;
    }

    public Boolean getPoistetaankoHylatyt() {
        return poistetaankoHylatyt;
    }

    public void setPoistetaankoHylatyt(Boolean poistetaankoHylatyt) {
        this.poistetaankoHylatyt = poistetaankoHylatyt;
    }

    @Override
    public String toString() {
        return "ValintatapajonoCreateDTO{" +
                "aloituspaikat=" + aloituspaikat +
                ", nimi='" + nimi + '\'' +
                ", kuvaus='" + kuvaus + '\'' +
                ", siirretaanSijoitteluun=" + siirretaanSijoitteluun +
                ", tasapistesaanto=" + tasapistesaanto +
                ", aktiivinen=" + aktiivinen +
                ", valisijoittelu=" + valisijoittelu +
                ", automaattinenLaskentaanSiirto=" + automaattinenLaskentaanSiirto +
                ", eiVarasijatayttoa=" + eiVarasijatayttoa +
                ", kaikkiEhdonTayttavatHyvaksytaan=" + kaikkiEhdonTayttavatHyvaksytaan +
                ", varasijat=" + varasijat +
                ", varasijaTayttoPaivat=" + varasijaTayttoPaivat +
                ", poissaOlevaTaytto=" + poissaOlevaTaytto +
                ", poistetaankoHylatyt=" + poistetaankoHylatyt +
                ", varasijojaKaytetaanAlkaen=" + varasijojaKaytetaanAlkaen +
                ", varasijojaTaytetaanAsti=" + varasijojaTaytetaanAsti +
                ", kaytetaanValintalaskentaa=" + kaytetaanValintalaskentaa +
                ", tayttojono='" + tayttojono + '\'' +
                '}';
    }
}
