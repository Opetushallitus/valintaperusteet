package fi.vm.sade.service.valintaperusteet.service.exception;

/**
 * User: wuoti
 * Date: 2.8.2013
 * Time: 14.03
 */
public class ValinnanVaiheEpaaktiivinenException extends RuntimeException {
    public ValinnanVaiheEpaaktiivinenException() {
    }

    public ValinnanVaiheEpaaktiivinenException(String message) {
        super(message);
    }

    public ValinnanVaiheEpaaktiivinenException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValinnanVaiheEpaaktiivinenException(Throwable cause) {
        super(cause);
    }
}
