package fi.vm.sade.service.valintaperusteet.model;

import fi.vm.sade.generic.model.BaseEntity;
import org.codehaus.jackson.map.annotate.JsonView;

import javax.persistence.*;

/**
 * User: tommiha
 * Date: 1/14/13
 * Time: 10:54 AM
 */
@MappedSuperclass
public abstract class Konvertteriparametri extends BaseEntity {
    @Column(name = "paluuarvo")
    private String paluuarvo;



    @JoinColumn(name = "funktiokutsu_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Funktiokutsu funktiokutsu;

    public String getPaluuarvo() {
        return paluuarvo;
    }

    public void setPaluuarvo(String paluuarvo) {
        this.paluuarvo = paluuarvo;
    }

    public Funktiokutsu getFunktiokutsu() {
        return funktiokutsu;
    }

    public void setFunktiokutsu(Funktiokutsu funktiokutsu) {
        this.funktiokutsu = funktiokutsu;
    }
}
