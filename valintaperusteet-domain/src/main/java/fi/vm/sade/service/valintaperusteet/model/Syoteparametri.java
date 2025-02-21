package fi.vm.sade.service.valintaperusteet.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

public class Syoteparametri extends BaseEntity {
  private String avain;

  private String arvo;

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
