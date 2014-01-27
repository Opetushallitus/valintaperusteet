package fi.vm.sade.service.valintaperusteet.service.impl.generator;

import java.util.ArrayList;
import java.util.List;

import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi;
import fi.vm.sade.service.valintaperusteet.dto.model.Valintaperustelahde;
import fi.vm.sade.service.valintaperusteet.model.Funktioargumentti;
import fi.vm.sade.service.valintaperusteet.model.Funktiokutsu;
import fi.vm.sade.service.valintaperusteet.model.FunktionArgumentti;
import fi.vm.sade.service.valintaperusteet.model.Laskentakaava;

/**
 * Created with IntelliJ IDEA. User: kkammone Date: 4.3.2013 Time: 14:26
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

    public static final String[] lisapistekoulutus = { kymppiluokka, vammaistenValmentavaJaKuntouttavaOpetusJaOhjaus,
            maahanmuuttajienAmmatilliseenPeruskoulutukseenValmistavaKoulutus, talouskoulu, ammattistartti,
            kansanopistonLukuvuodenMittainenLinjaAmmatilliseenPeruskoulutukseen };

    // Pohjakoulutus
    public static final String pohjakoulutusAvain = "POHJAKOULUTUS";

    public static final String ulkomaillaSuoritettuKoulutus = "0";
    public static final String perusopetuksenOppimaara = "1";
    public static final String perusopetuksenErityisopetuksenOsittainYksilollistettyOppimaara = "2";
    public static final String perusopetuksenErityisopetuksenYksilollistettyOppimaaraOpetusJarjestettyToimintaalueittain = "3";
    public static final String perusopetuksenPaaosinTaiKokonaanYksilollistettyOppimaara = "6";
    public static final String oppivelvollisuudenSuorittaminenKeskeytynyt = "7";
    public static final String lukionPaattotodistus = "9";

    public static final String[] pohjakoulutusPeruskoulutus = { perusopetuksenOppimaara,
            perusopetuksenErityisopetuksenOsittainYksilollistettyOppimaara,
            perusopetuksenErityisopetuksenYksilollistettyOppimaaraOpetusJarjestettyToimintaalueittain,
            perusopetuksenPaaosinTaiKokonaanYksilollistettyOppimaara };

    public static final String[] pohjakoulutusMuut = { oppivelvollisuudenSuorittaminenKeskeytynyt,
            lukionPaattotodistus, ulkomaillaSuoritettuKoulutus };

    // Onko todistuksen saantivuosi sama kuin hakuvuosi
    public static final String todistuksenSaantivuosi = "PK_PAATTOTODISTUSVUOSI";

    public static final int kuluvaVuosi = 2013;

    public static Laskentakaava luoPohjakoulutuspisteytysmalli() {

        // Pohjakoulutus -->

        List<Funktiokutsu> vertailut = new ArrayList<Funktiokutsu>();
        for (String arvo : pohjakoulutusPeruskoulutus) {
            vertailut.add(GenericHelper.luoHaeMerkkijonoJaVertaaYhtasuuruus(
                    GenericHelper.luoValintaperusteViite(pohjakoulutusAvain, false, Valintaperustelahde.HAETTAVA_ARVO),
                    arvo, false));
        }

        Funktiokutsu pohjakoulutusOnPeruskoulutus = GenericHelper.luoTai(vertailut
                .toArray(new FunktionArgumentti[vertailut.size()]));

        // <---

        // Todistuksen saantivuosi -->
        Funktiokutsu haeTodistuksenSaantivuosi = GenericHelper.luoHaeLukuarvo(
                GenericHelper.luoValintaperusteViite(todistuksenSaantivuosi, false, Valintaperustelahde.HAETTAVA_ARVO),
                -1);

        Funktiokutsu todistuksenSaantivuosiOnSamaKuinKuluvavuosi = GenericHelper.luoYhtasuuri(
                GenericHelper.luoLukuarvo(kuluvaVuosi), haeTodistuksenSaantivuosi);
        // <---

        Funktiokutsu ja = GenericHelper
                .luoJa(pohjakoulutusOnPeruskoulutus, todistuksenSaantivuosiOnSamaKuinKuluvavuosi);

        List<Funktiokutsu> args = new ArrayList<Funktiokutsu>();
        args.add(ja);
        // Lisäpistekoulutus -->
        for (String tunniste : lisapistekoulutus) {
            args.add(GenericHelper.luoHaeTotuusarvo(
                    GenericHelper.luoValintaperusteViite(tunniste, false, Valintaperustelahde.HAETTAVA_ARVO), false));
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
            Laskentakaava sukupuolipisteytysmalli, Laskentakaava urheilijanLisapiste) {

        Funktiokutsu summa = GenericHelper.luoSumma(painotettavatKeskiarvotLaskentakaava,
                yleinenkoulumenestyspisteytysmalli, pohjakoulutuspisteytysmalli, ilmanKoulutuspaikkaaPisteytysmalli,
                hakutoivejarjestyspisteytysmalli, tyokokemuspisteytysmalli, sukupuolipisteytysmalli,
                urheilijanLisapiste);

        return GenericHelper.luoLaskentakaavaJaNimettyFunktio(summa, "2. asteen peruskoulupohjainen peruskaava");
    }

    public static Laskentakaava ilmanKoulutuspaikkaaPisteytysmalli() {
        Funktiokutsu thenHaara = GenericHelper.luoLukuarvo(8.0);
        Funktiokutsu elseHaara = GenericHelper.luoLukuarvo(0.0);
        Funktiokutsu ehto = GenericHelper.luoEi(GenericHelper.luoHaeTotuusarvo(GenericHelper.luoValintaperusteViite(
                koulutuspaikkaAmmatilliseenTutkintoon, false, Valintaperustelahde.HAETTAVA_ARVO)));

        Funktiokutsu jos = GenericHelper.luoJosFunktio(ehto, thenHaara, elseHaara);

        return GenericHelper.luoLaskentakaavaJaNimettyFunktio(jos, "Ilman koulutuspaikkaa -pisteytys, 2 aste, pk");
    }

    public static Laskentakaava luoPainotettavatKeskiarvotLaskentakaava(PkAineet pkAineet) {

        Laskentakaava[] args = new Laskentakaava[] { pkAineet.getLaskentakaava(Aineet.kuvataide),
                pkAineet.getLaskentakaava(Aineet.musiikki), pkAineet.getLaskentakaava(PkAineet.kasityo),
                pkAineet.getLaskentakaava(PkAineet.kotitalous), pkAineet.getLaskentakaava(Aineet.liikunta) };

        Funktiokutsu kolmeParasta = GenericHelper.nParastaKeskiarvo(3, args);

        Funktiokutsu konvertteri = new Funktiokutsu();
        konvertteri.setFunktionimi(Funktionimi.KONVERTOILUKUARVO);

        Funktioargumentti funk = new Funktioargumentti();
        funk.setFunktiokutsuChild(kolmeParasta);
        funk.setIndeksi(1);

        konvertteri.getFunktioargumentit().add(funk);
        konvertteri.getArvovalikonvertteriparametrit().add(GenericHelper.luoArvovalikonvertteriparametri(0.0, 6.0, 0));
        konvertteri.getArvovalikonvertteriparametrit().add(GenericHelper.luoArvovalikonvertteriparametri(6.0, 6.5, 1));
        konvertteri.getArvovalikonvertteriparametrit().add(GenericHelper.luoArvovalikonvertteriparametri(6.5, 7.0, 2));
        konvertteri.getArvovalikonvertteriparametrit().add(GenericHelper.luoArvovalikonvertteriparametri(7.0, 7.5, 3));
        konvertteri.getArvovalikonvertteriparametrit().add(GenericHelper.luoArvovalikonvertteriparametri(7.5, 8.0, 4));
        konvertteri.getArvovalikonvertteriparametrit().add(GenericHelper.luoArvovalikonvertteriparametri(8.0, 8.5, 5));
        konvertteri.getArvovalikonvertteriparametrit().add(GenericHelper.luoArvovalikonvertteriparametri(8.5, 9.0, 6));
        konvertteri.getArvovalikonvertteriparametrit().add(GenericHelper.luoArvovalikonvertteriparametri(9.0, 9.5, 7));
        konvertteri.getArvovalikonvertteriparametrit().add(GenericHelper.luoArvovalikonvertteriparametri(9.5, 10.1, 8));

        Laskentakaava laskentakaava = GenericHelper.luoLaskentakaavaJaNimettyFunktio(konvertteri,
                "Painotettavat arvosanat pisteytysmalli, PK");
        return laskentakaava;
    }

    public static Laskentakaava luoPKPohjaisenKoulutuksenKaikkienAineidenKeskiarvo(PkAineet pkAineet) {
        Laskentakaava[] args = new Laskentakaava[] { pkAineet.getLaskentakaava(Aineet.aidinkieliJaKirjallisuus1),
                pkAineet.getLaskentakaava(Aineet.aidinkieliJaKirjallisuus2),
                pkAineet.getLaskentakaava(Aineet.historia), pkAineet.getLaskentakaava(Aineet.yhteiskuntaoppi),
                pkAineet.getLaskentakaava(Aineet.matematiikka), pkAineet.getLaskentakaava(Aineet.fysiikka),
                pkAineet.getLaskentakaava(Aineet.kemia), pkAineet.getLaskentakaava(Aineet.biologia),
                pkAineet.getLaskentakaava(Aineet.kuvataide), pkAineet.getLaskentakaava(Aineet.musiikki),
                pkAineet.getLaskentakaava(Aineet.maantieto), pkAineet.getLaskentakaava(PkAineet.kasityo),
                pkAineet.getLaskentakaava(PkAineet.kotitalous), pkAineet.getLaskentakaava(Aineet.liikunta),
                pkAineet.getLaskentakaava(Aineet.terveystieto), pkAineet.getLaskentakaava(Aineet.uskonto),
                pkAineet.getLaskentakaava(Aineet.a11Kieli), pkAineet.getLaskentakaava(Aineet.a12Kieli),
                pkAineet.getLaskentakaava(Aineet.a13Kieli), pkAineet.getLaskentakaava(Aineet.a21Kieli),
                pkAineet.getLaskentakaava(Aineet.a22Kieli), pkAineet.getLaskentakaava(Aineet.a23Kieli),
                pkAineet.getLaskentakaava(Aineet.b1Kieli), pkAineet.getLaskentakaava(Aineet.b21Kieli),
                pkAineet.getLaskentakaava(Aineet.b22Kieli), pkAineet.getLaskentakaava(Aineet.b23Kieli),
                pkAineet.getLaskentakaava(Aineet.b31Kieli), pkAineet.getLaskentakaava(Aineet.b32Kieli),
                pkAineet.getLaskentakaava(Aineet.b33Kieli) };

        Funktiokutsu keskiarvo = GenericHelper.luoKeskiarvo(args);
        Laskentakaava laskentakaava = GenericHelper.luoLaskentakaavaJaNimettyFunktio(keskiarvo,
                "Kaikkien aineiden keskiarvo, PK");
        return laskentakaava;
    }

}
