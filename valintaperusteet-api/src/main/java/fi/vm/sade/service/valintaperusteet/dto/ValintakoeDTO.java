package fi.vm.sade.service.valintaperusteet.dto;

import fi.vm.sade.service.valintaperusteet.dto.model.Koekutsu;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "service.valintaperusteet.dto.ValintakoeDTO", description = "Valintakoe")
public class ValintakoeDTO extends ValintakoeCreateDTO {

  @ApiModelProperty(value = "OID", required = true)
  private String oid;

  @ApiModelProperty(value = "Selvitetty tunniste", required = true)
  private String selvitettyTunniste;

  @ApiModelProperty(value = "Funktiokutsu", required = false)
  private FunktiokutsuDTO funktiokutsu;

  @ApiModelProperty(value = "Peritty", required = false)
  private Boolean peritty;

  public ValintakoeDTO() { }

  public ValintakoeDTO(String tunniste,
                       Long laskentakaavaId,
                       String nimi,
                       String kuvaus,
                       Boolean aktiivinen,
                       Boolean lahetetaankoKoekutsut,
                       Boolean kutsutaankoKaikki,
                       Integer kutsuttavienMaara,
                       Koekutsu kutsunKohde,
                       String kutsunKohdeAvain,
                       String oid,
                       FunktiokutsuDTO funktiokutsu) {
    super(
            tunniste,
            laskentakaavaId,
            nimi,
            kuvaus,
            aktiivinen,
            lahetetaankoKoekutsut,
            kutsutaankoKaikki,
            kutsuttavienMaara,
            kutsunKohde,
            kutsunKohdeAvain
    );
    this.oid = oid;
    this.selvitettyTunniste = tunniste;
    this.funktiokutsu = funktiokutsu;
    this.peritty = null;
  }

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
