package fi.vm.sade.service.valintaperusteet.dto;

import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi;
import fi.vm.sade.service.valintaperusteet.dto.model.Funktiotyyppi;
import fi.vm.sade.service.valintaperusteet.dto.model.ValidointivirheDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Schema(
    name = "FunktioargumentinLapsiDTO",
    description = "DTO, joka kuvaa joko funktiokutsua tai laskentakaavaa")
public class FunktioargumentinLapsiDTO {

  public static final String FUNKTIOKUTSUTYYPPI = "funktiokutsu";
  public static final String LASKENTAKAAVATYYPPI = "laskentakaava";

  @Schema(description = "Funktion nimi")
  private Funktionimi funktionimi;

  @Schema(description = "Arvokonvertteriparametrit")
  private Set<ArvokonvertteriparametriDTO> arvokonvertteriparametrit =
      new HashSet<ArvokonvertteriparametriDTO>();

  @Schema(description = "Arvovälikonvertteriparametrit")
  private List<ArvovalikonvertteriparametriDTO> arvovalikonvertteriparametrit =
      new LinkedList<ArvovalikonvertteriparametriDTO>();

  @Schema(description = "Syöteparametrit")
  private Set<SyoteparametriDTO> syoteparametrit = new HashSet<SyoteparametriDTO>();

  @Schema(description = "Funktioargumentit")
  private List<FunktioargumenttiDTO> funktioargumentit = new LinkedList<FunktioargumenttiDTO>();

  @Schema(description = "Valintaperusteviitteet")
  private List<ValintaperusteViiteDTO> valintaperusteviitteet =
      new ArrayList<ValintaperusteViiteDTO>();

  @Schema(description = "Validointivirheet")
  private List<ValidointivirheDTO> validointivirheet = new ArrayList<ValidointivirheDTO>();

  @Schema(description = "Onko laskentakaava luonnos vai valmis")
  private Boolean onLuonnos;

  @Schema(description = "Nimi", required = true)
  private String nimi;

  @Schema(description = "Kuvaus")
  private String kuvaus;

  @Schema(description = "Laskentakaavan tyyppi")
  private Funktiotyyppi tyyppi;

  @Schema(description = "ID")
  private Long id;

  @Schema(description = "Lapsen tyyppi")
  private String lapsityyppi;

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

  @Schema(description = "Näytetäänkö oppijan henkilökohtaisessa palvelussa")
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
