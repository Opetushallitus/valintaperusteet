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

    public SyotettyArvo(String tunniste, String arvo, String laskennallinenArvo) {
        this.tunniste = tunniste;
        this.arvo = arvo;
        this.laskennallinenArvo = laskennallinenArvo;
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
}
