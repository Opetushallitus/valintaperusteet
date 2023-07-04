package fi.vm.sade.service.valintaperusteet.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class LinkitettyHakijaryhmaValintatapajonoDTO extends HakijaryhmaValintatapajonoDTO {

  @Schema(description = "Valintatapajono OID")
  private String valintatapajonoOid;

  @Schema(description = "Hakukohde OID")
  private String hakukohdeOid;

  public String getValintatapajonoOid() {
    return valintatapajonoOid;
  }

  public void setValintatapajonoOid(String valintatapajonoOid) {
    this.valintatapajonoOid = valintatapajonoOid;
  }

  public String getHakukohdeOid() {
    return hakukohdeOid;
  }

  public void setHakukohdeOid(String hakukohdeOid) {
    this.hakukohdeOid = hakukohdeOid;
  }
}
