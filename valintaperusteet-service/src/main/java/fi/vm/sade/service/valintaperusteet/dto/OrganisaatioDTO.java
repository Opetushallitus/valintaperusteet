package fi.vm.sade.service.valintaperusteet.dto;

import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import org.codehaus.jackson.map.annotate.JsonView;

/**
 * Created with IntelliJ IDEA.
 * User: jukais
 * Date: 2.12.2013
 * Time: 16.39
 * To change this template use File | Settings | File Templates.
 */
public class OrganisaatioDTO {

    String oid;

    String parentOidPath;

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getParentOidPath() {
        return parentOidPath;
    }

    public void setParentOidPath(String parentOidPath) {
        this.parentOidPath = parentOidPath;
    }
}
