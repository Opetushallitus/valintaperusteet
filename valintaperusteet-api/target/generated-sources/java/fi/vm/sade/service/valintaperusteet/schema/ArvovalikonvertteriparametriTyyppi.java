
package fi.vm.sade.service.valintaperusteet.schema;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArvovalikonvertteriparametriTyyppi complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArvovalikonvertteriparametriTyyppi">
 *   &lt;complexContent>
 *     &lt;extension base="{http://valintaperusteet.service.sade.vm.fi/schema}KonvertteriparametriTyyppi">
 *       &lt;sequence>
 *         &lt;element name="minimiarvo" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="maksimiarvo" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="palautaHaettuArvo" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArvovalikonvertteriparametriTyyppi", propOrder = {
    "minimiarvo",
    "maksimiarvo",
    "palautaHaettuArvo"
})
public class ArvovalikonvertteriparametriTyyppi
    extends KonvertteriparametriTyyppi
    implements Serializable
{

    private final static long serialVersionUID = 100L;
    protected double minimiarvo;
    protected double maksimiarvo;
    protected boolean palautaHaettuArvo;

    /**
     * Gets the value of the minimiarvo property.
     * 
     */
    public double getMinimiarvo() {
        return minimiarvo;
    }

    /**
     * Sets the value of the minimiarvo property.
     * 
     */
    public void setMinimiarvo(double value) {
        this.minimiarvo = value;
    }

    /**
     * Gets the value of the maksimiarvo property.
     * 
     */
    public double getMaksimiarvo() {
        return maksimiarvo;
    }

    /**
     * Sets the value of the maksimiarvo property.
     * 
     */
    public void setMaksimiarvo(double value) {
        this.maksimiarvo = value;
    }

    /**
     * Gets the value of the palautaHaettuArvo property.
     * 
     */
    public boolean isPalautaHaettuArvo() {
        return palautaHaettuArvo;
    }

    /**
     * Sets the value of the palautaHaettuArvo property.
     * 
     */
    public void setPalautaHaettuArvo(boolean value) {
        this.palautaHaettuArvo = value;
    }

}
