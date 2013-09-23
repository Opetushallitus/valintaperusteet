package fi.vm.sade.service.valintaperusteet.service.exception;

import fi.vm.sade.service.valintaperusteet.model.Funktionimi;

/**
 * User: wuoti
 * Date: 23.9.2013
 * Time: 13.00
 */
public class FunktiokutsuaEiVoidaKayttaaValintakoelaskennassaException extends RuntimeException {
    private Long id;
    private Funktionimi funktionimi;

    public FunktiokutsuaEiVoidaKayttaaValintakoelaskennassaException(Long id, Funktionimi funktionimi) {
        this.id = id;
        this.funktionimi = funktionimi;
    }

    public FunktiokutsuaEiVoidaKayttaaValintakoelaskennassaException(String message, Long id, Funktionimi funktionimi) {
        super(message);
        this.id = id;
        this.funktionimi = funktionimi;
    }

    public FunktiokutsuaEiVoidaKayttaaValintakoelaskennassaException(String message, Throwable cause, Long id, Funktionimi funktionimi) {
        super(message, cause);
        this.id = id;
        this.funktionimi = funktionimi;
    }

    public FunktiokutsuaEiVoidaKayttaaValintakoelaskennassaException(Throwable cause, Long id, Funktionimi funktionimi) {
        super(cause);
        this.id = id;
        this.funktionimi = funktionimi;
    }
}