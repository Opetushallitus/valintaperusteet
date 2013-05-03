package fi.vm.sade.service.valintaperusteet.service.impl.generator;

import fi.vm.sade.service.valintaperusteet.model.Arvovalikonvertteriparametri;
import fi.vm.sade.service.valintaperusteet.model.Funktiokutsu;
import fi.vm.sade.service.valintaperusteet.model.Laskentakaava;
import fi.vm.sade.service.valintaperusteet.model.Valintaperustelahde;
import scala.actors.threadpool.Arrays;

/**
 * User: kwuoti
 * Date: 5.3.2013
 * Time: 12.14
 */
public class PkJaYoPohjaiset {

    public static final String tyokokemuskuukaudet = "tyokokemuskuukaudet";
    public static final String sukupuoli = "sukupuoli";
    public static final String koulutuspaikkaAmmatillisenTutkintoon = "koulutuspaikkaAmmatillisenTutkintoon";

    public static Laskentakaava luoHakutoivejarjestyspisteteytysmalli() {
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
                GenericHelper.luoArvovalikonvertteriparametri(3.0, 6.0, 1.0),
                GenericHelper.luoArvovalikonvertteriparametri(6.0, 12.0, 2.0),
                GenericHelper.luoArvovalikonvertteriparametri(12.0, 100000.0, 3.0)
        };

        Funktiokutsu f = GenericHelper.luoHaeLukuarvo(GenericHelper.luoValintaperusteViite(
                tyokokemuskuukaudet, true, false, Valintaperustelahde.HAETTAVA_ARVO), Arrays.asList(konvs));
        return GenericHelper.luoLaskentakaavaJaNimettyFunktio(f, "Työkokemuspisteytys, 2 aste, pk ja yo");
    }

    public static Laskentakaava luoSukupuolipisteytysmalli() {
        Funktiokutsu thenHaara = GenericHelper.luoLukuarvo(2.0);
        Funktiokutsu elseHaara = GenericHelper.luoLukuarvo(0.0);
        Funktiokutsu ehto = GenericHelper.luoDemografia(sukupuoli, 30.0);

        Funktiokutsu jos = GenericHelper.luoJosFunktio(ehto, thenHaara, elseHaara);
        return GenericHelper.luoLaskentakaavaJaNimettyFunktio(jos, "Sukupuolipisteytys, 2 aste, pk ja yo");
    }

    public static Laskentakaava ilmanKoulutuspaikkaaPisteytysmalli() {
        Funktiokutsu thenHaara = GenericHelper.luoLukuarvo(8.0);
        Funktiokutsu elseHaara = GenericHelper.luoLukuarvo(0.0);
        Funktiokutsu ehto = GenericHelper.luoEi(GenericHelper.luoHaeTotuusarvo(
                GenericHelper.luoValintaperusteViite(koulutuspaikkaAmmatillisenTutkintoon, false, false,
                        Valintaperustelahde.HAETTAVA_ARVO)));

        Funktiokutsu jos = GenericHelper.luoJosFunktio(ehto, thenHaara, elseHaara);

        return GenericHelper.luoLaskentakaavaJaNimettyFunktio(
                jos, "Ilman koulutuspaikkaa -pisteytys, 2 aste, pk ja yo");
    }
}
