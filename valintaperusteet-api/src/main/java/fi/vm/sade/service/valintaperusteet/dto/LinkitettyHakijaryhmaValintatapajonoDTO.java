package fi.vm.sade.service.valintaperusteet.dto;

import io.swagger.annotations.ApiModelProperty;

public class LinkitettyHakijaryhmaValintatapajonoDTO extends HakijaryhmaValintatapajonoDTO {

    @ApiModelProperty(value = "Valintatapajono OID", required = false)
    private String valintatapajonoOid;

    @ApiModelProperty(value = "Hakukohde OID", required = false)
    private String hakukohdeOid;

    public String getValintatapajonoOid() {
        return valintatapajonoOid;
    }

    public void setValintatapajonoOid(String valintatapajonoOid) {
        this.valintatapajonoOid = valintatapajonoOid;
    }

    public String getHakukohdeOid() {
        return hakukohdeOid;
    }

    public void setHakukohdeOid(String hakukohdeOid) {
        this.hakukohdeOid = hakukohdeOid;
    }
}
