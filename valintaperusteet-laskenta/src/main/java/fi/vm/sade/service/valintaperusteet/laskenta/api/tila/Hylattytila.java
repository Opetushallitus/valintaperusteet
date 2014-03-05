package fi.vm.sade.service.valintaperusteet.laskenta.api.tila;

import java.util.Map;

/**
 * User: kwuoti
 * Date: 25.2.2013
 * Time: 7.47
 */
public class Hylattytila extends Tila {

    public Hylattytila() {
        super(Tilatyyppi.HYLATTY);
    }

    public Hylattytila(Map<String,String> kuvaus, HylattyMetatieto metatieto) {
        super(Tilatyyppi.HYLATTY);
        this.kuvaus = kuvaus;
        this.metatieto = metatieto;
    }

    private Map<String,String> kuvaus;

    private HylattyMetatieto metatieto;

    public Map<String,String> getKuvaus() {
        return kuvaus;
    }

    public HylattyMetatieto getMetatieto() {
        return metatieto;
    }
}
