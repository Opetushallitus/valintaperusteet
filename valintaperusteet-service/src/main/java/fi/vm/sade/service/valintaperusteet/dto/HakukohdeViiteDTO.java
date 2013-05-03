package fi.vm.sade.service.valintaperusteet.dto;

import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: jukais
 * Date: 8.2.2013
 * Time: 13.34
 * To change this template use File | Settings | File Templates.
 */
public class HakukohdeViiteDTO {

    private String nimi;
    private String hakuoid;
    private String oid;
    private String valintaryhmaOid;
    private Set<String> valinnanvaiheetOids;

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
}
