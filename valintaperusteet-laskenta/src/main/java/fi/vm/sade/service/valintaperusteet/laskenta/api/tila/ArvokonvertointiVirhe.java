package fi.vm.sade.service.valintaperusteet.laskenta.api.tila;

public class ArvokonvertointiVirhe extends VirheMetatieto {
    private String konvertoitavaArvo;

    public ArvokonvertointiVirhe(String konvertoitavaArvo) {
        super(VirheMetatietotyyppi.ARVOKONVERTOINTI_VIRHE);
        this.konvertoitavaArvo = konvertoitavaArvo;
    }

    public ArvokonvertointiVirhe() {
        super(VirheMetatietotyyppi.ARVOKONVERTOINTI_VIRHE);
    }
}
