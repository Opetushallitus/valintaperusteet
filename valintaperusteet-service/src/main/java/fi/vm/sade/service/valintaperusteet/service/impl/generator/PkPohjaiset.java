package fi.vm.sade.service.valintaperusteet.service.impl.generator;

import fi.vm.sade.service.valintaperusteet.model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: kkammone
 * Date: 4.3.2013
 * Time: 14:26
 * To change this template use File | Settings | File Templates.
 */
public class PkPohjaiset {

    private PkPohjaiset() {
    }

    // Lisäpistekoulutus
    public static final String kymppiluokka = "LISAKOULUTUS_KYMPPI";
    public static final String vammaistenValmentavaJaKuntouttavaOpetusJaOhjaus = "LISAKOULUTUS_VAMMAISTEN";
    public static final String maahanmuuttajienAmmatilliseenPeruskoulutukseenValmistavaKoulutus = "LISAKOULUTUS_MAAHANMUUTTO";
    public static final String talouskoulu = "LISAKOULUTUS_TALOUS";
    public static final String ammattistartti = "LISAKOULUTUS_AMMATTISTARTTI";
    public static final String kansanopistonLukuvuodenMittainenLinjaAmmatilliseenPeruskoulutukseen = "LISAKOULUTUS_KANSANOPISTO";
    public static final String koulutuspaikkaAmmatilliseenTutkintoon = "KOULUTUSPAIKKA_AMMATILLISEEN_TUTKINTOON";


    public static final String[] lisapistekoulutus = {
            kymppiluokka, vammaistenValmentavaJaKuntouttavaOpetusJaOhjaus,
            maahanmuuttajienAmmatilliseenPeruskoulutukseenValmistavaKoulutus, talouskoulu, ammattistartti,
            kansanopistonLukuvuodenMittainenLinjaAmmatilliseenPeruskoulutukseen};

    // Pohjakoulutus
    public static final String pohjakoulutusAvain = "POHJAKOULUTUS";


    public static final String ulkomaillaSuoritettuKoulutus = "0";
    public static final String perusopetuksenOppimaara = "1";
    public static final String perusopetuksenErityisopetuksenOsittainYksilollistettyOppimaara = "2";
    public static final String perusopetuksenErityisopetuksenYksilollistettyOppimaaraOpetusJarjestettyToimintaalueittain = "3";
    public static final String perusopetuksenPaaosinTaiKokonaanYksilollistettyOppimaara = "6";
    public static final String oppivelvollisuudenSuorittaminenKeskeytynyt = "7";
    public static final String lukionPaattotodistus = "9";

    public static final String[] pohjakoulutusPeruskoulutus = {
            perusopetuksenOppimaara,
            perusopetuksenErityisopetuksenOsittainYksilollistettyOppimaara,
            perusopetuksenErityisopetuksenYksilollistettyOppimaaraOpetusJarjestettyToimintaalueittain,
            perusopetuksenPaaosinTaiKokonaanYksilollistettyOppimaara};

    public static final String[] pohjakoulutusMuut = {
            oppivelvollisuudenSuorittaminenKeskeytynyt, lukionPaattotodistus, ulkomaillaSuoritettuKoulutus
    };

    // Onko todistuksen saantivuosi sama kuin hakuvuosi
    public static final String todistuksenSaantivuosi = "PK_PAATTOTODISTUSVUOSI";

    public static final int kuluvaVuosi = 2013;

    public static Laskentakaava luoPohjakoulutuspisteytysmalli() {

        // Pohjakoulutus -->
        List<Arvokonvertteriparametri> konvs = new ArrayList<Arvokonvertteriparametri>();
        for (String arvo : pohjakoulutusPeruskoulutus) {
            konvs.add(GenericHelper.luoArvokonvertteriparametri(arvo, Boolean.TRUE.toString(), false));
        }

        for (String arvo : pohjakoulutusMuut) {
            konvs.add(GenericHelper.luoArvokonvertteriparametri(arvo, Boolean.FALSE.toString(), false));
        }

        Funktiokutsu pohjakoulutusOnPeruskoulutus = GenericHelper.luoHaeMerkkijonoJaKonvertoiTotuusarvoksi(
                GenericHelper.luoValintaperusteViite(pohjakoulutusAvain, true, false, Valintaperustelahde.HAETTAVA_ARVO)
                , konvs);
        // <---

        // Todistuksen saantivuosi -->
        Funktiokutsu haeTodistuksenSaantivuosi = GenericHelper.luoHaeLukuarvo(GenericHelper.luoValintaperusteViite(
                todistuksenSaantivuosi, true, false, Valintaperustelahde.HAETTAVA_ARVO));

        Funktiokutsu todistuksenSaantivuosiOnSamaKuinKuluvavuosi = GenericHelper.luoYhtasuuri(
                GenericHelper.luoLukuarvo(kuluvaVuosi), haeTodistuksenSaantivuosi);
        // <---

        Funktiokutsu ja = GenericHelper.luoJa(pohjakoulutusOnPeruskoulutus,
                todistuksenSaantivuosiOnSamaKuinKuluvavuosi);

        List<Funktiokutsu> args = new ArrayList<Funktiokutsu>();
        args.add(ja);
        // Lisäpistekoulutus -->
        for (String tunniste : lisapistekoulutus) {
            args.add(GenericHelper.luoHaeTotuusarvo(GenericHelper.luoValintaperusteViite(
                    tunniste, false, false, Valintaperustelahde.HAETTAVA_ARVO), false));
        }
        // <---

        Funktiokutsu tai = GenericHelper.luoTai(args.toArray(new FunktionArgumentti[args.size()]));

        Funktiokutsu thenHaara = GenericHelper.luoLukuarvo(6.0);
        Funktiokutsu elseHaara = GenericHelper.luoLukuarvo(0.0);

        Funktiokutsu jos = GenericHelper.luoJosFunktio(tai, thenHaara, elseHaara);

        return GenericHelper.luoLaskentakaavaJaNimettyFunktio(jos, "Pohjakoulutuspisteytys, 2 aste, pk");
    }

    public static Laskentakaava luoToisenAsteenPeruskoulupohjainenPeruskaava(
            Laskentakaava painotettavatKeskiarvotLaskentakaava, Laskentakaava yleinenkoulumenestyspisteytysmalli,
            Laskentakaava pohjakoulutuspisteytysmalli, Laskentakaava ilmanKoulutuspaikkaaPisteytysmalli,
            Laskentakaava hakutoivejarjestyspisteytysmalli, Laskentakaava tyokokemuspisteytysmalli,
            Laskentakaava sukupuolipisteytysmalli) {

        Funktiokutsu summa = GenericHelper.luoSumma(painotettavatKeskiarvotLaskentakaava,
                yleinenkoulumenestyspisteytysmalli, pohjakoulutuspisteytysmalli, ilmanKoulutuspaikkaaPisteytysmalli,
                hakutoivejarjestyspisteytysmalli, tyokokemuspisteytysmalli, sukupuolipisteytysmalli);

        return GenericHelper.luoLaskentakaavaJaNimettyFunktio(summa,
                "2. asteen peruskoulupohjainen peruskaava");
    }

    public static Laskentakaava ilmanKoulutuspaikkaaPisteytysmalli() {
        Funktiokutsu thenHaara = GenericHelper.luoLukuarvo(8.0);
        Funktiokutsu elseHaara = GenericHelper.luoLukuarvo(0.0);
        Funktiokutsu ehto = GenericHelper.luoEi(GenericHelper.luoHaeTotuusarvo(
                GenericHelper.luoValintaperusteViite(koulutuspaikkaAmmatilliseenTutkintoon, false, false,
                        Valintaperustelahde.HAETTAVA_ARVO)));

        Funktiokutsu jos = GenericHelper.luoJosFunktio(ehto, thenHaara, elseHaara);

        return GenericHelper.luoLaskentakaavaJaNimettyFunktio(
                jos, "Ilman koulutuspaikkaa -pisteytys, 2 aste, pk");
    }
}
