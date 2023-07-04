package fi.vm.sade.service.valintaperusteet.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ArvokonvertteriparametriDTO", description = "Arvokonvertteriparametri")
public class ArvokonvertteriparametriDTO {

  @Schema(description = "Paluuarvo", required = true)
  private String paluuarvo;

  @Schema(description = "Arvo", required = true)
  private String arvo;

  @Schema(description = "Hylkäysperuste")
  private String hylkaysperuste;

  @Schema(description = "Hylkäysperusteen kuvaukset")
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
