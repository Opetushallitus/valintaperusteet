package fi.vm.sade.service.valintaperusteet.laskenta.api.tila;

/**
 * User: kwuoti
 * Date: 25.2.2013
 * Time: 8.11
 */
public class Virhetila extends Tila {

    public Virhetila(String kuvaus, VirheMetatieto metatieto) {
        super(Tilatyyppi.VIRHE);
        this.kuvaus = kuvaus;
        this.metatieto = metatieto;
    }

    public Virhetila() {
        super(Tilatyyppi.VIRHE);
    }

    private String kuvaus;
    private VirheMetatieto metatieto;

    public String getKuvaus() {
        return kuvaus;
    }

    public VirheMetatieto getMetatieto() {
        return metatieto;
    }
}
