package fi.vm.sade.service.valintaperusteet.dto;

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
public class ValintaperustePuuDTO {

    @JsonView(JsonViews.Basic.class)
    private String hakuOid;

    @JsonView(JsonViews.Basic.class)
    private ValintaperustePuuDTO ylavalintaryhma;

    @JsonView(JsonViews.Basic.class)
    private Set<ValintaperustePuuDTO> alavalintaryhmat = new HashSet<ValintaperustePuuDTO>();

    @JsonView(JsonViews.Basic.class)
    private Set<ValintaperustePuuDTO> hakukohdeViitteet = new HashSet<ValintaperustePuuDTO>();

    @JsonView(JsonViews.Basic.class)
    private String hakuoid;

    @JsonView(JsonViews.Basic.class)
    private String oid;

    @JsonView(JsonViews.Basic.class)
    private String tarjoajaOid;

    @JsonView(JsonViews.Basic.class)
    private String nimi;

    @JsonView(JsonViews.Basic.class)
    private String tila;

    @JsonView(JsonViews.Basic.class)
    private ValintaperustePuuTyyppi tyyppi;


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
}
