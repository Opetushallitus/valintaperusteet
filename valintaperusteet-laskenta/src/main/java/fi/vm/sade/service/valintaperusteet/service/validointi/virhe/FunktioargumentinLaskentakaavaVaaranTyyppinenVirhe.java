package fi.vm.sade.service.valintaperusteet.service.validointi.virhe;

import fi.vm.sade.service.valintaperusteet.model.Funktiotyyppi;
import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import org.codehaus.jackson.map.annotate.JsonView;

/**
 * User: kwuoti
 * Date: 28.2.2013
 * Time: 12.36
 */
public class FunktioargumentinLaskentakaavaVaaranTyyppinenVirhe extends Validointivirhe {

    public FunktioargumentinLaskentakaavaVaaranTyyppinenVirhe(String virheviesti,
                                                              int indeksi,
                                                              Funktiotyyppi vaadittuTyyppi,
                                                              Funktiotyyppi annettuTyyppi) {
        super(Virhetyyppi.FUNKTIOARGUMENTIN_LASKENTAKAAVA_VAARAN_TYYPPINEN_VIRHE, virheviesti);
        this.indeksi = indeksi;
        this.vaadittuTyyppi = vaadittuTyyppi;
        this.annettuTyyppi = annettuTyyppi;
    }

    private int indeksi;

    private Funktiotyyppi vaadittuTyyppi;

    private Funktiotyyppi annettuTyyppi;

    public int getIndeksi() {
        return indeksi;
    }

    public Funktiotyyppi getVaadittuTyyppi() {
        return vaadittuTyyppi;
    }

    public Funktiotyyppi getAnnettuTyyppi() {
        return annettuTyyppi;
    }
}
