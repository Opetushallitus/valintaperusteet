package fi.vm.sade.service.valintaperusteet.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
    name = "HakijaryhmaValintatapajonoDTO",
    description = "Hakijaryhmän liittyminen valintatapajonoon")
public class HakijaryhmaValintatapajonoDTO implements Prioritized {

  @Schema(description = "OID", required = true)
  private String oid;

  @Schema(description = "Nimi")
  private String nimi;

  @Schema(description = "Kuvaus")
  private String kuvaus;

  @Schema(description = "Kiintio", required = true)
  private int kiintio;

  @Schema(description = "Kayta kaikki", required = true)
  private boolean kaytaKaikki;

  @Schema(description = "Tarkka kiintio", required = true)
  private boolean tarkkaKiintio;

  @Schema(description = "Käytetäänkö hakijaryhmään kuuluvia", required = true)
  private boolean kaytetaanRyhmaanKuuluvia;

  @Schema(description = "Aktiivinen", required = true)
  private Boolean aktiivinen;

  @Schema(description = "Prioriteetti", required = true)
  private int prioriteetti;

  @Schema(description = "Master haikjaryhmän OID")
  private String masterOid;

  @Schema(description = "Hakijaryhmatyyppikoodi")
  private KoodiDTO hakijaryhmatyyppikoodi = null;

  public Boolean getAktiivinen() {
    return aktiivinen;
  }

  public void setAktiivinen(Boolean aktiivinen) {
    this.aktiivinen = aktiivinen;
  }

  public int getKiintio() {
    return kiintio;
  }

  public void setKiintio(int kiintio) {
    this.kiintio = kiintio;
  }

  public boolean isKaytaKaikki() {
    return kaytaKaikki;
  }

  public void setKaytaKaikki(boolean kaytaKaikki) {
    this.kaytaKaikki = kaytaKaikki;
  }

  public boolean isTarkkaKiintio() {
    return tarkkaKiintio;
  }

  public void setTarkkaKiintio(boolean tarkkaKiintio) {
    this.tarkkaKiintio = tarkkaKiintio;
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

  public String getOid() {
    return oid;
  }

  public void setOid(String oid) {
    this.oid = oid;
  }

  public String getMasterOid() {
    return masterOid;
  }

  public void setMasterOid(String masterOid) {
    this.masterOid = masterOid;
  }

  public boolean isKaytetaanRyhmaanKuuluvia() {
    return kaytetaanRyhmaanKuuluvia;
  }

  public void setKaytetaanRyhmaanKuuluvia(boolean kaytetaanRyhmaanKuuluvia) {
    this.kaytetaanRyhmaanKuuluvia = kaytetaanRyhmaanKuuluvia;
  }

  public int getPrioriteetti() {
    return prioriteetti;
  }

  public void setPrioriteetti(int prioriteetti) {
    this.prioriteetti = prioriteetti;
  }

  public KoodiDTO getHakijaryhmatyyppikoodi() {
    return hakijaryhmatyyppikoodi;
  }

  public void setHakijaryhmatyyppikoodi(KoodiDTO hakijaryhmatyyppikoodi) {
    this.hakijaryhmatyyppikoodi = hakijaryhmatyyppikoodi;
  }
}
