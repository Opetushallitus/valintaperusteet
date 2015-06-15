package fi.vm.sade.service.valintaperusteet.laskenta.api.tila;

import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = OsallistumistietoaEiVoidaTulkitaVirhe.class, name = "OsallistumistietoaEiVoidaTulkitaVirhe"),
        @JsonSubTypes.Type(value = ValintaperustettaEiVoidaTulkitaTotuusarvoksiVirhe.class, name = "ValintaperustettaEiVoidaTulkitaTotuusarvoksiVirhe"),
        @JsonSubTypes.Type(value = ValintaperustettaEiVoidaTulkitaLukuarvoksiVirhe.class, name = "ValintaperustettaEiVoidaTulkitaLukuarvoksiVirhe"),
        @JsonSubTypes.Type(value = ArvokonvertointiVirhe.class, name = "ArvokonvertointiVirhe"),
        @JsonSubTypes.Type(value = ArvovalikonvertointiVirhe.class, name = "ArvovalikonvertointiVirhe"),
        @JsonSubTypes.Type(value = JakoNollallaVirhe.class, name = "JakoNollallaVirhe"),
        @JsonSubTypes.Type(value = HylkaamistaEiVoidaTulkita.class, name = "HylkaamistaEiVoidaTulkita"),
        @JsonSubTypes.Type(value = SyotettavaArvoMerkitsemattaVirhe.class, name = "SyotettavaArvoMerkitsemattaVirhe"),
        @JsonSubTypes.Type(value = HakukohteenValintaperusteMaarittelemattaVirhe.class, name = "HakukohteenValintaperusteMaarittelemattaVirhe"),
        @JsonSubTypes.Type(value = VirheellinenLaskentamoodiVirhe.class, name = "VirheellinenLaskentamoodiVirhe"),
        @JsonSubTypes.Type(value = SkaalattavaArvoEiOleLahdeskaalassaVirhe.class, name = "SkaalattavaArvoEiOleLahdeskaalassaVirhe"),
        @JsonSubTypes.Type(value = TuloksiaLiianVahanLahdeskaalanMaarittamiseenVirhe.class, name = "TuloksiaLiianVahanLahdeskaalanMaarittamiseenVirhe")
})

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
        HAKUKOHTEEN_VALINTAPERUSTE_MAARITTELEMATTA_VIRHE(HakukohteenValintaperusteMaarittelemattaVirhe.class),
        VIRHEELLINEN_LASKENTAMOODI(VirheellinenLaskentamoodiVirhe.class),
        SKAALATTAVA_ARVO_EI_OLE_LAHDESKAALASSA(SkaalattavaArvoEiOleLahdeskaalassaVirhe.class),
        TULOKSIA_LIIAN_VAHAN_LAHDESKAALAN_MAARITTAMISEEN(TuloksiaLiianVahanLahdeskaalanMaarittamiseenVirhe.class);

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
