package fi.vm.sade.service.valintaperusteet.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "service.valintaperusteet.dto.ValintakoeDTO", description = "Valintakoe")
public class ValintakoeDTO extends ValintakoeCreateDTO {

  @Schema(description = "OID", required = true)
  private String oid;

  @Schema(description = "Selvitetty tunniste", required = true)
  private String selvitettyTunniste;

  @Schema(description = "Funktiokutsu")
  private FunktiokutsuDTO funktiokutsu;

  @Schema(description = "Peritty")
  private Boolean peritty;

  public String getSelvitettyTunniste() {
    return selvitettyTunniste;
  }

  public void setSelvitettyTunniste(String selvitettyTunniste) {
    this.selvitettyTunniste = selvitettyTunniste;
  }

  public String getOid() {
    return oid;
  }

  public void setOid(String oid) {
    this.oid = oid;
  }

  public FunktiokutsuDTO getFunktiokutsu() {
    return funktiokutsu;
  }

  public void setFunktiokutsu(FunktiokutsuDTO funktiokutsu) {
    this.funktiokutsu = funktiokutsu;
  }

  public Boolean getPeritty() {
    return peritty;
  }

  public void setPeritty(Boolean peritty) {
    this.peritty = peritty;
  }

  @Override
  public String toString() {
    return "ValintakoeDTO{"
        + "oid='"
        + oid
        + '\''
        + ", selvitettyTunniste='"
        + selvitettyTunniste
        + '\''
        + ", funktiokutsu="
        + funktiokutsu
        + ", peritty="
        + peritty
        + '}';
  }
}
