package fi.vm.sade.service.valintaperusteet.dto;

import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.*;

@Schema(name = "ValintaperusteetFunktiokutsuDTO", description = "Funktiokutsu")
public class ValintaperusteetFunktiokutsuDTO {

  @Schema(description = "Funktion nimi", required = true)
  private Funktionimi funktionimi;

  @Schema(description = "Tallennetun tuloksen tunniste")
  private String tulosTunniste;

  @Schema(description = "Tallennetun tuloksen suomenkielinen teksti")
  private String tulosTekstiFi;

  @Schema(description = "Tallennetun tuloksen ruotsinkielinen teksti")
  private String tulosTekstiSv;

  @Schema(description = "Tallennetun tuloksen englanninkielinen teksti")
  private String tulosTekstiEn;

  @Schema(description = "Tallennetaanko tulos", required = true)
  private Boolean tallennaTulos;

  @Schema(description = "Näytetäänkö oppijan henkilokohtaisessa palvelussa")
  private boolean omaopintopolku;

  @Schema(description = "Arvokonvertteriparametrit")
  private Set<ArvokonvertteriparametriDTO> arvokonvertteriparametrit =
      new HashSet<ArvokonvertteriparametriDTO>();

  @Schema(description = "Arvovälikonvertteriparametrit")
  private Set<ArvovalikonvertteriparametriDTO> arvovalikonvertteriparametrit =
      new HashSet<ArvovalikonvertteriparametriDTO>();

  @Schema(description = "Syöteparametrit")
  private Set<SyoteparametriDTO> syoteparametrit = new HashSet<SyoteparametriDTO>();

  @Schema(description = "Funktioargumentit")
  private Set<ValintaperusteetFunktioargumenttiDTO> funktioargumentit =
      new HashSet<ValintaperusteetFunktioargumenttiDTO>();

  @Schema(description = "Valintaperusteviitteet")
  private Set<ValintaperusteViiteDTO> valintaperusteviitteet =
      new HashSet<ValintaperusteViiteDTO>();

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

  public void setArvokonvertteriparametrit(
      Set<ArvokonvertteriparametriDTO> arvokonvertteriparametrit) {
    this.arvokonvertteriparametrit = arvokonvertteriparametrit;
  }

  public Set<ArvovalikonvertteriparametriDTO> getArvovalikonvertteriparametrit() {
    return arvovalikonvertteriparametrit;
  }

  public void setArvovalikonvertteriparametrit(
      Set<ArvovalikonvertteriparametriDTO> arvovalikonvertteriparametrit) {
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

  public boolean getOmaopintopolku() {
    return omaopintopolku;
  }

  public void setOmaopintopolku(boolean omaopintopolku) {
    this.omaopintopolku = omaopintopolku;
  }
}
