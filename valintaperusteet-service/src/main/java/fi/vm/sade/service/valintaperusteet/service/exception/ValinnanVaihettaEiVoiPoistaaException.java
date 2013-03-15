package fi.vm.sade.service.valintaperusteet.service.exception;

/**
 * User: kwuoti
 * Date: 18.2.2013
 * Time: 13.25
 */
public class ValinnanVaihettaEiVoiPoistaaException extends RuntimeException {
    public ValinnanVaihettaEiVoiPoistaaException() {
    }

    public ValinnanVaihettaEiVoiPoistaaException(String message) {
        super(message);
    }

    public ValinnanVaihettaEiVoiPoistaaException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValinnanVaihettaEiVoiPoistaaException(Throwable cause) {
        super(cause);
    }
}
