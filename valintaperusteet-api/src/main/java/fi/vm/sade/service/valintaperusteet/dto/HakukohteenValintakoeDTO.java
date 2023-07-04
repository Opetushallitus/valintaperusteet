package fi.vm.sade.service.valintaperusteet.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "HakukohteenValintakoeDTO", description = "Hakukohteen valintakoe")
public class HakukohteenValintakoeDTO {
  private String tyyppiUri;
  private String oid;

  public String getTyyppiUri() {
    return tyyppiUri;
  }

  public void setTyyppiUri(String tyyppiUri) {
    this.tyyppiUri = tyyppiUri;
  }

  public void setOid(String oid) {
    this.oid = oid;
  }

  public String getOid() {
    return oid;
  }
}
