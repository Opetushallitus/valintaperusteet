package fi.vm.sade.service.valintaperusteet.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "ArvovalikonvertteriparametriDTO", description = "Arvovälikonvertteriparametri")
public class ArvovalikonvertteriparametriDTO
    implements Comparable<ArvovalikonvertteriparametriDTO> {
  @ApiModelProperty(value = "Paluuarvo")
  private String paluuarvo;

  @ApiModelProperty(value = "Minimiarvo", required = true)
  private String minValue;

  @ApiModelProperty(value = "Maksimiarvo", required = true)
  private String maxValue;

  @ApiModelProperty(value = "Palautetaanko haettu arvo")
  private String palautaHaettuArvo;

  @ApiModelProperty(value = "Hylkäysperuste")
  private String hylkaysperuste;

  @ApiModelProperty(value = "Hylkäysperusteen kuvaukset")
  private TekstiRyhmaDTO kuvaukset = new TekstiRyhmaDTO();

  public ArvovalikonvertteriparametriDTO() { }

  public ArvovalikonvertteriparametriDTO(String paluuarvo,
                                         String minValue,
                                         String maxValue,
                                         String palautaHaettuArvo,
                                         String hylkaysperuste,
                                         TekstiRyhmaDTO kuvaukset) {
    this.paluuarvo = paluuarvo;
    this.minValue = minValue;
    this.maxValue = maxValue;
    this.palautaHaettuArvo = palautaHaettuArvo;
    this.hylkaysperuste = hylkaysperuste;
    this.kuvaukset = kuvaukset;
  }

  public String getPaluuarvo() {
    return paluuarvo;
  }

  public void setPaluuarvo(String paluuarvo) {
    this.paluuarvo = paluuarvo;
  }

  public String getMinValue() {
    return minValue;
  }

  public void setMinValue(String minValue) {
    this.minValue = minValue;
  }

  public String getMaxValue() {
    return maxValue;
  }

  public void setMaxValue(String maxValue) {
    this.maxValue = maxValue;
  }

  public String getPalautaHaettuArvo() {
    return palautaHaettuArvo;
  }

  public void setPalautaHaettuArvo(String palautaHaettuArvo) {
    this.palautaHaettuArvo = palautaHaettuArvo;
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

  @Override
  public int compareTo(ArvovalikonvertteriparametriDTO o) {
    try {
      return Integer.compare(Integer.parseInt(minValue), Integer.parseInt(o.minValue));
    } catch (Exception e) {
      return minValue.compareTo(o.minValue);
    }
  }
}
