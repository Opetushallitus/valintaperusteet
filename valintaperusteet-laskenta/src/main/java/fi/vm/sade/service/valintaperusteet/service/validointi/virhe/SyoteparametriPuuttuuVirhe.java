package fi.vm.sade.service.valintaperusteet.service.validointi.virhe;

import fi.vm.sade.service.valintaperusteet.model.JsonViews;
import org.codehaus.jackson.map.annotate.JsonView;

/**
 * User: kwuoti
 * Date: 30.1.2013
 * Time: 13.08
 */
public class SyoteparametriPuuttuuVirhe extends Validointivirhe {
    public SyoteparametriPuuttuuVirhe(String virheviesti, String puuttuvaParametriAvain) {
        super(Virhetyyppi.SYOTEPARAMETRI_PUUTTUU, virheviesti);

        this.puuttuvaParametriAvain = puuttuvaParametriAvain;
    }

    private String puuttuvaParametriAvain;

    public String getPuuttuvaParametriAvain() {
        return puuttuvaParametriAvain;
    }
}
