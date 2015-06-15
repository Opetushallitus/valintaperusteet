package fi.vm.sade.service.valintaperusteet.laskenta.api.tila;

public class SyotettavaArvoMerkitsemattaVirhe extends VirheMetatieto {
    public SyotettavaArvoMerkitsemattaVirhe(String valintaperustetunniste) {
        super(VirheMetatietotyyppi.SYOTETTAVA_ARVO_MERKITSEMATTA);
        this.valintaperustetunniste = valintaperustetunniste;
    }

    public SyotettavaArvoMerkitsemattaVirhe() {
        super(VirheMetatietotyyppi.SYOTETTAVA_ARVO_MERKITSEMATTA);
    }

    private String valintaperustetunniste;

    public String getValintaperustetunniste() {
        return valintaperustetunniste;
    }
}
