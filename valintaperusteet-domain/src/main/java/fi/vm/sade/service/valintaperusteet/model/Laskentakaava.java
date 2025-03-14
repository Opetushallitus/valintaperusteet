package fi.vm.sade.service.valintaperusteet.model;

import fi.vm.sade.service.valintaperusteet.dto.model.Funktiotyyppi;
import jakarta.persistence.Cacheable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "laskentakaava")
@Cacheable(true)
public class Laskentakaava extends BaseEntity implements FunktionArgumentti {
  @Column(name = "on_luonnos", nullable = false)
  private Boolean onLuonnos;

  @Column(name = "nimi", nullable = false)
  private String nimi;

  @Column(name = "kuvaus")
  private String kuvaus;

  @JoinColumn(name = "kopio_laskentakaavasta_id", nullable = true, unique = false)
  @ManyToOne(fetch = FetchType.LAZY)
  private Laskentakaava kopioLaskentakaavasta;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "valintaryhmaviite", nullable = true, unique = false)
  private Valintaryhma valintaryhma;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "hakukohdeviite", nullable = true, unique = false)
  private HakukohdeViite hakukohde;

  @JoinColumn(name = "funktiokutsu_id", nullable = false, unique = false)
  @ManyToOne(optional = false, cascade = CascadeType.PERSIST)
  private Funktiokutsu funktiokutsu;

  @Column(name = "tyyppi", nullable = false)
  @Enumerated(EnumType.STRING)
  private Funktiotyyppi tyyppi;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "kopioLaskentakaavasta")
  private Set<Laskentakaava> kopiot = new HashSet<>();

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "laskentakaava", cascade = CascadeType.PERSIST)
  private Set<Jarjestyskriteeri> jarjestyskriteerit = new HashSet<Jarjestyskriteeri>();

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

  public Valintaryhma getValintaryhma() {
    return valintaryhma;
  }

  public void setValintaryhma(Valintaryhma valintaryhma) {
    this.valintaryhma = valintaryhma;
  }

  public HakukohdeViite getHakukohde() {
    return hakukohde;
  }

  public void setHakukohde(HakukohdeViite hakukohde) {
    this.hakukohde = hakukohde;
  }

  public Funktiokutsu getFunktiokutsu() {
    return funktiokutsu;
  }

  public void setFunktiokutsu(Funktiokutsu funktiokutsu) {
    this.funktiokutsu = funktiokutsu;
  }

  public Funktiotyyppi getTyyppi() {
    return tyyppi;
  }

  public void setTyyppi(Funktiotyyppi tyyppi) {
    this.tyyppi = tyyppi;
  }

  public Set<Jarjestyskriteeri> getJarjestyskriteerit() {
    return jarjestyskriteerit;
  }

  public void setJarjestyskriteerit(Set<Jarjestyskriteeri> jarjestyskriteerit) {
    this.jarjestyskriteerit = jarjestyskriteerit;
  }

  public Set<Laskentakaava> getKopiot() {
    return kopiot;
  }

  public void setKopiot(Set<Laskentakaava> kopiot) {
    this.kopiot = kopiot;
  }

  @Override
  public Long getId() {
    return super.getId();
  }

  @PrePersist
  @PreUpdate
  private void fixIt() {
    updateTyyppi();
    korjaaFunktiokutsunNimi();
  }

  private void updateTyyppi() {
    if (funktiokutsu != null) {
      tyyppi = funktiokutsu.getFunktionimi().getTyyppi();
    }
  }

  private void korjaaFunktiokutsunNimi() {
    if (funktiokutsu != null) {
      for (Syoteparametri parametri : getFunktiokutsu().getSyoteparametrit()) {
        if (parametri.getAvain().equals("nimi")) {
          parametri.setArvo(getNimi());
        }
      }
    }
  }

  public Laskentakaava getKopioLaskentakaavasta() {
    return kopioLaskentakaavasta;
  }

  public void setKopioLaskentakaavasta(Laskentakaava kopioLaskentakaavasta) {
    this.kopioLaskentakaavasta = kopioLaskentakaavasta;
  }
}
