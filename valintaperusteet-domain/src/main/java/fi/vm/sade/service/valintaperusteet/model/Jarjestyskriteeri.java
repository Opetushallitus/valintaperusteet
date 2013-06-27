package fi.vm.sade.service.valintaperusteet.model;


import fi.vm.sade.generic.model.BaseEntity;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonView;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "jarjestyskriteeri")
public class Jarjestyskriteeri extends BaseEntity implements LinkitettavaJaKopioitava<Jarjestyskriteeri, Set<Jarjestyskriteeri>> {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Column(name = "oid", nullable = false, unique = true)
    @JsonView(JsonViews.Basic.class)
    private String oid;

    @JoinColumn(name = "valintatapajono_id", nullable = false)
    @ManyToOne(optional = false)
    private Valintatapajono valintatapajono;

    @JoinColumn(name = "laskentakaava_id", nullable = false)
    @ManyToOne(optional = false)
    private Laskentakaava laskentakaava;

    @Column(name = "metatiedot")
    @JsonView(JsonViews.Basic.class)
    private String metatiedot;

    @JsonView(JsonViews.Basic.class)
    @Column(name = "aktiivinen", nullable = false)
    private Boolean aktiivinen;

    @JoinColumn(name = "edellinen_jarjestyskriteeri_id")
    @OneToOne(fetch = FetchType.LAZY)
    private Jarjestyskriteeri edellinen;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "edellinen")
    private Jarjestyskriteeri seuraava;

    @JoinColumn(name = "master_jarjestyskriteeri_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Jarjestyskriteeri master;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "master")
    private Set<Jarjestyskriteeri> kopiot = new HashSet<Jarjestyskriteeri>();

    public Valintatapajono getValintatapajono() {
        return valintatapajono;
    }

    public void setValintatapajono(Valintatapajono valintatapajono) {
        this.valintatapajono = valintatapajono;
    }

    public Laskentakaava getLaskentakaava() {
        return laskentakaava;
    }

    public void setLaskentakaava(Laskentakaava laskentakaava) {
        this.laskentakaava = laskentakaava;
    }

    public String getMetatiedot() {
        return metatiedot;
    }

    public void setMetatiedot(String metatiedot) {
        this.metatiedot = metatiedot;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    @JsonProperty("laskentakaava_id")
    @JsonView(JsonViews.Basic.class)
    @Transient
    public Long getLaskentakaavaId() {
        return laskentakaava.getId();
    }

    @JsonProperty("valintatapajono_oid")
    @JsonView(JsonViews.Basic.class)
    @Transient
    public String getValintatapajonoId() {
        return valintatapajono.getOid();
    }

    @JsonProperty(value = "inheritance")
    @JsonView(JsonViews.Basic.class)
    @Transient
    private Boolean getInheritance() {
        return getMaster() != null;
    }

    public Boolean getAktiivinen() {
        return aktiivinen;
    }

    public void setAktiivinen(Boolean aktiivinen) {
        this.aktiivinen = aktiivinen;
    }

    @Override
    public Jarjestyskriteeri getEdellinen() {
        return edellinen;
    }

    @Override
    public void setEdellinen(Jarjestyskriteeri edellinen) {
        this.edellinen = edellinen;
    }

    @Override
    public Jarjestyskriteeri getSeuraava() {
        return seuraava;
    }

    @Override
    public void setSeuraava(Jarjestyskriteeri seuraava) {
        this.seuraava = seuraava;
    }

    @Override
    public Jarjestyskriteeri getMaster() {
        return master;
    }

    @Override
    public void setMaster(Jarjestyskriteeri master) {
        this.master = master;
    }

    @Override
    public Set<Jarjestyskriteeri> getKopiot() {
        return kopiot;
    }

    @Override
    public void setKopiot(Set<Jarjestyskriteeri> kopiot) {
        this.kopiot = kopiot;
    }
}
