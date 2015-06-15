package fi.vm.sade.service.valintaperusteet.dto;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@ApiModel(value = "LaskentakaavaInsertDTO", description = "Laskentakaava")
public class LaskentakaavaInsertDTO {

    public LaskentakaavaInsertDTO() {
    }

    public LaskentakaavaInsertDTO(LaskentakaavaCreateDTO laskentakaava, String hakukohdeOid, String valintaryhmaOid) {
        this.laskentakaava = laskentakaava;
        this.hakukohdeOid = hakukohdeOid;
        this.valintaryhmaOid = valintaryhmaOid;
    }

    @ApiModelProperty(value = "Laskentakaava", required = true)
    private LaskentakaavaCreateDTO laskentakaava;

    @ApiModelProperty(value = "Hakukohde OID, jolle laskentakaava lisätään")
    private String hakukohdeOid;

    @ApiModelProperty(value = "Valintaryhmä OID, jolle laskentakaava lisätään")
    private String valintaryhmaOid;

    public LaskentakaavaCreateDTO getLaskentakaava() {
        return laskentakaava;
    }

    public void setLaskentakaava(LaskentakaavaCreateDTO laskentakaava) {
        this.laskentakaava = laskentakaava;
    }

    public String getHakukohdeOid() {
        return hakukohdeOid;
    }

    public void setHakukohdeOid(String hakukohdeOid) {
        this.hakukohdeOid = hakukohdeOid;
    }

    public String getValintaryhmaOid() {
        return valintaryhmaOid;
    }

    public void setValintaryhmaOid(String valintaryhmaOid) {
        this.valintaryhmaOid = valintaryhmaOid;
    }
}
