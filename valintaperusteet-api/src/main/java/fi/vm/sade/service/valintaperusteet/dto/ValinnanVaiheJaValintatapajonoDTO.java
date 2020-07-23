package fi.vm.sade.service.valintaperusteet.dto;

import java.util.List;

public class ValinnanVaiheJaValintatapajonoDTO {

  private String valinnanvaiheOid;
  private Boolean kuuluuSijoitteluun;
  private List<ValintatapajonoDTO> valintatapajonot;

  public ValinnanVaiheJaValintatapajonoDTO(
      String valinnanvaiheOid,
      Boolean kuuluuSijoitteluun,
      List<ValintatapajonoDTO> valintatapajonot) {
    this.valinnanvaiheOid = valinnanvaiheOid;
    this.kuuluuSijoitteluun = kuuluuSijoitteluun;
    this.valintatapajonot = valintatapajonot;
  }

  public String getValinnanvaiheOid() {
    return valinnanvaiheOid;
  }

  public Boolean getKuuluuSijoitteluun() {
    return kuuluuSijoitteluun;
  }

  public List<ValintatapajonoDTO> getValintatapajonot() {
    return valintatapajonot;
  }
}
