package fi.vm.sade.service.valintaperusteet.laskenta.api.tila;

/**
 * User: kwuoti
 * Date: 25.2.2013
 * Time: 8.35
 */
public class SyotettavaArvoMerkitsemattaVirhe extends VirheMetatieto {
    public SyotettavaArvoMerkitsemattaVirhe(String valintaperustetunniste) {
        super(VirheMetatietotyyppi.SYOTETTAVA_ARVO_MERKITSEMATTA);
        this.valintaperustetunniste = valintaperustetunniste;
    }

    private String valintaperustetunniste;

    public String getValintaperustetunniste() {
        return valintaperustetunniste;
    }
}
