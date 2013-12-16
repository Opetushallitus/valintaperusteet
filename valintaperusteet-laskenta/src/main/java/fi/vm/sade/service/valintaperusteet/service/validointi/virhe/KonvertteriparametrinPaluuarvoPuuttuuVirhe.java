package fi.vm.sade.service.valintaperusteet.service.validointi.virhe;

import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import org.codehaus.jackson.map.annotate.JsonView;

/**
 * User: kwuoti
 * Date: 30.1.2013
 * Time: 15.28
 */
public class KonvertteriparametrinPaluuarvoPuuttuuVirhe extends Validointivirhe {

    private int indeksi;

    public KonvertteriparametrinPaluuarvoPuuttuuVirhe(String virheviesti, int indeksi) {
        super(Virhetyyppi.KONVERTTERIPARAMETRIN_PALUUARVO_PUUTTUU, virheviesti);
        this.indeksi = indeksi;
    }

    public int getIndeksi() {
        return indeksi;
    }
}
