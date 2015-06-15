package fi.vm.sade.service.valintaperusteet.dto;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@ApiModel(value = "JarjestyskriteeriDTO", description = "Järjestyskriteeri")
public class JarjestyskriteeriDTO extends JarjestyskriteeriCreateDTO {
    @ApiModelProperty(value = "OID", required = true)
    private String oid;

    @ApiModelProperty(value = "Valintatapajono OID")
    private String valintatapajonoOid;

    @ApiModelProperty(value = "Onko järjestyskriteeri peritty")
    private Boolean inheritance;

    @ApiModelProperty(value = "Laskentakaava ID", required = true)
    private Long laskentakaavaId;

    @ApiModelProperty(value = "Järjestyskriteerin prioriteetti", required = false)
    private int prioriteetti;

    @ApiModelProperty(value = "Funktiokutsu", required = false)
    private FunktiokutsuDTO funktiokutsu;

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getValintatapajonoOid() {
        return valintatapajonoOid;
    }

    public void setValintatapajonoOid(String valintatapajonoOid) {
        this.valintatapajonoOid = valintatapajonoOid;
    }

    public Boolean getInheritance() {
        return inheritance;
    }

    public void setInheritance(Boolean inheritance) {
        this.inheritance = inheritance;
    }

    public Long getLaskentakaavaId() {
        return laskentakaavaId;
    }

    public void setLaskentakaavaId(Long laskentakaavaId) {
        this.laskentakaavaId = laskentakaavaId;
    }

    public int getPrioriteetti() {
        return prioriteetti;
    }

    public void setPrioriteetti(int prioriteetti) {
        this.prioriteetti = prioriteetti;
    }

    public FunktiokutsuDTO getFunktiokutsu() {
        return funktiokutsu;
    }

    public void setFunktiokutsu(FunktiokutsuDTO funktiokutsu) {
        this.funktiokutsu = funktiokutsu;
    }
}
