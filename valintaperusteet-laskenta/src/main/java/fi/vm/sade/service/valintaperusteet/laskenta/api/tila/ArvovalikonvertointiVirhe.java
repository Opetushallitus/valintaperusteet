package fi.vm.sade.service.valintaperusteet.laskenta.api.tila;

import java.math.BigDecimal;

/**
 * User: wuoti
 * Date: 14.8.2013
 * Time: 15.14
 */
public class ArvovalikonvertointiVirhe extends VirheMetatieto {
    private BigDecimal konvertoitavaArvo;

    public ArvovalikonvertointiVirhe(BigDecimal konvertoitavaArvo) {
        super(VirheMetatietotyyppi.ARVOVALIKONVERTOINTI_VIRHE);
        this.konvertoitavaArvo = konvertoitavaArvo;
    }

    public ArvovalikonvertointiVirhe() {
        super(VirheMetatietotyyppi.ARVOVALIKONVERTOINTI_VIRHE);
    }
}
