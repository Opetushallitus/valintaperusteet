package fi.vm.sade.service.valintaperusteet.dto;

import io.swagger.annotations.ApiModelProperty;

public class ValintatapajonoJaHakijaryhmaValintatapajonoDTO extends HakijaryhmaValintatapajonoDTO {

    @ApiModelProperty(value = "Valintatapajono OID", required = false)
    private String valintatapajonoOid;

    public String getValintatapajonoOid() {
        return valintatapajonoOid;
    }

    public void setValintatapajonoOid(String valintatapajonoOid) {
        this.valintatapajonoOid = valintatapajonoOid;
    }
}
