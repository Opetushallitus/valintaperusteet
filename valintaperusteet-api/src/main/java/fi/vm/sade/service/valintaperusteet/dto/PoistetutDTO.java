package fi.vm.sade.service.valintaperusteet.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class PoistetutDTO {
  private List<HakukohdeViite> poistetut = new ArrayList<>();

  public List<HakukohdeViite> getPoistetut() {
    return poistetut;
  }

  public void setPoistetut(List<HakukohdeViite> poistetut) {
    this.poistetut = poistetut;
  }

  // Just for testing purposes
  private long getPoistettuCount() {
    long hakukohdeViiteCount =
        poistetut.stream().filter(viite -> viite.isPoistettu() != null).count();
    List<ValinnanVaihe> valinnanVaiheet =
        poistetut.stream().map(HakukohdeViite::getValinnanVaihe).filter(Objects::nonNull).toList();
    long valinnanVaiheCount =
        valinnanVaiheet.stream().filter(vaihe -> vaihe.isPoistettu() != null).count();
    long jonoCount =
        valinnanVaiheet.stream()
            .map(ValinnanVaihe::getValintatapajono)
            .filter(Objects::nonNull)
            .toList()
            .stream()
            .mapToLong(Collection::size)
            .sum();
    long koeCount =
        valinnanVaiheet.stream()
            .map(ValinnanVaihe::getValintakoe)
            .filter(Objects::nonNull)
            .toList()
            .stream()
            .mapToLong(Collection::size)
            .sum();
    long perusteCount =
        poistetut.stream()
            .map(HakukohdeViite::getHakukohteenValintaperuste)
            .filter(Objects::nonNull)
            .toList()
            .stream()
            .mapToLong(Collection::size)
            .sum();
    return hakukohdeViiteCount + valinnanVaiheCount + jonoCount + koeCount + perusteCount;
  }

  public static class PoistettuItselfOrJustParent {
    protected Boolean poistettu = Boolean.TRUE;

    public Boolean isPoistettu() {
      return poistettu;
    }

    public void setPoistettuItself(boolean poistettuItself) {
      this.poistettu = poistettuItself ? Boolean.TRUE : null;
    }
  }

  public static class PoistettuOid extends PoistettuItselfOrJustParent {
    protected String oid;

    public String getOid() {
      return oid;
    }

    public PoistettuOid setOid(String oid) {
      this.oid = oid;
      return this;
    }
  }

  public static class Valintaperuste extends PoistettuItselfOrJustParent {
    private String tunniste;

    public String getTunniste() {
      return tunniste;
    }

    public Valintaperuste setTunniste(String tunniste) {
      this.tunniste = tunniste;
      return this;
    }
  }

  public static class ValinnanVaihe extends PoistettuItselfOrJustParent {
    private String valinnanVaiheOid;
    private List<PoistettuOid> valintatapajono;
    private List<PoistettuOid> valintakoe;

    public String getValinnanVaiheOid() {
      return valinnanVaiheOid;
    }

    public ValinnanVaihe setValinnanVaiheOid(String valinnanVaiheOid) {
      this.valinnanVaiheOid = valinnanVaiheOid;
      return this;
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

  public static class HakukohdeViite extends PoistettuItselfOrJustParent {
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
