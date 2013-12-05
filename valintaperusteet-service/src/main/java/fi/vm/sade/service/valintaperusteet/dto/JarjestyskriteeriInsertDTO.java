package fi.vm.sade.service.valintaperusteet.dto;

import com.fasterxml.jackson.annotation.JsonView;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import fi.vm.sade.service.valintaperusteet.model.JsonViews;

/**
 * User: wuoti
 * Date: 4.12.2013
 * Time: 10.40
 */
@ApiModel(value = "JarjestyskriteeriInsertDTO", description = "Jarjestyskriteeri ja laskentakaava")
public class JarjestyskriteeriInsertDTO {

    @ApiModelProperty(value = "Järjestyskriteeri", required = true)
    @JsonView(JsonViews.Basic.class)
    private JarjestyskriteeriCreateDTO jarjestyskriteeri;

    @ApiModelProperty(value = "Laskentakaava ID", required = true)
    @JsonView(JsonViews.Basic.class)
    private Long laskentakaavaId;

    public JarjestyskriteeriCreateDTO getJarjestyskriteeri() {
        return jarjestyskriteeri;
    }

    public void setJarjestyskriteeri(JarjestyskriteeriCreateDTO jarjestyskriteeri) {
        this.jarjestyskriteeri = jarjestyskriteeri;
    }

    public Long getLaskentakaavaId() {
        return laskentakaavaId;
    }

    public void setLaskentakaavaId(Long laskentakaavaId) {
        this.laskentakaavaId = laskentakaavaId;
    }
}
