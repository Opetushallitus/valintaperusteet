package fi.vm.sade.service.valintaperusteet.service.exception;

/**
 * User: kwuoti
 * Date: 18.2.2013
 * Time: 13.04
 */
public class LaskentakaavaOidTyhjaException extends RuntimeException {
    public LaskentakaavaOidTyhjaException() {
    }

    public LaskentakaavaOidTyhjaException(String message) {
        super(message);
    }

    public LaskentakaavaOidTyhjaException(String message, Throwable cause) {
        super(message, cause);
    }

    public LaskentakaavaOidTyhjaException(Throwable cause) {
        super(cause);
    }
}
