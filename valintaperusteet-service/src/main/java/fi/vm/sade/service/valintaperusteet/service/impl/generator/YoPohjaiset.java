package fi.vm.sade.service.valintaperusteet.service.impl.generator;

import fi.vm.sade.service.valintaperusteet.model.Funktiokutsu;
import fi.vm.sade.service.valintaperusteet.model.Laskentakaava;

/**
 * User: wuoti
 * Date: 15.5.2013
 * Time: 14.57
 */
public class YoPohjaiset {
    public static Laskentakaava luoToisenAsteenYlioppilaspohjainenPeruskaava(
            Laskentakaava hakutoivejarjestyspisteytysmalli,
            Laskentakaava tyokokemuspisteytysmalli, Laskentakaava sukupuolipisteytysmalli,
            Laskentakaava yleinenkoulumenestyspisteytysmalli) {
        Funktiokutsu summa = GenericHelper.luoSumma(hakutoivejarjestyspisteytysmalli, tyokokemuspisteytysmalli,
                sukupuolipisteytysmalli,
                yleinenkoulumenestyspisteytysmalli);

        return GenericHelper.luoLaskentakaavaJaNimettyFunktio(summa,
                "2. asteen ylioppilaspohjainen peruskaava");
    }
}
