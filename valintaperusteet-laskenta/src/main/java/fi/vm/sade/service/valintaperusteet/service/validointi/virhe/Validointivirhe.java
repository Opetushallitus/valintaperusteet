package fi.vm.sade.service.valintaperusteet.service.validointi.virhe;

import fi.vm.sade.service.valintaperusteet.model.Abstraktivalidointivirhe;

public class Validointivirhe extends Abstraktivalidointivirhe {
  private Virhetyyppi virhetyyppi;

  private String virheviesti;

  public Validointivirhe(Virhetyyppi virhetyyppi, String virheviesti) {
    this.virhetyyppi = virhetyyppi;
    this.virheviesti = virheviesti;
  }

  public Virhetyyppi getVirhetyyppi() {
    return virhetyyppi;
  }

  public void setVirhetyyppi(Virhetyyppi virhetyyppi) {
    this.virhetyyppi = virhetyyppi;
  }

  public String getVirheviesti() {
    return virheviesti;
  }

  public void setVirheviesti(String virheviesti) {
    this.virheviesti = virheviesti;
  }

  @Override
  public String toString() {
    return new StringBuilder().append(virhetyyppi).append(" ").append(virheviesti).toString();
  }
}
