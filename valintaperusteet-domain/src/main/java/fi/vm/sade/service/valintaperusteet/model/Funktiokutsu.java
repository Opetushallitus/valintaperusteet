package fi.vm.sade.service.valintaperusteet.model;

import fi.vm.sade.generic.model.BaseEntity;
import org.codehaus.jackson.map.annotate.JsonView;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "funktiokutsu")
@Cacheable(true)
public class Funktiokutsu extends BaseEntity implements FunktionArgumentti {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Enumerated(EnumType.STRING)
    @Column(name = "funktionimi", nullable = false)
    private Funktionimi funktionimi;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "funktiokutsu", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private Set<Arvokonvertteriparametri> arvokonvertteriparametrit = new HashSet<Arvokonvertteriparametri>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "funktiokutsu", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    // @Sort(type = SortType.NATURAL)
    @OrderBy("minValue")
    private Set<Arvovalikonvertteriparametri> arvovalikonvertteriparametrit = new HashSet<Arvovalikonvertteriparametri>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "funktiokutsu", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private Set<Syoteparametri> syoteparametrit = new HashSet<Syoteparametri>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @OrderBy("indeksi")
    private Set<Funktioargumentti> funktioargumentit = new TreeSet<Funktioargumentti>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "funktiokutsu", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @OrderBy("indeksi")
    private Set<ValintaperusteViite> valintaperusteviitteet = new TreeSet<ValintaperusteViite>();

    @Transient
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


    public Set<ValintaperusteViite> getValintaperusteviitteet() {
        return valintaperusteviitteet;
    }

    public void setValintaperusteviitteet(Set<ValintaperusteViite> valintaperusteviitteet) {
        this.valintaperusteviitteet = valintaperusteviitteet;
    }

    @Override
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
