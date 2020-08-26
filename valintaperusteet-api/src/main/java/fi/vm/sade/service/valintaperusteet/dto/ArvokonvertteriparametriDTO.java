package fi.vm.sade.service.valintaperusteet.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

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

  public ArvokonvertteriparametriDTO() { }

  public ArvokonvertteriparametriDTO(String paluuarvo,
                                     String arvo,
                                     String hylkaysperuste,
                                     TekstiRyhmaDTO kuvaukset) {
    this.paluuarvo = paluuarvo;
    this.arvo = arvo;
    this.hylkaysperuste = hylkaysperuste;
    this.kuvaukset = kuvaukset;
  }

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
