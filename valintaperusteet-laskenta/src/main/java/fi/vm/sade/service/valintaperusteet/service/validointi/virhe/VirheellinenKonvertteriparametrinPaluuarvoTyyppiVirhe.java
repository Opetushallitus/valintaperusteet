package fi.vm.sade.service.valintaperusteet.service.validointi.virhe;

import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import org.codehaus.jackson.map.annotate.JsonView;

/**
 * User: kwuoti
 * Date: 30.1.2013
 * Time: 13.48
 */
public class VirheellinenKonvertteriparametrinPaluuarvoTyyppiVirhe extends Validointivirhe {
    public VirheellinenKonvertteriparametrinPaluuarvoTyyppiVirhe(String virheviesti, int indeksi, String vaadittuTyyppi) {
        super(Virhetyyppi.VIRHEELLINEN_KONVERTTERIPARAMETRIN_PALUUARVOTYYPPI, virheviesti);
        this.indeksi = indeksi;
        this.vaadittuTyyppi = vaadittuTyyppi;
    }

    private int indeksi;

    private String vaadittuTyyppi;

    public int getIndeksi() {
        return indeksi;
    }

    public String getVaadittuTyyppi() {
        return vaadittuTyyppi;
    }
}
