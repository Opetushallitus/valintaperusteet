package fi.vm.sade.service.valintaperusteet.service.validointi.virhe;

import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import org.codehaus.jackson.map.annotate.JsonView;

/**
 * User: kwuoti Date: 8.2.2013 Time: 16.33
 */
public class TyhjaSyoteparametrinArvoVirhe extends Validointivirhe {

    @JsonView(JsonViews.Basic.class)
    private String avain;

    public TyhjaSyoteparametrinArvoVirhe(String virheviesti, String avain) {
        super(Virhetyyppi.TYHJA_SYOTEPARAMETRIN_ARVO, virheviesti);
        this.avain = avain;
    }

    public String getAvain() {
        return avain;
    }

    public void setAvain(String avain) {
        this.avain = avain;
    }
}
