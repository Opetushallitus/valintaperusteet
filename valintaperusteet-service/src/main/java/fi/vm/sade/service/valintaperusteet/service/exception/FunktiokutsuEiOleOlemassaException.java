package fi.vm.sade.service.valintaperusteet.service.exception;

/**
 * User: kwuoti
 * Date: 18.2.2013
 * Time: 13.09
 */
public class FunktiokutsuEiOleOlemassaException extends RuntimeException {
    private Long funktiokutsuId;

    public FunktiokutsuEiOleOlemassaException(Long funktiokutsuId) {
        this.funktiokutsuId = funktiokutsuId;
    }

    public FunktiokutsuEiOleOlemassaException(String message, Long funktiokutsuId) {
        super(message);
        this.funktiokutsuId = funktiokutsuId;
    }

    public FunktiokutsuEiOleOlemassaException(String message, Throwable cause, Long funktiokutsuId) {
        super(message, cause);
        this.funktiokutsuId = funktiokutsuId;
    }

    public FunktiokutsuEiOleOlemassaException(Throwable cause, Long funktiokutsuId) {
        super(cause);
        this.funktiokutsuId = funktiokutsuId;
    }
}
