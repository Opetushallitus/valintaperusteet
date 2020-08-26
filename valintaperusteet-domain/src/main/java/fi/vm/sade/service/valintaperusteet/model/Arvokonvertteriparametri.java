package fi.vm.sade.service.valintaperusteet.model;

import java.util.Objects;

public class Arvokonvertteriparametri implements Konvertteriparametri {
  private ArvokonvertteriparametriId id;

  private long version;

  private String paluuarvo;

  private String arvo;

  private String hylkaysperuste;

  private TekstiRyhma kuvaukset;

  public Arvokonvertteriparametri(ArvokonvertteriparametriId id,
                                  long version,
                                  String paluuarvo,
                                  String arvo,
                                  String hylkaysperuste,
                                  TekstiRyhma kuvaukset) {
    Objects.requireNonNull(hylkaysperuste, "hylkaysperuste tulee olla asetettu");
    this.id = id;
    this.version = version;
    this.paluuarvo = paluuarvo;
    this.arvo = arvo;
    this.hylkaysperuste = hylkaysperuste;
    this.kuvaukset = kuvaukset;
  }

  public ArvokonvertteriparametriId getId() {
    return id;
  }

  public String getPaluuarvo() {
    return paluuarvo;
  }

  public String getArvo() {
    return arvo;
  }

  public String getHylkaysperuste() {
    return hylkaysperuste;
  }

  public TekstiRyhma getKuvaukset() {
    return kuvaukset;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Arvokonvertteriparametri that = (Arvokonvertteriparametri) o;

    return id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }
}
