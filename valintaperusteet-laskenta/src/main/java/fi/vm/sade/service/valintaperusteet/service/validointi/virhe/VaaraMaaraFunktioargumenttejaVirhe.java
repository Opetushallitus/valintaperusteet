package fi.vm.sade.service.valintaperusteet.service.validointi.virhe;

import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import org.codehaus.jackson.map.annotate.JsonView;

/**
 * User: kwuoti
 * Date: 30.1.2013
 * Time: 15.12
 */
public class VaaraMaaraFunktioargumenttejaVirhe extends Validointivirhe {

    private String vaadittuLkm;

    private int annettuLkm;

    public VaaraMaaraFunktioargumenttejaVirhe(String virheviesti, String vaadittuLkm, int annettuLkm) {
        super(Virhetyyppi.VAARA_MAARA_FUNKTIOARGUMENTTEJA, virheviesti);
        this.vaadittuLkm = vaadittuLkm;
        this.annettuLkm = annettuLkm;
    }

    public String getVaadittuLkm() {
        return vaadittuLkm;
    }

    public int getAnnettuLkm() {
        return annettuLkm;
    }
}
