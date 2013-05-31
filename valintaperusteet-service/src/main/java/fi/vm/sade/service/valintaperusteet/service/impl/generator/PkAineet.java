package fi.vm.sade.service.valintaperusteet.service.impl.generator;

import fi.vm.sade.service.valintaperusteet.model.Funktiokutsu;
import fi.vm.sade.service.valintaperusteet.model.Laskentakaava;
import fi.vm.sade.service.valintaperusteet.model.Valintaperustelahde;

import java.util.Map;

/**
 * User: wuoti
 * Date: 31.5.2013
 * Time: 12.04
 */
public class PkAineet extends Aineet {
    public static final String PK_Valinnainen1 = "_VAL1";
    public static final String PK_Valinnainen2 = "_VAL2";
    public static final String PK_etuliite = "PK_";
    public static final String PK_kuvausjalkiliite = ", PK päättötodistus, mukaanlukien valinnaiset";

    public static final String kotitalous = "KO";
    public static final String kasityo = "KS";

    private enum PkAine {
        KO(kotitalous, "Kotitalous"),
        KS(kasityo, "Käsityö");

        PkAine(String tunniste, String kuvaus) {
            this.tunniste = tunniste;
            this.kuvaus = kuvaus;
        }

        String tunniste;
        String kuvaus;
    }

    public PkAineet() {
        for (PkAine aine : PkAine.values()) {
            getAineet().put(aine.tunniste, aine.kuvaus);
        }

        for (Map.Entry<String, String> aine : getAineet().entrySet()) {
            String ainetunniste = aine.getKey();
            String ainekuvaus = aine.getValue();

            getKaavat().put(ainetunniste, luoPKAine(ainetunniste, ainekuvaus));
        }
    }

    private Laskentakaava luoPKAine(String ainetunniste, String kuvaus) {
        Funktiokutsu aine = GenericHelper.luoHaeLukuarvo(
                GenericHelper.luoValintaperusteViite(PK_etuliite + ainetunniste, false, false,
                        Valintaperustelahde.HAETTAVA_ARVO));
        Funktiokutsu aineValinnainen1 = GenericHelper.luoHaeLukuarvo(
                GenericHelper.luoValintaperusteViite(PK_etuliite + ainetunniste + PK_Valinnainen1,
                        false, false, Valintaperustelahde.HAETTAVA_ARVO));
        Funktiokutsu aineValinnainen2 = GenericHelper.luoHaeLukuarvo(
                GenericHelper.luoValintaperusteViite(PK_etuliite + ainetunniste + PK_Valinnainen2,
                        false, false, Valintaperustelahde.HAETTAVA_ARVO));

        Funktiokutsu valinnainenKeskiarvo = GenericHelper.luoKeskiarvo(aineValinnainen1, aineValinnainen2);
        Funktiokutsu keskiarvo = GenericHelper.luoKeskiarvo(aine, valinnainenKeskiarvo);

        Laskentakaava laskentakaava = GenericHelper.luoLaskentakaavaJaNimettyFunktio(keskiarvo, kuvaus + PK_kuvausjalkiliite);
        return laskentakaava;
    }
}
