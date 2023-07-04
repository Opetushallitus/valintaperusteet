package fi.vm.sade.service.valintaperusteet.dto;

import fi.vm.sade.service.valintaperusteet.dto.model.Tasapistesaanto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;

@Schema(name = "ValintatapajonoCreateDTO", description = "Valintatapajono")
public class ValintatapajonoCreateDTO {
  @Schema(description = "Aloituspaikat", required = true)
  private Integer aloituspaikat;

  @Schema(description = "Nimi", required = true)
  private String nimi;

  @Schema(description = "Kuvaus")
  private String kuvaus;

  @Schema(description = "Tyyppi")
  private String tyyppi;

  @Schema(description = "Siirretään sijoitteluun", required = true)
  private Boolean siirretaanSijoitteluun = false;

  @Schema(description = "Tasapistesääntö", required = true)
  private Tasapistesaanto tasapistesaanto;

  @Schema(description = "Aktiivinen", required = true)
  private Boolean aktiivinen;

  @Schema(description = "Suoritetaanko jonolle välisijoittelu", required = true)
  private Boolean valisijoittelu = false;

  @Schema(
      description = "Siirretäänkö laskennan tulokset automaattisesti sijoitteluun",
      required = true)
  private Boolean automaattinenSijoitteluunSiirto = false;

  @Schema(description = "Ei varasijatäyttöä", required = true)
  private Boolean eiVarasijatayttoa = false;

  @Schema(
      description = "Hyväksytäänkö kaikki hyväksyttävissä olevat aloituspaikoista riippumatta",
      required = true)
  private Boolean kaikkiEhdonTayttavatHyvaksytaan = false;

  @Schema(description = "Varasijojen lkm. 0 == pois päältä", required = true)
  private Integer varasijat = 0;

  @Schema(description = "Kuinka monta päivää varasijoja täytetään", required = true)
  private Integer varasijaTayttoPaivat = 0;

  @Schema(description = "Merkitäänkö myöhästyneet vastaanottajat automaattisesti", required = true)
  private Boolean merkitseMyohAuto = false;

  @Schema(description = "Täytetäänkö poissaolevia", required = true)
  private Boolean poissaOlevaTaytto = false;

  @Schema(description = "Poistetaanko hylätyt laskentojen tuloksista", required = true)
  private Boolean poistetaankoHylatyt = false;

  @Schema(description = "Varasijasääntöjä käytetään alkaen")
  private Date varasijojaKaytetaanAlkaen;

  @Schema(description = "Varasijoja täytetään asti")
  private Date varasijojaTaytetaanAsti;

  @Schema(description = "Ei lasketa päivämäärän jälkeen")
  private Date eiLasketaPaivamaaranJalkeen;

  @Schema(description = "Käytetäänkö valintalaskentaa", required = true)
  private Boolean kaytetaanValintalaskentaa = true;

  @Schema(description = "Valintatapajono, josta vapaaksi jääneet paikat täytetään")
  private String tayttojono;

  public Integer getAloituspaikat() {
    return aloituspaikat;
  }

  public void setAloituspaikat(Integer aloituspaikat) {
    this.aloituspaikat = aloituspaikat;
  }

  public String getNimi() {
    return nimi;
  }

  public void setNimi(String nimi) {
    this.nimi = nimi;
  }

  public String getKuvaus() {
    return kuvaus;
  }

  public void setKuvaus(String kuvaus) {
    this.kuvaus = kuvaus;
  }

  public String getTyyppi() {
    return tyyppi;
  }

  public void setTyyppi(String tyyppi) {
    this.tyyppi = tyyppi;
  }

  public Boolean getSiirretaanSijoitteluun() {
    return siirretaanSijoitteluun;
  }

  public void setSiirretaanSijoitteluun(Boolean siirretaanSijoitteluun) {
    this.siirretaanSijoitteluun = siirretaanSijoitteluun;
  }

  public Tasapistesaanto getTasapistesaanto() {
    return tasapistesaanto;
  }

  public void setTasapistesaanto(Tasapistesaanto tasapistesaanto) {
    this.tasapistesaanto = tasapistesaanto;
  }

  public Boolean getAktiivinen() {
    return aktiivinen;
  }

  public void setAktiivinen(Boolean aktiivinen) {
    this.aktiivinen = aktiivinen;
  }

  public Boolean getEiVarasijatayttoa() {
    return eiVarasijatayttoa;
  }

  public void setEiVarasijatayttoa(Boolean eiVarasijatayttoa) {
    this.eiVarasijatayttoa = eiVarasijatayttoa;
  }

  public Integer getVarasijat() {
    return varasijat;
  }

  public void setVarasijat(Integer varasijat) {
    this.varasijat = varasijat;
  }

  public Integer getVarasijaTayttoPaivat() {
    return varasijaTayttoPaivat;
  }

  public void setVarasijaTayttoPaivat(Integer varasijaTayttoPaivat) {
    this.varasijaTayttoPaivat = varasijaTayttoPaivat;
  }

  public Boolean getMerkitseMyohAuto() {
    return merkitseMyohAuto;
  }

  public void setMerkitseMyohAuto(Boolean merkitseMyohAuto) {
    this.merkitseMyohAuto = merkitseMyohAuto;
  }

  public Boolean getPoissaOlevaTaytto() {
    return poissaOlevaTaytto;
  }

  public void setPoissaOlevaTaytto(Boolean poissaOlevaTaytto) {
    this.poissaOlevaTaytto = poissaOlevaTaytto;
  }

  public Date getVarasijojaKaytetaanAlkaen() {
    return varasijojaKaytetaanAlkaen;
  }

  public void setVarasijojaKaytetaanAlkaen(Date varasijojaKaytetaanAlkaen) {
    this.varasijojaKaytetaanAlkaen = varasijojaKaytetaanAlkaen;
  }

  public Date getVarasijojaTaytetaanAsti() {
    return varasijojaTaytetaanAsti;
  }

  public void setVarasijojaTaytetaanAsti(Date varasijojaTaytetaanAsti) {
    this.varasijojaTaytetaanAsti = varasijojaTaytetaanAsti;
  }

  public Date getEiLasketaPaivamaaranJalkeen() {
    return eiLasketaPaivamaaranJalkeen;
  }

  public void setEiLasketaPaivamaaranJalkeen(Date eiLasketaPaivamaaranJalkeen) {
    this.eiLasketaPaivamaaranJalkeen = eiLasketaPaivamaaranJalkeen;
  }

  public Boolean getKaytetaanValintalaskentaa() {
    return kaytetaanValintalaskentaa;
  }

  public void setKaytetaanValintalaskentaa(Boolean kaytetaanValintalaskentaa) {
    this.kaytetaanValintalaskentaa = kaytetaanValintalaskentaa;
  }

  public Boolean getKaikkiEhdonTayttavatHyvaksytaan() {
    return kaikkiEhdonTayttavatHyvaksytaan;
  }

  public void setKaikkiEhdonTayttavatHyvaksytaan(Boolean kaikkiEhdonTayttavatHyvaksytaan) {
    this.kaikkiEhdonTayttavatHyvaksytaan = kaikkiEhdonTayttavatHyvaksytaan;
  }

  public String getTayttojono() {
    return tayttojono;
  }

  public void setTayttojono(String tayttojono) {
    this.tayttojono = tayttojono;
  }

  public Boolean getValisijoittelu() {
    return valisijoittelu;
  }

  public void setValisijoittelu(Boolean valisijoittelu) {
    this.valisijoittelu = valisijoittelu;
  }

  public Boolean getautomaattinenSijoitteluunSiirto() {
    return automaattinenSijoitteluunSiirto;
  }

  public void setautomaattinenSijoitteluunSiirto(Boolean automaattinenSijoitteluunSiirto) {
    this.automaattinenSijoitteluunSiirto = automaattinenSijoitteluunSiirto;
  }

  public Boolean getPoistetaankoHylatyt() {
    return poistetaankoHylatyt;
  }

  public void setPoistetaankoHylatyt(Boolean poistetaankoHylatyt) {
    this.poistetaankoHylatyt = poistetaankoHylatyt;
  }

  @Override
  public String toString() {
    return "ValintatapajonoCreateDTO{"
        + "aloituspaikat="
        + aloituspaikat
        + ", nimi='"
        + nimi
        + '\''
        + ", kuvaus='"
        + kuvaus
        + '\''
        + ", siirretaanSijoitteluun="
        + siirretaanSijoitteluun
        + ", tasapistesaanto="
        + tasapistesaanto
        + ", aktiivinen="
        + aktiivinen
        + ", valisijoittelu="
        + valisijoittelu
        + ", automaattinenSijoitteluunSiirto="
        + automaattinenSijoitteluunSiirto
        + ", eiVarasijatayttoa="
        + eiVarasijatayttoa
        + ", kaikkiEhdonTayttavatHyvaksytaan="
        + kaikkiEhdonTayttavatHyvaksytaan
        + ", varasijat="
        + varasijat
        + ", varasijaTayttoPaivat="
        + varasijaTayttoPaivat
        + ", merkitseMyohAuto="
        + merkitseMyohAuto
        + ", poissaOlevaTaytto="
        + poissaOlevaTaytto
        + ", poistetaankoHylatyt="
        + poistetaankoHylatyt
        + ", varasijojaKaytetaanAlkaen="
        + varasijojaKaytetaanAlkaen
        + ", varasijojaTaytetaanAsti="
        + varasijojaTaytetaanAsti
        + ", eiLasketaPaivamaaranJalkeen="
        + eiLasketaPaivamaaranJalkeen
        + ", kaytetaanValintalaskentaa="
        + kaytetaanValintalaskentaa
        + ", tayttojono='"
        + tayttojono
        + '\''
        + '}';
  }
}
