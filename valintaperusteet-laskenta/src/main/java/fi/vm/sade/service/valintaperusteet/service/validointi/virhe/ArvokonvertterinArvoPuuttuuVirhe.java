package fi.vm.sade.service.valintaperusteet.service.validointi.virhe;

import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import org.codehaus.jackson.map.annotate.JsonView;

/**
 * User: kwuoti
 * Date: 30.1.2013
 * Time: 14.24
 */
public class ArvokonvertterinArvoPuuttuuVirhe extends Validointivirhe {
    public ArvokonvertterinArvoPuuttuuVirhe(String virheviesti, int indeksi) {
        super(Virhetyyppi.ARVOKONVERTTERIN_ARVO_PUUTTUU, virheviesti);
        this.indeksi = indeksi;
    }

    private int indeksi;

    public int getIndeksi() {
        return indeksi;
    }
}
