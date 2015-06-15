package fi.vm.sade.service.valintaperusteet.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "jarjestyskriteeri")
@Cacheable(true)
public class Jarjestyskriteeri extends BaseEntity implements LinkitettavaJaKopioitava<Jarjestyskriteeri, Set<Jarjestyskriteeri>> {
    private static final long serialVersionUID = 1L;

    @Column(name = "oid", nullable = false, unique = true)
    private String oid;

    @JoinColumn(name = "valintatapajono_id", nullable = false)
    @ManyToOne(optional = false)
    private Valintatapajono valintatapajono;

    @JoinColumn(name = "laskentakaava_id", nullable = false)
    @ManyToOne(optional = false)
    private Laskentakaava laskentakaava;

    @Column(name = "metatiedot")
    private String metatiedot;

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

    public Long getLaskentakaavaId() {
        return laskentakaava.getId();
    }

    public String getValintatapajonoId() {
        return valintatapajono.getOid();
    }

    public Boolean getInheritance() {
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
