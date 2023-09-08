package fi.vm.sade.service.valintaperusteet.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ValintaperusteetHakijaryhmaDTO", description = "Hakijaryhmä")
public class ValintaperusteetHakijaryhmaDTO implements Prioritized {
  @Schema(description = "OID", required = true)
  private String oid;

  @Schema(description = "Nimi", required = true)
  private String nimi;

  @Schema(description = "Kuvaus")
  private String kuvaus;

  @Schema(description = "Kiintio", required = true)
  private int kiintio;

  @Schema(description = "Hakijaryhmän prioriteetti")
  private int prioriteetti;

  @Schema(description = "Funktiokutsu")
  private ValintaperusteetFunktiokutsuDTO funktiokutsu;

  @Schema(description = "Vain hakijaryhmään kuuluvat voivat tulla hyväksytyksi")
  private boolean kaytaKaikki;

  @Schema(description = "Vain kiintiön verran voi tulla hyväksytyksi tästä hakijaryhmästä")
  private boolean tarkkaKiintio;

  @Schema(description = "Käytetäänkö vain hakijaryhmään kuuluvia", required = true)
  private boolean kaytetaanRyhmaanKuuluvia;

  @Schema(description = "valintatapajonon OID", required = true)
  private String valintatapajonoOid;

  @Schema(description = "hakukohteen OID", required = true)
  private String hakukohdeOid;

  @Schema(description = "Hakijaryhmän tyyppi koodi")
  private KoodiDTO hakijaryhmatyyppikoodi;

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

  public int getKiintio() {
    return kiintio;
  }

  public void setKiintio(int kiintio) {
    this.kiintio = kiintio;
  }

  public boolean isTarkkaKiintio() {
    return tarkkaKiintio;
  }

  public void setTarkkaKiintio(boolean tarkkaKiintio) {
    this.tarkkaKiintio = tarkkaKiintio;
  }

  public boolean isKaytaKaikki() {
    return kaytaKaikki;
  }

  public void setKaytaKaikki(boolean kaytaKaikki) {
    this.kaytaKaikki = kaytaKaikki;
  }

  public String getOid() {
    return oid;
  }

  public void setOid(String oid) {
    this.oid = oid;
  }

  public int getPrioriteetti() {
    return prioriteetti;
  }

  public void setPrioriteetti(int prioriteetti) {
    this.prioriteetti = prioriteetti;
  }

  public ValintaperusteetFunktiokutsuDTO getFunktiokutsu() {
    return funktiokutsu;
  }

  public void setFunktiokutsu(ValintaperusteetFunktiokutsuDTO funktiokutsu) {
    this.funktiokutsu = funktiokutsu;
  }

  public boolean isKaytetaanRyhmaanKuuluvia() {
    return kaytetaanRyhmaanKuuluvia;
  }

  public void setKaytetaanRyhmaanKuuluvia(boolean kaytetaanRyhmaanKuuluvia) {
    this.kaytetaanRyhmaanKuuluvia = kaytetaanRyhmaanKuuluvia;
  }

  public String getValintatapajonoOid() {
    return valintatapajonoOid;
  }

  public void setValintatapajonoOid(String valintatapajonoOid) {
    this.valintatapajonoOid = valintatapajonoOid;
  }

  public String getHakukohdeOid() {
    return hakukohdeOid;
  }

  public void setHakukohdeOid(String hakukohdeOid) {
    this.hakukohdeOid = hakukohdeOid;
  }

  public KoodiDTO getHakijaryhmatyyppikoodi() {
    return hakijaryhmatyyppikoodi;
  }

  public void setHakijaryhmatyyppikoodi(KoodiDTO hakijaryhmatyyppikoodi) {
    this.hakijaryhmatyyppikoodi = hakijaryhmatyyppikoodi;
  }
}
