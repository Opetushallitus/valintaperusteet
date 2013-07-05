package fi.vm.sade.service.valintaperusteet.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.codehaus.jackson.map.annotate.JsonView;

/**
 * User: kwuoti Date: 21.2.2013 Time: 9.20
 */
@Entity
@Table(name = "arvovalikonvertteriparametri")
public class Arvovalikonvertteriparametri extends Konvertteriparametri implements
        Comparable<Arvovalikonvertteriparametri> {

    @Override
    public int compareTo(Arvovalikonvertteriparametri o) {
        double thisVal = minValue;
        double anotherVal = o.minValue;
        double thisMax = maxValue;
        double anotherMax = o.maxValue;
        if (thisVal < anotherVal || thisMax < anotherMax) {
            return -1;
        } else {
            if (thisVal > anotherVal || thisMax > anotherMax) {
                return 1;
            }
        }
        return 0;
    }

    @Override
    public int hashCode() {
        return minValue.hashCode() * 31 + maxValue.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getSimpleName());
        sb.append("[min=");
        sb.append(minValue);
        sb.append(", max=");
        sb.append(maxValue);
        sb.append("]");

        return sb.toString();
    }

    @JsonView(JsonViews.Basic.class)
    @Column(name = "minvalue", nullable = false)
    private Double minValue;

    @JsonView(JsonViews.Basic.class)
    @Column(name = "maxvalue", nullable = false)
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

    private static final long serialVersionUID = 7028232303346391201L;
}
