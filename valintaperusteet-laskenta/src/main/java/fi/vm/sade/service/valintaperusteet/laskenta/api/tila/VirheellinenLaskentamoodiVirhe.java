package fi.vm.sade.service.valintaperusteet.laskenta.api.tila;

public class VirheellinenLaskentamoodiVirhe extends VirheMetatieto {
  private String funktio;
  private String laskentamoodi;

  public VirheellinenLaskentamoodiVirhe(String funktio, String laskentamoodi) {
    super(VirheMetatietotyyppi.VIRHEELLINEN_LASKENTAMOODI);

    this.funktio = funktio;
    this.laskentamoodi = laskentamoodi;
  }

  public VirheellinenLaskentamoodiVirhe() {
    super(VirheMetatietotyyppi.VIRHEELLINEN_LASKENTAMOODI);
  }

  public String getFunktio() {
    return funktio;
  }

  public String getLaskentamoodi() {
    return laskentamoodi;
  }
}
