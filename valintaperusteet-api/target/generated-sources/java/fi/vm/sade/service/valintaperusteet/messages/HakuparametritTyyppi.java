
package fi.vm.sade.service.valintaperusteet.messages;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for HakuparametritTyyppi complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="HakuparametritTyyppi">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="hakukohdeOid" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="valinnanVaiheJarjestysluku" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HakuparametritTyyppi", propOrder = {
    "hakukohdeOid",
    "valinnanVaiheJarjestysluku"
})
public class HakuparametritTyyppi
    implements Serializable
{

    private final static long serialVersionUID = 100L;
    @XmlElement(required = true)
    protected String hakukohdeOid;
    protected Integer valinnanVaiheJarjestysluku;

    /**
     * Gets the value of the hakukohdeOid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHakukohdeOid() {
        return hakukohdeOid;
    }

    /**
     * Sets the value of the hakukohdeOid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHakukohdeOid(String value) {
        this.hakukohdeOid = value;
    }

    /**
     * Gets the value of the valinnanVaiheJarjestysluku property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getValinnanVaiheJarjestysluku() {
        return valinnanVaiheJarjestysluku;
    }

    /**
     * Sets the value of the valinnanVaiheJarjestysluku property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setValinnanVaiheJarjestysluku(Integer value) {
        this.valinnanVaiheJarjestysluku = value;
    }

}
