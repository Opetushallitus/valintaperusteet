package fi.vm.sade.service.valintaperusteet.model;

import javax.persistence.*;

/**
 * User: kwuoti Date: 21.2.2013 Time: 9.20
 */
@Entity
@Table(name = "arvovalikonvertteriparametri")
@Cacheable(true)
public class Arvovalikonvertteriparametri extends Konvertteriparametri {


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

    //@Column(precision = 17, scale = 4, name = "minvalue", nullable = false)
    @Column(name = "minvalue", nullable = false)
    private String minValue;

    @Column(name = "maxvalue", nullable = false)
    private String maxValue;

    @Column(name = "palauta_haettu_arvo")
    private String palautaHaettuArvo;

    @Column(name = "hylkaysperuste", nullable = false)
    private String hylkaysperuste;

    @JoinColumn(name = "tekstiryhma_id", nullable = true)
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    private TekstiRyhma kuvaukset;

    public String getMinValue() {
        return minValue;
    }

    public void setMinValue(String minValue) {
        this.minValue = minValue;
    }

    public String getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(String maxValue) {
        this.maxValue = maxValue;
    }

    public String getPalautaHaettuArvo() {
        return palautaHaettuArvo;
    }

    public void setPalautaHaettuArvo(String palautaHaettuArvo) {
        this.palautaHaettuArvo = palautaHaettuArvo;
    }

    private static final long serialVersionUID = 7028232303346391201L;

    public String getHylkaysperuste() {
        return hylkaysperuste;
    }

    public void setHylkaysperuste(String hylkaysperuste) {
        this.hylkaysperuste = hylkaysperuste;
    }

    public TekstiRyhma getKuvaukset() {
        return kuvaukset;
    }

    public void setKuvaukset(TekstiRyhma kuvaukset) {
        this.kuvaukset = kuvaukset;
    }
}
