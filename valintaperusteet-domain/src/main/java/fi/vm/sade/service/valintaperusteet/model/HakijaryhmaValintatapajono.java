package fi.vm.sade.service.valintaperusteet.model;

import fi.vm.sade.generic.model.BaseEntity;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonView;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "hakijaryhma_jono")
@Cacheable(true)
public class HakijaryhmaValintatapajono extends BaseEntity implements LinkitettavaJaKopioitava<HakijaryhmaValintatapajono, Set<HakijaryhmaValintatapajono>> {

    private static final long serialVersionUID = 1L;

    @Column(name = "oid", nullable = false, unique = true)
    private String oid;


    @JoinColumn(name = "hakijaryhma_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Hakijaryhma hakijaryhma;

    @JoinColumn(name = "valintatapajono_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Valintatapajono valintatapajono;

    @Column(name = "aktiivinen", nullable = false)
    private Boolean aktiivinen;

    @JoinColumn(name = "edellinen_hakijaryhma_jono_id")
    @OneToOne(fetch = FetchType.LAZY)
    private HakijaryhmaValintatapajono edellinen;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "edellinen")
    private HakijaryhmaValintatapajono seuraava;

    @JoinColumn(name = "master_hakijaryhma_jono_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private HakijaryhmaValintatapajono master;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "master")
    private Set<HakijaryhmaValintatapajono> kopiot = new HashSet<HakijaryhmaValintatapajono>();

    public Hakijaryhma getHakijaryhma() {
        return hakijaryhma;
    }

    public void setHakijaryhma(Hakijaryhma hakijaryhma) {
        this.hakijaryhma = hakijaryhma;
    }

    public Valintatapajono getValintatapajono() {
        return valintatapajono;
    }

    public void setValintatapajono(Valintatapajono valintatapajono) {
        this.valintatapajono = valintatapajono;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public Boolean getAktiivinen() {
        return aktiivinen;
    }

    public void setAktiivinen(Boolean aktiivinen) {
        this.aktiivinen = aktiivinen;
    }

    @Override
    public HakijaryhmaValintatapajono getEdellinen() {
        return edellinen;
    }

    @Override
    public void setEdellinen(HakijaryhmaValintatapajono edellinen) {
        this.edellinen = edellinen;
    }

    @Override
    public HakijaryhmaValintatapajono getSeuraava() {
        return seuraava;
    }

    @Override
    public void setSeuraava(HakijaryhmaValintatapajono seuraava) {
        this.seuraava = seuraava;
    }

    @Override
    public HakijaryhmaValintatapajono getMaster() {
        return master;
    }

    @Override
    public void setMaster(HakijaryhmaValintatapajono master) {
        this.master = master;
    }

    @Override
    public Set<HakijaryhmaValintatapajono> getKopiot() {
        return kopiot;
    }

    @Override
    public void setKopiot(Set<HakijaryhmaValintatapajono> kopiot) {
        this.kopiot = kopiot;
    }

    @JsonProperty(value = "inheritance")
    @Transient
    public Boolean getInheritance() {
        return getMaster() != null;
    }
}
