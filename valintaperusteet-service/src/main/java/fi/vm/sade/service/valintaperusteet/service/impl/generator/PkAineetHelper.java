package fi.vm.sade.service.valintaperusteet.service.impl.generator;

import fi.vm.sade.service.valintaperusteet.model.*;

/**
 * User: kkammone
 * Date: 4.3.2013
 * Time: 14:25
 */
public class PkAineetHelper {


    private PkAineetHelper() {
    }

    public static final String PK_Valinnainen1 = "2";
    public static final String PK_Valinnainen2 = "3";
    public static final String PK_etuliite = "PK_";

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
}
