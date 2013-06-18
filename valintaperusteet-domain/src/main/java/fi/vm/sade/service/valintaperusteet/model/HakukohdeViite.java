package fi.vm.sade.service.valintaperusteet.model;

import fi.vm.sade.generic.model.BaseEntity;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonView;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * User: kkammone
 * Date: 11.1.2013
 * Time: 14:05
 */
@Entity
@Table(name = "hakukohde_viite")
public class HakukohdeViite extends BaseEntity {

    @Column(name = "hakuoid", nullable = false)
    @JsonView(JsonViews.Basic.class)
    private String hakuoid;

    @JsonView(JsonViews.Basic.class)
    @Column(name = "oid", unique = true, nullable = false)
    private String oid;

    @Column(name = "nimi")
    @JsonView(JsonViews.Basic.class)
    private String nimi;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    private Valintaryhma valintaryhma;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "hakukohdeViite")
    private Set<ValinnanVaihe> valinnanvaiheet = new HashSet<ValinnanVaihe>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "hakukohde")
    private Set<Laskentakaava> laskentakaava = new HashSet<Laskentakaava>();

    @JsonView(JsonViews.Basic.class)
    @JoinColumn(name = "hakukohdekoodi_id", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    private Hakukohdekoodi hakukohdekoodi;

    @JoinTable(name = "hakukohde_viite_opetuskielikoodi",
            joinColumns = @JoinColumn(name = "hakukohde_viite_id", referencedColumnName = BaseEntity.ID_COLUMN_NAME),
            inverseJoinColumns = @JoinColumn(name = "opetuskielikoodi_id", referencedColumnName = BaseEntity.ID_COLUMN_NAME))
    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Opetuskielikoodi> opetuskielet = new HashSet<Opetuskielikoodi>();


    @JoinTable(name = "hakukohde_viite_valintakoe",
            joinColumns = @JoinColumn(name = "hakukohde_viite_id", referencedColumnName = BaseEntity.ID_COLUMN_NAME),
            inverseJoinColumns = @JoinColumn(name = "valintakoe_id", referencedColumnName = BaseEntity.ID_COLUMN_NAME))
    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Valintakoekoodi> valintakokeet = new HashSet<Valintakoekoodi>();


    public String getHakuoid() {
        return hakuoid;
    }

    public void setHakuoid(String hakuoid) {
        this.hakuoid = hakuoid;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getNimi() {
        return nimi;
    }

    public void setNimi(String nimi) {
        this.nimi = nimi;
    }

    public Valintaryhma getValintaryhma() {
        return valintaryhma;
    }

    public void setValintaryhma(Valintaryhma valintaryhma) {
        this.valintaryhma = valintaryhma;
    }

    public Set<ValinnanVaihe> getValinnanvaiheet() {
        return valinnanvaiheet;
    }

    public void setValinnanvaiheet(Set<ValinnanVaihe> valinnanvaiheet) {
        this.valinnanvaiheet = valinnanvaiheet;
    }

    public Set<Laskentakaava> getLaskentakaava() {
        return laskentakaava;
    }

    public void setLaskentakaava(Set<Laskentakaava> laskentakaava) {
        this.laskentakaava = laskentakaava;
    }

    @JsonProperty(value = "valintaryhma_id")
    @JsonView(JsonViews.Basic.class)
    @Transient
    private String getValintaryhmaId() {
        return valintaryhma != null ? valintaryhma.getOid() : "";
    }

    public void addValinnanVaihe(ValinnanVaihe valinnanVaihe) {
        valinnanVaihe.setHakukohdeViite(this);
        this.getValinnanvaiheet().add(valinnanVaihe);
    }

    public Hakukohdekoodi getHakukohdekoodi() {
        return hakukohdekoodi;
    }

    public void setHakukohdekoodi(Hakukohdekoodi hakukohdekoodi) {
        this.hakukohdekoodi = hakukohdekoodi;
    }

    public Set<Opetuskielikoodi> getOpetuskielet() {
        return opetuskielet;
    }

    public void setOpetuskielet(Set<Opetuskielikoodi> opetuskielet) {
        this.opetuskielet = opetuskielet;
    }

    public Set<Valintakoekoodi> getValintakokeet() {
        return valintakokeet;
    }

    public void setValintakokeet(Set<Valintakoekoodi> valintakokeet) {
        this.valintakokeet = valintakokeet;
    }
}
