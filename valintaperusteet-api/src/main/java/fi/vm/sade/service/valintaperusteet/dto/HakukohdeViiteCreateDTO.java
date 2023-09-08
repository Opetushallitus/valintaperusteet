package fi.vm.sade.service.valintaperusteet.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "HakukohdeViiteCreateDTO", description = "Hakukohteen lisääminen")
public class HakukohdeViiteCreateDTO {

  @Schema(description = "Nimi")
  private String nimi;

  @Schema(description = "Haku OID", required = true)
  private String hakuoid;

  @Schema(description = "OID", required = true)
  private String oid;

  @Schema(description = "Tarjoaja OID")
  private String tarjoajaOid;

  @Schema(description = "Tila")
  private String tila;

  public String getNimi() {
    return nimi;
  }

  public void setNimi(String nimi) {
    this.nimi = nimi;
  }

  public String getHakuoid() {
    return hakuoid;
  }

  public void setHakuoid(String hakuoid) {
    this.hakuoid = hakuoid;
  }

  public String getOid() {
    return oid;
  }

  public void setOid(String oid) {
    this.oid = oid;
  }

  public String getTila() {
    return tila;
  }

  public void setTila(String tila) {
    this.tila = tila;
  }

  public String getTarjoajaOid() {
    return tarjoajaOid;
  }

  public void setTarjoajaOid(String tarjoajaOid) {
    this.tarjoajaOid = tarjoajaOid;
  }
}
