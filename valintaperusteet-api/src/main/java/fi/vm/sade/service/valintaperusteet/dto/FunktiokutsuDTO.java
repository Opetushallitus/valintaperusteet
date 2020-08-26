package fi.vm.sade.service.valintaperusteet.dto;

import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi;
import fi.vm.sade.service.valintaperusteet.dto.model.ValidointivirheDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@ApiModel(value = "FunktiokutsuDTO", description = "Funktiokutsu")
public class FunktiokutsuDTO {

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

  @ApiModelProperty(value = "Tallennetaanko tulos")
  private Boolean tallennaTulos = false;

  @ApiModelProperty(value = "Näytetäänkö oppijan henkilökohtaisessa palvelussa")
  private boolean omaopintopolku = false;

  @ApiModelProperty(value = "Arvokonvertteriparametrit")
  private Set<ArvokonvertteriparametriDTO> arvokonvertteriparametrit =
      new HashSet<ArvokonvertteriparametriDTO>();

  @ApiModelProperty(value = "Arvovälikonvertteriparametrit")
  private List<ArvovalikonvertteriparametriDTO> arvovalikonvertteriparametrit =
      new LinkedList<ArvovalikonvertteriparametriDTO>();

  @ApiModelProperty(value = "Syöteparametrit")
  private Set<SyoteparametriDTO> syoteparametrit = new HashSet<SyoteparametriDTO>();

  @ApiModelProperty(value = "Funktioargumentit")
  private List<FunktioargumenttiDTO> funktioargumentit = new LinkedList<FunktioargumenttiDTO>();

  @ApiModelProperty(value = "Valintaperusteviitteet")
  private List<ValintaperusteViiteDTO> valintaperusteviitteet =
      new ArrayList<ValintaperusteViiteDTO>();

  @ApiModelProperty(value = "Validointivirheet")
  private List<ValidointivirheDTO> validointivirheet = new ArrayList<ValidointivirheDTO>();

  public FunktiokutsuDTO() { }

  public FunktiokutsuDTO(Funktionimi funktionimi,
                         String tulosTunniste,
                         String tulosTekstiFi,
                         String tulosTekstiSv,
                         String tulosTekstiEn,
                         Boolean tallennaTulos,
                         boolean omaopintopolku,
                         Set<ArvokonvertteriparametriDTO> arvokonvertteriparametrit,
                         List<ArvovalikonvertteriparametriDTO> arvovalikonvertteriparametrit,
                         Set<SyoteparametriDTO> syoteparametrit,
                         List<FunktioargumenttiDTO> funktioargumentit,
                         List<ValintaperusteViiteDTO> valintaperusteviitteet,
                         List<ValidointivirheDTO> validointivirheet) {
    this.funktionimi = funktionimi;
    this.tulosTunniste = tulosTunniste;
    this.tulosTekstiFi = tulosTekstiFi;
    this.tulosTekstiSv = tulosTekstiSv;
    this.tulosTekstiEn = tulosTekstiEn;
    this.tallennaTulos = tallennaTulos;
    this.omaopintopolku = omaopintopolku;
    this.arvokonvertteriparametrit = arvokonvertteriparametrit;
    this.arvovalikonvertteriparametrit = arvovalikonvertteriparametrit;
    this.syoteparametrit = syoteparametrit;
    this.funktioargumentit = funktioargumentit;
    this.valintaperusteviitteet = valintaperusteviitteet;
    this.validointivirheet = validointivirheet;
  }

  public FunktiokutsuDTO(FunktioargumentinLapsiDTO lapsi) {
    this.funktionimi = lapsi.getFunktionimi();
    this.tulosTunniste = lapsi.getTulosTunniste();
    this.tulosTekstiFi = lapsi.getTulosTekstiFi();
    this.tulosTekstiSv = lapsi.getTulosTekstiSv();
    this.tulosTekstiEn = lapsi.getTulosTekstiEn();
    this.tallennaTulos = lapsi.getTallennaTulos();
    this.omaopintopolku = lapsi.getOmaopintopolku();
    this.arvokonvertteriparametrit = lapsi.getArvokonvertteriparametrit();
    this.arvovalikonvertteriparametrit = lapsi.getArvovalikonvertteriparametrit();
    this.syoteparametrit = lapsi.getSyoteparametrit();
    this.funktioargumentit = lapsi.getFunktioargumentit();
    this.valintaperusteviitteet = lapsi.getValintaperusteviitteet();
    this.validointivirheet = lapsi.getValidointivirheet();
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

  public void setArvokonvertteriparametrit(
      Set<ArvokonvertteriparametriDTO> arvokonvertteriparametrit) {
    this.arvokonvertteriparametrit = arvokonvertteriparametrit;
  }

  public List<ArvovalikonvertteriparametriDTO> getArvovalikonvertteriparametrit() {
    return arvovalikonvertteriparametrit;
  }

  public void setArvovalikonvertteriparametrit(
      List<ArvovalikonvertteriparametriDTO> arvovalikonvertteriparametrit) {
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

  public boolean getOmaopintopolku() {
    return omaopintopolku;
  }

  public void setOmaopintopolku(boolean omaopintopolku) {
    this.omaopintopolku = omaopintopolku;
  }
}
