package fi.vm.sade.service.valintaperusteet.service.exception;

import fi.vm.sade.service.valintaperusteet.dto.model.Funktionimi;

/**
 * User: wuoti Date: 30.7.2013 Time: 15.30
 */
public class FunktiokutsuMuodostaaSilmukanException extends Exception {
    private Long funktiokutsuId;
    private Funktionimi funktionimi;
    private Long laskentakaavaId;

    public FunktiokutsuMuodostaaSilmukanException(Long funktiokutsuId, Funktionimi funktionimi, Long laskentakaavaId) {
        this.funktiokutsuId = funktiokutsuId;
        this.funktionimi = funktionimi;
        this.laskentakaavaId = laskentakaavaId;
    }

    public FunktiokutsuMuodostaaSilmukanException(String message, Long funktiokutsuId, Funktionimi funktionimi,
            Long laskentakaavaId) {
        super(message);
        this.funktiokutsuId = funktiokutsuId;
        this.funktionimi = funktionimi;
        this.laskentakaavaId = laskentakaavaId;
    }

    public FunktiokutsuMuodostaaSilmukanException(String message, Throwable cause, Long funktiokutsuId,
            Funktionimi funktionimi, Long laskentakaavaId) {
        super(message, cause);
        this.funktiokutsuId = funktiokutsuId;
        this.funktionimi = funktionimi;
        this.laskentakaavaId = laskentakaavaId;
    }

    public FunktiokutsuMuodostaaSilmukanException(Throwable cause, Long funktiokutsuId, Funktionimi funktionimi,
            Long laskentakaavaId) {
        super(cause);
        this.funktiokutsuId = funktiokutsuId;
        this.funktionimi = funktionimi;
        this.laskentakaavaId = laskentakaavaId;
    }

    public Long getFunktiokutsuId() {
        return funktiokutsuId;
    }

    public Funktionimi getFunktionimi() {
        return funktionimi;
    }

    public Long getLaskentakaavaId() {
        return laskentakaavaId;
    }
}
