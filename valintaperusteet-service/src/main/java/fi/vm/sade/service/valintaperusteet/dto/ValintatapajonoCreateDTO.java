package fi.vm.sade.service.valintaperusteet.dto;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import fi.vm.sade.service.valintaperusteet.model.Tasapistesaanto;
import org.codehaus.jackson.map.annotate.JsonView;

/**
 * User: wuoti
 * Date: 27.11.2013
 * Time: 14.06
 */
@ApiModel(value = "ValintatapajonoCreateDTO", description = "Valintatapajono")
public class ValintatapajonoCreateDTO {
    @ApiModelProperty(value = "Aloituspaikat", required = true)
    @JsonView(JsonViews.Basic.class)
    private Integer aloituspaikat;

    @ApiModelProperty(value = "Nimi", required = true)
    @JsonView(JsonViews.Basic.class)
    private String nimi;

    @ApiModelProperty(value = "Kuvaus")
    @JsonView(JsonViews.Basic.class)
    private String kuvaus;

    @ApiModelProperty(value = "Siirretään sijoitteluun", required = true)
    @JsonView(JsonViews.Basic.class)
    private Boolean siirretaanSijoitteluun = false;

    @ApiModelProperty(value = "Tasapistesääntö", required = true)
    @JsonView(JsonViews.Basic.class)
    private Tasapistesaanto tasapistesaanto;

    @JsonView(JsonViews.Basic.class)
    @ApiModelProperty(value = "Aktiivinen", required = true)
    private Boolean aktiivinen;

    @JsonView(JsonViews.Basic.class)
    @ApiModelProperty(value = "Ei varasijatäyttöä", required = true)
    private Boolean eiVarasijatayttoa = false;

    @ApiModelProperty(value = "Varasijojen lkm", required = true)
    @JsonView(JsonViews.Basic.class)
    private Integer varasijat = 0;

    @ApiModelProperty(value = "Kuinka monta päivää varasijoja täytetään", required = true)
    @JsonView(JsonViews.Basic.class)
    private Integer varasijaTayttoPaivat = 0;

    @JsonView(JsonViews.Basic.class)
    @ApiModelProperty(value = "Täytetäänkö poissaolevia", required = true)
    private Boolean poissaOlevaTaytto = false;

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
}
