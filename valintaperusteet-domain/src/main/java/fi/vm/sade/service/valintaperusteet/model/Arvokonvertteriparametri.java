package fi.vm.sade.service.valintaperusteet.model;

public class Arvokonvertteriparametri extends Konvertteriparametri {
  private String arvo;

  private String hylkaysperuste;

  private TekstiRyhma kuvaukset;

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

  public TekstiRyhma getKuvaukset() {
    return kuvaukset;
  }

  public void setKuvaukset(TekstiRyhma kuvaukset) {
    this.kuvaukset = kuvaukset;
  }
}
