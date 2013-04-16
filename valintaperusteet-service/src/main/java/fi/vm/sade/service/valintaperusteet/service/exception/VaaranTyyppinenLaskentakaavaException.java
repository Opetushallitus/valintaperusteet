package fi.vm.sade.service.valintaperusteet.service.exception;

/**
 * User: kwuoti
 * Date: 16.4.2013
 * Time: 13.10
 */
public class VaaranTyyppinenLaskentakaavaException extends RuntimeException {

    public VaaranTyyppinenLaskentakaavaException() {
    }

    public VaaranTyyppinenLaskentakaavaException(String message) {
        super(message);
    }

    public VaaranTyyppinenLaskentakaavaException(String message, Throwable cause) {
        super(message, cause);
    }

    public VaaranTyyppinenLaskentakaavaException(Throwable cause) {
        super(cause);
    }
}
