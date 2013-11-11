package fi.vm.sade.service.valintaperusteet.laskenta.api.tila;

/**
 * User: wuoti
 * Date: 14.8.2013
 * Time: 15.13
 */
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
