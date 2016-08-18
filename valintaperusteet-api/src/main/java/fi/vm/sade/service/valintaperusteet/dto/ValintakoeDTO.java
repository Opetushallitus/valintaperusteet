package fi.vm.sade.service.valintaperusteet.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "ValintakoeDTO", description = "Valintakoe")
public class ValintakoeDTO extends ValintakoeCreateDTO {

    @ApiModelProperty(value = "OID", required = true)
    private String oid;

    @ApiModelProperty(value = "Selvitetty tunniste", required = true)
    private String selvitettyTunniste;

    @ApiModelProperty(value = "Funktiokutsu", required = false)
    private FunktiokutsuDTO funktiokutsu;

    public String getSelvitettyTunniste() {
        return selvitettyTunniste;
    }

    public void setSelvitettyTunniste(String selvitettyTunniste) {
        this.selvitettyTunniste = selvitettyTunniste;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public FunktiokutsuDTO getFunktiokutsu() {
        return funktiokutsu;
    }

    public void setFunktiokutsu(FunktiokutsuDTO funktiokutsu) {
        this.funktiokutsu = funktiokutsu;
    }

}
