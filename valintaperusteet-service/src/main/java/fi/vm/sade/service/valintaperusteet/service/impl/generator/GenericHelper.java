package fi.vm.sade.service.valintaperusteet.service.impl.generator;

import fi.vm.sade.service.valintaperusteet.model.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: kkammone
 * Date: 4.3.2013
 * Time: 14:25
 * To change this template use File | Settings | File Templates.
 */
public class GenericHelper {

    public static final String aidinkieliJaKirjallisuus1 = "AI_1";
    public static final String aidinkieliJaKirjallisuus2 = "AI_2";

    public static final String historia = "HI";
    public static final String yhteiskuntaoppi = "YH";
    public static final String matematiikka = "MA";
    public static final String fysiikka = "FY";
    public static final String kemia = "KE";
    public static final String biologia = "BI";
    public static final String kuvataide = "KU";
    public static final String musiikki = "MU";
    public static final String maantieto = "GE";
    public static final String kasityo = "KS";
    public static final String kotitalous = "KO";
    public static final String liikunta = "LI";
    public static final String terveystieto = "TE";
    public static final String uskonto = "KT";

    // A1-kieliä voi olla kolme
    public static final String a11Kieli = "A1_1";
    public static final String a12Kieli = "A1_2";
    public static final String a13Kieli = "A1_3";

    // A2-kieliä voi olla kolme
    public static final String a21Kieli = "A2_1";
    public static final String a22Kieli = "A2_2";
    public static final String a23Kieli = "A2_3";

    // B1-kieliä voi olla yksi
    public static final String b1Kieli = "B1_1";

    // B2-kieliä voi olla kolme
    public static final String b21Kieli = "B2_1";
    public static final String b22Kieli = "B2_2";
    public static final String b23Kieli = "B2_3";

    // B3-kieliä voi olla kolme
    public static final String b31Kieli = "B3_1";
    public static final String b32Kieli = "B3_2";
    public static final String b33Kieli = "B3_3";

    private GenericHelper() {
    }

    public static List<Funktioargumentti> luoFunktioargumentit(FunktionArgumentti... args) {
        List<Funktioargumentti> fargs = new ArrayList<Funktioargumentti>();

        for (int i = 0; i < args.length; ++i) {
            FunktionArgumentti k = args[i];
            Funktioargumentti arg = new Funktioargumentti();

            if (k instanceof Funktiokutsu) {
                arg.setFunktiokutsuChild((Funktiokutsu) k);
            } else if (k instanceof Laskentakaava) {
                arg.setLaskentakaavaChild((Laskentakaava) k);
            }

            arg.setIndeksi(i + 1);
            fargs.add(arg);
        }

        return fargs;
    }

    public static Funktiokutsu luoKeskiarvo(FunktionArgumentti... args) {
        Funktiokutsu funktiokutsu = new Funktiokutsu();
        funktiokutsu.setFunktionimi(Funktionimi.KESKIARVO);
        funktiokutsu.getFunktioargumentit().addAll(luoFunktioargumentit(args));
        return funktiokutsu;
    }


    public static Funktiokutsu nParastaKeskiarvo(int n, FunktionArgumentti... args) {
        Funktiokutsu nparasta = new Funktiokutsu();
        nparasta.setFunktionimi(Funktionimi.KESKIARVONPARASTA);
        Syoteparametri s = new Syoteparametri();
        s.setAvain("n");
        s.setArvo(String.valueOf(n));
        nparasta.getSyoteparametrit().add(s);
        nparasta.getFunktioargumentit().addAll(luoFunktioargumentit(args));
        return nparasta;
    }


    public static Funktiokutsu luoLukuarvo(double arvo) {
        Funktiokutsu funktiokutsu = new Funktiokutsu();
        funktiokutsu.setFunktionimi(Funktionimi.LUKUARVO);
        Syoteparametri syoteparametri = new Syoteparametri();
        syoteparametri.setAvain("luku");
        syoteparametri.setArvo(String.valueOf(arvo));
        funktiokutsu.getSyoteparametrit().add(syoteparametri);
        return funktiokutsu;
    }

    public static ValintaperusteViite luoValintaperusteViite(String tunniste, boolean onPakollinen, boolean onPaasykoe, Valintaperustelahde lahde) {
        return luoValintaperusteViite(tunniste, onPakollinen, onPaasykoe, lahde, "");
    }

    public static ValintaperusteViite luoValintaperusteViite(String tunniste, boolean onPakollinen, boolean onPaasykoe,
                                                             Valintaperustelahde lahde, String kuvaus) {
        ValintaperusteViite vp = new ValintaperusteViite();
        vp.setTunniste(tunniste);
        vp.setOnPakollinen(onPakollinen);
        vp.setOnPaasykoe(onPaasykoe);
        vp.setLahde(lahde);
        vp.setKuvaus(kuvaus);
        return vp;
    }


    public static Funktiokutsu luoHaeLukuarvo(ValintaperusteViite vp) {
        Funktiokutsu funktiokutsu = new Funktiokutsu();
        funktiokutsu.setFunktionimi(Funktionimi.HAELUKUARVO);
        funktiokutsu.setValintaperuste(vp);
        return funktiokutsu;
    }

    public static Syoteparametri luoSyoteparametri(String avain, String arvo) {
        Syoteparametri sp = new Syoteparametri();
        sp.setAvain(avain);
        sp.setArvo(arvo);
        return sp;
    }

    public static Funktiokutsu luoHaeLukuarvo(ValintaperusteViite vp, double oletusarvo) {
        Funktiokutsu funktiokutsu = luoHaeLukuarvo(vp);
        funktiokutsu.getSyoteparametrit().add(luoSyoteparametri("oletusarvo", String.valueOf(oletusarvo)));
        return funktiokutsu;
    }

    public static Funktiokutsu luoEnsimmainenHakutoive() {
        Funktiokutsu hakutoive = new Funktiokutsu();
        hakutoive.setFunktionimi(Funktionimi.HAKUTOIVE);
        Syoteparametri a = new Syoteparametri();
        a.setAvain("n");
        a.setArvo("1");
        hakutoive.getSyoteparametrit().add(a);
        return hakutoive;
    }

    public static Funktiokutsu luoJosFunktio(Funktiokutsu ehto, Funktiokutsu totta, Funktiokutsu vale) {
        Funktiokutsu f = new Funktiokutsu();
        f.setFunktionimi(Funktionimi.JOS);
        Funktioargumentti josArgumentti = new Funktioargumentti();
        josArgumentti.setFunktiokutsuChild(ehto);
        josArgumentti.setIndeksi(1);
        f.getFunktioargumentit().add(josArgumentti);

        Funktioargumentti tottaArgumentti = new Funktioargumentti();
        tottaArgumentti.setFunktiokutsuChild(totta);
        tottaArgumentti.setIndeksi(2);
        f.getFunktioargumentit().add(tottaArgumentti);

        Funktioargumentti valettaArgumentti = new Funktioargumentti();
        valettaArgumentti.setFunktiokutsuChild(vale);
        valettaArgumentti.setIndeksi(3);
        f.getFunktioargumentit().add(valettaArgumentti);

        return f;
    }

    public static Funktiokutsu luoNimettyFunktio(Funktiokutsu funktiokutsu, String nimi) {
        Funktiokutsu nimettyFunktio = new Funktiokutsu();

        switch (funktiokutsu.getFunktionimi().getTyyppi()) {

            case LUKUARVOFUNKTIO:
                nimettyFunktio.setFunktionimi(Funktionimi.NIMETTYLUKUARVO);
                break;
            case TOTUUSARVOFUNKTIO:
                nimettyFunktio.setFunktionimi(Funktionimi.NIMETTYTOTUUSARVO);
                break;
            case EI_VALIDI:
                throw new RuntimeException("Funktiokutsu ei ole validi");
        }

        Syoteparametri s = new Syoteparametri();
        s.setAvain("nimi");
        s.setArvo(nimi);
        nimettyFunktio.getSyoteparametrit().add(s);
        Funktioargumentti funktioargumentti = new Funktioargumentti();
        funktioargumentti.setFunktiokutsuChild(funktiokutsu);
        funktioargumentti.setIndeksi(1);
        nimettyFunktio.getFunktioargumentit().add(funktioargumentti);
        return nimettyFunktio;
    }

    public static Laskentakaava luoLaskentakaava(Funktiokutsu funktiokutsu, String nimi) {
        Laskentakaava laskentakaava = new Laskentakaava();
        laskentakaava.setNimi(nimi);
        laskentakaava.setKuvaus(nimi);
        laskentakaava.setFunktiokutsu(funktiokutsu);
        laskentakaava.setOnLuonnos(false);
        return laskentakaava;
    }

    public static Laskentakaava luoLaskentakaavaJaNimettyFunktio(Funktiokutsu funktiokutsu, String nimi) {
        Funktiokutsu nimetty = luoNimettyFunktio(funktiokutsu, nimi);
        return luoLaskentakaava(nimetty, nimi);
    }

    public static Arvovalikonvertteriparametri luoArvovalikonvertteriparametri(double min, double max,
                                                                               double paluuarvo) {
        Arvovalikonvertteriparametri a = new Arvovalikonvertteriparametri();
        a.setMaxValue(max);
        a.setMinValue(min);
        a.setPalautaHaettuArvo(false);
        a.setHylkaysperuste(false);
        a.setPaluuarvo(String.valueOf(paluuarvo));
        return a;
    }

    public static Arvokonvertteriparametri luoArvokonvertteriparametri(String arvo, String paluuarvo,
                                                                       boolean hylkaysperuste) {
        Arvokonvertteriparametri a = new Arvokonvertteriparametri();
        a.setArvo(arvo);
        a.setPaluuarvo(paluuarvo);
        a.setHylkaysperuste(hylkaysperuste);

        return a;
    }

    public static Funktiokutsu luoHaeMerkkijonoJaKonvertoiLukuarvoksi(ValintaperusteViite vp,
                                                                      Collection<Arvokonvertteriparametri> arvokonvertterit) {
        Funktiokutsu funktiokutsu = new Funktiokutsu();
        funktiokutsu.setFunktionimi(Funktionimi.HAEMERKKIJONOJAKONVERTOILUKUARVOKSI);
        funktiokutsu.getArvokonvertteriparametrit().addAll(arvokonvertterit);
        funktiokutsu.setValintaperuste(vp);

        return funktiokutsu;
    }

    public static Funktiokutsu luoDemografia(String tunniste, double prosenttiosuus) {
        Funktiokutsu funktiokutsu = new Funktiokutsu();
        funktiokutsu.setFunktionimi(Funktionimi.DEMOGRAFIA);
        funktiokutsu.getSyoteparametrit().add(luoSyoteparametri("tunniste", tunniste));
        funktiokutsu.getSyoteparametrit().add(luoSyoteparametri("prosenttiosuus", String.valueOf(prosenttiosuus)));

        return funktiokutsu;
    }


    public static Funktiokutsu luoHaeTotuusarvo(ValintaperusteViite vp) {
        Funktiokutsu funktiokutsu = new Funktiokutsu();
        funktiokutsu.setFunktionimi(Funktionimi.HAETOTUUSARVO);
        funktiokutsu.setValintaperuste(vp);

        return funktiokutsu;
    }

    public static Funktiokutsu luoHaeTotuusarvo(ValintaperusteViite vp, boolean oletusarvo) {
        Funktiokutsu funktiokutsu = luoHaeTotuusarvo(vp);
        funktiokutsu.getSyoteparametrit().add(luoSyoteparametri("oletusarvo", String.valueOf(oletusarvo)));

        return funktiokutsu;
    }

    public static Funktiokutsu luoEi(Funktiokutsu arg) {
        Funktiokutsu ei = new Funktiokutsu();
        ei.setFunktionimi(Funktionimi.EI);
        ei.getFunktioargumentit().addAll(luoFunktioargumentit(arg));

        return ei;
    }

    public static Funktiokutsu luoHaeLukuarvo(ValintaperusteViite vp, Collection<Arvovalikonvertteriparametri> konvs) {
        Funktiokutsu funktiokutsu = luoHaeLukuarvo(vp);
        funktiokutsu.getArvovalikonvertteriparametrit().addAll(konvs);
        return funktiokutsu;
    }

    public static Funktiokutsu luoHaeLukuarvo(ValintaperusteViite vp, double oletusarvo,
                                              Collection<Arvovalikonvertteriparametri> konvs) {
        Funktiokutsu funktiokutsu = luoHaeLukuarvo(vp, oletusarvo);
        funktiokutsu.getArvovalikonvertteriparametrit().addAll(konvs);
        return funktiokutsu;
    }

    public static Funktiokutsu luoTai(FunktionArgumentti... args) {
        Funktiokutsu funktiokutsu = new Funktiokutsu();
        funktiokutsu.setFunktionimi(Funktionimi.TAI);

        funktiokutsu.getFunktioargumentit().addAll(luoFunktioargumentit(args));
        return funktiokutsu;
    }

    public static Funktiokutsu luoHaeMerkkijonoJaKonvertoiTotuusarvoksi(ValintaperusteViite vp,
                                                                        boolean oletusarvo,
                                                                        Collection<Arvokonvertteriparametri> konvs) {
        Funktiokutsu funktiokutsu = luoHaeMerkkijonoJaKonvertoiTotuusarvoksi(vp, konvs);
        funktiokutsu.getSyoteparametrit().add(luoSyoteparametri("oletusarvo", String.valueOf(oletusarvo)));

        return funktiokutsu;
    }

    public static Funktiokutsu luoHaeMerkkijonoJaKonvertoiTotuusarvoksi(ValintaperusteViite vp,
                                                                        Collection<Arvokonvertteriparametri> konvs) {
        Funktiokutsu funktiokutsu = new Funktiokutsu();
        funktiokutsu.setFunktionimi(Funktionimi.HAEMERKKIJONOJAKONVERTOITOTUUSARVOKSI);
        funktiokutsu.setValintaperuste(vp);
        funktiokutsu.getArvokonvertteriparametrit().addAll(konvs);
        return funktiokutsu;
    }

    public static Funktiokutsu luoYhtasuuri(Funktiokutsu f1, Funktiokutsu f2) {
        Funktiokutsu f = new Funktiokutsu();
        f.setFunktionimi(Funktionimi.YHTASUURI);
        f.getFunktioargumentit().addAll(luoFunktioargumentit(f1, f2));
        return f;
    }

    public static Funktiokutsu luoJa(Funktiokutsu... args) {
        Funktiokutsu f = new Funktiokutsu();
        f.setFunktionimi(Funktionimi.JA);
        f.getFunktioargumentit().addAll(luoFunktioargumentit(args));
        return f;
    }

    public static Funktiokutsu luoSumma(FunktionArgumentti... args) {
        Funktiokutsu f = new Funktiokutsu();
        f.setFunktionimi(Funktionimi.SUMMA);
        f.getFunktioargumentit().addAll(luoFunktioargumentit(args));

        return f;
    }
}
