package fi.vm.sade.service.valintaperusteet.dto;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.HashSet;
import java.util.Set;

/**
 * User: wuoti
 * Date: 2.12.2013
 * Time: 9.47
 */
@ApiModel(value = "ArvokonvertteriparametriDTO", description = "Arvokonvertteriparametri")
public class ArvokonvertteriparametriDTO {

    @ApiModelProperty(value = "Paluuarvo", required = true)
    private String paluuarvo;

    @ApiModelProperty(value = "Arvo", required = true)
    private String arvo;

    @ApiModelProperty(value = "Hylkäysperuste")
    private String hylkaysperuste;

    @ApiModelProperty(value = "Hylkäysperusteen kuvaukset")
    private TekstiRyhmaDTO kuvaukset = new TekstiRyhmaDTO();

    public String getPaluuarvo() {
        return paluuarvo;
    }

    public void setPaluuarvo(String paluuarvo) {
        this.paluuarvo = paluuarvo;
    }

    public String getArvo() {
        return arvo;
    }

    public void setArvo(String arvo) {
        this.arvo = arvo;
    }

    public String getHylkaysperuste() {
        return hylkaysperuste;
    }

    public void setHylkaysperuste(String hylkaysperuste) {
        this.hylkaysperuste = hylkaysperuste;
    }

    public TekstiRyhmaDTO getKuvaukset() {
        return kuvaukset;
    }

    public void setKuvaukset(TekstiRyhmaDTO kuvaukset) {
        this.kuvaukset = kuvaukset;
    }
}
