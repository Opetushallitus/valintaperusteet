package fi.vm.sade.service.valintaperusteet.model;

import java.util.Objects;

public class Arvovalikonvertteriparametri implements Konvertteriparametri, Comparable<Arvovalikonvertteriparametri> {

  private ArvovalikonvertteriparametriId id;

  private long version;

  private String paluuarvo;

  private String minValue;

  private String maxValue;

  private String palautaHaettuArvo;

  private String hylkaysperuste;

  private TekstiRyhma kuvaukset;

  public Arvovalikonvertteriparametri(ArvovalikonvertteriparametriId id,
                                      long version,
                                      String paluuarvo,
                                      String minValue,
                                      String maxValue,
                                      String palautaHaettuArvo,
                                      String hylkaysperuste,
                                      TekstiRyhma kuvaukset) {
    Objects.requireNonNull(palautaHaettuArvo, "palautaHaettuArvo tulee olla asetettu");
    this.id = id;
    this.version = version;
    this.paluuarvo = paluuarvo;
    this.minValue = minValue;
    this.maxValue = maxValue;
    this.palautaHaettuArvo = palautaHaettuArvo;
    this.hylkaysperuste = hylkaysperuste;
    this.kuvaukset = kuvaukset;
  }

  public ArvovalikonvertteriparametriId getId() {
    return id;
  }

  public String getPaluuarvo() {
    return paluuarvo;
  }

  public String getMinValue() {
    return minValue;
  }

  public String getMaxValue() {
    return maxValue;
  }

  public String getPalautaHaettuArvo() {
    return palautaHaettuArvo;
  }

  public String getHylkaysperuste() {
    return hylkaysperuste;
  }

  public TekstiRyhma getKuvaukset() {
    return kuvaukset;
  }

  @Override
  public int compareTo(Arvovalikonvertteriparametri o) {
    try {
      return Integer.compare(Integer.parseInt(minValue), Integer.parseInt(o.minValue));
    } catch (Exception e) {
      return minValue.compareTo(o.minValue);
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Arvovalikonvertteriparametri that = (Arvovalikonvertteriparametri) o;

    return id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

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
}
