package fi.vm.sade.service.valintaperusteet.laskenta.api.tila;

public class HakukohteenValintaperusteMaarittelemattaVirhe extends VirheMetatieto {
  public HakukohteenValintaperusteMaarittelemattaVirhe(String valintaperustetunniste) {
    super(VirheMetatietotyyppi.HAKUKOHTEEN_VALINTAPERUSTE_MAARITTELEMATTA_VIRHE);
    this.valintaperustetunniste = valintaperustetunniste;
  }

  public HakukohteenValintaperusteMaarittelemattaVirhe() {
    super(VirheMetatietotyyppi.HAKUKOHTEEN_VALINTAPERUSTE_MAARITTELEMATTA_VIRHE);
  }

  private String valintaperustetunniste;

  public String getValintaperustetunniste() {
    return valintaperustetunniste;
  }
}
