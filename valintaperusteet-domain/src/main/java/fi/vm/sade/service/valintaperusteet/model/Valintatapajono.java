package fi.vm.sade.service.valintaperusteet.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

import fi.vm.sade.service.valintaperusteet.dto.model.Tasapistesaanto;

@Entity
@Table(name = "valintatapajono")
@Cacheable(true)
public class Valintatapajono extends BaseEntity implements
        LinkitettavaJaKopioitava<Valintatapajono, Set<Valintatapajono>> {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Column(name = "oid", nullable = false, unique = true)
    private String oid;

    @Column(name = "aloituspaikat", nullable = false)
    private Integer aloituspaikat;

    @Column(name = "nimi", nullable = false)
    private String nimi;

    @Column(name = "kuvaus")
    private String kuvaus;

    @Column(name = "siirretaan_sijoitteluun", nullable = false)
    private Boolean siirretaanSijoitteluun = false;

    @Column(name = "tasapistesaanto", nullable = false)
    @Enumerated(EnumType.STRING)
    private Tasapistesaanto tasapistesaanto;

    @Column(name = "aktiivinen", nullable = false)
    private Boolean aktiivinen;

    @Column(name = "valisijoittelu", nullable = false)
    private Boolean valisijoittelu;

    @Column(name = "ei_varasijatayttoa", nullable = false)
    private Boolean eiVarasijatayttoa = false;

    @Column(name = "varasijat", nullable = false)
    private Integer varasijat = 0;

    @Column(name = "varasijoja_kaytetaan_alkaen")
    private Date varasijojaKaytetaanAlkaen;

    @Column(name = "varasijoja_taytetaan_asti")
    private Date varasijojaTaytetaanAsti;

    @Column(name = "poissa_oleva_taytto")
    private Boolean poissaOlevaTaytto = false;

    @Column(name = "kaikki_ehdon_tayttavat_hyvaksytaan")
    private Boolean kaikkiEhdonTayttavatHyvaksytaan = false;

    @Column(name = "kaytetaan_valintalaskentaa", nullable = false)
    private Boolean kaytetaanValintalaskentaa = true;

    @JoinColumn(name = "varasijan_tayttojono_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Valintatapajono varasijanTayttojono;

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

    public Valintatapajono getVarasijanTayttojono() {
        return varasijanTayttojono;
    }

    public void setVarasijanTayttojono(Valintatapajono varasijanTayttojono) {
        this.varasijanTayttojono = varasijanTayttojono;
    }

    public Date getVarasijojaKaytetaanAlkaen() {
        return varasijojaKaytetaanAlkaen;
    }

    public void setVarasijojaKaytetaanAlkaen(Date varasijojaKaytetaanAlkaen) {
        this.varasijojaKaytetaanAlkaen = varasijojaKaytetaanAlkaen;
    }

    public Date getVarasijojaTaytetaanAsti() {
        return varasijojaTaytetaanAsti;
    }

    public void setVarasijojaTaytetaanAsti(Date varasijojaTaytetaanAsti) {
        this.varasijojaTaytetaanAsti = varasijojaTaytetaanAsti;
    }

    public Boolean getKaytetaanValintalaskentaa() {
        return kaytetaanValintalaskentaa;
    }

    public void setKaytetaanValintalaskentaa(Boolean kaytetaanValintalaskentaa) {
        this.kaytetaanValintalaskentaa = kaytetaanValintalaskentaa;
    }

    public Boolean getPoissaOlevaTaytto() {
        return poissaOlevaTaytto;
    }

    public void setPoissaOlevaTaytto(Boolean poissaOlevaTaytto) {
        this.poissaOlevaTaytto = poissaOlevaTaytto;
    }

    public String getValinnanVaiheId() {
        // Kai oidi olisi parempi palauttaa kuin id
        return valinnanVaihe.getOid();
    }

    public List<String> getHakijaryhmaIds() {
        List<String> hakijaryhmaIds = new ArrayList<String>();
        if (hakijaryhmat != null) {
            for (HakijaryhmaValintatapajono hakijaryhma : hakijaryhmat) {
                hakijaryhmaIds.add(hakijaryhma.getHakijaryhma().getOid());
            }
        }
        return hakijaryhmaIds;
    }

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

    @Transient
    public Boolean getInheritance() {
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

    public Boolean getKaikkiEhdonTayttavatHyvaksytaan() {
        return kaikkiEhdonTayttavatHyvaksytaan;
    }

    public void setKaikkiEhdonTayttavatHyvaksytaan(Boolean kaikkiEhdonTayttavatHyvaksytaan) {
        this.kaikkiEhdonTayttavatHyvaksytaan = kaikkiEhdonTayttavatHyvaksytaan;
    }

    public Boolean getValisijoittelu() {
        return valisijoittelu;
    }

    public void setValisijoittelu(Boolean valisijoittelu) {
        this.valisijoittelu = valisijoittelu;
    }
}
