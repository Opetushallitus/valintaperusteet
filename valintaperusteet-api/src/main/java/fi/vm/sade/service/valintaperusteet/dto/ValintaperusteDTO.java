package fi.vm.sade.service.valintaperusteet.dto;

import fi.vm.sade.service.valintaperusteet.dto.model.Funktiotyyppi;
import fi.vm.sade.service.valintaperusteet.dto.model.Valintaperustelahde;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(name = "ValintaperusteDTO", description = "Valintaperuste")
public class ValintaperusteDTO {

  @Schema(description = "Tunniste")
  private String tunniste;

  @Schema(description = "Kuvaus")
  private String kuvaus;

  @Schema(description = "Funktiotyyppi")
  private Funktiotyyppi funktiotyyppi;

  @Schema(description = "Valintaperusteen lähde")
  private Valintaperustelahde lahde;

  @Schema(
      description =
          "Onko valintaperuste pakollinen, eli laskenta merkitsee virheelliseksi hakemuksen millä ei ole arvoa tunnisteelle.")
  private boolean onPakollinen;

  @Schema(description = "Arvovälin minimi")
  private String min;

  @Schema(description = "Arvovälin maksimi")
  private String max;

  @Schema(description = "Arvot")
  private List<String> arvot;

  @Schema(description = "Osallistumistunniste")
  private String osallistuminenTunniste;

  @Schema(description = "Vaatiiko syötettävä arvo osallistumisen")
  private Boolean vaatiiOsallistumisen = true;

  @Schema(description = "Voidaanko arvo syöttää kaikille vai vaan kutsutuille")
  private Boolean syotettavissaKaikille = true;

  @Schema(description = "Tilastoidaanko")
  private Boolean tilastoidaan = null;

  @Schema(description = "Syötettävän arvon tyyppi")
  private KoodiDTO syötettavanArvonTyyppi = null;

  public Boolean getTilastoidaan() {
    return tilastoidaan;
  }

  public void setTilastoidaan(Boolean tilastoidaan) {
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

  public Funktiotyyppi getFunktiotyyppi() {
    return funktiotyyppi;
  }

  public void setFunktiotyyppi(Funktiotyyppi funktiotyyppi) {
    this.funktiotyyppi = funktiotyyppi;
  }

  public Valintaperustelahde getLahde() {
    return lahde;
  }

  public void setLahde(Valintaperustelahde lahde) {
    this.lahde = lahde;
  }

  public boolean isOnPakollinen() {
    return onPakollinen;
  }

  public void setOnPakollinen(boolean onPakollinen) {
    this.onPakollinen = onPakollinen;
  }

  public String getMin() {
    return min;
  }

  public void setMin(String min) {
    this.min = min;
  }

  public String getMax() {
    return max;
  }

  public void setMax(String max) {
    this.max = max;
  }

  public List<String> getArvot() {
    return arvot;
  }

  public void setArvot(List<String> arvot) {
    this.arvot = arvot;
  }

  public String getOsallistuminenTunniste() {
    return osallistuminenTunniste;
  }

  public void setOsallistuminenTunniste(String osallistuminenTunniste) {
    this.osallistuminenTunniste = osallistuminenTunniste;
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

  public KoodiDTO getSyötettavanArvonTyyppi() {
    return syötettavanArvonTyyppi;
  }

  public void setSyötettavanArvonTyyppi(KoodiDTO syötettavanArvonTyyppi) {
    this.syötettavanArvonTyyppi = syötettavanArvonTyyppi;
  }
}
