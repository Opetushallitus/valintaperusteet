package fi.vm.sade.service.valintaperusteet.service.exception;

import fi.vm.sade.service.valintaperusteet.model.Funktionimi;

/**
 * User: wuoti
 * Date: 23.9.2013
 * Time: 13.00
 */
public class FunktiokutsuaEiVoidaKayttaaValintalaskennassaException extends RuntimeException {
    private Long id;
    private Funktionimi funktionimi;

    public FunktiokutsuaEiVoidaKayttaaValintalaskennassaException(Long id, Funktionimi funktionimi) {
        this.id = id;
        this.funktionimi = funktionimi;
    }

    public FunktiokutsuaEiVoidaKayttaaValintalaskennassaException(String message, Long id, Funktionimi funktionimi) {
        super(message);
        this.id = id;
        this.funktionimi = funktionimi;
    }

    public FunktiokutsuaEiVoidaKayttaaValintalaskennassaException(String message, Throwable cause, Long id, Funktionimi funktionimi) {
        super(message, cause);
        this.id = id;
        this.funktionimi = funktionimi;
    }

    public FunktiokutsuaEiVoidaKayttaaValintalaskennassaException(Throwable cause, Long id, Funktionimi funktionimi) {
        super(cause);
        this.id = id;
        this.funktionimi = funktionimi;
    }
}
