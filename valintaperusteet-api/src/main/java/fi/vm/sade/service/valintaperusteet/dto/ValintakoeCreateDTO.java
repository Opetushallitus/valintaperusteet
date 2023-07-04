package fi.vm.sade.service.valintaperusteet.dto;

import fi.vm.sade.service.valintaperusteet.dto.model.Koekutsu;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ValintakoeCreateDTO", description = "Valintakoe")
public class ValintakoeCreateDTO {

  @Schema(description = "Tunniste", required = true)
  private String tunniste;

  @Schema(description = "Laskentakaava ID", required = true)
  private Long laskentakaavaId;

  @Schema(description = "Nimi", required = true)
  private String nimi;

  @Schema(description = "Kuvaus")
  private String kuvaus;

  @Schema(description = "Onko valintakoe aktiivinen", required = true)
  private Boolean aktiivinen;

  @Schema(description = "Lähetetäänkö koekutsut", required = true)
  private Boolean lahetetaankoKoekutsut;

  @Schema(description = "Kutsutaanko kaikki kokeeseen", required = true)
  private Boolean kutsutaankoKaikki;

  @Schema(description = "Kutsuttavien määrä")
  private Integer kutsuttavienMaara;

  @Schema(description = "Minne koekutsu osoitetaan")
  private Koekutsu kutsunKohde;

  @Schema(description = "Avain, josta kutsun kohde haetaan")
  private String kutsunKohdeAvain;

  public String getTunniste() {
    return tunniste;
  }

  public void setTunniste(String tunniste) {
    this.tunniste = tunniste;
  }

  public Long getLaskentakaavaId() {
    return laskentakaavaId;
  }

  public void setLaskentakaavaId(Long laskentakaavaId) {
    this.laskentakaavaId = laskentakaavaId;
  }

  public String getKuvaus() {
    return kuvaus;
  }

  public void setKuvaus(String kuvaus) {
    this.kuvaus = kuvaus;
  }

  public String getNimi() {
    return nimi;
  }

  public void setNimi(String nimi) {
    this.nimi = nimi;
  }

  public Boolean getAktiivinen() {
    return aktiivinen;
  }

  public void setAktiivinen(Boolean aktiivinen) {
    this.aktiivinen = aktiivinen;
  }

  public Boolean getLahetetaankoKoekutsut() {
    return lahetetaankoKoekutsut;
  }

  public void setLahetetaankoKoekutsut(Boolean lahetetaankoKoekutsut) {
    this.lahetetaankoKoekutsut = lahetetaankoKoekutsut;
  }

  public Boolean getKutsutaankoKaikki() {
    return kutsutaankoKaikki;
  }

  public void setKutsutaankoKaikki(Boolean kutsutaankoKaikki) {
    this.kutsutaankoKaikki = kutsutaankoKaikki;
  }

  public Integer getKutsuttavienMaara() {
    return kutsuttavienMaara;
  }

  public void setKutsuttavienMaara(final Integer kutsuttavienMaara) {
    this.kutsuttavienMaara = kutsuttavienMaara;
  }

  public Koekutsu getKutsunKohde() {
    return kutsunKohde;
  }

  public void setKutsunKohde(Koekutsu kutsunKohde) {
    this.kutsunKohde = kutsunKohde;
  }

  public String getKutsunKohdeAvain() {
    return kutsunKohdeAvain;
  }

  public void setKutsunKohdeAvain(String kutsunKohdeAvain) {
    this.kutsunKohdeAvain = kutsunKohdeAvain;
  }
}
