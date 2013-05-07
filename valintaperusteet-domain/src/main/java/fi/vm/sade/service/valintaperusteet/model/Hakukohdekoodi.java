package fi.vm.sade.service.valintaperusteet.model;

import fi.vm.sade.generic.model.BaseEntity;

import javax.persistence.*;

/**
 * User: wuoti
 * Date: 7.5.2013
 * Time: 12.55
 */
@Entity
@Table(name = "hakukohdekoodi")
public class Hakukohdekoodi extends BaseEntity {

    @Column(name = "uri", nullable = false)
    private String uri;

    @Column(name = "arvo", nullable = false)
    private String arvo;

    @Column(name = "nimi_fi")
    private String nimiFi;

    @Column(name = "nimi_sv")
    private String nimiSv;

    @Column(name = "nimi_en")
    private String nimiEn;

    @JoinColumn(name = "hakukohde_id", nullable = true)
    @OneToOne(fetch = FetchType.LAZY, optional = true)
    private HakukohdeViite hakukohde;

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

    public HakukohdeViite getHakukohde() {
        return hakukohde;
    }

    public void setHakukohde(HakukohdeViite hakukohde) {
        this.hakukohde = hakukohde;
    }

    public Valintaryhma getValintaryhma() {
        return valintaryhma;
    }

    public void setValintaryhma(Valintaryhma valintaryhma) {
        this.valintaryhma = valintaryhma;
    }
}
