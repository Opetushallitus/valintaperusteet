package fi.vm.sade.service.valintaperusteet.dto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * User: wuoti Date: 2.12.2013 Time: 14.08
 */
@ApiModel(value = "ValintaryhmaDTO", description = "Valintaryhmä")
public class ValintaryhmaDTO extends ValintaryhmaCreateDTO {

    @ApiModelProperty(value = "OID", required = true)
    private String oid;

    @ApiModelProperty(value = "Hakukohdekoodit")
    private Set<KoodiDTO> hakukohdekoodit = new HashSet<KoodiDTO>();

    @ApiModelProperty(value = "Valintakoekoodit")
    private List<KoodiDTO> valintakoekoodit = new ArrayList<KoodiDTO>();

    @ApiModelProperty(value = "Onko valintaryhmällä lapsivalintaryhmiä")
    private Boolean lapsivalintaryhma;

    @ApiModelProperty(value = "Onko valintaryhmällä lapsihakukohteita")
    private Boolean lapsihakukohde;

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

    public Boolean getLapsivalintaryhma() {
        return lapsivalintaryhma;
    }

    public void setLapsivalintaryhma(Boolean lapsivalintaryhma) {
        this.lapsivalintaryhma = lapsivalintaryhma;
    }

    public Boolean getLapsihakukohde() {
        return lapsihakukohde;
    }

    public void setLapsihakukohde(Boolean lapsihakukohde) {
        this.lapsihakukohde = lapsihakukohde;
    }
}
