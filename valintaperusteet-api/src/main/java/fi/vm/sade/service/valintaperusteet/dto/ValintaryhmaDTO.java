package fi.vm.sade.service.valintaperusteet.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Schema(name = "ValintaryhmaDTO", description = "Valintaryhmä")
public class ValintaryhmaDTO extends ValintaryhmaCreateDTO {
  @Schema(description = "OID", required = true)
  private String oid;

  @Schema(description = "Hakukohdekoodit")
  private Set<KoodiDTO> hakukohdekoodit = new HashSet<KoodiDTO>();

  @Schema(description = "Valintakoekoodit")
  private List<KoodiDTO> valintakoekoodit = new ArrayList<KoodiDTO>();

  @Schema(description = "Onko valintaryhmällä lapsivalintaryhmiä")
  private boolean lapsivalintaryhma;

  @Schema(description = "Onko valintaryhmällä lapsihakukohteita")
  private boolean lapsihakukohde;

  public String getOid() {
    return oid;
  }

  public void setOid(String oid) {
    this.oid = oid;
  }

  public Set<KoodiDTO> getHakukohdekoodit() {
    return hakukohdekoodit;
  }

  public void setHakukohdekoodit(Set<KoodiDTO> hakukohdekoodit) {
    this.hakukohdekoodit = hakukohdekoodit;
  }

  public List<KoodiDTO> getValintakoekoodit() {
    return valintakoekoodit;
  }

  public void setValintakoekoodit(List<KoodiDTO> valintakoekoodit) {
    this.valintakoekoodit = valintakoekoodit;
  }

  public boolean isLapsivalintaryhma() {
    return lapsivalintaryhma;
  }

  public void setLapsivalintaryhma(boolean lapsivalintaryhma) {
    this.lapsivalintaryhma = lapsivalintaryhma;
  }

  public boolean isLapsihakukohde() {
    return lapsihakukohde;
  }

  public void setLapsihakukohde(boolean lapsihakukohde) {
    this.lapsihakukohde = lapsihakukohde;
  }
}
