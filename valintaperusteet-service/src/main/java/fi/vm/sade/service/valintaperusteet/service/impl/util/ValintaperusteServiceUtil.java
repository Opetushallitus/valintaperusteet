package fi.vm.sade.service.valintaperusteet.service.impl.util;

import fi.vm.sade.service.valintaperusteet.model.Funktiokutsu;
import fi.vm.sade.service.valintaperusteet.model.Syoteparametri;

public abstract class ValintaperusteServiceUtil {
    public static Funktiokutsu getAinaPakollinenFunktiokutsu() {
        Funktiokutsu funktiokutsu = new Funktiokutsu();
        funktiokutsu.setFunktionimi(fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi.TOTUUSARVO);
        Syoteparametri param = new Syoteparametri();
        param.setArvo(Boolean.TRUE.toString());
        param.setAvain("totuusarvo");
        funktiokutsu.getSyoteparametrit().add(param);
        return funktiokutsu;
    }
}
