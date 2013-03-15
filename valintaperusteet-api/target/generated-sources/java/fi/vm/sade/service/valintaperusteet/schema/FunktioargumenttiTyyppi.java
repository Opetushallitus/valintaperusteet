
package fi.vm.sade.service.valintaperusteet.schema;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FunktioargumenttiTyyppi complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FunktioargumenttiTyyppi">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="indeksi" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="funktiokutsu" type="{http://valintaperusteet.service.sade.vm.fi/schema}FunktiokutsuTyyppi"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FunktioargumenttiTyyppi", propOrder = {
    "indeksi",
    "funktiokutsu"
})
public class FunktioargumenttiTyyppi
    implements Serializable
{

    private final static long serialVersionUID = 100L;
    protected int indeksi;
    @XmlElement(required = true)
    protected FunktiokutsuTyyppi funktiokutsu;

    /**
     * Gets the value of the indeksi property.
     * 
     */
    public int getIndeksi() {
        return indeksi;
    }

    /**
     * Sets the value of the indeksi property.
     * 
     */
    public void setIndeksi(int value) {
        this.indeksi = value;
    }

    /**
     * Gets the value of the funktiokutsu property.
     * 
     * @return
     *     possible object is
     *     {@link FunktiokutsuTyyppi }
     *     
     */
    public FunktiokutsuTyyppi getFunktiokutsu() {
        return funktiokutsu;
    }

    /**
     * Sets the value of the funktiokutsu property.
     * 
     * @param value
     *     allowed object is
     *     {@link FunktiokutsuTyyppi }
     *     
     */
    public void setFunktiokutsu(FunktiokutsuTyyppi value) {
        this.funktiokutsu = value;
    }

}
