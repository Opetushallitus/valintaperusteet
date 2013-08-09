package fi.vm.sade.service.valintaperusteet.laskenta.api.tila;

/**
 * User: kwuoti
 * Date: 25.2.2013
 * Time: 8.35
 */
public class SyotettavaArvoMerkitsemattaHylkays extends HylattyMetatieto {
    public SyotettavaArvoMerkitsemattaHylkays(String valintaperustetunniste) {
        super(Hylattymetatietotyyppi.SYOTETTAVA_ARVO_MERKITSEMATTA);
        this.valintaperustetunniste = valintaperustetunniste;
    }

    private String valintaperustetunniste;

    public String getValintaperustetunniste() {
        return valintaperustetunniste;
    }
}
