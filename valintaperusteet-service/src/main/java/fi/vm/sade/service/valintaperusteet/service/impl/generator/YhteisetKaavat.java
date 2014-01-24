package fi.vm.sade.service.valintaperusteet.service.impl.generator;

import fi.vm.sade.service.valintaperusteet.model.Funktiokutsu;
import fi.vm.sade.service.valintaperusteet.model.FunktionArgumentti;
import fi.vm.sade.service.valintaperusteet.model.Laskentakaava;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kjsaila on 24/01/14.
 */
public class YhteisetKaavat {

    public static final int HAKUTOIVEIDEN_LKM = 10;

    public static Laskentakaava luoHakutoivejarjestysTasapistekaava(String nimi) {
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
                nimi);

    }

    public static Laskentakaava luoPKPohjaisenKoulutuksenKaikkienAineidenKeskiarvo(PkAineet pkAineet, String nimi) {
        Laskentakaava[] args = new Laskentakaava[] { pkAineet.getLaskentakaava(Aineet.aidinkieliJaKirjallisuus1),
                pkAineet.getLaskentakaava(Aineet.aidinkieliJaKirjallisuus2),
                pkAineet.getLaskentakaava(Aineet.historia), pkAineet.getLaskentakaava(Aineet.yhteiskuntaoppi),
                pkAineet.getLaskentakaava(Aineet.matematiikka), pkAineet.getLaskentakaava(Aineet.fysiikka),
                pkAineet.getLaskentakaava(Aineet.kemia), pkAineet.getLaskentakaava(Aineet.biologia),
                pkAineet.getLaskentakaava(Aineet.kuvataide), pkAineet.getLaskentakaava(Aineet.musiikki),
                pkAineet.getLaskentakaava(Aineet.maantieto), pkAineet.getLaskentakaava(PkAineet.kasityo),
                pkAineet.getLaskentakaava(PkAineet.kotitalous), pkAineet.getLaskentakaava(Aineet.liikunta),
                pkAineet.getLaskentakaava(Aineet.terveystieto), pkAineet.getLaskentakaava(Aineet.uskonto),
                pkAineet.getLaskentakaava(Aineet.a11Kieli), pkAineet.getLaskentakaava(Aineet.a12Kieli),
                pkAineet.getLaskentakaava(Aineet.a13Kieli), pkAineet.getLaskentakaava(Aineet.a21Kieli),
                pkAineet.getLaskentakaava(Aineet.a22Kieli), pkAineet.getLaskentakaava(Aineet.a23Kieli),
                pkAineet.getLaskentakaava(Aineet.b1Kieli), pkAineet.getLaskentakaava(Aineet.b21Kieli),
                pkAineet.getLaskentakaava(Aineet.b22Kieli), pkAineet.getLaskentakaava(Aineet.b23Kieli),
                pkAineet.getLaskentakaava(Aineet.b31Kieli), pkAineet.getLaskentakaava(Aineet.b32Kieli),
                pkAineet.getLaskentakaava(Aineet.b33Kieli) };

        Funktiokutsu keskiarvo = GenericHelper.luoKeskiarvo(args);
        Laskentakaava laskentakaava = GenericHelper.luoLaskentakaavaJaNimettyFunktio(keskiarvo,
                nimi);
        return laskentakaava;
    }
}
