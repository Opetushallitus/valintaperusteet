package fi.vm.sade.service.valintaperusteet.laskenta.api.tila;

/**
 * User: kwuoti
 * Date: 25.2.2013
 * Time: 7.47
 */
public class Hylattytila extends Tila {

    public Hylattytila() {
        super(Tilatyyppi.HYLATTY);
    }

    public Hylattytila(String kuvaus, HylattyMetatieto metatieto) {
        super(Tilatyyppi.HYLATTY);
        this.kuvaus = kuvaus;
        this.metatieto = metatieto;
    }

    private String kuvaus;

    private HylattyMetatieto metatieto;

    public String getKuvaus() {
        return kuvaus;
    }

    public HylattyMetatieto getMetatieto() {
        return metatieto;
    }
}
