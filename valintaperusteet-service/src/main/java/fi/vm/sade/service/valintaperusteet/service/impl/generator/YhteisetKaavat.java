package fi.vm.sade.service.valintaperusteet.service.impl.generator;

import fi.vm.sade.service.valintaperusteet.model.Funktiokutsu;
import fi.vm.sade.service.valintaperusteet.model.FunktionArgumentti;
import fi.vm.sade.service.valintaperusteet.model.Laskentakaava;

import java.util.ArrayList;
import java.util.List;

public class YhteisetKaavat {

    public static final int HAKUTOIVEIDEN_LKM = 5;

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
}
