
package fi.vm.sade.service.valintaperusteet.messages;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for HaeValintaperusteetTyyppi complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="HaeValintaperusteetTyyppi">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="hakuparametrit" type="{http://valintaperusteet.service.sade.vm.fi/messages}HakuparametritTyyppi" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HaeValintaperusteetTyyppi", propOrder = {
    "hakuparametrit"
})
public class HaeValintaperusteetTyyppi
    implements Serializable
{

    private final static long serialVersionUID = 100L;
    @XmlElement(required = true)
    protected List<HakuparametritTyyppi> hakuparametrit;

    /**
     * Gets the value of the hakuparametrit property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the hakuparametrit property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getHakuparametrit().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link HakuparametritTyyppi }
     * 
     * 
     */
    public List<HakuparametritTyyppi> getHakuparametrit() {
        if (hakuparametrit == null) {
            hakuparametrit = new ArrayList<HakuparametritTyyppi>();
        }
        return this.hakuparametrit;
    }

}
