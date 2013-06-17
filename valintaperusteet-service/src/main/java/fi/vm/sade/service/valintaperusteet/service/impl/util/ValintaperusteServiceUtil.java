package fi.vm.sade.service.valintaperusteet.service.impl.util;

import fi.vm.sade.service.valintaperusteet.model.Funktiokutsu;
import fi.vm.sade.service.valintaperusteet.model.Funktionimi;
import fi.vm.sade.service.valintaperusteet.model.Syoteparametri;

/**
 * User: wuoti
 * Date: 13.5.2013
 * Time: 9.18
 */
public abstract class ValintaperusteServiceUtil {
    public static Funktiokutsu getAinaPakollinenFunktiokutsu() {
        Funktiokutsu funktiokutsu = new Funktiokutsu();
        funktiokutsu.setFunktionimi(Funktionimi.TOTUUSARVO);
        Syoteparametri param = new Syoteparametri();
        param.setArvo(Boolean.TRUE.toString());
        param.setAvain("totuusarvo");
        funktiokutsu.getSyoteparametrit().add(param);

        return funktiokutsu;
    }
}