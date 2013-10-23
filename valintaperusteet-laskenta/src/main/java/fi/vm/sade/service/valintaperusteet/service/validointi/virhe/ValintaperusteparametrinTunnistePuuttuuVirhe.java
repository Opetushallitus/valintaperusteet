package fi.vm.sade.service.valintaperusteet.service.validointi.virhe;

import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import org.codehaus.jackson.map.annotate.JsonView;

/**
 * User: wuoti
 * Date: 22.10.2013
 * Time: 9.48
 */
public class ValintaperusteparametrinTunnistePuuttuuVirhe extends Validointivirhe {

    @JsonView(JsonViews.Basic.class)
    private int indeksi;

    public ValintaperusteparametrinTunnistePuuttuuVirhe(String virheviesti, int indeksi) {
        super(Virhetyyppi.VALINTAPERUSTEPARAMETRIN_TUNNISTE_PUUTTUU, virheviesti);
        this.indeksi = indeksi;
    }

    public int getIndeksi() {
        return indeksi;
    }
}
