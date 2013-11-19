package fi.vm.sade.service.valintaperusteet.service.impl.lukio;

import fi.vm.sade.service.valintaperusteet.model.Funktiokutsu;
import fi.vm.sade.service.valintaperusteet.model.Laskentakaava;
import fi.vm.sade.service.valintaperusteet.model.Valintaperustelahde;
import fi.vm.sade.service.valintaperusteet.service.impl.generator.GenericHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * User: wuoti
 * Date: 30.9.2013
 * Time: 14.55
 */
public class LukionValintaperusteet {
    public static final String PAINOKERROIN_POSTFIX = "-painokerroin";

    public static final String AIDINKIELI_JA_KIRJALLISUUS1 = "AI";
    public static final String AIDINKIELI_JA_KIRJALLISUUS2 = "AI2";

    // A1-kieliä voi olla kolme
    public static final String A11KIELI = "A1";
    public static final String A12KIELI = "A12";
    public static final String A13KIELI = "A13";

    // A2-kieliä voi olla kolme
    public static final String A21KIELI = "A2";
    public static final String A22KIELI = "A22";
    public static final String A23KIELI = "A23";

    // B1-kieliä voi olla yksi
    public static final String B1KIELI = "B1";

    // B2-kieliä voi olla kolme
    public static final String B21KIELI = "B2";
    public static final String B22KIELI = "B22";
    public static final String B23KIELI = "B23";

    // B3-kieliä voi olla kolme
    public static final String B31KIELI = "B3";
    public static final String B32KIELI = "B32";
    public static final String B33KIELI = "B33";

    public static final String USKONTO = "KT";
    public static final String HISTORIA = "HI";
    public static final String YHTEISKUNTAOPPI = "YH";
    public static final String MATEMATIIKKA = "MA";
    public static final String FYSIIKKA = "FY";
    public static final String KEMIA = "KE";
    public static final String BIOLOGIA = "BI";
    public static final String TERVEYSTIETO = "TE";
    public static final String MAANTIETO = "GE";

    public static final String LIIKUNTA = "LI";
    public static final String KASITYO = "KS";
    public static final String KOTITALOUS = "KO";
    public static final String MUSIIKKI = "MU";
    public static final String KUVATAIDE = "KU";

    public static final String[] LUKUAINEET = {
            AIDINKIELI_JA_KIRJALLISUUS1,
            AIDINKIELI_JA_KIRJALLISUUS2,
            A11KIELI,
            A12KIELI,
            A13KIELI,
            A21KIELI,
            A22KIELI,
            A23KIELI,
            B1KIELI,
            B21KIELI,
            B22KIELI,
            B23KIELI,
            B31KIELI,
            B32KIELI,
            B33KIELI,
            USKONTO,
            HISTORIA,
            YHTEISKUNTAOPPI,
            MATEMATIIKKA,
            FYSIIKKA,
            KEMIA,
            BIOLOGIA,
            TERVEYSTIETO,
            MAANTIETO
    };

    public static final String[] TAITO_JA_TAIDEAINEET = {
            LIIKUNTA,
            KASITYO,
            KOTITALOUS,
            MUSIIKKI,
            KUVATAIDE
    };

    public static Laskentakaava painotettuLukuaineidenKeskiarvoJaPaasykoe(Laskentakaava ka, Laskentakaava paasykoe) {

        Funktiokutsu summa = GenericHelper.luoSumma(ka, paasykoe);

        return GenericHelper.luoLaskentakaavaJaNimettyFunktio(
                summa,
                "Lukion valintaperusteet, painotettu keskiarvo ja pääsykoe");

    }

    public static Laskentakaava painotettuLukuaineidenKeskiarvoJaLisanaytto(Laskentakaava ka, Laskentakaava lisanaytto) {
        Funktiokutsu summa = GenericHelper.luoSumma(ka, lisanaytto);

        return GenericHelper.luoLaskentakaavaJaNimettyFunktio(
                summa,
                "Lukion valintaperusteet, painotettu keskiarvo ja lisänäyttö");

    }

    public static Laskentakaava painotettuLukuaineidenKeskiarvoJaPaasykoeJaLisanaytto(Laskentakaava ka, Laskentakaava paasykoeJaLisanaytto) {

        Funktiokutsu summa = GenericHelper.luoSumma(ka, paasykoeJaLisanaytto);

        return GenericHelper.luoLaskentakaavaJaNimettyFunktio(
                summa,
                "Lukion valintaperusteet, painotettu keskiarvo, pääsykoe ja lisänäyttö");

    }


    public static Laskentakaava painotettuLukuaineidenKeskiarvo() {

        String minimi = "{{hakukohde.painotettuKeskiarvoHylkaysMin}}";
        String maksimi = "{{hakukohde.painotettuKeskiarvoHylkaysMax}}";

        List<GenericHelper.Painotus> painotukset = new ArrayList<GenericHelper.Painotus>();
        for (String aine : LUKUAINEET) {
            Funktiokutsu arvo = GenericHelper.luoHaeLukuarvo(GenericHelper.luoValintaperusteViite(aine, false, Valintaperustelahde.HAETTAVA_ARVO));
            Funktiokutsu painokerroin = GenericHelper.luoHaeLukuarvo(GenericHelper.luoValintaperusteViite(aine + PAINOKERROIN_POSTFIX, false, Valintaperustelahde.HAKUKOHTEEN_ARVO), 1.0);

            painotukset.add(new GenericHelper.Painotus(painokerroin, arvo));
        }

        for (String aine : TAITO_JA_TAIDEAINEET) {
            Funktiokutsu arvo = GenericHelper.luoHaeLukuarvo(GenericHelper.luoValintaperusteViite(aine, false, Valintaperustelahde.HAETTAVA_ARVO));
            Funktiokutsu painokerroin = GenericHelper.luoHaeLukuarvo(GenericHelper.luoValintaperusteViite(aine + PAINOKERROIN_POSTFIX, false, Valintaperustelahde.HAKUKOHTEEN_ARVO));

            painotukset.add(new GenericHelper.Painotus(painokerroin, arvo));
        }

        Funktiokutsu painotuksetFunktio = GenericHelper.luoPainotettuKeskiarvo(painotukset.toArray(new GenericHelper.Painotus[painotukset.size()]));

        return GenericHelper.luoLaskentakaavaJaNimettyFunktio(
                GenericHelper.luoHylkaaArvovalilla(painotuksetFunktio, "Painotettu keskiarvo hylätty", minimi, maksimi),
                "Lukion valintaperusteet, painotettu keskiarvo");
    }

    public static Laskentakaava paasykoeJaLisanaytto(Laskentakaava paasykoe, Laskentakaava lisanaytto) {

        Funktiokutsu summa = GenericHelper.luoSumma(paasykoe, lisanaytto);

        String minimi = "{{hakukohde.paasykoeJaLisanayttoHylkaysMin}}";
        String maksimi = "{{hakukohde.paasykoeJaLisanayttoHylkaysMax}}";

        return GenericHelper.luoLaskentakaavaJaNimettyFunktio(
                GenericHelper.luoHylkaaArvovalilla(summa, "Pääsykokeen ja lisänäytön summa ei ole tarpeeksi suuri", minimi, maksimi),
                "Lukion valintaperusteet, pääsykoe ja lisänäyttö");

    }

    public static Laskentakaava paasykoeLukuarvo(String paasykoeTunniste) {

        String minimi = "{{hakukohde.paasykoeHylkaysMin}}";
        String maksimi = "{{hakukohde.paasykoeHylkaysMax}}";

        Funktiokutsu funktiokutsu = GenericHelper.luoHaeLukuarvo(GenericHelper.luoValintaperusteViite(paasykoeTunniste, true, Valintaperustelahde.HAKUKOHTEEN_SYOTETTAVA_ARVO, "", true));

        return GenericHelper.luoLaskentakaavaJaNimettyFunktio(
                GenericHelper.luoHylkaaArvovalilla(funktiokutsu, "Pääsykoetulos hylätty", minimi, maksimi),
                "Lukion valintaperusteet, pääsykoe");

    }

    public static Laskentakaava lisanayttoLukuarvo(String lisanayttoTunniste) {

        String minimi = "{{hakukohde.lisanayttoHylkaysMin}}";
        String maksimi = "{{hakukohde.lisanayttoHylkaysMax}}";

        Funktiokutsu funktiokutsu = GenericHelper.luoHaeLukuarvo(GenericHelper.luoValintaperusteViite(lisanayttoTunniste, true, Valintaperustelahde.HAKUKOHTEEN_SYOTETTAVA_ARVO, "", true));

        return GenericHelper.luoLaskentakaavaJaNimettyFunktio(
                GenericHelper.luoHylkaaArvovalilla(funktiokutsu, "Pääsykoetulos hylätty", minimi, maksimi),
                "Lukion valintaperusteet, lisänäyttö");

    }

}
