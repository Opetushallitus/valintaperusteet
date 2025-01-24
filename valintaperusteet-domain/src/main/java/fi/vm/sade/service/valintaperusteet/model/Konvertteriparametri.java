package fi.vm.sade.service.valintaperusteet.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@MappedSuperclass
public abstract class Konvertteriparametri extends BaseEntity {
  @Column(name = "paluuarvo")
  private String paluuarvo;

  @JoinColumn(name = "funktiokutsu_id", nullable = false)
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JsonBackReference
  private Funktiokutsu funktiokutsu;

  public String getPaluuarvo() {
    return paluuarvo;
  }

  public void setPaluuarvo(String paluuarvo) {
    this.paluuarvo = paluuarvo;
  }

  public Funktiokutsu getFunktiokutsu() {
    return funktiokutsu;
  }

  public void setFunktiokutsu(Funktiokutsu funktiokutsu) {
    this.funktiokutsu = funktiokutsu;
  }
}
