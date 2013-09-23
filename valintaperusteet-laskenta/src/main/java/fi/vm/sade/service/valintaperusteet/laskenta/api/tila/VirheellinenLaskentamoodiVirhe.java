package fi.vm.sade.service.valintaperusteet.laskenta.api.tila;

/**
 * User: wuoti
 * Date: 14.8.2013
 * Time: 15.14
 */
public class VirheellinenLaskentamoodiVirhe extends VirheMetatieto {
    private String funktio;
    private String laskentamoodi;

    public VirheellinenLaskentamoodiVirhe(String funktio, String laskentamoodi) {
        super(VirheMetatietotyyppi.VIRHEELLINEN_LASKENTAMOODI);

        this.funktio = funktio;
        this.laskentamoodi = laskentamoodi;

    }

    public String getFunktio() {
        return funktio;
    }

    public String getLaskentamoodi() {
        return laskentamoodi;
    }
}
