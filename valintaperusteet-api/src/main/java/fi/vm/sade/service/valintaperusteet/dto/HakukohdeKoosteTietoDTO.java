package fi.vm.sade.service.valintaperusteet.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;

@Schema(
    name = "HakukohdeKoosteTietoDTO",
    description = "Hakukohteen valintaperusteista koostettuja tietoja")
public class HakukohdeKoosteTietoDTO {
  public final String hakukohdeOid;
  public final Boolean hasValintakoe;
  public final Date varasijatayttoPaattyy;

  public HakukohdeKoosteTietoDTO(
      String hakukohdeOid, Boolean hasValintakoe, Date varasijatayttoPaattyy) {
    this.hakukohdeOid = hakukohdeOid;
    this.hasValintakoe = hasValintakoe;
    this.varasijatayttoPaattyy = varasijatayttoPaattyy;
  }
}
