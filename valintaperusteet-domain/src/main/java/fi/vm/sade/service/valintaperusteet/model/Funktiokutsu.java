package fi.vm.sade.service.valintaperusteet.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "funktiokutsu")
@Cacheable(true)
public class Funktiokutsu extends BaseEntity implements FunktionArgumentti {
    private static final long serialVersionUID = 1L;

    @Enumerated(EnumType.STRING)
    @Column(name = "funktionimi", nullable = false)
    private Funktionimi funktionimi;

    @Column(name = "tulos_tunniste")
    private String tulosTunniste;

    @Column(name = "tulos_teksti_fi")
    private String tulosTekstiFi;

    @Column(name = "tulos_teksti_sv")
    private String tulosTekstiSv;

    @Column(name = "tulos_teksti_en")
    private String tulosTekstiEn;

    @Column(name = "tallenna_tulos", nullable = false)
    private Boolean tallennaTulos = false;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "funktiokutsu", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<Arvokonvertteriparametri> arvokonvertteriparametrit = new HashSet<Arvokonvertteriparametri>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "funktiokutsu", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    // @Sort(type = SortType.NATURAL)
    @OrderBy("minValue")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<Arvovalikonvertteriparametri> arvovalikonvertteriparametrit = new HashSet<Arvovalikonvertteriparametri>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "funktiokutsu", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<Syoteparametri> syoteparametrit = new HashSet<Syoteparametri>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    @OrderBy("indeksi")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<Funktioargumentti> funktioargumentit = new TreeSet<Funktioargumentti>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "funktiokutsu", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @OrderBy("indeksi")
    @OnDelete(action = OnDeleteAction.CASCADE)
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

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getTulosTunniste() {
        return tulosTunniste;
    }

    public void setTulosTunniste(String tulosTunniste) {
        this.tulosTunniste = tulosTunniste;
    }

    public String getTulosTekstiFi() {
        return tulosTekstiFi;
    }

    public void setTulosTekstiFi(String tulosTekstiFi) {
        this.tulosTekstiFi = tulosTekstiFi;
    }

    public String getTulosTekstiSv() {
        return tulosTekstiSv;
    }

    public void setTulosTekstiSv(String tulosTekstiSv) {
        this.tulosTekstiSv = tulosTekstiSv;
    }

    public String getTulosTekstiEn() {
        return tulosTekstiEn;
    }

    public void setTulosTekstiEn(String tulosTekstiEn) {
        this.tulosTekstiEn = tulosTekstiEn;
    }

    public Boolean getTallennaTulos() {
        return tallennaTulos;
    }

    public void setTallennaTulos(Boolean tallennaTulos) {
        this.tallennaTulos = tallennaTulos;
    }

    @Override
    public String toString() {
        return "Funktiokutsu{" +
                "funktionimi=" + funktionimi +
                ", tulosTunniste='" + tulosTunniste + '\'' +
                ", tulosTekstiFi='" + tulosTekstiFi + '\'' +
                ", tulosTekstiSv='" + tulosTekstiSv + '\'' +
                ", tulosTekstiEn='" + tulosTekstiEn + '\'' +
                ", tallennaTulos=" + tallennaTulos +
                ", arvokonvertteriparametrit=" + arvokonvertteriparametrit +
                ", arvovalikonvertteriparametrit=" + arvovalikonvertteriparametrit +
                ", syoteparametrit=" + syoteparametrit +
                ", funktioargumentit=" + funktioargumentit +
                ", valintaperusteviitteet=" + valintaperusteviitteet +
                ", validointivirheet=" + validointivirheet +
                ", base=" + super.toString() +
                '}';
    }
}
