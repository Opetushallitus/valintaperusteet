package fi.vm.sade.service.valintaperusteet.service.validointi.virhe;

import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import org.codehaus.jackson.map.annotate.JsonView;

/**
 * User: kwuoti
 * Date: 30.1.2013
 * Time: 13.05
 */
public class VaaraMaaraSyoteparametrejaVirhe extends Validointivirhe {

    public VaaraMaaraSyoteparametrejaVirhe(String virheviesti, int vaadittuParametriLkm, int annettuParametriLkm) {
        super(Virhetyyppi.VAARA_MAARA_SYOTEPARAMETREJA, virheviesti);
        this.vaadittuParametriLkm = vaadittuParametriLkm;
        this.annettuParametriLkm = annettuParametriLkm;
    }

    private int vaadittuParametriLkm;

    private int annettuParametriLkm;

    public int getVaadittuParametriLkm() {
        return vaadittuParametriLkm;
    }

    public void setVaadittuParametriLkm(int vaadittuParametriLkm) {
        this.vaadittuParametriLkm = vaadittuParametriLkm;
    }

    public int getAnnettuParametriLkm() {
        return annettuParametriLkm;
    }

    public void setAnnettuParametriLkm(int annettuParametriLkm) {
        this.annettuParametriLkm = annettuParametriLkm;
    }
}
