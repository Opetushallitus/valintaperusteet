package fi.vm.sade.service.valintaperusteet.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import fi.vm.sade.service.valintaperusteet.dto.model.Funktiotyyppi;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "funktiokutsu", columnDefinition = "jsonb")
  @Convert(converter = JsonNodeConverter.class)
  private FunktiokutsuWrapper kaava;

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
    if (this.kaava != null) {
      return this.kaava.getFunktiokutsu();
    }
    return funktiokutsu;
  }

  public void setFunktiokutsu(Funktiokutsu funktiokutsu) {
    this.funktiokutsu = funktiokutsu;
    this.kaava = new FunktiokutsuWrapper(funktiokutsu);
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

  public void migrateKaava() {
    this.kaava = new FunktiokutsuWrapper(this.funktiokutsu);
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

  /* Hibernate kilahtaa (palauttaa hauista duplikaatteja) jos jsonb-kentässä oleva tyyppi on samalla entiteetti
   *  joten käytetään wrapper-luokkaa */
  public static class FunktiokutsuWrapper {
    private Funktiokutsu funktiokutsu;

    public FunktiokutsuWrapper(Funktiokutsu funktiokutsu) {
      this.funktiokutsu = funktiokutsu;
    }

    public Funktiokutsu getFunktiokutsu() {
      return this.funktiokutsu;
    }
  }

  public Laskentakaava getKopioLaskentakaavasta() {
    return kopioLaskentakaavasta;
  }

  public void setKopioLaskentakaavasta(Laskentakaava kopioLaskentakaavasta) {
    this.kopioLaskentakaavasta = kopioLaskentakaavasta;
  }

  @Converter(autoApply = true)
  static class JsonNodeConverter implements AttributeConverter<FunktiokutsuWrapper, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(FunktiokutsuWrapper wrapper) {
      try {
        return objectMapper.writeValueAsString(wrapper == null ? null : wrapper.getFunktiokutsu());
      } catch (JsonProcessingException e) {
        throw new RuntimeException("Error while serializing JsonNode to JSON", e);
      }
    }

    @Override
    public FunktiokutsuWrapper convertToEntityAttribute(String dbData) {
      try {
        return dbData == null
            ? null
            : new FunktiokutsuWrapper(objectMapper.readValue(dbData, Funktiokutsu.class));
      } catch (JsonProcessingException e) {
        throw new RuntimeException("Error while deserializing JSON to JsonNode", e);
      }
    }
  }
}
