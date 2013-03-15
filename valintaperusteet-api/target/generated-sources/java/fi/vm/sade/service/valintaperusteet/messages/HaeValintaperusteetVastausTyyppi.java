
package fi.vm.sade.service.valintaperusteet.messages;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import fi.vm.sade.service.valintaperusteet.schema.ValintaperusteetTyyppi;


/**
 * <p>Java class for HaeValintaperusteetVastausTyyppi complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="HaeValintaperusteetVastausTyyppi">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="valintaPerusteet" type="{http://valintaperusteet.service.sade.vm.fi/schema}ValintaperusteetTyyppi" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HaeValintaperusteetVastausTyyppi", propOrder = {
    "valintaPerusteet"
})
public class HaeValintaperusteetVastausTyyppi
    implements Serializable
{

    private final static long serialVersionUID = 100L;
    @XmlElement(required = true)
    protected List<ValintaperusteetTyyppi> valintaPerusteet;

    /**
     * Gets the value of the valintaPerusteet property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the valintaPerusteet property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getValintaPerusteet().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ValintaperusteetTyyppi }
     * 
     * 
     */
    public List<ValintaperusteetTyyppi> getValintaPerusteet() {
        if (valintaPerusteet == null) {
            valintaPerusteet = new ArrayList<ValintaperusteetTyyppi>();
        }
        return this.valintaPerusteet;
    }

}
