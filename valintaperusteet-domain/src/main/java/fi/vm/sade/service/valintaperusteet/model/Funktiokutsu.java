package fi.vm.sade.service.valintaperusteet.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.*;

import org.codehaus.jackson.map.annotate.JsonView;

import fi.vm.sade.generic.model.BaseEntity;

@Entity
@Table(name = "funktiokutsu")
@Cacheable(true)
public class Funktiokutsu extends BaseEntity implements FunktionArgumentti {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @JsonView(JsonViews.Basic.class)
    @Enumerated(EnumType.STRING)
    @Column(name = "funktionimi", nullable = false)
    private Funktionimi funktionimi;

    @JsonView(JsonViews.Basic.class)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "funktiokutsu", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
    private Set<Arvokonvertteriparametri> arvokonvertteriparametrit = new HashSet<Arvokonvertteriparametri>();

    @JsonView(JsonViews.Basic.class)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "funktiokutsu", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
    // @Sort(type = SortType.NATURAL)
    @OrderBy("minValue")
    private Set<Arvovalikonvertteriparametri> arvovalikonvertteriparametrit = new TreeSet<Arvovalikonvertteriparametri>();

    @JsonView(JsonViews.Basic.class)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "funktiokutsu", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
    private Set<Syoteparametri> syoteparametrit = new HashSet<Syoteparametri>();

    @JsonView(JsonViews.Basic.class)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
    private Set<Funktioargumentti> funktioargumentit = new HashSet<Funktioargumentti>();

    @JsonView(JsonViews.Basic.class)
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "funktiokutsu", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
    private ValintaperusteViite valintaperuste;

    @Transient
    @JsonView(JsonViews.Basic.class)
    private List<Abstraktivalidointivirhe> validointivirheet = new ArrayList<Abstraktivalidointivirhe>();

    public Funktionimi getFunktionimi() {
        return funktionimi;
    }

    public void setFunktionimi(Funktionimi funktionimi) {
        this.funktionimi = funktionimi;
    }

    public Set<Syoteparametri> getSyoteparametrit() {
        return syoteparametrit;
    }

    public Set<Arvokonvertteriparametri> getArvokonvertteriparametrit() {
        return arvokonvertteriparametrit;
    }

    public void setArvokonvertteriparametrit(Set<Arvokonvertteriparametri> arvokonvertteriparametrit) {
        this.arvokonvertteriparametrit = arvokonvertteriparametrit;
    }

    public Set<Arvovalikonvertteriparametri> getArvovalikonvertteriparametrit() {
        return arvovalikonvertteriparametrit;
    }

    public void setArvovalikonvertteriparametrit(Set<Arvovalikonvertteriparametri> arvovalikonvertteriparametrit) {
        this.arvovalikonvertteriparametrit = arvovalikonvertteriparametrit;
    }

    public void setSyoteparametrit(Set<Syoteparametri> syoteparametrit) {
        this.syoteparametrit = syoteparametrit;
    }

    public Set<Funktioargumentti> getFunktioargumentit() {
        return funktioargumentit;
    }

    public void setFunktioargumentit(Set<Funktioargumentti> funktioargumentit) {
        this.funktioargumentit = funktioargumentit;
    }

    public ValintaperusteViite getValintaperuste() {
        return valintaperuste;
    }

    public void setValintaperuste(ValintaperusteViite valintaperuste) {
        this.valintaperuste = valintaperuste;
    }

    @Override
    @JsonView(JsonViews.Basic.class)
    public Long getId() {
        return super.getId();
    }

    public List<Abstraktivalidointivirhe> getValidointivirheet() {
        return validointivirheet;
    }

    public void setValidointivirheet(List<Abstraktivalidointivirhe> validointivirheet) {
        this.validointivirheet = validointivirheet;
    }
}
