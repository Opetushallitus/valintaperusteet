package fi.vm.sade.service.valintaperusteet.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "valintaryhma")
@Cacheable(true)
public class Valintaryhma extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @Column(name = "oid", nullable = false, unique = true)
    private String oid;

    @Column(name = "nimi", nullable = false)
    private String nimi;

    @Column(name = "kohdejoukko", nullable = true)
    private String kohdejoukko;

    @Column(name = "hakuoid", nullable = true)
    private String hakuoid;

    @Column(name = "hakuvuosi", nullable = true)
    private String hakuvuosi;

    @JoinColumn(name = "parent_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Valintaryhma ylavalintaryhma;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "ylavalintaryhma")
    private Set<Valintaryhma> alavalintaryhmat = new HashSet<Valintaryhma>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "valintaryhma")
    private Set<ValinnanVaihe> valinnanvaiheet = new HashSet<ValinnanVaihe>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "valintaryhma")
    private Set<Hakijaryhma> hakijaryhmat = new HashSet<Hakijaryhma>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "valintaryhma")
    private Set<HakukohdeViite> hakukohdeViitteet = new HashSet<HakukohdeViite>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "valintaryhma")
    private Set<Laskentakaava> laskentakaava = new HashSet<Laskentakaava>();

    @JoinTable(name = "valintaryhma_hakukohdekoodi",
            joinColumns = @JoinColumn(name = "valintaryhma_id", referencedColumnName = BaseEntity.ID_COLUMN_NAME),
            inverseJoinColumns = @JoinColumn(name = "hakukohdekoodi_id", referencedColumnName = BaseEntity.ID_COLUMN_NAME),
            uniqueConstraints = @UniqueConstraint(name = "UK_valintaryhma_hakukohdekoodi_001",
                    columnNames = {"valintaryhma_id", "hakukohdekoodi_id"}))
    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Hakukohdekoodi> hakukohdekoodit = new HashSet<Hakukohdekoodi>();

    @JoinTable(name = "valintaryhma_valintakoekoodi",
            joinColumns = @JoinColumn(name = "valintaryhma_id", referencedColumnName = BaseEntity.ID_COLUMN_NAME),
            inverseJoinColumns = @JoinColumn(name = "valintakoekoodi_id", referencedColumnName = BaseEntity.ID_COLUMN_NAME))
    @ManyToMany(fetch = FetchType.LAZY)
    private List<Valintakoekoodi> valintakoekoodit = new ArrayList<Valintakoekoodi>();

    @JoinTable(name = "valintaryhma_organisaatio",
            joinColumns = @JoinColumn(name = "valintaryhma_id", referencedColumnName = BaseEntity.ID_COLUMN_NAME),
            inverseJoinColumns = @JoinColumn(name = "organisaatio_id", referencedColumnName = BaseEntity.ID_COLUMN_NAME))
    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Organisaatio> organisaatiot = new HashSet<Organisaatio>();

    @JoinColumn(name = "vastuuorganisaatio_id", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    private Organisaatio vastuuorganisaatio;

    public String getNimi() {
        return nimi;
    }

    public void setNimi(String nimi) {
        this.nimi = nimi;
    }

    public Valintaryhma getYlavalintaryhma() {
        return ylavalintaryhma;
    }

    public void setYlavalintaryhma(Valintaryhma ylavalintaryhma) {
        this.ylavalintaryhma = ylavalintaryhma;
    }

    public Set<Valintaryhma> getAlavalintaryhmat() {
        if (alavalintaryhmat == null) {
            alavalintaryhmat = new HashSet<Valintaryhma>();
        }
        return alavalintaryhmat;
    }

    public Set<ValinnanVaihe> getValinnanvaiheet() {
        return valinnanvaiheet;
    }

    public Set<Hakijaryhma> getHakijaryhmat() {
        return hakijaryhmat;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public Set<HakukohdeViite> getHakukohdeViitteet() {
        return hakukohdeViitteet;
    }

    public void setHakukohdeViitteet(Set<HakukohdeViite> hakukohdeViitteet) {
        this.hakukohdeViitteet = hakukohdeViitteet;
    }

    @Transient
    public boolean getLapsivalintaryhma() {
        return alavalintaryhmat.size() > 0 ? true : false;
    }

    @Transient
    public boolean getLapsihakukohde() {
        return hakukohdeViitteet.size() > 0 ? true : false;
    }

    public void addValinnanVaihe(ValinnanVaihe valinnanVaihe) {
        valinnanVaihe.setValintaryhma(this);
        this.getValinnanvaiheet().add(valinnanVaihe);
    }

    public void addHakijaryhma(Hakijaryhma hakijaryhma) {
        hakijaryhma.setValintaryhma(this);
        this.getHakijaryhmat().add(hakijaryhma);
    }

    public Set<Laskentakaava> getLaskentakaava() {
        return laskentakaava;
    }

    public void setLaskentakaava(Set<Laskentakaava> laskentakaava) {
        this.laskentakaava = laskentakaava;
    }

    public Set<Hakukohdekoodi> getHakukohdekoodit() {
        return hakukohdekoodit;
    }

    public void setHakukohdekoodit(Set<Hakukohdekoodi> hakukohdekoodit) {
        this.hakukohdekoodit = hakukohdekoodit;
    }

    public List<Valintakoekoodi> getValintakoekoodit() {
        return valintakoekoodit;
    }

    public void setValintakoekoodit(List<Valintakoekoodi> valintakoekoodit) {
        this.valintakoekoodit = valintakoekoodit;
    }

    public void setHakijaryhmat(Set<Hakijaryhma> hakijaryhmat) {
        this.hakijaryhmat = hakijaryhmat;
    }

    public Set<Organisaatio> getOrganisaatiot() {
        return organisaatiot;
    }

    public void setOrganisaatiot(Set<Organisaatio> organisaatiot) {
        this.organisaatiot = organisaatiot;
    }

    public Organisaatio getVastuuorganisaatio() {
        return vastuuorganisaatio;
    }

    public void setVastuuorganisaatio(Organisaatio vastuuorganisaatio) {
        this.vastuuorganisaatio = vastuuorganisaatio;
    }

    public String getKohdejoukko() {
        return kohdejoukko;
    }

    public void setKohdejoukko(String kohdejoukko) {
        this.kohdejoukko = kohdejoukko;
    }

    public String getHakuoid() {
        return hakuoid;
    }

    public void setHakuoid(String hakuoid) {
        this.hakuoid = hakuoid;
    }

    public String getHakuvuosi() {
        return hakuvuosi;
    }

    public void setHakuvuosi(String hakuvuosi) {
        this.hakuvuosi = hakuvuosi;
    }
}
