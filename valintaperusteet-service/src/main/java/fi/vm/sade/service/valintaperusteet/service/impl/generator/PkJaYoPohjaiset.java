package fi.vm.sade.service.valintaperusteet.service.impl.generator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi;
import fi.vm.sade.service.valintaperusteet.dto.model.Valintaperustelahde;
import fi.vm.sade.service.valintaperusteet.model.Arvovalikonvertteriparametri;
import fi.vm.sade.service.valintaperusteet.model.Funktioargumentti;
import fi.vm.sade.service.valintaperusteet.model.Funktiokutsu;
import fi.vm.sade.service.valintaperusteet.model.FunktionArgumentti;
import fi.vm.sade.service.valintaperusteet.model.Laskentakaava;
import fi.vm.sade.service.valintaperusteet.model.ValintaperusteViite;

/**
 * User: kwuoti Date: 5.3.2013 Time: 12.14
 */
public class PkJaYoPohjaiset {

    public static final String tyokokemuskuukaudet = "TYOKOKEMUSKUUKAUDET";
    public static final String sukupuoli = "SUKUPUOLI";

    public static final String aidinkieli = "aidinkieli";

    public static final String yleinenKielitutkintoPrefix = "yleinen_kielitutkinto_";
    public static final String valtionhallinnonKielitutkintoPrefix = "valtionhallinnon_kielitutkinto_";

    public static final String perustopetuksenKieli = "perusopetuksen_kieli";

    public static final String lukionOppimaaranKieli = "lukion_kieli";
    public static final String toisenaKielenaPostfix = "_2";
    public static final String saamenkielisillePostfix = "_SE";
    public static final String viittomakielisillePostfix = "_VK";

    public static final String kielikoetunniste = "kielikoe_tunniste";
    public static final String opetuskieli = "opetuskieli";

    public static final String peruskoulunPaattotodistusVahintaanSeitseman = "peruskoulun_paattotodistus_vahintaan_seitseman_";
    public static final String lukionPaattotodistusVahintaanSeitseman = "lukion_paattotodistus_vahintaan_seitseman_";
    public static final String urheilijaLisapisteTunniste = "urheilija_lisapiste_tunniste";
    public static final String preferenceTunniste = "preference";
    public static final String urheilijanAmmatillisenKoulutuksenLisakysymysTunniste = "_urheilijan_ammatillisen_koulutuksen_lisakysymys";
    public static final String urheilijaHakuSallittu = "urheilija_haku_sallittu";
    public static final String hakukohteenOid = "hakukohde_oid";
    public static final String koulutusIdTunniste = "-Koulutus-id";

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

        Arvovalikonvertteriparametri[] konvs = { GenericHelper.luoArvovalikonvertteriparametri(-10000.0, 3.0, 0.0),
                GenericHelper.luoArvovalikonvertteriparametri(3.0, 6.0, 1.0),
                GenericHelper.luoArvovalikonvertteriparametri(6.0, 12.0, 2.0),
                GenericHelper.luoArvovalikonvertteriparametri(12.0, 10000.0, 3.0) };

        Funktiokutsu f = GenericHelper.luoHaeLukuarvo(
                GenericHelper.luoValintaperusteViite(tyokokemuskuukaudet, false, Valintaperustelahde.HAETTAVA_ARVO), 0,
                Arrays.asList(konvs));

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
        konvertteri.getArvovalikonvertteriparametrit().add(GenericHelper.luoArvovalikonvertteriparametri(0.0, 5.50, 0));
        konvertteri.getArvovalikonvertteriparametrit()
                .add(GenericHelper.luoArvovalikonvertteriparametri(5.50, 5.75, 1));
        konvertteri.getArvovalikonvertteriparametrit()
                .add(GenericHelper.luoArvovalikonvertteriparametri(5.75, 6.00, 2));
        konvertteri.getArvovalikonvertteriparametrit()
                .add(GenericHelper.luoArvovalikonvertteriparametri(6.00, 6.25, 3));
        konvertteri.getArvovalikonvertteriparametrit()
                .add(GenericHelper.luoArvovalikonvertteriparametri(6.25, 6.50, 4));
        konvertteri.getArvovalikonvertteriparametrit()
                .add(GenericHelper.luoArvovalikonvertteriparametri(6.50, 6.75, 5));
        konvertteri.getArvovalikonvertteriparametrit()
                .add(GenericHelper.luoArvovalikonvertteriparametri(6.75, 7.00, 6));
        konvertteri.getArvovalikonvertteriparametrit()
                .add(GenericHelper.luoArvovalikonvertteriparametri(7.00, 7.25, 7));
        konvertteri.getArvovalikonvertteriparametrit()
                .add(GenericHelper.luoArvovalikonvertteriparametri(7.25, 7.50, 8));
        konvertteri.getArvovalikonvertteriparametrit()
                .add(GenericHelper.luoArvovalikonvertteriparametri(7.50, 7.75, 9));
        konvertteri.getArvovalikonvertteriparametrit().add(
                GenericHelper.luoArvovalikonvertteriparametri(7.75, 8.00, 10));
        konvertteri.getArvovalikonvertteriparametrit().add(
                GenericHelper.luoArvovalikonvertteriparametri(8.00, 8.25, 11));
        konvertteri.getArvovalikonvertteriparametrit().add(
                GenericHelper.luoArvovalikonvertteriparametri(8.25, 8.50, 12));
        konvertteri.getArvovalikonvertteriparametrit().add(
                GenericHelper.luoArvovalikonvertteriparametri(8.50, 8.75, 13));
        konvertteri.getArvovalikonvertteriparametrit().add(
                GenericHelper.luoArvovalikonvertteriparametri(8.75, 9.00, 14));
        konvertteri.getArvovalikonvertteriparametrit().add(
                GenericHelper.luoArvovalikonvertteriparametri(9.00, 9.25, 15));
        konvertteri.getArvovalikonvertteriparametrit().add(
                GenericHelper.luoArvovalikonvertteriparametri(9.25, 10.1, 16));

        Laskentakaava palautettavaLaskentakaava = GenericHelper.luoLaskentakaavaJaNimettyFunktio(konvertteri, nimi);
        return palautettavaLaskentakaava;
    }

    public static Laskentakaava luoValintakoekaava(String nimi) {
        ValintaperusteViite valintaperuste = GenericHelper.luoValintaperusteViite(nimi, true,
                Valintaperustelahde.SYOTETTAVA_ARVO);

        List<Arvovalikonvertteriparametri> konvs = new ArrayList<Arvovalikonvertteriparametri>();

        konvs.add(GenericHelper.luoArvovalikonvertteriparametri(0.0, 10.0));

        Funktiokutsu funktiokutsu = GenericHelper.luoHaeLukuarvo(valintaperuste, konvs);

        return GenericHelper.luoLaskentakaavaJaNimettyFunktio(
                GenericHelper.luoHylkaaArvovalilla(funktiokutsu, "Valintakoetulos hylätty", "0.0", "1.0"), nimi);
    }

    public static Laskentakaava luoLisapistekaava(String nimi) {
        ValintaperusteViite valintaperuste = GenericHelper.luoValintaperusteViite(nimi, true,
                Valintaperustelahde.SYOTETTAVA_ARVO);
        List<Arvovalikonvertteriparametri> konvs = new ArrayList<Arvovalikonvertteriparametri>();
        konvs.add(GenericHelper.luoArvovalikonvertteriparametri(0.0, 3.0));

        return GenericHelper
                .luoLaskentakaavaJaNimettyFunktio(GenericHelper.luoHaeLukuarvo(valintaperuste, konvs), nimi);
    }

    public static Laskentakaava luoUrheilijaLisapisteenMahdollisuus() {
        Funktiokutsu[] args = new Funktiokutsu[YhteisetKaavat.HAKUTOIVEIDEN_LKM];
        for (int i = 1; i <= YhteisetKaavat.HAKUTOIVEIDEN_LKM; i++) {
            Funktiokutsu kutsu = GenericHelper
                    .luoJa(GenericHelper.luoValintaperusteyhtasuuruus(GenericHelper.luoValintaperusteViite(
                            preferenceTunniste + i + koulutusIdTunniste, false, Valintaperustelahde.HAETTAVA_ARVO,
                            "Hakukohteen OID hakemuksella"), GenericHelper.luoValintaperusteViite(hakukohteenOid, true,
                            Valintaperustelahde.HAKUKOHTEEN_ARVO, "Hakukohteen OID")), GenericHelper.luoHaeTotuusarvo(
                            GenericHelper.luoValintaperusteViite(preferenceTunniste + i
                                    + urheilijanAmmatillisenKoulutuksenLisakysymysTunniste, false,
                                    Valintaperustelahde.HAETTAVA_ARVO), false), GenericHelper.luoHaeTotuusarvo(
                            GenericHelper.luoValintaperusteViite(urheilijaHakuSallittu, false,
                                    Valintaperustelahde.HAKUKOHTEEN_ARVO), false));
            args[i - 1] = kutsu;
        }
        return GenericHelper.luoLaskentakaavaJaNimettyFunktio(GenericHelper.luoTai(args),
                "Mahdollisuus urheilijalisäpisteeseen");
    }

    public static Laskentakaava luoKielikokeenPakollisuudenLaskentakaava() {
        Funktiokutsu[] args = { luoKielikoeSuoritettuFunktiokutsu(), luoAidinkieliOnOpetuskieliFunktiokutsu(),
                luoKielikoekriteeri1Funktiokutsu(), luoKielikoekriteeri2Funktiokutsu(),
                luoKielikoekriteeri3Funktiokutsu(), luoKielikoekriteeri4Funktiokutsu(),
                luoKielikoekriteeri5Funktiokutsu(), luoKielikoekriteeri6Funktiokutsu(),
                luoKielikoekriteeri7Funktiokutsu(), luoKielikoekriteeri8Funktiokutsu() };

        return GenericHelper.luoLaskentakaavaJaNimettyFunktio(GenericHelper.luoEi(GenericHelper.luoTai(args)),
                "Kielikokeen pakollisuus - 2 aste, pk ja yo");
    }

    public static Funktiokutsu luoAidinkieliOnOpetuskieliFunktiokutsu() {
        return GenericHelper.luoValintaperusteyhtasuuruus(GenericHelper.luoValintaperusteViite(opetuskieli, true,
                Valintaperustelahde.HAKUKOHTEEN_ARVO, "Opetuskieli", false), GenericHelper.luoValintaperusteViite(
                aidinkieli, false, Valintaperustelahde.HAETTAVA_ARVO, "Äidinkieli"));
    }

    public static Funktiokutsu luoKielikoeSuoritettuFunktiokutsu() {
        return GenericHelper.luoHaeTotuusarvo(GenericHelper.luoValintaperusteViite(kielikoetunniste, false,
                Valintaperustelahde.HAKUKOHTEEN_SYOTETTAVA_ARVO, "Kielikokeen tunniste", true), false);
    }

    /**
     * 1) Hakijalla on perusopetuksen päästötodistus joka suoritettu
     * vastaanottavan oppilaitoksen opetuskielellä (suomi, ruotsi tai saame)
     */
    public static Funktiokutsu luoKielikoekriteeri1Funktiokutsu() {
        return GenericHelper.luoValintaperusteyhtasuuruus(GenericHelper.luoValintaperusteViite(opetuskieli, true,
                Valintaperustelahde.HAKUKOHTEEN_ARVO, "Opetuskieli", false), GenericHelper.luoValintaperusteViite(
                perustopetuksenKieli, false, Valintaperustelahde.HAETTAVA_ARVO, "Perusopetuksen kieli"));
    }

    /**
     * 2) Hakija on suorittanut vähintään arvosanalla 7 perusopetuksen toisen
     * kotimaisen A-kielen oppimäärän, joka on vastaanottavan oppilaitoksen
     * opetuskieli
     */
    public static Funktiokutsu luoKielikoekriteeri2Funktiokutsu() {
        final Funktiokutsu seitseman = GenericHelper.luoLukuarvo(7.0);
        class KriteeriGen {

            public Funktiokutsu luoKriteeri(final String ainetunniste) {
                Funktiokutsu oppiaineOnOpetuskieli = GenericHelper.luoValintaperusteyhtasuuruus(GenericHelper
                        .luoValintaperusteViite(opetuskieli, true, Valintaperustelahde.HAKUKOHTEEN_ARVO, "Opetuskieli",
                                false), GenericHelper.luoValintaperusteViite(PkAineet.oppiaine(ainetunniste), false,
                        Valintaperustelahde.HAETTAVA_ARVO, ainetunniste + " oppiaine"));

                Funktiokutsu arvosana = GenericHelper.luoHaeLukuarvo(GenericHelper.luoValintaperusteViite(
                        PkAineet.pakollinen(ainetunniste), false, Valintaperustelahde.HAETTAVA_ARVO), 0.0);

                Funktiokutsu arvosanaSuurempiKuin7 = GenericHelper.luoSuurempiTaiYhtasuuriKuin(arvosana, seitseman);

                return GenericHelper.luoJa(oppiaineOnOpetuskieli, arvosanaSuurempiKuin7);
            }
        }

        // Tarkastellaan A1- ja A2-kieliä
        final String[] aineet = { Aineet.a11Kieli, Aineet.a12Kieli, Aineet.a13Kieli, Aineet.a21Kieli, Aineet.a22Kieli,
                Aineet.a23Kieli };

        List<Funktiokutsu> args = new ArrayList<Funktiokutsu>();
        KriteeriGen gen = new KriteeriGen();
        for (String aine : aineet) {
            args.add(gen.luoKriteeri(aine));
        }

        return GenericHelper.luoTai(args.toArray(new FunktionArgumentti[args.size()]));
    }

    /**
     * 3) Hakija on suorittanut vähintään arvosanalla 7 perusopetuksen suomi tai
     * ruotsi toisena kielenä oppimäärän kielessä, joka on vastaanottavan
     * oppilaitoksen opetuskieli
     */
    public static Funktiokutsu luoKielikoekriteeri3Funktiokutsu() {

        final Funktiokutsu seitseman = GenericHelper.luoLukuarvo(7.0);
        final String fi = "fi";
        final String sv = "sv";

        class KriteeriGen {
            public Funktiokutsu luoKriteeri(final String ainetunniste) {
                Funktiokutsu aidinkieliOnFiTaiSvToisenaKielena = GenericHelper.luoTai(GenericHelper.luoJa(GenericHelper
                        .luoHaeMerkkijonoJaVertaaYhtasuuruus(GenericHelper.luoValintaperusteViite(opetuskieli, true,
                                Valintaperustelahde.HAKUKOHTEEN_ARVO, "Opetuskieli", false), fi), GenericHelper
                        .luoHaeMerkkijonoJaVertaaYhtasuuruus(GenericHelper.luoValintaperusteViite(
                                PkAineet.oppiaine(ainetunniste), false, Valintaperustelahde.HAETTAVA_ARVO, ainetunniste
                                        + " oppiaine"), fi + toisenaKielenaPostfix, false)), GenericHelper.luoJa(
                        GenericHelper.luoHaeMerkkijonoJaVertaaYhtasuuruus(GenericHelper.luoValintaperusteViite(
                                opetuskieli, true, Valintaperustelahde.HAKUKOHTEEN_ARVO, "Opetuskieli", false), sv),
                        GenericHelper.luoHaeMerkkijonoJaVertaaYhtasuuruus(GenericHelper.luoValintaperusteViite(
                                PkAineet.oppiaine(ainetunniste), false, Valintaperustelahde.HAETTAVA_ARVO, ainetunniste
                                        + " oppiaine"), sv + toisenaKielenaPostfix, false)));

                Funktiokutsu arvosanaAi = GenericHelper.luoHaeLukuarvo(GenericHelper.luoValintaperusteViite(
                        PkAineet.pakollinen(ainetunniste), false, Valintaperustelahde.HAETTAVA_ARVO), 0);

                Funktiokutsu arvosanaSuurempiKuin7 = GenericHelper.luoSuurempiTaiYhtasuuriKuin(arvosanaAi, seitseman);

                return GenericHelper.luoJa(aidinkieliOnFiTaiSvToisenaKielena, arvosanaSuurempiKuin7);
            }
        }

        KriteeriGen gen = new KriteeriGen();

        return GenericHelper.luoTai(gen.luoKriteeri(Aineet.aidinkieliJaKirjallisuus1),
                gen.luoKriteeri(Aineet.aidinkieliJaKirjallisuus2));
    }

    /**
     * 4) Hakija on suorittanut lukion oppimäärän ylioppilastutkinnon
     * vastaanottavan oppilaitoksen opetuskielellä
     */
    public static Funktiokutsu luoKielikoekriteeri4Funktiokutsu() {
        return GenericHelper.luoValintaperusteyhtasuuruus(GenericHelper.luoValintaperusteViite(opetuskieli, true,
                Valintaperustelahde.HAKUKOHTEEN_ARVO, "Opetuskieli", false), GenericHelper.luoValintaperusteViite(
                lukionOppimaaranKieli, false, Valintaperustelahde.HAETTAVA_ARVO, "Lukion oppimäärän suorituskieli"));
    }

    /**
     * 5) hakija on suorittanut lukiokoulutuksen koko oppimäärän jossakin
     * seuraavista äidinkieli, kirjallisuus-oppiaineen oppimäärissä: suomi
     * äidinkielenä, ruotsi äidinkielenä, suomi toisena kielinä, ruotsi toisena
     * kielienä, suomi saamenkielisille, suomi viittomakielisille, ruotsi
     * viittomakielisille joka on vastaanottavan oppilaitoksen opetuskieli
     */
    public static Funktiokutsu luoKielikoekriteeri5Funktiokutsu() {

        final Funktiokutsu viisi = GenericHelper.luoLukuarvo(5.0);
        final String fi = "fi";
        final String sv = "sv";

        class KriteeriGen {
            public Funktiokutsu luoKriteeri(final String ainetunniste) {
                Funktiokutsu opetuskieli = GenericHelper.luoTai(GenericHelper.luoJa(GenericHelper
                        .luoHaeMerkkijonoJaVertaaYhtasuuruus(GenericHelper.luoValintaperusteViite(
                                PkJaYoPohjaiset.opetuskieli, true, Valintaperustelahde.HAKUKOHTEEN_ARVO, "Opetuskieli",
                                false), fi, false), GenericHelper.luoTai(
                        GenericHelper.luoHaeMerkkijonoJaVertaaYhtasuuruus(GenericHelper.luoValintaperusteViite(
                                YoAineet.oppiaine(ainetunniste), false, Valintaperustelahde.HAETTAVA_ARVO), fi, false),
                        GenericHelper.luoHaeMerkkijonoJaVertaaYhtasuuruus(GenericHelper.luoValintaperusteViite(
                                YoAineet.oppiaine(ainetunniste), false, Valintaperustelahde.HAETTAVA_ARVO), fi
                                + toisenaKielenaPostfix, false),
                        GenericHelper.luoHaeMerkkijonoJaVertaaYhtasuuruus(GenericHelper.luoValintaperusteViite(
                                YoAineet.oppiaine(ainetunniste), false, Valintaperustelahde.HAETTAVA_ARVO), fi
                                + saamenkielisillePostfix, false),
                        GenericHelper.luoHaeMerkkijonoJaVertaaYhtasuuruus(GenericHelper.luoValintaperusteViite(
                                YoAineet.oppiaine(ainetunniste), false, Valintaperustelahde.HAETTAVA_ARVO), fi
                                + viittomakielisillePostfix, false))), GenericHelper.luoJa(GenericHelper
                        .luoHaeMerkkijonoJaVertaaYhtasuuruus(GenericHelper.luoValintaperusteViite(
                                PkJaYoPohjaiset.opetuskieli, true, Valintaperustelahde.HAKUKOHTEEN_ARVO, "Opetuskieli",
                                false), sv, false), GenericHelper.luoTai(
                        GenericHelper.luoHaeMerkkijonoJaVertaaYhtasuuruus(GenericHelper.luoValintaperusteViite(
                                YoAineet.oppiaine(ainetunniste), false, Valintaperustelahde.HAETTAVA_ARVO), sv, false),
                        GenericHelper.luoHaeMerkkijonoJaVertaaYhtasuuruus(GenericHelper.luoValintaperusteViite(
                                YoAineet.oppiaine(ainetunniste), false, Valintaperustelahde.HAETTAVA_ARVO), sv
                                + toisenaKielenaPostfix, false),
                        GenericHelper.luoHaeMerkkijonoJaVertaaYhtasuuruus(GenericHelper.luoValintaperusteViite(
                                YoAineet.oppiaine(ainetunniste), false, Valintaperustelahde.HAETTAVA_ARVO), sv
                                + saamenkielisillePostfix, false),
                        GenericHelper.luoHaeMerkkijonoJaVertaaYhtasuuruus(GenericHelper.luoValintaperusteViite(
                                YoAineet.oppiaine(ainetunniste), false, Valintaperustelahde.HAETTAVA_ARVO), sv
                                + viittomakielisillePostfix, false))));

                Funktiokutsu arvosanaSuurempiKuinViisi = GenericHelper.luoSuurempiTaiYhtasuuriKuin(
                        GenericHelper.luoHaeLukuarvo(GenericHelper.luoValintaperusteViite(
                                YoAineet.pakollinen(ainetunniste), false, Valintaperustelahde.HAETTAVA_ARVO), 0.0),
                        viisi);

                return GenericHelper.luoJa(opetuskieli, arvosanaSuurempiKuinViisi);
            }
        }

        KriteeriGen gen = new KriteeriGen();
        return GenericHelper.luoTai(gen.luoKriteeri(Aineet.aidinkieliJaKirjallisuus1),
                gen.luoKriteeri(Aineet.aidinkieliJaKirjallisuus2));

    }

    /**
     * 6) Hakija on suorittanut lukiokoulutuksen toisen kotimaisen kielen koko
     * oppimäärän joka on vastaanottavan oppilaitoksen opetuskieli
     */
    public static Funktiokutsu luoKielikoekriteeri6Funktiokutsu() {

        final Funktiokutsu viisi = GenericHelper.luoLukuarvo(5.0);

        class KriteeriGen {
            public Funktiokutsu luoKriteeri(final String ainetunniste) {
                Funktiokutsu oppiaineOnOpetuskieli = GenericHelper.luoValintaperusteyhtasuuruus(GenericHelper
                        .luoValintaperusteViite(opetuskieli, true, Valintaperustelahde.HAKUKOHTEEN_ARVO, "Opetuskieli",
                                false), GenericHelper.luoValintaperusteViite(YoAineet.oppiaine(ainetunniste), false,
                        Valintaperustelahde.HAETTAVA_ARVO, ainetunniste + " oppiaine"));

                Funktiokutsu arvosana = GenericHelper.luoHaeLukuarvo(GenericHelper.luoValintaperusteViite(
                        YoAineet.pakollinen(ainetunniste), false, Valintaperustelahde.HAETTAVA_ARVO), 0.0);

                Funktiokutsu arvosanaSuurempiKuin5 = GenericHelper.luoSuurempiTaiYhtasuuriKuin(arvosana, viisi);

                return GenericHelper.luoJa(oppiaineOnOpetuskieli, arvosanaSuurempiKuin5);
            }
        }

        final String[] aineet = { Aineet.a11Kieli, Aineet.a12Kieli, Aineet.a13Kieli, Aineet.a21Kieli, Aineet.a22Kieli,
                Aineet.a23Kieli, Aineet.b1Kieli };

        List<Funktiokutsu> args = new ArrayList<Funktiokutsu>();
        KriteeriGen gen = new KriteeriGen();
        for (String aine : aineet) {
            args.add(gen.luoKriteeri(aine));
        }

        return GenericHelper.luoTai(args.toArray(new FunktionArgumentti[args.size()]));
    }

    /**
     * 7) Hakija on suorittanut yleisten kielitutkintojen suomen tai ruotsin
     * kielen tutkinnon kaikki osakokeet vähintään taitotasolla 3 tai hakija on
     * suorittanut valtionhallinnon kielitutkintojen suomen tai ruotsin kielen
     * suullisen ja kirjallisen taidon tutkinnon vähintään taitotasolla
     * tyydyttävä
     */
    public static Funktiokutsu luoKielikoekriteeri7Funktiokutsu() {
        final String fi = "fi";
        final String sv = "sv";

        return GenericHelper.luoTai(GenericHelper.luoJa(GenericHelper.luoHaeMerkkijonoJaVertaaYhtasuuruus(GenericHelper
                .luoValintaperusteViite(PkJaYoPohjaiset.opetuskieli, true, Valintaperustelahde.HAKUKOHTEEN_ARVO,
                        "Opetuskieli", false), fi, false), GenericHelper.luoTai(GenericHelper.luoHaeTotuusarvo(
                GenericHelper.luoValintaperusteViite(yleinenKielitutkintoPrefix + fi, false,
                        Valintaperustelahde.HAETTAVA_ARVO), false), GenericHelper.luoHaeTotuusarvo(GenericHelper
                .luoValintaperusteViite(valtionhallinnonKielitutkintoPrefix + fi, false,
                        Valintaperustelahde.HAETTAVA_ARVO), false))), GenericHelper.luoJa(GenericHelper
                .luoHaeMerkkijonoJaVertaaYhtasuuruus(GenericHelper.luoValintaperusteViite(PkJaYoPohjaiset.opetuskieli,
                        true, Valintaperustelahde.HAKUKOHTEEN_ARVO, "Opetuskieli", false), sv, false), GenericHelper
                .luoTai(GenericHelper.luoHaeTotuusarvo(GenericHelper.luoValintaperusteViite(yleinenKielitutkintoPrefix
                        + sv, false, Valintaperustelahde.HAETTAVA_ARVO), false), GenericHelper.luoHaeTotuusarvo(
                        GenericHelper.luoValintaperusteViite(valtionhallinnonKielitutkintoPrefix + sv, false,
                                Valintaperustelahde.HAETTAVA_ARVO), false))));
    }

    /**
     * 8) PK Hakijan peruskoulun päättodistuksen tai LK hakijan lukion
     * päättötodistuksen arvosana on vähintään seitsemän hakukohteen
     * opetuskielelle
     */
    public static Funktiokutsu luoKielikoekriteeri8Funktiokutsu() {
        final String fi = "fi";
        final String sv = "sv";
        final String se = "se";
        final String vk = "vk";

        return GenericHelper
                .luoTai(GenericHelper.luoJa(GenericHelper.luoHaeMerkkijonoJaVertaaYhtasuuruus(GenericHelper
                        .luoValintaperusteViite(opetuskieli, true, Valintaperustelahde.HAKUKOHTEEN_ARVO, "Opetuskieli",
                                false), fi), GenericHelper.luoHaeTotuusarvo(GenericHelper.luoValintaperusteViite(
                        peruskoulunPaattotodistusVahintaanSeitseman + fi, false, Valintaperustelahde.HAETTAVA_ARVO),
                        false)), GenericHelper.luoJa(GenericHelper.luoHaeMerkkijonoJaVertaaYhtasuuruus(GenericHelper
                        .luoValintaperusteViite(opetuskieli, true, Valintaperustelahde.HAKUKOHTEEN_ARVO, "Opetuskieli",
                                false), sv), GenericHelper.luoHaeTotuusarvo(GenericHelper.luoValintaperusteViite(
                        peruskoulunPaattotodistusVahintaanSeitseman + sv, false, Valintaperustelahde.HAETTAVA_ARVO),
                        false)), GenericHelper.luoJa(GenericHelper.luoHaeMerkkijonoJaVertaaYhtasuuruus(GenericHelper
                        .luoValintaperusteViite(opetuskieli, true, Valintaperustelahde.HAKUKOHTEEN_ARVO, "Opetuskieli",
                                false), se), GenericHelper.luoHaeTotuusarvo(GenericHelper.luoValintaperusteViite(
                        peruskoulunPaattotodistusVahintaanSeitseman + se, false, Valintaperustelahde.HAETTAVA_ARVO),
                        false)), GenericHelper.luoJa(GenericHelper.luoHaeMerkkijonoJaVertaaYhtasuuruus(GenericHelper
                        .luoValintaperusteViite(opetuskieli, true, Valintaperustelahde.HAKUKOHTEEN_ARVO, "Opetuskieli",
                                false), vk), GenericHelper.luoHaeTotuusarvo(GenericHelper.luoValintaperusteViite(
                        peruskoulunPaattotodistusVahintaanSeitseman + vk, false, Valintaperustelahde.HAETTAVA_ARVO),
                        false)),
                        GenericHelper.luoJa(GenericHelper.luoHaeMerkkijonoJaVertaaYhtasuuruus(GenericHelper
                                .luoValintaperusteViite(opetuskieli, true, Valintaperustelahde.HAKUKOHTEEN_ARVO,
                                        "Opetuskieli", false), fi), GenericHelper.luoHaeTotuusarvo(GenericHelper
                                .luoValintaperusteViite(lukionPaattotodistusVahintaanSeitseman + fi, false,
                                        Valintaperustelahde.HAETTAVA_ARVO), false)), GenericHelper.luoJa(GenericHelper
                                .luoHaeMerkkijonoJaVertaaYhtasuuruus(GenericHelper.luoValintaperusteViite(opetuskieli,
                                        true, Valintaperustelahde.HAKUKOHTEEN_ARVO, "Opetuskieli", false), sv),
                                GenericHelper.luoHaeTotuusarvo(
                                        GenericHelper.luoValintaperusteViite(lukionPaattotodistusVahintaanSeitseman
                                                + sv, false, Valintaperustelahde.HAETTAVA_ARVO), false)), GenericHelper
                                .luoJa(GenericHelper.luoHaeMerkkijonoJaVertaaYhtasuuruus(GenericHelper
                                        .luoValintaperusteViite(opetuskieli, true,
                                                Valintaperustelahde.HAKUKOHTEEN_ARVO, "Opetuskieli", false), se),
                                        GenericHelper.luoHaeTotuusarvo(GenericHelper.luoValintaperusteViite(
                                                lukionPaattotodistusVahintaanSeitseman + se, false,
                                                Valintaperustelahde.HAETTAVA_ARVO), false)), GenericHelper.luoJa(
                                GenericHelper.luoHaeMerkkijonoJaVertaaYhtasuuruus(GenericHelper.luoValintaperusteViite(
                                        opetuskieli, true, Valintaperustelahde.HAKUKOHTEEN_ARVO, "Opetuskieli", false),
                                        vk), GenericHelper.luoHaeTotuusarvo(
                                        GenericHelper.luoValintaperusteViite(lukionPaattotodistusVahintaanSeitseman
                                                + vk, false, Valintaperustelahde.HAETTAVA_ARVO), false)));
    }

    public static Laskentakaava luoYhdistettyPeruskaavaJaKielikoekaava(Laskentakaava peruskaava,
            Laskentakaava kielikoekaava) {
        return GenericHelper.luoLaskentakaavaJaNimettyFunktio(GenericHelper.luoSumma(peruskaava, GenericHelper
                .luoHylkaa(kielikoekaava, "Kielikoetta ei suoritettu tai kielikokeen korvaavuusehto ei täyttynyt")),
                peruskaava.getNimi() + " + " + kielikoekaava.getNimi());
    }

    public static Laskentakaava luoYhdistettyPeruskaavaJaLisapistekaava(Laskentakaava peruskaava,
            Laskentakaava lisapistekaava) {
        return GenericHelper.luoLaskentakaavaJaNimettyFunktio(GenericHelper.luoSumma(peruskaava, lisapistekaava),
                peruskaava.getNimi() + " + " + lisapistekaava.getNimi());
    }

    public static Laskentakaava luoYhdistettyPeruskaavaJaValintakoekaava(Laskentakaava peruskaava,
            Laskentakaava valintakoekaava) {
        Funktiokutsu yhdistetty = GenericHelper.luoSumma(peruskaava, valintakoekaava);
        return GenericHelper.luoLaskentakaavaJaNimettyFunktio(yhdistetty, peruskaava.getNimi() + " + "
                + valintakoekaava.getNimi());
    }

    /**
     * Ulkomailla suoritetulla pohjakoulutuksella tai oppivelvollisuuden
     * suorittamisen keskeyttäneitä hakijoita ei kutsuta
     * valinta-/kielikokeeseen.
     */
    public static Laskentakaava luoUlkomaillaSuoritettuKoulutusTaiOppivelvollisuudenSuorittaminenKeskeytynyt() {
        Funktiokutsu ulkomaillaSuoritettuKoulutus = GenericHelper.luoHaeMerkkijonoJaVertaaYhtasuuruus(GenericHelper
                .luoValintaperusteViite(PkPohjaiset.pohjakoulutusAvain, false, Valintaperustelahde.HAETTAVA_ARVO),
                PkPohjaiset.ulkomaillaSuoritettuKoulutus, Boolean.FALSE);

        Funktiokutsu oppivelvollisuudenSuorittaminenKeskeytynyt = GenericHelper.luoHaeMerkkijonoJaVertaaYhtasuuruus(
                GenericHelper.luoValintaperusteViite(PkPohjaiset.pohjakoulutusAvain, false,
                        Valintaperustelahde.HAETTAVA_ARVO), PkPohjaiset.oppivelvollisuudenSuorittaminenKeskeytynyt,
                Boolean.FALSE);

        Funktiokutsu tai = GenericHelper.luoTai(ulkomaillaSuoritettuKoulutus,
                oppivelvollisuudenSuorittaminenKeskeytynyt);

        Funktiokutsu hylkaa = GenericHelper.luoHylkaa(tai,
                "Ulkomailla suoritettu koulutus tai oppivelvollisuuden keskeytyminen");

        return GenericHelper.luoLaskentakaavaJaNimettyFunktio(hylkaa, "Ulkomailla suoritettu koulutus tai "
                + "oppivelvollisuuden suorittaminen keskeytynyt");
    }

    public static Laskentakaava luoPoikkeavanValintaryhmanLaskentakaava(Laskentakaava valintakoekaava,
            Laskentakaava kielikoekaava) {
        return GenericHelper.luoLaskentakaavaJaNimettyFunktio(GenericHelper.luoSumma(valintakoekaava, GenericHelper
                .luoHylkaa(kielikoekaava, "Kielikoetta ei suoritettu tai kielikokeen korvaavuusehto ei täyttynyt")),
                valintakoekaava.getNimi() + " + hylkäysperusteet (*)");

    }

    public static Laskentakaava urheilijaLisapisteLukuarvo(String tunniste) {

        String alaraja = "0";
        String ylaraja = "3";

        List<Arvovalikonvertteriparametri> konvs = new ArrayList<Arvovalikonvertteriparametri>();
        konvs.add(GenericHelper.luoArvovalikonvertteriparametri(alaraja, ylaraja));

        Funktiokutsu funktiokutsu = GenericHelper.luoHaeLukuarvo(GenericHelper.luoValintaperusteViite(tunniste, false,
                Valintaperustelahde.HAKUKOHTEEN_SYOTETTAVA_ARVO, "", true), 0, konvs);

        return GenericHelper.luoLaskentakaavaJaNimettyFunktio(funktiokutsu, "Urheilijalisäpisteen laskentakaava");

    }

    public static Laskentakaava luoHakutoivejarjestysTasapistekaava() {
        return YhteisetKaavat.luoHakutoivejarjestysTasapistekaava("Hakutoivejärjestystasapistetilanne, 2 aste, pk ja yo");
    }
}
