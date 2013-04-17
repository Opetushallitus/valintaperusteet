package fi.vm.sade.service.valintaperusteet.dto;

/**
 * User: kwuoti
 * Date: 16.4.2013
 * Time: 13.01
 */
public class ValintakoeDTO {

    private String tunniste;
    private Long laskentakaavaId;

    public String getTunniste() {
        return tunniste;
    }

    public void setTunniste(String tunniste) {
        this.tunniste = tunniste;
    }

    public Long getLaskentakaavaId() {
        return laskentakaavaId;
    }

    public void setLaskentakaavaId(Long laskentakaavaId) {
        this.laskentakaavaId = laskentakaavaId;
    }
}
