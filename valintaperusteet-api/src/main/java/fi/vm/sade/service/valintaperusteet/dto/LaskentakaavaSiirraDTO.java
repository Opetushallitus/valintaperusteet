package fi.vm.sade.service.valintaperusteet.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "LaskentakaavaSiirraDTO", description = "Laskentakaavan siirto DTO")
public class LaskentakaavaSiirraDTO extends LaskentakaavaCreateDTO {

  public LaskentakaavaSiirraDTO() {}

  @Schema(description = "Nimi")
  private String uusinimi;

  @Schema(description = "Valintaryhmä OID, jolle laskentakaava lisätään")
  private String valintaryhmaOid;

  @Schema(description = "Hakukohde OID, jolle laskentakaava lisätään")
  private String hakukohdeOid;

  public String getValintaryhmaOid() {
    return valintaryhmaOid;
  }

  public void setValintaryhmaOid(String valintaryhmaOid) {
    this.valintaryhmaOid = valintaryhmaOid;
  }

  public String getUusinimi() {
    return uusinimi;
  }

  public void setUusinimi(String uusinimi) {
    this.uusinimi = uusinimi;
  }

  public String getHakukohdeOid() {
    return hakukohdeOid;
  }

  public void setHakukohdeOid(String hakukohdeOid) {
    this.hakukohdeOid = hakukohdeOid;
  }
}
