package fi.vm.sade.service.valintaperusteet.service.validointi.virhe;

import fi.vm.sade.service.valintaperusteet.model.Abstraktivalidointivirhe;
import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import org.codehaus.jackson.map.annotate.JsonView;

/**
 * User: kwuoti
 * Date: 30.1.2013
 * Time: 12.49
 */
public class Validointivirhe extends Abstraktivalidointivirhe {

    @JsonView(JsonViews.Basic.class)
    private Virhetyyppi virhetyyppi;

    @JsonView(JsonViews.Basic.class)
    private String virheviesti;

    public Validointivirhe(Virhetyyppi virhetyyppi, String virheviesti) {
        this.virhetyyppi = virhetyyppi;
        this.virheviesti = virheviesti;
    }

    public Virhetyyppi getVirhetyyppi() {
        return virhetyyppi;
    }

    public void setVirhetyyppi(Virhetyyppi virhetyyppi) {
        this.virhetyyppi = virhetyyppi;
    }

    public String getVirheviesti() {
        return virheviesti;
    }

    public void setVirheviesti(String virheviesti) {
        this.virheviesti = virheviesti;
    }
}
