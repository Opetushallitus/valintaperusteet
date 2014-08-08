package fi.vm.sade.service.valintaperusteet.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "hakijaryhma")
@Cacheable(true)
public class Hakijaryhma extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "oid", nullable = false, unique = true)
    private String oid;

    @Column(nullable = false)
    private String nimi;

    @Column
    private String kuvaus;

    @Column(nullable = false)
    private int kiintio;

    @Column
    private boolean kaytaKaikki;

    @Column
    private boolean tarkkaKiintio;

    @OneToMany(mappedBy = "hakijaryhma", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private Set<HakijaryhmaValintatapajono> jonot = new HashSet<>();

    @JoinColumn(name = "valintaryhma_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Valintaryhma valintaryhma;


    @JoinColumn(name = "laskentakaava_id", nullable = false)
    @ManyToOne(optional = false)
    private Laskentakaava laskentakaava;

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

    public boolean isKaytaKaikki() {
        return kaytaKaikki;
    }

    public void setKaytaKaikki(boolean kaytaKaikki) {
        this.kaytaKaikki = kaytaKaikki;
    }

    public boolean isTarkkaKiintio() {
        return tarkkaKiintio;
    }

    public void setTarkkaKiintio(boolean tarkkaKiintio) {
        this.tarkkaKiintio = tarkkaKiintio;
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

    public Laskentakaava getLaskentakaava() {
        return laskentakaava;
    }

    public void setLaskentakaava(Laskentakaava laskentakaava) {
        this.laskentakaava = laskentakaava;
    }


    @Transient
    public Long getLaskentakaavaId() {
        return laskentakaava.getId();
    }

    @Transient
    public void setLaskentakaavaId(Long id) {
        laskentakaava = new Laskentakaava();
        laskentakaava.setId(id);
    }

    public List<String> getValintatapajonoIds() {
        List<String> valintatapajonoIds = new ArrayList<String>();
        if (jonot != null) {
            for (HakijaryhmaValintatapajono hakijaryhma : jonot) {
                valintatapajonoIds.add(hakijaryhma.getHakijaryhma().getOid());
            }
        }
        return valintatapajonoIds;
    }

    public void setValintatapajonoIds(List<String> ids) {

    }
}
