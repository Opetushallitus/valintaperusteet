package fi.vm.sade.service.valintaperusteet.service.validointi.virhe;

import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import org.codehaus.jackson.map.annotate.JsonView;

/**
 * User: kwuoti
 * Date: 30.1.2013
 * Time: 14.29
 */
public class VirheellinenArvokonvertterinArvoTyyppiVirhe extends Validointivirhe {


    private int indeksi;

    public VirheellinenArvokonvertterinArvoTyyppiVirhe(String virheviesti, int indeksi) {
        super(Virhetyyppi.VIRHEELLINEN_ARVOKONVERTTERIN_ARVOTYYPPI, virheviesti);
        this.indeksi = indeksi;
    }

    public int getIndeksi() {
        return indeksi;
    }
}
