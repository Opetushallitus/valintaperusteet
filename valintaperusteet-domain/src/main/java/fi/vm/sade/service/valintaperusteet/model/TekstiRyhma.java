package fi.vm.sade.service.valintaperusteet.model;

import java.util.Set;

public class TekstiRyhma {

  private TekstiRyhmaId id;

  private long version;

  private Set<LokalisoituTeksti> tekstit;

  public TekstiRyhma(TekstiRyhmaId id, long version, Set<LokalisoituTeksti> tekstit) {
    this.id = id;
    this.version = version;
    this.tekstit = tekstit;
  }

  public TekstiRyhmaId getId() {
    return id;
  }

  public Set<LokalisoituTeksti> getTekstit() {
    return tekstit;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    TekstiRyhma that = (TekstiRyhma) o;

    return id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }
}
