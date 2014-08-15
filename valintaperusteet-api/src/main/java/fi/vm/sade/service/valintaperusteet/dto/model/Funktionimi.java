package fi.vm.sade.service.valintaperusteet.dto.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * User: kwuoti
 * Date: 18.1.2013
 * Time: 9.12
 */
public enum Funktionimi {
    LUKUARVO(Funktiotyyppi.LUKUARVOFUNKTIO, Laskentamoodi.VALINTAKOELASKENTA, Laskentamoodi.VALINTALASKENTA),
    TOTUUSARVO(Funktiotyyppi.TOTUUSARVOFUNKTIO, Laskentamoodi.VALINTAKOELASKENTA, Laskentamoodi.VALINTALASKENTA),
    SUMMA(Funktiotyyppi.LUKUARVOFUNKTIO, Laskentamoodi.VALINTAKOELASKENTA, Laskentamoodi.VALINTALASKENTA),
    MAKSIMI(Funktiotyyppi.LUKUARVOFUNKTIO, Laskentamoodi.VALINTAKOELASKENTA, Laskentamoodi.VALINTALASKENTA),
    MINIMI(Funktiotyyppi.LUKUARVOFUNKTIO, Laskentamoodi.VALINTAKOELASKENTA, Laskentamoodi.VALINTALASKENTA),
    TULO(Funktiotyyppi.LUKUARVOFUNKTIO, Laskentamoodi.VALINTAKOELASKENTA, Laskentamoodi.VALINTALASKENTA),
    KESKIARVO(Funktiotyyppi.LUKUARVOFUNKTIO, Laskentamoodi.VALINTAKOELASKENTA, Laskentamoodi.VALINTALASKENTA),
    MEDIAANI(Funktiotyyppi.LUKUARVOFUNKTIO, Laskentamoodi.VALINTAKOELASKENTA, Laskentamoodi.VALINTALASKENTA),
    KESKIARVONPARASTA(Funktiotyyppi.LUKUARVOFUNKTIO, Laskentamoodi.VALINTAKOELASKENTA, Laskentamoodi.VALINTALASKENTA),
    SUMMANPARASTA(Funktiotyyppi.LUKUARVOFUNKTIO, Laskentamoodi.VALINTAKOELASKENTA, Laskentamoodi.VALINTALASKENTA),
    NMAKSIMI(Funktiotyyppi.LUKUARVOFUNKTIO, Laskentamoodi.VALINTAKOELASKENTA, Laskentamoodi.VALINTALASKENTA),
    NMINIMI(Funktiotyyppi.LUKUARVOFUNKTIO, Laskentamoodi.VALINTAKOELASKENTA, Laskentamoodi.VALINTALASKENTA),
    JA(Funktiotyyppi.TOTUUSARVOFUNKTIO, Laskentamoodi.VALINTAKOELASKENTA, Laskentamoodi.VALINTALASKENTA),
    TAI(Funktiotyyppi.TOTUUSARVOFUNKTIO, Laskentamoodi.VALINTAKOELASKENTA, Laskentamoodi.VALINTALASKENTA),
    HAELUKUARVO(Funktiotyyppi.LUKUARVOFUNKTIO, Laskentamoodi.VALINTAKOELASKENTA, Laskentamoodi.VALINTALASKENTA),
    HAELUKUARVOEHDOLLA(Funktiotyyppi.LUKUARVOFUNKTIO, Laskentamoodi.VALINTAKOELASKENTA, Laskentamoodi.VALINTALASKENTA),
    HAETOTUUSARVO(Funktiotyyppi.TOTUUSARVOFUNKTIO, Laskentamoodi.VALINTAKOELASKENTA, Laskentamoodi.VALINTALASKENTA),
    HAEMERKKIJONOJAKONVERTOILUKUARVOKSI(Funktiotyyppi.LUKUARVOFUNKTIO, Laskentamoodi.VALINTAKOELASKENTA, Laskentamoodi.VALINTALASKENTA),
    HAETOTUUSARVOJAKONVERTOILUKUARVOKSI(Funktiotyyppi.LUKUARVOFUNKTIO, Laskentamoodi.VALINTAKOELASKENTA, Laskentamoodi.VALINTALASKENTA),
    HAEMERKKIJONOJAKONVERTOITOTUUSARVOKSI(Funktiotyyppi.TOTUUSARVOFUNKTIO, Laskentamoodi.VALINTAKOELASKENTA, Laskentamoodi.VALINTALASKENTA),
    NEGAATIO(Funktiotyyppi.LUKUARVOFUNKTIO, Laskentamoodi.VALINTAKOELASKENTA, Laskentamoodi.VALINTALASKENTA),
    OSAMAARA(Funktiotyyppi.LUKUARVOFUNKTIO, Laskentamoodi.VALINTAKOELASKENTA, Laskentamoodi.VALINTALASKENTA),
    JOS(Funktiotyyppi.LUKUARVOFUNKTIO, Laskentamoodi.VALINTAKOELASKENTA, Laskentamoodi.VALINTALASKENTA),
    SUUREMPI(Funktiotyyppi.TOTUUSARVOFUNKTIO, Laskentamoodi.VALINTAKOELASKENTA, Laskentamoodi.VALINTALASKENTA),
    PIENEMPI(Funktiotyyppi.TOTUUSARVOFUNKTIO, Laskentamoodi.VALINTAKOELASKENTA, Laskentamoodi.VALINTALASKENTA),
    SUUREMPITAIYHTASUURI(Funktiotyyppi.TOTUUSARVOFUNKTIO, Laskentamoodi.VALINTAKOELASKENTA, Laskentamoodi.VALINTALASKENTA),
    PIENEMPITAIYHTASUURI(Funktiotyyppi.TOTUUSARVOFUNKTIO, Laskentamoodi.VALINTAKOELASKENTA, Laskentamoodi.VALINTALASKENTA),
    YHTASUURI(Funktiotyyppi.TOTUUSARVOFUNKTIO, Laskentamoodi.VALINTAKOELASKENTA, Laskentamoodi.VALINTALASKENTA),
    EI(Funktiotyyppi.TOTUUSARVOFUNKTIO, Laskentamoodi.VALINTAKOELASKENTA, Laskentamoodi.VALINTALASKENTA),
    TYHJA(Funktiotyyppi.EI_VALIDI),
    NIMETTYTOTUUSARVO(Funktiotyyppi.TOTUUSARVOFUNKTIO, Laskentamoodi.VALINTAKOELASKENTA, Laskentamoodi.VALINTALASKENTA),
    NIMETTYLUKUARVO(Funktiotyyppi.LUKUARVOFUNKTIO, Laskentamoodi.VALINTAKOELASKENTA, Laskentamoodi.VALINTALASKENTA),
    KONVERTOILUKUARVO(Funktiotyyppi.LUKUARVOFUNKTIO, Laskentamoodi.VALINTAKOELASKENTA, Laskentamoodi.VALINTALASKENTA),
    HAKUTOIVE(Funktiotyyppi.TOTUUSARVOFUNKTIO, Laskentamoodi.VALINTAKOELASKENTA, Laskentamoodi.VALINTALASKENTA),
    DEMOGRAFIA(Funktiotyyppi.TOTUUSARVOFUNKTIO, Laskentamoodi.VALINTALASKENTA),
    PYORISTYS(Funktiotyyppi.LUKUARVOFUNKTIO, Laskentamoodi.VALINTAKOELASKENTA, Laskentamoodi.VALINTALASKENTA),
    HAEMERKKIJONOJAVERTAAYHTASUURUUS(Funktiotyyppi.TOTUUSARVOFUNKTIO, Laskentamoodi.VALINTAKOELASKENTA, Laskentamoodi.VALINTALASKENTA),
    HYLKAA(Funktiotyyppi.LUKUARVOFUNKTIO, Laskentamoodi.VALINTAKOELASKENTA, Laskentamoodi.VALINTALASKENTA),
    HYLKAAARVOVALILLA(Funktiotyyppi.LUKUARVOFUNKTIO, Laskentamoodi.VALINTALASKENTA, Laskentamoodi.VALINTAKOELASKENTA),
    SKAALAUS(Funktiotyyppi.LUKUARVOFUNKTIO, Laskentamoodi.VALINTALASKENTA),
    PAINOTETTUKESKIARVO(Funktiotyyppi.LUKUARVOFUNKTIO, Laskentamoodi.VALINTALASKENTA, Laskentamoodi.VALINTAKOELASKENTA),
    HAEYOARVOSANA(Funktiotyyppi.LUKUARVOFUNKTIO, Laskentamoodi.VALINTALASKENTA, Laskentamoodi.VALINTAKOELASKENTA),
    HAEOSAKOEARVOSANA(Funktiotyyppi.LUKUARVOFUNKTIO, Laskentamoodi.VALINTALASKENTA, Laskentamoodi.VALINTAKOELASKENTA),
    VALINTAPERUSTEYHTASUURUUS(Funktiotyyppi.TOTUUSARVOFUNKTIO, Laskentamoodi.VALINTALASKENTA, Laskentamoodi.VALINTAKOELASKENTA);


    private Funktiotyyppi tyyppi;
    private Set<Laskentamoodi> laskentamoodit;

    Funktionimi(Funktiotyyppi tyyppi, Laskentamoodi... laskentamoodit) {
        this.tyyppi = tyyppi;
        this.laskentamoodit = new HashSet<Laskentamoodi>(Arrays.asList(laskentamoodit));
    }

    public Funktiotyyppi getTyyppi() {
        return this.tyyppi;
    }

    public Set<Laskentamoodi> getLaskentamoodit() {
        return laskentamoodit;
    }
}
