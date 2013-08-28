package fi.vm.sade.service.valintaperusteet.model;

import fi.vm.sade.generic.model.BaseEntity;
import org.codehaus.jackson.map.annotate.JsonView;

import javax.persistence.*;

/**
 * User: kwuoti
 * Date: 17.1.2013
 * Time: 16.12
 */
@Entity
@Table(name="syoteparametri")
@Cacheable(true)
public class Syoteparametri extends BaseEntity {


    @JsonView(JsonViews.Basic.class)
    @Column(name="avain", nullable = false)
    private String avain;

    @JsonView(JsonViews.Basic.class)
    @Column(name="arvo", nullable = false)
    private String arvo;

    @JoinColumn(name = "funktiokutsu_id", nullable = false)
    @ManyToOne(optional = false)
    private Funktiokutsu funktiokutsu;

    public String getAvain() {
        return avain;
    }

    public void setAvain(String avain) {
        this.avain = avain;
    }

    public String getArvo() {
        return arvo;
    }

    public void setArvo(String arvo) {
        this.arvo = arvo;
    }

    public Funktiokutsu getFunktiokutsu() {
        return funktiokutsu;
    }

    public void setFunktiokutsu(Funktiokutsu funktiokutsu) {
        this.funktiokutsu = funktiokutsu;
    }
}
