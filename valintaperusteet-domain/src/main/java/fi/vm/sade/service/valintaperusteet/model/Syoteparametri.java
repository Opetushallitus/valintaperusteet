package fi.vm.sade.service.valintaperusteet.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "syoteparametri")
@Cacheable(true)
public class Syoteparametri extends BaseEntity {
  @Column(name = "avain", nullable = false)
  private String avain;

  @Column(name = "arvo", nullable = false)
  private String arvo;

  @JoinColumn(name = "funktiokutsu_id", nullable = false)
  @ManyToOne(optional = false)
  @JsonBackReference
  private Funktiokutsu funktiokutsu;

  public String getAvain() {
    return avain;
  }

  public void setAvain(String avain) {
    this.avain = avain;
  }

  public String getArvo() {
    return arvo;
  }

  public void setArvo(String arvo) {
    this.arvo = arvo;
  }

  public Funktiokutsu getFunktiokutsu() {
    return funktiokutsu;
  }

  public void setFunktiokutsu(Funktiokutsu funktiokutsu) {
    this.funktiokutsu = funktiokutsu;
  }
}
