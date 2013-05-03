package fi.vm.sade.service.valintaperusteet.dto;

/**
 * User: kwuoti
 * Date: 16.4.2013
 * Time: 13.01
 */
public class ValintakoeDTO {

    private String tunniste;
    private Long laskentakaavaId;
    private String nimi;
    private String kuvaus;

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

    public String getKuvaus() {
        return kuvaus;
    }

    public void setKuvaus(String kuvaus) {
        this.kuvaus = kuvaus;
    }

    public String getNimi() {
        return nimi;
    }

    public void setNimi(String nimi) {
        this.nimi = nimi;
    }
}
