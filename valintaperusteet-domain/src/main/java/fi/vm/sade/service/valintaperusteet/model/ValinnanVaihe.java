package fi.vm.sade.service.valintaperusteet.model;

import fi.vm.sade.generic.model.BaseEntity;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonView;

import javax.persistence.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "valinnan_vaihe")
@Cacheable(true)
public class ValinnanVaihe extends BaseEntity implements LinkitettavaJaKopioitava<ValinnanVaihe, Set<ValinnanVaihe>> {

    private static final long serialVersionUID = 1L;

    @Column(name = "oid", nullable = false, unique = true)
    private String oid;

    @JoinColumn(name = "edellinen_valinnan_vaihe_id")
    @OneToOne(fetch = FetchType.LAZY)
    private ValinnanVaihe edellinenValinnanVaihe;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "edellinenValinnanVaihe")
    private ValinnanVaihe seuraavaValinnanVaihe;

    @Column(name = "nimi", nullable = false)
    private String nimi;

    @Column(name = "kuvaus")
    private String kuvaus;

    @Column(name = "aktiivinen", nullable = false)
    private Boolean aktiivinen;

    @JoinColumn(name = "master_valinnan_vaihe_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private ValinnanVaihe masterValinnanVaihe;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "masterValinnanVaihe")
    private Set<ValinnanVaihe> kopioValinnanVaiheet = new HashSet<ValinnanVaihe>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "valinnanVaihe")
    private Set<Valintatapajono> jonot = new HashSet<Valintatapajono>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "valinnanVaihe")
    private Set<Valintakoe> valintakokeet = new HashSet<Valintakoe>();

    @JoinColumn(name = "valintaryhma_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Valintaryhma valintaryhma;

    @JoinColumn(name = "hakukohde_viite_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private HakukohdeViite hakukohdeViite;

    @Column(name = "valinnan_vaihe_tyyppi", nullable = false)
    @Enumerated(EnumType.STRING)
    private ValinnanVaiheTyyppi valinnanVaiheTyyppi;

    public ValinnanVaihe getEdellinenValinnanVaihe() {
        return edellinenValinnanVaihe;
    }

    public void setEdellinenValinnanVaihe(ValinnanVaihe edellinenValinnanVaihe) {
        this.edellinenValinnanVaihe = edellinenValinnanVaihe;
    }

    public ValinnanVaihe getSeuraavaValinnanVaihe() {
        return seuraavaValinnanVaihe;
    }

    public void setSeuraavaValinnanVaihe(ValinnanVaihe seuraavaValinnanVaihe) {
        this.seuraavaValinnanVaihe = seuraavaValinnanVaihe;
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

    public ValinnanVaihe getMasterValinnanVaihe() {
        return masterValinnanVaihe;
    }

    public void setMasterValinnanVaihe(ValinnanVaihe masterValinnanVaihe) {
        this.masterValinnanVaihe = masterValinnanVaihe;
    }

    public Set<ValinnanVaihe> getKopioValinnanVaiheet() {
        return kopioValinnanVaiheet;
    }

    public void setKopioValinnanvaiheet(Set<ValinnanVaihe> kopiot) {
        this.kopioValinnanVaiheet = kopiot;
    }

    public Set<Valintatapajono> getJonot() {
        return Collections.unmodifiableSet(jonot);
    }

    public Set<Valintakoe> getValintakokeet() {
        return Collections.unmodifiableSet(valintakokeet);
    }

    public void setValintakokeet(Set<Valintakoe> valintakokeet) {
        this.valintakokeet = valintakokeet;
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

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public ValinnanVaiheTyyppi getValinnanVaiheTyyppi() {
        return valinnanVaiheTyyppi;
    }

    public void setValinnanVaiheTyyppi(ValinnanVaiheTyyppi valinnanVaiheTyyppi) {
        this.valinnanVaiheTyyppi = valinnanVaiheTyyppi;
    }

    @Transient
    @Override
    public ValinnanVaihe getEdellinen() {
        return getEdellinenValinnanVaihe();
    }

    @Transient
    @Override
    public ValinnanVaihe getSeuraava() {
        return getSeuraavaValinnanVaihe();
    }

    @Transient
    @Override
    public void setEdellinen(ValinnanVaihe edellinen) {
        setEdellinenValinnanVaihe(edellinen);
    }

    @Transient
    @Override
    public void setSeuraava(ValinnanVaihe seuraava) {
        setSeuraavaValinnanVaihe(seuraava);
    }

    @Transient
    public Boolean getInheritance() {
        return getMasterValinnanVaihe() != null;
    }

    @Transient
    @Override
    public void setMaster(ValinnanVaihe master) {
        setMasterValinnanVaihe(master);
    }

    @Transient
    @Override
    public ValinnanVaihe getMaster() {
        return getMasterValinnanVaihe();
    }

    @Transient
    @Override
    public void setKopiot(Set<ValinnanVaihe> kopiot) {
        setKopioValinnanvaiheet(kopiot);
    }

    @Transient
    @Override
    public Set<ValinnanVaihe> getKopiot() {
        return getKopioValinnanVaiheet();
    }

    public void addJono(Valintatapajono jono) {
        if (!ValinnanVaiheTyyppi.TAVALLINEN.equals(valinnanVaiheTyyppi)) {
            throw new UnsupportedOperationException("Valintatapajonon voi lisätä vain tavalliselle valinnan vaiheelle");
        }

        jono.setValinnanVaihe(this);
        this.jonot.add(jono);
    }

    public void addValintakoe(Valintakoe koe) {
        if (!ValinnanVaiheTyyppi.VALINTAKOE.equals(valinnanVaiheTyyppi)) {
            throw new UnsupportedOperationException("Valintakokeita voi lisätä vain valintakoevalinnanvaiheelle");
        }

        koe.setValinnanVaihe(this);
        this.valintakokeet.add(koe);
    }


}
