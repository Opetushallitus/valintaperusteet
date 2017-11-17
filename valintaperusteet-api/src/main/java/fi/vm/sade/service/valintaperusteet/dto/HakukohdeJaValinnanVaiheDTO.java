package fi.vm.sade.service.valintaperusteet.dto;

import java.util.List;

public class HakukohdeJaValinnanVaiheDTO {

    private String hakukohdeOid;
    private List<ValinnanVaiheDTO> valinnanvaiheet;

    public HakukohdeJaValinnanVaiheDTO(String hakukohdeOid, List<ValinnanVaiheDTO> valinnanvaiheet) {
        this.hakukohdeOid = hakukohdeOid;
        this.valinnanvaiheet = valinnanvaiheet;
    }

    public String getHakukohdeOid() {
        return hakukohdeOid;
    }

    public List<ValinnanVaiheDTO> getValinnanvaiheet() {
        return valinnanvaiheet;
    }
}
