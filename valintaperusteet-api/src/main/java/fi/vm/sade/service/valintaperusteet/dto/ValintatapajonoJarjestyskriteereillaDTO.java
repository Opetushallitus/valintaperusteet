package fi.vm.sade.service.valintaperusteet.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ValintatapajonoJarjestyskriteereillaDTO extends AbstractWithModifyTimestamp
    implements Prioritized {
  private Integer aloituspaikat;

  private String kuvaus;

  private String tyyppi;

  private String nimi;

  private String oid;

  private int prioriteetti;

  private Boolean siirretaanSijoitteluun = true;

  private String tasasijasaanto = "ARVONTA";

  private Date eiLasketaPaivamaaranJalkeen;

  private List<ValintaperusteetJarjestyskriteeriDTO> jarjestyskriteerit =
      new ArrayList<ValintaperusteetJarjestyskriteeriDTO>();

  private boolean eiVarasijatayttoa;

  private boolean merkitseMyohAuto = false;

  private Boolean poissaOlevaTaytto = false;

  private Boolean kaikkiEhdonTayttavatHyvaksytaan = false;

  private Boolean kaytetaanValintalaskentaa = true;

  private Boolean valmisSijoiteltavaksi = true;

  private Boolean valisijoittelu = false;

  private boolean poistetaankoHylatyt = false;

  public void setAloituspaikat(Integer aloituspaikat) {
    this.aloituspaikat = aloituspaikat;
  }

  public Integer getAloituspaikat() {
    return aloituspaikat;
  }

  public void setKuvaus(String kuvaus) {
    this.kuvaus = kuvaus;
  }

  public String getKuvaus() {
    return kuvaus;
  }

  public String getTyyppi() {
    return tyyppi;
  }

  public void setTyyppi(String tyyppi) {
    this.tyyppi = tyyppi;
  }

  public void setNimi(String nimi) {
    this.nimi = nimi;
  }

  public String getNimi() {
    return nimi;
  }

  public void setOid(String oid) {
    this.oid = oid;
  }

  public String getOid() {
    return oid;
  }

  public void setPrioriteetti(int prioriteetti) {
    this.prioriteetti = prioriteetti;
  }

  public int getPrioriteetti() {
    return prioriteetti;
  }

  public void setSiirretaanSijoitteluun(Boolean siirretaanSijoitteluun) {
    this.siirretaanSijoitteluun = siirretaanSijoitteluun;
  }

  public Boolean getSiirretaanSijoitteluun() {
    return siirretaanSijoitteluun;
  }

  public void setTasasijasaanto(String tasasijasaanto) {
    this.tasasijasaanto = tasasijasaanto;
  }

  public String getTasasijasaanto() {
    return tasasijasaanto;
  }

  public Date getEiLasketaPaivamaaranJalkeen() {
    return eiLasketaPaivamaaranJalkeen;
  }

  public void setEiLasketaPaivamaaranJalkeen(Date eiLasketaPaivamaaranJalkeen) {
    this.eiLasketaPaivamaaranJalkeen = eiLasketaPaivamaaranJalkeen;
  }

  public List<ValintaperusteetJarjestyskriteeriDTO> getJarjestyskriteerit() {
    return jarjestyskriteerit;
  }

  public void setJarjestyskriteerit(List<ValintaperusteetJarjestyskriteeriDTO> jarjestyskriteerit) {
    this.jarjestyskriteerit = jarjestyskriteerit;
  }

  public boolean getEiVarasijatayttoa() {
    return eiVarasijatayttoa;
  }

  public void setEiVarasijatayttoa(boolean eiVarasijatayttoa) {
    this.eiVarasijatayttoa = eiVarasijatayttoa;
  }

  public Boolean getMerkitseMyohAuto() {
    return merkitseMyohAuto;
  }

  public void setMerkitseMyohAuto(Boolean merkitseMyohAuto) {
    this.merkitseMyohAuto = merkitseMyohAuto != null ? merkitseMyohAuto : false;
  }

  public Boolean getPoissaOlevaTaytto() {
    return poissaOlevaTaytto;
  }

  public void setPoissaOlevaTaytto(Boolean poissaOlevaTaytto) {
    this.poissaOlevaTaytto = poissaOlevaTaytto;
  }

  public Boolean getKaikkiEhdonTayttavatHyvaksytaan() {
    return kaikkiEhdonTayttavatHyvaksytaan;
  }

  public void setKaikkiEhdonTayttavatHyvaksytaan(Boolean kaikkiEhdonTayttavatHyvaksytaan) {
    this.kaikkiEhdonTayttavatHyvaksytaan = kaikkiEhdonTayttavatHyvaksytaan;
  }

  public Boolean getKaytetaanValintalaskentaa() {
    return kaytetaanValintalaskentaa;
  }

  public void setKaytetaanValintalaskentaa(Boolean kaytetaanValintalaskentaa) {
    this.kaytetaanValintalaskentaa = kaytetaanValintalaskentaa;
  }

  public Boolean getValmisSijoiteltavaksi() {
    return valmisSijoiteltavaksi;
  }

  public void setValmisSijoiteltavaksi(Boolean valmisSijoiteltavaksi) {
    this.valmisSijoiteltavaksi = valmisSijoiteltavaksi;
  }

  public Boolean getValisijoittelu() {
    return valisijoittelu;
  }

  public void setValisijoittelu(Boolean valisijoittelu) {
    this.valisijoittelu = valisijoittelu;
  }

  public boolean isPoistetaankoHylatyt() {
    return poistetaankoHylatyt;
  }

  public void setPoistetaankoHylatyt(boolean poistetaankoHylatyt) {
    this.poistetaankoHylatyt = poistetaankoHylatyt;
  }
}
