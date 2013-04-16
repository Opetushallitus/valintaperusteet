package fi.vm.sade.service.valintaperusteet.model;

import fi.vm.sade.generic.model.BaseEntity;
import org.codehaus.jackson.map.annotate.JsonView;

import javax.persistence.*;

/**
 * User: kwuoti
 * Date: 15.4.2013
 * Time: 15.55
 */
@Entity
@Table(name = "valintakoe")
public class Valintakoe extends BaseEntity {

    @JsonView(JsonViews.Basic.class)
    @Column(name = "oid", nullable = false, unique = true)
    private String oid;

    @JsonView(JsonViews.Basic.class)
    @Column(name = "tunniste", nullable = false)
    private String tunniste;

    @JoinColumn(name = "laskentakaava_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Laskentakaava laskentakaava;

    @JoinColumn(name = "valinnan_vaihe_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private ValinnanVaihe valinnanVaihe;

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getTunniste() {
        return tunniste;
    }

    public void setTunniste(String tunniste) {
        this.tunniste = tunniste;
    }

    public Laskentakaava getLaskentakaava() {
        return laskentakaava;
    }

    public void setLaskentakaava(Laskentakaava laskentakaava) {
        this.laskentakaava = laskentakaava;
    }

    public ValinnanVaihe getValinnanVaihe() {
        return valinnanVaihe;
    }

    public void setValinnanVaihe(ValinnanVaihe valinnanVaihe) {
        this.valinnanVaihe = valinnanVaihe;
    }
}
