package fi.vm.sade.service.valintaperusteet.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "HakijaryhmaDTO", description = "Hakijaryhmä")
public class HakijaryhmaCreateDTO {
  @ApiModelProperty(value = "Nimi", required = true)
  private String nimi;

  @ApiModelProperty(value = "Kuvaus")
  private String kuvaus;

  @ApiModelProperty(value = "Kiintio", required = true)
  private int kiintio;

  @ApiModelProperty(value = "Laskentakaavan ID", required = true)
  private Long laskentakaavaId;

  @ApiModelProperty(value = "Kayta kaikki")
  private boolean kaytaKaikki;

  @ApiModelProperty(value = "Tarkka kiintio")
  private boolean tarkkaKiintio;

  @ApiModelProperty(value = "Käytetäänkö hakijaryhmään kuuluvia", required = true)
  private boolean kaytetaanRyhmaanKuuluvia;

  @ApiModelProperty(value = "Hakijaryhmatyyppikoodi")
  private KoodiDTO hakijaryhmatyyppikoodi = null;

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

  public Long getLaskentakaavaId() {
    return laskentakaavaId;
  }

  public void setLaskentakaavaId(Long laskentakaavaId) {
    this.laskentakaavaId = laskentakaavaId;
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

  public boolean isKaytetaanRyhmaanKuuluvia() {
    return kaytetaanRyhmaanKuuluvia;
  }

  public void setKaytetaanRyhmaanKuuluvia(boolean kaytetaanRyhmaanKuuluvia) {
    this.kaytetaanRyhmaanKuuluvia = kaytetaanRyhmaanKuuluvia;
  }

  public KoodiDTO getHakijaryhmatyyppikoodi() {
    return hakijaryhmatyyppikoodi;
  }

  public void setHakijaryhmatyyppikoodi(KoodiDTO hakijaryhmatyyppikoodi) {
    this.hakijaryhmatyyppikoodi = hakijaryhmatyyppikoodi;
  }
}
