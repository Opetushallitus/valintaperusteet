package fi.vm.sade.service.valintaperusteet.dto;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import org.codehaus.jackson.map.annotate.JsonView;

/**
 * User: wuoti
 * Date: 28.11.2013
 * Time: 10.46
 */
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
}
