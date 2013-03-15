
package fi.vm.sade.service.valintaperusteet.messages;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the fi.vm.sade.service.valintaperusteet.messages package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _HaeValintaperusteetVastaus_QNAME = new QName("http://valintaperusteet.service.sade.vm.fi/messages", "haeValintaperusteetVastaus");
    private final static QName _HaeValintatapajonotSijoittelulleVastaus_QNAME = new QName("http://valintaperusteet.service.sade.vm.fi/messages", "haeValintatapajonotSijoittelulleVastaus");
    private final static QName _HaeValintaperusteet_QNAME = new QName("http://valintaperusteet.service.sade.vm.fi/messages", "haeValintaperusteet");
    private final static QName _HaeValintatapajonotSijoittelulle_QNAME = new QName("http://valintaperusteet.service.sade.vm.fi/messages", "haeValintatapajonotSijoittelulle");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: fi.vm.sade.service.valintaperusteet.messages
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link HaeValintatapajonotSijoittelulleVastausTyyppi }
     * 
     */
    public HaeValintatapajonotSijoittelulleVastausTyyppi createHaeValintatapajonotSijoittelulleVastausTyyppi() {
        return new HaeValintatapajonotSijoittelulleVastausTyyppi();
    }

    /**
     * Create an instance of {@link HakuparametritTyyppi }
     * 
     */
    public HakuparametritTyyppi createHakuparametritTyyppi() {
        return new HakuparametritTyyppi();
    }

    /**
     * Create an instance of {@link HaeValintaperusteetVastausTyyppi }
     * 
     */
    public HaeValintaperusteetVastausTyyppi createHaeValintaperusteetVastausTyyppi() {
        return new HaeValintaperusteetVastausTyyppi();
    }

    /**
     * Create an instance of {@link GenericFault }
     * 
     */
    public GenericFault createGenericFault() {
        return new GenericFault();
    }

    /**
     * Create an instance of {@link HaeValintatapajonotSijoittelulleTyyppi }
     * 
     */
    public HaeValintatapajonotSijoittelulleTyyppi createHaeValintatapajonotSijoittelulleTyyppi() {
        return new HaeValintatapajonotSijoittelulleTyyppi();
    }

    /**
     * Create an instance of {@link HaeValintaperusteetTyyppi }
     * 
     */
    public HaeValintaperusteetTyyppi createHaeValintaperusteetTyyppi() {
        return new HaeValintaperusteetTyyppi();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link HaeValintaperusteetVastausTyyppi }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://valintaperusteet.service.sade.vm.fi/messages", name = "haeValintaperusteetVastaus")
    public JAXBElement<HaeValintaperusteetVastausTyyppi> createHaeValintaperusteetVastaus(HaeValintaperusteetVastausTyyppi value) {
        return new JAXBElement<HaeValintaperusteetVastausTyyppi>(_HaeValintaperusteetVastaus_QNAME, HaeValintaperusteetVastausTyyppi.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link HaeValintatapajonotSijoittelulleVastausTyyppi }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://valintaperusteet.service.sade.vm.fi/messages", name = "haeValintatapajonotSijoittelulleVastaus")
    public JAXBElement<HaeValintatapajonotSijoittelulleVastausTyyppi> createHaeValintatapajonotSijoittelulleVastaus(HaeValintatapajonotSijoittelulleVastausTyyppi value) {
        return new JAXBElement<HaeValintatapajonotSijoittelulleVastausTyyppi>(_HaeValintatapajonotSijoittelulleVastaus_QNAME, HaeValintatapajonotSijoittelulleVastausTyyppi.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link HaeValintaperusteetTyyppi }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://valintaperusteet.service.sade.vm.fi/messages", name = "haeValintaperusteet")
    public JAXBElement<HaeValintaperusteetTyyppi> createHaeValintaperusteet(HaeValintaperusteetTyyppi value) {
        return new JAXBElement<HaeValintaperusteetTyyppi>(_HaeValintaperusteet_QNAME, HaeValintaperusteetTyyppi.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link HaeValintatapajonotSijoittelulleTyyppi }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://valintaperusteet.service.sade.vm.fi/messages", name = "haeValintatapajonotSijoittelulle")
    public JAXBElement<HaeValintatapajonotSijoittelulleTyyppi> createHaeValintatapajonotSijoittelulle(HaeValintatapajonotSijoittelulleTyyppi value) {
        return new JAXBElement<HaeValintatapajonotSijoittelulleTyyppi>(_HaeValintatapajonotSijoittelulle_QNAME, HaeValintatapajonotSijoittelulleTyyppi.class, null, value);
    }

}
