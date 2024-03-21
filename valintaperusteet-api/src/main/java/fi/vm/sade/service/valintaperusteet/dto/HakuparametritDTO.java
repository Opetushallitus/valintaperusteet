package fi.vm.sade.service.valintaperusteet.dto;

public class HakuparametritDTO {
  private String hakukohdeOid;
  private Integer valinnanVaiheJarjestysluku;

  private boolean haetaankoLaskukaavat = true;

  public String getHakukohdeOid() {
    return hakukohdeOid;
  }

  public void setHakukohdeOid(String hakukohdeOid) {
    this.hakukohdeOid = hakukohdeOid;
  }

  public Integer getValinnanVaiheJarjestysluku() {
    return valinnanVaiheJarjestysluku;
  }

  public void setValinnanVaiheJarjestysluku(Integer valinnanVaiheJarjestysluku) {
    this.valinnanVaiheJarjestysluku = valinnanVaiheJarjestysluku;
  }

  public boolean haetaankoLaskukaavat() {
    return haetaankoLaskukaavat;
  }

  public void haetaankoLaskukaavat(boolean haetaankoLaskukaavat) {
    this.haetaankoLaskukaavat = haetaankoLaskukaavat;
  }
}
