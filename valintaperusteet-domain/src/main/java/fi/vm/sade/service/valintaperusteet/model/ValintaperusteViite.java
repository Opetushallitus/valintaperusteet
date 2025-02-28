package fi.vm.sade.service.valintaperusteet.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fi.vm.sade.service.valintaperusteet.dto.model.Valintaperustelahde;

public class ValintaperusteViite implements Comparable<ValintaperusteViite> {
  private static final long serialVersionUID = 1L;

  public static final String OSALLISTUMINEN_POSTFIX = "-OSALLISTUMINEN";

  private String tunniste;

  private String kuvaus;

  private Valintaperustelahde lahde;

  @JsonBackReference private Funktiokutsu funktiokutsu;

  private Boolean onPakollinen;

  // Jos valintaperusteen lähde on hakukohde, voidaan epäsuoralla
  // viittauksella hakea
  // hakukohteelta tunniste, jolla viitataan hakemuksen arvoon
  private Boolean epasuoraViittaus;

  private Integer indeksi;

  @JsonSerialize(using = TekstiRyhmaSerializer.class)
  private TekstiRyhma kuvaukset;

  // VT-854 mahdollistetaan syötettävien arvojen pistesyöttö ilman laskentaa
  private Boolean vaatiiOsallistumisen = true;

  private Boolean syotettavissaKaikille = true;

  private Syotettavanarvontyyppi syotettavanarvontyyppi;

  private Boolean tilastoidaan = false;

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

  public Funktiokutsu getFunktiokutsu() {
    return funktiokutsu;
  }

  public void setFunktiokutsu(Funktiokutsu funktiokutsu) {
    this.funktiokutsu = funktiokutsu;
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

  public void setEpasuoraViittaus(boolean epasuoraViittaus) {
    this.epasuoraViittaus = epasuoraViittaus;
  }

  public Integer getIndeksi() {
    return indeksi;
  }

  public void setIndeksi(Integer indeksi) {
    this.indeksi = indeksi;
  }

  @JsonBackReference
  public String getOsallistuminenTunniste() {
    String osallistuminenTunniste = null;

    switch (lahde) {
      case SYOTETTAVA_ARVO:
        if (tunniste != null) {
          osallistuminenTunniste = tunniste + OSALLISTUMINEN_POSTFIX;
        }
        break;
      default:
        break;
    }

    return osallistuminenTunniste;
  }

  @Override
  public int compareTo(ValintaperusteViite o) {
    return indeksi - o.indeksi;
  }

  public TekstiRyhma getKuvaukset() {
    return kuvaukset;
  }

  public void setKuvaukset(TekstiRyhma kuvaukset) {
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

  public Syotettavanarvontyyppi getSyotettavanarvontyyppi() {
    return syotettavanarvontyyppi;
  }

  public void setSyotettavanarvontyyppi(Syotettavanarvontyyppi syotettavanarvontyyppi) {
    this.syotettavanarvontyyppi = syotettavanarvontyyppi;
  }

  public Boolean getTilastoidaan() {
    return tilastoidaan;
  }

  public void setTilastoidaan(boolean tilastoidaan) {
    this.tilastoidaan = tilastoidaan;
  }
}
