package fi.vm.sade.service.valintaperusteet.laskenta.api.tila;

/**
 * User: wuoti
 * Date: 14.8.2013
 * Time: 15.10
 */
public abstract class ValintaperusteVirheellinen extends VirheMetatieto {
    private String valintaperustetunniste;

    public ValintaperusteVirheellinen(VirheMetatietotyyppi metatietotyyppi, String valintaperustetunniste) {
        super(metatietotyyppi);
        this.valintaperustetunniste = valintaperustetunniste;
    }

    public String getValintaperustetunniste() {
        return valintaperustetunniste;
    }
}
