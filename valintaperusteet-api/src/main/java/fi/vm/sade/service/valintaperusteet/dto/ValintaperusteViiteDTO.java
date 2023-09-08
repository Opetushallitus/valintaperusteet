package fi.vm.sade.service.valintaperusteet.dto;

import fi.vm.sade.service.valintaperusteet.dto.model.Valintaperustelahde;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ValintaperusteViiteDTO", description = "Valintaperusteviite")
public class ValintaperusteViiteDTO implements Comparable<ValintaperusteViiteDTO> {

  @Schema(description = "tunniste", required = true)
  private String tunniste;

  @Schema(description = "Kuvaus")
  private String kuvaus;

  @Schema(description = "Valintaperusteen lähde", required = true)
  private Valintaperustelahde lahde;

  @Schema(description = "Onko valintaperuste pakollinen", required = true)
  private Boolean onPakollinen;

  // Jos valintaperusteen lähde on hakukohde, voidaan epäsuoralla
  // viittauksella hakea
  // hakukohteelta tunniste, jolla viitataan hakemuksen arvoon
  @Schema(description = "Viitataanko hakemuksen valintaperusteeseen epäsuorasti", required = true)
  private Boolean epasuoraViittaus;

  @Schema(description = "Indeksi", required = true)
  private Integer indeksi;

  @Schema(description = "Vaatii osallistumisen", required = true)
  private Boolean vaatiiOsallistumisen = true;

  @Schema(description = "Syotettavissa kaikille", required = true)
  private Boolean syotettavissaKaikille = true;

  @Schema(description = "Hylkäysperusteen kuvaukset")
  private TekstiRyhmaDTO kuvaukset = new TekstiRyhmaDTO();

  @Schema(description = "Syötettävän arvon tyyppi")
  private KoodiDTO syotettavanarvontyyppi;

  @Schema(description = "Tilastoidaan")
  private boolean tilastoidaan;

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
