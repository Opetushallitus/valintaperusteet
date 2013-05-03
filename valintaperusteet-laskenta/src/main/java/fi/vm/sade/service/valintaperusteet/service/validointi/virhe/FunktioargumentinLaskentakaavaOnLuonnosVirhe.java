package fi.vm.sade.service.valintaperusteet.service.validointi.virhe;

import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import org.codehaus.jackson.map.annotate.JsonView;

/**
 * User: kwuoti
 * Date: 28.2.2013
 * Time: 12.40
 */
public class FunktioargumentinLaskentakaavaOnLuonnosVirhe extends  Validointivirhe {
    public FunktioargumentinLaskentakaavaOnLuonnosVirhe(String virheviesti, int indeksi) {
        super(Virhetyyppi.FUNKTIOARGUMENTIN_LASKENTAKAAVA_ON_LUONNOS, virheviesti);
        this.indeksi = indeksi;
    }

    @JsonView(JsonViews.Basic.class)
    private int indeksi;

    public int getIndeksi() {
        return indeksi;
    }
}
