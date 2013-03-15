package fi.vm.sade.service.valintaperusteet.service.exception;

/**
 * User: kwuoti
 * Date: 18.2.2013
 * Time: 13.24
 */
public class ValinnanVaiheEiKuuluHakukohteeseenException extends RuntimeException {
    private String valinnanVaiheOid;
    private String hakukohdeOid;

    public ValinnanVaiheEiKuuluHakukohteeseenException(String valinnanVaiheOid, String hakukohdeOid) {
        this.valinnanVaiheOid = valinnanVaiheOid;
        this.hakukohdeOid = hakukohdeOid;
    }

    public ValinnanVaiheEiKuuluHakukohteeseenException(String message, String valinnanVaiheOid, String hakukohdeOid) {
        super(message);
        this.valinnanVaiheOid = valinnanVaiheOid;
        this.hakukohdeOid = hakukohdeOid;
    }

    public ValinnanVaiheEiKuuluHakukohteeseenException(String message, Throwable cause, String valinnanVaiheOid, String hakukohdeOid) {
        super(message, cause);
        this.valinnanVaiheOid = valinnanVaiheOid;
        this.hakukohdeOid = hakukohdeOid;
    }

    public ValinnanVaiheEiKuuluHakukohteeseenException(Throwable cause, String valinnanVaiheOid, String hakukohdeOid) {
        super(cause);
        this.valinnanVaiheOid = valinnanVaiheOid;
        this.hakukohdeOid = hakukohdeOid;
    }
}
