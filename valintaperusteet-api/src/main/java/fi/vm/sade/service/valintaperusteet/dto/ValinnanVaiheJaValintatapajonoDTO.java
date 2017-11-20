package fi.vm.sade.service.valintaperusteet.dto;

import java.util.List;

public class ValinnanVaiheJaValintatapajonoDTO {

    private String valinnanvaiheOid;
    private List<ValintatapajonoDTO> valintatapajonot;

    public ValinnanVaiheJaValintatapajonoDTO(String valinnanvaiheOid, List<ValintatapajonoDTO> valintatapajonot) {
        this.valinnanvaiheOid = valinnanvaiheOid;
        this.valintatapajonot = valintatapajonot;
    }

    public String getValinnanvaiheOid() {
        return valinnanvaiheOid;
    }

    public List<ValintatapajonoDTO> getValintatapajonot() {
        return valintatapajonot;
    }
}
