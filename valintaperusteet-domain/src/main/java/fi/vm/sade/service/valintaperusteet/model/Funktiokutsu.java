package fi.vm.sade.service.valintaperusteet.model;

import fi.vm.sade.service.valintaperusteet.dto.FunktiokutsuDTO;
import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi;

import java.util.List;
import java.util.Set;

public class Funktiokutsu implements FunktionArgumentti {
  private FunktiokutsuId id;

  private long version;

  private Funktionimi funktionimi;

  private String tulosTunniste;

  private String tulosTekstiFi;

  private String tulosTekstiSv;

  private String tulosTekstiEn;

  private boolean tallennaTulos;

  private boolean omaopintopolku;

  private Set<Arvokonvertteriparametri> arvokonvertteriparametrit;

  private List<Arvovalikonvertteriparametri> arvovalikonvertteriparametrit;

  private Set<Syoteparametri> syoteparametrit;

  private List<Funktioargumentti> funktioargumentit;

  private List<ValintaperusteViite> valintaperusteviitteet;

  private List<Abstraktivalidointivirhe> validointivirheet;

  public Funktiokutsu(FunktiokutsuId id,
                      long version,
                      Funktionimi funktionimi,
                      String tulosTunniste,
                      String tulosTekstiFi,
                      String tulosTekstiSv,
                      String tulosTekstiEn,
                      boolean tallennaTulos,
                      boolean omaopintopolku,
                      Set<Arvokonvertteriparametri> arvokonvertteriparametrit,
                      List<Arvovalikonvertteriparametri> arvovalikonvertteriparametrit,
                      Set<Syoteparametri> syoteparametrit,
                      List<Funktioargumentti> funktioargumentit,
                      List<ValintaperusteViite> valintaperusteviitteet) {
    this.id = id;
    this.version = version;
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
  }

  public FunktiokutsuId getId() {
    return id;
  }

  public Funktionimi getFunktionimi() {
    return funktionimi;
  }

  public String getTulosTunniste() {
    return tulosTunniste;
  }

  public String getTulosTekstiFi() {
    return tulosTekstiFi;
  }

  public String getTulosTekstiSv() {
    return tulosTekstiSv;
  }

  public String getTulosTekstiEn() {
    return tulosTekstiEn;
  }

  public boolean isTallennaTulos() {
    return tallennaTulos;
  }

  public boolean isOmaopintopolku() {
    return omaopintopolku;
  }

  public Set<Arvokonvertteriparametri> getArvokonvertteriparametrit() {
    return arvokonvertteriparametrit;
  }

  public List<Arvovalikonvertteriparametri> getArvovalikonvertteriparametrit() {
    return arvovalikonvertteriparametrit;
  }

  public Set<Syoteparametri> getSyoteparametrit() {
    return syoteparametrit;
  }

  public List<Funktioargumentti> getFunktioargumentit() {
    return funktioargumentit;
  }

  public List<ValintaperusteViite> getValintaperusteviitteet() {
    return valintaperusteviitteet;
  }

  public List<Abstraktivalidointivirhe> getValidointivirheet() {
    return validointivirheet;
  }

  public void setValidointivirheet(List<Abstraktivalidointivirhe> validointivirheet) {
    this.validointivirheet = validointivirheet;
  }

  public FunktiokutsuDTO toDto() {
    return new FunktiokutsuDTO(

    );
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Funktiokutsu that = (Funktiokutsu) o;

    return id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
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
