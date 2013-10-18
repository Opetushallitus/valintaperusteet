package fi.vm.sade.service.valintaperusteet.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import fi.vm.sade.generic.model.BaseEntity;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonView;

@Entity
@Table(name = "hakijaryhma")
@Cacheable(true)
public class Hakijaryhma extends BaseEntity implements LinkitettavaJaKopioitava<Hakijaryhma, Set<Hakijaryhma>> {

    private static final long serialVersionUID = 1L;

    @Column(name = "oid", nullable = false, unique = true)
    @JsonView(JsonViews.Basic.class)
    private String oid;

    @Column
    @JsonView(JsonViews.Basic.class)
    private String nimi;

    @Column
    @JsonView(JsonViews.Basic.class)
    private String kuvaus;

    @Column
    @JsonView(JsonViews.Basic.class)
    private int kiintio;

    @OneToMany(mappedBy = "hakijaryhma", fetch = FetchType.LAZY)
    private Set<HakijaryhmaValintatapajono> jonot = new HashSet<HakijaryhmaValintatapajono>();

    @JoinColumn(name = "valintaryhma_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Valintaryhma valintaryhma;

    @JoinColumn(name = "hakukohde_viite_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private HakukohdeViite hakukohdeViite;

    @JoinColumn(name = "laskentakaava_id", nullable = false)
    @ManyToOne(optional = false)
    private Laskentakaava laskentakaava;

    @JoinColumn(name = "edellinen_hakijaryhma_id")
    @OneToOne(fetch = FetchType.LAZY)
    private Hakijaryhma edellinen;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "edellinen")
    private Hakijaryhma seuraava;

    @JoinColumn(name = "master_hakijaryhma_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Hakijaryhma master;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "master")
    private Set<Hakijaryhma> kopiot = new HashSet<Hakijaryhma>();

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

    public String getKuvaus() {
        return kuvaus;
    }

    public void setKuvaus(String kuvaus) {
        this.kuvaus = kuvaus;
    }

    public int getKiintio() {
        return kiintio;
    }

    public void setKiintio(int kiintio) {
        this.kiintio = kiintio;
    }

    public Set<HakijaryhmaValintatapajono> getJonot() {
        return jonot;
    }

    public void setJonot(Set<HakijaryhmaValintatapajono> jonot) {
        this.jonot = jonot;
    }

    public Valintaryhma getValintaryhma() {
        return valintaryhma;
    }

    public void setValintaryhma(Valintaryhma valintaryhma) {
        this.valintaryhma = valintaryhma;
    }

    public HakukohdeViite getHakukohdeViite() {
        return hakukohdeViite;
    }

    public void setHakukohdeViite(HakukohdeViite hakukohdeViite) {
        this.hakukohdeViite = hakukohdeViite;
    }

    public Laskentakaava getLaskentakaava() {
        return laskentakaava;
    }

    public void setLaskentakaava(Laskentakaava laskentakaava) {
        this.laskentakaava = laskentakaava;
    }

    @Override
    public Hakijaryhma getEdellinen() {
        return edellinen;
    }

    @Override
    public void setEdellinen(Hakijaryhma edellinen) {
        this.edellinen = edellinen;
    }

    @Override
    public Hakijaryhma getSeuraava() {
        return seuraava;
    }

    @Override
    public void setSeuraava(Hakijaryhma seuraava) {
        this.seuraava = seuraava;
    }

    @Override
    public Hakijaryhma getMaster() {
        return master;
    }

    @Override
    public void setMaster(Hakijaryhma master) {
        this.master = master;
    }

    @Override
    public Set<Hakijaryhma> getKopiot() {
        return kopiot;
    }

    @Override
    public void setKopiot(Set<Hakijaryhma> kopiot) {
        this.kopiot = kopiot;
    }

    @JsonProperty(value = "inheritance")
    @JsonView(JsonViews.Basic.class)
    @Transient
    private Boolean getInheritance() {
        return getMaster() != null;
    }

    @JsonProperty("laskentakaava_id")
    @JsonView(JsonViews.Basic.class)
    @Transient
    public Long getLaskentakaavaId() {
        return laskentakaava.getId();
    }

    @JsonProperty("laskentakaava_id")
    @JsonView(JsonViews.Basic.class)
    @Transient
    public void setLaskentakaavaId(Long id) {
        laskentakaava = new Laskentakaava();
        laskentakaava.setId(id);
    }
}
