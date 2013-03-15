
package fi.vm.sade.service.valintaperusteet.schema;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ValintaperusteviiteTyyppi complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ValintaperusteviiteTyyppi">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="onPakollinen" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="tunniste" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ValintaperusteviiteTyyppi", propOrder = {
    "onPakollinen",
    "tunniste"
})
public class ValintaperusteviiteTyyppi
    implements Serializable
{

    private final static long serialVersionUID = 100L;
    protected boolean onPakollinen;
    @XmlElement(required = true)
    protected String tunniste;

    /**
     * Gets the value of the onPakollinen property.
     * 
     */
    public boolean isOnPakollinen() {
        return onPakollinen;
    }

    /**
     * Sets the value of the onPakollinen property.
     * 
     */
    public void setOnPakollinen(boolean value) {
        this.onPakollinen = value;
    }

    /**
     * Gets the value of the tunniste property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTunniste() {
        return tunniste;
    }

    /**
     * Sets the value of the tunniste property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTunniste(String value) {
        this.tunniste = value;
    }

}
