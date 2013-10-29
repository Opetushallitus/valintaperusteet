package fi.vm.sade.service.valintaperusteet.model;


import fi.vm.sade.generic.model.BaseEntity;
import org.codehaus.jackson.map.annotate.JsonView;

import javax.persistence.*;

@Table(name = "valintaperuste_viite", uniqueConstraints = @UniqueConstraint(name = "UK_valintaperuste_viite_001", columnNames = {"funktiokutsu_id", "indeksi"}))
@Entity
@Cacheable(true)
public class ValintaperusteViite extends BaseEntity implements Comparable<ValintaperusteViite> {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public final static String OSALLISTUMINEN_POSTFIX = "-OSALLISTUMINEN";

    @JsonView(JsonViews.Basic.class)
    @Column(name = "tunniste", nullable = false)
    private String tunniste;

    @JsonView(JsonViews.Basic.class)
    @Column(name = "kuvaus")
    private String kuvaus;

    @JsonView(JsonViews.Basic.class)
    @Enumerated(EnumType.STRING)
    @Column(name = "lahde", nullable = false)
    private Valintaperustelahde lahde;

    @JoinColumn(name = "funktiokutsu_id", nullable = false)
    @ManyToOne(optional = false)
    private Funktiokutsu funktiokutsu;

    @JsonView(JsonViews.Basic.class)
    @Column(name = "on_pakollinen", nullable = false)
    private Boolean onPakollinen;

    // Jos valintaperusteen lähde on hakukohde, voidaan epäsuoralla viittauksella hakea
    // hakukohteelta tunniste, jolla viitataan hakemuksen arvoon
    @JsonView(JsonViews.Basic.class)
    @Column(name = "epasuora_viittaus", nullable = true)
    private Boolean epasuoraViittaus;

    @JsonView(JsonViews.Basic.class)
    @Column(name = "indeksi", nullable = false)
    private Integer indeksi;

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
    @JsonView(JsonViews.Basic.class)
    public String getOsallistuminenTunniste() {
        String osallistuminenTunniste = null;

        switch (lahde) {
            case SYOTETTAVA_ARVO:
                osallistuminenTunniste = tunniste + OSALLISTUMINEN_POSTFIX;
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
}
