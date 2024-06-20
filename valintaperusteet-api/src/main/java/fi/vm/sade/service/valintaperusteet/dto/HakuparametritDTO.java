package fi.vm.sade.service.valintaperusteet.dto;

public class HakuparametritDTO {
  private String hakukohdeOid;
  private Integer valinnanVaiheJarjestysluku;

  private boolean haetaankoLaskukaavat = true;
  private boolean haetaankoPaivitysAikaleimat = false;

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

  public boolean haetaankoPaivitysAikaleimat() {
    return haetaankoPaivitysAikaleimat;
  }

  public void haetaankoLaskukaavat(boolean haetaankoLaskukaavat) {
    this.haetaankoLaskukaavat = haetaankoLaskukaavat;
  }

  public void haetaankoPaivitysAikaleimat(boolean haetaankoPaivitysAikaleimat) {
    this.haetaankoPaivitysAikaleimat = haetaankoPaivitysAikaleimat;
  }
}
