package fi.vm.sade.service.valintaperusteet.service.validointi.virhe;

import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import org.codehaus.jackson.map.annotate.JsonView;

/**
 * User: kwuoti
 * Date: 5.3.2013
 * Time: 8.44
 */
public class NSuurempiKuinFunktioargumenttienLkmVirhe extends Validointivirhe {
    private int n;

    public NSuurempiKuinFunktioargumenttienLkmVirhe(String virheviesti, int n) {
        super(Virhetyyppi.N_SUUREMPI_KUIN_FUNKTIOARGUMENTTIEN_LKM, virheviesti);
        this.n = n;
    }

    public int getN() {
        return n;
    }
}
