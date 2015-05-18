package fi.vm.sade.service.valintaperusteet.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import fi.vm.sade.service.valintaperusteet.dto.model.Funktiotyyppi;

/**
 * User: kwuoti Date: 28.1.2013 Time: 9.51
 */
@Entity
@Table(name = "laskentakaava")
@Cacheable(true)
public class Laskentakaava extends BaseEntity implements FunktionArgumentti {
    @Column(name = "on_luonnos", nullable = false)
    private Boolean onLuonnos;

    @Column(name = "nimi", nullable = false)
    private String nimi;

    @Column(name = "kuvaus")
    private String kuvaus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "valintaryhmaviite", nullable = true, unique = false)
    private Valintaryhma valintaryhma;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hakukohdeviite", nullable = true, unique = false)
    private HakukohdeViite hakukohde;

    @JoinColumn(name = "funktiokutsu_id", nullable = false, unique = false)
    @ManyToOne(optional = false, cascade = CascadeType.PERSIST)
    private Funktiokutsu funktiokutsu;

    @Column(name = "tyyppi", nullable = false)
    @Enumerated(EnumType.STRING)
    private Funktiotyyppi tyyppi;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "laskentakaava", cascade = CascadeType.PERSIST)
    private Set<Jarjestyskriteeri> jarjestyskriteerit = new HashSet<Jarjestyskriteeri>();

    public Boolean getOnLuonnos() {
        return onLuonnos;
    }

    public void setOnLuonnos(Boolean onLuonnos) {
        this.onLuonnos = onLuonnos;
    }

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

    public Valintaryhma getValintaryhma() {
        return valintaryhma;
    }

    public void setValintaryhma(Valintaryhma valintaryhma) {
        this.valintaryhma = valintaryhma;
    }

    public HakukohdeViite getHakukohde() {
        return hakukohde;
    }

    public void setHakukohde(HakukohdeViite hakukohde) {
        this.hakukohde = hakukohde;
    }

    public Funktiokutsu getFunktiokutsu() {
        return funktiokutsu;
    }

    public void setFunktiokutsu(Funktiokutsu funktiokutsu) {
        this.funktiokutsu = funktiokutsu;
    }

    public Funktiotyyppi getTyyppi() {
        return tyyppi;
    }

    public void setTyyppi(Funktiotyyppi tyyppi) {
        this.tyyppi = tyyppi;
    }

    public Set<Jarjestyskriteeri> getJarjestyskriteerit() {
        return jarjestyskriteerit;
    }

    public void setJarjestyskriteerit(Set<Jarjestyskriteeri> jarjestyskriteerit) {
        this.jarjestyskriteerit = jarjestyskriteerit;
    }

    @Override
    public Long getId() {
        return super.getId();
    }

    @PrePersist
    @PreUpdate
    private void fixIt() {
        updateTyyppi();
        korjaaFunktiokutsunNimi();
    }

    private void updateTyyppi() {
        if (funktiokutsu != null) {
            tyyppi = funktiokutsu.getFunktionimi().getTyyppi();
        }
    }

    private void korjaaFunktiokutsunNimi() {
        if (funktiokutsu != null) {
            for (Syoteparametri parametri : getFunktiokutsu().getSyoteparametrit()) {
                if (parametri.getAvain().equals("nimi")) {
                    parametri.setArvo(getNimi());
                }
            }
        }
    }
}
