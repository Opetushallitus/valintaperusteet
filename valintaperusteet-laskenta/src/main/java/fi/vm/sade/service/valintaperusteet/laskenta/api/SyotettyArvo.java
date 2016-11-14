package fi.vm.sade.service.valintaperusteet.laskenta.api;

import fi.vm.sade.service.valintaperusteet.dto.model.Osallistuminen;

public class SyotettyArvo {
    private String tunniste;
    private String arvo;
    private String laskennallinenArvo;
    private Osallistuminen osallistuminen;
    private String tyypinKoodiUri;
    private boolean tilastoidaan;

    public SyotettyArvo(String tunniste, String arvo, String laskennallinenArvo, Osallistuminen osallistuminen, String tyypinKoodiUri, boolean tilastoidaan) {
        this.tunniste = tunniste;
        this.arvo = arvo;
        this.laskennallinenArvo = laskennallinenArvo;
        this.osallistuminen = osallistuminen;
        this.tyypinKoodiUri = tyypinKoodiUri;
        this.tilastoidaan = tilastoidaan;
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

    public String getTyypinKoodiUri() {
        return tyypinKoodiUri;
    }

    public boolean isTilastoidaan() {
        return tilastoidaan;
    }
}
