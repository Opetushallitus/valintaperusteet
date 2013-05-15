package fi.vm.sade.service.valintaperusteet.service.impl.generator;

import fi.vm.sade.service.valintaperusteet.model.Funktiokutsu;
import fi.vm.sade.service.valintaperusteet.model.Laskentakaava;
import fi.vm.sade.service.valintaperusteet.model.Valintaperustelahde;

/**
 * User: wuoti
 * Date: 15.5.2013
 * Time: 10.27
 */
public class YoAineetHelper {

    public final static String LK_ETULIITE = "LK_";

    public static Laskentakaava luoYOAine(String aineTunnus, String nimi) {
        Funktiokutsu aine = GenericHelper.luoHaeLukuarvo(
                GenericHelper.luoValintaperusteViite(LK_ETULIITE + aineTunnus, true, false,
                        Valintaperustelahde.HAETTAVA_ARVO));

        Laskentakaava laskentakaava = GenericHelper.luoLaskentakaavaJaNimettyFunktio(aine, nimi);
        return laskentakaava;
    }

    public static Laskentakaava luoYOPohjaisenKoulutuksenPaattotodistuksenKeskiarvo(Laskentakaava... args) {
        Funktiokutsu keskiarvo = GenericHelper.luoKeskiarvo(args);
        Laskentakaava laskentakaava = GenericHelper.luoLaskentakaavaJaNimettyFunktio(
                keskiarvo, "Päättötodistuksen keskiarvo, LK");
        return laskentakaava;
    }
}
