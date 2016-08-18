package fi.vm.sade.service.valintaperusteet.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi;
import fi.vm.sade.service.valintaperusteet.dto.model.ValidointivirheDTO;

import java.util.*;

@ApiModel(value = "ValintaperusteetFunktiokutsuDTO", description = "Funktiokutsu")
public class ValintaperusteetFunktiokutsuDTO {

    @ApiModelProperty(value = "Funktion nimi", required = true)
    private Funktionimi funktionimi;

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

    @ApiModelProperty(value = "Arvokonvertteriparametrit")
    private Set<ArvokonvertteriparametriDTO> arvokonvertteriparametrit = new HashSet<ArvokonvertteriparametriDTO>();

    @ApiModelProperty(value = "Arvovälikonvertteriparametrit")
    private Set<ArvovalikonvertteriparametriDTO> arvovalikonvertteriparametrit = new HashSet<ArvovalikonvertteriparametriDTO>();

    @ApiModelProperty(value = "Syöteparametrit")
    private Set<SyoteparametriDTO> syoteparametrit = new HashSet<SyoteparametriDTO>();

    @ApiModelProperty(value = "Funktioargumentit")
    private Set<ValintaperusteetFunktioargumenttiDTO> funktioargumentit = new HashSet<ValintaperusteetFunktioargumenttiDTO>();

    @ApiModelProperty(value = "Valintaperusteviitteet")
    private Set<ValintaperusteViiteDTO> valintaperusteviitteet = new HashSet<ValintaperusteViiteDTO>();

    private Long id;

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

    public Set<ArvovalikonvertteriparametriDTO> getArvovalikonvertteriparametrit() {
        return arvovalikonvertteriparametrit;
    }

    public void setArvovalikonvertteriparametrit(Set<ArvovalikonvertteriparametriDTO> arvovalikonvertteriparametrit) {
        this.arvovalikonvertteriparametrit = arvovalikonvertteriparametrit;
    }

    public Set<SyoteparametriDTO> getSyoteparametrit() {
        return syoteparametrit;
    }

    public void setSyoteparametrit(Set<SyoteparametriDTO> syoteparametrit) {
        this.syoteparametrit = syoteparametrit;
    }


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

    public Set<ValintaperusteetFunktioargumenttiDTO> getFunktioargumentit() {
        return funktioargumentit;
    }

    public void setFunktioargumentit(Set<ValintaperusteetFunktioargumenttiDTO> funktioargumentit) {
        this.funktioargumentit = funktioargumentit;
    }

    public Set<ValintaperusteViiteDTO> getValintaperusteviitteet() {
        return valintaperusteviitteet;
    }

    public void setValintaperusteviitteet(Set<ValintaperusteViiteDTO> valintaperusteviitteet) {
        this.valintaperusteviitteet = valintaperusteviitteet;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
