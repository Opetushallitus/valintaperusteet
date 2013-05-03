package fi.vm.sade.service.valintaperusteet.service.validointi.virhe;

import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import org.codehaus.jackson.map.annotate.JsonView;

/**
 * User: kwuoti
 * Date: 30.1.2013
 * Time: 15.16
 */
public class VirheellinenFunktioargumentinTyyppiVirhe extends Validointivirhe {

    @JsonView(JsonViews.Basic.class)
    private int indeksi;

    @JsonView(JsonViews.Basic.class)
    private String vaadittuTyyppi;

    @JsonView(JsonViews.Basic.class)
    private String annettuTyyppi;

    public VirheellinenFunktioargumentinTyyppiVirhe(String virheviesti, int indeksi, String vaadittuTyyppi, String annettuTyyppi) {
        super(Virhetyyppi.VIRHEELLINEN_FUNKTIOARGUMENTIN_TYYPPI, virheviesti);
        this.indeksi = indeksi;
        this.vaadittuTyyppi = vaadittuTyyppi;
        this.annettuTyyppi = annettuTyyppi;
    }

    public int getIndeksi() {
        return indeksi;
    }

    public String getVaadittuTyyppi() {
        return vaadittuTyyppi;
    }

    public String getAnnettuTyyppi() {
        return annettuTyyppi;
    }
}
