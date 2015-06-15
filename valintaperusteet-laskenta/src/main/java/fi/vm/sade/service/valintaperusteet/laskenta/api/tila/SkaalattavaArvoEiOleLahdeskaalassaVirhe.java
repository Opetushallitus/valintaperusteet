package fi.vm.sade.service.valintaperusteet.laskenta.api.tila;

import java.math.BigDecimal;

public class SkaalattavaArvoEiOleLahdeskaalassaVirhe extends VirheMetatieto {
    private BigDecimal skaalattavaArvo;
    private BigDecimal lahdeskaalaMin;
    private BigDecimal lahdeskaalaMax;


    public SkaalattavaArvoEiOleLahdeskaalassaVirhe(BigDecimal skaalattavaArvo,
                                                   BigDecimal lahdeskaalaMin,
                                                   BigDecimal lahdeskaalaMax) {
        super(VirheMetatietotyyppi.SKAALATTAVA_ARVO_EI_OLE_LAHDESKAALASSA);

        this.skaalattavaArvo = skaalattavaArvo;
        this.lahdeskaalaMin = lahdeskaalaMin;
        this.lahdeskaalaMax = lahdeskaalaMax;
    }

    public SkaalattavaArvoEiOleLahdeskaalassaVirhe() {
        super(VirheMetatietotyyppi.SKAALATTAVA_ARVO_EI_OLE_LAHDESKAALASSA);
    }

    public BigDecimal getSkaalattavaArvo() {
        return skaalattavaArvo;
    }

    public BigDecimal getLahdeskaalaMin() {
        return lahdeskaalaMin;
    }

    public BigDecimal getLahdeskaalaMax() {
        return lahdeskaalaMax;
    }
}
