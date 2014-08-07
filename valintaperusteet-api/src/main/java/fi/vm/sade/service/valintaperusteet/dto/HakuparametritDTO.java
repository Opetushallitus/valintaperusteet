package fi.vm.sade.service.valintaperusteet.dto;

/**
 * Created by jukais on 4.3.2014.
 */
public class HakuparametritDTO {
    private String hakukohdeOid;
    private Integer valinnanVaiheJarjestysluku;

    public String getHakukohdeOid() {
        return hakukohdeOid;
    }

    public void setHakukohdeOid(String hakukohdeOid) {
        this.hakukohdeOid = hakukohdeOid;
    }

    public Integer getValinnanVaiheJarjestysluku() {
        return valinnanVaiheJarjestysluku;
    }

    public void setValinnanVaiheJarjestysluku(Integer valinnanVaiheJarjestysluku) {
        this.valinnanVaiheJarjestysluku = valinnanVaiheJarjestysluku;
    }
}
