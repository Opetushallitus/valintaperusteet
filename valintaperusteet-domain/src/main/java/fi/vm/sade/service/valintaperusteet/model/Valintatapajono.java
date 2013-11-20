package fi.vm.sade.service.valintaperusteet.model;

import fi.vm.sade.generic.model.BaseEntity;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonView;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "valintatapajono")
@Cacheable(true)
public class Valintatapajono extends BaseEntity implements LinkitettavaJaKopioitava<Valintatapajono, Set<Valintatapajono>> {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Column(name = "oid", nullable = false, unique = true)
    @JsonView(JsonViews.Basic.class)
    private String oid;

    @Column(name = "aloituspaikat", nullable = false)
    @JsonView(JsonViews.Basic.class)
    private Integer aloituspaikat;

    @Column(name = "nimi", nullable = false)
    @JsonView(JsonViews.Basic.class)
    private String nimi;

    @Column(name = "kuvaus")
    @JsonView(JsonViews.Basic.class)
    private String kuvaus;

    @Column(name = "siirretaan_sijoitteluun", nullable = false)
    @JsonView(JsonViews.Basic.class)
    private Boolean siirretaanSijoitteluun = false;

    @Column(name = "tasapistesaanto", nullable = false)
    @Enumerated(EnumType.STRING)
    @JsonView(JsonViews.Basic.class)
    private Tasapistesaanto tasapistesaanto;

    @JsonView(JsonViews.Basic.class)
    @Column(name = "aktiivinen", nullable = false)
    private Boolean aktiivinen;

    @JsonView(JsonViews.Basic.class)
    @Column(name = "ei_varasijatayttoa", nullable = false)
    private Boolean eiVarasijatayttoa = false;

    @Column(name = "varasijat", nullable = false)
    @JsonView(JsonViews.Basic.class)
    private Integer varasijat = 0;

    @Column(name = "varasija_taytto_paivat", nullable = false)
    @JsonView(JsonViews.Basic.class)
    private Integer varasijaTayttoPaivat = 0;

    @JsonView(JsonViews.Basic.class)
    @Column(name = "poissa_oleva_taytto", nullable = false)
    private Boolean poissaOlevaTaytto = false;

    @JoinColumn(name = "edellinen_valintatapajono_id")
    @OneToOne(fetch = FetchType.LAZY)
    private Valintatapajono edellinenValintatapajono;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "edellinenValintatapajono")
    private Valintatapajono seuraavaValintatapajono;

    @JoinColumn(name = "valinnan_vaihe_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private ValinnanVaihe valinnanVaihe;

    @JoinColumn(name = "master_valintatapajono_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Valintatapajono masterValintatapajono;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "masterValintatapajono")
    private Set<Valintatapajono> kopioValintatapajonot = new HashSet<Valintatapajono>();

    @OneToMany(mappedBy = "valintatapajono", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private Set<HakijaryhmaValintatapajono> hakijaryhmat = new HashSet<HakijaryhmaValintatapajono>();

    @OneToMany(mappedBy = "valintatapajono", cascade = CascadeType.REMOVE)
    private Set<Jarjestyskriteeri> jarjestyskriteerit = new HashSet<Jarjestyskriteeri>();

    public Integer getAloituspaikat() {
        return aloituspaikat;
    }

    public void setAloituspaikat(Integer aloituspaikat) {
        this.aloituspaikat = aloituspaikat;
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

    public Boolean getSiirretaanSijoitteluun() {
        return siirretaanSijoitteluun;
    }

    public void setSiirretaanSijoitteluun(Boolean siirretaanSijoitteluun) {
        this.siirretaanSijoitteluun = siirretaanSijoitteluun;
    }

    public Tasapistesaanto getTasapistesaanto() {
        return tasapistesaanto;
    }

    public void setTasapistesaanto(Tasapistesaanto tasapistesaanto) {
        this.tasapistesaanto = tasapistesaanto;
    }

    public Boolean getAktiivinen() {
        return aktiivinen;
    }

    public void setAktiivinen(Boolean aktiivinen) {
        this.aktiivinen = aktiivinen;
    }

    public Valintatapajono getEdellinenValintatapajono() {
        return edellinenValintatapajono;
    }

    public void setEdellinenValintatapajono(Valintatapajono edellinenValintatapajono) {
        this.edellinenValintatapajono = edellinenValintatapajono;
    }

    public Valintatapajono getSeuraavaValintatapajono() {
        return seuraavaValintatapajono;
    }

    public void setSeuraavaValintatapajono(Valintatapajono seuraavaValintatapajono) {
        this.seuraavaValintatapajono = seuraavaValintatapajono;
    }

    public Valintatapajono getMasterValintatapajono() {
        return masterValintatapajono;
    }

    public void setMasterValintatapajono(Valintatapajono masterValintatapajono) {
        this.masterValintatapajono = masterValintatapajono;
    }

    public Set<Valintatapajono> getKopioValintatapajonot() {
        return kopioValintatapajonot;
    }

    public void setKopioValintatapajonot(Set<Valintatapajono> kopiot) {
        this.kopioValintatapajonot = kopiot;
    }

    public ValinnanVaihe getValinnanVaihe() {
        return valinnanVaihe;
    }

    public void setValinnanVaihe(ValinnanVaihe valinnanVaihe) {
        this.valinnanVaihe = valinnanVaihe;
    }

    public Set<HakijaryhmaValintatapajono> getHakijaryhmat() {
        return hakijaryhmat;
    }

    public Set<Jarjestyskriteeri> getJarjestyskriteerit() {
        return jarjestyskriteerit;
    }

    public void setJarjestyskriteerit(Set<Jarjestyskriteeri> jarjestyskriteerit) {
        this.jarjestyskriteerit = jarjestyskriteerit;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public Integer getVarasijat() {
        return varasijat;
    }

    public void setVarasijat(Integer varasijat) {
        this.varasijat = varasijat;
    }

    public Integer getVarasijaTayttoPaivat() {
        return varasijaTayttoPaivat;
    }

    public void setVarasijaTayttoPaivat(Integer varasijaTayttoPaivat) {
        this.varasijaTayttoPaivat = varasijaTayttoPaivat;
    }

    public Boolean getPoissaOlevaTaytto() {
        return poissaOlevaTaytto;
    }

    public void setPoissaOlevaTaytto(Boolean poissaOlevaTaytto) {
        this.poissaOlevaTaytto = poissaOlevaTaytto;
    }

    @JsonProperty("valinnan_vaihe")
    @JsonView(JsonViews.Basic.class)
    public Long getValinnanVaiheId() {
        return valinnanVaihe.getId();
    }

    @JsonProperty("hakijaryhmat")
    @JsonView(JsonViews.Basic.class)
    public List<String> getHakijaryhmaIds() {
        List<String> hakijaryhmaIds = new ArrayList<String>();
        if (hakijaryhmat != null) {
            for (HakijaryhmaValintatapajono hakijaryhma : hakijaryhmat) {
                hakijaryhmaIds.add(hakijaryhma.getHakijaryhma().getOid());
            }
        }
        return hakijaryhmaIds;
    }

    @JsonProperty("hakijaryhmat")
    @JsonView(JsonViews.Basic.class)
    public void setHakijaryhmaIds(List<String> ids) {

    }

    @Transient
    @Override
    public Valintatapajono getEdellinen() {
        return getEdellinenValintatapajono();
    }

    @Transient
    @Override
    public Valintatapajono getSeuraava() {
        return getSeuraavaValintatapajono();
    }

    @Transient
    @Override
    public void setEdellinen(Valintatapajono edellinen) {
        setEdellinenValintatapajono(edellinen);
    }

    @Transient
    @Override
    public void setSeuraava(Valintatapajono seuraava) {
        setSeuraavaValintatapajono(seuraava);
    }

    @Transient
    @Override
    public void setMaster(Valintatapajono master) {
        setMasterValintatapajono(master);
    }

    @Transient
    @Override
    public Valintatapajono getMaster() {
        return getMasterValintatapajono();
    }

    @Transient
    @Override
    public void setKopiot(Set<Valintatapajono> kopiot) {
        setKopioValintatapajonot(kopiot);
    }

    @Transient
    @Override
    public Set<Valintatapajono> getKopiot() {
        return getKopioValintatapajonot();
    }

    @JsonProperty(value = "inheritance")
    @JsonView(JsonViews.Basic.class)
    @Transient
    private Boolean getInheritance() {
        return getMasterValintatapajono() != null;
    }

    public void addJarjestyskriteeri(Jarjestyskriteeri jarjestyskriteeri) {
        jarjestyskriteeri.setValintatapajono(this);
        this.jarjestyskriteerit.add(jarjestyskriteeri);
    }


    public Boolean getEiVarasijatayttoa() {
        return eiVarasijatayttoa;
    }

    public void setEiVarasijatayttoa(Boolean eiVarasijatayttoa) {
        this.eiVarasijatayttoa = eiVarasijatayttoa;
    }
}
