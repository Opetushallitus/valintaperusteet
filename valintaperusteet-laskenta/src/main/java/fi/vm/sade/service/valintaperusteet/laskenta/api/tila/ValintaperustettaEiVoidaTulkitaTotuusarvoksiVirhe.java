package fi.vm.sade.service.valintaperusteet.laskenta.api.tila;

/**
 * User: wuoti
 * Date: 14.8.2013
 * Time: 15.12
 */
public class ValintaperustettaEiVoidaTulkitaTotuusarvoksiVirhe extends ValintaperusteVirheellinen {
    public ValintaperustettaEiVoidaTulkitaTotuusarvoksiVirhe(String valintaperustetunniste) {
        super(VirheMetatietotyyppi.VALINTAPERUSTETTA_EI_VOIDA_TULKITA_TOTUUSARVOKSI, valintaperustetunniste);
    }

    public ValintaperustettaEiVoidaTulkitaTotuusarvoksiVirhe() {
        super(VirheMetatietotyyppi.VALINTAPERUSTETTA_EI_VOIDA_TULKITA_TOTUUSARVOKSI, "");
    }
}
