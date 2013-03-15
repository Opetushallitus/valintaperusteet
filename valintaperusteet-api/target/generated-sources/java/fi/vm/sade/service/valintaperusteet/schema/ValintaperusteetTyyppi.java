
package fi.vm.sade.service.valintaperusteet.schema;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 *                 1 hakukohteen, 1 valinnanvaiheen tiedot valintalaskentaa varten.
 *             
 * 
 * <p>Java class for ValintaperusteetTyyppi complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ValintaperusteetTyyppi">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="valintatapajonot" type="{http://valintaperusteet.service.sade.vm.fi/schema}ValintatapajonoJarjestyskriteereillaTyyppi" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="hakukohdeOid" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="valinnanVaiheOid" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="valinnanVaiheJarjestysluku" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ValintaperusteetTyyppi", propOrder = {
    "valintatapajonot"
})
public class ValintaperusteetTyyppi
    implements Serializable
{

    private final static long serialVersionUID = 100L;
    @XmlElement(required = true)
    protected List<ValintatapajonoJarjestyskriteereillaTyyppi> valintatapajonot;
    @XmlAttribute(name = "hakukohdeOid", required = true)
    protected String hakukohdeOid;
    @XmlAttribute(name = "valinnanVaiheOid", required = true)
    protected String valinnanVaiheOid;
    @XmlAttribute(name = "valinnanVaiheJarjestysluku", required = true)
    protected int valinnanVaiheJarjestysluku;

    /**
     * Gets the value of the valintatapajonot property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the valintatapajonot property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getValintatapajonot().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ValintatapajonoJarjestyskriteereillaTyyppi }
     * 
     * 
     */
    public List<ValintatapajonoJarjestyskriteereillaTyyppi> getValintatapajonot() {
        if (valintatapajonot == null) {
            valintatapajonot = new ArrayList<ValintatapajonoJarjestyskriteereillaTyyppi>();
        }
        return this.valintatapajonot;
    }

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
     * Gets the value of the valinnanVaiheOid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValinnanVaiheOid() {
        return valinnanVaiheOid;
    }

    /**
     * Sets the value of the valinnanVaiheOid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValinnanVaiheOid(String value) {
        this.valinnanVaiheOid = value;
    }

    /**
     * Gets the value of the valinnanVaiheJarjestysluku property.
     * 
     */
    public int getValinnanVaiheJarjestysluku() {
        return valinnanVaiheJarjestysluku;
    }

    /**
     * Sets the value of the valinnanVaiheJarjestysluku property.
     * 
     */
    public void setValinnanVaiheJarjestysluku(int value) {
        this.valinnanVaiheJarjestysluku = value;
    }

}
