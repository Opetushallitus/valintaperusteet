
package fi.vm.sade.service.valintaperusteet.messages;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import fi.vm.sade.service.valintaperusteet.schema.ValintatapajonoTyyppi;


/**
 * <p>Java class for HaeValintatapajonotSijoittelulleVastausTyyppi complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="HaeValintatapajonotSijoittelulleVastausTyyppi">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="valintatapajonot" type="{http://valintaperusteet.service.sade.vm.fi/schema}ValintatapajonoTyyppi" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HaeValintatapajonotSijoittelulleVastausTyyppi", propOrder = {
    "valintatapajonot"
})
public class HaeValintatapajonotSijoittelulleVastausTyyppi
    implements Serializable
{

    private final static long serialVersionUID = 100L;
    protected List<ValintatapajonoTyyppi> valintatapajonot;

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
     * {@link ValintatapajonoTyyppi }
     * 
     * 
     */
    public List<ValintatapajonoTyyppi> getValintatapajonot() {
        if (valintatapajonot == null) {
            valintatapajonot = new ArrayList<ValintatapajonoTyyppi>();
        }
        return this.valintatapajonot;
    }

}
