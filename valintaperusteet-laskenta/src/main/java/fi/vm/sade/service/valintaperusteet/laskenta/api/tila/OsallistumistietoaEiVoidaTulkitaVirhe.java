package fi.vm.sade.service.valintaperusteet.laskenta.api.tila;

public class OsallistumistietoaEiVoidaTulkitaVirhe extends ValintaperusteVirheellinen {
  public OsallistumistietoaEiVoidaTulkitaVirhe(String valintaperustetunniste) {
    super(VirheMetatietotyyppi.OSALLISTUSMISTIETOA_EI_VOIDA_TULKITA, valintaperustetunniste);
  }

  public OsallistumistietoaEiVoidaTulkitaVirhe() {
    super(VirheMetatietotyyppi.OSALLISTUSMISTIETOA_EI_VOIDA_TULKITA, "");
  }
}
