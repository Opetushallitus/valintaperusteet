package fi.vm.sade.service.valintaperusteet.service.validointi.virhe;

import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import org.codehaus.jackson.map.annotate.JsonView;

/**
 * User: kwuoti
 * Date: 30.1.2013
 * Time: 14.56
 */
public class ArvovalikonvertterinMinMaxPuutteellinenVirhe extends Validointivirhe {

    public ArvovalikonvertterinMinMaxPuutteellinenVirhe(String virheviesti, int indeksi) {
        super(Virhetyyppi.ARVOVALIKONVERTTERIN_MIN_MAX_PUUTTEELLINEN, virheviesti);
        this.indeksi = indeksi;
    }

    private int indeksi;

    public int getIndeksi() {
        return indeksi;
    }
}
