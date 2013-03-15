package fi.vm.sade.service.valintaperusteet.service.validointi.virhe;

import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import org.codehaus.jackson.map.annotate.JsonView;

/**
 * User: kwuoti
 * Date: 30.1.2013
 * Time: 14.59
 */
public class ArvovalikonvertterinMinimiSuurempiKuinMaksimiVirhe extends Validointivirhe {

    @JsonView(JsonViews.Basic.class)
    private int indeksi;

    public ArvovalikonvertterinMinimiSuurempiKuinMaksimiVirhe(String virheviesti, int indeksi) {
        super(Virhetyyppi.ARVOVALIKONVERTTERIN_MINIMI_SUUREMPI_KUIN_MAKSIMI, virheviesti);
        this.indeksi = indeksi;
    }

    public int getIndeksi() {
        return indeksi;
    }
}
