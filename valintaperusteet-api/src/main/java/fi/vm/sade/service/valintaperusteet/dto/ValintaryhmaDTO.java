package fi.vm.sade.service.valintaperusteet.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ApiModel(value = "ValintaryhmaDTO", description = "Valintaryhmä")
public class ValintaryhmaDTO extends ValintaryhmaCreateDTO {
  @ApiModelProperty(value = "OID", required = true)
  private String oid;

  @ApiModelProperty(value = "Hakukohdekoodit")
  private Set<KoodiDTO> hakukohdekoodit = new HashSet<KoodiDTO>();

  @ApiModelProperty(value = "Valintakoekoodit")
  private List<KoodiDTO> valintakoekoodit = new ArrayList<KoodiDTO>();

  @ApiModelProperty(value = "Onko valintaryhmällä lapsivalintaryhmiä")
  private boolean lapsivalintaryhma;

  @ApiModelProperty(value = "Onko valintaryhmällä lapsihakukohteita")
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
