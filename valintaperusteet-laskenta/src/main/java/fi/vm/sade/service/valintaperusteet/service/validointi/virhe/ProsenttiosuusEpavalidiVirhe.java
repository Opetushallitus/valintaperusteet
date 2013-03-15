package fi.vm.sade.service.valintaperusteet.service.validointi.virhe;

import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import org.codehaus.jackson.map.annotate.JsonView;

/**
 * User: kwuoti
 * Date: 5.3.2013
 * Time: 9.09
 */
public class ProsenttiosuusEpavalidiVirhe extends Validointivirhe {

    @JsonView(JsonViews.Basic.class)
    private double prosenttiosuus;

    public ProsenttiosuusEpavalidiVirhe(String virheviesti, double prosenttiosuus) {
        super(Virhetyyppi.PROSENTTIOSUUS_EPAVALIDI, virheviesti);
        this.prosenttiosuus = prosenttiosuus;
    }

    public double getProsenttiosuus() {
        return prosenttiosuus;
    }
}
