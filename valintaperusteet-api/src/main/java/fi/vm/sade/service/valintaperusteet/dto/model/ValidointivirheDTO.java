package fi.vm.sade.service.valintaperusteet.dto.model;

import java.math.BigDecimal;

/**
 * User: kwuoti
 * Date: 30.1.2013
 * Time: 16.26
 */
public class ValidointivirheDTO {

    private Virhetyyppi virhetyyppi;

    private String virheviesti;


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
