package fi.vm.sade.service.valintaperusteet.model;

import fi.vm.sade.service.valintaperusteet.dto.model.Valintaperustelahde;

public class ValintaperusteViite implements Comparable<ValintaperusteViite> {
  public static final String OSALLISTUMINEN_POSTFIX = "-OSALLISTUMINEN";

  private ValintaperusteViiteId id;

  private long version;

  private String tunniste;

  private String kuvaus;

  private Valintaperustelahde lahde;

  private boolean onPakollinen;

  // Jos valintaperusteen lähde on hakukohde, voidaan epäsuoralla
  // viittauksella hakea
  // hakukohteelta tunniste, jolla viitataan hakemuksen arvoon
  private boolean epasuoraViittaus;

  private int indeksi;

  private TekstiRyhma kuvaukset;

  private boolean vaatiiOsallistumisen;

  private boolean syotettavissaKaikille;

  private Syotettavanarvontyyppi syotettavanarvontyyppi;

  private boolean tilastoidaan;

  public ValintaperusteViite(ValintaperusteViiteId id,
                             long version,
                             String tunniste,
                             String kuvaus,
                             Valintaperustelahde lahde,
                             boolean onPakollinen,
                             boolean epasuoraViittaus,
                             int indeksi,
                             TekstiRyhma kuvaukset,
                             boolean vaatiiOsallistumisen,
                             boolean syotettavissaKaikille,
                             Syotettavanarvontyyppi syotettavanarvontyyppi,
                             boolean tilastoidaan) {
    this.id = id;
    this.version = version;
    this.tunniste = tunniste;
    this.kuvaus = kuvaus;
    this.lahde = lahde;
    this.onPakollinen = onPakollinen;
    this.epasuoraViittaus = epasuoraViittaus;
    this.indeksi = indeksi;
    this.kuvaukset = kuvaukset;
    this.vaatiiOsallistumisen = vaatiiOsallistumisen;
    this.syotettavissaKaikille = syotettavissaKaikille;
    this.syotettavanarvontyyppi = syotettavanarvontyyppi;
    this.tilastoidaan = tilastoidaan;
  }

  public ValintaperusteViiteId getId() {
    return id;
  }

  public String getTunniste() {
    return tunniste;
  }

  public String getKuvaus() {
    return kuvaus;
  }

  public Valintaperustelahde getLahde() {
    return lahde;
  }

  public boolean isOnPakollinen() {
    return onPakollinen;
  }

  public boolean isEpasuoraViittaus() {
    return epasuoraViittaus;
  }

  public int getIndeksi() {
    return indeksi;
  }

  public TekstiRyhma getKuvaukset() {
    return kuvaukset;
  }

  public boolean isVaatiiOsallistumisen() {
    return vaatiiOsallistumisen;
  }

  public boolean isSyotettavissaKaikille() {
    return syotettavissaKaikille;
  }

  public Syotettavanarvontyyppi getSyotettavanarvontyyppi() {
    return syotettavanarvontyyppi;
  }

  public boolean isTilastoidaan() {
    return tilastoidaan;
  }

  public String getOsallistuminenTunniste() {
    if (lahde == Valintaperustelahde.SYOTETTAVA_ARVO && tunniste != null) {
      return tunniste + OSALLISTUMINEN_POSTFIX;
    }

    return null;
  }

  @Override
  public int compareTo(ValintaperusteViite o) {
    return indeksi - o.indeksi;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ValintaperusteViite that = (ValintaperusteViite) o;

    return id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }
}
