package fi.vm.sade.service.valintaperusteet.model;

public class Arvovalikonvertteriparametri extends Konvertteriparametri
    implements Comparable<Arvovalikonvertteriparametri> {

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(this.getClass().getSimpleName());
    sb.append("[min=");
    sb.append(minValue);
    sb.append(", max=");
    sb.append(maxValue);
    sb.append("]");

    return sb.toString();
  }

  private String minValue;

  private String maxValue;

  private String palautaHaettuArvo;

  private String hylkaysperuste;

  private TekstiRyhma kuvaukset;

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

  private static final long serialVersionUID = 7028232303346391201L;

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

  @Override
  public int compareTo(Arvovalikonvertteriparametri o) {
    try {
      return Integer.compare(Integer.parseInt(minValue), Integer.parseInt(o.minValue));
    } catch (Exception e) {
      return minValue.compareTo(o.minValue);
    }
  }
}
