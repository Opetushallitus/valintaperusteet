package fi.vm.sade.service.valintaperusteet.laskenta.api;

/**
 * User: wuoti
 * Date: 17.9.2013
 * Time: 10.48
 */
public class SyotettyArvo {
    private String tunniste;
    private String arvo;
    private String laskennallinenArvo;
    private Osallistuminen osallistuminen;

    public SyotettyArvo(String tunniste, String arvo, String laskennallinenArvo, Osallistuminen osallistuminen) {
        this.tunniste = tunniste;
        this.arvo = arvo;
        this.laskennallinenArvo = laskennallinenArvo;
        this.osallistuminen = osallistuminen;
    }

    public String getTunniste() {
        return tunniste;
    }

    public String getArvo() {
        return arvo;
    }

    public String getLaskennallinenArvo() {
        return laskennallinenArvo;
    }

    public Osallistuminen getOsallistuminen() {
        return osallistuminen;
    }
}
