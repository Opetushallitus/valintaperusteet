package fi.vm.sade.service.valintaperusteet.dto;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import fi.vm.sade.service.valintaperusteet.model.Tasapistesaanto;
import org.codehaus.jackson.map.annotate.JsonView;

import java.util.Date;

/**
 * User: wuoti
 * Date: 27.11.2013
 * Time: 14.06
 */
@ApiModel(value = "ValintatapajonoCreateDTO", description = "Valintatapajono")
public class ValintatapajonoCreateDTO {
    @ApiModelProperty(value = "Aloituspaikat", required = true)
    private Integer aloituspaikat;

    @ApiModelProperty(value = "Nimi", required = true)
    private String nimi;

    @ApiModelProperty(value = "Kuvaus")
    private String kuvaus;

    @ApiModelProperty(value = "Siirretään sijoitteluun", required = true)
    private Boolean siirretaanSijoitteluun = false;

    @ApiModelProperty(value = "Tasapistesääntö", required = true)
    private Tasapistesaanto tasapistesaanto;

    @ApiModelProperty(value = "Aktiivinen", required = true)
    private Boolean aktiivinen;

    @ApiModelProperty(value = "Ei varasijatäyttöä", required = true)
    private Boolean eiVarasijatayttoa = false;

    @ApiModelProperty(value = "Varasijojen lkm. 0 == pois päältä", required = true)
    private Integer varasijat = 0;

    @ApiModelProperty(value = "Kuinka monta päivää varasijoja täytetään", required = true)
    private Integer varasijaTayttoPaivat = 0;

    @ApiModelProperty(value = "Täytetäänkö poissaolevia", required = true)
    private Boolean poissaOlevaTaytto = false;

    @ApiModelProperty(value = "Varasijasääntöjä käytetään alkaen")
    private Date varasijojaKaytetaanAlkaen;

    @ApiModelProperty(value = "Varasijoja täytetään asti")
    private Date varasijojaTaytetaanAsti;

    @ApiModelProperty(value = "Käytetäänkö valintalaskentaa", required = true)
    private Boolean kaytetaanValintalaskentaa = true;

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
}
