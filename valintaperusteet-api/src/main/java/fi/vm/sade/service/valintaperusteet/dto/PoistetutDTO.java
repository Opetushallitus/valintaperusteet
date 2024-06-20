package fi.vm.sade.service.valintaperusteet.dto;

import java.util.ArrayList;
import java.util.List;

public class PoistetutDTO {
  private List<HakukohdeViite> poistetut = new ArrayList<>();

  public List<HakukohdeViite> getPoistetut() {
    return poistetut;
  }

  public void setPoistetut(List<HakukohdeViite> poistetut) {
    this.poistetut = poistetut;
  }

  public static class Poistettu {
    protected boolean poistettu = true;

    public boolean isPoistettu() {
      return poistettu;
    }

    public void setPoistettu(boolean poistettu) {
      this.poistettu = poistettu;
    }
  }

  public static class PoistettuOid extends Poistettu {
    protected String oid;

    public String getOid() {
      return oid;
    }

    public void setOid(String oid) {
      this.oid = oid;
    }
  }

  public static class Valintaperuste extends Poistettu {
    private String tunniste;

    public String getTunniste() {
      return tunniste;
    }

    public void setTunniste(String tunniste) {
      this.tunniste = tunniste;
    }
  }

  public static class ValinnanVaihe extends Poistettu {
    private String valinnanVaiheOid;
    private List<PoistettuOid> valintatapajono;
    private List<PoistettuOid> valintakoe;

    public String getValinnanVaiheOid() {
      return valinnanVaiheOid;
    }

    public void setValinnanVaiheOid(String valinnanVaiheOid) {
      this.valinnanVaiheOid = valinnanVaiheOid;
    }

    public List<PoistettuOid> getValintatapajono() {
      return valintatapajono;
    }

    public void setValintatapajono(List<PoistettuOid> valintatapajono) {
      this.valintatapajono = valintatapajono;
    }

    public List<PoistettuOid> getValintakoe() {
      return valintakoe;
    }

    public void setValintakoe(List<PoistettuOid> valintakoe) {
      this.valintakoe = valintakoe;
    }
  }

  public static class HakukohdeViite extends Poistettu {
    private String hakukohdeOid;
    private ValinnanVaihe valinnanVaihe;
    private List<Valintaperuste> hakukohteenValintaperuste;

    public String getHakukohdeOid() {
      return hakukohdeOid;
    }

    public HakukohdeViite setHakukohdeOid(String hakukohdeOid) {
      this.hakukohdeOid = hakukohdeOid;
      return this;
    }

    public ValinnanVaihe getValinnanVaihe() {
      return valinnanVaihe;
    }

    public void setValinnanVaihe(ValinnanVaihe valinnanVaihe) {
      this.valinnanVaihe = valinnanVaihe;
    }

    public List<Valintaperuste> getHakukohteenValintaperuste() {
      return hakukohteenValintaperuste;
    }

    public void setHakukohteenValintaperuste(List<Valintaperuste> hakukohteenValintaperuste) {
      this.hakukohteenValintaperuste = hakukohteenValintaperuste;
    }
  }
}
