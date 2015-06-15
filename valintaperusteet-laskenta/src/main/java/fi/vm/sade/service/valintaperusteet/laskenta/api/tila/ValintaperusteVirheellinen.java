package fi.vm.sade.service.valintaperusteet.laskenta.api.tila;

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
