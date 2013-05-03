package fi.vm.sade.service.valintaperusteet.model;


import fi.vm.sade.generic.model.BaseEntity;
import org.codehaus.jackson.map.annotate.JsonView;

import javax.persistence.*;

@Table(name="valintaperuste_viite")
@Entity
public class ValintaperusteViite extends BaseEntity {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

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
    @OneToOne(optional = false)
    private Funktiokutsu funktiokutsu;

    @JsonView(JsonViews.Basic.class)
    @Column(name="on_pakollinen", nullable = false)
    private Boolean onPakollinen;

    @JsonView(JsonViews.Basic.class)
    @Column(name="on_paasykoe", nullable = false)
    private Boolean onPaasykoe;

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

    public Boolean getOnPaasykoe() {
        return onPaasykoe;
    }

    public void setOnPaasykoe(Boolean onPaasykoe) {
        this.onPaasykoe = onPaasykoe;
    }
}
