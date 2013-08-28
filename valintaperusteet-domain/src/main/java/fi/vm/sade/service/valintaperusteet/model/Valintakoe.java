package fi.vm.sade.service.valintaperusteet.model;

import fi.vm.sade.generic.model.BaseEntity;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonView;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * User: kwuoti
 * Date: 15.4.2013
 * Time: 15.55
 */
@Entity
@Table(name = "valintakoe")
@Cacheable(true)
public class Valintakoe extends BaseEntity implements Kopioitava<Valintakoe, Set<Valintakoe>> {

    @JsonView(JsonViews.Basic.class)
    @Column(name = "oid", nullable = false, unique = true)
    private String oid;

    @JsonView(JsonViews.Basic.class)
    @Column(name = "tunniste", nullable = false)
    private String tunniste;

    @Column(name = "nimi", nullable = false)
    @JsonView(JsonViews.Basic.class)
    private String nimi;

    @Column(name = "kuvaus")
    @JsonView(JsonViews.Basic.class)
    private String kuvaus;

    @JsonView(JsonViews.Basic.class)
    @Column(name = "aktiivinen", nullable = false)
    private Boolean aktiivinen;

    @JoinColumn(name = "laskentakaava_id", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    private Laskentakaava laskentakaava;

    @JoinColumn(name = "valinnan_vaihe_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private ValinnanVaihe valinnanVaihe;

    @JoinColumn(name = "master_valintakoe_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Valintakoe masterValintakoe;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "masterValintakoe")
    private Set<Valintakoe> kopioValintakokeet = new HashSet<Valintakoe>();

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getTunniste() {
        return tunniste;
    }

    public void setTunniste(String tunniste) {
        this.tunniste = tunniste;
    }

    public String getNimi() {
        return nimi;
    }

    public void setNimi(String nimi) {
        this.nimi = nimi;
    }

    public String getKuvaus() {
        return kuvaus;
    }

    public void setKuvaus(String kuvaus) {
        this.kuvaus = kuvaus;
    }

    public Boolean getAktiivinen() {
        return aktiivinen;
    }

    public void setAktiivinen(Boolean aktiivinen) {
        this.aktiivinen = aktiivinen;
    }

    public Laskentakaava getLaskentakaava() {
        return laskentakaava;
    }

    public void setLaskentakaava(Laskentakaava laskentakaava) {
        this.laskentakaava = laskentakaava;
    }

    public ValinnanVaihe getValinnanVaihe() {
        return valinnanVaihe;
    }

    public void setValinnanVaihe(ValinnanVaihe valinnanVaihe) {
        this.valinnanVaihe = valinnanVaihe;
    }

    public Valintakoe getMasterValintakoe() {
        return masterValintakoe;
    }

    public void setMasterValintakoe(Valintakoe masterValintakoe) {
        this.masterValintakoe = masterValintakoe;
    }

    public Set<Valintakoe> getKopioValintakokeet() {
        return kopioValintakokeet;
    }

    public void setKopioValintakokeet(Set<Valintakoe> kopioValintakokeet) {
        this.kopioValintakokeet = kopioValintakokeet;
    }

    @Transient
    public boolean ainaPakollinen() {
        return laskentakaava == null;
    }

    @JsonProperty("laskentakaavaId")
    @JsonView(JsonViews.Basic.class)
    @Transient
    public Long getLaskentakaavaId() {
        return laskentakaava != null ? laskentakaava.getId() : null;
    }

    @Transient
    @Override
    public void setMaster(Valintakoe master) {
        this.masterValintakoe = master;
    }

    @Transient
    @Override
    public Valintakoe getMaster() {
        return this.masterValintakoe;
    }

    @Transient
    @Override
    public void setKopiot(Set<Valintakoe> kopiot) {
        this.kopioValintakokeet = kopiot;
    }

    @Transient
    @Override
    public Set<Valintakoe> getKopiot() {
        return this.kopioValintakokeet;
    }
}
