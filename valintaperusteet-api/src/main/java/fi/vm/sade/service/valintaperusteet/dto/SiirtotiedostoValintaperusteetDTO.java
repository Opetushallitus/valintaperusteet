package fi.vm.sade.service.valintaperusteet.dto;

import java.util.ArrayList;
import java.util.List;

public class SiirtotiedostoValintaperusteetDTO extends AbstractWithModifyTimestamp {
  private String hakukohdeOid;

  private String hakuOid;

  private String tarjoajaOid;

  private List<ValintaperusteetValinnanVaiheDTO> valinnanVaiheet;

  private List<HakukohteenValintaperusteDTO> hakukohteenValintaperuste =
      new ArrayList<HakukohteenValintaperusteDTO>();

  public void setHakukohdeOid(String hakukohdeOid) {
    this.hakukohdeOid = hakukohdeOid;
  }

  public String getHakukohdeOid() {
    return hakukohdeOid;
  }

  public void setHakuOid(String hakuOid) {
    this.hakuOid = hakuOid;
  }

  public String getHakuOid() {
    return hakuOid;
  }

  public void setTarjoajaOid(String tarjoajaOid) {
    this.tarjoajaOid = tarjoajaOid;
  }

  public String getTarjoajaOid() {
    return tarjoajaOid;
  }

  public List<ValintaperusteetValinnanVaiheDTO> getValinnanVaiheet() {
    return valinnanVaiheet;
  }

  public void setValinnanVaiheet(List<ValintaperusteetValinnanVaiheDTO> valinnanVaiheet) {
    this.valinnanVaiheet = valinnanVaiheet;
  }

  public List<HakukohteenValintaperusteDTO> getHakukohteenValintaperuste() {
    return hakukohteenValintaperuste;
  }

  public void setHakukohteenValintaperuste(
      List<HakukohteenValintaperusteDTO> hakukohteenValintaperuste) {
    this.hakukohteenValintaperuste = hakukohteenValintaperuste;
  }
}
