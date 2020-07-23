package fi.vm.sade.service.valintaperusteet.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "HakukohdeViiteCreateDTO", description = "Hakukohteen lisääminen")
public class HakukohdeViiteCreateDTO {

  @ApiModelProperty(value = "Nimi")
  private String nimi;

  @ApiModelProperty(value = "Haku OID", required = true)
  private String hakuoid;

  @ApiModelProperty(value = "OID", required = true)
  private String oid;

  @ApiModelProperty(value = "Tarjoaja OID")
  private String tarjoajaOid;

  @ApiModelProperty(value = "Tila")
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
