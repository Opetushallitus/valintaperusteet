package fi.vm.sade.service.valintaperusteet.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi;
import java.util.*;

public class Funktiokutsu extends BaseEntity implements FunktionArgumentti {
  private static final long serialVersionUID = 1L;

  private Funktionimi funktionimi;

  private String tulosTunniste;

  private String tulosTekstiFi;

  private String tulosTekstiSv;

  private String tulosTekstiEn;

  private Boolean tallennaTulos = false;

  private boolean omaopintopolku = false;

  private Set<Arvokonvertteriparametri> arvokonvertteriparametrit =
      new HashSet<Arvokonvertteriparametri>();

  private Set<Arvovalikonvertteriparametri> arvovalikonvertteriparametrit =
      new HashSet<Arvovalikonvertteriparametri>();

  private Set<Syoteparametri> syoteparametrit = new HashSet<Syoteparametri>();

  private Set<Funktioargumentti> funktioargumentit = new TreeSet<Funktioargumentti>();

  private Set<ValintaperusteViite> valintaperusteviitteet = new TreeSet<ValintaperusteViite>();

  @JsonIgnore
  private List<Abstraktivalidointivirhe> validointivirheet =
      new ArrayList<Abstraktivalidointivirhe>();

  public Funktionimi getFunktionimi() {
    return funktionimi;
  }

  public void setFunktionimi(Funktionimi funktionimi) {
    this.funktionimi = funktionimi;
  }

  public Set<Syoteparametri> getSyoteparametrit() {
    return syoteparametrit;
  }

  public Set<Arvokonvertteriparametri> getArvokonvertteriparametrit() {
    return arvokonvertteriparametrit;
  }

  public void setArvokonvertteriparametrit(
      Set<Arvokonvertteriparametri> arvokonvertteriparametrit) {
    this.arvokonvertteriparametrit = arvokonvertteriparametrit;
  }

  public Set<Arvovalikonvertteriparametri> getArvovalikonvertteriparametrit() {
    return arvovalikonvertteriparametrit;
  }

  public void setArvovalikonvertteriparametrit(
      Set<Arvovalikonvertteriparametri> arvovalikonvertteriparametrit) {
    this.arvovalikonvertteriparametrit = arvovalikonvertteriparametrit;
  }

  public void setSyoteparametrit(Set<Syoteparametri> syoteparametrit) {
    this.syoteparametrit = syoteparametrit;
  }

  public Set<Funktioargumentti> getFunktioargumentit() {
    return funktioargumentit;
  }

  public void setFunktioargumentit(Set<Funktioargumentti> funktioargumentit) {
    this.funktioargumentit = funktioargumentit;
  }

  public Set<ValintaperusteViite> getValintaperusteviitteet() {
    return valintaperusteviitteet;
  }

  public void setValintaperusteviitteet(Set<ValintaperusteViite> valintaperusteviitteet) {
    this.valintaperusteviitteet = valintaperusteviitteet;
  }

  @Override
  public Long getId() {
    return super.getId();
  }

  public List<Abstraktivalidointivirhe> getValidointivirheet() {
    return validointivirheet;
  }

  public void setValidointivirheet(List<Abstraktivalidointivirhe> validointivirheet) {
    this.validointivirheet = validointivirheet;
  }

  public static long getSerialVersionUID() {
    return serialVersionUID;
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

  @Override
  public String toString() {
    return "Funktiokutsu{"
        + "funktionimi="
        + funktionimi
        + ", tulosTunniste='"
        + tulosTunniste
        + '\''
        + ", tulosTekstiFi='"
        + tulosTekstiFi
        + '\''
        + ", tulosTekstiSv='"
        + tulosTekstiSv
        + '\''
        + ", tulosTekstiEn='"
        + tulosTekstiEn
        + '\''
        + ", tallennaTulos="
        + tallennaTulos
        + ", omaopintopolku="
        + omaopintopolku
        + ", arvokonvertteriparametrit="
        + arvokonvertteriparametrit
        + ", arvovalikonvertteriparametrit="
        + arvovalikonvertteriparametrit
        + ", syoteparametrit="
        + syoteparametrit
        + ", funktioargumentit="
        + funktioargumentit
        + ", valintaperusteviitteet="
        + valintaperusteviitteet
        + ", validointivirheet="
        + validointivirheet
        + ", base="
        + super.toString()
        + '}';
  }
}
