package fi.vm.sade.service.valintaperusteet.model;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

@Entity
@Table(name = "tekstiryhma")
@Cacheable(true)
public class TekstiRyhma extends BaseEntity {
  @OneToMany(
      fetch = FetchType.LAZY,
      mappedBy = "ryhma",
      cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
  private Set<LokalisoituTeksti> tekstit = new HashSet<LokalisoituTeksti>();

  public Set<LokalisoituTeksti> getTekstit() {
    return tekstit;
  }

  public void setTekstit(Set<LokalisoituTeksti> tekstit) {
    this.tekstit = tekstit;
  }
}
