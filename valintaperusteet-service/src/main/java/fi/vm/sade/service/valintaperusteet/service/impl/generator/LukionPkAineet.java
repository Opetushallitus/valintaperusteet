package fi.vm.sade.service.valintaperusteet.service.impl.generator;

import fi.vm.sade.service.valintaperusteet.dto.model.Valintaperustelahde;
import fi.vm.sade.service.valintaperusteet.model.Funktiokutsu;
import fi.vm.sade.service.valintaperusteet.model.Laskentakaava;

import java.util.Map;

/**
 * User: wuoti Date: 31.5.2013 Time: 12.04
 */
public class LukionPkAineet extends Aineet {
    public static final String PK_etuliite = "PK_";
    public static final String PK_kymmpiluokka = "_10";
    public static final String PK_kuvausjalkiliite_ilman_valinnaisia = ", PK päättötodistus";

    public static final String kotitalous = "KO";
    public static final String kasityo = "KS";

    public static final String PK_OPPIAINE_TEMPLATE = PK_etuliite + "%s_OPPIAINE";

    private enum PkAine {
        KO(kotitalous, "Kotitalous"), KS(kasityo, "Käsityö");

        PkAine(String tunniste, String kuvaus) {
            this.tunniste = tunniste;
            this.kuvaus = kuvaus;
        }

        String tunniste;
        String kuvaus;
    }

    public LukionPkAineet() {
        for (PkAine aine : PkAine.values()) {
            getAineet().put(aine.tunniste, aine.kuvaus);
        }

        for (Map.Entry<String, String> aine : getAineet().entrySet()) {
            String ainetunniste = aine.getKey();
            String ainekuvaus = aine.getValue();

            getKaavat().put(ainetunniste, luoPKAine(ainetunniste, ainekuvaus));
        }
    }

    public static String pakollinen(String ainetunniste) {
        return PK_etuliite + ainetunniste;
    }

    public static String kymppi(String ainetunniste) {
        return PK_etuliite + ainetunniste + PK_kymmpiluokka;
    }

    private Laskentakaava luoPKAine(String ainetunniste, String kuvaus) {
        Funktiokutsu aine = GenericHelper.luoHaeLukuarvo(GenericHelper.luoValintaperusteViite(pakollinen(ainetunniste),
                false, Valintaperustelahde.HAETTAVA_ARVO));

        Funktiokutsu aine_kymppiluokka = GenericHelper.luoHaeLukuarvo(GenericHelper.luoValintaperusteViite(kymppi(ainetunniste),
                false, Valintaperustelahde.HAETTAVA_ARVO));

        Funktiokutsu max = GenericHelper.luoMaksimi(aine, aine_kymppiluokka);

        Laskentakaava laskentakaava = GenericHelper.luoLaskentakaavaJaNimettyFunktio(max, kuvaus
                + PK_kuvausjalkiliite_ilman_valinnaisia);
        return laskentakaava;
    }

    public static String oppiaine(String ainetunniste) {
        return String.format(PK_OPPIAINE_TEMPLATE, ainetunniste);
    }
}
