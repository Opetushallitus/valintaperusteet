
package fi.vm.sade.service.valintaperusteet.schema;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TasasijasaantoTyyppi.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="TasasijasaantoTyyppi">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="ARVONTA"/>
 *     &lt;enumeration value="YLITAYTTO"/>
 *     &lt;enumeration value="ALITAYTTO"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "TasasijasaantoTyyppi")
@XmlEnum
public enum TasasijasaantoTyyppi {

    ARVONTA,
    YLITAYTTO,
    ALITAYTTO;

    public String value() {
        return name();
    }

    public static TasasijasaantoTyyppi fromValue(String v) {
        return valueOf(v);
    }

}
