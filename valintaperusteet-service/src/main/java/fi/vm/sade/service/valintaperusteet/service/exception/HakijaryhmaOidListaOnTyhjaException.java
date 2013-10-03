package fi.vm.sade.service.valintaperusteet.service.exception;

/**
 * User: kwuoti
 * Date: 18.2.2013
 * Time: 13.34
 */
public class HakijaryhmaOidListaOnTyhjaException extends RuntimeException {
    public HakijaryhmaOidListaOnTyhjaException() {
    }

    public HakijaryhmaOidListaOnTyhjaException(String message) {
        super(message);
    }

    public HakijaryhmaOidListaOnTyhjaException(String message, Throwable cause) {
        super(message, cause);
    }

    public HakijaryhmaOidListaOnTyhjaException(Throwable cause) {
        super(cause);
    }
}
