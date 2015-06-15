package fi.vm.sade.service.valintaperusteet.laskenta.api.tila;

public class ValintaperustettaEiVoidaTulkitaTotuusarvoksiVirhe extends ValintaperusteVirheellinen {
    public ValintaperustettaEiVoidaTulkitaTotuusarvoksiVirhe(String valintaperustetunniste) {
        super(VirheMetatietotyyppi.VALINTAPERUSTETTA_EI_VOIDA_TULKITA_TOTUUSARVOKSI, valintaperustetunniste);
    }

    public ValintaperustettaEiVoidaTulkitaTotuusarvoksiVirhe() {
        super(VirheMetatietotyyppi.VALINTAPERUSTETTA_EI_VOIDA_TULKITA_TOTUUSARVOKSI, "");
    }
}
