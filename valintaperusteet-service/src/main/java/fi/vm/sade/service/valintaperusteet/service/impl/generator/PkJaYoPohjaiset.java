package fi.vm.sade.service.valintaperusteet.service.impl.generator;

import fi.vm.sade.service.valintaperusteet.model.*;
import fi.vm.sade.service.valintaperusteet.service.impl.LuoValintaperusteetServiceImpl;
import scala.actors.threadpool.Arrays;

import java.util.ArrayList;
import java.util.List;

/**
 * User: kwuoti
 * Date: 5.3.2013
 * Time: 12.14
 */
public class PkJaYoPohjaiset {

    public static final String tyokokemuskuukaudet = "TYOKOKEMUSKUUKAUDET";
    public static final String sukupuoli = "SUKUPUOLI";
    public static final int HAKUTOIVEIDEN_LKM = 5;
    public static final String aidinkieli = "aidinkieli";

    public static final String kielikoePrefix = "kielikoe_";

    public static final String yleinenKielitutkintoPrefix = "yleinen_kielitutkinto_";
    public static final String valtionhallinnonKielitutkintoPrefix = "valtionhallinnon_kielitutkinto_";

    public static final String perustopetuksenKieli = "perusopetuksen_kieli";

    public static final String lukionOppimaaranKieli = "lukion_kieli";
    public static final String toisenaKielenaPostfix = "_2";
    public static final String saamenkielisillePostfix = "_SE";
    public static final String viittomakielisillePostfix = "_VK";

    public static Laskentakaava luoHakutoivejarjestyspisteytysmalli() {
        Funktiokutsu pisteet = GenericHelper.luoLukuarvo(2.0);
        Funktiokutsu nollaarvo = GenericHelper.luoLukuarvo(0.0);
        Funktiokutsu ensimmainenHakutoive = GenericHelper.luoEnsimmainenHakutoive();

        Funktiokutsu jos = GenericHelper.luoJosFunktio(ensimmainenHakutoive, pisteet, nollaarvo);
        Laskentakaava laskentakaava = GenericHelper.luoLaskentakaavaJaNimettyFunktio(jos,
                "Hakutoivejärjestyspisteytys, 2 aste, pk ja yo");
        return laskentakaava;
    }


    public static Laskentakaava luoTyokokemuspisteytysmalli() {

        Arvovalikonvertteriparametri[] konvs = {
                GenericHelper.luoArvovalikonvertteriparametri(-10000.0, 3.0, 0.0, false),
                GenericHelper.luoArvovalikonvertteriparametri(3.0, 6.0, 1.0, false),
                GenericHelper.luoArvovalikonvertteriparametri(6.0, 12.0, 2.0, false),
                GenericHelper.luoArvovalikonvertteriparametri(12.0, 10000.0, 3.0, false)
        };

        Funktiokutsu f = GenericHelper.luoHaeLukuarvo(GenericHelper.luoValintaperusteViite(
                tyokokemuskuukaudet, false, Valintaperustelahde.HAETTAVA_ARVO), 0, Arrays.asList(konvs));

        return GenericHelper.luoLaskentakaavaJaNimettyFunktio(f, "Työkokemuspisteytys, 2 aste, pk ja yo");
    }

    public static Laskentakaava luoSukupuolipisteytysmalli() {
        Funktiokutsu thenHaara = GenericHelper.luoLukuarvo(2.0);
        Funktiokutsu elseHaara = GenericHelper.luoLukuarvo(0.0);
        Funktiokutsu ehto = GenericHelper.luoDemografia(sukupuoli, 30.0);

        Funktiokutsu jos = GenericHelper.luoJosFunktio(ehto, thenHaara, elseHaara);
        return GenericHelper.luoLaskentakaavaJaNimettyFunktio(jos, "Sukupuolipisteytys, 2 aste, pk ja yo");
    }

    public static Laskentakaava luoYleinenKoulumenestysLaskentakaava(Laskentakaava laskentakaava, String nimi) {
        Funktiokutsu konvertteri = new Funktiokutsu();
        konvertteri.setFunktionimi(Funktionimi.KONVERTOILUKUARVO);
        Funktioargumentti funk = new Funktioargumentti();
        funk.setLaskentakaavaChild(laskentakaava);
        funk.setIndeksi(1);

        konvertteri.getFunktioargumentit().add(funk);
        konvertteri.getArvovalikonvertteriparametrit().add(GenericHelper.luoArvovalikonvertteriparametri(0.0, 5.50, 0, false));
        konvertteri.getArvovalikonvertteriparametrit().add(GenericHelper.luoArvovalikonvertteriparametri(5.50, 5.75, 1, false));
        konvertteri.getArvovalikonvertteriparametrit().add(GenericHelper.luoArvovalikonvertteriparametri(5.75, 6.00, 2, false));
        konvertteri.getArvovalikonvertteriparametrit().add(GenericHelper.luoArvovalikonvertteriparametri(6.00, 6.25, 3, false));
        konvertteri.getArvovalikonvertteriparametrit().add(GenericHelper.luoArvovalikonvertteriparametri(6.25, 6.50, 4, false));
        konvertteri.getArvovalikonvertteriparametrit().add(GenericHelper.luoArvovalikonvertteriparametri(6.50, 6.75, 5, false));
        konvertteri.getArvovalikonvertteriparametrit().add(GenericHelper.luoArvovalikonvertteriparametri(6.75, 7.00, 6, false));
        konvertteri.getArvovalikonvertteriparametrit().add(GenericHelper.luoArvovalikonvertteriparametri(7.00, 7.25, 7, false));
        konvertteri.getArvovalikonvertteriparametrit().add(GenericHelper.luoArvovalikonvertteriparametri(7.25, 7.50, 8, false));
        konvertteri.getArvovalikonvertteriparametrit().add(GenericHelper.luoArvovalikonvertteriparametri(7.50, 7.75, 9, false));
        konvertteri.getArvovalikonvertteriparametrit().add(GenericHelper.luoArvovalikonvertteriparametri(7.75, 8.00, 10, false));
        konvertteri.getArvovalikonvertteriparametrit().add(GenericHelper.luoArvovalikonvertteriparametri(8.00, 8.25, 11, false));
        konvertteri.getArvovalikonvertteriparametrit().add(GenericHelper.luoArvovalikonvertteriparametri(8.25, 8.50, 12, false));
        konvertteri.getArvovalikonvertteriparametrit().add(GenericHelper.luoArvovalikonvertteriparametri(8.50, 8.75, 13, false));
        konvertteri.getArvovalikonvertteriparametrit().add(GenericHelper.luoArvovalikonvertteriparametri(8.75, 9.00, 14, false));
        konvertteri.getArvovalikonvertteriparametrit().add(GenericHelper.luoArvovalikonvertteriparametri(9.00, 9.25, 15, false));
        konvertteri.getArvovalikonvertteriparametrit().add(GenericHelper.luoArvovalikonvertteriparametri(9.25, 10.1, 16, false));

        Laskentakaava palautettavaLaskentakaava = GenericHelper.luoLaskentakaavaJaNimettyFunktio(konvertteri,
                nimi);
        return palautettavaLaskentakaava;
    }

    public static Laskentakaava luoValintakoekaava(String nimi) {
        ValintaperusteViite valintaperuste = GenericHelper.luoValintaperusteViite(nimi, true, Valintaperustelahde.SYOTETTAVA_ARVO);
        List<Arvovalikonvertteriparametri> konvs = new ArrayList<Arvovalikonvertteriparametri>();
        konvs.add(GenericHelper.luoArvovalikonvertteriparametri(0.0, 1.0, true));
        konvs.add(GenericHelper.luoArvovalikonvertteriparametri(1.0, 10.0, false));

        return GenericHelper.luoLaskentakaavaJaNimettyFunktio(GenericHelper.luoHaeLukuarvo(valintaperuste, konvs), nimi);
    }

    public static Laskentakaava luoHakutoivejarjestysTasapistekaava() {
        Funktiokutsu nolla = GenericHelper.luoLukuarvo(0.0);
        List<Funktiokutsu> summattavat = new ArrayList<Funktiokutsu>();
        int hakutoive = 1;
        for (int i = HAKUTOIVEIDEN_LKM; i > 0; --i) {
            Funktiokutsu pistemaara = GenericHelper.luoLukuarvo(i);

            summattavat.add(GenericHelper.luoJosFunktio(GenericHelper.luoNsHakutoive(hakutoive), pistemaara, nolla));
            ++hakutoive;
        }

        return GenericHelper.luoLaskentakaavaJaNimettyFunktio(
                GenericHelper.luoSumma(summattavat.toArray(new FunktionArgumentti[summattavat.size()])),
                "Hakutoivejärjestystasapistetilanne, 2 aste, pk ja yo");

    }

    public static Laskentakaava luoKielikokeenPakollisuudenLaskentakaava(LuoValintaperusteetServiceImpl.Kielikoodi k) {
        // Kaava palauttaa true, jos hakijan pitää osallistua kielikokeeseen, muutoin false.

        Funktiokutsu[] args = {
                luoKielikoeSuoritettuFunktiokutsu(k),
                luoAidinkieliOnOpetuskieliFunktiokutsu(k),
                luoKielikoekriteeri1(k),
                luoKielikoekriteeri2(k),
                luoKielikoekriteeri3(k),
                luoKielikoekriteeri4(k),
                luoKielikoekriteeri5(k),
                luoKielikoekriteeri6(k),
                luoKielikoekriteeri7(k),
        };


        return GenericHelper.luoLaskentakaavaJaNimettyFunktio(GenericHelper.luoEi(GenericHelper.luoTai(args)),
                "Kielikokeen pakollisuus - " + k.getNimi() + ", 2 aste, pk ja yo"
        );
    }

    public static Funktiokutsu luoAidinkieliOnOpetuskieliFunktiokutsu(LuoValintaperusteetServiceImpl.Kielikoodi k) {
        Funktiokutsu aidinkielivertailu = GenericHelper.luoHaeMerkkijonoJaVertaaYhtasuuruus(
                GenericHelper.luoValintaperusteViite(aidinkieli, false, Valintaperustelahde.HAETTAVA_ARVO),
                k.getKieliarvo(), false);
        return aidinkielivertailu;
    }

    public static Funktiokutsu luoKielikoeSuoritettuFunktiokutsu(LuoValintaperusteetServiceImpl.Kielikoodi k) {
        Funktiokutsu kielikoeSuoritettu = GenericHelper.luoHaeTotuusarvo(GenericHelper.luoValintaperusteViite(
                kielikoePrefix + k.getKieliarvo(), false, Valintaperustelahde.HAETTAVA_ARVO), false);

        return kielikoeSuoritettu;
    }

    /**
     * 1) Hakijalla on perusopetuksen päästötodistus joka suoritettu vastaanottavan oppilaitoksen opetuskielellä
     * (suomi, ruotsi tai saame)
     */
    public static Funktiokutsu luoKielikoekriteeri1(LuoValintaperusteetServiceImpl.Kielikoodi k) {
        return GenericHelper.luoHaeMerkkijonoJaVertaaYhtasuuruus(GenericHelper.luoValintaperusteViite(
                perustopetuksenKieli, false, Valintaperustelahde.HAETTAVA_ARVO), k.getKieliarvo(), false);
    }

    /**
     * 2) Hakija on suorittanut vähintään arvosanalla 7 perusopetuksen toisen kotimaisen A-kielen oppimäärän, joka on
     * vastaanottavan oppilaitoksen opetuskieli
     */
    public static Funktiokutsu luoKielikoekriteeri2(final LuoValintaperusteetServiceImpl.Kielikoodi k) {

        final Funktiokutsu seitseman = GenericHelper.luoLukuarvo(7.0);
        class KriteeriGen {

            public Funktiokutsu luoKriteeri(final String ainetunniste) {
                Funktiokutsu oppiaineOnOpetuskieli = GenericHelper.luoHaeMerkkijonoJaVertaaYhtasuuruus(
                        GenericHelper.luoValintaperusteViite(PkAineet.oppiaine(ainetunniste), false,
                                Valintaperustelahde.HAETTAVA_ARVO), k.getKieliarvo(), false);

                Funktiokutsu arvosana = GenericHelper.luoHaeLukuarvo(
                        GenericHelper.luoValintaperusteViite(PkAineet.pakollinen(ainetunniste), false,
                                Valintaperustelahde.HAETTAVA_ARVO), 0.0);

                Funktiokutsu arvosanaSuurempiKuin7 = GenericHelper.luoSuurempiTaiYhtasuuriKuin(arvosana, seitseman);

                return GenericHelper.luoJa(oppiaineOnOpetuskieli, arvosanaSuurempiKuin7);
            }
        }

        // Tarkastellaan A1- ja A2-kieliä
        final String[] aineet = {
                Aineet.a11Kieli,
                Aineet.a12Kieli,
                Aineet.a13Kieli,
                Aineet.a21Kieli,
                Aineet.a22Kieli,
                Aineet.a23Kieli
        };

        List<Funktiokutsu> args = new ArrayList<Funktiokutsu>();
        KriteeriGen gen = new KriteeriGen();
        for (String aine : aineet) {
            args.add(gen.luoKriteeri(aine));
        }

        return GenericHelper.luoTai(args.toArray(new FunktionArgumentti[args.size()]));
    }

    /**
     * 3) Hakija on suorittanut vähintään arvosanalla 7 perusopetuksen suomi tai ruotsi toisena kielenä oppimäärän
     * kielessä, joka on vastaanottavan oppilaitoksen opetuskieli
     */
    public static Funktiokutsu luoKielikoekriteeri3(final LuoValintaperusteetServiceImpl.Kielikoodi k) {

        final Funktiokutsu seitseman = GenericHelper.luoLukuarvo(7.0);

        class KriteeriGen {
            public Funktiokutsu luoKriteeri(final String ainetunniste) {
                Funktiokutsu aidinkieliOnFiTaiSvToisenaKielena = GenericHelper.luoHaeMerkkijonoJaVertaaYhtasuuruus(
                        GenericHelper.luoValintaperusteViite(PkAineet.oppiaine(ainetunniste), false,
                                Valintaperustelahde.HAETTAVA_ARVO), k.getKieliarvo() + toisenaKielenaPostfix, false);

                Funktiokutsu arvosanaAi = GenericHelper.luoHaeLukuarvo(GenericHelper.luoValintaperusteViite(
                        PkAineet.pakollinen(ainetunniste), false, Valintaperustelahde.HAETTAVA_ARVO), 0);

                Funktiokutsu arvosanaSuurempiKuin7 = GenericHelper.luoSuurempiTaiYhtasuuriKuin(arvosanaAi, seitseman);

                return GenericHelper.luoJa(aidinkieliOnFiTaiSvToisenaKielena, arvosanaSuurempiKuin7);
            }
        }

        KriteeriGen gen = new KriteeriGen();

        return GenericHelper.luoTai(
                gen.luoKriteeri(Aineet.aidinkieliJaKirjallisuus1),
                gen.luoKriteeri(Aineet.aidinkieliJaKirjallisuus2));
    }

    /**
     * 4) Hakija on suorittanut lukion oppimäärän ylioppilastutkinnon vastaanottavan oppilaitoksen opetuskielellä
     */
    public static Funktiokutsu luoKielikoekriteeri4(LuoValintaperusteetServiceImpl.Kielikoodi k) {
        return GenericHelper.luoHaeMerkkijonoJaVertaaYhtasuuruus(
                GenericHelper.luoValintaperusteViite(lukionOppimaaranKieli, false, Valintaperustelahde.HAETTAVA_ARVO),
                k.getKieliarvo(), false);
    }

    /**
     * 5) hakija on suorittanut lukiokoulutuksen  koko oppimäärän jossakin seuraavista äidinkieli,
     * kirjallisuus-oppiaineen oppimäärissä:
     * suomi äidinkielenä,
     * ruotsi äidinkielenä,
     * suomi toisena kielinä,
     * ruotsi toisena kielienä,
     * suomi saamenkielisille,
     * suomi viittomakielisille,
     * ruotsi viittomakielisille
     * joka on vastaanottavan oppilaitoksen opetuskieli
     */
    public static Funktiokutsu luoKielikoekriteeri5(final LuoValintaperusteetServiceImpl.Kielikoodi k) {

        final Funktiokutsu viisi = GenericHelper.luoLukuarvo(5.0);

        class KriteeriGen {
            public Funktiokutsu luoKriteeri(final String ainetunniste) {
                Funktiokutsu aidinkielena = GenericHelper.luoHaeMerkkijonoJaVertaaYhtasuuruus(
                        GenericHelper.luoValintaperusteViite(YoAineet.oppiaine(ainetunniste), false,
                                Valintaperustelahde.HAETTAVA_ARVO), k.getKieliarvo(), false);
                Funktiokutsu toisenaKielena = GenericHelper.luoHaeMerkkijonoJaVertaaYhtasuuruus(
                        GenericHelper.luoValintaperusteViite(YoAineet.oppiaine(ainetunniste), false,
                                Valintaperustelahde.HAETTAVA_ARVO), k.getKieliarvo() + toisenaKielenaPostfix, false);
                Funktiokutsu saamenkielisille = GenericHelper.luoHaeMerkkijonoJaVertaaYhtasuuruus(
                        GenericHelper.luoValintaperusteViite(YoAineet.oppiaine(ainetunniste), false,
                                Valintaperustelahde.HAETTAVA_ARVO), k.getKieliarvo() + saamenkielisillePostfix, false);
                Funktiokutsu viittomakielisille = GenericHelper.luoHaeMerkkijonoJaVertaaYhtasuuruus(
                        GenericHelper.luoValintaperusteViite(YoAineet.oppiaine(ainetunniste), false,
                                Valintaperustelahde.HAETTAVA_ARVO), k.getKieliarvo() + viittomakielisillePostfix, false);

                Funktiokutsu arvosana = GenericHelper.luoHaeLukuarvo(
                        GenericHelper.luoValintaperusteViite(YoAineet.pakollinen(ainetunniste), false,
                                Valintaperustelahde.HAETTAVA_ARVO), 0.0);

                Funktiokutsu suurempiKuinViisi = GenericHelper.luoSuurempiTaiYhtasuuriKuin(arvosana, viisi);

                return GenericHelper.luoJa(
                        GenericHelper.luoTai(aidinkielena, toisenaKielena, saamenkielisille, viittomakielisille),
                        suurempiKuinViisi);
            }
        }

        KriteeriGen gen = new KriteeriGen();
        return GenericHelper.luoTai(
                gen.luoKriteeri(Aineet.aidinkieliJaKirjallisuus1),
                gen.luoKriteeri(Aineet.aidinkieliJaKirjallisuus2));

    }

    /**
     * 6) Hakija on suorittanut lukiokoulutuksen toisen kotimaisen kielen koko oppimäärän  joka on vastaanottavan
     * oppilaitoksen opetuskieli
     */
    public static Funktiokutsu luoKielikoekriteeri6(final LuoValintaperusteetServiceImpl.Kielikoodi k) {

        final Funktiokutsu viisi = GenericHelper.luoLukuarvo(5.0);

        class KriteeriGen {
            public Funktiokutsu luoKriteeri(final String ainetunniste) {
                Funktiokutsu oppiaineOnOpetuskieli = GenericHelper.luoHaeMerkkijonoJaVertaaYhtasuuruus(
                        GenericHelper.luoValintaperusteViite(YoAineet.oppiaine(ainetunniste), false,
                                Valintaperustelahde.HAETTAVA_ARVO), k.getKieliarvo(), false);

                Funktiokutsu arvosana = GenericHelper.luoHaeLukuarvo(
                        GenericHelper.luoValintaperusteViite(YoAineet.pakollinen(ainetunniste), false,
                                Valintaperustelahde.HAETTAVA_ARVO), 0.0);

                Funktiokutsu arvosanaSuurempiKuin5 = GenericHelper.luoSuurempiTaiYhtasuuriKuin(arvosana, viisi);

                return GenericHelper.luoJa(oppiaineOnOpetuskieli, arvosanaSuurempiKuin5);
            }
        }

        final String[] aineet = {
                Aineet.a11Kieli,
                Aineet.a12Kieli,
                Aineet.a13Kieli,
                Aineet.a21Kieli,
                Aineet.a22Kieli,
                Aineet.a23Kieli,
                Aineet.b1Kieli
        };

        List<Funktiokutsu> args = new ArrayList<Funktiokutsu>();
        KriteeriGen gen = new KriteeriGen();
        for (String aine : aineet) {
            args.add(gen.luoKriteeri(aine));
        }

        return GenericHelper.luoTai(args.toArray(new FunktionArgumentti[args.size()]));
    }

    /**
     * 7) Hakija on suorittanut yleisten kielitutkintojen suomen tai ruotsin kielen tutkinnon kaikki osakokeet
     * vähintään taitotasolla 3 tai hakija on suorittanut valtionhallinnon kielitutkintojen suomen tai ruotsin kielen
     * suullisen ja kirjallisen taidon tutkinnon vähintään taitotasolla tyydyttävä
     */
    public static Funktiokutsu luoKielikoekriteeri7(final LuoValintaperusteetServiceImpl.Kielikoodi k) {
        Funktiokutsu yleinenKielitutktintoSuoritettu = GenericHelper.luoHaeTotuusarvo(
                GenericHelper.luoValintaperusteViite(yleinenKielitutkintoPrefix + k.getKieliarvo(), false,
                        Valintaperustelahde.HAETTAVA_ARVO), false);

        Funktiokutsu valtionhallinnonKielitutkintoSuoritettu = GenericHelper.luoHaeTotuusarvo(
                GenericHelper.luoValintaperusteViite(valtionhallinnonKielitutkintoPrefix + k.getKieliarvo(), false,
                        Valintaperustelahde.HAETTAVA_ARVO), false);

        return GenericHelper.luoTai(yleinenKielitutktintoSuoritettu, valtionhallinnonKielitutkintoSuoritettu);
    }
}
