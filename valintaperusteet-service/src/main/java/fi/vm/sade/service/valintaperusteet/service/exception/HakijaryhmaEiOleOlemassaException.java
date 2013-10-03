package fi.vm.sade.service.valintaperusteet.service.exception;

/**
 * Created with IntelliJ IDEA.
 * User: jukais
 * Date: 1.10.2013
 * Time: 17.07
 * To change this template use File | Settings | File Templates.
 */
public class HakijaryhmaEiOleOlemassaException extends RuntimeException {
    private String hakijaryhmaOid;

    public HakijaryhmaEiOleOlemassaException(String hakijaryhmaOid) {
        this.hakijaryhmaOid = hakijaryhmaOid;
    }

    public HakijaryhmaEiOleOlemassaException(String message, String hakijaryhmaOid) {
        super(message);
        this.hakijaryhmaOid = hakijaryhmaOid;
    }

    public HakijaryhmaEiOleOlemassaException(String message, Throwable cause, String hakijaryhmaOid) {
        super(message, cause);
        this.hakijaryhmaOid = hakijaryhmaOid;
    }

    public HakijaryhmaEiOleOlemassaException(Throwable cause, String hakijaryhmaOid) {
        super(cause);
        this.hakijaryhmaOid = hakijaryhmaOid;
    }
}
