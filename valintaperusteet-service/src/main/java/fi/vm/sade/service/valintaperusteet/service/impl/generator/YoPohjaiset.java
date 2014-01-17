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
            Laskentakaava yleinenkoulumenestyspisteytysmalli, Laskentakaava urheilijanLisapiste) {
        Funktiokutsu summa = GenericHelper.luoSumma(hakutoivejarjestyspisteytysmalli, tyokokemuspisteytysmalli,
                sukupuolipisteytysmalli,
                yleinenkoulumenestyspisteytysmalli, urheilijanLisapiste);

        return GenericHelper.luoLaskentakaavaJaNimettyFunktio(summa,
                "2. asteen ylioppilaspohjainen peruskaava");
    }

    public static Laskentakaava luoYOPohjaisenKoulutuksenPaattotodistuksenKeskiarvo(YoAineet yoAineet) {
        Laskentakaava[] args = new Laskentakaava[]{
                yoAineet.getLaskentakaava(Aineet.aidinkieliJaKirjallisuus1),
                yoAineet.getLaskentakaava(Aineet.aidinkieliJaKirjallisuus2),
                yoAineet.getLaskentakaava(Aineet.historia),
                yoAineet.getLaskentakaava(Aineet.yhteiskuntaoppi),
                yoAineet.getLaskentakaava(Aineet.matematiikka),
                yoAineet.getLaskentakaava(Aineet.fysiikka),
                yoAineet.getLaskentakaava(Aineet.kemia),
                yoAineet.getLaskentakaava(Aineet.biologia),
                yoAineet.getLaskentakaava(Aineet.kuvataide),
                yoAineet.getLaskentakaava(Aineet.musiikki),
                yoAineet.getLaskentakaava(Aineet.maantieto),
                yoAineet.getLaskentakaava(YoAineet.filosofia),
                yoAineet.getLaskentakaava(YoAineet.psykologia),
                yoAineet.getLaskentakaava(Aineet.liikunta),
                yoAineet.getLaskentakaava(Aineet.terveystieto),
                yoAineet.getLaskentakaava(Aineet.uskonto),
                yoAineet.getLaskentakaava(Aineet.a11Kieli),
                yoAineet.getLaskentakaava(Aineet.a12Kieli),
                yoAineet.getLaskentakaava(Aineet.a13Kieli),
                yoAineet.getLaskentakaava(Aineet.a21Kieli),
                yoAineet.getLaskentakaava(Aineet.a22Kieli),
                yoAineet.getLaskentakaava(Aineet.a23Kieli),
                yoAineet.getLaskentakaava(Aineet.b1Kieli),
                yoAineet.getLaskentakaava(Aineet.b21Kieli),
                yoAineet.getLaskentakaava(Aineet.b22Kieli),
                yoAineet.getLaskentakaava(Aineet.b23Kieli),
                yoAineet.getLaskentakaava(Aineet.b31Kieli),
                yoAineet.getLaskentakaava(Aineet.b32Kieli),
                yoAineet.getLaskentakaava(Aineet.b33Kieli)
        };

        Funktiokutsu keskiarvo = GenericHelper.luoKeskiarvo(args);
        Laskentakaava laskentakaava = GenericHelper.luoLaskentakaavaJaNimettyFunktio(
                keskiarvo, "Päättötodistuksen keskiarvo, LK");
        return laskentakaava;
    }
}
