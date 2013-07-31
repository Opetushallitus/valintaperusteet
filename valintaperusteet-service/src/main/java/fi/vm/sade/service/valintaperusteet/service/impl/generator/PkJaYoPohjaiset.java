package fi.vm.sade.service.valintaperusteet.service.impl.generator;

import fi.vm.sade.service.valintaperusteet.model.*;
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
}
