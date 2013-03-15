
package fi.vm.sade.service.valintaperusteet.schema;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ValintatapajonoJarjestyskriteereillaTyyppi complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ValintatapajonoJarjestyskriteereillaTyyppi">
 *   &lt;complexContent>
 *     &lt;extension base="{http://valintaperusteet.service.sade.vm.fi/schema}ValintatapajonoTyyppi">
 *       &lt;sequence>
 *         &lt;element name="jarjestyskriteerit" type="{http://valintaperusteet.service.sade.vm.fi/schema}JarjestyskriteeriTyyppi" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ValintatapajonoJarjestyskriteereillaTyyppi", propOrder = {
    "jarjestyskriteerit"
})
public class ValintatapajonoJarjestyskriteereillaTyyppi
    extends ValintatapajonoTyyppi
    implements Serializable
{

    private final static long serialVersionUID = 100L;
    @XmlElement(required = true)
    protected List<JarjestyskriteeriTyyppi> jarjestyskriteerit;

    /**
     * Gets the value of the jarjestyskriteerit property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the jarjestyskriteerit property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getJarjestyskriteerit().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JarjestyskriteeriTyyppi }
     * 
     * 
     */
    public List<JarjestyskriteeriTyyppi> getJarjestyskriteerit() {
        if (jarjestyskriteerit == null) {
            jarjestyskriteerit = new ArrayList<JarjestyskriteeriTyyppi>();
        }
        return this.jarjestyskriteerit;
    }

}
