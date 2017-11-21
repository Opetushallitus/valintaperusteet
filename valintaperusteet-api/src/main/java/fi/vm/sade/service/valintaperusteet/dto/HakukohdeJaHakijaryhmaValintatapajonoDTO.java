package fi.vm.sade.service.valintaperusteet.dto;

import java.util.List;

public class HakukohdeJaHakijaryhmaValintatapajonoDTO {
    private String hakukohdeOid;
    private List<HakijaryhmaValintatapajonoDTO> hakijaryhmat;

    public HakukohdeJaHakijaryhmaValintatapajonoDTO(String hakukohdeOid, List<HakijaryhmaValintatapajonoDTO> hakijaryhmat) {
        this.hakukohdeOid = hakukohdeOid;
        this.hakijaryhmat = hakijaryhmat;
    }

    public String getHakukohdeOid() {
        return hakukohdeOid;
    }

    public List<HakijaryhmaValintatapajonoDTO> getHakijaryhmat() {
        return hakijaryhmat;
    }
}
