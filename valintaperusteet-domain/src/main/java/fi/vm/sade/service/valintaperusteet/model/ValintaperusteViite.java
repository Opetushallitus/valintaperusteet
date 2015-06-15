package fi.vm.sade.service.valintaperusteet.model;

import javax.persistence.*;

import fi.vm.sade.service.valintaperusteet.dto.model.Valintaperustelahde;

@Table(name = "valintaperuste_viite", uniqueConstraints = @UniqueConstraint(name = "UK_valintaperuste_viite_001", columnNames = {
        "funktiokutsu_id", "indeksi"}))
@Entity
@Cacheable(true)
public class ValintaperusteViite extends BaseEntity implements Comparable<ValintaperusteViite> {
    private static final long serialVersionUID = 1L;

    public final static String OSALLISTUMINEN_POSTFIX = "-OSALLISTUMINEN";

    @Column(name = "tunniste", nullable = false)
    private String tunniste;

    @Column(name = "kuvaus")
    private String kuvaus;

    @Enumerated(EnumType.STRING)
    @Column(name = "lahde", nullable = false)
    private Valintaperustelahde lahde;

    @JoinColumn(name = "funktiokutsu_id", nullable = false)
    @ManyToOne(optional = false)
    private Funktiokutsu funktiokutsu;

    @Column(name = "on_pakollinen", nullable = false)
    private Boolean onPakollinen;

    // Jos valintaperusteen lähde on hakukohde, voidaan epäsuoralla
    // viittauksella hakea
    // hakukohteelta tunniste, jolla viitataan hakemuksen arvoon
    @Column(name = "epasuora_viittaus", nullable = true)
    private Boolean epasuoraViittaus;

    @Column(name = "indeksi", nullable = false)
    private Integer indeksi;

    @JoinColumn(name = "tekstiryhma_id", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    private TekstiRyhma kuvaukset;

    // VT-854 mahdollistetaan syötettävien arvojen pistesyöttö ilman laskentaa
    @Column(name = "vaatii_osallistumisen", nullable = false)
    private Boolean vaatiiOsallistumisen = true;

    @Column(name = "syotettavissa_kaikille", nullable = false)
    private Boolean syotettavissaKaikille = true;

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

    @Transient
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
}
