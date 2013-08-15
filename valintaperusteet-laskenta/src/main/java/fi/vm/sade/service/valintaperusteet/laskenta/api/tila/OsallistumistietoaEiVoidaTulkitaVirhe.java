package fi.vm.sade.service.valintaperusteet.laskenta.api.tila;

/**
 * User: wuoti
 * Date: 14.8.2013
 * Time: 15.11
 */
public class OsallistumistietoaEiVoidaTulkitaVirhe extends ValintaperusteVirheellinen {
    public OsallistumistietoaEiVoidaTulkitaVirhe(String valintaperustetunniste) {
        super(VirheMetatietotyyppi.OSALLISTUSMISTIETOA_EI_VOIDA_TULKITA, valintaperustetunniste);
    }
}
