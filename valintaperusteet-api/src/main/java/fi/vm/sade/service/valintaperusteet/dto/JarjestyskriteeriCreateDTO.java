package fi.vm.sade.service.valintaperusteet.dto;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

@ApiModel(value = "JarjestyskriteeriCreateDTO", description = "Järjestyskriteeri")
public class JarjestyskriteeriCreateDTO {

    @ApiModelProperty(value = "Metatiedot")
    private String metatiedot;

    @ApiModelProperty(value = "Aktiivinen", required = true)
    private Boolean aktiivinen;

    public String getMetatiedot() {
        return metatiedot;
    }

    public void setMetatiedot(String metatiedot) {
        this.metatiedot = metatiedot;
    }

    public Boolean getAktiivinen() {
        return aktiivinen;
    }

    public void setAktiivinen(Boolean aktiivinen) {
        this.aktiivinen = aktiivinen;
    }
}
