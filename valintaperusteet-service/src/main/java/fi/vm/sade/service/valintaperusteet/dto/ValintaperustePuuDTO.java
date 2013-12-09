package fi.vm.sade.service.valintaperusteet.dto;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import org.codehaus.jackson.map.annotate.JsonView;

import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: kkammone
 * Date: 17.10.2013
 * Time: 13:00
 * To change this template use File | Settings | File Templates.
 */

@ApiModel(value = "ValintaperustePuuDTO", description = "Valintaperustepuu")
public class ValintaperustePuuDTO {

    @ApiModelProperty(value = "Ylävalintaryhmä", required = true)
    private ValintaperustePuuDTO ylavalintaryhma;

    @ApiModelProperty(value = "Alavalintaryhmät", required = true)
    private Set<ValintaperustePuuDTO> alavalintaryhmat = new HashSet<ValintaperustePuuDTO>();

    @ApiModelProperty(value = "Hakukohdeviitteet", required = true)
    private Set<ValintaperustePuuDTO> hakukohdeViitteet = new HashSet<ValintaperustePuuDTO>();

    @ApiModelProperty(value = "Haku OID", required = true)
    private String hakuOid;

    @ApiModelProperty(value = "OID", required = true)
    private String oid;

    @ApiModelProperty(value = "Tarjoaja OID", required = true)
    private String tarjoajaOid;

    @ApiModelProperty(value = "Nimi", required = true)
    private String nimi;

    @ApiModelProperty(value = "Tila", required = true)
    private String tila;

    @ApiModelProperty(value = "Valintaperustepuun tyyppi", required = true)
    private ValintaperustePuuTyyppi tyyppi;

    @ApiModelProperty(value = "Organisaatiot", required = true)
    private Set<OrganisaatioDTO> organisaatiot = new HashSet<OrganisaatioDTO>();

    public String getHakuOid() {
        return hakuOid;
    }

    public void setHakuOid(String hakuOid) {
        this.hakuOid = hakuOid;
    }

    public ValintaperustePuuDTO getYlavalintaryhma() {
        return ylavalintaryhma;
    }

    public void setYlavalintaryhma(ValintaperustePuuDTO ylavalintaryhma) {
        this.ylavalintaryhma = ylavalintaryhma;
    }

    public Set<ValintaperustePuuDTO> getAlavalintaryhmat() {
        return alavalintaryhmat;
    }

    public void setAlavalintaryhmat(Set<ValintaperustePuuDTO> alavalintaryhmat) {
        this.alavalintaryhmat = alavalintaryhmat;
    }

    public Set<ValintaperustePuuDTO> getHakukohdeViitteet() {
        return hakukohdeViitteet;
    }

    public void setHakukohdeViitteet(Set<ValintaperustePuuDTO> hakukohdeViitteet) {
        this.hakukohdeViitteet = hakukohdeViitteet;
    }



    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getTarjoajaOid() {
        return tarjoajaOid;
    }

    public void setTarjoajaOid(String tarjoajaOid) {
        this.tarjoajaOid = tarjoajaOid;
    }

    public String getNimi() {
        return nimi;
    }

    public void setNimi(String nimi) {
        this.nimi = nimi;
    }

    public String getTila() {
        return tila;
    }

    public void setTila(String tila) {
        this.tila = tila;
    }
    public ValintaperustePuuTyyppi getTyyppi()
{
        return tyyppi;
    }

    public void setTyyppi(ValintaperustePuuTyyppi tyyppi) {
        this.tyyppi = tyyppi;
    }

    public Set<OrganisaatioDTO> getOrganisaatiot() {
        return organisaatiot;
    }

    public void setOrganisaatiot(Set<OrganisaatioDTO> organisaatiot) {
        this.organisaatiot = organisaatiot;
    }
}
