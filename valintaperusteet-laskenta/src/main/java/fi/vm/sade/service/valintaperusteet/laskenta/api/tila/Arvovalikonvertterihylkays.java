package fi.vm.sade.service.valintaperusteet.laskenta.api.tila;

/**
 * User: kwuoti
 * Date: 25.2.2013
 * Time: 8.28
 */
public class Arvovalikonvertterihylkays extends HylattyMetatieto {
    public Arvovalikonvertterihylkays(double arvo, double arvovaliMin, double arvovaliMax) {
        super(Hylattymetatietotyyppi.ARVOVALIKONVERTTERIHYLKAYS);
        this.arvo = arvo;
        this.arvovaliMin = arvovaliMin;
        this.arvovaliMax = arvovaliMax;
    }

    private double arvo;
    private double arvovaliMin;
    private double arvovaliMax;

    public double getArvo() {
        return arvo;
    }

    public double getArvovaliMin() {
        return arvovaliMin;
    }

    public double getArvovaliMax() {
        return arvovaliMax;
    }
}
