package fi.vm.sade.service.valintaperusteet.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "JarjestyskriteeriInsertDTO", description = "Jarjestyskriteeri ja laskentakaava")
public class JarjestyskriteeriInsertDTO {

    @ApiModelProperty(value = "Järjestyskriteeri", required = true)
    private JarjestyskriteeriCreateDTO jarjestyskriteeri;

    @ApiModelProperty(value = "Laskentakaava ID", required = true)
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
