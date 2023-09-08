package fi.vm.sade.service.valintaperusteet.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "LaskentakaavaInsertDTO", description = "Laskentakaava")
public class LaskentakaavaInsertDTO {

  public LaskentakaavaInsertDTO() {}

  public LaskentakaavaInsertDTO(
      LaskentakaavaCreateDTO laskentakaava, String hakukohdeOid, String valintaryhmaOid) {
    this.laskentakaava = laskentakaava;
    this.hakukohdeOid = hakukohdeOid;
    this.valintaryhmaOid = valintaryhmaOid;
  }

  @Schema(description = "Laskentakaava", required = true)
  private LaskentakaavaCreateDTO laskentakaava;

  @Schema(description = "Hakukohde OID, jolle laskentakaava lisätään")
  private String hakukohdeOid;

  @Schema(description = "Valintaryhmä OID, jolle laskentakaava lisätään")
  private String valintaryhmaOid;

  public LaskentakaavaCreateDTO getLaskentakaava() {
    return laskentakaava;
  }

  public void setLaskentakaava(LaskentakaavaCreateDTO laskentakaava) {
    this.laskentakaava = laskentakaava;
  }

  public String getHakukohdeOid() {
    return hakukohdeOid;
  }

  public void setHakukohdeOid(String hakukohdeOid) {
    this.hakukohdeOid = hakukohdeOid;
  }

  public String getValintaryhmaOid() {
    return valintaryhmaOid;
  }

  public void setValintaryhmaOid(String valintaryhmaOid) {
    this.valintaryhmaOid = valintaryhmaOid;
  }
}
