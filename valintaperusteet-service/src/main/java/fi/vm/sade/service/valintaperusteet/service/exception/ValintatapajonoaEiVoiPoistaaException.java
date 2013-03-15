package fi.vm.sade.service.valintaperusteet.service.exception;

/**
 * User: kwuoti
 * Date: 18.2.2013
 * Time: 15.12
 */
public class ValintatapajonoaEiVoiPoistaaException extends RuntimeException {
    public ValintatapajonoaEiVoiPoistaaException() {
    }

    public ValintatapajonoaEiVoiPoistaaException(String message) {
        super(message);
    }

    public ValintatapajonoaEiVoiPoistaaException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValintatapajonoaEiVoiPoistaaException(Throwable cause) {
        super(cause);
    }
}
