package fi.vm.sade.service.valintaperusteet.service.exception;

/**
 * User: wuoti
 * Date: 30.7.2013
 * Time: 15.30
 */
public class FunktiokutsuMuodostaaSilmukanException extends Exception {
    private Long funktiokutsuId;
    private Long laskentakaavaId;

    public FunktiokutsuMuodostaaSilmukanException(Long funktiokutsuId, Long laskentakaavaId) {
        this.funktiokutsuId = funktiokutsuId;
        this.laskentakaavaId = laskentakaavaId;
    }

    public FunktiokutsuMuodostaaSilmukanException(String message, Long funktiokutsuId, Long laskentakaavaId) {
        super(message);
        this.funktiokutsuId = funktiokutsuId;
        this.laskentakaavaId = laskentakaavaId;
    }

    public FunktiokutsuMuodostaaSilmukanException(String message, Throwable cause, Long funktiokutsuId, Long laskentakaavaId) {
        super(message, cause);
        this.funktiokutsuId = funktiokutsuId;
        this.laskentakaavaId = laskentakaavaId;
    }

    public FunktiokutsuMuodostaaSilmukanException(Throwable cause, Long funktiokutsuId, Long laskentakaavaId) {
        super(cause);
        this.funktiokutsuId = funktiokutsuId;
        this.laskentakaavaId = laskentakaavaId;
    }

    public Long getFunktiokutsuId() {
        return funktiokutsuId;
    }

    public Long getLaskentakaavaId() {
        return laskentakaavaId;
    }
}
