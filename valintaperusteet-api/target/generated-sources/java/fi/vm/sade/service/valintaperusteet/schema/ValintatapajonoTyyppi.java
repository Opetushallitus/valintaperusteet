
package fi.vm.sade.service.valintaperusteet.schema;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ValintatapajonoTyyppi complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ValintatapajonoTyyppi">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="nimi" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="kuvaus" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="siirretaanSijoitteluun" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="oid" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="prioriteetti" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="aloituspaikat" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="tasasijasaanto" use="required" type="{http://valintaperusteet.service.sade.vm.fi/schema}TasasijasaantoTyyppi" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ValintatapajonoTyyppi")
@XmlSeeAlso({
    ValintatapajonoJarjestyskriteereillaTyyppi.class
})
public class ValintatapajonoTyyppi
    implements Serializable
{

    private final static long serialVersionUID = 100L;
    @XmlAttribute(name = "nimi", required = true)
    protected String nimi;
    @XmlAttribute(name = "kuvaus")
    protected String kuvaus;
    @XmlAttribute(name = "siirretaanSijoitteluun", required = true)
    protected boolean siirretaanSijoitteluun;
    @XmlAttribute(name = "oid", required = true)
    protected String oid;
    @XmlAttribute(name = "prioriteetti", required = true)
    protected int prioriteetti;
    @XmlAttribute(name = "aloituspaikat", required = true)
    protected int aloituspaikat;
    @XmlAttribute(name = "tasasijasaanto", required = true)
    protected TasasijasaantoTyyppi tasasijasaanto;

    /**
     * Gets the value of the nimi property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNimi() {
        return nimi;
    }

    /**
     * Sets the value of the nimi property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNimi(String value) {
        this.nimi = value;
    }

    /**
     * Gets the value of the kuvaus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKuvaus() {
        return kuvaus;
    }

    /**
     * Sets the value of the kuvaus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKuvaus(String value) {
        this.kuvaus = value;
    }

    /**
     * Gets the value of the siirretaanSijoitteluun property.
     * 
     */
    public boolean isSiirretaanSijoitteluun() {
        return siirretaanSijoitteluun;
    }

    /**
     * Sets the value of the siirretaanSijoitteluun property.
     * 
     */
    public void setSiirretaanSijoitteluun(boolean value) {
        this.siirretaanSijoitteluun = value;
    }

    /**
     * Gets the value of the oid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOid() {
        return oid;
    }

    /**
     * Sets the value of the oid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOid(String value) {
        this.oid = value;
    }

    /**
     * Gets the value of the prioriteetti property.
     * 
     */
    public int getPrioriteetti() {
        return prioriteetti;
    }

    /**
     * Sets the value of the prioriteetti property.
     * 
     */
    public void setPrioriteetti(int value) {
        this.prioriteetti = value;
    }

    /**
     * Gets the value of the aloituspaikat property.
     * 
     */
    public int getAloituspaikat() {
        return aloituspaikat;
    }

    /**
     * Sets the value of the aloituspaikat property.
     * 
     */
    public void setAloituspaikat(int value) {
        this.aloituspaikat = value;
    }

    /**
     * Gets the value of the tasasijasaanto property.
     * 
     * @return
     *     possible object is
     *     {@link TasasijasaantoTyyppi }
     *     
     */
    public TasasijasaantoTyyppi getTasasijasaanto() {
        return tasasijasaanto;
    }

    /**
     * Sets the value of the tasasijasaanto property.
     * 
     * @param value
     *     allowed object is
     *     {@link TasasijasaantoTyyppi }
     *     
     */
    public void setTasasijasaanto(TasasijasaantoTyyppi value) {
        this.tasasijasaanto = value;
    }

}
