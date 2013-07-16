package fi.vm.sade.service.valintaperusteet.service.validointi.virhe;

import java.math.BigDecimal;

import org.codehaus.jackson.map.annotate.JsonView;

import fi.vm.sade.service.valintaperusteet.model.JsonViews;

/**
 * User: kwuoti Date: 5.3.2013 Time: 9.09
 */
public class ProsenttiosuusEpavalidiVirhe extends Validointivirhe {

    @JsonView(JsonViews.Basic.class)
    private BigDecimal prosenttiosuus;

    public ProsenttiosuusEpavalidiVirhe(String virheviesti, BigDecimal prosenttiosuus) {
        super(Virhetyyppi.PROSENTTIOSUUS_EPAVALIDI, virheviesti);
        this.prosenttiosuus = prosenttiosuus;
    }

    public BigDecimal getProsenttiosuus() {
        return prosenttiosuus;
    }
}
