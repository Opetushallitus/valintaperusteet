package fi.vm.sade.service.valintaperusteet.laskenta.api;

public class FunktioTulos {
    private String tunniste;
    private String arvo;

    public FunktioTulos(String tunniste, String arvo) {
        this.tunniste = tunniste;
        this.arvo = arvo;
    }

    public String getTunniste() {
        return tunniste;
    }

    public String getArvo() {
        return arvo;
    }
}
