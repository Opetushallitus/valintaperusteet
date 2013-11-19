package fi.vm.sade.service.valintaperusteet.dto;

import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import org.codehaus.jackson.map.annotate.JsonView;

import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: jukais
 * Date: 8.2.2013
 * Time: 13.34
 * To change this template use File | Settings | File Templates.
 */
public class HakukohdeViiteDTO {

    @JsonView(JsonViews.Basic.class)
    private String nimi;
    @JsonView(JsonViews.Basic.class)
    private String hakuoid;
    @JsonView(JsonViews.Basic.class)
    private String oid;
    @JsonView(JsonViews.Basic.class)
    private String valintaryhmaOid;
    @JsonView(JsonViews.Basic.class)
    private String tarjoajaOid;
    @JsonView(JsonViews.Basic.class)
    private Set<String> valinnanvaiheetOids;
    @JsonView(JsonViews.Basic.class)
    private String tila;

    public String getNimi() {
        return nimi;
    }

    public void setNimi(String nimi) {
        this.nimi = nimi;
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

    public String getValintaryhmaOid() {
        return valintaryhmaOid;
    }

    public Set<String> getValinnanvaiheetOids() {
        return valinnanvaiheetOids;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public void setValintaryhmaOid(String valintaryhmaOid) {
        this.valintaryhmaOid = valintaryhmaOid;
    }

    public void setValinnanvaiheetOids(Set<String> valinnanvaiheetOids) {
        this.valinnanvaiheetOids = valinnanvaiheetOids;
    }

    public String getTila() {
        return tila;
    }

    public void setTila(String tila) {
        this.tila = tila;
    }

    public String getTarjoajaOid() {
        return tarjoajaOid;
    }

    public void setTarjoajaOid(String tarjoajaOid) {
        this.tarjoajaOid = tarjoajaOid;
    }
}
