package fi.vm.sade.service.valintaperusteet.service.exception;

/**
 * User: kwuoti
 * Date: 18.2.2013
 * Time: 15.12
 */
public class HakijaryhmaaEiVoiPoistaaException extends RuntimeException {
    public HakijaryhmaaEiVoiPoistaaException() {
    }

    public HakijaryhmaaEiVoiPoistaaException(String message) {
        super(message);
    }

    public HakijaryhmaaEiVoiPoistaaException(String message, Throwable cause) {
        super(message, cause);
    }

    public HakijaryhmaaEiVoiPoistaaException(Throwable cause) {
        super(cause);
    }
}
