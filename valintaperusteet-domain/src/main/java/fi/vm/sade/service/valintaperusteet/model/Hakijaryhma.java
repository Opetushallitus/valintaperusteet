package fi.vm.sade.service.valintaperusteet.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import fi.vm.sade.generic.model.BaseEntity;
import org.codehaus.jackson.map.annotate.JsonView;

@Entity
@Table(name = "hakijaryhma")
public class Hakijaryhma extends BaseEntity {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Column(name = "oid", nullable = false, unique = true)
    @JsonView(JsonViews.Basic.class)
    private String oid;

    @Column(name = "nimi")
    private String nimi;

    @Column(name = "kasittelyjarjestys")
    private Integer kasittelyjarjestys;

    @ManyToMany(mappedBy = "hakijaryhmat", fetch = FetchType.LAZY)
    private Set<Valintatapajono> jonot = new HashSet<Valintatapajono>();

    @ManyToOne(fetch = FetchType.LAZY)
    private Valintaryhma valintaryhma;

    @Column(name = "on_poissulkeva")
    private Boolean onPoissulkeva;

    public String getNimi() {
        return nimi;
    }

    public void setNimi(String nimi) {
        this.nimi = nimi;
    }

    public Integer getKasittelyjarjestys() {
        return kasittelyjarjestys;
    }

    public void setKasittelyjarjestys(Integer kasittelyjarjestys) {
        this.kasittelyjarjestys = kasittelyjarjestys;
    }

    public Boolean getOnPoissulkeva() {
        return onPoissulkeva;
    }

    public void setOnPoissulkeva(Boolean onPoissulkeva) {
        this.onPoissulkeva = onPoissulkeva;
    }

    public Set<Valintatapajono> getJonot() {
        return jonot;
    }

    public void setJonot(Set<Valintatapajono> jonot) {
        this.jonot = jonot;
    }

    public Valintaryhma getValintaryhma() {
        return valintaryhma;
    }

    public void setValintaryhma(Valintaryhma valintaryhma) {
        this.valintaryhma = valintaryhma;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }
}
