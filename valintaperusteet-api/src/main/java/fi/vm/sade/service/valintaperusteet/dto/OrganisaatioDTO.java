package fi.vm.sade.service.valintaperusteet.dto;

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
