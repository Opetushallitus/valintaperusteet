package fi.vm.sade.service.valintaperusteet.dto.model;

public class ValidointivirheDTO {

  private Virhetyyppi virhetyyppi;

  private String virheviesti;

  public ValidointivirheDTO() { }

  public ValidointivirheDTO(Virhetyyppi virhetyyppi, String virheviesti) {
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
}
