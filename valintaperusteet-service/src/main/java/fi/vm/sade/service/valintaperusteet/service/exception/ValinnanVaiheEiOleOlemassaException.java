package fi.vm.sade.service.valintaperusteet.service.exception;

/**
 * User: kwuoti
 * Date: 18.2.2013
 * Time: 13.30
 */
public class ValinnanVaiheEiOleOlemassaException extends RuntimeException {
    private String valinnanVaiheOid;

    public ValinnanVaiheEiOleOlemassaException(String valinnanVaiheOid) {
        this.valinnanVaiheOid = valinnanVaiheOid;
    }

    public ValinnanVaiheEiOleOlemassaException(String message, String valinnanVaiheOid) {
        super(message);
        this.valinnanVaiheOid = valinnanVaiheOid;
    }

    public ValinnanVaiheEiOleOlemassaException(String message, Throwable cause, String valinnanVaiheOid) {
        super(message, cause);
        this.valinnanVaiheOid = valinnanVaiheOid;
    }

    public ValinnanVaiheEiOleOlemassaException(Throwable cause, String valinnanVaiheOid) {
        super(cause);
        this.valinnanVaiheOid = valinnanVaiheOid;
    }
}
