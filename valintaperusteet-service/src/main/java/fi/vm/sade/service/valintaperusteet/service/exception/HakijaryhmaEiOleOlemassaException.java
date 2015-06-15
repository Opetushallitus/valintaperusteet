package fi.vm.sade.service.valintaperusteet.service.exception;

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
