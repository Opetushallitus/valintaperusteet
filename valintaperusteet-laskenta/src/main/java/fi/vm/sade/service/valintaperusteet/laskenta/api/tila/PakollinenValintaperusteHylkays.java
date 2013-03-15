package fi.vm.sade.service.valintaperusteet.laskenta.api.tila;

/**
 * User: kwuoti
 * Date: 25.2.2013
 * Time: 8.35
 */
public class PakollinenValintaperusteHylkays extends HylattyMetatieto {
    public PakollinenValintaperusteHylkays(String valintaperustetunniste) {
        super(Hylattymetatietotyyppi.PAKOLLINEN_VALINTAPERUSTE_HYLKAYS);
        this.valintaperustetunniste = valintaperustetunniste;
    }

    private String valintaperustetunniste;

    public String getValintaperustetunniste() {
        return valintaperustetunniste;
    }
}
