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
    public static final String kymppiluokka = "suorittanut1";
    public static final String vammaistenValmentavaJaKuntouttavaOpetusJaOhjaus = "suorittanut2";
    public static final String maahanmuuttajienAmmatilliseenPeruskoulutukseenValmistavaKoulutus = "suorittanut3";
    public static final String talouskoulu = "suorittanut4";
    public static final String ammattistartti = "suorittanut15";
    public static final String kansanopistonLukuvuodenMittainenLinjaAmmatilliseenPeruskoulutukseen = "suorittanut6";

    public static final String[] lisapistekoulutus = {
            kymppiluokka, vammaistenValmentavaJaKuntouttavaOpetusJaOhjaus,
            maahanmuuttajienAmmatilliseenPeruskoulutukseenValmistavaKoulutus, talouskoulu, ammattistartti,
            kansanopistonLukuvuodenMittainenLinjaAmmatilliseenPeruskoulutukseen};

    // Pohjakoulutus
    public static final String pohjakoulutusAvain = "millatutkinnolla";

    public static final String perusopetuksenOppimaara = "tutkinto1";
    public static final String perusopetuksenErityisopetuksenOsittainYksilollistettyOppimaara = "tutkinto2";
    public static final String perusopetuksenErityisopetuksenYksilollistettyOppimaaraOpetusJarjestettyToimintaalueittain = "tutkinto3";
    public static final String perusopetuksenPaaosinTaiKokonaanYksilollistettyOppimaara = "tutkinto4";
    public static final String oppivelvollisuudenSuorittaminenKeskeytynyt = "tutkinto4";
    public static final String lukionPaattotodistus = "tutkinto5";
    public static final String ulkomaillaSuoritettuKoulutus = "tutkinto6";

    public static final String[] pohjakoulutusPeruskoulutus = {
            perusopetuksenOppimaara,
            perusopetuksenErityisopetuksenOsittainYksilollistettyOppimaara,
            perusopetuksenErityisopetuksenYksilollistettyOppimaaraOpetusJarjestettyToimintaalueittain,
            perusopetuksenPaaosinTaiKokonaanYksilollistettyOppimaara};

    public static final String[] pohjakoulutusMuut = {
            oppivelvollisuudenSuorittaminenKeskeytynyt, lukionPaattotodistus, ulkomaillaSuoritettuKoulutus
    };

    // Onko todistuksen saantivuosi sama kuin hakuvuosi
    public static final String todistuksenSaantivuosi = "paattotodistusvuosi_peruskoulu";

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
}
