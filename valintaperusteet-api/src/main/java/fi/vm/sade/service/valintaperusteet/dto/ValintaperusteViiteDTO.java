package fi.vm.sade.service.valintaperusteet.dto;

import fi.vm.sade.service.valintaperusteet.dto.model.Valintaperustelahde;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "ValintaperusteViiteDTO", description = "Valintaperusteviite")
public class ValintaperusteViiteDTO implements Comparable<ValintaperusteViiteDTO> {

  @ApiModelProperty(value = "tunniste", required = true)
  private String tunniste;

  @ApiModelProperty(value = "Kuvaus")
  private String kuvaus;

  @ApiModelProperty(value = "Valintaperusteen lähde", required = true)
  private Valintaperustelahde lahde;

  @ApiModelProperty(value = "Onko valintaperuste pakollinen", required = true)
  private Boolean onPakollinen;

  // Jos valintaperusteen lähde on hakukohde, voidaan epäsuoralla
  // viittauksella hakea
  // hakukohteelta tunniste, jolla viitataan hakemuksen arvoon
  @ApiModelProperty(
      value = "Viitataanko hakemuksen valintaperusteeseen epäsuorasti",
      required = true)
  private Boolean epasuoraViittaus;

  @ApiModelProperty(value = "Indeksi", required = true)
  private Integer indeksi;

  @ApiModelProperty(value = "Vaatii osallistumisen", required = true)
  private Boolean vaatiiOsallistumisen = true;

  @ApiModelProperty(value = "Syotettavissa kaikille", required = true)
  private Boolean syotettavissaKaikille = true;

  @ApiModelProperty(value = "Hylkäysperusteen kuvaukset")
  private TekstiRyhmaDTO kuvaukset = new TekstiRyhmaDTO();

  @ApiModelProperty(value = "Syötettävän arvon tyyppi", required = false)
  private KoodiDTO syotettavanarvontyyppi;

  @ApiModelProperty(value = "Tilastoidaan", required = false)
  private boolean tilastoidaan;

  public ValintaperusteViiteDTO() { }

  public ValintaperusteViiteDTO(String tunniste,
                                String kuvaus,
                                Valintaperustelahde lahde,
                                Boolean onPakollinen,
                                Boolean epasuoraViittaus,
                                Integer indeksi,
                                Boolean vaatiiOsallistumisen,
                                Boolean syotettavissaKaikille,
                                TekstiRyhmaDTO kuvaukset,
                                KoodiDTO syotettavanarvontyyppi,
                                boolean tilastoidaan) {
    this.tunniste = tunniste;
    this.kuvaus = kuvaus;
    this.lahde = lahde;
    this.onPakollinen = onPakollinen;
    this.epasuoraViittaus = epasuoraViittaus;
    this.indeksi = indeksi;
    this.vaatiiOsallistumisen = vaatiiOsallistumisen;
    this.syotettavissaKaikille = syotettavissaKaikille;
    this.kuvaukset = kuvaukset;
    this.syotettavanarvontyyppi = syotettavanarvontyyppi;
    this.tilastoidaan = tilastoidaan;
  }

  public String getTunniste() {
    return tunniste;
  }

  public void setTunniste(String tunniste) {
    this.tunniste = tunniste;
  }

  public String getKuvaus() {
    return kuvaus;
  }

  public void setKuvaus(String kuvaus) {
    this.kuvaus = kuvaus;
  }

  public Valintaperustelahde getLahde() {
    return lahde;
  }

  public void setLahde(Valintaperustelahde lahde) {
    this.lahde = lahde;
  }

  public Boolean getOnPakollinen() {
    return onPakollinen;
  }

  public void setOnPakollinen(Boolean onPakollinen) {
    this.onPakollinen = onPakollinen;
  }

  public Boolean getEpasuoraViittaus() {
    return epasuoraViittaus;
  }

  public void setEpasuoraViittaus(Boolean epasuoraViittaus) {
    this.epasuoraViittaus = epasuoraViittaus;
  }

  public Integer getIndeksi() {
    return indeksi;
  }

  public void setIndeksi(Integer indeksi) {
    this.indeksi = indeksi;
  }

  @Override
  public int compareTo(ValintaperusteViiteDTO o) {
    return indeksi - o.indeksi;
  }

  public TekstiRyhmaDTO getKuvaukset() {
    return kuvaukset;
  }

  public void setKuvaukset(TekstiRyhmaDTO kuvaukset) {
    this.kuvaukset = kuvaukset;
  }

  public Boolean getVaatiiOsallistumisen() {
    return vaatiiOsallistumisen;
  }

  public void setVaatiiOsallistumisen(Boolean vaatiiOsallistumisen) {
    this.vaatiiOsallistumisen = vaatiiOsallistumisen;
  }

  public Boolean getSyotettavissaKaikille() {
    return syotettavissaKaikille;
  }

  public void setSyotettavissaKaikille(Boolean syotettavissaKaikille) {
    this.syotettavissaKaikille = syotettavissaKaikille;
  }

  public boolean isTilastoidaan() {
    return tilastoidaan;
  }

  public void setTilastoidaan(boolean tilastoidaan) {
    this.tilastoidaan = tilastoidaan;
  }

  public void setSyotettavanarvontyyppi(KoodiDTO syotettavanarvontyyppi) {
    this.syotettavanarvontyyppi = syotettavanarvontyyppi;
  }

  public KoodiDTO getSyotettavanarvontyyppi() {
    return syotettavanarvontyyppi;
  }
}
