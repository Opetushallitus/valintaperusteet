package fi.vm.sade.service.valintaperusteet.service.exception;

/**
 * User: kwuoti
 * Date: 18.2.2013
 * Time: 12.47
 */
public class ValintaryhmaEiOleOlemassaException extends RuntimeException {

    private String valintaryhmaOid;

    public ValintaryhmaEiOleOlemassaException(String valintaryhmaOid) {
        this.valintaryhmaOid = valintaryhmaOid;
    }

    public ValintaryhmaEiOleOlemassaException(String message, String valintaryhmaOid) {
        super(message);
        this.valintaryhmaOid = valintaryhmaOid;
    }

    public ValintaryhmaEiOleOlemassaException(String message, Throwable cause, String valintaryhmaOid) {
        super(message, cause);
        this.valintaryhmaOid = valintaryhmaOid;
    }

    public ValintaryhmaEiOleOlemassaException(Throwable cause, String valintaryhmaOid) {
        super(cause);
        this.valintaryhmaOid = valintaryhmaOid;
    }
}
