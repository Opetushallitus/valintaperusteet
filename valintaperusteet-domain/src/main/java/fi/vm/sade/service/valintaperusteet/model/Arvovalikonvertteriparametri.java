package fi.vm.sade.service.valintaperusteet.model;

import org.codehaus.jackson.map.annotate.JsonView;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * User: kwuoti
 * Date: 21.2.2013
 * Time: 9.20
 */
@Entity
@Table(name = "arvovalikonvertteriparametri")
public class Arvovalikonvertteriparametri extends Konvertteriparametri {
    @JsonView(JsonViews.Basic.class)
    @Column(name = "minvalue")
    private Double minValue;

    @JsonView(JsonViews.Basic.class)
    @Column(name = "maxvalue")
    private Double maxValue;

    @JsonView(JsonViews.Basic.class)
    @Column(name = "palauta_haettu_arvo")
    private Boolean palautaHaettuArvo;

    public Double getMinValue() {
        return minValue;
    }

    public void setMinValue(Double minValue) {
        this.minValue = minValue;
    }

    public Double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Double maxValue) {
        this.maxValue = maxValue;
    }

    public Boolean getPalautaHaettuArvo() {
        return palautaHaettuArvo;
    }

    public void setPalautaHaettuArvo(Boolean palautaHaettuArvo) {
        this.palautaHaettuArvo = palautaHaettuArvo;
    }
}
