package fi.vm.sade.service.valintaperusteet.service.exception;

/**
 * User: kwuoti
 * Date: 18.2.2013
 * Time: 13.33
 */
public class ValintatapajonoEiOleOlemassaException extends RuntimeException {
    private String valintatapajonoOid;

    public ValintatapajonoEiOleOlemassaException(String valintatapajonoOid) {
        this.valintatapajonoOid = valintatapajonoOid;
    }

    public ValintatapajonoEiOleOlemassaException(String message, String valintatapajonoOid) {
        super(message);
        this.valintatapajonoOid = valintatapajonoOid;
    }

    public ValintatapajonoEiOleOlemassaException(String message, Throwable cause, String valintatapajonoOid) {
        super(message, cause);
        this.valintatapajonoOid = valintatapajonoOid;
    }

    public ValintatapajonoEiOleOlemassaException(Throwable cause, String valintatapajonoOid) {
        super(cause);
        this.valintatapajonoOid = valintatapajonoOid;
    }
}
