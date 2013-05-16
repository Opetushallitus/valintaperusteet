package fi.vm.sade.service.valintaperusteet.model;

import fi.vm.sade.generic.model.BaseEntity;
import org.codehaus.jackson.map.annotate.JsonView;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * User: wuoti
 * Date: 7.5.2013
 * Time: 12.55
 */
@Entity
@Table(name = "hakukohdekoodi")
public class Hakukohdekoodi extends BaseEntity {

    @JsonView(JsonViews.Basic.class)
    @Column(name = "uri", nullable = false)
    private String uri;

    @JsonView(JsonViews.Basic.class)
    @Column(name = "arvo", nullable = false)
    private String arvo;

    @JsonView(JsonViews.Basic.class)
    @Column(name = "nimi_fi")
    private String nimiFi;

    @JsonView(JsonViews.Basic.class)
    @Column(name = "nimi_sv")
    private String nimiSv;

    @JsonView(JsonViews.Basic.class)
    @Column(name = "nimi_en")
    private String nimiEn;

    @JsonView(JsonViews.Basic.class)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "hakukohdekoodi")
    private Set<HakukohdeViite> hakukohteet = new HashSet<HakukohdeViite>();

    @JoinColumn(name = "valintaryhma_id", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    private Valintaryhma valintaryhma;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getArvo() {
        return arvo;
    }

    public void setArvo(String arvo) {
        this.arvo = arvo;
    }

    public String getNimiFi() {
        return nimiFi;
    }

    public void setNimiFi(String nimiFi) {
        this.nimiFi = nimiFi;
    }

    public String getNimiSv() {
        return nimiSv;
    }

    public void setNimiSv(String nimiSv) {
        this.nimiSv = nimiSv;
    }

    public String getNimiEn() {
        return nimiEn;
    }

    public void setNimiEn(String nimiEn) {
        this.nimiEn = nimiEn;
    }

    public Valintaryhma getValintaryhma() {
        return valintaryhma;
    }

    public void setValintaryhma(Valintaryhma valintaryhma) {
        this.valintaryhma = valintaryhma;
    }

    public Set<HakukohdeViite> getHakukohteet() {
        return hakukohteet;
    }

    public void setHakukohteet(Set<HakukohdeViite> hakukohteet) {
        this.hakukohteet = hakukohteet;
    }

    public void addHakukohde(HakukohdeViite hakukohdeViite) {
        this.hakukohteet.add(hakukohdeViite);
        hakukohdeViite.setHakukohdekoodi(this);
    }
}
