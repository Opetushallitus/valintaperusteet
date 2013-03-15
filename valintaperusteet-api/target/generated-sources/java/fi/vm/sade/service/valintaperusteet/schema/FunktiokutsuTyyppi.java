
package fi.vm.sade.service.valintaperusteet.schema;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FunktiokutsuTyyppi complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FunktiokutsuTyyppi">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="oid" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
 *         &lt;element name="funktionimi" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="arvokonvertteriparametrit" type="{http://valintaperusteet.service.sade.vm.fi/schema}ArvokonvertteriparametriTyyppi" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="arvovalikonvertteriparametrit" type="{http://valintaperusteet.service.sade.vm.fi/schema}ArvovalikonvertteriparametriTyyppi" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="syoteparametrit" type="{http://valintaperusteet.service.sade.vm.fi/schema}SyoteparametriTyyppi" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="funktioargumentit" type="{http://valintaperusteet.service.sade.vm.fi/schema}FunktioargumenttiTyyppi" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="valintaperusteviite" type="{http://valintaperusteet.service.sade.vm.fi/schema}ValintaperusteviiteTyyppi" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FunktiokutsuTyyppi", propOrder = {
    "oid",
    "funktionimi",
    "arvokonvertteriparametrit",
    "arvovalikonvertteriparametrit",
    "syoteparametrit",
    "funktioargumentit",
    "valintaperusteviite"
})
public class FunktiokutsuTyyppi
    implements Serializable
{

    private final static long serialVersionUID = 100L;
    @XmlElement(required = true)
    protected Object oid;
    @XmlElement(required = true)
    protected String funktionimi;
    protected List<ArvokonvertteriparametriTyyppi> arvokonvertteriparametrit;
    protected List<ArvovalikonvertteriparametriTyyppi> arvovalikonvertteriparametrit;
    protected List<SyoteparametriTyyppi> syoteparametrit;
    protected List<FunktioargumenttiTyyppi> funktioargumentit;
    protected ValintaperusteviiteTyyppi valintaperusteviite;

    /**
     * Gets the value of the oid property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getOid() {
        return oid;
    }

    /**
     * Sets the value of the oid property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setOid(Object value) {
        this.oid = value;
    }

    /**
     * Gets the value of the funktionimi property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFunktionimi() {
        return funktionimi;
    }

    /**
     * Sets the value of the funktionimi property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFunktionimi(String value) {
        this.funktionimi = value;
    }

    /**
     * Gets the value of the arvokonvertteriparametrit property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the arvokonvertteriparametrit property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getArvokonvertteriparametrit().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ArvokonvertteriparametriTyyppi }
     * 
     * 
     */
    public List<ArvokonvertteriparametriTyyppi> getArvokonvertteriparametrit() {
        if (arvokonvertteriparametrit == null) {
            arvokonvertteriparametrit = new ArrayList<ArvokonvertteriparametriTyyppi>();
        }
        return this.arvokonvertteriparametrit;
    }

    /**
     * Gets the value of the arvovalikonvertteriparametrit property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the arvovalikonvertteriparametrit property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getArvovalikonvertteriparametrit().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ArvovalikonvertteriparametriTyyppi }
     * 
     * 
     */
    public List<ArvovalikonvertteriparametriTyyppi> getArvovalikonvertteriparametrit() {
        if (arvovalikonvertteriparametrit == null) {
            arvovalikonvertteriparametrit = new ArrayList<ArvovalikonvertteriparametriTyyppi>();
        }
        return this.arvovalikonvertteriparametrit;
    }

    /**
     * Gets the value of the syoteparametrit property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the syoteparametrit property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSyoteparametrit().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SyoteparametriTyyppi }
     * 
     * 
     */
    public List<SyoteparametriTyyppi> getSyoteparametrit() {
        if (syoteparametrit == null) {
            syoteparametrit = new ArrayList<SyoteparametriTyyppi>();
        }
        return this.syoteparametrit;
    }

    /**
     * Gets the value of the funktioargumentit property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the funktioargumentit property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFunktioargumentit().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FunktioargumenttiTyyppi }
     * 
     * 
     */
    public List<FunktioargumenttiTyyppi> getFunktioargumentit() {
        if (funktioargumentit == null) {
            funktioargumentit = new ArrayList<FunktioargumenttiTyyppi>();
        }
        return this.funktioargumentit;
    }

    /**
     * Gets the value of the valintaperusteviite property.
     * 
     * @return
     *     possible object is
     *     {@link ValintaperusteviiteTyyppi }
     *     
     */
    public ValintaperusteviiteTyyppi getValintaperusteviite() {
        return valintaperusteviite;
    }

    /**
     * Sets the value of the valintaperusteviite property.
     * 
     * @param value
     *     allowed object is
     *     {@link ValintaperusteviiteTyyppi }
     *     
     */
    public void setValintaperusteviite(ValintaperusteviiteTyyppi value) {
        this.valintaperusteviite = value;
    }

}
