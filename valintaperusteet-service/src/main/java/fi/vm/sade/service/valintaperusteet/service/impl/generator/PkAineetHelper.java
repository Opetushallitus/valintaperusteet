package fi.vm.sade.service.valintaperusteet.service.impl.generator;

import fi.vm.sade.service.valintaperusteet.model.*;

/**
 * Created with IntelliJ IDEA.
 * User: kkammone
 * Date: 4.3.2013
 * Time: 14:25
 * To change this template use File | Settings | File Templates.
 */
public class PkAineetHelper {

    private PkAineetHelper() {
    }

    public static final String PK_Valinnainen1 = "2";
    public static final String PK_Valinnainen2 = "3";
    public static final String PK_etuliite = "PK_";

    public static final String aidinkieliJaKirjallisuus = "AI";
    public static final String historia = "HI";
    public static final String yhteiskuntaoppi = "YH";
    public static final String matematiikka = "MA";
    public static final String kemia = "KE";
    public static final String biologia = "BI";
    public static final String kuvataide = "KU";
    public static final String musiikki = "MU";
    public static final String maantieto = "GE";
    public static final String kasityo = "KS";
    public static final String kotitalous = "KO";
    public static final String liikunta = "LI";
    public static final String terveystieto = "TE";
    public static final String a1Kieli = "A1";
    public static final String a2Kieli = "A2";
    public static final String b1Kieli = "B1";
    public static final String b2Kieli = "B2";
    public static final String b3Kieli = "B3";


    public static Laskentakaava luoPKAine(String aineTunnus, String nimi) {
        Funktiokutsu aine = GenericHelper.luoHaeLukuarvo(
                GenericHelper.luoValintaperusteViite(PK_etuliite + aineTunnus, true, false,
                Valintaperustelahde.HAETTAVA_ARVO));
        Funktiokutsu aineValinnainen1 = GenericHelper.luoHaeLukuarvo(
                GenericHelper.luoValintaperusteViite(PK_etuliite + aineTunnus + PK_Valinnainen1,
                true, false, Valintaperustelahde.HAETTAVA_ARVO));
        Funktiokutsu aineValinnainen2 = GenericHelper.luoHaeLukuarvo(
                GenericHelper.luoValintaperusteViite(PK_etuliite + aineTunnus + PK_Valinnainen2,
                true, false, Valintaperustelahde.HAETTAVA_ARVO));

        Funktiokutsu valinnainenKeskiarvo = GenericHelper.luoKeskiarvo(aineValinnainen1, aineValinnainen2);
        Funktiokutsu keskiarvo = GenericHelper.luoKeskiarvo(aine, valinnainenKeskiarvo);

        Laskentakaava laskentakaava = GenericHelper.luoLaskentakaavaJaNimettyFunktio(keskiarvo, nimi);
        return laskentakaava;
    }

    public static Laskentakaava luoPainotettavatKeskiarvotLaskentakaava(Laskentakaava... args) {
        Funktiokutsu kolmeParasta = GenericHelper.nParastaKeskiarvo(3, args);

        Funktiokutsu konvertteri = new Funktiokutsu();
        konvertteri.setFunktionimi(Funktionimi.KONVERTOILUKUARVO);

        Funktioargumentti funk = new Funktioargumentti();
        funk.setFunktiokutsuChild(kolmeParasta);
        funk.setIndeksi(1);

        konvertteri.getFunktioargumentit().add(funk);
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

    public static Laskentakaava luoPKPohjaisenKoulutuksenLukuaineidenKeskiarvo(Laskentakaava... args) {
        Funktiokutsu keskiarvo = GenericHelper.luoKeskiarvo(args);
        Laskentakaava laskentakaava = GenericHelper.luoLaskentakaavaJaNimettyFunktio(
                keskiarvo, "Lukuaineiden keskiarvo, PK");
        return laskentakaava;
    }

    public static Laskentakaava luoYleinenKoulumenestysLaskentakaava(Laskentakaava laskentakaava) {
        Funktiokutsu konvertteri = new Funktiokutsu();
        konvertteri.setFunktionimi(Funktionimi.KONVERTOILUKUARVO);
        Funktioargumentti funk = new Funktioargumentti();
        funk.setLaskentakaavaChild(laskentakaava);
        funk.setIndeksi(1);

        konvertteri.getFunktioargumentit().add(funk);
        konvertteri.getArvovalikonvertteriparametrit().add(GenericHelper.luoArvovalikonvertteriparametri(5.50, 5.75, 1));
        konvertteri.getArvovalikonvertteriparametrit().add(GenericHelper.luoArvovalikonvertteriparametri(5.75, 6.00, 2));
        konvertteri.getArvovalikonvertteriparametrit().add(GenericHelper.luoArvovalikonvertteriparametri(6.00, 6.25, 3));
        konvertteri.getArvovalikonvertteriparametrit().add(GenericHelper.luoArvovalikonvertteriparametri(6.25, 6.50, 4));
        konvertteri.getArvovalikonvertteriparametrit().add(GenericHelper.luoArvovalikonvertteriparametri(6.50, 6.75, 5));
        konvertteri.getArvovalikonvertteriparametrit().add(GenericHelper.luoArvovalikonvertteriparametri(6.75, 7.00, 6));
        konvertteri.getArvovalikonvertteriparametrit().add(GenericHelper.luoArvovalikonvertteriparametri(7.00, 7.25, 7));
        konvertteri.getArvovalikonvertteriparametrit().add(GenericHelper.luoArvovalikonvertteriparametri(7.25, 7.50, 8));
        konvertteri.getArvovalikonvertteriparametrit().add(GenericHelper.luoArvovalikonvertteriparametri(7.50, 7.75, 9));
        konvertteri.getArvovalikonvertteriparametrit().add(GenericHelper.luoArvovalikonvertteriparametri(7.75, 8.00, 10));
        konvertteri.getArvovalikonvertteriparametrit().add(GenericHelper.luoArvovalikonvertteriparametri(8.00, 8.25, 11));
        konvertteri.getArvovalikonvertteriparametrit().add(GenericHelper.luoArvovalikonvertteriparametri(8.25, 8.50, 12));
        konvertteri.getArvovalikonvertteriparametrit().add(GenericHelper.luoArvovalikonvertteriparametri(8.50, 8.75, 13));
        konvertteri.getArvovalikonvertteriparametrit().add(GenericHelper.luoArvovalikonvertteriparametri(8.75, 9.00, 14));
        konvertteri.getArvovalikonvertteriparametrit().add(GenericHelper.luoArvovalikonvertteriparametri(9.00, 9.25, 15));
        konvertteri.getArvovalikonvertteriparametrit().add(GenericHelper.luoArvovalikonvertteriparametri(9.25, 10.1, 16));

        Laskentakaava palautettavaLaskentakaava = GenericHelper.luoLaskentakaavaJaNimettyFunktio(konvertteri,
                "Yleinen koulumenestys pisteytysmalli, PK");
        return palautettavaLaskentakaava;
    }

}
