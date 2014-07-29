package fi.vm.sade.service.valintaperusteet.dto;

/**
 * Created by jukais on 14.3.2014.
 */
public class HakukohteenValintaperusteDTO {
    private String tunniste;
    private String arvo;

    public void setTunniste(String tunniste) {
        this.tunniste = tunniste;
    }

    public String getTunniste() {
        return tunniste;
    }

    public void setArvo(String arvo) {
        this.arvo = arvo;
    }

    public String getArvo() {
        return arvo;
    }
}
