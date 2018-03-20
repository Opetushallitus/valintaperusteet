package fi.vm.sade.service.valintaperusteet.dto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import fi.vm.sade.service.valintaperusteet.dto.model.ValidointivirheDTO;
import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi;
import fi.vm.sade.service.valintaperusteet.dto.model.Funktiotyyppi;

@ApiModel(value = "FunktioargumentinLapsiDTO", description = "DTO, joka kuvaa joko funktiokutsua tai laskentakaavaa")
public class FunktioargumentinLapsiDTO {

    public static final String FUNKTIOKUTSUTYYPPI = "funktiokutsu";
    public static final String LASKENTAKAAVATYYPPI = "laskentakaava";

    @ApiModelProperty(value = "Funktion nimi")
    private Funktionimi funktionimi;

    @ApiModelProperty(value = "Arvokonvertteriparametrit")
    private Set<ArvokonvertteriparametriDTO> arvokonvertteriparametrit = new HashSet<ArvokonvertteriparametriDTO>();

    @ApiModelProperty(value = "Arvovälikonvertteriparametrit")
    private List<ArvovalikonvertteriparametriDTO> arvovalikonvertteriparametrit = new LinkedList<ArvovalikonvertteriparametriDTO>();

    @ApiModelProperty(value = "Syöteparametrit")
    private Set<SyoteparametriDTO> syoteparametrit = new HashSet<SyoteparametriDTO>();

    @ApiModelProperty(value = "Funktioargumentit")
    private List<FunktioargumenttiDTO> funktioargumentit = new LinkedList<FunktioargumenttiDTO>();

    @ApiModelProperty(value = "Valintaperusteviitteet")
    private List<ValintaperusteViiteDTO> valintaperusteviitteet = new ArrayList<ValintaperusteViiteDTO>();

    @ApiModelProperty(value = "Validointivirheet")
    private List<ValidointivirheDTO> validointivirheet = new ArrayList<ValidointivirheDTO>();

    @ApiModelProperty(value = "Onko laskentakaava luonnos vai valmis")
    private Boolean onLuonnos;

    @ApiModelProperty(value = "Nimi", required = true)
    private String nimi;

    @ApiModelProperty(value = "Kuvaus")
    private String kuvaus;

    @ApiModelProperty(value = "Laskentakaavan tyyppi")
    private Funktiotyyppi tyyppi;

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "Lapsen tyyppi")
    private String lapsityyppi;

    @ApiModelProperty(value = "Tallennetun tuloksen tunniste")
    private String tulosTunniste;

    @ApiModelProperty(value = "Tallennetun tuloksen suomenkielinen teksti")
    private String tulosTekstiFi;

    @ApiModelProperty(value = "Tallennetun tuloksen ruotsinkielinen teksti")
    private String tulosTekstiSv;

    @ApiModelProperty(value = "Tallennetun tuloksen englanninkielinen teksti")
    private String tulosTekstiEn;

    @ApiModelProperty(value = "Tallennetaanko tulos", required = true)
    private Boolean tallennaTulos;

    @ApiModelProperty(value = "Näytetäänkö oppijan henkilökohtaisessa palvelussa")
    private boolean omaopintopolku = false;

    public String getTulosTunniste() {
        return tulosTunniste;
    }

    public void setTulosTunniste(String tulosTunniste) {
        this.tulosTunniste = tulosTunniste;
    }

    public String getTulosTekstiFi() {
        return tulosTekstiFi;
    }

    public void setTulosTekstiFi(String tulosTekstiFi) {
        this.tulosTekstiFi = tulosTekstiFi;
    }

    public String getTulosTekstiSv() {
        return tulosTekstiSv;
    }

    public void setTulosTekstiSv(String tulosTekstiSv) {
        this.tulosTekstiSv = tulosTekstiSv;
    }

    public String getTulosTekstiEn() {
        return tulosTekstiEn;
    }

    public void setTulosTekstiEn(String tulosTekstiEn) {
        this.tulosTekstiEn = tulosTekstiEn;
    }

    public Boolean getTallennaTulos() {
        return tallennaTulos;
    }

    public void setTallennaTulos(Boolean tallennaTulos) {
        this.tallennaTulos = tallennaTulos;
    }

    public Funktionimi getFunktionimi() {
        return funktionimi;
    }

    public void setFunktionimi(Funktionimi funktionimi) {
        this.funktionimi = funktionimi;
    }

    public Set<ArvokonvertteriparametriDTO> getArvokonvertteriparametrit() {
        return arvokonvertteriparametrit;
    }

    public void setArvokonvertteriparametrit(Set<ArvokonvertteriparametriDTO> arvokonvertteriparametrit) {
        this.arvokonvertteriparametrit = arvokonvertteriparametrit;
    }

    public List<ArvovalikonvertteriparametriDTO> getArvovalikonvertteriparametrit() {
        return arvovalikonvertteriparametrit;
    }

    public void setArvovalikonvertteriparametrit(List<ArvovalikonvertteriparametriDTO> arvovalikonvertteriparametrit) {
        this.arvovalikonvertteriparametrit = arvovalikonvertteriparametrit;
    }

    public Set<SyoteparametriDTO> getSyoteparametrit() {
        return syoteparametrit;
    }

    public void setSyoteparametrit(Set<SyoteparametriDTO> syoteparametrit) {
        this.syoteparametrit = syoteparametrit;
    }

    public List<FunktioargumenttiDTO> getFunktioargumentit() {
        return funktioargumentit;
    }

    public void setFunktioargumentit(List<FunktioargumenttiDTO> funktioargumentit) {
        this.funktioargumentit = funktioargumentit;
    }

    public List<ValintaperusteViiteDTO> getValintaperusteviitteet() {
        return valintaperusteviitteet;
    }

    public void setValintaperusteviitteet(List<ValintaperusteViiteDTO> valintaperusteviitteet) {
        this.valintaperusteviitteet = valintaperusteviitteet;
    }

    public List<ValidointivirheDTO> getValidointivirheet() {
        return validointivirheet;
    }

    public void setValidointivirheet(List<ValidointivirheDTO> validointivirheet) {
        this.validointivirheet = validointivirheet;
    }

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLapsityyppi() {
        return lapsityyppi;
    }

    public void setLapsityyppi(String lapsityyppi) {
        this.lapsityyppi = lapsityyppi;
    }

    public boolean getOmaopintopolku() {
        return omaopintopolku;
    }

    public void setOmaopintopolku(boolean omaopintopolku) {
        this.omaopintopolku = omaopintopolku;
    }
}
