package fi.vm.sade.service.valintaperusteet.model;

import javax.persistence.*;

/**
 * User: wuoti
 * Date: 12.9.2013
 * Time: 13.03
 */
@Entity
@Table(name = "hakukohteen_valintaperuste", uniqueConstraints =
        {@UniqueConstraint(name = "UK_hakukohteen_valintaperuste_01", columnNames = {"tunniste", "hakukohde_viite_id"})})
public class HakukohteenValintaperuste extends BaseEntity {

    @Column(name = "tunniste", nullable = false)
    private String tunniste;

    @Column(name = "arvo", nullable = false)
    private String arvo;

    @Column(name = "kuvaus")
    private String kuvaus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hakukohde_viite_id", nullable = false)
    private HakukohdeViite hakukohde;

    public String getTunniste() {
        return tunniste;
    }

    public void setTunniste(String tunniste) {
        this.tunniste = tunniste;
    }

    public String getArvo() {
        return arvo;
    }

    public void setArvo(String arvo) {
        this.arvo = arvo;
    }

    public String getKuvaus() {
        return kuvaus;
    }

    public void setKuvaus(String kuvaus) {
        this.kuvaus = kuvaus;
    }

    public HakukohdeViite getHakukohde() {
        return hakukohde;
    }

    public void setHakukohde(HakukohdeViite hakukohde) {
        this.hakukohde = hakukohde;
    }
}
