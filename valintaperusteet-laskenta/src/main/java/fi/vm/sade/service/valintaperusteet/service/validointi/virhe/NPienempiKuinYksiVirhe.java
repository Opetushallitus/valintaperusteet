package fi.vm.sade.service.valintaperusteet.service.validointi.virhe;

import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import org.codehaus.jackson.map.annotate.JsonView;

/**
 * User: kwuoti
 * Date: 5.3.2013
 * Time: 8.42
 */
public class NPienempiKuinYksiVirhe extends Validointivirhe {
    @JsonView(JsonViews.Basic.class)
    private int n;

    public NPienempiKuinYksiVirhe(String virheviesti, int n) {
        super(Virhetyyppi.N_PIENEMPI_KUIN_YKSI, virheviesti);
        this.n = n;
    }

    public int getN() {
        return n;
    }
}
