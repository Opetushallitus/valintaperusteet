package fi.vm.sade.service.valintaperusteet.service.impl.lukio;

import fi.vm.sade.service.valintaperusteet.model.*;
import fi.vm.sade.service.valintaperusteet.service.impl.generator.GenericHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * User: wuoti
 * Date: 30.9.2013
 * Time: 14.55
 */
public class LukionValintaperusteet {
    public static final String PAINOKERROIN_POSTFIX = "_painokerroin";
    public static final String AINE_PREFIX = "PK_";

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

    public static final String SAKSA = "DE";
    public static final String KREIKKA = "EL";
    public static final String ENGLANTI = "EN";
    public static final String ESPANJA = "ES";
    public static final String EESTI = "ET";
    public static final String SUOMI = "FI";
    public static final String RANSKA = "FR";
    public static final String ITALIA = "IT";
    public static final String JAPANI = "JA";
    public static final String LATINA = "LA";
    public static final String LIETTUA = "LT";
    public static final String LATVIA = "LV";
    public static final String PORTUGALI = "PT";
    public static final String VENAJA = "RU";
    public static final String SAAME = "SE";
    public static final String RUOTSI = "SV";
    public static final String VIITTOMAKIELI = "VK";
    public static final String KIINA = "ZH";


    public static final String[] LUKUAINEET = {
            AIDINKIELI_JA_KIRJALLISUUS1,
            AIDINKIELI_JA_KIRJALLISUUS2,
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

    public static final String[] KIELET = {
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
            B33KIELI
    };

    public static final String[] KIELIKOODIT = {
            SAKSA,
            KREIKKA,
            ENGLANTI,
            ESPANJA,
            EESTI,
            SUOMI,
            RANSKA,
            ITALIA,
            JAPANI,
            LATINA,
            LIETTUA,
            LATVIA,
            PORTUGALI,
            VENAJA,
            SAAME,
            RUOTSI,
            VIITTOMAKIELI,
            KIINA
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

        String minimi = "{{hakukohde.painotettu_keskiarvo_hylkays_min}}";
        String maksimi = "{{hakukohde.painotettu_keskiarvo_hylkays_max}}";

        List<GenericHelper.Painotus> painotukset = new ArrayList<GenericHelper.Painotus>();
        for (String aine : LUKUAINEET) {
            Funktiokutsu arvo = GenericHelper.luoHaeLukuarvo(GenericHelper.luoValintaperusteViite(AINE_PREFIX+aine, false, Valintaperustelahde.HAETTAVA_ARVO));
            Funktiokutsu painokerroin = GenericHelper.luoHaeLukuarvo(GenericHelper.luoValintaperusteViite(aine + PAINOKERROIN_POSTFIX, false, Valintaperustelahde.HAKUKOHTEEN_ARVO), 1.0);

            painotukset.add(new GenericHelper.Painotus(painokerroin, arvo));
        }

        for (String aine : KIELET) {
            for(String koodi : KIELIKOODIT) {
                String avain = "{{"+AINE_PREFIX+aine+"_OPPIAINE."+koodi+"}}";
                ValintaperusteViite vp = GenericHelper.luoValintaperusteViite(avain, false, Valintaperustelahde.HAETTAVA_ARVO);
                Funktiokutsu arvo = GenericHelper.luoHaeLukuarvoEhdolla(GenericHelper.luoValintaperusteViite(AINE_PREFIX+aine, false, Valintaperustelahde.HAETTAVA_ARVO),vp);
                Funktiokutsu painokerroin = GenericHelper.luoHaeLukuarvo(GenericHelper.luoValintaperusteViite(aine + "_" + koodi + PAINOKERROIN_POSTFIX, false, Valintaperustelahde.HAKUKOHTEEN_ARVO), 1.0);

                painotukset.add(new GenericHelper.Painotus(painokerroin, arvo));
            }

        }

        for (String aine : TAITO_JA_TAIDEAINEET) {
            Funktiokutsu arvo = GenericHelper.luoHaeLukuarvo(GenericHelper.luoValintaperusteViite(AINE_PREFIX+aine, false, Valintaperustelahde.HAETTAVA_ARVO));
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

        String minimi = "{{hakukohde.paasykoe_ja_lisanaytto_hylkays_min}}";
        String maksimi = "{{hakukohde.paasykoe_ja_lisanaytto_hylkays_max}}";

        return GenericHelper.luoLaskentakaavaJaNimettyFunktio(
                GenericHelper.luoHylkaaArvovalilla(summa, "Pääsykokeen ja lisänäytön summa ei ole tarpeeksi suuri", minimi, maksimi),
                "Lukion valintaperusteet, pääsykoe ja lisänäyttö");

    }

    public static Laskentakaava paasykoeLukuarvo(String paasykoeTunniste) {

        String minimi = "{{hakukohde.paasykoe_hylkays_min}}";
        String maksimi = "{{hakukohde.paasykoe_hylkays_max}}";
        String alaraja = "{{hakukohde.paasykoe_min}}";
        String ylaraja = "{{hakukohde.paasykoe_max}}";

        List<Arvovalikonvertteriparametri> konvs = new ArrayList<Arvovalikonvertteriparametri>();
        konvs.add(GenericHelper.luoArvovalikonvertteriparametri(alaraja, ylaraja));

        Funktiokutsu funktiokutsu = GenericHelper.luoHaeLukuarvo(GenericHelper.luoValintaperusteViite(paasykoeTunniste, true, Valintaperustelahde.HAKUKOHTEEN_SYOTETTAVA_ARVO, "", true), konvs);

        return GenericHelper.luoLaskentakaavaJaNimettyFunktio(
                GenericHelper.luoHylkaaArvovalilla(funktiokutsu, "Pääsykoetulos hylätty", minimi, maksimi),
                "Lukion valintaperusteet, pääsykoe");

    }

    public static Laskentakaava lisanayttoLukuarvo(String lisanayttoTunniste) {

        String minimi = "{{hakukohde.lisanaytto_hylkays_min}}";
        String maksimi = "{{hakukohde.lisanaytto_hylkays_max}}";
        String alaraja = "{{hakukohde.lisanaytto_min}}";
        String ylaraja = "{{hakukohde.lisanaytto_max}}";

        List<Arvovalikonvertteriparametri> konvs = new ArrayList<Arvovalikonvertteriparametri>();
        konvs.add(GenericHelper.luoArvovalikonvertteriparametri(alaraja, ylaraja));

        Funktiokutsu funktiokutsu = GenericHelper.luoHaeLukuarvo(GenericHelper.luoValintaperusteViite(lisanayttoTunniste, true, Valintaperustelahde.HAKUKOHTEEN_SYOTETTAVA_ARVO, "", true), konvs);

        return GenericHelper.luoLaskentakaavaJaNimettyFunktio(
                GenericHelper.luoHylkaaArvovalilla(funktiokutsu, "Pääsykoetulos hylätty", minimi, maksimi),
                "Lukion valintaperusteet, lisänäyttö");

    }

}
