package fi.vm.sade.service.valintaperusteet.model;

import fi.vm.sade.generic.model.BaseEntity;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonView;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "valintaryhma")
@XmlRootElement
@Cacheable(true)
public class Valintaryhma extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "oid", nullable = false, unique = true)
    @JsonView({JsonViews.Basic.class, JsonViews.ParentHierarchy.class})
    private String oid;

    @Column(name = "nimi", nullable = false)
    @JsonView({JsonViews.Basic.class, JsonViews.ParentHierarchy.class})
    private String nimi;

    @Column(name = "hakuOid", nullable = false)
    //@JsonView({JsonViews.Basic.class, JsonViews.ParentHierarchy.class})
    private String hakuOid;

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

    @JsonView({JsonViews.Basic.class})
    @JoinTable(name = "valintaryhma_hakukohdekoodi",
            joinColumns = @JoinColumn(name = "valintaryhma_id", referencedColumnName = BaseEntity.ID_COLUMN_NAME),
            inverseJoinColumns = @JoinColumn(name = "hakukohdekoodi_id", referencedColumnName = BaseEntity.ID_COLUMN_NAME),
            uniqueConstraints = @UniqueConstraint(name = "UK_valintaryhma_hakukohdekoodi_001",
                    columnNames = {"valintaryhma_id", "hakukohdekoodi_id"}))
    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Hakukohdekoodi> hakukohdekoodit = new HashSet<Hakukohdekoodi>();

    @JsonView({JsonViews.Basic.class})
    @JoinTable(name = "valintaryhma_opetuskielikoodi",
            joinColumns = @JoinColumn(name = "valintaryhma_id", referencedColumnName = BaseEntity.ID_COLUMN_NAME),
            inverseJoinColumns = @JoinColumn(name = "opetuskielikoodi_id", referencedColumnName = BaseEntity.ID_COLUMN_NAME),
            uniqueConstraints = @UniqueConstraint(name = "UK_valintaryhma_opetuskielikoodi_001",
                    columnNames = {"valintaryhma_id", "opetuskielikoodi_id"}))
    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Opetuskielikoodi> opetuskielikoodit = new HashSet<Opetuskielikoodi>();

    @JsonView({JsonViews.Basic.class})
    @JoinTable(name = "valintaryhma_valintakoekoodi",
            joinColumns = @JoinColumn(name = "valintaryhma_id", referencedColumnName = BaseEntity.ID_COLUMN_NAME),
            inverseJoinColumns = @JoinColumn(name = "valintakoekoodi_id", referencedColumnName = BaseEntity.ID_COLUMN_NAME))
    @ManyToMany(fetch = FetchType.LAZY)
    private List<Valintakoekoodi> valintakoekoodit = new ArrayList<Valintakoekoodi>();

    public String getNimi() {
        return nimi;
    }

    public String getHakuOid() {
        return hakuOid;
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

    public void setHakuOid(String hakuOid) {
        this.hakuOid = hakuOid;
    }

    public Set<HakukohdeViite> getHakukohdeViitteet() {
        return hakukohdeViitteet;
    }

    public void setHakukohdeViitteet(Set<HakukohdeViite> hakukohdeViitteet) {
        this.hakukohdeViitteet = hakukohdeViitteet;
    }

    @JsonProperty(value = "lapsivalintaryhma")
    @JsonView(JsonViews.Basic.class)
    @Transient
    public boolean lapsivalintaryhma() {
        return alavalintaryhmat.size() > 0 ? true : false;
    }

    @JsonProperty(value = "lapsihakukohde")
    @JsonView(JsonViews.Basic.class)
    @Transient
    public boolean lapsihakukohde() {
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

    public Set<Opetuskielikoodi> getOpetuskielikoodit() {
        return opetuskielikoodit;
    }

    public void setOpetuskielikoodit(Set<Opetuskielikoodi> opetuskielikoodit) {
        this.opetuskielikoodit = opetuskielikoodit;
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
}
