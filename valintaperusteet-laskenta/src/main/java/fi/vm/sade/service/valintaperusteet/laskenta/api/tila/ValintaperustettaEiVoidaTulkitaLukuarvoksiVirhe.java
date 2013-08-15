package fi.vm.sade.service.valintaperusteet.laskenta.api.tila;

/**
 * User: wuoti
 * Date: 14.8.2013
 * Time: 15.12
 */
public class ValintaperustettaEiVoidaTulkitaLukuarvoksiVirhe extends ValintaperusteVirheellinen {
    public ValintaperustettaEiVoidaTulkitaLukuarvoksiVirhe(String valintaperustetunniste) {
        super(VirheMetatietotyyppi.VALINTAPERUSTETTA_EI_VOIDA_TULKITA_LUKUARVOKSI, valintaperustetunniste);
    }
}
