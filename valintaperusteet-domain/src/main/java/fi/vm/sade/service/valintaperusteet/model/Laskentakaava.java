package fi.vm.sade.service.valintaperusteet.model;

import fi.vm.sade.generic.model.BaseEntity;
import org.codehaus.jackson.map.annotate.JsonView;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * User: kwuoti
 * Date: 28.1.2013
 * Time: 9.51
 */
@Entity
@Table(name="laskentakaava")
public class Laskentakaava extends BaseEntity implements FunktionArgumentti {
    @JsonView({JsonViews.Basic.class, JsonViews.Laskentakaava.class})
    @Column(name = "on_luonnos", nullable = false)
    private Boolean onLuonnos;

    @JsonView(JsonViews.Basic.class)
    @Column(name = "nimi", nullable = false)
    private String nimi;

    @JsonView(JsonViews.Basic.class)
    @Column(name = "kuvaus")
    private String kuvaus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "valintaryhmaviite", nullable = true, unique = false)
    @JsonView(JsonViews.Laskentakaava.class)
    private Valintaryhma valintaryhma;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hakukohdeviite", nullable = true, unique = false)
    private HakukohdeViite hakukohde;

    @JsonView({JsonViews.Basic.class, JsonViews.Laskentakaava.class})
    @JoinColumn(name = "funktiokutsu_id", nullable = false, unique = false)
    @ManyToOne(optional = false, cascade = CascadeType.PERSIST)
    private Funktiokutsu funktiokutsu;

    @JsonView({JsonViews.Basic.class, JsonViews.Laskentakaava.class})
    @Column(name="tyyppi", nullable = false)
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

    @JsonView({JsonViews.Basic.class, JsonViews.Laskentakaava.class})
    public String getNimi() {
        return nimi;
    }

    public void setNimi(String nimi) {
        this.nimi = nimi;
    }

    @JsonView({JsonViews.Basic.class, JsonViews.Laskentakaava.class})
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

    @JsonView({JsonViews.Basic.class, JsonViews.Laskentakaava.class})
    @Override
    public Long getId() {
        return super.getId();
    }

    @PrePersist
    @PreUpdate
    private void updateTyyppi() {
        if(funktiokutsu != null) {
            tyyppi = funktiokutsu.getFunktionimi().getTyyppi();
        }
    }

}
