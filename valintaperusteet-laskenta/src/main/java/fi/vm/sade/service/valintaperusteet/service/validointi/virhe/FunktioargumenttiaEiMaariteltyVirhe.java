package fi.vm.sade.service.valintaperusteet.service.validointi.virhe;

import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import org.codehaus.jackson.map.annotate.JsonView;

/**
 * User: kwuoti
 * Date: 28.2.2013
 * Time: 10.25
 */
public class FunktioargumenttiaEiMaariteltyVirhe extends Validointivirhe {

    public FunktioargumenttiaEiMaariteltyVirhe(String virheviesti, int indeksi) {
        super(Virhetyyppi.FUNKTIOARGUMENTTIA_EI_MAARITELTY, virheviesti);
        this.indeksi = indeksi;
    }


    @JsonView(JsonViews.Basic.class)
    private int indeksi;


    public int getIndeksi() {
        return indeksi;
    }
}
