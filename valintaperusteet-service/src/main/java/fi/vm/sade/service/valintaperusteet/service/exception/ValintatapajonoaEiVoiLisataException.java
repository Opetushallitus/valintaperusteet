package fi.vm.sade.service.valintaperusteet.service.exception;

/**
 * User: kwuoti
 * Date: 18.2.2013
 * Time: 13.34
 */
public class ValintatapajonoaEiVoiLisataException extends RuntimeException {
    public ValintatapajonoaEiVoiLisataException() {
    }

    public ValintatapajonoaEiVoiLisataException(String message) {
        super(message);
    }

    public ValintatapajonoaEiVoiLisataException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValintatapajonoaEiVoiLisataException(Throwable cause) {
        super(cause);
    }
}
