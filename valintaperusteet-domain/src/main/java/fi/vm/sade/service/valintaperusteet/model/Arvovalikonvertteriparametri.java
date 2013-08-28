package fi.vm.sade.service.valintaperusteet.model;

import java.math.BigDecimal;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.codehaus.jackson.map.annotate.JsonView;

/**
 * User: kwuoti Date: 21.2.2013 Time: 9.20
 */
@Entity
@Table(name = "arvovalikonvertteriparametri")
@Cacheable(true)
public class Arvovalikonvertteriparametri extends Konvertteriparametri implements
        Comparable<Arvovalikonvertteriparametri> {

    @Override
    public int compareTo(Arvovalikonvertteriparametri o) {
        BigDecimal thisVal = minValue;
        BigDecimal anotherVal = o.minValue;
        BigDecimal thisMax = maxValue;
        BigDecimal anotherMax = o.maxValue;
        if (thisVal.compareTo(anotherVal) == -1 || thisMax.compareTo(anotherMax) == -1) {
            return -1;
        } else {
            if (thisVal.compareTo(anotherVal) == 1 || thisMax.compareTo(anotherMax) == 1) {
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
    @Column(precision = 17, scale = 4, name = "minvalue", nullable = false)
    private BigDecimal minValue;

    @JsonView(JsonViews.Basic.class)
    @Column(precision = 17, scale = 4, name = "maxvalue", nullable = false)
    private BigDecimal maxValue;

    @JsonView(JsonViews.Basic.class)
    @Column(name = "palauta_haettu_arvo")
    private Boolean palautaHaettuArvo;

    public BigDecimal getMinValue() {
        return minValue;
    }

    public void setMinValue(BigDecimal minValue) {
        this.minValue = minValue;
    }

    public BigDecimal getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(BigDecimal maxValue) {
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
