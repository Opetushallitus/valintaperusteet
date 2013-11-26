package fi.vm.sade.service.valintaperusteet.model;

import org.codehaus.jackson.map.annotate.JsonView;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * User: kwuoti
 * Date: 21.2.2013
 * Time: 9.21
 */
@Entity
@Table(name = "arvokonvertteriparametri")
@Cacheable(true)
public class Arvokonvertteriparametri extends Konvertteriparametri {
    @JsonView(JsonViews.Basic.class)
    @Column(name = "arvo")
    private String arvo;

    @JsonView(JsonViews.Basic.class)
    @Column(name = "hylkaysperuste", nullable = false)
    private String hylkaysperuste;

    public String getArvo() {
        return arvo;
    }

    public void setArvo(String arvo) {
        this.arvo = arvo;
    }

    public String getHylkaysperuste() {
        return hylkaysperuste;
    }

    public void setHylkaysperuste(String hylkaysperuste) {
        this.hylkaysperuste = hylkaysperuste;
    }
}
