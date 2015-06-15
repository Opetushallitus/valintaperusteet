package fi.vm.sade.service.valintaperusteet.laskenta.api.tila;

public class Arvokonvertterihylkays extends HylattyMetatieto {

    public Arvokonvertterihylkays(String arvo) {
        super(Hylattymetatietotyyppi.ARVOKONVERTTERIHYLKAYS);
        this.arvo = arvo;
    }

    public Arvokonvertterihylkays() {
        super(Hylattymetatietotyyppi.ARVOKONVERTTERIHYLKAYS);
    }

    private String arvo;

    public String getArvo() {
        return arvo;
    }
}
