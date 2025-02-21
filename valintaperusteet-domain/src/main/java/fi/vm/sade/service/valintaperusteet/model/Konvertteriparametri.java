package fi.vm.sade.service.valintaperusteet.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

public abstract class Konvertteriparametri extends BaseEntity {
  private String paluuarvo;

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
