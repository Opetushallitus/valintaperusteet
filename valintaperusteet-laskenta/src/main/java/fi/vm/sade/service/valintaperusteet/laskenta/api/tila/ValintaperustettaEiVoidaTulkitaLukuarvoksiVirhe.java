package fi.vm.sade.service.valintaperusteet.laskenta.api.tila;

public class ValintaperustettaEiVoidaTulkitaLukuarvoksiVirhe extends ValintaperusteVirheellinen {
  public ValintaperustettaEiVoidaTulkitaLukuarvoksiVirhe(String valintaperustetunniste) {
    super(
        VirheMetatietotyyppi.VALINTAPERUSTETTA_EI_VOIDA_TULKITA_LUKUARVOKSI,
        valintaperustetunniste);
  }

  public ValintaperustettaEiVoidaTulkitaLukuarvoksiVirhe() {
    super(VirheMetatietotyyppi.VALINTAPERUSTETTA_EI_VOIDA_TULKITA_LUKUARVOKSI, "");
  }
}
