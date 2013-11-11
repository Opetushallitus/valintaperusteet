package fi.vm.sade.service.valintaperusteet.service.validointi.virhe;

import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import org.codehaus.jackson.map.annotate.JsonView;

/**
 * User: kwuoti Date: 30.1.2013 Time: 13.16
 */
public class VirheellinenSyoteParametrinTyyppiVirhe extends Validointivirhe {

    public VirheellinenSyoteParametrinTyyppiVirhe(String virheviesti, String syoteParametrinAvain, String vaadittuTyyppi) {
        super(Virhetyyppi.VIRHEELLINEN_SYOTEPARAMETRIN_TYYPPI, virheviesti);
        this.syoteParametrinAvain = syoteParametrinAvain;
        this.vaadittuTyyppi = vaadittuTyyppi;
    }

    @JsonView(JsonViews.Basic.class)
    private String syoteParametrinAvain;

    @JsonView(JsonViews.Basic.class)
    private String vaadittuTyyppi;

    public String getSyoteParametrinAvain() {
        return syoteParametrinAvain;
    }

    public String getVaadittuTyyppi() {
        return vaadittuTyyppi;
    }
}
