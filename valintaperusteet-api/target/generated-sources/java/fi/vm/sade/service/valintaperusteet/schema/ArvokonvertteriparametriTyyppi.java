
package fi.vm.sade.service.valintaperusteet.schema;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArvokonvertteriparametriTyyppi complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArvokonvertteriparametriTyyppi">
 *   &lt;complexContent>
 *     &lt;extension base="{http://valintaperusteet.service.sade.vm.fi/schema}KonvertteriparametriTyyppi">
 *       &lt;sequence>
 *         &lt;element name="arvo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArvokonvertteriparametriTyyppi", propOrder = {
    "arvo"
})
public class ArvokonvertteriparametriTyyppi
    extends KonvertteriparametriTyyppi
    implements Serializable
{

    private final static long serialVersionUID = 100L;
    @XmlElement(required = true)
    protected String arvo;

    /**
     * Gets the value of the arvo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getArvo() {
        return arvo;
    }

    /**
     * Sets the value of the arvo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setArvo(String value) {
        this.arvo = value;
    }

}
