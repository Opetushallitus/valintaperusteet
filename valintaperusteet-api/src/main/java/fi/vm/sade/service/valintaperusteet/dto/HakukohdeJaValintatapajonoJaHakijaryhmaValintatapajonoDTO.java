package fi.vm.sade.service.valintaperusteet.dto;

import java.util.List;

public class HakukohdeJaValintatapajonoJaHakijaryhmaValintatapajonoDTO {
    private String hakukohdeOid;
    private List<ValintatapajonoJaHakijaryhmaValintatapajonoDTO> hakijaryhmat;

    public HakukohdeJaValintatapajonoJaHakijaryhmaValintatapajonoDTO(String hakukohdeOid,
                                                                     List<ValintatapajonoJaHakijaryhmaValintatapajonoDTO> hakijaryhmat) {
        this.hakukohdeOid = hakukohdeOid;
        this.hakijaryhmat = hakijaryhmat;
    }

    public String getHakukohdeOid() {
        return hakukohdeOid;
    }

    public List<ValintatapajonoJaHakijaryhmaValintatapajonoDTO> getHakijaryhmat() {
        return hakijaryhmat;
    }
}
