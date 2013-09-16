package fi.vm.sade.service.valintaperusteet.laskenta.api.tila;

/**
 * User: wuoti
 * Date: 14.8.2013
 * Time: 15.03
 */
public abstract class VirheMetatieto {

    public static enum VirheMetatietotyyppi {
        OSALLISTUSMISTIETOA_EI_VOIDA_TULKITA(OsallistumistietoaEiVoidaTulkitaVirhe.class),
        VALINTAPERUSTETTA_EI_VOIDA_TULKITA_TOTUUSARVOKSI(ValintaperustettaEiVoidaTulkitaTotuusarvoksiVirhe.class),
        VALINTAPERUSTETTA_EI_VOIDA_TULKITA_LUKUARVOKSI(ValintaperustettaEiVoidaTulkitaLukuarvoksiVirhe.class),
        ARVOKONVERTOINTI_VIRHE(ArvokonvertointiVirhe.class),
        ARVOVALIKONVERTOINTI_VIRHE(ArvovalikonvertointiVirhe.class),
        JAKO_NOLLALLA(JakoNollallaVirhe.class),
        HYLKAAMISTA_EI_VOIDA_TULKITA(HylkaamistaEiVoidaTulkita.class),
        SYOTETTAVA_ARVO_MERKITSEMATTA(SyotettavaArvoMerkitsemattaVirhe.class),
        HAKUKOHTEEN_VALINTAPERUSTE_MAARITTELEMATTA_VIRHE(HakukohteenValintaperusteMaarittelemattaVirhe.class);

        VirheMetatietotyyppi(Class<? extends VirheMetatieto> tyyppi) {
            this.tyyppi = tyyppi;
        }

        private Class<? extends VirheMetatieto> tyyppi;
    }

    public VirheMetatieto(VirheMetatietotyyppi metatietotyyppi) {
        this.metatietotyyppi = metatietotyyppi;
    }

    private VirheMetatietotyyppi metatietotyyppi;

    public VirheMetatietotyyppi getMetatietotyyppi() {
        return metatietotyyppi;
    }
}
