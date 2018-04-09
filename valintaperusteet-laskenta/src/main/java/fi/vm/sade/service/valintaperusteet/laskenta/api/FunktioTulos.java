package fi.vm.sade.service.valintaperusteet.laskenta.api;

public class FunktioTulos {
    private String tunniste;
    private String arvo;
    private String nimiFi;
    private String nimiSv;
    private String nimiEn;
    private boolean omaopintopolku;

    public String getNimiFi() {
        return nimiFi;
    }

    public String getNimiSv() {
        return nimiSv;
    }

    public String getNimiEn() {
        return nimiEn;
    }

    public FunktioTulos(String tunniste, String arvo, String nimiFi, String nimiSv, String nimiEn, boolean omaopintopolku) {
        this.tunniste = tunniste;
        this.arvo = arvo;
        this.nimiFi = nimiFi;
        this.nimiSv = nimiSv;
        this.nimiEn = nimiEn;
        this.omaopintopolku = omaopintopolku;
    }

    public String getTunniste() {
        return tunniste;
    }

    public String getArvo() {
        return arvo;
    }

    public boolean isOmaopintopolku() {
        return omaopintopolku;
    }
}
