package fi.vm.sade.service.valintaperusteet.service.validointi.virhe;

import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import org.codehaus.jackson.map.annotate.JsonView;

/**
 * User: kwuoti
 * Date: 28.2.2013
 * Time: 12.24
 */
public class FunktiokutsuaEiOleMaariteltyFunktioargumentilleVirhe extends Validointivirhe {
    public FunktiokutsuaEiOleMaariteltyFunktioargumentilleVirhe(String virheviesti, int indeksi) {
        super(Virhetyyppi.FUNKTIOKUTSUA_EI_OLE_MAARITELTY_FUNKTIOARGUMENTILLE, virheviesti);
        this.indeksi = indeksi;
    }

    private int indeksi;

    public int getIndeksi() {
        return indeksi;
    }

    public void setIndeksi(int indeksi) {
        this.indeksi = indeksi;
    }
}
