package fi.vm.sade.service.valintaperusteet.service.validointi.virhe;

import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import org.codehaus.jackson.map.annotate.JsonView;

import java.math.BigDecimal;

/**
 * User: kwuoti Date: 5.3.2013 Time: 9.09
 */
public class ProsenttiosuusEpavalidiVirhe extends Validointivirhe {

    private BigDecimal prosenttiosuus;

    public ProsenttiosuusEpavalidiVirhe(String virheviesti, BigDecimal prosenttiosuus) {
        super(Virhetyyppi.PROSENTTIOSUUS_EPAVALIDI, virheviesti);
        this.prosenttiosuus = prosenttiosuus;
    }

    public BigDecimal getProsenttiosuus() {
        return prosenttiosuus;
    }
}
