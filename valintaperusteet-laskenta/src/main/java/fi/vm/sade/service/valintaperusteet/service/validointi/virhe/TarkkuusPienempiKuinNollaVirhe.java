package fi.vm.sade.service.valintaperusteet.service.validointi.virhe;

import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import org.codehaus.jackson.map.annotate.JsonView;

/**
 * User: kwuoti Date: 5.3.2013 Time: 9.09
 */
public class TarkkuusPienempiKuinNollaVirhe extends Validointivirhe {

    @JsonView(JsonViews.Basic.class)
    private int tarkkuus;

    public TarkkuusPienempiKuinNollaVirhe(String virheviesti, int tarkkuus) {
        super(Virhetyyppi.TARKKUUS_PIENEMPI_KUIN_NOLLA, virheviesti);
        this.tarkkuus = tarkkuus;
    }

    public int getTarkkuus() {
        return tarkkuus;
    }
}
