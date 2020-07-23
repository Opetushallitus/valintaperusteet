package fi.vm.sade.service.valintaperusteet.dto;

import java.util.List;

public class HakukohdeJaLinkitettyHakijaryhmaValintatapajonoDTO {
  private String hakukohdeOid;
  private List<LinkitettyHakijaryhmaValintatapajonoDTO> hakijaryhmat;

  public HakukohdeJaLinkitettyHakijaryhmaValintatapajonoDTO(
      String hakukohdeOid, List<LinkitettyHakijaryhmaValintatapajonoDTO> hakijaryhmat) {
    this.hakukohdeOid = hakukohdeOid;
    this.hakijaryhmat = hakijaryhmat;
  }

  public String getHakukohdeOid() {
    return hakukohdeOid;
  }

  public List<LinkitettyHakijaryhmaValintatapajonoDTO> getHakijaryhmat() {
    return hakijaryhmat;
  }
}
