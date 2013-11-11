package fi.vm.sade.service.valintaperusteet.laskenta.api.tila;

/**
 * User: kwuoti
 * Date: 25.2.2013
 * Time: 8.35
 */
public class EiOsallistunutHylkays extends HylattyMetatieto {
    public EiOsallistunutHylkays(String valintaperustetunniste) {
        super(Hylattymetatietotyyppi.EI_OSALLISTUNUT_HYLKAYS);
        this.valintaperustetunniste = valintaperustetunniste;
    }

    public EiOsallistunutHylkays() {
        super(Hylattymetatietotyyppi.EI_OSALLISTUNUT_HYLKAYS);
    }

    private String valintaperustetunniste;

    public String getValintaperustetunniste() {
        return valintaperustetunniste;
    }
}
