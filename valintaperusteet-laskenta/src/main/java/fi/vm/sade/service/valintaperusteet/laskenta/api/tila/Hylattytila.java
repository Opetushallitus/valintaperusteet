package fi.vm.sade.service.valintaperusteet.laskenta.api.tila;

/**
 * User: kwuoti
 * Date: 25.2.2013
 * Time: 7.47
 */
public class Hylattytila extends Tila {

    public Hylattytila(String funktiokutsuOid, String kuvaus, HylattyMetatieto metatieto) {
        super(Tilatyyppi.HYLATTY);
        this.funktiokutsuOid = funktiokutsuOid;
        this.kuvaus = kuvaus;
        this.metatieto = metatieto;
    }

    private String funktiokutsuOid;
    private String kuvaus;

    private HylattyMetatieto metatieto;

    public String getFunktiokutsuOid() {
        return funktiokutsuOid;
    }

    public String getKuvaus() {
        return kuvaus;
    }

    public HylattyMetatieto getMetatieto() {
        return metatieto;
    }
}
