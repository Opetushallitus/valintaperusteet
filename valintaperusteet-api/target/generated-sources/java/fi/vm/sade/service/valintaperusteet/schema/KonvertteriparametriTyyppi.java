
package fi.vm.sade.service.valintaperusteet.schema;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for KonvertteriparametriTyyppi complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="KonvertteriparametriTyyppi">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="paluuarvo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="hylkaysperuste" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "KonvertteriparametriTyyppi", propOrder = {
    "paluuarvo",
    "hylkaysperuste"
})
@XmlSeeAlso({
    ArvokonvertteriparametriTyyppi.class,
    ArvovalikonvertteriparametriTyyppi.class
})
public class KonvertteriparametriTyyppi
    implements Serializable
{

    private final static long serialVersionUID = 100L;
    protected String paluuarvo;
    protected boolean hylkaysperuste;

    /**
     * Gets the value of the paluuarvo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaluuarvo() {
        return paluuarvo;
    }

    /**
     * Sets the value of the paluuarvo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaluuarvo(String value) {
        this.paluuarvo = value;
    }

    /**
     * Gets the value of the hylkaysperuste property.
     * 
     */
    public boolean isHylkaysperuste() {
        return hylkaysperuste;
    }

    /**
     * Sets the value of the hylkaysperuste property.
     * 
     */
    public void setHylkaysperuste(boolean value) {
        this.hylkaysperuste = value;
    }

}
